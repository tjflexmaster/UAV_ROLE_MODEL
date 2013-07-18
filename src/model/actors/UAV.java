package model.actors;

import java.util.HashMap;
import model.team.UDO;

import simulator.*;

public class UAV extends Actor {

	public enum DATA_UAV_VGUI {

	}

	public enum DATA_UAV_OGUI {

	}

	public enum DATA_UAV_OP {

	}

	public UAV(ComChannelList inputs, ComChannelList outputs) {
		//initialize name
		_name = "UAV";
		
		//initialize states
		State IDLE = new State("IDLE");
		State TAKE_OFF = new State("TAKE_OFF"); 
		State FLY_LOITER = new State("FLY_LOITER"); 
		
		//add transitions
//		initializeIDLE(inputs, IDLE, TAKE_OFF);
		initializeTAKE_OFF(TAKE_OFF, FLY_LOITER);
		
//		//add states 
//		addState(IDLE);
//		addState(TAKE_OFF);
//		addState(FLY_LOITER);
//		
//		//initialize current state
//		_currentState = IDLE;
//		
//		//initialize subactors
//		_subactors = new ArrayList<Actor>();
		
		/* UAV Battery */
		inputs.clear();
		outputs.clear();
//		inputs.put(UDO.UAVBAT_TIME_TIL_DEAD.name(), UDO.UAVBAT_TIME_TIL_DEAD);
//		inputs.put(UDO.UAVBAT_TIME_TIL_LOW_UAVBAT.name(), UDO.UAVBAT_TIME_TIL_LOW_UAVBAT);
//		inputs.put(UDO.OGUI_TAKE_OFF_UAV.name(), UDO.OGUI_TAKE_OFF_UAV);
//		inputs.put(UDO.OP_POST_FLIGHT_COMPLETE_UAV.name(), UDO.OP_POST_FLIGHT_COMPLETE_UAV);
//		inputs.put(UDO.UAV_LANDED.name(), UDO.UAV_LANDED);
//		outputs.put(UDO.UAVBAT_TIME_TIL_DEAD.name(), UDO.UAVBAT_TIME_TIL_DEAD);
//		outputs.put(UDO.UAVBAT_TIME_TIL_LOW_UAVBAT.name(), UDO.UAVBAT_TIME_TIL_LOW_UAVBAT);
//		outputs.put(UDO.UAV_BATTERY_OK_OGUI.name(), UDO.UAV_BATTERY_OK_OGUI);
//		outputs.put(UDO.UAV_BATTERY_LOW_OGUI.name(), UDO.UAV_BATTERY_LOW_OGUI);
//		outputs.put(UDO.UAV_BATTERY_DEAD_OGUI.name(), UDO.UAV_BATTERY_DEAD_OGUI);
//		outputs.put(UDO.UAV_BATTERY_OFF_OGUI.name(), UDO.UAV_BATTERY_OFF_OGUI);
//		_subactors.add(new UAVBattery(inputs,outputs));
	}

	private void initializeTAKE_OFF(State TAKE_OFF, State FLY_LOITER) {
//		TAKE_OFF.addTransition(
//				null,
//				null,
//				null,
//				null,
//				FLY_LOITER, Duration.NEXT, 0);
	}

	private void initializeIDLE(HashMap<String, UDO> inputs, State IDLE,
			State TAKE_OFF) {
//		IDLE.addTransition(
//				new UDO[]{inputs.get(UDO.OGUI_TAKE_OFF_UAV.name())},
//				null,
//				null,
//				null,
//				TAKE_OFF, Duration.NEXT, 0);
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
	
//	@Override
//	public int get_nextTime(){
//		int time = super.get_nextTime();
//		for(Actor actor : _subactors){
//			if(time == -1)
//				time = actor.get_nextTime();
//			else if(actor.get_nextTime() != -1)
//				time = Math.min(time, actor.get_nextTime());
//		}
//		return time;
//	}
//	
//	@Override
//	public void set_nextTime(int nextTime){
//		int time = get_nextTime()-nextTime;
//		for(Actor actor : _subactors){
//			if(actor.get_nextTime() != -1){
//				actor.set_nextTime(actor.get_nextTime()-time);
//			}
//		}
//		if(super.get_nextTime() != -1){
//			super.set_nextTime(super.get_nextTime()-time);
//		}
//	}
	
}
