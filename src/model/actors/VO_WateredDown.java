package model.actors;

import model.team.Channels;
import model.team.Duration;
import simulator.*;

public class VO_WateredDown extends Actor {

	public VO_WateredDown(ComChannelList inputs, ComChannelList outputs) {
		//initialize name
		setName("VO_WateredDown");

		//initialize states
		State IDLE = new State("IDLE");
		
		//initialize memory
		initializeInternalVariables();

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
				Object AUDIO_MM_VO_COMM = _inputs.get(Channels.AUDIO_MM_VO_COMM.name()).value();
				Integer NEW_TARGET_SIGHTED_F = (Integer) this._internal_vars.getVariable("NEW_TARGET_SIGHTED_F");
				Integer NEW_TARGET_SIGHTED_T = (Integer) this._internal_vars.getVariable("NEW_TARGET_SIGHTED_T");
				if(MissionManager.AUDIO_MM_VO_COMM.MM_POKE_VO.equals(AUDIO_MM_VO_COMM)){
					this.setTempOutput(Channels.AUDIO_VO_MM_COMM.name(), VideoOperator.AUDIO_VO_MM_COMM.VO_ACK_MM);
					return true;
				} else if(MissionManager.AUDIO_MM_VO_COMM.MM_TARGET_DESCRIPTION.equals(AUDIO_MM_VO_COMM)){
					this.setTempOutput(Channels.AUDIO_VO_MM_COMM.name(), null);
					this.setTempInternalVar("TARGET_DESCRIPTION", "CURRENT");
					return true;
				} else if(MissionManager.AUDIO_MM_VO_COMM.MM_TERMINATE_SEARCH.equals(AUDIO_MM_VO_COMM)){
					this.setTempInternalVar("TARGET_DESCRIPTION", null);
					this.setTempOutput(Channels.AUDIO_VO_MM_COMM.name(), null);
				} else if(NEW_TARGET_SIGHTED_F == 1 && MissionManager.AUDIO_MM_VO_COMM.MM_ACK_VO.equals(AUDIO_MM_VO_COMM)){
					this.setTempInternalVar("NEW_TARGET_SIGHTED_F", 2);//advance
					this.setTempOutput(Channels.AUDIO_VO_MM_COMM.name(), VideoOperator.AUDIO_VO_MM_COMM.VO_TARGET_SIGHTED_F);//(IDLE, [MM_ACK_VO], [])->(IDLE, [VO_TARGET_SIGHTED_F], [])
					return true;
				} else if(NEW_TARGET_SIGHTED_T == 1 && MissionManager.AUDIO_MM_VO_COMM.MM_ACK_VO.equals(AUDIO_MM_VO_COMM)){
					this.setTempInternalVar("NEW_TARGET_SIGHTED_T", 2);//advance
					this.setTempOutput(Channels.AUDIO_VO_MM_COMM.name(), VideoOperator.AUDIO_VO_MM_COMM.VO_TARGET_SIGHTED_T);//(IDLE, [MM_ACK_VO], [])->(IDLE, [VO_TARGET_SIGHTED_T], [])
					return true;
				}
				return false;
			}
		});
		IDLE.add(new Transition(_internal_vars,inputs,outputs,IDLE, Duration.RANDOM.getRange()){//(IDLE, [], [])->(IDLE , [], [])
			@Override
			public boolean isEnabled(){
				Object TARGET_DESCRIPTION = this._internal_vars.getVariable("TARGET_DESCRIPTION");
				Integer NEW_TARGET_SIGHTED_F = (Integer) this._internal_vars.getVariable("NEW_TARGET_SIGHTED_F");
				Integer NEW_TARGET_SIGHTED_T = (Integer) this._internal_vars.getVariable("NEW_TARGET_SIGHTED_T");
				if("CURRENT".equals(TARGET_DESCRIPTION) && NEW_TARGET_SIGHTED_F == 0 && NEW_TARGET_SIGHTED_T == 0) {
					//randomly choose whether to use a true or false sighting
					int randInt = (int) (Math.random() * 10);
					if(randInt >= 5){
						this.setTempInternalVar("NEW_TARGET_SIGHTED_F", 1);//advance
					}else{
						this.setTempInternalVar("NEW_TARGET_SIGHTED_T", 1);//advance
					}
					
					this.setTempOutput(Channels.AUDIO_VO_MM_COMM.name(), VideoOperator.AUDIO_VO_MM_COMM.VO_POKE_MM);//(IDLE, [], [])->(IDLE, [(VO_TARGET_SIGHTED_F | VO_TARGET_SIGHTED_T)], [])
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
		this._internal_vars.addVariable("TARGET_DESCRIPTION", null);
	}
	
}