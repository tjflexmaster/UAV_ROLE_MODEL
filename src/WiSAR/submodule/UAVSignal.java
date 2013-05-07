package WiSAR.submodule;

import java.util.ArrayList;

import CUAS.Simulator.Actor;
import CUAS.Simulator.IData;
import CUAS.Simulator.IStateEnum;
import CUAS.Simulator.Simulator;
import WiSAR.Actors;
import WiSAR.Agents.UAVRole;
import WiSAR.Events.SignalEvent;

public class UAVSignal extends Actor {

	public enum Outputs implements IData {
		SIGNAL_NONE,
		SIGNAL_LOST,
		SIGNAL_OK, 
		SIGNAL_RESUMED
	}
	
	public enum States implements IStateEnum{
		IDLE,
		OK,
		LOST,
		RESUMED
	}
	
	public UAVSignal(){
		name(Actors.UAV_SIGNAL.name());
		nextState(States.IDLE,1);
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
		ArrayList<IData> uav = sim().getObservations(Actors.UAV.name());
		switch((States)state()){
		case IDLE:
			if(uav.contains(UAVRole.Outputs.UAV_TAKE_OFF)){
				nextState(States.OK,1);
			}else{
				//this state is only possible while the uav is on the ground.
				assert (uav.contains(UAVRole.Outputs.UAV_READY)
						|| uav.contains(UAVRole.Outputs.UAV_LANDED)
						|| uav.contains(UAVRole.Outputs.UAV_CRASHED)) : "The video feed should not be idle while the uav is in the air";
			}
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
		case IDLE:
			_state = Outputs.SIGNAL_NONE;
			break;
		case RESUMED:
			sim().addObservation(Outputs.SIGNAL_RESUMED, this.name());
			//when the signal is resumed have signal resumed to alert the system of the change,
			// but also immediately broadcast SIGNAL_OK
		case OK:
			_state = Outputs.SIGNAL_OK;
			break;
		case LOST:
			_state = Outputs.SIGNAL_LOST;
			break;
		default:
			break;
		}
		sim().addObservation(_state, this.name());
	}
}
