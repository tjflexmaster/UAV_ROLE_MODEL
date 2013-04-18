package NewModel.Roles;

import java.util.ArrayList;

import NewModel.Events.Event;
import NewModel.Events.IEvent;
import NewModel.Simulation.Simulator;
import NewModel.Utils.DataType;
import NewModel.Utils.PostOffice.POBOX;

public class UAVRole extends Role {
	
	/**
	 * UAV INTERNAL STATES
	 */
	//Takeoff and Landing vars
//	int _take_off_duration = Assumptions.UAV_TAKE_OFF_DUR;
	int _take_off_start_time = 0;
//	int _landing_duration = Assumptions.UAV_LANDING_DUR;
	int _landing_start_time = 0;
	
	//Battery Internal Vars
	boolean _battery_active = false;
	int _battery_duration = 0;
	int _battery_start_time = 0;
	int _low_battery_threshold = 1;
	
	//Flight Plan Internal Vars
	boolean _flight_plan = false;
	int _flight_plan_start_time = 0;
	int _flight_plan_duration = 1;
	int _flight_plan_pause_time = 0;
	
	//HAG Internal Vars
	int _hag_duration = 1;
	int _hag_start_time = 0;
	
	//SIGNAL Internal Vars
	int _signal_duration = 1;
	int _signal_start_time = 0;
	
	//FLIGHT Path internal vars
	int _bad_flight_duration = 1;
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
		if ( nextStateTime() != Simulator.getInstance().getTime() ) {
			return false;
		}
		
		//Update to the next state
		state(nextState());
		
		
		//Now determine what our next state will be
		//Each state has a designated duration
		int duration = 1;
		int remaining_bat = getRemainingBatteryTime(); //Find this out everytime
//		int remaining_bad_path = getRemainingBadFlightTime();
		
		//Update the internal states
		//If we have reached low battery
		if (  getRemainingLowBatteryTime() == 0 )
			_bat_state = DataType.UAV_BAT_LOW;
		
