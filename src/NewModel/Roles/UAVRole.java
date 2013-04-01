package NewModel.Roles;

import java.util.ArrayList;

import NewModel.Events.Event;
import NewModel.Events.EventType;
import NewModel.Simulation.Simulator;
import NewModel.Utils.DataType;
import NewModel.Utils.PostOffice.POBOX;

public class UAVRole extends Role {
	
	/**
	 * UAV INTERNAL STATES
	 */
	//Takeoff and Landing vars
	int _take_off_duration = 50;
	int _take_off_start_time = 0;
	int _landing_duration = 50;
	int _landing_start_time = 0;
	
	//Battery Internal Vars
	boolean _battery_active = false;
	int _battery_duration = 500;
	int _battery_start_time = 0;
	int _low_battery_threshold = 70;
	
	//Flight Plan Internal Vars
	boolean _flight_plan = false;
	int _flight_plan_start_time = 0;
	int _flight_plan_duration = 100;
	int _flight_plan_pause_time = 0;
	
	//HAG Internal Vars
	int _hag_duration = 100;
	int _hag_start_time = 0;
	
	//SIGNAL Internal Vars
	int _signal_duration = 100;
	int _signal_start_time = 0;
	
	//FLIGHT Path internal vars
	int _bad_flight_duration = 100;
	int _bad_flight_start_time = 0;
	
//	DataType _uav_state = DataType.UAV_READY;
	DataType _bat_state = DataType.UAV_BAT_OK;
	DataType _path_state = DataType.UAV_PATH_OK;
	DataType _hag_state = DataType.UAV_HAG_OK;
//	DataType _signal_state = DataType.UAV_SIGNAL_OK;
	DataType _plan_state = DataType.UAV_FLIGHT_PLAN_NO;
	
	/**
	 * END UAV STATES
	 */

