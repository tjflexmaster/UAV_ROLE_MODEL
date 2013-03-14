package NewModel.Roles;

import java.util.ArrayList;
import java.util.Random;

import NewModel.Simulation.Simulator;
import NewModel.Utils.DataType;
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
			case PS_POKE_MM:
				new_next_state = RoleState.IDLE;
				new_next_state_time = DurationGenerator.getRandDuration(5, 10);
				break;
			case PS_TX_MM:
				new_next_state = RoleState.PS_END_MM;
				new_next_state_time = DurationGenerator.getRandDuration(10, 30);
				break;
			case PS_END_MM:
				new_next_state = RoleState.IDLE;
				new_next_state_time = 1;
				break;
			case PS_ACK_MM:
				new_next_state = RoleState.PS_RX_MM;
				new_next_state_time = 1;
				break;
			case PS_RX_MM:
				//TODO Dont just receive forever
				new_next_state = null;
				new_next_state_time = 0;
				break;
			case STARTING:
				//Schedule an event in the future, this gets things running
				new_next_state = RoleState.PS_POKE_MM;
				new_next_state_time = 30;
				//Since I am going to bother the MM in 30 time units I need to give him some data
				//I put this into the PostOffice so that when we communicate it can be transferred.
				Simulator.addPost(RoleState.PS_TX_MM, DataType.SEARCH_AOI);
				Simulator.addPost(RoleState.PS_TX_MM, DataType.TARGET_DESCRIPTION);
				break;
			case IDLE:
				//TODO Look at TODO LIST 
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
			case PS_POKE_MM:
				//If the Parent Search is in this state then it wants to communicate with the MM
				//First Look at the MM State, if it has a listen state then begin to communicate
				if ( Simulator.team.getRoleState(RoleType.ROLE_MISSION_MANAGER) == RoleState.MM_ACK_PS ) {
					//IF the MM Acknowledged our request then begin transmitting on the next time step
					nextState(RoleState.PS_TX_MM, 1);
				}
				break;
			case PS_TX_MM:
				//If I am transmitting then continue to transmit no matter what
				//TODO Handle Interruptions
				break;
			case PS_END_MM:
				//Do Nothing I will soon be idle
				break;
			case PS_ACK_MM:
				//Nothing Interrupts the PS here
				break;
			case PS_RX_MM:
				if ( Simulator.team.getRoleState(RoleType.ROLE_MISSION_MANAGER) == RoleState.MM_END_PS ) {
					//Check the post office for data
					ArrayList<DataType> data = Simulator.removePosts(RoleState.MM_TX_PS);
					if ( data.contains( DataType.TARGET_SIGHTING ) ) {
						//If there is a sighting then have them do nothing
						nextState(RoleState.IDLE, 1);
					} else if ( data.contains( DataType.SEARCH_AOI_COMPLETE) ) {
						//If the MM reports that nothing was found then give him a new AOI in a few time steps
						nextState(RoleState.PS_POKE_MM, 10);
						//Add the new data to be given to the MM
						Simulator.addPost(RoleState.PS_TX_MM, DataType.SEARCH_AOI);
					} else {
						nextState(RoleState.IDLE, 1);
					}
					
				}
				break;
			case STARTING:
			case IDLE:
				//Look to see if MM is communicating
				if ( Simulator.team.getRoleState(RoleType.ROLE_MISSION_MANAGER) == RoleState.MM_POKE_PS ) {
					nextState(RoleState.PS_ACK_MM, 1);
				}
				
				break;
			default:
				//Do Nothing for any state not mentioned here
				break;
		}

	}

}
