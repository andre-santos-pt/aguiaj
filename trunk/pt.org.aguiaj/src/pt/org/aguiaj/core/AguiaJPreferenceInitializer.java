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
package pt.org.aguiaj.core;

import static pt.org.aguiaj.core.AguiaJParam.ACCESSOR_POLICY;
import static pt.org.aguiaj.core.AguiaJParam.FONT;
import static pt.org.aguiaj.core.AguiaJParam.JAVABAR_FONT;
import static pt.org.aguiaj.core.AguiaJParam.MEDIUM_FONT;
import static pt.org.aguiaj.core.AguiaJParam.METHOD_TIMEOUT;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import pt.org.aguiaj.core.AguiaJParam.PreferencesParam;

public class AguiaJPreferenceInitializer extends AbstractPreferenceInitializer {

	public AguiaJPreferenceInitializer() {
	}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = AguiaJActivator.getDefault().getPreferenceStore();
		
		store.setDefault(ACCESSOR_POLICY.name(), ACCESSOR_POLICY.getString());
		
		store.setDefault(PreferencesParam.FONTSIZE.name(), MEDIUM_FONT.getInt());
		store.setDefault(PreferencesParam.FONTFACE.name(), FONT.getString());
		store.setDefault(METHOD_TIMEOUT.name(), METHOD_TIMEOUT.getInt());
		store.setDefault(JAVABAR_FONT.name(), JAVABAR_FONT.getInt());
		
//		store.setDefault(PROTECTED_VISIBLE.name(), PROTECTED_VISIBLE.getBoolean());
//		store.setDefault(PACKAGEDEF_VISIBLE.name(), PACKAGEDEF_VISIBLE.getBoolean());
	}

}
