package actors;

import java.util.HashMap;

import simulator.Actor;
import simulator.State;
import team.Duration;
import team.UDO;


public class UAVBattery extends Actor {

	private int _start_time = 0;
//	private int _max_battery_life = 0;
//	private int _battery_life = 0;
	private int _low_battery_threshold = 0;
	private boolean _initialized = false;
	
	
	public UAVBattery(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs) {
		State inactive = new State("INACTIVE");
		State active = new State("ACTIVE");
		State low = new State("LOW");
		State dead = new State("DEAD");
		
		this.addState(inactive);
		this.addState(active);
		this.addState(low);
		this.addState(dead);

		//create transitions
		inactive.addTransition(new UDO[]{inputs.get(UDO.OGUI_TAKE_OFF_UAV.name())},new UDO[]{outputs.get(UDO.UAV_BATTERY_OK_OGUI.name())},active,null,1);
		//inactive.addTransition(new UDO[]{inputs.get(UDO.OP_POST_FLIGHT_COMPLETE_UAV.name())},new UDO[]{UDO.UAV_BATTERY_TIME_LEFT_UAV.set(new Integer(100))},inactive,null,1);
		
		active.addTransition(new UDO[]{}, new UDO[]{outputs.get(UDO.UAV_BATTERY_LOW_OGUI.name())}, low, null, -1);
		
		low.addTransition(new UDO[]{}, new UDO[]{outputs.get(UDO.UAV_BATTERY_DEAD_OGUI.name())}, dead, null, -1);
		low.addTransition(new UDO[]{inputs.get(UDO.UAV_LANDED)}, new UDO[]{outputs.get(UDO.UAV_BATTERY_OFF_OGUI.name())}, inactive, null, 0);
	}
}
