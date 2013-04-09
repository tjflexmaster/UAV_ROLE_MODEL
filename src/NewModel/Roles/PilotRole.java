package NewModel.Roles;

import java.util.ArrayList;

import NewModel.Events.Event;
import NewModel.Simulation.Assumptions;
import NewModel.Simulation.Simulator;
import NewModel.Utils.DataType;
import NewModel.Utils.DurationGenerator;
import NewModel.Utils.PostOffice.POBOX;

public class PilotRole extends Role {
	
	/**
	 * PILOT STATE VARS
	 */

	//Default Values
	DataType _search_state = DataType.SEARCH_NONE;
	
	//UAV Related States
	RoleState _uav_state = RoleState.UAV_READY;
//	DataType _uav_state = DataType.UAV_READY;
	DataType _bat_state = DataType.UAV_BAT_OK;
	DataType _path_state = DataType.UAV_PATH_OK;
	DataType _hag_state = DataType.UAV_HAG_OK;
	DataType _signal_state = DataType.UAV_SIGNAL_OK;
	DataType _plan_state = DataType.UAV_FLIGHT_PLAN_NO;
	
	//Bad Path Internal Vars
	int _bad_path_start_time = 0;
	int _bad_path_allowance_threshold = 50; //How long to we allow for a bad path before landing the UAV?
	
