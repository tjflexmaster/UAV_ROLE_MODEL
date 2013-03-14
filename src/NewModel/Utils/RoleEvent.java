package NewModel.Utils;

import NewModel.Roles.RoleState;
import NewModel.Simulation.Simulator;

public class RoleEvent {

	private int _time;
	private RoleState _state;
	
	public RoleEvent(RoleState state) {
		_time = Simulator.getTime();
		_state = state;
	}
	
	public int time()
	{
		return _time;
	}
	
	public RoleState state()
	{
		return _state;
	}
}
