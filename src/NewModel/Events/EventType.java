package NewModel.Events;

public enum EventType {
	/**
	 * UAV Events
	 */
	UAV_BAD_PATH,
	UAV_LOST_SIGNAL,
	UAV_LOW_BATTERY,
	UAV_LOW_HAG,
	
	/**
	 * Parent search Events
	 */
	PS_TERMINATE_SEARCH,
	PS_NEW_AOI,
	
	/**
	 * UGUI Events
	 */
	UGUI_INACCESSIBLE,
	
	/**
	 * Video GUI Events
	 */
	VGUI_INACCESSIBLE,
	VGUI_FALSE_POSITIVE,
	VGUI_TRUE_POSITIVE,
	
}
