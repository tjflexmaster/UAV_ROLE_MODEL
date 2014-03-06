package model.xml_parser;


import java.util.Map.Entry;
import java.util.Vector;

import org.jfree.chart.JFreeChart;

import simulator.ComChannel;
import simulator.Event;
import simulator.IState;
import simulator.ITransition;
import simulator.metrics.MetricContainer;
import simulator.metrics.MetricDisplayPanel;

public class XMLEvent extends Event
{
  
  XMLEvent(String name)
  {
    _name = name;
  }

  @Override
  public IState getCurrentState()
  {
    return _currentState;
  }

  @Override
  public ITransition getEnabledTransition()
  {
    if ( _transition != null && _transition.isEnabled() )
    {
      return _transition;
    }
    return null;
  }
  
  public void setTransition(XMLEventTransition t)
  {
    //Validate outputs
    for(Entry<String, ComChannel> entry : t.getOutputChannels().entrySet()) 
    {
      if ( entry.getValue().type() != ComChannel.Type.EVENT ) {
        assert false : "Events are only allowed to affect ComChannels of type EVENT";
      }
    }
    
    _transition = t;
  }

  public String toString()
  {
    return _name;
  }
  
  @Override
  public Vector<JFreeChart> getCharts()
  {
    return new Vector<JFreeChart>();
  }

  @Override
  public Vector<MetricDisplayPanel> getPanels()
  {
    return new Vector<MetricDisplayPanel>();
  }
  
  @Override
  public void setMetrics(MetricContainer c)
  {
    // TODO Auto-generated method stub
    
  }
}
