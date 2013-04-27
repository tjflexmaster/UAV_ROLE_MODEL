package WiSAR.Agents;

import java.util.ArrayList;

import CUAS.Simulator.Actor;
import CUAS.Simulator.IData;
import CUAS.Simulator.IStateEnum;
import CUAS.Simulator.Simulator;
import WiSAR.Durations;

public class VideoGUIRole extends Actor {

   
    public enum Outputs implements IData
    {
        /**
         * GUI Inputs
         */
    	ACK_VO,
        STREAM_ENDED,
        BAD_STREAM,
        ANOMALY_PRESENT,
        STREAM_STARTED, 
        ANOMALY_IDENTIFIED, 
        FALSE_POSITIVE, 
        TRUE_POSITIVE
       
    }
   
    public enum States implements IStateEnum
    {
        IDLE,
        RX_VO,
        ACK_VO,
        STREAMING
    }
    
	public VideoGUIRole()
	{
		name( Roles.VIDEO_OPERATOR_GUI.name() );
		nextState(States.IDLE, 1);
		
	}

   @Override
    public void processNextState() {//Is our next state now?
        if ( nextStateTime() != Simulator.getInstance().getTime() ) {
            return;
        }
        state(nextState());
        switch ((States) nextState()) {
	        case ACK_VO:
	        	_output.add(Outputs.ACK_VO);
	        	nextState(States.RX_VO,1);
	        	break;
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
				if (_input.contains(VideoOperatorRole.Outputs.POKE_VO)) {
					nextState(States.ACK_VO,1);
				}
				break;
			case RX_VO:
				if (_input.contains(VideoOperatorRole.Outputs.END_FEED_VO)) {
					_output.add(Outputs.STREAM_ENDED);
					nextState(States.IDLE,1);
				} else if (_input.contains(VideoOperatorRole.Outputs.START_FEED_VO)) {
					_output.add(Outputs.STREAM_STARTED);
					nextState(States.STREAMING,1);
				} else if (_input.contains(VideoOperatorRole.Outputs.CLICK_FRAME_VO)) {
					_output.add(Outputs.ANOMALY_IDENTIFIED);
					nextState(States.STREAMING,1);
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
				if (_input.contains(VideoOperatorRole.Outputs.POKE_VO)) {
					nextState(States.ACK_VO,1);
				}
				break;
			default:
				break;
		}
    }


	@Override
	public void addInput(ArrayList<IData> data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ArrayList<IData> getObservations() {
		// TODO Auto-generated method stub
		return null;
	}

    /**
     * /////////////////////////////PRIVATE HELPER METHODS///////////////////////////////////////////
     */
}