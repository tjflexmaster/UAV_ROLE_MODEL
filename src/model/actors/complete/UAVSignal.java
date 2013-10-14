package model.actors.complete;

import model.team.*;
import simulator.*;

public class UAVSignal extends Actor {
public enum DATA_UAVS_UAV_COMM{
	UAVS_OK_UAV,
	UAVS_LOST_UAV,
	UAVS_RESUMED_UAV,
}
public enum DATA_UAVS_VGUI_COMM{
	UAVS_OK_VGUI,
	UAVS_LOST_VGUI,
	UAVS_RESUMED_VGUI,
}
public enum DATA_UAVS_OGUI_COMM{
	UAVS_OK_OGUI,
	UAVS_LOST_OGUI,
	UAVS_RESUMED_OGUI,
}
public UAVSignal(ComChannelList inputs, ComChannelList outputs) {
	State RESUMED = new State("RESUMED");
	State LOST = new State("LOST");
	State OK = new State("OK");
	State IDLE = new State("IDLE");
	initializeIDLE(inputs, outputs, IDLE, OK);
	initializeOK(inputs, outputs, OK, LOST);
	initializeLOST(inputs, outputs, LOST, RESUMED);
}
 public void initializeIDLE(ComChannelList inputs, ComChannelList outputs, State IDLE, State OK) {
	// (IDLE,[D=OP_LAUNCH_UAV],[],1,NEXT,1.0)X(OK,[D=UAVS_OK_OGUI,D=UAVS_OK_VGUI,D=UAVS_OK_UAV],[])
	IDLE.add(new Transition(_internal_vars, inputs, outputs, OK, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!Operator.DATA_OP_UAV_COMM.OP_LAUNCH_UAV.equals(_inputs.get(Channels.DATA_OP_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_UAVS_OGUI_COMM.name(), UAVSignal.DATA_UAVS_OGUI_COMM.UAVS_OK_OGUI);
			setTempOutput(Channels.DATA_UAVS_VGUI_COMM.name(), UAVSignal.DATA_UAVS_VGUI_COMM.UAVS_OK_VGUI);
			setTempOutput(Channels.DATA_UAVS_UAV_COMM.name(), UAVSignal.DATA_UAVS_UAV_COMM.UAVS_OK_UAV);
			return true;
		}
	});
	add(IDLE);
}
 public void initializeOK(ComChannelList inputs, ComChannelList outputs, State OK, State LOST) {
	// (OK,[E=SIGNAL_LOST_UAVS_EVENT],[],1,NEXT,1.0)X(LOST,[D=UAVS_LOST_OGUI,D=UAVS_LOST_VGUI,D=UAVS_LOST_UAV],[])
	OK.add(new Transition(_internal_vars, inputs, outputs, LOST, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(_inputs.get(Channels.SIGNAL_LOST_UAVS_EVENT.name()).value() == null && !(Boolean)_inputs.get(Channels.SIGNAL_LOST_UAVS_EVENT.name()).value()) {
				return false;
			}
			setTempOutput(Channels.DATA_UAVS_OGUI_COMM.name(), UAVSignal.DATA_UAVS_OGUI_COMM.UAVS_LOST_OGUI);
			setTempOutput(Channels.DATA_UAVS_VGUI_COMM.name(), UAVSignal.DATA_UAVS_VGUI_COMM.UAVS_LOST_VGUI);
			setTempOutput(Channels.DATA_UAVS_UAV_COMM.name(), UAVSignal.DATA_UAVS_UAV_COMM.UAVS_LOST_UAV);
			return true;
		}
	});
	add(OK);
}
 public void initializeLOST(ComChannelList inputs, ComChannelList outputs, State LOST, State RESUMED) {
	// (LOST,[E=SIGNAL_BACK_UAVS_EVENT],[],1,NEXT,1.0)X(RESUMED,[D=UAVS_RESUMED_OGUI,D=UAVS_RESUMED_VGUI,D=UAVS_RESUMED_UAV],[])
	LOST.add(new Transition(_internal_vars, inputs, outputs, RESUMED, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(_inputs.get(Channels.SIGNAL_BACK_UAVS_EVENT.name()).value() == null && !(Boolean)_inputs.get(Channels.SIGNAL_BACK_UAVS_EVENT.name()).value()) {
				return false;
			}
			setTempOutput(Channels.DATA_UAVS_OGUI_COMM.name(), UAVSignal.DATA_UAVS_OGUI_COMM.UAVS_RESUMED_OGUI);
			setTempOutput(Channels.DATA_UAVS_VGUI_COMM.name(), UAVSignal.DATA_UAVS_VGUI_COMM.UAVS_RESUMED_VGUI);
			setTempOutput(Channels.DATA_UAVS_UAV_COMM.name(), UAVSignal.DATA_UAVS_UAV_COMM.UAVS_RESUMED_UAV);
			return true;
		}
	});
	add(LOST);
}
@Override
protected void initializeInternalVariables() {
}
}