package model.actors;

import java.util.ArrayList;
import java.util.HashMap;

import model.team.Channels;
import model.team.Duration;
import simulator.*;

public class VO_WateredDown extends Actor {

	public VO_WateredDown(ComChannelList inputs, ComChannelList outputs) {
		//initialize name
		_name = "VO_WateredDown";

		//initialize states
		State IDLE = new State("IDLE",0);

		//initialize transitions
		initializeIDLE(inputs, outputs, IDLE);
		this.initializeInternalVariables();
		//initialize current state
		startState(IDLE);
		this.startState(IDLE);
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
				} else if(MissionManager.AUDIO_MM_VO_COMM.MM_TARGET_DESCRIPTION.equals(_inputs.get(Channels.AUDIO_MM_VO_COMM.name()).value())){
					this.setTempOutput(Channels.AUDIO_VO_MM_COMM.name(), null);
					this.setTempInternalVar("TARGET_DESCRIPTION", "CURRENT");
					return true;
				} else if(MissionManager.AUDIO_MM_VO_COMM.MM_TERMINATE_SEARCH.equals(_inputs.get(Channels.AUDIO_MM_VO_COMM.name()).value())){
					this.setTempInternalVar("TARGET_DESCRIPTION", null);
					this.setTempOutput(Channels.AUDIO_VO_MM_COMM.name(), null);
				} else if(((Integer) this._internal_vars.getVariable("NEW_TARGET_SIGHTED_F")) == 1 && MissionManager.AUDIO_MM_VO_COMM.MM_ACK_VO.equals(_inputs.get(Channels.AUDIO_MM_VO_COMM.name()).value())){
					this.setTempInternalVar("NEW_TARGET_SIGHTED_F", 2);//advance
					this.setTempOutput(Channels.AUDIO_VO_MM_COMM.name(), VideoOperator.AUDIO_VO_MM_COMM.VO_TARGET_SIGHTED_F);//(IDLE, [MM_ACK_VO], [])->(IDLE, [VO_TARGET_SIGHTED_F], [])
					return true;
				} else if(((Integer) this._internal_vars.getVariable("NEW_TARGET_SIGHTED_T")) == 1 && MissionManager.AUDIO_MM_VO_COMM.MM_ACK_VO.equals(_inputs.get(Channels.AUDIO_MM_VO_COMM.name()).value())){
					this.setTempInternalVar("NEW_TARGET_SIGHTED_T", 2);//advance
					this.setTempOutput(Channels.AUDIO_VO_MM_COMM.name(), VideoOperator.AUDIO_VO_MM_COMM.VO_TARGET_SIGHTED_T);//(IDLE, [MM_ACK_VO], [])->(IDLE, [VO_TARGET_SIGHTED_T], [])
					return true;
				}
				return false;
			}
		});IDLE.add(new Transition(_internal_vars,inputs,outputs,IDLE, Duration.RANDOM.getRange()){//(IDLE, [], [])->(IDLE , [], [])
			@Override
			public boolean isEnabled(){
				if("CURRENT".equals(this._internal_vars.getVariable("TARGET_DESCRIPTION")) &&((Integer) this._internal_vars.getVariable("NEW_TARGET_SIGHTED_F")) == 0 && ((Integer) this._internal_vars.getVariable("NEW_TARGET_SIGHTED_T")) == 0) {
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
//				else if(((Integer) this._internal_vars.getVariable("NEW_TARGET_SIGHTED_F")) == 2 || ((Integer) this._internal_vars.getVariable("NEW_TARGET_SIGHTED_T")) == 2){
//					if(((Integer) this._internal_vars.getVariable("NEW_TARGET_SIGHTED_F")) == 2){
//						this.setTempInternalVar("NEW_TARGET_SIGHTED_F", 3);//advance
//					} else if(((Integer) this._internal_vars.getVariable("NEW_TARGET_SIGHTED_T")) == 2) {
//						this.setTempInternalVar("NEW_TARGET_SIGHTED_T", 3);//advance
//					}
//					
//					this.setTempOutput(Channels.AUDIO_VO_MM_COMM.name(), VideoOperator.AUDIO_VO_MM_COMM.VO_END_MM);//(IDLE, [MM_ACK_VO], [])->(IDLE, [VO_END_MM], [])
//					return true;
//				}
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

//	@Override
//	public HashMap<IActor, ITransition> getTransitions() {
//		State state = this.getCurrentState();
//		ArrayList<ITransition> enabledTransitions = state.getEnabledTransitions();
//		if(enabledTransitions.size() == 0)
//			return null;
//		ITransition nextTransition = enabledTransitions.get(0);
//		for(ITransition t : enabledTransitions){
//			if(nextTransition.priority() > t.priority()){
//				nextTransition = t;
//			}
//		}
//		HashMap<IActor, ITransition> transitions = new HashMap<IActor, ITransition>();
//		transitions.put(this, nextTransition);
//		return transitions;
//	}
	
}