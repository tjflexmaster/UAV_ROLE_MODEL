package model.actors;

import java.util.HashMap;

import model.team.*;
import simulator.*;

public class VideoOperator extends Actor {

	public VideoOperator(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs) {
		//initialize name
		_name = "VIDEO_OPERATOR";

		//initialize states
		State IDLE = new State("IDLE");
		//comm with mission manager
		State RX_MM = new State("RX_MM");
		State POKE_MM = new State("POKE_MM");
		State TX_MM = new State("TX_MM");
		State END_MM = new State("END_MM");
		//comm with operator
		State POKE_OP = new State("POKE_OP");
		State TX_OP = new State("TX_OP");
		State END_OP = new State("END_OP");
		//comm with video gui
		State OBSERVE_NORMAL = new State("OBSERVE_NORMAL");
		State OBSERVE_FLYBY = new State("OBSERVE_FLYBY");
		State POKE_GUI = new State("POKE_GUI");
		State TX_GUI = new State("TX_GUI");
		State END_GUI = new State("END_GUI");

		//initialize transitions
		initializeIDLE(inputs, outputs, IDLE, RX_MM, OBSERVE_NORMAL);
		//comm with mission manager
		initializeRX_MM(inputs, outputs, IDLE, RX_MM, OBSERVE_NORMAL);
		initializePOKE_MM(inputs, POKE_MM, TX_MM);
		initializeTX_MM(TX_MM, END_MM);
		initializeEND_MM(outputs, IDLE, END_MM);
		//comm with operator
		initializePOKE_OP(inputs, POKE_OP, TX_OP);
		initializeTX_OP(TX_OP, END_OP);
		initializeEND_OP(outputs, IDLE, END_OP);
		//comm with video gui
		initializeOBSERVE_NORMAL(inputs, outputs, RX_MM, OBSERVE_NORMAL, POKE_GUI, POKE_OP);
		initializeOBSERVE_FLYBY(inputs, outputs, RX_MM, OBSERVE_NORMAL,OBSERVE_FLYBY, POKE_MM, POKE_OP);
		initializePOKE_GUI(POKE_GUI, TX_GUI);
		initializeTX_GUI(TX_GUI);
		initializeEND_GUI(outputs, IDLE, END_GUI);

		//add states
		addState(IDLE);
		//comm with mission manager
		addState(RX_MM);
		addState(POKE_MM);
		addState(TX_MM);
		addState(END_MM);
		//comm with operator
		addState(POKE_OP);
		addState(TX_OP);
		addState(END_OP);
		//comm with video gui
		addState(OBSERVE_NORMAL);
		addState(OBSERVE_FLYBY);
		addState(POKE_GUI);
		addState(TX_GUI);
		addState(END_GUI);
		
		//initialize current state
		_currentState = IDLE;
	}

	private void initializeIDLE(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs, State IDLE, State RX_MM, State OBSERVE_NORMAL) {
		IDLE.addTransition(
				new UDO[]{inputs.get(UDO.MM_POKE_VO.name())},
				null,
				new UDO[]{outputs.get(UDO.VO_ACK_MM.name())},
				null,
				RX_MM, Duration.ACK, 0);
		/*IDLE.addTransition(
				new UDO[]{UDO.VO_TARGET_DESCRIPTION_VO},
				new UDO[]{},
				OBSERVE_NORMAL, null, 0);
		IDLE.addTransition(
				new UDO[]{},
				new UDO[]{},
				OBSERVE_NORMAL, null, -1);*/
	}

