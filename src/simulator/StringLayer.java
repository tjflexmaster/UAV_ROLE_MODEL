package simulator;

public class StringLayer implements IComLayer
{
  
  private String _name;
  private String _value;
  private IComLayer.DataType _datatype = DataType.STRING;
  
  public StringLayer(String name)
  {
    _name = name;
    value(null);
  }
  
  public StringLayer(String name, String value)
  {
    _name = name;
    value(value);
  }
  
  public void value(String value)
  {
    if ( value.equals("") )
      _value = null;
    else
      _value = value;
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
      value(getString(obj));
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
    String temp = getString(obj);
    return _value.equals(temp);
  }

  @Override
  public boolean isNotEqual(Object obj)
  {
    return !isEqual(obj);
  }

  @Override
  public boolean isGt(Object obj)
  {
    assert false : "Called isGt on StringLayer";
    return false;
  }

  @Override
  public boolean isLt(Object obj)
  {
    assert false : "Called isLt on StringLayer";
    return false;
  }

  @Override
  public boolean isGtOrEqual(Object obj)
  {
    assert false : "Called isGtOrEqual on StringLayer";
    return false;
  }

  @Override
  public boolean isLtOrEqual(Object obj)
  {
    assert false : "Called isLtOrEqual on StringLayer";
    return false;
  }
  
  ///////////////////////////////////////////////////
  
  @Override
  public String toString()
  {
    return _name + "=" + _value;
  }
  
  //////////////////////////////////////////////////
  
  private String getString(Object obj)
  {
    if ( obj instanceof String )
      return (String) obj;
    else if ( obj instanceof Integer )
      return ((Integer) obj).toString();
    else if ( obj instanceof Boolean )
      return ((Boolean) obj).toString();
    else
      assert false : "Unrecognized object sent to StringLayer";
    
    return null;
  }

}
