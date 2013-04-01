package NewModel.Roles;

import java.util.ArrayList;

import NewModel.Events.Event;
import NewModel.Simulation.Simulator;
import NewModel.Utils.DataType;
import NewModel.Utils.PostOffice.POBOX;

public class UAVGUIRole extends Role {
	
	/**
	 * UAV GUI INTERNAL STATES
	 */
	
	//UAV State
	RoleState _uav_state = RoleState.UAV_READY;
//	DataType _uav_state = DataType.UAV_READY;
	DataType _bat_state = DataType.UAV_BAT_OK;
	DataType _hag_state = DataType.UAV_HAG_OK;
	DataType _path_state = DataType.UAV_PATH_OK;
	DataType _signal_state = DataType.UAV_SIGNAL_OK;
	DataType _plan_state = DataType.UAV_FLIGHT_PLAN_NO;
	
	//INACCESSIBLE EVENT
	int _inaccessible_start_time = 0;
	int _inaccessible_duration = 0;
	
	/**
	 * END UAV GUI STATES
	 */

	public UAVGUIRole()
	{
		type(RoleType.ROLE_UAV_GUI);
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
			case UGUI_NORMAL:
				nextState(null, 0);
				//Send State to the Pilot
				sendUAVDataToPilot();
				break;
			case UGUI_ALARM:
				//After some time go to an audible alarm
				nextState(RoleState.UGUI_AUDIBLE_ALARM, 50);
				//Send State to the Pilot
				sendUAVDataToPilot();
				break;
			case UGUI_AUDIBLE_ALARM:
				//Return to regular alarm if no response
				nextState(RoleState.UGUI_ALARM, 50);
				//Send State to the Pilot
				sendUAVDataToPilot();
				break;
			case UGUI_INACCESSIBLE:
				//Determine how long the GUI will be inaccessible
				nextState(getUGUIState(), _inaccessible_duration);
				break;
			case STARTING:
				nextState(RoleState.UGUI_NORMAL, 1);
				break;
			default:
				nextState(null, 0);
				break;
		}
		
