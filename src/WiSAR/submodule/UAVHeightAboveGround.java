package WiSAR.submodule;

import java.util.ArrayList;

import WiSAR.Durations;
import WiSAR.Agents.OperatorGUIRole;
import WiSAR.submodule.UAVBattery.Outputs;
import WiSAR.submodule.UAVFlightPlan.States;
import CUAS.Simulator.Actor;
import CUAS.Simulator.IData;
import CUAS.Simulator.IStateEnum;
import CUAS.Simulator.Simulator;

public class UAVHeightAboveGround extends Actor {

	public enum Outputs implements IData {
		HAG_DANGEROUS,
		HAG_NONE,
		HAG_GOOD
	}
	
	public enum States implements IStateEnum{
		INACTIVE,
		ACTIVE
	}
	
	@Override
	public void processNextState() {

        if ( nextStateTime() != Simulator.getInstance().getTime() ) {
        	//Set Observations
            setObservations();
            return;
        }
	}

	@Override
	public void processInputs() {
        
		ArrayList<IData> input = sim().getInput(this.name());
		switch((States)state()){
		case COMPLETE:
		case NO_PATH:
			if(input.contains(OperatorGUIRole.Outputs.OGUI_PATH_NEW)){
				_start_time = sim().getTime();
				_path_dur = sim().duration(Durations.UAV_FLIGHT_PLAN_DUR.range());
				nextState(States.YES_PATH,1);
			}
			break;
		default:
			break;
		}
		setObservations();
		input.clear();
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
	
}
