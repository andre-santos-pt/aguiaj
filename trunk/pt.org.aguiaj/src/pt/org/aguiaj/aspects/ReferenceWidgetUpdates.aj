package pt.org.aguiaj.aspects;

import java.util.Iterator;
import java.util.List;

import pt.org.aguiaj.common.Reference;
import pt.org.aguiaj.core.typewidgets.SelectReferenceWidget;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;


public aspect ReferenceWidgetUpdates {
	private final Multimap<Class<?>, SelectReferenceWidget> selectWidgetsTable;
	
	public ReferenceWidgetUpdates() {
		selectWidgetsTable = LinkedListMultimap.create();
	}

	after(SelectReferenceWidget widget) : 
		execution(SelectReferenceWidget.new(..)) && this(widget){
		registerSelectWidget(widget);
	}

	after(Class<?> refType, Object object, String reference) : 
		execution(void ObjectModel.addReference(Class<?>, Object, String)) && 
		args(refType, object, reference) {
			updateWidgets(refType);
	}

	void around (Object object) : 
		execution(void ObjectModel.removeObject(Object)) && args(object) {
			List<Reference> refs = ObjectModel.aspectOf().getReferences(object);
			proceed(object);
			for(Reference r : refs)
				updateWidgets(r.type);
		}

	void around(String reference) :
		execution(void ObjectModel.removeReference(String)) && args(reference) {
			Class<?> refType = ObjectModel.getReferenceType(reference);
			proceed(reference);
			updateWidgets(refType);			
		}
	

	private void registerSelectWidget(SelectReferenceWidget widget) {
		Class<?> refType = widget.getReferenceType();
		
		selectWidgetsTable.put(refType, widget);		
		List<Reference> refs = ObjectModel.aspectOf().getCompatibleReferences(refType);
		widget.setObjects(refs);
	}

	private void updateWidgets(Class<?> refType) {	
		for(Class<?> clazz : selectWidgetsTable.keySet()) {
			if(clazz.isAssignableFrom(refType)) {			
				for(Iterator<SelectReferenceWidget> it = selectWidgetsTable.get(clazz).iterator(); 
				it.hasNext(); ) {
					SelectReferenceWidget widget = it.next();
					if(widget.isDisposed()) {
						it.remove();
					}
					else {
						List<Reference> refs = ObjectModel.aspectOf().getCompatibleReferences(clazz);
						widget.setObjects(refs);
					}
				}
			}
			else if(refType.isArray() && refType.getComponentType().isAssignableFrom(clazz)) {
				updateWidgets(refType.getComponentType());
			}
		}
	}
	
}
