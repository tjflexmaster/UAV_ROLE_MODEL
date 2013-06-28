package model.actors;

import java.util.HashMap;

import model.team.*;

import simulator.*;

public class MissionManager extends Actor {

	public MissionManager(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs) {
		//initialize name
		_name = "MISSION_MANAGER";
		
		//initialize states
		State IDLE = new State("IDLE");
		//comm with PS
		State POKE_PS = new State("POKE_PS");
		State TX_PS = new State("TX_PS");
		State RX_PS = new State("RX_PS");
		//comm with OP
		State POKE_OP = new State("POKE_OP");
//		State POKE_AOI_OP = new State("POKE_AOI_OP");
//		State POKE_TERM_OP = new State("POKE_TERM_OP");
//		State TX_AOI_OP = new State("TX_AOI_OP");
//		State TX_TERM_OP = new State("TX_TERM_OP");
		State TX_OP = new State("TX_OP");
		State END_OP = new State("END_OP");
		State RX_OP = new State("RX_OP");
		//comm with VO
		State POKE_VO = new State("POKE_VO");
//		State POKE_DESC_VO = new State("POKE_DESC_VO");
//		State POKE_TERM_VO = new State("POKE_TERM_VO");
//		State TX_DESC_VO = new State("TX_DESC_VO");
//		State TX_TERM_VO = new State("TX_TERM_VO");
		State TX_VO = new State("TX_VO");
		State RX_VO = new State("RX_VO");
		//comm with VGUI
		State OBSERVING_VGUI = new State("OBSERVING_VGUI");
		State POKE_VGUI = new State("POKE_VGUI");
		State TX_VGUI = new State("TX_VGUI");
		
		//initialize transitions
		initializeIdle(inputs, outputs, IDLE, RX_PS);
		initializeRX_PS(inputs, outputs, IDLE, RX_PS, POKE_OP);
		initializePOKE_PS(inputs, outputs, POKE_PS,TX_PS);
		initializePOKE_OP(inputs, outputs, IDLE, POKE_OP, TX_OP);
		initializeRX_OP(inputs, IDLE, RX_OP);
		initializePOKE_VO(inputs, outputs, IDLE, POKE_VO, TX_VO);
		initializeRX_VO(inputs, IDLE, RX_VO);
		TX_OP.addTransition(
				new UDO[]{ UDO.MM_TARGET_DESCRIPTION_MM,UDO.MM_NEW_SEARCH_AOI_MM},
				new UDO[]{ outputs.get(UDO.MM_END_OP.name()),  outputs.get(UDO.MM_NEW_SEARCH_AOI_OP.name()), UDO.MM_TARGET_DESCRIPTION_MM},
				END_OP, Duration.MM_TX_OP, 0);
		//add states
		addState(IDLE);
		//comm with PS
		addState(POKE_PS);
		addState(TX_PS);
		addState(RX_PS);
		//comm with OP
		addState(POKE_OP);
//		addState(POKE_AOI_OP);
//		addState(POKE_TERM_OP);
//		addState(TX_AOI_OP);
//		addState(TX_TERM_OP);
		addState(TX_OP);
		addState(RX_OP);
		//comm with VO
		addState(POKE_VO);
//		addState(POKE_DESC_VO);
//		addState(POKE_TERM_VO);
//		addState(TX_DESC_VO);
//		addState(TX_TERM_VO);
		addState(TX_VO);
		addState(RX_VO);
		//comm with VGUI
		addState(OBSERVING_VGUI);
		addState(POKE_VGUI);
		addState(TX_VGUI);
		
		//initialize current state
		_currentState = IDLE;
	}

	private void initializeRX_VO(HashMap<String, UDO> inputs, State IDLE, State RX_VO) {
		/*RX_VO.addTransition(
				new UDO[]{inputs.get(UDO.VO_END_MM.name()),inputs.get(UDO.VO_TARGET_SIGHTING_F_MM.name())},
				null,
				IDLE, null, -1);
		RX_VO.addTransition(
				new UDO[]{inputs.get(UDO.VO_END_MM.name()), inputs.get(UDO.VO_TARGET_SIGHTING_T_MM.name())},
				null,
				IDLE, null, -1);*/
	}

