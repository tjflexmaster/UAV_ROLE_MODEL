package model.events;

import model.actors.MissionManager;
import model.actors.Operator;
import model.actors.VideoOperator;
import model.actors.VideoOperatorGui;
import model.team.Channels;
import model.team.Duration;
import simulator.ComChannel;
import simulator.ComChannelList;
import simulator.IState;
import simulator.ITransition;
import simulator.State;
import simulator.Transition;

public class VguiValidationReqTMMEvent extends simulator.Event {
	public VguiValidationReqTMMEvent(final ComChannelList inputs, final ComChannelList outputs)
	{
		//Define internal variables
		_name = "VguiValidationTEvent";
		
		//Define the event states
		State state = this.getState();
		_outputs = new ComChannelList();
		_outputs.putAll(outputs);
		//Define the Event transitions
		_transition = new Transition(getInternalVars(), inputs, outputs, state) {
			@SuppressWarnings("unchecked")
			@Override 
			public boolean isEnabled() {
				ComChannel<VideoOperatorGui.VISUAL_VGUI_MM_COMM> request = (ComChannel<VideoOperatorGui.VISUAL_VGUI_MM_COMM>) _inputs.get(Channels.VIDEO_VGUI_MM_COMM.name());
				if ( VideoOperatorGui.VISUAL_VGUI_MM_COMM.VGUI_ALERT_MM.equals(request.value()) ) {
					this.setTempOutput(Channels.VIDEO_VGUI_MM_COMM.name(), VideoOperatorGui.VISUAL_VGUI_MM_COMM.VGUI_VALIDATION_REQ_T);
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
