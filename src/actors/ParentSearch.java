package actors;

import java.util.HashMap;

import simulator.Actor;
import simulator.State;
import team.Duration;
import team.UDO;

public class ParentSearch extends Actor {

	public ParentSearch(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs) {
		//initialize name
		_name = "PARENT_SEARCH";
		
		//initialize states
		State IDLE = new State("IDLE");
		State POKE_MM = new State("POKE_MM");
		State TX_MM = new State("TX_MM");
		State END_MM = new State("END_MM");
		State RX_MM = new State("RX_MM");

		//initialize transitions
		initializeIDLE(inputs, outputs, IDLE, POKE_MM, RX_MM);

		initializePokeMM(inputs, outputs, IDLE, POKE_MM, TX_MM);
		TX_MM.addTransition(
				new UDO[]{UDO.PS_NEW_SEARCH_AOI_PS, UDO.PS_TARGET_DESCRIPTION_PS},
				new UDO[]{outputs.get(UDO.PS_END_MM.name()), outputs.get(UDO.PS_NEW_SEARCH_AOI_MM.name()), outputs.get(UDO.PS_TARGET_DESCRIPTION_MM.name())},
				END_MM, Duration.PS_TX_DATA_MM, 0);
		END_MM.addTransition(
				null,
				null,
				IDLE,Duration.NEXT,0);
		//add states
		addState(IDLE);
		addState(POKE_MM);
		addState(TX_MM);
		addState(END_MM);
		addState(RX_MM);
		
		//initialize current state
		_currentState = IDLE;
	}

	/**
	 * @param inputs
	 * @param outputs
	 * @param IDLE
	 * @param POKE_MM
	 * @param TX_MM
	 */
	private void initializePokeMM(HashMap<String, UDO> inputs,
			HashMap<String, UDO> outputs, State IDLE, State POKE_MM, State TX_MM) {
		POKE_MM.addTransition(
				new UDO[]{UDO.PS_NEW_SEARCH_AOI_PS, UDO.PS_TARGET_DESCRIPTION_PS},
				new UDO[]{outputs.get(UDO.PS_POKE_MM.name()), UDO.PS_NEW_SEARCH_AOI_PS, UDO.PS_TARGET_DESCRIPTION_PS},
				IDLE, Duration.PS_POKE_MM, 0);
		POKE_MM.addTransition(
				new UDO[]{inputs.get(UDO.MM_ACK_PS.name()), UDO.PS_NEW_SEARCH_AOI_PS, UDO.PS_TARGET_DESCRIPTION_PS},
				new UDO[]{UDO.PS_NEW_SEARCH_AOI_PS, UDO.PS_TARGET_DESCRIPTION_PS},
				TX_MM,Duration.NEXT,1);
	}

	private void initializeIDLE(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs, State IDLE, State POKE_MM, State RX_MM) {
		IDLE.addTransition(
				new UDO[]{inputs.get(UDO.PS_TIME_TIL_START_PS.name()).update(new Integer(0))}, 
				new UDO[]{outputs.get(UDO.PS_POKE_MM.name()), outputs.get(UDO.PS_NEW_SEARCH_AOI_PS.name()), outputs.get(UDO.PS_TARGET_DESCRIPTION_PS.name())},
				POKE_MM, Duration.PS_SEND_DATA_PS, 0);
		/*IDLE.addTransition(
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
		IDLE.addTransition(
				new UDO[]{UDO.PS_TERMINATE_SEARCH_PS},
				new UDO[]{outputs.get(UDO.PS_POKE_MM.name()), outputs.get(UDO.PS_TERMINATE_SEARCH_MM.name())},
				POKE_MM, null, 1);*/
	}


}
