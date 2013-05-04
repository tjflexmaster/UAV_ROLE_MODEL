package WiSAR.submodule;

import java.util.ArrayList;

import CUAS.Simulator.Actor;
import CUAS.Simulator.IData;
import CUAS.Simulator.IStateEnum;
import CUAS.Simulator.Simulator;
import WiSAR.Actors;
import WiSAR.Durations;
import WiSAR.Agents.OperatorGUIRole;
import WiSAR.Agents.UAVRole;

public class UAVFlightPlan extends Actor {

	private int _path_dur;
	private int _start_time;
	private int _time_paused;
	private int _pause_time;
	
	public enum Outputs implements IData {
		UAV_FLIGHT_PLAN_NO,
		UAV_FLIGHT_PLAN_YES,
		UAV_FLIGHT_PLAN_PAUSED,
		UAV_FLIGHT_PLAN_COMPLETE
	}
	
	public enum States implements IStateEnum{
		NO_PATH,
		YES_PATH,
		PAUSED,
		RESUME_AFTER_TAKE_OFF,
		COMPLETE
		
	}
	
	public UAVFlightPlan(){
		name(Actors.UAV_FLIGHT_PLAN.name());
		nextState(States.NO_PATH,1);
	}
	
	@Override
	public void processNextState() {

        if ( nextStateTime() != Simulator.getInstance().getTime() ) {
        	//Set Observations
            setObservations();
            return;
        }
        
        
        state(nextState());
		
        switch((States)nextState()){
        case YES_PATH:
        	nextState(States.COMPLETE,getTimeRemaining());
        	break;
        case COMPLETE:
        	nextState(States.NO_PATH,1);
        	break;
    	default:
    		nextState(null,0);
    		break;
        }
        setObservations();
	}


	@Override
	public void processInputs() {
		ArrayList<IData> input = sim().getInput(this.name());
		ArrayList<IData> uav = sim().getObservations(Actors.UAV.name());
		switch((States)state()){
		case COMPLETE: 
			//check if there is a new path to immediately follow, if so return to YES_PATH
			if(input.contains(OperatorGUIRole.Outputs.OGUI_PATH_NEW)){
				_start_time = sim().getTime();
				_path_dur = sim().duration(Durations.UAV_FLIGHT_PLAN_DUR.range());
				nextState(States.YES_PATH,1);
			}
		case NO_PATH:
			if(input.contains(OperatorGUIRole.Outputs.OGUI_PATH_NEW)){
				_start_time = sim().getTime();
				_path_dur = sim().duration(Durations.UAV_FLIGHT_PLAN_DUR.range());
				//if the uav is loitering then it can immediately enter the flight plan, otherwise it will go to the paused state
				if(uav.contains(UAVRole.Outputs.UAV_LOITERING)){
					nextState(States.YES_PATH,1);
				}else{
					_pause_time = sim().getTime();
					nextState(States.PAUSED,1);
				}
			}
			break;
		case YES_PATH:
			//TODO assert that the UAV is flying
			//replace path
			if(input.contains(OperatorGUIRole.Outputs.OGUI_PATH_NEW)){
				_start_time = sim().getTime();
				_path_dur = sim().duration(Durations.UAV_FLIGHT_PLAN_DUR.range());
				nextState(States.COMPLETE,_path_dur);
			}
			//end path
			else if(input.contains(OperatorGUIRole.Outputs.OGUI_PATH_END)){
				nextState(States.NO_PATH,1);
			}
			//adjust path
			else if(input.contains(OperatorGUIRole.Outputs.OGUI_ADJUST_PATH)){
				_path_dur = getTimeRemaining() + sim().duration(Durations.UAV_ADJUST_PATH.range());
				nextState(States.COMPLETE,_path_dur);
			} 
			//land
			else if(input.contains(OperatorGUIRole.Outputs.OGUI_LAND)){
				_pause_time = sim().getTime();
				nextState(States.PAUSED,1);
			}
			//pause path
			else if(input.contains(OperatorGUIRole.Outputs.OGUI_PAUSE_FLIGHT_PLAN)){
				_pause_time = sim().getTime();
				nextState(States.PAUSED,1);
			}
			//execute a flyby
			else if(input.contains(OperatorGUIRole.Outputs.OGUI_FLYBY_F)
					|| input.contains(OperatorGUIRole.Outputs.OGUI_FLYBY_T)){
				_pause_time = sim().getTime();
				nextState(States.PAUSED,1);
			}
			break;
		case PAUSED:
			//two ways to get out of the paused state, 
				//if currently loitering and the OGUI issues the cmd to resume
			if(input.contains(OperatorGUIRole.Outputs.RESUME_PATH)
					|| uav.contains(UAVRole.Outputs.UAV_LOITERING)){
				nextState(States.YES_PATH,1);
			//else if the UAV has been issued the cmd to take off. Or the land has been aborted
			}else if(input.contains(OperatorGUIRole.Outputs.TAKE_OFF)
					|| input.contains(OperatorGUIRole.Outputs.OGUI_ABORT_LAND)){
				nextState(States.RESUME_AFTER_TAKE_OFF,1);
			}
			break;
		case RESUME_AFTER_TAKE_OFF:
			//once the UAV has made it airborn resume path
			if(uav.contains(UAVRole.Outputs.UAV_LOITERING) || uav.contains(UAVRole.Outputs.UAV_FLYING)){
				nextState(States.YES_PATH,1);
			}
			break;
		default:
			break;
		}
		input.clear();
	}
	private void resume() {
		_path_dur -= (_pause_time - _start_time);
		_start_time = sim().getTime();
	}

	private void setObservations() {
		IData _state;
		switch((States)state()){
		case NO_PATH:
			_state = Outputs.UAV_FLIGHT_PLAN_NO;
			break;
		case YES_PATH:
			_state = Outputs.UAV_FLIGHT_PLAN_YES;
			break;
		case PAUSED:
			_state = Outputs.UAV_FLIGHT_PLAN_PAUSED;
			break;
		case COMPLETE:
			_state = Outputs.UAV_FLIGHT_PLAN_COMPLETE;
			break;
		default:
			_state = null;
			break;
		}
		sim().addObservation(_state, this.name());
	}
	
	private int getTimeRemaining(){
		return Math.max(0,_start_time + _path_dur - sim().getTime());
	}

}
