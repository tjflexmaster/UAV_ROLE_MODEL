package actors;

import java.util.HashMap;

import simulator.*;
import team.Duration;
import team.UDO;

public class Operator extends Actor {

	public Operator(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs) {
		//initialize name
		_name = "OPERATOR";
		
		//initialize states
		State IDLE = new State("IDLE");
		State POST_FLIGHT = new State("POST_FLIGHT");
		State POST_FLIGHT_COMPLETE = new State("POST_FLIGHT_COMPLETE");
		State LAUNCH_UAV = new State("LAUNCH_UAV");
		State OBSERVE_GUI = new State("OBSERVE_GUI");
		State OBSERVE_UAV = new State("OBSERVE_UAV");
		//comm with mission manager
		State POKE_MM = new State("POKE_MM");
		State TX_MM = new State("TX_MM");
		State END_MM = new State("END_MM");
		State RX_MM = new State("RX_MM");
		//comm with the video operator
		State RX_VO = new State("RX_VO");
		State OBSERVE_FLYBY = new State("OBSERVE_FLYBY");
		//comm with the operator gui
		State POKE_OGUI = new State("POKE_OGUI");
		State TX_OGUI = new State("TX_OGUI");
		State END_OGUI = new State("END_OGUI");

		//initialize transitions
		initializeIDLE(inputs, outputs, IDLE, RX_MM, LAUNCH_UAV, OBSERVE_GUI);
		initializeRX_MM(inputs, outputs, RX_MM, POKE_OGUI, IDLE);
		initializeOBSERVE_GUI(inputs, outputs, OBSERVE_GUI, POKE_OGUI, POST_FLIGHT, OBSERVE_UAV);
		initializeOBSERVE_UAV(inputs, outputs, OBSERVE_UAV, POST_FLIGHT, OBSERVE_GUI);
		initializeTX_OGUI(inputs, outputs, TX_OGUI, END_OGUI);
		initializePOST_FLIGHT(inputs, outputs, POST_FLIGHT, POST_FLIGHT_COMPLETE);
		initializePOST_FLIGHT_COMPLETE(inputs, outputs, POST_FLIGHT_COMPLETE, IDLE);
		
		//add states
		addState(IDLE);
		addState(POST_FLIGHT);
		addState(POST_FLIGHT_COMPLETE);
		addState(LAUNCH_UAV);
		addState(OBSERVE_GUI);
		addState(OBSERVE_UAV);
		addState(POKE_MM);
		addState(TX_MM);
		addState(END_MM);
		addState(RX_MM);
		addState(RX_VO);
		addState(OBSERVE_FLYBY);
		addState(POKE_OGUI);
		addState(TX_OGUI);
		addState(END_OGUI);
		
		//initialize current state
		_currentState = IDLE;
	}

	private void initializeIDLE(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs, State IDLE, State RX_MM, State LAUNCH_UAV, State OBSERVE_GUI) {
		IDLE.addTransition(
				new UDO[]{inputs.get(UDO.MM_POKE_OP.name())},
				new UDO[]{outputs.get(UDO.OP_ACK_MM.name())},
				RX_MM, Duration.ACK, 0);
		/*IDLE.addTransition(
				new UDO[]{inputs.get(UDO.OP_TAKE_OFF_OP)},
				new UDO[]{outputs.get(UDO.OP_TAKE_OFF_OGUI)},
				LAUNCH_UAV, null, 0);
		IDLE.addTransition(
				new UDO[]{inputs.get(UDO.UAV_FLYING_NORMAL)},
				null,
				OBSERVE_GUI, null, 0);
		IDLE.addTransition(
				new UDO[]{inputs.get(UDO.UAV_FLYING_FLYBY)},
				null,
				OBSERVE_GUI, null, 0);*/
	}
	
	private void initializeRX_MM(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs, State RX_MM, State POKE_OGUI, State IDLE){
		RX_MM.addTransition(//wait for input
				null,
				null,
				IDLE, Duration.OP_RX_MM, 0);
		RX_MM.addTransition(//return to IDLE if input is null
				new UDO[]{inputs.get(UDO.MM_END_OP.name())},
				null,
				IDLE, Duration.NEXT,1);
		RX_MM.addTransition(//process new search area of interest
				new UDO[]{inputs.get(UDO.MM_END_OP.name()), inputs.get(UDO.MM_NEW_SEARCH_AOI_OP.name())},
				new UDO[]{outputs.get(UDO.OP_NEW_SEARCH_AOI_OP.name())},
				POKE_OGUI, Duration.NEXT, 2);
	}

	private void initializeTX_OGUI(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs, State TX_OGUI, State END_OGUI) {
		TX_OGUI.addTransition(//transmit take off orders via operator gui
				new UDO[]{UDO.OP_NEW_SEARCH_AOI_OP},
				new UDO[]{outputs.get(UDO.OP_TAKE_OFF_OGUI.name())},
				END_OGUI, Duration.OP_TX_OGUI, 0);
	}

	private void initializeOBSERVE_GUI(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs, State OBSERVE_GUI, State POKE_OGUI, State POST_FLIGHT, State OBSERVE_UAV) {
		/*OBSERVE_GUI.addTransition(
				new UDO[]{inputs.get(UDO.OGUI_FLYBY_REQ_F_OP)},
				new UDO[]{outputs.get(UDO.OP_POKE_OGUI)},
				POKE_OGUI, null, 0);
		OBSERVE_GUI.addTransition(
				new UDO[]{inputs.get(UDO.OGUI_FLYBY_REQ_T_OP)},
				new UDO[]{outputs.get(UDO.OP_POKE_OGUI)},
				POKE_OGUI, null, 0);
		OBSERVE_GUI.addTransition(
				new UDO[]{inputs.get(UDO.OGUI_lANDED_OP)},
				null,
				POST_FLIGHT, null, 0);
		OBSERVE_GUI.addTransition(
				null,
				null,
				OBSERVE_UAV, null, -1);*/
	}

	private void initializeOBSERVE_UAV(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs, State OBSERVE_UAV, State POST_FLIGHT, State OBSERVE_GUI) {
		/*OBSERVE_UAV.addTransition(
				new UDO[]{inputs.get(UDO.UAV_LANDED)},
				null,
				POST_FLIGHT, null, 0);
		OBSERVE_UAV.addTransition(
				null,
				null,
				OBSERVE_GUI, null, -1);*/
	}
	
	private void initializePOST_FLIGHT(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs, State POST_FLIGHT, State POST_FLIGHT_COMPLETE){
		/*POST_FLIGHT.addTransition(
				null,
				new UDO[]{outputs.get(UDO.OP_POST_FLIGHT_COMPLETE_UAV)},
				POST_FLIGHT_COMPLETE, null, 0);*/
	}
	
	private void initializePOST_FLIGHT_COMPLETE(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs, State POST_FLIGHT_COMPLETE, State IDLE){
		/*POST_FLIGHT_COMPLETE.addTransition(
				null,
				null,
				IDLE, null, 0);*/
	}
	
}
