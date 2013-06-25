package actors;

import java.util.HashMap;

import simulator.*;
import team.UDO;

public class OperatorGui extends Actor{

	public OperatorGui(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs) {
		//add states
		State IDLE = new State("IDLE");_states.add(IDLE);
		State NORMAL = new State("NORMAL");_states.add(NORMAL);
		State ALARM = new State("ALARM");_states.add(ALARM);
		
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
