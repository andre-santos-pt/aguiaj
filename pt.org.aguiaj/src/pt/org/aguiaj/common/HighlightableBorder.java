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
package pt.org.aguiaj.common;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import pt.org.aguiaj.core.AguiaJParam;

public class HighlightableBorder extends Composite implements Highlightable {
	private Color normalColor;

	public HighlightableBorder(Composite parent, Color normalColor) {
		super(parent, SWT.NONE);
		
		this.normalColor = normalColor;

		RowLayout layout = new RowLayout(SWT.HORIZONTAL);
		layout.marginBottom = 2;
		layout.marginTop = 2;
		layout.marginRight = 2;
		layout.marginLeft = 2;	
		setLayout(layout);
	}

	public void highlight() {
		if(!isDisposed()) {
			setBackground(AguiaJColor.VALUECHANGE.getColor());
			launchUnhilight();
		}
	}

	private void launchUnhilight() {
		Job job = new Job("unhighlight") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						if(!isDisposed())
							setBackground(normalColor);				
					}
				});
				return Status.OK_STATUS;
			}
		};
		job.schedule(AguiaJParam.HIGHLIGHT_TIMEOUT.getInt() * 1000);
	}

}
