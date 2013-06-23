package actors;

import java.util.HashMap;

import simulator.*;
import team.*;

public class MissionManager extends Actor {
	

	public MissionManager(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs) {
		//state names
		State IDLE = new State("IDLE");
		//comm with PS
		State POKE_PS = new State("POKE_PS");
		State TX_PS = new State("TX_PS");
		State END_PS = new State("END_PS");
		State RX_PS = new State("RX_PS");
		//comm with OP
		State POKE_OP = new State("POKE_OP");
		State POKE_AOI_OP = new State("POKE_AOI_OP");
		State POKE_TERM_OP = new State("POKE_TERM_OP");
		State TX_AOI_OP = new State("TX_AOI_OP");
		State TX_TERM_OP = new State("TX_TERM_OP");
		State TX_OP = new State("TX_OP");
		State END_OP = new State("END_OP");
		State RX_OP = new State("RX_OP");
		//comm with VO
		State POKE_VO = new State("POKE_VO");
		State POKE_DESC_VO = new State("POKE_DESC_VO");
		State POKE_TERM_VO = new State("POKE_TERM_VO");
		State TX_DESC_VO = new State("TX_DESC_VO");
		State TX_TERM_VO = new State("TX_TERM_VO");
		State TX_VO = new State("TX_VO");
		State END_VO = new State("END_VO");
		State RX_VO = new State("RX_VO");
		//comm with VGUI
		State OBSERVING_VGUI = new State("OBSERVING_VGUI");
		State POKE_VGUI = new State("POKE_VGUI");
		State TX_VGUI = new State("TX_VGUI");
		State END_VGUI = new State("END_VGUI");
		
		//state transitions
		//comm with PS
		initializePSComm(inputs, outputs, IDLE, POKE_PS, TX_PS, RX_PS, RX_OP,
				OBSERVING_VGUI);
		
		
		//comm with OP
		initializeOPComm(inputs, outputs, IDLE, POKE_OP, TX_OP, RX_OP);
		
		
		//comm with VO
		initializeVOComm(inputs, outputs, IDLE, RX_OP, POKE_VO, TX_VO, RX_VO);
		
		//comm with VGUI
		
		initializeObservingVGUI(inputs, outputs, IDLE, RX_OP, OBSERVING_VGUI);

		
		//add states
		_states.add(IDLE);
		//comm PS
		_states.add(POKE_PS);
		_states.add(TX_PS);
		_states.add(END_PS);
		_states.add(RX_PS);
		//comm OP
		_states.add(POKE_OP);
		_states.add(POKE_AOI_OP);
		_states.add(POKE_TERM_OP);
		_states.add(TX_AOI_OP);
		_states.add(TX_TERM_OP);
		_states.add(TX_OP);
		_states.add(END_OP);
		_states.add(RX_OP);
		//comm VO
		_states.add(POKE_VO);
		_states.add(POKE_DESC_VO);
		_states.add(POKE_TERM_VO);
		_states.add(TX_DESC_VO);
		_states.add(TX_TERM_VO);
		_states.add(TX_VO);
		_states.add(END_VO);
		_states.add(RX_VO);
		//comm VGUI
		_states.add(OBSERVING_VGUI);
		_states.add(POKE_VGUI);
		_states.add(TX_VGUI);
		_states.add(END_VGUI);
	}

