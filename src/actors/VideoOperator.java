package actors;

import java.util.HashMap;
import simulator.*;
import team.*;

public class VideoOperator extends Actor {

	public VideoOperator(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs) {

		//add states
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
		State OBSERVING_NORMAL = new State("OBSERVE_NORMAL");
		State OBSERVING_FLYBY = new State("OBSERVE_FLYBY");
		State POKE_GUI = new State("POKE_GUI");
		State TX_GUI = new State("TX_GUI");
		State END_GUI = new State("END_GUI");
		
		addState(IDLE);
		addState(RX_MM);
		addState(POKE_MM);
		addState(TX_MM);
		addState(END_MM);
		addState(POKE_OP);
		addState(TX_OP);
		addState(END_OP);
		addState(OBSERVING_NORMAL);
		addState(OBSERVING_FLYBY);
		addState(POKE_GUI);
		addState(TX_GUI);

		//add transitions
		//start simulation
		IDLE.addTransition(
				new UDO[]{inputs.get(UDO.MM_POKE_VO)},
				new UDO[]{outputs.get(UDO.VO_ACK_MM)},
				RX_MM, null, 0);
		RX_MM.addTransition(
				null,
				null,
				RX_MM, null, 0);
		RX_MM.addTransition(
				new UDO[]{inputs.get(UDO.MM_END_VO), inputs.get(UDO.MM_TARGET_DESCRIPTION_VO)},
				new UDO[]{outputs.get(UDO.VO_TARGET_DESCRIPTION_VO)},
				OBSERVING_NORMAL, null, 0);

		//add states
		
		//idle state
		intializeIdleState(inputs, outputs, IDLE, RX_MM, OBSERVING_NORMAL);
		initializeRXMMState(inputs, IDLE, RX_MM);
		
		POKE_MM.addTransition(
				new UDO[]{inputs.get(UDO.MM_ACK_VO.name()), inputs.get(UDO.VO_TARGET_SIGHTED_T_VO.name())},
				new UDO[]{UDO.VO_TARGET_SIGHTED_T_VO},
				TX_MM, null, 0);
		POKE_MM.addTransition(
				new UDO[]{inputs.get(UDO.MM_ACK_VO.name()), inputs.get(UDO.VO_TARGET_SIGHTED_F_VO.name())},
				new UDO[]{UDO.VO_TARGET_SIGHTED_F_VO},
				TX_MM, null, 0);
		
		TX_MM.addTransition(
				new UDO[]{UDO.VO_TARGET_SIGHTED_F_VO},
				new UDO[]{UDO.VO_TARGET_SIGHTED_F_VO},
				END_MM , null, 0);
		TX_MM.addTransition(
				new UDO[]{UDO.VO_TARGET_SIGHTED_T_VO},
				new UDO[]{UDO.VO_TARGET_SIGHTED_T_VO},
				END_MM, null, 0);
		
		END_MM.addTransition(
				new UDO[]{UDO.VO_TARGET_SIGHTED_F_VO}, 
				new UDO[]{outputs.get(UDO.VO_TARGET_SIGHTING_F_MM.name()), outputs.get(UDO.VO_END_MM.name())},
				IDLE, null, 0);
		END_MM.addTransition(
				new UDO[]{UDO.VO_TARGET_SIGHTED_T_VO}, 
				new UDO[]{outputs.get(UDO.VO_TARGET_SIGHTING_T_MM.name()), outputs.get(UDO.VO_END_MM.name())}, 
				IDLE, null, 0);
//		end_mm.addTransition(null,
//				new UDO[]{outputs.get(UDO.VO_END_MM.name())},
//				idle, null, 0);
		
		initializeObservingNormalState(inputs, outputs, RX_MM,
				OBSERVING_NORMAL, POKE_GUI, POKE_OP);
		
		POKE_OP.addTransition(
				new UDO[]{inputs.get(UDO.OP_ACK_VO.name()), UDO.VO_BAD_STREAM_VO},
				new UDO[]{UDO.VO_BAD_STREAM_VO},
				TX_OP, null, 0);
		
		TX_OP.addTransition(
				new UDO[]{UDO.VO_BAD_STREAM_VO},
				new UDO[]{UDO.VO_BAD_STREAM_VO},
				END_OP, null, 0);
		
		END_OP.addTransition(
				new UDO[]{UDO.VO_BAD_STREAM_VO},
				new UDO[]{outputs.get(UDO.VO_BAD_STREAM_OP.name()), outputs.get(UDO.VO_END_OP.name())},
				IDLE, null, 0);

		intializePokeGUIState(POKE_GUI, TX_GUI);
		
		initializeTXGUIState(TX_GUI);
		
		initializeEndGUIState(outputs, IDLE, END_GUI);
		

		initializeObservingFlybyState(inputs, outputs, RX_MM, OBSERVING_NORMAL,
				OBSERVING_FLYBY, POKE_MM, POKE_OP);
		
	}

