package actors;

import java.util.HashMap;

import simulator.*;
import team.Duration;
import team.UDO;

public class OperatorGui extends Actor{

	public OperatorGui(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs) {
		//initialize name
		_name = "OPERATOR_GUI";
		//initialize states
		State NORMAL = new State("NORMAL");
		State ALARM = new State("ALARM");
		State AUDIBLE_ALARM = new State("AUDIBLE_ALARM");
		
		//initialize transitions
		initializeIDLE(inputs, outputs, NORMAL);
		
		//add states
		addState(NORMAL);
		addState(ALARM);
		addState(AUDIBLE_ALARM);
		
		//initialize _currentState
		_currentState = NORMAL;
	}
	
	private void initializeIDLE(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs, State NORMAL){
		NORMAL.addTransition(
				new UDO[]{inputs.get(UDO.OP_TAKE_OFF_OGUI.name())},
				new UDO[]{outputs.get(UDO.OGUI_TAKE_OFF_UAV.name())},
				NORMAL, Duration.NEXT, 0);
	}
	
}
