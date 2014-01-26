package simulator;

public class Memory
{
  IComLayer _layer;
  String _name;
  
  public Memory(String name, Object value)
  {
    _name = name;
    _layer = ComLayerFactory.createLayer(_name, value);
  }
  
  public Memory(String name, IComLayer.DataType dataType, Object value)
  {
    _name = name;
    _layer = ComLayerFactory.createLayer(_name, dataType);
    _layer.value(value);
  }

  public IComLayer layer()
  {
    return _layer;
  }
  
  public void layer(Object value)
  {
    _layer.value(value);
  }
  
  public String name()
  {
    return _name;
  }
  
//  @SuppressWarnings("unchecked")
//  public void set(Object value) 
//  {
//    _value = (T) value;
//  }
//  
//  @SuppressWarnings("unchecked")
//  public void set(String value)
//  {
//    if ( _value instanceof Integer ) {
//      _value = (T) (Object) Integer.parseInt(value);
//    }
//    else if ( _value instanceof Boolean ) {
//      _value = (T) (Object) Boolean.parseBoolean(value);
//    }
//    else
//      _value = (T) value;
//  }
}
