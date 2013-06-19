package team;

public enum UDO {
	
	/* Parent Search Outputs */
	
	PS_POKE_MM,
	PS_END_MM,
	PS_TERMINATE_SEARCH_MM,
	PS_TARGET_DESCRIPTION_MM,
	PS_NEW_SEARCH_AOI,
	PS_BUSY_MM,
	PS_ACK_MM,
	
	/* Mission Manager Outputs */
	
	MM_POKE_PS,
	MM_POKE_VO,
	MM_POKE_OP,
	MM_POKE_VGUI,
	MM_ACK_PS,
	MM_ACK_VO,
	MM_ACK_OP,
	MM_END_PS,
	MM_END_VO,
	MM_END_OP,
	MM_END_VGUI,
	MM_TARGET_DESCRIPTION_VO,
	MM_TERMINATE_SEARCH_VO,
	MM_NEW_SEARCH_AOI_OP,
	MM_TERMINATE_SEARCH_OP,
	MM_SEARCH_AOI_COMPLETE,
	MM_SEARCH_FAILED,
	MM_TARGET_SIGHTING_F,
	MM_TARGET_SIGHTING_T,
	MM_FLYBY_REQ_F_VGUI,
	MM_FLYBY_REQ_T_VGUI,
	MM_ANOMALY_DISMISSED_F_VGUI,
	MM_ANOMALY_DISMISSED_T_VGUI,
	MM_ANOMALY_DISMISSED_T,
	MM_ANOMALY_DISMISSED_F,
	MM_BUSY_PS,
	MM_BUSY_OP,
	MM_BUSY_VO,

	/* Video Operator Outputs */

	VO_POKE_MM,
	VO_END_MM,
	VO_TARGET_SIGHTING_T_MM,
	VO_TARGET_SIGHTING_F_MM,
	VO_POKE_OP,
	VO_END_OP,
	VO_BAD_STREAM_OP,
	VO_POKE_VGUI,
	VO_END_VGUI,
	VO_FLYBY_REQ_T_VGUI,
	VO_FLYBY_REQ_F_VGUI,
	VO_FLYBY_END_SUCCESS_VGUI,
	VO_FLYBY_END_FAILED_VGUI,
	VO_POSSIBLE_ANOMALY_DETECTED_T_VGUI,
	VO_POSSIBLE_ANOMALY_DETECTED_F_VGUI,
	VO_ACK_MM,
	VO_BUSY_MM,
	VO_SIGNAL_BAD_VO,

	/* Video Operator Gui Outputs */
	
	VGUI_FALSE_POSITIVE_VO,
	VGUI_TRUE_POSITIVE_VO,
	VGUI_VALIDATION_REQ_T,
	VGUI_VALIDATION_REQ_F,
	VGUI_VALIDATION_REQ_T_MM,
	VGUI_VALIDATION_REQ_F_MM,
	VO_FLYBY_REQ_T_OGUI,
	VO_FLYBY_REQ_F_OGUI,
	VO_FLYBY_ANOMALY_F_VO,
	VO_FLYBY_ANOMALY_T_VO,
	VO_FLYBY_END_FAILED_OGUI,
	VO_FLYBY_END_SUCCESS_OGUI,
	MM_FLYBY_REQ_F_OGUI,
	MM_FLYBY_REQ_T_OGUI,
	VGUI_NORMAL,
	VGUI_FLYBY_T,
	VGUI_FLYBY_F,
	
	/* UAV Operator Outputs */
	
	OP_POKE_MM,
	OP_END_MM,
	OP_POKE_OGUI,
	OP_END_OGUI,
	OP_ACK_MM,
	OP_ACK_VO,
	OP_BUSY_MM,
	OP_BUSY_VO,
	OP_NEW_FLIGHT_PLAN_OGUI,
	OP_LAND_OGUI,
	OP_LOITER_OGUI,
	OP_RESUME_OGUI,
	OP_SEARCH_AOI_COMPLETE_MM,
	OP_POST_FLIGHT_COMPLETE_UAV,
	OP_TAKE_OFF_UAV,
	OP_FLYBY_START_F_VGUI,
	OP_FLYBY_START_T_VGUI,
	OP_FLYBY_START_T_OGUI,
	OP_FLYBY_END_OGUI,
	OP_MODIFY_FLIGHT_PLAN_OGUI,
	OP_FLYBY_START_F_OGUI,
	OP_TAKE_OFF_OGUI,
	
