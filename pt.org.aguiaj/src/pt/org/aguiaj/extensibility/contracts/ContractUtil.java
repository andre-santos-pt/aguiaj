package pt.org.aguiaj.extensibility.contracts;

public class ContractUtil {

	public static Object unwrap(Object object) {
		
		Object o = object;
		while(o instanceof ContractDecorator)
			o = ((ContractDecorator<?>) o).getWrappedObject();
		
		return o;
	}
}
