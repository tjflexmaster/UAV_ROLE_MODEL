package listeners;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.Map.*;

import gov.nasa.jpf.*;
import gov.nasa.jpf.vm.*;
import simulator.*;

public class MetricListener2 extends ListenerAdapter {
	
	/**
	 * stores the metrics
	 */
	Path _rootPath = new Path( null, 0, 0, 0 );
	Path _currentPath = _rootPath;
	Path HCDW;//Highest Cumulative Decision Workload
	Path HCRW;//Highest Cumulative Resource Workload
	Path HCTW;//Highest Cumulative Temporal Workload
	Path LCDW;//Lowest Cumulative Decision Workload
	Path LCRW;//Lowest Cumulative Resource Workload
	Path LCTW;//Lowest Cumulative Temporal Workload
	Path HPDW;//Highest Peak Decision Workload
	Path HPRW;//Highest Peak Resource Workload
	Path HPTW;//Highest Peak Temporal Workload
	Path LPDW;//Lowest Peak Decision Workload
	Path LPRW;//Lowest Peak Resource Workload
	Path LPTW;//Lowest Peak Temporal Workload
	
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
		if( methodName.equals( "duration" ) )
			advancePath();
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
		if( methodName.equals( "duration" ) )
			advancePath();
	}
	
	private void advancePath( ) {
		Path newPath = new Path( _currentPath,
				_currentPath._decisionWorkload,
				_currentPath._resourceWorkload,
				_currentPath._temporalWorkload );
		newPath._parentPath = _currentPath;
		_currentPath._childPaths.add( newPath );
		_currentPath = newPath;
	}
	
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
		else if ( fullMethodName.contains( "endSimulation" ) )
			assignPaths(_rootPath, "");
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
		Object availableTransitions = ti.getStackFrameExecuting( insnToExecute, 0 ).getLocalOrFieldValue( workloadInfo.getName( ) );
		
		//form metrics and keys
		MetricKey currentKey = new MetricKey( (int) timeValue, DEIToString( actorValue ), DEIToString( stateValue ) );
		Metric currentMetric = new Metric( Metric.TypeEnum.setDecisionWorkload, (int) availableTransitions );
		
		//store metric
		Metric metric = _currentPath._decisionMetrics.get( currentKey );
		if ( metric == null )
			_currentPath._decisionMetrics.put( currentKey, currentMetric );
		else
			 metric.add( (int) availableTransitions );
		_currentPath._decisionWorkload += (int) availableTransitions;
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
		Object channelConflicts = ti.getStackFrameExecuting( insnToExecute, 0 ).getLocalOrFieldValue( loadInfo.getName( ) );
		
		//form metrics and keys
		MetricKey currentKey = new MetricKey( (int) timeValue, DEIToString( actor_targetValue ), DEIToString( channel_typeValue ) );
		Metric currentMetric = new Metric( Metric.TypeEnum.setChannelConflict, (int) channelConflicts );
		
		//store metric
		Metric metric = _currentPath._resourceMetrics.get( currentKey );
		if ( metric == null )
			_currentPath._resourceMetrics.put( currentKey, currentMetric );
		else
			 metric.add( (int) channelConflicts );
		_currentPath._resourceWorkload += (int) channelConflicts;
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
		Object channelLoad = ti.getStackFrameExecuting( insnToExecute, 0 ).getLocalOrFieldValue( loadInfo.getName( ) );
		
		//form metrics and keys
		MetricKey currentKey = new MetricKey( (int) timeValue, DEIToString( actorTargetValue ), DEIToString( channel_typeValue ) );
		Metric currentMetric = new Metric( Metric.TypeEnum.setChannelLoad, (int) channelLoad );
		
		//store metric
		Metric metric = _currentPath._temporalMetrics.get( currentKey );
		if ( metric == null )
			_currentPath._temporalMetrics.put( currentKey, currentMetric );
		else
			 metric.add( (int) channelLoad );
		_currentPath._temporalWorkload += (int) channelLoad;
	}

	private void assignPaths(Path currentPath, String metrics) {
		metrics += currentPath.toString();
		
		if ( !currentPath._childPaths.isEmpty() )
			for ( Path childPath : currentPath._childPaths )
				assignPaths( comparePeakWorkload( childPath ), metrics );
		else
			compareAndPrintCumlativeWorkload( currentPath, metrics );
	}
	
	private Path comparePeakWorkload( Path path ) {
//		if ( HPDW == null || HPDW. )
//			
		Path HPRW;
		Path HPTW;
		Path LPDW;
		Path LPRW;
		Path LPTW;
		return path;
	}
	
	private Path compareAndPrintCumlativeWorkload( Path path, String metrics ) {
		if ( HCDW == null || HCDW._decisionWorkload < path._decisionWorkload) {
			write( "HCDW.csv", metrics );
			HCDW = new Path(path._parentPath, path._decisionWorkload, path._resourceWorkload, path._temporalWorkload);
			System.out.println("printed new HCDW.csv");
		}
		
		if ( HCRW == null || HCRW._resourceWorkload < path._resourceWorkload) {
			write( "HCRW.csv", metrics );
			HCRW = new Path(path._parentPath, path._decisionWorkload, path._resourceWorkload, path._temporalWorkload);
			System.out.println("printed new HCRW.csv");
		}
		
		if ( HCTW == null || HCTW._temporalWorkload < path._temporalWorkload) {
			write( "HCTW.csv", metrics );
			HCTW = new Path(path._parentPath, path._decisionWorkload, path._resourceWorkload, path._temporalWorkload);
			System.out.println("printed new HCTW.csv");
		}
		
		if ( LCDW == null || LCDW._decisionWorkload > path._decisionWorkload) {
			write( "LCDW.csv", metrics );
			LCDW = new Path(path._parentPath, path._decisionWorkload, path._resourceWorkload, path._temporalWorkload);
			System.out.println("printed new LCDW.csv");
		}
		
		if ( LCRW == null || LCRW._resourceWorkload > path._resourceWorkload) {
			write( "LCRW.csv", metrics );
			LCRW = new Path(path._parentPath, path._decisionWorkload, path._resourceWorkload, path._temporalWorkload);
			System.out.println("printed new LCRW.csv");
		}
		
		if ( LCTW == null || LCTW._temporalWorkload > path._temporalWorkload) {
			write( "LCTW.csv", metrics );
			LCTW = new Path(path._parentPath, path._decisionWorkload, path._resourceWorkload, path._temporalWorkload);
			System.out.println("printed new LCTW.csv");
		}
		
		return path;
	}

	private void write(String filename, String metrics) {
		try {
			FileWriter writer = new FileWriter(new File("src/csvFiles/" + filename));
	
			writer.write(metrics);
			
			writer.close();
		} catch (IOException e) {
			System.err.println("-- couldn't print " + filename + ".csv --");
			e.printStackTrace();
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

}