	public UAVRole()
	{
		type(RoleType.ROLE_UAV);
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
		int remaining = getRemainingFlightTime(); //Find this out everytime
		
		//If signal has returned then make note of it here
//		isSignalLost();
		
		//If a state isn't included then it doesn't deviate from the default
		switch(nextState()) {
			case UAV_TAKE_OFF:
				//Assume constant take off duration
				if ( _take_off_start_time != 0 ) {
					duration = Math.max(1, _take_off_start_time + _take_off_duration - Simulator.getTime());
				} else {
					duration = _take_off_duration;
					_take_off_start_time = Simulator.getTime();
				}
				
				
				if ( !_battery_active ) {
					//Assume battery duration is the same every time
					_battery_duration = 500;
					_battery_start_time = Simulator.getTime();
					_battery_active = true;
				}
				
				if ( _flight_plan ) {
					nextState(RoleState.UAV_FLYING, duration);
				} else {
					nextState(RoleState.UAV_LOITERING, duration);
				}
				
				//_uav_state = DataType.UAV_TAKE_OFF;
				break;
			case UAV_LANDING:
				//Assume constant take off duration
				if ( _landing_start_time != 0 ) {
					duration = Math.max(1, _landing_start_time + _landing_duration - Simulator.getTime());
				} else {
					duration = _landing_duration;
					_landing_start_time = Simulator.getTime();
					//First clean up the flight plan
					pauseFlightPlan();
				}
				
				
				//It takes time to land, if we land too late then we will crash,
				//check to see if there is time to land.
				if ( duration > remaining ) {
					nextState(RoleState.UAV_CRASHED, remaining);
				} else {
					nextState(RoleState.UAV_LANDED, duration);
				}
				//_uav_state = DataType.UAV_LANDING;
				break;
			case UAV_FLYING:
				//When the UAV is flying it will try to follow a flight plan.  We assume all fligt plans are of length 100
				//When a flight plan is complete the UAV will loiter.
				//We need to keep track of the proposed flight time
				
				//Clean up Take off and Landing variables
				_take_off_start_time = 0;
				_landing_start_time = 0;
				
				//If no flight plan then loiter
				if ( _flight_plan ) {
					
					if ( _flight_plan_start_time == 0 ) {
						//Update the flight plan start time to now
						_flight_plan_start_time = Simulator.getTime();
					}
					
					//The duration is set when the flight plan is received or when we enter another state besided flying.
					//We will fly until the plane crashes or the flight plan is done
					if ( _flight_plan_duration >= remaining ) {
					
						if ( _hag_state == DataType.UAV_HAG_LOW && _bat_state == DataType.UAV_BAT_LOW ) {
							nextState(RoleState.UAV_CRASHED, remaining);
						} else if ( _hag_state == DataType.UAV_HAG_LOW ) {
							nextState(RoleState.UAV_CRASHED, remaining);
						} else if ( _bat_state == DataType.UAV_BAT_LOW ) {
							nextState(RoleState.UAV_CRASHED, remaining);
//						} else if ( _signal_state == DataType.UAV_SIGNAL_LOST ) {
//							nextState(RoleState.UAV_CRASHED, remaining);
						} else {
							if ( remaining > _low_battery_threshold ) {
								//Our next state is the same, except that we want to queue when the battery will become low
								nextState(state(), remaining - _low_battery_threshold);
							} else {
								//We are now low battery, we will crash unless something is changed.
								//Place this in our global message box to the GUI
								Simulator.addPost(POBOX.UAV_UGUI, DataType.UAV_BAT_LOW);
								nextState(RoleState.UAV_CRASHED, remaining);
							}
						}
					} else {
						nextState(RoleState.UAV_LOITERING, _flight_plan_duration);
					}
				} else {
					nextState(RoleState.UAV_LOITERING, 1);
				}
				
				//_uav_state = DataType.UAV_FLYING;
				break;
			case UAV_LOITERING:
				//When we are flying we will update our state when we have low battery
				//When we set our state to Flying we need to check the state of battery and hag
				
				//Clean up Take off and Landing variables
				_take_off_start_time = 0;
				_landing_start_time = 0;
				
				pauseFlightPlan();
				
				//Handle how long we will loiter
				if ( _hag_state == DataType.UAV_HAG_LOW && _bat_state == DataType.UAV_BAT_LOW ) {
					nextState(RoleState.UAV_CRASHED, remaining);
				} else if ( _hag_state == DataType.UAV_HAG_LOW ) {
					nextState(RoleState.UAV_CRASHED, remaining);
				} else if ( _bat_state == DataType.UAV_BAT_LOW ) {
					nextState(RoleState.UAV_CRASHED, remaining);
				} else {
					if ( remaining > _low_battery_threshold ) {
						//Our next state is the same, except that we want to queue when the battery will become low
						nextState(state(), remaining - _low_battery_threshold);
					} else {
						//We are now low battery, we will crash unless something is changed.
						//Place this in our global message box to the GUI
						_bat_state = DataType.UAV_BAT_LOW;
//						Simulator.addPost(POBOX.UAV_UGUI, DataType.UAV_BAT_LOW);
						nextState(RoleState.UAV_CRASHED, remaining);
					}
				}
				//_uav_state = DataType.UAV_LOITERING;
				break;
			case UAV_CRASHED:
//				_signal_state = DataType.UAV_SIGNAL_LOST;
				//_uav_state = DataType.UAV_CRASHED;
				nextState(null,0);
				break;
			case UAV_LANDED:
				//Clean up Take off and Landing variables
				_take_off_start_time = 0;
				_landing_start_time = 0;
				
				//_uav_state = DataType.UAV_LANDED;
				nextState(null, 0);
				break;
			case UAV_READY:
				//Clean up Take off and Landing variables
				_take_off_start_time = 0;
				_landing_start_time = 0;
				
//				isSignalLost();
				
				//reset all of the internal state values
				//Probably doesn't need to be done
//				_signal_state = DataType.UAV_SIGNAL_OK;
				_hag_state = DataType.UAV_HAG_OK;
				_bat_state = DataType.UAV_BAT_OK;
				_path_state = DataType.UAV_PATH_OK;
				break;
			case UAV_NO_SIGNAL:
				
				pauseFlightPlan();
				
				int flight_time = getRemainingFlightTime();
				int signal_time = getRemainingLostSignalTime();
				if ( flight_time <= signal_time ) {
					nextState(RoleState.UAV_CRASHED, flight_time);
				} else {
					if ( _flight_plan && getFlightPlanTime() > 0 ) {
						nextState(RoleState.UAV_FLYING, signal_time);
					} else {
						nextState(RoleState.UAV_LOITERING, signal_time);
					}
				}
				break;
			case STARTING:
				_flight_plan = false;
				nextState(RoleState.UAV_READY, 1);
				break;
			default:
				nextState(null, 0);
				break;
		}
		
		//Send Data to Listeners
		sendUAVDataToUGUI();
		sendUAVDataToPilot();
		
		return true;
	}
	
