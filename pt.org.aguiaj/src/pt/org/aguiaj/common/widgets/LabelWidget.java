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
package pt.org.aguiaj.common.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;

import pt.org.aguiaj.common.AguiaJColor;
import pt.org.aguiaj.common.Fonts;
import pt.org.aguiaj.core.AguiaJParam;
import pt.org.aguiaj.core.Highlightable;
import pt.org.aguiaj.core.Highlighter;
import pt.org.aguiaj.objects.ObjectsView;


public class LabelWidget extends Composite implements Highlightable {
	
	private Control control;
	private Highlighter highlighter;
	
	private LabelWidget(Composite parent, AguiaJParam fontFace, AguiaJParam size, String text, int style, boolean link, 
			String tooltip, AguiaJColor color) {
		
		super(parent, SWT.NONE);
		setLayout(new FillLayout());
			
		if(link) {
			Link linkWidget = new Link(this, style);
			Fonts.set(linkWidget, fontFace, size, style);
			linkWidget.setText("<a>" + text + "</a>");
			control = linkWidget;
		}
		else {
			Label labelWidget = new Label(this, style);
			Fonts.set(labelWidget, fontFace, size, style);
			labelWidget.setText(text);
			control = labelWidget;
		}
		
		control.setToolTipText(tooltip);
		control.setForeground(color.getColor());
		highlighter = new Highlighter(control);
	}
	
	public void addHyperlinkAction(Listener listener) {
		if(!(control instanceof Link))
			throw new AssertionError("Control is not a link");
		
		((Link) control).addListener(SWT.Selection, listener);
	}
	
	
	public void addObjectHighlightCapability(final ObjectToHighlightProvider provider) {
		control.addMouseTrackListener(new MouseTrackListener() {
			
			@Override
			public void mouseHover(MouseEvent e) {
			}
			
			@Override
			public void mouseExit(MouseEvent e) {
				ObjectsView.getInstance().unhighlight();
			}
			
			@Override
			public void mouseEnter(MouseEvent e) {
				Object obj = provider.getObjectToHighlight();
				if(obj != null)
					ObjectsView.getInstance().highlight(obj);
			}
		});
	}

	public interface ObjectToHighlightProvider {
		Object getObjectToHighlight();
	}
	
	public Control getControl() {
		return control;		
	}
	
	
	
	
	public static class Builder {
		private String text = "";
		private boolean link = false;
		private AguiaJParam size = AguiaJParam.MEDIUM_FONT;
		private AguiaJParam fontFace = AguiaJParam.FONT;
		private int style = SWT.NONE;
		private String tooltip = "";
		private AguiaJColor color = AguiaJColor.BLACK;
		
		public Builder text(String text) {
			this.text = text;
			return this;
		}
		
		public Builder link() {
			link = true;
			return this;
		}
		
		public Builder linkIf(boolean condition) {
			return condition ? link() : this;
		}
		
		public Builder tiny() {
			size = AguiaJParam.TINY_FONT;
			return this;
		}
		
		public Builder small() {
			size = AguiaJParam.SMALL_FONT;
			return this;
		}
		
		public Builder medium() {
			size = AguiaJParam.MEDIUM_FONT;
			return this;
		}
		
		public Builder big() {
			size = AguiaJParam.BIG_FONT;
			return this;
		}
		
		public Builder huge() {
			size = AguiaJParam.HUGE_FONT;
			return this;
		}
		
//		public Builder font(String fontName) {
//			this.fontFace = fontName;
//			return this;
//		}
		
		public Builder italic() {
			style |= SWT.ITALIC;
			return this;
		}
		
		public Builder italicIf(boolean condition) {
			return condition ? italic() : this;
		}
		
		public Builder bold() {
			style |= SWT.BOLD;
			return this;
		}
		
		public Builder boldIf(boolean condition) {
			return condition ? bold() : this;
		}
		
		public Builder border() {
			style |= SWT.BORDER;
			return this;
		}

		public Builder toolTip(String tooltip) {
			this.tooltip = tooltip;
			return this;
		}

		public Builder color(AguiaJColor color) {
			this.color = color;
			return this;
		}
		
		public LabelWidget create(Composite parent) {
			LabelWidget label = new LabelWidget(parent, fontFace, size, text, style, link, tooltip, color);
			return label;
		}
	}

	@Override
	public void highlight() {
		highlighter.highlight();
	}

	@Override
	public void unhighlight() {
		highlighter.unhighlight();
	}
}
