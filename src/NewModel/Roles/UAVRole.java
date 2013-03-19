package NewModel.Roles;

import java.util.ArrayList;

import NewModel.Simulation.Simulator;
import NewModel.Utils.DataType;
import NewModel.Utils.PostOffice.POBOX;

public class UAVRole extends Role {
	
	/**
	 * UAV INTERNAL STATES
	 */
	public enum UAVState {
		GROUNDED,
		TAKE_OFF,
		FLYING,
		LOITERING,
		LANDING,
		CRASHED
	}
	public enum UAVBatteryState {
		OK,
		LOW
	}
	public enum UAVPathState {
		OK,
		BAD
	}
	public enum UAVHAGState {
		OK,
		LOW
	}
	public enum UAVSignalState {
		OK,
		LOST
	}
	
	//Battery Internal Vars
	int _battery_duration = 500;
	int _flight_start_time = 0;
	int _low_battery_threshold = 70;
	
	//HAG Internal Vars
	int _hag_duration = 100;
	int _hag_start_time = 0;
	
	UAVState _uav_state = UAVState.GROUNDED;
	UAVBatteryState _bat_state = UAVBatteryState.OK;
	UAVPathState _path_state = UAVPathState.OK;
	UAVHAGState _hag_state = UAVHAGState.OK;
	UAVSignalState _signal_state = UAVSignalState.OK;
	
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
				//UGUI Messages
				Simulator.addPost(POBOX.UAV_UGUI, DataType.UAV_BAT_OK);
				Simulator.addPost(POBOX.UAV_UGUI, DataType.UAV_PATH_OK);
				Simulator.addPost(POBOX.UAV_UGUI, DataType.UAV_SIGNAL_OK);
				//Pilot Messages
				Simulator.addPost(POBOX.UAV_PILOT, DataType.UAV_HAG_OK);
				
				nextState(RoleState.UAV_FLYING, duration);
				break;
			case UAV_LANDING:
				//It takes time to land, if we land too late then we will crash,
				//check to see if there is time to land.
				duration = 50;
				if ( duration > remaining ) {
					nextState(RoleState.UAV_CRASH, remaining);
				} else {
					nextState(RoleState.UAV_GROUNDED, duration);
				}
				break;
			case UAV_FLYING:
			case UAV_LOITERING:
				//When we are flying we will update our state when we have low battery
				//When we set our state to Flying we need to check the state of battery and hag
				if ( _hag_state == UAVHAGState.LOW && _bat_state == UAVBatteryState.LOW ) {
					nextState(RoleState.UAV_CRASH, remaining);
				} else if ( _hag_state == UAVHAGState.LOW ) {
					nextState(RoleState.UAV_CRASH, remaining);
				} else if ( _bat_state == UAVBatteryState.LOW ) {
					nextState(RoleState.UAV_CRASH, remaining);
				} else {
					if ( remaining > _low_battery_threshold ) {
						//Our next state is the same, except that we want to queue when the battery will become low
						nextState(state(), remaining - _low_battery_threshold);
					} else {
						//We are now low battery, we will crash unless something is changed.
						//Place this in our global message box to the GUI
						Simulator.addPost(POBOX.UAV_UGUI, DataType.UAV_BAT_LOW);
						nextState(RoleState.UAV_CRASH, remaining);
					}
				}
				break;
			case UAV_CRASH:
				_signal_state = UAVSignalState.LOST;
				nextState(null,0);
				break;
			case UAV_GROUNDED:
				//reset all of the internal state values
				//Probably doesn't need to be done
				_signal_state = UAVSignalState.OK;
				_hag_state = UAVHAGState.OK;
				_bat_state = UAVBatteryState.OK;
				_path_state = UAVPathState.OK;
				nextState(null, 0);
				break;
			case STARTING:
				nextState(RoleState.UAV_GROUNDED, 1);
				break;
			default:
				nextState(null, 0);
				break;
		}
		
		
		return true;
	}
	
	@Override
	public void updateState() {
		ArrayList<DataType> data;
		switch(nextState()) {
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
								nextState(RoleState.UAV_CRASH, 1);
								return; //Don't do any more processing
							case LAND:
								next_state = RoleState.UAV_LANDING;
								next_time = nextStateTime();
								break;
							case FLIGHT_PLAN:
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
								//Go to Loitering after taking off
								next_state = RoleState.UAV_LOITERING;
								next_time = 1;
								break;
							case KILL:
								//Crash the UAV on purpose
								//Perform this immediately
								nextState(RoleState.UAV_CRASH, 1);
								return; //Don't do any more processing
							case RESUME:
							case LAND:
								next_state = nextState();
								next_time = nextStateTime();
								break;
							case FLIGHT_PLAN:
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
					
					//Loop through all commands sent by the GUI while landing
					for(DataType info : data) {
						switch(info) {
							case LOITER:
								//Go to Loitering after taking off
								next_state = RoleState.UAV_LOITERING;
								next_time = 1;
								break;
							case KILL:
								//Crash the UAV on purpose
								//Perform this immediately
								nextState(RoleState.UAV_CRASH, 1);
								return; //Don't do any more processing
							case LAND:
								next_state = RoleState.UAV_LANDING;
								next_time = 1;
								break;
							case FLIGHT_PLAN:
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
								nextState(RoleState.UAV_CRASH, 1);
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
			case UAV_CRASH:
				//Do Nothing we cannot communicate
				break;
			case UAV_GROUNDED:
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
							case KILL:
							case LAND:
							case FLIGHT_PLAN:
							case RESUME:
								next_state = nextState();
								next_time = nextStateTime();
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
				nextState(RoleState.UAV_GROUNDED, 1);
			default:
				nextState(null, 0);
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
		_hag_state = UAVHAGState.LOW;
		Simulator.addPost(POBOX.UAV_PILOT, DataType.UAV_HAG_LOW);
		//Stay in the same state but trigger a state change so those watching the
		//UAV will see the change in state
		nextState(state(), 1);
	}
	
	/**
	 * This sets the UAV state to a new follow flight path state
	 * @param recover_time
	 */
	public void createFlightPathEvent(UAVPathState state)
	{
		if ( state == UAVPathState.BAD ) {
			_path_state = UAVPathState.BAD;
			Simulator.addPost(POBOX.UAV_UGUI, DataType.UAV_PATH_BAD);
		} else {
			_path_state = UAVPathState.OK;
			Simulator.addPost(POBOX.UAV_UGUI, DataType.UAV_PATH_OK);
		}
		//Stay in the same state but trigger a state change so those watching the
		//UAV will see the change in state
		nextState(state(), 1);
	}
	
	/**
	 * This create signal event
	 * @return
	 */
	public void createSignalEvent(UAVSignalState state)
	{
		_signal_state = state;
		if ( state == UAVSignalState.LOST ) {
			Simulator.addPost(POBOX.UAV_UGUI, DataType.UAV_SIGNAL_LOST);
		} else {
			Simulator.addPost(POBOX.UAV_UGUI, DataType.UAV_SIGNAL_OK);
		}
		nextState(state(), 1);
	}

	private int getRemainingFlightTime()
	{
		int remaining_bat = Math.max(0, _flight_start_time + _battery_duration - Simulator.getTime() );
		int remaining_hag = Math.max(0, _hag_start_time + _hag_duration - Simulator.getTime() );
		if ( _hag_state == UAVHAGState.LOW && _bat_state == UAVBatteryState.LOW ) {
			return Math.min(remaining_bat, remaining_hag);
		} else if ( _hag_state == UAVHAGState.LOW ) {
			return remaining_hag;
		} else {
			return remaining_bat;
		}
	}
	
}
