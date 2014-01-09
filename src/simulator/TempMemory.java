package simulator;

public class TempMemory
{
  private Memory<?> _memory;
  private String _value;
  private String _action;
  
  public TempMemory(Memory<?> memory, String temp_val)
  {
    _memory = memory;
    _value = temp_val;
    _action = "";
  }
  
  public TempMemory(Memory<?> memory, String temp_val, String action)
  {
    _memory = memory;
    _value = temp_val;
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
  
  public String value()
  {
    return _value;
  }
  
  public void value(String value)
  {
    _value = value;
  }
  
  public void fire()
  {
    if ( _action == "+" ) {
      _memory.set((Integer)_memory.value() + Integer.parseInt(_value));
    } else
      _memory.set(_value);
  }
}
