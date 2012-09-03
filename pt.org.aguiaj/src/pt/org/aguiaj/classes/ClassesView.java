/*******************************************************************************
 * Copyright (c) 2012 André L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     André L. Santos - initial API and implementation
 ******************************************************************************/
package pt.org.aguiaj.classes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.ISizeProvider;
import org.eclipse.ui.part.ViewPart;

import pt.org.aguiaj.common.AguiaJImage;
import pt.org.aguiaj.common.DragNDrop;
import pt.org.aguiaj.common.SWTUtils;
import pt.org.aguiaj.core.AguiaJActivator;
import pt.org.aguiaj.core.commands.ChangeWorkingDirCommand;
import pt.org.aguiaj.core.commands.ReloadClassesCommand;
import pt.org.aguiaj.extensibility.AguiaJContribution;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class ClassesView extends ViewPart implements ISizeProvider {

	private static ClassesView instance;

	private BiMap<String, TabItem> classAreas;

	private TabFolder packageTabs;
	private Composite parent;

	private boolean firstTime;

	private Multimap<String, Class<?>> packagesClasses;
	private Set<ClassAreaWidget> classAreaWidgets;

	private Map<String, Action> importActionMap;
	
	public ClassesView() {
		instance = this;	
		classAreas = HashBiMap.create();
		firstTime = true;
		classAreaWidgets = Sets.newHashSet();
		
		importActionMap = Maps.newHashMap();
	}

	public static ClassesView getInstance() {
		if(instance == null) 	
			SWTUtils.showView(AguiaJContribution.CLASSES_VIEW);			

		return instance;
	}

	private void createActions() {
		IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
		Action refreshAction = new Action("Refresh") {
			public void run() { 
				try {
					ReloadClassesCommand reload = new ReloadClassesCommand();
					reload.execute(null);					
				} catch (ExecutionException e) {					
					e.printStackTrace();
				}
			}
		};

		IMenuManager dropDownMenu = getViewSite().getActionBars().getMenuManager();

		dropDownMenu.add(ChangeWorkingDirCommand.createAction());
		dropDownMenu.add(createImportMenu());

		refreshAction.setImageDescriptor(AguiaJImage.REFRESH.getImageDescriptor());
		toolbarManager.add(refreshAction);
	}

	private MenuManager createImportMenu() {
		MenuManager importMenu = new MenuManager("Import package", null);

		List<String> packageNames = new ArrayList<String>(AguiaJActivator.getDefault().getPluginPackages());
		Collections.sort(packageNames);

		for(final String packageName : packageNames) {
			final Collection<Class<?>> classes = AguiaJActivator.getDefault().getPackageClasses(packageName);

			boolean toImport = false;			
			for(Class<?> c : classes)
				if(ClassModel.getInstance().isPluginTypeToImport(c))
					toImport = true;

			if(toImport) {
				final Action action = new Action(packageName) {	
					public String getId(){
						return "import package";				
					}

					public void run() { 
						ClassModel.getInstance().activatePackage(packageName);

						ReloadClassesCommand reload = new ReloadClassesCommand();
						try {
							reload.execute(null);
						} catch (ExecutionException e) {						
							e.printStackTrace();
						}
						setEnabled(false);
						ClassesView.getInstance().selectPackage(packageName);
					}
				};
				importMenu.add(action);
				importActionMap.put(packageName, action);
			}			
		}
		return importMenu;
	}

	public void packageWasImported(String packageName) {
		importActionMap.get(packageName).setEnabled(false);
	}

	public void createPartControl(Composite parent) {	
		this.parent = parent;
		packageTabs = new TabFolder(parent, SWT.BORDER);
		DragNDrop.addFileDragNDropSupport(packageTabs);		
		createTabs();
	}

	private void createTabs() {
		packagesClasses = AguiaJActivator.getDefault().getPackagesClasses();

		addActivePluginClasses();
		createTabClassAreas();

		if(firstTime) {
			createActions();
			firstTime = false;
		}
	}


	private void addActivePluginClasses() {
		for(Class<?> clazz : ClassModel.getInstance().getActivePluginTypes()) {
			String pckName = clazz.getPackage().getName();
			packagesClasses.put(pckName, clazz);
		}
	}

	public void reload(IPath newPath) {
		int tabIndex = packageTabs.getSelectionIndex();

		if(newPath == null)
			AguiaJActivator.getDefault().reloadClasses();
		else
			AguiaJActivator.getDefault().loadClasses(newPath);

		createTabs();

		if(tabIndex != -1)
			((TabFolder) packageTabs).setSelection(tabIndex);

		packageTabs.layout();
		parent.layout();	
	}

	public void updateClassWidgets() {
		for(ClassAreaWidget widget : classAreaWidgets)
			widget.updateClassWidgets();
	}

	private void createTabClassAreas() {
		ClassModel.getInstance().handleIconMapping();
		
		List<String> packageNames = new ArrayList<String>(packagesClasses.keySet());
		Collections.sort(packageNames);

		// clean up
		for(String key : classAreas.keySet())
			if(!packageNames.contains(key))
				classAreas.get(key).dispose();
		
		// filter existing plugin packages (no point to redo the UI)
		for(Iterator<String> it = packageNames.iterator(); it.hasNext(); ) {
			String pckName = it.next();
			if(AguiaJActivator.getDefault().isPluginPackage(pckName) &&
				classAreas.containsKey(pckName))
				it.remove();							
		}
		
		for(String packageName : packageNames) {
			TabItem tab = null;
			
			if(classAreas.containsKey(packageName)) {
				tab = classAreas.get(packageName);
				ClassAreaWidget classArea = (ClassAreaWidget) tab.getControl();
				classArea.dispose();
			}
			
			Collection<Class<?>> classList = packagesClasses.get(packageName);

			ClassAreaWidget packageArea = new ClassAreaWidget(packageTabs, packageName, classList);
			
			if(tab == null)
				tab = new TabItem(packageTabs, SWT.NONE);

			if(AguiaJActivator.getDefault().isPluginPackage(packageName))
				tab.setImage(AguiaJImage.IMPORTED_PACKAGE.getImage());
			else
				tab.setImage(AguiaJImage.PACKAGE.getImage());

			tab.setText(packageName.equals("") ? "default" : packageName);

			tab.setControl(packageArea);
			classAreas.put(packageName, tab);
			classAreaWidgets.add(packageArea);
		}
	}

	public void addTab(final String packageName, List<Class<?>> classes) {
		if(!classAreas.containsKey(packageName)) {
			ClassAreaWidget packageArea = new ClassAreaWidget(packageTabs, packageName, classes);
			final TabItem tab = new TabItem(packageTabs, SWT.NONE);
			tab.setImage(AguiaJImage.PACKAGE.getImage());			
			tab.setText(packageName.equals("") ? "default" : packageName);
			tab.setControl(packageArea);			
			classAreas.put(packageName, tab);
			classAreaWidgets.add(packageArea);
		}		
	}

	public void removeTab(final String packageName) {
		if(classAreas.containsKey(packageName)) {
			classAreas.remove(packageName).dispose();
			importActionMap.get(packageName).setEnabled(true);	
			ClassModel.getInstance().deactivatePackage(packageName);
			reload(null);
		}
	}
	
	public void selectPackage(String packageName) {
		TabItem tab = classAreas.get(packageName);
		if(tab != null)
			packageTabs.setSelection(tab);
	}

	public String getSelectedPackage() {
		int selection = packageTabs.getSelectionIndex();
		if(selection == -1)
			return null;

		TabItem tab = packageTabs.getItem(selection);
		ClassAreaWidget packageArea = (ClassAreaWidget) tab.getControl();

		return packageArea.getPackageName();
	}

	public Collection<Class<?>> getSelectedPackageClasses() {
		String pck = getSelectedPackage();
		Collection<Class<?>> classes = Collections.emptySet();
		if(pck != null)
			classes = packagesClasses.get(pck);

		return classes;
	}

	public String getActivePlugin() {
		int selection = packageTabs.getSelectionIndex();
		if(selection == -1)
			return null;

		TabItem tab = packageTabs.getItem(selection);
		ClassAreaWidget packageArea = (ClassAreaWidget) tab.getControl();

		if(packageArea.isPluginPackage())
			return packageArea.getPluginId();
		else
			return null;			
	}


	public void setFocus() {
		packageTabs.setFocus();

	}


	public int computePreferredSize(boolean width, int availableParallel, int availablePerpendicular, int preferredResult) {		
		parent.pack();
		int w = parent.getClientArea().width;
		if(w > 350)
			w = 350;
		return width ? w : preferredResult;
	}

	public int getSizeFlags(boolean width) {	
		return 0;
	}

}
