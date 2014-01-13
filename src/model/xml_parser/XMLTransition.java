package model.xml_parser;

import java.util.ArrayList;
import java.util.Vector;

import simulator.ComChannel;
import simulator.ComChannelList;
import simulator.ITransition;
import simulator.Memory;
import simulator.MemoryList;
import simulator.Range;
import simulator.State;
import simulator.TempComChannel;
import simulator.TempMemory;

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
    //Set the channel outputs
    for(TempComChannel c : _outputs) {
      c.fire();
    }
    
    //Set the memory outputs
    for(TempMemory m : _memory_output) {
      m.fire();
    }
    
    //Set the Actor state to the end state
    _actor.setStartState(_endState);
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
  
  public void addInput(ComChannel<?> c, XMLPredicate<?> p)
  {
    _inputs.add(c);
    _predicates.add(p);
  }
  
  public void addOutput(TempComChannel c)
  {
    _outputs.add(c);
  }
  
  public void addInputMemory(Memory<?> memory, XMLPredicate<?> p)
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
}
