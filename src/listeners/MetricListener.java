package listeners;

import java.util.TreeMap;

import simulator.Metric;
import simulator.MetricKey;
import gov.nasa.jpf.*;
import gov.nasa.jpf.vm.*;

public class MetricListener extends ListenerAdapter {
	/**
	 * stores the metrics
	 */
	//TODO create storage variable (can't use MetricKey or Metric Manager classes, this would create an infinite loop)
	
//	/**
//	 * constructs the class (commented out because jpf didn't like this of constructor)
//	 */
//	MetricListener ( ) {
//		super();
//		//TODO initialize storage variable
//	}
	
	/**
	 * acts whenever methods execute
	 */
	@Override
	public void executeInstruction ( VM vm, ThreadInfo ti, Instruction insnToExecute ) {
		//get the methods information
		MethodInfo mi = insnToExecute.getMethodInfo();
		String mname = mi.getFullName();
		
		//only act on these methods
		if ( mname.contains("setTime") || mname.contains("setActor") || mname.contains("setState") || mname.contains("setTransition") || mname.contains("addMetric") ) {
			
			//find the desired parameter
    		LocalVarInfo parameter = mi.getLocalVar(1, 1);
    		if(parameter != null){
    			//get the parameters information
    			String parName = parameter.getName();
    			String parType = parameter.getType();
    			StackFrame s = ti.getStackFrameExecuting(insnToExecute, 0);
    			
    			//reform the parameter's value
    			Object value = ( parType.contains("Integer") ) ? (Integer) s.getLocalOrFieldValue(parName) :
//    				( varType.contains("String") ) ? (java.lang.String) s.getLocalOrFieldValue(varName) : 
//        			( varType.contains("MetricEnum") ) ? (simulator.Metric.MetricEnum) s.getLocalOrFieldValue(varName) : 
    				s.getLocalOrFieldValue(parName);
    			
    			//TODO build metric map
    			switch(parName){
    			case "time" :
    				//TODO update storage variable's time
    				break;
    			case "actor_name" :
    				//TODO update storage variable's actor_name
    				break;
    			case "state_name" :
    				//TODO update storage variable's state_name
    				break;
    			case "transition" :
    				//TODO update storage variable's transition
    				break;
    			case "metric" :
    				//TODO add metric to storage variable
    				break;
    			}
    			
    			//debug
    			System.out.println(parName);
    		}
		}
	}
}
