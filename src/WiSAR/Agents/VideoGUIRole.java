package WiSAR.Agents;

import java.util.ArrayList;

import CUAS.Simulator.Actor;
import CUAS.Simulator.IData;
import CUAS.Simulator.IStateEnum;
import CUAS.Simulator.Simulator;
import WiSAR.Actors;
import WiSAR.Events.TargetSightingFalseEvent;
import WiSAR.Events.TargetSightingTrueEvent;

public class VideoGUIRole extends Actor {

	boolean target_true;
   
    public enum Outputs implements IData
    {
    	/**
    	 * Observables
    	 */
    	VGUI_FLYBY,
    	VGUI_NORMAL,
    	
        /**
         * For the VideoOperator or MissionManager
         */
    	VGUI_ACK,
        VGUI_STREAM_ENDED,
        VGUI_STREAM_STARTED, 
        VGUI_BAD_STREAM,
        VGUI_FALSE_POSITIVE, 
        VGUI_TRUE_POSITIVE,
        VGUI_ACCESSIBLE,
        
        /**
         * GUI to GUI ouputs
         */
        VGUI_REQUEST_FLYBY_T,
        VGUI_REQUEST_FLYBY_F, 
        VGUI_NO_STREAM,
        VGUI_FLYBY_T, 
        VGUI_FLYBY_F, 
        VGUI_VALIDATION_REQ_TRUE, 
        VGUI_VALIDATION_REQ_FALSE,
       
    }
   
    public enum States implements IStateEnum
    {
        IDLE,
        STREAMING_NORMAL,
        STREAMING_FLYBY,
        INACCESSIBLE,
    }
    
	public VideoGUIRole()
	{
		name( Actors.VIDEO_OPERATOR_GUI.name() );
		nextState(States.IDLE, 1);
		
	}

   @Override
    public void processNextState() {//Is our next state now?
        if ( nextStateTime() != Simulator.getInstance().getTime() ) {
            return;
        }
        state(nextState());
        switch ((States) nextState()) {
        	default:
	        	nextState(null,0);
	        	break;
        }
    }

	@Override
	public void processInputs() {
		
		//Pull Input and any observations that need to be made from the simulator
		ArrayList<IData> input = sim().getInput(this.name());
		ArrayList<IData> uav_data = sim().getObservations(Actors.UAV.name());
		switch ( (States) state() ) {
			case IDLE:
				if(uav_data.contains(UAVRole.Outputs.UAV_FEED_ACTIVE)){
					nextState(States.STREAMING_NORMAL,1);
				}
				break;
			case STREAMING_NORMAL:
				if(input.contains(TargetSightingFalseEvent.Outputs.TARGET_SIGHTED_FALSE)){
					//TODO implement behavior till receiving input.contains(TargetSightingFalseEvent.Outputs.TARGET_SIGHTED_FALSE_END
				}
				if(input.contains(TargetSightingTrueEvent.Outputs.TARGET_SIGHTED_TRUE)){
					//TODO implement behavior till receiving input.contains(TargetSightingFalseEvent.Outputs.TARGET_SIGHTED_TRUE_END
				}
				if (input.contains(VideoOperatorRole.Outputs.VO_POKE)) {
					sim().addOutput(Actors.VIDEO_OPERATOR.name(), Outputs.VGUI_ACK);
				}
				else if(input.contains(VideoOperatorRole.Outputs.VO_END)){
					if ( input.contains(VideoOperatorRole.Outputs.VO_POSSIBLE_ANOMALY_DETECTED_T) ) {
						sim().addOutput(Actors.OPERATOR_GUI.name(), Outputs.VGUI_REQUEST_FLYBY_T);
						nextState(States.STREAMING_NORMAL,1);
					} else if ( input.contains(VideoOperatorRole.Outputs.VO_POSSIBLE_ANOMALY_DETECTED_F) ) {
						sim().addOutput(Actors.OPERATOR_GUI.name(), Outputs.VGUI_REQUEST_FLYBY_F);
						nextState(States.STREAMING_NORMAL,1);
					} else if ( input.contains(VideoOperatorRole.Outputs.VO_LIKELY_ANOMALY_DETECTED_T) ) {
						sim().addOutput(Actors.OPERATOR_GUI.name(), Outputs.VGUI_REQUEST_FLYBY_T);
						nextState(States.STREAMING_NORMAL,1);
					} else if ( input.contains(VideoOperatorRole.Outputs.VO_LIKELY_ANOMALY_DETECTED_F) ) {
						sim().addOutput(Actors.OPERATOR_GUI.name(), Outputs.VGUI_REQUEST_FLYBY_F);
						nextState(States.STREAMING_NORMAL,1);
					}
				}
				else if(input.contains(OperatorGUIRole.Outputs.OGUI_FLYBY_REQ_T)){
					sim().addOutput(Actors.VIDEO_OPERATOR.name(), Outputs.VGUI_FLYBY_T);
					nextState(States.STREAMING_FLYBY,1);
				}
				else if(input.contains(OperatorGUIRole.Outputs.OGUI_FLYBY_REQ_F)){
					sim().addOutput(Actors.VIDEO_OPERATOR.name(), Outputs.VGUI_FLYBY_F);
					nextState(States.STREAMING_FLYBY,1);
				}

				if(uav_data.contains(UAVRole.Outputs.UAV_FEED_INACTIVE)){
					nextState(States.INACCESSIBLE,1);
				} else if(uav_data.contains(UAVRole.Outputs.UAV_FEED_INACTIVE)){
					nextState(States.IDLE,1);
				}
				break;
			case STREAMING_FLYBY:
				if(input.contains(VideoOperatorRole.Outputs.VO_FLYBY_END)){
					nextState(States.STREAMING_NORMAL,1);
				}
				if(uav_data.contains(UAVRole.Outputs.UAV_FEED_INACTIVE)){
					nextState(States.INACCESSIBLE,1);
				}
				break;
			case INACCESSIBLE:
				if(uav_data.contains(UAVRole.Outputs.UAV_FEED_ACTIVE)){
					nextState(States.STREAMING_NORMAL,1);
				}
				break;
			default:
				break;
		}
		setObservations();
    }

	private void setObservations() {
		IData _state = Outputs.VGUI_NO_STREAM;
		switch((States)state()){
		case STREAMING_FLYBY:
			_state = Outputs.VGUI_FLYBY;
			break;
		case STREAMING_NORMAL:
			_state = Outputs.VGUI_NORMAL;
			break;
		}
		sim().addObservation(_state, this.name());
		
	}

    /**
     * /////////////////////////////PRIVATE HELPER METHODS///////////////////////////////////////////
     */
}
