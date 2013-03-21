package NewModel.Roles;

/**
 * These states represent all possible states within the state machine.
 * 
 * ENV = Environment
 * UAV = Unmanned Aerial Vehicle
 * PS = Parent Search
 * PILOT = Pilot
 * VA = Video Analyst
 * UGUI = UAV GUI
 * MM = Mission Manager
 * GUI = Graphical User Interface
 * HAG = Height Above Ground
 * BAT = Battery
 * TX_<> = Transmitting to <Role>
 * RX_<> = Receiving from <Role>
 * POKE_<> = Try to grab attention of <Role>
 * ACK_<> = Acknowledge poke from <Role>
 * END_<> = End a transmission to <Role>
 * 
 * @author TJ-ASUS
 *
 */
public enum RoleState {
	
	/**
	 * Environment States
	 */
	ENV_WIND_HIGH,
	ENV_WIND_BAD,
	ENV_RAIN_NO,
	ENV_RAIN_YES,
	
	
	/**
	 * UAV States
	 */
	UAV_TAKE_OFF,
	UAV_LANDING,
	UAV_FLYING,
	UAV_LOITERING,
//	UAV_FLYING_HAG_LOW,
//	UAV_FLYING_BAT_LOW,
//	UAV_FLYING_BAT_LOW_HAG_LOW,
//	UAV_LOITERING_HAG_LOW,
//	UAV_LOITERING_BAT_LOW,
//	UAV_LOITERING_BAT_LOW_HAG_LOW,
//	UAV_STRUGGLING,
//	UAV_STRUGGLING_HAG_LOW,
//	UAV_STRUGGLING_BAT_LOW,
//	UAV_STRUGGLING_BAT_LOW_HAG_LOW,
	UAV_CRASH,
	UAV_GROUNDED,
//	UAV_NO_SIGNAL,
	
	
	/**
	 * Parent Search States
	 */
	PS_POKE_MM,
	PS_TX_MM,
	PS_END_MM,
	PS_ACK_MM,
	PS_RX_MM,
//	PS_SEARCH_POKE_MM,
//	PS_SEARCH_TX_MM,
//	PS_SEARCH_END_MM,
//	PS_TERMINATE_SEARCH_POKE_MM,
//	PS_TERMINATE_SEARCH_TX_MM,
//	PS_TERMINATE_SEARCH_END_MM,
//	PS_TARGET_SIGHTING_ACK_MM,
//	PS_TARGET_SIGHTING_RX_MM,
//	PS_SEARCH_COMPLETE_ACK_MM,
//	PS_SEARCH_COMPLETE_RX_MM,
	
	
	
	/**
	 * Mission Manager States
	 */
//	MM_SEARCH_TX_PILOT,
//	MM_HEALTH_TX_PILOT,
//	MM_UAV_STATUS_TX_PILOT,
//	MM_SEARCH_STATUS_CMD_PILOT,
//	MM_COMMUNICATING_PILOT,
//	MM_SEARCH_CMD_VA,
//	MM_HEALTH_CMD_VA,
//	MM_SEARCH_STATUS_CMD_VA,
//	MM_COMMUNICATING_VA,
//	MM_SEARCH_ACK_PS,
//	MM_SEARCH_RX_PS,
//	MM_TERMINATE_SEARCH_ACK_PS,
//	MM_TERMINATE_SEARCH_RX_PS,
//	MM_SEARCH_COMPLETE_POKE_PS,
//	MM_SEARCH_COMPLETE_TX_PS,
//	MM_SEARCH_COMPLETE_END_PS,
//	MM_TARGET_SIGHTING_POKE_PS,
//	MM_TARGET_SIGHTING_TX_PS,
//	MM_TARGET_SIGHTING_END_PS,
	MM_POKE_PS,
	MM_TX_PS,
	MM_END_PS,
	MM_ACK_PS,
	MM_RX_PS,
	
	MM_POKE_PILOT,
	MM_TX_PILOT,
	MM_END_PILOT,
	MM_ACK_PILOT,
	MM_RX_PILOT,
	
	MM_POKE_VA,
	MM_TX_VA,
	MM_END_VA,
	MM_ACK_VA,
	MM_RX_VA,
	
	
	
	
	/**
	 * Pilot States
	 */
	PILOT_POKE_MM,
	PILOT_TX_MM,
	PILOT_END_MM,
	PILOT_ACK_MM,
	PILOT_RX_MM,
//	PILOT_PHYSICAL_BREAK,
	PILOT_OBSERVING_UAV,
	PILOT_OBSERVING_GUI,
	PILOT_LAUNCH_UAV,
	PILOT_POKE_UGUI,
	PILOT_TX_UGUI,
	PILOT_END_UGUI,
	PILOT_POST_FLIGHT,
	
	/**
	 * UAV GUI States
	 */
	UGUI_NORMAL,
	UGUI_ALARM,
	UGUI_AUDIBLE_ALARM,
	UGUI_INACCESSIBLE,
	
	
	/**
	 * Video Analyst States
	 */
	
	
	
	/**
	 * Video Analyst GUI States
	 */
	
	
	/**
	 * General States
	 */
	IDLE,
	STARTING,
	
}
