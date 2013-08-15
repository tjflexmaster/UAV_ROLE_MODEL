package simulator;

import java.util.HashMap;

import simulator.Metric.MetricEnum;

public interface IMetricManager {
	HashMap<MetricKey, Metric> metric_map = new HashMap<MetricKey, Metric>();
	MetricKey currentKey = new MetricKey(-1, "", "", -1);
	
	/**
	 * creates the metric key and adds 1 to the metric
	 */
	public void addMetric(MetricEnum metric);
}
