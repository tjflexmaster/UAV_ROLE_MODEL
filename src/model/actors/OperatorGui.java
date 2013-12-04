package model.actors;

import java.util.HashMap;

import simulator.*;

public class OperatorGui extends Actor{

	public enum OGUI_VGUI_DATA {

	}

	public enum OGUI_UAV_DATA {

	}

	public enum VIDEO_OGUI_OP_COMM {
		OGUI_LANDED_OP,
		OGUI_FLYBY_REQ_F_OP,
		OGUI_FLYBY_REQ_T_OP,
		OGUI_FLYBY_END_FAILED_VGUI_OP,
		OGUI_FLYBY_END_FAILED_OP,
		OGUI_FLYBY_END_SUCCESS_OP,
		OGUI_BATTERY_LOW_OP,
		OGUI_FLYING_NORMAL_OP,
		OGUI_FLYING_FLYBY_OP,
		OGUI_ON_GROUND_OP,
	}

	public enum DATA_OGUI_UAV_COMM {
		OGUI_TAKE_OFF_UAV
		
	}

	public OperatorGui(ComChannelList inputs, ComChannelList outputs) {
		//initialize name
		setName("OPERATOR_GUI");
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
		//(NORMAL,[OP_TAKE_OFF_OGUI],[])x(NORMAL,[OGUI_TAKE_OFF_UAV],[])
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
