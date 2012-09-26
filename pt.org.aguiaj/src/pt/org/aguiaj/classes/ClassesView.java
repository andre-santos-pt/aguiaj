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
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
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

	private BiMap<String, CTabItem> packageMapping;

	private CTabFolder packageTabs;
	private Composite parent;

	private boolean firstTime;

	private Multimap<String, Class<?>> packagesClasses;
	private Set<ClassAreaWidget> classAreaWidgets;

	private Map<String, Action> importActionMap;
	
	public ClassesView() {
		instance = this;	
		firstTime = true;
		packageMapping = HashBiMap.create();
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
		parent.setLayout(new FillLayout());
		packageTabs = new CTabFolder(parent, SWT.BORDER);
		packageTabs.setSimple(false);
		packageTabs.addCTabFolder2Listener(new CTabFolder2Adapter() {
			public void close(CTabFolderEvent event) {
				String packageName = packageMapping.inverse().get(event.item);
				removeTab(packageName);					
			}
		});
		DragNDrop.addFileDragNDropSupport(packageTabs);		
		if(firstTime) {
			createActions();
			firstTime = false;
		}	
		
		createTabs();
	}

	private void createTabs() {
		packagesClasses = AguiaJActivator.getDefault().getPackagesClasses();

		addActivePluginClasses();
		
		ClassModel.getInstance().handleIconMapping();
		
		List<String> packageNames = new ArrayList<String>(packagesClasses.keySet());
		Collections.sort(packageNames);

		cleanUpOldPackages(packageNames);
		filterPluginPackages(packageNames); // no point to redo the UI		
		
		createTabClassAreas(packageNames);
		refreshLayout();
	}

	private void refreshLayout() {
		parent.layout(true, true);
		for(ClassAreaWidget widget : classAreaWidgets)
			widget.refreshSize();
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
			((CTabFolder) packageTabs).setSelection(tabIndex);

		refreshLayout();
	}

	public void updateClassWidgets() {
		for(ClassAreaWidget widget : classAreaWidgets)
			widget.updateClassWidgets();
	}

	
	private void createTabClassAreas(List<String> packageNames) {
		for(String packageName : packageNames) {
			CTabItem tab = null;
			
			if(packageMapping.containsKey(packageName)) {
				tab = packageMapping.get(packageName);
				ClassAreaWidget classArea = (ClassAreaWidget) tab.getControl();
				classArea.dispose();
			}
			
			Collection<Class<?>> classList = packagesClasses.get(packageName);

			ClassAreaWidget packageArea = new ClassAreaWidget(packageTabs, packageName, classList);
			
			if(tab == null)
				tab = createTab(packageName);		

			tab.setControl(packageArea);
			
			packageMapping.put(packageName, tab);
			classAreaWidgets.add(packageArea);
		}	
	}

	private void cleanUpOldPackages(List<String> packageNames) {
		for(Iterator<String> it = packageMapping.keySet().iterator(); it.hasNext(); ) {
			String key = it.next();
			if(!packageNames.contains(key)) {
				packageMapping.get(key).dispose();
				it.remove();
			}
		}
	}

	private void filterPluginPackages(List<String> packageNames) {
		for(Iterator<String> it = packageNames.iterator(); it.hasNext(); ) {
			String pckName = it.next();
			if(AguiaJActivator.getDefault().isPluginPackage(pckName) &&
				packageMapping.containsKey(pckName))
				
				it.remove();							
		}
	}

	private CTabItem createTab(String packageName) {
		int style = AguiaJActivator.getDefault().isPluginPackage(packageName) 
				? SWT.CLOSE : SWT.NONE;
	
		CTabItem tab = new CTabItem(packageTabs, style);
		tab.setText(packageName.equals("") ? "default" : packageName);	
		if(AguiaJActivator.getDefault().isPluginPackage(packageName))
			tab.setImage(AguiaJImage.IMPORTED_PACKAGE.getImage());
		else
			tab.setImage(AguiaJImage.PACKAGE.getImage());
		return tab;
	}


	

	public void removeTab(final String packageName) {
		if(packageMapping.containsKey(packageName)) {
			packageMapping.remove(packageName).dispose();
			if(AguiaJActivator.getDefault().isPluginPackage(packageName))
				importActionMap.get(packageName).setEnabled(true);	
			ClassModel.getInstance().deactivatePackage(packageName);
			reload(null);
		}
	}
	
	public void selectPackage(String packageName) {
		CTabItem tab = packageMapping.get(packageName);
		if(tab != null)
			packageTabs.setSelection(tab);
	}

	public String getActivePlugin() {
		int selection = packageTabs.getSelectionIndex();
		if(selection == -1)
			return null;

		CTabItem tab = packageTabs.getItem(selection);
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
