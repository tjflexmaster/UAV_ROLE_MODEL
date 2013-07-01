package model.actors;

import java.util.*;

import model.team.*;

import simulator.*;

public class UAVBattery extends Actor {

	private enum Memory implements IDO{
		TIME_TILL_LOW(3600),
		TIME_TILL_DEAD(500);
		
		Memory(Integer i){
			data = i;
		}
		
		private Object data;
		@Override
		public Object get() {
			// TODO Auto-generated method stub
			return data;
		}
		@Override
		public Memory set(Object object) {
			data = object;
			return this;
		}
		@Override
		public Memory update(Integer integer) {
			data = integer;
			return this;
		}
		
	}
	
	public UAVBattery(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs) {
		//initialize name
		_name = "UAV_BATTERY";
		
		//initialize states
		State INACTIVE = new State("INACTIVE");
		State ACTIVE = new State("ACTIVE");
		State LOW = new State("LOW");
		State DEAD = new State("DEAD");
		
		INACTIVE.addTransition(
				new UDO[]{inputs.get(UDO.OGUI_TAKE_OFF_UAV.name())},
				null,
				new UDO[]{outputs.get(UDO.UAV_BATTERY_OK_OGUI.name())},
				new IDO[]{Memory.TIME_TILL_LOW.set(Duration.UAVBAT_DURATION.getdur())},
				ACTIVE, Duration.NEXT, 0);
		
		ACTIVE.addTransition(
				null,
				null,
				new UDO[]{outputs.get(UDO.UAV_BATTERY_LOW_OGUI.name())},
				null,
				LOW, Duration.UAVBAT_ACTIVE_TO_LOW, 0);

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
		addState(INACTIVE);
		addState(ACTIVE);
		addState(LOW);
		addState(DEAD);
		
		//set current state
		_currentState = INACTIVE;
	}
}
