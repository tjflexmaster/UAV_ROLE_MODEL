package simulator;


public class IntegerLayer implements IComLayer
{
  private String _name;
  private Integer _value;
  private DataType _datatype = DataType.INTEGER;
  
  public IntegerLayer(String name)
  {
    _name = name;
    value((Integer) null);
  }
  
  public IntegerLayer(String name, int value)
  {
    _name = name;
    value(value);
  }
  
  public IntegerLayer(String name, String value)
  {
    _name = name;
    value(value);
  }
  
  public void value(Integer value)
  {
    _value = value;
  }
  
  public void value(String value)
  {
    _value = Integer.getInteger(value);
  }
  
  public void add(Object value)
  {
    _value += getInteger(value);
  }
  
  public void subtract(Object value)
  {
    _value -= getInteger(value);
  }
  
  //////////////////////////////////////////////////////

  @Override
  public String name()
  {
    return _name;
  }

  @Override
  public Object value()
  {
    return _value;
  }
  
  @Override
  public void value(Object obj)
  {
    if ( obj != null )
      value(getInteger(obj));
    else
      _value = null;
  }
  
  @Override
  public DataType dataType()
  {
    return _datatype;
  }

  @Override
  public boolean isEqual(Object obj)
  {
    return _value == getInteger(obj);
  }

  @Override
  public boolean isNotEqual(Object obj)
  {
    return !isEqual(obj);
  }

  @Override
  public boolean isGt(Object obj)
  {
    return _value > getInteger(obj);
  }

  @Override
  public boolean isLt(Object obj)
  {
    return _value < getInteger(obj);
  }

  @Override
  public boolean isGtOrEqual(Object obj)
  {
    return _value >= getInteger(obj);
  }

  @Override
  public boolean isLtOrEqual(Object obj)
  {
    return _value <= getInteger(obj);
  }

///////////////////////////////////////////////////
  
  @Override
  public String toString()
  {
  return _name + "=" + _value.toString();
  }

//////////////////////////////////////////////////

  private Integer getInteger(Object obj)
  {
    if ( obj instanceof Integer )
      return (Integer) obj;
    else if ( obj instanceof String )
      return Integer.getInteger((String) obj);
    else
      assert false : "Unrecognized object sent to IntegerLayer";
    
    return null;
  }
}