package simulator;

import simulator.metrics.IMetrics;
import simulator.metrics.MetricContainer;

public class Memory implements IMetrics
{
  IComLayer _layer;
  String _name;
  
  public Memory(String name, Object value)
  {
    _name = name;
    _layer = ComLayerFactory.createLayer(_name, value);
  }
  
  public Memory(String name, IComLayer.DataType dataType, Object value)
  {
    _name = name;
    _layer = ComLayerFactory.createLayer(_name, dataType);
    _layer.value(value);
  }

  public IComLayer layer()
  {
    return _layer;
  }
  
  public void layer(Object value)
  {
    _layer.value(value);
  }
  
  public String name()
  {
    return _name;
  }

  @Override
  public void setMetrics(MetricContainer c)
  {
    
    
  }
  
}
