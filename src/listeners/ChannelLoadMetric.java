package listeners;

public class ChannelLoadMetric implements Comparable<ChannelLoadMetric> {

	public int _time;
	public String _source;
	public String _target;
	public String _channel_type;
	public int _load;
	
	ChannelLoadMetric(int time, String source, String target, String channel_type, int load)
	{
		this._time = time;
		_source = source;
		_target = target;
		_channel_type = channel_type;
		_load = load;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((_target == null) ? 0 : _target.hashCode());
		result = prime * result
				+ ((_source == null) ? 0 : _source.hashCode());
		result = prime * result + ((_channel_type == null) ? 0 : _channel_type.hashCode());
		result = prime * result + _time;
		result = prime * result + _load;
		return result;
	}
	
	
	@Override
	public int compareTo(ChannelLoadMetric arg0) {
		if ( arg0._target.hashCode() < _target.hashCode() )
			return -1;
		else if ( arg0._target.hashCode() > _target.hashCode() )
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
		return _time + "," + _source + "," + _target + "," + _channel_type + "," + _load;
	}
}