	@Override
	public void updateState() {
		ArrayList<DataType> data;
		
		//Look at different things depending on the state
		switch( state() ) {
			case UAV_TAKE_OFF:
				//If we are taking off accept commands from the UGUI
				//Check the post office for data
				data = Simulator.removePosts(POBOX.UGUI_UAV);
				if ( !data.isEmpty() ) {
					
					//Initialize our next state to the current next state
					RoleState next_state = nextState();
					int next_time = nextStateTime();
					
					//Loop through all commands sent by the GUI while landing
					for(DataType info : data) {
						switch(info) {
							case LOITER:
								//Go to Loitering after taking off
								next_state = RoleState.UAV_LOITERING;
								next_time = nextStateTime();
								break;
							case KILL:
								//Crash the UAV on purpose
								//Perform this immediately
								nextState(RoleState.UAV_CRASHED, 1);
								return; //Don't do any more processing
							case LAND:
								//Land after finishing takeoff
								next_state = RoleState.UAV_LANDING;
								next_time = nextStateTime();
								break;
							case FLIGHT_PLAN:
								//Change our flight plan
								_flight_plan = true;
								_flight_plan_duration = 100;
								_flight_plan_start_time = 0;
								_flight_plan_pause_time = 0;
								_plan_state = DataType.UAV_FLIGHT_PLAN_YES;
								next_state = RoleState.UAV_FLYING;
								next_time = nextStateTime();
								break;
							case RESUME:
							case TAKE_OFF:
							default:
								//Ignore
								break;
						}//end switch
						
					}//end for loop
					
					//After looking at all commands update our next state
					nextState(next_state, next_time);
				}
				break;
			case UAV_LANDING:
				//Check the post office for data
				data = Simulator.removePosts(POBOX.UGUI_UAV);
				if ( !data.isEmpty() ) {
					
					//Initialize our next state to the current next state
					RoleState next_state = nextState();
					int next_time = nextStateTime();
					
					//Loop through all commands sent by the GUI while landing
					for(DataType info : data) {
						switch(info) {
							case LOITER:
								//Immediately start Loitering
								next_state = RoleState.UAV_LOITERING;
								next_time = 1;
								break;
							case KILL:
								//Crash the UAV on purpose
								//Perform this immediately
								nextState(RoleState.UAV_CRASHED, 1);
								return; //Don't do any more processing
							case RESUME:
							case LAND:
								next_state = nextState();
								next_time = nextStateTime();
								break;
							case FLIGHT_PLAN:
								//Immediately begin flying the new flight plan
								_flight_plan = true;
								_flight_plan_duration = 100;
								_flight_plan_start_time = 0;
								_flight_plan_pause_time = 0;
								_plan_state = DataType.UAV_FLIGHT_PLAN_YES;
								//ASSUMPTION: Start flying when given a flight plan
								next_state = RoleState.UAV_FLYING;
								next_time = 1;
								break;
							case TAKE_OFF:
							default:
								//Ignore
								break;
						}//end switch
						
					}//end for loop
					
					//After looking at all commands update our next state
					nextState(next_state, next_time);
				}
				break;
			case UAV_FLYING:
				//If the GUI goes down then the UAV moves into NO_SIGNAL mode
				if ( Simulator.getRoleState(RoleType.ROLE_UAV_GUI) == RoleState.UGUI_INACCESSIBLE ) {
					nextState(RoleState.UAV_NO_SIGNAL, 1);
				} else {
					//Check the post office for data
					data = Simulator.removePosts(POBOX.UGUI_UAV);
					if ( !data.isEmpty() ) {
						
						//Initialize our next state to the current next state
						RoleState next_state = nextState();
						int next_time = nextStateTime();
						
						//Loop through all commands sent by the GUI while flying
						for(DataType info : data) {
							switch(info) {
								case LOITER:
									//Go to Loitering
									next_state = RoleState.UAV_LOITERING;
									next_time = 1;
									break;
								case KILL:
									//Crash the UAV on purpose
									//Perform this immediately
									nextState(RoleState.UAV_CRASHED, 1);
									return; //Don't do any more processing
								case LAND:
									next_state = RoleState.UAV_LANDING;
									next_time = 1;
									break;
								case FLIGHT_PLAN:
									//set new flight plan
									_flight_plan = true;
									_flight_plan_duration = 100;
									_flight_plan_start_time = 0;
									_flight_plan_pause_time = 0;
									_plan_state = DataType.UAV_FLIGHT_PLAN_YES;
									
									//Assume that the HAG was corrected
									if ( _hag_state == DataType.UAV_HAG_LOW ) {
										_hag_state = DataType.UAV_HAG_OK;
									}
									next_state = RoleState.UAV_FLYING;
									next_time = 1;
									break;
								case RESUME:
									next_state = nextState();
									next_time = nextStateTime();
									break;
								case TAKE_OFF:
								default:
									//Ignore
									break;
							}//end switch
							
						}//end for loop
						
						//After looking at all commands update our next state
						nextState(next_state, next_time);
					}
				}
				break;
			case UAV_LOITERING:
				//If the GUI goes down then the UAV moves into NO_SIGNAL mode
				if ( Simulator.getRoleState(RoleType.ROLE_UAV_GUI) == RoleState.UGUI_INACCESSIBLE ) {
					nextState(RoleState.UAV_NO_SIGNAL, 1);
				} else {
					//Check the post office for data
					data = Simulator.removePosts(POBOX.UGUI_UAV);
					if ( !data.isEmpty() ) {
						
						//Initialize our next state to the current next state
						RoleState next_state = nextState();
						int next_time = nextStateTime();
						
						//Loop through all commands sent by the GUI while landing
						for(DataType info : data) {
							switch(info) {
								case LOITER:
									//Go to Loitering after taking off
									next_state = nextState();
									next_time = nextStateTime();
									break;
								case KILL:
									//Crash the UAV on purpose
									//Perform this immediately
									nextState(RoleState.UAV_CRASHED, 1);
									return; //Don't do any more processing
								case LAND:
									next_state = RoleState.UAV_LANDING;
									next_time = 1;
									break;
								case RESUME:
									next_state = RoleState.UAV_FLYING;
									_flight_plan_pause_time = 0;
									next_time = 1;
									break;
								case FLIGHT_PLAN:
									_flight_plan = true;
									_flight_plan_duration = 100;
									_plan_state = DataType.UAV_FLIGHT_PLAN_YES;
									
									//Assume HAG was corrected
									if ( _hag_state == DataType.UAV_HAG_LOW ) {
										_hag_state = DataType.UAV_HAG_OK;
									}
									next_state = RoleState.UAV_FLYING;
									next_time = 1;
									_flight_plan_pause_time = 0;
								case TAKE_OFF:
								default:
									//Ignore
									break;
							}//end switch
							
						}//end for loop
						
						//After looking at all commands update our next state
						nextState(next_state, next_time);
					}
				}
				break;
			case UAV_CRASHED:
				//Do Nothing we cannot communicate
				break;
			case UAV_LANDED:
				//Check the post office for data
				data = Simulator.removePosts(POBOX.UGUI_UAV);
				if ( !data.isEmpty() ) {
					
					if ( data.contains(DataType.FLIGHT_PLAN) ) {
						_flight_plan = true;
						_flight_plan_duration = 100;
						_flight_plan_start_time = 0;
						_flight_plan_pause_time = 0;
						_plan_state = DataType.UAV_FLIGHT_PLAN_YES;
					}
				}
				
				//Now get Pilot Data, this puts us back into the ready state
				data = Simulator.removePosts(POBOX.PILOT_UAV);
				if ( !data.isEmpty() ) {
					if ( data.contains(DataType.POST_FLIGHT_COMPLETE) ) {
						nextState(RoleState.UAV_READY, 1);
					}
				}
				break;
			case UAV_READY:
				//Check the post office for data
				data = Simulator.removePosts(POBOX.UGUI_UAV);
				if ( !data.isEmpty() ) {
					
					//Initialize our next state to the current next state
					RoleState next_state = nextState();
					int next_time = nextStateTime();
					
					//Loop through all commands sent by the GUI
					for(DataType info : data) {
						switch(info) {
							case LOITER:
							case KILL:
							case LAND:
							case RESUME:
								next_state = nextState();
								next_time = nextStateTime();
								break;
							case FLIGHT_PLAN:
								_flight_plan = true;
								_flight_plan_duration = 100;
								_flight_plan_start_time = 0;
								_flight_plan_pause_time = 0;
								_plan_state = DataType.UAV_FLIGHT_PLAN_YES;
								next_state = RoleState.UAV_FLYING;
								next_time = 1;
								break;
							case TAKE_OFF:
								next_state = RoleState.UAV_TAKE_OFF;
								next_time = 1;
							default:
								//Ignore
								break;
						}//end switch
						
					}//end for loop
					
					//After looking at all commands update our next state
					nextState(next_state, next_time);
				}
				break;
			case UAV_NO_SIGNAL:
				//No Signals so we don't listen to anything
				break;
			case STARTING:
			default:
				//Ignore
				break;
		}//end switch
		
		/**
		 * HAndle timeouts on events
		 */
		//Flight path is bad
		if ( _path_state == DataType.UAV_PATH_BAD ) {
			if ( !isFlightBad() ) {
				_path_state = DataType.UAV_PATH_OK;
				_bad_flight_start_time = 0;
				System.out.println("UAV PATH Restored");
				if ( nextStateTime() > 1 ) { 
					nextState(state(), 1);
				}
			}
		}
		
//		//Signal is lost
//		if ( _signal_state == DataType.UAV_SIGNAL_LOST ) {
//			if ( !isSignalLost() ) {
//				_signal_state = DataType.UAV_SIGNAL_OK;
//				_signal_start_time = 0;
//				System.out.println("UAV Signal Restored");
//				if ( nextStateTime() > 1 ) { 
//					nextState(state(), 1);
//				}
//			}
//		}
	}
	
