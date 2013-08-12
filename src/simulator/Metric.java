package simulator;

import java.util.HashMap;

public class Metric {
	String name;
	HashMap<Integer, Integer> entries;
	public Metric(String _name){
		name = _name;
		entries = new HashMap<Integer, Integer>();
	}
	
	public void addEntry(Integer _value){
		//TODO get time
		Integer time = 0;
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
		
		result += name;
		
		return result;
	}
}