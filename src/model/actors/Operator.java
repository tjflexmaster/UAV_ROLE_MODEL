package model.actors;

import model.team.*;
import simulator.*;

public class Operator extends Actor {
public enum AUDIO_OP_MM_COMM{
	OP_ACK_MM,
	OP_POKE_MM,
	OP_SEARCH_FAILED_MM,
	OP_SEARCH_COMPLETE_MM,
}
public enum DATA_OP_UAV_COMM{
	OP_TAKE_OFF_UAV,
	OP_POST_FLIGHT_COMPLETE_UAV,
}
public enum DATA_OP_OP_COMM{
	OP_START_LISTEN_TO_MM_OP,
	OP_START_LAUNCH_OP,
	OP_END_LAUNCH_OP,
	OP_END_LISTEN_TO_MM_OP,
	OP_START_SET_AOI_OP,
	OP_END_SET_AOI_OP,
}
public enum DATA_OP_OGUI_COMM{
	OP_LAND_OGUI,
	OP_NEW_SEARCH_AOI_OGUI,
	OP_END_FLYBY_OGUI,
}
public Operator(ComChannelList inputs, ComChannelList outputs) {
	setName("Operator");
	State END_OGUI = new State("END_OGUI");
	State TX_OGUI = new State("TX_OGUI");
	State OBSERVE_FLYBY = new State("OBSERVE_FLYBY");
	State RX_VO = new State("RX_VO");
	State END_MM = new State("END_MM");
	State TX_MM = new State("TX_MM");
	State POKE_MM = new State("POKE_MM");
	State OBSERVE_UAV = new State("OBSERVE_UAV");
	State OBSERVE_GUI = new State("OBSERVE_GUI");
	State POST_FLIGHT_COMPLETE = new State("POST_FLIGHT_COMPLETE");
	State POST_FLIGHT = new State("POST_FLIGHT");
	State POKE_OGUI = new State("POKE_OGUI");
	State LAUNCH_UAV = new State("LAUNCH_UAV");
	State RX_MM = new State("RX_MM");
	State IDLE = new State("IDLE");
	initializeInternalVariables();
	initializeIDLE(inputs, outputs, POKE_OGUI, LAUNCH_UAV, IDLE, RX_MM);
	initializeOBSERVE_UAV(inputs, outputs, OBSERVE_GUI, RX_MM, POST_FLIGHT, OBSERVE_UAV, POKE_MM);
	initializeLAUNCH_UAV(inputs, outputs, LAUNCH_UAV, OBSERVE_GUI);
	initializeTX_MM(inputs, outputs, TX_MM, END_MM);
	initializePOST_FLIGHT_COMPLETE(inputs, outputs, POST_FLIGHT_COMPLETE, IDLE);
	initializePOKE_MM(inputs, outputs, POKE_MM, TX_MM);
	initializeRX_MM(inputs, outputs, RX_MM, IDLE);
	initializeOBSERVE_FLYBY(inputs, outputs, OBSERVE_FLYBY, POKE_OGUI);
	initializeRX_VO(inputs, outputs, RX_VO, IDLE);
	initializeEND_OGUI(inputs, outputs, END_OGUI, OBSERVE_GUI);
	initializePOST_FLIGHT(inputs, outputs, POST_FLIGHT, POST_FLIGHT_COMPLETE);
	initializePOKE_OGUI(inputs, outputs, POKE_OGUI, TX_OGUI);
	initializeTX_OGUI(inputs, outputs, TX_OGUI, END_OGUI);
	initializeEND_MM(inputs, outputs, END_MM, IDLE);
	initializeOBSERVE_GUI(inputs, outputs, RX_MM, OBSERVE_UAV, OBSERVE_GUI, POKE_OGUI);
	startState(IDLE);
}
 public void initializeIDLE(ComChannelList inputs, ComChannelList outputs, State POKE_OGUI, State LAUNCH_UAV, State IDLE, State RX_MM) {
	// (IDLE,[A=MM_POKE_OP],[],1,NEXT,1.0)x(RX_MM,[A=OP_ACK_MM,D=OP_START_LISTEN_TO_MM_OP],[])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, RX_MM, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!MissionManager.AUDIO_MM_OP_COMM.MM_POKE_OP.equals(_inputs.get(Channels.AUDIO_MM_OP_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.AUDIO_OP_MM_COMM.name(), Operator.AUDIO_OP_MM_COMM.OP_ACK_MM);
			setTempOutput(Channels.DATA_OP_OP_COMM.name(), Operator.DATA_OP_OP_COMM.OP_START_LISTEN_TO_MM_OP);
			return true;
		}
	});
	// (IDLE,[],[TAKE_OFF=TRUE],1,NEXT,1.0)x(LAUNCH_UAV,[D=OP_TAKE_OFF_UAV],[])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, LAUNCH_UAV, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("TAKE_OFF"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_UAV_COMM.name(), Operator.DATA_OP_UAV_COMM.OP_TAKE_OFF_UAV);
			return true;
		}
	});
	// (IDLE,[],[LAND_UAV=TRUE],1,NEXT,1.0)x(POKE_OGUI,[],[])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("LAND_UAV"))) {
				return false;
			}
			return true;
		}
	});
	// (IDLE,[V=UAV_LANDED_OP],[NEW_SEARCH_AOI>0],1,NEXT,1.0)x(LAUNCH_UAV,[D=OP_TAKE_OFF_UAV,D=OP_START_LAUNCH_OP],[])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, LAUNCH_UAV, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_LANDED_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(_internal_vars.getVariable("NEW_SEARCH_AOI") instanceof Integer && new Integer(0) >= (Integer) _internal_vars.getVariable ("NEW_SEARCH_AOI")) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_UAV_COMM.name(), Operator.DATA_OP_UAV_COMM.OP_TAKE_OFF_UAV);
			setTempOutput(Channels.DATA_OP_OP_COMM.name(), Operator.DATA_OP_OP_COMM.OP_START_LAUNCH_OP);
			return true;
		}
	});
	// (IDLE,[V!=UAV_LANDED_OP],[NEW_SEARCH_AOI>0],1,NEXT,1.0)x(POKE_OGUI,[],[])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(UAV.VIDEO_UAV_OP_COMM.UAV_LANDED_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(_internal_vars.getVariable("NEW_SEARCH_AOI") instanceof Integer && new Integer(0) >= (Integer) _internal_vars.getVariable ("NEW_SEARCH_AOI")) {
				return false;
			}
			return true;
		}
	});
	add(IDLE);
}
 public void initializeOBSERVE_UAV(ComChannelList inputs, ComChannelList outputs, State OBSERVE_GUI, State RX_MM, State POST_FLIGHT, State OBSERVE_UAV, State POKE_MM) {
	// (OBSERVE_UAV,[V=UAV_CRASHED_OP],[],1,NEXT,1.0)X(POKE_MM,[A=OP_POKE_MM],[SEARCH_FAILED=TRUE])
	OBSERVE_UAV.add(new Transition(_internal_vars, inputs, outputs, POKE_MM, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_CRASHED_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.AUDIO_OP_MM_COMM.name(), Operator.AUDIO_OP_MM_COMM.OP_POKE_MM);
			setTempInternalVar("SEARCH_FAILED", true);
			return true;
		}
	});
	// (OBSERVE_UAV,[V=UAV_LANDED_OP],[],1,NEXT,1.0)x(POST_FLIGHT,[],[LAND_UAV=FALSE])
	OBSERVE_UAV.add(new Transition(_internal_vars, inputs, outputs, POST_FLIGHT, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_LANDED_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			setTempInternalVar("LAND_UAV", false);
			return true;
		}
	});
	// (OBSERVE_UAV,[A=MM_POKE_OP],[],1,NEXT,1.0)x(RX_MM,[A=OP_ACK_MM],[])
	OBSERVE_UAV.add(new Transition(_internal_vars, inputs, outputs, RX_MM, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!MissionManager.AUDIO_MM_OP_COMM.MM_POKE_OP.equals(_inputs.get(Channels.AUDIO_MM_OP_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.AUDIO_OP_MM_COMM.name(), Operator.AUDIO_OP_MM_COMM.OP_ACK_MM);
			return true;
		}
	});
	// (OBSERVE_UAV,[],[],1,OP_OBSERVE_GUI,1.0)x(OBSERVE_GUI,[],[])
	OBSERVE_UAV.add(new Transition(_internal_vars, inputs, outputs, OBSERVE_GUI, Duration.OP_OBSERVE_GUI.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			return true;
		}
	});
	add(OBSERVE_UAV);
}
 public void initializeLAUNCH_UAV(ComChannelList inputs, ComChannelList outputs, State LAUNCH_UAV, State OBSERVE_GUI) {
	// (LAUNCH_UAV,[V=OGUI_FLYING_NORMAL_OP],[],1,NEXT,1.0)x(OBSERVE_GUI,[D=OP_END_LAUNCH_OP],[])
	LAUNCH_UAV.add(new Transition(_internal_vars, inputs, outputs, OBSERVE_GUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.VIDEO_OGUI_OP_COMM.OGUI_FLYING_NORMAL_OP.equals(_inputs.get(Channels.VIDEO_OGUI_OP_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OP_COMM.name(), Operator.DATA_OP_OP_COMM.OP_END_LAUNCH_OP);
			return true;
		}
	});
	add(LAUNCH_UAV);
}
 public void initializeTX_MM(ComChannelList inputs, ComChannelList outputs, State TX_MM, State END_MM) {
	// (TX_MM,[],[SEARCH_FAILED=TRUE],1,OP_TX_MM,1.0)x(END_MM,[A=OP_SEARCH_FAILED_MM],[])
	TX_MM.add(new Transition(_internal_vars, inputs, outputs, END_MM, Duration.OP_TX_MM.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("SEARCH_FAILED"))) {
				return false;
			}
			setTempOutput(Channels.AUDIO_OP_MM_COMM.name(), Operator.AUDIO_OP_MM_COMM.OP_SEARCH_FAILED_MM);
			return true;
		}
	});
	// (TX_MM,[],[SEARCH_COMPLETE=TRUE],1,OP_TX_MM,1.0)x(END_MM,[A=OP_SEARCH_COMPLETE_MM],[])
	TX_MM.add(new Transition(_internal_vars, inputs, outputs, END_MM, Duration.OP_TX_MM.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("SEARCH_COMPLETE"))) {
				return false;
			}
			setTempOutput(Channels.AUDIO_OP_MM_COMM.name(), Operator.AUDIO_OP_MM_COMM.OP_SEARCH_COMPLETE_MM);
			return true;
		}
	});
	add(TX_MM);
}
 public void initializePOST_FLIGHT_COMPLETE(ComChannelList inputs, ComChannelList outputs, State POST_FLIGHT_COMPLETE, State IDLE) {
	// (POST_FLIGHT_COMPLETE,[],[],1,NEXT,1.0)x(IDLE,[],[])
	POST_FLIGHT_COMPLETE.add(new Transition(_internal_vars, inputs, outputs, IDLE, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			return true;
		}
	});
	add(POST_FLIGHT_COMPLETE);
}
 public void initializePOKE_MM(ComChannelList inputs, ComChannelList outputs, State POKE_MM, State TX_MM) {
	// (POKE_MM,[A=MM_ACK_OP],[],1,NEXT,1.0)x(TX_MM,[],[])
	POKE_MM.add(new Transition(_internal_vars, inputs, outputs, TX_MM, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!MissionManager.AUDIO_MM_OP_COMM.MM_ACK_OP.equals(_inputs.get(Channels.AUDIO_MM_OP_COMM.name()).value())) {
				return false;
			}
			return true;
		}
	});
	add(POKE_MM);
}
 public void initializeRX_MM(ComChannelList inputs, ComChannelList outputs, State RX_MM, State IDLE) {
	// (RX_MM,[A=MM_NEW_SEARCH_AOI_OP],[],1,NEXT,1.0)x(IDLE,[D=OP_END_LISTEN_TO_MM_OP,D=OP_START_SET_AOI_OP],[NEW_SEARCH_AOI=++])
	RX_MM.add(new Transition(_internal_vars, inputs, outputs, IDLE, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!MissionManager.AUDIO_MM_OP_COMM.MM_NEW_SEARCH_AOI_OP.equals(_inputs.get(Channels.AUDIO_MM_OP_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OP_COMM.name(), Operator.DATA_OP_OP_COMM.OP_END_LISTEN_TO_MM_OP);
			setTempOutput(Channels.DATA_OP_OP_COMM.name(), Operator.DATA_OP_OP_COMM.OP_START_SET_AOI_OP);
			setTempInternalVar("NEW_SEARCH_AOI", (Integer)_internal_vars.getVariable("NEW_SEARCH_AOI") + 1);
			return true;
		}
	});
	// (RX_MM,[A=MM_TERMINATE_SEARCH_OP],[],1,NEXT,1.0)x(IDLE,[D=OP_END_LISTEN_TO_MM_OP],[TERMINATE_SEARCH=NEW,LAND_UAV=TRUE])
	RX_MM.add(new Transition(_internal_vars, inputs, outputs, IDLE, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!MissionManager.AUDIO_MM_OP_COMM.MM_TERMINATE_SEARCH_OP.equals(_inputs.get(Channels.AUDIO_MM_OP_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OP_COMM.name(), Operator.DATA_OP_OP_COMM.OP_END_LISTEN_TO_MM_OP);
			setTempInternalVar("TERMINATE_SEARCH", "NEW");
			setTempInternalVar("LAND_UAV", true);
			return true;
		}
	});
	// (RX_MM,[A=MM_END_OP],[],1,NEXT,1.0)x(IDLE,[D=OP_END_LISTEN_TO_MM_OP],[])
	RX_MM.add(new Transition(_internal_vars, inputs, outputs, IDLE, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!MissionManager.AUDIO_MM_OP_COMM.MM_END_OP.equals(_inputs.get(Channels.AUDIO_MM_OP_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OP_COMM.name(), Operator.DATA_OP_OP_COMM.OP_END_LISTEN_TO_MM_OP);
			return true;
		}
	});
	add(RX_MM);
}
 public void initializeOBSERVE_FLYBY(ComChannelList inputs, ComChannelList outputs, State OBSERVE_FLYBY, State POKE_OGUI) {
	// (OBSERVE_FLYBY,[V=OGUI_FLYBY_END_FAILED_OP],[],1,NEXT,1.0)x(POKE_OGUI,[],[END_FLYBY=TRUE])
	OBSERVE_FLYBY.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.VIDEO_OGUI_OP_COMM.OGUI_FLYBY_END_FAILED_OP.equals(_inputs.get(Channels.VIDEO_OGUI_OP_COMM.name()).value())) {
				return false;
			}
			setTempInternalVar("END_FLYBY", true);
			return true;
		}
	});
	// (OBSERVE_FLYBY,[V=OGUI_FLYBY_END_SUCCESS_OP],[],1,NEXT,1.0)x(POKE_OGUI,[],[END_FLYBY=TRUE])
	OBSERVE_FLYBY.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.VIDEO_OGUI_OP_COMM.OGUI_FLYBY_END_SUCCESS_OP.equals(_inputs.get(Channels.VIDEO_OGUI_OP_COMM.name()).value())) {
				return false;
			}
			setTempInternalVar("END_FLYBY", true);
			return true;
		}
	});
	// (OBSERVE_FLYBY,[V=OGUI_BATTERY_LOW_OP],[],1,NEXT,1.0)x(POKE_OGUI,[],[LAND_UAV=TRUE])
	OBSERVE_FLYBY.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.VIDEO_OGUI_OP_COMM.OGUI_BATTERY_LOW_OP.equals(_inputs.get(Channels.VIDEO_OGUI_OP_COMM.name()).value())) {
				return false;
			}
			setTempInternalVar("LAND_UAV", true);
			return true;
		}
	});
	add(OBSERVE_FLYBY);
}
 public void initializeRX_VO(ComChannelList inputs, ComChannelList outputs, State RX_VO, State IDLE) {
	// (RX_VO,[A=VO_BAD_STREAM_OP],[],1,NEXT,1.0)x(IDLE,[],[BAD_STREAM=TRUE])
	RX_VO.add(new Transition(_internal_vars, inputs, outputs, IDLE, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!VideoOperator.AUDIO_VO_OP_COMM.VO_BAD_STREAM_OP.equals(_inputs.get(Channels.AUDIO_VO_OP_COMM.name()).value())) {
				return false;
			}
			setTempInternalVar("BAD_STREAM", true);
			return true;
		}
	});
	add(RX_VO);
}
 public void initializeEND_OGUI(ComChannelList inputs, ComChannelList outputs, State END_OGUI, State OBSERVE_GUI) {
	// (END_OGUI,[],[],1,NEXT,1.0)x(OBSERVE_GUI,[],[])
	END_OGUI.add(new Transition(_internal_vars, inputs, outputs, OBSERVE_GUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			return true;
		}
	});
	add(END_OGUI);
}
 public void initializePOST_FLIGHT(ComChannelList inputs, ComChannelList outputs, State POST_FLIGHT, State POST_FLIGHT_COMPLETE) {
	// (POST_FLIGHT,[],[],1,OP_POST_FLIGHT_COMPLETE,1.0)x(POST_FLIGHT_COMPLETE,[D=OP_POST_FLIGHT_COMPLETE_UAV],[])
	POST_FLIGHT.add(new Transition(_internal_vars, inputs, outputs, POST_FLIGHT_COMPLETE, Duration.OP_POST_FLIGHT_COMPLETE.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			setTempOutput(Channels.DATA_OP_UAV_COMM.name(), Operator.DATA_OP_UAV_COMM.OP_POST_FLIGHT_COMPLETE_UAV);
			return true;
		}
	});
	add(POST_FLIGHT);
}
 public void initializePOKE_OGUI(ComChannelList inputs, ComChannelList outputs, State POKE_OGUI, State TX_OGUI) {
	// (POKE_OGUI,[],[],1,NEXT,1.0)x(TX_OGUI,[],[])
	POKE_OGUI.add(new Transition(_internal_vars, inputs, outputs, TX_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			return true;
		}
	});
	add(POKE_OGUI);
}
 public void initializeTX_OGUI(ComChannelList inputs, ComChannelList outputs, State TX_OGUI, State END_OGUI) {
	// (TX_OGUI,[],[LAND_UAV=TRUE],1,OP_TX_OGUI,1.0)x(END_OGUI,[D=OP_LAND_OGUI],[TERMINATE_SEARCH=CURRENT])
	TX_OGUI.add(new Transition(_internal_vars, inputs, outputs, END_OGUI, Duration.OP_TX_OGUI.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("LAND_UAV"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_LAND_OGUI);
			setTempInternalVar("TERMINATE_SEARCH", "CURRENT");
			return true;
		}
	});
	// (TX_OGUI,[],[NEW_SEARCH_AOI>0],1,OP_TX_OGUI,1.0)x(END_OGUI,[D=OP_NEW_SEARCH_AOI_OGUI,D=OP_END_SET_AOI_OP],[NEW_SEARCH_AOI=--])
	TX_OGUI.add(new Transition(_internal_vars, inputs, outputs, END_OGUI, Duration.OP_TX_OGUI.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(_internal_vars.getVariable("NEW_SEARCH_AOI") instanceof Integer && new Integer(0) >= (Integer) _internal_vars.getVariable ("NEW_SEARCH_AOI")) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_NEW_SEARCH_AOI_OGUI);
			setTempOutput(Channels.DATA_OP_OP_COMM.name(), Operator.DATA_OP_OP_COMM.OP_END_SET_AOI_OP);
			setTempInternalVar("NEW_SEARCH_AOI", (Integer)_internal_vars.getVariable("NEW_SEARCH_AOI") - 1);
			return true;
		}
	});
	// (TX_OGUI,[],[END_FLYBY=TRUE],1,OP_TX_OGUI,1.0)x(END_OGUI,[D=OP_END_FLYBY_OGUI],[END_FLYBY=FALSE])
	TX_OGUI.add(new Transition(_internal_vars, inputs, outputs, END_OGUI, Duration.OP_TX_OGUI.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("END_FLYBY"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_END_FLYBY_OGUI);
			setTempInternalVar("END_FLYBY", false);
			return true;
		}
	});
	add(TX_OGUI);
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
 public void initializeOBSERVE_GUI(ComChannelList inputs, ComChannelList outputs, State RX_MM, State OBSERVE_UAV, State OBSERVE_GUI, State POKE_OGUI) {
	// (OBSERVE_GUI,[V=OGUI_FLYBY_REQ_F_OP],[],1,NEXT,1.0)x(POKE_OGUI,[],[])
	OBSERVE_GUI.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.VIDEO_OGUI_OP_COMM.OGUI_FLYBY_REQ_F_OP.equals(_inputs.get(Channels.VIDEO_OGUI_OP_COMM.name()).value())) {
				return false;
			}
			return true;
		}
	});
	// (OBSERVE_GUI,[V=OGUI_FLYBY_REQ_T_OP],[],1,NEXT,1.0)x(POKE_OGUI,[],[])
	OBSERVE_GUI.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.VIDEO_OGUI_OP_COMM.OGUI_FLYBY_REQ_T_OP.equals(_inputs.get(Channels.VIDEO_OGUI_OP_COMM.name()).value())) {
				return false;
			}
			return true;
		}
	});
	// (OBSERVE_GUI,[],[NEW_SEARCH_AOI>0],1,NEXT,1.0)x(POKE_OGUI,[],[])
	OBSERVE_GUI.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(_internal_vars.getVariable("NEW_SEARCH_AOI") instanceof Integer && new Integer(0) >= (Integer) _internal_vars.getVariable ("NEW_SEARCH_AOI")) {
				return false;
			}
			return true;
		}
	});
	// (OBSERVE_GUI,[V=OGUI_LANDED_OP],[],1,NEXT,1.0)x(OBSERVE_UAV,[],[])
	OBSERVE_GUI.add(new Transition(_internal_vars, inputs, outputs, OBSERVE_UAV, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.VIDEO_OGUI_OP_COMM.OGUI_LANDED_OP.equals(_inputs.get(Channels.VIDEO_OGUI_OP_COMM.name()).value())) {
				return false;
			}
			return true;
		}
	});
	// (OBSERVE_GUI,[A=MM_POKE_OP],[],1,NEXT,1.0)x(RX_MM,[A=OP_ACK_MM],[])
	OBSERVE_GUI.add(new Transition(_internal_vars, inputs, outputs, RX_MM, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!MissionManager.AUDIO_MM_OP_COMM.MM_POKE_OP.equals(_inputs.get(Channels.AUDIO_MM_OP_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.AUDIO_OP_MM_COMM.name(), Operator.AUDIO_OP_MM_COMM.OP_ACK_MM);
			return true;
		}
	});
	// (OBSERVE_GUI,[],[],1,OP_OBSERVE_UAV,1.0)x(OBSERVE_UAV,[],[])
	OBSERVE_GUI.add(new Transition(_internal_vars, inputs, outputs, OBSERVE_UAV, Duration.OP_OBSERVE_UAV.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			return true;
		}
	});
	add(OBSERVE_GUI);
}
@Override
protected void initializeInternalVariables() {
	_internal_vars.addVariable("TAKE_OFF", false);
	_internal_vars.addVariable("LAND_UAV", false);
	_internal_vars.addVariable("NEW_SEARCH_AOI", 0);
	_internal_vars.addVariable("SEARCH_FAILED", false);
	_internal_vars.addVariable("SEARCH_COMPLETE", false);
	_internal_vars.addVariable("TERMINATE_SEARCH", null);
	_internal_vars.addVariable("BAD_STREAM", false);
	_internal_vars.addVariable("END_FLYBY", false);
}
}