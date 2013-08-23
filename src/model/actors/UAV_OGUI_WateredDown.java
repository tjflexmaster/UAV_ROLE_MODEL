package model.actors;

import model.team.Channels;
import simulator.ComChannelList;
import simulator.State;
import simulator.Transition;

public class UAV_OGUI_WateredDown extends simulator.Actor {

	public UAV_OGUI_WateredDown(ComChannelList inputs, ComChannelList outputs) {
		//initialize name
		setName("UAV_OGUI_WateredDown");

		//initialize states
		State IDLE = new State("IDLE");

		//initialize memory
		initializeInternalVariables();

		IDLE.add(new Transition(_internal_vars, inputs, outputs, IDLE){
			@Override
			public boolean isEnabled(){
				//default outputs
				Object UAV_STATE = _internal_vars.getVariable("UAV_STATE");
				Object VISUAL_OP_OGUI_COMM = _inputs.get(Channels.VISUAL_OP_OGUI_COMM.name()).value();
				Object VISUAL_OP_UAV_COMM = _inputs.get(Channels.VISUAL_OP_UAV_COMM.name()).value();
				if("LANDED".equals(UAV_STATE)){
					this.setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VISUAL_UAV_OP_COMM.LANDED);
				}else{
					this.setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VISUAL_UAV_OP_COMM.FLYING);
				}
				if(Operator.VISUAL_OP_OGUI_COMM.OP_LAND_UAV.equals(VISUAL_OP_OGUI_COMM)){
					this.setTempOutput(Channels.VIDEO_OGUI_OP_COMM.name(), OperatorGui.VIDEO_OGUI_OP_COMM.OGUI_LANDED);
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
