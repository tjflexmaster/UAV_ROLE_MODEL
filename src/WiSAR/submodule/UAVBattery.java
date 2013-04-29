package WiSAR.submodule;

import CUAS.Simulator.Actor;
import CUAS.Simulator.IData;
import CUAS.Simulator.IStateEnum;
import CUAS.Simulator.Simulator;
import WiSAR.Durations;
import WiSAR.Agents.Roles;
import WiSAR.Agents.UAVRole;


public class UAVBattery extends Actor {

	private int start_time = 0;
	private int battery_life = 0;
	private int low_battery_life = 0;
	
	public enum Outputs implements IData {
		BATTERY_DEAD,
		BATTERY_LOW,
		BATTERY_OFF,
		BATTERY_OK
		
	}
	public enum States implements IStateEnum{
		OFF,
		OK,
		LOW,
		DEAD
	}
	@Override
	public void processNextState() {

        if ( nextStateTime() != Simulator.getInstance().getTime() ) {
            return;
        }
        state(nextState());
        int duration = 1;
        //If a state isn't included then it doesn't deviate from the default
        switch((States)nextState()) {
        case OFF:
        	_output.add(Outputs.BATTERY_OFF);
        case OK:
        	start_time = sim().getTime();
        	battery_life = sim().duration(Durations.UAV_BATTERY_DUR.range());
        	low_battery_life = sim().duration(Durations.UAV_LOW_BATTERY_THRESHOLD_DUR.range());
        	duration = battery_life - low_battery_life;
        	_output.add(Outputs.BATTERY_OK);
        	nextState(States.LOW, duration);
        	break;
        case LOW:
        	duration = low_battery_life;
        	_output.add(Outputs.BATTERY_LOW);
        	nextState(States.DEAD,duration);
        	break;
        case DEAD:
        	_output.add(Outputs.BATTERY_DEAD);
    	default:
    		nextState(null,0);
    		break;
        }
	}

	@Override
	public void processInputs() {
		switch((States)state()){
		case OFF:
			
			if(_input.contains(UAVRole.Outputs.ACTIVATE_BATTERY)){
				nextState(States.OK,1);
			}
			break;
		case OK:
		case LOW:
			if(_input.contains(UAVRole.Outputs.DEACTIVATE_BATTERY)){
				nextState(States.OFF,1);
			}
		default:
		}
	}
	
	public int getRemainingTime(){
		return (start_time + battery_life) - sim().getTime();
	}

}