	/**
	 * @param inputs
	 * @param outputs
	 * @param IDLE
	 * @param RX_OP
	 * @param OBSERVING_VGUI
	 */
	private void initializeObservingVGUI(HashMap<String, UDO> inputs,
			HashMap<String, UDO> outputs, State IDLE, State RX_OP,
			State OBSERVING_VGUI) {
		OBSERVING_VGUI.addTransition(new UDO[]{inputs.get(UDO.VGUI_VALIDATION_REQ_F_MM.name())}, 
				new UDO[]{outputs.get(UDO.MM_FLYBY_REQ_F_VGUI.name())}, IDLE, null, 0);
		OBSERVING_VGUI.addTransition(new UDO[]{inputs.get(UDO.VGUI_VALIDATION_REQ_T_MM.name())}, 
				new UDO[]{outputs.get(UDO.MM_FLYBY_REQ_T_VGUI.name())}, IDLE, null, 0);
		OBSERVING_VGUI.addTransition(new UDO[]{inputs.get(UDO.OP_POKE_MM.name())}, new UDO[]{outputs.get(UDO.MM_ACK_OP.name())}, RX_OP, null, 0);
		OBSERVING_VGUI.addTransition(new UDO[]{inputs.get(UDO.VO_POKE_MM.name())}, new UDO[]{outputs.get(UDO.MM_ACK_VO.name())}, RX_OP, null, 0);
		OBSERVING_VGUI.addTransition(new UDO[]{inputs.get(UDO.PS_POKE_MM.name())}, new UDO[]{outputs.get(UDO.MM_ACK_PS.name())}, RX_OP, null, 0);
	}

	/**
	 * @param inputs
	 * @param outputs
	 * @param IDLE
	 * @param RX_OP
	 * @param POKE_VO
	 * @param TX_VO
	 * @param RX_VO
	 */
	private void initializeVOComm(HashMap<String, UDO> inputs,
			HashMap<String, UDO> outputs, State IDLE, State RX_OP,
			State POKE_VO, State TX_VO, State RX_VO) {
		IDLE.addTransition(new UDO[]{inputs.get(UDO.VO_POKE_MM.name())}, new UDO[]{outputs.get(UDO.MM_ACK_VO.name())}, RX_OP, null, 0);
		RX_VO.addTransition(new UDO[]{inputs.get(UDO.VO_END_MM.name())}, null, IDLE, null, -1);
		RX_VO.addTransition(new UDO[]{inputs.get(UDO.VO_END_MM.name()), inputs.get(UDO.VO_TARGET_SIGHTING_F_MM.name())}, null, IDLE, null, -1);
		RX_VO.addTransition(new UDO[]{inputs.get(UDO.VO_END_MM.name()), inputs.get(UDO.VO_TARGET_SIGHTING_T_MM.name())}, null, IDLE, null, -1);
		POKE_VO.addTransition(new UDO[]{inputs.get(UDO.VO_ACK_MM.name())}, null, TX_VO, null, 0);
		POKE_VO.addTransition(new UDO[]{inputs.get(UDO.VO_BUSY_MM)}, null, IDLE, null, 0);
		POKE_VO.addTransition(new UDO[]{inputs.get(UDO.PS_POKE_MM.name())}, new UDO[]{outputs.get(UDO.OP_POKE_MM.name())}, null, null, 0);
		POKE_VO.addTransition(new UDO[]{inputs.get(UDO.OP_POKE_MM.name())}, new UDO[]{outputs.get(UDO.MM_BUSY_OP)}, null, null, 0);
		//TODO implement the doNextTask method of the previous model for the end state
	}

	/**
	 * @param inputs
	 * @param outputs
	 * @param IDLE
	 * @param POKE_OP
	 * @param TX_OP
	 * @param RX_OP
	 */
	private void initializeOPComm(HashMap<String, UDO> inputs,
			HashMap<String, UDO> outputs, State IDLE, State POKE_OP,
			State TX_OP, State RX_OP) {
		IDLE.addTransition(new UDO[]{inputs.get(UDO.OP_POKE_MM.name())}, new UDO[]{outputs.get(UDO.MM_ACK_OP.name())}, RX_OP, null, 0);
		RX_OP.addTransition(new UDO[]{inputs.get(UDO.OP_END_MM.name())}, null, IDLE, null, -1);
		RX_OP.addTransition(new UDO[]{inputs.get(UDO.OP_END_MM.name()), inputs.get(UDO.OP_SEARCH_AOI_COMPLETE_MM.name())}, null, IDLE, null, 0);
		POKE_OP.addTransition(new UDO[]{inputs.get(UDO.OP_ACK_MM.name())}, null, TX_OP, null, 0);
		POKE_OP.addTransition(new UDO[]{inputs.get(UDO.OP_BUSY_MM.name())}, null, IDLE, null, 0);
		POKE_OP.addTransition(new UDO[]{inputs.get(UDO.PS_POKE_MM.name())}, new UDO[]{outputs.get(UDO.MM_BUSY_PS.name())}, POKE_OP, null, 0);
		POKE_OP.addTransition(new UDO[]{inputs.get(UDO.VO_POKE_MM.name())}, new UDO[]{outputs.get(UDO.MM_BUSY_VO.name())}, POKE_OP, null, 0);
		//TODO implement the doNextTask method of the previous model for the end state
	}