	//Signal Lost Vars
	boolean _reset_flight_plan = false;
	
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
		//Each state has a designated duration
//		int duration = 1;
		//If a state isn't included then it doesn't deviate from the default
		switch(nextState()) {
			case PILOT_POKE_MM:
//				duration = 30;
				nextState(RoleState.IDLE, Assumptions.PILOT_POKE_MM_DUR);
				break;
			case PILOT_TX_MM:
				//TODO change this duration based on the data being transmitted
//				duration = 20;
				nextState(RoleState.PILOT_END_MM, Assumptions.PILOT_TX_MM_DUR);
				break;
			case PILOT_END_MM:
				nextState(RoleState.IDLE, 1);
				break;
			case PILOT_ACK_MM:
				nextState(RoleState.PILOT_RX_MM, 1);
				break;
			case PILOT_RX_MM:
				//TODO dont just receive forever
				nextState(RoleState.IDLE, Assumptions.PILOT_RX_MM_DUR);
				break;
			case PILOT_OBSERVING_UAV:
				//We should only get here if the UAV is flying
//				_uav_state = Simulator.getRoleState(RoleType.ROLE_UAV);
//				duration = 10;
				nextState(RoleState.PILOT_OBSERVING_GUI, Assumptions.PILOT_OBSERVE_UAV_DUR);
				break;
			case PILOT_OBSERVING_GUI:
//				if ( Simulator.getRoleState(RoleType.ROLE_UAV_GUI) != RoleState.UGUI_INACCESSIBLE ) {
//					_uav_state = Simulator.getRoleState(RoleType.ROLE_UAV);
//				}
//				duration = 60;
				if ( Simulator.getRoleState(RoleType.ROLE_UAV_GUI) == RoleState.UGUI_INACCESSIBLE ) {
					nextState(RoleState.PILOT_OBSERVING_UAV, 1);
				} else {
					nextState(RoleState.PILOT_OBSERVING_UAV, Assumptions.PILOT_OBSERVE_UGUI_DUR);
				}
				
				break;
			case PILOT_LAUNCH_UAV:
				//Give command to the GUI to take off
				//Assumption: The UGUI is working and the pilot can communicate as needed
				//Launch the UAV and then wait for it to leave the take off state
//				duration = 100;
//				_uav_state = Simulator.getRoleState(RoleType.ROLE_UAV);
				Simulator.addPost(POBOX.PILOT_UGUI, DataType.TAKE_OFF);
				nextState(null, 0);
				break;
			case PILOT_POKE_UGUI:
//				duration = 30;
				nextState(RoleState.IDLE, Assumptions.PILOT_POKE_UGUI_DUR);
				break;
			case PILOT_TX_UGUI:
				//TODO base this duration on the items being transmitted
//				duration = 15;
				nextState(RoleState.PILOT_END_UGUI, Assumptions.PILOT_TX_UGUI_DUR);
				break;
			case PILOT_END_UGUI:
				//After we do something on the GUI we click a "save button" signaling completion
//				duration = 1;
				nextState(RoleState.PILOT_WAIT_UGUI, 1);
				break;
			case PILOT_WAIT_UGUI:
				//Helper State, in 1 time steps the UGUI passes data to the UAV
				// in the next time step it updates itself.  After this the Pilot can then
				// read the latest data on the gui.
//				duration = 1;
				nextState(RoleState.PILOT_OBSERVING_GUI, 1);
				break;
			case PILOT_POST_FLIGHT:
				_uav_state = Simulator.getRoleState(RoleType.ROLE_UAV);
				if ( _uav_state == RoleState.UAV_LANDED ) {
					//Plane is on the ground
//					duration = 30;
					Simulator.addPost(POBOX.PILOT_UAV, DataType.POST_FLIGHT_COMPLETE); // Do this immediately to avoid looping
					nextState(RoleState.PILOT_POST_FLIGHT_COMPLETE, Assumptions.PILOT_POST_FLIGHT_LAND_DUR);
				} else if ( _uav_state == RoleState.UAV_CRASHED ) {
					//Recover the crashed UAV
//					duration = 100;
					nextState(RoleState.PILOT_POST_FLIGHT_COMPLETE, Assumptions.PILOT_POST_FLIGHT_CRASH_DUR);
				}
				break;
			case PILOT_POST_FLIGHT_COMPLETE:
				_uav_state = Simulator.getRoleState(RoleType.ROLE_UAV);
				if ( _uav_state == RoleState.UAV_CRASHED ) {
					Simulator.addPost(POBOX.PILOT_MM, DataType.SEARCH_AOI_FAILED);
					nextState(RoleState.PILOT_POKE_MM, 1);
				} else {
					if ( _search_state == DataType.SEARCH_ACTIVE ) {
						//Relaunch to finish search
						nextState(RoleState.PILOT_LAUNCH_UAV, 1);
					} else {
						//Nothing to do
						nextState(RoleState.IDLE, 1);
					}
				}
				break;
			case STARTING:
				nextState(RoleState.IDLE, 1);
				break;
			case IDLE:
				switch(_uav_state) {
					case UAV_FLYING:
					case UAV_LOITERING:
					case UAV_TAKE_OFF:
					case UAV_LANDING:
					case UAV_NO_SIGNAL:
						nextState(RoleState.PILOT_OBSERVING_GUI, 1);
						break;
					case UAV_LANDED:
					case UAV_CRASHED:
						nextState(RoleState.PILOT_POST_FLIGHT, 1);
						break;
					default:
						//Stay Idle forever
						nextState(null, 0);
						break;
				}
				break;
			default:
				nextState(null, 0);
				break;
		}
		
