package simulator;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Team implements ITeam {
	
	ArrayList<IEvent> _events = new ArrayList<IEvent>();
	ArrayList<IActor> _actors = new ArrayList<IActor>();
	ComChannelList _com_channels;
	

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
	public ArrayList<IEvent> getEvents() {
		return _events;
	}
	
	protected void addActor(IActor actor) 
	{
		assert !_actors.contains(actor):"Actor is already a part of the team";
		
		_actors.add(actor);
	}
	
	protected void addEvent(IEvent event, int count) 
	{
		assert !_events.contains(event):"Event is already a part of the team";
		event.setEventCount(count);
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
}
