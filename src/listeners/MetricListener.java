package listeners;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;


public class MetricListener extends ListenerAdapter {
	//Constructor
	@Override
	  public void executeInstruction (VM vm, ThreadInfo ti, Instruction insnToExecute) {
	    MethodInfo mi = insnToExecute.getMethodInfo();
	    String mname = mi.getFullName();
	    if (mname.contains("addMetric")) {
	    	System.out.println("addMetric was called.");
	    	//MetricEnum metric = ;
	    	System.out.println(mi.getLongName());
	    }
	}
}
