package simulator;

//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//import java.sql.Statement;
import java.util.*;

import simulator.Metric.MetricEnum;
/**
 * Metrics taken care of
 * accessing internal variables
 * accessing active internal variables
 * accessing com channels
 * accessing active com channels
 * initializing com_channels
 * deactivating com_channels
 * changing com_channel data
 * @author jaredmoore
 *
 */
public class MetricManager {
	
	private static MetricManager _instance = null;
	
	private MetricManager() {}
	
	public static synchronized MetricManager instance() {
		if ( _instance == null ) {
			_instance = new MetricManager();
		}
		return _instance;
	}

	public static void setDecisionWorkload(int time, String actor, String state, int workload)
	{
		//Do nothing JPF will handle the data
	}
	
	public static void setChannelConflict(int time, String actor, String channel_type)
	{
		//Do nothing JPF will handle the data
	}
	
	public static void setChannelLoad(int time, String actor, String channel_type, int load)
	{
		//Do nothing JPF will handle the data
	}
	
}
