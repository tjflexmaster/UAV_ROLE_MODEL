package listeners;

import gov.nasa.jpf.*;
import gov.nasa.jpf.vm.*;

public class MetricListener extends ListenerAdapter {
	//Constructor
	@Override
	public void executeInstruction ( VM vm, ThreadInfo ti, Instruction insnToExecute ) {
		
		MethodInfo mi = insnToExecute.getMethodInfo();
		String mname = mi.getFullName();
		LocalVarInfo[] li = new LocalVarInfo[100];
	    if ( mname.contains("advanceTime") ) {
	    	int index = mi.getArgumentLocalVars()[0].getSlotIndex();
	    	StackFrame s = ti.getStackFrameExecuting(insnToExecute, 0);
	    	
//			int et = (Integer) s.getSlotAttr(index);
	    	System.out.println(s.getSlot(index));
	    }
//	    	System.out.println( mname + " was called." );
	}
}
