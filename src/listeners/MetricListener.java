package listeners;

import java.util.*;
import java.util.Map.*;

import gov.nasa.jpf.*;
import gov.nasa.jpf.vm.*;
import simulator.*;

public class MetricListener extends ListenerAdapter {

	/**
	 * A path stores non-deterministic workload metrics.
	 */
	public class Path {
		public HashMap<MetricKey, Metric> _metrics;
		public Path _parentPath;
		public ArrayList<Path> _childPaths;
		public double _totalWorkload;
		public int _totalTimeElapsed;
		
		Path ( ) {
			_metrics = new HashMap<MetricKey, Metric>();
			_parentPath = null;
			_childPaths = new ArrayList<Path>();
			_totalWorkload = 0.0;
			_totalTimeElapsed = 0;
		}
		
		public Path getLowest( ) {
			Path lowestPath = null;
			
			return lowestPath;
		}
		
		public Path getHighest( ) {
			Path highestPath = null;
			
			return highestPath;
		}
	}
	
	/**
	 * stores the metrics
	 */
	Path _rootPath = new Path();
	Path _currentPath = _rootPath;
	
	/**
	 * acts whenever methods execute
	 */
	@Override
	public void executeInstruction ( VM vm, ThreadInfo ti, Instruction insnToExecute ) {
		//get the methods information
		MethodInfo mi = insnToExecute.getMethodInfo( );
		String fullMethodName = mi.getFullName( );

		//only act on these methods
		if ( fullMethodName.contains( "setDecisionWorkload" ) )
			storeDecisionWorkload( vm, ti, insnToExecute, mi );
		else if ( fullMethodName.contains( "setChannelConflict" ) )
			storeChannelConflict( vm, ti, insnToExecute, mi );
		else if ( fullMethodName.contains( "setChannelLoad" ) )
			storeChannelLoad( vm, ti, insnToExecute, mi);
		else if ( fullMethodName.contains( "backtrack" ) )
			handleBacktrack( );
		else if ( fullMethodName.contains( "endSimulation" ) )
			printSimpleMetrics( );
	}

	private void storeDecisionWorkload(VM vm, ThreadInfo ti, Instruction insnToExecute, MethodInfo mi ) {
		
		//get the desired parameters
		int currentPC = ti.getPC( ).getPosition( );
		
		//get parameter information
		LocalVarInfo timeInfo = mi.getLocalVar( 1, currentPC );
		LocalVarInfo actorInfo = mi.getLocalVar( 2, currentPC );
		LocalVarInfo stateInfo = mi.getLocalVar( 3, currentPC );
		LocalVarInfo workloadInfo = mi.getLocalVar( 4, currentPC );
		
		//get parameter values
		Object timeValue = ti.getStackFrameExecuting( insnToExecute, 0 ).getLocalOrFieldValue( timeInfo.getName( ) );
		Object actorValue = ti.getStackFrameExecuting( insnToExecute, 0 ).getLocalOrFieldValue( actorInfo.getName( ) );
		Object stateValue = ti.getStackFrameExecuting( insnToExecute, 0 ).getLocalOrFieldValue( stateInfo.getName( ) );
		Object workloadValue = ti.getStackFrameExecuting( insnToExecute, 0 ).getLocalOrFieldValue( workloadInfo.getName( ) );
		
		//form metrics and keys
		MetricKey currentKey = new MetricKey( (int) timeValue, DEIToString( actorValue ), DEIToString( stateValue ) );
		Metric currentMetric = new Metric( Metric.TypeEnum.setDecisionWorkload, (int) workloadValue );
		
		//store metric
		Metric metric = _currentPath._metrics.get( currentKey );
		if ( metric == null ) { 
			_currentPath._metrics.put( currentKey, currentMetric );
		} else {
			 metric.add( (int) workloadValue );
		}
		
	}
	
