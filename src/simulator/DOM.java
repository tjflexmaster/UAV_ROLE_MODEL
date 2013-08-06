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
	private NodeList _channel_nodes;
	private NodeList _memory_nodes;
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
		String name = team_node.getAttribute("name");
		team.name(name);
		
		NodeList child_nodes = team_node.getChildNodes();
		for( int i=0; i < child_nodes.getLength(); i++ ) {
			String nodeName = child_nodes.item(i).getNodeName();
			
			if ( nodeName == "channels" ) {//CHANNELS
				Element child = (Element) child_nodes.item(i);
				
				//Set channel nodes so data types can be found later
				_channel_nodes = child.getElementsByTagName("channel");
				
				for ( int j=0; j < _channel_nodes.getLength(); j++ ) {
					Element channel = (Element) _channel_nodes.item(j);
					
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
							new_channel = new ComChannel<Integer>(channel.getAttribute("name"), type, "Integer");
							break;
						case "Boolean":
							new_channel = new ComChannel<Boolean>(channel.getAttribute("name"), type, "Boolean");
							break;
						case "String":
							new_channel = new ComChannel<String>(channel.getAttribute("name"), type, "String");
							break;
						default:
							assert false : "Unknown dataType: " + channel.getAttribute("dataType");
							break;
					}
					team.addComChannel(new_channel);
				}
			} else if ( nodeName == "actors" ) {//ACTORS
				Element actors_node = (Element) child_nodes.item(i);

				ArrayList<Actor> actors = getActors(actors_node, team.getComChannels());
				for(Actor a : actors) {
					team.addActor(a);
				}
			} else if ( nodeName == "events" ) {//EVENTS
				Element events_node = (Element) child_nodes.item(i);
				
				ArrayList<Event> events = getEvents(events_node, team.getComChannels());
				for(Event e : events) {
					team.addEvent(e);
				}
			}
		}
		
		return team;
	}
	
	public ArrayList<Actor> getActors(Element actors_node, ComChannelList all_coms){
		ArrayList<Actor> actors = new ArrayList<Actor>();
		
		NodeList child_nodes = actors_node.getElementsByTagName("actor");
		for( int i=0; i < child_nodes.getLength(); i++ ) {
			String nodeName = child_nodes.item(i).getNodeName();
			
			if ( nodeName == "actor" ) {
				Element actor_node = (Element) child_nodes.item(i);
				BasicActor actor = new BasicActor();
				
				String name = actor_node.getAttribute("name").toString();
				actor.setName(name);
				String startState = ((Element) actor_node.getElementsByTagName("startState").item(0)).getAttribute("name").toString();
				
				//Create Actor channels
				ArrayList<String> channels = getChannels(actor_node);
				ComChannelList coms = new ComChannelList();
				for(String channel : channels){//why do we add all team channels to each actor? -rob
					coms.add(all_coms.get(channel));
				}
				
				//Create memory variables
//				ActorVariableWrapper vars = actor.getInternalVars();
				Element memory = (Element) actor_node.getElementsByTagName("memory").item(0);
				_memory_nodes = memory.getElementsByTagName("variable");
				int numberOfNodes = _memory_nodes.getLength();
				for( int j=0; j < numberOfNodes; j++ ) {
					Element var = (Element) _memory_nodes.item(j);
					String varName = var.getAttribute("name").toString();
					Object data = getMemoryValue(var);
//					vars.addVariable(varName, data);
					actor.addVariable(varName, data);
				}
				ArrayList<State> states = getStates(actor_node, actor.getInternalVars(), coms);
				for(State state : states) {
					actor.add(state);
					if( state.equals(startState) )
						actor.startState(state);
				}
				Element sub_actors_node = (Element) actor_node.getElementsByTagName("subActors").item(0);
				ArrayList<Actor> sub_actors = getActors(sub_actors_node,coms);
				for(Actor sub_actor : sub_actors){
					actor.addSubActor(sub_actor);
				}
				actors.add(actor);
			}
		}
		
		return actors;
	}
	
	private ArrayList<Event> getEvents(Element events_node, ComChannelList all_channels){
		ArrayList<Event> team_events = new ArrayList<Event>();
		
		NodeList child_nodes = events_node.getChildNodes();
		int numberOfNodes = child_nodes.getLength();
		for( int i=0; i<numberOfNodes; i++ ) {
			String nodeName = child_nodes.item(i).getNodeName();
			
			if ( nodeName.equals("event") ) {													//for each event
				Element event_node = (Element) child_nodes.item(i);
				String name = event_node.getAttribute("name");										//get event name
				int count = Math.max(0, Integer.parseInt(event_node.getAttribute("count")));			//get number of instances
				
				//CHANNELS
				ArrayList<String> channels = getChannels(event_node);								//get com channels
				ComChannelList coms = new ComChannelList();
				for ( String channel : channels ) {												//for each channel
					coms.add(all_channels.get(channel));										//add to all channels
				}
				
				//INPUTS
				NodeList inputnodes = event_node.getElementsByTagName("input");						//get input
				HashMap<ComChannel<?>, IPredicate> inputs = new HashMap<ComChannel<?>, IPredicate>();
				for ( int k=0; k < inputnodes.getLength(); k++) {
					Element input = (Element) inputnodes.item(k);
					ComChannel<?> c = coms.getChannel(input.getAttribute("name"));
					IPredicate p = getPredicate(input);
					inputs.put(c, p);
				}
				
				//OUTPUTS
				Element output = (Element) event_node.getElementsByTagName("output").item(0);
				ComChannel<?> o = coms.getChannel(output.getAttribute("name"));
				Object value = getValue(output);
				
				Event e = new Event(name, count, inputs, o, value);								//form new event with name, instances, output & output value
				team_events.add(e);																//add event to the team
				
			}//end if
		}//end for
		
		return team_events;
	}

	private ArrayList<State> getStates(Element actor, ActorVariableWrapper internal_vars, ComChannelList coms) {
		ArrayList<State> states = new ArrayList<State>();
		NodeList state_nodes = actor.getElementsByTagName("state");
		int numberOfNodes = state_nodes.getLength();
		for( int index=0; index<numberOfNodes; index++ ){
			Element state_node = (Element) state_nodes.item(index);
			String title = state_node.getAttribute("name").toString();
			State state = new State(title,1);
			states.add(state);
		}
		for( int index=0; index<numberOfNodes; index++ ){
			Element state_node = (Element) state_nodes.item(index);
			String title = state_node.getAttribute("name").toString();
			State state = states.get(index);
			
			//ASSERTIONS
			ArrayList<Assertion> assertions = getAssertions(state_node, states, internal_vars, coms);
			for( Assertion assertion : assertions ){
				state.addAssertion(assertion);
			}
			
			//TRANSITIONS
			ArrayList<Transition> transitions = getTransitions(state_node, states, internal_vars, coms);
			for( Transition t : transitions ){
				state.add(t);
			}
		}
		return states;
	}

	private ArrayList<Assertion> getAssertions(Element state_node, ArrayList<State> states, ActorVariableWrapper internal_vars, ComChannelList coms) {
		ArrayList<Assertion> assertions = new ArrayList<Assertion>();
		
		NodeList assertion_nodes = state_node.getElementsByTagName("assert");			//found assertions nodes
		int numberOfNodes = assertion_nodes.getLength();
		for( int i=0; i < numberOfNodes; i++ ) {							//for all assertions nodes							//for each assert
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
		
		NodeList channel_nodes = ((Element)actor.getElementsByTagName("channels").item(0)).getElementsByTagName("channel");
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
		int numberOfNodes = transition_nodes.getLength();
		for( int index=0; index<numberOfNodes; index++ ){
			Element transition_node = (Element) transition_nodes.item(index);
			int max = Integer.parseInt(transition_node.getAttribute("duration-min").toString());
			int min = Integer.parseInt(transition_node.getAttribute("duration-max").toString());
			
			int priority = 0;
			if( transition_node.getAttribute("priority")!="" )
				priority = Integer.parseInt(transition_node.getAttribute("priority").toString());
			
			double probability = 0.0;
			if ( transition_node.getAttribute("probability")!="" )
				probability = Double.parseDouble(transition_node.getAttribute("probability").toString());
			
			//get inputs
			ComChannelList inputs = new ComChannelList();
			NodeList input_nodes = transition_node.getElementsByTagName("input");
			final HashMap<ComChannel<?>, IPredicate> input_prereqs = new HashMap<ComChannel<?>, IPredicate>();
			final HashMap<String, IPredicate> internal_prereqs = new HashMap<String, IPredicate>();
			int numberOfInputNodes = input_nodes.getLength();
			for(int inputIndex = 0; inputIndex < numberOfInputNodes; inputIndex++){
				Element input = (Element) input_nodes.item(inputIndex);
				addInput(coms, inputs, input_prereqs, internal_prereqs, input);
			}
			
			//get outputs
			ComChannelList outputs = new ComChannelList();
//			NodeList output_nodes = ((Element)transition_node.getElementsByTagName("outputs").item(0)).getElementsByTagName("output");
			NodeList output_nodes = transition_node.getElementsByTagName("output");
			final HashMap<String,Object> temp_out = new HashMap<String,Object>();
			final HashMap<String,Object> temp_internals = new HashMap<String,Object>();
			int output_size = output_nodes.getLength();
			for(int outputIndex = 0; outputIndex < output_size; outputIndex++){
				Element output = (Element) output_nodes.item(outputIndex);
				addOutput(internal_vars, coms, outputs, temp_out, temp_internals, output);
			}
			
			State end_state = getEndState(states, transition_node);
			Transition transition = new Transition(internal_vars, inputs, outputs, end_state, new Range(min,max), priority, probability){
				@Override
				public boolean isEnabled(){
					for(Entry<String, ComChannel<?>> input : _inputs.entrySet()){
						IPredicate prereq = input_prereqs.get(input.getValue());
						if(prereq != null && !prereq.evaluate(input.getValue().value()))
							return false;
					}
					for(Entry<String, Object> internal : this._internal_vars.getAllVariables().entrySet()){
						IPredicate prereq = internal_prereqs.get(internal.getKey());
						if( prereq != null && !prereq.evaluate(internal.getValue()))
							return false;
					}

					for(Entry<String, Object> output : temp_out.entrySet()){
						this.setTempOutput(output.getKey(), temp_out.get(output.getKey()));
					}
					for(Entry<String, Object> internal : temp_internals.entrySet()){
						this.setTempInternalVar(internal.getKey(), internal.getValue());
					}
					return true;
				}
			};
			transitions.add(transition);
			
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
	private void addInput(ComChannelList coms, ComChannelList inputs, final HashMap<ComChannel<?>, IPredicate> input_prereqs,
			final HashMap<String, IPredicate> internal_prereqs, Element input) {
		String predicate = input.getAttribute("predicate");
		String data = getTextValue(input, "value");
		String source = input.getAttribute("type");
		String source_name = input.getAttribute("name");
		
		IPredicate p = getPredicate(input);													//get predicate
		
		if(source.equals("chan")){															//inputs
			ComChannel<?> c = coms.getChannel(source_name);									//get channel name
			inputs.add(c);																	//add channel to inputs
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
		String data = output.getAttribute("value");
		String type = output.getAttribute("type");
		String name = output.getAttribute("name");
		if(type.equals("chan")){
			ComChannel next_output = coms.get(name);
			assert(next_output != null):"Missing a ComChannel in the transitions";
			outputs.add(next_output);
			switch(next_output._value_type){
			case "String":
				temp_out.put(name, data);
				break;
			case "Integer":
				temp_out.put(name, Integer.parseInt(data));
				break;
			case "Boolean":
				temp_out.put(name, Boolean.parseBoolean(data));
				break;
			default:
				assert true: "Missing data type";
			}
		} else {
			assert(internal_vars.getVariable(name) != null):"Missing an internal variable in the transitions";
			String internal_data_type = internal_vars.getVariableType(name);
			switch(internal_data_type){
			case "String":
				temp_out.put(name, data);
				break;
			case "Integer":
				temp_out.put(name, Integer.parseInt(data));
				break;
			case "Boolean":
				temp_out.put(name, Boolean.parseBoolean(data));
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
		String endStateName = ((Element)transition_node.getElementsByTagName("endState").item(0)).getAttribute("name");
		for ( State nextState : states ) {
			if ( nextState.getName().equals(endStateName) ) {
				return nextState;
			}
		}
		assert true : "Couldn't find an end state.";
		return null;
	}

	private String getTextValue(Element e, String tag) throws NullPointerException {
		Element titleElem = (Element)e.getElementsByTagName(tag).item(0);
		if(titleElem == null)
			return "";
		
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
	
	private Object getMemoryValue(Element variable)
	{
		Object data = null;
		
		switch(variable.getAttribute("dataType")){
			case "String":
				data = variable.getTextContent();
				break;
			case "Integer":
				data = Integer.parseInt(variable.getTextContent());
				break;
			case "Boolean":
				data = Boolean.parseBoolean(variable.getTextContent());
				break;
			default:
				assert true: "Missing data type";
		}
		
		return data;
	}
	
	private Object getValue(Element input)
	{
		Object data = null;
		String datatype = null;
		
		String value = input.getAttribute("value");
		if ( value.isEmpty() )
			return null;
		else {
			String type = input.getAttribute("type");
			if ( type.equals("chan") )
				datatype = getChannelDataType(input);
			else if(type.equals(""))
				datatype = input.getAttribute("dataType");
			else
				datatype = getMemoryDataType(input);
			
			switch(datatype){
				case "String":
					
					data = input.getAttribute("value");
					break;
				case "Integer":
					data = Integer.parseInt(input.getAttribute("value"));
					break;
				case "Boolean":
					data = Boolean.parseBoolean(input.getAttribute("value"));
					break;
				default:
					assert true: "Missing data type";
			}
		}
		
		return data;
	}
	
	private IPredicate getPredicate(Element input)
	{
		Object data = getValue(input);
		
		IPredicate predicate = null;
		switch(input.getAttribute("predicate")) {
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
	
	private String getChannelDataType(Element input)
	{
		for ( int j=0; j<_channel_nodes.getLength(); j++ ) {
			Element e = (Element) _channel_nodes.item(j);
			String channelName = e.getAttribute("name").toString();
			String inputName = input.getAttribute("name").toString();
			if ( channelName.equals(inputName) ) {
				return e.getAttribute("dataType");
			}
		}
		assert true : "Couldn't get channel data type, maybe it wasn't added to the team?";
		return null;
	}
	
	private String getMemoryDataType(Element input)
	{
		for ( int j=0; j < _memory_nodes.getLength(); j++ ) {
			Element e = (Element) _memory_nodes.item(j);
			if ( e.getAttribute("name").equals(input.getAttribute("name")) ) {
				return e.getAttribute("dataType");
			}
		}
		assert true : "Couldn't get memory data type, maybe it wasn't added to the team?";
		return null;
	}

}
