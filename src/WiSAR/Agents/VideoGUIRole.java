package WiSAR.Agents;

import java.util.ArrayList;

import CUAS.Simulator.Actor;
import CUAS.Simulator.IData;
import CUAS.Simulator.IStateEnum;
import CUAS.Simulator.Simulator;
import WiSAR.Actors;
import WiSAR.Durations;

public class VideoGUIRole extends Actor {

	boolean target_true;
   
    public enum Outputs implements IData
    {
        /**
         * For the VideoOperator or MissionManager
         */
    	VGUI_ACK,
        VGUI_STREAM_ENDED,
        VGUI_STREAM_STARTED, 
        VGUI_BAD_STREAM,
        VGUI_FALSE_POSITIVE, 
        VGUI_TRUE_POSITIVE,
        
        /**
         * GUI to GUI ouputs
         */
        VGUI_REQUEST_FLYBY_T,
        VGUI_REQUEST_FLYBY_F, 
        VGUI_NO_STREAM,
        VGUI_FLYBY_T, 
        VGUI_FLYBY_F
       
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
		
		switch ( (States) state() ) {
			case IDLE:
				if (input.contains(VideoOperatorRole.Outputs.VO_START_FEED)) {
					nextState(States.STREAMING_NORMAL,1);
				}
				break;
			case STREAMING_NORMAL:
				if (input.contains(VideoOperatorRole.Outputs.VO_POKE)) {
					sim().addOutput(Actors.VIDEO_OPERATOR.name(), Outputs.VGUI_ACK);
				}
				else if(input.contains(VideoOperatorRole.Outputs.VO_END)){
					if ( input.contains(VideoOperatorRole.Outputs.VO_UNLIKELY_ANOMALY_DETECTED_T) ) {
						sim().addOutput(Actors.OPERATOR_GUI.name(), Outputs.VGUI_REQUEST_FLYBY_T);
						nextState(States.STREAMING_NORMAL,1);
					} else if ( input.contains(VideoOperatorRole.Outputs.VO_UNLIKELY_ANOMALY_DETECTED_F) ) {
						sim().addOutput(Actors.OPERATOR_GUI.name(), Outputs.VGUI_REQUEST_FLYBY_F);
						nextState(States.STREAMING_NORMAL,1);
					} else if ( input.contains(VideoOperatorRole.Outputs.VO_POSSIBLE_ANOMALY_DETECTED_T) ) {
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
				else if(input.contains(OperatorGUIRole.Outputs.OGUI_FLYBY_T)){
					sim().addOutput(Actors.VIDEO_OPERATOR.name(), Outputs.VGUI_FLYBY_T);
					nextState(States.STREAMING_FLYBY,1);
				}
				else if(input.contains(OperatorGUIRole.Outputs.OGUI_FLYBY_F)){
					sim().addOutput(Actors.VIDEO_OPERATOR.name(), Outputs.VGUI_FLYBY_F);
					nextState(States.STREAMING_FLYBY,1);
				}
				//TODO implement event handling
//				else if (_input.contains(EventEnum.VGUI_INACCESSIBLE)) {
//					_output.add(Outputs.STREAM_ENDED);
//					nextState(States.IDLE,1);
//				}
//				else if (_input.contains(EventEnum.VGUI_BAD_STREAM)) {
//					_output.add(Outputs.BAD_STREAM);
//					nextState(States.STREAMING,1);
//				}
				break;
			case STREAMING_FLYBY:
				if(input.contains(VideoOperatorRole.Outputs.VO_FLYBY_END)){
					nextState(States.STREAMING_NORMAL,1);
				}
				break;
			default:
				break;
		}
    }

    /**
     * /////////////////////////////PRIVATE HELPER METHODS///////////////////////////////////////////
     */
}
