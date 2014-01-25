package simulator;

public class BooleanLayer implements IComLayer
{

  private String _name;
  private Boolean _value;
  
  @SuppressWarnings("null")
  public BooleanLayer(String name)
  {
    _name = name;
    value((Boolean) null);
  }
  
  public BooleanLayer(String name, boolean value)
  {
    _name = name;
    value(value);
  }
  
  public BooleanLayer(String name, String value)
  {
    _name = name;
    value(value);
  }
  
  public void value(boolean value)
  {
    _value = value;
  }
  
  public void value(String value)
  {
    _value = Boolean.getBoolean(value);
  }
  
////////////////////////////////////////////////////////
  
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
  public boolean isEqual(Object obj)
  {
    return _value == getBoolean(obj);
  }

  @Override
  public boolean isNotEqual(Object obj)
  {
    return _value != getBoolean(obj);
  }

  @Override
  public boolean isGt(Object obj)
  {
    assert false : "Called isGt on BooleanLayer";
    return false;
  }

  @Override
  public boolean isLt(Object obj)
  {
    assert false : "Called isLt on BooleanLayer";
    return false;
  }

  @Override
  public boolean isGtOrEqual(Object obj)
  {
    assert false : "Called isGtOrEqual on BooleanLayer";
    return false;
  }

  @Override
  public boolean isLtOrEqual(Object obj)
  {
    assert false : "Called isLTOrEqual on BooleanLayer";
    return false;
  }

///////////////////////////////////////////////////
  
  @Override
  public String toString()
  {
    return _name + "=" + _value.toString();
  }

//////////////////////////////////////////////////

  private Boolean getBoolean(Object obj)
  {
    if ( obj instanceof Boolean )
      return (Boolean) obj;
    else if ( obj instanceof String )
      return Boolean.getBoolean((String) obj);
    else
      assert false : "Unrecognized object sent to BooleanLayer";
    
    return null;
  }
}
