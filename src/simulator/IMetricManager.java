package simulator;

import java.util.HashMap;

public interface IMetricManager {
	HashMap<MetricKey, Metric> metric_map = new HashMap<MetricKey, Metric>();
	/**
	 * creates the metric key and adds 1 to the metric
	 */
	public void addMetric(String actor_name, String State, int transition_number, int time, MetricEnum metric);
	
	/**
	 * 
	 */
	public String toString();

}
