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

public class OpEndMMEvent extends simulator.Event {
	public OpEndMMEvent(final ComChannelList inputs, final ComChannelList outputs)
	{
		//Define internal variables
		_name = "OpEndEvent";
		
		//Define the event states
		State state = this.getState();
		_outputs = new ComChannelList();
		_outputs.putAll(outputs);
		//Define the Event transitions
		_transition = new Transition(getInternalVars(), inputs, outputs, state, Duration.OP_TX_MM.getRange()) {
			@SuppressWarnings("unchecked")
			@Override 
			public boolean isEnabled() {
				if(MissionManager.AUDIO_MM_OP_COMM.MM_ACK_OP.equals(_inputs.get(Channels.AUDIO_MM_OP_COMM.name()).value())){
					this.setTempOutput(Channels.AUDIO_OP_MM_COMM.name(), Operator.AUDIO_OP_MM_COMM.OP_END_MM);
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

	@Override
	public IState getCurrentState() {
		// TODO Auto-generated method stub
		return null;
	}
}
