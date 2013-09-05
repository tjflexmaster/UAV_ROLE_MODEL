package listeners;

import com.sun.xml.internal.ws.util.StringUtils;

public class ChannelConflictMetric implements Comparable<ChannelConflictMetric> {

	public int _time;
	public String _actor;
	public String _channel_type;
	public int _count;
	
	public ChannelConflictMetric(int time, String actor, String channel_type, int count) {
		_time = time;
		_actor = actor;
		_channel_type = channel_type;
		_count= count;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((_actor == null) ? 0 : _actor.hashCode());
		result = prime * result + ((_channel_type == null) ? 0 : _channel_type.hashCode());
		result = prime * result + _time;
		result = prime * result + _count;
		return result;
	}
	
	
	@Override
	public int compareTo(ChannelConflictMetric arg0) {
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
		return _time + "," + _actor + "," + _channel_type + "," + _count;
	}
}