	private void initializeRX_MM(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs, State IDLE, State RX_MM, State OBSERVE_NORMAL) {
		RX_MM.addTransition(
				new UDO[]{inputs.get(UDO.MM_END_VO.name())},
				null,
				null,
				null,
				IDLE, Duration.NEXT, -1);
		RX_MM.addTransition(
				null,
				null,
				null,
				null,
				IDLE, Duration.VO_RX_MM, 0);
		RX_MM.addTransition(
				new UDO[]{inputs.get(UDO.MM_END_VO.name()), inputs.get(UDO.MM_TARGET_DESCRIPTION_VO.name())},
				null,
				new UDO[]{UDO.VO_TARGET_DESCRIPTION_VO},
				null,
				OBSERVE_NORMAL, Duration.NEXT, 0);
		/*RX_MM.addTransition(
				new UDO[]{inputs.get(UDO.MM_END_VO.name()), inputs.get(UDO.MM_TARGET_DESCRIPTION_VO.name())},
				new UDO[]{UDO.VO_TARGET_DESCRIPTION_VO},
				IDLE, null, 0);
		RX_MM.addTransition(
				new UDO[]{inputs.get(UDO.MM_END_VO.name()), inputs.get(UDO.MM_TERMINATE_SEARCH_VO.name())},
				null,
				IDLE, null, 0);*/
	}

	private void initializePOKE_MM(HashMap<String, UDO> inputs, State POKE_MM, State TX_MM) {
		/*POKE_MM.addTransition(
				new UDO[]{inputs.get(UDO.MM_ACK_VO.name()), inputs.get(UDO.VO_TARGET_SIGHTED_T_VO.name())},
				new UDO[]{UDO.VO_TARGET_SIGHTED_T_VO},
				TX_MM, null, 0);
		POKE_MM.addTransition(
				new UDO[]{inputs.get(UDO.MM_ACK_VO.name()), inputs.get(UDO.VO_TARGET_SIGHTED_F_VO.name())},
				new UDO[]{UDO.VO_TARGET_SIGHTED_F_VO},
				TX_MM, null, 0);*/
	}

	private void initializeTX_MM(State TX_MM, State END_MM) {
		/*TX_MM.addTransition(
				new UDO[]{UDO.VO_TARGET_SIGHTED_F_VO},
				new UDO[]{UDO.VO_TARGET_SIGHTED_F_VO},
				END_MM , null, 0);
		TX_MM.addTransition(
				new UDO[]{UDO.VO_TARGET_SIGHTED_T_VO},
				new UDO[]{UDO.VO_TARGET_SIGHTED_T_VO},
				END_MM, null, 0);*/
	}

	private void initializeEND_MM(HashMap<String, UDO> outputs, State IDLE, State END_MM) {
		/*END_MM.addTransition(
				new UDO[]{UDO.VO_TARGET_SIGHTED_F_VO}, 
				new UDO[]{outputs.get(UDO.VO_TARGET_SIGHTING_F_MM.name()), outputs.get(UDO.VO_END_MM.name())},
				IDLE, null, 0);
		END_MM.addTransition(
				new UDO[]{UDO.VO_TARGET_SIGHTED_T_VO}, 
				new UDO[]{outputs.get(UDO.VO_TARGET_SIGHTING_T_MM.name()), outputs.get(UDO.VO_END_MM.name())}, 
				IDLE, null, 0);
		END_MM.addTransition(null,
				new UDO[]{outputs.get(UDO.VO_END_MM.name())},
				IDLE, null, 0);*/
	}

	private void initializePOKE_OP(HashMap<String, UDO> inputs, State POKE_OP, State TX_OP) {
		/*POKE_OP.addTransition(
				new UDO[]{inputs.get(UDO.OP_ACK_VO.name()), UDO.VO_BAD_STREAM_VO},
				new UDO[]{UDO.VO_BAD_STREAM_VO},
				TX_OP, null, 0);*/
	}

	private void initializeTX_OP(State TX_OP, State END_OP) {
		/*TX_OP.addTransition(
				new UDO[]{UDO.VO_BAD_STREAM_VO},
				new UDO[]{UDO.VO_BAD_STREAM_VO},
				END_OP, null, 0);*/
	}

	private void initializeEND_OP(HashMap<String, UDO> outputs, State IDLE, State END_OP) {
		/*END_OP.addTransition(
				new UDO[]{UDO.VO_BAD_STREAM_VO},
				new UDO[]{outputs.get(UDO.VO_BAD_STREAM_OP.name()), outputs.get(UDO.VO_END_OP.name())},
				IDLE, null, 0);*/
	}

