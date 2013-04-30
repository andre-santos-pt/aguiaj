package pt.org.aguiaj.core.typewidgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import pt.org.aguiaj.core.AguiaJParam;


public abstract class TextTypeWidget extends PrimitiveTypeWidget {
	private Text text;
	private VerifyListener listener;
	private boolean dirty;
	private String last;
	
	public abstract class VerifyListener implements Listener {
		private boolean ignore;
		
		public boolean ignore() {
			return ignore;
		}
		
		public void setIgnore() {
			ignore = true;
		}
		
		public void unsetIgnore() {
			ignore = false;
		}
		
		@Override
		public void handleEvent(Event event) {
			if(ignore())
				return;
			
			if(charOk(event.character)) {
				if(!dirty) {
					last = text.getText();
					dirty = true;
				}
			}
			else
				event.doit = false;
		}
		
		public abstract boolean charOk(int code);
	}
	
	public TextTypeWidget(Composite parent, WidgetProperty type, boolean modifiable) {
		super(parent, type, modifiable);
	}

	protected Text getText() {
		return text;
	}
	
	protected abstract VerifyListener createVerifyListener();
	protected abstract int getWidth();
	
	@Override
	protected void createContents(Composite parent) {
		text = createTextWidget(parent);
		if(getUsageType() == WidgetProperty.PARAMETER) {
			text.setLayoutData(new RowData());
			((RowData) text.getLayoutData()).width = getWidth();
		}
		setVerifyListener(createVerifyListener());
		update(defaultValue());
	}
	
	
	private void setVerifyListener(VerifyListener listener) {
		this.listener = listener;
		text.addListener(SWT.Verify, listener);
		text.addKeyListener(new KeyAdapter() {			
			public void keyPressed(KeyEvent event) {
				if(event.keyCode == SWT.CR) {
					dirty = false;
				}
			}
		});
	}
	
	private void addFocusListener(final Text text) {
		if(isModifiable())
			addEnterKeyListener(text);
		
		text.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				if(dirty) {
					listener.setIgnore();
					text.setText(last);
					listener.unsetIgnore();
				}
			}

			@Override
			public void focusGained(FocusEvent e) {
				text.selectAll();
			}
		});
	}
	
	private Text createTextWidget(Composite parent) {
		final Text text = new Text(parent, SWT.BORDER |
				(getUsageType() == WidgetProperty.PROPERTY || !isModifiable() ? SWT.READ_ONLY : SWT.BORDER));
		
		if(!isModifiable())
			text.setBackground(parent.getBackground());
		
		FontData data = new FontData("Courier", AguiaJParam.MEDIUM_FONT.getInt(), SWT.NONE);
		Font font = new Font(Display.getDefault(), data);
		text.setFont(font);
			
		if(getUsageType() != WidgetProperty.PARAMETER)
			addFocusListener(text);
		
		text.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				text.selectAll();	
			}
		});
		
		return text;
	}
	
	public void update(Object object) {
		if(!text.isDisposed() && object != null) {
			listener.setIgnore();
			text.setText(object.toString());
			listener.unsetIgnore();
			layout();
			getParent().layout();
			getParent().pack();
		}
	}
	
	public String toString() {
		return getObject().toString();
	}
	
	public Control getControl() {
		return text;
	}

}
