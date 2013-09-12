package listeners;

public class DecisionWorkloadMetric implements Comparable<DecisionWorkloadMetric> {

	public int _time;
	public String _actor;
	public String _state;
	public int _workload;
	
	public DecisionWorkloadMetric(int time, String actor, String state, int workload) {
		_time = time;
		_actor = actor;
		_state = state;
		_workload = workload;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((_actor == null) ? 0 : _actor.hashCode());
		result = prime * result + ((_state == null) ? 0 : _state.hashCode());
		result = prime * result + _time;
		result = prime * result + _workload;
		return result;
	}
	
	
	@Override
	public int compareTo(DecisionWorkloadMetric arg0) {
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
		return _time + "," + _actor + "," + _state + "," + _workload;
	}

}
