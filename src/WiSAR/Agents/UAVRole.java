
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
	boolean _flight_plan = false;
	
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
    
    public UAVRole(){
    	name(Roles.UAV.name());
    	nextState(States.UAV_READY,1);
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
				if(_input.contains(OperatorGUIRole.Outputs.GOOD_PATH)){
					_flight_plan = true;
				}
				//TODO Handle Take off cmd
				if(_input.contains(OperatorGUIRole.Outputs.TAKE_OFF)){
					nextState(States.UAV_TAKE_OFF,1);
				}
				break;
			case UAV_TAKE_OFF:
				//TODO Handle Land Cmd
				
				//TODO Handle new Flight path
				int duration = sim().duration(Durations.UAV_TAKE_OFF_DUR.range());
				if(_flight_plan){
					nextState(States.UAV_FLYING,duration);
				}
				//TODO Handle Loiter
				else{
					nextState(States.UAV_LOITERING,duration);
				}
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
