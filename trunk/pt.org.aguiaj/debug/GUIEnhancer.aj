package pt.aguiaj.debug;


import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.WeakHashMap;

import org.aspectj.lang.reflect.SourceLocation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ArmEvent;
import org.eclipse.swt.events.ArmListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Widget;

import pt.guide.core.Defaults;
import pt.guide.core.Location;
import pt.guide.core.Trace;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;



public privileged abstract aspect GUIEnhancer {

	private Map<Widget, Trace> traceTable;
	private Map<Widget, Widget> underTrace;
	private BiMap<Widget, Integer> idTable;

	private Map<Integer, Widget> callees;

	private final int portOut;
	private final int portIn;

	private boolean firstTime;

	private int nextId = 1;

	private Widget armed;

	private boolean altOn;
	private boolean shiftOn;
	
	private ServerSocket serverSocket = null;

	protected pointcut scope() : if(true);

	public GUIEnhancer() {
		this(Defaults.ECLIPSE_PORT, Defaults.APPLICATION_PORT);
	}

	public GUIEnhancer(int portIn, int portOut) {		
		this.portIn = portIn;
		this.portOut = portOut;
		
		traceTable = new WeakHashMap<Widget, Trace>();
		underTrace = new WeakHashMap<Widget, Widget>();
		idTable = HashBiMap.create();

		callees = new WeakHashMap<Integer, Widget>();
		firstTime = true;

		try {
			serverSocket = new ServerSocket(portIn);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		new RequestHandler().start();
	}

	public class RequestHandler extends Thread {
		@Override
		public void run() {
			Socket sock;
			while(true) {
				Widget item = null;
				try {
					sock = serverSocket.accept();

					InputStream is = sock.getInputStream();  
					ObjectInputStream ois = new ObjectInputStream(is);  

					Location loc = (Location) ois.readObject();  
					System.out.println("REC: " + loc);
					item = callees.get(loc.id());
					ois.close();
					is.close();
					sock.close();					
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				if(item != null)
					triggerNavigation(item);
				else
					System.err.println("not found");
			}
		}
	}

	private Image shot;

	after() returning(final MenuItem item) :
		call(MenuItem+.new(..)) && scope() {

		item.addArmListener(new ArmListener() {			
			@Override
			public void widgetArmed(ArmEvent e) {
				if(altOn)
					shot = shotSurrounding();			
			}
		});

	}

	after() returning(final Item item) :
		call(Item+.new(..)) && scope() {

		item.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {				
				if(altOn) {
//					System.out.println("selected - " + item);
					triggerNavigation(item);
				}
			}		
		});				
	}		
	
//	private final Cursor cursor = new Cursor(Display.getDefault(), SWT.CURSOR_HAND);

	after(Widget control) : 
		call(* *(..)) && target(control) && scope() {

		if(traceTable.containsKey(control)) {
			SourceLocation loc = thisJoinPointStaticPart.getSourceLocation();

//			int size = traceTable.get(control).size();
			int id = idTable.get(control);
			Location traceLoc = traceTable.get(control).addLocation(loc, id);

//			if(traceTable.get(control).size() > size && underTrace.containsKey(control))
//				triggerNavigation(control);

//			Object thisObj = thisJoinPoint.getThis();
//			if(thisObj instanceof Widget) {
//				callees.put(traceLoc.id, (Widget) thisObj);
//				System.out.println(traceLoc.id + " -> " + thisObj);
//			}
		}
	}

	after() returning(final Widget control) :
		call(Widget+.new(..)) && scope() {

		SourceLocation loc = thisJoinPointStaticPart.getSourceLocation();
		int id = nextId;

		idTable.put(control, id);

		traceTable.put(control, new Trace(control.getClass().getSimpleName(), false, loc, id));

		nextId++;
		if(firstTime)
			addAltListener(control);
		
		if(control instanceof Shell) {			
			((Shell) control).addListener(SWT.MouseDown, new Listener() {
				
				@Override
				public void handleEvent(Event event) {
					if(altOn)
						triggerNavigation(control);
				}
			});
			
				
		}
		else if(control instanceof Control) {
			control.addListener(SWT.MouseDown, new Listener() {

				@Override
				public void handleEvent(Event event) {
					if(altOn)
						triggerNavigation(control);
				}
			});
		}
	}

	private void addAltListener(final Widget control) {
		final Display display = control.getDisplay();
		display.addFilter(SWT.KeyDown, new Listener() {

			@Override
			public void handleEvent(Event event) {
				if(event.keyCode == SWT.ALT) {
					altOn = true;
					if((event.stateMask & SWT.SHIFT) != 0)
						shiftOn = true;
				}
			}				
		});

		display.addFilter(SWT.KeyUp, new Listener() {

			@Override
			public void handleEvent(Event event) {
				if(event.keyCode == SWT.ALT) {
					altOn = false;
					shiftOn = false;
				}
			}
		});

		firstTime = false;
	}

	private void triggerNavigation(Widget widget) {
		if(widget instanceof TabFolder) {
			TabFolder folder = (TabFolder) widget;
			TabItem[] items = folder.getSelection();
			if(items.length > 0)
				widget = items[0];
		}
		
		if(widget instanceof Control && shiftOn) {
			widget = ((Control) widget).getParent(); 
		}
		
		Trace trace = traceTable.get(widget);
		if(trace == null) {
			System.err.println("Control not found");
			return;
		}
		
		Socket clientSocket = null;
		try {
			clientSocket = new Socket("localhost", portOut);	
			OutputStream os = clientSocket.getOutputStream();  
			ObjectOutputStream oos = new ObjectOutputStream(os);  
			oos.writeObject(trace);
			oos.close();
			os.close();
			clientSocket.close();

			Image image = makeScreenshot(widget);			

			ImageData data = image.getImageData();
			ImageLoader loader = new ImageLoader();
			loader.data = new ImageData[] {data};	

			clientSocket = new Socket("localhost", portOut);
			os = clientSocket.getOutputStream();  
			loader.save(os, SWT.IMAGE_PNG);
			os.close();
			clientSocket.close();
			image.dispose();

			if(!underTrace.containsKey(widget))
				underTrace.put(widget, widget);
		} 
		catch (IOException ex) {
			System.out.println("Accept failed: " + portOut);
		} 

	}

	private static String sourceLocationToString(SourceLocation loc) {
		return loc.getWithinType().getName() + ":" + loc.getLine();
	}


	private static boolean equal(SourceLocation a, SourceLocation b) {
		return 
		a.getWithinType().equals(a.getWithinType()) && 
		a.getLine() == b.getLine();
	}





	private Image makeScreenshot(Widget widget) {
		Image image = null;

		if(widget instanceof Control) {
			Control control = (Control) widget;

			if(!control.isDisposed()) {
				GC gc = new GC((Control) control);
				int width = ((Control) control).getBounds().width;
				int height = ((Control) control).getBounds().height;
				image = new Image(control.getDisplay(), width, height);
				gc.copyArea(image, 0, 0);
				gc.dispose();
			}
		}
		else if(widget instanceof MenuItem) {
			return shot;
		}
		else if(widget instanceof Item) {
			image = shotSurrounding();
		}
		
		if(image == null) {
			image = new Image(Display.getDefault(), SHOT_WIDTH, SHOT_HEIGHT);
		}

		return image;
	}

	
	private static final int SHOT_WIDTH = 225;
	private static final int SHOT_HEIGHT = 75;

	private Image shotSurrounding() {
		GC gc = new GC(Display.getDefault());
		Image image = new Image(Display.getDefault(), SHOT_WIDTH, SHOT_HEIGHT);
		Point loc = Display.getDefault().getCursorLocation();
		int quarter = SHOT_WIDTH/4;
		gc.copyArea(image, loc.x - SHOT_WIDTH/4, loc.y - SHOT_HEIGHT/2);
		return image;
	}
}
