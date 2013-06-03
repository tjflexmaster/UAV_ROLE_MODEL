package WiSAR.Agents;

import java.util.ArrayList;

import CUAS.Simulator.Actor;
import CUAS.Simulator.IActor;
import CUAS.Simulator.IData;
import CUAS.Simulator.IStateEnum;
import CUAS.Simulator.Simulator;
import WiSAR.Actors;
import WiSAR.Durations;
import WiSAR.Transition;
import WiSAR.submodule.FlybyAnomaly;
import WiSAR.submodule.UAVBattery;
import WiSAR.submodule.UAVFlightPlan;
import WiSAR.submodule.UAVHeightAboveGround;
import WiSAR.submodule.UAVSignal;
import WiSAR.submodule.UAVVideoFeed;

public class UAVRole extends Actor  {
	
	private IData _flight_plan;
	private IData _battery;
	private IData _signal;
	private IData _hag;
	private int _dur;
	private int _take_off_time;
	private boolean _flyby;
	
	protected ArrayList<IActor> _sub_actors = new ArrayList<IActor>();
	
    public enum Outputs implements IData
    {
    	UAV_READY,
    	UAV_TAKE_OFF,
    	UAV_FLYING_NORMAL,
    	UAV_FLYING_FLYBY,
    	UAV_LOITERING,
    	UAV_LANDING,
    	UAV_LANDED,
    	UAV_CRASHED,
    	
    	//Do not send any other output about the UAV, the sub-actors will send that information
    	//Changing this breaks the OperatorGUI
//    	/**
//    	 * Output to VGUI
//    	 */
//    	UAV_FEED_ACTIVE,
//    	UAV_FEED_GOOD,
//    	UAV_FEED_BAD,
//    	UAV_FEED_INACTIVE,
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
    }
	
	/**
	 * Initiate Transitions
	 */
	public ArrayList<Transition> Transitions = new ArrayList<Transition>();
    