		if ( getRemainingBadFlightTime() == 0 )
			_path_state = DataType.UAV_PATH_OK;
		
		
		//If a state isn't included then it doesn't deviate from the default
		switch(nextState()) {
			case UAV_TAKE_OFF:
				//Assume constant take off duration
				if ( _take_off_start_time != 0 ) {
					duration = Math.max(1, _take_off_start_time + Simulator.getInstance().duration(Duration.UAV_TAKE_OFF_DUR.name()) - Simulator.getInstance().getTime());
				} else {
					duration = Simulator.getInstance().duration(Duration.UAV_TAKE_OFF_DUR.name());
					_take_off_start_time = Simulator.getInstance().getTime();
				}
				
				
				if ( !_battery_active ) {
					//Assume battery duration is the same every time
					_battery_start_time = Simulator.getInstance().getTime();
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
					duration = Math.max(1, _landing_start_time + Simulator.getInstance().duration(Duration.UAV_LANDING_DUR.name()) - Simulator.getInstance().getTime());
				} else {
					duration = Simulator.getInstance().duration(Duration.UAV_LANDING_DUR.name());
					_landing_start_time = Simulator.getInstance().getTime();
					//First clean up the flight plan
					pauseFlightPlan();
				}
				
				
				//It takes time to land, if we land too late then we will crash,
				//check to see if there is time to land.
				if ( duration > remaining_bat ) {
					nextState(RoleState.UAV_CRASHED, remaining_bat);
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
					
					//If no flight plan start time exists then set it to now
					if ( _flight_plan_start_time == 0 || _flight_plan_start_time == _flight_plan_pause_time ) {
						//Update the flight plan start time to now
						_flight_plan_start_time = Simulator.getInstance().getTime();
					}
					
					//Logic for what state should come next while airborne
					setNextAirborneState();

					
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
				
				//Logic for what state should come next while airborne
				setNextAirborneState();
		
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
				_hag_start_time = 0;
				
				_bat_state = DataType.UAV_BAT_OK;
				_battery_start_time = 0;
				_low_battery_threshold = Simulator.getInstance().duration(Duration.UAV_LOW_BATTERY_THRESHOLD_DUR.name());
				_battery_duration = Simulator.getInstance().duration(Duration.UAV_BATTERY_DUR.name());
				_battery_active = false;
				
				_path_state = DataType.UAV_PATH_OK;
				break;
			case UAV_NO_SIGNAL:
				
				pauseFlightPlan();
				
				int flight_time = getRemainingBatteryTime();
				int signal_time = getRemainingLostSignalTime();
				if ( flight_time <= signal_time || signal_time <= 0 ) {
					nextState(RoleState.UAV_CRASHED, flight_time);
				} else {
					if ( _flight_plan && getRemainingFlightPlanTime() > 0 ) {
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
				data = Simulator.getInstance().removePosts(POBOX.UGUI_UAV);
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
								_flight_plan_duration = Simulator.getInstance().duration(Duration.UAV_FLIGHT_PLAN_DUR.name());
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
				data = Simulator.getInstance().removePosts(POBOX.UGUI_UAV);
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
								_flight_plan_duration = Simulator.getInstance().duration(Duration.UAV_FLIGHT_PLAN_DUR.name());
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
				if ( Simulator.getInstance().getRoleState(RoleType.ROLE_UAV_GUI) == RoleState.UGUI_INACCESSIBLE ) {
					nextState(RoleState.UAV_NO_SIGNAL, 1);
				} else {
					//Check the post office for data
					data = Simulator.getInstance().removePosts(POBOX.UGUI_UAV);
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
									_flight_plan_duration = Simulator.getInstance().duration(Duration.UAV_FLIGHT_PLAN_DUR.name());
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
				if ( Simulator.getInstance().getRoleState(RoleType.ROLE_UAV_GUI) == RoleState.UGUI_INACCESSIBLE ) {
					nextState(RoleState.UAV_NO_SIGNAL, 1);
				} else {
					//Check the post office for data
					data = Simulator.getInstance().removePosts(POBOX.UGUI_UAV);
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
									_flight_plan_duration = Simulator.getInstance().duration(Duration.UAV_FLIGHT_PLAN_DUR.name());
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
				data = Simulator.getInstance().removePosts(POBOX.UGUI_UAV);
				if ( !data.isEmpty() ) {
					
					if ( data.contains(DataType.FLIGHT_PLAN) ) {
						_flight_plan = true;
						_flight_plan_duration = Simulator.getInstance().duration(Duration.UAV_FLIGHT_PLAN_DUR.name());
						_flight_plan_start_time = 0;
						_flight_plan_pause_time = 0;
						_plan_state = DataType.UAV_FLIGHT_PLAN_YES;
					}
				}
				
				//Now get Pilot Data, this puts us back into the ready state
				data = Simulator.getInstance().removePosts(POBOX.PILOT_UAV);
				if ( !data.isEmpty() ) {
					if ( data.contains(DataType.POST_FLIGHT_COMPLETE) ) {
						nextState(RoleState.UAV_READY, 1);
					}
				}
				break;
			case UAV_READY:
				//Check the post office for data
				data = Simulator.getInstance().removePosts(POBOX.UGUI_UAV);
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
								_flight_plan_duration = Simulator.getInstance().duration(Duration.UAV_FLIGHT_PLAN_DUR.name());
								_flight_plan_start_time = 0;
								_flight_plan_pause_time = 0;
								_plan_state = DataType.UAV_FLIGHT_PLAN_YES;
								next_state = RoleState.UAV_READY;
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
				//This scenario is when the GUI goes down, the UAV enters a NO SIGNAL mode which will have it fly back to base and loiter.
				if ( getRemainingLostSignalTime() <= 0 && Simulator.getInstance().getRoleState(RoleType.ROLE_UAV_GUI) != RoleState.UGUI_INACCESSIBLE ) {
					if ( _flight_plan && getRemainingFlightPlanTime() > 0 ) {
						nextState(RoleState.UAV_FLYING, 1);
					} else {
						nextState(RoleState.UAV_LOITERING, 1);
					}
				}
				break;
			case STARTING:
			default:
				//Ignore
				break;
		}//end switch
		
		/**
		 * Handle timeouts on events
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
	public void processEvent(IEvent event) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void processEvents(ArrayList<Event> events) {
		//Look for specific event types
		for( Event e : events ) {
			switch(e.type()) {
				case UAV_BAD_PATH:
					createBadFlightEvent(e.duration());
					break;
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
			_hag_start_time = Simulator.getInstance().getTime();
			_hag_state = DataType.UAV_HAG_LOW;
			//Stay in the same state but trigger a state change so those watching the
			//UAV will see the change in state
			nextState(state(), 1);
//			System.out.println("Created HAG Event of duration: " + duration);
		} else {
			System.out.println("Unable to Create HAG Event, UAV is not Flying");
		}
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
			case UAV_NO_SIGNAL:
				_bad_flight_duration = duration;
				_bad_flight_start_time = Simulator.getInstance().getTime();
				_path_state = DataType.UAV_PATH_BAD;
				//Stay in the same state but trigger a state change so those watching the
				//UAV will see the change in state
				nextState(state(), 1);
				break;
			default:
				assert false : "Unable to Create Bad Flight Event, UAV is not Airborne";
//				System.out.println("Unable to Create Bad Flight, UAV is not Flying or Loitering");
				break;
		}
		
	}
	
	/**
	 * This create signal event
	 * @return
	 */
	private void createLostSignalEvent(int duration)
	{
//		_signal_state = DataType.UAV_SIGNAL_LOST;
		_signal_start_time = Simulator.getInstance().getTime();
		_signal_duration = duration;
		
		if ( state() == RoleState.UAV_FLYING || 
				state() == RoleState.UAV_TAKE_OFF || 
				state() ==RoleState.UAV_LOITERING || 
				state() ==RoleState.UAV_LANDING)
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
				int battery_dead = Simulator.getInstance().getTime() + duration;
				_battery_duration = battery_dead - _battery_start_time;
				_low_battery_threshold = duration;
				_bat_state = DataType.UAV_BAT_LOW;
				nextState(state(), 1);
//				System.out.println("Created Low Battery Event of duration: " + duration);
				break;
			default:
				assert false : "Unable to Create Low Battery Event, UAV is not Airborne";
				System.out.println("Unable to Create Low Battery Event, UAV is not Airborne");
				break;
		}
	}

	
	
	
	
	
	
	/**
	 * This method contains the logic for determining the next state once the UAV enters
	 * an airborne state
	 */
	private void setNextAirborneState()
	{
		int remaining_bat = getRemainingBatteryTime();
		int remaining_low_bat = getRemainingLowBatteryTime();
		int remaining_hag = getRemainingHAGTime();
		int remaining_bad_path = getRemainingBadFlightTime();
		int remaining_flight_plan = getRemainingFlightPlanTime();
		
		int result_time = 0;
		RoleState result_state = state();
		
		//First Look at the battery, use this as a baseline
		if ( remaining_low_bat >= 0 ) {
			result_time = remaining_low_bat;
			result_state = state();
		} else {
			result_time = remaining_bat;
			result_state = RoleState.UAV_CRASHED;
		}
		
		//Now check HAG
		if ( remaining_hag >= 0 &&
				remaining_hag <= result_time ) {
			result_time = remaining_hag;
			result_state = RoleState.UAV_CRASHED;
		}
		
		//Now check flightplan
		if ( remaining_flight_plan >= 0 &&
				state() == RoleState.UAV_FLYING &&
				remaining_flight_plan < result_time ) {
			result_time = remaining_flight_plan;
			result_state = RoleState.UAV_LOITERING;
		}
		
		//Now check path
		if ( remaining_bad_path >= 0 && 
				remaining_bad_path < result_time ) {
			result_time = remaining_bad_path;
			result_state = state();
		}
		
		nextState(result_state, result_time);
	}
	
	/**
	 * Returns the amount of time remaining until the UAV enters a low battery state
	 * @return
	 */
	private int getRemainingLowBatteryTime()
	{
		//If we already have low bat then this method should not be used
		if ( _bat_state == DataType.UAV_BAT_LOW ) {
			return -1;
		} else if ( _battery_start_time == 0 ) {
			//If the UAV isn't active then it has full battery duration remaining
			return _battery_duration;
		} else {
			return Math.max(0, _battery_start_time + _battery_duration - _low_battery_threshold - Simulator.getInstance().getTime());
		}
	}
	
	private int getRemainingBatteryTime()
	{
		return Math.max(0, _battery_start_time + _battery_duration - Simulator.getInstance().getTime());
	}
	
	private int getRemainingHAGTime()
	{
		if ( _hag_state == DataType.UAV_HAG_LOW ) {
			return Math.max(0, _hag_start_time + _hag_duration - Simulator.getInstance().getTime() );
		} else {
			return -1;
		}
	}
	
	private int getRemainingLostSignalTime()
	{
		if ( state() == RoleState.UAV_NO_SIGNAL ) {
			int remaining_time = _signal_start_time + _signal_duration - Simulator.getInstance().getTime();
			return Math.max(0, remaining_time);
		} else {
			return -1;
		}
	}
	
	private boolean isFlightBad()
	{
		if ( _path_state == DataType.UAV_PATH_BAD ) {
			if ( getRemainingBadFlightTime() <= 0 ) {
				_path_state = DataType.UAV_PATH_OK;
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}
	
	/**
	 * Determine how much time is left of bad path following for the UAV
	 * @return
	 */
	private int getRemainingBadFlightTime()
	{
		if ( _path_state == DataType.UAV_PATH_BAD ) {
			int remaining_time = _bad_flight_start_time + _bad_flight_duration - Simulator.getInstance().getTime();
			return Math.max(0, remaining_time);
		} else {
			return -1;
		}
	}
	
	/**
	 * Used to determine how much longer the UAV will follow the given flight path
	 * @return
	 */
	private int getRemainingFlightPlanTime()
	{
		//Make sure to take pauses into account
		if ( _flight_plan ) {
			int remaining_plan = _flight_plan_start_time + _flight_plan_duration - Simulator.getInstance().getTime();
			if ( _flight_plan_pause_time >= _flight_plan_start_time ) {
				remaining_plan += Simulator.getInstance().getTime() - _flight_plan_pause_time;
			}
			return Math.max(0, remaining_plan);
		} else {
			return -1;
		}
	}
	
	/**
	 * This method is used to pause the flight plan
	 */
	private void pauseFlightPlan()
	{
		if ( _flight_plan ) {
			
			//We need to fix the flight plans duration as we have already covered some of the flight plan
			//Do this by changing the duration to that of the remaining.
			_flight_plan_duration = getRemainingFlightPlanTime();
			
			//The flight plan is finished
			if ( _flight_plan_duration == 0 ) {
				_flight_plan = false;
				_plan_state = DataType.UAV_FLIGHT_PLAN_NO;
				_flight_plan_start_time = 0;
				
			} else {
			
				//Set the pause time
				//Only set the pause time if not already paused
				if ( _flight_plan_pause_time < _flight_plan_start_time ) {
					_flight_plan_pause_time = Simulator.getInstance().getTime();
					_flight_plan_start_time = Simulator.getInstance().getTime();
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
			Simulator.getInstance().clearPost(POBOX.UAV_UGUI);
		
		//Send data to the GUI that the flight plan is completed
		Simulator.getInstance().addPost(POBOX.UAV_UGUI, _hag_state);
		Simulator.getInstance().addPost(POBOX.UAV_UGUI, _bat_state);
		Simulator.getInstance().addPost(POBOX.UAV_UGUI, _path_state);
		Simulator.getInstance().addPost(POBOX.UAV_UGUI, _plan_state);
		}
	}
	
	private void sendUAVDataToPilot()
	{
		Simulator.getInstance().clearPost(POBOX.UAV_PILOT);
		
		Simulator.getInstance().addPost(POBOX.UAV_PILOT, _hag_state);
	}
	
}
