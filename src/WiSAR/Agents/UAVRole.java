package WiSAR.Agents;

import WiSAR.Durations;
import CUAS.Simulator.Actor;
import CUAS.Simulator.IData;
import CUAS.Simulator.IStateEnum;
import CUAS.Simulator.Simulator;

public class UAVRole extends Actor  {
	
    public enum Outputs implements IData
    {
    	
    }
    
    public enum States implements IStateEnum
    {
        IDLE,
        RX_OP_GUI,
        TAKING_OFF,
        IN_AIR,
        LANDING,
        CRASHING,
        LOST
    }
    
	@Override
	public void processNextState() {
        if ( nextStateTime() != Simulator.getInstance().getTime() ) {
            return;
        }
        state(nextState());
        switch ((States) nextState()) {
	        case RX_OP_GUI:
	        	nextState(States.IN_AIR,sim().duration(Durations.OGUI_RX_DUR.range()));
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
				break;
			case RX_OP_GUI:
				break;
			case TAKING_OFF:
				break;
			case IN_AIR:
				break;
			case LANDING:
				break;
			case CRASHING:
				break;
			case LOST:
				break;
			default:
				break;
		}
	}

}
