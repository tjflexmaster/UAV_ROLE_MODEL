package model.actors;

import java.util.HashMap;

import model.team.Duration;
import model.team.UDO;

import simulator.*;

public class OperatorGui extends Actor{

	public enum OGUI_VGUI_DATA {

	}

	public enum OGUI_UAV_DATA {

	}

	public enum OGUI_OP_DATA {

	}

	public OperatorGui(ComChannelList inputs, ComChannelList outputs) {
		//initialize name
		_name = "OPERATOR_GUI";
		//initialize states
		State NORMAL = new State("NORMAL");
		State ALARM = new State("ALARM");
		State AUDIBLE_ALARM = new State("AUDIBLE_ALARM");
		
		//initialize transitions
		initializeIDLE(inputs, outputs, NORMAL);
		
		//add states
		add(NORMAL);
		add(ALARM);
		add(AUDIBLE_ALARM);
		
		//initialize _currentState
		startState(NORMAL);
	}
	
	private void initializeIDLE(ComChannelList inputs, ComChannelList outputs, State NORMAL){
//		NORMAL.addTransition(
//				new UDO[]{inputs.get(UDO.OP_TAKE_OFF_OGUI.name())},
//				null,
//				new UDO[]{outputs.get(UDO.OGUI_TAKE_OFF_UAV.name())},
//				null,
//				NORMAL, Duration.NEXT, 0);
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
