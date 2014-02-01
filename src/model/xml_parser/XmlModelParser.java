package model.xml_parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import simulator.ComChannel;
import simulator.ComChannelList;
import simulator.IState;
import simulator.ITransition;
import simulator.Memory;
import simulator.Range;
import simulator.Simulator;
import simulator.State;
import simulator.TempComChannel;
import simulator.TempMemory;
import simulator.Simulator.DebugMode;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;

public class XmlModelParser {

	private XMLTeam m_team;
	
	/** Constructor
	 * 
	 * @param xml_path
	 */
	public XmlModelParser(String xml_path)
	{
		
		try {
		  Builder parser = new Builder();
		  Document doc = parser.build(new File(xml_path));
//		  System.out.println(doc.toXML());
		  //This should print out the xml.
		  
		  //Get the Team
		  Element team_element = doc.getRootElement();
		  m_team = new XMLTeam(team_element.getAttribute("name").getValue());
		  
		  parseTeamChannels(team_element);
		  
		  parseActors(team_element);
		  
		  parseEvents(team_element);
		  
		}
		catch (ParsingException ex) {
		  System.err.println("Error parsing the model!");
		}
		catch (IOException ex) {
		  System.err.println("Cannot open xml string.");
		}
	}
	
	public XMLTeam getTeam()
	{
	  return m_team;
	}
	
	/**
	 * Helper methods for building the classes
	 */
	private void parseTeamChannels(Element team_e)
	{
	  //Setup the Team com channels
    Elements channels = parseElementArray(team_e, "channels", "channel");
    for(int i=0; i < channels.size(); i++) {
      Element channel_e = channels.get(i);
      m_team.addComChannel( parseComChannel(channel_e) );
    }//end for
	}
	
	private void parseActors(Element team_e)
	{
		Elements actors = parseElementArray(team_e, "actors", "actor");
		assert actors.size() >= 1 : "Actors are missing";
		for(int i=0; i < actors.size(); i++) {
			Element actor_e = actors.get(i);
			XMLActor actor = new XMLActor(actor_e.getAttributeValue("name"));
//			System.out.println(actor_e.toXML());
			
			//Parse out an Actors available channels and make sure they are
			//part of the team channels.
			Elements inputchannels = parseElementArray(actor_e, "inputchannels", "channel");
			for(int j=0; j < inputchannels.size(); j++) {
			  Element channel_e = inputchannels.get(j);
			  ComChannel c = m_team.getComChannel(channel_e.getValue());
			  assert c != null : "Input Channel("+channel_e.getValue()+") for Actor("+
			      actor.name()+") not defined";
			  actor.addInputChannel(c);
			}
			//Parse outputchannels
			Elements outputchannels = parseElementArray(actor_e, "outputchannels", "channel");
      for(int j=0; j < outputchannels.size(); j++) {
        Element channel_e = outputchannels.get(j);
        ComChannel c = m_team.getComChannel(channel_e.getValue());
        assert c != null : "Output Channel("+channel_e.getValue()+") for Actor("+
            actor.name()+") not defined in team DiTG";
        actor.addOutputChannel(c);
      }
      
      //Parse Memory
      Elements memories_e = actor_e.getChildElements("memory");
      for(int j=0; j < memories_e.size(); j++) {
        Element memory_e = memories_e.get(j);
        actor.addMemory( parseMemory(memory_e) );
      }
      
      //parse the states
      parseStates(actor, actor_e);
      
      //Add Actor to Team
			m_team.addActor(actor);
		}
	}
	
	private void parseStates(XMLActor actor, Element actor_e)
	{
	  //Parse startstate
    Element startState_e = actor_e.getFirstChildElement("startState");
    
    //Parse States
    Elements states_e = parseElementArray(actor_e, "states", "state");
    //First initialize all of the states
    HashMap<String, State> stateMap = new HashMap<String, State>();
    for(int j=0; j < states_e.size(); j++) {
      Element state_e = states_e.get(j);
      
      String name = state_e.getAttributeValue("name");
      State state = new State(name);
      stateMap.put(name, state);
      
      //Add to actor states
      actor.addState(state);
      
    //If this is the start state then set it
      if ( name.equals(startState_e.getValue()) )
        actor.setStartState(state);
      
    }//end for
    
    //Now loop through the states and add the transitions
    for(int j=0; j < states_e.size(); j++) {
      Element state_e = states_e.get(j);
      State s = stateMap.get(state_e.getAttributeValue("name"));
      Elements transitions_e = state_e.getChildElements("transition");
      //Let the user know that there are states with no transitions
      if ( transitions_e.size() <= 0 ) 
        System.out.println("No transitions in State("+s.getName()+
            ") for Actor("+actor.name()+")");
      for(int k=0; k < transitions_e.size(); k++) {
        Element transition_e = transitions_e.get(k);
        s.add((ITransition) parseTransition(transition_e, actor) );
      }
      
    }//end for
	}
	
