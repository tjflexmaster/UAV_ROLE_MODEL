package actors;

import java.util.HashMap;
import simulator.*;
import team.UDO;

public class VideoOperator extends Actor {

	public VideoOperator(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs) {
		State idle = new State("IDLE");
		State rx_mm = new State("RX_MM");
		State observing_normal = new State("OBSERVING_NORMAL");
		State observing_flyby = new State("OBSERVING_FLYBY");
		State poke_gui = new State("POKE_GUI");
		State tx_gui = new State("TX_GUI");
		State end_gui = new State("END_GUI");
		State poke_mm = new State("POKE_MM");
		State tx_mm = new State("TX_MM");
		State end_mm = new State("END_MM");
		State poke_op = new State("POKE_OPERATOR");
		State tx_op = new State("TX_OPERATOR");
		State end_op = new State("END_OPERATOR");
		
		//idle state
		intializeIdleState(inputs, outputs, idle, rx_mm, observing_normal);
		
		initializeRXMMState(inputs, idle, rx_mm);
		
		poke_mm.addTransition(new UDO[]{inputs.get(UDO.MM_ACK_VO.name()), inputs.get(UDO.VO_TARGET_SIGHTED_T_VO.name())},
				new UDO[]{UDO.VO_TARGET_SIGHTED_T_VO},
				tx_mm, null, 0);
		poke_mm.addTransition(new UDO[]{inputs.get(UDO.MM_ACK_VO.name()), inputs.get(UDO.VO_TARGET_SIGHTED_F_VO.name())},
				new UDO[]{UDO.VO_TARGET_SIGHTED_F_VO},
				tx_mm, null, 0);
		
		tx_mm.addTransition(new UDO[]{UDO.VO_TARGET_SIGHTED_F_VO},
				new UDO[]{UDO.VO_TARGET_SIGHTED_F_VO},
				end_mm, null, 0);
		tx_mm.addTransition(new UDO[]{UDO.VO_TARGET_SIGHTED_T_VO},
				new UDO[]{UDO.VO_TARGET_SIGHTED_T_VO},
				end_mm, null, 0);
		
		end_mm.addTransition(new UDO[]{UDO.VO_TARGET_SIGHTED_F_VO}, 
				new UDO[]{outputs.get(UDO.VO_TARGET_SIGHTING_F_MM.name()), outputs.get(UDO.VO_END_MM.name())},
				idle, null, 0);
		end_mm.addTransition(new UDO[]{UDO.VO_TARGET_SIGHTED_T_VO}, 
				new UDO[]{outputs.get(UDO.VO_TARGET_SIGHTING_T_MM.name()), outputs.get(UDO.VO_END_MM.name())}, 
				idle, null, 0);
//		end_mm.addTransition(null,
//				new UDO[]{outputs.get(UDO.VO_END_MM.name())},
//				idle, null, 0);
		
		initializeObservingNormalState(inputs, outputs, rx_mm,
				observing_normal, poke_gui, poke_op);
		
		poke_op.addTransition(new UDO[]{inputs.get(UDO.OP_ACK_VO.name()), UDO.VO_BAD_STREAM_VO},
				new UDO[]{UDO.VO_BAD_STREAM_VO},
				tx_op, null, 0);
		
		tx_op.addTransition(new UDO[]{UDO.VO_BAD_STREAM_VO},
				new UDO[]{UDO.VO_BAD_STREAM_VO},
				end_op, null, 0);
		
		end_op.addTransition(new UDO[]{UDO.VO_BAD_STREAM_VO},
				new UDO[]{outputs.get(UDO.VO_BAD_STREAM_OP.name()), outputs.get(UDO.VO_END_OP.name())},
				idle, null, 0);

		intializePokeGUIState(poke_gui, tx_gui);
		
		initializeTXGUIState(tx_gui);
		
		initializeEndGUIState(outputs, tx_gui, end_gui);
		

		initializeObservingFlybyState(inputs, outputs, rx_mm, observing_normal,
				observing_flyby, poke_mm, poke_op);
		//comm op
		
		//comm vgui
		
		
		this.addState(idle);
		this.addState(rx_mm);
		this.addState(observing_normal);
		this.addState(observing_flyby);
		this.addState(poke_gui);
		this.addState(tx_gui);
		this.addState(end_gui);
		this.addState(poke_mm);
		this.addState(tx_mm);
		this.addState(end_mm);
		this.addState(poke_op);
		this.addState(tx_op);
		this.addState(end_op);
	}

