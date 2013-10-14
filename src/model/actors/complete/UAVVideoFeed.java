package model.actors.complete;

import model.team.*;
import simulator.*;

public class UAVVideoFeed extends Actor {
public enum DATA_UAVVF_UAV_COMM{
	UAVVF_OK_UAV,
	UAVVF_BAD_UAV,
	UAVVF_IDLE_UAV,
}
public enum DATA_UAVVF_VGUI_COMM{
	UAVVF_OK_VGUI,
	UAVVF_BAD_VGUI,
	UAVVF_IDLE_VGUI,
}
public UAVVideoFeed(ComChannelList inputs, ComChannelList outputs) {
	State BAD = new State("BAD");
	State OK = new State("OK");
	State IDLE = new State("IDLE");
	initializeIDLE(inputs, outputs, IDLE, OK);
	initializeOK(inputs, outputs, IDLE, OK, BAD);
	initializeBAD(inputs, outputs, BAD, IDLE);
}
 public void initializeIDLE(ComChannelList inputs, ComChannelList outputs, State IDLE, State OK) {
	// (IDLE,[D=OP_LAUNCH_UAV],[],0,NEXT,1.0)X(OK,[D=UAVVF_OK_VGUI,D=UAVVF_OK_UAV],[])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, OK, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!Operator.DATA_OP_UAV_COMM.OP_LAUNCH_UAV.equals(_inputs.get(Channels.DATA_OP_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_UAVVF_VGUI_COMM.name(), UAVVideoFeed.DATA_UAVVF_VGUI_COMM.UAVVF_OK_VGUI);
			setTempOutput(Channels.DATA_UAVVF_UAV_COMM.name(), UAVVideoFeed.DATA_UAVVF_UAV_COMM.UAVVF_OK_UAV);
			return true;
		}
	});
	add(IDLE);
}
 public void initializeOK(ComChannelList inputs, ComChannelList outputs, State IDLE, State OK, State BAD) {
	// (OK,[E=VSIGNAL_BAD_EVENT],[],1,NEXT,1.0)X(BAD,[D=UAVVF_BAD_VGUI,D=UAVVF_BAD_UAV],[])
	OK.add(new Transition(_internal_vars, inputs, outputs, BAD, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(_inputs.get(Channels.VSIGNAL_BAD_EVENT.name()).value() == null && !(Boolean)_inputs.get(Channels.VSIGNAL_BAD_EVENT.name()).value()) {
				return false;
			}
			setTempOutput(Channels.DATA_UAVVF_VGUI_COMM.name(), UAVVideoFeed.DATA_UAVVF_VGUI_COMM.UAVVF_BAD_VGUI);
			setTempOutput(Channels.DATA_UAVVF_UAV_COMM.name(), UAVVideoFeed.DATA_UAVVF_UAV_COMM.UAVVF_BAD_UAV);
			return true;
		}
	});
	// (OK,[D=UAV_LANDED_OGUI],[],0,NEXT,1.0)X(IDLE,[D=UAVVF_IDLE_VGUI,D=UAVVF_IDLE_UAV],[])
	OK.add(new Transition(_internal_vars, inputs, outputs, IDLE, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.DATA_UAV_OGUI_COMM.UAV_LANDED_OGUI.equals(_inputs.get(Channels.DATA_UAV_OGUI_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_UAVVF_VGUI_COMM.name(), UAVVideoFeed.DATA_UAVVF_VGUI_COMM.UAVVF_IDLE_VGUI);
			setTempOutput(Channels.DATA_UAVVF_UAV_COMM.name(), UAVVideoFeed.DATA_UAVVF_UAV_COMM.UAVVF_IDLE_UAV);
			return true;
		}
	});
	// (OK,[D=UAV_CRASHED_OGUI],[],0,NEXT,1.0)X(IDLE,[D=UAVVF_IDLE_VGUI,D=UAVVF_IDLE_UAV],[])
	OK.add(new Transition(_internal_vars, inputs, outputs, IDLE, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.DATA_UAV_OGUI_COMM.UAV_CRASHED_OGUI.equals(_inputs.get(Channels.DATA_UAV_OGUI_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_UAVVF_VGUI_COMM.name(), UAVVideoFeed.DATA_UAVVF_VGUI_COMM.UAVVF_IDLE_VGUI);
			setTempOutput(Channels.DATA_UAVVF_UAV_COMM.name(), UAVVideoFeed.DATA_UAVVF_UAV_COMM.UAVVF_IDLE_UAV);
			return true;
		}
	});
	add(OK);
}
 public void initializeBAD(ComChannelList inputs, ComChannelList outputs, State BAD, State IDLE) {
	// (BAD,[D=UAV_LANDED_OGUI],[],0,NEXT,1.0)X(IDLE,[D=UAVVF_IDLE_VGUI,D=UAVVF_IDLE_UAV],[])
	BAD.add(new Transition(_internal_vars, inputs, outputs, IDLE, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.DATA_UAV_OGUI_COMM.UAV_LANDED_OGUI.equals(_inputs.get(Channels.DATA_UAV_OGUI_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_UAVVF_VGUI_COMM.name(), UAVVideoFeed.DATA_UAVVF_VGUI_COMM.UAVVF_IDLE_VGUI);
			setTempOutput(Channels.DATA_UAVVF_UAV_COMM.name(), UAVVideoFeed.DATA_UAVVF_UAV_COMM.UAVVF_IDLE_UAV);
			return true;
		}
	});
	// (BAD,[D=UAV_CRASHED_OGUI],[],0,NEXT,1.0)X(IDLE,[D=UAVVF_IDLE_VGUI,D=UAVVF_IDLE_UAV],[])
	BAD.add(new Transition(_internal_vars, inputs, outputs, IDLE, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.DATA_UAV_OGUI_COMM.UAV_CRASHED_OGUI.equals(_inputs.get(Channels.DATA_UAV_OGUI_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_UAVVF_VGUI_COMM.name(), UAVVideoFeed.DATA_UAVVF_VGUI_COMM.UAVVF_IDLE_VGUI);
			setTempOutput(Channels.DATA_UAVVF_UAV_COMM.name(), UAVVideoFeed.DATA_UAVVF_UAV_COMM.UAVVF_IDLE_UAV);
			return true;
		}
	});
	add(BAD);
}
@Override
protected void initializeInternalVariables() {
}
}