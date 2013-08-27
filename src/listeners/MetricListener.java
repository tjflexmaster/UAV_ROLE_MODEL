package listeners;

import java.util.TreeMap;

import simulator.Metric;
import simulator.Metric.MetricEnum;
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
    			
    			switch( parName ){
    			case "time" :
    				_currentKey.setBDMTime( ( int ) value ); 
    				break;
    			case "actor_name" :
    				_currentKey.setBDMActor( DEIToString( value ) );
    				break;
    			case "state_name" :
    				_currentKey.setBDMState( DEIToString( value ) );
    				break;
    			case "transition" :
    				_currentKey.setBDMTransition( ( int ) value );
    				break;
    			case "metric" :
    				MetricEnum metric = DEIToMetricEnum( value );
    				Metric metrics = _metrics.get( _currentKey );
    				if ( metrics == null )
    					metrics = new Metric( );
    				else
    					metrics.increment( metric );
    				MetricKey key = _currentKey.clone( );
    				_metrics.put( key, metrics );
    				break;
    			}
    			
    			//debug
    			System.out.println( _currentKey );
    		}
		}
	}
	
	/**
	 * must always return a !null MetricEnum
	 * @param object a generic object returned by getLocalOrFieldValue
	 * @return the appropriate MetricEnum
	 */
	private MetricEnum DEIToMetricEnum( Object object ) {
		DynamicElementInfo DEI = ( DynamicElementInfo ) object;
		MetricEnum result = null;
		
//		System.out.println( DEI.getStringChars( ) );
		result = MetricEnum.ACTIVE;
		
		return result;
	}

	/**
	 * must always return a !null string
	 * @param object a generic object returned by getLocalOrFieldValue
	 * @return a string representation of the object
	 */
	private String DEIToString( Object object ) {
		DynamicElementInfo DEI = ( DynamicElementInfo ) object;
		String result = "";
		
		char[] stringChars = DEI.getStringChars( );
		for( char nextChar : stringChars )
			result += nextChar;
		
		return result;
	}
}
