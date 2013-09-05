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
		
		public TreeSet<DecisionWorkloadMetric> _dwmMetrics;
		public TreeSet<ChannelConflictMetric> _ccmMetrics;
		public TreeSet<ChannelLoadMetric> _clmMetrics;
		public TreeSet<ActorOutputMetric> _aomMetrics;
		
		public TreeMap<MetricKey, Metric> _metrics;
		public Path _parentPath;
		public ArrayList<Path> _childPaths;
		public double _totalWorkload;
		public int _totalTimeElapsed;
		
		Path ( ) {
			_dwmMetrics = new TreeSet<DecisionWorkloadMetric>();
			_ccmMetrics = new TreeSet<ChannelConflictMetric>();
			_clmMetrics = new TreeSet<ChannelLoadMetric>();
			_aomMetrics = new TreeSet<ActorOutputMetric>();
			
			_metrics = new TreeMap<MetricKey, Metric>( );
			_parentPath = null;
			_childPaths = new ArrayList<Path>( );
			_totalWorkload = 0.0;
			_totalTimeElapsed = 0;
		}
		
		public String toString( ) {
			String eol = System.getProperty("line.separator");
			String result = "";
			
//			for ( Entry<MetricKey, Metric> metric : this._metrics.entrySet( ) )
//				result += "(" + metric.getKey() + ", " + metric.getValue() + ")--";
			result += "DecisionWorkload: ";
			for ( DecisionWorkloadMetric m : this._dwmMetrics )
				result += m + " | ";
			result += eol;
			result += "ChannelConflict: ";
			for ( ChannelConflictMetric m : this._ccmMetrics )
				result += m + " | ";
			result += eol;
			result += "ChannelLoad: ";
			for ( ChannelLoadMetric m : this._clmMetrics )
				result += m + " | ";
			result += eol;
			result += "ActorOutput: ";
			for ( ActorOutputMetric m : this._aomMetrics )
				result += m + " | ";
			result += eol;
			
			return result;
		}
		
	}
	
	/**
	 * stores the metrics
	 */
	Path _rootPath = new Path( );
	Path _currentPath = _rootPath;
	
	/**
	 * acts whenever certain methods execute
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
		else if ( fullMethodName.contains( "setActorOutput" ) )
			storeActorOutput( vm, ti, insnToExecute, mi);
		else if ( fullMethodName.contains( "endSimulation" ) )
			printMetrics(_rootPath, "");
		
	}

	private void storeDecisionWorkload( VM vm, ThreadInfo ti, Instruction insnToExecute, MethodInfo mi ) {
		
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
//		MetricKey currentKey = new MetricKey( (int) timeValue, DEIToString( actorValue ), DEIToString( stateValue ) );
//		Metric currentMetric = new Metric( Metric.TypeEnum.setDecisionWorkload, (int) workloadValue );
		//store metric
//		Metric metric = _currentPath._metrics.get( currentKey );
//		if ( metric == null ) { 
//			_currentPath._metrics.put( currentKey, currentMetric );
//		} else {
//			 metric.add( (int) workloadValue );
//		}
		
		//store the metric data
		DecisionWorkloadMetric m = new DecisionWorkloadMetric((int) timeValue, DEIToString( actorValue ), DEIToString( stateValue ), (int) workloadValue); 
		
		if ( _currentPath._dwmMetrics.contains(m) ) {
			assert false : "Duplicate Decision workload metric";
		} else
			_currentPath._dwmMetrics.add(m);
		
		
	}
	
	private void storeChannelConflict( VM vm, ThreadInfo ti, Instruction insnToExecute, MethodInfo mi ) {
		
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
//		MetricKey currentKey = new MetricKey( (int) timeValue, DEIToString( actor_targetValue ), DEIToString( channel_typeValue ) );
//		Metric currentMetric = new Metric( Metric.TypeEnum.setChannelConflict, (int) loadValue );
//		
//		//store metric
//		Metric metric = _currentPath._metrics.get( currentKey );
//		if ( metric == null ) { 
//			_currentPath._metrics.put( currentKey, currentMetric );
//		} else {
//			 metric.add( (int) loadValue );
//		}
		
		ChannelConflictMetric m = new ChannelConflictMetric((int) timeValue, DEIToString( actor_targetValue ), DEIToString( channel_typeValue ), (int) loadValue);
		if ( _currentPath._ccmMetrics.contains(m) ) {
			assert false : "Duplicate channel conflict metric";
		} else
			_currentPath._ccmMetrics.add(m);
	}

	private void storeChannelLoad( VM vm, ThreadInfo ti, Instruction insnToExecute, MethodInfo mi ) {
		
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
		
//		//form metrics and keys
//		MetricKey currentKey = new MetricKey( (int) timeValue, DEIToString( actorTargetValue ), DEIToString( channel_typeValue ) );
//		Metric currentMetric = new Metric( Metric.TypeEnum.setChannelLoad, (int) workloadValue );
//		
//		//store metric
//		Metric metric = _currentPath._metrics.get( currentKey );
//		if ( metric == null )
//			_currentPath._metrics.put( currentKey, currentMetric );
//		else
//			 metric.add( (int) workloadValue );
		
		ChannelLoadMetric m = new ChannelLoadMetric((int) timeValue , DEIToString(actorSourceValue), DEIToString( actorTargetValue ), DEIToString( channel_typeValue ), (int) workloadValue);
		if ( _currentPath._clmMetrics.contains(m) ) {
			assert false : "Duplicate channel load metric";
		} else
			_currentPath._clmMetrics.add(m);
	}
	
private void storeActorOutput( VM vm, ThreadInfo ti, Instruction insnToExecute, MethodInfo mi ) {
		
		//get the desired parameters
		int currentPC = ti.getPC( ).getPosition( );
		
		//get parameter information
		LocalVarInfo timeInfo = mi.getLocalVar( 1, currentPC );
		LocalVarInfo actorInfo = mi.getLocalVar( 2, currentPC );
		LocalVarInfo memoryInfo = mi.getLocalVar(3, currentPC);
		LocalVarInfo outputInfo = mi.getLocalVar( 4, currentPC );
		
		//get parameter values
		Object timeValue = ti.getStackFrameExecuting( insnToExecute, 0 ).getLocalOrFieldValue( timeInfo.getName( ) );
		Object actorValue = ti.getStackFrameExecuting( insnToExecute, 0 ).getLocalOrFieldValue( actorInfo.getName( ) );
		Object memoryValue = ti.getStackFrameExecuting( insnToExecute, 0 ).getLocalOrFieldValue( memoryInfo.getName( ) );
		Object outputValue = ti.getStackFrameExecuting( insnToExecute, 0 ).getLocalOrFieldValue( outputInfo.getName( ) );
		
//		//form metrics and keys
//		MetricKey currentKey = new MetricKey( (int) timeValue, DEIToString( actorTargetValue ), DEIToString( channel_typeValue ) );
//		Metric currentMetric = new Metric( Metric.TypeEnum.setChannelLoad, (int) workloadValue );
//		
//		//store metric
//		Metric metric = _currentPath._metrics.get( currentKey );
//		if ( metric == null )
//			_currentPath._metrics.put( currentKey, currentMetric );
//		else
//			 metric.add( (int) workloadValue );
		
		ActorOutputMetric m = new ActorOutputMetric((int) timeValue , DEIToString(actorValue), (int) memoryValue, (int) outputValue);
		if ( _currentPath._aomMetrics.contains(m) ) {
			assert false : "Duplicate actor output metric";
		} else
			_currentPath._aomMetrics.add(m);
	}

	private void printMetrics(Path currentPath, String metrics) {
		
		metrics += ">>" + currentPath.toString();
		
		if( !currentPath._childPaths.isEmpty() )
			for( Path childPath : currentPath._childPaths )
				printMetrics( childPath, metrics );
		else
			System.out.println(metrics);
		
	}
	
	/**
	 * acts whenever a choice generator is set (at the first point of non-determinism).
	 * at this point we start a new child path.
	 */
	@Override
	public void choiceGeneratorSet ( VM vm, ChoiceGenerator<?> newCG ) {
		
		//get the methods information
		MethodInfo mi = newCG.getInsn( ).getMethodInfo( );
		String methodName = mi.getName( );
		
		//form a new branch
		if( methodName.equals( "duration" ) ) {
			Path newPath = new Path( );
			newPath._parentPath = _currentPath;
			_currentPath._childPaths.add( newPath );
			_currentPath = newPath;
			
//			//debug
//			System.out.println( "choiceGeneratorSet" );
		}
		
	}

	/**
	 * acts whenever a choice generator is advanced (at the following points of non-determinism).
	 * at this point we return to the parent path and start a new path.
	 */
	@Override
	public void choiceGeneratorAdvanced ( VM vm, ChoiceGenerator<?> currentCG ) {
		
		//get the methods information
		MethodInfo mi = currentCG.getInsn( ).getMethodInfo( );
		String methodName = mi.getName( );
		
		//form a new branch
		if( methodName.equals( "duration" ) ) {
			_currentPath = _currentPath._parentPath;
			Path newPath = new Path( );
			newPath._parentPath = _currentPath;
			_currentPath._childPaths.add( newPath );
			_currentPath = newPath;
			
//			//debug
//			System.out.println( "choiceGeneratorAdvanced" );
		}
		
	}
	
//	/**
//	 * acts when a choice generator completes all possible choices.
//	 * at this point we return to the parent path
//	 */
//	@Override
//	public void choiceGeneratorProcessed ( VM vm, ChoiceGenerator<?> processedCG ) {
//		
//		//get the methods information
//		MethodInfo mi = processedCG.getInsn().getMethodInfo( );
//		String fullMethodName = mi.getFullName( );
//		
//		if( fullMethodName.contains( "updateTransition" ) ) {
//			_currentPath = _currentPath._parentPath;
//			System.out.println( "choiceGeneratorProcessed" );
//		}
//		
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

//	private void handleBacktrack( ) {
//		
//		Path newPath = new Path();
//		assert _currentPath._parentPath != null : "There is no parent path.";
//		_currentPath._parentPath._childPaths.add( newPath );
//		_currentPath = newPath;
//		
//	}
	
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
