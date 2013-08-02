package model.actors;

import model.team.Channels;
import simulator.ComChannelList;
import simulator.State;
import simulator.Transition;

public class UAV_OGUI_WateredDown extends simulator.Actor {

	public UAV_OGUI_WateredDown(ComChannelList inputs, ComChannelList outputs) {
		_name = "UAV_OGUI_WateredDown";
		
		State IDLE = new State("IDLE",0);
		
		this.initializeInternalVariables();
		
		this._name = "UAV/OGUI";
		
		this.startState(IDLE);

		IDLE.add(new Transition(_internal_vars, inputs, outputs, IDLE){
			@Override
			public boolean isEnabled(){
				//default outputs
				if(_internal_vars.getVariable("UAV_STATE").equals("LANDED")){
					this.setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VISUAL_UAV_OP_COMM.LANDED);
				}else{
					this.setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VISUAL_UAV_OP_COMM.FLYING);
				}
				if(Operator.DATA_OP_OGUI_COMM.OP_LAND_UAV.equals(_inputs.get(Channels.DATA_OP_OGUI_COMM.name()).value())){
					this.setTempOutput(Channels.VIDEO_OGUI_OP_COMM.name(), OperatorGui.VIDEO_OGUI_OP_COMM.OGUI_LANDED);
					return true;
				}
				if(Operator.DATA_OP_UAV_COMM.TAKE_OFF.equals(_inputs.get(Channels.DATA_OP_UAV.name()).value())){
					this.setTempOutput(Channels.VIDEO_OGUI_OP_COMM.name(), OperatorGui.VIDEO_OGUI_OP_COMM.UAV_FLYING_NORMAL);
					this.setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VISUAL_UAV_OP_COMM.FLYING);
					this.setTempInternalVar("UAV_STATE", "FLYING");
					return true;
				} else if(_internal_vars.getVariable("UAV_STATE").equals("LANDED") && !UAV.VISUAL_UAV_OP_COMM.LANDED.equals(_outputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())){
					this.setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VISUAL_UAV_OP_COMM.LANDED);
					return true;
				} else if(_internal_vars.getVariable("UAV_STATE").equals("FLYING") && !UAV.VISUAL_UAV_OP_COMM.FLYING.equals(_outputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())){
					this.setTempOutput(Channels.VIDEO_UAV_OP_COMM.name(), UAV.VISUAL_UAV_OP_COMM.FLYING);
					return true;
				}
				return false;
			}
		});
		this.add(IDLE);
	}
	
	@Override
	protected void initializeInternalVariables() {
		_internal_vars.addVariable("UAV_STATE", "LANDED");
		
	}


}
