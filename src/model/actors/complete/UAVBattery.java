package model.actors.complete;

import model.team.*;
import simulator.*;

public class UAVBattery extends Actor {
public enum DATA_UAVBAT_OGUI_COMM{
	UAVBAT_LOW_OGUI,
	UAVBAT_DEAD_OGUI,
}
public enum DATA_UAVBAT_UAV_COMM{
	UAVBAT_LOW_UAV,
	UAVBAT_DEAD_UAV,
}
public UAVBattery(ComChannelList inputs, ComChannelList outputs) {
	State DEAD = new State("DEAD");
	State LOW = new State("LOW");
	State ACTIVE = new State("ACTIVE");
	State INACTIVE = new State("INACTIVE");
	initializeLOW(inputs, outputs, DEAD, LOW, INACTIVE);
	initializeINACTIVE(inputs, outputs, INACTIVE, ACTIVE);
	initializeACTIVE(inputs, outputs, LOW, ACTIVE, INACTIVE);
}
 public void initializeLOW(ComChannelList inputs, ComChannelList outputs, State DEAD, State LOW, State INACTIVE) {
	// (LOW,[D=UAV_LANDED_OGUI],[],0,NEXT,1.0)X(INACTIVE,[],[])
	LOW.add(new Transition(_internal_vars, inputs, outputs, INACTIVE, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.DATA_UAV_OGUI_COMM.UAV_LANDED_OGUI.equals(_inputs.get(Channels.DATA_UAV_OGUI_COMM.name()).value())) {
				return false;
			}
			return true;
		}
	});
	// (LOW,[],[],0,TIME_TO_DIE,1.0)X(DEAD,[D=UAVBAT_DEAD_OGUI,D=UAVBAT_DEAD_UAV],[])
	LOW.add(new Transition(_internal_vars, inputs, outputs, DEAD, Duration.TIME_TO_DIE.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			setTempOutput(Channels.DATA_UAVBAT_OGUI_COMM.name(), UAVBattery.DATA_UAVBAT_OGUI_COMM.UAVBAT_DEAD_OGUI);
			setTempOutput(Channels.DATA_UAVBAT_UAV_COMM.name(), UAVBattery.DATA_UAVBAT_UAV_COMM.UAVBAT_DEAD_UAV);
			return true;
		}
	});
	add(LOW);
}
 public void initializeINACTIVE(ComChannelList inputs, ComChannelList outputs, State INACTIVE, State ACTIVE) {
	// (INACTIVE,[D=OP_LAUNCH_UAV],[],1,NEXT,1.0)X(ACTIVE,[],[])
	INACTIVE.add(new Transition(_internal_vars, inputs, outputs, ACTIVE, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!Operator.DATA_OP_UAV_COMM.OP_LAUNCH_UAV.equals(_inputs.get(Channels.DATA_OP_UAV_COMM.name()).value())) {
				return false;
			}
			return true;
		}
	});
	add(INACTIVE);
}
 public void initializeACTIVE(ComChannelList inputs, ComChannelList outputs, State LOW, State ACTIVE, State INACTIVE) {
	// (ACTIVE,[D=UAV_LANDED_OGUI],[],0,NEXT,1.0)X(INACTIVE,[],[])
	ACTIVE.add(new Transition(_internal_vars, inputs, outputs, INACTIVE, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.DATA_UAV_OGUI_COMM.UAV_LANDED_OGUI.equals(_inputs.get(Channels.DATA_UAV_OGUI_COMM.name()).value())) {
				return false;
			}
			return true;
		}
	});
	// (ACTIVE,[],[],0,BATTERY_DUR,1.0)X(LOW,[D=UAVBAT_LOW_OGUI,D=UAVBAT_LOW_UAV],[])
	ACTIVE.add(new Transition(_internal_vars, inputs, outputs, LOW, Duration.BATTERY_DUR.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			setTempOutput(Channels.DATA_UAVBAT_OGUI_COMM.name(), UAVBattery.DATA_UAVBAT_OGUI_COMM.UAVBAT_LOW_OGUI);
			setTempOutput(Channels.DATA_UAVBAT_UAV_COMM.name(), UAVBattery.DATA_UAVBAT_UAV_COMM.UAVBAT_LOW_UAV);
			return true;
		}
	});
	add(ACTIVE);
}
@Override
protected void initializeInternalVariables() {
}
}