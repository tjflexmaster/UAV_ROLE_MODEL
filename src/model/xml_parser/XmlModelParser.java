package model.xml_parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import simulator.ComChannel;
import simulator.ComChannelList;
import simulator.IState;
import simulator.ITransition;
import simulator.Memory;
import simulator.Range;
import simulator.State;
import simulator.TempComChannel;
import simulator.TempMemory;
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
			  actor.addInputChannel(m_team.getComChannel(channel_e.getValue()));
			}
			//Parse outputchannels
			Elements outputchannels = parseElementArray(actor_e, "outputchannels", "channel");
      for(int j=0; j < outputchannels.size(); j++) {
        Element channel_e = outputchannels.get(j);
        actor.addOutputChannel(m_team.getComChannel(channel_e.getValue()));
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
      
      //If this is the start state then add it
      if ( name.equals(startState_e.getValue()) )
        actor.setStartState(state);
      
      //Add to actor states
      actor.addState(state);
      
    }//end for
    
    //Now loop through the states and add the transitions
    for(int j=0; j < states_e.size(); j++) {
      Element state_e = states_e.get(j);
      State s = stateMap.get(state_e.getAttributeValue("name"));
      Elements transitions_e = state_e.getChildElements("transition");
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
        ComChannel<?> c = m_team.getComChannel(name);
        assert c != null : "Invalid transition input.  Team has no ComChannel named: " +
            name;
        t.addInput(c, new XMLPredicate<ComChannel<?>>(predicate, c, value));
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
        ComChannel<?> c = m_team.getComChannel(name);
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
	private ComChannel<?> parseComChannel(Element comchannel)
	{
	  String dataType = comchannel.getAttributeValue("dataType");
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
      case "EVENT":
        t = ComChannel.Type.EVENT;
        break;
      default:
        assert false: "Unrecognized channel type. " + type;
        break;
    }
    
    ComChannel<?> c = null;
    switch(dataType) {
      case "Integer":
        c = new ComChannel<Integer>(name, t, source, target);
        break;
      case "Boolean":
        c = new ComChannel<Boolean>(name, t, source, target);
        break;
      case "String":
        c = new ComChannel<String>(name, t, source, target);
        break;
      default:
        assert false:"Unrecognized channel type: " + dataType + ""
            + " Use one of the following (String, Integer, Boolean)";
    }//end switch
    
    return c;
	}
	
	/**
	 * Create a Memory Object from xml.
	 * ex: <memory name="SEARCH_AREAS" dataType="Integer">0</memory>
	 * @param memory
	 * @return
	 */
	private Memory<?> parseMemory(Element memory)
	{
	  String name = memory.getAttributeValue("name");
    String type = memory.getAttributeValue("dataType");
    //Check for a null value
    Elements nullElements = memory.getChildElements("null");
    Object obj = null;
    if (nullElements.size() <= 0) {
      String value = memory.getValue();
      obj = XMLDataTypes.getObject(type, value);
    }
    
    Memory<?> m = null;
    switch(type) {
      case "Integer":
        m = new Memory<Integer>(name, (Integer) obj);
        break;
      case "Boolean":
        m = new Memory<Boolean>(name, (Boolean) obj);
        break;
      case "String":
        m = new Memory<String>(name, (String) obj);
        break;
      default:
        assert false:"Unrecognized memory type: " + type + ""
            + " Use one of the following (String, Integer, Boolean)";
    }//end switch
    return m;
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
	  int minDur = Integer.parseInt(transition.getAttributeValue("durationMin"));
	  int maxDur = Integer.parseInt(transition.getAttributeValue("durationMax"));
	  int priority = Integer.parseInt(transition.getAttributeValue("priority"));
	  
	  //Get end State
	  Element endState_e = transition.getChildElements("endState").get(0);
	  IState endState = null;
	  String endStateName = endState_e.getValue();
	  for(IState s : actor.getStates()) {
	    if ( s.getName().equals(endStateName) )
	      endState = s;
	  }
	  assert endState!=null:"Invalid transition end state. Actor has no state:" + 
	      endState_e.getValue();
	  
    //Create the transition
	  XMLTransition t = new XMLTransition(actor, (State) endState, 
	      new Range(minDur, maxDur), priority);
	  
	  //Parse description
	  Elements description_e = transition.getChildElements("description");
	  if ( description_e.size() >= 1 )
	    t.description(description_e.get(0).getValue());
	  
	  //Set the transition comchannel inputs
    Elements inputchannels = parseElementArray(transition, "inputs", "channel");
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
      ComChannel<?> c = actor.getInputComChannel(name);
      assert c != null : "Invalid transition input.  Actor has no input channel:" +
          name;
      t.addInput(c, new XMLPredicate<ComChannel<?>>(predicate, c, value));
    }
    
    //Set the transition memory inputs
    Elements memoryinputs = parseElementArray(transition, "inputs", "memory");
    for(int j=0; j < memoryinputs.size(); j++) {
      Element memory_e = memoryinputs.get(j);
      String name = memory_e.getAttributeValue("name");
      String predicate = memory_e.getAttributeValue("predicate");
      //check null
      Elements nullElements = memory_e.getChildElements("null");
      String value = null;
      if (nullElements.size() <= 0) {
        value = memory_e.getValue();
      }
      Memory<?> m = actor.getMemory(name);
      assert m != null : "Invalid transition input.  Actor has no memory:" +
          name;
      t.addInputMemory(m, new XMLPredicate<Memory<?>>(predicate, m, value));
    }
	  
    //Set the transition outputs
    Elements outputchannels = parseElementArray(transition, "outputs", "channel");
    for(int j=0; j < outputchannels.size(); j++) {
      Element channel_e = outputchannels.get(j);
      String name = channel_e.getAttributeValue("name");
      //check for a null value
      Elements nullElements = channel_e.getChildElements("null");
      String value = null;
      if (nullElements.size() <= 0) {
        value = channel_e.getValue();
      }
      ComChannel<?> c = actor.getOutputComChannel(name);
      assert c != null : "Invalid transition output.  Actor has no output channel:" +
          name;
      t.addOutput(new TempComChannel(c, value));
    }
    
    //Set the transition memory outputs
    Elements memoryoutputs = parseElementArray(transition, "outputs", "memory");
    for(int j=0; j < memoryoutputs.size(); j++) {
      Element memory_e = memoryoutputs.get(j);
      String name = memory_e.getAttributeValue("name");
      String action = memory_e.getAttributeValue("action");
      //check null
      Elements nullElements = memory_e.getChildElements("null");
      String value = null;
      if (nullElements.size() <= 0) {
        value = memory_e.getValue();
      }
      Memory<?> m = actor.getMemory(name);
      assert m != null : "Invalid transition input.  Actor has no memory:" +
          name;
      t.addOutputMemory(new TempMemory(m, value, action));
    }
	  return t;
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
