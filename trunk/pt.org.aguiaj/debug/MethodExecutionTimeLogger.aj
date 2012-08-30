package pt.aguiaj.debug;

import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.Signature;

import pt.aguiaj.common.MonitorExecution;

public aspect MethodExecutionTimeLogger {

	private Map<Signature, Long> timestamps = new HashMap<Signature, Long>();
		
	before() :
		execution(@MonitorExecution * * (..)) {		
			Signature sig = thisJoinPoint.getSignature();
			timestamps.put(sig, System.currentTimeMillis());			
		}
	
	after() :
		execution(@MonitorExecution * * (..)) {		
			Signature sig = thisJoinPoint.getSignature();
			long dif = System.currentTimeMillis() - timestamps.get(sig);
			System.out.println(sig.toShortString() + " : " + dif);
		}

	before() :
		execution(@MonitorExecution *.new(..)) {		
			Signature sig = thisJoinPoint.getSignature();
			timestamps.put(sig, System.currentTimeMillis());			
		}
	
	after() :
		execution(@MonitorExecution *.new(..)) {		
			Signature sig = thisJoinPoint.getSignature();
			long dif = System.currentTimeMillis() - timestamps.get(sig);
			System.out.println(sig.toShortString() + " : " + dif);
		}
}
