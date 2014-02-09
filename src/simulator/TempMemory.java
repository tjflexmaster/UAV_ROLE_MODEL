package simulator;


public class TempMemory
{
  private Memory _memory;
  private Object _value;
  private String _action;
  
  public TempMemory(Memory memory, Object value)
  {
    _memory = memory;
    _value = value;
    _action = "";
  }
  
  public TempMemory(Memory memory, Object value, String action)
  {
    _memory = memory;
    _value = value;
    _action = action;
  }
  
  public void action(String action)
  {
    _action = action;
  }
  
  public String action()
  {
    return _action;
  }
  
  public Object value()
  {
    return _value;
  }
  
  public void value(Object value)
  {
    _value = value;
  }
  
  public Memory memory()
  {
    return _memory;
  }
  
  public void fire()
  {
    IComLayer layer = _memory.layer();
    
    //Perform actions
    if ( layer.dataType() == IComLayer.DataType.INTEGER) {
      if ( _action.equals("+") ) {
       ((IntegerLayer) layer).add(_value);
      } else if ( _action.equals("-") ) {
        ((IntegerLayer) layer).subtract(_value);
      } else
        _memory.layer(_value);
    } else
      _memory.layer(_value);
  }
  
}
