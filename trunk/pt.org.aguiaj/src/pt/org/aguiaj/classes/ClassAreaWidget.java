/*******************************************************************************
 * Copyright (c) 2012 Andr� L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andr� L. Santos - initial API and implementation
 ******************************************************************************/
package pt.org.aguiaj.classes;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Collection;
import java.util.Set;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import pt.org.aguiaj.common.AguiaJColor;
import pt.org.aguiaj.common.AguiaJImage;
import pt.org.aguiaj.common.DragNDrop;
import pt.org.aguiaj.common.SWTUtils;
import pt.org.aguiaj.common.widgets.IconWidget;
import pt.org.aguiaj.common.widgets.LabelWidget;
import pt.org.aguiaj.core.AguiaJActivator;
import pt.org.aguiaj.core.AguiaJParam;
import pt.org.aguiaj.core.DocumentationView;
import pt.org.aguiaj.core.ReflectionUtils;
import pt.org.aguiaj.core.UIText;

import com.google.common.collect.Sets;

public class ClassAreaWidget extends ScrolledComposite {
	private static final int SPACING = 30;

	private Composite area;
	private Set<ClassWidget> classWidgets;
	private String pluginId;
	private String packageName;
	
	public ClassAreaWidget(Composite parent, final String packageName, Collection<Class<?>> classes) {
		super(parent, SWT.NONE | SWT.V_SCROLL | SWT.H_SCROLL);
		this.packageName = packageName;
		
		classWidgets = Sets.newHashSet();

		area = new Composite(this, SWT.NONE);
		area.setBackground(AguiaJColor.OBJECT_AREA.getColor());
		
		RowLayout compositeLayout = new RowLayout(SWT.VERTICAL);
		compositeLayout.spacing = SPACING;
		compositeLayout.marginLeft = 10;
		area.setLayout(compositeLayout);

		for(final Class<?> clazz : classes) {
			if(ClassModel.getInstance().isPluginClass(clazz)) {
				pluginId = ClassModel.getInstance().getPluginId(clazz);
			}
		}

		if(isPluginPackage())
			addPluginHeader();

		for(final Class<?> clazz : classes)
			if(clazz.isInterface() || 
			Modifier.isAbstract(clazz.getModifiers()) && !clazz.isEnum())
				new AbstractClassWidget(area, clazz);

		for(final Class<?> clazz : classes) {
			if(!clazz.isInterface() && 
				(!Modifier.isAbstract(clazz.getModifiers()) || clazz.isEnum())) {
				if(ReflectionUtils.tryClass(clazz)) {
					classWidgets.add(new ClassWidget(area, clazz));
				}
				else {
					new ErrorWidget(area, clazz);
				}
			}		
		}

		area.setToolTipText("Class Area (create objects and invoke static operations by pressing the buttons)");
		setContent(area);
		setExpandHorizontal(true);
		setExpandVertical(true);

		
		final ControlListener listener = new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				Rectangle r = getClientArea();
				setMinSize(area.computeSize(r.width, SWT.DEFAULT));
			}
		};
		
		addControlListener(listener);

//		Menu menu = new Menu(area);
//		MenuItem item = new MenuItem(menu, SWT.PUSH);
//		item.setText(UIText.REMOVE.get());
//		item.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				removeControlListener(listener);
//				ClassesView.getInstance().removeTab(packageName);
//			}
//		});
//		area.setMenu(menu);
		
		DragNDrop.addFileDragNDropSupport(area);
	}
	
	public void updateClassWidgets() {
		for(ClassWidget widget : classWidgets)
			widget.updateFields();
	}

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
		
		final String jarLocation = AguiaJActivator.getDefault().getPluginJarLocation(pluginId);

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
		SWTUtils.setColorRecursively(comp, AguiaJColor.WHITE.getColor());
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


	private static class ErrorWidget extends Composite {
		public ErrorWidget(Composite parent, Class<?> clazz) {
			super(parent, SWT.NONE);
			setLayout(new RowLayout(SWT.HORIZONTAL));		
			new IconWidget(parent, AguiaJImage.ERROR);									
			new LabelWidget.Builder()
			.text(clazz.getSimpleName())
			.big()
			.toolTip("Class could not be loaded, either due to compilation errors or missing dependencies.")
			.color(AguiaJColor.ALERT)
			.create(this);
		}
	}
}