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
	TARGET_SIGHTING,
	SEARCH_AOI_COMPLETE,
	
	/**
	 * MM TX Pilot, VA
	 */
	REQ_HEALTH_STATUS,
	REQ_UAV_STATUS,
	REQ_SEARCH_STATUS,
	CMD_START,
	CMD_STOP,
	CMD_PAUSE,
	CMD_RESUME,
	
	/**
	 * Health Responses
	 */
	HEALTH_GOOD,
	HEALTH_BAD,
	
	/**
	 * UAV Status Responses
	 */
	UAV_SEARCHING,
	UAV_GROUNDED,
	UAV_CRASHED,
	
	/**
	 * Search Responses
	 */
	SEARCH_ACTIVE,
	SEARCH_IDLE,
	SEARCH_COMPLETE,
	NO_SEARCH
	
	
}