	/**
	 * @param inputs
	 * @param outputs
	 * @param idle
	 * @param rx_mm
	 * @param observing_normal
	 */
	private void intializeIdleState(HashMap<String, UDO> inputs,
			HashMap<String, UDO> outputs, State idle, State rx_mm,
			State observing_normal) {
		idle.addTransition(new UDO[]{inputs.get(UDO.MM_POKE_VO.name())},
				new UDO[]{outputs.get(UDO.VO_ACK_MM.name())},
				rx_mm, null, 1);
		idle.addTransition(new UDO[]{UDO.VO_TARGET_DESCRIPTION_VO},
				new UDO[]{},
				observing_normal, null, 0);
		idle.addTransition(new UDO[]{},
				new UDO[]{},
				observing_normal, null, -1);
	}

	/**
	 * @param inputs
	 * @param idle
	 * @param rx_mm
	 */
	private void initializeRXMMState(HashMap<String, UDO> inputs, State idle,
			State rx_mm) {
		rx_mm.addTransition(new UDO[]{inputs.get(UDO.MM_END_VO.name())},
				null,
				idle, null, -1);
		rx_mm.addTransition(new UDO[]{inputs.get(UDO.MM_END_VO.name()), inputs.get(UDO.MM_TARGET_DESCRIPTION_VO.name())},
				new UDO[]{UDO.VO_TARGET_DESCRIPTION_VO},
				idle, null, 0);
		rx_mm.addTransition(new UDO[]{inputs.get(UDO.MM_END_VO.name()), inputs.get(UDO.MM_TERMINATE_SEARCH_VO.name())},
				null,
				idle, null, 0);
	}

	/**
	 * @param inputs
	 * @param outputs
	 * @param rx_mm
	 * @param observing_normal
	 * @param poke_gui
	 * @param poke_op
	 */
	private void initializeObservingNormalState(HashMap<String, UDO> inputs,
			HashMap<String, UDO> outputs, State rx_mm, State observing_normal,
			State poke_gui, State poke_op) {
		observing_normal.addTransition(new UDO[]{inputs.get(UDO.MM_POKE_VO.name())}, 
				new UDO[]{outputs.get(UDO.VO_ACK_MM.name())}, 
				rx_mm, null, 0);
		observing_normal.addTransition(new UDO[]{inputs.get(UDO.VGUI_BAD_STREAM_VO.name())},
				new UDO[]{UDO.VO_BAD_STREAM_VO, outputs.get(UDO.VO_POKE_OP.name())},
				poke_op, null, 0);
		observing_normal.addTransition(new UDO[]{inputs.get(UDO.VGUI_FALSE_POSITIVE_VO.name())},
				new UDO[]{UDO.VO_FLYBY_REQ_F_VO, outputs.get(UDO.VO_POKE_VGUI.name())},
				poke_gui, null, 0);
		observing_normal.addTransition(new UDO[]{inputs.get(UDO.VGUI_FALSE_POSITIVE_VO.name())},
				new UDO[]{UDO.VO_POSSIBLE_ANOMALY_DETECTED_F_VO, outputs.get(UDO.VO_POKE_VGUI.name())},
				poke_gui, null, 0);
		observing_normal.addTransition(new UDO[]{inputs.get(UDO.VGUI_TRUE_POSITIVE_VO.name())},
				new UDO[]{UDO.VO_FLYBY_REQ_T_VO, outputs.get(UDO.VO_POKE_VGUI.name())},
				poke_gui, null, 0);
		observing_normal.addTransition(new UDO[]{inputs.get(UDO.VGUI_TRUE_POSITIVE_VO.name())},
				new UDO[]{UDO.VO_POSSIBLE_ANOMALY_DETECTED_T_VO, outputs.get(UDO.VO_POKE_VGUI.name())},
				poke_gui, null, 0);
	}

	/**
	 * @param poke_gui
	 * @param tx_gui
	 */
	private void intializePokeGUIState(State poke_gui, State tx_gui) {
		poke_gui.addTransition(new UDO[]{UDO.VO_FLYBY_REQ_F_VO},
				new UDO[]{UDO.VO_FLYBY_REQ_F_VO},
				tx_gui, null, 0);
		poke_gui.addTransition(new UDO[]{UDO.VO_FLYBY_REQ_T_VO},
				new UDO[]{UDO.VO_FLYBY_REQ_T_VO},
				tx_gui, null, 0);
		poke_gui.addTransition(new UDO[]{UDO.VO_POSSIBLE_ANOMALY_DETECTED_F_VO},
				new UDO[]{UDO.VO_POSSIBLE_ANOMALY_DETECTED_F_VO},
				tx_gui, null, 0);
		poke_gui.addTransition(new UDO[]{UDO.VO_POSSIBLE_ANOMALY_DETECTED_T_VO},
				new UDO[]{UDO.VO_POSSIBLE_ANOMALY_DETECTED_T_VO},
				tx_gui, null, 0);
	}

