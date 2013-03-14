package NewModel.Roles;

import NewModel.Simulation.Simulator;
import NewModel.Utils.DurationGenerator;

public class MissionManagerRole extends Role {

	public MissionManagerRole()
	{
		type(RoleType.ROLE_MISSION_MANAGER);
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
			case MM_SEARCH_ACK_PS:
				new_next_state = RoleState.MM_SEARCH_RX_PS;
				new_next_state_time = 1;
				break;
			case MM_SEARCH_RX_PS:
				//TODO Dont just receive forever.
				new_next_state = null;
				new_next_state_time = 0;
				break;
			case MM_TERMINATE_SEARCH_ACK_PS:
				new_next_state = RoleState.MM_TERMINATE_SEARCH_RX_PS;
				new_next_state_time = 1;
				break;
			case MM_TERMINATE_SEARCH_RX_PS:
				//TODO Dont just receive forever
				new_next_state = null;
				new_next_state_time = 0;
				break;
			case MM_SEARCH_COMPLETE_POKE_PS:
				new_next_state = RoleState.IDLE;
				new_next_state_time = DurationGenerator.getRandDuration(2, 10);
				break;
			case MM_SEARCH_COMPLETE_TX_PS:
				new_next_state = RoleState.MM_SEARCH_COMPLETE_END_PS;
				new_next_state_time = DurationGenerator.getRandDuration(10, 30);
				break;
			case MM_SEARCH_COMPLETE_END_PS:
				new_next_state = RoleState.IDLE;
				new_next_state_time = 1;
				break;
			case MM_TARGET_SIGHTING_POKE_PS:
				new_next_state = RoleState.IDLE;
				new_next_state_time = DurationGenerator.getRandDuration(2, 10);
				break;
			case MM_TARGET_SIGHTING_TX_PS:
				new_next_state = RoleState.MM_TARGET_SIGHTING_END_PS;
				new_next_state_time = DurationGenerator.getRandDuration(10, 30);
				break;
			case MM_TARGET_SIGHTING_END_PS:
				new_next_state = RoleState.IDLE;
				new_next_state_time = 1;
				break;
			case STARTING:
				new_next_state = RoleState.IDLE;
				new_next_state_time = 1;
				break;
			default:
				//Stay as we are
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
			case MM_SEARCH_ACK_PS:
				//Do nothing
				break;
			case MM_SEARCH_RX_PS:
				//Look for end of TX
				if ( Simulator.team.getRoleState(RoleType.ROLE_PARENT_SEARCH) == RoleState.PS_SEARCH_END_MM ) {
					//TODO Send search information to the pilot and video analyst instead of going idle
					nextState(RoleState.IDLE, 1);
				}
				break;
			case MM_TERMINATE_SEARCH_ACK_PS:
				//Do nothing
				break;
			case MM_TERMINATE_SEARCH_RX_PS:
				//Look for end of TX
				if ( Simulator.team.getRoleState(RoleType.ROLE_PARENT_SEARCH) == RoleState.PS_TERMINATE_SEARCH_END_MM ) {
					//TODO Send termination info to other roles
					nextState(RoleState.IDLE, 1);
				}
				break;
			case MM_SEARCH_COMPLETE_POKE_PS:
				//Look for Ack
				if ( Simulator.team.getRoleState(RoleType.ROLE_PARENT_SEARCH) == RoleState.PS_SEARCH_COMPLETE_ACK_MM ) {
					nextState(RoleState.MM_SEARCH_COMPLETE_TX_PS, 1);
				}
				break;
			case MM_SEARCH_COMPLETE_TX_PS:
				//No interruptions
				//TODO Handle interruptions
				
				break;
			case MM_SEARCH_COMPLETE_END_PS:
				//Do nothing
				break;
			case MM_TARGET_SIGHTING_POKE_PS:
				//Look for Ack
				if ( Simulator.team.getRoleState(RoleType.ROLE_PARENT_SEARCH) == RoleState.PS_TARGET_SIGHTING_ACK_MM ) {
					nextState(RoleState.MM_TARGET_SIGHTING_TX_PS, 1);
				}
				break;
			case MM_TARGET_SIGHTING_TX_PS:
				//No interruptions
				//TODO Handle interruptions
				break;
			case MM_TARGET_SIGHTING_END_PS:
				//Do Nothing
				break;
			case IDLE:
				//If the MM is idle then do the following things in sequence
				//First check for Parent Search Commands
				if ( Simulator.team.getRoleState(RoleType.ROLE_PARENT_SEARCH) == RoleState.PS_SEARCH_POKE_MM ) {
					nextState(RoleState.MM_SEARCH_ACK_PS, 1);
				}
				
				//Second check for Parent Terminate Search Commands
				if ( Simulator.team.getRoleState(RoleType.ROLE_PARENT_SEARCH) == RoleState.PS_TERMINATE_SEARCH_POKE_MM ) {
					nextState(RoleState.MM_TERMINATE_SEARCH_RX_PS, 1);
				}
				break;
			default:
				//Do nothing for states not mentioned
				break;
		}

	}

}
