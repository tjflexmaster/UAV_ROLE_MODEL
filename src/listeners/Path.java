package listeners;

import java.util.*;
import java.util.Map.Entry;

import simulator.Metric;
import simulator.MetricKey;



	/**
 * A path stores non-deterministic workload metrics.
 */
public class Path {
	
	public TreeMap<MetricKey, Metric> _cumulativeResourceMetrics;
	public TreeMap<MetricKey, Metric> _cumulativeTemporalMetrics;
	public TreeMap<MetricKey, Metric> _cumulativeDecisionMetrics;
	public int _cumulativeResourceWorkload;
	public int _cumulativeTemporalWorkload;
	public int _cumulativeDecisionWorkload;
	public Path _parentPath;
	public ArrayList<Path> _childPaths;
//	public int _totalTimeElapsed;
	
	public Path(Path parent, int cumulativeDecisionWorkload, int cumulativeResourceWorkload, int cumulativeTemporalWorkload,
			TreeMap<MetricKey, Metric> cumulativeDecisionMetrics, TreeMap<MetricKey, Metric> cumulativeResourceMetrics, TreeMap<MetricKey, Metric> cumulativeTemporalMetrics) {
		_cumulativeResourceMetrics = cumulativeResourceMetrics;
		_cumulativeTemporalMetrics = cumulativeTemporalMetrics;
		_cumulativeDecisionMetrics = cumulativeDecisionMetrics;
		_cumulativeResourceWorkload = cumulativeDecisionWorkload;
		_cumulativeTemporalWorkload = cumulativeResourceWorkload;
		_cumulativeDecisionWorkload = cumulativeTemporalWorkload;
		_parentPath = parent;
		_childPaths = new ArrayList<Path>( );
//		_totalTimeElapsed = 0;
	}

	public String toString( ) {
		String result = "time, resource workload, temporal workload, decision workload, resource data ((Actor State Value)*), temporal data ((Actor State Value)*), decision data ((Actor State Value)*)\n";
		for ( int time = 0; time < 200; time++ )
			result += time
					+ "," + getMetricsByTime( _cumulativeResourceMetrics, time )
					+ "," + getMetricsByTime( _cumulativeTemporalMetrics, time )
					+ "," + getMetricsByTime( _cumulativeDecisionMetrics, time )
					+ "," + getActorsByTime( time ) + "\n";
		
		return result;
	}
	
	private int getMetricsByTime( TreeMap<MetricKey, Metric> metrics, int time ) {
		int result = 0;
		boolean found = false;
		for ( Entry<MetricKey, Metric> metric : metrics.entrySet( ) )
			if ( found && ( metric.getKey().getTime() != time ) )
				break;
			else if ( found = ( metric.getKey().getTime() == time ) )
				result += metric.getValue()._value;
		
		return result;
	}
	
	private String getActorsByTime( int time ) {
		String result = "";

		boolean found = false;
		for ( Entry<MetricKey, Metric> metric : _cumulativeResourceMetrics.entrySet( ) )
			if ( found && ( metric.getKey().getTime() != time ) )
				break;
			else if ( found = ( metric.getKey().getTime() == time ) )
				result += "( " + metric.getKey().getActor() + " " + metric.getKey().getState() + " " + metric.getValue()._value + " ) ";

		result += ",";
		found = false;
		for ( Entry<MetricKey, Metric> metric : _cumulativeTemporalMetrics.entrySet( ) )
			if ( found && ( metric.getKey().getTime() != time ) )
				break;
			else if ( found = ( metric.getKey().getTime() == time ) )
				result += "( " + metric.getKey().getActor() + " " + metric.getKey().getState() + " " + metric.getValue()._value + " ) ";

		result += ",";
		found = false;
		for ( Entry<MetricKey, Metric> metric : _cumulativeDecisionMetrics.entrySet( ) )
			if ( found && ( metric.getKey().getTime() != time ) )
				break;
			else if ( found = ( metric.getKey().getTime() == time ) )
				result += "( " + metric.getKey().getActor() + " " + metric.getKey().getState() + " " + metric.getValue()._value + " ) ";
		
		return result;
	}
	
}