package NewModel.Roles;

import java.util.ArrayList;

import NewModel.Events.Event;
import NewModel.Simulation.Assumptions;
import NewModel.Simulation.Simulator;
import NewModel.Utils.DataType;
import NewModel.Utils.DurationGenerator;
import NewModel.Utils.PostOffice.POBOX;

public class MissionManagerRole extends Role {

	/**
	 * STATE VARS
	 */
	private int _search_aoi_count = 0;
	
	/**
	 * END STATE VARS
	 */
	
	public MissionManagerRole()
	{
		type(RoleType.ROLE_MISSION_MANAGER);
	}
	
	@Override
	public boolean processNextState()
	{
		//Is our next state now?
		if ( nextStateTime() != Simulator.getInstance().getTime() ) {
			return false;
		}
		
		//Update to the next state
		state(nextState());
		
		//Now determine what our next state will be
		//Each state has a designated duration
		
		//If a state isn't included then it doesn't deviate from the default
		switch(nextState()) {
			case MM_POKE_PS:
			case MM_POKE_PILOT:
				nextState(RoleState.IDLE, Assumptions.MM_POKE_DUR);
				break;
			case MM_TX_PS:
				//TODO change this duration based on the data being transmitted
				nextState(RoleState.MM_END_PS, Assumptions.MM_TX_PS_DUR);
				break;
			case MM_TX_PILOT:
				//TODO change this duration based on the data being transmitted
				nextState(RoleState.MM_END_PILOT, Assumptions.MM_TX_PILOT_DUR);
				break;
			case MM_END_PS:
			case MM_END_PILOT:
				nextState(RoleState.IDLE, 1);
				break;
			case MM_ACK_PS:
				nextState(RoleState.MM_RX_PS, 1);
				break;
			case MM_ACK_PILOT:
				nextState(RoleState.MM_RX_PILOT, 1);
				break;
			case MM_RX_PS:
			case MM_RX_PILOT:
				nextState(RoleState.IDLE, Assumptions.MM_RX_DUR);
				break;
			case STARTING:
				nextState(RoleState.IDLE, 1);
				break;
			case IDLE:
				//TODO Look at my TODO List and see if I need to do something more
				break;
			default:
				//Stay as we are
				nextState(null, 0);
				break;
		}
		
		return true;
	}
	
	@Override
	public void updateState() {
		switch( state() ) {
			case MM_POKE_PS:
				//Look for Ack
				if ( Simulator.getInstance().getRoleState(RoleType.ROLE_PARENT_SEARCH) == RoleState.PS_ACK_MM ) {
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
				//Whatever the MM does next it should be on the next time step so that it does not
				//appear that he is receiving after the PS stopped transmitting.
				if ( Simulator.getInstance().getRoleState(RoleType.ROLE_PARENT_SEARCH) == RoleState.PS_END_MM ) {
					
					//Check the post office for data
					ArrayList<DataType> data = Simulator.getInstance().removePosts(POBOX.PS_MM);
					if ( !data.isEmpty() ) {
						if ( data.contains( DataType.TERMINATE_SEARCH ) ) {
							Simulator.getInstance().addPost(POBOX.MM_PILOT, DataType.TERMINATE_SEARCH);
							Simulator.getInstance().addPost(POBOX.MM_VA, DataType.TERMINATE_SEARCH);
							nextState(RoleState.MM_POKE_PILOT,1);
							//TODO Also poke the VA
						} else if ( data.contains( DataType.SEARCH_AOI) ) {
							Simulator.getInstance().addPost(POBOX.MM_PILOT, DataType.SEARCH_AOI);
							Simulator.getInstance().addPost(POBOX.MM_VA, DataType.SEARCH_AOI);
							if ( data.contains( DataType.TARGET_DESCRIPTION) ) {
								Simulator.getInstance().addPost(POBOX.MM_PILOT, DataType.TARGET_DESCRIPTION);
								Simulator.getInstance().addPost(POBOX.MM_VA, DataType.TARGET_DESCRIPTION);
							}
							nextState(RoleState.MM_POKE_PILOT,1);
							//TODO Also poke the VA
						} else if ( data.contains( DataType.TARGET_DESCRIPTION) ) {
							
//							Simulator.getInstance().addPost(POBOX.MM_PILOT, DataType.TARGET_DESCRIPTION);
//							nextState(RoleState.MM_POKE_VA,1);
							//TODO Also poke pilot
						}else {
							nextState(RoleState.IDLE, 1);
						}
					}
				}
				break;
			case MM_POKE_PILOT:
				//Look for Ack
				if ( Simulator.getInstance().getRoleState(RoleType.ROLE_PILOT) == RoleState.PILOT_ACK_MM ) {
					nextState(RoleState.MM_TX_PILOT, 1);
				}
				//TODO Handle interruptions from PS or VA
				break;
			case MM_TX_PILOT:
				//TODO Handle interruptions from PS or VA
				break;
			case MM_END_PILOT:
				//Do nothing
				break;
			case MM_ACK_PILOT:
				//Do Nothing
				break;
			case MM_RX_PILOT:
				//Watch for the end of the TX
				if ( Simulator.getInstance().getRoleState(RoleType.ROLE_PILOT) == RoleState.PILOT_END_MM ) {
					//What data was sent
					//Check the post office for data
					ArrayList<DataType> data = Simulator.getInstance().removePosts(POBOX.PILOT_MM);
					if ( !data.isEmpty() ) {
						if ( data.contains( DataType.SEARCH_COMPLETE ) ) {
							Simulator.getInstance().addPost(POBOX.MM_PS, DataType.SEARCH_AOI_COMPLETE);
							nextState(RoleState.MM_POKE_PS,1);
						} else if ( data.contains( DataType.SEARCH_AOI_FAILED) ) {
							//TODO Also tell VA
							
							//Assumption that if the search failed then no further searches can be performed so the search is over.
							assert false : "Search Failed!";
							
						}else {
							nextState(RoleState.IDLE, 1);
						}
					} else {
						nextState(RoleState.IDLE, 1);
					}
				}
				//TODO Handle interruptions from PS or VA
				break;
			case IDLE:
				//If the MM is idle then do the following things in sequence
				//First check for Parent Search Commands
				if ( Simulator.getInstance().getRoleState(RoleType.ROLE_PARENT_SEARCH) == RoleState.PS_POKE_MM ) {
					nextState(RoleState.MM_ACK_PS, 1);
				} else if ( Simulator.getInstance().getRoleState(RoleType.ROLE_PILOT) == RoleState.PILOT_POKE_MM ) {
					nextState(RoleState.MM_ACK_PILOT, 1);
				}
				//TODO Handle interruptions from VA
				break;
			default:
				//Do nothing for states not mentioned
				break;
		}

	}//end udpateState

	
	@Override
	public void processEvents(ArrayList<Event> events) {
		//Do nothing
	}
}
