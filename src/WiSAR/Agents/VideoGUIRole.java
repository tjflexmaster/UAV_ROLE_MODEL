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
         * GUI Outputs
         */
    	VGUI_ACK,
        VGUI_STREAM_ENDED,
        VGUI_STREAM_STARTED, 
        VGUI_BAD_STREAM,
        VGUI_FALSE_POSITIVE, 
        VGUI_TRUE_POSITIVE,
        
        /**
         * For the OGUI
         */
        VGUI_REQUEST_FLYBY_T,
        VGUI_REQUEST_FLYBY_F
       
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
				if (input.contains(VideoOperatorRole.Outputs.VO_POKE)) {
					sim().addOutput(Actors.VIDEO_OPERATOR.name(), Outputs.VGUI_ACK);
				}
				if(input.contains(VideoOperatorRole.Outputs.VO_END));{
					if(input.contains(VideoOperatorRole.Outputs.VO_START_FEED)){
						nextState(States.STREAMING_NORMAL,1);
					}
					
					//TODO handle other inputs
				}
				break;
			case STREAMING_NORMAL:
				if(input.contains(VideoOperatorRole.Outputs.VO_END));{
					if (input.contains(VideoOperatorRole.Outputs.VO_END_FEED)) {
//						_output.add(Outputs.VGUI_STREAM_ENDED);
						nextState(States.IDLE,1);
					} else if(input.contains(VideoOperatorRole.Outputs.VO_FLYBY_T)){
						sim().addOutput(Actors.OPERATOR_GUI.name(), Outputs.VGUI_REQUEST_FLYBY_T);
					} else if(input.contains(VideoOperatorRole.Outputs.VO_FLYBY_F)){
						sim().addOutput(Actors.OPERATOR_GUI.name(), Outputs.VGUI_REQUEST_FLYBY_F);
					} else if(input.contains(OperatorGUIRole.Outputs.OGUI_FLYBY_T)){
						nextState(States.STREAMING_FLYBY,1);
					}else if(input.contains(OperatorGUIRole.Outputs.OGUI_FLYBY_F)){
						nextState(States.STREAMING_FLYBY,1);
					}
					
					//TODO handle other inputs
				}
				//TODO implement event handling
//				if (_input.contains(EventEnum.VGUI_INACCESSIBLE)) {
//					_output.add(Outputs.STREAM_ENDED);
//					nextState(States.IDLE,1);
//				} else if (_input.contains(EventEnum.VGUI_FALSE_POSITIVE)) {
//					_output.add(Outputs.FALSE_POSITIVE);
//					nextState(States.STREAMING,1);
//				}else if (_input.contains(EventEnum.VGUI_TRUE_POSITIVE)) {
//					_output.add(Outputs.TRUE_POSITIVE);
//					nextState(States.STREAMING,1);
//				}else if (_input.contains(EventEnum.VGUI_BAD_STREAM)) {
//					_output.add(Outputs.BAD_STREAM);
//					nextState(States.STREAMING,1);
//				}
				if (input.contains(VideoOperatorRole.Outputs.VO_POKE)) {
					sim().addOutput(Actors.VIDEO_OPERATOR.name(), Outputs.VGUI_ACK);
				}
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
