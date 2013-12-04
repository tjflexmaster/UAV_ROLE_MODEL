package model.actors;

import model.team.*;
import simulator.*;

public class MissionManager extends Actor {
public enum DATA_MM_MM_COMM{
	MM_START_LISTEN_TO_OP_MM,
	MM_START_LISTEN_TO_VO_MM,
	MM_START_LISTEN_TO_PS_MM,
	MM_STOP_TRANSMIT_SEARCH_COMPLETE_MM,
	MM_STOP_SEARCH_MM,
	MM_STOP_LISTEN_TO_PS_MM,
	MM_START_TRANSMIT_AOI_MM,
	MM_START_SEARCH_MM,
	MM_START_TRANSMIT_TARGET_DESCRIPTION_MM,
	MM_START_TRANSMIT_TERMINATE_SEARCH_MM,
	MM_STOP_TRANSMIT_AOI_MM,
	MM_STOP_LISTEN_TO_OP_MM,
	MM_START_TRANSMIT_SEARCH_COMPLETE_MM,
	MM_STOP_TRANSMIT_TARGET_DESCRIPTION_MM,
	MM_STOP_LISTEN_TO_VO_MM,
	MM_STOP_VERIFICATION_MM,
}
public enum AUDIO_MM_PS_COMM{
	MM_ACK_PS,
	MM_POKE_PS,
	MM_SEARCH_COMPLETE_PS,
	MM_SEARCH_FAILED_PS,
	MM_TARGET_SIGHTED_T_PS,
	MM_TARGET_SIGHTED_F_PS,
	MM_END_PS,
}
public enum DATA_MM_VGUI_COMM{
	MM_FLYBY_REQ_T_VGUI,
	MM_FLYBY_REQ_F_VGUI,
	MM_ANOMALY_DISMISSED_T_VGUI,
	MM_ANOMALY_DISMISSED_F_VGUI,
}
public enum AUDIO_MM_VO_COMM{
	MM_POKE_VO,
	MM_ACK_VO,
	MM_TARGET_DESCRIPTION_VO,
	MM_TERMINATE_SEARCH_VO,
	MM_END_VO,
}
public enum AUDIO_MM_OP_COMM{
	MM_POKE_OP,
	MM_ACK_OP,
	MM_END_OP,
	MM_NEW_SEARCH_AOI_OP,
	MM_TERMINATE_SEARCH_OP,
}
public MissionManager(ComChannelList inputs, ComChannelList outputs) {
	setName("MissionManager");
	State END_VGUI = new State("END_VGUI");
	State TX_VGUI = new State("TX_VGUI");
	State OBSERVING_VGUI = new State("OBSERVING_VGUI");
	State END_VO = new State("END_VO");
	State TX_VO = new State("TX_VO");
	State END_OP = new State("END_OP");
	State TX_OP = new State("TX_OP");
	State END_PS = new State("END_PS");
	State TX_PS = new State("TX_PS");
	State POKE_VGUI = new State("POKE_VGUI");
	State RX_PS = new State("RX_PS");
	State RX_VO = new State("RX_VO");
	State RX_OP = new State("RX_OP");
	State POKE_PS = new State("POKE_PS");
	State POKE_OP = new State("POKE_OP");
	State POKE_VO = new State("POKE_VO");
	State IDLE = new State("IDLE");
	initializeInternalVariables();
	initializeRX_PS(inputs, outputs, RX_PS, IDLE);
	initializeIDLE(inputs, outputs, POKE_VGUI, RX_PS, RX_VO, RX_OP, POKE_PS, POKE_OP, IDLE, POKE_VO);
	initializePOKE_VGUI(inputs, outputs, POKE_VGUI, TX_VGUI);
	initializePOKE_PS(inputs, outputs, RX_PS, POKE_PS, TX_PS);
	initializeEND_VGUI(inputs, outputs, END_VGUI, IDLE);
	initializeTX_PS(inputs, outputs, TX_PS, END_PS);
	initializePOKE_VO(inputs, outputs, IDLE, RX_PS, POKE_VO, TX_VO);
	initializeOBSERVING_VGUI(inputs, outputs, OBSERVING_VGUI, IDLE);
	initializePOKE_OP(inputs, outputs, RX_PS, POKE_OP, TX_OP);
	initializeRX_OP(inputs, outputs, RX_OP, IDLE);
	initializeTX_VGUI(inputs, outputs, TX_VGUI, END_VGUI);
	initializeEND_PS(inputs, outputs, END_PS, IDLE);
	initializeTX_OP(inputs, outputs, TX_OP, END_OP);
	initializeRX_VO(inputs, outputs, RX_VO, IDLE);
	initializeTX_VO(inputs, outputs, TX_VO, END_VO);
	initializeEND_OP(inputs, outputs, POKE_VO, END_OP, IDLE);
	initializeEND_VO(inputs, outputs, END_VO, IDLE);
	startState(IDLE);
}
 public void initializeRX_PS(ComChannelList inputs, ComChannelList outputs, State RX_PS, State IDLE) {
	// (RX_PS,[A=PS_END_MM],[],1,NEXT,1.0)x(IDLE,[D=MM_STOP_LISTEN_TO_PS_MM],[])
	RX_PS.add(new Transition(_internal_vars, inputs, outputs, IDLE, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!ParentSearch.AUDIO_PS_MM_COMM.PS_END_MM.equals(_inputs.get(Channels.AUDIO_PS_MM_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_MM_MM_COMM.name(), MissionManager.DATA_MM_MM_COMM.MM_STOP_LISTEN_TO_PS_MM);
			return true;
		}
	});
	// (RX_PS,[A=PS_NEW_SEARCH_AOI_MM],[],1,NEXT,1.0)x(IDLE,[D=MM_STOP_LISTEN_TO_PS_MM,D=MM_START_TRANSMIT_AOI_MM,D=MM_START_SEARCH_MM],[AREA_OF_INTEREST=NEW])
	RX_PS.add(new Transition(_internal_vars, inputs, outputs, IDLE, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!ParentSearch.AUDIO_PS_MM_COMM.PS_NEW_SEARCH_AOI_MM.equals(_inputs.get(Channels.AUDIO_PS_MM_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_MM_MM_COMM.name(), MissionManager.DATA_MM_MM_COMM.MM_STOP_LISTEN_TO_PS_MM);
			setTempOutput(Channels.DATA_MM_MM_COMM.name(), MissionManager.DATA_MM_MM_COMM.MM_START_TRANSMIT_AOI_MM);
			setTempOutput(Channels.DATA_MM_MM_COMM.name(), MissionManager.DATA_MM_MM_COMM.MM_START_SEARCH_MM);
			setTempInternalVar("AREA_OF_INTEREST", "NEW");
			return true;
		}
	});
	// (RX_PS,[A=PS_TARGET_DESCRIPTION_MM],[],1,NEXT,1.0)x(IDLE,[D=MM_STOP_LISTEN_TO_PS_MM,D=MM_START_TRANSMIT_TARGET_DESCRIPTION_MM],[TARGET_DESCRIPTION=NEW])
	RX_PS.add(new Transition(_internal_vars, inputs, outputs, IDLE, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!ParentSearch.AUDIO_PS_MM_COMM.PS_TARGET_DESCRIPTION_MM.equals(_inputs.get(Channels.AUDIO_PS_MM_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_MM_MM_COMM.name(), MissionManager.DATA_MM_MM_COMM.MM_STOP_LISTEN_TO_PS_MM);
			setTempOutput(Channels.DATA_MM_MM_COMM.name(), MissionManager.DATA_MM_MM_COMM.MM_START_TRANSMIT_TARGET_DESCRIPTION_MM);
			setTempInternalVar("TARGET_DESCRIPTION", "NEW");
			return true;
		}
	});
	// (RX_PS,[A=PS_TERMINATE_SEARCH_MM],[],1,NEXT,1.0)x(IDLE,[D=MM_STOP_LISTEN_TO_PS_MM,D=MM_START_TRANSMIT_TERMINATE_SEARCH_MM],[TERMINATE_SEARCH_VO=NEW,TERMINATE_SEARCH_OP=NEW])
	RX_PS.add(new Transition(_internal_vars, inputs, outputs, IDLE, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!ParentSearch.AUDIO_PS_MM_COMM.PS_TERMINATE_SEARCH_MM.equals(_inputs.get(Channels.AUDIO_PS_MM_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_MM_MM_COMM.name(), MissionManager.DATA_MM_MM_COMM.MM_STOP_LISTEN_TO_PS_MM);
			setTempOutput(Channels.DATA_MM_MM_COMM.name(), MissionManager.DATA_MM_MM_COMM.MM_START_TRANSMIT_TERMINATE_SEARCH_MM);
			setTempInternalVar("TERMINATE_SEARCH_VO", "NEW");
			setTempInternalVar("TERMINATE_SEARCH_OP", "NEW");
			return true;
		}
	});
	add(RX_PS);
}
 public void initializeIDLE(ComChannelList inputs, ComChannelList outputs, State POKE_VGUI, State RX_PS, State RX_VO, State RX_OP, State POKE_PS, State POKE_OP, State IDLE, State POKE_VO) {
	// (IDLE,[],[TERMINATE_SEARCH_VO=NEW],4,NEXT,1.0)X(POKE_VO,[A=MM_POKE_VO],[])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_VO, Duration.NEXT.getRange(), 4, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!"NEW".equals(_internal_vars.getVariable ("TERMINATE_SEARCH_VO"))) {
				return false;
			}
			setTempOutput(Channels.AUDIO_MM_VO_COMM.name(), MissionManager.AUDIO_MM_VO_COMM.MM_POKE_VO);
			return true;
		}
	});
	// (IDLE,[],[TERMINATE_SEARCH_OP=NEW],4,NEXT,1.0)X(POKE_OP,[A=MM_POKE_OP],[])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_OP, Duration.NEXT.getRange(), 4, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!"NEW".equals(_internal_vars.getVariable ("TERMINATE_SEARCH_OP"))) {
				return false;
			}
			setTempOutput(Channels.AUDIO_MM_OP_COMM.name(), MissionManager.AUDIO_MM_OP_COMM.MM_POKE_OP);
			return true;
		}
	});
	// (IDLE,[],[TARGET_SIGHTED_F=TRUE],0,NEXT,1.0)X(POKE_PS,[A=MM_POKE_OP],[])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_PS, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("TARGET_SIGHTED_F"))) {
				return false;
			}
			setTempOutput(Channels.AUDIO_MM_OP_COMM.name(), MissionManager.AUDIO_MM_OP_COMM.MM_POKE_OP);
			return true;
		}
	});
	// (IDLE,[],[TARGET_SIGHTED_T=TRUE],0,NEXT,1.0)X(POKE_PS,[A=MM_POKE_OP],[])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_PS, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("TARGET_SIGHTED_T"))) {
				return false;
			}
			setTempOutput(Channels.AUDIO_MM_OP_COMM.name(), MissionManager.AUDIO_MM_OP_COMM.MM_POKE_OP);
			return true;
		}
	});
	// (IDLE,[A=OP_POKE_MM],[],1,NEXT,1.0)x(RX_OP,[A=MM_ACK_OP,D=MM_START_LISTEN_TO_OP_MM],[])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, RX_OP, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!Operator.AUDIO_OP_MM_COMM.OP_POKE_MM.equals(_inputs.get(Channels.AUDIO_OP_MM_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.AUDIO_MM_OP_COMM.name(), MissionManager.AUDIO_MM_OP_COMM.MM_ACK_OP);
			setTempOutput(Channels.DATA_MM_MM_COMM.name(), MissionManager.DATA_MM_MM_COMM.MM_START_LISTEN_TO_OP_MM);
			return true;
		}
	});
	// (IDLE,[A=VO_POKE_MM],[],1,NEXT,1.0)x(RX_VO,[A=MM_ACK_VO,D=MM_START_LISTEN_TO_VO_MM],[])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, RX_VO, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!VideoOperator.AUDIO_VO_MM_COMM.VO_POKE_MM.equals(_inputs.get(Channels.AUDIO_VO_MM_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.AUDIO_MM_VO_COMM.name(), MissionManager.AUDIO_MM_VO_COMM.MM_ACK_VO);
			setTempOutput(Channels.DATA_MM_MM_COMM.name(), MissionManager.DATA_MM_MM_COMM.MM_START_LISTEN_TO_VO_MM);
			return true;
		}
	});
	// (IDLE,[A=PS_POKE_MM],[],1,NEXT,1.0)x(RX_PS,[A=MM_ACK_PS,D=MM_START_LISTEN_TO_PS_MM],[])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, RX_PS, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!ParentSearch.AUDIO_PS_MM_COMM.PS_POKE_MM.equals(_inputs.get(Channels.AUDIO_PS_MM_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.AUDIO_MM_PS_COMM.name(), MissionManager.AUDIO_MM_PS_COMM.MM_ACK_PS);
			setTempOutput(Channels.DATA_MM_MM_COMM.name(), MissionManager.DATA_MM_MM_COMM.MM_START_LISTEN_TO_PS_MM);
			return true;
		}
	});
	// (IDLE,[],[TARGET_DESCRIPTION=NEW],1,NEXT,1.0)x(POKE_VO,[A=MM_POKE_VO],[])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_VO, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!"NEW".equals(_internal_vars.getVariable ("TARGET_DESCRIPTION"))) {
				return false;
			}
			setTempOutput(Channels.AUDIO_MM_VO_COMM.name(), MissionManager.AUDIO_MM_VO_COMM.MM_POKE_VO);
			return true;
		}
	});
	// (IDLE,[],[AREA_OF_INTEREST=NEW],1,NEXT,1.0)x(POKE_OP,[A=MM_POKE_OP],[])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_OP, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!"NEW".equals(_internal_vars.getVariable ("AREA_OF_INTEREST"))) {
				return false;
			}
			setTempOutput(Channels.AUDIO_MM_OP_COMM.name(), MissionManager.AUDIO_MM_OP_COMM.MM_POKE_OP);
			return true;
		}
	});
	// (IDLE,[],[ANOMALY_DISMISSED_T=TRUE],1,NEXT,1.0)x(POKE_VGUI,[],[ANOMALY_DISMISSED_T=TRUE])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_VGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("ANOMALY_DISMISSED_T"))) {
				return false;
			}
			setTempInternalVar("ANOMALY_DISMISSED_T", true);
			return true;
		}
	});
	// (IDLE,[],[ANOMALY_DISMISSED_F=TRUE],1,NEXT,1.0)x(POKE_VGUI,[],[ANOMALY_DISMISSED_F=TRUE])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_VGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("ANOMALY_DISMISSED_F"))) {
				return false;
			}
			setTempInternalVar("ANOMALY_DISMISSED_F", true);
			return true;
		}
	});
	// (IDLE,[],[FLYBY_REQ_T=TRUE],1,NEXT,1.0)x(POKE_VGUI,[],[FLYBY_REQ_T=TRUE])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_VGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_REQ_T"))) {
				return false;
			}
			setTempInternalVar("FLYBY_REQ_T", true);
			return true;
		}
	});
	// (IDLE,[],[FLYBY_REQ_F=TRUE],1,NEXT,1.0)x(POKE_VGUI,[],[FLYBY_REQ_F=TRUE])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_VGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_REQ_F"))) {
				return false;
			}
			setTempInternalVar("FLYBY_REQ_F", true);
			return true;
		}
	});
	// (IDLE,[],[TARGET_SIGHTED_F=TRUE],1,NEXT,1.0)x(POKE_PS,[A=MM_POKE_PS],[])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_PS, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("TARGET_SIGHTED_F"))) {
				return false;
			}
			setTempOutput(Channels.AUDIO_MM_PS_COMM.name(), MissionManager.AUDIO_MM_PS_COMM.MM_POKE_PS);
			return true;
		}
	});
	// (IDLE,[],[TARGET_SIGHTED_T=TRUE],1,NEXT,1.0)x(POKE_PS,[A=MM_POKE_PS],[])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_PS, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("TARGET_SIGHTED_T"))) {
				return false;
			}
			setTempOutput(Channels.AUDIO_MM_PS_COMM.name(), MissionManager.AUDIO_MM_PS_COMM.MM_POKE_PS);
			return true;
		}
	});
	// (IDLE,[],[SEARCH_COMPLETE=TRUE],1,NEXT,1.0)x(POKE_PS,[A=MM_POKE_PS],[])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_PS, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("SEARCH_COMPLETE"))) {
				return false;
			}
			setTempOutput(Channels.AUDIO_MM_PS_COMM.name(), MissionManager.AUDIO_MM_PS_COMM.MM_POKE_PS);
			return true;
		}
	});
	// (IDLE,[],[SEARCH_FAILED=TRUE],1,NEXT,1.0)x(POKE_PS,[A=MM_POKE_PS],[])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_PS, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("SEARCH_FAILED"))) {
				return false;
			}
			setTempOutput(Channels.AUDIO_MM_PS_COMM.name(), MissionManager.AUDIO_MM_PS_COMM.MM_POKE_PS);
			return true;
		}
	});
	add(IDLE);
}
 public void initializePOKE_VGUI(ComChannelList inputs, ComChannelList outputs, State POKE_VGUI, State TX_VGUI) {
	// (POKE_VGUI,[],[],1,NEXT,1.0)x(TX_VGUI,[],[])
	POKE_VGUI.add(new Transition(_internal_vars, inputs, outputs, TX_VGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			return true;
		}
	});
	add(POKE_VGUI);
}
 public void initializePOKE_PS(ComChannelList inputs, ComChannelList outputs, State RX_PS, State POKE_PS, State TX_PS) {
	// (POKE_PS,[A=PS_ACK_MM],[],1,NEXT,1.0)x(TX_PS,[],[])
	POKE_PS.add(new Transition(_internal_vars, inputs, outputs, TX_PS, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!ParentSearch.AUDIO_PS_MM_COMM.PS_ACK_MM.equals(_inputs.get(Channels.AUDIO_PS_MM_COMM.name()).value())) {
				return false;
			}
			return true;
		}
	});
	// (POKE_PS,[A=PS_POKE_MM],[],1,NEXT,1.0)X(RX_PS,[A=MM_ACK_PS],[])
	POKE_PS.add(new Transition(_internal_vars, inputs, outputs, RX_PS, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!ParentSearch.AUDIO_PS_MM_COMM.PS_POKE_MM.equals(_inputs.get(Channels.AUDIO_PS_MM_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.AUDIO_MM_PS_COMM.name(), MissionManager.AUDIO_MM_PS_COMM.MM_ACK_PS);
			return true;
		}
	});
	add(POKE_PS);
}
 public void initializeEND_VGUI(ComChannelList inputs, ComChannelList outputs, State END_VGUI, State IDLE) {
	// (END_VGUI,[],[],1,NEXT,1.0)x(IDLE,[],[])
	END_VGUI.add(new Transition(_internal_vars, inputs, outputs, IDLE, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			return true;
		}
	});
	add(END_VGUI);
}
 public void initializeTX_PS(ComChannelList inputs, ComChannelList outputs, State TX_PS, State END_PS) {
	// (TX_PS,[],[SEARCH_COMPLETE=TRUE],1,MM_TX_PS,1.0)x(END_PS,[A=MM_SEARCH_COMPLETE_PS,D=MM_STOP_TRANSMIT_SEARCH_COMPLETE_MM,D=MM_STOP_SEARCH_MM],[SEARCH_COMPLETE=FALSE])
	TX_PS.add(new Transition(_internal_vars, inputs, outputs, END_PS, Duration.MM_TX_PS.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("SEARCH_COMPLETE"))) {
				return false;
			}
			setTempOutput(Channels.AUDIO_MM_PS_COMM.name(), MissionManager.AUDIO_MM_PS_COMM.MM_SEARCH_COMPLETE_PS);
			setTempOutput(Channels.DATA_MM_MM_COMM.name(), MissionManager.DATA_MM_MM_COMM.MM_STOP_TRANSMIT_SEARCH_COMPLETE_MM);
			setTempOutput(Channels.DATA_MM_MM_COMM.name(), MissionManager.DATA_MM_MM_COMM.MM_STOP_SEARCH_MM);
			setTempInternalVar("SEARCH_COMPLETE", false);
			return true;
		}
	});
	// (TX_PS,[],[SEARCH_FAILED=TRUE],1,MM_TX_PS,1.0)x(END_PS,[A=MM_SEARCH_FAILED_PS,D=MM_STOP_SEARCH_MM],[SEARCH_FAILED=FALSE])
	TX_PS.add(new Transition(_internal_vars, inputs, outputs, END_PS, Duration.MM_TX_PS.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("SEARCH_FAILED"))) {
				return false;
			}
			setTempOutput(Channels.AUDIO_MM_PS_COMM.name(), MissionManager.AUDIO_MM_PS_COMM.MM_SEARCH_FAILED_PS);
			setTempOutput(Channels.DATA_MM_MM_COMM.name(), MissionManager.DATA_MM_MM_COMM.MM_STOP_SEARCH_MM);
			setTempInternalVar("SEARCH_FAILED", false);
			return true;
		}
	});
	// (TX_PS,[],[TARGET_SIGHTED_T=TRUE],1,MM_TX_PS,1.0)x(END_PS,[A=MM_TARGET_SIGHTED_T_PS,D=MM_STOP_SEARCH_MM],[TARGET_SIGHTED_T=FALSE])
	TX_PS.add(new Transition(_internal_vars, inputs, outputs, END_PS, Duration.MM_TX_PS.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("TARGET_SIGHTED_T"))) {
				return false;
			}
			setTempOutput(Channels.AUDIO_MM_PS_COMM.name(), MissionManager.AUDIO_MM_PS_COMM.MM_TARGET_SIGHTED_T_PS);
			setTempOutput(Channels.DATA_MM_MM_COMM.name(), MissionManager.DATA_MM_MM_COMM.MM_STOP_SEARCH_MM);
			setTempInternalVar("TARGET_SIGHTED_T", false);
			return true;
		}
	});
	// (TX_PS,[],[TARGET_SIGHTED_F=TRUE],1,MM_TX_PS,1.0)x(END_PS,[A=MM_TARGET_SIGHTED_F_PS,D=MM_STOP_SEARCH_MM],[TARGET_SIGHTED_F=FALSE])
	TX_PS.add(new Transition(_internal_vars, inputs, outputs, END_PS, Duration.MM_TX_PS.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("TARGET_SIGHTED_F"))) {
				return false;
			}
			setTempOutput(Channels.AUDIO_MM_PS_COMM.name(), MissionManager.AUDIO_MM_PS_COMM.MM_TARGET_SIGHTED_F_PS);
			setTempOutput(Channels.DATA_MM_MM_COMM.name(), MissionManager.DATA_MM_MM_COMM.MM_STOP_SEARCH_MM);
			setTempInternalVar("TARGET_SIGHTED_F", false);
			return true;
		}
	});
	// (TX_PS,[],[],0,MM_TX_PS,1.0)X(END_PS,[A=MM_END_PS],[])
	TX_PS.add(new Transition(_internal_vars, inputs, outputs, END_PS, Duration.MM_TX_PS.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			setTempOutput(Channels.AUDIO_MM_PS_COMM.name(), MissionManager.AUDIO_MM_PS_COMM.MM_END_PS);
			return true;
		}
	});
	add(TX_PS);
}
 public void initializePOKE_VO(ComChannelList inputs, ComChannelList outputs, State IDLE, State RX_PS, State POKE_VO, State TX_VO) {
	// (POKE_VO,[A=VO_ACK_MM],[],1,NEXT,1.0)x(TX_VO,[],[])
	POKE_VO.add(new Transition(_internal_vars, inputs, outputs, TX_VO, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!VideoOperator.AUDIO_VO_MM_COMM.VO_ACK_MM.equals(_inputs.get(Channels.AUDIO_VO_MM_COMM.name()).value())) {
				return false;
			}
			return true;
		}
	});
	// (POKE_VO,[A=PS_POKE_MM],[],1,NEXT,1.0)X(RX_PS,[A=MM_ACK_PS],[])
	POKE_VO.add(new Transition(_internal_vars, inputs, outputs, RX_PS, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!ParentSearch.AUDIO_PS_MM_COMM.PS_POKE_MM.equals(_inputs.get(Channels.AUDIO_PS_MM_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.AUDIO_MM_PS_COMM.name(), MissionManager.AUDIO_MM_PS_COMM.MM_ACK_PS);
			return true;
		}
	});
	// (POKE_VO,[],[],0,MM_POKE_VO,1.0)X(IDLE,[],[])
	POKE_VO.add(new Transition(_internal_vars, inputs, outputs, IDLE, Duration.MM_POKE_VO.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			return true;
		}
	});
	add(POKE_VO);
}
 public void initializeOBSERVING_VGUI(ComChannelList inputs, ComChannelList outputs, State OBSERVING_VGUI, State IDLE) {
	// (OBSERVING_VGUI,[],[],1,MM_TO_IDLE,1.0)x(IDLE,[D=MM_STOP_VERIFICATION_MM],[])
	OBSERVING_VGUI.add(new Transition(_internal_vars, inputs, outputs, IDLE, Duration.MM_TO_IDLE.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			setTempOutput(Channels.DATA_MM_MM_COMM.name(), MissionManager.DATA_MM_MM_COMM.MM_STOP_VERIFICATION_MM);
			return true;
		}
	});
	// (OBSERVING_VGUI,[V=VGUI_VALIDATION_REQ_T_MM],[],1,NEXT,1.0)x(IDLE,[],[FLYBY_REQ_T=TRUE])
	OBSERVING_VGUI.add(new Transition(_internal_vars, inputs, outputs, IDLE, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!VideoOperatorGui.VIDEO_VGUI_MM_COMM.VGUI_VALIDATION_REQ_T_MM.equals(_inputs.get(Channels.VIDEO_VGUI_MM_COMM.name()).value())) {
				return false;
			}
			setTempInternalVar("FLYBY_REQ_T", true);
			return true;
		}
	});
	// (OBSERVING_VGUI,[V=VGUI_VALIDATION_REQ_T_MM],[],1,NEXT,1.0)x(IDLE,[],[ANOMALY_DISMISSED_T=TRUE])
	OBSERVING_VGUI.add(new Transition(_internal_vars, inputs, outputs, IDLE, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!VideoOperatorGui.VIDEO_VGUI_MM_COMM.VGUI_VALIDATION_REQ_T_MM.equals(_inputs.get(Channels.VIDEO_VGUI_MM_COMM.name()).value())) {
				return false;
			}
			setTempInternalVar("ANOMALY_DISMISSED_T", true);
			return true;
		}
	});
	// (OBSERVING_VGUI,[V=VGUI_VALIDATION_REQ_F_MM],[],1,NEXT,1.0)x(IDLE,[],[FLYBY_REQ_F=TRUE])
	OBSERVING_VGUI.add(new Transition(_internal_vars, inputs, outputs, IDLE, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!VideoOperatorGui.VIDEO_VGUI_MM_COMM.VGUI_VALIDATION_REQ_F_MM.equals(_inputs.get(Channels.VIDEO_VGUI_MM_COMM.name()).value())) {
				return false;
			}
			setTempInternalVar("FLYBY_REQ_F", true);
			return true;
		}
	});
	// (OBSERVING_VGUI,[V=VGUI_VALIDATION_REQ_F_MM],[],1,NEXT,1.0)x(IDLE,[],[ANOMALY_DISMISSED_F=TRUE])
	OBSERVING_VGUI.add(new Transition(_internal_vars, inputs, outputs, IDLE, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!VideoOperatorGui.VIDEO_VGUI_MM_COMM.VGUI_VALIDATION_REQ_F_MM.equals(_inputs.get(Channels.VIDEO_VGUI_MM_COMM.name()).value())) {
				return false;
			}
			setTempInternalVar("ANOMALY_DISMISSED_F", true);
			return true;
		}
	});
	add(OBSERVING_VGUI);
}
 public void initializePOKE_OP(ComChannelList inputs, ComChannelList outputs, State RX_PS, State POKE_OP, State TX_OP) {
	// (POKE_OP,[A=OP_ACK_MM],[],1,NEXT,1.0)x(TX_OP,[],[])
	POKE_OP.add(new Transition(_internal_vars, inputs, outputs, TX_OP, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!Operator.AUDIO_OP_MM_COMM.OP_ACK_MM.equals(_inputs.get(Channels.AUDIO_OP_MM_COMM.name()).value())) {
				return false;
			}
			return true;
		}
	});
	// (POKE_OP,[A=PS_POKE_MM],[],2,NEXT,1.0)x(RX_PS,[A=MM_END_OP,A=MM_ACK_PS],[])
	POKE_OP.add(new Transition(_internal_vars, inputs, outputs, RX_PS, Duration.NEXT.getRange(), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!ParentSearch.AUDIO_PS_MM_COMM.PS_POKE_MM.equals(_inputs.get(Channels.AUDIO_PS_MM_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.AUDIO_MM_OP_COMM.name(), MissionManager.AUDIO_MM_OP_COMM.MM_END_OP);
			setTempOutput(Channels.AUDIO_MM_PS_COMM.name(), MissionManager.AUDIO_MM_PS_COMM.MM_ACK_PS);
			return true;
		}
	});
	add(POKE_OP);
}
 public void initializeRX_OP(ComChannelList inputs, ComChannelList outputs, State RX_OP, State IDLE) {
	// (RX_OP,[A=OP_SEARCH_COMPLETE_MM],[],1,NEXT,1.0)x(IDLE,[D=MM_STOP_LISTEN_TO_OP_MM,D=MM_START_TRANSMIT_SEARCH_COMPLETE_MM],[SEARCH_COMPLETE=TRUE])
	RX_OP.add(new Transition(_internal_vars, inputs, outputs, IDLE, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!Operator.AUDIO_OP_MM_COMM.OP_SEARCH_COMPLETE_MM.equals(_inputs.get(Channels.AUDIO_OP_MM_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_MM_MM_COMM.name(), MissionManager.DATA_MM_MM_COMM.MM_STOP_LISTEN_TO_OP_MM);
			setTempOutput(Channels.DATA_MM_MM_COMM.name(), MissionManager.DATA_MM_MM_COMM.MM_START_TRANSMIT_SEARCH_COMPLETE_MM);
			setTempInternalVar("SEARCH_COMPLETE", true);
			return true;
		}
	});
	// (RX_OP,[A=OP_SEARCH_FAILED_MM],[],1,NEXT,1.0)x(IDLE,[D=MM_STOP_LISTEN_TO_OP_MM],[SEARCH_FAILED=TRUE])
	RX_OP.add(new Transition(_internal_vars, inputs, outputs, IDLE, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!Operator.AUDIO_OP_MM_COMM.OP_SEARCH_FAILED_MM.equals(_inputs.get(Channels.AUDIO_OP_MM_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_MM_MM_COMM.name(), MissionManager.DATA_MM_MM_COMM.MM_STOP_LISTEN_TO_OP_MM);
			setTempInternalVar("SEARCH_FAILED", true);
			return true;
		}
	});
	add(RX_OP);
}
 public void initializeTX_VGUI(ComChannelList inputs, ComChannelList outputs, State TX_VGUI, State END_VGUI) {
	// (TX_VGUI,[],[FLYBY_REQ_T=TRUE],1,MM_TX_VGUI,1.0)x(END_VGUI,[D=MM_FLYBY_REQ_T_VGUI,D=MM_STOP_VERIFICATION_MM],[FLYBY_REQ_T=FALSE])
	TX_VGUI.add(new Transition(_internal_vars, inputs, outputs, END_VGUI, Duration.MM_TX_VGUI.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_REQ_T"))) {
				return false;
			}
			setTempOutput(Channels.DATA_MM_VGUI_COMM.name(), MissionManager.DATA_MM_VGUI_COMM.MM_FLYBY_REQ_T_VGUI);
			setTempOutput(Channels.DATA_MM_MM_COMM.name(), MissionManager.DATA_MM_MM_COMM.MM_STOP_VERIFICATION_MM);
			setTempInternalVar("FLYBY_REQ_T", false);
			return true;
		}
	});
	// (TX_VGUI,[],[FLYBY_REQ_F=TRUE],1,MM_TX_VGUI,1.0)x(END_VGUI,[D=MM_FLYBY_REQ_F_VGUI,D=MM_STOP_VERIFICATION_MM],[FLYBY_REQ_F=FALSE])
	TX_VGUI.add(new Transition(_internal_vars, inputs, outputs, END_VGUI, Duration.MM_TX_VGUI.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_REQ_F"))) {
				return false;
			}
			setTempOutput(Channels.DATA_MM_VGUI_COMM.name(), MissionManager.DATA_MM_VGUI_COMM.MM_FLYBY_REQ_F_VGUI);
			setTempOutput(Channels.DATA_MM_MM_COMM.name(), MissionManager.DATA_MM_MM_COMM.MM_STOP_VERIFICATION_MM);
			setTempInternalVar("FLYBY_REQ_F", false);
			return true;
		}
	});
	// (TX_VGUI,[],[ANOMALY_DISMISSED_T=TRUE],1,MM_TX_VGUI,1.0)x(END_VGUI,[D=MM_ANOMALY_DISMISSED_T_VGUI,D=MM_STOP_VERIFICATION_MM],[ANOMALY_DISMISSED_T=FALSE])
	TX_VGUI.add(new Transition(_internal_vars, inputs, outputs, END_VGUI, Duration.MM_TX_VGUI.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("ANOMALY_DISMISSED_T"))) {
				return false;
			}
			setTempOutput(Channels.DATA_MM_VGUI_COMM.name(), MissionManager.DATA_MM_VGUI_COMM.MM_ANOMALY_DISMISSED_T_VGUI);
			setTempOutput(Channels.DATA_MM_MM_COMM.name(), MissionManager.DATA_MM_MM_COMM.MM_STOP_VERIFICATION_MM);
			setTempInternalVar("ANOMALY_DISMISSED_T", false);
			return true;
		}
	});
	// (TX_VGUI,[],[ANOMALY_DISMISSED_F=TRUE],1,MM_TX_VGUI,1.0)x(END_VGUI,[D=MM_ANOMALY_DISMISSED_F_VGUI,D=MM_STOP_VERIFICATION_MM],[ANOMALY_DISMISSED_F=FALSE])
	TX_VGUI.add(new Transition(_internal_vars, inputs, outputs, END_VGUI, Duration.MM_TX_VGUI.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("ANOMALY_DISMISSED_F"))) {
				return false;
			}
			setTempOutput(Channels.DATA_MM_VGUI_COMM.name(), MissionManager.DATA_MM_VGUI_COMM.MM_ANOMALY_DISMISSED_F_VGUI);
			setTempOutput(Channels.DATA_MM_MM_COMM.name(), MissionManager.DATA_MM_MM_COMM.MM_STOP_VERIFICATION_MM);
			setTempInternalVar("ANOMALY_DISMISSED_F", false);
			return true;
		}
	});
	add(TX_VGUI);
}
 public void initializeEND_PS(ComChannelList inputs, ComChannelList outputs, State END_PS, State IDLE) {
	// (END_PS,[],[],1,NEXT,1.0)x(IDLE,[],[])
	END_PS.add(new Transition(_internal_vars, inputs, outputs, IDLE, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			return true;
		}
	});
	add(END_PS);
}
 public void initializeTX_OP(ComChannelList inputs, ComChannelList outputs, State TX_OP, State END_OP) {
	// (TX_OP,[],[AREA_OF_INTEREST=NEW],1,MM_TX_OP,1.0)x(END_OP,[A=MM_NEW_SEARCH_AOI_OP,D=MM_STOP_TRANSMIT_AOI_MM],[AREA_OF_INTEREST=CURRENT])
	TX_OP.add(new Transition(_internal_vars, inputs, outputs, END_OP, Duration.MM_TX_OP.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!"NEW".equals(_internal_vars.getVariable ("AREA_OF_INTEREST"))) {
				return false;
			}
			setTempOutput(Channels.AUDIO_MM_OP_COMM.name(), MissionManager.AUDIO_MM_OP_COMM.MM_NEW_SEARCH_AOI_OP);
			setTempOutput(Channels.DATA_MM_MM_COMM.name(), MissionManager.DATA_MM_MM_COMM.MM_STOP_TRANSMIT_AOI_MM);
			setTempInternalVar("AREA_OF_INTEREST", "CURRENT");
			return true;
		}
	});
	// (TX_OP,[],[TERMINATE_SEARCH_OP=NEW],1,MM_TX_OP,1.0)x(END_OP,[A=MM_TERMINATE_SEARCH_OP],[TERMINATE_SEARCH_OP=CURRENT])
	TX_OP.add(new Transition(_internal_vars, inputs, outputs, END_OP, Duration.MM_TX_OP.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!"NEW".equals(_internal_vars.getVariable ("TERMINATE_SEARCH_OP"))) {
				return false;
			}
			setTempOutput(Channels.AUDIO_MM_OP_COMM.name(), MissionManager.AUDIO_MM_OP_COMM.MM_TERMINATE_SEARCH_OP);
			setTempInternalVar("TERMINATE_SEARCH_OP", "CURRENT");
			return true;
		}
	});
	add(TX_OP);
}
 public void initializeRX_VO(ComChannelList inputs, ComChannelList outputs, State RX_VO, State IDLE) {
	// (RX_VO,[A=VO_TARGET_SIGHTED_F_MM],[],1,NEXT,1.0)x(IDLE,[D=MM_STOP_LISTEN_TO_VO_MM],[TARGET_SIGHTED_F=TRUE])
	RX_VO.add(new Transition(_internal_vars, inputs, outputs, IDLE, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!VideoOperator.AUDIO_VO_MM_COMM.VO_TARGET_SIGHTED_F_MM.equals(_inputs.get(Channels.AUDIO_VO_MM_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_MM_MM_COMM.name(), MissionManager.DATA_MM_MM_COMM.MM_STOP_LISTEN_TO_VO_MM);
			setTempInternalVar("TARGET_SIGHTED_F", true);
			return true;
		}
	});
	// (RX_VO,[A=VO_TARGET_SIGHTED_T_MM],[],1,NEXT,1.0)x(IDLE,[D=MM_STOP_LISTEN_TO_VO_MM],[TARGET_SIGHTED_T=TRUE])
	RX_VO.add(new Transition(_internal_vars, inputs, outputs, IDLE, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!VideoOperator.AUDIO_VO_MM_COMM.VO_TARGET_SIGHTED_T_MM.equals(_inputs.get(Channels.AUDIO_VO_MM_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_MM_MM_COMM.name(), MissionManager.DATA_MM_MM_COMM.MM_STOP_LISTEN_TO_VO_MM);
			setTempInternalVar("TARGET_SIGHTED_T", true);
			return true;
		}
	});
	add(RX_VO);
}
 public void initializeTX_VO(ComChannelList inputs, ComChannelList outputs, State TX_VO, State END_VO) {
	// (TX_VO,[],[TARGET_DESCRIPTION=NEW],1,MM_TX_VO,1.0)x(END_VO,[A=MM_TARGET_DESCRIPTION_VO,D=MM_STOP_TRANSMIT_TARGET_DESCRIPTION_MM],[TARGET_DESCRIPTION=CURRENT])
	TX_VO.add(new Transition(_internal_vars, inputs, outputs, END_VO, Duration.MM_TX_VO.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!"NEW".equals(_internal_vars.getVariable ("TARGET_DESCRIPTION"))) {
				return false;
			}
			setTempOutput(Channels.AUDIO_MM_VO_COMM.name(), MissionManager.AUDIO_MM_VO_COMM.MM_TARGET_DESCRIPTION_VO);
			setTempOutput(Channels.DATA_MM_MM_COMM.name(), MissionManager.DATA_MM_MM_COMM.MM_STOP_TRANSMIT_TARGET_DESCRIPTION_MM);
			setTempInternalVar("TARGET_DESCRIPTION", "CURRENT");
			return true;
		}
	});
	// (TX_VO,[],[TERMINATE_SEARCH_VO=NEW],1,MM_TX_VO,1.0)x(END_VO,[A=MM_TERMINATE_SEARCH_VO,A=MM_END_VO],[TERMINATE_SEARCH_VO=CURRENT])
	TX_VO.add(new Transition(_internal_vars, inputs, outputs, END_VO, Duration.MM_TX_VO.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!"NEW".equals(_internal_vars.getVariable ("TERMINATE_SEARCH_VO"))) {
				return false;
			}
			setTempOutput(Channels.AUDIO_MM_VO_COMM.name(), MissionManager.AUDIO_MM_VO_COMM.MM_TERMINATE_SEARCH_VO);
			setTempOutput(Channels.AUDIO_MM_VO_COMM.name(), MissionManager.AUDIO_MM_VO_COMM.MM_END_VO);
			setTempInternalVar("TERMINATE_SEARCH_VO", "CURRENT");
			return true;
		}
	});
	add(TX_VO);
}
 public void initializeEND_OP(ComChannelList inputs, ComChannelList outputs, State POKE_VO, State END_OP, State IDLE) {
	// (END_OP,[],[],1,NEXT,1.0)x(IDLE,[],[])
	END_OP.add(new Transition(_internal_vars, inputs, outputs, IDLE, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			return true;
		}
	});
	// (END_OP,[],[TARGET_DESCRIPTION=NEW],2,NEXT,1.0)x(POKE_VO,[],[])
	END_OP.add(new Transition(_internal_vars, inputs, outputs, POKE_VO, Duration.NEXT.getRange(), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!"NEW".equals(_internal_vars.getVariable ("TARGET_DESCRIPTION"))) {
				return false;
			}
			return true;
		}
	});
	add(END_OP);
}
 public void initializeEND_VO(ComChannelList inputs, ComChannelList outputs, State END_VO, State IDLE) {
	// (END_VO,[],[],1,NEXT,1.0)x(IDLE,[],[])
	END_VO.add(new Transition(_internal_vars, inputs, outputs, IDLE, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			return true;
		}
	});
	add(END_VO);
}
@Override
protected void initializeInternalVariables() {
	_internal_vars.addVariable("TERMINATE_SEARCH_VO", "");
	_internal_vars.addVariable("TERMINATE_SEARCH_OP", "");
	_internal_vars.addVariable("TARGET_SIGHTED_F", false);
	_internal_vars.addVariable("TARGET_SIGHTED_T", false);
	_internal_vars.addVariable("TARGET_DESCRIPTION", "");
	_internal_vars.addVariable("AREA_OF_INTEREST", "");
	_internal_vars.addVariable("ANOMALY_DISMISSED_T", false);
	_internal_vars.addVariable("ANOMALY_DISMISSED_F", false);
	_internal_vars.addVariable("FLYBY_REQ_T", false);
	_internal_vars.addVariable("FLYBY_REQ_F", false);
	_internal_vars.addVariable("SEARCH_COMPLETE", false);
	_internal_vars.addVariable("SEARCH_FAILED", false);
}
}