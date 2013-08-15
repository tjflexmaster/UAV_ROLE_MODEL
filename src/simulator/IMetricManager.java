package simulator;

import java.util.HashMap;

import simulator.Metric.MetricEnum;

public interface IMetricManager {
	
	/**
	 * creates the metric key and adds 1 to the metric
	 */
	public void addMetric(MetricEnum metric);
}
