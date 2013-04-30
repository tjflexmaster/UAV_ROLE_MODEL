package WiSAR.Agents;

import java.util.ArrayList;
import WiSAR.Actors;
import WiSAR.Durations;
import WiSAR.submodule.UAVBattery;
import CUAS.Simulator.Actor;
import CUAS.Simulator.IActor;
import CUAS.Simulator.IData;
import CUAS.Simulator.IStateEnum;
import CUAS.Simulator.Simulator;
import WiSAR.Actors;
import WiSAR.Durations;
import WiSAR.submodule.UAVBattery;
import WiSAR.submodule.UAVFlightPlan;

public class UAVRole extends Actor  {
	
	IData _flight_plan;
	IData _battery;
	IData _signal;
	IData _hag;
	IData _path;
	
	protected ArrayList<IActor> _sub_actors = new ArrayList<IActor>();
	
    public enum Outputs implements IData
    {
    	UAV_READY,
    	UAV_TAKE_OFF,
    	UAV_FLYING,
    	UAV_LOITERING,
    	UAV_LANDING,
    	UAV_LANDED,
    	UAV_CRASHED,
    	
    	UAV_SIGNAL_OK,
    	UAV_SIGNAL_LOST,
    	
    	UAV_FLIGHT_PLAN_YES,
    	UAV_FLIGHT_PLAN_NO,
    	
    	UAV_HAG_OK,
    	UAV_HAG_LOW,
    	
    	UAV_PATH_OK,
    	
    	/**
    	 * GUI Outputs
    	 */
    	UAV_PATH_COMPLETE,
    	UAV_PATH_BAD,
    	
    	/**
    	 * Output to battery
    	 */
    	ACTIVATE_BATTERY,
    	DEACTIVATE_BATTERY,
    	RESET_BATTERY
    }
    
    public enum States implements IStateEnum
    {
        UAV_READY,
        UAV_TAKE_OFF,
        UAV_FLYING,
        UAV_LOITERING,
        UAV_LANDING,
        UAV_LANDED,
        UAV_CRASHED,
        UAV_END_PATH
    }
      
    
    public UAVRole()
    {
    	name(Actors.UAV.name());
    	nextState(States.UAV_READY, 1);
    	
    	//Add children
    	_sub_actors.add(new UAVBattery());
    	_sub_actors.add(new UAVFlightPlan());
    	
    	//Duplicate input to all sub actors
    	for(IActor sub : _sub_actors) {
    		//The child receives all the input that the parent receives
    		sim().linkInput(this.name(), sub.name());
    		
    		//The parent makes all of this actors observations visible through itself
    		//When observing the UAV you will also see the battery observations
    		sim().linkObservations(sub.name(), this.name()); 
    	}
    }
    
	@Override
    public int nextStateTime()
    {
    	/**
		 * Process all sub-actors first
		 */
		int min_next_state_time = 0;
		for(IActor child : _sub_actors) {
			int time = child.nextStateTime();
			if ( time  > 0 ) {
				min_next_state_time = Math.min(time, min_next_state_time);
			} else {
				min_next_state_time = Math.max(time, min_next_state_time);
			}
		}
		
		if ( min_next_state_time == 0 )
			return super.nextStateTime();
		else if ( super.nextStateTime() == 0 )
			return min_next_state_time;
		else
			return Math.min(super.nextStateTime(), min_next_state_time);

    }
    
    
	@Override
	public void processNextState() {

		/**
		 * Process all sub-actors first
		 */
		for(IActor child : _sub_actors) {
			child.processNextState();
		}
		
		/**
		 * Now do the processing for this state
		 */
        if ( nextStateTime() != Simulator.getInstance().getTime() ) {
        	//Set Observations
        	if ( state() != null )
        		setObservations();
            return;
        }
        state(nextState());
        
        
        switch ((States) nextState()) {
        	/**
        	 * The UAV should not change any of its states alone.
        	 * 
        	 * Look to the sub-actors for when specific durations will occur.
        	 */
 
        	case UAV_READY:
        		//reset battery
        		sim().addOutput(Actors.UAV_BATTERY.name(), Outputs.DEACTIVATE_BATTERY);
        		sim().addOutput(Actors.UAV_BATTERY.name(), Outputs.RESET_BATTERY);
        		nextState(null, 0);
        		break;
        	case UAV_TAKE_OFF:
        		sim().addOutput(Actors.UAV_BATTERY.name(), Outputs.ACTIVATE_BATTERY);
        		int take_off_dur = sim().duration(Durations.UAV_TAKE_OFF_DUR.range());
        		
        		//TODO Base next state on the observation of the UAV Flight Plan
    			nextState(States.UAV_LOITERING, take_off_dur );
        		break;
        	case UAV_FLYING:
        		nextState(null, 0);
        		break;
        	case UAV_LOITERING:
        		nextState(null, 0);
        		break;
        	case UAV_LANDED:
        		sim().addOutput(Actors.UAV_BATTERY.name(), Outputs.DEACTIVATE_BATTERY);
        		nextState(null, 0);
        		break;
        	case UAV_LANDING:
    			nextState(States.UAV_LANDED,  sim().duration(Durations.UAV_LANDING_DUR.range()));
        		break;
	        case UAV_CRASHED:
	        	assert false : "The UAV Crashed!";
	        	break;
	        case UAV_END_PATH:
	        	nextState(States.UAV_LOITERING,1);
        	default:
	        	nextState(null,0);
	        	break;
        }
        
        //Set Observations
        setObservations();
	}

