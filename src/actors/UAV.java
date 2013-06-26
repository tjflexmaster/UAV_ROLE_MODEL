package actors;

import java.util.ArrayList;
import java.util.HashMap;

import simulator.*;
import team.Duration;
import team.UDO;

public class UAV extends Actor {
	
	private ArrayList<Actor> _subactors;

	public UAV(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs) {
		//initialize name
		_name = "UAV";
		_subactors = new ArrayList<Actor>();
		
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
		inputs.put(UDO.OP_POST_FLIGHT_COMPLETE_UAV.name(), UDO.OP_POST_FLIGHT_COMPLETE_UAV);
		inputs.put(UDO.UAV_LANDED.name(), UDO.UAV_LANDED);
		outputs.put(UDO.UAVBAT_TIME_TIL_DEAD.name(), UDO.UAVBAT_TIME_TIL_DEAD);
		outputs.put(UDO.UAVBAT_TIME_TIL_LOW_UAVBAT.name(), UDO.UAVBAT_TIME_TIL_LOW_UAVBAT);
		outputs.put(UDO.UAV_BATTERY_OK_OGUI.name(), UDO.UAV_BATTERY_OK_OGUI);
		outputs.put(UDO.UAV_BATTERY_DEAD_OGUI.name(), UDO.UAV_BATTERY_DEAD_OGUI);
		outputs.put(UDO.UAV_BATTERY_OFF_OGUI.name(), UDO.UAV_BATTERY_OFF_OGUI);
		_subactors.add(new UAVBattery(inputs,outputs));
		
		
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

	public boolean updateTransition() {
		boolean updated = false;
		updated = super.updateTransition()||updated;
		for(Actor actor : _subactors){
			updated = actor.updateTransition()||updated;
		}
		return updated;
		
	}
	
	public boolean processTransition(){
		boolean processed = false;
		processed = processed||super.processTransition()||processed;
		for(Actor actor : _subactors){
			processed = actor.processTransition()||processed;
		}
		return processed;
	}
	
	@Override
	public int get_nextTime(){
		int time = super.get_nextTime();
		for(Actor actor : _subactors){
			if(actor.get_nextTime() != -1)
				time = Math.min(time, actor.get_nextTime());
		}
		return time;
	}
	
	@Override
	public void set_nextTime(int nextTime){
		int time = get_nextTime()-nextTime;
		for(Actor actor : _subactors){
			if(actor.get_nextTime() != -1)
				actor.set_nextTime(actor.get_nextTime()-time);
		}
		super.set_nextTime(super.get_nextTime()-time);
	}
	

//	/**
//	 * this method works like a normal toSTring method
//	 * @return return the string representation of the actor
//	 */
//	@Override
//	public String toString() {
//		String result = "";
//		
//		result += _name + "(" + get_nextTime() + "): " + _currentState.toString() + " X ";
//		if (_currentTransition != null) {
//			result += _currentTransition.toString();
//		}
//		
//		for(Actor actor : _subactors){
//			result += "\n" + actor.toString();
//		}
//		
//		return result;
//	}

}