	private void storeChannelConflict(VM vm, ThreadInfo ti, Instruction insnToExecute, MethodInfo mi) {
		
		//get the desired parameters
		int currentPC = ti.getPC( ).getPosition( );
		
		//get parameter information
		LocalVarInfo timeInfo = mi.getLocalVar( 1, currentPC );
		LocalVarInfo actor_targetInfo = mi.getLocalVar( 2, currentPC );
		LocalVarInfo channel_typeInfo = mi.getLocalVar( 3, currentPC );
		LocalVarInfo loadInfo = mi.getLocalVar( 4, currentPC );
		
		//get parameter values
		Object timeValue = ti.getStackFrameExecuting( insnToExecute, 0 ).getLocalOrFieldValue( timeInfo.getName( ) );
		Object actor_targetValue = ti.getStackFrameExecuting( insnToExecute, 0 ).getLocalOrFieldValue( actor_targetInfo.getName( ) );
		Object channel_typeValue = ti.getStackFrameExecuting( insnToExecute, 0 ).getLocalOrFieldValue( channel_typeInfo.getName( ) );
		Object loadValue = ti.getStackFrameExecuting( insnToExecute, 0 ).getLocalOrFieldValue( loadInfo.getName( ) );
		
		//form metrics and keys
		MetricKey currentKey = new MetricKey( (int) timeValue, DEIToString( actor_targetValue ), DEIToString( channel_typeValue ) );
		Metric currentMetric = new Metric( Metric.TypeEnum.setChannelConflict, (int) loadValue );
		
		//store metric
		Metric metric = _currentPath._metrics.get( currentKey );
		if ( metric == null ) { 
			_currentPath._metrics.put( currentKey, currentMetric );
		} else {
			 metric.add( (int) loadValue );
		}
		
	}

	private void storeChannelLoad(VM vm, ThreadInfo ti, Instruction insnToExecute, MethodInfo mi) {
		
		//get the desired parameters
		int currentPC = ti.getPC( ).getPosition( );
		
		//get parameter information
		LocalVarInfo timeInfo = mi.getLocalVar( 1, currentPC );
		LocalVarInfo actorSource = mi.getLocalVar( 2, currentPC );
		LocalVarInfo actorTarget = mi.getLocalVar(3, currentPC);
		LocalVarInfo channel_typeInfo = mi.getLocalVar( 4, currentPC );
		LocalVarInfo loadInfo = mi.getLocalVar( 5, currentPC );
		
		//get parameter values
		Object timeValue = ti.getStackFrameExecuting( insnToExecute, 0 ).getLocalOrFieldValue( timeInfo.getName( ) );
		Object actorSourceValue = ti.getStackFrameExecuting( insnToExecute, 0 ).getLocalOrFieldValue( actorSource.getName( ) );
		Object actorTargetValue = ti.getStackFrameExecuting( insnToExecute, 0).getLocalOrFieldValue( actorTarget.getName() );
		Object channel_typeValue = ti.getStackFrameExecuting( insnToExecute, 0 ).getLocalOrFieldValue( channel_typeInfo.getName( ) );
		Object workloadValue = ti.getStackFrameExecuting( insnToExecute, 0 ).getLocalOrFieldValue( loadInfo.getName( ) );
		
		//form metrics and keys
		MetricKey currentKey = new MetricKey( (int) timeValue, DEIToString( actorTargetValue ), DEIToString( channel_typeValue ) );
		Metric currentMetric = new Metric( Metric.TypeEnum.setChannelLoad, (int) workloadValue );
		
		//store metric
		Metric metric = _currentPath._metrics.get( currentKey );
		if ( metric == null ) { 
			_currentPath._metrics.put( currentKey, currentMetric );
		} else {
			 metric.add( (int) workloadValue );
		}
		
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

	private void handleBacktrack( ) {
		
		Path newPath = new Path();
		assert _currentPath._parentPath != null : "There is no parent path.";
		_currentPath._parentPath._childPaths.add( newPath );
		_currentPath = newPath;
		
	}

	private void printSimpleMetrics( ) {
		
		for( Entry<MetricKey, Metric> metric : _currentPath._metrics.entrySet( ) ){
//			System.out.println( "(" + metric.getKey() + ", " + metric.getValue() + ")" );
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
}
