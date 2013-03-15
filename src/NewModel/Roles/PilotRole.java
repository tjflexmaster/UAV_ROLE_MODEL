package NewModel.Roles;

import java.util.ArrayList;

import NewModel.Simulation.Simulator;
import NewModel.Utils.DataType;
import NewModel.Utils.DurationGenerator;

public class PilotRole extends Role {
	
	/**
	 * PILOT STATE VARS
	 */
	private enum UAVState {
		GROUNDED,
		FLYING,
		LOITERING,
		CRASHED
	}
	private enum SearchState {
		NONE,
		ACTIVE,
		COMPLETE,
		TERMINATED
	}
	private enum InteruptState {
		BUSY,
		AVAILABLE
	}
	
	//Default Values
	UAVState _uav_state = UAVState.GROUNDED;
	SearchState _search_state = SearchState.NONE;
	InteruptState _interupt_state = InteruptState.AVAILABLE;
	
	/**
	 * END PILOT STATE VARS
	 */
	

	public PilotRole()
	{
		type(RoleType.ROLE_PILOT);
		_search_state = SearchState.NONE;
		_uav_state = UAVState.GROUNDED;
		_interupt_state = InteruptState.AVAILABLE;
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
		//Each state has a designated duration
		int duration = 1;
		//If a state isn't included then it doesn't deviate from the default
		switch(nextState()) {
			case PILOT_POKE_MM:
				duration = DurationGenerator.getRandDuration(20, 30);
				nextState(RoleState.IDLE, duration);
				break;
			case PILOT_TX_MM:
				//TODO change this duration based on the data being transmitted
				duration = DurationGenerator.getRandDuration(20, 30);
				nextState(RoleState.PILOT_END_MM, duration);
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
			case PILOT_OBSERVING_UAV:
				//We should only get here if the UAV is flying
				duration = DurationGenerator.getRandDuration(20, 30);
				nextState(RoleState.PILOT_OBSERVING_GUI, duration);
				break;
			case PILOT_OBSERVING_GUI:
				duration = DurationGenerator.getRandDuration(20, 30);
				nextState(RoleState.PILOT_OBSERVING_UAV, duration);
				break;
			case PILOT_LAUNCH_UAV:
//				duration = DurationGenerator.getRandDuration(30, 40);
				_uav_state = UAVState.FLYING;
				nextState(RoleState.PILOT_OBSERVING_UAV, duration);
				break;
			case PILOT_POKE_UGUI:
//				DurationGenerator.getRandDuration(5, 20);
				nextState(RoleState.IDLE, duration);
				break;
			case PILOT_TX_UGUI:
				//TODO base this duration on the items being transmitted
				duration = 1;
				nextState(RoleState.PILOT_END_UGUI, duration);
				break;
			case PILOT_END_UGUI:
				//TODO Look at what I need to do next and do it, such as Launch UAV, pick it up, etc...
				duration = 1;
				nextState(RoleState.PILOT_OBSERVING_UAV, duration);
				break;
			case PILOT_POST_FLIGHT:
				if ( _uav_state == UAVState.GROUNDED ) {
					//Plane is on the ground
					duration = DurationGenerator.getRandDuration(9, 10);
					if ( _search_state == SearchState.ACTIVE ) {
						nextState(RoleState.PILOT_LAUNCH_UAV, duration);
					} else {
						nextState(RoleState.IDLE, duration);
					}
				} else if ( _uav_state == UAVState.CRASHED ) {
					//Recover the crashed UAV
					duration = DurationGenerator.getRandDuration(50, 100);
					nextState(RoleState.IDLE, duration);
					//TODO UAV is crashed so end the simulation
				}
				break;
			case STARTING:
				nextState(RoleState.IDLE, duration);
				break;
			case IDLE:
				//TODO Look at todo list to see what needs to be done
				break;
			default:
				nextState(null, duration);
				break;
		}
		
		return true;
	}
	
