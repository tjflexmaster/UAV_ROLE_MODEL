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
	MetricKey _currentKey = new MetricKey(-1, "", "", -1);
	TreeMap<MetricKey, Metric> _metrics = new TreeMap<MetricKey, Metric>();
	
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
		MethodInfo mi = insnToExecute.getMethodInfo( );
		String mname = mi.getFullName( );
		
		//only act on these methods
		if ( mname.contains( "setTime" )
				|| mname.contains( "setActor" )
				|| mname.contains( "setState" )
				|| mname.contains( "setTransition" )
				|| mname.contains( "addMetric" ) ) {
			
    		LocalVarInfo parameter = mi.getLocalVar( 1, 1 );
    		if( parameter != null ){
    			//get the desired parameter's information
    			String parName = parameter.getName( );//name
    			Object value = ti.getStackFrameExecuting( insnToExecute, 0 ).getLocalOrFieldValue( parName );//value
    			
    			//TODO build metric map
    			switch( parName ){
    			case "time" :
    				_currentKey.setBDMTime( ( Integer ) value ); 
    				break;
    			case "actor_name" :
//    				_currentKey.setBDMActor( ( String ) value );
    				break;
    			case "state_name" :
//    				_currentKey.setBDMState( ( String ) value );
    				break;
    			case "transition" :
    				_currentKey.setBDMTransition( ( Integer ) value );
    				break;
    			case "metric" :
//    				_metrics.put( _currentKey, ( Metric ) value );
    				break;
    			}
    			
    			//debug
    			System.out.println( _currentKey );
    		}
		}
	}
}
