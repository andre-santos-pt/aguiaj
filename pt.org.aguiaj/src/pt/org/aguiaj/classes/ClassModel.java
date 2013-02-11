/*******************************************************************************
 * Copyright (c) 2013 Andre L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andre L. Santos - initial API and implementation
 ******************************************************************************/
package pt.org.aguiaj.classes;


import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.graphics.Image;

import pt.org.aguiaj.common.AguiaJImage;
import pt.org.aguiaj.core.AguiaJActivator;
import pt.org.aguiaj.core.AguiaJParam;
import pt.org.aguiaj.core.InspectionPolicy;
import pt.org.aguiaj.core.Inspector;
import pt.org.aguiaj.core.typewidgets.WidgetFactory;
import pt.org.aguiaj.extensibility.VisualizationWidget;
import pt.org.aguiaj.standard.StandardInspectionPolicy;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class ClassModel {
	private final Set<Class<?>> userClasses;
	private final Map<String, Image> iconMapping;

	private final Set<Class<?>> classSet;

	private final Map<Class<?>,String> pluginClassSet;
	private final Set<Class<?>> activePluginClasses;
	private final Set<Class<?>> pluginClassesForImport;

	private final Map<Class<?>, ImmutableList<Constructor<?>>> visibleConstructors;
	private final Map<Class<?>, List<Field>> invisibleAttributes;
	private final Map<Class<?>, List<Field>> visibleAttributes;
	private final Map<Class<?>, List<Method>> queryMethods;	
	private final Map<Class<?>, Map<Class<?>, List<Method>>> commandMethodsByType;
	private final Map<Class<?>, List<Method>> allAvailableMethods;	
	
	private final Multimap<Class<?>, ClassMemberFilter> filterMap;

	private final Multimap<Class<?>, Method> promotions;
	
	private Inspector inspector;
	
	private static ClassModel instance;

	public ClassModel() {
		userClasses = newHashSet();
		iconMapping = newHashMap();

		classSet = newHashSet();
		pluginClassSet = Maps.newLinkedHashMap();
		activePluginClasses = Sets.newLinkedHashSet();
		pluginClassesForImport = newHashSet();

		visibleConstructors = newHashMap();
		invisibleAttributes = newHashMap();
		visibleAttributes = newHashMap();
		queryMethods = newHashMap();
		commandMethodsByType = newHashMap();
		allAvailableMethods = newHashMap();
		
		filterMap = ArrayListMultimap.create();
		promotions = ArrayListMultimap.create();
		
		inspector = new Inspector(loadInspectionPolicy()); 
	}
	
	public static ClassModel getInstance() {
		if(instance == null)
			instance = new ClassModel();
		
		return instance;
	}
	
	private InspectionPolicy loadInspectionPolicy() {
		try {
			Class<?> inspectionPolicyClass = Class.forName(AguiaJParam.INSPECTION_POLICY.getString());
			return (InspectionPolicy) inspectionPolicyClass.newInstance();

		} catch (Exception e) {
			e.printStackTrace();
			return new StandardInspectionPolicy();
		}		
	}
	
	public static Inspector getInspector() {
		return getInstance().inspector;
	}



	
	
	void handleIconMapping() {
		userClasses.clear();

		Map<String, Image> previousMapping = new HashMap<String, Image>(iconMapping);
//		iconMapping.clear();

		userClasses.addAll(AguiaJActivator.getDefault().getPackagesClasses().values());		

		for(Class<?> c : userClasses) {
			String key = c.getName();
			if(hasIcon(c) && previousMapping.containsKey(key)) {				
				iconMapping.put(key, previousMapping.get(key));
			}
		}

		for(Class<?> c : userClasses) {
			String key = c.getName();
//			if(isPluginClass(c)) {
//				final Image icon = AguiaJActivator.getDefault().getPluginTypeIcon(c);
//				if(icon != null) {
//					iconMapping.put(key,icon);
//					continue;
//				}
//			}
			
			if(hasIcon(c) && !iconMapping.containsKey(key)) {				
				final AguiaJImage typeIcon = AguiaJImage.nextTypeIcon();
				iconMapping.put(key, typeIcon.getImage());
			}
		}
	}
	
	public boolean isClassInUse(Class<?> clazz) {
		return classSet.contains(clazz);
	}
	
	public boolean isUserClass(String name) {
		for(Class<?> c : classSet)
			if(!isPluginClass(c) && c.getName().equals(name))
				return true;
		
		return false;
	}

	public void addFilter(ClassMemberFilter filter) {
		filterMap.put(filter.getTargetType(), filter);
	}

	public void addAccessorPromotion(Method method) {
		promotions.put(method.getDeclaringClass(), method);
	}
	
	private boolean hasIcon(Class<?> clazz) {
		return 
		!clazz.isEnum() &&
		(
		hasSubClasses(clazz) || 
		clazz.isInterface() || 
		Modifier.isAbstract(clazz.getModifiers())
		);
	}

	public Set<Class<?>> getAllClasses() {
		return Collections.unmodifiableSet(classSet);
	}

	public void addPluginClass(Class<?> clazz, Class<? extends VisualizationWidget<?>> view, boolean allowImport, String pluginId)
	throws Exception {
		if(!pluginClassSet.containsKey(clazz)) {
			if(view != null)
				WidgetFactory.INSTANCE.addVisualizationWidgetType(new Class<?>[] {clazz}, view);		

			if(allowImport)
				pluginClassesForImport.add(clazz);

			pluginClassSet.put(clazz, pluginId);
			
			iconMapping.put(clazz.getName(), AguiaJActivator.getDefault().getPluginTypeIcon(clazz));
		}
	}


	public void activatePlugin(String pluginId) {		
		for(Class<?> c : getPluginClasses(pluginId)) {
			if(isPluginTypeToImport(c)) {
				activatePluginClass(c);
			}
		}
	}

	public void activatePackage(String packageName) {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		for(Class<?> c : pluginClassSet.keySet()) {
			if(c.getPackage().getName().equals(packageName))
				classes.add(c);
		}

		for(Class<?> c : classes) {
			if(isPluginTypeToImport(c)) {
				activatePluginClass(c);
			}
		}
	}
	
	public void deactivatePackage(String packageName) {
		for(Iterator<Class<?>> it = activePluginClasses.iterator(); it.hasNext(); )
			if(it.next().getPackage().getName().equals(packageName))
				it.remove();
	}

	private void activatePluginClass(Class<?> clazz) {
		assert pluginClassSet.containsKey(clazz);
		activePluginClasses.add(clazz);
	}

	private List<Class<?>> getPluginClasses(String pluginId) {
		List<Class<?>> classes = new ArrayList<Class<?>>();

		for(Class<?> c : pluginClassSet.keySet()) {
			if(pluginClassSet.get(c).equals(pluginId))
				classes.add(c);
		}

		return classes;
	}


	public void addClass(Class<?> clazz) {
		classSet.add(clazz);

		visibleConstructors.put(clazz, ImmutableList.copyOf(inspector.getVisibleConstructors(clazz)));
		invisibleAttributes.put(clazz, filteredInvisibleAttributes(clazz));		
		visibleAttributes.put(clazz, filteredAttributes(clazz));	

		List<Method> filteredQueryMethods = filteredAccessorMethods(clazz);
		queryMethods.put(clazz, filteredQueryMethods);	

		Map<Class<?>,List<Method>> filteredCommandMethodsByType = filteredCommandMethods(clazz, filteredQueryMethods);
		commandMethodsByType.put(clazz, filteredCommandMethodsByType);
		
		List<Method> all = new ArrayList<Method>();
		all.addAll(filteredQueryMethods);
		all.addAll(inspector.getCommandMethods(clazz));
		all.addAll(inspector.getVisibleStaticMethods(clazz));
		allAvailableMethods.put(clazz, all);
	}

	private List<Field> filteredInvisibleAttributes(Class<?> clazz) {
		List<Field> invAttributes = inspector.getInvisibleInstanceAttributes(clazz);
		for(Iterator<Field> it = invAttributes.iterator(); it.hasNext(); ) {
			Field field = it.next();	
			for(ClassMemberFilter filter : findFilters(clazz))
				if(filter.filter(field)) {
					it.remove();
					break;
				}
		}
		return invAttributes;
	}

	private List<Field> filteredAttributes(Class<?> clazz) {
		List<Field> attributes = inspector.getVisibleInstanceAttributes(clazz);
		List<ClassMemberFilter> filters = findFilters(clazz);
		for(Iterator<Field> it = attributes.iterator(); it.hasNext(); ) {
			Field field = it.next();		
			for(ClassMemberFilter f : filters) {
				if(f.filter(field)) {
					it.remove();
				}
			}
		}
		return attributes;
	}
	
//	private List<Field> filteredStaticAttributes(Class<?> clazz) {
//		for(Field field : clazz.getFields())
//			if(inspector.getVisibleAttributes(clazz, true) 
//					
//					.getPolicy().isStaticFieldVisible(field) && !field.isEnumConstant()) {
//				field.setAccessible(true);
//				staticFields.add(field);
//			}
//	}

	private List<Method> filteredAccessorMethods(Class<?> clazz) {
		List<Method> queryMethods = Inspector.getAccessorMethods(clazz);
		final List<ClassMemberFilter> filters = findFilters(clazz);
		for(Iterator<Method> it = queryMethods.iterator(); it.hasNext(); ) {
			Method method = it.next();
			for(ClassMemberFilter f : filters)
				if(f.filter(method)) {
					it.remove();
					break;
				}
		}
		
		if(!filters.isEmpty()) {
			Collections.sort(queryMethods, new Comparator<Method>() {

				@Override
				public int compare(Method m1, Method m2) {
					int m1Index = 0;
					int m2Index = 0;
					for(ClassMemberFilter f : filters) {
						if(f instanceof NameBasedFilter) {
							String[] methodNames = ((NameBasedFilter) f).getMethodNames();							
							for(int i = 0; i < methodNames.length; i++) {
								if(m1.getName().equals(methodNames[i]))
									m1Index = i;
								else if(m2.getName().equals(methodNames[i]))
									m2Index = i;
							}
						}
					}
					return new Integer(m1Index).compareTo(m2Index);
				}
			});
		}
		
		return queryMethods;
	}


	private Map<Class<?>,List<Method>> filteredCommandMethods(Class<?> clazz, List<Method> queryMethods) {					
		Map<Class<?>,List<Method>> commandMethodsByType = inspector.getCommandMethodsByType(clazz);
		List<ClassMemberFilter> filters = findFilters(clazz);
		
		for(List<Method> commandMethods : commandMethodsByType.values()) {
			for(Iterator<Method> it = commandMethods.iterator(); it.hasNext(); ) {
				Method method = it.next();
				for(ClassMemberFilter f : filters) {					
					if(f.filter(method) && !toPromote(clazz, method)) {
						it.remove();
						break;
					}
				}
				
				if(toPromote(clazz, method)) {
					queryMethods.add(method);
					it.remove();
				}
			}
		}
		return commandMethodsByType;
	}

	private List<ClassMemberFilter> findFilters(Class<?> clazz) {
		List<ClassMemberFilter> filters = new ArrayList<ClassMemberFilter>();
		for(Class<?> key : filterMap.keySet()) {
			if(key.isAssignableFrom(clazz))
				filters.addAll(filterMap.get(key));
		}
		return filters;
	}
	
	private boolean toPromote(Class<?> clazz, Method method) {
		for(Class<?> key : promotions.keySet())
			if(key.isAssignableFrom(clazz))
				for(Method m : promotions.get(key))
					if(m.getName().equals(method.getName()))
						return true;
			
		return false;
	}

	public void clearClasses() {
		classSet.clear();
		visibleConstructors.clear();
		invisibleAttributes.clear();
		visibleAttributes.clear();
		queryMethods.clear();
		commandMethodsByType.clear();

		addDefaultClasses();
		inspector = new Inspector(loadInspectionPolicy());
	}


	public void addDefaultClasses() {
		addClass(Object.class);		

		for(Class<?> pluginClass : pluginClassSet.keySet())
			addClass(pluginClass);		
	}

	public boolean isPluginClass(Class<?> clazz) {
		return pluginClassSet.containsKey(clazz);
	}

	public String getRelatedPluginClass(Class<?> clazz) {
		for(Class<?> c : pluginClassSet.keySet())
			if(c.isAssignableFrom(clazz))
				return pluginClassSet.get(c);

		return null;
	}

	public String getPluginId(Class<?> clazz) {
		return pluginClassSet.get(clazz);
	}

	public boolean isPluginClass(String fullName) {
		for(Class<?> c : pluginClassSet.keySet())
			if(c.getName().equals(fullName))
				return true;
		return false;
	}

	public Class<?> getPluginClass(String name) {
		for(Class<?> c : pluginClassSet.keySet()) {
			if(c.getName().equals(name))
				return c;
		}
		return null;
	}

	public Iterable<Class<?>> getPluginTypes() {
		return pluginClassSet.keySet();
	}

	public Iterable<Class<?>> getActivePluginTypes() {
		return activePluginClasses;
	}

	public boolean isPluginTypeActive(Class<?> clazz) {
		return activePluginClasses.contains(clazz);
	}
	
	public Iterable<Class<?>> getPluginsTypesForImport() {
		return pluginClassesForImport;
	}

	public boolean isPluginTypeToImport(Class<?> type) {
		return pluginClassesForImport.contains(type);
	}

	public boolean ambiguousClassName(Class<?> clazz) {
		for(Class<?> c : classSet)
			if(!c.equals(clazz) && c.getSimpleName().equals(clazz.getSimpleName()))
				return true;
		return false;
	}

	public boolean isPolymorphic(Class<?> clazz) {
		return hasSubClasses(clazz) || clazz.isInterface();
	}

	public boolean hasSubClasses(Class<?> clazz) {
		for(Class<?> c: userClasses)
			for(Class<?> super_c : Inspector.superClasses(c))
				if(super_c.equals(clazz))
					return true;
		return false;
	}
	
	
	public List<Constructor<?>> getVisibleConstructors(Class<?> clazz) {
		if(!visibleConstructors.containsKey(clazz))
			return Collections.emptyList();
		else
			return visibleConstructors.get(clazz);
	}

	// Invisible attributes
	public List<Field> getInvisibleAttributes(Class<?> clazz) {
		if(!invisibleAttributes.containsKey(clazz))
			return Collections.emptyList();
		else
			return invisibleAttributes.get(clazz);
	}



	// visible attributes
	public List<Field> getVisibleAttributes(Class<?> clazz) {
		if(!visibleAttributes.containsKey(clazz))
			return Collections.emptyList();
		else
			return visibleAttributes.get(clazz);
	}




	
	
	// query methods
	public List<Method> getAccessorMethods(Class<?> clazz) {
		if(queryMethods.containsKey(clazz)) {
			return queryMethods.get(clazz);
		}
		else {
			Class<?> type = getBottomMostCompatibleType(queryMethods.keySet(), clazz);
			return queryMethods.get(type);
		}
	}


	public List<Method> getAllAvailableMethods(Class<?> clazz) {
		if(allAvailableMethods.containsKey(clazz)) {
			return allAvailableMethods.get(clazz);
		}
		else {
			Class<?> type = getBottomMostCompatibleType(allAvailableMethods.keySet(), clazz);
			return allAvailableMethods.get(type);
		}	
	}



	// command methods
	public Map<Class<?>, List<Method>> getCommandMethodsByType(Class<?> clazz) {
		if(commandMethodsByType.containsKey(clazz)) {
			return commandMethodsByType.get(clazz);
		}
		else {
			Class<?> type = getBottomMostCompatibleType(commandMethodsByType.keySet(), clazz);
			return commandMethodsByType.get(type);
		}		
	}
	
	


	private static Class<?> getBottomMostCompatibleType(Collection<Class<?>> list, Class<?> clazz) {
		List<Class<?>> compatible = new ArrayList<Class<?>>();
		for(Class<?> type : list) {
			if(type.isAssignableFrom(clazz))
				compatible.add(type);
		}

		assert !compatible.isEmpty() : "There should be at least one compatible type - Object!";
		
		Collections.sort(compatible, new ClassHierarchyComparator());		
		return compatible.get(compatible.size() - 1);
	}


	public Image getIcon(Class<?> clazz) {
		Image icon = iconMapping.get(clazz.getName());
		if(icon == null)
			return AguiaJImage.QUESTION.getImage();  	
		else
			return icon;
	}
}
