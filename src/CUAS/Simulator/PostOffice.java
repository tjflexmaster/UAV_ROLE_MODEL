package CUAS.Simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class PostOffice  {
	
	private HashMap<String,ArrayList<IData>> POBox;
	private HashMap<String, ArrayList<String>> _linked_poboxes;
	
	private HashMap<String, ArrayList<IData>> _temp_observations;
	private HashMap<String, ArrayList<IData>> _visible_observations;
	private HashMap<String, ArrayList<String>> _linked_observations;
	
	
	public PostOffice() {
		POBox = new HashMap<String,ArrayList<IData>>();
	}
	
	

	public void addOutput(IData data, String name){
		//Duplicate this data as needed
		ArrayList<String> targets = new ArrayList<String>();
		if ( _linked_poboxes.containsKey(name) ) {
			targets = _linked_poboxes.get(name);
		}
		targets.add(name);
		
		for( String target : targets ) {
			if(POBox.containsKey(target)){
				POBox.get(target).add(data);
			}else{
				ArrayList<IData> inputs = new ArrayList<IData>();
				inputs.add(data);
				POBox.put(target, inputs);
			}
		}
	}
	
	public void addOutputs(ArrayList<IData> data, String name){
		//Duplicate this data as needed
		ArrayList<String> targets = new ArrayList<String>();
		if ( _linked_poboxes.containsKey(name) ) {
			targets = _linked_poboxes.get(name);
		}
		targets.add(name);
		
		for( String target : targets ) {
			if(POBox.containsKey(target)){
				POBox.get(target).addAll(data);
			}else{
				ArrayList<IData> inputs = new ArrayList<IData>();
				inputs.addAll(data);
				POBox.put(target, inputs);
			}
		}
	}
	
	public void sendInput(){
		
		//Send direct output
		for(Map.Entry<String, ArrayList<IData>> pair : POBox.entrySet()){
			sim().addInput(pair.getKey(), pair.getValue());
			pair.getValue().clear();
		}
		
//		//Make observations visible
//		for(Map.Entry<String, ArrayList<IData>> pair : _temp_observations.entrySet()) {
//			if ( _visible_observations.containsKey(pair.getKey()) ) {
//				_visible_observations.get(pair.getKey()).addAll(pair.getValue());
//			} else {
//				_visible_observations.put(pair.getKey(), pair.getValue());
//			}
//		}
	}
	
	/**
	 * Force all input to the parent to be sent to the child
	 * @param parent
	 * @param child
	 */
	public void linkInput(String parent, String child)
	{
		if(_linked_poboxes.containsKey(parent)){
			_linked_poboxes.get(parent).add(child);
		}else{
			ArrayList<String> inputs = new ArrayList<String>();
			inputs.add(child);
			_linked_poboxes.put(parent, inputs);
		}
	}
	
	public void updateObservations()
	{
		_visible_observations.clear();
		
		_visible_observations = _temp_observations;
		
		_temp_observations.clear();
	}
	
	public void addObservation(IData data, String actor_name)
	{
		//Duplicate this data as needed
		ArrayList<String> targets = new ArrayList<String>();
		if ( _linked_observations.containsKey(actor_name) ) {
			targets = _linked_observations.get(actor_name);
		}
		targets.add(actor_name);
		
		for( String target : targets ) {
			if ( _temp_observations.containsKey(target) ) {
				_temp_observations.get(target).add(data);
			} else {
				ArrayList<IData> inputs = new ArrayList<IData>();
				inputs.add(data);
				_temp_observations.put(target, inputs);
			}
		}
	}
	
	public void addObservations(ArrayList<IData> data, String actor_name)
	{
		//Duplicate this data as needed
		ArrayList<String> targets = new ArrayList<String>();
		if ( _linked_observations.containsKey(actor_name) ) {
			targets = _linked_observations.get(actor_name);
		}
		targets.add(actor_name);
		
		for( String target : targets ) {
			if ( _temp_observations.containsKey(target) ) {
				_temp_observations.get(target).addAll(data);
			} else {
				ArrayList<IData> inputs = new ArrayList<IData>();
				inputs.addAll(data);
				_temp_observations.put(target, inputs);
			}
		}
	}
	
	public ArrayList<IData> getObservations(String actor_name)
	{
		if ( _visible_observations.containsKey(actor_name) ) {
			return _visible_observations.get(actor_name);
		} else {
			return new ArrayList<IData>();
		}
	}

	/**
	 * Observing the parent also returns the child observations
	 * @param parent
	 * @param child
	 */
	public void linkObservations(String parent, String child)
	{
		if(_linked_observations.containsKey(parent)){
			_linked_observations.get(parent).add(child);
		}else{
			ArrayList<String> inputs = new ArrayList<String>();
			inputs.add(child);
			_linked_observations.put(parent, inputs);
		}
	}


	/**
	 * Convenience method so that getInstance does not have to be called over and over
	 * @return
	 */
	protected Simulator sim()
	{
		return Simulator.getInstance();
	}

}
