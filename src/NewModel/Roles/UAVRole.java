package NewModel.Roles;

import java.util.ArrayList;

import NewModel.Simulation.Simulator;
import NewModel.Utils.DataType;
import NewModel.Utils.PostOffice.POBOX;

public class UAVRole extends Role {
	
	/**
	 * UAV INTERNAL STATES
	 */
	
	//Battery Internal Vars
	int _battery_duration = 500;
	int _flight_start_time = 0;
	int _low_battery_threshold = 70;
	
	//Flight Plan Internal Vars
	boolean _flight_plan = false;
	int _flight_plan_start_time = 0;
	int _flight_plan_duration = 100;
	int _flight_plan_pause_time = 0;
	
	//HAG Internal Vars
	int _hag_duration = 100;
	int _hag_start_time = 0;
	
//	DataType _uav_state = DataType.UAV_READY;
	DataType _bat_state = DataType.UAV_BAT_OK;
	DataType _path_state = DataType.UAV_PATH_OK;
	DataType _hag_state = DataType.UAV_HAG_OK;
	DataType _signal_state = DataType.UAV_SIGNAL_OK;
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
		
		//If a state isn't included then it doesn't deviate from the default
		switch(nextState()) {
			case UAV_TAKE_OFF:
				//Assume battery duration is the same every time
				_battery_duration = 500;
				_flight_start_time = Simulator.getTime();
				
				//Assume constant take off duration
				duration = 50;
				
				if ( _flight_plan ) {
					nextState(RoleState.UAV_FLYING, duration);
				} else {
					nextState(RoleState.UAV_LOITERING, duration);
				}
				
				//_uav_state = DataType.UAV_TAKE_OFF;
				break;
			case UAV_LANDING:
				//First clean up the flight plan
				pauseFlightPlan();
				
				//It takes time to land, if we land too late then we will crash,
				//check to see if there is time to land.
				duration = 50;
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
				
				//If no flight plan then loiter
				if ( _flight_plan ) {
					
					//Update the flight plan start time to now
					_flight_plan_start_time = Simulator.getTime();
					
					//The duration is set when the flight plan is received or when we enter another state besided flying.
					//We will fly until the plane crashes or the flight plan is done
					if ( _flight_plan_duration >= remaining && _flight_plan) {
					
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
				_signal_state = DataType.UAV_SIGNAL_LOST;
				//_uav_state = DataType.UAV_CRASHED;
				nextState(null,0);
				break;
			case UAV_LANDED:
				//_uav_state = DataType.UAV_LANDED;
				nextState(null, 0);
				break;
			case UAV_READY:
				//reset all of the internal state values
				//Probably doesn't need to be done
				//_uav_state = DataType.UAV_READY;
				_signal_state = DataType.UAV_SIGNAL_OK;
				_hag_state = DataType.UAV_HAG_OK;
				_bat_state = DataType.UAV_BAT_OK;
				_path_state = DataType.UAV_PATH_OK;
//				_flight_plan = false;
//				nextState(null, 0);
				break;
			case STARTING:
				_flight_plan = false;
				nextState(RoleState.UAV_READY, 1);
				break;
			default:
				nextState(null, 0);
				break;
		}
		
		//Each time the UAV updates state it should send it to the UGUI, if it has signal
		if ( _signal_state == DataType.UAV_SIGNAL_OK) {
			sendUAVDataToUGUI();
		}
		
		sendUAVDataToPilot();
		
		return true;
	}
	
	@Override
	public void updateState() {
		ArrayList<DataType> data;
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
								_plan_state = DataType.UAV_FLIGHT_PLAN_YES;
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
				break;
			case UAV_LOITERING:
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
								_plan_state = DataType.UAV_FLIGHT_PLAN_YES;
								next_state = nextState();
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
			case STARTING:
			default:
				//Ignore
				break;
		}
	}
	
