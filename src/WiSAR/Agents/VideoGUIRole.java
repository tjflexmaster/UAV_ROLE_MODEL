package WiSAR.Roles;

import NewModel.Events.IEvent;
import NewModel.Roles.Role;
import NewModel.Simulation.IInputEnum;
import NewModel.Simulation.IOutputEnum;
import NewModel.Simulation.IStateEnum;
import NewModel.Simulation.Simulator;
import WiSAR.Durations;
import WiSAR.Events;

public class VideoGUIRole extends Role {

    public enum Inputs implements IInputEnum
    {
        /**
         * For the GUI
         */
    	VO_POKE,
    	START,
        CLICK,
        TERMINATE,
        STREAM_ENDED,
        BAD_STREAM,
        ANOMALY_PRESENT, 
        FALSE_POSITIVE,
        TRUE_POSITIVE, 
       
    }
   
    public enum Outputs implements IOutputEnum
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
	    public boolean processNextState() {//Is our next state now?
	        if ( nextStateTime() != Simulator.getInstance().getTime() ) {
	            return false;
	        }
	        state(nextState());
	        switch ((States) nextState()) {
		        case ACK_VO:
		        	_output.add(Outputs.ACK_VO);
		        	nextState(States.RX_VO,1);
		        	break;
		        case RX_VO:
		        	if ( _output.contains(Outputs.STREAM_ENDED) )
		        		nextState(States.IDLE,simulator().duration(Durations.VGUI_RETURN_TO_IDLE.range()));
		        	else 
		        		nextState(States.STREAMING,simulator().duration(Durations.VGUI_START_STREAM.range()));
		        	break;
		        case STREAMING:
		        	if ( _output.contains(Outputs.STREAM_ENDED) ) {
		        		nextState(States.IDLE,simulator().duration(Durations.VGUI_RETURN_TO_IDLE.range()));
		        	} else {
			        	nextState(null,0);
		        	}
		        	break;
	        	default:
		        	nextState(null,0);
		        	break;
	        }
	        return false;
	    }

	    @Override
	    public void updateState () {
	    	_output.clear();
			switch ( (States) state() ) {
				case IDLE:
					if (_input.contains(Inputs.VO_POKE)) {
						nextState(States.ACK_VO,1);
					}
					break;
				case RX_VO:
					if (_input.contains(Inputs.TERMINATE)) {
						_output.add(Outputs.STREAM_ENDED);
						nextState(States.IDLE,1);
					} else if (_input.contains(Inputs.START)) {
						_output.add(Outputs.STREAM_STARTED);
						nextState(States.STREAMING,1);
					} else if (_input.contains(Inputs.CLICK)) {
						_output.add(Outputs.ANOMALY_IDENTIFIED);
						nextState(States.STREAMING,1);
					}
					break;
				case STREAMING:
					if (_input.contains(Inputs.STREAM_ENDED)) {
						_output.add(Outputs.STREAM_ENDED);
						nextState(States.IDLE,1);
					} else {
			        	nextState(null,0);
						if (_input.contains(Inputs.FALSE_POSITIVE)) {
							_output.add(Outputs.FALSE_POSITIVE);
							nextState(States.STREAMING,1);
						}else if (_input.contains(Inputs.TRUE_POSITIVE)) {
							_output.add(Outputs.TRUE_POSITIVE);
							nextState(States.STREAMING,1);
						}else if (_input.contains(Inputs.BAD_STREAM)) {
							_output.add(Outputs.BAD_STREAM);
							nextState(States.STREAMING,1);
						}
						if (_input.contains(Inputs.CLICK)) {
							nextState(States.ACK_VO,1);
						}
					}
					break;
				default:
					break;
			}
	    }

    @Override
    public void processEvent(IEvent event) {
        // TODO Auto-generated method stub
    	switch((Events)event){
    	case VGUI_INACCESSIBLE:
    		_input.add(Inputs.STREAM_ENDED);
    	case VGUI_FALSE_POSITIVE:
    		_input.add(Inputs.FALSE_POSITIVE);
    	case VGUI_TRUE_POSITIVE:
    		_input.add(Inputs.TRUE_POSITIVE);
    	}
    }
   
    /**
     * /////////////////////////////PRIVATE HELPER METHODS///////////////////////////////////////////
     */
}