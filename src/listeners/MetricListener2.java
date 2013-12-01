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
	Path _rootPath = new Path( null, 0, 0, 0, new TreeMap<MetricKey, Metric>(), new TreeMap<MetricKey, Metric>(), new TreeMap<MetricKey, Metric>() );
	Path _currentPath = _rootPath;
	Path HCDW;//Highest Cumulative Decision Workload
	Path HCRW;//Highest Cumulative Resource Workload
	Path HCTW;//Highest Cumulative Temporal Workload
	Path LCDW;//Lowest Cumulative Decision Workload
	Path LCRW;//Lowest Cumulative Resource Workload
	Path LCTW;//Lowest Cumulative Temporal Workload
	
	/**
	 * acts whenever a choice generator is set (at the first point of non-determinism).
	 * at this point we start a new child path.
	 */
	@Override
	public void choiceGeneratorSet ( VM vm, ChoiceGenerator<?> newCG ) {
		makeChoice( newCG.getInsn( ).getMethodInfo( ) );
	}

	/**
	 * acts whenever a choice generator is advanced (at the following points of non-determinism).
	 * at this point we return to the parent path and start a new path.
	 */
	@Override
	public void choiceGeneratorAdvanced ( VM vm, ChoiceGenerator<?> currentCG ) {
		makeChoice( currentCG.getInsn( ).getMethodInfo( ) );
	}
	
	private void makeChoice( MethodInfo mi ) {
		String methodName = mi.getName( );
		if( methodName.equals( "duration" ) )
			advancePath( mi );
	}
	
	private void advancePath( MethodInfo mi ) {
		System.out.println( "point of non-determinism" );
		Path newPath = new Path( _currentPath,
				_currentPath._cumulativeDecisionWorkload,
				_currentPath._cumulativeResourceWorkload,
				_currentPath._cumulativeTemporalWorkload,
				_currentPath._cumulativeDecisionMetrics,
				_currentPath._cumulativeResourceMetrics,
				_currentPath._cumulativeTemporalMetrics );
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
			storeDecisionWorkload( ti, insnToExecute, mi );
		else if ( fullMethodName.contains( "setChannelConflict" ) )
			storeChannelConflict( ti, insnToExecute, mi );
		else if ( fullMethodName.contains( "setChannelLoad" ) )
			storeChannelLoad( ti, insnToExecute, mi );
		else if ( fullMethodName.contains( "endSimulation" ) )
			compareAndPrintCumlativeWorkload( _currentPath );
	}

	private void storeDecisionWorkload( ThreadInfo ti, Instruction insnToExecute, MethodInfo mi ) {
		//get parameter values
		int time = getIntParameter( 1, ti, insnToExecute, mi );
		String actorName = getStringParameter( 2, ti, insnToExecute, mi );
		String stateName = getStringParameter( 3, ti, insnToExecute, mi );
		int availableTransitions = getIntParameter( 4, ti, insnToExecute, mi );
		
		//don't measure mock (watered down) model objects
		if( isMock( actorName ) )
			return;
		
		//form metrics and keys
		MetricKey currentKey = new MetricKey( time, actorName, stateName );
		Metric currentMetric = new Metric( Metric.TypeEnum.setDecisionWorkload, availableTransitions );
		
		//store metric
		Metric metric = _currentPath._cumulativeDecisionMetrics.get( currentKey );
		if ( metric == null )
			_currentPath._cumulativeDecisionMetrics.put( currentKey, currentMetric );
		else
			 metric.add( availableTransitions );
		_currentPath._cumulativeDecisionWorkload += availableTransitions;
	}
	
	private void storeChannelConflict( ThreadInfo ti, Instruction insnToExecute, MethodInfo mi ) {
		//get parameter values
		int time = getIntParameter( 1, ti, insnToExecute, mi );
		String targetActor = getStringParameter( 2, ti, insnToExecute, mi );
		int channelConflicts = getIntParameter( 4, ti, insnToExecute, mi );
		
		//don't measure mock (watered down) model objects
		if( isMock( targetActor ) )
			return;
		
		//form metrics and keys
		MetricKey currentKey = new MetricKey( time, targetActor );
		Metric currentMetric = new Metric( Metric.TypeEnum.setChannelConflict, channelConflicts );
		
		//store metric
		Metric metric = _currentPath._cumulativeResourceMetrics.get( currentKey );
		if ( metric == null )
			_currentPath._cumulativeResourceMetrics.put( currentKey, currentMetric );
		else
			 metric.add( channelConflicts );
		_currentPath._cumulativeResourceWorkload += channelConflicts;
	}

	private void storeChannelLoad( ThreadInfo ti, Instruction insnToExecute, MethodInfo mi ) {
		//get parameter values
		int time = getIntParameter( 1, ti, insnToExecute, mi );
		String targetActor = getStringParameter( 3, ti, insnToExecute, mi );
		String outputName = getStringParameter( 5, ti, insnToExecute, mi );
		int channelLoad = getIntParameter( 6, ti, insnToExecute, mi );
		
		//don't measure mock (watered down) model objects
		if( isMock( targetActor ) )
			return;
		
		//form metrics and keys
		MetricKey currentKey = new MetricKey( time + channelLoad, targetActor );
		Metric currentOutput = new Metric( Metric.TypeEnum.setActorOutputs, outputName );
		Metric currentMetric = new Metric( Metric.TypeEnum.setChannelLoad, channelLoad );
		
		//store metric
		Metric metric = _currentPath._cumulativeTemporalMetrics.get( currentKey );
		if ( metric == null )
			_currentPath._cumulativeTemporalMetrics.put( currentKey, currentMetric );
		else
			 metric.add( channelLoad );
		_currentPath._actorOutputs.put(currentKey, currentOutput);
		_currentPath._cumulativeTemporalWorkload += channelLoad;
	}
	
	private Path compareAndPrintCumlativeWorkload( Path path ) {
		if ( HCDW == null || HCDW._cumulativeDecisionWorkload < path._cumulativeDecisionWorkload) {
			write( "HCDW.csv", path.toString() );
			HCDW = new Path(path._parentPath, path._cumulativeDecisionWorkload, path._cumulativeResourceWorkload, path._cumulativeTemporalWorkload, path._cumulativeDecisionMetrics, path._cumulativeResourceMetrics, path._cumulativeTemporalMetrics);
		}
		
		if ( HCRW == null || HCRW._cumulativeResourceWorkload < path._cumulativeResourceWorkload) {
			write( "HCRW.csv", path.toString() );
			HCRW = new Path(path._parentPath, path._cumulativeDecisionWorkload, path._cumulativeResourceWorkload, path._cumulativeTemporalWorkload, path._cumulativeDecisionMetrics, path._cumulativeResourceMetrics, path._cumulativeTemporalMetrics);
		}
		
		if ( HCTW == null || HCTW._cumulativeTemporalWorkload < path._cumulativeTemporalWorkload) {
			write( "HCTW.csv", path.toString() );
			HCTW = new Path(path._parentPath, path._cumulativeDecisionWorkload, path._cumulativeResourceWorkload, path._cumulativeTemporalWorkload, path._cumulativeDecisionMetrics, path._cumulativeResourceMetrics, path._cumulativeTemporalMetrics);
		}
		
		if ( LCDW == null || LCDW._cumulativeDecisionWorkload > path._cumulativeDecisionWorkload) {
			write( "LCDW.csv", path.toString() );
			LCDW = new Path(path._parentPath, path._cumulativeDecisionWorkload, path._cumulativeResourceWorkload, path._cumulativeTemporalWorkload, path._cumulativeDecisionMetrics, path._cumulativeResourceMetrics, path._cumulativeTemporalMetrics);
		}
		
		if ( LCRW == null || LCRW._cumulativeResourceWorkload > path._cumulativeResourceWorkload) {
			write( "LCRW.csv", path.toString() );
			LCRW = new Path(path._parentPath, path._cumulativeDecisionWorkload, path._cumulativeResourceWorkload, path._cumulativeTemporalWorkload, path._cumulativeDecisionMetrics, path._cumulativeResourceMetrics, path._cumulativeTemporalMetrics);
		}
		
		if ( LCTW == null || LCTW._cumulativeTemporalWorkload > path._cumulativeTemporalWorkload) {
			write( "LCTW.csv", path.toString() );
			LCTW = new Path(path._parentPath, path._cumulativeDecisionWorkload, path._cumulativeResourceWorkload, path._cumulativeTemporalWorkload, path._cumulativeDecisionMetrics, path._cumulativeResourceMetrics, path._cumulativeTemporalMetrics);
		}
		
		return path;
	}
	
	private int getIntParameter( int i, ThreadInfo ti, Instruction insnToExecute, MethodInfo mi) {
		return (int) ti.getStackFrameExecuting( insnToExecute, 0 ).getLocalOrFieldValue( mi.getLocalVar( i, ti.getPC( ).getPosition( ) ).getName() );
	}

	private String getStringParameter( int i, ThreadInfo ti, Instruction insnToExecute, MethodInfo mi) {
		return DEIToString( ti.getStackFrameExecuting( insnToExecute, 0 ).getLocalOrFieldValue( mi.getLocalVar( i, ti.getPC( ).getPosition( ) ).getName() ) );
	}
	
	private boolean isMock( String actor ) {
		if (actor.contains("ater"))
			return true;
		return false;
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
