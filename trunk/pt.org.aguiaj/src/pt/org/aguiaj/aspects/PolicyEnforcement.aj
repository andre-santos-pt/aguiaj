package pt.org.aguiaj.aspects;

public aspect PolicyEnforcement {

	declare warning
	: get(java.io.PrintStream System.out) &&
	!within(pt.aguiaj.debug..*)
	: "illegal access to System.out";

	declare warning
	: get(java.io.PrintStream System.err) &&
	!within(pt.aguiaj.debug..*)
	: "review access to System.err";
}
