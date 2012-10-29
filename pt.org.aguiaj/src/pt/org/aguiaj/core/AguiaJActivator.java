/*******************************************************************************
 * Copyright (c) 2012 Andr� L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andr� L. Santos - initial API and implementation
 ******************************************************************************/
package pt.org.aguiaj.core;

import static pt.org.aguiaj.extensibility.AguiaJContribution.ACCESSOR_POLICY_DESCRIPTION;
import static pt.org.aguiaj.extensibility.AguiaJContribution.ACCESSOR_POLICY_ID;
import static pt.org.aguiaj.extensibility.AguiaJContribution.EXTENSION_ACCESSOR_POLICY;
import static pt.org.aguiaj.extensibility.AguiaJContribution.EXTENSION_OBJECT_WIDGET;
import static pt.org.aguiaj.extensibility.AguiaJContribution.OBJECT_WIDGET_ALLOWIMPORT;
import static pt.org.aguiaj.extensibility.AguiaJContribution.OBJECT_WIDGET_EXCLUDE;
import static pt.org.aguiaj.extensibility.AguiaJContribution.OBJECT_WIDGET_GROUP;
import static pt.org.aguiaj.extensibility.AguiaJContribution.OBJECT_WIDGET_ICON;
import static pt.org.aguiaj.extensibility.AguiaJContribution.OBJECT_WIDGET_ID;
import static pt.org.aguiaj.extensibility.AguiaJContribution.OBJECT_WIDGET_INCLUDE;
import static pt.org.aguiaj.extensibility.AguiaJContribution.OBJECT_WIDGET_METHOD;
import static pt.org.aguiaj.extensibility.AguiaJContribution.OBJECT_WIDGET_METHOD_ID;
import static pt.org.aguiaj.extensibility.AguiaJContribution.OBJECT_WIDGET_PROMOTE;
import static pt.org.aguiaj.extensibility.AguiaJContribution.OBJECT_WIDGET_VIEW;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import pt.org.aguiaj.classes.ClassMemberFilter;
import pt.org.aguiaj.classes.ClassModel;
import pt.org.aguiaj.classes.ClassesView;
import pt.org.aguiaj.classes.NameBasedFilter;
import pt.org.aguiaj.common.AguiaJImage;
import pt.org.aguiaj.common.SWTUtils;
import pt.org.aguiaj.core.commands.JavaBarView;
import pt.org.aguiaj.core.commands.ReloadClassesCommand;
import pt.org.aguiaj.core.exceptions.ActiveExceptionHandler;
import pt.org.aguiaj.core.typewidgets.ActiveTypeWidget;
import pt.org.aguiaj.extensibility.AccessorMethodDetectionPolicy;
import pt.org.aguiaj.extensibility.AguiaJContribution;
import pt.org.aguiaj.extensibility.VisualizationWidget;
import pt.org.aguiaj.standard.GetIsAccessorPolicy;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
/**
 * The activator class controls the plug-in life cycle
 */
public class AguiaJActivator extends AbstractUIPlugin {
	// The shared instance
	private static AguiaJActivator plugin;

	private static final AccessorMethodDetectionPolicy defaultPolicy = 
			new GetIsAccessorPolicy();

	private Multimap<String, Class<?>> packagesClasses;
	private IPath workingDir;

	private Multimap<String, Class<?>> plugins; // id -> class[]
	private Map<String, String> pluginNames; // id -> name

	private Map<Class<?>, Image> pluginTypeImages;

	private Map<String, Class<? extends AccessorMethodDetectionPolicy>> accessorPolicies;

	public AguiaJActivator() {
		plugin = this;

		workingDir = Platform.getLocation();
		packagesClasses = ArrayListMultimap.create();

		ActiveTypeWidget.loadTypeWidgets();
		ActiveDefaultObjectWidgetExtension.loadExtensions();
		ActiveExceptionHandler.loadExceptionHandlers();

		plugins = ArrayListMultimap.create();
		pluginNames = Maps.newHashMap();
		pluginTypeImages = Maps.newHashMap();

		accessorPolicies = Maps.newHashMap();
	}

	public IPath getWorkingDirectory() {
		return workingDir;
	}	

