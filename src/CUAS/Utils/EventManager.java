package CUAS.Utils;

import java.util.ArrayList;

import CUAS.Simulator.IEvent;


public class EventManager {

	ArrayList<IEvent> _events = new ArrayList<IEvent>();
	
	public EventManager()
	{
		
	}
	
	public void addEvent(IEvent event)
	{
		_events.add(event);
	}
	
	public void removeEvent(IEvent event)
	{
		_events.remove(event);
	}
	
	public int totalEventsRemaining()
	{
		int result = 0;
		for( IEvent event : _events ) {
			result += event.getCount();
		}
		return result;
	}
	
	public int getNextEventTime()
	{
		
		if ( _events.isEmpty() )
			return 0;
		
		int result = 0;
		for( IEvent event : _events ) {
			if ( event.getCount() > 0 ) {
				int time = event.getNextTime();
				if ( result == 0 || time < result )
					result = time;
			}
		}
		
		return result;
	}
	
	public void processNextState()
	{
		for( IEvent event : _events ) {
			if ( event.getCount() > 0 ) {
				event.processNextState();
			}
		}
	}
	
	public void processInputs()
	{
		for( IEvent event : _events ) {
			if ( event.getCount() > 0 ) {
				event.processInputs();
			}
		}
	}
	
}
