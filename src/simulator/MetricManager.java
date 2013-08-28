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
public class MetricManager implements IMetricManager {
//	HashMap<MetricKey, Metric> metric_map = new HashMap<MetricKey, Metric>();
	MetricKey currentKey = new MetricKey(-1, "", "", -1);
	TreeMap<MetricKey, Metric> actor_metrics;
//	Connection c = null;
//	HashMap<MetricKey,Metric> actor_metrics;//a hash of the actors (keys) and the metrics applied to them (values)
	
	public MetricManager(){
//		actor_metrics = new HashMap<MetricKey,Metric>();
		actor_metrics = new TreeMap<MetricKey, Metric>();
		
//		try {
//			Class.forName("org.sqlite.JDBC");
//			String db = Simulator.getSim().dbname;
//			c = DriverManager.getConnection("jdbc:sqlite:"+db+".db");
//		} catch (Exception e) {
//			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
//			System.exit(0);
//		}
	}
	
//	public void close()
//	{
//		try {
//			c.close();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}
	
	@Override
	public void addMetric(MetricEnum metric, String name)
	{
		Metric metrics = actor_metrics.get(currentKey);
		if(metrics == null)
			metrics = new Metric();
		metrics.increment(metric);
		MetricKey key = currentKey.clone();
//		System.out.println("MetricKey: " + key.toString() +  ":" + metric.name() + " - " + key.hashCode());
		actor_metrics.put(key, metrics);
		
//		Statement stmt = null;
//		try {
//			stmt = c.createStatement();
//			String sql = "INSERT INTO singlemetric " +
//					"VALUES (" + key._time + ",'" + key._actor_name + "','" + key._state + "'," + key._transition + ",'" + metric.name() + "','" + name + "');";
//			stmt.executeUpdate(sql);
//			stmt.close();
//		} catch (Exception e) {
//			System.out.println("SQL insert error: " + e.getMessage());
//		}
	}

	@Override
	public void addMetric(MetricEnum metric) {
		addMetric(metric, "Unknown");
	}
}
