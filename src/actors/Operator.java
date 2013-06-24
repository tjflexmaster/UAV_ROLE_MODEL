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
	}
	
}
