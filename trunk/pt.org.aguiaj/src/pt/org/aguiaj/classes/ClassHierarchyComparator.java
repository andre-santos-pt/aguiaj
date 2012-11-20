package pt.org.aguiaj.classes;

import java.util.Comparator;

public class ClassHierarchyComparator implements Comparator<Class<?>> {
	public int compare(Class<?> a, Class<?> b) {
		if(a.equals(b))
			return 0;
		else if(a.isAssignableFrom(b))
			return -1;
		else
			return 1;
	}
}