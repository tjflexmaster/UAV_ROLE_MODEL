package listeners;

import gov.nasa.jpf.*;
import gov.nasa.jpf.vm.*;

public class MetricListener extends ListenerAdapter {
	//Constructor
	@Override
	public void executeInstruction ( VM vm, ThreadInfo ti, Instruction insnToExecute ) {
		MethodInfo mi = insnToExecute.getMethodInfo();
		String mname = mi.getFullName();
	    if ( mname.contains("advanceTime") ) {
	    	StackFrame s = ti.getStackFrameExecuting(insnToExecute, 0);
	    	
	    	String[] localVarNames = mi.getLocalVariableNames();
	    	for (int i=0; i<localVarNames.length; i++) {
	    		LocalVarInfo localVar = mi.getLocalVar(localVarNames[i], 1);
	    		if(localVar != null){
	    			System.out.println(localVar.toString());// + ":" + s.getSlot(localVar.getSlotIndex()) + ", ");
	    		}
//	    		System.out.print(localVarNames[i]);
	    	}
//	    	System.out.print("\n");
	    }
	}
}
