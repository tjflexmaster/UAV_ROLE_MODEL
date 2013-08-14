package simulator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Metric {
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