package actors;

import java.util.HashMap;

import simulator.*;
import team.UDO;

public class Operator extends Actor {

	public Operator(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs) {
		//add states
		State IDLE = new State("IDLE");_states.add(IDLE);
		//comm with PS
		State RX_MM = new State("RX_MM");_states.add(RX_MM);
		//comm with OGUI
		State TX_OGUI = new State("TX_OGUI");_states.add(TX_OGUI);
		
		//add transitions
		//start simulation
		IDLE.addTransition(
				new UDO[]{inputs.get(UDO.MM_POKE_OP)},
				new UDO[]{outputs.get(UDO.OP_ACK_MM)},
				RX_MM, null, 0);
		RX_MM.addTransition(
				null,
				null,
				RX_MM, null, 0);
		RX_MM.addTransition(
				new UDO[]{inputs.get(UDO.MM_END_OP), inputs.get(UDO.MM_NEW_SEARCH_AOI_OP)},
				new UDO[]{outputs.get(UDO.OP_NEW_SEARCH_AOI_OP)},
				TX_OGUI, null, 0);
		TX_OGUI.addTransition(
				new UDO[]{inputs.get(UDO.OP_NEW_SEARCH_AOI_OP)},
				new UDO[]{outputs.get(UDO.OP_TAKE_OFF_OGUI)},
				IDLE, null, 0);

		//add states
		State idle = new State("IDLE");_states.add(idle);
		State post_flight = new State("POST_FLIGHT");_states.add(post_flight);
		State post_flight_complete = new State("POST_FLIGHT_COMPLETE");_states.add(post_flight_complete);
		State launch_uav = new State("LAUNCH_UAV");_states.add(launch_uav);
		State observing_gui = new State("OBSERVING_GUI");_states.add(observing_gui);
		State observing_uav = new State("OBSERVING_UAV");_states.add(observing_uav);
		State poke_mm = new State("POKE_MM");_states.add(poke_mm);
		State tx_mm = new State("TX_MM");_states.add(tx_mm);
		State end_mm = new State("END_MM");_states.add(end_mm);
		State rx_mm = new State("RX_MM");_states.add(rx_mm);
		State rx_vo = new State("RX_VO");_states.add(rx_vo);
		State observing_flyby = new State("OBSERVING_FLYBY");_states.add(observing_flyby);
		State poke_gui = new State("POKE_GUI");_states.add(poke_gui);
		State tx_gui = new State("TX_GUI");_states.add(tx_gui);
		State end_gui = new State("END_GUI");_states.add(end_gui);
		
		//add transitions
		idle.addTransition(
				new UDO[]{inputs.get(UDO.MM_POKE_OP.name())},
				new UDO[]{outputs.get(UDO.OP_ACK_MM.name())},
				rx_mm, null, 0);
		idle.addTransition(
				new UDO[]{UDO.OP_TAKE_OFF_OP},
				new UDO[]{outputs.get(UDO.OP_TAKE_OFF_OGUI.name())},
				launch_uav, null, 0);
		idle.addTransition(
				new UDO[]{inputs.get(UDO.UAV_FLYING_NORMAL.name())},
				null,
				observing_gui, null, 0);
		idle.addTransition(
				new UDO[]{inputs.get(UDO.UAV_FLYING_FLYBY.name())},
				null,
				observing_gui, null, 0);
		
		observing_gui.addTransition(
				new UDO[]{inputs.get(UDO.OGUI_FLYBY_REQ_F_OP.name())},
				new UDO[]{outputs.get(UDO.OP_POKE_OGUI.name())},
				poke_gui, null, 0);
		observing_gui.addTransition(
				new UDO[]{inputs.get(UDO.OGUI_FLYBY_REQ_T_OP.name())},
				new UDO[]{outputs.get(UDO.OP_POKE_OGUI.name())},
				poke_gui, null, 0);
		observing_gui.addTransition(
				new UDO[]{inputs.get(UDO.OGUI_lANDED_OP.name())},
				null,
				post_flight, null, 0);
		observing_gui.addTransition(
				null,
				null,
				observing_uav, null, -1);
		
		observing_uav.addTransition(
				new UDO[]{inputs.get(UDO.UAV_LANDED.name())},
				null,
				post_flight, null, 0);
		observing_uav.addTransition(
				null,
				null,
				observing_gui, null, -1);
		
		post_flight.addTransition(
				null,
				new UDO[]{outputs.get(UDO.OP_POST_FLIGHT_COMPLETE_UAV.name())},
				post_flight_complete, null, 0);
		post_flight_complete.addTransition(
				null,
				null,
				idle, null, 0);
	}
	
}
