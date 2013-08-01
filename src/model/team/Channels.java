package model.team;

import simulator.ComChannel.Type;

public enum Channels {
	NEW_SEARCH_EVENT,
	TERMINATE_SEARCH_EVENT,
	AUDIO_PS_MM_COMM,
	AUDIO_MM_PS_COMM,
	NEW_SEARCH_AREA_EVENT,
	TARGET_DESCRIPTION_EVENT,
	AUDIO_VO_MM_COMM,
	AUDIO_MM_VO_COMM,
	VIDEO_MM_VGUI_COMM,
	AUDIO_OP_MM_COMM,
	AUDIO_MM_OP_COMM,
	VIDEO_VGUI_MM_COMM,
	AUDIO_VGUI_MM_COMM, VIDEO_OGUI_OP_COMM, DATA_OP_OGUI, AUDIO_VO_OP_COMM,
	
	
//	NEW_SEARCH_EVENT(Type.AUDIO),
//	TERMINATE_SEARCH_EVENT(Type.AUDIO),
//	
//	PS_MM_COMM(Type.AUDIO),
//	
//	MM_PS_COMM(Type.AUDIO)
//	;
//	
//	private Type _type;
//	
//	private Channels(Type type)
//	{
//		this._type = type;
//	}
//	
//	public Type type()
//	{
//		return _type;
//	}
	
}
