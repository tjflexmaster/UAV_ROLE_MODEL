package WiSAR.submodule;

import java.util.ArrayList;
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
		IDLE,
		OK,
		BAD,
	}
	
	public UAVVideoFeed(){
		name(Actors.UAV_VIDEO_FEED.name());
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
			break;
		case OK:
			if(input.contains(VideoFeedEvent.Outputs.E_VSIGNAL_BAD)){
				nextState(States.BAD,1);
			}else if(uav.contains(UAVRole.Outputs.UAV_LANDED)
					|| uav.contains(UAVRole.Outputs.UAV_CRASHED)){
				nextState(States.IDLE,1);
			}
			break;
		case BAD:
			if(uav.contains(UAVRole.Outputs.UAV_LANDED)
					|| uav.contains(UAVRole.Outputs.UAV_CRASHED)){
				nextState(States.IDLE,1);
			}
			break;
		}

	}

	private void setObservations() {
		IData _state = Outputs.VF_SIGNAL_NONE;
		switch((States)state()){
		case IDLE:
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
}