	/**
	 * @param tx_gui
	 */
	private void initializeTXGUIState(State tx_gui) {
		tx_gui.addTransition(new UDO[]{UDO.VO_FLYBY_REQ_F_VO},
				new UDO[]{UDO.VO_FLYBY_REQ_F_VO},
				tx_gui, null, 0);
		tx_gui.addTransition(new UDO[]{UDO.VO_FLYBY_REQ_T_VO},
				new UDO[]{UDO.VO_FLYBY_REQ_T_VO},
				tx_gui, null, 0);
		tx_gui.addTransition(new UDO[]{UDO.VO_POSSIBLE_ANOMALY_DETECTED_F_VO},
				new UDO[]{UDO.VO_POSSIBLE_ANOMALY_DETECTED_F_VO},
				tx_gui, null, 0);
		tx_gui.addTransition(new UDO[]{UDO.VO_POSSIBLE_ANOMALY_DETECTED_T_VO},
				new UDO[]{UDO.VO_POSSIBLE_ANOMALY_DETECTED_T_VO},
				tx_gui, null, 0);
	}

	/**
	 * @param outputs
	 * @param tx_gui
	 * @param end_gui
	 */
	private void initializeEndGUIState(HashMap<String, UDO> outputs,
			State tx_gui, State end_gui) {
		end_gui.addTransition(new UDO[]{UDO.VO_FLYBY_REQ_F_VO},
				new UDO[]{UDO.VO_FLYBY_REQ_F_VO, outputs.get(UDO.VO_END_VGUI.name())},
				tx_gui, null, 0);
		end_gui.addTransition(new UDO[]{UDO.VO_FLYBY_REQ_T_VO},
				new UDO[]{UDO.VO_FLYBY_REQ_T_VO, outputs.get(UDO.VO_END_VGUI.name())},
				tx_gui, null, 0);
		end_gui.addTransition(new UDO[]{UDO.VO_POSSIBLE_ANOMALY_DETECTED_F_VO},
				new UDO[]{UDO.VO_POSSIBLE_ANOMALY_DETECTED_F_VO, outputs.get(UDO.VO_END_VGUI.name())},
				tx_gui, null, 0);
		end_gui.addTransition(new UDO[]{UDO.VO_POSSIBLE_ANOMALY_DETECTED_T_VO},
				new UDO[]{UDO.VO_POSSIBLE_ANOMALY_DETECTED_T_VO, outputs.get(UDO.VO_END_VGUI.name())},
				tx_gui, null, 0);
	}

	/**
	 * @param inputs
	 * @param outputs
	 * @param rx_mm
	 * @param observing_normal
	 * @param observing_flyby
	 * @param poke_mm
	 * @param poke_op
	 */
	private void initializeObservingFlybyState(HashMap<String, UDO> inputs,
			HashMap<String, UDO> outputs, State rx_mm, State observing_normal,
			State observing_flyby, State poke_mm, State poke_op) {
		observing_flyby.addTransition(new UDO[]{inputs.get(UDO.MM_POKE_VO.name())}, 
				new UDO[]{outputs.get(UDO.VO_ACK_MM.name())}, 
				rx_mm, null, 0);
		observing_flyby.addTransition(new UDO[]{inputs.get(UDO.VGUI_BAD_STREAM_VO.name())},
				new UDO[]{UDO.VO_BAD_STREAM_VO, outputs.get(UDO.VO_POKE_OP.name())},
				poke_op, null, 0);
		observing_flyby.addTransition(new UDO[]{inputs.get(UDO.VGUI_FLYBY_ANOMALY_F_VO.name())},
				new UDO[]{outputs.get(UDO.VO_FLYBY_END_FAILED_VGUI.name())},
				observing_normal, null, 0);
		observing_flyby.addTransition(new UDO[]{inputs.get(UDO.VGUI_FLYBY_ANOMALY_T_VO.name())},
				new UDO[]{outputs.get(UDO.VO_FLYBY_END_FAILED_VGUI.name())},
				observing_normal, null, 0);
		observing_flyby.addTransition(new UDO[]{inputs.get(UDO.VGUI_FLYBY_ANOMALY_F_VO.name())},
				new UDO[]{outputs.get(UDO.VO_FLYBY_END_SUCCESS_VGUI.name()), UDO.VO_TARGET_SIGHTED_F_VO},
				poke_mm, null, 0);
		observing_flyby.addTransition(new UDO[]{inputs.get(UDO.VGUI_FLYBY_ANOMALY_T_VO.name())},
				new UDO[]{outputs.get(UDO.VO_FLYBY_END_SUCCESS_VGUI.name()), UDO.VO_TARGET_SIGHTED_T_VO},
				poke_mm, null, 0);
	}
}
