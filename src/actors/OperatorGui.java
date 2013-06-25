package actors;

import java.util.HashMap;

import simulator.*;
import team.UDO;

public class OperatorGui extends Actor{

	public OperatorGui(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs) {
		//initialize name
		_name = "OPERATOR_GUI";
		//initialize states
		State IDLE = new State("IDLE");
		State NORMAL = new State("NORMAL");
		State ALARM = new State("ALARM");
		
		//initialize transitions
		initializeIDLE(inputs, outputs, IDLE, NORMAL);
		
		//add states
		addState(IDLE);
		addState(NORMAL);
		addState(ALARM);
		
		//initialize _currentState
		_currentState = IDLE;
	}
	
	private void initializeIDLE(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs, State IDLE, State NORMAL){
		IDLE.addTransition(
				new UDO[]{inputs.get(UDO.OP_TAKE_OFF_OGUI)},
				new UDO[]{outputs.get(UDO.OGUI_TAKE_OFF_UAV)},
				NORMAL, null, 0);
	}
	
}
