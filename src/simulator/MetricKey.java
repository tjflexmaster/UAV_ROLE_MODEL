package simulator;

public class MetricKey implements Comparable<MetricKey>{
	private int _time = -1;
	private String _actor_name = null;
	private String _state_name = null;
	private int _transition = -1;
	
	public void setTime(int time) {
		_time = time;
	}
	
	public int getTime() {
		return _time;
	}
	
	public void setActor(String actor_name) {
		_actor_name = actor_name;
	}
	
	public String getActor() {
		return _actor_name;
	}
	
	public void setState(String state_name) {
		_state_name = state_name;
	}
	
	public String getState() {
		return _state_name;
	}
	
	public void setTransition(int transition) {
		_transition = transition;
	}
	
	public int getTransition() {
		return _transition;
	}
	
	public MetricKey(int time, String actor_name) {
		_time = time;
		_actor_name = actor_name;
	}
	
	public MetricKey(int time, String actor_name, String state_name){
		_time = time;
		_actor_name = actor_name;
		_state_name  = state_name;
	}
	
	public MetricKey(int time, String actor_name, String state_name, int transition_number) {
		_time = time;
		_actor_name = actor_name;
		_state_name  = state_name;
		_transition = transition_number;
	}
	
	public String toString() {
		String result = "";
		
		result += "(";
		if(_time != -1){
			result += _time;
		}
		result += ", ";
		if(_actor_name != null){
			result += _actor_name;
		}
		result += ", ";
		if (_state_name != null){
			result += _state_name;
		}
		result += ", ";
		if (_transition != -1){
			result += _transition;
		}
		result += ")";
		
		return result;
	}

	@Override
	public int compareTo(MetricKey o) {
		if (_time < o._time) {
			return -1;
		} else {
			return 1;
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((_actor_name == null) ? 0 : _actor_name.hashCode());
		result = prime * result + ((_state_name == null) ? 0 : _state_name.hashCode());
		result = prime * result + _time;
		result = prime * result + _transition;
		return result;
	}

//	
//	public String toString()
//	{
//		return _time + ":" + _actor_name + ":" + _state_name + ":" + _transition;
//	}
//	
//	@Override
//	public MetricKey clone()
//	{
//		MetricKey key = new MetricKey(_time, _actor_name, _state_name, _transition);
//		return key;
//	}
//	
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (!(obj instanceof MetricKey))
//			return false;
//		MetricKey other = (MetricKey) obj;
//		if (_actor_name == null) {
//			if (other._actor_name != null)
//				return false;
//		} else if (!_actor_name.equals(other._actor_name))
//			return false;
//		if (_state_name == null) {
//			if (other._state_name != null)
//				return false;
//		} else if (!_state_name.equals(other._state_name))
//			return false;
//		if (_time != other._time)
//			return false;
//		if (_transition != other._transition)
//			return false;
//		return true;
//	}
//
//	public boolean equals(int time, String actor_name, String state,
//			int transition_number) {
//		return time == _time && actor_name.equals(_actor_name) && state.equals(_state_name) && transition_number == _transition;
//	}
//
//	@Override
//	public int compareTo(Object arg0) {
//		if ( arg0 instanceof MetricKey) {
//			MetricKey key = (MetricKey) arg0;
//			if ( key._time > _time ) {
//				return -1;
//			} else if ( key._time == _time ) {
//				int actor = _actor_name.compareTo(key._actor_name);
//				if ( actor == 0 ) {
//					int state = _state_name.compareTo(key._state_name);
//					if ( state == 0 ) {
//						return _transition - key._transition;
//					} else {
//						return state;
//					}
//				} else {
//					return actor;
//				}
//			} else {
//				return 1;
//			}
//		} else
//			return 0;
//	}
}
