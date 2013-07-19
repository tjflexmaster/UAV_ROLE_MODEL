package model.actors;

import java.util.HashMap;

import model.team.UDO;

import simulator.*;

public class VideoOperatorGui extends Actor {

	public enum VGUI_OGUI_COMM {

	}

	public enum VGUI_VO_COMM {
		VGUI_BAD_STREAM_VO,
		VGUI_FALSE_POSITIVE_VO,
		VGUI_TRUE_POSITIVE_VO,
		VGUI_FLYBY_ANOMALY_F_VO,
		VGUI_FLYBY_ANOMALY_T_VO

	}

	public enum VISUAL_VGUI_MM_COMM {
		VGUI_VALIDATION_REQ_T,
		VGUI_ALERT_MM

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
