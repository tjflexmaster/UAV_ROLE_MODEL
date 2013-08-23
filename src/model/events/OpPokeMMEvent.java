package model.events;

import model.actors.MissionManager;
import model.actors.Operator;
import model.team.Channels;
import model.team.Duration;
import simulator.ComChannel;
import simulator.ComChannelList;
import simulator.IState;
import simulator.ITransition;
import simulator.State;
import simulator.Transition;

public class OpPokeMMEvent extends simulator.Event {
	public OpPokeMMEvent(final ComChannelList inputs, final ComChannelList outputs)
	{
		//Define internal variables
		_name = "OpPokeEvent";
		
		//Define the event states
		State state = this.getState();
		_outputs = new ComChannelList();
		_outputs.putAll(outputs);
		//Define the Event transitions
		_transition = new Transition(getInternalVars(), inputs, outputs, state, Duration.NEXT.getRange()) {
			@SuppressWarnings("unchecked")
			@Override 
			public boolean isEnabled() {
				this.setTempOutput(Channels.AUDIO_OP_MM_COMM.name(), Operator.AUDIO_OP_MM_COMM.OP_POKE_MM);
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
	public IState getCurrentState() {
		// TODO Auto-generated method stub
		return null;
	}
}
