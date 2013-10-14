package model.actors.complete;

import model.actors.MissionManager;
import model.team.*;
import simulator.*;

public class VideoOperator extends Actor {
public enum AUDIO_VO_OP_COMM{
	VO_POKE_OP,
	VO_BAD_STREAM_OP,
}
public enum DATA_VO_VGUI_COMM{
	VO_POKE_VGUI,
	VO_END_VGUI,
	VO_FLYBY_REQ_F_VGUI,
	VO_FLYBY_REQ_T_VGUI,
	VO_FLYBY_END_SUCCESS_VGUI,
	VO_FLYBY_END_FAILED_VGUI,
	VO_POSSIBLE_ANOMALY_DETECTED_T_VGUI,
	VO_POSSIBLE_ANOMALY_DETECTED_F_VGUI,
}
public enum AUDIO_VO_MM_COMM{
	VO_ACK_MM,
	VO_POKE_MM,
	VO_END_MM,
	VO_TARGET_FOUND_T_MM,
	VO_TARGET_FOUND_F_MM,
}
public VideoOperator(ComChannelList inputs, ComChannelList outputs) {
	State OBSERVING_FLYBY = new State("OBSERVING_FLYBY");
	State END_VGUI = new State("END_VGUI");
	State TX_VGUI = new State("TX_VGUI");
	State END_OP = new State("END_OP");
	State TX_OP = new State("TX_OP");
	State END_MM = new State("END_MM");
	State TX_MM = new State("TX_MM");
	State POKE_MM = new State("POKE_MM");
	State POKE_OP = new State("POKE_OP");
	State POKE_VGUI = new State("POKE_VGUI");
	State OBSERVING_NORMAL = new State("OBSERVING_NORMAL");
	State RX_MM = new State("RX_MM");
	State IDLE = new State("IDLE");
	initializeIDLE(inputs, outputs, POKE_MM, POKE_OP, POKE_VGUI, OBSERVING_NORMAL, IDLE, RX_MM);
	initializeOBSERVING_NORMAL(inputs, outputs, POKE_MM, POKE_VGUI, POKE_OP, OBSERVING_NORMAL, RX_MM);
	initializePOKE_VGUI(inputs, outputs, IDLE, POKE_VGUI, TX_VGUI);
	initializeEND_VGUI(inputs, outputs, POKE_MM, POKE_OP, POKE_VGUI, END_VGUI, IDLE);
	initializeOBSERVING_FLYBY(inputs, outputs, POKE_MM, POKE_VGUI, POKE_OP, OBSERVING_FLYBY, RX_MM);
	initializeTX_MM(inputs, outputs, TX_MM, END_MM);
	initializeEND_OP(inputs, outputs, POKE_MM, POKE_OP, POKE_VGUI, END_OP, IDLE);
	initializePOKE_OP(inputs, outputs, IDLE, POKE_OP, TX_OP);
	initializeTX_VGUI(inputs, outputs, TX_VGUI, END_VGUI);
	initializeTX_OP(inputs, outputs, TX_OP, END_OP);
	initializeEND_MM(inputs, outputs, POKE_MM, POKE_OP, POKE_VGUI, END_MM, IDLE);
	initializePOKE_MM(inputs, outputs, RX_MM, TX_MM, POKE_MM, IDLE);
	initializeRX_MM(inputs, outputs, RX_MM, IDLE);
}
 public void initializeIDLE(ComChannelList inputs, ComChannelList outputs, State POKE_MM, State POKE_OP, State POKE_VGUI, State OBSERVING_NORMAL, State IDLE, State RX_MM) {
	// (IDLE,[A=MM_POKE_VO],[],3,[1-1],1.0)x(RX_MM,[A=VO_ACK_MM],[])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, RX_MM, new Range(1, 1), 3, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!MissionManager.AUDIO_MM_VO_COMM.MM_POKE_VO.equals(_inputs.get(Channels.AUDIO_MM_VO_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.AUDIO_VO_MM_COMM.name(), VideoOperator.AUDIO_VO_MM_COMM.VO_ACK_MM);
			return true;
		}
	});
	// (IDLE,[],[SEARCH_ACTIVE=TRUE],1,[1-1],1.0)x(OBSERVING_NORMAL,[],[])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, OBSERVING_NORMAL, new Range(1, 1), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("SEARCH_ACTIVE"))) {
				return false;
			}
			return true;
		}
	});
	// (IDLE,[],[FLYBY_REQ_F=TRUE],2,[1-1],1.0)X(POKE_VGUI,[D=VO_POKE_VGUI],[])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_VGUI, new Range(1, 1), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_REQ_F"))) {
				return false;
			}
			setTempOutput(Channels.DATA_VO_VGUI_COMM.name(), VideoOperator.DATA_VO_VGUI_COMM.VO_POKE_VGUI);
			return true;
		}
	});
	// (IDLE,[],[FLYBY_REQ_T=TRUE],2,[1-1],1.0)X(POKE_VGUI,[D=VO_POKE_VGUI],[])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_VGUI, new Range(1, 1), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_REQ_T"))) {
				return false;
			}
			setTempOutput(Channels.DATA_VO_VGUI_COMM.name(), VideoOperator.DATA_VO_VGUI_COMM.VO_POKE_VGUI);
			return true;
		}
	});
	// (IDLE,[],[FLYBY_END_SUCCESS=TRUE],2,[1-1],1.0)X(POKE_VGUI,[D=VO_POKE_VGUI],[])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_VGUI, new Range(1, 1), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_END_SUCCESS"))) {
				return false;
			}
			setTempOutput(Channels.DATA_VO_VGUI_COMM.name(), VideoOperator.DATA_VO_VGUI_COMM.VO_POKE_VGUI);
			return true;
		}
	});
	// (IDLE,[],[FLYBY_END_FAILED=TRUE],2,[1-1],1.0)X(POKE_VGUI,[D=VO_POKE_VGUI],[])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_VGUI, new Range(1, 1), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_END_FAILED"))) {
				return false;
			}
			setTempOutput(Channels.DATA_VO_VGUI_COMM.name(), VideoOperator.DATA_VO_VGUI_COMM.VO_POKE_VGUI);
			return true;
		}
	});
	// (IDLE,[],[POSSIBLE_ANOMALY_DETECTED_T=TRUE],2,[1-1],1.0)X(POKE_VGUI,[D=VO_POKE_VGUI],[])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_VGUI, new Range(1, 1), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("POSSIBLE_ANOMALY_DETECTED_T"))) {
				return false;
			}
			setTempOutput(Channels.DATA_VO_VGUI_COMM.name(), VideoOperator.DATA_VO_VGUI_COMM.VO_POKE_VGUI);
			return true;
		}
	});
	// (IDLE,[],[POSSIBLE_ANOMALY_DETECTED_F=TRUE],2,[1-1],1.0)X(POKE_VGUI,[D=VO_POKE_VGUI],[])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_VGUI, new Range(1, 1), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("POSSIBLE_ANOMALY_DETECTED_F"))) {
				return false;
			}
			setTempOutput(Channels.DATA_VO_VGUI_COMM.name(), VideoOperator.DATA_VO_VGUI_COMM.VO_POKE_VGUI);
			return true;
		}
	});
	// (IDLE,[],[BAD_STREAM=TRUE],2,[1-1],1.0)X(POKE_OP,[A=VO_POKE_OP],[])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_OP, new Range(1, 1), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("BAD_STREAM"))) {
				return false;
			}
			setTempOutput(Channels.AUDIO_VO_OP_COMM.name(), VideoOperator.AUDIO_VO_OP_COMM.VO_POKE_OP);
			return true;
		}
	});
	// (IDLE,[],[TARGET_SIGHTING_T=TRUE],2,[1-1],1.0)X(POKE_MM,[A=VO_POKE_MM],[])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_MM, new Range(1, 1), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("TARGET_SIGHTING_T"))) {
				return false;
			}
			setTempOutput(Channels.AUDIO_VO_MM_COMM.name(), VideoOperator.AUDIO_VO_MM_COMM.VO_POKE_MM);
			return true;
		}
	});
	// (IDLE,[],[TARGET_SIGHTING_F=TRUE],2,[1-1],1.0)X(POKE_MM,[A=VO_POKE_MM],[])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_MM, new Range(1, 1), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("TARGET_SIGHTING_F"))) {
				return false;
			}
			setTempOutput(Channels.AUDIO_VO_MM_COMM.name(), VideoOperator.AUDIO_VO_MM_COMM.VO_POKE_MM);
			return true;
		}
	});
	add(IDLE);
}
 public void initializeOBSERVING_NORMAL(ComChannelList inputs, ComChannelList outputs, State POKE_MM, State POKE_VGUI, State POKE_OP, State OBSERVING_NORMAL, State RX_MM) {
	// (OBSERVING_NORMAL,[A=MM_POKE_VO],[],3,[1-1],1.0)x(RX_MM,[A=VO_ACK_MM],[])
	OBSERVING_NORMAL.add(new Transition(_internal_vars, inputs, outputs, RX_MM, new Range(1, 1), 3, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!MissionManager.AUDIO_MM_VO_COMM.MM_POKE_VO.equals(_inputs.get(Channels.AUDIO_MM_VO_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.AUDIO_VO_MM_COMM.name(), VideoOperator.AUDIO_VO_MM_COMM.VO_ACK_MM);
			return true;
		}
	});
	// (OBSERVING_NORMAL,[V=VGUI_BAD_STREAM_VO],[],2,[1-1],1.0)x(POKE_OP,[A=VO_POKE_OP],[BAD_STREAM=TRUE])
	OBSERVING_NORMAL.add(new Transition(_internal_vars, inputs, outputs, POKE_OP, new Range(1, 1), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!VideoOperatorGui.VIDEO_VGUI_VO_COMM.VGUI_BAD_STREAM_VO.equals(_inputs.get(Channels.VIDEO_VGUI_VO_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.AUDIO_VO_OP_COMM.name(), VideoOperator.AUDIO_VO_OP_COMM.VO_POKE_OP);
			setTempInternalVar("BAD_STREAM", true);
			return true;
		}
	});
	// (OBSERVING_NORMAL,[V=VGUI_FALSE_POSITIVE_VO],[],1,[1-1],.3)x(POKE_VGUI,[D=VO_POKE_VGUI],[FLYBY_REQ_F=TRUE])
	OBSERVING_NORMAL.add(new Transition(_internal_vars, inputs, outputs, POKE_VGUI, new Range(1, 1), 1, .3) {
		@Override
		public boolean isEnabled() { 
			if(!VideoOperatorGui.VIDEO_VGUI_VO_COMM.VGUI_FALSE_POSITIVE_VO.equals(_inputs.get(Channels.VIDEO_VGUI_VO_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_VO_VGUI_COMM.name(), VideoOperator.DATA_VO_VGUI_COMM.VO_POKE_VGUI);
			setTempInternalVar("FLYBY_REQ_F", true);
			return true;
		}
	});
	// (OBSERVING_NORMAL,[V=VGUI_FALSE_POSITIVE_VO],[],1,[1-1],.3)x(POKE_VGUI,[D=VO_POKE_VGUI],[POSSIBLE_ANOMALY_DETECTED_F=TRUE])
	OBSERVING_NORMAL.add(new Transition(_internal_vars, inputs, outputs, POKE_VGUI, new Range(1, 1), 1, .3) {
		@Override
		public boolean isEnabled() { 
			if(!VideoOperatorGui.VIDEO_VGUI_VO_COMM.VGUI_FALSE_POSITIVE_VO.equals(_inputs.get(Channels.VIDEO_VGUI_VO_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_VO_VGUI_COMM.name(), VideoOperator.DATA_VO_VGUI_COMM.VO_POKE_VGUI);
			setTempInternalVar("POSSIBLE_ANOMALY_DETECTED_F", true);
			return true;
		}
	});
	// (OBSERVING_NORMAL,[],[TARGET_SIGHTING_F=TRUE],2,[1-1],.3)x(POKE_MM,[A=VO_POKE_MM],[])
	OBSERVING_NORMAL.add(new Transition(_internal_vars, inputs, outputs, POKE_MM, new Range(1, 1), 2, .3) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("TARGET_SIGHTING_F"))) {
				return false;
			}
			setTempOutput(Channels.AUDIO_VO_MM_COMM.name(), VideoOperator.AUDIO_VO_MM_COMM.VO_POKE_MM);
			return true;
		}
	});
	// (OBSERVING_NORMAL,[V=VGUI_TRUE_POSITIVE_VO],[],1,[1-1],.3)x(POKE_VGUI,[D=VO_POKE_VGUI],[FLYBY_REQ_T=TRUE])
	OBSERVING_NORMAL.add(new Transition(_internal_vars, inputs, outputs, POKE_VGUI, new Range(1, 1), 1, .3) {
		@Override
		public boolean isEnabled() { 
			if(!VideoOperatorGui.VIDEO_VGUI_VO_COMM.VGUI_TRUE_POSITIVE_VO.equals(_inputs.get(Channels.VIDEO_VGUI_VO_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_VO_VGUI_COMM.name(), VideoOperator.DATA_VO_VGUI_COMM.VO_POKE_VGUI);
			setTempInternalVar("FLYBY_REQ_T", true);
			return true;
		}
	});
	// (OBSERVING_NORMAL,[V=VGUI_TRUE_POSITIVE_VO],[],1,[1-1],.3)x(POKE_VGUI,[D=VO_POKE_VGUI],[POSSIBLE_ANOMALY_DETECTED_T=TRUE])
	OBSERVING_NORMAL.add(new Transition(_internal_vars, inputs, outputs, POKE_VGUI, new Range(1, 1), 1, .3) {
		@Override
		public boolean isEnabled() { 
			if(!VideoOperatorGui.VIDEO_VGUI_VO_COMM.VGUI_TRUE_POSITIVE_VO.equals(_inputs.get(Channels.VIDEO_VGUI_VO_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_VO_VGUI_COMM.name(), VideoOperator.DATA_VO_VGUI_COMM.VO_POKE_VGUI);
			setTempInternalVar("POSSIBLE_ANOMALY_DETECTED_T", true);
			return true;
		}
	});
	// (OBSERVING_NORMAL,[],[TARGET_SIGHTING_T=TRUE],2,[1-1],.3)x(POKE_MM,[A=VO_POKE_MM],[])
	OBSERVING_NORMAL.add(new Transition(_internal_vars, inputs, outputs, POKE_MM, new Range(1, 1), 2, .3) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("TARGET_SIGHTING_T"))) {
				return false;
			}
			setTempOutput(Channels.AUDIO_VO_MM_COMM.name(), VideoOperator.AUDIO_VO_MM_COMM.VO_POKE_MM);
			return true;
		}
	});
	// (OBSERVING_NORMAL,[],[FLYBY_END_SUCCESS=TRUE],2,[1-1],1.0)x(POKE_VGUI,[D=VO_POKE_VGUI],[])
	OBSERVING_NORMAL.add(new Transition(_internal_vars, inputs, outputs, POKE_VGUI, new Range(1, 1), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_END_SUCCESS"))) {
				return false;
			}
			setTempOutput(Channels.DATA_VO_VGUI_COMM.name(), VideoOperator.DATA_VO_VGUI_COMM.VO_POKE_VGUI);
			return true;
		}
	});
	// (OBSERVING_NORMAL,[],[FLYBY_END_FAILED=TRUE],2,[1-1],1.0)x(POKE_VGUI,[D=VO_POKE_VGUI],[])
	OBSERVING_NORMAL.add(new Transition(_internal_vars, inputs, outputs, POKE_VGUI, new Range(1, 1), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_END_FAILED"))) {
				return false;
			}
			setTempOutput(Channels.DATA_VO_VGUI_COMM.name(), VideoOperator.DATA_VO_VGUI_COMM.VO_POKE_VGUI);
			return true;
		}
	});
	add(OBSERVING_NORMAL);
}
 public void initializePOKE_VGUI(ComChannelList inputs, ComChannelList outputs, State IDLE, State POKE_VGUI, State TX_VGUI) {
	// (POKE_VGUI,[],[],1,[1-1],1.0)x(TX_VGUI,[],[])
	POKE_VGUI.add(new Transition(_internal_vars, inputs, outputs, TX_VGUI, new Range(1, 1), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			return true;
		}
	});
	// (POKE_VGUI,[],[],0,[60-1800],1.0)x(IDLE,[],[])
	POKE_VGUI.add(new Transition(_internal_vars, inputs, outputs, IDLE, new Range(60, 1800), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			return true;
		}
	});
	add(POKE_VGUI);
}
 public void initializeEND_VGUI(ComChannelList inputs, ComChannelList outputs, State POKE_MM, State POKE_OP, State POKE_VGUI, State END_VGUI, State IDLE) {
	// (END_VGUI,[],[],0,[1-1],1.0)x(IDLE,[],[])
	END_VGUI.add(new Transition(_internal_vars, inputs, outputs, IDLE, new Range(1, 1), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			return true;
		}
	});
	// (END_VGUI,[],[FLYBY_REQ_F=TRUE],2,[1-1],1.0)x(POKE_VGUI,[D=VO_POKE_VGUI],[])
	END_VGUI.add(new Transition(_internal_vars, inputs, outputs, POKE_VGUI, new Range(1, 1), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_REQ_F"))) {
				return false;
			}
			setTempOutput(Channels.DATA_VO_VGUI_COMM.name(), VideoOperator.DATA_VO_VGUI_COMM.VO_POKE_VGUI);
			return true;
		}
	});
	// (END_VGUI,[],[FLYBY_REQ_T=TRUE],2,[1-1],1.0)x(POKE_VGUI,[D=VO_POKE_VGUI],[])
	END_VGUI.add(new Transition(_internal_vars, inputs, outputs, POKE_VGUI, new Range(1, 1), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_REQ_T"))) {
				return false;
			}
			setTempOutput(Channels.DATA_VO_VGUI_COMM.name(), VideoOperator.DATA_VO_VGUI_COMM.VO_POKE_VGUI);
			return true;
		}
	});
	// (END_VGUI,[],[FLYBY_END_SUCCESS=TRUE],2,[1-1],1.0)x(POKE_VGUI,[D=VO_POKE_VGUI],[])
	END_VGUI.add(new Transition(_internal_vars, inputs, outputs, POKE_VGUI, new Range(1, 1), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_END_SUCCESS"))) {
				return false;
			}
			setTempOutput(Channels.DATA_VO_VGUI_COMM.name(), VideoOperator.DATA_VO_VGUI_COMM.VO_POKE_VGUI);
			return true;
		}
	});
	// (END_VGUI,[],[FLYBY_END_FAILED=TRUE],2,[1-1],1.0)x(POKE_VGUI,[D=VO_POKE_VGUI],[])
	END_VGUI.add(new Transition(_internal_vars, inputs, outputs, POKE_VGUI, new Range(1, 1), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_END_FAILED"))) {
				return false;
			}
			setTempOutput(Channels.DATA_VO_VGUI_COMM.name(), VideoOperator.DATA_VO_VGUI_COMM.VO_POKE_VGUI);
			return true;
		}
	});
	// (END_VGUI,[],[POSSIBLE_ANOMALY_DETECTED_T=TRUE],2,[1-1],1.0)x(POKE_VGUI,[D=VO_POKE_VGUI],[])
	END_VGUI.add(new Transition(_internal_vars, inputs, outputs, POKE_VGUI, new Range(1, 1), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("POSSIBLE_ANOMALY_DETECTED_T"))) {
				return false;
			}
			setTempOutput(Channels.DATA_VO_VGUI_COMM.name(), VideoOperator.DATA_VO_VGUI_COMM.VO_POKE_VGUI);
			return true;
		}
	});
	// (END_VGUI,[],[POSSIBLE_ANOMALY_DETECTED_F=TRUE],2,[1-1],1.0)x(POKE_VGUI,[D=VO_POKE_VGUI],[])
	END_VGUI.add(new Transition(_internal_vars, inputs, outputs, POKE_VGUI, new Range(1, 1), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("POSSIBLE_ANOMALY_DETECTED_F"))) {
				return false;
			}
			setTempOutput(Channels.DATA_VO_VGUI_COMM.name(), VideoOperator.DATA_VO_VGUI_COMM.VO_POKE_VGUI);
			return true;
		}
	});
	// (END_VGUI,[],[BAD_STREAM=TRUE],2,[1-1],1.0)x(POKE_OP,[A=VO_POKE_OP],[])
	END_VGUI.add(new Transition(_internal_vars, inputs, outputs, POKE_OP, new Range(1, 1), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("BAD_STREAM"))) {
				return false;
			}
			setTempOutput(Channels.AUDIO_VO_OP_COMM.name(), VideoOperator.AUDIO_VO_OP_COMM.VO_POKE_OP);
			return true;
		}
	});
	// (END_VGUI,[],[TARGET_SIGHTING_T=TRUE],2,[1-1],1.0)x(POKE_MM,[A=VO_POKE_MM],[])
	END_VGUI.add(new Transition(_internal_vars, inputs, outputs, POKE_MM, new Range(1, 1), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("TARGET_SIGHTING_T"))) {
				return false;
			}
			setTempOutput(Channels.AUDIO_VO_MM_COMM.name(), VideoOperator.AUDIO_VO_MM_COMM.VO_POKE_MM);
			return true;
		}
	});
	// (END_VGUI,[],[TARGET_SIGHTING_F=TRUE],2,[1-1],1.0)x(POKE_MM,[A=VO_POKE_MM],[])
	END_VGUI.add(new Transition(_internal_vars, inputs, outputs, POKE_MM, new Range(1, 1), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("TARGET_SIGHTING_F"))) {
				return false;
			}
			setTempOutput(Channels.AUDIO_VO_MM_COMM.name(), VideoOperator.AUDIO_VO_MM_COMM.VO_POKE_MM);
			return true;
		}
	});
	add(END_VGUI);
}
 public void initializeOBSERVING_FLYBY(ComChannelList inputs, ComChannelList outputs, State POKE_MM, State POKE_VGUI, State POKE_OP, State OBSERVING_FLYBY, State RX_MM) {
	// (OBSERVING_FLYBY,[A=MM_POKE_VO],[],1,[1-1],1.0)x(RX_MM,[A=VO_ACK_MM],[])
	OBSERVING_FLYBY.add(new Transition(_internal_vars, inputs, outputs, RX_MM, new Range(1, 1), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!MissionManager.AUDIO_MM_VO_COMM.MM_POKE_VO.equals(_inputs.get(Channels.AUDIO_MM_VO_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.AUDIO_VO_MM_COMM.name(), VideoOperator.AUDIO_VO_MM_COMM.VO_ACK_MM);
			return true;
		}
	});
	// (OBSERVING_FLYBY,[V=VGUI_BAD_STREAM_VO],[],2,[1-1],1.0)x(POKE_OP,[A=VO_POKE_OP],[BAD_STREAM=TRUE])
	OBSERVING_FLYBY.add(new Transition(_internal_vars, inputs, outputs, POKE_OP, new Range(1, 1), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!VideoOperatorGui.VIDEO_VGUI_VO_COMM.VGUI_BAD_STREAM_VO.equals(_inputs.get(Channels.VIDEO_VGUI_VO_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.AUDIO_VO_OP_COMM.name(), VideoOperator.AUDIO_VO_OP_COMM.VO_POKE_OP);
			setTempInternalVar("BAD_STREAM", true);
			return true;
		}
	});
	// (OBSERVING_FLYBY,[V=VGUI_FLYBY_ANOMALY_F_VO],[],1,[1-1],.5)x(POKE_VGUI,[D=VO_POKE_VGUI],[VO_FLYBY_END_SUCCESS=TRUE,TARGET_SIGHTING_F=TRUE])
	OBSERVING_FLYBY.add(new Transition(_internal_vars, inputs, outputs, POKE_VGUI, new Range(1, 1), 1, .5) {
		@Override
		public boolean isEnabled() { 
			if(!VideoOperatorGui.VIDEO_VGUI_VO_COMM.VGUI_FLYBY_ANOMALY_F_VO.equals(_inputs.get(Channels.VIDEO_VGUI_VO_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_VO_VGUI_COMM.name(), VideoOperator.DATA_VO_VGUI_COMM.VO_POKE_VGUI);
			setTempInternalVar("VO_FLYBY_END_SUCCESS", true);
			setTempInternalVar("TARGET_SIGHTING_F", true);
			return true;
		}
	});
	// (OBSERVING_FLYBY,[V=VGUI_FLYBY_ANOMALY_F_VO],[],1,[1-1],.5)x(POKE_VGUI,[D=VO_POKE_VGUI],[VO_FLYBY_END_SUCCESS=TRUE])
	OBSERVING_FLYBY.add(new Transition(_internal_vars, inputs, outputs, POKE_VGUI, new Range(1, 1), 1, .5) {
		@Override
		public boolean isEnabled() { 
			if(!VideoOperatorGui.VIDEO_VGUI_VO_COMM.VGUI_FLYBY_ANOMALY_F_VO.equals(_inputs.get(Channels.VIDEO_VGUI_VO_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_VO_VGUI_COMM.name(), VideoOperator.DATA_VO_VGUI_COMM.VO_POKE_VGUI);
			setTempInternalVar("VO_FLYBY_END_SUCCESS", true);
			return true;
		}
	});
	// (OBSERVING_FLYBY,[V=VGUI_FLYBY_ANOMALY_T_VO],[],1,[1-1],.5)x(POKE_VGUI,[D=VO_POKE_VGUI],[VO_FLYBY_END_FAILED=TRUE,TARGET_SIGHTING_T=TRUE])
	OBSERVING_FLYBY.add(new Transition(_internal_vars, inputs, outputs, POKE_VGUI, new Range(1, 1), 1, .5) {
		@Override
		public boolean isEnabled() { 
			if(!VideoOperatorGui.VIDEO_VGUI_VO_COMM.VGUI_FLYBY_ANOMALY_T_VO.equals(_inputs.get(Channels.VIDEO_VGUI_VO_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_VO_VGUI_COMM.name(), VideoOperator.DATA_VO_VGUI_COMM.VO_POKE_VGUI);
			setTempInternalVar("VO_FLYBY_END_FAILED", true);
			setTempInternalVar("TARGET_SIGHTING_T", true);
			return true;
		}
	});
	// (OBSERVING_FLYBY,[V=VGUI_FLYBY_ANOMALY_T_VO],[],1,[1-1],.5)x(POKE_VGUI,[D=VO_POKE_VGUI],[VO_FLYBY_END_FAILED=TRUE])
	OBSERVING_FLYBY.add(new Transition(_internal_vars, inputs, outputs, POKE_VGUI, new Range(1, 1), 1, .5) {
		@Override
		public boolean isEnabled() { 
			if(!VideoOperatorGui.VIDEO_VGUI_VO_COMM.VGUI_FLYBY_ANOMALY_T_VO.equals(_inputs.get(Channels.VIDEO_VGUI_VO_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_VO_VGUI_COMM.name(), VideoOperator.DATA_VO_VGUI_COMM.VO_POKE_VGUI);
			setTempInternalVar("VO_FLYBY_END_FAILED", true);
			return true;
		}
	});
	// (OBSERVING_FLYBY,[],[FLYBY_REQ_F=TRUE],2,[1-1],1.0)x(POKE_VGUI,[D=VO_POKE_VGUI],[])
	OBSERVING_FLYBY.add(new Transition(_internal_vars, inputs, outputs, POKE_VGUI, new Range(1, 1), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_REQ_F"))) {
				return false;
			}
			setTempOutput(Channels.DATA_VO_VGUI_COMM.name(), VideoOperator.DATA_VO_VGUI_COMM.VO_POKE_VGUI);
			return true;
		}
	});
	// (OBSERVING_FLYBY,[],[FLYBY_REQ_T=TRUE],2,[1-1],1.0)x(POKE_VGUI,[D=VO_POKE_VGUI],[])
	OBSERVING_FLYBY.add(new Transition(_internal_vars, inputs, outputs, POKE_VGUI, new Range(1, 1), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_REQ_T"))) {
				return false;
			}
			setTempOutput(Channels.DATA_VO_VGUI_COMM.name(), VideoOperator.DATA_VO_VGUI_COMM.VO_POKE_VGUI);
			return true;
		}
	});
	// (OBSERVING_FLYBY,[],[FLYBY_END_SUCCESS=TRUE],2,[1-1],1.0)x(POKE_VGUI,[D=VO_POKE_VGUI],[])
	OBSERVING_FLYBY.add(new Transition(_internal_vars, inputs, outputs, POKE_VGUI, new Range(1, 1), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_END_SUCCESS"))) {
				return false;
			}
			setTempOutput(Channels.DATA_VO_VGUI_COMM.name(), VideoOperator.DATA_VO_VGUI_COMM.VO_POKE_VGUI);
			return true;
		}
	});
	// (OBSERVING_FLYBY,[],[FLYBY_END_FAILED=TRUE],2,[1-1],1.0)x(POKE_VGUI,[D=VO_POKE_VGUI],[])
	OBSERVING_FLYBY.add(new Transition(_internal_vars, inputs, outputs, POKE_VGUI, new Range(1, 1), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_END_FAILED"))) {
				return false;
			}
			setTempOutput(Channels.DATA_VO_VGUI_COMM.name(), VideoOperator.DATA_VO_VGUI_COMM.VO_POKE_VGUI);
			return true;
		}
	});
	// (OBSERVING_FLYBY,[],[POSSIBLE_ANOMALY_DETECTED_T=TRUE],2,[1-1],1.0)x(POKE_VGUI,[D=VO_POKE_VGUI],[])
	OBSERVING_FLYBY.add(new Transition(_internal_vars, inputs, outputs, POKE_VGUI, new Range(1, 1), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("POSSIBLE_ANOMALY_DETECTED_T"))) {
				return false;
			}
			setTempOutput(Channels.DATA_VO_VGUI_COMM.name(), VideoOperator.DATA_VO_VGUI_COMM.VO_POKE_VGUI);
			return true;
		}
	});
	// (OBSERVING_FLYBY,[],[POSSIBLE_ANOMALY_DETECTED_F=TRUE],2,[1-1],1.0)x(POKE_VGUI,[D=VO_POKE_VGUI],[])
	OBSERVING_FLYBY.add(new Transition(_internal_vars, inputs, outputs, POKE_VGUI, new Range(1, 1), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("POSSIBLE_ANOMALY_DETECTED_F"))) {
				return false;
			}
			setTempOutput(Channels.DATA_VO_VGUI_COMM.name(), VideoOperator.DATA_VO_VGUI_COMM.VO_POKE_VGUI);
			return true;
		}
	});
	// (OBSERVING_FLYBY,[],[TARGET_SIGHTING_T=TRUE],2,[1-1],1.0)x(POKE_MM,[A=VO_POKE_MM],[])
	OBSERVING_FLYBY.add(new Transition(_internal_vars, inputs, outputs, POKE_MM, new Range(1, 1), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("TARGET_SIGHTING_T"))) {
				return false;
			}
			setTempOutput(Channels.AUDIO_VO_MM_COMM.name(), VideoOperator.AUDIO_VO_MM_COMM.VO_POKE_MM);
			return true;
		}
	});
	// (OBSERVING_FLYBY,[],[TARGET_SIGHTING_F=TRUE],2,[1-1],1.0)x(POKE_MM,[A=VO_POKE_MM],[])
	OBSERVING_FLYBY.add(new Transition(_internal_vars, inputs, outputs, POKE_MM, new Range(1, 1), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("TARGET_SIGHTING_F"))) {
				return false;
			}
			setTempOutput(Channels.AUDIO_VO_MM_COMM.name(), VideoOperator.AUDIO_VO_MM_COMM.VO_POKE_MM);
			return true;
		}
	});
	add(OBSERVING_FLYBY);
}
 public void initializeTX_MM(ComChannelList inputs, ComChannelList outputs, State TX_MM, State END_MM) {
	// (TX_MM,[],[],0,[60-1800],1.0)x(END_MM,[A=VO_END_MM],[])
	TX_MM.add(new Transition(_internal_vars, inputs, outputs, END_MM, new Range(60, 1800), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			setTempOutput(Channels.AUDIO_VO_MM_COMM.name(), VideoOperator.AUDIO_VO_MM_COMM.VO_END_MM);
			return true;
		}
	});
	// (TX_MM,[],[TARGET_SIGHTING_T=TRUE],1,VO_TX_MM,1.0)X(END_MM,[A=VO_TARGET_FOUND_T_MM],[TARGET_SIGHTING_T=FALSE])
	TX_MM.add(new Transition(_internal_vars, inputs, outputs, END_MM, Duration.VO_TX_MM.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("TARGET_SIGHTING_T"))) {
				return false;
			}
			setTempOutput(Channels.AUDIO_VO_MM_COMM.name(), VideoOperator.AUDIO_VO_MM_COMM.VO_TARGET_FOUND_T_MM);
			setTempInternalVar("TARGET_SIGHTING_T", false);
			return true;
		}
	});
	// (TX_MM,[],[TARGET_SIGHTING_F=TRUE],1,VO_TX_MM,1.0)X(END_MM,[A=VO_TARGET_FOUND_F_MM],[TARGET_SIGHTING_F=FALSE])
	TX_MM.add(new Transition(_internal_vars, inputs, outputs, END_MM, Duration.VO_TX_MM.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("TARGET_SIGHTING_F"))) {
				return false;
			}
			setTempOutput(Channels.AUDIO_VO_MM_COMM.name(), VideoOperator.AUDIO_VO_MM_COMM.VO_TARGET_FOUND_F_MM);
			setTempInternalVar("TARGET_SIGHTING_F", false);
			return true;
		}
	});
	add(TX_MM);
}
 public void initializeEND_OP(ComChannelList inputs, ComChannelList outputs, State POKE_MM, State POKE_OP, State POKE_VGUI, State END_OP, State IDLE) {
	// (END_OP,[],[],0,[1-1],1.0)x(IDLE,[],[])
	END_OP.add(new Transition(_internal_vars, inputs, outputs, IDLE, new Range(1, 1), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			return true;
		}
	});
	// (END_OP,[],[FLYBY_REQ_F=TRUE],2,[1-1],1.0)X(POKE_VGUI,[D=VO_POKE_VGUI],[])
	END_OP.add(new Transition(_internal_vars, inputs, outputs, POKE_VGUI, new Range(1, 1), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_REQ_F"))) {
				return false;
			}
			setTempOutput(Channels.DATA_VO_VGUI_COMM.name(), VideoOperator.DATA_VO_VGUI_COMM.VO_POKE_VGUI);
			return true;
		}
	});
	// (END_OP,[],[FLYBY_REQ_T=TRUE],2,[1-1],1.0)X(POKE_VGUI,[D=VO_POKE_VGUI],[])
	END_OP.add(new Transition(_internal_vars, inputs, outputs, POKE_VGUI, new Range(1, 1), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_REQ_T"))) {
				return false;
			}
			setTempOutput(Channels.DATA_VO_VGUI_COMM.name(), VideoOperator.DATA_VO_VGUI_COMM.VO_POKE_VGUI);
			return true;
		}
	});
	// (END_OP,[],[FLYBY_END_SUCCESS=TRUE],2,[1-1],1.0)X(POKE_VGUI,[D=VO_POKE_VGUI],[])
	END_OP.add(new Transition(_internal_vars, inputs, outputs, POKE_VGUI, new Range(1, 1), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_END_SUCCESS"))) {
				return false;
			}
			setTempOutput(Channels.DATA_VO_VGUI_COMM.name(), VideoOperator.DATA_VO_VGUI_COMM.VO_POKE_VGUI);
			return true;
		}
	});
	// (END_OP,[],[FLYBY_END_FAILED=TRUE],2,[1-1],1.0)X(POKE_VGUI,[D=VO_POKE_VGUI],[])
	END_OP.add(new Transition(_internal_vars, inputs, outputs, POKE_VGUI, new Range(1, 1), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_END_FAILED"))) {
				return false;
			}
			setTempOutput(Channels.DATA_VO_VGUI_COMM.name(), VideoOperator.DATA_VO_VGUI_COMM.VO_POKE_VGUI);
			return true;
		}
	});
	// (END_OP,[],[POSSIBLE_ANOMALY_DETECTED_T=TRUE],2,[1-1],1.0)X(POKE_VGUI,[D=VO_POKE_VGUI],[])
	END_OP.add(new Transition(_internal_vars, inputs, outputs, POKE_VGUI, new Range(1, 1), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("POSSIBLE_ANOMALY_DETECTED_T"))) {
				return false;
			}
			setTempOutput(Channels.DATA_VO_VGUI_COMM.name(), VideoOperator.DATA_VO_VGUI_COMM.VO_POKE_VGUI);
			return true;
		}
	});
	// (END_OP,[],[POSSIBLE_ANOMALY_DETECTED_F=TRUE],2,[1-1],1.0)X(POKE_VGUI,[D=VO_POKE_VGUI],[])
	END_OP.add(new Transition(_internal_vars, inputs, outputs, POKE_VGUI, new Range(1, 1), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("POSSIBLE_ANOMALY_DETECTED_F"))) {
				return false;
			}
			setTempOutput(Channels.DATA_VO_VGUI_COMM.name(), VideoOperator.DATA_VO_VGUI_COMM.VO_POKE_VGUI);
			return true;
		}
	});
	// (END_OP,[],[BAD_STREAM=TRUE],2,[1-1],1.0)X(POKE_OP,[A=VO_POKE_OP],[])
	END_OP.add(new Transition(_internal_vars, inputs, outputs, POKE_OP, new Range(1, 1), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("BAD_STREAM"))) {
				return false;
			}
			setTempOutput(Channels.AUDIO_VO_OP_COMM.name(), VideoOperator.AUDIO_VO_OP_COMM.VO_POKE_OP);
			return true;
		}
	});
	// (END_OP,[],[TARGET_SIGHTING_T=TRUE],2,[1-1],1.0)x(POKE_MM,[A=VO_POKE_MM],[])
	END_OP.add(new Transition(_internal_vars, inputs, outputs, POKE_MM, new Range(1, 1), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("TARGET_SIGHTING_T"))) {
				return false;
			}
			setTempOutput(Channels.AUDIO_VO_MM_COMM.name(), VideoOperator.AUDIO_VO_MM_COMM.VO_POKE_MM);
			return true;
		}
	});
	// (END_OP,[],[TARGET_SIGHTING_F=TRUE],2,[1-1],1.0)x(POKE_MM,[A=VO_POKE_MM],[])
	END_OP.add(new Transition(_internal_vars, inputs, outputs, POKE_MM, new Range(1, 1), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("TARGET_SIGHTING_F"))) {
				return false;
			}
			setTempOutput(Channels.AUDIO_VO_MM_COMM.name(), VideoOperator.AUDIO_VO_MM_COMM.VO_POKE_MM);
			return true;
		}
	});
	add(END_OP);
}
 public void initializePOKE_OP(ComChannelList inputs, ComChannelList outputs, State IDLE, State POKE_OP, State TX_OP) {
	// (POKE_OP,[A=OP_ACK_VO],[],1,[1-1],1.0)X(TX_OP,[],[])
	POKE_OP.add(new Transition(_internal_vars, inputs, outputs, TX_OP, new Range(1, 1), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!Operator.AUDIO_OP_VO_COMM.OP_ACK_VO.equals(_inputs.get(Channels.AUDIO_OP_VO_COMM.name()).value())) {
				return false;
			}
			return true;
		}
	});
	// (POKE_OP,[],[],0,[60-1800],1.0)x(IDLE,[],[])
	POKE_OP.add(new Transition(_internal_vars, inputs, outputs, IDLE, new Range(60, 1800), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			return true;
		}
	});
	add(POKE_OP);
}
 public void initializeTX_VGUI(ComChannelList inputs, ComChannelList outputs, State TX_VGUI, State END_VGUI) {
	// (TX_VGUI,[],[],0,[60-1800],1.0)x(END_VGUI,[D=VO_END_VGUI],[])
	TX_VGUI.add(new Transition(_internal_vars, inputs, outputs, END_VGUI, new Range(60, 1800), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			setTempOutput(Channels.DATA_VO_VGUI_COMM.name(), VideoOperator.DATA_VO_VGUI_COMM.VO_END_VGUI);
			return true;
		}
	});
	// (TX_VGUI,[],[FLYBY_REQ_F=TRUE],0,[60-1800],1.0)X(END_VGUI,[D=VO_FLYBY_REQ_F_VGUI],[])
	TX_VGUI.add(new Transition(_internal_vars, inputs, outputs, END_VGUI, new Range(60, 1800), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_REQ_F"))) {
				return false;
			}
			setTempOutput(Channels.DATA_VO_VGUI_COMM.name(), VideoOperator.DATA_VO_VGUI_COMM.VO_FLYBY_REQ_F_VGUI);
			return true;
		}
	});
	// (TX_VGUI,[],[FLYBY_REQ_T=TRUE],0,[60-1800],1.0)X(END_VGUI,[D=VO_FLYBY_REQ_T_VGUI],[])
	TX_VGUI.add(new Transition(_internal_vars, inputs, outputs, END_VGUI, new Range(60, 1800), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_REQ_T"))) {
				return false;
			}
			setTempOutput(Channels.DATA_VO_VGUI_COMM.name(), VideoOperator.DATA_VO_VGUI_COMM.VO_FLYBY_REQ_T_VGUI);
			return true;
		}
	});
	// (TX_VGUI,[],[FLYBY_END_SUCCESS=TRUE],0,[60-1800],1.0)X(END_VGUI,[D=VO_FLYBY_END_SUCCESS_VGUI],[])
	TX_VGUI.add(new Transition(_internal_vars, inputs, outputs, END_VGUI, new Range(60, 1800), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_END_SUCCESS"))) {
				return false;
			}
			setTempOutput(Channels.DATA_VO_VGUI_COMM.name(), VideoOperator.DATA_VO_VGUI_COMM.VO_FLYBY_END_SUCCESS_VGUI);
			return true;
		}
	});
	// (TX_VGUI,[],[FLYBY_END_FAILED=TRUE],0,[60-1800],1.0)X(END_VGUI,[D=VO_FLYBY_END_FAILED_VGUI],[])
	TX_VGUI.add(new Transition(_internal_vars, inputs, outputs, END_VGUI, new Range(60, 1800), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_END_FAILED"))) {
				return false;
			}
			setTempOutput(Channels.DATA_VO_VGUI_COMM.name(), VideoOperator.DATA_VO_VGUI_COMM.VO_FLYBY_END_FAILED_VGUI);
			return true;
		}
	});
	// (TX_VGUI,[],[POSSIBLE_ANOMALY_DETECTED_T=TRUE],0,[60-1800],1.0)X(END_VGUI,[D=VO_POSSIBLE_ANOMALY_DETECTED_T_VGUI],[])
	TX_VGUI.add(new Transition(_internal_vars, inputs, outputs, END_VGUI, new Range(60, 1800), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("POSSIBLE_ANOMALY_DETECTED_T"))) {
				return false;
			}
			setTempOutput(Channels.DATA_VO_VGUI_COMM.name(), VideoOperator.DATA_VO_VGUI_COMM.VO_POSSIBLE_ANOMALY_DETECTED_T_VGUI);
			return true;
		}
	});
	// (TX_VGUI,[],[POSSIBLE_ANOMALY_DETECTED_F=TRUE],0,[60-1800],1.0)X(END_VGUI,[D=VO_POSSIBLE_ANOMALY_DETECTED_F_VGUI],[])
	TX_VGUI.add(new Transition(_internal_vars, inputs, outputs, END_VGUI, new Range(60, 1800), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("POSSIBLE_ANOMALY_DETECTED_F"))) {
				return false;
			}
			setTempOutput(Channels.DATA_VO_VGUI_COMM.name(), VideoOperator.DATA_VO_VGUI_COMM.VO_POSSIBLE_ANOMALY_DETECTED_F_VGUI);
			return true;
		}
	});
	add(TX_VGUI);
}
 public void initializeTX_OP(ComChannelList inputs, ComChannelList outputs, State TX_OP, State END_OP) {
	// (TX_OP,[],[],0,[60-1800],1.0)x(END_OP,[A=VO_BAD_STREAM_OP],[])
	TX_OP.add(new Transition(_internal_vars, inputs, outputs, END_OP, new Range(60, 1800), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			setTempOutput(Channels.AUDIO_VO_OP_COMM.name(), VideoOperator.AUDIO_VO_OP_COMM.VO_BAD_STREAM_OP);
			return true;
		}
	});
	add(TX_OP);
}
 public void initializeEND_MM(ComChannelList inputs, ComChannelList outputs, State POKE_MM, State POKE_OP, State POKE_VGUI, State END_MM, State IDLE) {
	// (END_MM,[],[],0,[1-1],1.0)x(IDLE,[],[])
	END_MM.add(new Transition(_internal_vars, inputs, outputs, IDLE, new Range(1, 1), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			return true;
		}
	});
	// (END_MM,[],[FLYBY_REQ_F=TRUE],2,[1-1],1.0)X(POKE_VGUI,[D=VO_POKE_VGUI],[])
	END_MM.add(new Transition(_internal_vars, inputs, outputs, POKE_VGUI, new Range(1, 1), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_REQ_F"))) {
				return false;
			}
			setTempOutput(Channels.DATA_VO_VGUI_COMM.name(), VideoOperator.DATA_VO_VGUI_COMM.VO_POKE_VGUI);
			return true;
		}
	});
	// (END_MM,[],[FLYBY_REQ_T=TRUE],2,[1-1],1.0)X(POKE_VGUI,[D=VO_POKE_VGUI],[])
	END_MM.add(new Transition(_internal_vars, inputs, outputs, POKE_VGUI, new Range(1, 1), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_REQ_T"))) {
				return false;
			}
			setTempOutput(Channels.DATA_VO_VGUI_COMM.name(), VideoOperator.DATA_VO_VGUI_COMM.VO_POKE_VGUI);
			return true;
		}
	});
	// (END_MM,[],[FLYBY_END_SUCCESS=TRUE],2,[1-1],1.0)X(POKE_VGUI,[D=VO_POKE_VGUI],[])
	END_MM.add(new Transition(_internal_vars, inputs, outputs, POKE_VGUI, new Range(1, 1), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_END_SUCCESS"))) {
				return false;
			}
			setTempOutput(Channels.DATA_VO_VGUI_COMM.name(), VideoOperator.DATA_VO_VGUI_COMM.VO_POKE_VGUI);
			return true;
		}
	});
	// (END_MM,[],[FLYBY_END_FAILED=TRUE],2,[1-1],1.0)X(POKE_VGUI,[D=VO_POKE_VGUI],[])
	END_MM.add(new Transition(_internal_vars, inputs, outputs, POKE_VGUI, new Range(1, 1), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_END_FAILED"))) {
				return false;
			}
			setTempOutput(Channels.DATA_VO_VGUI_COMM.name(), VideoOperator.DATA_VO_VGUI_COMM.VO_POKE_VGUI);
			return true;
		}
	});
	// (END_MM,[],[POSSIBLE_ANOMALY_DETECTED_T=TRUE],2,[1-1],1.0)X(POKE_VGUI,[D=VO_POKE_VGUI],[])
	END_MM.add(new Transition(_internal_vars, inputs, outputs, POKE_VGUI, new Range(1, 1), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("POSSIBLE_ANOMALY_DETECTED_T"))) {
				return false;
			}
			setTempOutput(Channels.DATA_VO_VGUI_COMM.name(), VideoOperator.DATA_VO_VGUI_COMM.VO_POKE_VGUI);
			return true;
		}
	});
	// (END_MM,[],[POSSIBLE_ANOMALY_DETECTED_F=TRUE],2,[1-1],1.0)X(POKE_VGUI,[D=VO_POKE_VGUI],[])
	END_MM.add(new Transition(_internal_vars, inputs, outputs, POKE_VGUI, new Range(1, 1), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("POSSIBLE_ANOMALY_DETECTED_F"))) {
				return false;
			}
			setTempOutput(Channels.DATA_VO_VGUI_COMM.name(), VideoOperator.DATA_VO_VGUI_COMM.VO_POKE_VGUI);
			return true;
		}
	});
	// (END_MM,[],[BAD_STREAM=TRUE],2,[1-1],1.0)X(POKE_OP,[A=VO_POKE_OP],[])
	END_MM.add(new Transition(_internal_vars, inputs, outputs, POKE_OP, new Range(1, 1), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("BAD_STREAM"))) {
				return false;
			}
			setTempOutput(Channels.AUDIO_VO_OP_COMM.name(), VideoOperator.AUDIO_VO_OP_COMM.VO_POKE_OP);
			return true;
		}
	});
	// (END_MM,[],[TARGET_SIGHTING_T=TRUE],2,[1-1],1.0)X(POKE_MM,[A=VO_POKE_MM],[])
	END_MM.add(new Transition(_internal_vars, inputs, outputs, POKE_MM, new Range(1, 1), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("TARGET_SIGHTING_T"))) {
				return false;
			}
			setTempOutput(Channels.AUDIO_VO_MM_COMM.name(), VideoOperator.AUDIO_VO_MM_COMM.VO_POKE_MM);
			return true;
		}
	});
	// (END_MM,[],[TARGET_SIGHTING_F=TRUE],2,[1-1],1.0)X(POKE_MM,[A=VO_POKE_MM],[])
	END_MM.add(new Transition(_internal_vars, inputs, outputs, POKE_MM, new Range(1, 1), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("TARGET_SIGHTING_F"))) {
				return false;
			}
			setTempOutput(Channels.AUDIO_VO_MM_COMM.name(), VideoOperator.AUDIO_VO_MM_COMM.VO_POKE_MM);
			return true;
		}
	});
	add(END_MM);
}
 public void initializePOKE_MM(ComChannelList inputs, ComChannelList outputs, State RX_MM, State TX_MM, State POKE_MM, State IDLE) {
	// (POKE_MM,[],[],0,[60-1800],1.0)x(IDLE,[],[])
	POKE_MM.add(new Transition(_internal_vars, inputs, outputs, IDLE, new Range(60, 1800), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			return true;
		}
	});
	// (POKE_MM,[A=MM_ACK_VO],[],1,[1-1],1.0)X(TX_MM,[],[])
	POKE_MM.add(new Transition(_internal_vars, inputs, outputs, TX_MM, new Range(1, 1), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!MissionManager.AUDIO_MM_VO_COMM.MM_ACK_VO.equals(_inputs.get(Channels.AUDIO_MM_VO_COMM.name()).value())) {
				return false;
			}
			return true;
		}
	});
	// (POKE_MM,[A=MM_BUSY_VO],[],1,[1-1],1.0)X(IDLE,[],[])
	POKE_MM.add(new Transition(_internal_vars, inputs, outputs, IDLE, new Range(1, 1), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!MissionManager.AUDIO_MM_VO_COMM.MM_BUSY_VO.equals(_inputs.get(Channels.AUDIO_MM_VO_COMM.name()).value())) {
				return false;
			}
			return true;
		}
	});
	// (POKE_MM,[A=MM_POKE_VO],[],1,[1-1],1.0)X(RX_MM,[A=VO_ACK_MM],[])
	POKE_MM.add(new Transition(_internal_vars, inputs, outputs, RX_MM, new Range(1, 1), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!MissionManager.AUDIO_MM_VO_COMM.MM_POKE_VO.equals(_inputs.get(Channels.AUDIO_MM_VO_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.AUDIO_VO_MM_COMM.name(), VideoOperator.AUDIO_VO_MM_COMM.VO_ACK_MM);
			return true;
		}
	});
	add(POKE_MM);
}
 public void initializeRX_MM(ComChannelList inputs, ComChannelList outputs, State RX_MM, State IDLE) {
	// (RX_MM,[],[],0,[1900-1900],1.0)x(IDLE,[],[])
	RX_MM.add(new Transition(_internal_vars, inputs, outputs, IDLE, new Range(1900, 1900), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			return true;
		}
	});
	// (RX_MM,[A=MM_TARGET_DESCRIPTION_VO],[],1,[1-1],1.0)X(IDLE,[],[TARGE_DESCRIPTION=NEW])
	RX_MM.add(new Transition(_internal_vars, inputs, outputs, IDLE, new Range(1, 1), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!MissionManager.AUDIO_MM_VO_COMM.MM_TARGET_DESCRIPTION_VO.equals(_inputs.get(Channels.AUDIO_MM_VO_COMM.name()).value())) {
				return false;
			}
			setTempInternalVar("TARGE_DESCRIPTION", "NEW");
			return true;
		}
	});
	// (RX_MM,[A=MM_TERMINATE_SEARCH_VO],[],1,[1-1],1.0)X(IDLE,[],[SEARCH_ACTIVE=FALSE])
	RX_MM.add(new Transition(_internal_vars, inputs, outputs, IDLE, new Range(1, 1), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!MissionManager.AUDIO_MM_VO_COMM.MM_TERMINATE_SEARCH_VO.equals(_inputs.get(Channels.AUDIO_MM_VO_COMM.name()).value())) {
				return false;
			}
			setTempInternalVar("SEARCH_ACTIVE", false);
			return true;
		}
	});
	// (RX_MM,[A=MM_END_VO],[],1,[1-1],1.0)X(IDLE,[],[])
	RX_MM.add(new Transition(_internal_vars, inputs, outputs, IDLE, new Range(1, 1), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!MissionManager.AUDIO_MM_VO_COMM.MM_END_VO.equals(_inputs.get(Channels.AUDIO_MM_VO_COMM.name()).value())) {
				return false;
			}
			return true;
		}
	});
	add(RX_MM);
}
@Override
protected void initializeInternalVariables() {
	_internal_vars.addVariable("SEARCH_ACTIVE", false);
	_internal_vars.addVariable("FLYBY_REQ_F", false);
	_internal_vars.addVariable("FLYBY_REQ_T", false);
	_internal_vars.addVariable("FLYBY_END_SUCCESS", false);
	_internal_vars.addVariable("FLYBY_END_FAILED", false);
	_internal_vars.addVariable("POSSIBLE_ANOMALY_DETECTED_T", false);
	_internal_vars.addVariable("POSSIBLE_ANOMALY_DETECTED_F", false);
	_internal_vars.addVariable("BAD_STREAM", false);
	_internal_vars.addVariable("TARGET_SIGHTING_T", false);
	_internal_vars.addVariable("TARGET_SIGHTING_F", false);
	_internal_vars.addVariable("TARGE_DESCRIPTION", null);
	_internal_vars.addVariable("VO_FLYBY_END_SUCCESS", false);
	_internal_vars.addVariable("VO_FLYBY_END_FAILED", false);
}
}