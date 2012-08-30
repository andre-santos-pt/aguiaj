package pt.org.aguiaj.common;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class Predicate {
	
	private boolean value;
	private int modifiers;

	private Predicate(AccessibleObject obj) {
		modifiers = modifiers(obj);
	}	
	
	public static Predicate check(AccessibleObject obj) {
		return new Predicate(obj);
	}
	
	public boolean isTrue() {
		return value;
	}
	
	public boolean isFalse() {
		return !value;
	}
	
	private int modifiers(AccessibleObject obj) {
		if(obj instanceof Field)
			return ((Field) obj).getModifiers();
		else if(obj instanceof Method)
			return ((Method) obj).getModifiers();
		else if(obj instanceof Constructor<?>)
			return ((Constructor<?>) obj).getModifiers();
		else
			return 0;
	}
	
	public Predicate _static() {
		if(!Modifier.isStatic(modifiers))
			value = false;
		
		return this;
	}
	
	public Predicate _instance() {
		if(Modifier.isStatic(modifiers))
			value = false;
		
		return this;
	}
	
	
	
	public Predicate _private() {
		if(!Modifier.isPrivate(modifiers))
			value = false;
		
		return this;
	}
	
	public static void main(String[] args) {
		Method m = null;
		try {
			m = String.class.getMethod("toString");
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	
		if(check(m)._private()._static().isTrue()) {
			
		}
		
	}
		
	
}
