package simulator;

import simulator.ComChannel.Type;

public class ComChannel<T> {
	public enum Type
	{
		VISUAL,
		AUDIO,
		DATA,
		EVENT
	}
	
	T _value;
	String _name;
	Type _type;
	String _value_type;
	
	public ComChannel(String name, Type type, String value_type)
	{
		_name = name;
		_type = type;
		_value_type = value_type;
	}
	
	public ComChannel(String name, T value, Type type, String value_type)
	{
		_name = name;
		_type = type;
		_value = value;
		_value_type = value_type;
	}
	
	
	@SuppressWarnings("unchecked")
	public void set(Object value) 
	{
//		assert (value instanceof classType):"Invalid ComChannel datatype.";
		_value = (T) value;
	}
	
	public T value()
	{
		return _value;
	}
	
	public String name()
	{
		return _name;
	}
	
	public Type type()
	{
		return _type;
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

}
