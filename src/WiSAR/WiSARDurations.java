package WiSAR;

import java.util.HashMap;
import java.util.Map;

import NewModel.Utils.Range;

public class WiSARDurations {
	private HashMap<String, Range> _ranges;
	
	public enum Duration {
		/**
		 * Parent Search Time Assumptions
		 */
		PS_POKE_MM_DUR,
		PS_TX_MM_DUR,
		PS_RX_MM_DUR,
		
		/**
		 * Mission Manager Time Assumptions
		 */
		MM_POKE_DUR,
		MM_TX_PS_DUR,
		MM_TX_PILOT_DUR,
		MM_RX_DUR,
		
		
		PILOT_POKE_MM_DUR,
		PILOT_TX_MM_DUR,
		PILOT_RX_MM_DUR,
		PILOT_OBSERVE_UAV_DUR,
		PILOT_OBSERVE_UGUI_DUR,
		PILOT_LAUNCH_UAV_DUR,
		PILOT_POKE_UGUI_DUR,
		PILOT_TX_UGUI_DUR,
		PILOT_TX_UGUI_FLIGHT_PLAN_DUR,
		PILOT_POST_FLIGHT_LAND_DUR,
		PILOT_POST_FLIGHT_CRASH_DUR,
		PILOT_BAD_PATH_THRESHOLD_DUR,
		
		UGUI_AUDIBLE_ALARM_DUR,
		UGUI_ALARM_UNNOTICED_DUR,
		
		UAV_TAKE_OFF_DUR,
		UAV_BATTERY_DUR,
		UAV_LANDING_DUR,
		UAV_LOW_BATTERY_THRESHOLD_DUR,
		UAV_FLIGHT_PLAN_DUR,
		
	}
	
	public WiSARDurations()
	{
		//These numbers are assumed to be in seconds just so I can wrap my brain around them
		//60 = 1 min
		//1800 = 30 min
		//2700 = 45 min
		_ranges = new HashMap<String, Range>();
		
		_ranges.put(Duration.PS_POKE_MM_DUR.name(), new Range(60, 1800));
		_ranges.put(Duration.PS_RX_MM_DUR.name(), new Range(1800, 1800));
		_ranges.put(Duration.PS_TX_MM_DUR.name(), new Range(60, 1800));
		
		_ranges.put(Duration.MM_POKE_DUR.name(), new Range(60, 1800));
		_ranges.put(Duration.MM_RX_DUR.name(), new Range(1800, 1800));
		_ranges.put(Duration.MM_TX_PILOT_DUR.name(), new Range(30, 1800));
		_ranges.put(Duration.MM_TX_PS_DUR.name(), new Range(60, 1800));
		
		_ranges.put(Duration.PILOT_LAUNCH_UAV_DUR.name(), new Range(300, 600));
		_ranges.put(Duration.PILOT_OBSERVE_UAV_DUR.name(), new Range(30, 300));
		_ranges.put(Duration.PILOT_OBSERVE_UGUI_DUR.name(), new Range(60, 300));
		_ranges.put(Duration.PILOT_POKE_MM_DUR.name(), new Range(60, 1800));
		_ranges.put(Duration.PILOT_RX_MM_DUR.name(), new Range(1800, 1800));
		_ranges.put(Duration.PILOT_TX_MM_DUR.name(), new Range(60, 600));
		_ranges.put(Duration.PILOT_POKE_UGUI_DUR.name(), new Range(30, 30));
		_ranges.put(Duration.PILOT_TX_UGUI_DUR.name(), new Range(10, 60)); //This is simple commands like Loiter/Kill/Resume
		_ranges.put(Duration.PILOT_TX_UGUI_FLIGHT_PLAN_DUR.name(), new Range(300, 1800)); //This is creating a new flight plan
		_ranges.put(Duration.PILOT_BAD_PATH_THRESHOLD_DUR.name(), new Range(600, 1200));
		_ranges.put(Duration.PILOT_POST_FLIGHT_CRASH_DUR.name(), new Range(1800, 3600));
		_ranges.put(Duration.PILOT_POST_FLIGHT_LAND_DUR.name(), new Range(300, 1800));
		
		_ranges.put(Duration.UGUI_ALARM_UNNOTICED_DUR.name(), new Range(60, 300));
		_ranges.put(Duration.UGUI_AUDIBLE_ALARM_DUR.name(), new Range(60, 600));
		
		_ranges.put(Duration.UAV_BATTERY_DUR.name(), new Range(2400, 3600));
		_ranges.put(Duration.UAV_FLIGHT_PLAN_DUR.name(), new Range(900, 7200));
		_ranges.put(Duration.UAV_LANDING_DUR.name(), new Range(300, 600));
		_ranges.put(Duration.UAV_TAKE_OFF_DUR.name(), new Range(120, 300));
		_ranges.put(Duration.UAV_LOW_BATTERY_THRESHOLD_DUR.name(), new Range(480, 720));
		
	}
	
	public Map<String, Range> durations()
	{
		return _ranges;
	}
	
}