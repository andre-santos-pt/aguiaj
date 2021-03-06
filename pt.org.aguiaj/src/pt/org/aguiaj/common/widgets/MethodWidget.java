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
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import pt.org.aguiaj.common.Fonts;
import pt.org.aguiaj.common.SWTUtils;
import pt.org.aguiaj.core.AguiaJParam;
import pt.org.aguiaj.core.Highlightable;
import pt.org.aguiaj.core.Highlighter;
import pt.org.aguiaj.core.Inspector;
import pt.org.aguiaj.core.ReflectionUtils;
import pt.org.aguiaj.core.TypeWidget;
import pt.org.aguiaj.core.commands.java.MethodInvocationCommand;
import pt.org.aguiaj.core.documentation.DocumentationLinking;
import pt.org.aguiaj.core.typewidgets.AbstractTypeWidget;
import pt.org.aguiaj.core.typewidgets.WidgetFactory;
import pt.org.aguiaj.core.typewidgets.WidgetProperty;
import pt.org.aguiaj.extensibility.Reference;
import pt.org.aguiaj.objects.ObjectModel;
import pt.org.aguiaj.objects.ObjectModel.Contract;
import pt.org.aguiaj.standard.StandardNamePolicy;


public class MethodWidget implements Highlightable { 

	private Object object;
	private Method method;

	private boolean inherited;
	private boolean overriding;

	private Button invokeButton;
	private Composite argsComposite;
	private Highlighter highlighter;

	private List<TypeWidget> paramWidgets;

	public MethodWidget(Composite parent, Object object, Method method, FieldContainer fieldContainer) {
		assert parent != null;
		assert method != null;
		assert fieldContainer != null;

		if(ObjectModel.getInstance().hasContract(object, method)) {
			Contract contract = ObjectModel.getInstance().getContract(object, method);
			this.object = contract.decorator;
			this.method = contract.wrappedMethod;
		}
		else {
			this.object = object;
			this.method = method;
		}

		if(object != null) {
			Class<?> clazz = object.getClass();
			inherited = Inspector.isInherited(clazz, method);
			overriding = Inspector.isOverriding(clazz, method);
		}
		createInvokeButton(parent, object, method);

		argsComposite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(method.getParameterTypes().length, false);
		layout.horizontalSpacing = 0;
		argsComposite.setLayout(layout);

		paramWidgets = createParameterWidgets(method, argsComposite);
	}

	private void createInvokeButton(Composite parent, Object object, Method method) {
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));		
		comp.setLayout(new RowLayout());
		invokeButton = new Button(comp, SWT.PUSH);
		invokeButton.setText(StandardNamePolicy.prettyCommandName(method));

		highlighter = new Highlighter(comp);

		String toolTip = StandardNamePolicy.getMethodToolTip(object, method, inherited, overriding);

		invokeButton.setToolTipText(toolTip);

		invokeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				executeInvocation();
			}
		});
		
		int style = inherited ? SWT.ITALIC : overriding ? SWT.BOLD  : SWT.NONE;
		
		Fonts.set(invokeButton, AguiaJParam.MEDIUM_FONT, style);
		
		DocumentationLinking.add(invokeButton, method);
	}



	private List<TypeWidget> createParameterWidgets(Method method, Composite argsComposite) {
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
		int nParams = paramWidgets.size();
		Object[] args = new Object[nParams];
		String[] argsText = new String[nParams];

		for(int i = 0; i < args.length; i++) {
			args[i] = paramWidgets.get(i).getObject();
			argsText[i] = paramWidgets.get(i).getTextualRepresentation();
		}

		Reference ref = ObjectModel.getInstance().getCompatibleReference(object, method);
		if(object != null && ref == null) {
			SWTUtils.showMessage("Error", "No compatible reference available", SWT.ICON_ERROR);
			return;
		}
		
		String objectReference = object != null && ref != null ? ref.name : null;
		MethodInvocationCommand cmd = new MethodInvocationCommand(object, objectReference, method, args, argsText);
		ObjectModel.getInstance().execute(cmd);
	}

	@Override
	public void highlight() {
		highlighter.highlight();
	}

	@Override
	public void unhighlight() {
		highlighter.unhighlight();
	}

	public void enable() {
		argsComposite.setEnabled(true);
		invokeButton.setEnabled(true);
		highlighter.enable();
	}
	
	public void disable() {
		argsComposite.setEnabled(false);
		invokeButton.setEnabled(false);
		highlighter.disable();
	}

	public void setArgs(Object[] args) {
		if(args.length != paramWidgets.size())
			return;
		
		for(int i = 0; i < paramWidgets.size(); i++)
			paramWidgets.get(i).update(args[i]);
	}
}
