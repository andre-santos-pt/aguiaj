package pt.aguiaj.debug;

import pt.guide.core.Defaults;


public aspect GUITA extends GUIEnhancer {
	
	public GUITA() {
		super(7777, Defaults.ECLIPSE_PORT);
	}
}
