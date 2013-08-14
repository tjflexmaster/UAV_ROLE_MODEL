package simulator;

import java.util.*;

import simulator.Metric.MetricEnum;
/**
 * Metrics taken care of
 * accessing internal variables
 * accessing active internal variables
 * accessing com channels
 * accessing active com channels
 * initializing com_channels
 * deactivating com_channels
 * changing com_channel data
 * @author jaredmoore
 *
 */
public class MetricManager implements IMetricManager {
	HashMap<MetricKey,Metric> actor_metrics;//a hash of the actors (keys) and the metrics applied to them (values)
	
	public MetricManager(){
		actor_metrics = new HashMap<MetricKey,Metric>();
	}

	@Override
	public void addMetric(String actor_name, String state, int transition_number, int time, MetricEnum metric) {
		MetricKey key = new MetricKey(time, actor_name, state, transition_number);
		Metric metrics = actor_metrics.get(key);
		if(metrics == null)
			metrics = new Metric();
		metrics.increment(metric);
		actor_metrics.put(key, metrics);
	}
}