	@Override
	public void processEvents(ArrayList<Event> events) {
		//Look for specific event types
		for( Event e : events ) {
			switch(e.type()) {
				case UAV_LOW_BATTERY:
					createLowBatteryEvent(e.duration());
					break;
				case UAV_LOST_SIGNAL:
					createLostSignalEvent(e.duration());
					break;
				case UAV_LOW_HAG:
					createHAGEvent(e.duration());
					break;
				default:
					//Do nothing this event isn't handled
					break;
			}
		}
	}
	
	/**
	 * /////////////////////////////PRIVATE HELPER METHODS///////////////////////////////////////////
	 */
	
	
	
	/**
	 * This sets the HAG to low and will cause the UAV to crash if no
	 * flight plan change is received within the recover time.
	 * @param duration  How long before the UAV will crash
	 */
	private void createHAGEvent(int duration)
	{
		//Only create this event if the UAV is FLYING
		if ( state() == RoleState.UAV_FLYING ) {
			_hag_duration = duration;
			_hag_start_time = Simulator.getTime();
			_hag_state = DataType.UAV_HAG_LOW;
			//Stay in the same state but trigger a state change so those watching the
			//UAV will see the change in state
			nextState(state(), 1);
			System.out.println("Created HAG Event of duration: " + duration);
		}
		
		System.out.println("Unable to Create HAG Event, UAV is not Flying");
	}
	
	
	
