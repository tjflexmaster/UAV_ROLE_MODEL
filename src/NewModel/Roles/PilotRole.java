package NewModel.Roles;

import NewModel.Simulation.Simulator;
import NewModel.Utils.DurationGenerator;

public class PilotRole extends Role {
	
	/**
	 * PILOT STATE VARS
	 */
	private enum UAVState {
		GROUNDED,
		FLYING,
		MANUAL_FLYING,
		CRASHED
	}
	private enum SearchState {
		NONE,
		ACTIVE,
		COMPLETE,
		IDLE
	}
	
	//Default Values
	UAVState _uav_state = UAVState.GROUNDED;
	SearchState _search_state = SearchState.NONE;
	
	/**
	 * END PILOT STATE VARS
	 */
	

	public PilotRole()
	{
		type(RoleType.ROLE_PILOT);
	}
	
	@Override
	public boolean processNextState() {
		//Is our next state now?
		if ( nextStateTime() != Simulator.getTime() ) {
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
			case PILOT_POKE_MM:
				new_next_state = RoleState.IDLE;
				new_next_state_time = DurationGenerator.getRandDuration(5, 10);
				nextState(RoleState.IDLE, DurationGenerator.getRandDuration(5, 10));
				break;
			case PILOT_TX_MM:
				//TODO change this duration based on the data being transmitted
				nextState(RoleState.MM_END_PS, DurationGenerator.getRandDuration(10, 30));
				break;
			case PILOT_END_MM:
				nextState(RoleState.IDLE, 1);
				break;
			case PILOT_ACK_MM:
				nextState(RoleState.PILOT_RX_MM, 1);
				break;
			case PILOT_RX_MM:
				//TODO dont just receive forever
				nextState(null, 0);
				break;
			case PILOT_PHYSICAL_BREAK:
				//When a pilot goes on a break what does he do when he comes back?
				//Assume that he checks the GUI when he comes back, if the UAV is not airborne
				//he would not check the GUI so this would depend on his internal state.
				if ( _uav_state == UAVState.FLYING ) {
					nextState(RoleState.PILOT_OBSERVING_GUI, 1);
				} else {
					nextState(RoleState.IDLE, 1);
				}
				break;
			case PILOT_OBSERVING_UAV:
//				if ( _uav_state == UAVState.FLYING ) {
				//We should only get here if the UAV is flying
				nextState(RoleState.PILOT_OBSERVING_GUI, DurationGenerator.getRandDuration(20, 40));
				break;
			case PILOT_OBSERVING_GUI:
//				if ( _uav_state == UAVState.FLYING )
				//We should only get here if the UAV is flying
				nextState(RoleState.PILOT_OBSERVING_UAV, DurationGenerator.getRandDuration(20, 40));
				break;
			case PILOT_FLIGHT_PLANNING:
				if ( _uav_state == UAVState.GROUNDED ) {
					//TODO Move pilot into a take-off state
				}
				break;
			case PILOT_MANUAL_FLYING:
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
