package model.events;

import model.actors.MissionManager;
import model.actors.Operator;
import model.actors.VideoOperator;
import model.team.Channels;
import model.team.Duration;
import simulator.ComChannelList;
import simulator.ITransition;
import simulator.State;
import simulator.Transition;

public class VoEndMMEvent extends simulator.Event {
	public VoEndMMEvent(final ComChannelList inputs, final ComChannelList outputs)
	{
		//Define internal variables
		_name = "VoPokeEvent";
		
		//Define the event states
		State state = this.getState();
		_outputs = new ComChannelList();
		_outputs.putAll(outputs);
		//Define the Event transitions
		_transition = new Transition(getInternalVars(), inputs, outputs, state, Duration.VO_TX_MM.getRange()) {
			@SuppressWarnings("unchecked")
			@Override 
			public boolean isEnabled() {
				if(MissionManager.AUDIO_MM_VO_COMM.MM_ACK_VO.equals(_inputs.get(Channels.AUDIO_MM_VO_COMM.name()).value())){
					this.setTempOutput(Channels.AUDIO_VO_MM_COMM.name(), VideoOperator.AUDIO_VO_MM_COMM.VO_END_MM);
					return true;
				}
				return false;
			};
		};
		state.add(_transition);
		
	}

	@Override
	public ITransition getEnabledTransition() {
		if(_transition.isEnabled())
			return _transition;
		return null;
	}
}
