/*******************************************************************************
 * Copyright (c) 2014 Andre L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andre L Santos - developer
 ******************************************************************************/
package pt.org.aguiaj.classes;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.osgi.framework.Bundle;

import pt.org.aguiaj.common.AguiaJColor;
import pt.org.aguiaj.common.AguiaJImage;
import pt.org.aguiaj.common.DragNDrop;
import pt.org.aguiaj.common.SWTUtils;
import pt.org.aguiaj.common.widgets.IconWidget;
import pt.org.aguiaj.common.widgets.LabelWidget;
import pt.org.aguiaj.core.AguiaJActivator;
import pt.org.aguiaj.core.AguiaJParam;
import pt.org.aguiaj.core.ReflectionUtils;
import pt.org.aguiaj.core.documentation.DocumentationLinking;
import pt.org.aguiaj.core.documentation.DocumentationView;
import pt.org.aguiaj.standard.StandardNamePolicy;

import com.google.common.collect.Sets;

class PackageWidget extends Composite {
	private static final int SPACING = 20;
	private static final int MARGIN = 5;

	private ScrolledComposite scrl;
	private Composite area;
	private Set<ClassWidget> classWidgets;
	private String pluginId;
	private String packageName;

	public PackageWidget(Composite parent, final String packageName, Collection<Class<?>> classes) {
		super(parent, SWT.NONE);
		this.packageName = packageName;

		setLayout(new FillLayout());

		classWidgets = Sets.newHashSet();

		scrl = new ScrolledComposite(this, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);	

		area = new Composite(scrl, SWT.NONE);
		area.setBackground(AguiaJColor.OBJECT_AREA.getColor());

		GridLayout areaLayout = new GridLayout(1, true);
		areaLayout.verticalSpacing = SPACING;
		areaLayout.marginBottom = MARGIN;
		areaLayout.marginTop = MARGIN;
		areaLayout.marginLeft = MARGIN;
		areaLayout.marginRight = MARGIN;

		area.setLayout(areaLayout);
		area.setToolTipText("Class Area (create objects and invoke static operations by pressing the buttons)");

		scrl.setContent(area);
		scrl.setExpandHorizontal(true);
		scrl.setExpandVertical(true);
		scrl.setAlwaysShowScrollBars(true);

		for(final Class<?> clazz : classes) {
			if(ClassModel.getInstance().isPluginClass(clazz)) {
				pluginId = ClassModel.getInstance().getPluginId(clazz);
			}
		}

		if(isPluginPackage())
			addPluginHeader();

		Iterator<Class<?>> it = classes.iterator();
		while(it.hasNext()) {
			Class<?> clazz = it.next();
			Composite w = null;
			if(ReflectionUtils.tryClass(clazz)) {
				if(clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers()) && !clazz.isEnum()) {
					w = new AbstractClassWidget(area, clazz);
				}
				else {
					w = new ClassWidget(area, clazz);
					w.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
					classWidgets.add((ClassWidget) w);
				}
			}
//			catch(Throwable e) {
//				if(w != null)
//					w.dispose();
//				new ErrorWidget(area, clazz);
//			}
			else {
				new ErrorWidget(area, clazz);
			}

			if(it.hasNext())
				new Label(area, SWT.SEPARATOR | SWT.HORIZONTAL).setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		}

		addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				scrl.setMinSize(area.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			}
		});

		SWTUtils.setColorRecursively(this, AguiaJColor.WHITE.getColor());
		DragNDrop.addFileDragNDropSupport(area);
		layout();
	}


	public void refreshSize() {
		if(!scrl.isDisposed() && !area.isDisposed()) {
			scrl.setMinSize(area.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			scrl.layout();
		}
	}

	//	public void updateClassWidgets() {
	//		for(ClassWidget widget : classWidgets)
	//			widget.updateFields();
	//	}

	public boolean isPluginPackage() {
		return pluginId != null;
	}

	public String getPluginId() {
		return pluginId;
	}

	public String getPackageName() {
		return packageName;
	}


	private void addPluginHeader() {
		Composite comp = new Composite(area, SWT.NONE);
		comp.setLayout(new RowLayout(SWT.VERTICAL));

		final String jarLocation = AguiaJActivator.getInstance().getPluginJarLocation(pluginId);

		new LabelWidget.Builder()
		.text("This package is provided by a plugin.")
		.small()
		.create(comp);

		new LabelWidget.Builder()
		.text("jar location")
		.small()
		.link()
		.create(comp)
		.addHyperlinkAction(new Listener () {
			public void handleEvent(Event event) {
				new PathDialog(Display.getDefault().getActiveShell(), jarLocation).open();
			}
		});

		handleDocumentationLink(comp);
	}

	private void handleDocumentationLink(Composite parent) {
		IPath path = new Path(AguiaJParam.DOC_ROOT.getString());

		for(String frag : packageName.split("\\."))
			path = path.append(frag);

		path = path.append(AguiaJParam.DOC_PACKAGESUMMARY.getString());

		final URL url = Platform.getBundle(pluginId).getEntry(path.toString());

		if(url != null) 
			new LabelWidget.Builder()
		.text("javadoc")
		.small()
		.link()
		.create(parent)
		.addHyperlinkAction(new Listener () {
			public void handleEvent(Event event) {
				try {
					URL fileurl = FileLocator.resolve(url);
					DocumentationView.getInstance().load(fileurl.getFile());
					DocumentationView.activate();
				}
				catch(IOException ex) {
					ex.printStackTrace();
				}
			}
		});
	}

	Set<ClassWidget> getClassWidgets() {
		return Collections.unmodifiableSet(classWidgets);
	}
	
	
	
	
	
	

	private static class ErrorWidget extends Composite {
		public ErrorWidget(Composite parent, Class<?> clazz) {
			super(parent, SWT.NONE);
			setLayout(new RowLayout(SWT.HORIZONTAL));		
			IconWidget.createForRowLayout(this, AguiaJImage.ERROR.getImage());									
			new LabelWidget.Builder()
			.text(clazz.getSimpleName())
			.huge()
			.toolTip("Class could not be loaded, due to compilation errors, static initialization errors, or missing dependencies.")
			.color(AguiaJColor.ALERT)
			.create(this);
		}
	}

	private static class AbstractClassWidget extends Composite  {

		public AbstractClassWidget(Composite parent, final Class<?> clazz) {
			super(parent, SWT.NONE);
			setLayout(new RowLayout(SWT.HORIZONTAL));
			IconWidget.createForRowLayout(this, clazz).setToolTipText("Polymorphic type");
			boolean link = ClassModel.getInstance().isPluginClass(clazz) &&
					(clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers()) && !Modifier.isFinal(clazz.getModifiers()));

			LabelWidget classNameLabel =  new LabelWidget.Builder()
			.text(StandardNamePolicy.prettyClassName(clazz))
			.big()
			.linkIf(link)
			.create(this);

			if(link && ClassModel.getInstance().isPluginClass(clazz)) {
				classNameLabel.addHyperlinkAction(new Listener () {
					public void handleEvent(Event event) {
						Bundle bundle = Platform.getBundle(AguiaJActivator.getInstance().getPluginId(clazz));
						Path path = new Path("src/" + clazz.getName().replace('.', '/') + ".java");
						URL fileURL = bundle.getEntry(path.toOSString());

						File fileToOpen = null;
						try {
							URL fileURL2 = FileLocator.resolve(fileURL);
							String encode = fileURL2.toString().replaceAll("\\s", "%20");
							URI uri = new URI(encode);
							fileToOpen = new File(uri);
						} catch (Exception e) {
							e.printStackTrace();
						}

						if (fileToOpen != null && fileToOpen.exists() && fileToOpen.isFile()) {
							IFileStore fileStore = EFS.getLocalFileSystem().getStore(fileToOpen.toURI());
							IFileInfo info = fileStore.fetchInfo();
							info.setAttribute(EFS.ATTRIBUTE_READ_ONLY, true);
							try {
								fileStore.putInfo(info, EFS.SET_ATTRIBUTES, null);
							} catch (CoreException e1) {
								e1.printStackTrace();
							} 
							IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
							try {
								IDE.openEditorOnFileStore( page, fileStore );
							} catch ( PartInitException e ) {
								e.printStackTrace();
							}
						}
					}
				});
			}
			DocumentationLinking.add(classNameLabel.getControl(), clazz);
		}
	}



}