	@Override
	public void processInputs() {

		/**
		 * Process all sub-actors first
		 */
		for(IActor child : _sub_actors) {
			child.processInputs();
		}
		
		//Pull Input and any observations that need to be made from the simulator
		ArrayList<IData> input = sim().getInput(this.name());
		
		/**
		 * Get observations of sub actors
		 */
		handleBatteryObservations();
		if(_battery == UAVBattery.Outputs.BATTERY_DEAD){
			nextState(States.UAV_CRASHED,1);
			return;
		}
		handleFlightPlanObservations();
		
		switch ( (States) state() ) {
			case UAV_READY:
//				//Handle new Flight path
//				if(_input.contains(OperatorGUIRole.Outputs.GOOD_PATH)){
//					_flight_plan = true;
//				}
				//Handle Take off cmd
				if(input.contains(OperatorGUIRole.Outputs.TAKE_OFF)){
					nextState(States.UAV_TAKE_OFF,1);
				}
				break;
			case UAV_TAKE_OFF:
				if ( _battery == UAVBattery.Outputs.BATTERY_DEAD ) {
					nextState(States.UAV_CRASHED, 1);
				}
				//Handle Land Cmd
				if(input.contains(OperatorGUIRole.Outputs.LAND)){
					int duration = sim().duration(Durations.UAV_LANDING_DUR.range());
					nextState(States.UAV_LANDED,duration);
				}else if(_flight_plan == UAVFlightPlan.Outputs.UAV_FLIGHT_PLAN_YES){
					nextState(States.UAV_FLYING,sim().duration(Durations.UAV_TAKE_OFF_DUR.range()));
				}
				//Handle new Flight path
//				if(_flight_plan){
//					int duration = sim().duration(Durations.UAV_TAKE_OFF_DUR.range());
//					nextState(States.UAV_FLYING,duration);
//				}
//				else if (_input.contains(OperatorGUIRole.Outputs.GOOD_PATH)) {
//					_flight_plan = true;
//					int duration = sim().duration(Durations.UAV_TAKE_OFF_DUR.range());
//					nextState(States.UAV_FLYING,duration);
//				}
				//Handle Loiter
//				else{
//					int duration = sim().duration(Durations.UAV_LOW_BATTERY_THRESHOLD_DUR.range());
//					nextState(States.UAV_LOITERING,duration);
//					_flight_plan = false;
//				}
				break;
			case UAV_FLYING:
				if ( _battery == UAVBattery.Outputs.BATTERY_DEAD ) {
					nextState(States.UAV_CRASHED, 1);
				} else if(_flight_plan == UAVFlightPlan.Outputs.UAV_FLIGHT_PLAN_COMPLETE){
					nextState(States.UAV_LOITERING,sim().duration(Durations.UAV_ADJUST_PATH.range()));
				}
				//Handle New Flight Path
//				if(_input.contains(OperatorGUIRole.Outputs.GOOD_PATH)){
//					_flight_plan = true;
//				}
				//Handle Land Cmd
				if(input.contains(OperatorGUIRole.Outputs.LAND)){
					int duration = sim().duration(Durations.UAV_ADJUST_PATH.range());
					nextState(States.UAV_LANDING,duration);
				}
				//Handle Loiter Cmd
				else if(input.contains(OperatorGUIRole.Outputs.LOITER)){
					int duration = sim().duration(Durations.UAV_ADJUST_PATH.range());
					nextState(States.UAV_LOITERING,duration);
//					_flight_plan = false;
				}
				break;
			case UAV_LOITERING:
				if ( _battery == UAVBattery.Outputs.BATTERY_DEAD ) {
					nextState(States.UAV_CRASHED, 1);
				}else{
					if(_flight_plan == UAVFlightPlan.Outputs.UAV_FLIGHT_PLAN_YES){
						nextState(States.UAV_FLYING,1);
					}
				}
				//Handle New Flight Path
//				if(_input.contains(OperatorGUIRole.Outputs.GOOD_PATH)){
//					int duration = sim().duration(Durations.UAV_ADJUST_PATH.range());
//					nextState(States.UAV_FLYING,duration);
//					_flight_plan = true;
//				}
				//Handle Resume cmd
//				else if(_input.contains(OperatorGUIRole.Outputs.RESUME_PATH)){
//					int duration = sim().duration(Durations.UAV_ADJUST_PATH.range());
//					nextState(States.UAV_FLYING,duration);
//					_flight_plan = true;
//				}
				//Handle Land cmd
//				else if(_input.contains(OperatorGUIRole.Outputs.LAND)){
//					int duration = sim().duration(Durations.UAV_LANDING_DUR.range());
//					nextState(States.UAV_LANDED,duration);
//				}
				break;
			case UAV_LANDING:
				if ( _battery == UAVBattery.Outputs.BATTERY_DEAD ) {
					nextState(States.UAV_CRASHED, 1);
				}
				//TODO Handle Fly cmd
				//Handle new Flight Plan
//				if(_input.contains(OperatorGUIRole.Outputs.GOOD_PATH)){
//					int duration = sim().duration(Durations.UAV_ADJUST_PATH.range());
//					nextState(States.UAV_FLYING,duration);
//					_flight_plan = true;
//				}
				break;
			case UAV_LANDED:
				//Handle New Flight Plan
//				if(_input.contains(OperatorGUIRole.Outputs.GOOD_PATH)){
//					int duration = sim().duration(Durations.UAV_PREPARATION_DUR.range());
//					nextState(States.UAV_READY,duration);
//					_flight_plan = true;
//				}
				break;
			case UAV_CRASHED:
				//Handle Nothing cause simulation should have ended.
				break;
			default:
				break;
		}//end switch
	}
	
