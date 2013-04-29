package WiSAR;

import CUAS.Utils.Range;

public enum Durations {
	
	/**
	 * Parent Search Time Assumptions
	 */
	PS_POKE_MM_DUR(new Range(60, 1800)),
	PS_TX_TERMINATE_MM_DUR(new Range(60, 300)),
	PS_TX_AOI_MM_DUR(new Range(600, 1800)),
	PS_RX_MM_DUR(new Range(1800, 1800)),
	
	/**
	 * Mission Manager Time Assumptions
	 */
	MM_POKE_DUR(new Range(60, 1800)),
	MM_TX_SEARCH_FAILED_PS_DUR(new Range(300, 600)),
	MM_TX_SEARCH_COMPLETE_PS_DUR(new Range(300, 600)),
	MM_TX_SIGHTING_PS_DUR(new Range(600, 1800)),
	MM_TX_AOI_PILOT_DUR(new Range(300, 900)),
	MM_TX_INITIATE_FEED_VO_DUR(new Range(300, 900)),
	MM_TX_TERMINATE_PILOT_DUR(new Range(60, 300)),
	MM_RX_DUR(new Range(1800, 1800)),
	MM_TX_END_FEED(new Range(300,1800)),
	
	
	PILOT_POKE_MM_DUR(new Range(60, 1800)),
	PILOT_TX_MM_DUR(new Range(60, 1800)),
	PILOT_RX_MM_DUR(new Range(60, 1800)),
	PILOT_OBSERVE_UAV_DUR(new Range(60, 1800)),
	PILOT_OBSERVE_UGUI_DUR(new Range(60, 1800)),
	PILOT_LAUNCH_UAV_DUR(new Range(60, 1800)),
	PILOT_POKE_UGUI_DUR(new Range(60, 1800)),
	PILOT_TX_UGUI_DUR(new Range(60, 1800)),
	PILOT_TX_UGUI_FLIGHT_PLAN_DUR(new Range(60, 1800)),
	PILOT_POST_FLIGHT_LAND_DUR(new Range(60, 1800)),
	PILOT_POST_FLIGHT_CRASH_DUR(new Range(60, 1800)),
	PILOT_BAD_PATH_THRESHOLD_DUR(new Range(60, 1800)),
	
	UGUI_AUDIBLE_ALARM_DUR(new Range(60, 1800)),
	UGUI_ALARM_UNNOTICED_DUR(new Range(60, 1800)),
	
	UAV_TAKE_OFF_DUR(new Range(60, 1800)),
	UAV_BATTERY_DUR(new Range(60, 1800)),
	UAV_LANDING_DUR(new Range(60, 1800)),
	UAV_LOW_BATTERY_THRESHOLD_DUR(new Range(60, 1800)),
	UAV_FLIGHT_PLAN_DUR(new Range(60, 1800)),
	VO_RX_MM_DUR(new Range(60, 1800)),
	VO_POKE_VGUI_DUR(new Range(60, 1800)),
	VO_POKE_MM_DUR(new Range(60, 1800)),
	VO_POKE_OPERATOR_DUR(new Range(60, 1800)),
	VO_TX_VGUI_DUR(new Range(60, 1800)),
	VO_TX_MM_DUR(new Range(60, 1800)),
	VO_TX_OPERATOR_BAD_STREAM(new Range(60, 1800)),
	VO_TX_OPERATOR_STREAM_ENDED(new Range(60, 1800)),
	VO_TX_OPERATOR_LOOK_CLOSER(new Range(60, 1800)), 
	
	VGUI_RETURN_TO_IDLE(new Range(60, 1800)),
	VGUI_START_STREAM(new Range(60, 1800)),
	VGUI_RX_DUR(new Range(60, 1800));

	private Range _range;
	
	private Durations(Range range)
	{
		this._range = range;
	}
	
	public Range range()
	{
		return _range;
	}
}
