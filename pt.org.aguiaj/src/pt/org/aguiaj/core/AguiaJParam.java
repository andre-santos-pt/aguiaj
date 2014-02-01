/*******************************************************************************
 * Copyright (c) 2014 Andre L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andre L Santos - developer
 ******************************************************************************/
package pt.org.aguiaj.core;

import org.eclipse.jface.preference.IPreferenceStore;

import pt.org.aguiaj.standard.GetIsAccessorPolicy;
import pt.org.aguiaj.standard.StandardInspectionPolicy;

public enum AguiaJParam {	
	TINY_FONT(6) {
		public int getInt() {
			return TINY_FONT.getPreferenceInt(PreferencesParam.FONTSIZE.name()) - 4;
		}
	},
	SMALL_FONT(8) {
		public int getInt() {	
			return SMALL_FONT.getPreferenceInt(PreferencesParam.FONTSIZE.name()) - 2;
		}
	},
	MEDIUM_FONT(10) {
		public int getInt() {
			int m = MEDIUM_FONT.getPreferenceInt(PreferencesParam.FONTSIZE.name());
			return m;
		}
	},
	BIG_FONT(12) {
		public int getInt() {
			return BIG_FONT.getPreferenceInt(PreferencesParam.FONTSIZE.name()) + 2;
		}
	},
	HUGE_FONT(16) {
		public int getInt() {
			return HUGE_FONT.getPreferenceInt(PreferencesParam.FONTSIZE.name()) + 4;
		}
	},
	JAVABAR_FONT(20) {
		public int getInt() {
			return JAVABAR_FONT.getPreferenceInt(name());
		}
	},
	METHOD_TIMEOUT(2) {
		public int getInt() {
			return METHOD_TIMEOUT.getPreferenceInt(name());
		}
	},
	HIGHLIGHT_TIMEOUT(3) {
		public int getInt() {
			return Integer.parseInt(HIGHLIGHT_TIMEOUT.value.toString());
		}
	},
	FONT("Arial") {
		public String getString() {
			return AguiaJActivator.getInstance().getPreferenceStore().getString(PreferencesParam.FONTFACE.name());
		}
	},
	INSPECTION_POLICY(StandardInspectionPolicy.class.getName()) {
		public String getString() {
			IPreferenceStore prefStore = AguiaJActivator.getInstance().getPreferenceStore();
			String className = prefStore.getString(name());
			if(className == null || className.isEmpty())
				className = INSPECTION_POLICY.value.toString();
			return className;
		}
	},
	ACCESSOR_POLICY(GetIsAccessorPolicy.class.getName())  {
		public String getString() {
			IPreferenceStore prefStore = AguiaJActivator.getInstance().getPreferenceStore();
			String className = prefStore.getString(name());
			if(className == null || className.isEmpty())
				className = ACCESSOR_POLICY.value.toString();
			return className;
		};		
	},	
	DOC_ROOT("doc"),
	DOC_ROOT_INDEX("doc/index.html"),
	DOC_PACKAGESUMMARY("package-summary.html"),
	FEEDBACK_FORM_URL("https://spreadsheets.google.com/viewform?formkey=dDFIVW42MDZvUkdLc1BHS0dvVGlyeFE6MQ");

	
	
	private int getPreferenceInt(String key) {
		AguiaJActivator activator = AguiaJActivator.getInstance();
		if(activator == null)
			return (Integer) value;
		else {
			int i = activator.getPreferenceStore().getInt(key);
			return i > 0 ? i : Integer.parseInt(this.value.toString());
		}
	}
	
	public enum PreferencesParam {
		FONTSIZE("Font size"), 
		FONTFACE("Font face");
		
		public final String description;
		
		private PreferencesParam(String description) {
			this.description = description;
		}			
	}
	
	private Class<?> type;
	private Object value;

	private AguiaJParam(Object defaultValue) {
		this.value = defaultValue;
		this.type = defaultValue.getClass();
	}

	public String getKey() {
		return this.name();
	}
	
	public Class<?> getType() {
		return type;
	}

	public String toString() {
		assert type.equals(String.class);
			
		return name() + "=" + value;
	}
	
	public String getString() {
		assert type.equals(String.class);
		if(value == null)
			return null;
		else
			return value.toString();
	}

	public int getInt() {
		throw new AssertionError("Wrong usage");
	}

	public boolean getBoolean() {
		throw new AssertionError("Wrong usage");
	}
}