		return true;
	}
	
	@Override
	public void updateState() {
		
		RoleState next_state;
		
		switch( state() ) {
			case UGUI_NORMAL:
			case UGUI_ALARM:
				//First obtain state from the UAV
				next_state = getUGUIState();
				
				//Send Pilot Commands
				if ( sendPilotCommandsToUAV() || next_state != state() ) {
					nextState(next_state, 1);
				}
				
				//Third Send Data to Pilot
				sendUAVDataToPilot();
				break;
			case UGUI_AUDIBLE_ALARM:
				//First obtain state from the UAV
				next_state = getUGUIState();
				
				//Send Pilot Commands
				//If the pilot has done an action then we should move back to the regular alarm
				if ( sendPilotCommandsToUAV() ) {
					nextState(next_state, 1);
				} else if ( next_state == RoleState.UGUI_NORMAL ) {
					nextState(next_state, 1);
				}
				
				//Third Send Data to Pilot
				sendUAVDataToPilot();
				break;
			case UGUI_INACCESSIBLE:
				//We accept no input from the Pilot or the UAV
				break;
				
			default:
				//Ignore
				break;
		}//end switch
	}
	
	@Override
	public void processEvents(ArrayList<Event> events) {
		//Do nothing
	}
	
	/**
	 * /////////////////////////////PRIVATE HELPER METHODS///////////////////////////////////////////
	 */
	
	/**
	 * Returns back the state the UGUI should become based on the UAV data
	 * @return
	 */
	private RoleState getUGUIState()
	{
		_uav_state = Simulator.getRoleState(RoleType.ROLE_UAV);
		if ( _uav_state == RoleState.UAV_CRASHED || _uav_state == RoleState.UAV_NO_SIGNAL ) {
			return RoleState.UGUI_ALARM;
		} else {
			ArrayList<DataType> data = Simulator.removePosts(POBOX.UAV_UGUI);
		
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
						case UAV_PATH_BAD:
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
			
			//Since we have a signal look at the other UAV status information to see what
			//we should do
			if ( (_bat_state == DataType.UAV_BAT_LOW || 
					_path_state == DataType.UAV_PATH_BAD || 
					_hag_state == DataType.UAV_HAG_LOW) &&
					(_uav_state == RoleState.UAV_FLYING ||
					_uav_state == RoleState.UAV_LOITERING ||
					_uav_state == RoleState.UAV_LANDING ||
					_uav_state == RoleState.UAV_TAKE_OFF) ) {
				return RoleState.UGUI_ALARM;
			} else {
				return RoleState.UGUI_NORMAL;
			}
		}
	}
	
	/**
	 * This method sends the pilot commands onto the UAV
	 * Return true if there are things that need to be sent
	 */
	private boolean sendPilotCommandsToUAV()
	{
		ArrayList<DataType> data = new ArrayList<DataType>();
		 _uav_state = Simulator.getRoleState(RoleType.ROLE_UAV);
		 
		//Assumption: The UAV will get our commands when it can finally connect
		//Only pull pilot commands after the pilot has finished transmitting
		if ( Simulator.getRoleState(RoleType.ROLE_PILOT) == RoleState.PILOT_END_UGUI ||
				Simulator.getRoleState(RoleType.ROLE_PILOT) == RoleState.PILOT_LAUNCH_UAV ) {
			data = Simulator.removePosts(POBOX.PILOT_UGUI);
		}
		
		//If we have received pilot commands then we need to send them to the UAV
		//to do this trigger a change in state on the next time step.
		if ( !data.isEmpty() ) {
			for( DataType info : data ) {
				switch(info) {
					case TAKE_OFF:
						if ( _uav_state == RoleState.UAV_READY ) {
							Simulator.addPost(POBOX.UGUI_UAV, DataType.TAKE_OFF);
						}
						break;
					case LAND:
						if ( _uav_state == RoleState.UAV_FLYING || _uav_state == RoleState.UAV_LOITERING || _uav_state == RoleState.UAV_TAKE_OFF ) {
							Simulator.addPost(POBOX.UGUI_UAV, DataType.LAND);
						}
						break;
					case LOITER:
						if ( _uav_state == RoleState.UAV_FLYING || _uav_state == RoleState.UAV_LANDING || _uav_state == RoleState.UAV_TAKE_OFF ) {
							Simulator.addPost(POBOX.UGUI_UAV, DataType.LOITER);
						}
						break;
					case RESUME:
						if ( _uav_state == RoleState.UAV_LOITERING ) {
							Simulator.addPost(POBOX.UGUI_UAV, DataType.RESUME);
						}
						break;
					case KILL:
						Simulator.addPost(POBOX.UGUI_UAV, DataType.KILL);
						break;
					case FLIGHT_PLAN:
						Simulator.addPost(POBOX.UGUI_UAV, DataType.FLIGHT_PLAN);
						break;
					default:
						//Ignore
						break;
				}//end switch
			}//end for loop

			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Look at the UAV state and send that to the Pilot,
	 * this should refresh everytime the state changes
	 */
	private void sendUAVDataToPilot()
	{
		//Clear out previous data
		Simulator.clearPost(POBOX.UGUI_PILOT);
		
		//Send UAV internal states
//		Simulator.addPost(POBOX.UGUI_PILOT, _uav_state);
		Simulator.addPost(POBOX.UGUI_PILOT, _bat_state);
		Simulator.addPost(POBOX.UGUI_PILOT, _hag_state);
		Simulator.addPost(POBOX.UGUI_PILOT, _path_state);
//		Simulator.addPost(POBOX.UGUI_PILOT, _signal_state);
		Simulator.addPost(POBOX.UGUI_PILOT, _plan_state);
	}
	
	
	/**
	 * Event method which causes the UGUI to become inaccessible for some duration
	 * @param duration
	 */
	private void createUGUIInAccessibleEvent(int duration)
	{
		_inaccessible_duration = duration;
		nextState(RoleState.UGUI_INACCESSIBLE, 1);
		
	}
	
}
