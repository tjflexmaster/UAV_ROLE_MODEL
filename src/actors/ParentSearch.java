package actors;

import java.util.HashMap;

import simulator.*;
import team.UDO;

public class ParentSearch extends Actor {

	public ParentSearch(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs) {
		//state names
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
	}

}
