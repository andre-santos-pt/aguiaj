package pt.org.aguiaj.core.documentation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Control;

import pt.org.aguiaj.classes.ClassModel;
import pt.org.aguiaj.core.AguiaJParam;

public class DocumentationLinking {

	public static void add(Control control, Class<?> clazz) {				
		addSupport(control, clazz, "");
	}

	public static void add(Control control, Field field) {				
		addSupport(control, field.getDeclaringClass(), anchor(field));
	}

	public static void add(Control control, Constructor<?> constructor) {
		addSupport(control, constructor.getDeclaringClass(), anchor(constructor));
	}

	public static void add(Control control, Method method) {
		final Class<?> clazz = method.getDeclaringClass();
		if(isPluginWithDocumentation(clazz))
			addSupport(control, clazz, anchor(method));
	}

	private static void addSupport(Control control, final Class<?> clazz, final String anchor) {
		addHover(control, clazz, anchor);
	}

	private static boolean isPluginWithDocumentation(Class<?> clazz) {
		if(!ClassModel.getInstance().isPluginClass(clazz))
			return false;

		String pluginId = ClassModel.getInstance().getPluginId(clazz);
		return Platform.getBundle(pluginId).getEntry(AguiaJParam.DOC_ROOT.getString()) != null;
	}




	private static void addHover(final Control control, final Class<?> clazz, final String anchor) {
		control.setData(new ControlAnchor(clazz, anchor));
	}

	private static String anchor(Method method) {
		return method.getName() + 
				"(" + concatParams(method.getParameterTypes()) + ")";
	}

	private static String anchor(Field field) {
		return field.getName();
	}

	private static String anchor(Constructor<?> constructor) {
		return constructor.getDeclaringClass().getSimpleName() + 
				"(" + concatParams(constructor.getParameterTypes()) + ")";
	}

	private static String concatParams(Class<?>[] params) {
		String list = "";
		for(int i = 0; i < params.length; i++) {
			if(i != 0)
				list += ", ";

			if(params[i].isPrimitive())
				list += params[i].getSimpleName();
			else if(params[i].isArray())
				list += componentType(params[i]) + arrayBrackets(params[i]);
			else
				list += params[i].getName();
		}
		return list;
	}

	private static String componentType(Class<?> type) {
		if(!type.isArray())
			return type.getName();
		else
			return componentType(type.getComponentType());
	}

	private static String arrayBrackets(Class<?> type) {
		if(type.isArray() && !type.getComponentType().isArray())
			return "[]";
		else
			return "[]" + arrayBrackets(type.getComponentType());
	}


}
