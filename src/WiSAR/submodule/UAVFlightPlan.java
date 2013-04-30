package WiSAR.submodule;

import CUAS.Simulator.Actor;
import CUAS.Simulator.IData;
import CUAS.Simulator.IStateEnum;
import CUAS.Simulator.Simulator;
import WiSAR.Actors;
import WiSAR.Durations;
import WiSAR.Agents.OperatorGUIRole;

public class UAVFlightPlan extends Actor {

	private int _path_dur;
	private int _start_time;
	private int _time_paused;
	
	public enum Outputs implements IData {
		NO_PATH,
		YES_PATH,
		PAUSED,
		COMPLETE
	}
	
	public enum States implements IStateEnum{
		NO_PATH,
		YES_PATH,
		PAUSED,
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
		switch((States)state()){
		case COMPLETE:
		case NO_PATH:
			if(_input.contains(OperatorGUIRole.Outputs.OGUI_PATH_NEW)){
				_start_time = sim().getTime();
				_path_dur = sim().duration(Durations.UAV_FLIGHT_PLAN_DUR.range());
				nextState(States.YES_PATH,1);
			}
			break;
		case YES_PATH:
			if(_input.contains(OperatorGUIRole.Outputs.OGUI_PATH_END)){
				nextState(States.NO_PATH,1);
			}
			//TODO check if received paused command.
			break;
		case PAUSED:
			if(_input.contains(OperatorGUIRole.Outputs.RESUME_PATH)){
				resume();
				nextState(States.YES_PATH,1);
			}
		default:
			break;
		}
		_input.clear();
	}
	private void resume() {
		_path_dur -= (sim().getTime() - _start_time);
		_start_time = sim().getTime();
	}

	private void setObservations() {
		IData _state;
		switch((States)state()){
		case NO_PATH:
			_state = Outputs.NO_PATH;
			break;
		case YES_PATH:
			_state = Outputs.YES_PATH;
			break;
		case PAUSED:
			_state = Outputs.PAUSED;
			break;
		case COMPLETE:
			_state = Outputs.COMPLETE;
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
