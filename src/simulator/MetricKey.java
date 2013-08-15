package simulator;

public class MetricKey {
	public int _time;
	public String _actor_name;
	public String _state;
	public int _transition;
	
	public MetricKey(int time, String actor_name, String state,
			int transition_number) {
		_time = time;
		_actor_name = actor_name;
		_state  =_state;
		_transition = transition_number;
	}
	
	@Override
	public MetricKey clone()
	{
		MetricKey key = new MetricKey(_time, _actor_name, _state, _transition);
		return key;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((_actor_name == null) ? 0 : _actor_name.hashCode());
		result = prime * result + ((_state == null) ? 0 : _state.hashCode());
		result = prime * result + _time;
		result = prime * result + _transition;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof MetricKey))
			return false;
		MetricKey other = (MetricKey) obj;
		if (_actor_name == null) {
			if (other._actor_name != null)
				return false;
		} else if (!_actor_name.equals(other._actor_name))
			return false;
		if (_state == null) {
			if (other._state != null)
				return false;
		} else if (!_state.equals(other._state))
			return false;
		if (_time != other._time)
			return false;
		if (_transition != other._transition)
			return false;
		return true;
	}

	public boolean equals(int time, String actor_name, String state,
			int transition_number) {
		return time == _time && actor_name.equals(_actor_name) && state.equals(_state) && transition_number == _transition;
	}

}
