package NewModel.Events;

public class Event {

	private int _duration = 1;
	private EventType _type;
	
	public Event(EventType type, int duration)
	{
		duration(duration);
		type(type);
		
	}
	
	public int duration()
	{
		return _duration;
	}
	
	public void duration(int duration)
	{
		_duration = Math.max(1, duration);
	}
	
	public EventType type()
	{
		return _type;
	}
	
	public void type(EventType type)
	{
		_type = type;
	}
}
