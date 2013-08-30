package simulator;

public class Metric {
	
	public enum TypeEnum {
		_channel_type,
		_actor_target,
		_workload,
		_load,
		_unknown
	}

	public TypeEnum _type = TypeEnum._unknown;
	public int _value = -1;
	
	public Metric (TypeEnum type, int value) {
		_type = type;
		_value = value;
	}
	
	public String toString() {
		String result = "";
		
		result += "(" + _type.name() + ", " + _value + ")";
		
		return result;
	}

	public void add(int value) {
		_value += value;
	}
	
//	public enum MetricEnum{
//		CHANNEL_ACTIVE_A,
//		CHANNEL_ACTIVE_V,
//		CHANNEL_ACTIVE_D,
//		CHANNEL_ACTIVE_O,
//		CHANNEL_INACTIVE_A,
//		CHANNEL_INACTIVE_V,
//		CHANNEL_INACTIVE_D,
//		CHANNEL_INACTIVE_O,
//		ENABLED,
//		ACTIVE,
//		MEMORY_ACTIVE,
//		MEMORY_INACTIVE,
//		CHANNEL_TEMP_A,
//		CHANNEL_TEMP_V,
//		CHANNEL_TEMP_D,
//		CHANNEL_TEMP_O,
//		CHANNEL_FIRE_A,
//		CHANNEL_FIRE_V,
//		CHANNEL_FIRE_D,
//		CHANNEL_FIRE_O,
//		MEMORY_TEMP,
//		MEMORY_FIRE,	
//	}
//	
//	String name;//the name of the metric (memory_accessed, input_accessed, ...)
//	HashMap<MetricEnum, Integer> metrics;//a hash of the time (key) and value (value) of the metric.
//	
//	public Metric(){
//		metrics = new HashMap<MetricEnum, Integer>();
//		for(MetricEnum metric_key : MetricEnum.values()){
//			metrics.put(metric_key, 0);
//		}
//	}
//	
//	public void increment(MetricEnum key){
//		Integer value = metrics.get(key);
//		value++;
//		metrics.put(key, value);
//	}
}