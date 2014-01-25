package simulator;


import java.util.HashMap;
import java.util.Map.Entry;

public class ComChannel<T> {
	public enum Type
	{
		VISUAL,
		AUDIO,
		DATA,
		EVENT
	}
	
//	private T _value;
	private String _name;
	private Type _type;
	private String _source;
	private String _target;
	private HashMap<String, T> _layers = new HashMap<String, T>();
	
	public ComChannel(String name, Type type)
	{
		_name = name;
		_type = type;
		_source = "None";
		_target = "None";
	}
	
	public ComChannel(String name, T value, Type type)
	{
		_name = name;
		_type = type;
//		_value = value;
		value(value);
		_source = "None";
		_target = "None";
	}
	
	public ComChannel(String name, Type type, String source, String target)
	{
		_name = name;
		_type = type;
		_source = source;
		_target = target;
	}
	
	public ComChannel(String name, T value, Type type, String source, String target)
	{
		_name = name;
		_type = type;
		value(value);
		_source = source;
		_target = target;
	}
	
	public void value(T value)
	{
	  _layers.put(_name, value);
	}
	
	public T value()
	{
	  return _layers.get(_name);
	}
	
	@SuppressWarnings("unchecked")
	public void set(Object value) 
	{
		value((T) value);
	}
	
	@SuppressWarnings("unchecked")
  public void set(String value)
	{
	  T temp_val = _layers.get(_name);
	  if ( temp_val instanceof Integer ) {
	    temp_val = (T) (Object) Integer.parseInt(value);
	  }
	  else if ( temp_val instanceof Boolean ) {
	    temp_val = (T) (Object) Boolean.parseBoolean(value);
	  }
	  else
	    temp_val = (T) value;
	  
	  _layers.put(_name, temp_val);
	}
	
	public String name()
	{
		return _name;
	}
	
	public String source()
	{
		return _source;
	}
	
	public String target()
	{
		return _target;
	}
	
	//Layers
	public int layerCount()
	{
	  return _layers.size();
	}
	
	public void addLayer(String name, T value)
	{
	  if ( name != null && name != "" )
	    _layers.put(name, value);
	  else
	    value(value);
	}
	
	@SuppressWarnings("unchecked")
  public void addLayer(String name, String value)
	{
	  if ( name != null && name != "" ) {
  	  T temp_val = _layers.get(_name);
  	  if ( temp_val instanceof Integer ) {
  	    temp_val = (T) (Object) Integer.parseInt(value);
      }
      else if ( temp_val instanceof Boolean ) {
        temp_val = (T) (Object) Boolean.parseBoolean(value);
      }
      else
        temp_val = (T) value;
  	  
  	  _layers.put(name, temp_val);
	  } else {
	    set(value);
	  }
	}
	
	public T getLayer(String name)
	{
	  return _layers.get(name);
	}
	
	public boolean isLayerEqual(String name, T value)
	{
	  if ( !_layers.containsKey(name) )
	    return false;
	  
	  return _layers.get(name).equals(value);
	}
	
	
	public boolean isEqual(T value)
	{
	  return isLayerEqual(_name, value);
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
		if(layerCount() <= 0){
			return "null";
		}
		String value = "";
		for(Entry<String, T> e : _layers.entrySet()) {
		  value += e.getKey().toString() + "=" + e.getValue().toString() + ",";
		}
		return value;
	}

	public Type type() {
		return _type;
	}
}
