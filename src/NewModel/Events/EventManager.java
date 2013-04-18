package NewModel.Events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;


public class EventManager {

//	SortedMap<Integer, ArrayList<Event> > _events = new TreeMap<Integer, ArrayList<Event>>();
	HashMap<IEvent, Integer> _events = new HashMap<IEvent, Integer>();
	
	public EventManager()
	{
		
	}
	
	public void addEvent(IEvent event)
	{
		addEvent(event, 1);
	}
	
	public void addEvent(IEvent event, int count)
	{
		int prev_count =  _events.containsKey(event) ? _events.get(event) : 0;
		_events.put(event, prev_count + count);
	}
	
	public void removeEvent(IEvent event)
	{
		removeEvent(event, 1);
	}
	
	public void removeEvent(IEvent event, int count)
	{
		//Decrement the number of these events remaining
		int prev_count =  _events.containsKey(event) ? _events.get(event) : 0;
		_events.put(event, prev_count - count);
	}
	
	public ArrayList<IEvent> getEvents()
	{
		ArrayList<IEvent> events = new ArrayList<IEvent>();
		
		Iterator<Entry<IEvent, Integer>> it = _events.entrySet().iterator();
		while( it.hasNext() ) {
			Entry<IEvent, Integer> event = it.next();
			if ( event.getValue() > 0 ) {
				events.add( event.getKey() );
			}
		}//end while
		
		return events;
	}
	
//	public void addEvent(Event event, int time)
//	{
//		ArrayList<Event> event_list;
//		if ( !_events.containsKey(time) ) {
//			event_list = new ArrayList<Event>();
//		} else {
//			event_list = _events.get(time);
//		}
//		
//		//Add to the event list
//		event_list.add(event);
//		_events.put(time, event_list);
//	}
	
//	public int getNextEventTime(int time)
//	{
//		Set<Integer> keys = _events.keySet();
//		
//		for( int next_time : keys ) {
//			if ( next_time > time ) {
//				return next_time;
//			}
//		}
//		return 0;
//	}
//	
//	public ArrayList<Event> getEvents(int time)
//	{
//		if ( _events.containsKey(time) ) {
//			return _events.get(time);
//		} else {
//			return new ArrayList<Event>();
//		}
//	}
	
}
