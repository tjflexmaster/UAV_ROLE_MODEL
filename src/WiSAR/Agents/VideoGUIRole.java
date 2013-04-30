package WiSAR.Agents;

import java.util.ArrayList;

import CUAS.Simulator.Actor;
import CUAS.Simulator.IData;
import CUAS.Simulator.IStateEnum;
import CUAS.Simulator.Simulator;
import WiSAR.Actors;
import WiSAR.Durations;

public class VideoGUIRole extends Actor {

   
    public enum Outputs implements IData
    {
        /**
         * GUI Outputs
         */
    	VGUI_ACK,
        VGUI_STREAM_ENDED,
        VGUI_STREAM_STARTED, 
        VGUI_BAD_STREAM,
        VGUI_ANOMALY_IDENTIFIED, 
        VGUI_FALSE_POSITIVE, 
        VGUI_TRUE_POSITIVE
       
    }
   
    public enum States implements IStateEnum
    {
        IDLE,
        RX_VO,
        STREAMING
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
	        case RX_VO:
        		nextState(States.STREAMING,sim().duration(Durations.VGUI_RX_DUR.range()));
	        	break;
        	default:
	        	nextState(null,0);
	        	break;
        }
    }

	@Override
	public void processInputs() {
    	_output.clear();
		switch ( (States) state() ) {
			case IDLE:
				if (_input.contains(VideoOperatorRole.Outputs.VO_POKE)) {
					sim().addOutput(Actors.VIDEO_OPERATOR.name(), Outputs.VGUI_ACK);
					nextState(States.RX_VO, 1);
				}
				break;
			case RX_VO:
				if(_input.contains(VideoOperatorRole.Outputs.VO_END));{
					if (_input.contains(VideoOperatorRole.Outputs.VO_END_FEED)) {
						_output.add(Outputs.VGUI_STREAM_ENDED);
						nextState(States.IDLE,1);
					} else if (_input.contains(VideoOperatorRole.Outputs.VO_START_FEED)) {
						_output.add(Outputs.VGUI_STREAM_STARTED);
						nextState(States.STREAMING,1);
					} else if (_input.contains(VideoOperatorRole.Outputs.VO_CLICK_FRAME)) {
						_output.add(Outputs.VGUI_ANOMALY_IDENTIFIED);
						nextState(States.STREAMING,1);
					}else{
						//TODO handle other inputs
						nextState(States.STREAMING,1);
					}
				}
				break;
			case STREAMING:
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
				if (_input.contains(VideoOperatorRole.Outputs.VO_POKE)) {
					sim().addOutput(Actors.VIDEO_OPERATOR.name(), Outputs.VGUI_ACK);
					nextState(States.RX_VO,1);
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
