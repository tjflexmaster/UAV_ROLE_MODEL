package WiSAR.submodule;

import java.util.ArrayList;

<<<<<<< Upstream, based on master
import WiSAR.Actors;
import WiSAR.Durations;
import WiSAR.Agents.OperatorRole;
import WiSAR.Agents.UAVRole;
import WiSAR.submodule.UAVFlightPlan.States;
import CUAS.Simulator.Actor;
import CUAS.Simulator.IData;
import CUAS.Simulator.IStateEnum;
import CUAS.Simulator.Simulator;

public class UAVVideoFeed extends Actor {

	public enum Outputs implements IData {
		VF_SIGNAL_OK,
		VF_SIGNAL_BAD,
		VF_SIGNAL_NONE
	}
	
	public enum States implements IStateEnum{
		SIGNAL_NONE,
		SIGNAL_OK,
		SIGNAL_BAD
	}
	
	public UAVVideoFeed(){
		name(Actors.UAV.name());
		nextState(States.SIGNAL_NONE, 1);
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
	        case SIGNAL_NONE:
	        case SIGNAL_BAD:
	        case SIGNAL_OK:
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
		case SIGNAL_NONE: 
			//check if there is a new path to immediately follow, if so return to YES_PATH
			if ( uav.contains(UAVRole.Outputs.UAV_TAKE_OFF) ){
				nextState(States.SIGNAL_OK, 1);
			}
			break;
		case SIGNAL_OK:
			//TODO Listen for a Bad Video Stream Event, this makes the stream bad until it lands
			
			if ( uav.contains(UAVRole.Outputs.UAV_LANDED) )
				nextState(States.SIGNAL_NONE, 1);
			break;
		case SIGNAL_BAD:
			if ( uav.contains(UAVRole.Outputs.UAV_READY) )
				nextState(States.SIGNAL_NONE, 1);
			break;
		default:
			break;
		}

	}

	
	private void setObservations()
	{
		switch((States) state()) {
			case SIGNAL_NONE:
				sim().addObservation(Outputs.VF_SIGNAL_NONE, this.name());
				break;
			case SIGNAL_BAD:
				sim().addObservation(Outputs.VF_SIGNAL_BAD, this.name());
				break;
			case SIGNAL_OK:
				sim().addObservation(Outputs.VF_SIGNAL_OK, this.name());
				break;
		}
	}
=======
import CUAS.Simulator.Actor;
import CUAS.Simulator.IData;
import CUAS.Simulator.IStateEnum;
import CUAS.Simulator.Simulator;
import WiSAR.Actors;
import WiSAR.Agents.UAVRole;
import WiSAR.Events.VideoFeedEvent;

public class UAVVideoFeed extends Actor {

	public enum Outputs implements IData{
		VF_SIGNAL_NONE,
		VF_SIGNAL_OK,
		VF_SIGNAL_BAD
	}
	
	public enum States implements IStateEnum{
		NONE,
		OK,
		BAD,
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
		case NONE:
			if(uav.contains(UAVRole.Outputs.UAV_TAKE_OFF)){
				nextState(States.OK,1);
			}else{
				//this state is only possible while the uav is on the ground.
				assert (uav.contains(UAVRole.Outputs.UAV_READY)
						|| uav.contains(UAVRole.Outputs.UAV_LANDED)
						|| uav.contains(UAVRole.Outputs.UAV_CRASHED)) : "The video feed should not be idle while the uav is in the air";
			}
			break;
		case OK:
			if(input.contains(VideoFeedEvent.Outputs.E_VSIGNAL_BAD)){
				nextState(States.BAD,1);
			}else if(uav.contains(UAVRole.Outputs.UAV_LANDED)
					|| uav.contains(UAVRole.Outputs.UAV_CRASHED)){
				nextState(States.NONE,1);
			}
			break;
		case BAD:
			if(uav.contains(UAVRole.Outputs.UAV_LANDED)
					|| uav.contains(UAVRole.Outputs.UAV_CRASHED)){
				nextState(States.NONE,1);
			}
			break;
		}

	}

	private void setObservations() {
		IData _state = Outputs.VF_SIGNAL_NONE;
		switch((States)state()){
		case NONE:
			_state = Outputs.VF_SIGNAL_NONE;
			break;
		case OK:
			_state = Outputs.VF_SIGNAL_OK;
			break;
		case BAD:
			_state = Outputs.VF_SIGNAL_BAD;
			break;
		}
		sim().addObservation(_state, this.name());
	}

>>>>>>> 095d6bd implemented the video feed submodule of the UAV and the VF event.
}
