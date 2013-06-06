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

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import pt.org.aguiaj.common.AguiaJColor;
import pt.org.aguiaj.common.AguiaJImage;
import pt.org.aguiaj.common.DragNDrop;
import pt.org.aguiaj.common.SWTUtils;
import pt.org.aguiaj.common.widgets.IconWidget;
import pt.org.aguiaj.common.widgets.LabelWidget;
import pt.org.aguiaj.core.AguiaJActivator;
import pt.org.aguiaj.core.AguiaJParam;
import pt.org.aguiaj.core.ReflectionUtils;
import pt.org.aguiaj.core.documentation.DocumentationLinking;
import pt.org.aguiaj.core.documentation.DocumentationView;
import pt.org.aguiaj.standard.StandardNamePolicy;

import com.google.common.collect.Sets;

class PackageWidget extends Composite {
	private static final int SPACING = 20;
	private static final int MARGIN = 5;

	private ScrolledComposite scrl;
	private Composite area;
	private Set<ClassWidget> classWidgets;
	private String pluginId;
	private String packageName;

	public PackageWidget(Composite parent, final String packageName, Collection<Class<?>> classes) {
		super(parent, SWT.NONE);
		this.packageName = packageName;

		setLayout(new FillLayout());

		classWidgets = Sets.newHashSet();

		scrl = new ScrolledComposite(this, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);	

		area = new Composite(scrl, SWT.NONE);
		area.setBackground(AguiaJColor.OBJECT_AREA.getColor());

		GridLayout areaLayout = new GridLayout(1, true);
		areaLayout.verticalSpacing = SPACING;
		areaLayout.marginBottom = MARGIN;
		areaLayout.marginTop = MARGIN;
		areaLayout.marginLeft = MARGIN;
		areaLayout.marginRight = MARGIN;
		
//		areaLayout.spacing = SPACING;
//		areaLayout.marginTop = MARGIN;
//		areaLayout.marginLeft = MARGIN;
		area.setLayout(areaLayout);
		area.setToolTipText("Class Area (create objects and invoke static operations by pressing the buttons)");

		scrl.setContent(area);
		scrl.setExpandHorizontal(true);
		scrl.setExpandVertical(true);
		scrl.setAlwaysShowScrollBars(true);

		for(final Class<?> clazz : classes) {
			if(ClassModel.getInstance().isPluginClass(clazz)) {
				pluginId = ClassModel.getInstance().getPluginId(clazz);
			}
		}

		if(isPluginPackage())
			addPluginHeader();

		Iterator<Class<?>> it = classes.iterator();
		while(it.hasNext()) {
			Class<?> clazz = it.next();
			if(clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers()) && !clazz.isEnum()) {
				new AbstractClassWidget(area, clazz);
			}
			else if(ReflectionUtils.tryClass(clazz)) {
				ClassWidget w = new ClassWidget(area, clazz);
				w.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				classWidgets.add(w);
			}
			else {
				new ErrorWidget(area, clazz);
			}
			
			if(it.hasNext())
				new Label(area, SWT.SEPARATOR | SWT.HORIZONTAL).setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		}

		addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				scrl.setMinSize(area.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			}
		});

		SWTUtils.setColorRecursively(this, AguiaJColor.WHITE.getColor());
		DragNDrop.addFileDragNDropSupport(area);
		layout();
	}

	
	public void refreshSize() {
		if(!scrl.isDisposed() && !area.isDisposed()) {
			scrl.setMinSize(area.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			scrl.layout();
		}
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

	Set<ClassWidget> getClassWidgets() {
		return Collections.unmodifiableSet(classWidgets);
	}

	private static class ErrorWidget extends Composite {
		public ErrorWidget(Composite parent, Class<?> clazz) {
			super(parent, SWT.NONE);
			setLayout(new RowLayout(SWT.HORIZONTAL));		
			IconWidget.createForRowLayout(parent, AguiaJImage.ERROR.getImage());									
			new LabelWidget.Builder()
			.text(clazz.getSimpleName())
			.big()
			.toolTip("Class could not be loaded, either due to compilation errors or missing dependencies.")
			.color(AguiaJColor.ALERT)
			.create(parent);
		}
	}
	
	private static class AbstractClassWidget extends Composite  {

		public AbstractClassWidget(Composite parent, Class<?> clazz) {
			super(parent, SWT.NONE);
			setLayout(new RowLayout(SWT.HORIZONTAL));
			IconWidget.createForRowLayout(this, clazz).setToolTipText("Polymorphic type");
			LabelWidget classNameLabel =  new LabelWidget.Builder()
				.text(StandardNamePolicy.prettyClassName(clazz))
				.big()
				.create(this);
			
			DocumentationLinking.add(classNameLabel.getControl(), clazz);
		}
	}
}
