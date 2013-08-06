package simulator;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Team implements ITeam {
	
	ArrayList<Event> _events = new ArrayList<Event>();
	ArrayList<IActor> _actors = new ArrayList<IActor>();
	public ComChannelList _com_channels;
	

	@Override
	public HashMap<IActor, ITransition> getActorTransitions() {
		HashMap<IActor, ITransition> result = new HashMap<IActor, ITransition>();
		for( IActor a : _actors ) {
			HashMap<IActor, ITransition> transitions = a.getTransitions();
			if(transitions != null)
				result.putAll(transitions);
		}
		
		return result;
	}
	
	@Override
	public ArrayList<Event> getEvents() {
		return _events;
	}
	
	@Override
	public ComChannelList getAllChannels()
	{
		return _com_channels;
	}
	
	protected void addActor(IActor actor) 
	{
		assert !_actors.contains(actor):"Actor is already a part of the team";
		
		_actors.add(actor);
	}
	
	protected void addEvent(Event event) 
	{
		assert !_events.contains(event):"Event is already a part of the team";
		_events.add(event);
	}
	
	protected void addComChannel(ComChannel<?> c)
	{
		assert _com_channels.containsKey(c.name()):"Com channel already defined";
		_com_channels.add(c);
	}

	protected ComChannel<?> getComChannel(String name)
	{
		return _com_channels.get(name);
	}
	
	protected final ComChannelList getComChannels()
	{
		return (ComChannelList) _com_channels.clone();
	}

	public HashMap<Actor, Integer> getWorkload(){
		HashMap<Actor, Integer> workload = new HashMap<Actor, Integer>();
		for(IActor a : _actors){
			workload.put((Actor)a, a.getWorkload());
		}
		return workload;
	}
}