    public UAVRole()
    {
    	name(Actors.UAV.name());
    	nextState(States.UAV_READY, 1);
    	_flyby = false;

		//define transitions
		//rx AOI
    	//Transitions.add(new Transition(States.UAV_READY, OperatorGUIRole.Outputs.OP_NEW_FLIGHT_PLAN, States.UAV_READY));
    	//Transitions.add(new Transition(States.UAV_READY, OperatorGUIRole.Outputs.OP_TAKE_OFF, States.UAV_TAKE_OFF, Outputs.UAV_TAKE_OFF));
    	
    	//Add children
    	_sub_actors.add(new UAVBattery());
    	_sub_actors.add(new UAVFlightPlan());
    	_sub_actors.add(new UAVHeightAboveGround());
    	_sub_actors.add(new UAVVideoFeed());
    	_sub_actors.add(new FlybyAnomaly());
    	_sub_actors.add(new UAVSignal());
    	
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
			if ( time  > 0 && min_next_state_time > 0) {
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
        		nextState(null, 0);
        		break;
        	case UAV_TAKE_OFF:
        		_dur = sim().duration(Durations.UAV_TAKE_OFF_DUR.range());
        		_take_off_time = sim().getTime();
        		//Base next state on the observation of the UAV Flight Plan
        		if(_flight_plan == UAVFlightPlan.Outputs.UAV_FLIGHT_PLAN_PAUSED)
        			nextState(States.UAV_FLYING, _dur );
        		else
        			nextState(States.UAV_LOITERING,_dur);
        		break;
        	case UAV_FLYING:
        		nextState(null, 0);
        		break;
        	case UAV_LOITERING:
        		nextState(null, 0);
        		break;
        	case UAV_LANDED:
        		nextState(null, 0);
        		break;
        	case UAV_LANDING:
        		_dur = sim().duration(Durations.UAV_LANDING_DUR.range());
    			nextState(States.UAV_LANDED, _dur);
        		break;
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
		handleHAGObservations();
		handleSignalObservations();
		
		switch ( (States) state() ) {
			case UAV_READY:
				//Handle Take off cmd
				if(input.contains(OperatorRole.Outputs.OP_TAKE_OFF)){
					nextState(States.UAV_TAKE_OFF,1);
				}
				
				//Handle Flyby Cmds
				if ( input.contains(OperatorRole.Outputs.OP_FLYBY_START_F) ||
						input.contains(OperatorRole.Outputs.OP_FLYBY_START_T)) {
					_flyby = true;
				}
				
				if ( input.contains(OperatorRole.Outputs.OP_FLYBY_END) ) {
					_flyby = false;
				}
				break;
			case UAV_TAKE_OFF:
				if ( _battery == UAVBattery.Outputs.BATTERY_DEAD ) {
					nextState(States.UAV_CRASHED, 1);
				}
				//Handle Land Cmd
				else if(input.contains(OperatorRole.Outputs.OP_LAND)){
					_dur -= sim().getTime() - _take_off_time;
					nextState(States.UAV_LANDED,_dur);
				// if mid take off the OGUI transmits a new flight plan and the UAV has no current flight plan
				}else if(_flight_plan == UAVFlightPlan.Outputs.UAV_FLIGHT_PLAN_NO
						&& input.contains(OperatorRole.Outputs.OP_NEW_FLIGHT_PLAN)){
					_dur -= sim().getTime() - _take_off_time;
					nextState(States.UAV_FLYING,_dur);
				// if mid take off the OGUI terminates the current flight plan.
				}else if(_flight_plan == UAVFlightPlan.Outputs.UAV_FLIGHT_PLAN_PAUSED
						&& input.contains(OperatorRole.Outputs.OP_LOITER)){
					_dur -= sim().getTime() - _take_off_time;
					nextState(States.UAV_LOITERING,_dur);
				}
				
				//Handle Flyby Cmds
				if ( input.contains(OperatorRole.Outputs.OP_FLYBY_START_F) ||
						input.contains(OperatorRole.Outputs.OP_FLYBY_START_T)) {
					_flyby = true;
				}
				if ( input.contains(OperatorRole.Outputs.OP_FLYBY_END) ) {
					_flyby = false;
				}
				break;
			case UAV_FLYING:
				if ( _battery == UAVBattery.Outputs.BATTERY_DEAD || _hag == UAVHeightAboveGround.Outputs.HAG_CRASHED) {
					nextState(States.UAV_CRASHED, 1);
				//handle cmd to land
				} else if(input.contains(OperatorRole.Outputs.OP_LAND)){
					_dur = sim().duration(Durations.UAV_ADJUST_PATH.range());
					nextState(States.UAV_LANDING,_dur);
				//if the flight plan reaches completion, the Op orders its termination or to pause the flight
				}else if(_flight_plan == UAVFlightPlan.Outputs.UAV_FLIGHT_PLAN_COMPLETE 
						|| input.contains(OperatorRole.Outputs.OP_LOITER)){
					_dur = sim().duration(Durations.UAV_ADJUST_PATH.range());
					nextState(States.UAV_LOITERING,_dur);
				}
				
				//Handle Flyby Commands
				if ( input.contains(OperatorRole.Outputs.OP_FLYBY_START_F) ||
						input.contains(OperatorRole.Outputs.OP_FLYBY_START_T)) {
					_flyby = true;
				}
				
				if ( input.contains(OperatorRole.Outputs.OP_FLYBY_END) ) {
					_flyby = false;
				}
				break;
			case UAV_LOITERING:
				if ( _battery == UAVBattery.Outputs.BATTERY_DEAD || _hag == UAVHeightAboveGround.Outputs.HAG_CRASHED) {
					nextState(States.UAV_CRASHED, 1);
				}else{
					//Handle Resume cmd
					if(input.contains(OperatorRole.Outputs.OP_RESUME)){
						nextState(States.UAV_FLYING,1);
					}
					//Handle Land cmd
					else if(input.contains(OperatorRole.Outputs.OP_LAND)){
						_dur = sim().duration(Durations.UAV_LANDING_DUR.range());
						nextState(States.UAV_LANDED,_dur);
					} else if ( input.contains(OperatorRole.Outputs.OP_FLYBY_START_F) ||
							input.contains(OperatorRole.Outputs.OP_FLYBY_START_T)) {
						_flyby = true;
						nextState(States.UAV_FLYING, 1);
					}else if ( input.contains(OperatorRole.Outputs.OP_FLYBY_END) ) {
						_flyby = false;
					} else if ( input.contains(OperatorRole.Outputs.OP_NEW_FLIGHT_PLAN)){
						nextState(States.UAV_FLYING,1);
					}
				}
				break;
			case UAV_LANDING:
				if ( _battery == UAVBattery.Outputs.BATTERY_DEAD ) {
					nextState(States.UAV_CRASHED, 1);
				}else{
					if(input.contains(OperatorRole.Outputs.OP_TAKE_OFF)){
						if(_flight_plan == UAVFlightPlan.Outputs.UAV_FLIGHT_PLAN_YES){
							nextState(States.UAV_FLYING,1);
						}else{
							nextState(States.UAV_LOITERING,1);
						}
					}
				}	
				
				//Handle receiving Flyby Cmds
				if ( input.contains(OperatorRole.Outputs.OP_FLYBY_START_F) ||
						input.contains(OperatorRole.Outputs.OP_FLYBY_START_T)) {
					_flyby = true;
				}
				
				if ( input.contains(OperatorRole.Outputs.OP_FLYBY_END) ) {
					_flyby = false;
				}
				break;
			case UAV_LANDED:
				if(input.contains(OperatorRole.Outputs.OP_POST_FLIGHT_COMPLETE)){
					nextState(States.UAV_READY,1);
				}
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
				if ( _flyby )
					_state = Outputs.UAV_FLYING_FLYBY;
				else
					_state = Outputs.UAV_FLYING_NORMAL;
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

	private void handleSignalObservations() {
		ArrayList<IData> observations = sim().getObservations(Actors.UAV_SIGNAL.name());
		if(observations.size() > 0){
			_signal = observations.get(0);
		}
		
	}
	private void handleFlightPlanObservations() {
		ArrayList<IData> observations = sim().getObservations(Actors.UAV_FLIGHT_PLAN.name());
		if ( observations.size() > 0 ) {
			_flight_plan = observations.get(0);
		}
	}
	
	private void handleHAGObservations(){
		ArrayList<IData> observations = sim().getObservations(Actors.UAV_HAG.name());
		if(observations.size() > 0){
			_hag = observations.get(0);
		}
	}
}
