package simulator;


public class TempComChannel
{
  ComChannel _channel;
  String _layer;
  Object _value;
  Memory _memory;
    
  public TempComChannel(ComChannel channel, Object value)
  {
    init(channel, channel.name(), value);
  }
  
  public TempComChannel(ComChannel channel, String layer, Object value)
  {
    init(channel, layer, value);
  }
  
  public TempComChannel(ComChannel channel, Memory memory)
  {
    initMemory(channel, channel.name(), memory);
  }
  
  public TempComChannel(ComChannel channel, String layer, Memory memory)
  {
    initMemory(channel, layer, memory);
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
    
    //Set the value (Do we use memory value?)
    Object val = null;
    if ( _memory != null )
      val = _memory.layer().value();
    else
      val = _value;
    
    if ( layer == null )
      _channel.setLayer(ComLayerFactory.createLayer(_layer, val));
    else {
      layer.value(val);
      _channel.setLayer(layer);
    }
  }

  private void init(ComChannel channel, String layer, Object value)
  {
    _channel = channel;
    _layer = layer;
    _value = value;
    _memory = null;
  }
  
  private void initMemory(ComChannel channel, String layer, Memory memory)
  {
    _channel = channel;
    _layer = layer;
    _value = null;
    _memory = memory;
  }
}
