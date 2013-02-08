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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;

import pt.org.aguiaj.common.AguiaJColor;
import pt.org.aguiaj.core.AguiaJParam;
import pt.org.aguiaj.core.Highlightable;
import pt.org.aguiaj.core.Highlighter;
import pt.org.aguiaj.objects.ObjectsView;


public class LabelWidget extends Composite implements Highlightable {
	
	private Control control;
	private Highlighter highlighter;
	
	private LabelWidget(Composite parent, Font font, String text, boolean link, 
			boolean hasBorder, String tooltip, AguiaJColor color) {
		
		super(parent, SWT.NONE);
		setLayout(new FillLayout());
		
		int style = hasBorder ? SWT.BORDER : SWT.NONE;
		
		if(link) {
			Link linkWidget = new Link(this, style);
			linkWidget.setFont(font);
			linkWidget.setText("<a>" + text + "</a>");
			control = linkWidget;
		}
		else {
			Label labelWidget = new Label(this, style);
			labelWidget.setFont(font);
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
		private int size = AguiaJParam.MEDIUM_FONT.getInt();
		private String fontName = AguiaJParam.FONT.getString();
		private int style = SWT.NONE;
		private boolean border = false;
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
			size = AguiaJParam.TINY_FONT.getInt();
			return this;
		}
		
		public Builder small() {
			size = AguiaJParam.SMALL_FONT.getInt();
			return this;
		}
		
		public Builder medium() {
			size = AguiaJParam.MEDIUM_FONT.getInt();
			return this;
		}
		
		public Builder big() {
			size = AguiaJParam.BIG_FONT.getInt();
			return this;
		}
		
		public Builder huge() {
			size = AguiaJParam.HUGE_FONT.getInt();
			return this;
		}
		
		public Builder font(String fontName) {
			this.fontName = fontName;
			return this;
		}
		
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
			border = true;
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
			FontData data = new FontData(fontName, size, style);			
			Font font = new Font(Display.getDefault(), data);
			return new LabelWidget(parent, font, text, link, border, tooltip, color);
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
