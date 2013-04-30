package WiSAR.submodule;

import CUAS.Simulator.Actor;
import CUAS.Simulator.IData;
import CUAS.Simulator.IStateEnum;
import CUAS.Simulator.Simulator;
import WiSAR.Actors;
import WiSAR.Durations;
import WiSAR.Agents.UAVRole;


public class UAVBattery extends Actor {

	private int _start_time = 0;
	private int _battery_life = 0;
	private int _low_battery_threshold = 0;
	private int _pause_time = 0;
	private boolean _initialized = false;
	
	public enum Outputs implements IData {
		BATTERY_DEAD,
		BATTERY_LOW,
		BATTERY_OFF,
		BATTERY_OK
		
	}
	public enum States implements IStateEnum{
		INACTIVE,
		ACTIVE,
		LOW,
		DEAD
	}
	
	public UAVBattery()
	{
		name(Actors.UAV_BATTERY.name());
		nextState(States.INACTIVE, 1);
	}
	
	@Override
	public void processNextState() {

        if ( nextStateTime() != Simulator.getInstance().getTime() ) {
        	//Set Observations
            setObservations();
            return;
        }
        
        if ( !_initialized ) {
        	//Initialize the Battery
        	resetBattery();
        	_initialized = true;
        }
        
        state(nextState());
        
        //If a state isn't included then it doesn't deviate from the default
        switch((States)nextState()) {
	        case INACTIVE:
	        	_pause_time = sim().getTime();
	        	pauseBattery();
	        	nextState(null, 0); //Battery wont change while it is not in use
	        	break;
	        case ACTIVE:
	        	//Next State is Low Battery
	        	_start_time = sim().getTime();
	        	nextState(States.LOW, getLowBatteryTime());
	        	break;
	        case LOW:
	        	nextState(States.DEAD, getRemainingTime() );
	        	break;
	        case DEAD:
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
		 * The battery can be activated and deactivated,
		 * It can also be reset back to fresh.
		 */
		if ( _input.contains(UAVRole.Outputs.RESET_BATTERY) ) {
			resetBattery();
		}
		
		switch( (States)state() ){
			case INACTIVE:
				if(_input.contains(UAVRole.Outputs.ACTIVATE_BATTERY)){
					nextState(States.ACTIVE, 1);
				}
				break;
			case ACTIVE:
			case LOW:
				if(_input.contains(UAVRole.Outputs.DEACTIVATE_BATTERY)){
					nextState(States.INACTIVE, 1);
				}
				break;
		}
		_input.clear();
	}
	
	/**
	 * HELPER METHODS
	 */
	
	private void pauseBattery()
	{
		//Change the battery duration by the amount that the battery used
		_battery_life -= (_pause_time - _start_time);
	}
	
	private int getRemainingTime()
	{
		return Math.max(0, ((_start_time + _battery_life) - sim().getTime()) );
	}
	
	private int getLowBatteryTime()
	{
		return Math.max(0, _start_time + _battery_life - _low_battery_threshold - sim().getTime() );
	}
	
	private void setObservations()
	{
		IData _state = Outputs.BATTERY_OK;
		switch((States) state()) {
			case INACTIVE:
				_state = Outputs.BATTERY_OFF;
				break;
			case LOW:
				_state = Outputs.BATTERY_LOW;
				break;
			case ACTIVE:
				_state = Outputs.BATTERY_OK;
				break;
			case DEAD:
				_state = Outputs.BATTERY_DEAD;
				break;
		}
		sim().addObservation(_state, this.name());
	}
	
	private void resetBattery()
	{
		_battery_life = sim().duration(Durations.UAV_BATTERY_DUR.range());
		_low_battery_threshold = sim().duration(Durations.UAV_LOW_BATTERY_THRESHOLD_DUR.range());
	}
}
