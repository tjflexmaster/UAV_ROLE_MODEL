package NewModel.Simulation;

public class Assumptions {

	/**
	 * General Durations
	 */
	public static final int DUR_1 = 1;
	public static final int DUR_2 = 2;
	public static final int DUR_3 = 3;
	public static final int DUR_4 = 4;
	public static final int DUR_5 = 5;
	public static final int DUR_6 = 6;
	public static final int DUR_7 = 7;
	public static final int DUR_8 = 8;
	public static final int DUR_9 = 9;
	
	/**
	 * Parent Search Time Assumptions
	 */
	public static final int PS_POKE_MM_DUR = 1000;
	public static final int PS_TX_MM_DUR = 50;
	public static final int PS_RX_MM_DUR = 1000;
	
	/**
	 * Mission Manager Time Assumptions
	 */
	public static final int MM_POKE_DUR = 30;
	public static final int MM_TX_PS_DUR = 30;
	public static final int MM_TX_PILOT_DUR = 30;
	public static final int MM_RX_DUR = 1000;
	
	/**
	 * Pilot Role Time Assumptions
	 */
	public static final int PILOT_POKE_MM_DUR = 30;
	public static final int PILOT_TX_MM_DUR = 20;
	public static final int PILOT_RX_MM_DUR = 1000;
	public static final int PILOT_OBSERVE_UAV_DUR = 10;
	public static final int PILOT_OBSERVE_UGUI_DUR = 50;
	public static final int PILOT_LAUNCH_UAV_DUR = 50;
	public static final int PILOT_POKE_UGUI_DUR = 30;
	public static final int PILOT_TX_UGUI_DUR = 15;
	public static final int PILOT_POST_FLIGHT_LAND_DUR = 30;
	public static final int PILOT_POST_FLIGHT_CRASH_DUR = 100;
	
	/**
	 * UAV GUI Time Assumptions
	 */
	public static final int UGUI_AUDIBLE_ALARM_DUR = 50;
	public static final int UGUI_ALARM_UNNOTICED_DUR = 50;
	
	/**
	 * UAV Time Assumptions
	 */
	public static final int UAV_TAKE_OFF_DUR = 50;
	public static final int UAV_BATTERY_DUR = 500;
	public static final int UAV_LANDING_DUR = 50;
	public static final int UAV_LOW_BATTERY_THRESHOLD = 100;
	public static final int UAV_FLIGHT_PLAN_DUR = 200;
	
}