	/**
	 * Create Event objects for the team from event xml
	 * ex: <events>
    <event name="NEW_SEARCH_EVENT" count="3">
      <transition>
          <inputs>
            <channel name="AUDIO_PS_MM" predicate="eq"></channel>
            <channel name="VISUAL_PS_MM" predicate="eq"></channel>
          </inputs>
          <outputs>
            <channel name="NEW_SEARCH_EVENT">NEW_SEARCH_EVENT</channel>
          </outputs>
      </transition>
    </event>
  </events>
	 * @param team_e
	 */
	private void parseEvents(Element team_e)
	{
	  Elements events = parseElementArray(team_e, "events", "event");
    for(int i=0; i < events.size(); i++) {
      Element event_e = events.get(i);
      XMLEvent event = new XMLEvent(event_e.getAttributeValue("name"));
      event.setEventCount(Integer.parseInt(event_e.getAttributeValue("count")));
      Element transition_e = event_e.getChildElements("transition").get(0);
      
      XMLEventTransition t = new XMLEventTransition();
      
      //Parse the transition description
      Elements description_e = transition_e.getChildElements("description");
      if ( description_e.size() >= 1 )
        t.description(description_e.get(0).getValue());
      
      //Set the transition comchannel inputs
      Elements inputchannels = parseElementArray(transition_e, "inputs", "channel");
      for(int j=0; j < inputchannels.size(); j++) {
        Element channel_e = inputchannels.get(j);
        String name = channel_e.getAttributeValue("name");
        String predicate = channel_e.getAttributeValue("predicate");
        //check for a null value
        Elements nullElements = channel_e.getChildElements("null");
        String value = null;
        if (nullElements.size() <= 0) {
          value = channel_e.getValue();
        }
        ComChannel c = m_team.getComChannel(name);
        assert c != null : "Invalid transition input.  Team has no ComChannel named: " +
            name;
        t.addInput(c, new XMLPredicate<ComChannel>(predicate, c, value));
      }
      
      //Parse outputchannels
      Elements outputchannels = parseElementArray(transition_e, "outputs", "channel");
      for(int j=0; j < outputchannels.size(); j++) {
        Element channel_e = outputchannels.get(j);
        String name = channel_e.getAttributeValue("name");
        //check for a null value
        Elements nullElements = channel_e.getChildElements("null");
        String value = null;
        if (nullElements.size() <= 0) {
          value = channel_e.getValue();
        }
        ComChannel c = m_team.getComChannel(name);
        assert c != null : "Invalid transition output.  Team has no ComChannel named: " +
            name;
        t.addOutput(new TempComChannel(c, value));
      }
      
      event.setTransition(t);
      
      m_team.addEvent(event, event.getEventCount());
    }
	}
	
	
	/**
	 * XML Parsing methods
	 */
	
	/**
	 * Helper method for extracting arrays from the xml
	 * @param parent
	 * @param parent_name
	 * @param child_name
	 * @return
	 */
	private Elements parseElementArray(Element parent,
									   String parent_name,
									   String child_name)
	{
		Element wrapper = parent.getChildElements(parent_name).get(0);
		Elements children = wrapper.getChildElements(child_name); 
		return children;
	}
	