		return true;
	}
	
	@Override
	public void updateState() {
		switch( state() ) {
			case PILOT_POKE_MM:
				//Look for Ack
				if ( Simulator.getRoleState(RoleType.ROLE_MISSION_MANAGER) == RoleState.MM_ACK_PILOT ) {
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
				//Whatever the Pilot does next it should be on the next time step so that it does not
				//appear that he is receiving after the MM stopped transmitting.
				if ( Simulator.getRoleState(RoleType.ROLE_MISSION_MANAGER) == RoleState.MM_END_PILOT ) {
					
					//Check the post office for data
					ArrayList<DataType> data = Simulator.removePosts(POBOX.MM_PILOT);
					if ( !data.isEmpty() ) {
						if ( data.contains( DataType.TERMINATE_SEARCH ) ) {
							//Change our internal search state to TERMINATED
							_search_state = DataType.SEARCH_NONE;
							//Land the plane if flying, otherwise go idle
							if ( _uav_state == RoleState.UAV_FLYING || 
									_uav_state == RoleState.UAV_LOITERING || 
									_uav_state == RoleState.UAV_TAKE_OFF ||
									_uav_state == RoleState.UAV_NO_SIGNAL ) {
								Simulator.addPost(POBOX.PILOT_UGUI, DataType.LAND);
								nextState(RoleState.PILOT_POKE_UGUI, 1);
							} else {
								nextState(RoleState.IDLE, 1);
							}
							
						} else if ( data.contains( DataType.SEARCH_AOI) ) {
							_search_state = DataType.SEARCH_ACTIVE;
							//Build a flight plan
							//Note: The Pilot should launch the UAV after building the flight plan if it is grounded
							Simulator.addPost(POBOX.PILOT_UGUI, DataType.FLIGHT_PLAN);
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
				
				//TODO Get VA input as well
				break;
			case PILOT_LAUNCH_UAV:
				//Technically the pilot is observing the UAV here so we can update the uav state
				_uav_state = Simulator.getRoleState(RoleType.ROLE_UAV);
				//Listen for GUI alarm
				if ( Simulator.getRoleState(RoleType.ROLE_UAV_GUI) == RoleState.UGUI_AUDIBLE_ALARM ) {
					nextState(RoleState.PILOT_OBSERVING_GUI, 1);
				} else {
					ArrayList<DataType> data = Simulator.removePosts(POBOX.UAV_PILOT);
					if ( !data.isEmpty() ) {
						for ( DataType info : data ) {
							switch(info) {
								case UAV_HAG_OK:
								case UAV_HAG_LOW:
									_hag_state = info;
									break;
								default:
									break;
							}//end switch
						}//end for
					}
					
					//Decide what to do if anything
					if ( _uav_state == RoleState.UAV_CRASHED || _uav_state == RoleState.UAV_LANDED ) {
						//Go pickup the UAV
						nextState(RoleState.PILOT_POST_FLIGHT, 1);
					} else if ( _uav_state == RoleState.UAV_FLYING || _uav_state == RoleState.UAV_LOITERING || _uav_state == RoleState.UAV_NO_SIGNAL ) {
						//Now we watch what is going on
						nextState(RoleState.PILOT_OBSERVING_GUI, 1);
					} else if ( _hag_state == DataType.UAV_HAG_LOW ) {
						//TODO does it matter what the uav state is?
						Simulator.addPost(POBOX.PILOT_UGUI, DataType.FLIGHT_PLAN);
						nextState(RoleState.PILOT_POKE_UGUI, 1);
					}
				}
				
				
				break;
			case PILOT_OBSERVING_UAV:
				_uav_state = Simulator.getRoleState(RoleType.ROLE_UAV);
				
				if ( Simulator.team.getRoleState(RoleType.ROLE_MISSION_MANAGER) == RoleState.MM_POKE_PILOT ) {
					nextState(RoleState.PILOT_ACK_MM, 1);
				} else if ( Simulator.getRoleState(RoleType.ROLE_UAV_GUI) == RoleState.UGUI_AUDIBLE_ALARM ) {
					nextState(RoleState.PILOT_OBSERVING_GUI, 1);
				} else {
					ArrayList<DataType> data = Simulator.removePosts(POBOX.UAV_PILOT);
					if ( !data.isEmpty() ) {
						for ( DataType info : data ) {
							switch(info) {
								case UAV_HAG_OK:
								case UAV_HAG_LOW:
									_hag_state = info;
									break;
								default:
									break;
							}//end switch
						}//end for
					}
					
					//Decide what to do if anything
					if ( _uav_state == RoleState.UAV_CRASHED || _uav_state == RoleState.UAV_LANDED ) {
						nextState(RoleState.PILOT_POST_FLIGHT, 1);
					} else if ( _hag_state == DataType.UAV_HAG_LOW ) {
						//TODO does it matter what the uav state is?
						Simulator.addPost(POBOX.PILOT_UGUI, DataType.FLIGHT_PLAN);
						nextState(RoleState.PILOT_POKE_UGUI, 1);
					} else if( _uav_state == RoleState.UAV_READY ) {
						//If the uav is ready and we have an active search then launch the UAV
						if ( _search_state == DataType.SEARCH_ACTIVE ) {
							if ( _plan_state == DataType.UAV_FLIGHT_PLAN_NO ) {
								Simulator.addPost(POBOX.PILOT_UGUI, DataType.FLIGHT_PLAN);
								nextState(RoleState.PILOT_POKE_UGUI, 1);
							} else {
								nextState(RoleState.PILOT_LAUNCH_UAV, 1);
							}
						}
					}
				}
				
				break;
			case PILOT_OBSERVING_GUI:
				//These are ordered as priority
				if ( Simulator.getRoleState(RoleType.ROLE_MISSION_MANAGER) == RoleState.MM_POKE_PILOT ) {
					nextState(RoleState.PILOT_ACK_MM, 1);
				} else if ( Simulator.getRoleState(RoleType.ROLE_UAV_GUI) != RoleState.UGUI_INACCESSIBLE ) {
					ArrayList<DataType> data = Simulator.removePosts(POBOX.UGUI_PILOT);
					if ( !data.isEmpty() ) {
						//Update the UAV state
						for( DataType info : data ) {
							switch(info) {
								case UAV_BAT_OK:
								case UAV_BAT_LOW:
									_bat_state = info;
									break;
								case UAV_HAG_OK:
								case UAV_HAG_LOW:
									_hag_state = info;
									break;
								case UAV_PATH_OK:
									_path_state = info;
									_bad_path_start_time = 0;
									break;
								case UAV_PATH_BAD:
									if ( _path_state != info ) {
										_bad_path_start_time = Simulator.getTime();
									}
									_path_state = info;
									break;
								case UAV_FLIGHT_PLAN_YES:
								case UAV_FLIGHT_PLAN_NO:
									_plan_state = info;
									break;
								default:
									//Do nothing
									break;
							}
						}//end for
					}
					
					_uav_state = Simulator.getRoleState(RoleType.ROLE_UAV);
					
					//Based on the Pilot GUI observations what do we do next
					//INTERNAL STATE MACHINE
					if ( _uav_state == RoleState.UAV_FLYING ) {
						if ( _hag_state == DataType.UAV_HAG_LOW ) {
							Simulator.addPost(POBOX.PILOT_UGUI, DataType.FLIGHT_PLAN);
							nextState(RoleState.PILOT_POKE_UGUI, 1);
						} else if ( _bat_state == DataType.UAV_BAT_LOW ) {
							//If battery is low then have it land
							Simulator.addPost(POBOX.PILOT_UGUI, DataType.LAND);
							nextState(RoleState.PILOT_POKE_UGUI, 1);
//							} else if ( _signal_state == DataType.UAV_SIGNAL_LOST ) {
//								//Wait until we have signal again, then change the flight plan
//								_reset_flight_plan = true;
//							} else if ( _signal_state == DataType.UAV_SIGNAL_OK && _reset_flight_plan ) {
//								Simulator.addPost(POBOX.PILOT_UGUI, DataType.FLIGHT_PLAN);
//								nextState(RoleState.PILOT_POKE_UGUI, 1);
//								_reset_flight_plan = false;
						} else if ( _path_state == DataType.UAV_PATH_BAD && !allowBadPath() ) {
							//If the UAV cannot fly the path specified because of wind or hardware malfunction then land the plan
							Simulator.addPost(POBOX.PILOT_UGUI, DataType.LAND);
							nextState(RoleState.PILOT_POKE_UGUI, 1);
						}
					} else if ( _uav_state == RoleState.UAV_LOITERING ) {
						if ( _hag_state == DataType.UAV_HAG_LOW ) {
							Simulator.addPost(POBOX.PILOT_UGUI, DataType.FLIGHT_PLAN);
							nextState(RoleState.PILOT_POKE_UGUI, 1);
							
						} else if ( _bat_state == DataType.UAV_BAT_LOW ) {
							//If battery is low then have it land
							Simulator.addPost(POBOX.PILOT_UGUI, DataType.LAND);
							nextState(RoleState.PILOT_POKE_UGUI, 1);
							
//							} else if ( _signal_state == DataType.UAV_SIGNAL_LOST ) {
//								//Wait until we have signal again, then change the flight plan
//								_reset_flight_plan = true;
//								
//							} else if ( _signal_state == DataType.UAV_SIGNAL_OK && _reset_flight_plan ) {
//								Simulator.addPost(POBOX.PILOT_UGUI, DataType.FLIGHT_PLAN);
//								nextState(RoleState.PILOT_POKE_UGUI, 1);
//								_reset_flight_plan = false;
//								
						} else if ( _path_state == DataType.UAV_PATH_BAD && !allowBadPath() ) {
							//If the UAV cannot fly the path specified because of wind or hardware malfunction then land the plan
							Simulator.addPost(POBOX.PILOT_UGUI, DataType.LAND);
							nextState(RoleState.PILOT_POKE_UGUI, 1);
							
						} else if ( _plan_state == DataType.UAV_FLIGHT_PLAN_NO ) {
							//Assumption: Flight Plan is now complete
							if ( _search_state == DataType.SEARCH_ACTIVE ) {
								_search_state = DataType.SEARCH_COMPLETE;
								//We should let the MM know
								Simulator.addPost(POBOX.PILOT_MM, DataType.SEARCH_AOI_COMPLETE);
								nextState(RoleState.PILOT_POKE_MM, 1);
							}
						}
						
					} else if ( _uav_state == RoleState.UAV_LANDING ) {
						if ( _path_state == DataType.UAV_PATH_BAD && !allowBadPath() ) {
							//UAV cannot fly, pilot is unable to execute search
							Simulator.addPost(POBOX.PILOT_MM, DataType.SEARCH_AOI_FAILED);
							nextState(RoleState.PILOT_POKE_MM, 1);
						}
						
					} else if ( _uav_state == RoleState.UAV_CRASHED || _uav_state == RoleState.UAV_LANDED ) {
						nextState(RoleState.PILOT_POST_FLIGHT, 1);
						
					} else if ( _uav_state == RoleState.UAV_TAKE_OFF ) {
						//While the UAV is taking off the pilot is in launch mode
						
					} else if ( _uav_state == RoleState.UAV_READY ) {
						if ( _plan_state == DataType.UAV_FLIGHT_PLAN_YES ) {
							nextState(RoleState.PILOT_LAUNCH_UAV, 1);
						} else {
							if ( _search_state == DataType.SEARCH_ACTIVE ) {
								Simulator.addPost(POBOX.PILOT_UGUI, DataType.FLIGHT_PLAN);
								nextState(RoleState.PILOT_POKE_UGUI, 1);
							}
						}
					} else if ( _uav_state == RoleState.UAV_NO_SIGNAL ) {
						//If the UAV has no signal then we want to send a new Flight Path
						//After this we wait and see
						if ( _search_state == DataType.SEARCH_ACTIVE ) {
							Simulator.addPost(POBOX.PILOT_UGUI, DataType.FLIGHT_PLAN);
							nextState(RoleState.PILOT_POKE_UGUI, 1);
						}
					}
					
					
				}
				break;
			case PILOT_POST_FLIGHT:
				//Pilot is manually doing something with the plane, handle any interruptions here
				//TODO Handle interruptions
				break;
			case PILOT_POST_FLIGHT_COMPLETE:
				//Helper Method
				break;
			case PILOT_POKE_UGUI:
				//Look for Ack
				if ( Simulator.team.getRoleState(RoleType.ROLE_UAV_GUI) != RoleState.UGUI_INACCESSIBLE ) {
					nextState(RoleState.PILOT_TX_UGUI, 1);
				}
				break;
			case PILOT_TX_UGUI:
				//TODO Handle interruptions while working with the GUI
				break;
			case PILOT_END_UGUI:
			case PILOT_WAIT_UGUI:
				//No interruptions
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
	
	
	@Override
	public void processEvents(ArrayList<Event> events) {
		//Do nothing
	}
	
	
	/**
	 * Private HELPER METHODS
	 */
	
	
	/**
	 * Return true or false based on whether or not the UAV has been unable
	 * to follow the flight plan for a given length of time
	 */
	private boolean allowBadPath()
	{
		if ( Simulator.getTime() - _bad_path_start_time >= _bad_path_allowance_threshold && _bad_path_start_time > 0 ) {
			return false;
		}
		return true;
	}

}
