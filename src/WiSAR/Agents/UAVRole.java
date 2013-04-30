<<<<<<< HEAD
package WiSAR.Agents;

import WiSAR.Durations;
import CUAS.Simulator.Actor;
import CUAS.Simulator.IData;
import CUAS.Simulator.IStateEnum;
import CUAS.Simulator.Simulator;

public class UAVRole extends Actor  {
	
	IData _search;
	IData _battery;
	IData _signal;
	
    public enum Outputs implements IData
    {
    	UAV_READY,
    	UAV_TAKE_OFF,
    	UAV_FLYING,
    	UAV_LANDING,
    	UAV_LANDED,
    	UAV_CRASHED,
    	
    	UAV_BAT_OK,
    	UAV_BAT_LOW,
    	
    	UAV_SIGNAL_OK,
    	UAV_SIGNAL_LOST,
    	
    	UAV_FLIGHT_PLAN_YES,
    	UAV_FLIGHT_PLAN_NO,
    	
    	UAV_HAG_OK,
    	UAV_HAG_LOW,
    	
    	UAV_PATH_OK,
    	UAV_PATH_BAD
    }
    
    public enum States implements IStateEnum
    {
        UAV_READY,
        UAV_TAKE_OFF,
        UAV_FLYING,
        UAV_LOITERING,
        UAV_LANDING,
        UAV_LANDED,
        UAV_CRASHED
    }
    
	@Override
	public void processNextState() {
        if ( nextStateTime() != Simulator.getInstance().getTime() ) {
            return;
        }
        state(nextState());
        switch ((States) nextState()) {
        	/**
        	 * The UAV should not change any of its states alone.
        	 * 
        	 * Look to the sub-actors for when specific durations will occur.
        	 */
	        case UAV_CRASHED:
	        	assert false : "The UAV Crashed!";
	        	break;
        	default:
	        	nextState(null,0);
	        	break;
        }
        
        //Set Observations
        setObservations();
	}

	@Override
	public void processInputs() {
		switch ( (States) state() ) {
			case UAV_READY:
				//TODO Handle new Flight path
				
				//TODO Handle Take off cmd
				break;
			case UAV_TAKE_OFF:
				//TODO Handle Land Cmd
				
				//TODO Handle new Flight path
				
				//TODO Handle Loiter
				break;
			case UAV_FLYING:
				//TODO Handle New Flight Path
				
				//TODO Handle Land Cmd
				
				//TODO Handle Loiter Cmd
				
				break;
			case UAV_LOITERING:
				//TODO Handle New Flight Path
				
				//TODO Handle Resume cmd
				
				//TODO Handle Land cmd
				break;
			case UAV_LANDING:
				//TODO Handle Fly cmd
				//TODO Handle new Flight Plan
				break;
			case UAV_LANDED:
				//TODO Handle New Flight Plan
				break;
			case UAV_CRASHED:
				//Handle Nothing cause simulation should have ended.
				break;
			default:
				break;
		}
	}
	
	private void setObservations()
	{
		
	}

}
=======
package WiSAR.Agents;

import WiSAR.Durations;
import CUAS.Simulator.Actor;
import CUAS.Simulator.IData;
import CUAS.Simulator.IStateEnum;
import CUAS.Simulator.Simulator;

public class UAVRole extends Actor  {
	
    public enum Outputs implements IData
    {
		DEACTIVATE_BATTERY,
		ACTIVATE_BATTERY
    	
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
>>>>>>> branch 'master' of https://github.com/tjflexmaster/UAV_ROLE_MODEL.git
