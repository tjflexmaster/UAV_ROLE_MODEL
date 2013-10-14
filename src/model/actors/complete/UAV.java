package model.actors.complete;

import model.team.*;
import simulator.*;

public class UAV extends Actor {
public enum DATA_UAV_UAV_COMM{
	UAVB_DEAD_UAV,
}
public enum DATA_UAV_OGUI_COMM{
	UAV_LOITERING_OGUI,
	UAV_FLYING_OGUI,
	UAV_CRASHED_OGUI,
	UAV_TAKE_OFF_OGUI,
	UAV_LANDING_OGUI,
	UAV_FLYING_NORMAL_OGUI,
	UAV_FLYING_FLYBY_OGUI,
	UAV_LANDED_OGUI,
}
public enum VIDEO_UAV_OP_COMM{
	UAV_LOITERING_OP,
	UAV_FLYING_OP,
	UAV_CRASHED_OP,
	UAV_TAKE_OFF_OP,
	UAV_READY_OP,
	UAV_LANDING_OP,
	UAV_FLYING_NORMAL_OP,
	UAV_FLYING_FLYBY_OP,
	UAV_LANDED_OP,
}
public enum DATA_UAV_VGUI_COMM{
	UAV_LOITERING_VGUI,
	UAV_FLYING_VGUI,
	UAV_CRASHED_VGUI,
	UAV_TAKE_OFF_VGUI,
	UAV_READY_VGUI,
	UAV_LANDING_VGUI,
	UAV_FLYING_NORMAL_VGUI,
	UAV_FLYING_FLYBY_VGUI,
	UAV_LANDED_VGUI,
}
public UAV(ComChannelList inputs, ComChannelList outputs) {
	State LANDED = new State("LANDED");
	State FLYING_FLYBY = new State("FLYING_FLYBY");
	State LANDING = new State("LANDING");
	State FLYING_NORMAL = new State("FLYING_NORMAL");
	State READY = new State("READY");
	State CRASHED = new State("CRASHED");
	State FLYING = new State("FLYING");
	State LOITERING = new State("LOITERING");
	State TAKE_OFF = new State("TAKE_OFF");
	initializeFLYING_FLYBY(inputs, outputs, LOITERING, LANDING, FLYING_FLYBY, CRASHED);
	initializeLANDED(inputs, outputs, LANDED, READY);
	initializeLANDING(inputs, outputs, LANDED, LANDING, CRASHED);
	initializeTAKE_OFF(inputs, outputs, CRASHED, FLYING, TAKE_OFF, LOITERING);
	initializeLOITERING(inputs, outputs, LANDING, FLYING, LOITERING, CRASHED);
	initializeFLYING_NORMAL(inputs, outputs, LOITERING, LANDING, FLYING_NORMAL, CRASHED);
	initializeREADY(inputs, outputs, READY, TAKE_OFF);
}
 public void initializeFLYING_FLYBY(ComChannelList inputs, ComChannelList outputs, State LOITERING, State LANDING, State FLYING_FLYBY, State CRASHED) {
	// (FLYING_FLYBY,[D=UAVBAT_DEAD_UAV],[],2,NEXT,1.0)X(CRASHED,[V=UAV_CRASHED_OP,D=UAV_CRASHED_OGUI,D=UAV_CRASHED_VGUI],[])
	FLYING_FLYBY.add(new Transition(_internal_vars, inputs, outputs, CRASHED, Duration.NEXT.getRange(), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAVBattery.DATA_UAVBAT_UAV_COMM.UAVBAT_DEAD_UAV.equals(_inputs.get(Channels.DATA_UAVBAT_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VIDEO_UAV_OP_COMM.UAV_CRASHED_OP);
			setTempOutput(Channels.DATA_UAV_OGUI_COMM.name(), UAV.DATA_UAV_OGUI_COMM.UAV_CRASHED_OGUI);
			setTempOutput(Channels.DATA_UAV_VGUI_COMM.name(), UAV.DATA_UAV_VGUI_COMM.UAV_CRASHED_VGUI);
			return true;
		}
	});
	// (FLYING_FLYBY,[D=UAVHAG_CRASHED_UAV],[],2,NEXT,1.0)X(CRASHED,[V=UAV_CRASHED_OP,D=UAV_CRASHED_OGUI,D=UAV_CRASHED_VGUI],[])
	FLYING_FLYBY.add(new Transition(_internal_vars, inputs, outputs, CRASHED, Duration.NEXT.getRange(), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAVHeightAboveGround.DATA_UAVHAG_UAV_COMM.UAVHAG_CRASHED_UAV.equals(_inputs.get(Channels.DATA_UAVHAG_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VIDEO_UAV_OP_COMM.UAV_CRASHED_OP);
			setTempOutput(Channels.DATA_UAV_OGUI_COMM.name(), UAV.DATA_UAV_OGUI_COMM.UAV_CRASHED_OGUI);
			setTempOutput(Channels.DATA_UAV_VGUI_COMM.name(), UAV.DATA_UAV_VGUI_COMM.UAV_CRASHED_VGUI);
			return true;
		}
	});
	// (FLYING_FLYBY,[D=OGUI_LAND_UAV],[],1,UAV_ADJUST_PATH,1.0)X(LANDING,[D=UAV_LANDING_OGUI,D=UAV_LANDING_VGUI,V=UAV_LANDING_OP],[])
	FLYING_FLYBY.add(new Transition(_internal_vars, inputs, outputs, LANDING, Duration.UAV_ADJUST_PATH.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.DATA_OGUI_UAV_COMM.OGUI_LAND_UAV.equals(_inputs.get(Channels.DATA_OGUI_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_UAV_OGUI_COMM.name(), UAV.DATA_UAV_OGUI_COMM.UAV_LANDING_OGUI);
			setTempOutput(Channels.DATA_UAV_VGUI_COMM.name(), UAV.DATA_UAV_VGUI_COMM.UAV_LANDING_VGUI);
			setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VIDEO_UAV_OP_COMM.UAV_LANDING_OP);
			return true;
		}
	});
	// (FLYING_FLYBY,[D=UAVFP_COMPLETE_UAV],[],1,UAV_ADJUST_PATH,1.0)X(LOITERING,[V=UAV_LOITERING_OP,D=UAV_LOITERING_OGUI,D=UAV_LOITERING_VGUI],[])
	FLYING_FLYBY.add(new Transition(_internal_vars, inputs, outputs, LOITERING, Duration.UAV_ADJUST_PATH.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAVFlightPlan.DATA_UAVFP_UAV_COMM.UAVFP_COMPLETE_UAV.equals(_inputs.get(Channels.DATA_UAVFP_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VIDEO_UAV_OP_COMM.UAV_LOITERING_OP);
			setTempOutput(Channels.DATA_UAV_OGUI_COMM.name(), UAV.DATA_UAV_OGUI_COMM.UAV_LOITERING_OGUI);
			setTempOutput(Channels.DATA_UAV_VGUI_COMM.name(), UAV.DATA_UAV_VGUI_COMM.UAV_LOITERING_VGUI);
			return true;
		}
	});
	// (FLYING_FLYBY,[D=OGUI_LOITER_UAV],[],1,UAV_ADJUST_PATH,1.0)X(LOITERING,[V=UAV_LOITERING_OP,D=UAV_LOITERING_OGUI,D=UAV_LOITERING_VGUI],[])
	FLYING_FLYBY.add(new Transition(_internal_vars, inputs, outputs, LOITERING, Duration.UAV_ADJUST_PATH.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.DATA_OGUI_UAV_COMM.OGUI_LOITER_UAV.equals(_inputs.get(Channels.DATA_OGUI_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VIDEO_UAV_OP_COMM.UAV_LOITERING_OP);
			setTempOutput(Channels.DATA_UAV_OGUI_COMM.name(), UAV.DATA_UAV_OGUI_COMM.UAV_LOITERING_OGUI);
			setTempOutput(Channels.DATA_UAV_VGUI_COMM.name(), UAV.DATA_UAV_VGUI_COMM.UAV_LOITERING_VGUI);
			return true;
		}
	});
	// (FLYING_FLYBY,[D=OGUI_LOITER_UAV],[],1,UAV_ADJUST_PATH,1.0)X(LOITERING,[V=UAV_LOITERING_OP,D=UAV_LOITERING_OGUI,D=UAV_LOITERING_VGUI],[])
	FLYING_FLYBY.add(new Transition(_internal_vars, inputs, outputs, LOITERING, Duration.UAV_ADJUST_PATH.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.DATA_OGUI_UAV_COMM.OGUI_LOITER_UAV.equals(_inputs.get(Channels.DATA_OGUI_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VIDEO_UAV_OP_COMM.UAV_LOITERING_OP);
			setTempOutput(Channels.DATA_UAV_OGUI_COMM.name(), UAV.DATA_UAV_OGUI_COMM.UAV_LOITERING_OGUI);
			setTempOutput(Channels.DATA_UAV_VGUI_COMM.name(), UAV.DATA_UAV_VGUI_COMM.UAV_LOITERING_VGUI);
			return true;
		}
	});
	// (FLYING_FLYBY,[D=OGUI_FLYBY_START_F_UAV],[],1,NEXT,1.0)X(FLYING_FLYBY,[V=UAV_FLYING_FLYBY_OP,D=UAV_FLYING_FLYBY_OGUI,D=UAV_FLYING_FLYBY_VGUI],[FLYBY=TRUE])
	FLYING_FLYBY.add(new Transition(_internal_vars, inputs, outputs, FLYING_FLYBY, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.DATA_OGUI_UAV_COMM.OGUI_FLYBY_START_F_UAV.equals(_inputs.get(Channels.DATA_OGUI_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_FLYBY_OP);
			setTempOutput(Channels.DATA_UAV_OGUI_COMM.name(), UAV.DATA_UAV_OGUI_COMM.UAV_FLYING_FLYBY_OGUI);
			setTempOutput(Channels.DATA_UAV_VGUI_COMM.name(), UAV.DATA_UAV_VGUI_COMM.UAV_FLYING_FLYBY_VGUI);
			setTempInternalVar("FLYBY", true);
			return true;
		}
	});
	// (FLYING_FLYBY,[D=OGUI_FLYBY_START_T_UAV],[],1,NEXT,1.0)X(FLYING_FLYBY,[V=UAV_FLYING_FLYBY_OP,D=UAV_FLYING_FLYBY_OGUI,D=UAV_FLYING_FLYBY_VGUI],[FLYBY=TRUE])
	FLYING_FLYBY.add(new Transition(_internal_vars, inputs, outputs, FLYING_FLYBY, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.DATA_OGUI_UAV_COMM.OGUI_FLYBY_START_T_UAV.equals(_inputs.get(Channels.DATA_OGUI_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_FLYBY_OP);
			setTempOutput(Channels.DATA_UAV_OGUI_COMM.name(), UAV.DATA_UAV_OGUI_COMM.UAV_FLYING_FLYBY_OGUI);
			setTempOutput(Channels.DATA_UAV_VGUI_COMM.name(), UAV.DATA_UAV_VGUI_COMM.UAV_FLYING_FLYBY_VGUI);
			setTempInternalVar("FLYBY", true);
			return true;
		}
	});
	// (FLYING_FLYBY,[D=OGUI_FLYBY_END_UAV],[],1,NEXT,1.0)X(FLYING_FLYBY,[V=UAV_FLYING_FLYBY_OP,D=UAV_FLYING_FLYBY_OGUI,D=UAV_FLYING_FLYBY_VGUI],[FLYBY=FALSE])
	FLYING_FLYBY.add(new Transition(_internal_vars, inputs, outputs, FLYING_FLYBY, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.DATA_OGUI_UAV_COMM.OGUI_FLYBY_END_UAV.equals(_inputs.get(Channels.DATA_OGUI_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_FLYBY_OP);
			setTempOutput(Channels.DATA_UAV_OGUI_COMM.name(), UAV.DATA_UAV_OGUI_COMM.UAV_FLYING_FLYBY_OGUI);
			setTempOutput(Channels.DATA_UAV_VGUI_COMM.name(), UAV.DATA_UAV_VGUI_COMM.UAV_FLYING_FLYBY_VGUI);
			setTempInternalVar("FLYBY", false);
			return true;
		}
	});
	add(FLYING_FLYBY);
}
 public void initializeLANDED(ComChannelList inputs, ComChannelList outputs, State LANDED, State READY) {
	// (LANDED,[D=OP_POST_FLIGHT_COMPLETE_UAV],[],1,NEXT,1.0)X(READY,[V=UAV_READY_OP,D=UAV_READY_VGUI],[])
	LANDED.add(new Transition(_internal_vars, inputs, outputs, READY, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!Operator.DATA_OP_UAV_COMM.OP_POST_FLIGHT_COMPLETE_UAV.equals(_inputs.get(Channels.DATA_OP_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VIDEO_UAV_OP_COMM.UAV_READY_OP);
			setTempOutput(Channels.DATA_UAV_VGUI_COMM.name(), UAV.DATA_UAV_VGUI_COMM.UAV_READY_VGUI);
			return true;
		}
	});
	add(LANDED);
}
 public void initializeLANDING(ComChannelList inputs, ComChannelList outputs, State LANDED, State LANDING, State CRASHED) {
	// (LANDING,[D=UAVB_DEAD_UAV],[],10,NEXT,1.0)X(CRASHED,[V=UAV_CRASHED_OP,D=UAV_CRASHED_OGUI,D=UAV_CRASHED_VGUI],[])
	LANDING.add(new Transition(_internal_vars, inputs, outputs, CRASHED, Duration.NEXT.getRange(), 10, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.DATA_UAV_UAV_COMM.UAVB_DEAD_UAV.equals(_inputs.get(Channels.DATA_UAV_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VIDEO_UAV_OP_COMM.UAV_CRASHED_OP);
			setTempOutput(Channels.DATA_UAV_OGUI_COMM.name(), UAV.DATA_UAV_OGUI_COMM.UAV_CRASHED_OGUI);
			setTempOutput(Channels.DATA_UAV_VGUI_COMM.name(), UAV.DATA_UAV_VGUI_COMM.UAV_CRASHED_VGUI);
			return true;
		}
	});
	// (LANDING,[],[],0,UAV_LANDING,1.0)X(LANDED,[V=UAV_LANDED_OP,D=UAV_LANDED_OGUI,D=UAV_LANDED_VGUI],[])
	LANDING.add(new Transition(_internal_vars, inputs, outputs, LANDED, Duration.UAV_LANDING.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VIDEO_UAV_OP_COMM.UAV_LANDED_OP);
			setTempOutput(Channels.DATA_UAV_OGUI_COMM.name(), UAV.DATA_UAV_OGUI_COMM.UAV_LANDED_OGUI);
			setTempOutput(Channels.DATA_UAV_VGUI_COMM.name(), UAV.DATA_UAV_VGUI_COMM.UAV_LANDED_VGUI);
			return true;
		}
	});
	// (LANDING,[],[],0,UAV_LANDING,1.0)X(LANDED,[V=UAV_LANDED_OP],[])
	LANDING.add(new Transition(_internal_vars, inputs, outputs, LANDED, Duration.UAV_LANDING.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VIDEO_UAV_OP_COMM.UAV_LANDED_OP);
			return true;
		}
	});
	// (LANDING,[D=OGUI_FLYBY_START_F_UAV],[],1,NEXT,1.0)X(LANDING,[V=UAV_LANDING_OP,D=UAV_LANDING_OGUI,D=UAV_LANDING_VGUI],[FLYBY=TRUE])
	LANDING.add(new Transition(_internal_vars, inputs, outputs, LANDING, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.DATA_OGUI_UAV_COMM.OGUI_FLYBY_START_F_UAV.equals(_inputs.get(Channels.DATA_OGUI_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VIDEO_UAV_OP_COMM.UAV_LANDING_OP);
			setTempOutput(Channels.DATA_UAV_OGUI_COMM.name(), UAV.DATA_UAV_OGUI_COMM.UAV_LANDING_OGUI);
			setTempOutput(Channels.DATA_UAV_VGUI_COMM.name(), UAV.DATA_UAV_VGUI_COMM.UAV_LANDING_VGUI);
			setTempInternalVar("FLYBY", true);
			return true;
		}
	});
	// (LANDING,[D=OGUI_FLYBY_START_T_UAV],[],1,NEXT,1.0)X(LANDING,[V=UAV_LANDING_OP,D=UAV_LANDING_OGUI,D=UAV_LANDING_VGUI],[FLYBY=TRUE])
	LANDING.add(new Transition(_internal_vars, inputs, outputs, LANDING, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.DATA_OGUI_UAV_COMM.OGUI_FLYBY_START_T_UAV.equals(_inputs.get(Channels.DATA_OGUI_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VIDEO_UAV_OP_COMM.UAV_LANDING_OP);
			setTempOutput(Channels.DATA_UAV_OGUI_COMM.name(), UAV.DATA_UAV_OGUI_COMM.UAV_LANDING_OGUI);
			setTempOutput(Channels.DATA_UAV_VGUI_COMM.name(), UAV.DATA_UAV_VGUI_COMM.UAV_LANDING_VGUI);
			setTempInternalVar("FLYBY", true);
			return true;
		}
	});
	// (LANDING,[D=OGUI_FLYBY_END_UAV],[],1,NEXT,1.0)X(LANDING,[V=UAV_LANDING_OP,D=UAV_LANDING_OGUI,D=UAV_LANDING_VGUI],[FLYBY=FALSE])
	LANDING.add(new Transition(_internal_vars, inputs, outputs, LANDING, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.DATA_OGUI_UAV_COMM.OGUI_FLYBY_END_UAV.equals(_inputs.get(Channels.DATA_OGUI_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VIDEO_UAV_OP_COMM.UAV_LANDING_OP);
			setTempOutput(Channels.DATA_UAV_OGUI_COMM.name(), UAV.DATA_UAV_OGUI_COMM.UAV_LANDING_OGUI);
			setTempOutput(Channels.DATA_UAV_VGUI_COMM.name(), UAV.DATA_UAV_VGUI_COMM.UAV_LANDING_VGUI);
			setTempInternalVar("FLYBY", false);
			return true;
		}
	});
	add(LANDING);
}
 public void initializeTAKE_OFF(ComChannelList inputs, ComChannelList outputs, State CRASHED, State FLYING, State TAKE_OFF, State LOITERING) {
	// (TAKE_OFF,[D=UAVFP_PAUSED_UAV],[],0,UAV_TAKE_OFF,1.0)X(LOITERING,[V=UAV_LOITERING_OP,D=UAV_LOITERING_OGUI,D=UAV_LOITERING_VGUI],[])
	TAKE_OFF.add(new Transition(_internal_vars, inputs, outputs, LOITERING, Duration.UAV_TAKE_OFF.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAVFlightPlan.DATA_UAVFP_UAV_COMM.UAVFP_PAUSED_UAV.equals(_inputs.get(Channels.DATA_UAVFP_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VIDEO_UAV_OP_COMM.UAV_LOITERING_OP);
			setTempOutput(Channels.DATA_UAV_OGUI_COMM.name(), UAV.DATA_UAV_OGUI_COMM.UAV_LOITERING_OGUI);
			setTempOutput(Channels.DATA_UAV_VGUI_COMM.name(), UAV.DATA_UAV_VGUI_COMM.UAV_LOITERING_VGUI);
			return true;
		}
	});
	// (TAKE_OFF,[D!=UAVFP_PAUSED_UAV],[],0,UAV_TAKE_OFF,1.0)X(FLYING,[V=UAV_FLYING_OP,D=UAV_FLYING_OGUI,D=UAV_FLYING_VGUI],[])
	TAKE_OFF.add(new Transition(_internal_vars, inputs, outputs, FLYING, Duration.UAV_TAKE_OFF.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(UAVFlightPlan.DATA_UAVFP_UAV_COMM.UAVFP_PAUSED_UAV.equals(_inputs.get(Channels.DATA_UAVFP_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_OP);
			setTempOutput(Channels.DATA_UAV_OGUI_COMM.name(), UAV.DATA_UAV_OGUI_COMM.UAV_FLYING_OGUI);
			setTempOutput(Channels.DATA_UAV_VGUI_COMM.name(), UAV.DATA_UAV_VGUI_COMM.UAV_FLYING_VGUI);
			return true;
		}
	});
	// (TAKE_OFF,[D=UAVB_DEAD_UAV],[],10,NEXT,1.0)X(CRASHED,[V=UAV_CRASHED_OP,D=UAV_CRASHED_OGUI,D=UAV_CRASHED_VGUI],[])
	TAKE_OFF.add(new Transition(_internal_vars, inputs, outputs, CRASHED, Duration.NEXT.getRange(), 10, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.DATA_UAV_UAV_COMM.UAVB_DEAD_UAV.equals(_inputs.get(Channels.DATA_UAV_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VIDEO_UAV_OP_COMM.UAV_CRASHED_OP);
			setTempOutput(Channels.DATA_UAV_OGUI_COMM.name(), UAV.DATA_UAV_OGUI_COMM.UAV_CRASHED_OGUI);
			setTempOutput(Channels.DATA_UAV_VGUI_COMM.name(), UAV.DATA_UAV_VGUI_COMM.UAV_CRASHED_VGUI);
			return true;
		}
	});
	// (TAKE_OFF,[],[FLIGHT_PLAN=PAUSED],1,UAV_TAKE_OFF,1.0)X(FLYING,[V=UAV_FLYING_OP,D=UAV_FLYING_OGUI,D=UAV_FLYING_VGUI],[FLIGHT_PLAN=CURRENT])
	TAKE_OFF.add(new Transition(_internal_vars, inputs, outputs, FLYING, Duration.UAV_TAKE_OFF.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!"PAUSED".equals(_internal_vars.getVariable ("FLIGHT_PLAN"))) {
				return false;
			}
			setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_OP);
			setTempOutput(Channels.DATA_UAV_OGUI_COMM.name(), UAV.DATA_UAV_OGUI_COMM.UAV_FLYING_OGUI);
			setTempOutput(Channels.DATA_UAV_VGUI_COMM.name(), UAV.DATA_UAV_VGUI_COMM.UAV_FLYING_VGUI);
			setTempInternalVar("FLIGHT_PLAN", "CURRENT");
			return true;
		}
	});
	// (TAKE_OFF,[],[],0,UAV_TAKE_OFF,1.0)X(LOITERING,[V=UAV_LOITERING_OP,D=UAV_LOITERING_OGUI,D=UAV_LOITERING_VGUI],[])
	TAKE_OFF.add(new Transition(_internal_vars, inputs, outputs, LOITERING, Duration.UAV_TAKE_OFF.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VIDEO_UAV_OP_COMM.UAV_LOITERING_OP);
			setTempOutput(Channels.DATA_UAV_OGUI_COMM.name(), UAV.DATA_UAV_OGUI_COMM.UAV_LOITERING_OGUI);
			setTempOutput(Channels.DATA_UAV_VGUI_COMM.name(), UAV.DATA_UAV_VGUI_COMM.UAV_LOITERING_VGUI);
			return true;
		}
	});
	add(TAKE_OFF);
}
 public void initializeLOITERING(ComChannelList inputs, ComChannelList outputs, State LANDING, State FLYING, State LOITERING, State CRASHED) {
	// (LOITERING,[D=UAVB_DEAD_UAV],[],10,NEXT,1.0)X(CRASHED,[V=UAV_CRASHED_OP,D=UAV_CRASHED_OGUI,D=UAV_CRASHED_VGUI],[])
	LOITERING.add(new Transition(_internal_vars, inputs, outputs, CRASHED, Duration.NEXT.getRange(), 10, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.DATA_UAV_UAV_COMM.UAVB_DEAD_UAV.equals(_inputs.get(Channels.DATA_UAV_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VIDEO_UAV_OP_COMM.UAV_CRASHED_OP);
			setTempOutput(Channels.DATA_UAV_OGUI_COMM.name(), UAV.DATA_UAV_OGUI_COMM.UAV_CRASHED_OGUI);
			setTempOutput(Channels.DATA_UAV_VGUI_COMM.name(), UAV.DATA_UAV_VGUI_COMM.UAV_CRASHED_VGUI);
			return true;
		}
	});
	// (LOITERING,[D=OGUI_RESUME_UAV],[],1,NEXT,1.0)X(FLYING,[V=UAV_FLYING_OP],[])
	LOITERING.add(new Transition(_internal_vars, inputs, outputs, FLYING, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.DATA_OGUI_UAV_COMM.OGUI_RESUME_UAV.equals(_inputs.get(Channels.DATA_OGUI_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_OP);
			return true;
		}
	});
	// (LOITERING,[D=OGUI_LAND_UAV],[],1,NEXT,1.0)X(LANDING,[V=UAV_LANDING_OP,D=UAV_LANDING_OGUI,D=UAV_LANDING_VGUI],[])
	LOITERING.add(new Transition(_internal_vars, inputs, outputs, LANDING, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.DATA_OGUI_UAV_COMM.OGUI_LAND_UAV.equals(_inputs.get(Channels.DATA_OGUI_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VIDEO_UAV_OP_COMM.UAV_LANDING_OP);
			setTempOutput(Channels.DATA_UAV_OGUI_COMM.name(), UAV.DATA_UAV_OGUI_COMM.UAV_LANDING_OGUI);
			setTempOutput(Channels.DATA_UAV_VGUI_COMM.name(), UAV.DATA_UAV_VGUI_COMM.UAV_LANDING_VGUI);
			return true;
		}
	});
	// (LOITERING,[D=OGUI_FLYBY_START_F_UAV],[],1,NEXT,1.0)X(FLYING,[V=UAV_FLYING_OP,D=UAV_FLYING_OGUI,D=UAV_FLYING_VGUI],[FLYBY=TRUE])
	LOITERING.add(new Transition(_internal_vars, inputs, outputs, FLYING, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.DATA_OGUI_UAV_COMM.OGUI_FLYBY_START_F_UAV.equals(_inputs.get(Channels.DATA_OGUI_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_OP);
			setTempOutput(Channels.DATA_UAV_OGUI_COMM.name(), UAV.DATA_UAV_OGUI_COMM.UAV_FLYING_OGUI);
			setTempOutput(Channels.DATA_UAV_VGUI_COMM.name(), UAV.DATA_UAV_VGUI_COMM.UAV_FLYING_VGUI);
			setTempInternalVar("FLYBY", true);
			return true;
		}
	});
	// (LOITERING,[D=OGUI_FLYBY_START_T_UAV],[],1,NEXT,1.0)X(FLYING,[V=UAV_FLYING_OP,D=UAV_FLYING_OGUI,D=UAV_FLYING_VGUI],[FLYBY=TRUE])
	LOITERING.add(new Transition(_internal_vars, inputs, outputs, FLYING, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.DATA_OGUI_UAV_COMM.OGUI_FLYBY_START_T_UAV.equals(_inputs.get(Channels.DATA_OGUI_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_OP);
			setTempOutput(Channels.DATA_UAV_OGUI_COMM.name(), UAV.DATA_UAV_OGUI_COMM.UAV_FLYING_OGUI);
			setTempOutput(Channels.DATA_UAV_VGUI_COMM.name(), UAV.DATA_UAV_VGUI_COMM.UAV_FLYING_VGUI);
			setTempInternalVar("FLYBY", true);
			return true;
		}
	});
	// (LOITERING,[D=OGUI_FLYBY_END_UAV],[],1,NEXT,1.0)X(LOITERING,[V=UAV_LOITERING_OP,D=UAV_LOITERING_OGUI,D=UAV_LOITERING_VGUI],[FLYBY=FALSE])
	LOITERING.add(new Transition(_internal_vars, inputs, outputs, LOITERING, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.DATA_OGUI_UAV_COMM.OGUI_FLYBY_END_UAV.equals(_inputs.get(Channels.DATA_OGUI_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VIDEO_UAV_OP_COMM.UAV_LOITERING_OP);
			setTempOutput(Channels.DATA_UAV_OGUI_COMM.name(), UAV.DATA_UAV_OGUI_COMM.UAV_LOITERING_OGUI);
			setTempOutput(Channels.DATA_UAV_VGUI_COMM.name(), UAV.DATA_UAV_VGUI_COMM.UAV_LOITERING_VGUI);
			setTempInternalVar("FLYBY", false);
			return true;
		}
	});
	// (LOITERING,[D=OGUI_NEW_FP_UAV],[],1,NEXT,1.0)X(FLYING,[V=UAV_FLYING_OP,D=UAV_FLYING_OGUI,D=UAV_FLYING_VGUI],[])
	LOITERING.add(new Transition(_internal_vars, inputs, outputs, FLYING, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.DATA_OGUI_UAV_COMM.OGUI_NEW_FP_UAV.equals(_inputs.get(Channels.DATA_OGUI_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_OP);
			setTempOutput(Channels.DATA_UAV_OGUI_COMM.name(), UAV.DATA_UAV_OGUI_COMM.UAV_FLYING_OGUI);
			setTempOutput(Channels.DATA_UAV_VGUI_COMM.name(), UAV.DATA_UAV_VGUI_COMM.UAV_FLYING_VGUI);
			return true;
		}
	});
	add(LOITERING);
}
 public void initializeFLYING_NORMAL(ComChannelList inputs, ComChannelList outputs, State LOITERING, State LANDING, State FLYING_NORMAL, State CRASHED) {
	// (FLYING_NORMAL,[D=UAVBAT_DEAD_UAV],[],2,NEXT,1.0)X(CRASHED,[V=UAV_CRASHED_OP,D=UAV_CRASHED_OGUI,D=UAV_CRASHED_VGUI],[])
	FLYING_NORMAL.add(new Transition(_internal_vars, inputs, outputs, CRASHED, Duration.NEXT.getRange(), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAVBattery.DATA_UAVBAT_UAV_COMM.UAVBAT_DEAD_UAV.equals(_inputs.get(Channels.DATA_UAVBAT_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VIDEO_UAV_OP_COMM.UAV_CRASHED_OP);
			setTempOutput(Channels.DATA_UAV_OGUI_COMM.name(), UAV.DATA_UAV_OGUI_COMM.UAV_CRASHED_OGUI);
			setTempOutput(Channels.DATA_UAV_VGUI_COMM.name(), UAV.DATA_UAV_VGUI_COMM.UAV_CRASHED_VGUI);
			return true;
		}
	});
	// (FLYING_NORMAL,[D=UAVHAG_CRASHED_UAV],[],2,NEXT,1.0)X(CRASHED,[V=UAV_CRASHED_OP,D=UAV_CRASHED_OGUI,D=UAV_CRASHED_VGUI],[])
	FLYING_NORMAL.add(new Transition(_internal_vars, inputs, outputs, CRASHED, Duration.NEXT.getRange(), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAVHeightAboveGround.DATA_UAVHAG_UAV_COMM.UAVHAG_CRASHED_UAV.equals(_inputs.get(Channels.DATA_UAVHAG_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VIDEO_UAV_OP_COMM.UAV_CRASHED_OP);
			setTempOutput(Channels.DATA_UAV_OGUI_COMM.name(), UAV.DATA_UAV_OGUI_COMM.UAV_CRASHED_OGUI);
			setTempOutput(Channels.DATA_UAV_VGUI_COMM.name(), UAV.DATA_UAV_VGUI_COMM.UAV_CRASHED_VGUI);
			return true;
		}
	});
	// (FLYING_NORMAL,[D=OGUI_LAND_UAV],[],1,UAV_ADJUST_PATH,1.0)X(LANDING,[D=UAV_LANDING_OGUI,D=UAV_LANDING_VGUI,V=UAV_LANDING_OP],[])
	FLYING_NORMAL.add(new Transition(_internal_vars, inputs, outputs, LANDING, Duration.UAV_ADJUST_PATH.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.DATA_OGUI_UAV_COMM.OGUI_LAND_UAV.equals(_inputs.get(Channels.DATA_OGUI_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_UAV_OGUI_COMM.name(), UAV.DATA_UAV_OGUI_COMM.UAV_LANDING_OGUI);
			setTempOutput(Channels.DATA_UAV_VGUI_COMM.name(), UAV.DATA_UAV_VGUI_COMM.UAV_LANDING_VGUI);
			setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VIDEO_UAV_OP_COMM.UAV_LANDING_OP);
			return true;
		}
	});
	// (FLYING_NORMAL,[D=UAVFP_COMPLETE_UAV],[],1,UAV_ADJUST_PATH,1.0)X(LOITERING,[V=UAV_LOITERING_OP,D=UAV_LOITERING_OGUI,D=UAV_LOITERING_VGUI],[])
	FLYING_NORMAL.add(new Transition(_internal_vars, inputs, outputs, LOITERING, Duration.UAV_ADJUST_PATH.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAVFlightPlan.DATA_UAVFP_UAV_COMM.UAVFP_COMPLETE_UAV.equals(_inputs.get(Channels.DATA_UAVFP_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VIDEO_UAV_OP_COMM.UAV_LOITERING_OP);
			setTempOutput(Channels.DATA_UAV_OGUI_COMM.name(), UAV.DATA_UAV_OGUI_COMM.UAV_LOITERING_OGUI);
			setTempOutput(Channels.DATA_UAV_VGUI_COMM.name(), UAV.DATA_UAV_VGUI_COMM.UAV_LOITERING_VGUI);
			return true;
		}
	});
	// (FLYING_NORMAL,[D=OGUI_LOITER_UAV],[],1,UAV_ADJUST_PATH,1.0)X(LOITERING,[V=UAV_LOITERING_OP,D=UAV_LOITERING_OGUI,D=UAV_LOITERING_VGUI],[])
	FLYING_NORMAL.add(new Transition(_internal_vars, inputs, outputs, LOITERING, Duration.UAV_ADJUST_PATH.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.DATA_OGUI_UAV_COMM.OGUI_LOITER_UAV.equals(_inputs.get(Channels.DATA_OGUI_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VIDEO_UAV_OP_COMM.UAV_LOITERING_OP);
			setTempOutput(Channels.DATA_UAV_OGUI_COMM.name(), UAV.DATA_UAV_OGUI_COMM.UAV_LOITERING_OGUI);
			setTempOutput(Channels.DATA_UAV_VGUI_COMM.name(), UAV.DATA_UAV_VGUI_COMM.UAV_LOITERING_VGUI);
			return true;
		}
	});
	// (FLYING_NORMAL,[D=OGUI_LOITER_UAV],[],1,UAV_ADJUST_PATH,1.0)X(LOITERING,[V=UAV_LOITERING_OP,D=UAV_LOITERING_OGUI,D=UAV_LOITERING_VGUI],[])
	FLYING_NORMAL.add(new Transition(_internal_vars, inputs, outputs, LOITERING, Duration.UAV_ADJUST_PATH.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.DATA_OGUI_UAV_COMM.OGUI_LOITER_UAV.equals(_inputs.get(Channels.DATA_OGUI_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VIDEO_UAV_OP_COMM.UAV_LOITERING_OP);
			setTempOutput(Channels.DATA_UAV_OGUI_COMM.name(), UAV.DATA_UAV_OGUI_COMM.UAV_LOITERING_OGUI);
			setTempOutput(Channels.DATA_UAV_VGUI_COMM.name(), UAV.DATA_UAV_VGUI_COMM.UAV_LOITERING_VGUI);
			return true;
		}
	});
	// (FLYING_NORMAL,[D=OGUI_FLYBY_START_F_UAV],[],1,NEXT,1.0)X(FLYING_NORMAL,[V=UAV_FLYING_NORMAL_OP,D=UAV_FLYING_NORMAL_OGUI,D=UAV_FLYING_NORMAL_VGUI],[FLYBY=TRUE])
	FLYING_NORMAL.add(new Transition(_internal_vars, inputs, outputs, FLYING_NORMAL, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.DATA_OGUI_UAV_COMM.OGUI_FLYBY_START_F_UAV.equals(_inputs.get(Channels.DATA_OGUI_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_NORMAL_OP);
			setTempOutput(Channels.DATA_UAV_OGUI_COMM.name(), UAV.DATA_UAV_OGUI_COMM.UAV_FLYING_NORMAL_OGUI);
			setTempOutput(Channels.DATA_UAV_VGUI_COMM.name(), UAV.DATA_UAV_VGUI_COMM.UAV_FLYING_NORMAL_VGUI);
			setTempInternalVar("FLYBY", true);
			return true;
		}
	});
	// (FLYING_NORMAL,[D=OGUI_FLYBY_START_T_UAV],[],1,NEXT,1.0)X(FLYING_NORMAL,[V=UAV_FLYING_NORMAL_OP,D=UAV_FLYING_NORMAL_OGUI,D=UAV_FLYING_NORMAL_VGUI],[FLYBY=TRUE])
	FLYING_NORMAL.add(new Transition(_internal_vars, inputs, outputs, FLYING_NORMAL, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.DATA_OGUI_UAV_COMM.OGUI_FLYBY_START_T_UAV.equals(_inputs.get(Channels.DATA_OGUI_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_NORMAL_OP);
			setTempOutput(Channels.DATA_UAV_OGUI_COMM.name(), UAV.DATA_UAV_OGUI_COMM.UAV_FLYING_NORMAL_OGUI);
			setTempOutput(Channels.DATA_UAV_VGUI_COMM.name(), UAV.DATA_UAV_VGUI_COMM.UAV_FLYING_NORMAL_VGUI);
			setTempInternalVar("FLYBY", true);
			return true;
		}
	});
	// (FLYING_NORMAL,[D=OGUI_FLYBY_END_UAV],[],1,NEXT,1.0)X(FLYING_NORMAL,[V=UAV_FLYING_NORMAL_OP,D=UAV_FLYING_NORMAL_OGUI,D=UAV_FLYING_NORMAL_VGUI],[FLYBY=FALSE])
	FLYING_NORMAL.add(new Transition(_internal_vars, inputs, outputs, FLYING_NORMAL, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.DATA_OGUI_UAV_COMM.OGUI_FLYBY_END_UAV.equals(_inputs.get(Channels.DATA_OGUI_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VIDEO_UAV_OP_COMM.UAV_FLYING_NORMAL_OP);
			setTempOutput(Channels.DATA_UAV_OGUI_COMM.name(), UAV.DATA_UAV_OGUI_COMM.UAV_FLYING_NORMAL_OGUI);
			setTempOutput(Channels.DATA_UAV_VGUI_COMM.name(), UAV.DATA_UAV_VGUI_COMM.UAV_FLYING_NORMAL_VGUI);
			setTempInternalVar("FLYBY", false);
			return true;
		}
	});
	add(FLYING_NORMAL);
}
 public void initializeREADY(ComChannelList inputs, ComChannelList outputs, State READY, State TAKE_OFF) {
	// (READY,[D=OP_LAUNCH_UAV],[],0,NEXT,1.0)X(TAKE_OFF,[V=UAV_TAKE_OFF_OP,D=UAV_TAKE_OFF_OGUI,D=UAV_TAKE_OFF_VGUI],[])
	READY.add(new Transition(_internal_vars, inputs, outputs, TAKE_OFF, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!Operator.DATA_OP_UAV_COMM.OP_LAUNCH_UAV.equals(_inputs.get(Channels.DATA_OP_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VIDEO_UAV_OP_COMM.UAV_TAKE_OFF_OP);
			setTempOutput(Channels.DATA_UAV_OGUI_COMM.name(), UAV.DATA_UAV_OGUI_COMM.UAV_TAKE_OFF_OGUI);
			setTempOutput(Channels.DATA_UAV_VGUI_COMM.name(), UAV.DATA_UAV_VGUI_COMM.UAV_TAKE_OFF_VGUI);
			return true;
		}
	});
	// (READY,[D=OGUI_FLYBY_START_F_UAV],[],0,NEXT,1.0)X(READY,[V=UAV_READY_OP,D=UAV_READY_VGUI],[FLYBY=TRUE])
	READY.add(new Transition(_internal_vars, inputs, outputs, READY, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.DATA_OGUI_UAV_COMM.OGUI_FLYBY_START_F_UAV.equals(_inputs.get(Channels.DATA_OGUI_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VIDEO_UAV_OP_COMM.UAV_READY_OP);
			setTempOutput(Channels.DATA_UAV_VGUI_COMM.name(), UAV.DATA_UAV_VGUI_COMM.UAV_READY_VGUI);
			setTempInternalVar("FLYBY", true);
			return true;
		}
	});
	// (READY,[D=OGUI_FLYBY_START_T_UAV],[],0,NEXT,1.0)X(READY,[V=UAV_READY_OP,D=UAV_READY_VGUI],[FLYBY=TRUE])
	READY.add(new Transition(_internal_vars, inputs, outputs, READY, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.DATA_OGUI_UAV_COMM.OGUI_FLYBY_START_T_UAV.equals(_inputs.get(Channels.DATA_OGUI_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VIDEO_UAV_OP_COMM.UAV_READY_OP);
			setTempOutput(Channels.DATA_UAV_VGUI_COMM.name(), UAV.DATA_UAV_VGUI_COMM.UAV_READY_VGUI);
			setTempInternalVar("FLYBY", true);
			return true;
		}
	});
	// (READY,[D=OGUI_FLYBY_END_UAV],[],1,NEXT,1.0)X(READY,[V=UAV_READY_OP,D=UAV_READY_VGUI],[FLYBY=FALSE])
	READY.add(new Transition(_internal_vars, inputs, outputs, READY, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.DATA_OGUI_UAV_COMM.OGUI_FLYBY_END_UAV.equals(_inputs.get(Channels.DATA_OGUI_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VIDEO_UAV_OP_COMM.UAV_READY_OP);
			setTempOutput(Channels.DATA_UAV_VGUI_COMM.name(), UAV.DATA_UAV_VGUI_COMM.UAV_READY_VGUI);
			setTempInternalVar("FLYBY", false);
			return true;
		}
	});
	add(READY);
}
@Override
protected void initializeInternalVariables() {
	_internal_vars.addVariable("FLIGHT_PLAN", null);
	_internal_vars.addVariable("FLYBY", false);
}
}