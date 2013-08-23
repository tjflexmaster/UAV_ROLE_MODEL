package listeners;

import gov.nasa.jpf.*;
import gov.nasa.jpf.vm.*;

public class MetricListener extends ListenerAdapter {
	//Constructor
	@Override
	public void executeInstruction ( VM vm, ThreadInfo ti, Instruction insnToExecute ) {
		MethodInfo mi = insnToExecute.getMethodInfo();
		String mname = mi.getFullName();
	    if ( mname.contains("advanceTime") || mname.contains("fire") || mname.contains("getEnabledTransition") )
	    	System.out.println( mname + " was called." );
	}
}