	private void initializePOKE_VO(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs, State IDLE, State POKE_VO, State TX_VO) {
		POKE_VO.addTransition(
				new UDO[]{inputs.get(UDO.MM_TARGET_DESCRIPTION_MM.name())},
				new UDO[]{outputs.get(UDO.MM_POKE_VO.name()), UDO.MM_TARGET_DESCRIPTION_MM},
				IDLE, Duration.MM_POKE_VO, 0);
		POKE_VO.addTransition(
				new UDO[]{inputs.get(UDO.VO_ACK_MM.name()), UDO.MM_TARGET_DESCRIPTION_MM},
				new UDO[]{outputs.get(UDO.MM_END_VO.name()), outputs.get(UDO.MM_TARGET_DESCRIPTION_VO.name())},
				TX_VO, Duration.NEXT, 0);
	}

	private void initializeRX_OP(HashMap<String, UDO> inputs, State IDLE, State RX_OP) {
		/*RX_OP.addTransition(
				new UDO[]{inputs.get(UDO.OP_END_MM.name()), inputs.get(UDO.OP_SEARCH_AOI_COMPLETE_MM.name())},
				null,
				IDLE, null, 0);*/
	}

	private void initializePOKE_OP(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs,State IDLE, State POKE_OP, State TX_OP) {
		POKE_OP.addTransition(
				new UDO[]{UDO.MM_TARGET_DESCRIPTION_MM, UDO.MM_NEW_SEARCH_AOI_MM},
				new UDO[]{outputs.get(UDO.MM_POKE_OP.name()), UDO.MM_TARGET_DESCRIPTION_MM, UDO.MM_NEW_SEARCH_AOI_MM},
				IDLE, Duration.MM_POKE_PS, 0);
		POKE_OP.addTransition(
				new UDO[]{inputs.get(UDO.OP_ACK_MM.name()), UDO.MM_TARGET_DESCRIPTION_MM, UDO.MM_NEW_SEARCH_AOI_MM},
				new UDO[]{UDO.MM_TARGET_DESCRIPTION_MM, UDO.MM_NEW_SEARCH_AOI_MM},
				TX_OP, Duration.NEXT, 1);
	}

	private void initializePOKE_PS(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs, State POKE_PS, State TX_PS) {
		/*POKE_PS.addTransition(
				new UDO[]{inputs.get(UDO.PS_BUSY_MM)},
				new UDO[]{outputs.get(UDO.MM_POKE_PS)},
				POKE_PS, null, 0);*/
	}

	private void initializeRX_PS(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs, State IDLE, State RX_PS, State POKE_OP) {
		RX_PS.addTransition(
				new UDO[]{inputs.get(UDO.PS_END_MM.name())},
				null,
				IDLE, Duration.MM_RX_PS, -1);
//		RX_PS.addTransition(
//				null,
//				null,
//				RX_PS, null, 0);
		RX_PS.addTransition(
				new UDO[]{inputs.get(UDO.PS_END_MM.name()), inputs.get(UDO.PS_TARGET_DESCRIPTION_MM.name()), inputs.get(UDO.PS_NEW_SEARCH_AOI_MM.name())},
				new UDO[]{outputs.get(UDO.MM_TARGET_DESCRIPTION_MM.name()), outputs.get(UDO.MM_NEW_SEARCH_AOI_MM.name()), outputs.get(UDO.MM_POKE_OP.name())},
				POKE_OP, Duration.NEXT, 0);
		/*RX_PS.addTransition(
				new UDO[]{inputs.get(UDO.PS_TERMINATE_SEARCH_MM)},
				new UDO[]{outputs.get(UDO.MM_TERMINATE_SEARCH_MM)},
				RX_PS, null, 0);*/
	}

	private void initializeIdle(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs, State IDLE, State RX_PS) {
		IDLE.addTransition(
				new UDO[]{inputs.get(UDO.PS_POKE_MM.name())},
				new UDO[]{outputs.get(UDO.MM_ACK_PS.name())},
				RX_PS, Duration.ACK, 0);
		/*IDLE.addTransition(
				new UDO[]{inputs.get(UDO.VGUI_POSSIBLE_ANOMALY_DETECTED_F_MM)},
				new UDO[]{outputs.get(UDO.MM_FLYBY_REQ_F_VGUI)},
				IDLE, null, 0);
		IDLE.addTransition(
				new UDO[]{inputs.get(UDO.VGUI_POSSIBLE_ANOMALY_DETECTED_T_MM)},
				new UDO[]{outputs.get(UDO.MM_FLYBY_REQ_T_VGUI)},
				IDLE, null, 0);*/
	}

}
