package simulator;

public class TempComChannel
{
  ComChannel<?> _channel;
  String _value;
    
  public TempComChannel(ComChannel<?> channel, String temp_val)
  {
    _channel = channel;
    _value = temp_val;
  }
  
  public String value()
  {
    return _value;
  }
  
  public void value(String value)
  {
    _value = value;
  }
  
  public ComChannel<?> channel()
  {
    return _channel;
  }
  
  public void fire()
  {
    //First convert the string value into the correct type for the comchannel
    _channel.set(_value);
  }

}