	/**
	 * Create a ComChannel object from xml.
	 * ex: <channel name="AUDIO_PS_MM" type="AUDIO" source="ParentSearch" 
	 *   target="MissionManager" dataType="String"/>
	 * @param comchannel
	 * @return
	 */
	private ComChannel parseComChannel(Element comchannel)
	{
//	  String dataType = comchannel.getAttributeValue("dataType");
    String name = comchannel.getAttributeValue("name");
    String type = comchannel.getAttributeValue("type");
    String source = comchannel.getAttributeValue("source");
    String target = comchannel.getAttributeValue("target");
    
    ComChannel.Type t = null;
    switch(type) {
      case "AUDIO":
        t = ComChannel.Type.AUDIO;
        break;
      case "VISUAL":
        t = ComChannel.Type.VISUAL;
        break;
      case "DATA":
        t = ComChannel.Type.DATA;
        break;
      case "TACTILE":
        t = ComChannel.Type.TACTILE;
        break;
      case "EVENT":
        t = ComChannel.Type.EVENT;
        break;
      default:
        assert false: "Unrecognized channel type. " + type;
        break;
    }
    
    ComChannel c = new ComChannel(name, t, source, target);
    
    return c;
	}
	
	/**
	 * Create a Memory Object from xml.
	 * ex: <memory name="SEARCH_AREAS" dataType="Integer">0</memory>
	 * @param memory
	 * @return
	 */
	private Memory parseMemory(Element memory)
	{
	  String name = memory.getAttributeValue("name");
    String type = memory.getAttributeValue("dataType");
    assert type != null : "Memory: " + name + " does not declare a dataType";
    //Check for a null value
    Elements nullElements = memory.getChildElements("null");
    Object obj = null;
    if (nullElements.size() <= 0) {
      String value = memory.getValue();
      obj = XMLDataTypes.getObject(type, value);
    }
    
    return new Memory(name, obj);
	}
	
	/**
	 * Create a Transition object from xml.
	 * ex: <transition durationMin="1" durationMax="1" priority="10">
              <inputs>
                <channel name="NEW_SEARCH_EVENT" predicate=""></channel>
              </inputs>
              <outputs>
                <channel name="AUDIO_PS_MM">POKE</channel>
                <memory name="SEARCH_AREAS" action="+">1</memory>
              </outputs>
              <endState>POKE_MM</endState>
            </transition>
	 * @param transition
	 * @param actor
	 * @param states
	 * @return
	 */
	private XMLTransition parseTransition(Element transition, XMLActor actor)
	{
	  String durMin = transition.getAttributeValue("durationMin");
	  String durMax = transition.getAttributeValue("durationMax");
	  String transPriority = transition.getAttributeValue("priority");
	  assert durMin != null : "Missing durationMin in transition. Actor("+
	      actor.name()+")";
	  assert durMax != null : "Missing durationMax in transition. Actor("+
        actor.name()+")";
	  assert transPriority != null : "Missing priority in transition. Actor("+
        actor.name()+")";
	  int minDur=0;
	  int maxDur=0;
	  int priority=0;
	  try {
	    minDur = Integer.parseInt(durMin);
  	  maxDur = Integer.parseInt(durMax);
  	  priority = Integer.parseInt(transPriority);
	  } catch(Exception e) {
	    assert false : "Invalid transition values. Actor("+actor.name()+")" + e.getMessage();
	  }
	  
	  //Get end State
	  Elements endStateElements = transition.getChildElements("endState");
	  assert endStateElements.size() > 0 : "Missing transition endState node." +
	      "  Actor("+actor.name()+")";
	  Element endState_e = endStateElements.get(0);
	  IState endState = null;
	  String endStateName = endState_e.getValue();
	  for(IState s : actor.getStates()) {
	    if ( s.getName().equals(endStateName) )
	      endState = s;
	  }
	  assert endState!=null:"Invalid transition end state. Actor("+actor.name()+
	      ") has no state:" + endState_e.getValue();
	  
    //Create the transition
	  XMLTransition t = new XMLTransition(actor, (State) endState, 
	      new Range(minDur, maxDur), priority);
	  
	  //Parse description
	  Elements description_e = transition.getChildElements("description");
	  if ( description_e.size() >= 1 )
	    t.description(description_e.get(0).getValue());
	  
	  //Parse inputs
	  parseTransitionComChannelInputs(transition, actor, t);
	  parseTransitionMemoryInputs(transition, actor, t);
	  
	  //Parse outputs
	  parseTransitionComChannelOutputs(transition, actor, t);
	  parseTransitionMemoryOutputs(transition, actor, t);
    
	  return t;
	}
	
