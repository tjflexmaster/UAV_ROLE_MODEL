package simulator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Metric {
	
	public enum MetricEnum{
		CHANNEL_ACTIVE_A,
		CHANNEL_ACTIVE_V,
		CHANNEL_ACTIVE_D,
		CHANNEL_ACTIVE_O,
		CHANNEL_INACTIVE_A,
		CHANNEL_INACTIVE_V,
		CHANNEL_INACTIVE_D,
		CHANNEL_INACTIVE_O,
		ENABLED,
		ACTIVE,
		MEMORY_ACTIVE,
		MEMORY_INACTIVE,
		CHANNEL_TEMP_A,
		CHANNEL_TEMP_V,
		CHANNEL_TEMP_D,
		CHANNEL_TEMP_O,
		CHANNEL_FIRE_A,
		CHANNEL_FIRE_V,
		CHANNEL_FIRE_D,
		CHANNEL_FIRE_O,
		MEMORY_TEMP_O,
		MEMORY_FIRE_A,
		
	}
	
	String name;//the name of the metric (memory_accessed, input_accessed, ...)
	HashMap<Integer, Integer> entries;//a hash of the time (key) and value (value) of the metric.
	
	public Metric(String _name){
		name = _name;
		entries = new HashMap<Integer, Integer>();
	}
	
	public void addEntry(Integer _value){
		//TODO get time
		Integer time = Simulator.getSim().getClockTime();
		entries.put(time, _value);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if(obj instanceof String){
			return name.equals(obj);
		}
		if (!(obj instanceof Metric))
			return false;
		Metric other = (Metric) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	public String toString() {
		String result = "";
		
		Iterator it = entries.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry)it.next();
			result += "\n\t( TIME:" + pairs.getKey() + " , VALUE:" + pairs.getValue().toString() + " , NAME:" + name + " )";
			it.remove();
		}
		
		return result;
	}
}