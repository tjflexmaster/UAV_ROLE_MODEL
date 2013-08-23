package model.actors;

import model.team.Channels;
import model.team.Duration;
import simulator.Actor;
import simulator.ComChannelList;
import simulator.State;
import simulator.Transition;

public class UAVHeightAboveGround extends Actor {
public UAVHeightAboveGround(ComChannelList inputs, ComChannelList outputs) {
	State INACTIVE = new State("INACTIVE");
	State LOW = new State("LOW");
	State GOOD = new State("GOOD");
	State CRASHED = new State("CRASHED");
	initializeGOOD(inputs, outputs, LOW, GOOD, INACTIVE);
	initializeLOW(inputs, outputs, GOOD, CRASHED, LOW, INACTIVE);
	initializeINACTIVE(inputs, outputs, INACTIVE, GOOD);
}
 public void initializeGOOD(ComChannelList inputs, ComChannelList outputs, State LOW, State GOOD, State INACTIVE) {
	// (GOOD,[D=UAV_LANDED_UAVHAG],[],1,NEXT,1.0)X(INACTIVE,[D=UAVHAG_INACTIVE_UAV],[])
	GOOD.add(new Transition(_internal_vars, inputs, outputs, INACTIVE, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.DATA_UAV_COMM.UAV_LANDED_UAVHAG.equals(_inputs.get(Channels.DATA_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_UAV_UAV_COMM.name(), UAV.DATA_UAV_COMM.UAVHAG_INACTIVE_UAV);
			return true;
		}
	});
	// (GOOD,[E=HAG_EVENT],[],0,NEXT,1.0)X(LOW,[D=UAVHAG_LOW_UAV],[])
	GOOD.add(new Transition(_internal_vars, inputs, outputs, LOW, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(_inputs.get(Channels.HAG_EVENT.name()).value() == null && !(Boolean)_inputs.get(Channels.HAG_EVENT.name()).value()) {
				return false;
			}
			setTempOutput(Channels.DATA_UAV_UAV_COMM.name(), UAV.DATA_UAV_COMM.UAVHAG_LOW_UAV);
			return true;
		}
	});
	add(GOOD);
}
 public void initializeLOW(ComChannelList inputs, ComChannelList outputs, State GOOD, State CRASHED, State LOW, State INACTIVE) {
	// (LOW,[D=UAV_LANDED_UAVHAG],[],2,NEXT,1.0)X(INACTIVE,[D=UAVHAG_INACTIVE_UAV],[])
	LOW.add(new Transition(_internal_vars, inputs, outputs, INACTIVE, Duration.NEXT.getRange(), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.DATA_UAV_COMM.UAV_LANDED_UAVHAG.equals(_inputs.get(Channels.DATA_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_UAV_UAV_COMM.name(), UAV.DATA_UAV_COMM.UAVHAG_INACTIVE_UAV);
			return true;
		}
	});
	// (LOW,[E=CRASHED_EVENT],[],1,NEXT,1.0)X(CRASHED,[D=UAVHAG_CRASHED_UAV],[])
	LOW.add(new Transition(_internal_vars, inputs, outputs, CRASHED, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(_inputs.get(Channels.CRASHED_EVENT.name()).value() == null && !(Boolean)_inputs.get(Channels.CRASHED_EVENT.name()).value()) {
				return false;
			}
			setTempOutput(Channels.DATA_UAV_UAV_COMM.name(), UAV.DATA_UAV_COMM.UAVHAG_CRASHED_UAV);
			return true;
		}
	});
	// (LOW,[D=OP_MODIFY_FLIGHT_PLAN_UAV],[],0,UAV_ADJUST_PATH,1.0)X(GOOD,[D=UAVHAG_GOOD_UAV],[])
	LOW.add(new Transition(_internal_vars, inputs, outputs, GOOD, Duration.UAV_ADJUST_PATH.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!Operator.VISUAL_OP_UAV_COMM.OP_MODIFY_FLIGHT_PLAN_UAV.equals(_inputs.get(Channels.VISUAL_OP_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_UAV_UAV_COMM.name(), UAV.DATA_UAV_COMM.UAVHAG_GOOD_UAV);
			return true;
		}
	});
	add(LOW);
}
 public void initializeINACTIVE(ComChannelList inputs, ComChannelList outputs, State INACTIVE, State GOOD) {
	// (INACTIVE,[D=OP_TAKE_OFF_UAV],[],0,NEXT,1.0)X(GOOD,[D=UAVHAG_GOOD_UAV],[])
	INACTIVE.add(new Transition(_internal_vars, inputs, outputs, GOOD, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!Operator.VISUAL_OP_UAV_COMM.OP_TAKE_OFF_UAV.equals(_inputs.get(Channels.VISUAL_OP_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_UAV_UAV_COMM.name(), UAV.DATA_UAV_COMM.UAVHAG_GOOD_UAV);
			return true;
		}
	});
	// (INACTIVE,[D=OGUI_TAKE_OFF_UAV],[],0,NEXT,1.0)X(GOOD,[D=UAVHAG_GOOD_UAV],[])
	INACTIVE.add(new Transition(_internal_vars, inputs, outputs, GOOD, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.DATA_OGUI_UAV_COMM.OGUI_TAKE_OFF_UAV.equals(_inputs.get(Channels.DATA_OGUI_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_UAV_UAV_COMM.name(), UAV.DATA_UAV_COMM.UAVHAG_GOOD_UAV);
			return true;
		}
	});
	add(INACTIVE);
}
@Override
protected void initializeInternalVariables() {
}
}