	private void parseTransitionComChannelInputs(Element transition, 
                                               XMLActor actor,
                                               XMLTransition t)
	{
	  //Set the transition comchannel inputs
    Elements inputchannels = parseElementArray(transition, "inputs", "channel");
    for(int j=0; j < inputchannels.size(); j++) {
      Element channel_e = inputchannels.get(j);
      String name = channel_e.getAttributeValue("name");
      String predicate = channel_e.getAttributeValue("predicate");
      String dataType = channel_e.getAttributeValue("dataType");
      
      //Get the real channel obj
      ComChannel c = actor.getInputComChannel(name);
      if ( c == null ) {
        //Allow actors to check their own output channels for input (clear channel)
        c = actor.getOutputComChannel(name);
      }
      assert c != null : "Invalid transition input.  Actor("+ actor.name()+
          ") has no input channel:" + name;
      
      //Parse layers if they exist
      Elements layerElements = channel_e.getChildElements("layer");
      //If no layer elements then assume value is meant for the default layer
      if ( layerElements.size() <= 0 ) {
        Elements nullElements = channel_e.getChildElements("null");
        Object obj = null;
        if (nullElements.size() <= 0) {
          //Convert string to object of specified type
          if ( dataType != null )
            obj = XMLDataTypes.getObject(dataType, channel_e.getValue());
          else
            obj = channel_e.getValue();
        }
        
        //Add to transition inputs
        t.addInput(c, new XMLPredicate<ComChannel>(predicate, c, obj));
        
      } else {
        //The channel has layers, parse each layer
        for(int k=0; k < layerElements.size(); k++) {
          Element layer_e = layerElements.get(k);
          String layer_name = layer_e.getAttributeValue("name");
          String layer_predicate = layer_e.getAttributeValue("predicate");
          assert layer_predicate != null : "Missing input predicate. Actor("+
              actor.name() +") Layer("+layer_name+")";
          String layer_dataType = layer_e.getAttributeValue("dataType");
          Elements nullElements = channel_e.getChildElements("null");
          Object obj = null;
          if (nullElements.size() <= 0) {
            //Convert string to object of specified type
            if ( layer_dataType != null )
              obj = XMLDataTypes.getObject(layer_dataType, layer_e.getValue());
            else
              obj = layer_e.getValue();
          }
          
          //Check if this is the default layer
          if ( layer_name == null || layer_name.equals("") ) {
            layer_name = c.name();
          }
          
          //Add the layer input to the transition
          t.addInput(c, new XMLPredicate<ComChannel>(layer_predicate, c, obj, layer_name));
        }//end for layer
      }//end if
    }//end for channel
	}
	
	private void parseTransitionMemoryInputs(Element transition, 
                                           XMLActor actor,
                                           XMLTransition t)
	{
    //Set the transition memory inputs
    Elements memoryinputs = parseElementArray(transition, "inputs", "memory");
    for(int j=0; j < memoryinputs.size(); j++) {
      Element memory_e = memoryinputs.get(j);
      String name = memory_e.getAttributeValue("name");
      String predicate = memory_e.getAttributeValue("predicate");
      String dataType = memory_e.getAttributeValue("dataType");
      //check null
      Elements nullElements = memory_e.getChildElements("null");
      Object obj = null;
      if (nullElements.size() <= 0) {
        //Convert string to object of specified type
        if ( dataType != null )
          obj = XMLDataTypes.getObject(dataType, memory_e.getValue());
        else
          obj = memory_e.getValue();
      }
      Memory m = actor.getMemory(name);
      assert m != null : "Invalid transition input.  Actor("+actor.name()+
          ") has no memory:" + name;
      t.addInputMemory(m, new XMLPredicate<Memory>(predicate, m, obj));
    }
    
	}
	
