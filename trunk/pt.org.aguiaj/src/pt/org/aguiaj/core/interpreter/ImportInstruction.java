package pt.org.aguiaj.core.interpreter;

import java.util.Map;
import java.util.Set;

import pt.org.aguiaj.common.Reference;
import pt.org.aguiaj.core.commands.java.ImportCommand;
import pt.org.aguiaj.core.commands.java.JavaCommand;

public class ImportInstruction implements Instruction {

	private String packageName;
	
	@Override
	public JavaCommand getCommand() {
		return new ImportCommand(packageName);
	}

	@Override
	public boolean acceptText(String text,
			Map<String, Reference> referenceTable, Set<Class<?>> classSet)
			throws ParseException {
		
		String s = text.trim();
		if(!s.startsWith("import"))
			return false;
		
		packageName = s.substring("import".length()).trim();
		
		return true;
	}

}
