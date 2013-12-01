package listeners;

import java.util.*;
import java.util.Map.Entry;

import simulator.Metric;
import simulator.MetricKey;



	/**
 * A path stores non-deterministic workload metrics.
 */
public class Path {

	public Path _parentPath;
	public int _cumulativeResourceWorkload;
	public int _cumulativeTemporalWorkload;
	public int _cumulativeDecisionWorkload;
	public TreeMap<MetricKey, Metric> _cumulativeResourceMetrics;
	public TreeMap<MetricKey, Metric> _cumulativeTemporalMetrics;
	public TreeMap<MetricKey, Metric> _cumulativeDecisionMetrics;
	public TreeMap<MetricKey, Metric> _actorTasks;
	public TreeMap<MetricKey, Metric> _actorOutputs;
	public ArrayList<Path> _childPaths;
	
	public Path(Path parent,
			int cumulativeDecisionWorkload,
			int cumulativeResourceWorkload,
			int cumulativeTemporalWorkload,
			TreeMap<MetricKey, Metric> cumulativeDecisionMetrics,
			TreeMap<MetricKey, Metric> cumulativeResourceMetrics,
			TreeMap<MetricKey, Metric> cumulativeTemporalMetrics) {
		_parentPath = parent;
		_cumulativeResourceMetrics = cumulativeResourceMetrics;
		_cumulativeTemporalMetrics = cumulativeTemporalMetrics;
		_cumulativeDecisionMetrics = cumulativeDecisionMetrics;
		_cumulativeResourceWorkload = cumulativeDecisionWorkload;
		_cumulativeTemporalWorkload = cumulativeResourceWorkload;
		_cumulativeDecisionWorkload = cumulativeTemporalWorkload;
		_actorTasks = new TreeMap<MetricKey, Metric>();
		_actorOutputs = new TreeMap<MetricKey, Metric>();
		_childPaths = new ArrayList<Path>( );
	}

	public String toString( ) {
		String result = "time, resource data ((Actor State Value)*), resource workload, temporal data ((Actor State Value)*), temporal workload, decision data ((Actor State Value)*), decision workload, task data, fired outputs\n";
		for ( int time = 0; time < 200; time++ )
			result += getMetricsByTime( time );
		
		return result;
	}
	
	private String getMetricsByTime( int time ) {
		String result = time + ", ";

		int cumulativeResourceValue = 0;
		boolean found = false;
		for ( Entry<MetricKey, Metric> metric : _cumulativeResourceMetrics.entrySet( ) )
			if ( found && ( metric.getKey().getTime() != time ) )
				break;
			else if ( found = ( metric.getKey().getTime() == time ) )
				result += "( " + metric.getKey().getActor() + " " + metric.getKey().getState() + " " + (cumulativeResourceValue += metric.getValue()._value) + " ) ";
		result += ", " + cumulativeResourceValue;

		int cumulativeTemporalValue = 0;
		result += ",";
		found = false;
		for ( Entry<MetricKey, Metric> metric : _cumulativeTemporalMetrics.entrySet( ) )
			if ( found && ( metric.getKey().getTime() != time ) )
				break;
			else if ( found = ( metric.getKey().getTime() == time ) )
				result += "( " + metric.getKey().getActor() + " " + metric.getKey().getState() + " " + (cumulativeTemporalValue += metric.getValue()._value) + " ) ";
		result += ", " + cumulativeTemporalValue;

		int cumulativeDecisionValue = 0;
		result += ",";
		found = false;
		for ( Entry<MetricKey, Metric> metric : _cumulativeDecisionMetrics.entrySet( ) )
			if ( found && ( metric.getKey().getTime() != time ) )
				break;
			else if ( found = ( metric.getKey().getTime() == time ) )
				result += "( " + metric.getKey().getActor() + " " + metric.getKey().getState() + " " + (cumulativeDecisionValue += metric.getValue()._value) + " ) ";
		result += ", " + cumulativeDecisionValue;

		result += ",";
		found = false;
		for ( Entry<MetricKey, Metric> metric : _actorTasks.entrySet() )
			if ( found && ( metric.getKey().getTime() != time ) )
				break;
			else if ( found = ( metric.getKey().getTime() == time ) )
				if ( metric.getValue()._valueString.contains( "START" ) )
					result += "( " + metric.getKey().getActor() + " " + metric.getValue()._valueString + " START ) ";
				else
					result += "( " + metric.getKey().getActor() + " " + metric.getValue()._valueString + " STOP ) ";
					
		
		result += ",";
		found = false;
		for ( Entry<MetricKey, Metric> metric : _actorOutputs.entrySet() )
			if ( found && ( metric.getKey().getTime() != time ) )
				break;
			else if ( found = ( metric.getKey().getTime() == time ) )
				result += "( " + metric.getKey().getActor() + " " + metric.getValue()._valueString + " ) ";
		
		return result + "\n";
	}
}