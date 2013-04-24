package pt.org.aguiaj.console;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import aguiaj.console.Console;

import pt.org.aguiaj.extensibility.AguiaJHelper;
import pt.org.aguiaj.extensibility.ObjectEventListenerAdapter;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "pt.org.aguiaj.console"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	/**
	 * The constructor
	 */
	public Activator() {
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
//		AguiaJHelper.addObjectModelListener(new ObjectEventListenerAdapter() {
//			@Override
//			public void init() {
//				if(!AguiaJHelper.existsReference("out")) {
//					AguiaJHelper.executeJavaInstruction("Console out = Console.getInstance()");
//					
//					if(ShowConsoleAction.instance == null || !ShowConsoleAction.instance.isOn())
//						AguiaJHelper.hide("out");
//				}
//			}
//		});
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

}
