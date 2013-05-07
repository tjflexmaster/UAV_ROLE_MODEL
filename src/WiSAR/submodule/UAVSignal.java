package WiSAR.submodule;

import java.util.ArrayList;

import CUAS.Simulator.Actor;
import CUAS.Simulator.IData;
import CUAS.Simulator.IStateEnum;
import CUAS.Simulator.Simulator;
import WiSAR.Actors;
import WiSAR.Events.SignalEvent;

public class UAVSignal extends Actor {

	public enum Outputs implements IData {
		SIGNAL_LOST,
		SIGNAL_OK, 
		SIGNAL_RESUMED
	}
	
	public enum States implements IStateEnum{
		OK,
		LOST,
		RESUMED
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
        case RESUMED:
        	nextState(States.OK,1);
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
		case OK:
			if(input.contains(SignalEvent.Outputs.E_SIGNAL_LOST)){
				nextState(States.LOST,1);
			}
			break;
		case LOST:
			if(input.contains(SignalEvent.Outputs.E_SIGNAL_BACK)){
				nextState(States.RESUMED,1);
			}
			break;
		}
	}


	private void setObservations() {
		IData _state = Outputs.SIGNAL_OK;
		switch((States)state()){
		case OK:
			_state = Outputs.SIGNAL_OK;
			break;
		case LOST:
			_state = Outputs.SIGNAL_LOST;
			break;
		case RESUMED:
			_state = Outputs.SIGNAL_RESUMED;
		}
		sim().addObservation(_state, this.name());
	}
}
