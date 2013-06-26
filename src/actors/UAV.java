package actors;

import java.util.HashMap;

import simulator.*;
import team.Duration;
import team.UDO;

public class UAV extends Actor {

	public UAV(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs) {
		//initialize name
		_name = "UAV";
		
		//initialize states
		State IDLE = new State("IDLE");
		State TAKE_OFF = new State("TAKE_OFF"); 
		State FLY_LOITER = new State("FLY_LOITER"); 
		
		//add transitions
		initializeIDLE(inputs, IDLE, TAKE_OFF);
		initializeTAKE_OFF(TAKE_OFF, FLY_LOITER);
		
		/* UAV Battery */
		inputs.clear();
		outputs.clear();
		inputs.put(UDO.UAVBAT_TIME_TIL_DEAD.name(), UDO.UAVBAT_TIME_TIL_DEAD);
		inputs.put(UDO.UAVBAT_TIME_TIL_LOW_UAVBAT.name(), UDO.UAVBAT_TIME_TIL_LOW_UAVBAT);
		inputs.put(UDO.OGUI_TAKE_OFF_UAV.name(), UDO.OGUI_TAKE_OFF_UAV);
		//inputs.put(UDO.OP_POST_FLIGHT_COMPLETE_UAV.name(), UDO.OP_POST_FLIGHT_COMPLETE_UAV);
		//inputs.put(UDO.UAV_LANDED.name(), UDO.UAV_LANDED);
		outputs.put(UDO.UAVBAT_TIME_TIL_DEAD.name(), UDO.UAVBAT_TIME_TIL_DEAD);
		outputs.put(UDO.UAVBAT_TIME_TIL_LOW_UAVBAT.name(), UDO.UAVBAT_TIME_TIL_LOW_UAVBAT);
		//outputs.put(UDO.UAV_BATTERY_OK_OGUI.name(), UDO.UAV_BATTERY_OK_OGUI);
		//outputs.put(UDO.UAV_BATTERY_DEAD_OGUI.name(), UDO.UAV_BATTERY_DEAD_OGUI);
		//outputs.put(UDO.UAV_BATTERY_OFF_OGUI.name(), UDO.UAV_BATTERY_OFF_OGUI);
		
		//add states 
		addState(IDLE);
		addState(TAKE_OFF);
		addState(FLY_LOITER);
		
		//initialize current state
		_currentState = IDLE;
	}

	private void initializeTAKE_OFF(State TAKE_OFF, State FLY_LOITER) {
		TAKE_OFF.addTransition(
				null,
				null,
				FLY_LOITER, Duration.NEXT, 0);
	}

	private void initializeIDLE(HashMap<String, UDO> inputs, State IDLE,
			State TAKE_OFF) {
		IDLE.addTransition(
				new UDO[]{inputs.get(UDO.OGUI_TAKE_OFF_UAV.name())},
				null,
				TAKE_OFF, Duration.NEXT, 0);
	}


}
