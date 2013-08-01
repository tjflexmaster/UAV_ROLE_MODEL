package model.actors;

import java.util.HashMap;

import model.team.Channels;
import simulator.*;

public class VOWateredDown extends Actor {

	public VOWateredDown(ComChannelList inputs, ComChannelList outputs) {
		//initialize name
		_name = "VIDEO_OPERATOR";

		//initialize states
		State IDLE = new State("IDLE",0);

		//initialize transitions
		initializeIDLE(inputs, outputs, IDLE);
		
		//initialize current state
		startState(IDLE);
	}

	/**
	 */
	private void initializeIDLE(ComChannelList inputs, ComChannelList outputs, State IDLE) {//(IDLE, [], [])->( , [], [])
		IDLE.add(new Transition(_internal_vars,inputs,outputs,IDLE){//(IDLE, [], [])->(IDLE , [], [])
			@Override
			public boolean isEnabled(){
				if(MissionManager.AUDIO_MM_VO_COMM.MM_POKE_VO.equals(_inputs.get(Channels.AUDIO_MM_VO_COMM.name()).value())){
					this.setTempOutput(Channels.AUDIO_VO_MM_COMM.name(), VideoOperator.AUDIO_VO_MM_COMM.VO_ACK_MM);
					return true;
				} else if(((Integer) this._internal_vars.getVariable("NEW_TARGET_SIGHTED_F")) == 0 && ((Integer) this._internal_vars.getVariable("NEW_TARGET_SIGHTED_T")) == 0) {
					//randomly choose whether to use a true or false sighting
					int randInt = (int) (Math.random() * 10);
					if(randInt >= 5){
						this.setTempInternalVar("NEW_TARGET_SIGHTED_F", 1);//advance
					}else{
						this.setTempInternalVar("NEW_TARGET_SIGHTED_T", 1);//advance
					}
					
					this.setTempOutput(Channels.AUDIO_VO_MM_COMM.name(), VideoOperator.AUDIO_VO_MM_COMM.VO_POKE_MM);//(IDLE, [], [])->(IDLE, [(VO_TARGET_SIGHTED_F | VO_TARGET_SIGHTED_T)], [])
					return true;
				} else if(((Integer) this._internal_vars.getVariable("NEW_TARGET_SIGHTED_F")) == 1 && MissionManager.AUDIO_MM_VO_COMM.MM_ACK_VO.equals(_inputs.get(Channels.AUDIO_MM_VO_COMM.name()).value())){
					this.setTempInternalVar("NEW_TARGET_SIGHTED_F", 2);//advance
					
					this.setTempOutput(Channels.AUDIO_VO_MM_COMM.name(), VideoOperator.AUDIO_VO_MM_COMM.VO_TARGET_SIGHTED_F);//(IDLE, [MM_ACK_VO], [])->(IDLE, [VO_TARGET_SIGHTED_F], [])
					return true;
				} else if(((Integer) this._internal_vars.getVariable("NEW_TARGET_SIGHTED_T")) == 1 && MissionManager.AUDIO_MM_VO_COMM.MM_ACK_VO.equals(_inputs.get(Channels.AUDIO_MM_VO_COMM.name()).value())){
					this.setTempInternalVar("NEW_TARGET_SIGHTED_T", 2);//advance
					
					this.setTempOutput(Channels.AUDIO_VO_MM_COMM.name(), VideoOperator.AUDIO_VO_MM_COMM.VO_TARGET_SIGHTED_T);//(IDLE, [MM_ACK_VO], [])->(IDLE, [VO_TARGET_SIGHTED_T], [])
					return true;
				} else if(((Integer) this._internal_vars.getVariable("NEW_TARGET_SIGHTED_F")) == 2 || ((Integer) this._internal_vars.getVariable("NEW_TARGET_SIGHTED_T")) == 2){
					if(((Integer) this._internal_vars.getVariable("NEW_TARGET_SIGHTED_F")) == 2){
						this.setTempInternalVar("NEW_TARGET_SIGHTED_F", 3);//advance
					} else if(((Integer) this._internal_vars.getVariable("NEW_TARGET_SIGHTED_T")) == 2) {
						this.setTempInternalVar("NEW_TARGET_SIGHTED_T", 3);//advance
					}
					
					this.setTempOutput(Channels.AUDIO_VO_MM_COMM.name(), VideoOperator.AUDIO_VO_MM_COMM.VO_END_MM);//(IDLE, [MM_ACK_VO], [])->(IDLE, [VO_END_MM], [])
					return true;
				}
				return false;
			}
		});
		
		add(IDLE);
	}

	@Override
	protected void initializeInternalVariables() {
		this._internal_vars.addVariable("NEW_TARGET_SIGHTED_T", 0);
		this._internal_vars.addVariable("NEW_TARGET_SIGHTED_F", 0);
	}

	@Override
	public HashMap<IActor, ITransition> getTransitions() {
		// TODO Auto-generated method stub
		return null;
	}
	
}