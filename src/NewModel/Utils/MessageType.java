package NewModel.Utils;

/**
 * MessageType represents the specific type of information that is being communicated.
 * These are broken down into three generic types of messages.
 * 
 * MSG - This represents information passing.
 * CMD - This represents a command.
 * REQ - This represents a request for information.
 * 
 * @author TJ-ASUS
 *
 */

public enum MessageType {

	/**
	 * Area of interest (AOI) is a region that needs to be searched.  This is information
	 * is usually given in the form of a command.
	 */
	CMD_AREA_OF_INTEREST,
	
	/**
	 * Target Description is the most current description of the missing person,
	 * as the search progresses this can change.
	 */
	MSG_TARGET_DESCRIPTION,
	
	/**
	 * Terminate Search is a command to end the search from the parent search, 
	 * hopefully because the person has been found.
	 */
	CMD_TERMINATE_SEARCH,
	
	/**
	 * Search Terminated is a response from the mission manager to the parent search
	 * that all searching is finished or cannot be completed.
	 */
	MSG_SEARCH_TERMINATED,
	
	/**
	 * Target sighting is reported when it is believed that the target has been sighted.
	 */
	MSG_TARGET_SIGHTING,
	
	/**
	 * Area of interest report is a report to the parent search about the
	 * search of an AOI.
	 */
	MSG_AREA_OF_INTEREST_SUCCESS,
	MSG_AREA_OF_INTEREST_FAILURE,
	
	/**
	 * Start Searching Command represents a command from the Mission manager to commence search
	 * operations.
	 * Pause Search sent from Mission Manager, may represent anything that requires the Pilot or
	 * Video Analyst to stop what they are doing and take a break.
	 */
	CMD_START_SEARCH,
	CMD_STOP_SEARCH,
	CMD_PAUSE_SEARCH,
	CMD_RESUME_SEARCH,
	
	/**
	 * Ready represents that team members are ready
	 */
	MSG_READY,
	
	/**
	 * Search Status
	 */
	REQ_SEARCH_STATUS,
	MSG_SEARCH_STATUS_COMPLETE,
	MSG_SEARCH_STATUS_INCOMPLETE,
	
	/**
	 * Health Status
	 */
	REQ_HEALTH_STATUS,
	MSG_HEALTH_STATUS_GOOD,
	MSG_HEALTH_STATUS_POOR,
	MSG_HEALTH_STATUS_BAD,
	
	/**
	 * Basic UAV Status
	 */
	REQ_BASIC_UAV_STATUS,
	MSG_BASIC_UAV_STATUS_GROUNDED,
	MSG_BASIC_UAV_STATUS_FLYING,
	MSG_BASIC_UAV_STATUS_LOITERING,
	MSG_BASIC_UAV_STATUS_DEAD,
	
	/**
	 * Search Insight
	 */
	MSG_SEARCH_INSIGHT,
	
	/**
	 * Problem with Environment, such as wind, snow, light, etc...
	 */
	MSG_ENVIRONMENT_PROBLEM,
	
	/**
	 * Video Feeds
	 */
	REQ_VIDEO_FEED,
	REQ_VALIDATE_ANNOTATION,
	MSG_VIDEO_FEED,
	MSG_VALIDATE_ANNOTATION,
	
	
	/**
	 * Flight Requests
	 */
	REQ_FLIGHT,
	MSG_FLIGHT_SUCCESS,
	MSG_FLIGHT_FAILURE,
	
	/**
	 * Pilot Commands to UAV GUI
	 */
	CMD_MISSION,
	CMD_KILL,
	
	/**
	 * UAV communications used by the UAV, Pilot, and UAV GUI
	 */
	CMD_MANUAL_FLY_UAV,
	MSG_MISSION,
	MSG_KILL,
	REQ_UAV_STATUS,
	MSG_UAV_STATUS_OK,
	MSG_UAV_STATUS_OFF_COURSE,
	MSG_UAV_STATUS_LOW_BATTERY,
	MSG_UAV_STATUS_LOSS_OF_SIGNAL,
	MSG_SERVER_STATUS_GOOD,
	MSG_SERVER_STATUS_BAD,
	
	/**
	 * Video GUI Commands and Msgs
	 */
	CMD_STREAM_VIDEO,
	CMD_ANNOTATE_VIDEO,
	MSG_STREAM_VIDEO_GOOD,
	MSG_STREAM_VIDEO_BAD,
	MSG_FALSE_ANNOTATION,
	MSG_TRUE_ANNOTATION,
	MSG_FALSE_TARGET_SIGHTING,
	MSG_TRUE_TARGET_SIGHTING,
	
	/**
	 * Environment Messages
	 */
	MSG_FLIGHT_CONDITIONS_GOOD,
	MSG_FLIGHT_CONDITIONS_BAD,
	MSG_SEARCH_CONDITIONS_GOOD,
	MSG_SEARCH_CONDITIONS_BAD,
	
	/**
	 * No Response represents a time when no response will be returned.  
	 * For simulatin purposes this is faked. 
	 */
	MSG_NO_RESPONSE
	
}
