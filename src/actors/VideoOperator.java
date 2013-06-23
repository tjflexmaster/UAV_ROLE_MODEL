package actors;

import java.util.HashMap;

import simulator.*;
import team.UDO;

public class VideoOperator extends Actor {

	public VideoOperator(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs) {
		State idle = new State("IDLE");
		State rx_mm = new State("RX_MM");
		State observing_normal = new State("OBSERVING_NORMAL");
		State observing_flyby = new State("OBSERVING_FLYBY");
		State poke_gui = new State("POKE_GUI");
		State tx_gui = new State("TX_GUI");
		State end_gui = new State("END_GUI");
		State poke_mm = new State("POKE_MM");
		State tx_mm = new State("TX_MM");
		State end_mm = new State("END_MM");
		State poke_operator = new State("POKE_OPERATOR");
		State tx_operator = new State("TX_OPERATOR");
		State end_operator = new State("END_OPERATOR");
		
		//comm mm
		idle.addTransition(new UDO[]{inputs.get(UDO.MM_POKE_VO.name())}, new UDO[]{outputs.get(UDO.VO_ACK_MM.name())}, rx_mm, null, 0);
		//comm op
		
		//comm vgui
		
		
		this.addState(idle);
		this.addState(rx_mm);
		this.addState(observing_normal);
		this.addState(observing_flyby);
		this.addState(poke_gui);
		this.addState(tx_gui);
		this.addState(end_gui);
		this.addState(poke_mm);
		this.addState(tx_mm);
		this.addState(end_mm);
		this.addState(poke_operator);
		this.addState(tx_operator);
		this.addState(end_operator);
	}
}
