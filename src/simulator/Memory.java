package simulator;

public class Memory<T>
{
  T _value;
  String _name;
  
  public Memory(String name, T value)
  {
    _name = name;
    _value = value;
  }

  public T value()
  {
    return _value;
  }
  
  public void value(T value)
  {
    _value = value;
  }
  
  public String name()
  {
    return _name;
  }
  
  @SuppressWarnings("unchecked")
  public void set(Object value) 
  {
    _value = (T) value;
  }
  
  @SuppressWarnings("unchecked")
  public void set(String value)
  {
    if ( _value instanceof Integer ) {
      _value = (T) (Object) Integer.parseInt(value);
    }
    else if ( _value instanceof Boolean ) {
      _value = (T) (Object) Boolean.parseBoolean(value);
    }
    else
      _value = (T) value;
  }
}
