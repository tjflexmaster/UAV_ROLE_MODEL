package simulator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;


public class DOM {
	private Document d;
	String path;
	public DOM(String f){
		try {
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			File file = new File(f);
			path = file.getParent();
			d = db.parse(file);
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public DOM(){
		DocumentBuilder db;
		try {
			db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			d = db.newDocument();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ITeam getTeam()
	{
		//Create a team
		AnonymousTeam team = new AnonymousTeam();
		Element team_node = (Element) d.getElementsByTagName("team").item(0);
		assert team_node!=null : "Missing Team Element in xml";
		team.name(team_node.getAttribute("name"));
		
		//Create Team channels
		NodeList child_nodes = team_node.getChildNodes();
		for( int i=0; i < child_nodes.getLength(); i++ ) {
			if ( child_nodes.item(i).getNodeName() == "channels" ) {
				Element child = (Element) child_nodes.item(i);
				NodeList channel_nodes = child.getElementsByTagName("channel");
				for ( int j=0; j < channel_nodes.getLength(); j++ ) {
					Element channel = (Element) channel_nodes.item(j);
					
					//Verify the channel type
					ComChannel.Type type = null;
					switch(channel.getAttribute("type")) {
						case "audio":
							type = ComChannel.Type.AUDIO;
							break;
						case "visual":
							type = ComChannel.Type.VISUAL;
							break;
						case "event":
							type = ComChannel.Type.EVENT;
							break;
						case "data":
							type = ComChannel.Type.DATA;
							break;
						default:
							assert false : "Unknown channel type: " + channel.getAttribute("type");
							break;
					}
					
					//Create the new Channel
					ComChannel<?> new_channel = null;
					switch(channel.getAttribute("dataType")) {
						case "Integer":
							new_channel = new ComChannel<Integer>(channel.getAttribute("name"), type);
							break;
						case "Boolean":
							new_channel = new ComChannel<Boolean>(channel.getAttribute("name"), type);
							break;
						case "String":
							new_channel = new ComChannel<String>(channel.getAttribute("name"), type);
							break;
						default:
							assert false : "Unknown dataType: " + channel.getAttribute("dataType");
							break;
					}
					team.addComChannel(new_channel);
				}
			}
		}
		
		//Create Actors
		Element actors_node = (Element) team_node.getElementsByTagName("actors").item(0);
		ArrayList<Actor> actors = getActors(actors_node, team.getComChannels());
		for(Actor a : actors) {
			team.addActor(a);
		}
		
		
		//Create Events
		ArrayList<Event> events = getEvents(team_node, team.getComChannels());
		for(Event e : events) {
			team.addEvent(e, 1);
		}
		
		return team;
	}
	
	public ArrayList<Actor> getActors(Element actors_node, ComChannelList all_coms){
		ArrayList<Actor> actors = new ArrayList<Actor>();
		NodeList actor_nodes = actors_node.getElementsByTagName("actor");
		int size = actor_nodes.getLength();
		for(int index = 0; index < size; index++){
			Element actor_node = (Element)actor_nodes.item(index);
			Actor actor = new AnonymousActor();
			
			String name = getTextValue(actor_node,"name");
			String startState = getTextValue(actor_node,"startState");
			ArrayList<String> channels = getChannels(actor_node);
			ComChannelList coms = new ComChannelList();
			for(String channel : channels){
				coms.add(all_coms.get(channel));
			}
			ArrayList<State> states = getStates(actor_node, actor.getInternalVars(), coms);
			for(State state : states){
				actor.add(state);
				if(state.equals(startState))
					actor.startState(state);
			}
			Element sub_actors_node = (Element) actor_node.getElementsByTagName("subActors").item(0);
			ArrayList<Actor> sub_actors = getActors(sub_actors_node,coms);
			for(Actor sub_actor : sub_actors){
				actor.addSubActor(sub_actor);
			}
			actors.add(actor);
		}
		return actors;
	}
	
	private ArrayList<Event> getEvents(Element team, ComChannelList all_channels)
	{
		ArrayList<Event> team_events = new ArrayList<Event>();
		
		NodeList child_nodes = team.getChildNodes();
		for( int i=0; i < child_nodes.getLength(); i++ ) {
			if ( child_nodes.item(i).getNodeName() == "events" ) {								//found events nodes
				Element events = (Element) child_nodes.item(i);
				NodeList event_nodes = events.getElementsByTagName("event");
				for ( int j=0; j < event_nodes.getLength(); j++ ) {								//for each event
					Element event = (Element) event_nodes.item(j);
					String name = event.getAttribute("name");									//get event name
					int count = Math.max(0, Integer.parseInt(event.getAttribute("count")));		//get number of instances
					
					//Get Event com channels
					ArrayList<String> channels = getChannels(event);							//get com channels
					ComChannelList coms = new ComChannelList();
					for(String channel : channels){												//for each channel
						coms.add(all_channels.get(channel));									//add to all channels
					}
					
					//Get inputs and outputs
					NodeList inputnodes = event.getElementsByTagName("input");					//get input
					HashMap<ComChannel<?>, IPredicate> inputs = new HashMap<ComChannel<?>, IPredicate>();
					for( int k=0; k < inputnodes.getLength(); k++) {							//for each input
						Element input = (Element) inputnodes.item(k);							
						Element value = (Element) input.getElementsByTagName("value").item(0);	//get expected value
						ComChannel<?> c = coms.getChannel(input.getAttribute("name"));			//get channel name
						IPredicate p = getPredicate(value);										//get predicate
						inputs.put(c, p);														//add pred & chan to inputs map
					}
					
					Element output = (Element) event.getElementsByTagName("output").item(0);	//get outputs
					ComChannel<?> o = coms.getChannel(output.getAttribute("name"));				//get channel name
					Element o_value = (Element) output.getElementsByTagName("value").item(0);	//get value to assign
					Object value = getValue(o_value);
					
					Event e = new Event(name, count, inputs, o, value);							//form new event with name, instances, output & output value
					team_events.add(e);															//add event to the team
				}
				
			}//end if
		}//end for
		
		return team_events;
	}

	private ArrayList<State> getStates(Element actor, ActorVariableWrapper internal_vars, ComChannelList coms) {
		ArrayList<State> states = new ArrayList<State>();
		NodeList state_nodes = actor.getElementsByTagName("state");
		int size = state_nodes.getLength();
		for(int index = 0; index < size; index++){
			Element state_node = (Element) state_nodes.item(index);
			String title = getTextValue(state_node,"name");
			State state = new State(title);
			states.add(state);
		}
		for(int index = 0; index < size; index++){
			Element state_node = (Element) state_nodes.item(index);
			String title = state_node.getAttribute("name");
			State state = states.get(states.indexOf(title));
			ArrayList<Assertion> assertions = getAssertions(state_node, states, internal_vars, coms);
			for(Assertion assertion : assertions){
				state.addAssertion(assertion);
			}
			ArrayList<Transition> transitions = getTransitions(state_node, states, internal_vars, coms);
			for(Transition t : transitions){
				state.add(t);
			}
		}
		return states;
	}

	private ArrayList<Assertion> getAssertions(Element state_node, ArrayList<State> states, ActorVariableWrapper internal_vars, ComChannelList coms) {

		ArrayList<Assertion> assertions = new ArrayList<Assertion>();
		
		NodeList assertion_nodes = state_node.getElementsByTagName("assert");			//found assertions nodes
		for( int i=0; i < assertion_nodes.getLength(); i++ ) {							//for all assertions nodes							//for each assert
			Element assertion = (Element) assertion_nodes.item(i);
			
			//get inputs
			NodeList input_nodes = assertion.getElementsByTagName("input");				//get input
			HashMap<ComChannel<?>, IPredicate> inputs = new HashMap<ComChannel<?>, IPredicate>();
			for( int k=0; k < input_nodes.getLength(); k++) {							//for each input
				Element input = (Element) input_nodes.item(k);							
				Element value = (Element) input.getElementsByTagName("value").item(0);	//get expected value
				ComChannel<?> c = coms.getChannel(input.getAttribute("name"));			//get channel name
				IPredicate p = getPredicate(value);										//get predicate
				inputs.put(c, p);														//add pred & chan to inputs map
			}
			
			//get message
			String message = assertion.getAttribute("message");							//get message
		}
		
		return assertions;
	}

	private ArrayList<String> getChannels(Element actor) {
		ArrayList<String> channels = new ArrayList<String>();
		NodeList channel_nodes = ((Element)actor.getElementsByTagName("channels").item(0))
												.getElementsByTagName("channel");
		int size = channel_nodes.getLength();
		for(int index = 0; index < size; index++){
			Element channel = (Element)channel_nodes.item(index);
			channels.add(channel.getAttribute("name"));
		}
		return channels;
	}

	private ArrayList<Transition> getTransitions(Element state, ArrayList<State> states, ActorVariableWrapper internal_vars, ComChannelList coms) {
		ArrayList<Transition> transitions = new ArrayList<Transition>();
		NodeList transition_nodes = state.getElementsByTagName("transition");
		int size = transition_nodes.getLength();
		for(int index = 0; index < size; index++){
			Element transition_node = (Element) transition_nodes.item(index);
			int max = Integer.parseInt(transition_node.getAttribute("duration-min"));
			int min = Integer.parseInt(transition_node.getAttribute("duration-max"));
			int priority = Integer.parseInt(transition_node.getAttribute("priority"));
			double probability = Double.parseDouble(transition_node.getAttribute("probability"));
			
			//get inputs
			ComChannelList inputs = new ComChannelList();
			NodeList input_nodes = transition_node.getElementsByTagName("input");
			final HashMap<ComChannel<?>, IPredicate> input_prereqs = new HashMap<ComChannel<?>, IPredicate>();
			final HashMap<String, IPredicate> internal_prereqs = new HashMap<String, IPredicate>();
			int input_size = input_nodes.getLength();
			for(int sub_index = 0; sub_index < input_size; sub_index++){
				Element input = (Element) input_nodes.item(index);
				addInput(coms, inputs, input_prereqs, internal_prereqs, input);
			}
			
			//get outputs
			ComChannelList outputs = new ComChannelList();
			NodeList output_nodes = transition_node.getElementsByTagName("output");
			final HashMap<String,Object> temp_out = new HashMap<String,Object>();
			final HashMap<String,Object> temp_internals = new HashMap<String,Object>();
			int output_size = output_nodes.getLength();
			for(int sub_index = 0; sub_index < output_size; sub_index++){
				Element output = (Element) output_nodes.item(index);
				addOutput(internal_vars, coms, outputs, temp_out, temp_internals, output);
			}
			
			State end_state = getEndState(states, transition_node);
			Transition transition = new Transition(internal_vars, inputs, outputs, end_state, new Range(min,max), priority, probability){
				@Override
				public boolean isEnabled(){
					for(Entry<String, ComChannel<?>> input : _inputs.entrySet()){
						IPredicate prereq = input_prereqs.get(input.getValue());
						if(!prereq.evaluate(input.getValue().value()))
							return false;
					}
					for(Entry<String, Object> internal : this._internal_vars.getAllVariables().entrySet()){
						IPredicate prereq = internal_prereqs.get(internal.getKey());
						if(!prereq.evaluate(internal.getValue()))
							return false;
					}

					for(Entry<String, Object> output : temp_out.entrySet()){
						this.setTempOutput(output.getKey(), temp_out.get(output.getValue()));
					}
					for(Entry<String, Object> internal : temp_internals.entrySet()){
						this.setTempInternalVar(internal.getKey(), internal.getValue());
					}
					return true;
				}
			};
			
		}
		return transitions;
	}

	/**
	 * @param coms
	 * @param inputs
	 * @param input_prereqs
	 * @param internal_prereqs
	 * @param input
	 */
	private void addInput(ComChannelList coms, ComChannelList inputs, final HashMap<ComChannel<?>, IPredicate> input_prereqs, final HashMap<String, IPredicate> internal_prereqs, Element input) {
		String predicate = input.getAttribute("predicate");
		String data = getTextValue(input, "value");
		String source = input.getAttribute("type");
		String source_name = input.getAttribute("name");
		
		IPredicate p = getPredicate((Element) input.getElementsByTagName("value"));			//get predicate
		
		if(source.equals("channel")){														//inputs
			ComChannel<?> c = coms.getChannel(input.getAttribute("name"));					//get channel name
			input_prereqs.put(c, p);														//add pred & chan to inputs map
		} else {																			//memory
			internal_prereqs.put(source_name, p);											//add pred & chan to inputs map
		}
	}

	/**
	 * @param internal_vars
	 * @param coms
	 * @param outputs
	 * @param temp_out
	 * @param temp_internals
	 * @param output
	 */
	private void addOutput(ActorVariableWrapper internal_vars,
			ComChannelList coms, ComChannelList outputs,
			final HashMap<String, Object> temp_out,
			final HashMap<String, Object> temp_internals, Element output) {
		String data_type = output.getAttribute("dataType");
		String predicate = output.getAttribute("predicate");
		String data = getTextValue(output, "value");
		String source = output.getAttribute("type");
		String source_name = output.getAttribute("name");
		if(source.equals("channel")){
			ComChannel next_output = coms.get(source_name);
			assert(next_output != null):"Missing a ComChannel in the transitions";
			outputs.add(next_output);
			
			switch(data_type){
			case "String":
				temp_out.put(source_name, data);
				break;
			case "Integer":
				temp_out.put(source_name, Integer.parseInt(data));
				break;
			case "Boolean":
				temp_out.put(source_name, Boolean.parseBoolean(data));
				break;
			default:
				assert true: "Missing data type";
			}
		} else {
			assert(internal_vars.getVariable(source_name) != null):"Missing an internal variable in the transitions";
			
			switch(data_type){
			case "String":
				temp_internals.put(source_name, data);
				break;
			case "Integer":
				temp_internals.put(source_name, Integer.parseInt(data));
				break;
			case "Boolean":
				temp_internals.put(source_name, Boolean.parseBoolean(data));
				break;
			default:
				assert true: "Missing data type";
			}
		}
	}

	/**
	 * @param states
	 * @param transition_node
	 * @return
	 */
	private State getEndState(ArrayList<State> states, Element transition_node) {
		String endState = ((Element)transition_node.getElementsByTagName("endState").item(0)).getAttribute("name");
		State end_state = states.get(states.indexOf(endState));
		return end_state;
	}

	private String getTextValue(Element e, String tag) throws NullPointerException {
		Element titleElem = (Element)e.getElementsByTagName(tag).item(0);
		String value = titleElem.getTextContent();

		return value;
	}

	private String getTextValue(Element e, String tag, int i) throws NullPointerException {
		Element titleElem = (Element)e.getElementsByTagName(tag).item(i);
		String value = titleElem.getTextContent();

		return value;
	}
	
	private Element buildTextElement(String type, String text) {
		
		Element textElem = d.createElement(type);
		textElem.appendChild(d.createTextNode(text));
		return textElem;
	}

	private Element buildTextElement(String type, Integer value) {
		Element textElem = d.createElement(type);
		textElem.appendChild(d.createTextNode(Integer.toString(value)));
		return textElem;
	}
	
	private Object getValue(Element value)
	{
		Object data = null;
		switch(value.getAttribute("dataType")){
		case "String":
			data = value.getTextContent();
			break;
		case "Integer":
			data = Integer.parseInt(value.getTextContent());
			break;
		case "Boolean":
			data = Boolean.parseBoolean(value.getTextContent());
			break;
		default:
			assert true: "Missing data type";
		}
		
		return data;
	}
	
	private IPredicate getPredicate(Element value)
	{
		Object data = getValue(value);
		
		IPredicate predicate = null;
		switch(value.getAttribute("predicate")) {
			case "eq":
				predicate = new EqualPredicate(data);
				break;
			case "gt":
				predicate = new GreaterThanPredicate(data);
				break;
			case "lt":
				predicate = new LessThanPredicate(data);
				break;
			case "neq":
				predicate = new NotEqualPredicate(data);
				break;
			case "gteq":
				predicate = new GreaterThanEqualPredicate(data);
				break;
			case "lteq":
				predicate = new LessThanEqualPredicate(data);
				break;
			default:
				assert false : "Unknown predicate value";
				break;
		}
		
		return predicate;
	}

}
