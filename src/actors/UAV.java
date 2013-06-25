package actors;

import java.util.HashMap;

import simulator.*;
import team.UDO;

public class UAV extends Actor {

	public UAV(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs) {
		//add states
		State IDLE = new State("IDLE"); 
		addState(IDLE);
		State TAKE_OFF = new State("TAKE_OFF"); 
		addState(TAKE_OFF);
		State FLY_LOITER = new State("FLY_LOITER"); 
		addState(FLY_LOITER);
		
		//add transitions
		IDLE.addTransition(
				new UDO[]{},
				new UDO[]{},
				TAKE_OFF, null, 0);
		TAKE_OFF.addTransition(
				new UDO[]{},
				new UDO[]{},
				FLY_LOITER, null, 0);
		
		/* UAV Battery */
		inputs.clear();
		outputs.clear();
		inputs.put(UDO.UAVBAT_TIME_TIL_DEAD.name(), UDO.UAVBAT_TIME_TIL_DEAD);
		inputs.put(UDO.UAVBAT_TIME_TIL_LOW_UAVBAT.name(), UDO.UAVBAT_TIME_TIL_LOW_UAVBAT);
		inputs.put(UDO.OGUI_TAKE_OFF_UAV.name(), UDO.OGUI_TAKE_OFF_UAV);
		inputs.put(UDO.OP_POST_FLIGHT_COMPLETE_UAV.name(), UDO.OP_POST_FLIGHT_COMPLETE_UAV);
		inputs.put(UDO.UAV_LANDED.name(), UDO.UAV_LANDED);
		outputs.put(UDO.UAVBAT_TIME_TIL_DEAD.name(), UDO.UAVBAT_TIME_TIL_DEAD);
		outputs.put(UDO.UAVBAT_TIME_TIL_LOW_UAVBAT.name(), UDO.UAVBAT_TIME_TIL_LOW_UAVBAT);
		outputs.put(UDO.UAV_BATTERY_OK_OGUI.name(), UDO.UAV_BATTERY_OK_OGUI);
		outputs.put(UDO.UAV_BATTERY_DEAD_OGUI.name(), UDO.UAV_BATTERY_DEAD_OGUI);
		outputs.put(UDO.UAV_BATTERY_OFF_OGUI.name(), UDO.UAV_BATTERY_OFF_OGUI);
	}

	@Override
	public boolean updateTransition() {
		// TODO Auto-generated method stub
		return false;
	}

}
