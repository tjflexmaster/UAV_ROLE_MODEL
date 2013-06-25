package actors;

import java.util.HashMap;

import simulator.*;
import team.UDO;

public class ParentSearch extends Actor {

	public ParentSearch(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs) {
		//state names
		State IDLE = new State("IDLE");
		State POKE_MM = new State("POKE_MM");
		State TX_MM = new State("TX_MM");
		State END_MM = new State("END_MM");
		State RX_MM = new State("RX_MM");

		//transitions
		IDLE.addTransition(
				new UDO[]{inputs.get(UDO.MM_POKE_PS.name())},
				new UDO[]{outputs.get(UDO.PS_ACK_MM.name())},
				RX_MM, null, 0);
		IDLE.addTransition(
				new UDO[]{inputs.get(UDO.EVENT_TERMINATE_SEARCH_PS.name())},
				new UDO[]{UDO.PS_TERMINATE_SEARCH_PS},
				IDLE, null, 2);
		IDLE.addTransition(
				new UDO[]{inputs.get(UDO.EVENT_START_SEARCH_PS.name())},
				new UDO[]{UDO.PS_NEW_SEARCH_AOI_PS, UDO.PS_TARGET_DESCRIPTION_PS},
				IDLE,null,0);
		IDLE.addTransition(
				new UDO[]{UDO.PS_NEW_SEARCH_AOI_PS},
				new UDO[]{outputs.get(UDO.PS_POKE_MM.name()), outputs.get(UDO.PS_NEW_SEARCH_AOI_MM.name())},
				POKE_MM, null, 0);
		IDLE.addTransition(
				new UDO[]{UDO.PS_TARGET_DESCRIPTION_PS},
				new UDO[]{outputs.get(UDO.PS_POKE_MM.name()), outputs.get(UDO.PS_TARGET_DESCRIPTION_MM.name())},
				POKE_MM, null, 0);
		IDLE.addTransition(new UDO[]{UDO.PS_TERMINATE_SEARCH_PS},
				new UDO[]{outputs.get(UDO.PS_POKE_MM.name()), outputs.get(UDO.PS_TERMINATE_SEARCH_MM.name())},
				POKE_MM, null, 1);
		IDLE.addTransition(
				new UDO[]{inputs.get(UDO.PS_TIME_TIL_START_PS).update(new Integer(0))}, 
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
		//add states
		_states.add(IDLE);
		_states.add(POKE_MM);
		_states.add(TX_MM);
		_states.add(END_MM);
		_states.add(RX_MM);
	}

}
