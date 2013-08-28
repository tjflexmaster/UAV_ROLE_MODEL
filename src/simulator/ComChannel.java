package simulator;

import simulator.ComChannel.Type;
import simulator.Metric.MetricEnum;

public class ComChannel<T> {
	public enum Type
	{
		VISUAL,
		AUDIO,
		DATA
	}
	
	T _value;
	String _name;
	Type _type;
	
	public ComChannel(String name, Type type)
	{
		_name = name;
		_type = type;
	}
	
	public ComChannel(String name, T value, Type type)
	{
		_name = name;
		_type = type;
		_value = value;
	}
	
	
	@SuppressWarnings("unchecked")
	public void set(Object value) 
	{
//		Simulator.getSim().addMetric(MetricEnum.CHANNEL_FIRE_A, _name);
//		assert (value instanceof classType):"Invalid ComChannel datatype.";
		_value = (T) value;
	}
	
	public T value()
	{
		Object data = _value;
//		if(data != null
//				|| (data instanceof Boolean && (Boolean)data)
//				|| (data instanceof Integer && (Integer)data != 0)){
//			if(_type == Type.AUDIO)
//				Simulator.getSim().addMetric(MetricEnum.CHANNEL_ACTIVE_A, _name);
//			else if(_type == Type.VISUAL)
//				Simulator.getSim().addMetric(MetricEnum.CHANNEL_ACTIVE_V, _name);
//			else if (_type == Type.DATA)
//				Simulator.getSim().addMetric(MetricEnum.CHANNEL_ACTIVE_D, _name);
//			else
//				Simulator.getSim().addMetric(MetricEnum.CHANNEL_ACTIVE_O, _name);
//		}else{
//			if(_type == Type.AUDIO)
//				Simulator.getSim().addMetric(MetricEnum.CHANNEL_INACTIVE_A, _name);
//			else if(_type == Type.VISUAL)
//				Simulator.getSim().addMetric(MetricEnum.CHANNEL_INACTIVE_V, _name);
//			else if (_type == Type.DATA)
//				Simulator.getSim().addMetric(MetricEnum.CHANNEL_INACTIVE_D, _name);
//			else
//				Simulator.getSim().addMetric(MetricEnum.CHANNEL_INACTIVE_O, _name);
//		}
		return _value;
	}
	
	public String name()
	{
		return _name;
	}
	
	public boolean isEqual(T value)
	{
		return _value.equals(value);
	}

	@Override
	public boolean equals(Object obj)
	{
		if ( obj == this )
			return true;

		if (obj instanceof ComChannel) {
			if ( ((ComChannel<?>) obj).name().equals(this._name) )
				return true;
		}
		if (obj instanceof String) {
			if ( ((String) obj).equals(this._name) )
				return true;
		}
		
		
		return false;
	}
	
	@Override
	public int hashCode()
	{
		return _name.hashCode();
	}
	
	@Override
	public String toString(){
		if(_value == null){
			return "null";
		}
		return _value.toString();
	}

	public Type type() {
		return _type;
	}
}
