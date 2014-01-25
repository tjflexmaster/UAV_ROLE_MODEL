package simulator;


public class TempComChannel
{
  ComChannel _channel;
  IComLayer _layer;
    
  public TempComChannel(ComChannel channel, IComLayer layer)
  {
    _channel = channel;
    layer(layer);
  }
  
  public IComLayer layer()
  {
    return _layer;
  }
  
  public void layer(IComLayer layer)
  {
    _layer = layer;
  }
  
  public ComChannel channel()
  {
    return _channel;
  }
  
  public void fire()
  {
    //Set the designated layer of the channel to this value
    _channel.addLayer(_layer);
  }

}
