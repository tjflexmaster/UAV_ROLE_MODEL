package WiSAR;

import CUAS.Utils.Range;
import NewModel.Events.IEvent;

public enum EventEnum implements IEvent {
	/**
	 * UAV Events
	 */
	UAV_BAD_PATH(new Range(60, 1800)),
	UAV_LOST_SIGNAL(new Range(30, 600)),
	UAV_LOW_BATTERY(new Range(60, 600)),
	UAV_LOW_HAG(new Range(120, 600)),
	
	/**
	 * Parent search Events
	 */
	PS_TERMINATE_SEARCH(new Range()),
	PS_NEW_AOI(new Range()),
	
	/**
	 * UGUI Events
	 */
	UGUI_INACCESSIBLE(new Range(60, 1800)),
	
	/**
	 * Video GUI Events
	 */
	VGUI_INACCESSIBLE(new Range()),
	VGUI_FALSE_POSITIVE(new Range()),
	VGUI_TRUE_POSITIVE(new Range());

	private Range _duration;
	
	private EventEnum(Range duration)
	{
		_duration = duration;
	}
	
	@Override
	public Range duration() {
		return _duration;
	}

}
