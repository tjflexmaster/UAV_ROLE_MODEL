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

public class VoPokeMMEvent extends simulator.Event {
	public VoPokeMMEvent(final ComChannelList inputs, final ComChannelList outputs)
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
				this.setTempOutput(Channels.AUDIO_VO_MM_COMM.name(), VideoOperator.AUDIO_VO_MM_COMM.VO_POKE_MM);
				return true;
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

	@Override
	public int getWorkload() {
		// TODO Auto-generated method stub
		return 0;
	}
}
