package CUAS.Simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class PostOffice  {
	
	private HashMap<String,ArrayList<IData>> _inbound_pobox;
	private HashMap<String,ArrayList<IData>> _outbound_pobox;
	private HashMap<String, ArrayList<String>> _linked_poboxes;
	
	private HashMap<String, ArrayList<IData>> _temp_observations;
	private HashMap<String, ArrayList<IData>> _visible_observations;
	private HashMap<String, ArrayList<String>> _linked_observations;
	
	
	public PostOffice() {
		_inbound_pobox = new HashMap<String, ArrayList<IData>>();
		_outbound_pobox = new HashMap<String, ArrayList<IData>>();
		_linked_poboxes = new HashMap<String, ArrayList<String>>();
		
		_temp_observations = new HashMap<String, ArrayList<IData>>();
		_visible_observations = new HashMap<String, ArrayList<IData>>();
		_linked_observations = new HashMap<String, ArrayList<String>>();
	}
	
	

	public void addOutput(IData data, String name){
		//Duplicate this data as needed
		ArrayList<String> targets = new ArrayList<String>();
		if ( _linked_poboxes.containsKey(name) ) {
			targets = _linked_poboxes.get(name);
		}
		targets.add(name);
		
		for( String target : targets ) {
			if(_inbound_pobox.containsKey(target)){
				_inbound_pobox.get(target).add(data);
			}else{
				ArrayList<IData> inputs = new ArrayList<IData>();
				inputs.add(data);
				_inbound_pobox.put(target, inputs);
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
			if(_inbound_pobox.containsKey(target)){
				_inbound_pobox.get(target).addAll(data);
			}else{
				ArrayList<IData> inputs = new ArrayList<IData>();
				inputs.addAll(data);
				_inbound_pobox.put(target, inputs);
			}
		}
	}
	
	/**
	 * This method moves input from the inbound poboxes to the outbound poboxes
	 */
	public void processInboundData()
	{
		//Process Direct Input
		_outbound_pobox.clear();
		_outbound_pobox.putAll(_inbound_pobox);
		_inbound_pobox.clear();
		
		//Process Observations
		_visible_observations.clear();
		_visible_observations.putAll(_temp_observations);
		_temp_observations.clear();
	}
	
	/**
	 * Request input for a specific actor
	 * 
	 * @param actor_name
	 * @return
	 */
	public ArrayList<IData> getInput(String actor_name)
	{
		if ( _outbound_pobox.containsKey(actor_name) )
			return _outbound_pobox.get(actor_name);
		else
			return new ArrayList<IData>();
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
	
//	public void updateObservations()
//	{
//		_visible_observations.clear();
//		
//		_visible_observations.putAll(_temp_observations);
//		
//		_temp_observations.clear();
//	}
	
	public void addObservation(IData data, String actor_name)
	{
		//Duplicate this data as needed
		ArrayList<String> targets = new ArrayList<String>();
		if ( _linked_observations.containsKey(actor_name) ) {
			targets = _linked_observations.get(actor_name);
		}
		if(!targets.contains(actor_name))
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
