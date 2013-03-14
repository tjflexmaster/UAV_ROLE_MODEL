package NewModel.Roles;

import java.util.Random;

import NewModel.Simulation.Simulator;
import NewModel.Utils.DurationGenerator;

public class ParentSearchRole extends Role {

	public ParentSearchRole()
	{
		type(RoleType.ROLE_PARENT_SEARCH);
	}
	
	@Override
	public boolean processNextState()
	{
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
			case PS_SEARCH_POKE_MM:
				new_next_state = RoleState.IDLE;
				new_next_state_time = DurationGenerator.getRandDuration(2, 10);
				break;
			case PS_SEARCH_TX_MM:
				new_next_state = RoleState.PS_SEARCH_END_MM;
				new_next_state_time = DurationGenerator.getRandDuration(10, 30);
				break;
			case PS_SEARCH_END_MM:
				new_next_state = RoleState.IDLE;
				new_next_state_time = 1;
				break;
			case PS_TERMINATE_SEARCH_POKE_MM:
				new_next_state = RoleState.IDLE;
				new_next_state_time = DurationGenerator.getRandDuration(5, 30);
				break;
			case PS_TERMINATE_SEARCH_TX_MM:
				new_next_state = RoleState.PS_TERMINATE_SEARCH_END_MM;
				new_next_state_time = DurationGenerator.getRandDuration(10, 30);
				break;
			case PS_TERMINATE_SEARCH_END_MM:
				new_next_state = RoleState.IDLE;
				new_next_state_time = 1;
				break;
			case PS_SEARCH_COMPLETE_ACK_MM:
				new_next_state = RoleState.PS_SEARCH_COMPLETE_RX_MM;
				new_next_state_time = 1;
				break;
			case PS_SEARCH_COMPLETE_RX_MM:
				//TODO Dont just receive forever
				new_next_state = null;
				new_next_state_time = 0;
				break;
			case PS_TARGET_SIGHTING_ACK_MM:
				new_next_state = RoleState.PS_TARGET_SIGHTING_RX_MM;
				new_next_state_time = 1;
				break;
			case PS_TARGET_SIGHTING_RX_MM:
				new_next_state = RoleState.IDLE;
				new_next_state_time = 0;
				break;
			case STARTING:
				//Schedule an event in the future, this gets things running
				new_next_state = RoleState.PS_SEARCH_POKE_MM;
				new_next_state_time = 30;
				break;
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
		switch( state() ) {
			case PS_SEARCH_POKE_MM:
				//If the Parent Search is in this state then it wants to communicate with the MM
				//First Look at the MM State, if it has a listen state then begin to communicate
				if ( Simulator.team.getRoleState(RoleType.ROLE_MISSION_MANAGER) == RoleState.MM_SEARCH_ACK_PS ) {
					//IF the MM Acknowledged our request then begin transmitting on the next time step
					nextState(RoleState.PS_SEARCH_TX_MM, 1);
				}
				break;
			case PS_SEARCH_TX_MM:
				//If I am transmitting then continue to transmit no matter what
				//TODO Handle Interruptions
				break;
			case PS_SEARCH_END_MM:
				//Do Nothing I will soon be idle
				break;
			case PS_TERMINATE_SEARCH_POKE_MM:
				//Look for an Acknowledgment from the MM
				if ( Simulator.team.getRoleState(RoleType.ROLE_MISSION_MANAGER) == RoleState.MM_TERMINATE_SEARCH_ACK_PS ) {
					nextState(RoleState.PS_TERMINATE_SEARCH_TX_MM, 1);
				}
				break;
			case PS_TERMINATE_SEARCH_TX_MM:
				//No interruptions while transmitting
				//TODO handle interruptions
				break;
			case PS_TERMINATE_SEARCH_END_MM:
				//No interruptions here
				break;
			case PS_SEARCH_COMPLETE_ACK_MM:
				//Nothing Interrupts the PS here
				break;
			case PS_SEARCH_COMPLETE_RX_MM:
				if ( Simulator.team.getRoleState(RoleType.ROLE_MISSION_MANAGER) == RoleState.MM_SEARCH_COMPLETE_END_PS ) {
					nextState(RoleState.IDLE, 1);
				}
				break;
			case PS_TARGET_SIGHTING_ACK_MM:
				//Nothing interrupts the PS here
				break;
			case PS_TARGET_SIGHTING_RX_MM:
				if ( Simulator.team.getRoleState(RoleType.ROLE_MISSION_MANAGER) == RoleState.MM_TARGET_SIGHTING_END_PS ) {
					nextState(RoleState.IDLE, 1);
				}
				break;
			case STARTING:
			case IDLE:
				//Look to see if MM is communicating
				if ( Simulator.team.getRoleState(RoleType.ROLE_MISSION_MANAGER) == RoleState.MM_SEARCH_COMPLETE_POKE_PS ) {
					nextState(RoleState.PS_SEARCH_COMPLETE_ACK_MM, 1);
				} else if ( Simulator.team.getRoleState(RoleType.ROLE_MISSION_MANAGER) == RoleState.MM_TARGET_SIGHTING_POKE_PS ) {
					nextState(RoleState.PS_TARGET_SIGHTING_ACK_MM, 1);
				}
				
				break;
			default:
				//Do Nothing for any state not mentioned here
				break;
		}

	}

}
