package model.actors.complete;

import model.actors.MissionManager;
import model.team.*;
import simulator.*;

public class Operator extends Actor {
public enum VIDEO_OP_UAV_COMM{
	OP_POST_FLIGHT_UAV,
}
public enum AUDIO_OP_MM_COMM{
	OP_ACK_MM,
	OP_POKE_MM,
	OP_END_MM,
	OP_SEARCH_COMPLETE_MM,
}
public enum DATA_OP_UAV_COMM{
	OP_POST_FLIGHT_UAV,
	OP_LAUNCH_UAV,
	OP_POST_FLIGHT_COMPLETE_UAV,
}
public enum DATA_OP_OGUI_COMM{
	OP_POKE_OGUI,
	OP_END_OGUI,
	OP_NEW_FLIGHT_PLAN_OGUI,
	OP_LAND_OGUI,
	OP_LOITER_OGUI,
	OP_RESUME_OGUI,
	OP_MODIFY_FP_OGUI,
}
public enum AUDIO_OP_VO_COMM{
	OP_ACK_VO,
	OP_BUSY_VO,
}
public enum DATA_OP_MM_COMM{
	OP_POKE_MM,
}
public Operator(ComChannelList inputs, ComChannelList outputs) {
	State RV_VO = new State("RV_VO");
	State OBSERVING_UAV = new State("OBSERVING_UAV");
	State POST_FLIGHT_COMPLETE = new State("POST_FLIGHT_COMPLETE");
	State END_OGUI = new State("END_OGUI");
	State TX_OGUI = new State("TX_OGUI");
	State END_MM = new State("END_MM");
	State TX_MM = new State("TX_MM");
	State POKE_MM = new State("POKE_MM");
	State LAUNCH_UAV = new State("LAUNCH_UAV");
	State POKE_OGUI = new State("POKE_OGUI");
	State FLYBY_OGUI = new State("FLYBY_OGUI");
	State POST_FLIGHT = new State("POST_FLIGHT");
	State OBSERVING_OGUI = new State("OBSERVING_OGUI");
	State RX_VO = new State("RX_VO");
	State RX_MM = new State("RX_MM");
	State IDLE = new State("IDLE");
	initializeIDLE(inputs, outputs, POKE_MM, LAUNCH_UAV, POKE_OGUI, FLYBY_OGUI, POST_FLIGHT, OBSERVING_OGUI, RX_VO, IDLE, RX_MM);
	initializeOBSERVING_OGUI(inputs, outputs, POKE_MM, FLYBY_OGUI, RV_VO, RX_MM, LAUNCH_UAV, POST_FLIGHT, POKE_OGUI, OBSERVING_OGUI, OBSERVING_OGUI);
	initializeLAUNCH_UAV(inputs, outputs, FLYBY_OGUI, POST_FLIGHT, LAUNCH_UAV, OBSERVING_OGUI);
	initializeOBSERVING_UAV(inputs, outputs, POKE_MM, FLYBY_OGUI, RV_VO, RX_MM, POKE_OGUI, LAUNCH_UAV, OBSERVING_UAV, POST_FLIGHT);
	initializeTX_MM(inputs, outputs, TX_MM, END_MM);
	initializePOST_FLIGHT_COMPLETE(inputs, outputs, LAUNCH_UAV, POST_FLIGHT_COMPLETE, IDLE);
	initializePOKE_MM(inputs, outputs, RX_MM, IDLE, POKE_MM, TX_MM);
	initializeRX_MM(inputs, outputs, POKE_OGUI, RX_MM, IDLE);
	initializeRX_VO(inputs, outputs, RX_VO, IDLE);
	initializeEND_OGUI(inputs, outputs, END_OGUI, OBSERVING_OGUI);
	initializePOST_FLIGHT(inputs, outputs, POST_FLIGHT, POST_FLIGHT_COMPLETE);
	initializePOKE_OGUI(inputs, outputs, POKE_OGUI, IDLE);
	initializeTX_OGUI(inputs, outputs, POKE_OGUI, TX_OGUI, END_OGUI);
	initializeEND_MM(inputs, outputs, IDLE, LAUNCH_UAV, POKE_MM, FLYBY_OGUI, END_MM, POKE_OGUI);
}
 public void initializeIDLE(ComChannelList inputs, ComChannelList outputs, State POKE_MM, State LAUNCH_UAV, State POKE_OGUI, State FLYBY_OGUI, State POST_FLIGHT, State OBSERVING_OGUI, State RX_VO, State IDLE, State RX_MM) {
	// (IDLE,[A=MM_POKE_OP],[],3,NEXT,1.0)X(RX_MM,[A=OP_ACK_MM],[])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, RX_MM, Duration.NEXT.getRange(), 3, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!MissionManager.AUDIO_MM_OP_COMM.MM_POKE_OP.equals(_inputs.get(Channels.AUDIO_MM_OP_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.AUDIO_OP_MM_COMM.name(), Operator.AUDIO_OP_MM_COMM.OP_ACK_MM);
			return true;
		}
	});
	// (IDLE,[A=VO_POKE_OP],[],2,NEXT,1.0)X(RX_VO,[A=OP_ACK_VO],[])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, RX_VO, Duration.NEXT.getRange(), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!VideoOperator.AUDIO_VO_OP_COMM.VO_POKE_OP.equals(_inputs.get(Channels.AUDIO_VO_OP_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.AUDIO_OP_VO_COMM.name(), Operator.AUDIO_OP_VO_COMM.OP_ACK_VO);
			return true;
		}
	});
	// (IDLE,[A=VO_POKE_OP,A=MM_POKE_OP],[],4,NEXT,1.0)X(RX_MM,[A=OP_ACK_MM,A=OP_BUSY_VO],[])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, RX_MM, Duration.NEXT.getRange(), 4, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!VideoOperator.AUDIO_VO_OP_COMM.VO_POKE_OP.equals(_inputs.get(Channels.AUDIO_VO_OP_COMM.name()).value())) {
				return false;
			}
			if(!MissionManager.AUDIO_MM_OP_COMM.MM_POKE_OP.equals(_inputs.get(Channels.AUDIO_MM_OP_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.AUDIO_OP_MM_COMM.name(), Operator.AUDIO_OP_MM_COMM.OP_ACK_MM);
			setTempOutput(Channels.AUDIO_OP_VO_COMM.name(), Operator.AUDIO_OP_VO_COMM.OP_BUSY_VO);
			return true;
		}
	});
	// (IDLE,[V!=UAV_LANDED_OP],[],1,NEXT,1.0)X(OBSERVING_OGUI,[],[])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, OBSERVING_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(UAV.VIDEO_UAV_OP_COMM.UAV_LANDED_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			return true;
		}
	});
	// (IDLE,[V=UAV_LANDED_OP],[],1,NEXT,1.0)X(POST_FLIGHT,[D=OP_POST_FLIGHT_UAV],[])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, POST_FLIGHT, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_LANDED_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_UAV_COMM.name(), Operator.DATA_OP_UAV_COMM.OP_POST_FLIGHT_UAV);
			return true;
		}
	});
	// (IDLE,[V=UAV_FLYING_NORMAL_OP],[FLYBY_START_F=TRUE],1,NEXT,1.0)X(FLYBY_OGUI,[],[FLYBY_START_F=FALSE])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, FLYBY_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_NORMAL_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_START_F"))) {
				return false;
			}
			setTempInternalVar("FLYBY_START_F", false);
			return true;
		}
	});
	// (IDLE,[V=UAV_FLYING_NORMAL_OP],[FLYBY_START_T=TRUE],1,NEXT,1.0)X(FLYBY_OGUI,[],[FLYBY_START_T=FALSE])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, FLYBY_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_NORMAL_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_START_T"))) {
				return false;
			}
			setTempInternalVar("FLYBY_START_T", false);
			return true;
		}
	});
	// (IDLE,[V=UAV_FLYING_NORMAL_OP],[NEW_FLIGHT_PLAN=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[NEW_FLIGHT_PLAN=FALSE])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_NORMAL_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("NEW_FLIGHT_PLAN"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("NEW_FLIGHT_PLAN", false);
			return true;
		}
	});
	// (IDLE,[V=UAV_FLYING_NORMAL_OP],[LAND=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[LAND=FALSE])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_NORMAL_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("LAND"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("LAND", false);
			return true;
		}
	});
	// (IDLE,[V=UAV_FLYING_NORMAL_OP],[LOITER=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[LOITER=FALSE])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_NORMAL_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("LOITER"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("LOITER", false);
			return true;
		}
	});
	// (IDLE,[V=UAV_FLYING_NORMAL_OP],[RESUME=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[RESUME=FALSE])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_NORMAL_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("RESUME"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("RESUME", false);
			return true;
		}
	});
	// (IDLE,[V=UAV_FLYING_FLYBY_OP],[RESUME=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[RESUME=FALSE])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_FLYBY_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("RESUME"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("RESUME", false);
			return true;
		}
	});
	// (IDLE,[V=UAV_FLYING_FLYBY_OP],[LOITER=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[LOITER=FALSE])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_FLYBY_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("LOITER"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("LOITER", false);
			return true;
		}
	});
	// (IDLE,[V=UAV_FLYING_FLYBY_OP],[LAND=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[LAND=FALSE])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_FLYBY_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("LAND"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("LAND", false);
			return true;
		}
	});
	// (IDLE,[V=UAV_FLYING_FLYBY_OP],[NEW_FLIGHT_PLAN=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[NEW_FLIGHT_PLAN=FALSE])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_FLYBY_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("NEW_FLIGHT_PLAN"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("NEW_FLIGHT_PLAN", false);
			return true;
		}
	});
	// (IDLE,[V=UAV_FLYING_FLYBY_OP],[FLYBY_START_T=TRUE],1,NEXT,1.0)X(FLYBY_OGUI,[],[FLYBY_START_T=FALSE])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, FLYBY_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_FLYBY_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_START_T"))) {
				return false;
			}
			setTempInternalVar("FLYBY_START_T", false);
			return true;
		}
	});
	// (IDLE,[V=UAV_FLYING_FLYBY_OP],[FLYBY_START_F=TRUE],1,NEXT,1.0)X(FLYBY_OGUI,[],[FLYBY_START_F=FALSE])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, FLYBY_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_FLYBY_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_START_F"))) {
				return false;
			}
			setTempInternalVar("FLYBY_START_F", false);
			return true;
		}
	});
	// (IDLE,[V=UAV_FLYING_LOITERING_OP],[FLYBY_START_F=TRUE],1,NEXT,1.0)X(FLYBY_OGUI,[],[FLYBY_START_F=FALSE])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, FLYBY_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_LOITERING_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_START_F"))) {
				return false;
			}
			setTempInternalVar("FLYBY_START_F", false);
			return true;
		}
	});
	// (IDLE,[V=UAV_FLYING_LOITERING_OP],[FLYBY_START_T=TRUE],1,NEXT,1.0)X(FLYBY_OGUI,[],[FLYBY_START_T=FALSE])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, FLYBY_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_LOITERING_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_START_T"))) {
				return false;
			}
			setTempInternalVar("FLYBY_START_T", false);
			return true;
		}
	});
	// (IDLE,[V=UAV_FLYING_LOITERING_OP],[NEW_FLIGHT_PLAN=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[NEW_FLIGHT_PLAN=FALSE])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_LOITERING_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("NEW_FLIGHT_PLAN"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("NEW_FLIGHT_PLAN", false);
			return true;
		}
	});
	// (IDLE,[V=UAV_FLYING_LOITERING_OP],[LAND=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[LAND=FALSE])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_LOITERING_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("LAND"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("LAND", false);
			return true;
		}
	});
	// (IDLE,[V=UAV_FLYING_LOITERING_OP],[LOITER=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[LOITER=FALSE])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_LOITERING_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("LOITER"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("LOITER", false);
			return true;
		}
	});
	// (IDLE,[V=UAV_FLYING_LOITERING_OP],[RESUME=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[RESUME=FALSE])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_LOITERING_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("RESUME"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("RESUME", false);
			return true;
		}
	});
	// (IDLE,[V=UAV_READY_OP],[TAKE_OFF=TRUE],1,NEXT,1.0)X(LAUNCH_UAV,[D=OP_LAUNCH_UAV],[])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, LAUNCH_UAV, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_READY_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("TAKE_OFF"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_UAV_COMM.name(), Operator.DATA_OP_UAV_COMM.OP_LAUNCH_UAV);
			return true;
		}
	});
	// (IDLE,[V=UAV_FLYING_NORMAL_OP],[SEARCH_COMPLETE=TRUE],1,NEXT,1.0)X(POKE_MM,[D=OP_POKE_MM],[])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_MM, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_NORMAL_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("SEARCH_COMPLETE"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_MM_COMM.name(), Operator.DATA_OP_MM_COMM.OP_POKE_MM);
			return true;
		}
	});
	// (IDLE,[V=UAV_FLYING_FLYBY_OP],[SEARCH_COMPLETE=TRUE],1,NEXT,1.0)X(POKE_MM,[D=OP_POKE_MM],[])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_MM, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_FLYBY_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("SEARCH_COMPLETE"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_MM_COMM.name(), Operator.DATA_OP_MM_COMM.OP_POKE_MM);
			return true;
		}
	});
	// (IDLE,[V=UAV_FLYING_LOITERING_OP],[SEARCH_COMPLETE=TRUE],1,NEXT,1.0)X(POKE_MM,[D=OP_POKE_MM],[])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_MM, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_LOITERING_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("SEARCH_COMPLETE"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_MM_COMM.name(), Operator.DATA_OP_MM_COMM.OP_POKE_MM);
			return true;
		}
	});
	add(IDLE);
}
 public void initializeOBSERVING_OGUI(ComChannelList inputs, ComChannelList outputs, State POKE_MM, State FLYBY_OGUI, State RV_VO, State RX_MM, State LAUNCH_UAV, State POST_FLIGHT, State POKE_OGUI, State OBSERVING_OGUI, State OBSERVING_OGUI) {
	// (OBSERVING_OGUI,[],[],0,OBSERVE_UAV,1.0)X(OBSERVING_OGUI,[],[])
	OBSERVING_OGUI.add(new Transition(_internal_vars, inputs, outputs, OBSERVING_OGUI, Duration.OBSERVE_UAV.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			return true;
		}
	});
	// (OBSERVING_OGUI,[],[],0,OBSERVE_OGUI,1.0)X(OBSERVING_OGUI,[],[])
	OBSERVING_OGUI.add(new Transition(_internal_vars, inputs, outputs, OBSERVING_OGUI, Duration.OBSERVE_OGUI.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			return true;
		}
	});
	// (OBSERVING_OGUI,[V=OGUI_ALERT_OP,V=UAV_HAG_LOW_OP],[],10,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[HAG_LOW=TRUE])
	OBSERVING_OGUI.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 10, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.VIDEO_OGUI_OP_COMM.OGUI_ALERT_OP.equals(_inputs.get(Channels.VIDEO_OGUI_OP_COMM.name()).value())) {
				return false;
			}
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_HAG_LOW_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("HAG_LOW", true);
			return true;
		}
	});
	// (OBSERVING_OGUI,[V=OGUI_ALERT_OP,V=UAV_BATTERY_LOW],[],10,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[BATTERY_LOW=TRUE])
	OBSERVING_OGUI.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 10, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.VIDEO_OGUI_OP_COMM.OGUI_ALERT_OP.equals(_inputs.get(Channels.VIDEO_OGUI_OP_COMM.name()).value())) {
				return false;
			}
			if(!UAV.VIDEO_UAV__COMM.UAV_BATTERY_LOW.equals(_inputs.get(Channels.VIDEO_UAV__COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("BATTERY_LOW", true);
			return true;
		}
	});
	// (OBSERVING_OGUI,[V=OGUI_ALERT_OP,V=UAV_SIGNAL_LOST],[],10,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[SIGNAL_LOST=TRUE])
	OBSERVING_OGUI.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 10, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.VIDEO_OGUI_OP_COMM.OGUI_ALERT_OP.equals(_inputs.get(Channels.VIDEO_OGUI_OP_COMM.name()).value())) {
				return false;
			}
			if(!UAV.VIDEO_UAV__COMM.UAV_SIGNAL_LOST.equals(_inputs.get(Channels.VIDEO_UAV__COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("SIGNAL_LOST", true);
			return true;
		}
	});
	// (OBSERVING_OGUI,[V=OGUI_FLYBY_FAILED_OP],[],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[FLYBY=FALSE])
	OBSERVING_OGUI.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.VIDEO_OGUI_OP_COMM.OGUI_FLYBY_FAILED_OP.equals(_inputs.get(Channels.VIDEO_OGUI_OP_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("FLYBY", false);
			return true;
		}
	});
	// (OBSERVING_OGUI,[V=OGUI_FLYBY_SUCCESS_OP],[],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[FLYBY=FALSE])
	OBSERVING_OGUI.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.VIDEO_OGUI_OP_COMM.OGUI_FLYBY_SUCCESS_OP.equals(_inputs.get(Channels.VIDEO_OGUI_OP_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("FLYBY", false);
			return true;
		}
	});
	// (OBSERVING_OGUI,[V=OGUI_LANDED_OP],[],1,NEXT,1.0)X(POST_FLIGHT,[D=UAV_POST_FLIGHT_OP],[])
	OBSERVING_OGUI.add(new Transition(_internal_vars, inputs, outputs, POST_FLIGHT, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.VIDEO_OGUI_OP_COMM.OGUI_LANDED_OP.equals(_inputs.get(Channels.VIDEO_OGUI_OP_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_UAV_OP_COMM.name(), UAV.DATA_UAV_OP_COMM.UAV_POST_FLIGHT_OP);
			return true;
		}
	});
	// (OBSERVING_OGUI,[V=OGUI_READY_OP],[FLIGHT_PLAN=ACTIVE],1,NEXT,1.0)X(LAUNCH_UAV,[D=OP_LAUNCH_UAV],[])
	OBSERVING_OGUI.add(new Transition(_internal_vars, inputs, outputs, LAUNCH_UAV, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.VIDEO_OGUI_OP_COMM.OGUI_READY_OP.equals(_inputs.get(Channels.VIDEO_OGUI_OP_COMM.name()).value())) {
				return false;
			}
			if(!"ACTIVE".equals(_internal_vars.getVariable ("FLIGHT_PLAN"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_UAV_COMM.name(), Operator.DATA_OP_UAV_COMM.OP_LAUNCH_UAV);
			return true;
		}
	});
	// (OBSERVING_OGUI,[V=OGUI_FLYBY_REQ_OP],[FLYBY_START!=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[FLYBY_START=TRUE])
	OBSERVING_OGUI.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.VIDEO_OGUI_OP_COMM.OGUI_FLYBY_REQ_OP.equals(_inputs.get(Channels.VIDEO_OGUI_OP_COMM.name()).value())) {
				return false;
			}
			if(new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_START"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("FLYBY_START", true);
			return true;
		}
	});
	// (OBSERVING_OGUI,[A=MM_POKE_OP],[],2,NEXT,1.0)X(RX_MM,[A=OP_ACK_MM],[])
	OBSERVING_OGUI.add(new Transition(_internal_vars, inputs, outputs, RX_MM, Duration.NEXT.getRange(), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!MissionManager.AUDIO_MM_OP_COMM.MM_POKE_OP.equals(_inputs.get(Channels.AUDIO_MM_OP_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.AUDIO_OP_MM_COMM.name(), Operator.AUDIO_OP_MM_COMM.OP_ACK_MM);
			return true;
		}
	});
	// (OBSERVING_OGUI,[A=VO_POKE_OP,A=MM_POKE_OP],[],3,NEXT,1.0)X(RX_MM,[A=OP_ACK_MM,A=OP_BUSY_VO],[])
	OBSERVING_OGUI.add(new Transition(_internal_vars, inputs, outputs, RX_MM, Duration.NEXT.getRange(), 3, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!VideoOperator.AUDIO_VO_OP_COMM.VO_POKE_OP.equals(_inputs.get(Channels.AUDIO_VO_OP_COMM.name()).value())) {
				return false;
			}
			if(!MissionManager.AUDIO_MM_OP_COMM.MM_POKE_OP.equals(_inputs.get(Channels.AUDIO_MM_OP_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.AUDIO_OP_MM_COMM.name(), Operator.AUDIO_OP_MM_COMM.OP_ACK_MM);
			setTempOutput(Channels.AUDIO_OP_VO_COMM.name(), Operator.AUDIO_OP_VO_COMM.OP_BUSY_VO);
			return true;
		}
	});
	// (OBSERVING_OGUI,[A=VO_POKE_OP],[],1,NEXT,1.0)X(RV_VO,[A=OP_ACK_VO],[])
	OBSERVING_OGUI.add(new Transition(_internal_vars, inputs, outputs, RV_VO, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!VideoOperator.AUDIO_VO_OP_COMM.VO_POKE_OP.equals(_inputs.get(Channels.AUDIO_VO_OP_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.AUDIO_OP_VO_COMM.name(), Operator.AUDIO_OP_VO_COMM.OP_ACK_VO);
			return true;
		}
	});
	// (OBSERVING_OGUI,[V=UAV_FLYING_NORMAL_OP],[FLYBY_START_F=TRUE],1,NEXT,1.0)X(FLYBY_OGUI,[],[FLYBY_START_F=FALSE])
	OBSERVING_OGUI.add(new Transition(_internal_vars, inputs, outputs, FLYBY_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_NORMAL_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_START_F"))) {
				return false;
			}
			setTempInternalVar("FLYBY_START_F", false);
			return true;
		}
	});
	// (OBSERVING_OGUI,[V=UAV_FLYING_NORMAL_OP],[FLYBY_START_T=TRUE],1,NEXT,1.0)X(FLYBY_OGUI,[],[FLYBY_START_T=FALSE])
	OBSERVING_OGUI.add(new Transition(_internal_vars, inputs, outputs, FLYBY_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_NORMAL_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_START_T"))) {
				return false;
			}
			setTempInternalVar("FLYBY_START_T", false);
			return true;
		}
	});
	// (OBSERVING_OGUI,[V=UAV_FLYING_NORMAL_OP],[NEW_FLIGHT_PLAN=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[NEW_FLIGHT_PLAN=FALSE])
	OBSERVING_OGUI.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_NORMAL_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("NEW_FLIGHT_PLAN"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("NEW_FLIGHT_PLAN", false);
			return true;
		}
	});
	// (OBSERVING_OGUI,[V=UAV_FLYING_NORMAL_OP],[LAND=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[LAND=FALSE])
	OBSERVING_OGUI.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_NORMAL_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("LAND"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("LAND", false);
			return true;
		}
	});
	// (OBSERVING_OGUI,[V=UAV_FLYING_NORMAL_OP],[LOITER=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[LOITER=FALSE])
	OBSERVING_OGUI.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_NORMAL_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("LOITER"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("LOITER", false);
			return true;
		}
	});
	// (OBSERVING_OGUI,[V=UAV_FLYING_NORMAL_OP],[RESUME=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[RESUME=FALSE])
	OBSERVING_OGUI.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_NORMAL_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("RESUME"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("RESUME", false);
			return true;
		}
	});
	// (OBSERVING_OGUI,[V=UAV_FLYING_FLYBY_OP],[RESUME=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[RESUME=FALSE])
	OBSERVING_OGUI.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_FLYBY_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("RESUME"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("RESUME", false);
			return true;
		}
	});
	// (OBSERVING_OGUI,[V=UAV_FLYING_FLYBY_OP],[LOITER=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[LOITER=FALSE])
	OBSERVING_OGUI.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_FLYBY_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("LOITER"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("LOITER", false);
			return true;
		}
	});
	// (OBSERVING_OGUI,[V=UAV_FLYING_FLYBY_OP],[LAND=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[LAND=FALSE])
	OBSERVING_OGUI.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_FLYBY_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("LAND"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("LAND", false);
			return true;
		}
	});
	// (OBSERVING_OGUI,[V=UAV_FLYING_FLYBY_OP],[NEW_FLIGHT_PLAN=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[NEW_FLIGHT_PLAN=FALSE])
	OBSERVING_OGUI.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_FLYBY_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("NEW_FLIGHT_PLAN"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("NEW_FLIGHT_PLAN", false);
			return true;
		}
	});
	// (OBSERVING_OGUI,[V=UAV_FLYING_FLYBY_OP],[FLYBY_START_T=TRUE],1,NEXT,1.0)X(FLYBY_OGUI,[],[FLYBY_START_T=FALSE])
	OBSERVING_OGUI.add(new Transition(_internal_vars, inputs, outputs, FLYBY_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_FLYBY_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_START_T"))) {
				return false;
			}
			setTempInternalVar("FLYBY_START_T", false);
			return true;
		}
	});
	// (OBSERVING_OGUI,[V=UAV_FLYING_FLYBY_OP],[FLYBY_START_F=TRUE],1,NEXT,1.0)X(FLYBY_OGUI,[],[FLYBY_START_F=FALSE])
	OBSERVING_OGUI.add(new Transition(_internal_vars, inputs, outputs, FLYBY_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_FLYBY_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_START_F"))) {
				return false;
			}
			setTempInternalVar("FLYBY_START_F", false);
			return true;
		}
	});
	// (OBSERVING_OGUI,[V=UAV_FLYING_LOITERING_OP],[FLYBY_START_F=TRUE],1,NEXT,1.0)X(FLYBY_OGUI,[],[FLYBY_START_F=FALSE])
	OBSERVING_OGUI.add(new Transition(_internal_vars, inputs, outputs, FLYBY_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_LOITERING_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_START_F"))) {
				return false;
			}
			setTempInternalVar("FLYBY_START_F", false);
			return true;
		}
	});
	// (OBSERVING_OGUI,[V=UAV_FLYING_LOITERING_OP],[FLYBY_START_T=TRUE],1,NEXT,1.0)X(FLYBY_OGUI,[],[FLYBY_START_T=FALSE])
	OBSERVING_OGUI.add(new Transition(_internal_vars, inputs, outputs, FLYBY_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_LOITERING_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_START_T"))) {
				return false;
			}
			setTempInternalVar("FLYBY_START_T", false);
			return true;
		}
	});
	// (OBSERVING_OGUI,[V=UAV_FLYING_LOITERING_OP],[NEW_FLIGHT_PLAN=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[NEW_FLIGHT_PLAN=FALSE])
	OBSERVING_OGUI.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_LOITERING_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("NEW_FLIGHT_PLAN"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("NEW_FLIGHT_PLAN", false);
			return true;
		}
	});
	// (OBSERVING_OGUI,[V=UAV_FLYING_LOITERING_OP],[LAND=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[LAND=FALSE])
	OBSERVING_OGUI.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_LOITERING_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("LAND"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("LAND", false);
			return true;
		}
	});
	// (OBSERVING_OGUI,[V=UAV_FLYING_LOITERING_OP],[LOITER=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[LOITER=FALSE])
	OBSERVING_OGUI.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_LOITERING_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("LOITER"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("LOITER", false);
			return true;
		}
	});
	// (OBSERVING_OGUI,[V=UAV_FLYING_LOITERING_OP],[RESUME=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[RESUME=FALSE])
	OBSERVING_OGUI.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_LOITERING_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("RESUME"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("RESUME", false);
			return true;
		}
	});
	// (OBSERVING_OGUI,[V=UAV_READY_OP],[TAKE_OFF=TRUE],1,NEXT,1.0)X(LAUNCH_UAV,[D=OP_LAUNCH_UAV],[])
	OBSERVING_OGUI.add(new Transition(_internal_vars, inputs, outputs, LAUNCH_UAV, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_READY_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("TAKE_OFF"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_UAV_COMM.name(), Operator.DATA_OP_UAV_COMM.OP_LAUNCH_UAV);
			return true;
		}
	});
	// (OBSERVING_OGUI,[V=UAV_FLYING_NORMAL_OP],[SEARCH_COMPLETE=TRUE],1,NEXT,1.0)X(POKE_MM,[D=OP_POKE_MM],[])
	OBSERVING_OGUI.add(new Transition(_internal_vars, inputs, outputs, POKE_MM, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_NORMAL_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("SEARCH_COMPLETE"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_MM_COMM.name(), Operator.DATA_OP_MM_COMM.OP_POKE_MM);
			return true;
		}
	});
	// (OBSERVING_OGUI,[V=UAV_FLYING_FLYBY_OP],[SEARCH_COMPLETE=TRUE],1,NEXT,1.0)X(POKE_MM,[D=OP_POKE_MM],[])
	OBSERVING_OGUI.add(new Transition(_internal_vars, inputs, outputs, POKE_MM, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_FLYBY_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("SEARCH_COMPLETE"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_MM_COMM.name(), Operator.DATA_OP_MM_COMM.OP_POKE_MM);
			return true;
		}
	});
	// (OBSERVING_OGUI,[V=UAV_FLYING_LOITERING_OP],[SEARCH_COMPLETE=TRUE],1,NEXT,1.0)X(POKE_MM,[D=OP_POKE_MM],[])
	OBSERVING_OGUI.add(new Transition(_internal_vars, inputs, outputs, POKE_MM, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_LOITERING_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("SEARCH_COMPLETE"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_MM_COMM.name(), Operator.DATA_OP_MM_COMM.OP_POKE_MM);
			return true;
		}
	});
	// (OBSERVING_OGUI,[V=OGUI_SEARCH_COMPLETE_UAV],[],1,NEXT,1.0)X(POKE_MM,[D=OP_POKE_MM],[SEARCH_COMPLETE=TRUE])
	OBSERVING_OGUI.add(new Transition(_internal_vars, inputs, outputs, POKE_MM, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.VIDEO_OGUI_UAV_COMM.OGUI_SEARCH_COMPLETE_UAV.equals(_inputs.get(Channels.VIDEO_OGUI_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_MM_COMM.name(), Operator.DATA_OP_MM_COMM.OP_POKE_MM);
			setTempInternalVar("SEARCH_COMPLETE", true);
			return true;
		}
	});
	add(OBSERVING_OGUI);
}
 public void initializeLAUNCH_UAV(ComChannelList inputs, ComChannelList outputs, State FLYBY_OGUI, State POST_FLIGHT, State LAUNCH_UAV, State OBSERVING_OGUI) {
	// (LAUNCH_UAV,[V=UAV_FLYING_NORMAL_OP],[],1,NEXT,1.0)X(OBSERVING_OGUI,[],[])
	LAUNCH_UAV.add(new Transition(_internal_vars, inputs, outputs, OBSERVING_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_NORMAL_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			return true;
		}
	});
	// (LAUNCH_UAV,[V=UAV_LOITERING_OP],[],1,NEXT,1.0)X(OBSERVING_OGUI,[],[])
	LAUNCH_UAV.add(new Transition(_internal_vars, inputs, outputs, OBSERVING_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_LOITERING_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			return true;
		}
	});
	// (LAUNCH_UAV,[V=UAV_LANDING_OP],[],1,NEXT,1.0)X(OBSERVING_OGUI,[],[])
	LAUNCH_UAV.add(new Transition(_internal_vars, inputs, outputs, OBSERVING_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_LANDING_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			return true;
		}
	});
	// (LAUNCH_UAV,[V=UAV_LANDED_OP],[],1,NEXT,1.0)X(POST_FLIGHT,[D=OP_POST_FLIGHT_UAV],[])
	LAUNCH_UAV.add(new Transition(_internal_vars, inputs, outputs, POST_FLIGHT, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_LANDED_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_UAV_COMM.name(), Operator.DATA_OP_UAV_COMM.OP_POST_FLIGHT_UAV);
			return true;
		}
	});
	// (LAUNCH_UAV,[V=UAV_FLYING_FLYBY],[],1,NEXT,1.0)X(FLYBY_OGUI,[],[])
	LAUNCH_UAV.add(new Transition(_internal_vars, inputs, outputs, FLYBY_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV__COMM.UAV_FLYING_FLYBY.equals(_inputs.get(Channels.VIDEO_UAV__COMM.name()).value())) {
				return false;
			}
			return true;
		}
	});
	add(LAUNCH_UAV);
}
 public void initializeOBSERVING_UAV(ComChannelList inputs, ComChannelList outputs, State POKE_MM, State FLYBY_OGUI, State RV_VO, State RX_MM, State POKE_OGUI, State LAUNCH_UAV, State OBSERVING_UAV, State POST_FLIGHT) {
	// (OBSERVING_UAV,[V=UAV_LANDED_OP],[],1,NEXT,1.0)X(POST_FLIGHT,[V=OP_POST_FLIGHT_UAV],[])
	OBSERVING_UAV.add(new Transition(_internal_vars, inputs, outputs, POST_FLIGHT, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_LANDED_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_OP_UAV_COMM.name(), Operator.VIDEO_OP_UAV_COMM.OP_POST_FLIGHT_UAV);
			return true;
		}
	});
	// (OBSERVING_UAV,[V=UAV_READY_OP],[FLIGHT_PLAN=ACTIVE],1,NEXT,1.0)X(LAUNCH_UAV,[D=OP_LAUNCH_UAV],[])
	OBSERVING_UAV.add(new Transition(_internal_vars, inputs, outputs, LAUNCH_UAV, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_READY_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!"ACTIVE".equals(_internal_vars.getVariable ("FLIGHT_PLAN"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_UAV_COMM.name(), Operator.DATA_OP_UAV_COMM.OP_LAUNCH_UAV);
			return true;
		}
	});
	// (OBSERVING_UAV,[A=UAV_HAG_LOW_OP],[],10,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[HAG_LOW=TRUE])
	OBSERVING_UAV.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 10, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.AUDIO_UAV_OP_COMM.UAV_HAG_LOW_OP.equals(_inputs.get(Channels.AUDIO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("HAG_LOW", true);
			return true;
		}
	});
	// (OBSERVING_UAV,[A=MM_POKE_OP],[],2,NEXT,1.0)X(RX_MM,[A=OP_ACK_MM],[])
	OBSERVING_UAV.add(new Transition(_internal_vars, inputs, outputs, RX_MM, Duration.NEXT.getRange(), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!MissionManager.AUDIO_MM_OP_COMM.MM_POKE_OP.equals(_inputs.get(Channels.AUDIO_MM_OP_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.AUDIO_OP_MM_COMM.name(), Operator.AUDIO_OP_MM_COMM.OP_ACK_MM);
			return true;
		}
	});
	// (OBSERVING_UAV,[A=VO_POKE_OP,A=MM_POKE_OP],[],3,NEXT,1.0)X(RX_MM,[A=OP_ACK_MM,A=OP_BUSY_VO],[])
	OBSERVING_UAV.add(new Transition(_internal_vars, inputs, outputs, RX_MM, Duration.NEXT.getRange(), 3, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!VideoOperator.AUDIO_VO_OP_COMM.VO_POKE_OP.equals(_inputs.get(Channels.AUDIO_VO_OP_COMM.name()).value())) {
				return false;
			}
			if(!MissionManager.AUDIO_MM_OP_COMM.MM_POKE_OP.equals(_inputs.get(Channels.AUDIO_MM_OP_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.AUDIO_OP_MM_COMM.name(), Operator.AUDIO_OP_MM_COMM.OP_ACK_MM);
			setTempOutput(Channels.AUDIO_OP_VO_COMM.name(), Operator.AUDIO_OP_VO_COMM.OP_BUSY_VO);
			return true;
		}
	});
	// (OBSERVING_UAV,[A=VO_POKE_OP],[],1,NEXT,1.0)X(RV_VO,[A=OP_ACK_VO],[])
	OBSERVING_UAV.add(new Transition(_internal_vars, inputs, outputs, RV_VO, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!VideoOperator.AUDIO_VO_OP_COMM.VO_POKE_OP.equals(_inputs.get(Channels.AUDIO_VO_OP_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.AUDIO_OP_VO_COMM.name(), Operator.AUDIO_OP_VO_COMM.OP_ACK_VO);
			return true;
		}
	});
	// (OBSERVING_UAV,[V=UAV_FLYING_NORMAL_OP],[FLYBY_START_F=TRUE],1,NEXT,1.0)X(FLYBY_OGUI,[],[FLYBY_START_F=FALSE])
	OBSERVING_UAV.add(new Transition(_internal_vars, inputs, outputs, FLYBY_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_NORMAL_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_START_F"))) {
				return false;
			}
			setTempInternalVar("FLYBY_START_F", false);
			return true;
		}
	});
	// (OBSERVING_UAV,[V=UAV_FLYING_FLYBY_OP],[FLYBY_START_F=TRUE],1,NEXT,1.0)X(FLYBY_OGUI,[],[FLYBY_START_F=FALSE])
	OBSERVING_UAV.add(new Transition(_internal_vars, inputs, outputs, FLYBY_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_FLYBY_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_START_F"))) {
				return false;
			}
			setTempInternalVar("FLYBY_START_F", false);
			return true;
		}
	});
	// (OBSERVING_UAV,[V=UAV_FLYING_LOITERING_OP],[FLYBY_START_F=TRUE],1,NEXT,1.0)X(FLYBY_OGUI,[],[FLYBY_START_F=FALSE])
	OBSERVING_UAV.add(new Transition(_internal_vars, inputs, outputs, FLYBY_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_LOITERING_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_START_F"))) {
				return false;
			}
			setTempInternalVar("FLYBY_START_F", false);
			return true;
		}
	});
	// (OBSERVING_UAV,[V=UAV_FLYING_NORMAL_OP],[FLYBY_START_T=TRUE],1,NEXT,1.0)X(FLYBY_OGUI,[],[FLYBY_START_T=FALSE])
	OBSERVING_UAV.add(new Transition(_internal_vars, inputs, outputs, FLYBY_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_NORMAL_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_START_T"))) {
				return false;
			}
			setTempInternalVar("FLYBY_START_T", false);
			return true;
		}
	});
	// (OBSERVING_UAV,[V=UAV_FLYING_FLYBY_OP],[FLYBY_START_T=TRUE],1,NEXT,1.0)X(FLYBY_OGUI,[],[FLYBY_START_T=FALSE])
	OBSERVING_UAV.add(new Transition(_internal_vars, inputs, outputs, FLYBY_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_FLYBY_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_START_T"))) {
				return false;
			}
			setTempInternalVar("FLYBY_START_T", false);
			return true;
		}
	});
	// (OBSERVING_UAV,[V=UAV_FLYING_LOITERING_OP],[FLYBY_START_T=TRUE],1,NEXT,1.0)X(FLYBY_OGUI,[],[FLYBY_START_T=FALSE])
	OBSERVING_UAV.add(new Transition(_internal_vars, inputs, outputs, FLYBY_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_LOITERING_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_START_T"))) {
				return false;
			}
			setTempInternalVar("FLYBY_START_T", false);
			return true;
		}
	});
	// (OBSERVING_UAV,[V=UAV_FLYING_NORMAL_OP],[NEW_FLIGHT_PLAN=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[NEW_FLIGHT_PLAN=FALSE])
	OBSERVING_UAV.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_NORMAL_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("NEW_FLIGHT_PLAN"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("NEW_FLIGHT_PLAN", false);
			return true;
		}
	});
	// (OBSERVING_UAV,[V=UAV_FLYING_FLYBY_OP],[NEW_FLIGHT_PLAN=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[NEW_FLIGHT_PLAN=FALSE])
	OBSERVING_UAV.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_FLYBY_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("NEW_FLIGHT_PLAN"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("NEW_FLIGHT_PLAN", false);
			return true;
		}
	});
	// (OBSERVING_UAV,[V=UAV_FLYING_LOITERING_OP],[NEW_FLIGHT_PLAN=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[NEW_FLIGHT_PLAN=FALSE])
	OBSERVING_UAV.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_LOITERING_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("NEW_FLIGHT_PLAN"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("NEW_FLIGHT_PLAN", false);
			return true;
		}
	});
	// (OBSERVING_UAV,[V=UAV_FLYING_NORMAL_OP],[LAND=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[LAND=FALSE])
	OBSERVING_UAV.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_NORMAL_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("LAND"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("LAND", false);
			return true;
		}
	});
	// (OBSERVING_UAV,[V=UAV_FLYING_FLYBY_OP],[LAND=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[LAND=FALSE])
	OBSERVING_UAV.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_FLYBY_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("LAND"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("LAND", false);
			return true;
		}
	});
	// (OBSERVING_UAV,[V=UAV_FLYING_LOITERING_OP],[LAND=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[LAND=FALSE])
	OBSERVING_UAV.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_LOITERING_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("LAND"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("LAND", false);
			return true;
		}
	});
	// (OBSERVING_UAV,[V=UAV_FLYING_NORMAL_OP],[LOITER=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[LOITER=FALSE])
	OBSERVING_UAV.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_NORMAL_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("LOITER"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("LOITER", false);
			return true;
		}
	});
	// (OBSERVING_UAV,[V=UAV_FLYING_FLYBY_OP],[LOITER=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[LOITER=FALSE])
	OBSERVING_UAV.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_FLYBY_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("LOITER"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("LOITER", false);
			return true;
		}
	});
	// (OBSERVING_UAV,[V=UAV_FLYING_LOITERING_OP],[LOITER=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[LOITER=FALSE])
	OBSERVING_UAV.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_LOITERING_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("LOITER"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("LOITER", false);
			return true;
		}
	});
	// (OBSERVING_UAV,[V=UAV_FLYING_NORMAL_OP],[RESUME=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[RESUME=FALSE])
	OBSERVING_UAV.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_NORMAL_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("RESUME"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("RESUME", false);
			return true;
		}
	});
	// (OBSERVING_UAV,[V=UAV_FLYING_FLYBY_OP],[RESUME=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[RESUME=FALSE])
	OBSERVING_UAV.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_FLYBY_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("RESUME"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("RESUME", false);
			return true;
		}
	});
	// (OBSERVING_UAV,[V=UAV_FLYING_LOITERING_OP],[RESUME=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[RESUME=FALSE])
	OBSERVING_UAV.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_LOITERING_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("RESUME"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("RESUME", false);
			return true;
		}
	});
	// (OBSERVING_UAV,[V=UAV_READY_OP],[TAKE_OFF=TRUE],1,NEXT,1.0)X(LAUNCH_UAV,[D=OP_LAUNCH_UAV],[])
	OBSERVING_UAV.add(new Transition(_internal_vars, inputs, outputs, LAUNCH_UAV, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_READY_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("TAKE_OFF"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_UAV_COMM.name(), Operator.DATA_OP_UAV_COMM.OP_LAUNCH_UAV);
			return true;
		}
	});
	// (OBSERVING_UAV,[V=UAV_FLYING_NORMAL_OP],[SEARCH_COMPLETE=TRUE],1,NEXT,1.0)X(POKE_MM,[D=OP_POKE_MM],[SEARCH_COMPLETE=FALSE])
	OBSERVING_UAV.add(new Transition(_internal_vars, inputs, outputs, POKE_MM, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_NORMAL_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("SEARCH_COMPLETE"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_MM_COMM.name(), Operator.DATA_OP_MM_COMM.OP_POKE_MM);
			setTempInternalVar("SEARCH_COMPLETE", false);
			return true;
		}
	});
	// (OBSERVING_UAV,[V=UAV_FLYING_FLYBY_OP],[SEARCH_COMPLETE=TRUE],1,NEXT,1.0)X(POKE_MM,[D=OP_POKE_MM],[SEARCH_COMPLETE=FALSE])
	OBSERVING_UAV.add(new Transition(_internal_vars, inputs, outputs, POKE_MM, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_FLYBY_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("SEARCH_COMPLETE"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_MM_COMM.name(), Operator.DATA_OP_MM_COMM.OP_POKE_MM);
			setTempInternalVar("SEARCH_COMPLETE", false);
			return true;
		}
	});
	// (OBSERVING_UAV,[V=UAV_FLYING_LOITERING_OP],[SEARCH_COMPLETE=TRUE],1,NEXT,1.0)X(POKE_MM,[D=OP_POKE_MM],[SEARCH_COMPLETE=FALSE])
	OBSERVING_UAV.add(new Transition(_internal_vars, inputs, outputs, POKE_MM, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_LOITERING_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("SEARCH_COMPLETE"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_MM_COMM.name(), Operator.DATA_OP_MM_COMM.OP_POKE_MM);
			setTempInternalVar("SEARCH_COMPLETE", false);
			return true;
		}
	});
	add(OBSERVING_UAV);
}
 public void initializeTX_MM(ComChannelList inputs, ComChannelList outputs, State TX_MM, State END_MM) {
	// (TX_MM,[],[],0,OP_TX_MM,1.0)x(END_MM,[A=OP_END_MM],[])
	TX_MM.add(new Transition(_internal_vars, inputs, outputs, END_MM, Duration.OP_TX_MM.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			setTempOutput(Channels.AUDIO_OP_MM_COMM.name(), Operator.AUDIO_OP_MM_COMM.OP_END_MM);
			return true;
		}
	});
	// (TX_MM,[],[SEARCH_COMPLETE=TRUE],1,OP_TX_MM,1.0)x(END_MM,[A=OP_SEARCH_COMPLETE_MM],[SEARCH_COMPLETE=FALSE,LAND=TRUE])
	TX_MM.add(new Transition(_internal_vars, inputs, outputs, END_MM, Duration.OP_TX_MM.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("SEARCH_COMPLETE"))) {
				return false;
			}
			setTempOutput(Channels.AUDIO_OP_MM_COMM.name(), Operator.AUDIO_OP_MM_COMM.OP_SEARCH_COMPLETE_MM);
			setTempInternalVar("SEARCH_COMPLETE", false);
			setTempInternalVar("LAND", true);
			return true;
		}
	});
	add(TX_MM);
}
 public void initializePOST_FLIGHT_COMPLETE(ComChannelList inputs, ComChannelList outputs, State LAUNCH_UAV, State POST_FLIGHT_COMPLETE, State IDLE) {
	// (POST_FLIGHT_COMPLETE,[],[],0,NEXT,1.0)X(IDLE,[],[])
	POST_FLIGHT_COMPLETE.add(new Transition(_internal_vars, inputs, outputs, IDLE, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			return true;
		}
	});
	// (POST_FLIGHT_COMPLETE,[V=UAV_READY_OP],[TAKE_OFF=TRUE],1,NEXT,1.0)X(LAUNCH_UAV,[D=OP_LAUNCH_UAV],[])
	POST_FLIGHT_COMPLETE.add(new Transition(_internal_vars, inputs, outputs, LAUNCH_UAV, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_READY_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("TAKE_OFF"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_UAV_COMM.name(), Operator.DATA_OP_UAV_COMM.OP_LAUNCH_UAV);
			return true;
		}
	});
	add(POST_FLIGHT_COMPLETE);
}
 public void initializePOKE_MM(ComChannelList inputs, ComChannelList outputs, State RX_MM, State IDLE, State POKE_MM, State TX_MM) {
	// (POKE_MM,[A=MM_ACK_OP],[],1,NEXT,1.0)X(TX_MM,[],[])
	POKE_MM.add(new Transition(_internal_vars, inputs, outputs, TX_MM, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!MissionManager.AUDIO_MM_OP_COMM.MM_ACK_OP.equals(_inputs.get(Channels.AUDIO_MM_OP_COMM.name()).value())) {
				return false;
			}
			return true;
		}
	});
	// (POKE_MM,[A=MM_BUSY_OP],[],1,NEXT,1.0)X(IDLE,[],[])
	POKE_MM.add(new Transition(_internal_vars, inputs, outputs, IDLE, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!MissionManager.AUDIO_MM_OP_COMM.MM_BUSY_OP.equals(_inputs.get(Channels.AUDIO_MM_OP_COMM.name()).value())) {
				return false;
			}
			return true;
		}
	});
	// (POKE_MM,[A=MM_POKE_OP],[],2,NEXT,1.0)X(RX_MM,[A=OP_ACK_MM],[])
	POKE_MM.add(new Transition(_internal_vars, inputs, outputs, RX_MM, Duration.NEXT.getRange(), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!MissionManager.AUDIO_MM_OP_COMM.MM_POKE_OP.equals(_inputs.get(Channels.AUDIO_MM_OP_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.AUDIO_OP_MM_COMM.name(), Operator.AUDIO_OP_MM_COMM.OP_ACK_MM);
			return true;
		}
	});
	// (POKE_MM,[A=VO_POKE_OP],[],1,NEXT,1.0)X(POKE_MM,[A=OP_POKE_MM,A=OP_BUSY_VO],[])
	POKE_MM.add(new Transition(_internal_vars, inputs, outputs, POKE_MM, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!VideoOperator.AUDIO_VO_OP_COMM.VO_POKE_OP.equals(_inputs.get(Channels.AUDIO_VO_OP_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.AUDIO_OP_MM_COMM.name(), Operator.AUDIO_OP_MM_COMM.OP_POKE_MM);
			setTempOutput(Channels.AUDIO_OP_VO_COMM.name(), Operator.AUDIO_OP_VO_COMM.OP_BUSY_VO);
			return true;
		}
	});
	// (POKE_MM,[],[],0,OP_POKE_MM,1.0)x(IDLE,[],[])
	POKE_MM.add(new Transition(_internal_vars, inputs, outputs, IDLE, Duration.OP_POKE_MM.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			return true;
		}
	});
	add(POKE_MM);
}
 public void initializeRX_MM(ComChannelList inputs, ComChannelList outputs, State POKE_OGUI, State RX_MM, State IDLE) {
	// (RX_MM,[],[],0,OP_RX_MM,1.0)X(IDLE,[],[])
	RX_MM.add(new Transition(_internal_vars, inputs, outputs, IDLE, Duration.OP_RX_MM.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			return true;
		}
	});
	// (RX_MM,[A=MM_END_OP],[],1,NEXT,1.0)X(IDLE,[],[])
	RX_MM.add(new Transition(_internal_vars, inputs, outputs, IDLE, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!MissionManager.AUDIO_MM_OP_COMM.MM_END_OP.equals(_inputs.get(Channels.AUDIO_MM_OP_COMM.name()).value())) {
				return false;
			}
			return true;
		}
	});
	// (RX_MM,[A=MM_NEW_SEARCH_AOI_OP],[],2,NEXT,1.0)X(IDLE,[],[NEW_SEARCH_AOI=++])
	RX_MM.add(new Transition(_internal_vars, inputs, outputs, IDLE, Duration.NEXT.getRange(), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!MissionManager.AUDIO_MM_OP_COMM.MM_NEW_SEARCH_AOI_OP.equals(_inputs.get(Channels.AUDIO_MM_OP_COMM.name()).value())) {
				return false;
			}
			setTempInternalVar("NEW_SEARCH_AOI", (Integer)_internal_vars.getVariable("++") + 1);
			return true;
		}
	});
	// (RX_MM,[A=MM_TERMINATE_SEARCH_OP,V=UAV_FLYING_NORMAL_OP],[],3,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[SEARCH_ACTIVE=FALSE,LAND=TRUE])
	RX_MM.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 3, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!MissionManager.AUDIO_MM_OP_COMM.MM_TERMINATE_SEARCH_OP.equals(_inputs.get(Channels.AUDIO_MM_OP_COMM.name()).value())) {
				return false;
			}
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_NORMAL_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("SEARCH_ACTIVE", false);
			setTempInternalVar("LAND", true);
			return true;
		}
	});
	// (RX_MM,[A=MM_TERMINATE_SEARCH_OP,V=UAV_FLYING_FLYBY_OP],[],3,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[SEARCH_ACTIVE=FALSE,LAND=TRUE])
	RX_MM.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 3, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!MissionManager.AUDIO_MM_OP_COMM.MM_TERMINATE_SEARCH_OP.equals(_inputs.get(Channels.AUDIO_MM_OP_COMM.name()).value())) {
				return false;
			}
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_FLYBY_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("SEARCH_ACTIVE", false);
			setTempInternalVar("LAND", true);
			return true;
		}
	});
	// (RX_MM,[A=MM_TERMINATE_SEARCH_OP,V=UAV_LOITERING_OP],[],3,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[SEARCH_ACTIVE=FALSE,LAND=TRUE])
	RX_MM.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 3, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!MissionManager.AUDIO_MM_OP_COMM.MM_TERMINATE_SEARCH_OP.equals(_inputs.get(Channels.AUDIO_MM_OP_COMM.name()).value())) {
				return false;
			}
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_LOITERING_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("SEARCH_ACTIVE", false);
			setTempInternalVar("LAND", true);
			return true;
		}
	});
	// (RX_MM,[A=MM_TERMINATE_SEARCH_OP,V=UAV_TAKE_OFF_OP],[],3,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[SEARCH_ACTIVE=FALSE,LAND=TRUE])
	RX_MM.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 3, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!MissionManager.AUDIO_MM_OP_COMM.MM_TERMINATE_SEARCH_OP.equals(_inputs.get(Channels.AUDIO_MM_OP_COMM.name()).value())) {
				return false;
			}
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_TAKE_OFF_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("SEARCH_ACTIVE", false);
			setTempInternalVar("LAND", true);
			return true;
		}
	});
	add(RX_MM);
}
 public void initializeRX_VO(ComChannelList inputs, ComChannelList outputs, State RX_VO, State IDLE) {
	// (RX_VO,[],[],0,OP_RX_VO,1.0)X(IDLE,[],[])
	RX_VO.add(new Transition(_internal_vars, inputs, outputs, IDLE, Duration.OP_RX_VO.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			return true;
		}
	});
	// (RX_VO,[A=VO_END_OP],[],1,NEXT,1.0)X(IDLE,[],[])
	RX_VO.add(new Transition(_internal_vars, inputs, outputs, IDLE, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!VideoOperator.AUDIO_VO_OP_COMM.VO_END_OP.equals(_inputs.get(Channels.AUDIO_VO_OP_COMM.name()).value())) {
				return false;
			}
			return true;
		}
	});
	// (RX_VO,[A=VO_BAD_STREAM_OP],[],2,NEXT,1.0)X(IDLE,[],[BAD_STREAM=TRUE])
	RX_VO.add(new Transition(_internal_vars, inputs, outputs, IDLE, Duration.NEXT.getRange(), 2, 1.0) {
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
 public void initializeEND_OGUI(ComChannelList inputs, ComChannelList outputs, State END_OGUI, State OBSERVING_OGUI) {
	// (END_OGUI,[],[],0,NEXT,1.0)X(OBSERVING_OGUI,[],[])
	END_OGUI.add(new Transition(_internal_vars, inputs, outputs, OBSERVING_OGUI, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			return true;
		}
	});
	add(END_OGUI);
}
 public void initializePOST_FLIGHT(ComChannelList inputs, ComChannelList outputs, State POST_FLIGHT, State POST_FLIGHT_COMPLETE) {
	// (POST_FLIGHT,[],[],0,OP_POST_FLIGHT,1.0)X(POST_FLIGHT_COMPLETE,[D=OP_POST_FLIGHT_COMPLETE_UAV],[])
	POST_FLIGHT.add(new Transition(_internal_vars, inputs, outputs, POST_FLIGHT_COMPLETE, Duration.OP_POST_FLIGHT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			setTempOutput(Channels.DATA_OP_UAV_COMM.name(), Operator.DATA_OP_UAV_COMM.OP_POST_FLIGHT_COMPLETE_UAV);
			return true;
		}
	});
	add(POST_FLIGHT);
}
 public void initializePOKE_OGUI(ComChannelList inputs, ComChannelList outputs, State POKE_OGUI, State IDLE) {
	// (POKE_OGUI,[],[],0,OP_POKE_OGUI,1.0)X(IDLE,[],[])
	POKE_OGUI.add(new Transition(_internal_vars, inputs, outputs, IDLE, Duration.OP_POKE_OGUI.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			return true;
		}
	});
	add(POKE_OGUI);
}
 public void initializeTX_OGUI(ComChannelList inputs, ComChannelList outputs, State POKE_OGUI, State TX_OGUI, State END_OGUI) {
	// (TX_OGUI,[],[],0,OP_TX_OGUI,1.0)X(END_OGUI,[D=OP_END_OGUI],[])
	TX_OGUI.add(new Transition(_internal_vars, inputs, outputs, END_OGUI, Duration.OP_TX_OGUI.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_END_OGUI);
			return true;
		}
	});
	// (TX_OGUI,[],[FLIGHT_PLAN=NEW],1,OP_TX_OGUI,1.0)X(END_OGUI,[D=OP_NEW_FLIGHT_PLAN_OGUI],[FLIGHT_PLAN=CURRENT])
	TX_OGUI.add(new Transition(_internal_vars, inputs, outputs, END_OGUI, Duration.OP_TX_OGUI.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!"NEW".equals(_internal_vars.getVariable ("FLIGHT_PLAN"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_NEW_FLIGHT_PLAN_OGUI);
			setTempInternalVar("FLIGHT_PLAN", "CURRENT");
			return true;
		}
	});
	// (TX_OGUI,[],[LAND=TRUE],1,OP_TX_OGUI,1.0)X(END_OGUI,[D=OP_LAND_OGUI],[LAND=FALSE])
	TX_OGUI.add(new Transition(_internal_vars, inputs, outputs, END_OGUI, Duration.OP_TX_OGUI.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("LAND"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_LAND_OGUI);
			setTempInternalVar("LAND", false);
			return true;
		}
	});
	// (TX_OGUI,[],[LOITER=TRUE],1,OP_TX_OGUI,1.0)X(END_OGUI,[D=OP_LOITER_OGUI],[LOITER=FALSE])
	TX_OGUI.add(new Transition(_internal_vars, inputs, outputs, END_OGUI, Duration.OP_TX_OGUI.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("LOITER"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_LOITER_OGUI);
			setTempInternalVar("LOITER", false);
			return true;
		}
	});
	// (TX_OGUI,[],[RESUME=TRUE],1,OP_TX_OGUI,1.0)X(END_OGUI,[D=OP_RESUME_OGUI],[RESUME=FALSE])
	TX_OGUI.add(new Transition(_internal_vars, inputs, outputs, END_OGUI, Duration.OP_TX_OGUI.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("RESUME"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_RESUME_OGUI);
			setTempInternalVar("RESUME", false);
			return true;
		}
	});
	// (TX_OGUI,[],[HAG_LOW=TRUE],2,OP_TX_OGUI,1.0)X(END_OGUI,[D=OP_MODIFY_FP_OGUI],[])
	TX_OGUI.add(new Transition(_internal_vars, inputs, outputs, END_OGUI, Duration.OP_TX_OGUI.getRange(), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("HAG_LOW"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_MODIFY_FP_OGUI);
			return true;
		}
	});
	// (TX_OGUI,[],[SIGNAL_LOST=TRUE],2,OP_TX_OGUI,1.0)X(END_OGUI,[D=OP_MODIFY_FP_OGUI],[])
	TX_OGUI.add(new Transition(_internal_vars, inputs, outputs, END_OGUI, Duration.OP_TX_OGUI.getRange(), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("SIGNAL_LOST"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_MODIFY_FP_OGUI);
			return true;
		}
	});
	// (TX_OGUI,[],[BATTERY_LOW=TRUE],2,OP_TX_OGUI,1.0)X(END_OGUI,[D=OP_LAND_OGUI],[])
	TX_OGUI.add(new Transition(_internal_vars, inputs, outputs, END_OGUI, Duration.OP_TX_OGUI.getRange(), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("BATTERY_LOW"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_LAND_OGUI);
			return true;
		}
	});
	// (TX_OGUI,[V=OGUI_ALERT_OP,V=UAV_HAG_LOW_OP],[HAG_LOW!=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[])
	TX_OGUI.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.VIDEO_OGUI_OP_COMM.OGUI_ALERT_OP.equals(_inputs.get(Channels.VIDEO_OGUI_OP_COMM.name()).value())) {
				return false;
			}
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_HAG_LOW_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(new Boolean(true).equals(_internal_vars.getVariable ("HAG_LOW"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			return true;
		}
	});
	// (TX_OGUI,[V=OGUI_ALERT_OP,V=UAV_BATTERY_LOW],[BATTERY_LOW!=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[])
	TX_OGUI.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.VIDEO_OGUI_OP_COMM.OGUI_ALERT_OP.equals(_inputs.get(Channels.VIDEO_OGUI_OP_COMM.name()).value())) {
				return false;
			}
			if(!UAV.VIDEO_UAV__COMM.UAV_BATTERY_LOW.equals(_inputs.get(Channels.VIDEO_UAV__COMM.name()).value())) {
				return false;
			}
			if(new Boolean(true).equals(_internal_vars.getVariable ("BATTERY_LOW"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			return true;
		}
	});
	// (TX_OGUI,[V=OGUI_ALERT_OP,V=UAV_SIGNAL_LOST],[SIGNAL_LOST!=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[])
	TX_OGUI.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.VIDEO_OGUI_OP_COMM.OGUI_ALERT_OP.equals(_inputs.get(Channels.VIDEO_OGUI_OP_COMM.name()).value())) {
				return false;
			}
			if(!UAV.VIDEO_UAV__COMM.UAV_SIGNAL_LOST.equals(_inputs.get(Channels.VIDEO_UAV__COMM.name()).value())) {
				return false;
			}
			if(new Boolean(true).equals(_internal_vars.getVariable ("SIGNAL_LOST"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			return true;
		}
	});
	add(TX_OGUI);
}
 public void initializeEND_MM(ComChannelList inputs, ComChannelList outputs, State IDLE, State LAUNCH_UAV, State POKE_MM, State FLYBY_OGUI, State END_MM, State POKE_OGUI) {
	// (END_MM,[V=UAV_FLYING_NORMAL_OP],[NEW_FLIGHT_PLAN=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[NEW_FLIGHT_PLAN=FALSE])
	END_MM.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_NORMAL_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("NEW_FLIGHT_PLAN"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("NEW_FLIGHT_PLAN", false);
			return true;
		}
	});
	// (END_MM,[V=UAV_FLYING_NORMAL_OP],[FLYBY_START_F=TRUE],1,NEXT,1.0)X(FLYBY_OGUI,[],[FLYBY_START_F=FALSE])
	END_MM.add(new Transition(_internal_vars, inputs, outputs, FLYBY_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_NORMAL_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_START_F"))) {
				return false;
			}
			setTempInternalVar("FLYBY_START_F", false);
			return true;
		}
	});
	// (END_MM,[V=UAV_FLYING_NORMAL_OP],[FLYBY_START_T=TRUE],1,NEXT,1.0)X(FLYBY_OGUI,[],[FLYBY_START_T=FALSE])
	END_MM.add(new Transition(_internal_vars, inputs, outputs, FLYBY_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_NORMAL_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_START_T"))) {
				return false;
			}
			setTempInternalVar("FLYBY_START_T", false);
			return true;
		}
	});
	// (END_MM,[V=UAV_FLYING_NORMAL_OP],[LAND=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[LAND=FALSE])
	END_MM.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_NORMAL_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("LAND"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("LAND", false);
			return true;
		}
	});
	// (END_MM,[V=UAV_FLYING_NORMAL_OP],[SEARCH_COMPLETE=TRUE],1,NEXT,1.0)X(POKE_MM,[D=OP_POKE_MM],[SEARCH_COMPLETE=FALSE])
	END_MM.add(new Transition(_internal_vars, inputs, outputs, POKE_MM, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_NORMAL_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("SEARCH_COMPLETE"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_MM_COMM.name(), Operator.DATA_OP_MM_COMM.OP_POKE_MM);
			setTempInternalVar("SEARCH_COMPLETE", false);
			return true;
		}
	});
	// (END_MM,[V=UAV_FLYING_NORMAL_OP],[RESUME=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[RESUME=FALSE])
	END_MM.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_NORMAL_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("RESUME"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("RESUME", false);
			return true;
		}
	});
	// (END_MM,[V=UAV_FLYING_NORMAL_OP],[LOITER=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[LOITER=FALSE])
	END_MM.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_NORMAL_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("LOITER"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("LOITER", false);
			return true;
		}
	});
	// (END_MM,[V=UAV_FLYING_LOITERING_OP],[FLYBY_START_F=TRUE],1,NEXT,1.0)X(FLYBY_OGUI,[],[FLYBY_START_F=FALSE])
	END_MM.add(new Transition(_internal_vars, inputs, outputs, FLYBY_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_LOITERING_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_START_F"))) {
				return false;
			}
			setTempInternalVar("FLYBY_START_F", false);
			return true;
		}
	});
	// (END_MM,[V=UAV_FLYING_LOITERING_OP],[FLYBY_START_T=TRUE],1,NEXT,1.0)X(FLYBY_OGUI,[],[FLYBY_START_T=FALSE])
	END_MM.add(new Transition(_internal_vars, inputs, outputs, FLYBY_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_LOITERING_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_START_T"))) {
				return false;
			}
			setTempInternalVar("FLYBY_START_T", false);
			return true;
		}
	});
	// (END_MM,[V=UAV_FLYING_LOITERING_OP],[LOITER=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[LOITER=FALSE])
	END_MM.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_LOITERING_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("LOITER"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("LOITER", false);
			return true;
		}
	});
	// (END_MM,[V=UAV_FLYING_LOITERING_OP],[NEW_FLIGHT_PLAN=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[NEW_FLIGHT_PLAN=FALSE])
	END_MM.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_LOITERING_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("NEW_FLIGHT_PLAN"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("NEW_FLIGHT_PLAN", false);
			return true;
		}
	});
	// (END_MM,[V=UAV_FLYING_LOITERING_OP],[LAND=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[LAND=FALSE])
	END_MM.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_LOITERING_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("LAND"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("LAND", false);
			return true;
		}
	});
	// (END_MM,[V=UAV_FLYING_LOITERING_OP],[RESUME=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[RESUME=FALSE])
	END_MM.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_LOITERING_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("RESUME"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("RESUME", false);
			return true;
		}
	});
	// (END_MM,[V=UAV_FLYING_LOITERING_OP],[SEARCH_COMPLETE=TRUE],1,NEXT,1.0)X(POKE_MM,[D=OP_POKE_MM],[SEARCH_COMPLETE=FALSE])
	END_MM.add(new Transition(_internal_vars, inputs, outputs, POKE_MM, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_LOITERING_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("SEARCH_COMPLETE"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_MM_COMM.name(), Operator.DATA_OP_MM_COMM.OP_POKE_MM);
			setTempInternalVar("SEARCH_COMPLETE", false);
			return true;
		}
	});
	// (END_MM,[V=UAV_FLYING_FLYBY_OP],[FLYBY_START_F=TRUE],1,NEXT,1.0)X(FLYBY_OGUI,[],[FLYBY_START_F=FALSE])
	END_MM.add(new Transition(_internal_vars, inputs, outputs, FLYBY_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_FLYBY_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_START_F"))) {
				return false;
			}
			setTempInternalVar("FLYBY_START_F", false);
			return true;
		}
	});
	// (END_MM,[V=UAV_FLYING_FLYBY_OP],[FLYBY_START_T=TRUE],1,NEXT,1.0)X(FLYBY_OGUI,[],[FLYBY_START_T=FALSE])
	END_MM.add(new Transition(_internal_vars, inputs, outputs, FLYBY_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_FLYBY_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("FLYBY_START_T"))) {
				return false;
			}
			setTempInternalVar("FLYBY_START_T", false);
			return true;
		}
	});
	// (END_MM,[V=UAV_FLYING_FLYBY_OP],[NEW_FLIGHT_PLAN=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[NEW_FLIGHT_PLAN=FALSE])
	END_MM.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_FLYBY_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("NEW_FLIGHT_PLAN"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("NEW_FLIGHT_PLAN", false);
			return true;
		}
	});
	// (END_MM,[V=UAV_FLYING_FLYBY_OP],[LAND=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[LAND=FALSE])
	END_MM.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_FLYBY_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("LAND"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("LAND", false);
			return true;
		}
	});
	// (END_MM,[V=UAV_FLYING_FLYBY_OP],[LOITER=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[LOITER=FALSE])
	END_MM.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_FLYBY_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("LOITER"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("LOITER", false);
			return true;
		}
	});
	// (END_MM,[V=UAV_FLYING_FLYBY_OP],[RESUME=TRUE],1,NEXT,1.0)X(POKE_OGUI,[D=OP_POKE_OGUI],[RESUME=FALSE])
	END_MM.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_FLYBY_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("RESUME"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_POKE_OGUI);
			setTempInternalVar("RESUME", false);
			return true;
		}
	});
	// (END_MM,[V=UAV_FLYING_FLYBY_OP],[SEARCH_COMPLETE=TRUE],1,NEXT,1.0)X(POKE_MM,[D=OP_POKE_MM],[SEARCH_COMPLETE=FALSE])
	END_MM.add(new Transition(_internal_vars, inputs, outputs, POKE_MM, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_FLYBY_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("SEARCH_COMPLETE"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_MM_COMM.name(), Operator.DATA_OP_MM_COMM.OP_POKE_MM);
			setTempInternalVar("SEARCH_COMPLETE", false);
			return true;
		}
	});
	// (END_MM,[V=UAV_READY_OP],[TAKE_OFF=TRUE],1,NEXT,1.0)X(LAUNCH_UAV,[D=OP_LAUNCH_UAV],[])
	END_MM.add(new Transition(_internal_vars, inputs, outputs, LAUNCH_UAV, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VIDEO_UAV_OP_COMM.UAV_READY_OP.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("TAKE_OFF"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OP_UAV_COMM.name(), Operator.DATA_OP_UAV_COMM.OP_LAUNCH_UAV);
			return true;
		}
	});
	// (END_MM,[],[],0,NEXT,1.0)X(IDLE,[],[])
	END_MM.add(new Transition(_internal_vars, inputs, outputs, IDLE, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			return true;
		}
	});
	add(END_MM);
}
@Override
protected void initializeInternalVariables() {
	_internal_vars.addVariable("FLYBY_START_F", false);
	_internal_vars.addVariable("FLYBY_START_T", false);
	_internal_vars.addVariable("NEW_FLIGHT_PLAN", false);
	_internal_vars.addVariable("LAND", false);
	_internal_vars.addVariable("LOITER", false);
	_internal_vars.addVariable("RESUME", false);
	_internal_vars.addVariable("TAKE_OFF", false);
	_internal_vars.addVariable("SEARCH_COMPLETE", false);
	_internal_vars.addVariable("NEW_SEARCH_AOI", 
	_internal_vars.addVariable("SEARCH_ACTIVE", false);
	_internal_vars.addVariable("BAD_STREAM", false);
	_internal_vars.addVariable("HAG_LOW", false);
	_internal_vars.addVariable("SIGNAL_LOST", false);
	_internal_vars.addVariable("BATTERY_LOW", false);
}
}