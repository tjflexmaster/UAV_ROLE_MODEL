package model.actors.complete;

import model.team.*;
import simulator.*;

public class UAVHeightAboveGround extends Actor {
public enum DATA_UAVHAG_UAV_COMM{
	UAVHAG_GOOD_UAV,
	UAVHAG_INACTIVE_UAV,
	UAVHAG_LOW_UAV,
	UAVHAG_CRASHED_UAV,
}
public enum VIDEO_UAVHAG_OP_COMM{
	UAVHAG_GOOD_OP,
	UAVHAG_INACTIVE_OP,
	UAVHAG_LOW_OP,
	UAVHAG_CRASHED_OP,
	UAVHAGF_GOOD_OP,
}
public enum DATA_UAVHAG_OGUI_COMM{
	UAVHAG_GOOD_OGUI,
	UAVHAG_INACTIVE_OGUI,
	UAVHAG_LOW_OGUI,
	UAVHAG_CRASHED_OGUI,
}
public enum DATA_UAVHAG_VGUI_COMM{
	UAVHAG_GOOD_VGUI,
	UAVHAG_INACTIVE_VGUI,
	UAVHAG_LOW_VGUI,
	UAVHAG_CRASHED_VGUI,
}
public UAVHeightAboveGround(ComChannelList inputs, ComChannelList outputs) {
	State CRASHED = new State("CRASHED");
	State LOW = new State("LOW");
	State GOOD = new State("GOOD");
	State INACTIVE = new State("INACTIVE");
	initializeGOOD(inputs, outputs, LOW, GOOD, INACTIVE);
	initializeLOW(inputs, outputs, GOOD, INACTIVE, LOW, CRASHED);
	initializeINACTIVE(inputs, outputs, INACTIVE, GOOD);
}
 public void initializeGOOD(ComChannelList inputs, ComChannelList outputs, State LOW, State GOOD, State INACTIVE) {
	// (GOOD,[D=UAV_LANDED_OGUI],[],1,NEXT,1.0)X(INACTIVE,[D=UAVHAG_INACTIVE_OGUI,D=UAVHAG_INACTIVE_VGUI,V=UAVHAG_INACTIVE_OP,D=UAVHAG_INACTIVE_UAV],[])
	GOOD.add(new Transition(_internal_vars, inputs, outputs, INACTIVE, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.DATA_UAV_OGUI_COMM.UAV_LANDED_OGUI.equals(_inputs.get(Channels.DATA_UAV_OGUI_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_UAVHAG_OGUI_COMM.name(), UAVHeightAboveGround.DATA_UAVHAG_OGUI_COMM.UAVHAG_INACTIVE_OGUI);
			setTempOutput(Channels.DATA_UAVHAG_VGUI_COMM.name(), UAVHeightAboveGround.DATA_UAVHAG_VGUI_COMM.UAVHAG_INACTIVE_VGUI);
			setTempOutput(Channels.VIDEO_UAVHAG_OP_COMM.name(), UAVHeightAboveGround.VIDEO_UAVHAG_OP_COMM.UAVHAG_INACTIVE_OP);
			setTempOutput(Channels.DATA_UAVHAG_UAV_COMM.name(), UAVHeightAboveGround.DATA_UAVHAG_UAV_COMM.UAVHAG_INACTIVE_UAV);
			return true;
		}
	});
	// (GOOD,[E=HAG_EVENT],[],0,NEXT,1.0)X(LOW,[D=UAVHAG_LOW_OGUI,D=UAVHAG_LOW_VGUI,V=UAVHAG_LOW_OP,D=UAVHAG_LOW_UAV],[])
	GOOD.add(new Transition(_internal_vars, inputs, outputs, LOW, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(_inputs.get(Channels.HAG_EVENT.name()).value() == null && !(Boolean)_inputs.get(Channels.HAG_EVENT.name()).value()) {
				return false;
			}
			setTempOutput(Channels.DATA_UAVHAG_OGUI_COMM.name(), UAVHeightAboveGround.DATA_UAVHAG_OGUI_COMM.UAVHAG_LOW_OGUI);
			setTempOutput(Channels.DATA_UAVHAG_VGUI_COMM.name(), UAVHeightAboveGround.DATA_UAVHAG_VGUI_COMM.UAVHAG_LOW_VGUI);
			setTempOutput(Channels.VIDEO_UAVHAG_OP_COMM.name(), UAVHeightAboveGround.VIDEO_UAVHAG_OP_COMM.UAVHAG_LOW_OP);
			setTempOutput(Channels.DATA_UAVHAG_UAV_COMM.name(), UAVHeightAboveGround.DATA_UAVHAG_UAV_COMM.UAVHAG_LOW_UAV);
			return true;
		}
	});
	add(GOOD);
}
 public void initializeLOW(ComChannelList inputs, ComChannelList outputs, State GOOD, State INACTIVE, State LOW, State CRASHED) {
	// (LOW,[],[],0,UAVHAG_LOW_TO_CRASH,1.0)X(CRASHED,[D=UAVHAG_CRASHED_OGUI,D=UAVHAG_CRASHED_VGUI,V=UAVHAG_CRASHED_OP,D=UAVHAG_CRASHED_UAV],[])
	LOW.add(new Transition(_internal_vars, inputs, outputs, CRASHED, Duration.UAVHAG_LOW_TO_CRASH.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			setTempOutput(Channels.DATA_UAVHAG_OGUI_COMM.name(), UAVHeightAboveGround.DATA_UAVHAG_OGUI_COMM.UAVHAG_CRASHED_OGUI);
			setTempOutput(Channels.DATA_UAVHAG_VGUI_COMM.name(), UAVHeightAboveGround.DATA_UAVHAG_VGUI_COMM.UAVHAG_CRASHED_VGUI);
			setTempOutput(Channels.VIDEO_UAVHAG_OP_COMM.name(), UAVHeightAboveGround.VIDEO_UAVHAG_OP_COMM.UAVHAG_CRASHED_OP);
			setTempOutput(Channels.DATA_UAVHAG_UAV_COMM.name(), UAVHeightAboveGround.DATA_UAVHAG_UAV_COMM.UAVHAG_CRASHED_UAV);
			return true;
		}
	});
	// (LOW,[D=UAV_LANDED_OGUI],[],2,NEXT,1.0)X(INACTIVE,[D=UAVHAG_INACTIVE_VGUI,D=UAVHAG_INACTIVE_OGUI,V=UAVHAG_INACTIVE_OP,D=UAVHAG_INACTIVE_UAV],[])
	LOW.add(new Transition(_internal_vars, inputs, outputs, INACTIVE, Duration.NEXT.getRange(), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.DATA_UAV_OGUI_COMM.UAV_LANDED_OGUI.equals(_inputs.get(Channels.DATA_UAV_OGUI_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_UAVHAG_VGUI_COMM.name(), UAVHeightAboveGround.DATA_UAVHAG_VGUI_COMM.UAVHAG_INACTIVE_VGUI);
			setTempOutput(Channels.DATA_UAVHAG_OGUI_COMM.name(), UAVHeightAboveGround.DATA_UAVHAG_OGUI_COMM.UAVHAG_INACTIVE_OGUI);
			setTempOutput(Channels.VIDEO_UAVHAG_OP_COMM.name(), UAVHeightAboveGround.VIDEO_UAVHAG_OP_COMM.UAVHAG_INACTIVE_OP);
			setTempOutput(Channels.DATA_UAVHAG_UAV_COMM.name(), UAVHeightAboveGround.DATA_UAVHAG_UAV_COMM.UAVHAG_INACTIVE_UAV);
			return true;
		}
	});
	// (LOW,[E=HAG_CRASHED_EVENT],[],1,NEXT,1.0)X(CRASHED,[D=UAVHAG_CRASHED_OGUI,D=UAVHAG_CRASHED_VGUI,V=UAVHAG_CRASHED_OP,D=UAVHAG_CRASHED_UAV],[])
	LOW.add(new Transition(_internal_vars, inputs, outputs, CRASHED, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(_inputs.get(Channels.HAG_CRASHED_EVENT.name()).value() == null && !(Boolean)_inputs.get(Channels.HAG_CRASHED_EVENT.name()).value()) {
				return false;
			}
			setTempOutput(Channels.DATA_UAVHAG_OGUI_COMM.name(), UAVHeightAboveGround.DATA_UAVHAG_OGUI_COMM.UAVHAG_CRASHED_OGUI);
			setTempOutput(Channels.DATA_UAVHAG_VGUI_COMM.name(), UAVHeightAboveGround.DATA_UAVHAG_VGUI_COMM.UAVHAG_CRASHED_VGUI);
			setTempOutput(Channels.VIDEO_UAVHAG_OP_COMM.name(), UAVHeightAboveGround.VIDEO_UAVHAG_OP_COMM.UAVHAG_CRASHED_OP);
			setTempOutput(Channels.DATA_UAVHAG_UAV_COMM.name(), UAVHeightAboveGround.DATA_UAVHAG_UAV_COMM.UAVHAG_CRASHED_UAV);
			return true;
		}
	});
	// (LOW,[D=OGUI_MODIFY_FP_UAV],[],0,UAV_ADJUST_PATH,1.0)X(GOOD,[D=UAVHAG_GOOD_OGUI,D=UAVHAG_GOOD_VGUI,V=UAVHAGF_GOOD_OP,D=UAVHAG_GOOD_UAV],[])
	LOW.add(new Transition(_internal_vars, inputs, outputs, GOOD, Duration.UAV_ADJUST_PATH.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.DATA_OGUI_UAV_COMM.OGUI_MODIFY_FP_UAV.equals(_inputs.get(Channels.DATA_OGUI_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_UAVHAG_OGUI_COMM.name(), UAVHeightAboveGround.DATA_UAVHAG_OGUI_COMM.UAVHAG_GOOD_OGUI);
			setTempOutput(Channels.DATA_UAVHAG_VGUI_COMM.name(), UAVHeightAboveGround.DATA_UAVHAG_VGUI_COMM.UAVHAG_GOOD_VGUI);
			setTempOutput(Channels.VIDEO_UAVHAG_OP_COMM.name(), UAVHeightAboveGround.VIDEO_UAVHAG_OP_COMM.UAVHAGF_GOOD_OP);
			setTempOutput(Channels.DATA_UAVHAG_UAV_COMM.name(), UAVHeightAboveGround.DATA_UAVHAG_UAV_COMM.UAVHAG_GOOD_UAV);
			return true;
		}
	});
	add(LOW);
}
 public void initializeINACTIVE(ComChannelList inputs, ComChannelList outputs, State INACTIVE, State GOOD) {
	// (INACTIVE,[D=OP_LAUNCH_UAV],[],0,NEXT,1.0)X(GOOD,[D=UAVHAG_GOOD_OGUI,D=UAVHAG_GOOD_VGUI,V=UAVHAG_GOOD_OP,D=UAVHAG_GOOD_UAV],[])
	INACTIVE.add(new Transition(_internal_vars, inputs, outputs, GOOD, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!Operator.DATA_OP_UAV_COMM.OP_LAUNCH_UAV.equals(_inputs.get(Channels.DATA_OP_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_UAVHAG_OGUI_COMM.name(), UAVHeightAboveGround.DATA_UAVHAG_OGUI_COMM.UAVHAG_GOOD_OGUI);
			setTempOutput(Channels.DATA_UAVHAG_VGUI_COMM.name(), UAVHeightAboveGround.DATA_UAVHAG_VGUI_COMM.UAVHAG_GOOD_VGUI);
			setTempOutput(Channels.VIDEO_UAVHAG_OP_COMM.name(), UAVHeightAboveGround.VIDEO_UAVHAG_OP_COMM.UAVHAG_GOOD_OP);
			setTempOutput(Channels.DATA_UAVHAG_UAV_COMM.name(), UAVHeightAboveGround.DATA_UAVHAG_UAV_COMM.UAVHAG_GOOD_UAV);
			return true;
		}
	});
	add(INACTIVE);
}
@Override
protected void initializeInternalVariables() {
}
}