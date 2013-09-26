package pt.org.aguiaj.core;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Control;

import pt.org.aguiaj.common.AguiaJColor;

public class Highlighter implements Highlightable {

	private Control control;
	private Color prevColor;

	public Highlighter(Control control) {
		this.control = control;
	}

	@Override
	public void highlight() {
		if(!control.isDisposed()) {
			prevColor = control.getBackground();
			control.setBackground(AguiaJColor.HIGHLIGHT.getColor());
		}
	}

	@Override
	public void unhighlight() {
		if(!control.isDisposed()) {
			if(prevColor != null) {
				control.setBackground(prevColor);
				prevColor = null;
			}
		}
	}

}
