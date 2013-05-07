package WiSAR.submodule;

import java.util.ArrayList;

import WiSAR.Actors;
import WiSAR.Durations;
import WiSAR.Agents.OperatorRole;
import WiSAR.Events.HAGEvent;
import CUAS.Simulator.Actor;
import CUAS.Simulator.IData;
import CUAS.Simulator.IStateEnum;
import CUAS.Simulator.Simulator;

public class UAVHeightAboveGround extends Actor {

	public enum Outputs implements IData {
		HAG_LOW,
		HAG_NONE,
		HAG_GOOD, 
		HAG_CRASHED
	}
	
	public enum States implements IStateEnum{
		INACTIVE,
		GOOD,
		LOW,
		CRASHED
	}

	public UAVHeightAboveGround(){
		name(Actors.UAV_HAG.name());
		nextState(States.INACTIVE,1);
	}
	
	@Override
	public void processNextState() {

        if ( nextStateTime() != Simulator.getInstance().getTime() ) {
        	//Set Observations
            setObservations();
            return;
        }
        
        state(nextState());
        
        switch((States)state()){
	        case LOW:
	        	nextState(States.CRASHED, sim().duration(WiSAR.Durations.HAG_DANGER_TO_CRASH_DUR.range()));
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
        switch((States)state()){
        case INACTIVE:
        	if(input.contains(OperatorRole.Outputs.OP_TAKE_OFF)){
        		nextState(States.GOOD,1);
        	}
        	break;
        case GOOD:
        	if(input.contains(HAGEvent.Outputs.EHAG_LOW)){
        		nextState(States.LOW,1);
        	}
        	break;
        case LOW:
        	if(input.contains(HAGEvent.Outputs.EHAG_CRASHED)){
        		nextState(States.CRASHED,1);
        	} else if(input.contains(OperatorRole.Outputs.OP_MODIFY_FLIGHT_PLAN)){
        		nextState(States.GOOD,sim().duration(Durations.UAV_ADJUST_PATH.range()));
        	}
        	break;
        case CRASHED:
        	break;
        	
        }
		input.clear();
	}
	
	private void setObservations()
	{
		IData _state = Outputs.HAG_NONE;
		switch((States) state()) {
			case INACTIVE:
				_state = Outputs.HAG_NONE;
				break;
			case GOOD:
				_state = Outputs.HAG_GOOD;
				break;
			case LOW:
				_state = Outputs.HAG_LOW;
				break;
			case CRASHED:
				_state = Outputs.HAG_CRASHED;
				break;
		}
		sim().addObservation(_state, this.name());
	}
	
}
