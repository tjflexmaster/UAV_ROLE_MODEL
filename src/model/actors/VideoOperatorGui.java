package model.actors;

import java.util.HashMap;

import model.team.UDO;

import simulator.*;

public class VideoOperatorGui extends Actor {

	public enum VGUI_OGUI_COMM {

	}

	public enum VGUI_VO_COMM {
		VGUI_BAD_STREAM,
		VGUI_FALSE_POSITIVE_VO,
		VGUI_TRUE_POSITIVE_VO

	}

	public enum VGUI_MM_COMM {
		VGUI_VALIDATION_REQ_T

	}

	public VideoOperatorGui(ComChannelList inputs, ComChannelList outputs) {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void initializeInternalVariables() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public HashMap<IActor, ITransition> getTransitions() {
		// TODO Auto-generated method stub
		return null;
	}

}
