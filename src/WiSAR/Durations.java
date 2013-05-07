package WiSAR;

import CUAS.Utils.Range;

public enum Durations {
	/**
	 * Event Time Assumptions
	 */
	EVENT_TARGET_SIGHTED_DUR(new Range(60,1800)),
	HAG_DANGER_TO_CRASH_DUR(new Range(100,1800)),
	
	/**
	 * Parent Search Time Assumptions
	 */
	PS_POKE_MM_DUR(new Range(60, 1800)),
	PS_TX_TERMINATE_MM_DUR(new Range(60, 300)),
	PS_TX_AOI_MM_DUR(new Range(600, 1800)),
	PS_RX_MM_DUR(new Range(1800, 1800)),
	PS_TX_TARGET_DESCRIPTION(new Range(300, 1800)),
	
	/**
	 * Mission Manager Time Assumptions
	 */
	MM_POKE_DUR(new Range(60, 1800)),
	MM_TX_SEARCH_FAILED_PS_DUR(new Range(300, 600)),
	MM_TX_SEARCH_COMPLETE_PS_DUR(new Range(300, 600)),
	MM_TX_SIGHTING_PS_DUR(new Range(600, 1800)),
	MM_TX_AOI_OP_DUR(new Range(300, 900)),
	MM_TX_TARGET_DESCRIPTION_DUR(new Range(300, 900)),
	MM_TX_TERMINATE_SEARCH_DUR(new Range(60, 300)),
	MM_RX_DUR(new Range(1800, 1800)),
	MM_TX_END_FEED(new Range(300, 1800)),
	MM_OBSERVE_VGUI(new Range(300, 600)),
	MM_TX_FLYBY(new Range(200, 300)),
	MM_DEFAULT_TX_DUR(new Range(300, 600)),
	MM_VERIFY_ANOMALY_DUR(new Range(300, 900)),
	MM_IDLE_DUR(new Range(60, 300)),
	MM_DETECT_FP(new Range(30, 30)),
	MM_DETECT_TP(new Range(50, 50)),
	
	/**
	 * Operator assumptions
	 */
	OPERATOR_POKE_MM_DUR(new Range(60, 1800)),
	OPERATOR_TX_MM_DUR(new Range(60, 1800)),
	OPERATOR_RX_MM_DUR(new Range(1800, 1800)),
	OPERATOR_OBSERVE_UAV_DUR(new Range(60, 1800)),
	OPERATOR_OBSERVE_UGUI_DUR(new Range(60, 1800)),
	OPERATOR_LAUNCH_UAV_DUR(new Range(60, 1800)),
	OPERATOR_POKE_UGUI_DUR(new Range(60, 1800)),
	OPERATOR_TX_UGUI_DUR(new Range(60, 1800)),
	OPERATOR_TX_UGUI_FLIGHT_PLAN_DUR(new Range(60, 1800)),
	OPERATOR_POST_FLIGHT_LAND_DUR(new Range(60, 1800)),
	OPERATOR_POST_FLIGHT_CRASH_DUR(new Range(60, 1800)),
	OPERATOR_BAD_PATH_THRESHOLD_DUR(new Range(60, 1800)),
	
	UGUI_AUDIBLE_ALARM_DUR(new Range(60, 1800)),
	UGUI_ALARM_UNNOTICED_DUR(new Range(60, 1800)),
	/**
	 * UAV assumptions
	 */
	UAV_TAKE_OFF_DUR(new Range(60, 1800)),
	UAV_BATTERY_DUR(new Range(1500, 1800)),
	UAV_LOW_BATTERY_THRESHOLD_DUR(new Range(60, 60)),
	UAV_LANDING_DUR(new Range(60, 1800)),
	UAV_FLIGHT_PLAN_DUR(new Range(60, 1800)),
	
	
	/**
	 * VideoOperator assumptions
	 */
	VO_RX_MM_DUR(new Range(60, 1800)),
	VO_POKE_VGUI_DUR(new Range(60, 1800)),
	VO_POKE_MM_DUR(new Range(60, 1800)),
	VO_POKE_OPERATOR_DUR(new Range(60, 1800)),
	VO_TX_VGUI_DUR(new Range(60, 1800)),
	VO_TX_MM_DUR(new Range(60, 1800)),
	VO_TX_OPERATOR_BAD_STREAM(new Range(60, 1800)),
	VO_TX_OPERATOR_STREAM_ENDED(new Range(60, 1800)),
	VO_TX_OPERATOR_LOOK_CLOSER(new Range(60, 1800)), 
	VO_FLYBY_DUR(new Range(60,1800)),
	VO_DETECT_POSSIBLE_FP(new Range(20, 20)),
	VO_DETECT_POSSIBLE_TP(new Range(40, 40)),
	VO_DETECT_LIKELY_FP(new Range(10, 10)),
	VO_DETECT_LIKELY_TP(new Range(30, 30)),
	VO_BELIEVE_FLYBY_FP(new Range(10, 10)),
	VO_BELIEVE_FLYBY_TP(new Range(90, 90)),
	
	/**
	 * VideoGUI assumptions
	 */
	VGUI_RETURN_TO_IDLE(new Range(60, 1800)),
	VGUI_START_STREAM(new Range(60, 1800)),
	VGUI_RX_DUR(new Range(60, 1800)), 
	
	OGUI_RX_DUR(new Range(60, 1800)), 
	
	UAV_ADJUST_PATH(new Range(60, 1800)), 
	UAV_PREPARATION_DUR(new Range(60, 1800)),
	
	/**
	 * Flyby assumptions
	 */
	FLYBY_FIND_ANOMALY(new Range(60,1200));

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
