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
package pt.org.aguiaj.common.widgets;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import pt.org.aguiaj.aspects.ObjectModel;
import pt.org.aguiaj.core.AguiaJParam;
import pt.org.aguiaj.core.DocumentationView;
import pt.org.aguiaj.core.Inspector;
import pt.org.aguiaj.core.TypeWidget;
import pt.org.aguiaj.core.commands.java.MethodInvocationCommand;
import pt.org.aguiaj.core.typewidgets.AbstractTypeWidget;
import pt.org.aguiaj.core.typewidgets.WidgetFactory;
import pt.org.aguiaj.core.typewidgets.WidgetProperty;
import pt.org.aguiaj.standard.StandardNamePolicy;



public class MethodWidget { 

	private Map<Method, List<TypeWidget>> methodTextArgsTable = new HashMap<Method, List<TypeWidget>>();

	private Object object;
	private Method method;

	private boolean inherited;
	private boolean overriding;

	public MethodWidget(Composite parent, Class<?> clazz, Object object, final Method method, final FieldContainer fieldContainer) {
		assert parent != null;
		assert clazz != null;
		assert method != null;
		assert fieldContainer != null;

		this.object = object;
		this.method = method;

		inherited = Inspector.isInherited(clazz, method);
		overriding = Inspector.isOverriding(clazz, method);

		createInvokeButton(parent);

		Composite argsComposite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(method.getParameterTypes().length, false);
		layout.horizontalSpacing = 0;
		argsComposite.setLayout(layout);
		List<TypeWidget> paramTexts = createParameterWidgets(fieldContainer, method, argsComposite);
		methodTextArgsTable.put(method, paramTexts);
	}

	private void createInvokeButton(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));		
//		FillLayout layout = new FillLayout();
//		layout.marginWidth = 2;
//		layout.marginHeight = 2;
		comp.setLayout(new RowLayout());
		final Button invokeButton = new Button(comp, SWT.PUSH);
		invokeButton.setText(StandardNamePolicy.prettyCommandName(method));
		//		invokeButton.setBounds(5, 5, 80, 30);

		String toolTip = StandardNamePolicy.getMethodToolTip(object, method);

		invokeButton.setToolTipText(toolTip);

		invokeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				executeInvocation();
			}
		});
		FontData data = new FontData(AguiaJParam.FONT.getString(), 
				AguiaJParam.MEDIUM_FONT.getInt(), inherited ? SWT.ITALIC : overriding ? SWT.BOLD : SWT.NONE);
		Font font = new Font(Display.getDefault(), data);
		invokeButton.setFont(font);
//		invokeButton.setLayoutData(new RowData());
//		((RowData) invokeButton.getLayoutData()).height = data.getHeight() + 6;

		DocumentationView.getInstance().addDocumentationSupport(invokeButton, method);
	}



	private List<TypeWidget> createParameterWidgets(FieldContainer fieldContainer, final Method method, Composite argsComposite) {
		List<TypeWidget> paramTexts = new ArrayList<TypeWidget>();
		Class<?>[] paramTypes = method.getParameterTypes();

		for(int i = 0; i < paramTypes.length; i++) {
			Class<?> paramType = paramTypes[i];
			final TypeWidget widget;

			widget = WidgetFactory.INSTANCE.createWidget(
					argsComposite, 
					paramType, 
					EnumSet.of(WidgetProperty.PARAMETER, WidgetProperty.MODIFIABLE));

			if(widget instanceof AbstractTypeWidget) {
				AbstractTypeWidget aWidget = (AbstractTypeWidget) widget;
				aWidget.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
			}

			widget.update(widget.defaultValue());
			paramTexts.add(widget);

			// in last param, ENTER triggers operation invocation
			if(i == paramTypes.length - 1 && widget.getControl() != null) {
				widget.getControl().addKeyListener(new KeyAdapter() {

					public void keyPressed(KeyEvent event) {
						if(event.keyCode == SWT.CR) {
							executeInvocation();
						}
					}
				});
			}
		}

		return paramTexts;
	}

	private void executeInvocation() {
		List<TypeWidget> paramWidgets = methodTextArgsTable.get(method);
		int nParams = paramWidgets.size();
		Object[] args = new Object[nParams];
		String[] argsText = new String[nParams];

		for(int i = 0; i < args.length; i++) {
			args[i] = paramWidgets.get(i).getObject();
			argsText[i] = paramWidgets.get(i).getTextualRepresentation();
		}
		String objectReference = object != null ? ObjectModel.getFirstReference(object).name : null;
		MethodInvocationCommand cmd = new MethodInvocationCommand(object, objectReference, method, args, argsText);
		cmd.execute();		
	}
}
