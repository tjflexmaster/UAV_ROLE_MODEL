package actors;

import java.util.HashMap;

import simulator.*;
import team.*;

public class MissionManager extends Actor {
	

	public MissionManager(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs) {
		//add states
		State IDLE = new State("IDLE");
		addState(IDLE);
		//comm with PS
		State POKE_PS = new State("POKE_PS");
		addState(POKE_PS);
		State TX_PS = new State("TX_PS");
		addState(TX_PS);
		State RX_PS = new State("RX_PS");
		addState(RX_PS);
		//comm with OP
		State POKE_OP = new State("POKE_OP");
		addState(POKE_OP);
		State POKE_AOI_OP = new State("POKE_AOI_OP");
		addState(POKE_AOI_OP);
		State POKE_TERM_OP = new State("POKE_TERM_OP");
		addState(POKE_TERM_OP);
		State TX_AOI_OP = new State("TX_AOI_OP");
		addState(TX_AOI_OP);
		State TX_TERM_OP = new State("TX_TERM_OP");
		addState(TX_TERM_OP);
		State TX_OP = new State("TX_OP");
		addState(TX_OP);
		State RX_OP = new State("RX_OP");
		addState(RX_OP);
		//comm with VO
		State POKE_VO = new State("POKE_VO");
		addState(POKE_VO);
		State POKE_DESC_VO = new State("POKE_DESC_VO");
		addState(POKE_DESC_VO);
		State POKE_TERM_VO = new State("POKE_TERM_VO");
		addState(POKE_TERM_VO);
		State TX_DESC_VO = new State("TX_DESC_VO");
		addState(TX_DESC_VO);
		State TX_TERM_VO = new State("TX_TERM_VO");
		addState(TX_TERM_VO);
		State TX_VO = new State("TX_VO");
		addState(TX_VO);
		State RX_VO = new State("RX_VO");
		addState(RX_VO);
		//comm with VGUI
		State OBSERVING_VGUI = new State("OBSERVING_VGUI");
		addState(OBSERVING_VGUI);
		State POKE_VGUI = new State("POKE_VGUI");
		addState(POKE_VGUI);
		State TX_VGUI = new State("TX_VGUI");
		addState(TX_VGUI);
		

		//add transitions
		initializeIdleState(inputs, outputs, IDLE, RX_PS);
		initializeRXPSState(inputs, outputs, IDLE, RX_PS, POKE_OP);
		
		POKE_PS.addTransition(
				new UDO[]{inputs.get(UDO.PS_BUSY_MM)},
				new UDO[]{outputs.get(UDO.MM_POKE_PS)},
				POKE_PS, null, 0);
		POKE_OP.addTransition(
				new UDO[]{inputs.get(UDO.MM_TARGET_DESCRIPTION_MM), inputs.get(UDO.MM_NEW_SEARCH_AOI_MM)},
				new UDO[]{outputs.get(UDO.MM_POKE_OP), outputs.get(UDO.MM_TARGET_DESCRIPTION_MM), outputs.get(UDO.MM_NEW_SEARCH_AOI_MM)},
				POKE_OP, null, 0);
		POKE_OP.addTransition(
				new UDO[]{inputs.get(UDO.OP_ACK_MM), inputs.get(UDO.MM_TARGET_DESCRIPTION_MM), inputs.get(UDO.MM_NEW_SEARCH_AOI_MM)},
				new UDO[]{outputs.get(UDO.MM_POKE_OP), outputs.get(UDO.MM_TARGET_DESCRIPTION_MM), outputs.get(UDO.MM_NEW_SEARCH_AOI_OP)},
				POKE_VO, null, 0);
		RX_OP.addTransition(
				new UDO[]{inputs.get(UDO.OP_END_MM), inputs.get(UDO.OP_SEARCH_AOI_COMPLETE_MM)},
				null,
				IDLE, null, 0);
		POKE_VO.addTransition(
				new UDO[]{inputs.get(UDO.MM_TARGET_DESCRIPTION_MM)},
				new UDO[]{outputs.get(UDO.MM_POKE_VO), outputs.get(UDO.MM_TARGET_DESCRIPTION_MM)},
				POKE_VO, null, 0);
		POKE_VO.addTransition(
				new UDO[]{inputs.get(UDO.VO_ACK_MM), inputs.get(UDO.MM_TARGET_DESCRIPTION_MM)},
				new UDO[]{outputs.get(UDO.MM_END_VO), outputs.get(UDO.MM_TARGET_DESCRIPTION_VO)},
				IDLE, null, 0);
		RX_VO.addTransition(
				new UDO[]{inputs.get(UDO.VO_END_MM),inputs.get(UDO.VO_TARGET_SIGHTING_F_MM)},
				null,
				IDLE, null, -1);
		RX_VO.addTransition(
				new UDO[]{inputs.get(UDO.VO_END_MM), inputs.get(UDO.VO_TARGET_SIGHTING_T_MM)},
				null,
				IDLE, null, -1);
	}

	/**
	 * @param inputs
	 * @param outputs
	 * @param IDLE
	 * @param RX_PS
	 * @param POKE_OP
	 */
	private void initializeRXPSState(HashMap<String, UDO> inputs,
			HashMap<String, UDO> outputs, State IDLE, State RX_PS, State POKE_OP) {
		RX_PS.addTransition(
				null,
				null,
				RX_PS, null, 0);
		RX_PS.addTransition(
				new UDO[]{inputs.get(UDO.PS_END_MM), inputs.get(UDO.PS_TARGET_DESCRIPTION_MM), inputs.get(UDO.PS_NEW_SEARCH_AOI_MM)},
				new UDO[]{outputs.get(UDO.MM_TARGET_DESCRIPTION_MM), outputs.get(UDO.MM_NEW_SEARCH_AOI_MM)},
				POKE_OP, null, 0);
		RX_PS.addTransition(
				new UDO[]{inputs.get(UDO.PS_END_MM)},
				null,
				IDLE, null, -1);
		RX_PS.addTransition(
				new UDO[]{inputs.get(UDO.PS_TERMINATE_SEARCH_MM)},
				new UDO[]{outputs.get(UDO.MM_TERMINATE_SEARCH_MM)},
				RX_PS, null, 0);
	}

	/**
	 * @param inputs
	 * @param outputs
	 * @param IDLE
	 * @param RX_PS
	 */
	private void initializeIdleState(HashMap<String, UDO> inputs,
			HashMap<String, UDO> outputs, State IDLE, State RX_PS) {
		IDLE.addTransition(
				new UDO[]{inputs.get(UDO.PS_POKE_MM)},
				new UDO[]{outputs.get(UDO.MM_ACK_PS)},
				RX_PS, null, 0);
		IDLE.addTransition(
				new UDO[]{inputs.get(UDO.VGUI_POSSIBLE_ANOMALY_DETECTED_F_MM)},
				new UDO[]{outputs.get(UDO.MM_FLYBY_REQ_F_VGUI)},
				IDLE, null, 0);
		IDLE.addTransition(
				new UDO[]{inputs.get(UDO.VGUI_POSSIBLE_ANOMALY_DETECTED_T_MM)},
				new UDO[]{outputs.get(UDO.MM_FLYBY_REQ_T_VGUI)},
				IDLE, null, 0);
	}

}
