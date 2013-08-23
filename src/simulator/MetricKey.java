package simulator;

public class MetricKey implements Comparable {
	public int _time;
	public String _actor_name;
	public String _state;
	public int _transition;
	
	public MetricKey(int time, String actor_name, String state,
			int transition_number) {
		_time = time;
		_actor_name = actor_name;
		_state  = state;
		_transition = transition_number;
	}
	
	public String toString()
	{
		return _time + ":" + _actor_name + ":" + _state + ":" + _transition;
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

	@Override
	public int compareTo(Object arg0) {
		if ( arg0 instanceof MetricKey) {
			MetricKey key = (MetricKey) arg0;
			if ( key._time > _time ) {
				return -1;
			} else if ( key._time == _time ) {
				int actor = _actor_name.compareTo(key._actor_name);
				if ( actor == 0 ) {
					int state = _state.compareTo(key._state);
					if ( state == 0 ) {
						return _transition - key._transition;
					} else {
						return state;
					}
				} else {
					return actor;
				}
			} else {
				return 1;
			}
		} else
			return 0;
	}

}
