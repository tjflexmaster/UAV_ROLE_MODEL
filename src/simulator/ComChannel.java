package simulator;


import java.util.HashMap;
import java.util.Map.Entry;

public class ComChannel {
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
	private HashMap<String, IComLayer> _layers = new HashMap<String, IComLayer>();
	
	public ComChannel(String name, Type type)
	{
		_name = name;
		_type = type;
		_source = "None";
		_target = "None";
	}
	
	public ComChannel(String name, Object obj, Type type)
	{
		_name = name;
		_type = type;
		setLayer(obj);
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
	
	public ComChannel(String name, Object obj, Type type, String source, String target)
	{
		_name = name;
		_type = type;
		setLayer(obj);
		_source = source;
		_target = target;
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
	
	public void setLayer(Object obj)
	{
	  _layers.put(_name, ComLayerFactory.createLayer(_name, obj));
	}
	
	public void setLayer(IComLayer layer)
	{
    _layers.put(layer.name(), layer);
	}
	
	public IComLayer getLayer()
	{
	  return _layers.get(_name);
	}
	
	public IComLayer getLayer(String name)
	{
	  if ( name != null && !name.isEmpty() )
	    return _layers.get(name);
	  else
	    return getLayer();
	}
	
	public HashMap<String, IComLayer> getLayers()
	{
	  //Return a copy
	  return new HashMap<String, IComLayer>(_layers);
	}
	
	public void clearLayers()
	{
	  for(Entry<String, IComLayer> e : _layers.entrySet()) {
	    e.getValue().value(null);
	  }
	}
	
	//If all layers are null then a channel is considered inactive
	public boolean isActive()
	{
	  for( Entry<String, IComLayer> e : _layers.entrySet() ) {
	    if ( e.getValue().value() != null )
	      return true;
	  }
	  return false;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if ( obj == this )
			return true;

		if (obj instanceof ComChannel) {
			if ( ((ComChannel) obj).name().equals(this._name) )
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
		for(Entry<String, IComLayer> e : _layers.entrySet()) {
		  value += e.getValue().toString() + ",";
		}
		return value;
	}

	public Type type() {
		return _type;
	}
}
