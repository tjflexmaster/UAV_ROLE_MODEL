package listeners;

import gov.nasa.jpf.*;
import gov.nasa.jpf.vm.*;

public class MetricListener extends ListenerAdapter {
	/**
	 * stores the metrics
	 */
	
	/**
	 * acts whenever methods execute
	 */
	@Override
	public void executeInstruction ( VM vm, ThreadInfo ti, Instruction insnToExecute ) {
		//get the methods information
		MethodInfo mi = insnToExecute.getMethodInfo( );
		String mname = mi.getFullName( );
		
		//only act on these methods
		if ( mname.contains( "setDecisionWorkload" )
				|| mname.contains( "setChannelConflict" )
				|| mname.contains( "setChannelLoad" ) ) {
			
			int currentPC = ti.getPC().getPosition();
    		LocalVarInfo parameter = mi.getLocalVar( 1, currentPC );
    		if( parameter != null ){
    			//get the desired parameter's information
    			String parName = parameter.getName( );//name
    			Object value = ti.getStackFrameExecuting( insnToExecute, 0 ).getLocalOrFieldValue( parName );//value
    			
    			switch( parName ){
    			case "time" : ;
    			case "actor" : ;
    			case "state" : ;
    			case "workload" : ;
    			case "channel_type" : ;
    			case "load" : ;
    			}
    		}
		}
	}
	
//	/**
//	 * must always return a !null MetricEnum
//	 * @param object a generic object returned by getLocalOrFieldValue
//	 * @return the appropriate MetricEnum
//	 */
//	private MetricEnum DEIToMetricEnum( Object object ) {
//		DynamicElementInfo DEI = ( DynamicElementInfo ) object;
//		MetricEnum result = null;
//		
//		Fields fields = DEI.getFields( );
//		int enumIndex = fields.getIntValue( 1 );
//		switch ( enumIndex ) {
//		case 0 : result = MetricEnum.CHANNEL_ACTIVE_A;break;
//		case 1 : result = MetricEnum.CHANNEL_ACTIVE_V;break;
//		case 2 : result = MetricEnum.CHANNEL_ACTIVE_D;break;
//		case 3 : result = MetricEnum.CHANNEL_ACTIVE_O;break;
//		case 4 : result = MetricEnum.CHANNEL_INACTIVE_A;break;
//		case 5 : result = MetricEnum.CHANNEL_INACTIVE_V;break;
//		case 6 : result = MetricEnum.CHANNEL_INACTIVE_D;break;
//		case 7 : result = MetricEnum.CHANNEL_INACTIVE_O;break;
//		case 8 : result = MetricEnum.ENABLED;break;
//		case 9 : result = MetricEnum.ACTIVE;break;
//		case 10 : result = MetricEnum.MEMORY_ACTIVE;break;
//		case 11 : result = MetricEnum.MEMORY_INACTIVE;break;
//		case 12 : result = MetricEnum.CHANNEL_TEMP_A;break;
//		case 13 : result = MetricEnum.CHANNEL_TEMP_V;break;
//		case 14 : result = MetricEnum.CHANNEL_TEMP_D;break;
//		case 15 : result = MetricEnum.CHANNEL_TEMP_O;break;
//		case 16 : result = MetricEnum.CHANNEL_FIRE_A;break;
//		case 17 : result = MetricEnum.CHANNEL_FIRE_V;break;
//		case 18 : result = MetricEnum.CHANNEL_FIRE_D;break;
//		case 19 : result = MetricEnum.CHANNEL_FIRE_O;break;
//		case 20 : result = MetricEnum.MEMORY_TEMP;break;
//		case 21 : result = MetricEnum.MEMORY_FIRE;break;
//		}
//		
//		return result;
//	}

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
