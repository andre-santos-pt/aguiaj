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
package pt.org.aguiaj.common.widgets;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;

import pt.org.aguiaj.classes.ClassModel;
import pt.org.aguiaj.objects.ObjectWidget;

public class TypeMemberMouseTrackAdapter extends MouseTrackAdapter {
		private ObjectWidget widget;
		private List<Method> methods;

		public TypeMemberMouseTrackAdapter(ObjectWidget widget, Class<?> type) {
			this.widget = widget;
			methods = ClassModel.getInstance().getAllAvailableMethods(type);
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
