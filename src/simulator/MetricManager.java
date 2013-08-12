package simulator;

import java.util.*;

public class MetricManager {
	HashMap<String, ArrayList<Metric>> actor_metrics;
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
			result += pairs.getKey() + " - " + pairs.getValue().toString() + "\n";
			it.remove();
		}
		
		return result;
	}
}