	/**
	 * @param inputs
	 * @param outputs
	 * @param IDLE
	 * @param RX_MM
	 * @param OBSERVING_NORMAL
	 */
	private void intializeIdleState(HashMap<String, UDO> inputs,
			HashMap<String, UDO> outputs, State IDLE, State RX_MM,
			State OBSERVING_NORMAL) {
		IDLE.addTransition(
				new UDO[]{inputs.get(UDO.MM_POKE_VO.name())},
				new UDO[]{outputs.get(UDO.VO_ACK_MM.name())},
				RX_MM, null, 1);
		IDLE.addTransition(
				new UDO[]{UDO.VO_TARGET_DESCRIPTION_VO},
				new UDO[]{},
				OBSERVING_NORMAL, null, 0);
		IDLE.addTransition(
				new UDO[]{},
				new UDO[]{},
				OBSERVING_NORMAL, null, -1);
	}

	/**
	 * @param inputs
	 * @param IDLE
	 * @param RX_MM
	 */
	private void initializeRXMMState(HashMap<String, UDO> inputs, State IDLE,
			State RX_MM) {
		RX_MM.addTransition(
				new UDO[]{inputs.get(UDO.MM_END_VO.name())},
				null,
				IDLE, null, -1);
		RX_MM.addTransition(
				new UDO[]{inputs.get(UDO.MM_END_VO.name()), inputs.get(UDO.MM_TARGET_DESCRIPTION_VO.name())},
				new UDO[]{UDO.VO_TARGET_DESCRIPTION_VO},
				IDLE, null, 0);
		RX_MM.addTransition(
				new UDO[]{inputs.get(UDO.MM_END_VO.name()), inputs.get(UDO.MM_TERMINATE_SEARCH_VO.name())},
				null,
				IDLE, null, 0);
	}

	/**
	 * @param inputs
	 * @param outputs
	 * @param RX_MM
	 * @param OBSERVING_NORMAL
	 * @param POKE_GUI
	 * @param POKE_OP
	 */
	private void initializeObservingNormalState(HashMap<String, UDO> inputs,
			HashMap<String, UDO> outputs, State RX_MM, State OBSERVING_NORMAL,
			State POKE_GUI, State POKE_OP) {
		OBSERVING_NORMAL.addTransition(
				new UDO[]{inputs.get(UDO.MM_POKE_VO.name())}, 
				new UDO[]{outputs.get(UDO.VO_ACK_MM.name())}, 
				RX_MM, null, 0);
		OBSERVING_NORMAL.addTransition(
				new UDO[]{inputs.get(UDO.VGUI_BAD_STREAM_VO.name())},
				new UDO[]{UDO.VO_BAD_STREAM_VO, outputs.get(UDO.VO_POKE_OP.name())},
				POKE_OP, null, 0);
		OBSERVING_NORMAL.addTransition(
				new UDO[]{inputs.get(UDO.VGUI_FALSE_POSITIVE_VO.name())},
				new UDO[]{UDO.VO_FLYBY_REQ_F_VO, outputs.get(UDO.VO_POKE_VGUI.name())},
				POKE_GUI, null, 0);
		OBSERVING_NORMAL.addTransition(
				new UDO[]{inputs.get(UDO.VGUI_FALSE_POSITIVE_VO.name())},
				new UDO[]{UDO.VO_POSSIBLE_ANOMALY_DETECTED_F_VO, outputs.get(UDO.VO_POKE_VGUI.name())},
				POKE_GUI, null, 0);
		OBSERVING_NORMAL.addTransition(
				new UDO[]{inputs.get(UDO.VGUI_TRUE_POSITIVE_VO.name())},
				new UDO[]{UDO.VO_FLYBY_REQ_T_VO, outputs.get(UDO.VO_POKE_VGUI.name())},
				POKE_GUI, null, 0);
		OBSERVING_NORMAL.addTransition(new UDO[]{inputs.get(UDO.VGUI_TRUE_POSITIVE_VO.name())},
				new UDO[]{UDO.VO_POSSIBLE_ANOMALY_DETECTED_T_VO, outputs.get(UDO.VO_POKE_VGUI.name())},
				POKE_GUI, null, 0);
	}

