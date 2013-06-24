package actors;

import java.util.HashMap;

import simulator.*;
import team.UDO;

public class ParentSearch extends Actor {

	public ParentSearch(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs) {
		//state names
<<<<<<< HEAD
		State idle = new State("IDLE");
		State poke_mm = new State("POKE_MM");
		State tx_mm = new State("TX_MM");
		State end_mm = new State("END_MM");
		State rx_mm = new State("RX_MM");

		//transitions
		idle.addTransition(new UDO[]{inputs.get(UDO.MM_POKE_PS.name())},
				new UDO[]{outputs.get(UDO.PS_ACK_MM.name())},
				rx_mm, null, 0);
		idle.addTransition(new UDO[]{inputs.get(UDO.EVENT_TERMINATE_SEARCH_PS.name())}, new UDO[]{UDO.PS_TERMINATE_SEARCH_PS}, idle, null, 2);
		idle.addTransition(new UDO[]{inputs.get(UDO.EVENT_START_SEARCH_PS.name())},
				new UDO[]{UDO.PS_NEW_SEARCH_AOI_PS, UDO.PS_TARGET_DESCRIPTION_PS},
				idle,null,0);
		idle.addTransition(new UDO[]{UDO.PS_NEW_SEARCH_AOI_PS},
				new UDO[]{outputs.get(UDO.PS_POKE_MM.name()), outputs.get(UDO.PS_NEW_SEARCH_AOI_MM.name())},
				poke_mm, null, 0);
		idle.addTransition(new UDO[]{UDO.PS_TARGET_DESCRIPTION_PS},
				new UDO[]{outputs.get(UDO.PS_POKE_MM.name()), outputs.get(UDO.PS_TARGET_DESCRIPTION_MM.name())},
				poke_mm, null, 0);
		idle.addTransition(new UDO[]{UDO.PS_TERMINATE_SEARCH_PS},
				new UDO[]{outputs.get(UDO.PS_POKE_MM.name()), outputs.get(UDO.PS_TERMINATE_SEARCH_MM.name())},
				poke_mm, null, 1);

		//add states
		_states.add(idle);
		_states.add(poke_mm);
		_states.add(tx_mm);
		_states.add(end_mm);
		_states.add(rx_mm);
=======
		State IDLE = new State("IDLE");
		State POKE_MM = new State("POKE_MM");
		
		//add states
		_states.add(IDLE);
		_states.add(POKE_MM);
		
		//state transitions
		//start simulation
		IDLE.addTransition(
				new UDO[]{inputs.get(UDO.UAVBAT_TIME_TIL_START_UAVBAT).update(new Integer(0))}, 
				new UDO[]{outputs.get(UDO.PS_POKE_MM), outputs.get(UDO.PS_NEW_SEARCH_AOI_PS), outputs.get(UDO.PS_TARGET_DESCRIPTION_PS)},
				POKE_MM, null, 0);
		POKE_MM.addTransition(
				new UDO[]{inputs.get(UDO.MM_BUSY_PS), inputs.get(UDO.PS_NEW_SEARCH_AOI_PS), inputs.get(UDO.PS_TARGET_DESCRIPTION_PS)},
				new UDO[]{outputs.get(UDO.PS_POKE_MM), outputs.get(UDO.PS_NEW_SEARCH_AOI_PS), outputs.get(UDO.PS_TARGET_DESCRIPTION_PS)},
				POKE_MM, null, 0);
		POKE_MM.addTransition(
				new UDO[]{inputs.get(UDO.MM_ACK_PS), inputs.get(UDO.PS_NEW_SEARCH_AOI_PS), inputs.get(UDO.PS_TARGET_DESCRIPTION_PS)},
				new UDO[]{outputs.get(UDO.PS_END_MM), outputs.get(UDO.PS_NEW_SEARCH_AOI_MM), outputs.get(UDO.PS_TARGET_DESCRIPTION_MM)},
				IDLE, null, 0);
>>>>>>> branch 'master' of http://github.com/tjflexmaster/UAV_ROLE_MODEL.git
	}

}
