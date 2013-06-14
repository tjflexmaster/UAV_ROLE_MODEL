package Actors;

import java.util.ArrayList;

import Simulator.Actor;
import Simulator.Duration;
import Simulator.State;
import Simulator.UDO;

public class MissionManager extends Actor {

	public MissionManager(ArrayList<UDO> inputs, ArrayList<UDO> outputs) {
		_states.add(new State("IDLE"));

		/**
		 * Communicate with PS
		 */
		_states.add(new State("POKE_PS"));
		_states.add(new State("TX_PS"));
		_states.add(new State("END_PS"));
		_states.add(new State("RX_PS"));
		initializePSComm(inputs.get(inputs.indexOf(UDO.PS_POKE_MM)), 
				inputs.get(inputs.indexOf(UDO.PS_ACK_MM)), 
				inputs.get(inputs.indexOf(UDO.PS_BUSY_MM)),
				inputs.get(inputs.indexOf(UDO.VO_POKE_MM)),
				inputs.get(inputs.indexOf(UDO.OP_POKE_MM)),
				outputs.get(outputs.indexOf(UDO.MM_POKE_PS)),
				outputs.get(outputs.indexOf(UDO.MM_ACK_PS)),
				outputs.get(outputs.indexOf(UDO.MM_ACK_OP)),
				outputs.get(outputs.indexOf(UDO.MM_ACK_VO)));
		
		
		/**
		 * Communicate with OP
		 */
		
		_states.add(new State("POKE_OP"));
		_states.add(new State("POKE_AOI_OP"));
		_states.add(new State("POKE_TERM_OP"));
		_states.add(new State("TX_AOI_OP"));
		_states.add(new State("TX_TERM_OP"));
		_states.add(new State("TX_OP"));
		_states.add(new State("END_OP"));
		_states.add(new State("RX_OP"));

		/**
		 * Communicate with VO
		 */
		
		_states.add(new State("POKE_VO"));
		_states.add(new State("POKE_DESC_VO"));
		_states.add(new State("POKE_TERM_VO"));
		_states.add(new State("TX_DESC_VO"));
		_states.add(new State("TX_TERM_VO"));
		_states.add(new State("TX_VO"));
		_states.add(new State("END_VO"));
		_states.add(new State("RX_VO"));
		
		/**
		 * Communicate with VGUI
		 */
		
		_states.add(new State("OBSERVING_VGUI"));
		_states.add(new State("POKE_VGUI"));
		_states.add(new State("TX_VGUI"));
		_states.add(new State("END_VGUI"));
	}

	/**
	 * @param ps_poke
	 * @param ps_ack
	 * @param ps_busy
	 * @param vo_poke
	 * @param op_poke
	 * @param poke
	 * @param ack_ps
	 * @param ack_op
	 * @param ack_vo
	 */
	private void initializePSComm(UDO ps_poke, UDO ps_ack, UDO ps_busy,
			UDO vo_poke, UDO op_poke, UDO poke, UDO ack_ps, UDO ack_op,
			UDO ack_vo) {
		State state = _states.get(_states.indexOf("POKE_PS"));
		//TODO handle recuring transitions
		state.addTransition(null, new UDO[]{poke}, state, 1, 0)
				.addTransition(new UDO[]{ps_ack}, null, _states.get(_states.indexOf("TX_PS")), 1, 1)
				.addTransition(new UDO[]{ps_busy, ps_poke}, new UDO[]{ack_ps}, _states.get(_states.indexOf("ACK_PS")), 1, 1)
				.addTransition(new UDO[]{ps_busy,op_poke}, new UDO[]{ack_op}, _states.get(_states.indexOf("ACK_OP")), 1, 1)
				.addTransition(new UDO[]{ps_busy,vo_poke}, new UDO[]{ack_vo}, _states.get(_states.indexOf("ACK_VO")), 1, 1);
		state = _states.get(_states.indexOf("TX_PS"));
		
	}

	@Override
	public boolean updateTransition(int currentTime) {
		// TODO Auto-generated method stub
		return false;
	}

}
