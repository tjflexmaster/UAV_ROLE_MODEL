package model.xml_parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Vector;

import simulator.Actor;
import simulator.ComChannel;
import simulator.ComChannelList;
import simulator.IComLayer;
import simulator.ITransition;
import simulator.Memory;
import simulator.MemoryList;
import simulator.Range;
import simulator.State;
import simulator.TempComChannel;
import simulator.TempMemory;
import simulator.metrics.IMetrics;
import simulator.metrics.MetricContainer;

public class XMLTransition implements ITransition
{
  
  protected ComChannelList _inputs = new ComChannelList();
  protected ArrayList<TempComChannel > _outputs = new ArrayList<TempComChannel>();
  protected MemoryList _memory_input = new MemoryList();
  protected ArrayList<TempMemory > _memory_output = new ArrayList<TempMemory>();
  private Range _range;
  private State _endState;
  private int _priority;
  private double _probability;
  
  private int _transition_number;
  
  private Vector<XMLPredicate<?>> _predicates = new Vector();
  
  private XMLActor _actor;
  
  /**
   * Description of this transition
   */
  private String _description;
  
  XMLTransition(XMLActor actor, State endState, Range range, int priority)
  {
    _inputs = new ComChannelList();
    _outputs = new ArrayList<TempComChannel >();
    _memory_input = new MemoryList();
    _memory_output = new ArrayList<TempMemory >();
    
    _actor = actor;
    _endState = endState;
    _range = range;
    _priority = priority;
    _probability = 1;
  }

  @Override
  public void fire()
  {
    //Set the memory outputs
    //TRICKY: Do this before firing the channels, this allows us to modify
    // memory and then send that memory value across a channel.
    for(TempMemory m : _memory_output) {
      m.fire();
    }
    
    //Set the channel outputs
    for(TempComChannel c : _outputs) {
      c.fire();
    }
    
    //Set the Actor state to the end state
    _actor.setState(_endState);
  }

  @Override
  public boolean isEnabled()
  {
    //Loop through each predicate and see if they are all true
    for(XMLPredicate<?> p : _predicates) {
      if ( !p.test() )
        return false;
    }
    return true;
  }

  @Override
  public Range getDurationRange()
  {
    return _range;
  }

  @Override
  public int priority()
  {
    return _priority;
  }

  @Override
  public ComChannelList getInputChannels()
  {
    return _inputs;
  }
  
  @Override
  public HashMap<String, IComLayer> getInputLayers()
  {
    HashMap<String, IComLayer> result = new HashMap<String, IComLayer>();
    for(XMLPredicate<?> p : _predicates) {
      if ( p.source() instanceof ComChannel ) {
        IComLayer l = ((ComChannel) p.source()).getLayer(p.layer());
        result.put(((ComChannel) p.source()).name(), l);
      }
    }
    
    return result;
  }
  
  @Override
  public MemoryList getInputMemory()
  {
    return _memory_input;
  }

  @Override
  public ComChannelList getOutputChannels()
  {
    ComChannelList result = new ComChannelList();
    for(TempComChannel t : _outputs) {
      result.add(t.channel());
    }
    return result;
  }

  @Override
  public ArrayList<TempComChannel > getTempOutputChannels()
  {
    return _outputs;
  }

  @Override
  public ArrayList<TempMemory > getTempOutputMemory()
  {
    return _memory_output;
  }

  @Override
  public void setIndex(int indexOf)
  {
    _transition_number = indexOf;

  }

  @Override
  public int getIndex()
  {
    return _transition_number;
  }

  
  /**
   * HELPER METHODS
   */
  
  public void addInput(ComChannel c, XMLPredicate<?> p)
  {
    _inputs.add(c);
    _predicates.add(p);
  }
  
  public void addOutput(TempComChannel c)
  {
    _outputs.add(c);
  }
  
  public void addInputMemory(Memory memory, XMLPredicate<?> p)
  {
    _memory_input.add(memory);
    _predicates.add(p);
  }
  
  public void addOutputMemory(TempMemory m)
  {
    _memory_output.add(m);
  }

  public void description(String desc)
  {
    _description = desc;
  }
  
  public String description()
  {
    return _description;
  }
  
  public String toString()
  {
    return _actor.name() + ": StartState: " + _actor.getCurrentState().getName() +
        " EndState: " + _endState.getName() + " Description: " + description();
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    XMLTransition other = (XMLTransition) obj;
    return toString().equals(other.toString());
  }
  

  @Override
  public int hashCode()
  {
    return toString().hashCode();
  }

  @Override
  public void setMetrics(MetricContainer c)
  {
    for(Entry<String, ComChannel> e : _inputs.entrySet()) {
      e.getValue().setMetrics(c);
    }
    
    for(XMLPredicate<?> p : _predicates) {
      if ( p.source() instanceof ComChannel ) {
        ComChannel channel = (ComChannel) p.source();
        if ( channel.isActive() ) {
          switch(channel.type()) {
            case AUDIO:
              c.numOfAudioLayers++;
              break;
            case VISUAL:
              c.numOfVisualLayers++;
              break;
          }//end switch
        }
      }
    }//end for
    
    Vector<String> tempNames = new Vector<String>();
    for(TempComChannel t : _outputs) {
      if ( !tempNames.contains(t.channel().name()) ) {
        c.numOfChannelOutputs++;
        tempNames.add(t.channel().name());
      }
    }
    c.numOfLayerOutputs += _outputs.size();
    c.numOfMemoryOutputs += _memory_output.size();
    
    if ( isEnabled() )
      c.numOfEnabledTransitions++;
  }

}
