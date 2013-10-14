package model.actors.complete;

import model.team.*;
import simulator.*;

public class OperatorGui extends Actor {
public enum VIDEO_OGUI_OP_COMM{
	OGUI_SIGNAL_LOST_OP,
	OGUI_HAG_LOW_OP,
	OGUI_BAT_LOW_OP,
}
public enum DATA_OGUI_UAV_COMM{
	OGUI_NEW_FP_UAV,
	OGUI_SEARCH_COMPLETE_UAV,
	OGUI_LAND_UAV,
	OGUI_LOITER_UAV,
	OGUI_RESUME_UAV,
	OGUI_FLYBY_START_F_UAV,
	OGUI_FLYBY_START_T_UAV,
	OGUI_FLYBY_END_UAV,
	OGUI_MODIFY_FP_UAV,
}
public OperatorGui(ComChannelList inputs, ComChannelList outputs) {
	State NORMAL = new State("NORMAL");
	State ALARM = new State("ALARM");
	initializeALARM(inputs, outputs, ALARM, NORMAL);
	initializeNORMAL(inputs, outputs, NORMAL, ALARM);
}
 public void initializeALARM(ComChannelList inputs, ComChannelList outputs, State ALARM, State NORMAL) {
	// (ALARM,[D!=UAVS_LOST_OGUI,D=UAVHAG_LOW_OGUI,D=UAVB_LOW_OGUI],[],1,NEXT,1.0)X(NORMAL,[],[])
	ALARM.add(new Transition(_internal_vars, inputs, outputs, NORMAL, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(UAVSignal.DATA_UAVS_OGUI_COMM.UAVS_LOST_OGUI.equals(_inputs.get(Channels.DATA_UAVS_OGUI_COMM.name()).value())) {
				return false;
			}
			if(!UAVHeightAboveGround.DATA_UAVHAG_OGUI_COMM.UAVHAG_LOW_OGUI.equals(_inputs.get(Channels.DATA_UAVHAG_OGUI_COMM.name()).value())) {
				return false;
			}
			if(!UAV.DATA_UAV_OGUI_COMM.UAVB_LOW_OGUI.equals(_inputs.get(Channels.DATA_UAV_OGUI_COMM.name()).value())) {
				return false;
			}
			return true;
		}
	});
	// (ALARM,[D=UAVFP_COMPLETE_OGUI],[FLIGHT_PLANS>0],0,NEXT,1.0)X(ALARM,[D=OGUI_NEW_FP_UAV],[FLIGHT_PLANS=--])
	ALARM.add(new Transition(_internal_vars, inputs, outputs, ALARM, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAVFlightPlan.DATA_UAVFP_OGUI_COMM.UAVFP_COMPLETE_OGUI.equals(_inputs.get(Channels.DATA_UAVFP_OGUI_COMM.name()).value())) {
				return false;
			}
			if(_internal_vars.getVariable("FLIGHT_PLANS") instanceof Integer && new Integer(0) > (Integer) _internal_vars.getVariable ("FLIGHT_PLANS")) {
				return false;
			}
			setTempOutput(Channels.DATA_OGUI_UAV_COMM.name(), OperatorGui.DATA_OGUI_UAV_COMM.OGUI_NEW_FP_UAV);
			setTempInternalVar("FLIGHT_PLANS", (Integer)_internal_vars.getVariable("--") - 1);
			return true;
		}
	});
	// (ALARM,[D=OP_NEW_FLIGHT_PLAN_OGUI],[],0,NEXT,1.0)X(ALARM,[],[CURRENT_INSTRUCTION=NEW_FP,FLIGHT_PLANS=++])
	ALARM.add(new Transition(_internal_vars, inputs, outputs, ALARM, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!Operator.DATA_OP_OGUI_COMM.OP_NEW_FLIGHT_PLAN_OGUI.equals(_inputs.get(Channels.DATA_OP_OGUI_COMM.name()).value())) {
				return false;
			}
			setTempInternalVar("CURRENT_INSTRUCTION", "NEW_FP");
			setTempInternalVar("FLIGHT_PLANS", (Integer)_internal_vars.getVariable("++") + 1);
			return true;
		}
	});
	// (ALARM,[D=OP_LAND_OGUI],[],0,NEXT,1.0)X(ALARM,[D=OGUI_LAND_UAV],[CURRENT_INSTRUCTION=LAND])
	ALARM.add(new Transition(_internal_vars, inputs, outputs, ALARM, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!Operator.DATA_OP_OGUI_COMM.OP_LAND_OGUI.equals(_inputs.get(Channels.DATA_OP_OGUI_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_OGUI_UAV_COMM.name(), OperatorGui.DATA_OGUI_UAV_COMM.OGUI_LAND_UAV);
			setTempInternalVar("CURRENT_INSTRUCTION", "LAND");
			return true;
		}
	});
	// (ALARM,[D=OP_LOITER_OGUI],[],0,NEXT,1.0)X(ALARM,[D=OGUI_LOITER_UAV],[CURRENT_INSTRUCTION=LOITER])
	ALARM.add(new Transition(_internal_vars, inputs, outputs, ALARM, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!Operator.DATA_OP_OGUI_COMM.OP_LOITER_OGUI.equals(_inputs.get(Channels.DATA_OP_OGUI_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_OGUI_UAV_COMM.name(), OperatorGui.DATA_OGUI_UAV_COMM.OGUI_LOITER_UAV);
			setTempInternalVar("CURRENT_INSTRUCTION", "LOITER");
			return true;
		}
	});
	// (ALARM,[D=OP_RESUME_OGUI],[],0,NEXT,1.0)X(ALARM,[D=OGUI_RESUME_UAV],[CURRENT_INSTRUCTION=RESUME])
	ALARM.add(new Transition(_internal_vars, inputs, outputs, ALARM, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!Operator.DATA_OP_OGUI_COMM.OP_RESUME_OGUI.equals(_inputs.get(Channels.DATA_OP_OGUI_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_OGUI_UAV_COMM.name(), OperatorGui.DATA_OGUI_UAV_COMM.OGUI_RESUME_UAV);
			setTempInternalVar("CURRENT_INSTRUCTION", "RESUME");
			return true;
		}
	});
	// (ALARM,[D=OP_FLYBY_START_F_OGUI],[],0,NEXT,1.0)X(ALARM,[D=OGUI_FLYBY_START_F_UAV],[CURRENT_INSTRUCTION=START_FLYBY_F])
	ALARM.add(new Transition(_internal_vars, inputs, outputs, ALARM, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!Operator.DATA_OP_OGUI_COMM.OP_FLYBY_START_F_OGUI.equals(_inputs.get(Channels.DATA_OP_OGUI_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_OGUI_UAV_COMM.name(), OperatorGui.DATA_OGUI_UAV_COMM.OGUI_FLYBY_START_F_UAV);
			setTempInternalVar("CURRENT_INSTRUCTION", "START_FLYBY_F");
			return true;
		}
	});
	// (ALARM,[D=OP_FLYBY_START_T_OGUI],[],0,NEXT,1.0)X(ALARM,[D=OGUI_FLYBY_START_T_UAV],[CURRENT_INSTRUCTION=START_FLYBY_T])
	ALARM.add(new Transition(_internal_vars, inputs, outputs, ALARM, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!Operator.DATA_OP_OGUI_COMM.OP_FLYBY_START_T_OGUI.equals(_inputs.get(Channels.DATA_OP_OGUI_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_OGUI_UAV_COMM.name(), OperatorGui.DATA_OGUI_UAV_COMM.OGUI_FLYBY_START_T_UAV);
			setTempInternalVar("CURRENT_INSTRUCTION", "START_FLYBY_T");
			return true;
		}
	});
	// (ALARM,[D=OP_FLYBY_END_OGUI],[],0,NEXT,1.0)X(ALARM,[D=OGUI_FLYBY_END_UAV],[CURRENT_INSTRUCTION=END_FLYBY])
	ALARM.add(new Transition(_internal_vars, inputs, outputs, ALARM, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!Operator.DATA_OP_OGUI_COMM.OP_FLYBY_END_OGUI.equals(_inputs.get(Channels.DATA_OP_OGUI_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_OGUI_UAV_COMM.name(), OperatorGui.DATA_OGUI_UAV_COMM.OGUI_FLYBY_END_UAV);
			setTempInternalVar("CURRENT_INSTRUCTION", "END_FLYBY");
			return true;
		}
	});
	// (ALARM,[D=OP_MODIFY_FP_OGUI],[],0,NEXT,1.0)X(ALARM,[D=OGUI_MODIFY_FP_UAV],[CURRENT_INSTRUCTION=MODIFY_FP])
	ALARM.add(new Transition(_internal_vars, inputs, outputs, ALARM, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!Operator.DATA_OP_OGUI_COMM.OP_MODIFY_FP_OGUI.equals(_inputs.get(Channels.DATA_OP_OGUI_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_OGUI_UAV_COMM.name(), OperatorGui.DATA_OGUI_UAV_COMM.OGUI_MODIFY_FP_UAV);
			setTempInternalVar("CURRENT_INSTRUCTION", "MODIFY_FP");
			return true;
		}
	});
	// (ALARM,[D=OP_MODIFY_FP_OGUI],[],0,NEXT,1.0)X(ALARM,[D=OGUI_MODIFY_FP_UAV],[CURRENT_INSTRUCTION=MODIFY_FP])
	ALARM.add(new Transition(_internal_vars, inputs, outputs, ALARM, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!Operator.DATA_OP_OGUI_COMM.OP_MODIFY_FP_OGUI.equals(_inputs.get(Channels.DATA_OP_OGUI_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_OGUI_UAV_COMM.name(), OperatorGui.DATA_OGUI_UAV_COMM.OGUI_MODIFY_FP_UAV);
			setTempInternalVar("CURRENT_INSTRUCTION", "MODIFY_FP");
			return true;
		}
	});
	add(ALARM);
}
 public void initializeNORMAL(ComChannelList inputs, ComChannelList outputs, State NORMAL, State ALARM) {
	// (NORMAL,[D=UAVS_LOST_OGUI],[],1,NEXT,1.0)X(ALARM,[V=OGUI_SIGNAL_LOST_OP],[UAV_S=LOST])
	NORMAL.add(new Transition(_internal_vars, inputs, outputs, ALARM, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAVSignal.DATA_UAVS_OGUI_COMM.UAVS_LOST_OGUI.equals(_inputs.get(Channels.DATA_UAVS_OGUI_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_OGUI_OP_COMM.name(), OperatorGui.VIDEO_OGUI_OP_COMM.OGUI_SIGNAL_LOST_OP);
			setTempInternalVar("UAV_S", "LOST");
			return true;
		}
	});
	// (NORMAL,[D=UAVHAG_LOW_OGUI],[],1,NEXT,1.0)X(ALARM,[V=OGUI_HAG_LOW_OP],[UAV_HAG=LOW])
	NORMAL.add(new Transition(_internal_vars, inputs, outputs, ALARM, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAVHeightAboveGround.DATA_UAVHAG_OGUI_COMM.UAVHAG_LOW_OGUI.equals(_inputs.get(Channels.DATA_UAVHAG_OGUI_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_OGUI_OP_COMM.name(), OperatorGui.VIDEO_OGUI_OP_COMM.OGUI_HAG_LOW_OP);
			setTempInternalVar("UAV_HAG", "LOW");
			return true;
		}
	});
	// (NORMAL,[D=UAVB_LOW_OGUI],[],1,NEXT,1.0)X(ALARM,[V=OGUI_BAT_LOW_OP],[UAV_BAT=LOW])
	NORMAL.add(new Transition(_internal_vars, inputs, outputs, ALARM, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.DATA_UAV_OGUI_COMM.UAVB_LOW_OGUI.equals(_inputs.get(Channels.DATA_UAV_OGUI_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_OGUI_OP_COMM.name(), OperatorGui.VIDEO_OGUI_OP_COMM.OGUI_BAT_LOW_OP);
			setTempInternalVar("UAV_BAT", "LOW");
			return true;
		}
	});
	// (NORMAL,[D=UAVFP_COMPLETE_OGUI],[FLIGHT_PLANS>1],0,NEXT,1.0)X(NORMAL,[D=OGUI_NEW_FP_UAV],[FLIGHT_PLANS=--])
	NORMAL.add(new Transition(_internal_vars, inputs, outputs, NORMAL, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAVFlightPlan.DATA_UAVFP_OGUI_COMM.UAVFP_COMPLETE_OGUI.equals(_inputs.get(Channels.DATA_UAVFP_OGUI_COMM.name()).value())) {
				return false;
			}
			if(_internal_vars.getVariable("FLIGHT_PLANS") instanceof Integer && new Integer(1) > (Integer) _internal_vars.getVariable ("FLIGHT_PLANS")) {
				return false;
			}
			setTempOutput(Channels.DATA_OGUI_UAV_COMM.name(), OperatorGui.DATA_OGUI_UAV_COMM.OGUI_NEW_FP_UAV);
			setTempInternalVar("FLIGHT_PLANS", (Integer)_internal_vars.getVariable("--") - 1);
			return true;
		}
	});
	// (NORMAL,[D=UAVFP_COMPLETE_OGUI],[FLIGHT_PLANS=1],0,NEXT,1.0)X(NORMAL,[D=OGUI_SEARCH_COMPLETE_UAV],[FLIGHT_PLANS=--])
	NORMAL.add(new Transition(_internal_vars, inputs, outputs, NORMAL, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAVFlightPlan.DATA_UAVFP_OGUI_COMM.UAVFP_COMPLETE_OGUI.equals(_inputs.get(Channels.DATA_UAVFP_OGUI_COMM.name()).value())) {
				return false;
			}
			if(!new Integer(1).equals(_internal_vars.getVariable ("FLIGHT_PLANS"))) {
				return false;
			}
			setTempOutput(Channels.DATA_OGUI_UAV_COMM.name(), OperatorGui.DATA_OGUI_UAV_COMM.OGUI_SEARCH_COMPLETE_UAV);
			setTempInternalVar("FLIGHT_PLANS", (Integer)_internal_vars.getVariable("--") - 1);
			return true;
		}
	});
	// (NORMAL,[D=OP_NEW_FLIGHT_PLAN_OGUI],[],0,NEXT,1.0)X(NORMAL,[],[CURRENT_INSTRUCTION=NEW_FP,FLIGHT_PLANS=++])
	NORMAL.add(new Transition(_internal_vars, inputs, outputs, NORMAL, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!Operator.DATA_OP_OGUI_COMM.OP_NEW_FLIGHT_PLAN_OGUI.equals(_inputs.get(Channels.DATA_OP_OGUI_COMM.name()).value())) {
				return false;
			}
			setTempInternalVar("CURRENT_INSTRUCTION", "NEW_FP");
			setTempInternalVar("FLIGHT_PLANS", (Integer)_internal_vars.getVariable("++") + 1);
			return true;
		}
	});
	// (NORMAL,[D=OP_LAND_OGUI],[],0,NEXT,1.0)X(NORMAL,[D=OGUI_LAND_UAV],[CURRENT_INSTRUCTION=LAND])
	NORMAL.add(new Transition(_internal_vars, inputs, outputs, NORMAL, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!Operator.DATA_OP_OGUI_COMM.OP_LAND_OGUI.equals(_inputs.get(Channels.DATA_OP_OGUI_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_OGUI_UAV_COMM.name(), OperatorGui.DATA_OGUI_UAV_COMM.OGUI_LAND_UAV);
			setTempInternalVar("CURRENT_INSTRUCTION", "LAND");
			return true;
		}
	});
	// (NORMAL,[D=OP_LOITER_OGUI],[],0,NEXT,1.0)X(NORMAL,[D=OGUI_LOITER_UAV],[CURRENT_INSTRUCTION=LOITER])
	NORMAL.add(new Transition(_internal_vars, inputs, outputs, NORMAL, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!Operator.DATA_OP_OGUI_COMM.OP_LOITER_OGUI.equals(_inputs.get(Channels.DATA_OP_OGUI_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_OGUI_UAV_COMM.name(), OperatorGui.DATA_OGUI_UAV_COMM.OGUI_LOITER_UAV);
			setTempInternalVar("CURRENT_INSTRUCTION", "LOITER");
			return true;
		}
	});
	// (NORMAL,[D=OP_RESUME_OGUI],[],0,NEXT,1.0)X(NORMAL,[D=OGUI_RESUME_UAV],[CURRENT_INSTRUCTION=RESUME])
	NORMAL.add(new Transition(_internal_vars, inputs, outputs, NORMAL, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!Operator.DATA_OP_OGUI_COMM.OP_RESUME_OGUI.equals(_inputs.get(Channels.DATA_OP_OGUI_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_OGUI_UAV_COMM.name(), OperatorGui.DATA_OGUI_UAV_COMM.OGUI_RESUME_UAV);
			setTempInternalVar("CURRENT_INSTRUCTION", "RESUME");
			return true;
		}
	});
	// (NORMAL,[D=OP_FLYBY_START_F_OGUI],[],0,NEXT,1.0)X(NORMAL,[D=OGUI_FLYBY_START_F_UAV],[CURRENT_INSTRUCTION=START_FLYBY_F])
	NORMAL.add(new Transition(_internal_vars, inputs, outputs, NORMAL, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!Operator.DATA_OP_OGUI_COMM.OP_FLYBY_START_F_OGUI.equals(_inputs.get(Channels.DATA_OP_OGUI_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_OGUI_UAV_COMM.name(), OperatorGui.DATA_OGUI_UAV_COMM.OGUI_FLYBY_START_F_UAV);
			setTempInternalVar("CURRENT_INSTRUCTION", "START_FLYBY_F");
			return true;
		}
	});
	// (NORMAL,[D=OP_FLYBY_START_T_OGUI],[],0,NEXT,1.0)X(NORMAL,[D=OGUI_FLYBY_START_T_UAV],[CURRENT_INSTRUCTION=START_FLYBY_T])
	NORMAL.add(new Transition(_internal_vars, inputs, outputs, NORMAL, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!Operator.DATA_OP_OGUI_COMM.OP_FLYBY_START_T_OGUI.equals(_inputs.get(Channels.DATA_OP_OGUI_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_OGUI_UAV_COMM.name(), OperatorGui.DATA_OGUI_UAV_COMM.OGUI_FLYBY_START_T_UAV);
			setTempInternalVar("CURRENT_INSTRUCTION", "START_FLYBY_T");
			return true;
		}
	});
	// (NORMAL,[D=OP_FLYBY_END_OGUI],[],0,NEXT,1.0)X(NORMAL,[D=OGUI_FLYBY_END_UAV],[CURRENT_INSTRUCTION=END_FLYBY])
	NORMAL.add(new Transition(_internal_vars, inputs, outputs, NORMAL, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!Operator.DATA_OP_OGUI_COMM.OP_FLYBY_END_OGUI.equals(_inputs.get(Channels.DATA_OP_OGUI_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_OGUI_UAV_COMM.name(), OperatorGui.DATA_OGUI_UAV_COMM.OGUI_FLYBY_END_UAV);
			setTempInternalVar("CURRENT_INSTRUCTION", "END_FLYBY");
			return true;
		}
	});
	// (NORMAL,[D=OP_MODIFY_FP_OGUI],[],0,NEXT,1.0)X(NORMAL,[D=OGUI_MODIFY_FP_UAV],[CURRENT_INSTRUCTION=MODIFY_FP])
	NORMAL.add(new Transition(_internal_vars, inputs, outputs, NORMAL, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!Operator.DATA_OP_OGUI_COMM.OP_MODIFY_FP_OGUI.equals(_inputs.get(Channels.DATA_OP_OGUI_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_OGUI_UAV_COMM.name(), OperatorGui.DATA_OGUI_UAV_COMM.OGUI_MODIFY_FP_UAV);
			setTempInternalVar("CURRENT_INSTRUCTION", "MODIFY_FP");
			return true;
		}
	});
	// (NORMAL,[D=OP_MODIFY_FP_OGUI],[],0,NEXT,1.0)X(NORMAL,[D=OGUI_MODIFY_FP_UAV],[CURRENT_INSTRUCTION=MODIFY_FP])
	NORMAL.add(new Transition(_internal_vars, inputs, outputs, NORMAL, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!Operator.DATA_OP_OGUI_COMM.OP_MODIFY_FP_OGUI.equals(_inputs.get(Channels.DATA_OP_OGUI_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_OGUI_UAV_COMM.name(), OperatorGui.DATA_OGUI_UAV_COMM.OGUI_MODIFY_FP_UAV);
			setTempInternalVar("CURRENT_INSTRUCTION", "MODIFY_FP");
			return true;
		}
	});
	add(NORMAL);
}
@Override
protected void initializeInternalVariables() {
	_internal_vars.addVariable("UAV_S", null);
	_internal_vars.addVariable("UAV_HAG", null);
	_internal_vars.addVariable("UAV_BAT", null);
	_internal_vars.addVariable("FLIGHT_PLANS", 0);
	_internal_vars.addVariable("CURRENT_INSTRUCTION", null);
}
}