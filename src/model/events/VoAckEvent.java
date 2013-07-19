package model.events;

import model.actors.MissionManager;
import model.actors.VideoOperator;
import model.team.Channels;
import model.team.Duration;
import simulator.ComChannel;
import simulator.ComChannelList;
import simulator.ITransition;
import simulator.State;
import simulator.Transition;

public class VoAckEvent extends simulator.Event {
	public VoAckEvent(final ComChannelList inputs, final ComChannelList outputs)
	{
		//Define internal variables
		_name = "VoAckEvent";
		
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
				ComChannel<Boolean> mm_comm_vo = (ComChannel<Boolean>) _inputs.get(Channels.AUDIO_MM_VO_COMM.name());
				if ( MissionManager.AUDIO_MM_VO_COMM.MM_POKE_VO.equals(mm_comm_vo.value()) ) {
					this.setTempOutput(Channels.AUDIO_VO_MM_COMM.name(), VideoOperator.AUDIO_VO_MM_COMM.VO_ACK_MM);
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
}