	/**
	 * @param POKE_GUI
	 * @param TX_GUI
	 */
	private void intializePokeGUIState(State POKE_GUI, State TX_GUI) {
		POKE_GUI.addTransition(
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
				TX_GUI, null, 0);
	}

	/**
	 * @param TX_GUI
	 */
	private void initializeTXGUIState(State TX_GUI) {
		TX_GUI.addTransition(
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
				TX_GUI, null, 0);
	}

	/**
	 * @param outputs
	 * @param IDLE
	 * @param END_GUI
	 */
	private void initializeEndGUIState(HashMap<String, UDO> outputs,
			State IDLE, State END_GUI) {
		END_GUI.addTransition(
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
				IDLE, null, 0);
	}

	/**
	 * @param inputs
	 * @param outputs
	 * @param RX_MM
	 * @param OBSERVING_NORMAL
	 * @param OBSERVING_FLYBY
	 * @param POKE_MM
	 * @param poke_op
	 */
	private void initializeObservingFlybyState(HashMap<String, UDO> inputs,
			HashMap<String, UDO> outputs, State RX_MM, State OBSERVING_NORMAL,
			State OBSERVING_FLYBY, State POKE_MM, State poke_op) {
		OBSERVING_FLYBY.addTransition(
				new UDO[]{inputs.get(UDO.MM_POKE_VO.name())}, 
				new UDO[]{outputs.get(UDO.VO_ACK_MM.name())}, 
				RX_MM, null, 0);
		OBSERVING_FLYBY.addTransition(
				new UDO[]{inputs.get(UDO.VGUI_BAD_STREAM_VO.name())},
				new UDO[]{UDO.VO_BAD_STREAM_VO, outputs.get(UDO.VO_POKE_OP.name())},
				poke_op, null, 0);
		OBSERVING_FLYBY.addTransition(
				new UDO[]{inputs.get(UDO.VGUI_FLYBY_ANOMALY_F_VO.name())},
				new UDO[]{outputs.get(UDO.VO_FLYBY_END_FAILED_VGUI.name())},
				OBSERVING_NORMAL, null, 0);
		OBSERVING_FLYBY.addTransition(
				new UDO[]{inputs.get(UDO.VGUI_FLYBY_ANOMALY_T_VO.name())},
				new UDO[]{outputs.get(UDO.VO_FLYBY_END_FAILED_VGUI.name())},
				OBSERVING_NORMAL, null, 0);
		OBSERVING_FLYBY.addTransition(
				new UDO[]{inputs.get(UDO.VGUI_FLYBY_ANOMALY_F_VO.name())},
				new UDO[]{outputs.get(UDO.VO_FLYBY_END_SUCCESS_VGUI.name()), UDO.VO_TARGET_SIGHTED_F_VO},
				POKE_MM, null, 0);
		OBSERVING_FLYBY.addTransition(
				new UDO[]{inputs.get(UDO.VGUI_FLYBY_ANOMALY_T_VO.name())},
				new UDO[]{outputs.get(UDO.VO_FLYBY_END_SUCCESS_VGUI.name()), UDO.VO_TARGET_SIGHTED_T_VO},
				POKE_MM, null, 0);
	}
}
