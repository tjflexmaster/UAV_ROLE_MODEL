package simulator;

import simulator.metrics.MetricContainer;


public class BooleanLayer implements IComLayer
{

  private String _name;
  private Boolean _value;
  private DataType _datatype = DataType.BOOLEAN;
  
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
  
  public void value(Boolean value)
  {
    _value = value;
    
    //Set metric vars
    _lastChangeTime = Simulator.getSim().getClockTime();
    _totalChanges++;
  }
  
  public void value(String value)
  {
    value(Boolean.parseBoolean(value));
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
  public void value(Object obj)
  {
    if ( obj != null )
      value(getBoolean(obj));
    else
      value((Boolean) obj);
  }
  
  @Override
  public DataType dataType()
  {
    return _datatype;
  }

  @Override
  public boolean isEqual(Object obj)
  {
    if ( _value != null )
      return _value == getBoolean(obj);
    else 
      return getBoolean(obj) == null;
  }

  @Override
  public boolean isNotEqual(Object obj)
  {
    return !isEqual(obj);
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
      return Boolean.parseBoolean((String) obj);
    else
      assert false : "Unrecognized object sent to BooleanLayer";
    
    return null;
  }

  //////////////ILayerMetrics///////////////////////
  public int _lastChangeTime = 0;
  public int _totalChanges = 0;
  
//  @Override
//  public double averageChangeRate()
//  {
//    return (double) (_totalChanges / Simulator.getSim().getClockTime());
//  }
//
//  @Override
//  public int lastTimeBetweenChange()
//  {
//    return Simulator.getSim().getClockTime() - _lastChangeTime;
//  }
//
//  @Override
//  public int numOfChanges()
//  {
//    return _totalChanges;
//  }
//
//  @Override
//  public boolean active()
//  {
//    return _value != null;
//  }

  @Override
  public void setMetrics(MetricContainer c)
  {
    // TODO Auto-generated method stub
    
  }
}
