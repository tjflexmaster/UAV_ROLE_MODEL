package model.actors;

import java.util.*;

import model.team.*;

import simulator.*;

public class UAVBattery extends Actor {

	
	public UAVBattery(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs) {
		//initialize name
		setName("UAV_BATTERY");
		
		//initialize states
		State INACTIVE = new State("INACTIVE",0);
		State ACTIVE = new State("ACTIVE",0);
		State LOW = new State("LOW",0);
		State DEAD = new State("DEAD",0);
		
//		INACTIVE.addTransition(
//				new UDO[]{inputs.get(UDO.OGUI_TAKE_OFF_UAV.name())},
//				null,
//				new UDO[]{outputs.get(UDO.UAV_BATTERY_OK_OGUI.name())},
//				new IDO[]{Memory.TIME_TILL_LOW.set(Duration.UAVBAT_DURATION.getdur())},
//				ACTIVE, Duration.NEXT, 0);
//		
//		ACTIVE.addTransition(
//				null,
//				null,
//				new UDO[]{outputs.get(UDO.UAV_BATTERY_LOW_OGUI.name())},
//				null,
//				LOW, Duration.UAVBAT_ACTIVE_TO_LOW, 0);

//		INACTIVE.addTransition(
//				new UDO[]{inputs.get(UDO.OGUI_TAKE_OFF_UAV.name())},
//				new UDO[]{outputs.get(UDO.UAV_BATTERY_OK_OGUI.name())},
//				ACTIVE, Duration.NEXT, 1);
//		INACTIVE.addTransition(
//				new UDO[]{inputs.get(UDO.OP_POST_FLIGHT_COMPLETE_UAV.name())},
//				new UDO[]{outputs.get(UDO.UAVBAT_TIME_TIL_LOW_UAVBAT.name()).update(new Integer(3600))},
//				INACTIVE, Duration.NEXT, 1);
//		ACTIVE.addTransition(new TimerTransition(
//				new UDO[]{inputs.get(UDO.UAVBAT_TIME_TIL_LOW_UAVBAT.name())},
//				new UDO[]{outputs.get(UDO.UAV_BATTERY_LOW_OGUI.name()),outputs.get(UDO.UAVBAT_TIME_TIL_DEAD.name()).update(new Integer(1800))},
//				LOW, Duration.NEXT, -1));
//		LOW.addTransition(new TimerTransition(
//				new UDO[]{inputs.get(UDO.UAVBAT_TIME_TIL_DEAD.name())},
//				new UDO[]{outputs.get(UDO.UAV_BATTERY_DEAD_OGUI.name())},
//				DEAD, Duration.NEXT, -1));
//		LOW.addTransition(new
//				UDO[]{inputs.get(UDO.UAV_LANDED)},
//				new UDO[]{outputs.get(UDO.UAV_BATTERY_OFF_OGUI.name())},
//				INACTIVE, Duration.NEXT, 0);
		
		//add states
		add(INACTIVE);
		add(ACTIVE);
		add(LOW);
		add(DEAD);
		
		//set current state
		startState(INACTIVE);
	}

	@Override
	protected void initializeInternalVariables() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public HashMap<IActor, ITransition> getTransitions() {
		// TODO Auto-generated method stub
		return null;
	}
}