	public Multimap<String, Class<?>> getPackagesClasses() {
		return packagesClasses;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		getPreferenceStore().addPropertyChangeListener(new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				ReloadClassesCommand reload = new ReloadClassesCommand();
				try {
					reload.execute(null);
				} catch (ExecutionException e) {					
					e.printStackTrace();
				}
				JavaBarView.getInstance().updateFont();
			}
		});
		loadObjectWidgetPlugins();
		loadAccessorPolicyPlugins();
		KeyShortcuts.addKeyShortcuts();
		
		ClassModel.getInstance().addDefaultClasses();
	}


	
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);		
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static AguiaJActivator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */

	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(AguiaJContribution.AGUIAJ_PLUGIN, path);
	}

	public Image getPluginTypeIcon(Class<?> type) {
		return pluginTypeImages.get(type);
	}

	protected void initializeImageRegistry(ImageRegistry registry) {
		super.initializeImageRegistry(registry);

		Bundle bundle = Platform.getBundle(AguiaJContribution.AGUIAJ_PLUGIN);

		for(AguiaJImage image : AguiaJImage.values()) {
			ImageDescriptor imageDesc = 
				ImageDescriptor.createFromURL(FileLocator.find(bundle, image.getPath(), null));	
			registry.put(image.getId(), imageDesc);
		}			

		IPath imagesPath = new Path("images");
		for(String id : getPluginIds()) {

			if(!id.equals(AguiaJContribution.AGUIAJ_PLUGIN)) {
				bundle = Platform.getBundle(id);
				URL imagesFolder = FileLocator.find(bundle, imagesPath, null);
				if(imagesFolder != null) {
					try {
						imagesFolder = FileLocator.toFileURL(imagesFolder);
					} catch (IOException e1) {
						e1.printStackTrace();
					}

					File dir = new File(imagesFolder.getPath());

					if(dir != null && dir.isDirectory()) {
						for(String file : dir.list()) {
							URL fileURL = FileLocator.find(bundle, imagesPath.append(file), null);
							ImageDescriptor imageDesc = ImageDescriptor.createFromURL(fileURL);
							registry.put(file.substring(0, file.indexOf('.')), imageDesc);
						}
					}
				}
			}
		}

	}

	public void reloadClasses() {
		loadClasses(workingDir);
	}

	public static void handlePluginError(String message) {
		SWTUtils.showMessage("Plugin Error", message, SWT.ICON_ERROR);
	}
	
	public static String getActivePlugin() {
		return ClassesView.getInstance().getActivePlugin();
	}

	public static URL getPluginFolder(String pluginId) {

		URL url = Platform.getBundle(pluginId).getEntry("/");
		try {
			url = FileLocator.resolve(url);
		}
		catch(IOException ex) {
			ex.printStackTrace();
		}

		return url;
	}

	private Map<String, File> pluginClassFiles = new HashMap<String, File>();

	public Map<String, File> getPluginClassFiles() {
		return pluginClassFiles;
	}

	public String getPluginLocation(Class<?> aClass) {
		File f = pluginClassFiles.get(aClass.getName());
		Path path = new Path(f.getAbsolutePath());
		return path.removeLastSegments(1).toOSString();
	}

	public String getPluginJarLocation(final String pluginId) {
		URL pluginFolder = getPluginFolder(pluginId);
		Path path = new Path(pluginFolder.getPath());
		
		String jarLocation = path.toString();
		if(jarLocation.endsWith("!\\"))
			jarLocation = jarLocation.substring(0, jarLocation.length()-2);

		if(jarLocation.startsWith("file:\\"))
			jarLocation = jarLocation.substring("file:\\".length());

		if(jarLocation.endsWith("!/"))
			jarLocation = jarLocation.substring(0, jarLocation.length()-2);

		if(jarLocation.startsWith("file:"))
			jarLocation = jarLocation.substring("file:".length());
		
//		if(!jarLocation.endsWith(".jar"))
//			jarLocation += "bin";

		return jarLocation;
	}



	public Set<String> getPluginIds() {
		return plugins.keySet();
	}

	public Set<String> getPluginPackages() {
		Set<String> packs = Sets.newHashSet();
		for(Class<?> c : plugins.values())
			packs.add(c.getPackage().getName());

		return packs;
	}

	public boolean isPluginPackage(String packageName) {
		for(Class<?> c : plugins.values())
			if(c.getPackage().getName().equals(packageName))
				return true;

		return false;
	}
	
	

	public Collection<Class<?>> getPackageClasses(String packageName) {
		Set<Class<?>> classes = Sets.newHashSet();
		for(Class<?> c : plugins.values())
			if(c.getPackage().getName().equals(packageName))
				classes.add(c);

		return classes;
	}

	public Collection<Class<?>> getPluginClasses(String pluginId) {
		return plugins.get(pluginId);		
	}

	public String getPluginName(String pluginId) {
		return pluginNames.get(pluginId);
	}

	public Set<String> getAccessorPolicies() {
		return accessorPolicies.keySet();
	}

	
	public AccessorMethodDetectionPolicy getAccessorPolicy() {
		String name = AguiaJParam.ACCESSOR_POLICY.getString();
		Class<? extends AccessorMethodDetectionPolicy> policyClass = accessorPolicies.get(name);
		if(policyClass != null) {			
			try {
				return policyClass.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}		
		return defaultPolicy;
	}

	private void loadObjectWidgetPlugins() {
		IConfigurationElement[] config = 
			Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_OBJECT_WIDGET);

		for (final IConfigurationElement e : config) {

			String pluginName = e.getDeclaringExtension().getLabel();			
			String pluginID = e.getContributor().getName();

			if(!pluginNames.containsKey(pluginID)) {
				pluginNames.put(pluginID, !"".equals(pluginName) ? pluginName : pluginID);
			}

			if(e.getName().equals(OBJECT_WIDGET_GROUP))
				handleObjectWidgets(e, pluginID);			
		}
	}

	@SuppressWarnings("unchecked")
	private void handleObjectWidgets(final IConfigurationElement e, String pluginID) {
		for (final IConfigurationElement objWidget : e.getChildren()) {
			final String className = objWidget.getAttribute(OBJECT_WIDGET_ID);	

			if(className == null)
				continue;

			String allowImportText = objWidget.getAttribute(OBJECT_WIDGET_ALLOWIMPORT);
			boolean allowImport = Boolean.parseBoolean(allowImportText);

			IPath pluginPath = new Path(getPluginFolder(pluginID).getPath());

			IPath path = pluginPath.append("bin");
			for(String s : className.split("\\."))
				path = path.append(s);
			path = path.addFileExtension("class");

			try {
				Bundle bundle = Platform.getBundle(pluginID);
				Class<?> clazz = null;
				try {					
					clazz = bundle.loadClass(className);			
				}
				catch(ClassNotFoundException ex2) {
					AguiaJActivator.handlePluginError("Check plugin.xml, class not found: " + className);
					continue;
				}
				
				try {
					handleIcon(pluginID, objWidget, clazz);

					for(Method promotion : createPromotionsToAccessor(objWidget, clazz))
						ClassModel.getInstance().addAccessorPromotion(promotion);

					for(ClassMemberFilter f : createFilters(objWidget, clazz))
						ClassModel.getInstance().addFilter(f);

					Class<? extends VisualizationWidget<?>> view = null;

					if(objWidget.getAttribute(OBJECT_WIDGET_VIEW) != null) {
						view = (Class<? extends VisualizationWidget<?>>) bundle.loadClass(objWidget.getAttribute(OBJECT_WIDGET_VIEW));
					}

					ClassModel.getInstance().addPluginClass(clazz, view, allowImport, pluginID);

					for(Class<?> inner : clazz.getClasses()) {		
						ClassModel.getInstance().addPluginClass(inner, null, allowImport, pluginID);
					}
				}
				catch(Exception ex2) {
					AguiaJActivator.handlePluginError(ex2.getMessage());
					continue;
				}
				plugins.put(pluginID, clazz);

				for(Class<?> inner : clazz.getClasses()) {							
					plugins.put(pluginID, inner);							
				}

				pluginClassFiles.put(className, path.toFile());

			} catch (InvalidRegistryObjectException e1) {
				e1.printStackTrace();
			} 
		}
	}

	private void handleIcon(String pluginID, final IConfigurationElement objWidget, Class<?> clazz) {
		String iconPath = objWidget.getAttribute(OBJECT_WIDGET_ICON);
		if(iconPath != null) {
			ImageDescriptor imgDesc = imageDescriptorFromPlugin(pluginID, iconPath);
			if(imgDesc != null)
				pluginTypeImages.put(clazz, imgDesc.createImage());
		}
	}

	private List<Method> createPromotionsToAccessor(IConfigurationElement objWidget, Class<?> clazz) {
		List<Method> promotions = Lists.newArrayList();

		for(IConfigurationElement prom : objWidget.getChildren(OBJECT_WIDGET_PROMOTE)) {
			List<String> methodNames = getMethodNames(prom);
			for(Method m : clazz.getMethods())
				if(methodNames.contains(m.getName()))
					promotions.add(m);
		}
		return promotions;
	}

	@SuppressWarnings("unchecked")
	private void loadAccessorPolicyPlugins() {
		IConfigurationElement[] config = 
			Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_ACCESSOR_POLICY);

		for (final IConfigurationElement e : config) {
			String pluginID = e.getContributor().getName();
			String className = e.getAttribute(ACCESSOR_POLICY_ID);
			String desc = e.getAttribute(ACCESSOR_POLICY_DESCRIPTION);
			Bundle bundle = Platform.getBundle(pluginID);
			Class<? extends AccessorMethodDetectionPolicy> clazz = null;
			try {
				clazz = (Class<? extends AccessorMethodDetectionPolicy>) bundle.loadClass(className);
				accessorPolicies.put(desc, clazz);
			} 
			catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			}	
		}
	}

	private List<ClassMemberFilter> createFilters(final IConfigurationElement objWidget, Class<?> clazz) {
		List<ClassMemberFilter> filters = Lists.newArrayList();

		for(IConfigurationElement ex : objWidget.getChildren(OBJECT_WIDGET_EXCLUDE))
			filters.add(new NameBasedFilter(clazz, getMethodNames(ex), true));

		for(IConfigurationElement ex : objWidget.getChildren(OBJECT_WIDGET_INCLUDE))
			filters.add(new NameBasedFilter(clazz, getMethodNames(ex), false));

		return filters;
	}	 

	private List<String> getMethodNames(IConfigurationElement ex) {
		List<String> methodNames = Lists.newArrayList();
		for(IConfigurationElement m : ex.getChildren(OBJECT_WIDGET_METHOD)) {
			String name = m.getAttribute(OBJECT_WIDGET_METHOD_ID);
			methodNames.add(name);				
		}
		return methodNames;
	}

	public void loadClasses(IPath workingDir) {
		assert workingDir.toFile().exists() && workingDir.toFile().isDirectory();

		this.workingDir = workingDir;

		AguiaClassLoader.newClassLoader();

		packagesClasses = readClasses(workingDir);

		// try bin directory, if exists		
		if(packagesClasses.isEmpty()) {
			IPath binDirPath = workingDir.append("bin");
			File binDir = binDirPath.toFile();
			if(binDir.exists() && binDir.isDirectory()) {
				Multimap<String, Class<?>> tmp = readClasses(binDirPath);
				if(!tmp.isEmpty()) {					
					packagesClasses = tmp;
					this.workingDir = binDirPath;
				}
			}
		}

		// try a bin directory on parent folder, if exists		
		if(packagesClasses.isEmpty()) {
			IPath binTry = workingDir.removeLastSegments(1).append("bin");
			File binTryDir = binTry.toFile();
			if(binTryDir.exists() && binTryDir.isDirectory()) {
				Multimap<String, Class<?>> tmp = readClasses(binTry);
				if(!tmp.isEmpty()) {
					packagesClasses = tmp;
					this.workingDir = binTry;
				}
			}			
		}

		// TODO : rever
		//		for(String key : packagesClasses.keySet())
		//			packagesClasses.putAll(key, reorderGeneralizationsFirst(packagesClasses.get(key)));

		// filter non-visible classes
		for(Iterator<Class<?>> it = packagesClasses.values().iterator(); it.hasNext(); ) {
			Class<?> c = it.next();
			if(!ClassModel.getInstance().getInspector().isClassVisible(c))
				it.remove();
		}
	}

	// TODO: to ClassModel
	private static Multimap<String, Class<?>> readClasses(IPath workingDir) {
		Map<String,List<Class<?>>> classes = ReflectionUtils.readClassFiles(workingDir);
		Multimap<String, Class<?>> ret = ArrayListMultimap.create();
		for(String key : classes.keySet()) {
			ret.putAll(key, classes.get(key));
		}

		return ret;
	}

	//	private static List<Class<?>> reorderGeneralizationsFirst(Collection<Class<?>> list) {
	//		List<Class<?>> ret = new ArrayList<Class<?>>();
	//		for(Class<?> c: list) {
	//			if(list.contains(c.getSuperclass()))
	//				ret.add(ret.size(), c);
	//			else
	//				ret.add(0, c);
	//		}
	//		return ret;
	//	}

}
