package simulator;

import java.util.*;
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
public class MetricManager {
	HashMap<String, ArrayList<Metric>> actor_metrics;//a hash of the actors (keys) and the metrics applied to them (values)
	
	public MetricManager(){
		actor_metrics = new HashMap<String, ArrayList<Metric>>();
	}
	
	public void addMetric(String _actor, String _metric, Integer _value){
		ArrayList<Metric> metrics = actor_metrics.get(_actor);
		if(metrics == null){
			metrics = new ArrayList<Metric>();
			actor_metrics.put(_actor, metrics);
		}
		int metricIndex = metrics.indexOf(_metric);
		Metric metric = null;
		if(metricIndex == -1){// == null){
			metric = new Metric(_metric);
			metrics.add(metric);
		}else{
			metric = metrics.get(metricIndex);
		}
		metric.addEntry(_value);
	}
	
	public String toString(){
		String result = "";
		
		Iterator it = actor_metrics.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry)it.next();
			result += "\nACTOR:" + pairs.getKey() + " METRICS:" + pairs.getValue().toString();
			it.remove();
		}
		
		return result;
	}
}