	/**
	 * This sets the UAV state to a new follow flight path state
	 * @param recover_time
	 */
	private void createBadFlightEvent(int duration)
	{
		switch(state()) {
			case UAV_FLYING:
			case UAV_LOITERING:
				_bad_flight_duration = duration;
				_bad_flight_start_time = Simulator.getTime();
				_path_state = DataType.UAV_PATH_BAD;
				nextState(state(), 1);
				System.out.println("Created Bad Path Event of duration: " + duration);
				break;
			default:
				System.out.println("Unable to Create Bad Flight, UAV is not Flying or Loitering");
				break;
		}
		//Stay in the same state but trigger a state change so those watching the
		//UAV will see the change in state
		
	}
	
	/**
	 * This create signal event
	 * @return
	 */
	private void createLostSignalEvent(int duration)
	{
//		_signal_state = DataType.UAV_SIGNAL_LOST;
		_signal_start_time = Simulator.getTime();
		_signal_duration = duration;
		nextState(RoleState.UAV_NO_SIGNAL, 1);
		
//		System.out.println("Created Lost Signal Event of duration: " + duration);
	}
	
	/**
	 * Create a low battery event
	 * @return
	 */
	private void createLowBatteryEvent(int duration)
	{
		switch(state()) {
			case UAV_FLYING:
			case UAV_LOITERING:
			case UAV_LANDING:
			case UAV_TAKE_OFF:
				int battery_dead = Simulator.getTime() + duration;
				_battery_duration = battery_dead - _battery_start_time;
				_low_battery_threshold = duration;
				_bat_state = DataType.UAV_BAT_LOW;
				nextState(state(), 1);
				System.out.println("Created Low Battery Event of duration: " + duration);
				break;
			default:
				System.out.println("Unable to Create Low Battery Event, UAV is not Airborne");
				break;
		}
	}

	
	
	
	
	
	