	/**
	 * Make these things on the UAV observable
	 */
	private void setObservations()
	{
		//Make UAV State Observable
		IData _state = Outputs.UAV_READY;
		switch((States) state() ) {
			case UAV_FLYING:
		    	_state = Outputs.UAV_FLYING;
		    	break;
		    case UAV_READY:
		    	_state = Outputs.UAV_READY;
		    	break;
		    case UAV_TAKE_OFF:
		    	_state = Outputs.UAV_TAKE_OFF;
		    	break;
		    case UAV_LOITERING:
		    	_state = Outputs.UAV_LOITERING;
		    	break;
		    case UAV_LANDING:
		    	_state = Outputs.UAV_LANDING;
		    	break;
		    case UAV_LANDED:
		    	_state = Outputs.UAV_LANDED;
		    	break;
		    case UAV_CRASHED:
		    	_state = Outputs.UAV_CRASHED;
		    	break;
		}
		sim().addObservation(_state, this.name());
	
//		sim().addObservation(_battery, this.name());
//		sim().addObservation(_flight_plan, this.name());
//		sim().addObservation(_hag, this.name());
//		sim().addObservation(_path, this.name());
//		sim().addObservation(_signal, this.name());
	}
	
	/**
	 * Observe UAV Battery
	 */
	private void handleBatteryObservations()
	{
		ArrayList<IData> observations = sim().getObservations(Actors.UAV_BATTERY.name());
		if ( observations.size() > 0 ) {
			_battery = observations.get(0);
		}
	}

	private void handleFlightPlanObservations() {
		ArrayList<IData> observations = sim().getObservations(Actors.UAV_FLIGHT_PLAN.name());
		if ( observations.size() > 0 ) {
			_flight_plan = observations.get(0);
		}
	}
}