	private void initializeOBSERVE_NORMAL(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs, State RX_MM, State OBSERVE_NORMAL, State POKE_GUI, State POKE_OP) {
		OBSERVE_NORMAL.addTransition(
				new UDO[]{UDO.VO_TARGET_DESCRIPTION_VO},
				null,
				new UDO[]{},
				null,
				OBSERVE_NORMAL, Duration.NEXT, 0);
		/*OBSERVE_NORMAL.addTransition(
				new UDO[]{inputs.get(UDO.MM_POKE_VO.name())}, 
				new UDO[]{outputs.get(UDO.VO_ACK_MM.name())}, 
				RX_MM, null, 0);
		OBSERVE_NORMAL.addTransition(
				new UDO[]{inputs.get(UDO.VGUI_BAD_STREAM_VO.name())},
				new UDO[]{UDO.VO_BAD_STREAM_VO, outputs.get(UDO.VO_POKE_OP.name())},
				POKE_OP, null, 0);
		OBSERVE_NORMAL.addTransition(
				new UDO[]{inputs.get(UDO.VGUI_FALSE_POSITIVE_VO.name())},
				new UDO[]{UDO.VO_FLYBY_REQ_F_VO, outputs.get(UDO.VO_POKE_VGUI.name())},
				POKE_GUI, null, 0);
		OBSERVE_NORMAL.addTransition(
				new UDO[]{inputs.get(UDO.VGUI_FALSE_POSITIVE_VO.name())},
				new UDO[]{UDO.VO_POSSIBLE_ANOMALY_DETECTED_F_VO, outputs.get(UDO.VO_POKE_VGUI.name())},
				POKE_GUI, null, 0);
		OBSERVE_NORMAL.addTransition(
				new UDO[]{inputs.get(UDO.VGUI_TRUE_POSITIVE_VO.name())},
				new UDO[]{UDO.VO_FLYBY_REQ_T_VO, outputs.get(UDO.VO_POKE_VGUI.name())},
				POKE_GUI, null, 0);
		OBSERVE_NORMAL.addTransition(new UDO[]{inputs.get(UDO.VGUI_TRUE_POSITIVE_VO.name())},
				new UDO[]{UDO.VO_POSSIBLE_ANOMALY_DETECTED_T_VO, outputs.get(UDO.VO_POKE_VGUI.name())},
				POKE_GUI, null, 0);*/
	}

	private void initializeOBSERVE_FLYBY(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs, State RX_MM, State OBSERVE_NORMAL, State OBSERVE_FLYBY, State POKE_MM, State poke_op) {
		/*OBSERVE_FLYBY.addTransition(
				new UDO[]{inputs.get(UDO.MM_POKE_VO.name())}, 
				new UDO[]{outputs.get(UDO.VO_ACK_MM.name())}, 
				RX_MM, null, 0);
		OBSERVE_FLYBY.addTransition(
				new UDO[]{inputs.get(UDO.VGUI_BAD_STREAM_VO.name())},
				new UDO[]{UDO.VO_BAD_STREAM_VO, outputs.get(UDO.VO_POKE_OP.name())},
				poke_op, null, 0);
		OBSERVE_FLYBY.addTransition(
				new UDO[]{inputs.get(UDO.VGUI_FLYBY_ANOMALY_F_VO.name())},
				new UDO[]{outputs.get(UDO.VO_FLYBY_END_FAILED_VGUI.name())},
				OBSERVE_NORMAL, null, 0);
		OBSERVE_FLYBY.addTransition(
				new UDO[]{inputs.get(UDO.VGUI_FLYBY_ANOMALY_T_VO.name())},
				new UDO[]{outputs.get(UDO.VO_FLYBY_END_FAILED_VGUI.name())},
				OBSERVE_NORMAL, null, 0);
		OBSERVE_FLYBY.addTransition(
				new UDO[]{inputs.get(UDO.VGUI_FLYBY_ANOMALY_F_VO.name())},
				new UDO[]{outputs.get(UDO.VO_FLYBY_END_SUCCESS_VGUI.name()), UDO.VO_TARGET_SIGHTED_F_VO},
				POKE_MM, null, 0);
		OBSERVE_FLYBY.addTransition(
				new UDO[]{inputs.get(UDO.VGUI_FLYBY_ANOMALY_T_VO.name())},
				new UDO[]{outputs.get(UDO.VO_FLYBY_END_SUCCESS_VGUI.name()), UDO.VO_TARGET_SIGHTED_T_VO},
				POKE_MM, null, 0);*/
	}