	/* UAV Operator Gui Outputs */
	
	OGUI_STATE_NORMAL,
	OGUI_STATE_ALARM,
	OGUI_FLYBY_START_F_VO,
	OGUI_FLYBY_START_T_VO,
	OP_FLYBY_END_UAV,
	OP_LAND_UAV,
	OP_LOITER_UAV,
	OGUI_MODIFY_FLIGHT_PLAN_UAV,
	OGUI_NEW_FLIGHT_PLAN_UAV,
	OGUI_RESUME_UAV,
	OGUI_TAKE_OFF_UAV,
	OGUI_UAV_BATTERY_DEAD_OP,
	OGUI_UAV_BATTERY_LOW_OP,
	OGUI_UAV_BATTERY_OFF_OP,
	OGUI_UAV_BATTERY_OK_OP,
	OGUI_UAV_FLIGHT_PLAN_NO_OP,
	OGUI_UAV_FLIGHT_PLAN_YES_OP,
	OGUI_UAV_FLIGHT_PLAN_PAUSED_OP,
	OGUI_UAV_FLIGHT_PLAN_COMPLETE_OP,
	OGUI_UAV_HAG_LOW_OP,
	OGUI_UAV_HAG_NONE_OP,
	OGUI_UAV_HAG_GOOD_OP,
	OGUI_UAV_HAG_CRASHED_OP,
	OGUI_UAV_SIGNAL_NONE_OP,
	OGUI_UAV_SIGNAL_LOST_OP,
	OGUI_UAV_SIGNAL_OK_OP,
	OGUI_UAV_SIGNAL_RESUMED_OP,
	OGUI_UAV_FLYING_NORMAL_OP,
	OGUI_UAV_FLYING_FLYBY_OP,
	OGUI_UAV_LOITERING_OP,
	OGUI_UAV_LANDING_OP,
	OGUI_UAV_TAKE_OFF_OP,
	OGUI_UAV_LANDED_OP,
	OGUI_UAV_READY_OP,
	OGUI_FLYBY_START_F_UAV,
	OGUI_FLYBY_START_T_UAV,
	OGUI_FLYBY_END_UAV,
	OGUI_LAND_UAV,
	OGUI_LOITER_UAV,
	OGUI_FLYBY_REQ_T_OP,
	OGUI_FLYBY_REQ_F_OP,
	OGUI_FLYBY_END_SUCCESS_OP,
	OGUI_FLYBY_END_FAILED_OP,
	
	/* UAV Outputs */
	
	UAV_FLYING_NORMAL,
	UAV_FLYING_FLYBY,
	UAV_LOITERING,
	UAV_LANDING,
	UAV_TAKE_OFF,
	UAV_LANDED,
	UAV_READY,
	UAV_FLIGHT_PLAN_COMPLETE,
	UAV_BATTERY_DEAD_OGUI,
	UAV_BATTERY_LOW_OGUI,
	UAV_BATTERY_OFF_OGUI,
	UAV_BATTERY_OK_OGUI,
	UAV_FLIGHT_PLAN_NO_OGUI,
	UAV_FLIGHT_PLAN_YES_OGUI,
	UAV_FLIGHT_PLAN_PAUSED_OGUI,
	UAV_FLIGHT_PLAN_COMPLETE_OGUI,
	UAV_HAG_LOW_OGUI,
	UAV_HAG_NONE_OGUI,
	UAV_HAG_GOOD_OGUI,
	UAV_HAG_CRASHED_OGUI,
	UAV_SIGNAL_NONE_OGUI,
	UAV_SIGNAL_LOST_OGUI,
	UAV_SIGNAL_OK_OGUI,
	UAV_SIGNAL_RESUMED_OGUI;

	private boolean _active;
	private boolean _primed;
	
	private UDO(){
		this._active = false;
		this._primed = false;
		
	}

	/**
	 * this method is used to tell if a UDO is active
	 * @return return the state of the UDO
	 */
	public boolean isActive() {
		
		return _active;
		
	}

	/**
	 * primes the UDO, actors will soon be able to respond to it
	 */
	public void prime() {
		
		_primed = true;
		
	}
	
	/**
	 * updates the data status
	 * works to activate or deactivate the UDO based on the temp status
	 */
	public void processData() {
		
		_active = _primed;//activates or deactivates the UDO 
		_primed = false;
		
	}
}
