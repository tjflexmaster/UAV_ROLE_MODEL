package listeners;

import java.util.*;
import java.util.Map.Entry;

import simulator.Metric;
import simulator.MetricKey;



	/**
 * A path stores non-deterministic workload metrics.
 */
public class Path {
	
	public TreeMap<MetricKey, Metric> _resourceMetrics;
	public TreeMap<MetricKey, Metric> _temporalMetrics;
	public TreeMap<MetricKey, Metric> _decisionMetrics;
	public Path _parentPath;
	public ArrayList<Path> _childPaths;
	public int _resourceWorkload;
	public int _temporalWorkload;
	public int _decisionWorkload;
//	public int _totalTimeElapsed;
	
	public Path(Path parent, int decisionWorkload,
			int resourceWorkload, int temporalWorkload) {
		_resourceMetrics = new TreeMap<MetricKey, Metric>( );
		_temporalMetrics = new TreeMap<MetricKey, Metric>( );
		_decisionMetrics = new TreeMap<MetricKey, Metric>( );
		_childPaths = new ArrayList<Path>( );
		_parentPath = parent;
		_resourceWorkload = decisionWorkload;
		_temporalWorkload = resourceWorkload;
		_decisionWorkload = temporalWorkload;
//		_totalTimeElapsed = 0;
	}

	public String toString( ) {
		String result = "time, channel conflicts, channel load, available transitions\n";
		
		for ( int time = 0; time < 200; time++ )
			result += time
					+ "," + getMetricsByTime( _resourceMetrics, time )
					+ "," + getMetricsByTime( _temporalMetrics, time )
					+ "," + getMetricsByTime( _decisionMetrics, time ) + "\n";
		
		return result;
	}
	
	private int getMetricsByTime( TreeMap<MetricKey, Metric> metrics, int time ) {
		
		int value = 0;
		boolean found = false;
		for ( Entry<MetricKey, Metric> metric : metrics.entrySet( ) )
			if ( found && ( metric.getKey().getTime() != time ) )
				break;
			else if ( found = ( metric.getKey().getTime() == time ) )
				value += metric.getValue()._value;
		
		return value;
	}
	
}