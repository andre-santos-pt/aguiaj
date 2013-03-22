package pt.org.aguiaj.classes;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;


public class FilterComparator implements Comparator<Method> {
		private final List<ClassMemberFilter> filters;

		FilterComparator(List<ClassMemberFilter> filters) {
			this.filters = filters;
		}

		@Override
		public int compare(Method m1, Method m2) {
			int m1Index = 0;
			int m2Index = 0;
			for(ClassMemberFilter f : filters) {
				if(f instanceof NameBasedFilter) {
					String[] methodNames = ((NameBasedFilter) f).getMethodNames();							
					for(int i = 0; i < methodNames.length; i++) {
						if(m1.getName().equals(methodNames[i]))
							m1Index = i;
						else if(m2.getName().equals(methodNames[i]))
							m2Index = i;
					}
				}
			}
			return new Integer(m1Index).compareTo(m2Index);
		}
	}