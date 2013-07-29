package simulator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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
	
	public ArrayList<Actor> getActors(){
		ArrayList<Actor> actors = new ArrayList<Actor>();
		NodeList actor_nodes = d.getElementsByTagName("actor");
		int size = actor_nodes.getLength();
		for(int index = 0; index < size; index++){
			Element actor_node = (Element)actor_nodes.item(index);
			Actor actor = new AnonymousActor();
			
			String name = getTextValue(actor_node,"name");
			String startState = getTextValue(actor_node,"startState");
			ArrayList<String> channels = getChannels(actor_node);
			
			ArrayList<State> states = getStates(actor_node, actor.getInternalVars());
			for(State state : states){
				actor.add(state);
				if(state.equals(startState))
					actor.startState(state);
			}
			actors.add(actor);
		}
		return actors;
	}

	private ArrayList<State> getStates(Element actor, ActorVariableWrapper internal_vars) {
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
			ArrayList<Transition> transitions = getTransitions(state_node, states, internal_vars);
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

	private ArrayList<Transition> getTransitions(Element state, ArrayList<State> states, ActorVariableWrapper internal_vars) {
		ArrayList<Transition> transitions = new ArrayList<Transition>();
		NodeList transition_nodes = state.getElementsByTagName("state");
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
			
			ComChannelList outputs = new ComChannelList();
			
			String endState = getTextValue(transition_node, "endState");
			Transition transition = new Transition(internal_vars, inputs, outputs, states.get(states.indexOf(endState))){
				@Override
				public boolean isEnabled(){
					
					return false;
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
