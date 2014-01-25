package simulator;


public class TempComChannel
{
  ComChannel<?> _channel;
  String _value;
  String _layer;
    
  public TempComChannel(ComChannel<?> channel, String temp_val)
  {
    _channel = channel;
    value(temp_val);
    layer("");
  }
  
  public TempComChannel(ComChannel<?> channel, String temp_val, String layer)
  {
    _channel = channel;
    value(temp_val);
    layer(layer);
  }
  
  public String value()
  {
    return _value;
  }
  
  public void value(String value)
  {
    _value = value;
  }
  
  public String layer()
  {
    return _layer;
  }
  
  public void layer(String layer)
  {
    if ( layer == "" )
      _layer = null;
    else
      _layer = layer;
  }
  
  public ComChannel<?> channel()
  {
    return _channel;
  }
  
  public void fire()
  {
    //Set the designated layer of the channel to this value
    _channel.addLayer( _layer, _value);
  }

}