	/**
	 * @param inputs
	 * @param outputs
	 * @param IDLE
	 * @param POKE_PS
	 * @param TX_PS
	 * @param RX_PS
	 * @param RX_OP
	 * @param OBSERVING_VGUI
	 */
	private void initializePSComm(HashMap<String, UDO> inputs,
			HashMap<String, UDO> outputs, State IDLE, State POKE_PS,
			State TX_PS, State RX_PS, State RX_OP, State OBSERVING_VGUI) {
		IDLE.addTransition(new UDO[]{inputs.get(UDO.VGUI_POSSIBLE_ANOMALY_DETECTED_F_MM.name())}, null, OBSERVING_VGUI, null, 0);
		IDLE.addTransition(new UDO[]{inputs.get(UDO.VGUI_POSSIBLE_ANOMALY_DETECTED_T_MM.name())}, null, OBSERVING_VGUI, null, 0);
		IDLE.addTransition(new UDO[]{inputs.get(UDO.PS_POKE_MM.name())}, new UDO[]{outputs.get(UDO.MM_ACK_PS.name())}, RX_OP, null, 0);
		//TODO implement the doNextTask method of the previous model for the idle state
		//TODO implement memory for the received data
		RX_PS.addTransition(new UDO[]{inputs.get(UDO.PS_END_MM.name()), inputs.get(UDO.PS_TERMINATE_SEARCH_MM.name())}, null, IDLE, null, 0);
		RX_PS.addTransition(new UDO[]{inputs.get(UDO.PS_END_MM.name()), inputs.get(UDO.PS_TARGET_DESCRIPTION_MM.name())}, null, IDLE, null, 0);
		RX_PS.addTransition(new UDO[]{inputs.get(UDO.PS_END_MM.name()), inputs.get(UDO.PS_NEW_SEARCH_AOI.name())}, null, IDLE, null, 0);
		RX_PS.addTransition(new UDO[]{inputs.get(UDO.PS_END_MM.name()), inputs.get(UDO.PS_NEW_SEARCH_AOI.name()), inputs.get(UDO.PS_TARGET_DESCRIPTION_MM.name())}, null, IDLE, null, 0);
		RX_PS.addTransition(new UDO[]{inputs.get(UDO.PS_END_MM.name())}, null, IDLE, null, -1);
		POKE_PS.addTransition(new UDO[]{inputs.get(UDO.PS_ACK_MM.name())}, null, TX_PS, null, 0);
		POKE_PS.addTransition(new UDO[]{inputs.get(UDO.PS_BUSY_MM.name())}, null, IDLE, null, 0);
		POKE_PS.addTransition(new UDO[]{inputs.get(UDO.PS_POKE_MM.name())}, new UDO[]{outputs.get(UDO.MM_ACK_PS.name())}, IDLE, null, 0);
		POKE_PS.addTransition(new UDO[]{inputs.get(UDO.OP_POKE_MM.name())}, new UDO[]{outputs.get(UDO.MM_BUSY_OP.name())}, IDLE, null, 0);
		POKE_PS.addTransition(new UDO[]{inputs.get(UDO.VO_POKE_MM.name())}, new UDO[]{outputs.get(UDO.MM_BUSY_VO.name())}, IDLE, null, 0);
		//TODO implement the doNextTask method of the previous model for the end state
	}

	@Override
	public boolean updateTransition() {
		// TODO Auto-generated method stub
		return false;
	}

}
