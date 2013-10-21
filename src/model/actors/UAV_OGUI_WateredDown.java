package model.actors;

import model.team.Channels;
import model.team.Duration;
import simulator.ComChannelList;
import simulator.State;
import simulator.Transition;

public class UAV_OGUI_WateredDown extends simulator.Actor {

	public UAV_OGUI_WateredDown(ComChannelList inputs, ComChannelList outputs) {
		//initialize name
		setName("UAV_OGUI_WateredDown");

		//initialize states
		State IDLE = new State("IDLE");
		State CRASHED = new State("CRASHED");

		//initialize memory
		initializeInternalVariables();
		IDLE.add(new Transition(_internal_vars, inputs, outputs, CRASHED, Duration.UAVBAT_LOW_TO_DEAD.getRange(), 3, 1.0){
			@Override
			public boolean isEnabled(){

				if(OperatorGui.VIDEO_OGUI_OP_COMM.OGUI_BATTERY_LOW.equals(_outputs.get(Channels.VIDEO_OGUI_OP_COMM.name()).value())){
					this.setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VISUAL_UAV_OP_COMM.CRASHED);
					this.setTempOutput(Channels.DATA_UAV_VGUI_COMM.name(), UAV.DATA_UAV_VGUI.CRASHED);
					return true;
				}
				return false;
			}
		});
		IDLE.add(new Transition(_internal_vars, inputs, outputs, IDLE, Duration.UAVBAT_ACTIVE_TO_LOW.getRange()){
			@Override
			public boolean isEnabled(){
				Object UAV_STATE = _internal_vars.getVariable("UAV_STATE");
				if("CRASHED".equals(UAV_STATE)){
					return false;
				}
				if("FLYING".equals(_internal_vars.getVariable("UAV_STATE"))){
					this.setTempOutput(Channels.VIDEO_OGUI_OP_COMM.name(), OperatorGui.VIDEO_OGUI_OP_COMM.OGUI_BATTERY_LOW);
					return true;
				}
				return false;
			}
		});
		IDLE.add(new Transition(_internal_vars, inputs, outputs, IDLE, Duration.NEXT.getRange(), 2, 1.0){
			@Override
			public boolean isEnabled(){
				//default outputs
				Object UAV_STATE = _internal_vars.getVariable("UAV_STATE");
				if("CRASHED".equals(UAV_STATE)){
					return false;
				}
				Object VISUAL_OP_OGUI_COMM = _inputs.get(Channels.VISUAL_OP_OGUI_COMM.name()).value();
				Object VISUAL_OP_UAV_COMM = _inputs.get(Channels.VISUAL_OP_UAV_COMM.name()).value();
				if("LANDED".equals(UAV_STATE)){
					this.setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VISUAL_UAV_OP_COMM.LANDED);
				}else{
					this.setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VISUAL_UAV_OP_COMM.FLYING);
				}
				if(Operator.VISUAL_OP_OGUI_COMM.OP_LAND_UAV.equals(VISUAL_OP_OGUI_COMM)){
					this.setTempOutput(Channels.VIDEO_OGUI_OP_COMM.name(), OperatorGui.VIDEO_OGUI_OP_COMM.OGUI_LANDED);
					this.setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), OperatorGui.VIDEO_OGUI_OP_COMM.OGUI_LANDED);
					this.setTempInternalVar("UAV_STATE", "LANDED");
					return true;
				}
				if(Operator.VISUAL_OP_UAV_COMM.TAKE_OFF.equals(VISUAL_OP_UAV_COMM)){
					this.setTempOutput(Channels.VIDEO_OGUI_OP_COMM.name(), OperatorGui.VIDEO_OGUI_OP_COMM.UAV_FLYING_NORMAL);
					this.setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VISUAL_UAV_OP_COMM.FLYING);
					this.setTempInternalVar("UAV_STATE", "FLYING");
					return true;
				} else if("LANDED".equals(UAV_STATE) && !UAV.VISUAL_UAV_OP_COMM.LANDED.equals(_outputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())){
					this.setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VISUAL_UAV_OP_COMM.LANDED);
					return true;
				} else if("FLYING".equals(UAV_STATE) && !UAV.VISUAL_UAV_OP_COMM.FLYING.equals(_outputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())){
					this.setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VISUAL_UAV_OP_COMM.FLYING);
					return true;
				}
				return false;
			}
		});
		this.add(IDLE);
		
		startState(IDLE);
	}
	
	@Override
	protected void initializeInternalVariables() {
		_internal_vars.addVariable("UAV_STATE", "LANDED");
		
	}


}
