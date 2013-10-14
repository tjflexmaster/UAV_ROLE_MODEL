package model.events;

import model.actors.Operator;
import model.team.Channels;
import model.team.Duration;
import simulator.ComChannel;
import simulator.ComChannelList;
import simulator.IState;
import simulator.ITransition;
import simulator.State;
import simulator.Transition;
import temp_package.MissionManager;

public class OpAckEvent extends simulator.Event {
	public OpAckEvent(final ComChannelList inputs, final ComChannelList outputs)
	{
		//Define internal variables
		_name = "OoAckEvent";
		
		//Define the event states
		State state = this.getState();
		_outputs = new ComChannelList();
		_outputs.putAll(outputs);
		//Define the Event transitions
		_transition = new Transition(getInternalVars(), inputs, outputs, state, Duration.NEXT.getRange()) {
			@SuppressWarnings("unchecked")
			@Override 
			public boolean isEnabled() {
				
				//TODO Check the simulator to see if we have a transition already
				ComChannel<Boolean> mm_comm_op = (ComChannel<Boolean>) _inputs.get(Channels.AUDIO_MM_OP_COMM.name());
				if ( MissionManager.AUDIO_MM_OP_COMM.MM_POKE_OP.equals(mm_comm_op.value()) ) {
					this.setTempOutput(Channels.AUDIO_OP_MM_COMM.name(), Operator.AUDIO_OP_MM_COMM.OP_ACK_MM);
					return true;
				} else
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
