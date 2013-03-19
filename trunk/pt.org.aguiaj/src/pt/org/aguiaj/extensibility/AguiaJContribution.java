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
package pt.org.aguiaj.extensibility;

/**
 * Constant strings related to the constributions of AGUIA/J within the Eclipse framework.
 */
public interface AguiaJContribution {

	/**
	 * Main plugin id.
	 */
	public static final String AGUIAJ_PLUGIN = "pt.org.aguiaj";
	
	/**
	 * Classes view id.
	 */
	public static final String CLASSES_VIEW = "pt.org.aguiaj.classesView";
	
	/**
	 * Objects view id.
	 */
	public static final String OBJECTS_VIEW = "pt.org.aguiaj.objectsView";
	
	/**
	 * JavaBar view id.
	 */
	public static final String JAVABAR_VIEW = "pt.org.aguiaj.javabarView";
	
	/**
	 * History view id.
	 */
	public static final String HISTORY_VIEW = "pt.org.aguiaj.historyView";
	
	/**
	 * Documentation view id.
	 */
	public static final String DOCUMENTATION_VIEW = "pt.org.aguiaj.documentationView";

	/**
	 * AGUIA/J perspective id.
	 */
	public static final String PERSPECTIVE = "pt.org.aguiaj.perspective";
	
	/**
	 * Id of the extension point for plugging object visualization widgets into AGUIA/J.
	 */
	public static final String EXTENSION_OBJECT_WIDGET = "pt.org.aguiaj.objectWidgets";	
	
	
	public static final String OBJECT_WIDGET_GROUP = "group";
	public static final String OBJECT_WIDGET_CLASS = "class";
	public static final String OBJECT_WIDGET_ID = "id";
	public static final String OBJECT_WIDGET_VIEW = "viewClass";
	public static final String OBJECT_CONTRACT_PROXY = "contractProxy";
	
	public static final String OBJECT_WIDGET_ALLOWIMPORT = "allowImport";
	public static final String OBJECT_WIDGET_ICON = "icon";
	public static final String OBJECT_WIDGET_METHOD = "method";
	public static final String OBJECT_WIDGET_METHOD_ID = "id";
	public static final String OBJECT_WIDGET_PROMOTE = "promoteToAccessor";
	public static final String OBJECT_WIDGET_EXCLUDE = "excludeFilter";
	public static final String OBJECT_WIDGET_INCLUDE = "includeFilter";
	
	
	/**
	 * Id of the extension point for plugging method accessor detection policies into AGUIA/J.
	 */
	public static final String EXTENSION_ACCESSOR_POLICY = "pt.org.aguiaj.accessorDetectionPolicies";
	
	public static final String ACCESSOR_POLICY_ID = "id";
	public static final String ACCESSOR_POLICY_DESCRIPTION = "description";
	
	public static final String EXTENSION_LANGUAGES = "pt.org.aguiaj.languages";
}
