package actors;

import java.util.HashMap;

import simulator.*;
import team.UDO;

public class OperatorGui extends Actor{

	public OperatorGui(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs) {
		//add states
		State IDLE = new State("IDLE");
		addState(IDLE);
		State NORMAL = new State("NORMAL");
		addState(NORMAL);
		State ALARM = new State("ALARM");
		addState(ALARM);
		
		//add transitions
		initializeIDLE(inputs, outputs, IDLE, NORMAL);
	}
	
	private void initializeIDLE(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs, State IDLE, State NORMAL){
		IDLE.addTransition(
				new UDO[]{inputs.get(UDO.OP_TAKE_OFF_OGUI)},
				new UDO[]{outputs.get(UDO.OGUI_TAKE_OFF_UAV)},
				NORMAL, null, 0);
	}
	
}
