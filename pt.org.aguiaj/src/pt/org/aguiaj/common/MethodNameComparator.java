package pt.org.aguiaj.common;

import java.lang.reflect.Method;
import java.util.Comparator;

import pt.org.aguiaj.standard.StandardNamePolicy;

public class MethodNameComparator implements Comparator<Method> {
	@Override
	public int compare(Method a, Method b) {
		String prettyA = StandardNamePolicy.prettyPropertyName(a);
		String prettyB = StandardNamePolicy.prettyPropertyName(b);
		return prettyA.compareTo(prettyB);
	}
}