	/**
	 * This sets the HAG to low and will cause the UAV to crash if no
	 * flight plan change is received within the recover time.
	 * @param recover_time
	 */
	public void createHAGEvent(int recover_time)
	{
		_hag_duration = recover_time;
		_hag_start_time = Simulator.getTime();
		_hag_state = DataType.UAV_HAG_LOW;
//		Simulator.addPost(POBOX.UAV_PILOT, DataType.UAV_HAG_LOW);
		//Stay in the same state but trigger a state change so those watching the
		//UAV will see the change in state
		nextState(state(), 1);
	}
	
	//TODO Use a duration for this
//	/**
//	 * This sets the UAV state to a new follow flight path state
//	 * @param recover_time
//	 */
//	public void createFlightPathEvent()
//	{
//		if ( state == UAVPathState.BAD ) {
//			_path_state = UAVPathState.BAD;
//			Simulator.addPost(POBOX.UAV_UGUI, DataType.UAV_PATH_BAD);
//		} else {
//			_path_state = UAVPathState.OK;
//			Simulator.addPost(POBOX.UAV_UGUI, DataType.UAV_PATH_OK);
//		}
//		//Stay in the same state but trigger a state change so those watching the
//		//UAV will see the change in state
//		nextState(state(), 1);
//	}
	
	//TODO Use a duration for this, this is difficult because whenever a future state is planned it must check to see if signal will change in that time and throw a dummy event instead.
//	/**
//	 * This create signal event
//	 * @return
//	 */
//	public void createSignalEvent(UAVSignalState state)
//	{
//		_signal_state = state;
//		if ( state == UAVSignalState.LOST ) {
//			Simulator.addPost(POBOX.UAV_UGUI, DataType.UAV_SIGNAL_LOST);
//		} else {
//			Simulator.addPost(POBOX.UAV_UGUI, DataType.UAV_SIGNAL_OK);
//		}
//		nextState(state(), 1);
//	}
	
	/**
	 * Create a low battery event
	 * @return
	 */
	public void createLowBatteryEvent()
	{
		_battery_duration = Simulator.getTime() - _flight_start_time + _low_battery_threshold;
		_bat_state = DataType.UAV_BAT_LOW;
//		Simulator.addPost(POBOX.UAV_UGUI, DataType.UAV_BAT_LOW);
		nextState(state(), 1);
	}

	/**
	 * Looks are HAG and Battery to determine how much longer the UAV can fly before crashing
	 * @return
	 */
	private int getRemainingFlightTime()
	{
		int remaining_bat = Math.max(0, _flight_start_time + _battery_duration - Simulator.getTime() );
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
	 * Used to determine how much longer the UAV will follow the given flight path
	 * @return
	 */
	private int getFlightPlanTime()
	{
		//Make sure to take pauses into account
		int remaining_plan = _flight_plan_start_time + _flight_plan_duration - Simulator.getTime();
		if ( _flight_plan_pause_time > _flight_plan_start_time ) {
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
				//Let the GUI know that the flight plan is finished
//				Simulator.addPost(POBOX.UAV_UGUI, DataType.SEARCH_COMPLETE);
			} else {
			
				//Set the pause time
				//Only set the pause time if not already paused
				if ( _flight_plan_pause_time < _flight_plan_start_time ) {
					_flight_plan_pause_time = Simulator.getTime();
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
		Simulator.clearPost(POBOX.UAV_UGUI);
		
		//Send data to the GUI that the flight plan is completed
//		Simulator.addPost(POBOX.UAV_PILOT, _uav_state);
		Simulator.addPost(POBOX.UAV_UGUI, _bat_state);
		Simulator.addPost(POBOX.UAV_UGUI, _path_state);
		Simulator.addPost(POBOX.UAV_UGUI, _plan_state);
		Simulator.addPost(POBOX.UAV_UGUI, _signal_state);
	}
	
	private void sendUAVDataToPilot()
	{
		Simulator.clearPost(POBOX.UAV_PILOT);
		
//		Simulator.addPost(POBOX.UAV_PILOT, _uav_state);
		Simulator.addPost(POBOX.UAV_PILOT, _hag_state);
	}
	
}
