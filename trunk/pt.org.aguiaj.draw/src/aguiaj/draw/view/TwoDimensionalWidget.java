package aguiaj.draw.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import aguiaj.draw.TwoDimensional;

import pt.org.aguiaj.extensibility.VisualizationWidget;

public class TwoDimensionalWidget implements VisualizationWidget<TwoDimensional> {

	private Label label;
	private int x = 0;
	@Override
	public void update(TwoDimensional dim) {
		label.setText(dim.getWidth() + x  + " x " + dim.getHeight());
	}

	@Override
	public void createSection(Composite parent) {
		parent.setLayout(new FillLayout());
		label = new Label(parent, SWT.NONE);
	}

	@Override
	public boolean needsRelayout() {
		return true;
	}

	@Override
	public Control getControl() {
		return label;
	}

}