	/**
	 * Looks are HAG and Battery to determine how much longer the UAV can fly before crashing
	 * @return
	 */
	private int getRemainingFlightTime()
	{
		int remaining_bat = Math.max(0, _battery_start_time + _battery_duration - Simulator.getTime() );
		int remaining_hag = Math.max(0, _hag_start_time + _hag_duration - Simulator.getTime() );
		if ( _hag_state == DataType.UAV_HAG_LOW && _bat_state == DataType.UAV_BAT_LOW ) {
			return Math.min(remaining_bat, remaining_hag);
		} else if ( _hag_state == DataType.UAV_HAG_LOW ) {
			return remaining_hag;
		} else {
			return remaining_bat;
		}
	}
	
	/**
	 * Lets us know if the signal is still lost
	 * @return
	 */
//	private boolean isSignalLost()
//	{
//		if ( _signal_state == DataType.UAV_SIGNAL_LOST )
//			if ( Simulator.getTime() - _signal_start_time > _signal_duration ) {
//				//Signal is back, update my state
//				_signal_state = DataType.UAV_SIGNAL_OK;
//				return false;
//			} else {
//				return true;
//		} else {
//			return false;
//		}
//	}
	
	private int getRemainingLostSignalTime()
	{
		int remaining_time = _signal_start_time + _signal_duration - Simulator.getTime();
		return Math.max(0, remaining_time);
	}
	
	private boolean isFlightBad()
	{
		if ( Simulator.getTime() - _bad_flight_start_time > _bad_flight_duration ) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Used to determine how much longer the UAV will follow the given flight path
	 * @return
	 */
	private int getFlightPlanTime()
	{
		//Make sure to take pauses into account
		int remaining_plan = _flight_plan_start_time + _flight_plan_duration - Simulator.getTime();
		if ( _flight_plan_pause_time >= _flight_plan_start_time ) {
			remaining_plan += Simulator.getTime() - _flight_plan_pause_time;
		}
		
		return Math.max(0, remaining_plan);
	}
	
	/**
	 * This method is used to pause the flight plan
	 */
	private void pauseFlightPlan()
	{
		if ( _flight_plan ) {
			
			//We need to fix the flight plans duration as we have already covered some of the flight plan
			//Do this by changing the duration to that of the remaining.
			_flight_plan_duration = getFlightPlanTime();
			
			//The flight plan is finished
			if ( _flight_plan_duration == 0 ) {
				_flight_plan = false;
				_plan_state = DataType.UAV_FLIGHT_PLAN_NO;
				_flight_plan_start_time = 0;
				//Let the GUI know that the flight plan is finished
//				Simulator.addPost(POBOX.UAV_UGUI, DataType.SEARCH_COMPLETE);
			} else {
			
				//Set the pause time
				//Only set the pause time if not already paused
				if ( _flight_plan_pause_time < _flight_plan_start_time ) {
					_flight_plan_pause_time = Simulator.getTime();
					_flight_plan_start_time = Simulator.getTime();
				}
			}
		}
	}
	
	/**
	 * This method sends the state of the UAV to the UGUI everytime the UAV changes it's state,
	 * If only an internal state has changed a state change should still be triggered
	 */
	private void sendUAVDataToUGUI()
	{
		if ( state() != RoleState.UAV_CRASHED || state() != RoleState.UAV_NO_SIGNAL ) {
			Simulator.clearPost(POBOX.UAV_UGUI);
		
		//Send data to the GUI that the flight plan is completed
//		Simulator.addPost(POBOX.UAV_PILOT, _uav_state);
//		if ( !isSignalLost() ) {
				Simulator.addPost(POBOX.UAV_UGUI, _hag_state);
				Simulator.addPost(POBOX.UAV_UGUI, _bat_state);
				Simulator.addPost(POBOX.UAV_UGUI, _path_state);
				Simulator.addPost(POBOX.UAV_UGUI, _plan_state);
//		}
//		Simulator.addPost(POBOX.UAV_UGUI, _signal_state);
		}
	}
	
	private void sendUAVDataToPilot()
	{
		Simulator.clearPost(POBOX.UAV_PILOT);
		
//		Simulator.addPost(POBOX.UAV_PILOT, _uav_state);
		Simulator.addPost(POBOX.UAV_PILOT, _hag_state);
	}
	
}
