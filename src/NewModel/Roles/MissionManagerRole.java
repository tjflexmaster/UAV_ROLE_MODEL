package NewModel.Roles;

import java.util.ArrayList;

import NewModel.Simulation.Simulator;
import NewModel.Utils.DataType;
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
			case MM_POKE_PS:
				new_next_state = RoleState.IDLE;
				new_next_state_time = DurationGenerator.getRandDuration(5, 10);
				break;
			case MM_TX_PS:
				new_next_state = RoleState.MM_END_PS;
				//TODO change this duration based on the data being transmitted
				new_next_state_time = DurationGenerator.getRandDuration(10, 30);
				break;
			case MM_END_PS:
				new_next_state = RoleState.IDLE;
				new_next_state_time = 1;
				break;
			case MM_ACK_PS:
				new_next_state = RoleState.MM_RX_PS;
				new_next_state_time = 1;
				break;
			case MM_RX_PS:
				//TODO Dont just receive forever.
				new_next_state = null;
				new_next_state_time = 0;
				break;
			case STARTING:
				new_next_state = RoleState.IDLE;
				new_next_state_time = 1;
				break;
			case IDLE:
				//TODO Look at my TODO List and see if I need to do something more
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
			case MM_POKE_PS:
				//Look for Ack
				if ( Simulator.team.getRoleState(RoleType.ROLE_PARENT_SEARCH) == RoleState.PS_ACK_MM ) {
					nextState(RoleState.MM_TX_PS, 1);
				}
				break;
			case MM_TX_PS:
				//No interruptions
				//TODO Handle interruptions
				
				break;
			case MM_END_PS:
				//Do nothing
				break;
			case MM_ACK_PS:
				//Do nothing
				break;
			case MM_RX_PS:
				//Look for end of TX
				if ( Simulator.team.getRoleState(RoleType.ROLE_PARENT_SEARCH) == RoleState.PS_END_MM ) {
					
					//Check the post office for data
					ArrayList<DataType> data = Simulator.removePosts(RoleState.PS_TX_MM);
					if ( !data.isEmpty() ) {
						if ( data.contains( DataType.TERMINATE_SEARCH ) ) {
							
							//TODO Send search information to the pilot and video analyst instead of going idle
							//If there is a sighting then have them do nothing
							nextState(RoleState.IDLE, 1);
							
						} else if ( data.contains( DataType.SEARCH_AOI) ) {
							
							//TODO Send search information to the pilot and video analyst instead of going idle
							
							//If the MM reports that nothing was found then give him a new AOI in a few time steps
//							nextState(RoleState.MM_POKE_PS, 30);
							nextState(RoleState.IDLE, 1);
							
							if ( data.contains( DataType.TARGET_DESCRIPTION) ) {
								//TODO Send this data as well
							}
							//Add the new data to be given to the PS
//							Simulator.addPost(RoleState.MM_TX_PS, DataType.SEARCH_AOI_COMPLETE);
						} else if ( data.contains( DataType.TARGET_DESCRIPTION) ) {
							
							//TODO Send search information to the pilot and video analyst instead of going idle
							nextState(RoleState.IDLE, 1);
							
						}else {
							nextState(RoleState.IDLE, 1);
						}
					}
				}
				break;
			case IDLE:
				//If the MM is idle then do the following things in sequence
				//First check for Parent Search Commands
				if ( Simulator.team.getRoleState(RoleType.ROLE_PARENT_SEARCH) == RoleState.PS_POKE_MM ) {
					nextState(RoleState.MM_ACK_PS, 1);
				}
				//Second check TODO List
				break;
			default:
				//Do nothing for states not mentioned
				break;
		}

	}

}
