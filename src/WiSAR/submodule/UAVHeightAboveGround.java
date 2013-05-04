package WiSAR.submodule;

import java.util.ArrayList;

import WiSAR.Actors;
import WiSAR.Durations;
import WiSAR.Agents.OperatorGUIRole;
import WiSAR.Agents.UAVRole;
import WiSAR.Events.HAGEvent;
import WiSAR.submodule.UAVBattery.Outputs;
import WiSAR.submodule.UAVFlightPlan.States;
import CUAS.Simulator.Actor;
import CUAS.Simulator.IData;
import CUAS.Simulator.IStateEnum;
import CUAS.Simulator.Simulator;

public class UAVHeightAboveGround extends Actor {

	public enum Outputs implements IData {
		HAG_DANGEROUS,
		HAG_NONE,
		HAG_GOOD, 
		HAG_CRASHED
	}
	
	public enum States implements IStateEnum{
		INACTIVE,
		GOOD,
		DANGEROUS,
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
        	if(input.contains(UAVRole.Outputs.UAV_TAKE_OFF)){
        		nextState(States.GOOD,1);
        	}
        	break;
        case GOOD:
        	if(input.contains(HAGEvent.Outputs.EHAG_DANGEROUS)){
        		nextState(States.DANGEROUS,1);
        	}
        	break;
        case DANGEROUS:
        	if(input.contains(HAGEvent.Outputs.EHAG_CRASHED)){
        		nextState(States.CRASHED,1);
        	} else if(input.contains(OperatorGUIRole.Outputs.OGUI_ADJUST_PATH)){
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
			case DANGEROUS:
				_state = Outputs.HAG_DANGEROUS;
				break;
			case CRASHED:
				_state = Outputs.HAG_CRASHED;
				break;
		}
		sim().addObservation(_state, this.name());
	}
	
}
