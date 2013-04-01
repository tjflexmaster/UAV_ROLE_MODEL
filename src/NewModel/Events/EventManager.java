package NewModel.Events;

import java.util.ArrayList;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class EventManager {

	SortedMap<Integer, ArrayList<Event> > _events = new TreeMap<Integer, ArrayList<Event>>();
	
	public EventManager()
	{
		
	}
	
	public void addEvent(Event event, int time)
	{
		ArrayList<Event> event_list;
		if ( !_events.containsKey(time) ) {
			event_list = new ArrayList<Event>();
		} else {
			event_list = _events.get(time);
		}
		
		//Add to the event list
		event_list.add(event);
		_events.put(time, event_list);
	}
	
	public int getNextEventTime(int time)
	{
		Set<Integer> keys = _events.keySet();
		
		for( int next_time : keys ) {
			if ( next_time > time ) {
				return next_time;
			}
		}
		return 0;
	}
	
	public ArrayList<Event> getEvents(int time)
	{
		if ( _events.containsKey(time) ) {
			return _events.get(time);
		} else {
			return new ArrayList<Event>();
		}
	}
	
}
