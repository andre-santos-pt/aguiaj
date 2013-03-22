package pt.org.aguiaj.common.widgets;

import java.lang.reflect.Method;
import java.util.Set;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;

import pt.org.aguiaj.classes.ClassModel;
import pt.org.aguiaj.objects.ObjectWidget;

public class TypeMemberMouseTrackAdapter extends MouseTrackAdapter {
		private ObjectWidget widget;
		private Set<Method> methods;

		public TypeMemberMouseTrackAdapter(ObjectWidget widget, Class<?> type) {
			this.widget = widget;
			methods = ClassModel.getInspector().methodsOfSupertype(widget.getObject().getClass(), type);
		}

		@Override
		public void mouseEnter(MouseEvent e) {
			for(Method m : methods)
				widget.highlight(m);
		}

		@Override
		public void mouseExit(MouseEvent e) {
			for(Method m : methods)
				widget.unhighlight(m);
		}
	}