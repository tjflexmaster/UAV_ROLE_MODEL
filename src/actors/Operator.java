package actors;

import java.util.HashMap;

import simulator.*;
import team.UDO;

public class Operator extends Actor {

	public Operator(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs) {
		
		State idle = new State("IDLE");
		State post_flight = new State("POST_FLIGHT");
		State post_flight_complete = new State("POST_FLIGHT_COMPLETE");
		State launch_uav = new State("LAUNCH_UAV");
		State observing_gui = new State("OBSERVING_GUI");
		State observing_uav = new State("OBSERVING_UAV");
		State poke_mm = new State("POKE_MM");
		State tx_mm = new State("TX_MM");
		State end_mm = new State("END_MM");
		State rx_mm = new State("RX_MM");
		State rx_vo = new State("RX_VO");
		State observing_flyby = new State("OBSERVING_FLYBY");
		State poke_gui = new State("POKE_GUI");
		State tx_gui = new State("TX_GUI");
		State end_gui = new State("END_GUI");
		
		//add transitions
		idle.addTransition(new UDO[]{inputs.get(UDO.MM_POKE_OP.name())}, new UDO[]{outputs.get(UDO.OP_ACK_MM.name())}, rx_mm, null, 0);
		idle.addTransition(new UDO[]{UDO.OP_TAKE_OFF_OP}, new UDO[]{outputs.get(UDO.OP_TAKE_OFF_OGUI.name())}, launch_uav, null, 0);
		idle.addTransition(new UDO[]{inputs.get(UDO.UAV_FLYING_NORMAL.name())}, null, observing_gui, null, 0);
		idle.addTransition(new UDO[]{inputs.get(UDO.UAV_FLYING_FLYBY.name())}, null, observing_gui, null, 0);
		
		observing_gui.addTransition(new UDO[]{inputs.get(UDO.OGUI_FLYBY_REQ_F_OP.name())}, new UDO[]{outputs.get(UDO.OP_POKE_OGUI.name())}, poke_gui, null, 0);
		observing_gui.addTransition(new UDO[]{inputs.get(UDO.OGUI_FLYBY_REQ_T_OP.name())}, new UDO[]{outputs.get(UDO.OP_POKE_OGUI.name())}, poke_gui, null, 0);
		observing_gui.addTransition(new UDO[]{inputs.get(UDO.OGUI_lANDED_OP.name())}, null, post_flight, null, 0);
		observing_gui.addTransition(null, null, observing_uav, null, -1);
		
		observing_uav.addTransition(new UDO[]{inputs.get(UDO.UAV_LANDED.name())}, null, post_flight, null, 0);
		observing_uav.addTransition(null, null, observing_gui, null, -1);
		
		post_flight.addTransition(null, new UDO[]{outputs.get(UDO.OP_POST_FLIGHT_COMPLETE_UAV.name())}, post_flight_complete, null, 0);
		post_flight_complete.addTransition(null, null, idle, null, 0);
		
		//TODO add communications transitions
		
		
		//add states
		_states.add(idle);
		_states.add(poke_gui);
		_states.add(tx_gui);
		_states.add(end_gui);
		_states.add(poke_mm);
		_states.add(tx_mm);
		_states.add(end_mm);
		_states.add(rx_mm);
		_states.add(rx_vo);
		_states.add(observing_flyby);
		_states.add(observing_gui);
		_states.add(observing_uav);
		_states.add(post_flight);
		_states.add(post_flight_complete);
		_states.add(launch_uav);
	}

	
}