	private void parseTransitionComChannelOutputs(Element transition,
	                                              XMLActor actor,
	                                              XMLTransition t)
	{
	  //Set the transition outputs
    Elements outputchannels = parseElementArray(transition, "outputs", "channel");
    for(int j=0; j < outputchannels.size(); j++) {
      Element channel_e = outputchannels.get(j);
      String name = channel_e.getAttributeValue("name");
      String dataType = channel_e.getAttributeValue("dataType");
      
      //Get the real channel obj
      ComChannel c = actor.getOutputComChannel(name);
      
      //If the channel doesn't exist then look for an incoming channel of type data
      boolean canSetValueToNull = false;
      if ( c == null ) {
        c = actor.getInputComChannel(name);
        if ( c != null && 
            (c.type() == ComChannel.Type.DATA ||
             c.type() == ComChannel.Type.EVENT))
          canSetValueToNull = true;
          
        assert c != null : "Invalid transition output.  Actor("+ actor.name() +
            ") has no output or input named channel:" + name;
      }
      
      
      //Parse layers if they exist
      Elements layerElements = channel_e.getChildElements("layer");
      //If no layer elements then assume value is meant for the default layer
      if ( layerElements.size() <= 0 ) {
        Elements nullElements = channel_e.getChildElements("null");
        Object obj = null;
        Memory mem = null;
        //Check that it is not being set to null
        if (nullElements.size() <= 0) {
          //Make sure that input data channels can only be set to null
          assert !canSetValueToNull : "Invalid output value for channel: "+c.name()+
            ".  Actor(" + actor.name() + ") can only set input data channels to null";
          
          //Check to see if we are outputing a memory value
          Elements memoryElements = channel_e.getChildElements("memory");
          if ( memoryElements.size() > 0 )
            mem = actor.getMemory(memoryElements.get(0).getAttributeValue("name"));
          else {
            //Convert string to object of specified type
            if ( dataType != null )
              obj = XMLDataTypes.getObject(dataType, channel_e.getValue());
            else
              obj = channel_e.getValue();
          }
        }
        
        //Add to transition inputs
        if ( mem != null )
          t.addOutput(new TempComChannel(c, mem));
        else
          t.addOutput(new TempComChannel(c, obj));
        
      } else {
        //The channel has layers, parse each layer
        for(int k=0; k < layerElements.size(); k++) {
          Element layer_e = layerElements.get(k);
          String layer_name = layer_e.getAttributeValue("name");
          String layer_dataType = layer_e.getAttributeValue("dataType");
          Elements nullElements = layer_e.getChildElements("null");
          Object obj = null;
          Memory mem = null;
          if (nullElements.size() <= 0) {
            //Check to see if we are outputing a memory value
            Elements memoryElements = layer_e.getChildElements("memory");
            if ( memoryElements.size() > 0 )
              mem = actor.getMemory(memoryElements.get(0).getAttributeValue("name"));
            else {
              //Convert string to object of specified type
              if ( layer_dataType != null )
                obj = XMLDataTypes.getObject(layer_dataType, layer_e.getValue());
              else
                obj = layer_e.getValue();
            }
          }
          
          //Check if this is the default layer
          if ( layer_name == null || layer_name.equals("") ) {
            layer_name = c.name();
          }
          
        //Add to transition inputs
          if ( mem != null )
            t.addOutput(new TempComChannel(c, layer_name, mem));
          else
            t.addOutput(new TempComChannel(c, layer_name, obj));
          
        }//end for layer
      }//end if
    }//end for channel
	}
	
	
	private void parseTransitionMemoryOutputs(Element transition,
                                            XMLActor actor,
                                            XMLTransition t)
	{
	  //Set the transition memory outputs
    Elements memoryoutputs = parseElementArray(transition, "outputs", "memory");
    for(int j=0; j < memoryoutputs.size(); j++) {
      Element memory_e = memoryoutputs.get(j);
      String name = memory_e.getAttributeValue("name");
      String action = memory_e.getAttributeValue("action");
      String dataType = memory_e.getAttributeValue("dataType");
      //check null
      Elements nullElements = memory_e.getChildElements("null");
      Object obj = null;
      if (nullElements.size() <= 0) {
        //Convert string to object of specified type
        if ( dataType != null )
          obj = XMLDataTypes.getObject(dataType, memory_e.getValue());
        else
          obj = memory_e.getValue();
      }
      Memory m = actor.getMemory(name);
      assert m != null : "Invalid transition input.  Actor("+actor.name()+
          ") has no memory:" + name;
      t.addOutputMemory(new TempMemory(m, obj, action));
    }
	}
	
	/**
	 * Return a com channel enum
	 * @param type
	 * @return
	 */
	private ComChannel.Type getComChannelType(String type)
	{
		if ( type.equals("visual") )
			return ComChannel.Type.VISUAL;
		else if ( type.equals("audio") )
			return ComChannel.Type.AUDIO;
		else
			return ComChannel.Type.DATA;
		
	}
	
}