	@Override
	public void updateState() {
		switch( state() ) {
			case PILOT_POKE_MM:
				//Look for Ack
				if ( Simulator.team.getRoleState(RoleType.ROLE_MISSION_MANAGER) == RoleState.MM_ACK_PILOT ) {
					nextState(RoleState.PILOT_TX_MM, 1);
				}
				break;
			case PILOT_TX_MM:
				//No interruptions
				//TODO Handle interruptions
				
				break;
			case PILOT_END_MM:
				//Do nothing
				break;
			case PILOT_ACK_MM:
				//Do nothing
				break;
			case PILOT_RX_MM:
				//Look for end of TX
				//Whatever the MM does next it should be on the next time step so that it does not
				//appear that he is receiving after the PS stopped transmitting.
				if ( Simulator.team.getRoleState(RoleType.ROLE_MISSION_MANAGER) == RoleState.MM_END_PILOT ) {
					
					//Check the post office for data
					ArrayList<DataType> data = Simulator.removePosts(RoleState.MM_TX_PILOT);
					if ( !data.isEmpty() ) {
						if ( data.contains( DataType.TERMINATE_SEARCH ) ) {
							//Change our internal search state to TERMINATED
							_search_state = SearchState.TERMINATED;
							//Land the plane if flying, otherwise go idle
							if ( _uav_state == UAVState.FLYING || _uav_state == UAVState.LOITERING ) {
								Simulator.addPost(RoleState.PILOT_TX_UGUI, DataType.LAND);
								nextState(RoleState.PILOT_POKE_UGUI, 1);
							} else {
								nextState(RoleState.IDLE, 1);
							}
							
						} else if ( data.contains( DataType.SEARCH_AOI) ) {
							//Build a flight plan, and then launch if needed
							Simulator.addPost(RoleState.PILOT_TX_UGUI, DataType.FLIGHT_PLAN);
							nextState(RoleState.PILOT_POKE_UGUI, 1);
						} else if ( data.contains( DataType.MM_CMD_PAUSE) ) {
						} else if ( data.contains( DataType.MM_CMD_RESUME) ) {
						} else if ( data.contains( DataType.MM_CMD_PAUSE) ) {
						} else if ( data.contains( DataType.MM_CMD_PAUSE) ) {
							//TODO handle the MM Commands
						}else {
							nextState(RoleState.IDLE, 1);
						}
					} else {
						//TODO Go to a better state
						nextState(RoleState.IDLE, 1);
					}
				}
				break;
			case PILOT_LAUNCH_UAV:
				//Technically the pilot is observing the UAV here
			case PILOT_OBSERVING_UAV:
				//TODO Look for changes in UAV state
				
				if ( Simulator.team.getRoleState(RoleType.ROLE_MISSION_MANAGER) == RoleState.MM_POKE_PILOT ) {
					nextState(RoleState.PILOT_ACK_MM, 1);
				}
				
				break;
			case PILOT_OBSERVING_GUI:
				//TODO Look at the UGUI State and act based on what it says
				
				if ( Simulator.team.getRoleState(RoleType.ROLE_MISSION_MANAGER) == RoleState.MM_POKE_PILOT ) {
					nextState(RoleState.PILOT_ACK_MM, 1);
				}
				break;
			case PILOT_POST_FLIGHT:
				//Pilot is manually doing something with the plane, handle any interuptions here
				break;
			case PILOT_POKE_UGUI:
				//Look for Ack
				if ( Simulator.team.getRoleState(RoleType.ROLE_UAV_GUI) == RoleState.UGUI_ACK_PILOT ) {
					nextState(RoleState.PILOT_TX_UGUI, 1);
				}
				break;
			case PILOT_TX_UGUI:
				//TODO Handle interuptions while working with the GUI
				break;
			case PILOT_END_UGUI:
				//No interuptions
				break;
			case IDLE:
				//Look for commands from the MM or alerts from the UGUI
				if ( Simulator.team.getRoleState(RoleType.ROLE_MISSION_MANAGER) == RoleState.MM_POKE_PILOT ) {
					nextState(RoleState.PILOT_ACK_MM, 1);
				} else if ( Simulator.team.getRoleState(RoleType.ROLE_UAV_GUI) == RoleState.UGUI_AUDIBLE_ALARM ) {
					nextState(RoleState.PILOT_OBSERVING_GUI,1);
				}
				
				break;
			default:
				//Do nothing for states not mentioned
				break;
		}

	}

}
