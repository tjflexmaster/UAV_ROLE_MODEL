package NewModel.Roles;

import NewModel.Simulation.Simulator;
import NewModel.Utils.DurationGenerator;

public class UAVGUIRole extends Role {
	
	/**
	 * UAV GUI INTERNAL STATES
	 */
	private enum UAVState {
		GROUNDED,
		TAKE_OFF,
		FLYING,
		LOITERING,
		LANDING,
		CRASHED,
		NO_SIGNAL
	}
	private enum GUIState {
		AVAILABLE,
		UNAVAILABLE
	}
	private enum UAVBatteryState {
		OK,
		LOW
	}
	private enum UAVPathState {
		OK,
		BAD
	}
	
	//Default Values
	UAVState _uav_state = UAVState.GROUNDED;
	GUIState _gui_state = GUIState.AVAILABLE;
	UAVBatteryState _bat_state = UAVBatteryState.OK;
	UAVPathState _path_state = UAVPathState.OK;
	
	
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
			case UGUI_TAKE_OFF:
			
				break;
			case UGUI_FLYING:
				
				break;
			case UGUI_FLYING_BAT_LOW:
				
				break;
			case UGUI_FLYING_PATH_BAD:
				
				break;
			case UGUI_FLYING_BAT_LOW_PATH_BAD:
				
				break;
			case UGUI_LOITERING:
				
				break;
			case UGUI_LOITERING_BAT_LOW:
				
				break;
			case UGUI_LOITERING_PATH_BAD:
				
				break;
			case UGUI_LOITERING_BAT_LOW_PATH_BAD:
				
				break;
			case UGUI_SIGNAL_LOST:
				
				break;
			case UGUI_AUDIBLE_ALARM:
				
				break;
			case UGUI_GROUNDED:
				
				break;
			case UGUI_LANDING:
				
				break;
			case UGUI_INACCESSIBLE:
				//Determine how long the GUI will be inaccessible
				duration = DurationGenerator.getRandDuration(30, 40);
				nextState(findGUIState(), duration);
				break;
			case STARTING:
				nextState(RoleState.IDLE, 1);
			default:
				nextState(null, 0);
				break;
		}
		
		
		return true;
	}
	
	@Override
	public void updateState() {
		// TODO Auto-generated method stub

	}
	
	/**
	 * This is a helper method that determines the state of the GUI by looking at the UAV state.
	 * The assumption being that communication between the GUI and the UAV is near instantaneous, therefore, 
	 * the GUI directly displays its state based on the UAV.
	 * 
	 * @return RoleState
	 */
	private RoleState findGUIState()
	{
		RoleState result;
		
		//Look at the UAV
		switch( Simulator.team.getRoleState(RoleType.ROLE_UAV) ) {
		case UAV_NO_SIGNAL:
			_uav_state = UAVState.NO_SIGNAL;
			result =  RoleState.UGUI_SIGNAL_LOST;
			break;
		case UAV_TAKE_OFF:
			_uav_state = UAVState.TAKE_OFF;
			result = RoleState.UGUI_TAKE_OFF;
			break;
		case UAV_LANDING:
			_uav_state = UAVState.TAKE_OFF;
			result = RoleState.UGUI_TAKE_OFF;
			break;
		case UAV_FLYING:
			_uav_state = UAVState.TAKE_OFF;
			result = RoleState.UGUI_TAKE_OFF;
			break;
		case UAV_FLYING_HAG_LOW:
			_uav_state = UAVState.TAKE_OFF;
			result = RoleState.UGUI_TAKE_OFF;
			break;
		case UAV_FLYING_BAT_LOW:
			_uav_state = UAVState.TAKE_OFF;
			result = RoleState.UGUI_TAKE_OFF;
			break;
		case UAV_FLYING_BAT_LOW_HAG_LOW:
			_uav_state = UAVState.TAKE_OFF;
			result = RoleState.UGUI_TAKE_OFF;
			break;
		case UAV_LOITERING_HAG_LOW:
			_uav_state = UAVState.LOITERING;
			_path_state = UAVPathState.OK;
			_bat_state = UAVBatteryState.OK;
			result = RoleState.UGUI_LOITERING;
			break;
		case UAV_LOITERING_BAT_LOW:
		case UAV_LOITERING_BAT_LOW_HAG_LOW:
			_uav_state = UAVState.LOITERING;
			_path_state = UAVPathState.OK;
			_bat_state = UAVBatteryState.LOW;
			result = RoleState.UGUI_FLYING_PATH_BAD;
			break;
		case UAV_STRUGGLING:
		case UAV_STRUGGLING_HAG_LOW:
//			_uav_state = UAVState.FLYING; //Stay in the same state we were in before
			_path_state = UAVPathState.BAD;
			_bat_state = UAVBatteryState.OK;
			if ( _uav_state = UAVState.FLYING )
			result = RoleState.UGUI_FLYING_PATH_BAD;
			break;
		case UAV_STRUGGLING_BAT_LOW:
		case UAV_STRUGGLING_BAT_LOW_HAG_LOW:
//			_uav_state = UAVState.FLYING; //Stay in the same UAV state as before
			_path_state = UAVPathState.BAD;
			_bat_state = UAVBatteryState.LOW;
			result = RoleState.UGUI_FLYING_BAT_LOW_PATH_BAD;
			break;
		case UAV_CRASH:
			
			break;
		case UAV_GROUNDED:
			
			break;
		}
	}

}
