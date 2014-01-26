package simulator;


public class TempComChannel
{
  ComChannel _channel;
  String _layer;
  Object _value;
    
  public TempComChannel(ComChannel channel, Object value)
  {
    init(channel, channel.name(), value);
  }
  
  public TempComChannel(ComChannel channel, String layer, Object value)
  {
    init(channel, layer, value);
  }
  
  public String layer()
  {
    return _layer;
  }
  
  public Object value()
  {
    return _value;
  }
  
  public ComChannel channel()
  {
    return _channel;
  }
  
  public void fire()
  {
    //Set the designated layer of the channel to this value
    IComLayer layer = _channel.getLayer(_layer);
    if ( layer == null )
      _channel.setLayer(ComLayerFactory.createLayer(_layer, _value));
    else {
      layer.value(_value);
      _channel.setLayer(_layer);
    }
  }

  private void init(ComChannel channel, String layer, Object value)
  {
    _channel = channel;
    _layer = layer;
    _value = value;
  }
}
