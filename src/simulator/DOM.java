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
						case "int":
							new_channel = new ComChannel<Integer>(channel.getAttribute("name"), type);
							break;
						case "bool":
							new_channel = new ComChannel<Boolean>(channel.getAttribute("name"), type);
							break;
						case "string":
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
		ArrayList<Actor> actors = getActors(team.getComChannels());
		for(Actor a : actors) {
			team.addActor(a);
		}
		
		
		//Create Events
		
		return team;
	}
	
	public ArrayList<Actor> getActors(ComChannelList all_coms){
		ArrayList<Actor> actors = new ArrayList<Actor>();
		NodeList actor_nodes = d.getElementsByTagName("actor");
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
			actors.add(actor);
		}
		return actors;
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
			String title = getTextValue(state_node,"name");
			State state = new State(title);
			ArrayList<Transition> transitions = getTransitions(state_node, states, internal_vars, coms);
			for(Transition t : transitions){
				state.add(t);
			}
		}
		return states;
	}

	private ArrayList<String> getChannels(Element actor) {
		// TODO Auto-generated method stub
		return null;
	}

	private ArrayList<Transition> getTransitions(Element state, ArrayList<State> states, ActorVariableWrapper internal_vars, ComChannelList coms) {
		ArrayList<Transition> transitions = new ArrayList<Transition>();
		NodeList transition_nodes = state.getElementsByTagName("transition");
		int size = transition_nodes.getLength();
		for(int index = 0; index < size; index++){
			Element transition_node = (Element) transition_nodes.item(index);
			Element duration = (Element) transition_node.getElementsByTagName("durationRange").item(0);
			int max = Integer.parseInt(getTextValue(duration,"max"));
			int min = Integer.parseInt(getTextValue(duration,"min"));
			int priority = Integer.parseInt(getTextValue(transition_node,"priority"));
			double probability = Double.parseDouble(getTextValue(transition_node, "probability"));

			ComChannelList inputs = new ComChannelList();
			NodeList input_nodes = transition_node.getElementsByTagName("input");
			final HashMap<ComChannel,DataComparator> input_prereqs = new HashMap<ComChannel,DataComparator>();
			final HashMap<String, DataComparator> internal_prereqs = new HashMap<String, DataComparator>();
			int input_size = input_nodes.getLength();
			for(int sub_index = 0; sub_index < input_size; sub_index++){
				Element input = (Element) input_nodes.item(index);
				String data_type = input.getAttribute("dataType");
				String predicate = input.getAttribute("predicate");
				String data = getTextValue(input, "value");
				String source = ((Element)input.getElementsByTagName("source").item(0)).getAttribute("type");
				String source_name = ((Element)input.getElementsByTagName("source").item(0)).getAttribute("name");
				DataComparator prereq = null;
				switch(predicate){
				case "eq":
					prereq = new DataComparator(data, data_type){
						@Override
						public boolean isTrue(Object o){
							return o.equals(data);
						}
					};
					break;
				case "gt":prereq = new DataComparator(data, data_type){
					@Override
					public boolean isTrue(Object o){
						return (int) o > (int)data;
					}
				};
					break;
				case "lt":prereq = new DataComparator(data, data_type){
					@Override
					public boolean isTrue(Object o){
						return (int) o < (int)data;
					}
				};
					break;
				case "neq":prereq = new DataComparator(data, data_type){
					@Override
					public boolean isTrue(Object o){
						return !o.equals(data);
					}
				};
					break;
				case "gteq":prereq = new DataComparator(data, data_type){
					@Override
					public boolean isTrue(Object o){
						return (int) o >= (int)data;
					}
				};
					break;
				case "lteq":prereq = new DataComparator(data, data_type){
					@Override
					public boolean isTrue(Object o){
						return (int) o <= (int)data;
					}
				};
					break;
				default:
					assert (true): "bad predicate within the transition";
				}
				if(source.equals("channel")){
					ComChannel next_input = coms.get(source_name);
					assert(next_input != null):"Missing a ComChannel in the transitions";
					inputs.add(next_input);
					
					input_prereqs.put(next_input, prereq);
				} else {
					internal_prereqs.put(source_name, prereq);
				}
			}
			ComChannelList outputs = new ComChannelList();
			NodeList output_nodes = transition_node.getElementsByTagName("input");
			final HashMap<ComChannel,Object> temp_out = new HashMap<ComChannel,Object>();
			final HashMap<String,Object> temp_internals = new HashMap<String,Object>();
			int output_size = output_nodes.getLength();
			for(int sub_index = 0; sub_index < output_size; sub_index++){
				Element output = (Element) output_nodes.item(index);
				String data_type = output.getAttribute("dataType");
				String predicate = output.getAttribute("predicate");
				String data = getTextValue(output, "value");
				String source = ((Element)output.getElementsByTagName("source").item(0)).getAttribute("type");
				String source_name = ((Element)output.getElementsByTagName("source").item(0)).getAttribute("name");
				if(source.equals("channel")){
					ComChannel next_output = coms.get(source_name);
					assert(next_output != null):"Missing a ComChannel in the transitions";
					outputs.add(next_output);
					
					switch(data_type){
					case "String":
						temp_out.put(next_output, data);
						break;
					case "Integer":
						temp_out.put(next_output, Integer.parseInt(data));
						break;
					case "Boolean":
						temp_out.put(next_output, Boolean.parseBoolean(data));
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
			
			String endState = getTextValue(transition_node, "endState");
			Transition transition = new Transition(internal_vars, inputs, outputs, states.get(states.indexOf(endState))){
				@Override
				public boolean isEnabled(){
					for(Entry<String, ComChannel<?>> input : _inputs.entrySet()){
						DataComparator prereq = input_prereqs.get(input.getValue());
						if(!prereq.isTrue(input.getValue().value()))
							return false;
					}
					for(Entry<String, Object> internal : this._internal_vars.getAllVariables().entrySet()){
						DataComparator prereq = internal_prereqs.get(internal.getKey());
						if(!prereq.isTrue(internal.getValue()))
							return false;
					}

					for(Entry<String, ComChannel<?>> output : _inputs.entrySet()){
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
	public String output(){
		DOMImplementationLS dom_implementation = (DOMImplementationLS) d.getImplementation();
		LSSerializer output = dom_implementation.createLSSerializer();
		return output.toString();
	}


}
