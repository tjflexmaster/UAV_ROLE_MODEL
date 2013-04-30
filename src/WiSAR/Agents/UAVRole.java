
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
				//Handle new Flight path
				if(_input.contains(OperatorGUIRole.Outputs.GOOD_PATH)){
					_flight_plan = true;
				}
				//Handle Take off cmd
				if(_input.contains(OperatorGUIRole.Outputs.TAKE_OFF)){
					nextState(States.UAV_TAKE_OFF,1);
				}
				break;
			case UAV_TAKE_OFF:
				//Handle Land Cmd
				if(_input.contains(OperatorGUIRole.Outputs.LAND)){
					int duration = sim().duration(Durations.UAV_LANDING_DUR.range());
					nextState(States.UAV_LANDED,duration);
				}
				//Handle new Flight path
				if(_flight_plan){
					int duration = sim().duration(Durations.UAV_TAKE_OFF_DUR.range());
					nextState(States.UAV_FLYING,duration);
				}
				else if (_input.contains(OperatorGUIRole.Outputs.GOOD_PATH)) {
					_flight_plan = true;
					int duration = sim().duration(Durations.UAV_TAKE_OFF_DUR.range());
					nextState(States.UAV_FLYING,duration);
				}
				//Handle Loiter
				else{
					int duration = sim().duration(Durations.UAV_LOW_BATTERY_THRESHOLD_DUR.range());
					nextState(States.UAV_LOITERING,duration);
					_flight_plan = false;
				}
				break;
			case UAV_FLYING:
				//Handle New Flight Path
				if(_input.contains(OperatorGUIRole.Outputs.GOOD_PATH)){
					_flight_plan = true;
				}
				//Handle Land Cmd
				if(_input.contains(OperatorGUIRole.Outputs.LAND)){
					int duration = sim().duration(Durations.UAV_ADJUST_PATH.range());
					nextState(States.UAV_LANDING,duration);
				}
				//Handle Loiter Cmd
				else{
					int duration = sim().duration(Durations.UAV_LOW_BATTERY_THRESHOLD_DUR.range());
					nextState(States.UAV_LOITERING,duration);
					_flight_plan = false;
				}
				break;
			case UAV_LOITERING:
				//Handle New Flight Path
				if(_input.contains(OperatorGUIRole.Outputs.GOOD_PATH)){
					int duration = sim().duration(Durations.UAV_ADJUST_PATH.range());
					nextState(States.UAV_FLYING,duration);
					_flight_plan = true;
				}
				//Handle Resume cmd
				else if(_input.contains(OperatorGUIRole.Outputs.RESUME_PATH)){
					int duration = sim().duration(Durations.UAV_ADJUST_PATH.range());
					nextState(States.UAV_FLYING,duration);
					_flight_plan = true;
				}
				//Handle Land cmd
				else if(_input.contains(OperatorGUIRole.Outputs.LAND)){
					int duration = sim().duration(Durations.UAV_LANDING_DUR.range());
					nextState(States.UAV_LANDED,duration);
				}
				break;
			case UAV_LANDING:
				//TODO Handle Fly cmd
				//Handle new Flight Plan
				if(_input.contains(OperatorGUIRole.Outputs.GOOD_PATH)){
					int duration = sim().duration(Durations.UAV_ADJUST_PATH.range());
					nextState(States.UAV_FLYING,duration);
					_flight_plan = true;
				}
				break;
			case UAV_LANDED:
				//Handle New Flight Plan
				if(_input.contains(OperatorGUIRole.Outputs.GOOD_PATH)){
					int duration = sim().duration(Durations.UAV_PREPARATION_DUR.range());
					nextState(States.UAV_READY,duration);
					_flight_plan = true;
				}
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
