package NewModel.Utils;

public enum DataType {
	
	/**
	 * SEARCH DATA From PS
	 */
	SEARCH_AOI,
	TARGET_DESCRIPTION,
	TERMINATE_SEARCH,
	
	/**
	 * SEARCH Results
	 */
	SEARCH_AOI_SIGHTING,
	SEARCH_AOI_COMPLETE,
	SEARCH_AOI_FAILED,
	
	/**
	 * MM TX Pilot, VA
	 */
	REQ_HEALTH_STATUS,
	REQ_UAV_STATUS,
	REQ_SEARCH_STATUS,
	MM_CMD_START,
	MM_CMD_STOP,
	MM_CMD_PAUSE,
	MM_CMD_RESUME,
	
	/**
	 * Health Responses
	 */
	HEALTH_GOOD,
	HEALTH_BAD,
	
	/**
	 * UAV Status
	 */
	//STATUS
	UAV_TAKE_OFF,
	UAV_GROUNDED,
	UAV_FLYING,
	UAV_LOITERING,
	UAV_CRASHED,
	UAV_LANDING,
	
	/**
	 * UAV Data
	 */
	//FLIGHT PLAN
	UAV_FLIGHT_PLAN_YES,
	UAV_FLIGHT_PLAN_NO,
	//SIGNAL
	UAV_SIGNAL_OK,
	UAV_SIGNAL_LOST,
	//BATTERY
	UAV_BAT_OK,
	UAV_BAT_LOW,
	//HEIGHT ABOVE GROUND
	UAV_HAG_OK,
	UAV_HAG_LOW,
	//PATH_FOLLOWING
	UAV_PATH_OK,
	UAV_PATH_BAD,
	
	
	/**
	 * Search Responses
	 */
	SEARCH_ACTIVE,
	SEARCH_IDLE,
	SEARCH_COMPLETE,
	NO_SEARCH,
	
	/**
	 * Pilot to UGUI to UAV
	 */
	TAKE_OFF,
	LAND,
	LOITER,
	RESUME,
	KILL,
	FLIGHT_PLAN
	
	
	
}
