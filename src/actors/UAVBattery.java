package actors;

import java.util.*;

import simulator.*;
import team.*;

public class UAVBattery extends Actor {
	
	public UAVBattery(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs) {
		State INACTIVE = new State("INACTIVE");
		State ACTIVE = new State("ACTIVE");
		State LOW = new State("LOW");
		State DEAD = new State("DEAD");
		
		addState(INACTIVE);
		addState(ACTIVE);
		addState(LOW);
		addState(DEAD);

		//addTransition(
		//		inputs[],
		//		outputs[],
		//		nextState, duration, priority);
		INACTIVE.addTransition(
				new UDO[]{inputs.get(UDO.OGUI_TAKE_OFF_UAV.name())},
				new UDO[]{outputs.get(UDO.UAV_BATTERY_OK_OGUI.name())},
				ACTIVE, null, 1);
		INACTIVE.addTransition(
				new UDO[]{inputs.get(UDO.OP_POST_FLIGHT_COMPLETE_UAV.name())},
				new UDO[]{outputs.get(UDO.UAVBAT_TIME_TIL_LOW_UAVBAT.name()).update(new Integer(3600))},
				INACTIVE, null, 1);
		ACTIVE.addTransition(
				new UDO[]{inputs.get(UDO.UAVBAT_TIME_TIL_LOW_UAVBAT.name())},
				new UDO[]{outputs.get(UDO.UAV_BATTERY_LOW_OGUI.name()),outputs.get(UDO.UAVBAT_TIME_TIL_DEAD.name()).update(new Integer(1800))},
				LOW, null, -1);
		LOW.addTransition(
				new UDO[]{inputs.get(UDO.UAVBAT_TIME_TIL_DEAD.name())},
				new UDO[]{outputs.get(UDO.UAV_BATTERY_DEAD_OGUI.name())},
				DEAD, null, -1);
		LOW.addTransition(new
				UDO[]{inputs.get(UDO.UAV_LANDED)},
				new UDO[]{outputs.get(UDO.UAV_BATTERY_OFF_OGUI.name())},
				INACTIVE, null, 0);
	}
}
