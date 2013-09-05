package listeners;

public class ActorOutputMetric implements Comparable<ActorOutputMetric> {
	
	public int _time;
	public String _actor;
	public int _memory;
	public int _output;
	
	ActorOutputMetric(int time, String actor, int memory, int output)
	{
		this._time = time;
		_actor = actor;
		_memory = memory;
		_output = output;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((_actor == null) ? 0 : _actor.hashCode());
		result = prime * result + _time;
		return result;
	}
	
	
	@Override
	public int compareTo(ActorOutputMetric arg0) {
		if ( arg0._actor.hashCode() < _actor.hashCode() )
			return -1;
		else if ( arg0._actor.hashCode() > _actor.hashCode() )
			return 1;
		else {
			if ( arg0._time < _time )
				return 1;
			else
				return -1;
		}
	}
	
	@Override
	public String toString()
	{
		return _time + "," + _actor + "," + _memory + "," + _output;
	}

}
