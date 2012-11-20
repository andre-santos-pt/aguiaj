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
package pt.org.aguiaj.aspects;

import static com.google.common.collect.Maps.newHashMap;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import pt.org.aguiaj.classes.ClassModel;
import pt.org.aguiaj.common.Reference;
import pt.org.aguiaj.objects.ObjectsView;
import pt.org.aguiaj.standard.StandardNamePolicy;

public aspect ObjectModel {

	private static class NullObject {  }
	private static final NullObject NULL_OBJECT = new NullObject();

	private final Map<String, Object> referenceTable;
	private final Map<String, Class<?>> referenceTypeTable;
	private final IdentityObjectSet objectSet;

	private final Map<String, Object> enumReferenceTable;

	public ObjectModel() {
		referenceTable = new LinkedHashMap<String, Object>();
		referenceTypeTable = newHashMap();
		objectSet = new IdentityObjectSet();
		enumReferenceTable = new LinkedHashMap<String, Object>();
	}	

	public static ObjectModel getInstance() {
		return ObjectModel.aspectOf();
	}

	// additions (objects and references)
	before(Object object, String reference, Class<?> refType) : 
		execution(void ObjectsView.addObjectWidget(Object,String,Class<?>)) && args(object, reference, refType) {	
		assert object != null;			
		objectSet.add(object); 
	}	

	before(Object object) : 
		execution(void ObjectsView.addDeadObjectWidget(Object)) && args(object) {	
		assert object != null;			
		objectSet.add(object); 
	}	

	after(Class<?> type, String reference, Object object) :
		execution(void ObjectsView.addNonNullReference(Class<?>, String, Object)) && args(type, reference, object){
		addReference(type, object, reference);
	}

	after(Class<?> type, String reference) :
		execution(void ObjectsView.addNullReference(Class<?>, String)) && args(type, reference){
		addReference(type, null, reference);
	}

	after(Class<?> type) :
		execution(void ClassModel.addClass(Class<?>)) && args(type) {

		if(!ClassModel.getInstance().isPluginClass(type)) {
			List<Field> fields = ClassModel.getInspector().getVisibleStaticAttributes(type);
			fields.addAll(ClassModel.getInspector().getEnumFields(type));
			
			for(Field f : fields)  {
				Object obj = null;
				try {
					obj = f.get(null);
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				addReference(f.getType(), obj, refName(type, f));
			}
		}
	}
	
	private static String refName(Class<?> clazz, Field field) {
		return clazz.getSimpleName() + "." + field.getName();
	}


	private void addReference(Class<?> type, Object object, String reference) {
		if(!referenceTypeTable.containsKey(reference)) {
			referenceTypeTable.put(reference, type);
		}

		if(object == null)
			referenceTable.put(reference, NULL_OBJECT);
		else
			referenceTable.put(reference, object);
	}



	// removals (object and references)

	after(Object object) :
		execution(void ObjectsView.remove(Object)) && args(object) {
		assert objectSet.contains(object);		
		removeObject(object);
	}

	private void removeObject(Object object) {
		assert object != null;

		if(!object.getClass().isEnum())		
			objectSet.remove(object);

		for(String ref : referenceTable.keySet().toArray(new String[referenceTable.size()])) {
			if(referenceTable.get(ref) == object) {
				removeReference(ref);
			}
		}
	}


	before(String id) :
		execution(void ObjectsView.removeReference(String)) && args(id) {
		assert id != null;
		removeReference(id);
	}

	before(String id) :
		execution(void ObjectsView.removeNullReference(String)) && args(id) {
		assert id != null;
		removeReference(id);
	}

	private void removeReference(String id) {
		assert referenceTable.containsKey(id);
		referenceTable.remove(id);
		referenceTypeTable.remove(id);		
	}





	// objects------------------------------------------------------

	public Object[] getAllObjects() {
		return getObjects(Object.class);
	}

	public Object[] getObjects(Class<?> type) {
		assert type != null;

		IdentityObjectSet set = new IdentityObjectSet();

		for(Object o : objectSet.objects())
			if(!(o == NULL_OBJECT) && type.isInstance(o) && !isDeadObject(o))
				set.add(o);


		for(Object o : enumReferenceTable.values())
			if(type.isInstance(o))
				set.add(o);

		return set.objects();
	}

	public boolean objectExists(Object object) {
		return objectSet.contains(object);
	}

	public static Object getObject(String reference) {
		Object obj = aspectOf().referenceTable.get(reference);

		return obj == NULL_OBJECT ? null : obj;
	}



	// references ------------------------------------------------

	//	public List<Reference> getReferences(Class<?> type) {
	//		List<Reference> refs = new ArrayList<Reference>();
	//		
	//		for(String r : referenceTable.keySet()) {
	//			if(referenceTypeTable.get(r).equals(type)) {
	//				Object obj = referenceTable.get(r);
	//				refs.add(new Reference(r, type, obj));
	//			}
	//		}
	//		
	//		return refs;
	//	}

	public Map<String, Reference> getReferenceTable() {
		Map<String, Reference> table = newHashMap();
		for(String key : referenceTable.keySet()) {
			Object obj = referenceTable.get(key);
			if(obj == NULL_OBJECT)
				obj = null;
			table.put(key, new Reference(key, referenceTypeTable.get(key), obj));
		}
		return table;
	}

	public List<Reference> getCompatibleReferences(Class<?> type) {
		List<Reference> refs = new ArrayList<Reference>();

		for(String r : referenceTable.keySet()) {
			Class<?> refType = referenceTypeTable.get(r);

			if(type.isAssignableFrom(refType)) {
				Object obj = referenceTable.get(r);
				if(obj == NULL_OBJECT)
					obj = null;
				refs.add(new Reference(r, refType, obj));
			}
			//			else if(refType.isArray() && type.isAssignableFrom(refType.getComponentType())) {
			//				Object arrayObj = referenceTable.get(r);
			//				//TODO: bug : Argument is not an array
			//				if(!(arrayObj instanceof NullObject)) {
			//					for(int i = 0; i < Array.getLength(arrayObj); i++) {
			//						Object obj = Array.get(arrayObj, i);
			//						String refName = r + "[" + i + "]";
			//						refs.add(new Reference(refName, refType.getComponentType(), obj));
			//					}
			//				}
			//			}


		}

		for(String r : enumReferenceTable.keySet()) {
			Object obj = enumReferenceTable.get(r);

			if(ClassModel.getInstance().isPluginTypeActive(obj.getClass()) &&
					type.isAssignableFrom(obj.getClass())) {
				Class<?> refType = referenceTypeTable.get(r);
				refs.add(new Reference(r, refType, obj));
			}
		}


		for(Class<?> c : ClassModel.getInstance().getActivePluginTypes()) {	
			//			if(!c.isEnum()) {
			for(Field f : c.getFields()) {
				if(Modifier.isStatic(f.getModifiers()) && 
						type.isAssignableFrom(f.getType())) {
					Object obj = null;
					try {
						obj = f.get(null);
					} 
					catch (Exception e) {
						e.printStackTrace();
					}
					refs.add(new Reference(c.getSimpleName() + "." + f.getName(), f.getType(), obj));
				}
			}
			//			}
		}

		return refs;
	}

	public List<Reference> getReferences(Object object) {
		List<Reference> refs = new ArrayList<Reference>();

		for(String r : referenceTable.keySet()) {
			Object obj = referenceTable.get(r);

			if(obj == object) {
				Class<?> refType = referenceTypeTable.get(r);
				refs.add(new Reference(r, refType, object));
			}
		}



		return refs;
	}

	public boolean isReferenceInUse(String reference) {
		assert reference != null;
		return referenceTable.containsKey(reference);
	}

	public boolean isNullReference(String reference) {		
		return 
				referenceTable.containsKey(reference) && 
				referenceTable.get(reference) == NULL_OBJECT;
	}


	public static Class<?> getReferenceType(String reference) {
		return aspectOf().referenceTypeTable.get(reference);
	}


	public static Reference getFirstReference(Object object) {
		return aspectOf().getFirstReferenceAux(object);
	}

	private Reference getFirstReferenceAux(Object object) {
		for(String ref : referenceTable.keySet()) {
			if(referenceTable.get(ref) == object)
				return new Reference(ref, referenceTypeTable.get(ref), object);
		}		
		return null;
	}


	public List<Reference> getNullReferences() {
		List<Reference> nullRefs = new ArrayList<Reference>();
		for(String ref : referenceTable.keySet()) {
			Object obj = referenceTable.get(ref);
			if(obj == NULL_OBJECT) {
				Class<?> refType = referenceTypeTable.get(ref);
				nullRefs.add(new Reference(ref, refType , null));
			}
		}
		return nullRefs;
	}	

	public Object[] getDeadObjects() {
		IdentityObjectSet dead = new IdentityObjectSet();

		for(Object object : objectSet.objects())
			if(isDeadObject(object))
				dead.add(object);

		return dead.objects();
	}

	private boolean isDeadObject(Object object) {
		return objectSet.contains(object) && !referenceTableContainsObject(object);
	}

	private boolean referenceTableContainsObject(Object object) {
		for(Object o : referenceTable.values())
			if(o == object)
				return true;
		return false;
	}


	public String nextReference(Class<?> clazz) {			
		String refLetter = StandardNamePolicy.baseReferenceName(clazz);
		int i = 1;
		String ref = refLetter + i;
		while(referenceTable.containsKey(ref)) {
			i++;
			ref = refLetter + i;
		}
		return ref;
	}
}
