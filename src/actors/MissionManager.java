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
		//comm with OP
		//comm with VO
		//comm with VGUI
		
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
	
	@Override
	public boolean hasNewTransition(int currentTime) {
		// TODO Auto-generated method stub
		return false;
	}

}
