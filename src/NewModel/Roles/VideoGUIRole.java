package NewModel.Roles;

import NewModel.Simulation.Simulator;

public class VideoGUIRole extends Role {

	public VideoGUIRole()
	{
		type(RoleType.ROLE_VIDEO_GUI);
	}
	
	@Override
	public boolean processNextState() {
		//Is our next state now? Is it a valid state?
		if ( nextStateTime() != Simulator.getTime() || nextState() == null ) {
			return false;
		}
		
		//Update to the next state
		state(nextState());
		
		//Now determine what our next state will be
		RoleState new_next_state = null;
		//Each state has a designated duration
		int new_next_state_time = 0;
		
		//If a state isn't included then it doesn't deviate from the default
		switch(nextState()) {
			case STARTING:
				new_next_state = RoleState.IDLE;
				new_next_state_time = 1;
			default:
				new_next_state = null;
				new_next_state_time = 0;
				break;
		}
		
		nextState(new_next_state, new_next_state_time);
		
		return true;
	}

	@Override
	public void updateState() {
		// TODO Auto-generated method stub

	}
}