	private void initializePOKE_GUI(State POKE_GUI, State TX_GUI) {
		/*POKE_GUI.addTransition(
				new UDO[]{UDO.VO_FLYBY_REQ_F_VO},
				new UDO[]{UDO.VO_FLYBY_REQ_F_VO},
				TX_GUI, null, 0);
		POKE_GUI.addTransition(
				new UDO[]{UDO.VO_FLYBY_REQ_T_VO},
				new UDO[]{UDO.VO_FLYBY_REQ_T_VO},
				TX_GUI, null, 0);
		POKE_GUI.addTransition(
				new UDO[]{UDO.VO_POSSIBLE_ANOMALY_DETECTED_F_VO},
				new UDO[]{UDO.VO_POSSIBLE_ANOMALY_DETECTED_F_VO},
				TX_GUI, null, 0);
		POKE_GUI.addTransition(
				new UDO[]{UDO.VO_POSSIBLE_ANOMALY_DETECTED_T_VO},
				new UDO[]{UDO.VO_POSSIBLE_ANOMALY_DETECTED_T_VO},
				TX_GUI, null, 0);*/
	}

	private void initializeTX_GUI(State TX_GUI) {
		/*TX_GUI.addTransition(
				new UDO[]{UDO.VO_FLYBY_REQ_F_VO},
				new UDO[]{UDO.VO_FLYBY_REQ_F_VO},
				TX_GUI, null, 0);
		TX_GUI.addTransition(
				new UDO[]{UDO.VO_FLYBY_REQ_T_VO},
				new UDO[]{UDO.VO_FLYBY_REQ_T_VO},
				TX_GUI, null, 0);
		TX_GUI.addTransition(
				new UDO[]{UDO.VO_POSSIBLE_ANOMALY_DETECTED_F_VO},
				new UDO[]{UDO.VO_POSSIBLE_ANOMALY_DETECTED_F_VO},
				TX_GUI, null, 0);
		TX_GUI.addTransition(
				new UDO[]{UDO.VO_POSSIBLE_ANOMALY_DETECTED_T_VO},
				new UDO[]{UDO.VO_POSSIBLE_ANOMALY_DETECTED_T_VO},
				TX_GUI, null, 0);*/
	}

	private void initializeEND_GUI(HashMap<String, UDO> outputs, State IDLE, State END_GUI) {
		/*END_GUI.addTransition(
				new UDO[]{UDO.VO_FLYBY_REQ_F_VO},
				new UDO[]{UDO.VO_FLYBY_REQ_F_VO, outputs.get(UDO.VO_END_VGUI.name())},
				IDLE, null, 0);
		END_GUI.addTransition(
				new UDO[]{UDO.VO_FLYBY_REQ_T_VO},
				new UDO[]{UDO.VO_FLYBY_REQ_T_VO, outputs.get(UDO.VO_END_VGUI.name())},
				IDLE, null, 0);
		END_GUI.addTransition(
				new UDO[]{UDO.VO_POSSIBLE_ANOMALY_DETECTED_F_VO},
				new UDO[]{UDO.VO_POSSIBLE_ANOMALY_DETECTED_F_VO, outputs.get(UDO.VO_END_VGUI.name())},
				IDLE, null, 0);
		END_GUI.addTransition(
				new UDO[]{UDO.VO_POSSIBLE_ANOMALY_DETECTED_T_VO},
				new UDO[]{UDO.VO_POSSIBLE_ANOMALY_DETECTED_T_VO, outputs.get(UDO.VO_END_VGUI.name())},
				IDLE, null, 0);*/
	}
}
