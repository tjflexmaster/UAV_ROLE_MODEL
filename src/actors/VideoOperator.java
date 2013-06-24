package actors;

import java.util.HashMap;

import simulator.*;
import team.*;

public class VideoOperator extends Actor {

	public VideoOperator(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs) {
		//add states
		State IDLE = new State("IDLE");this.addState(IDLE);
		//comm with mission manager
		State RX_MM = new State("RX_MM");this.addState(RX_MM);
		State POKE_MM = new State("POKE_MM");this.addState(POKE_MM);
		State TX_MM = new State("TX_MM");this.addState(TX_MM);
		//comm with operator
		State POKE_OP = new State("POKE_OP");this.addState(POKE_OP);
		State TX_OP = new State("TX_OP");this.addState(TX_OP);
		State END_OP = new State("END_OP");this.addState(END_OP);
		//comm with video gui
		State OBSERVE_NORMAL = new State("OBSERVE_NORMAL");this.addState(OBSERVE_NORMAL);
		State OBSERVE_FLYBY = new State("OBSERVE_FLYBY");this.addState(OBSERVE_FLYBY);
		State POKE_GUI = new State("POKE_GUI");this.addState(POKE_GUI);
		State TX_GUI = new State("TX_GUI");this.addState(TX_GUI);
		
		//add transitions
		//start simulation
		IDLE.addTransition(
				new UDO[]{inputs.get(UDO.MM_POKE_VO)},
				new UDO[]{outputs.get(UDO.VO_ACK_MM)},
				RX_MM, null, 0);
		RX_MM.addTransition(
				null,
				null,
				RX_MM, null, 0);
		RX_MM.addTransition(
				new UDO[]{inputs.get(UDO.MM_END_VO), inputs.get(UDO.MM_TARGET_DESCRIPTION_VO)},
				new UDO[]{outputs.get(UDO.VO_TARGET_DESCRIPTION_VO)},
				OBSERVE_NORMAL, null, 0);
	}
}
