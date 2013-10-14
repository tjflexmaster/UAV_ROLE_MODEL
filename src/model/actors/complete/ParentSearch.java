package model.actors.complete;

import model.actors.MissionManager;
import model.team.*;
import simulator.*;

public class ParentSearch extends Actor {
public enum AUDIO_PS_MM_COMM{
	PS_NEW_SEARCH_AOI_MM,
	PS_TARGET_DESCRIPTION_MM,
	PS_TERMINATE_SEARCH_MM,
	PS_END_MM,
	PS_POKE_MM,
	PS_ACK_MM,
}
public ParentSearch(ComChannelList inputs, ComChannelList outputs) {
	State END_MM = new State("END_MM");
	State TX_MM = new State("TX_MM");
	State POKE_MM = new State("POKE_MM");
	State IDLE = new State("IDLE");
	State RX_MM = new State("RX_MM");
	initializeIDLE(inputs, outputs, RX_MM, IDLE, POKE_MM);
	initializeTX_MM(inputs, outputs, TX_MM, END_MM);
	initializeEND_MM(inputs, outputs, END_MM, IDLE);
	initializePOKE_MM(inputs, outputs, IDLE, POKE_MM, TX_MM);
	initializeRX_MM(inputs, outputs, RX_MM, IDLE);
}
 public void initializeIDLE(ComChannelList inputs, ComChannelList outputs, State RX_MM, State IDLE, State POKE_MM) {
	// (IDLE,[E=NEW_SEARCH_EVENT],[],1,NEXT,1.0)x(POKE_MM,[A=PS_POKE_MM],[SEARCH_ACTIVE=++])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_MM, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(_inputs.get(Channels.NEW_SEARCH_EVENT.name()).value() == null && !(Boolean)_inputs.get(Channels.NEW_SEARCH_EVENT.name()).value()) {
				return false;
			}
			setTempOutput(Channels.AUDIO_PS_MM_COMM.name(), ParentSearch.AUDIO_PS_MM_COMM.PS_POKE_MM);
			setTempInternalVar("SEARCH_ACTIVE", (Integer)_internal_vars.getVariable("++") + 1);
			return true;
		}
	});
	// (IDLE,[E=NEW_SEARCH_AREA_EVENT],[SEARCH_ACTIVE=FALSE],1,NEXT,1.0)x(POKE_MM,[],[SEARCH_ACTIVE=TRUE,NEW_SEARCH_AOI=++])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_MM, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(_inputs.get(Channels.NEW_SEARCH_AREA_EVENT.name()).value() == null && !(Boolean)_inputs.get(Channels.NEW_SEARCH_AREA_EVENT.name()).value()) {
				return false;
			}
			if(!new Boolean(false).equals(_internal_vars.getVariable ("SEARCH_ACTIVE"))) {
				return false;
			}
			setTempInternalVar("SEARCH_ACTIVE", true);
			setTempInternalVar("NEW_SEARCH_AOI", (Integer)_internal_vars.getVariable("++") + 1);
			return true;
		}
	});
	// (IDLE,[E=NEW_TARGET_DESCRIPTION_EVENT],[SEARCH_ACTIVE=FALSE],1,NEXT,1.0)x(POKE_MM,[A=PS_POKE_MM],[SEARCH_ACTIVE=TRUE,NEW_TARGET_DESCRIPTION=++])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_MM, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(_inputs.get(Channels.NEW_TARGET_DESCRIPTION_EVENT.name()).value() == null && !(Boolean)_inputs.get(Channels.NEW_TARGET_DESCRIPTION_EVENT.name()).value()) {
				return false;
			}
			if(!new Boolean(false).equals(_internal_vars.getVariable ("SEARCH_ACTIVE"))) {
				return false;
			}
			setTempOutput(Channels.AUDIO_PS_MM_COMM.name(), ParentSearch.AUDIO_PS_MM_COMM.PS_POKE_MM);
			setTempInternalVar("SEARCH_ACTIVE", true);
			setTempInternalVar("NEW_TARGET_DESCRIPTION", (Integer)_internal_vars.getVariable("++") + 1);
			return true;
		}
	});
	// (IDLE,[E=TERMINATE_SEARCH_EVENT],[SEARCH_ACTIVE=FALSE],1,NEXT,1.0)x(POKE_MM,[A=PS_POKE_MM],[SEARCH_ACTIVE=TRUE,NEW_TERMINATE_SEARCH=++])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_MM, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(_inputs.get(Channels.TERMINATE_SEARCH_EVENT.name()).value() == null && !(Boolean)_inputs.get(Channels.TERMINATE_SEARCH_EVENT.name()).value()) {
				return false;
			}
			if(!new Boolean(false).equals(_internal_vars.getVariable ("SEARCH_ACTIVE"))) {
				return false;
			}
			setTempOutput(Channels.AUDIO_PS_MM_COMM.name(), ParentSearch.AUDIO_PS_MM_COMM.PS_POKE_MM);
			setTempInternalVar("SEARCH_ACTIVE", true);
			setTempInternalVar("NEW_TERMINATE_SEARCH", (Integer)_internal_vars.getVariable("++") + 1);
			return true;
		}
	});
	// (IDLE,[],[NEW_TERMINATE_SEARCH>0],1,NEXT,1.0)x(POKE_MM,[A=PS_POKE_MM],[])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_MM, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(_internal_vars.getVariable("NEW_TERMINATE_SEARCH") instanceof Integer && new Integer(0) > (Integer) _internal_vars.getVariable ("NEW_TERMINATE_SEARCH")) {
				return false;
			}
			setTempOutput(Channels.AUDIO_PS_MM_COMM.name(), ParentSearch.AUDIO_PS_MM_COMM.PS_POKE_MM);
			return true;
		}
	});
	// (IDLE,[],[NEW_SEARCH_AOI>0],1,NEXT,1.0)x(POKE_MM,[A=PS_POKE_MM],[])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_MM, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(_internal_vars.getVariable("NEW_SEARCH_AOI") instanceof Integer && new Integer(0) > (Integer) _internal_vars.getVariable ("NEW_SEARCH_AOI")) {
				return false;
			}
			setTempOutput(Channels.AUDIO_PS_MM_COMM.name(), ParentSearch.AUDIO_PS_MM_COMM.PS_POKE_MM);
			return true;
		}
	});
	// (IDLE,[],[NEW_TARGET_DESCRIPTION>0],1,NEXT,1.0)x(POKE_MM,[A=PS_POKE_MM],[])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_MM, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(_internal_vars.getVariable("NEW_TARGET_DESCRIPTION") instanceof Integer && new Integer(0) > (Integer) _internal_vars.getVariable ("NEW_TARGET_DESCRIPTION")) {
				return false;
			}
			setTempOutput(Channels.AUDIO_PS_MM_COMM.name(), ParentSearch.AUDIO_PS_MM_COMM.PS_POKE_MM);
			return true;
		}
	});
	// (IDLE,[A=MM_POKE_PS],[],1,NEXT,1.0)x(RX_MM,[A=PS_ACK_MM],[])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, RX_MM, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!MissionManager.AUDIO_MM_PS_COMM.MM_POKE_PS.equals(_inputs.get(Channels.AUDIO_MM_PS_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.AUDIO_PS_MM_COMM.name(), ParentSearch.AUDIO_PS_MM_COMM.PS_ACK_MM);
			return true;
		}
	});
	add(IDLE);
}
 public void initializeTX_MM(ComChannelList inputs, ComChannelList outputs, State TX_MM, State END_MM) {
	// (TX_MM,[],[NEW_SEARCH_AOI>0],1,PS_TX_MM,1.0)x(END_MM,[A=PS_NEW_SEARCH_AOI_MM],[NEW_SEARCH_AOI=--])
	TX_MM.add(new Transition(_internal_vars, inputs, outputs, END_MM, Duration.PS_TX_MM.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(_internal_vars.getVariable("NEW_SEARCH_AOI") instanceof Integer && new Integer(0) > (Integer) _internal_vars.getVariable ("NEW_SEARCH_AOI")) {
				return false;
			}
			setTempOutput(Channels.AUDIO_PS_MM_COMM.name(), ParentSearch.AUDIO_PS_MM_COMM.PS_NEW_SEARCH_AOI_MM);
			setTempInternalVar("NEW_SEARCH_AOI", (Integer)_internal_vars.getVariable("--") - 1);
			return true;
		}
	});
	// (TX_MM,[],[NEW_TARGET_DESCRIPTION>0],1,PS_TX_MM,1.0)x(END_MM,[A=PS_TARGET_DESCRIPTION_MM],[NEW_TARGET_DESCRIPTION=--])
	TX_MM.add(new Transition(_internal_vars, inputs, outputs, END_MM, Duration.PS_TX_MM.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(_internal_vars.getVariable("NEW_TARGET_DESCRIPTION") instanceof Integer && new Integer(0) > (Integer) _internal_vars.getVariable ("NEW_TARGET_DESCRIPTION")) {
				return false;
			}
			setTempOutput(Channels.AUDIO_PS_MM_COMM.name(), ParentSearch.AUDIO_PS_MM_COMM.PS_TARGET_DESCRIPTION_MM);
			setTempInternalVar("NEW_TARGET_DESCRIPTION", (Integer)_internal_vars.getVariable("--") - 1);
			return true;
		}
	});
	// (TX_MM,[],[NEW_TERMINATE_SEARCH>0],1,PS_TX_MM,1.0)x(END_MM,[A=PS_TERMINATE_SEARCH_MM],[NEW_TERMINATE_SEARCH=--])
	TX_MM.add(new Transition(_internal_vars, inputs, outputs, END_MM, Duration.PS_TX_MM.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(_internal_vars.getVariable("NEW_TERMINATE_SEARCH") instanceof Integer && new Integer(0) > (Integer) _internal_vars.getVariable ("NEW_TERMINATE_SEARCH")) {
				return false;
			}
			setTempOutput(Channels.AUDIO_PS_MM_COMM.name(), ParentSearch.AUDIO_PS_MM_COMM.PS_TERMINATE_SEARCH_MM);
			setTempInternalVar("NEW_TERMINATE_SEARCH", (Integer)_internal_vars.getVariable("--") - 1);
			return true;
		}
	});
	// (TX_MM,[],[],0,PS_TX_MM,1.0)x(END_MM,[A=PS_END_MM],[])
	TX_MM.add(new Transition(_internal_vars, inputs, outputs, END_MM, Duration.PS_TX_MM.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			setTempOutput(Channels.AUDIO_PS_MM_COMM.name(), ParentSearch.AUDIO_PS_MM_COMM.PS_END_MM);
			return true;
		}
	});
	add(TX_MM);
}
 public void initializeEND_MM(ComChannelList inputs, ComChannelList outputs, State END_MM, State IDLE) {
	// (END_MM,[],[],1,NEXT,1.0)x(IDLE,[],[])
	END_MM.add(new Transition(_internal_vars, inputs, outputs, IDLE, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			return true;
		}
	});
	add(END_MM);
}
 public void initializePOKE_MM(ComChannelList inputs, ComChannelList outputs, State IDLE, State POKE_MM, State TX_MM) {
	// (POKE_MM,[A=MM_ACK_PS],[],1,NEXT,1.0)x(TX_MM,[],[])
	POKE_MM.add(new Transition(_internal_vars, inputs, outputs, TX_MM, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!MissionManager.AUDIO_MM_PS_COMM.MM_ACK_PS.equals(_inputs.get(Channels.AUDIO_MM_PS_COMM.name()).value())) {
				return false;
			}
			return true;
		}
	});
	// (POKE_MM,[],[],1,PS_POKE_MM,1.0)x(IDLE,[],[])
	POKE_MM.add(new Transition(_internal_vars, inputs, outputs, IDLE, Duration.PS_POKE_MM.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			return true;
		}
	});
	add(POKE_MM);
}
 public void initializeRX_MM(ComChannelList inputs, ComChannelList outputs, State RX_MM, State IDLE) {
	// (RX_MM,[A=MM_SEARCH_COMPLETE_PS],[],1,NEXT,1.0)x(IDLE,[],[SEARCH_COMPLETE=TRUE,SEARCH_ACTIVE=TRUE])
	RX_MM.add(new Transition(_internal_vars, inputs, outputs, IDLE, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!MissionManager.AUDIO_MM_PS_COMM.MM_SEARCH_COMPLETE_PS.equals(_inputs.get(Channels.AUDIO_MM_PS_COMM.name()).value())) {
				return false;
			}
			setTempInternalVar("SEARCH_COMPLETE", true);
			setTempInternalVar("SEARCH_ACTIVE", true);
			return true;
		}
	});
	// (RX_MM,[A=MM_SEARCH_FAILED_PS],[],1,NEXT,1.0)x(IDLE,[],[SEARCH_COMPLETE=TRUE,SEARCH_ACTIVE=TRUE])
	RX_MM.add(new Transition(_internal_vars, inputs, outputs, IDLE, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!MissionManager.AUDIO_MM_PS_COMM.MM_SEARCH_FAILED_PS.equals(_inputs.get(Channels.AUDIO_MM_PS_COMM.name()).value())) {
				return false;
			}
			setTempInternalVar("SEARCH_COMPLETE", true);
			setTempInternalVar("SEARCH_ACTIVE", true);
			return true;
		}
	});
	// (RX_MM,[A=MM_TARGET_SIGHTED_T_PS],[],1,NEXT,1.0)x(IDLE,[],[TARGET_FOUND=TRUE])
	RX_MM.add(new Transition(_internal_vars, inputs, outputs, IDLE, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!MissionManager.AUDIO_MM_PS_COMM.MM_TARGET_SIGHTED_T_PS.equals(_inputs.get(Channels.AUDIO_MM_PS_COMM.name()).value())) {
				return false;
			}
			setTempInternalVar("TARGET_FOUND", true);
			return true;
		}
	});
	// (RX_MM,[A=MM_TARGET_SIGHTED_F_PS],[],1,NEXT,1.0)x(IDLE,[],[TARGET_FOUND=TRUE])
	RX_MM.add(new Transition(_internal_vars, inputs, outputs, IDLE, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!MissionManager.AUDIO_MM_PS_COMM.MM_TARGET_SIGHTED_F_PS.equals(_inputs.get(Channels.AUDIO_MM_PS_COMM.name()).value())) {
				return false;
			}
			setTempInternalVar("TARGET_FOUND", true);
			return true;
		}
	});
	add(RX_MM);
}
@Override
protected void initializeInternalVariables() {
	_internal_vars.addVariable("SEARCH_COMPLETE", false);
	_internal_vars.addVariable("SEARCH_ACTIVE", false);
	_internal_vars.addVariable("TARGET_FOUND", false);
	_internal_vars.addVariable("NEW_SEARCH_AOI", 0);
	_internal_vars.addVariable("NEW_TARGET_DESCRIPTION", 0);
	_internal_vars.addVariable("NEW_TERMINATE_SEARCH", 0);
}
}