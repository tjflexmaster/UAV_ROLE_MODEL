package actors;

import java.util.HashMap;

import simulator.*;
import team.UDO;

public class Operator extends Actor {

	public Operator(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs) {
		//add states
		State IDLE = new State("IDLE");
		addState(IDLE);
		State POST_FLIGHT = new State("POST_FLIGHT");
		addState(POST_FLIGHT);
		State POST_FLIGHT_COMPLETE = new State("POST_FLIGHT_COMPLETE");
		addState(POST_FLIGHT_COMPLETE);
		State LAUNCH_UAV = new State("LAUNCH_UAV");
		addState(LAUNCH_UAV);
		State OBSERVING_GUI = new State("OBSERVING_GUI");
		addState(OBSERVING_GUI);
		State OBSERVING_UAV = new State("OBSERVING_UAV");
		addState(OBSERVING_UAV);
		State POKE_MM = new State("POKE_MM");
		addState(POKE_MM);
		State TX_MM = new State("TX_MM");
		addState(TX_MM);
		State END_MM = new State("END_MM");
		addState(END_MM);
		State RX_MM = new State("RX_MM");
		addState(RX_MM);
		State RX_VO = new State("RX_VO");
		addState(RX_VO);
		State OBSERVING_FLYBY = new State("OBSERVING_FLYBY");
		addState(OBSERVING_FLYBY);
		State POKE_OGUI = new State("POKE_GUI");
		addState(POKE_OGUI);
		State TX_OGUI = new State("TX_GUI");
		addState(TX_OGUI);
		State END_OGUI = new State("END_GUI");
		addState(END_OGUI);

		//add transitions
		initializeIDLE(inputs, outputs, IDLE, RX_MM, LAUNCH_UAV, OBSERVING_GUI);
		initializeRX_MM(inputs, outputs, RX_MM, TX_OGUI);
		initializeOBSERVING_GUI(inputs, outputs, OBSERVING_GUI, POKE_OGUI, POST_FLIGHT, OBSERVING_UAV);
		initializeOBSERVING_UAV(inputs, outputs, OBSERVING_UAV, POST_FLIGHT, OBSERVING_GUI);
		initializeTX_OGUI(inputs, outputs, TX_OGUI, IDLE);
		initializePOST_FLIGHT(inputs, outputs, POST_FLIGHT, POST_FLIGHT_COMPLETE);
		initializePOST_FLIGHT_COMPLETE(inputs, outputs, POST_FLIGHT_COMPLETE, IDLE);
	}

	private void initializeTX_OGUI(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs, State TX_OGUI, State IDLE) {
		TX_OGUI.addTransition(
				new UDO[]{inputs.get(UDO.OP_NEW_SEARCH_AOI_OP)},
				new UDO[]{outputs.get(UDO.OP_TAKE_OFF_OGUI)},
				IDLE, null, 0);
	}

	private void initializeIDLE(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs, State IDLE, State RX_MM, State LAUNCH_UAV, State OBSERVING_GUI) {
		IDLE.addTransition(
				new UDO[]{inputs.get(UDO.MM_POKE_OP)},
				new UDO[]{outputs.get(UDO.OP_ACK_MM)},
				RX_MM, null, 0);
		IDLE.addTransition(
				new UDO[]{inputs.get(UDO.MM_POKE_OP.name())},
				new UDO[]{outputs.get(UDO.OP_ACK_MM.name())},
				RX_MM, null, 0);
		IDLE.addTransition(
				new UDO[]{UDO.OP_TAKE_OFF_OP},
				new UDO[]{outputs.get(UDO.OP_TAKE_OFF_OGUI.name())},
				LAUNCH_UAV, null, 0);
		IDLE.addTransition(
				new UDO[]{inputs.get(UDO.UAV_FLYING_NORMAL.name())},
				null,
				OBSERVING_GUI, null, 0);
		IDLE.addTransition(
				new UDO[]{inputs.get(UDO.UAV_FLYING_FLYBY.name())},
				null,
				OBSERVING_GUI, null, 0);
	}
	
	private void initializeRX_MM(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs, State RX_MM, State TX_OGUI){
		RX_MM.addTransition(
				null,
				null,
				RX_MM, null, 0);
		RX_MM.addTransition(
				new UDO[]{inputs.get(UDO.MM_END_OP), inputs.get(UDO.MM_NEW_SEARCH_AOI_OP)},
				new UDO[]{outputs.get(UDO.OP_NEW_SEARCH_AOI_OP)},
				TX_OGUI, null, 0);
	}

	private void initializeOBSERVING_GUI(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs, State OBSERVING_GUI, State POKE_OGUI, State POST_FLIGHT, State OBSERVING_UAV) {
		OBSERVING_GUI.addTransition(
				new UDO[]{inputs.get(UDO.OGUI_FLYBY_REQ_F_OP.name())},
				new UDO[]{outputs.get(UDO.OP_POKE_OGUI.name())},
				POKE_OGUI, null, 0);
		OBSERVING_GUI.addTransition(
				new UDO[]{inputs.get(UDO.OGUI_FLYBY_REQ_T_OP.name())},
				new UDO[]{outputs.get(UDO.OP_POKE_OGUI.name())},
				POKE_OGUI, null, 0);
		OBSERVING_GUI.addTransition(
				new UDO[]{inputs.get(UDO.OGUI_lANDED_OP.name())},
				null,
				POST_FLIGHT, null, 0);
		OBSERVING_GUI.addTransition(
				null,
				null,
				OBSERVING_UAV, null, -1);
	}

	private void initializeOBSERVING_UAV(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs, State OBSERVING_UAV, State POST_FLIGHT, State OBSERVING_GUI) {
		OBSERVING_UAV.addTransition(
				new UDO[]{inputs.get(UDO.UAV_LANDED.name())},
				null,
				POST_FLIGHT, null, 0);
		OBSERVING_UAV.addTransition(
				null,
				null,
				OBSERVING_GUI, null, -1);
	}
	
	private void initializePOST_FLIGHT(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs, State POST_FLIGHT, State POST_FLIGHT_COMPLETE){
		POST_FLIGHT.addTransition(
				null,
				new UDO[]{outputs.get(UDO.OP_POST_FLIGHT_COMPLETE_UAV.name())},
				POST_FLIGHT_COMPLETE, null, 0);
	}
	
	private void initializePOST_FLIGHT_COMPLETE(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs, State POST_FLIGHT_COMPLETE, State IDLE){
		POST_FLIGHT_COMPLETE.addTransition(
				null,
				null,
				IDLE, null, 0);
	}
	
}
