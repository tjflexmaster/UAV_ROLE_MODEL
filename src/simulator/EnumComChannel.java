package simulator;

import java.util.*;

public enum EnumComChannel implements IComChannel {
	
	;
	
	private Object _value;
	private HashMap<String, Object> _values;
	
	EnumComChannel(HashMap<String, Object> values){
		_values = values;
	}
	
	public Object findAllowableValue(String key){
		return _values.get(key);
	}

	@Override
	public void set(Object value) {
		_value = value;
	}

	@Override
	public Object get() {
		return _value;
	}

}
