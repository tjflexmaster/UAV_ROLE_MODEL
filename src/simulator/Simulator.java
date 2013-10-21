package simulator;

import gov.nasa.jpf.vm.Verify;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class Simulator {
	
	public enum DebugMode {
		DEBUG,
		PROD
	}
	
	public enum DurationMode {
		MIN,
		MAX,
		MEAN,
		MIN_MAX,
		MIN_MAX_MEAN
	}
	
	private ITeam _team;
	private IDeltaClock _clock;
	private HashMap<IActor, ITransition> _ready_transitions = new HashMap<IActor, ITransition>();
	private ArrayList<IActor> _active_events = new ArrayList<IActor>();
	private DebugMode _mode;
	private DurationMode _duration;
	private Random _random;
	public MetricManager _metrics;
	
	//Singleton variables
	private boolean _setup = false;
	private static Simulator _instance = null;
	private Date _date = null;
	
//	public String dbname = "";
	
	
	/**
	 * Get simulator singleton
	 * @return
	 */
	public static synchronized Simulator getSim() {
        if (_instance == null) {
            _instance = new Simulator();
        }
        return _instance;
	}
	
	private Simulator() {
		_clock = new DeltaClock();
		_date = new Date();
		
	}
	
	public void setup(ITeam team, DebugMode mode, DurationMode duration)
	{
		_setup = false;
//		dbname = "MetricDB" + _date.getTime();
		_clock = new DeltaClock();
//		_metrics = new MetricManager();
		
		_team = team;
		_mode = mode;
		_duration = duration;
		
		initializeRandom();
		
		//Setup the database
//		Connection c = null;
//		Statement stmt = null;
//		try {
//			Class.forName("org.sqlite.JDBC");
//			c = DriverManager.getConnection("jdbc:sqlite:"+dbname+".db");
//			System.out.println("Opened database successfully");
//			
//			stmt = c.createStatement();
//		      String sql = "CREATE TABLE metrics " +
//		                   "(" +
//		                   " time           INT    NOT NULL, " + 
//		                   " actor            TEXT     NOT NULL, " + 
//		                   " state        TEXT, " + 
//		                   "transition_id		INT, " +
//	                   	   "CHANNEL_ACTIVE_A	INT, " +
//	                   	   "CHANNEL_ACTIVE_V	INT, " +
//	                   	   "CHANNEL_ACTIVE_D	INT, " +
//	                   	   "CHANNEL_ACTIVE_O	INT, " +
//	                   	   "CHANNEL_INACTIVE_A	INT, " +
//	                   	   "CHANNEL_INACTIVE_V	INT, " +
//	                   	   "CHANNEL_INACTIVE_D	INT, " +
//	                   	   "CHANNEL_INACTIVE_O	INT, " +
//	                   	   "ENABLED				INT, " +
//	                   	   "ACTIVE				INT, " +
//	                   	   "MEMORY_ACTIVE		INT, " +
//	                   	   "MEMORY_INACTIVE		INT, " +
//	                   	   "CHANNEL_TEMP_A		INT, " +
//	                   	   "CHANNEL_TEMP_V		INT, " +
//	                   	   "CHANNEL_TEMP_D		INT, " +
//	                   	   "CHANNEL_TEMP_O		INT, " +
//	                   	   "CHANNEL_FIRE_A		INT, " +
//	                   	   "CHANNEL_FIRE_V		INT, " +
//	                   	   "CHANNEL_FIRE_D		INT, " +
//	                   	   "CHANNEL_FIRE_O		INT, " +
//	                   	   "MEMORY_TEMP			INT, " +
//	                   	   "MEMORY_FIRE			INT " +
//		                   ")"; 
//		      stmt.executeUpdate(sql);
//		      stmt.close();
//		      
//		      //Create another table for individual metrics
//		      stmt = c.createStatement();
//		      String tablesql = "CREATE TABLE singlemetric " +
//		                   "(" +
//		                   " time           	INT    NOT NULL, " + 
//		                   " actor            	TEXT     NOT NULL, " + 
//		                   " state        		TEXT, " + 
//		                   "transition_id		INT, " +
//	                   	   "metric				TEXT, " +
//		                   "name				TEXT " +
//		                   ")"; 
//		      stmt.executeUpdate(tablesql);
//		      stmt.close();
//		      
//		      c.close();
//		} catch (Exception e) {
//			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
//			System.exit(0);
//		}
//		System.out.println("Table created successfully");
		_setup = true;
		
		
	}
	
	/**
	 * Main Simulation method.
	 */
	public void run()
	{
		assert _setup : "Simulator not setup correctly";
	
//		HashMap<String, String> data = new HashMap<String, String>();
//		ArrayList<MetricDataStruct> metrics = new ArrayList<MetricDataStruct>();
//		String workloadOutput = "";
//		MetricDataStruct metric = new MetricDataStruct(0);
	
		System.out.println("Started");
		do {
			//Get all event and team transitions
			loadTransitions();
			
			//Save the metrics
//			metric._active_channels = _team.getAllChannels().countActiveChannels();
//			metrics.add(metric);
			
			//Advance Time
			_clock.advanceTime();
			if(_clock.getElapsedTime()==175){
				System.out.println();
			}
			//Save time for the current Key in the metric manager
//			_metrics.currentKey.setTime(_clock.getElapsedTime());
			
			//Start a new metric
//			metric = new MetricDataStruct(_clock.elapsedTime());
			
			System.out.printf("\nadvanced: %d", _clock.getElapsedTime());
//			String name = dt.actor.name();
//			HashMap<Actor, Integer> workload = _team.getWorkload();
//			for(Entry<Actor, Integer> actor_workload : workload.entrySet()){
//				String name = actor_workload.getKey()._name;
//				String state = actor_workload.getKey().getCurrentState().toString();
//				int work = actor_workload.getValue();
//				if(data.containsKey(name)){
//					data.put(name, data.get(name)+ "\n" + state + "\t" + work);
//				}else{
//					data.put(name, "\n" + name +"\n" + state + "\t" + work);
//				}
//				workloadOutput += ("\n" + name + "\t" + state + "\t" + work);
//				System.out.println("\n" + name + "\t" + state + "\t" + work);
//			}
			
			//Process Ready Transitions
			_ready_transitions.clear();
			_ready_transitions.putAll(_clock.getReadyTransitions());
			for(Entry<IActor, ITransition> e : _ready_transitions.entrySet()){
				System.out.println('\n' + e.toString());
				
				//Set metric key
				IActor a = (IActor) e.getKey();
				ITransition t = (ITransition) e.getValue();
//				Simulator.getSim()._metrics.currentKey.setActor(a.name());
//				Simulator.getSim()._metrics.currentKey.setState(a.getCurrentState().getName());
//				Simulator.getSim()._metrics.currentKey.setTransition(t.getIndex());
				
				t.fire();
//				metric._fired_transitions++;
//				ComChannelList outputs = transition.getOutputChannels();
//				metric._updated_channels = outputs.size();
//				metric._updated_audio_channels = outputs.countChannels(Type.AUDIO);
//				metric._updated_visual_channels = outputs.countChannels(Type.VISUAL);
//				metric._updated_data_channels = outputs.countChannels(Type.DATA);
//				metric._states_changed++;
			}
			
//			System.out.println("Looping" + _clock.getElapsedTime());
//			System.out.println(_metrics.toString());
//			printMetrics();
			
//			System.out.println("Test");
			
		} while (!_ready_transitions.isEmpty());
		
//		printMetrics();

		MetricManager.instance().endSimulation();
		System.out.println("Finished");
//		_metrics.close();
//		try {
//			PrintWriter workloadWriter = new PrintWriter(new File("workload.txt"));
//			for(Entry<String, String> actor_workload : data.entrySet())
//				workloadWriter.print(actor_workload.getValue());
//			workloadWriter.close();
//			
//			PrintWriter metricsWriter = new PrintWriter(new File("metrics.txt"));
//			for(MetricDataStruct m : metrics) {
//				metricsWriter.println(m.toString());
//			}
//			metricsWriter.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
		
	}
	
//	private void printMetrics() {
//		String output = "";
//		
////		//header
////		output += "time,";
////		output += "actor,";
////		output += "state,";
////		output += "transition,";
////		for(MetricEnum metricName : MetricEnum.values()){
////			output += metricName.name() + ",";
////		}
////		output += "\n";
//		
//		//add all keys and metrics (in order)
////		HashMap<MetricKey, Metric> keys = _metrics.actor_metrics;
////		TreeMap<MetricKey, Metric> keys = _metrics.actor_metrics;
//		
////		Connection c = null;
////		Statement stmt = null;
//		
//		//print output to a file
//		try {
//			PrintWriter metricsWriter = new PrintWriter(new File("metrics.txt"));
//			
////			Class.forName("org.sqlite.JDBC");
////			c = DriverManager.getConnection("jdbc:sqlite:"+dbname+".db");
//			
//			
//			//header
//			output += "time,";
//			output += "actor,";
//			output += "state,";
//			output += "transition,";
//			for(MetricEnum metricName : MetricEnum.values()){
//				output += metricName.name() + ",";
//			}
//			metricsWriter.println(output);
//			
//			for(Map.Entry<MetricKey, Metric> metrics : keys.entrySet()){
//				output = "";
//				
//				//add key
//				MetricKey metricKey = metrics.getKey();
//				output += metricKey.getTime() + ",";
//				output += "'"+metricKey.getActor() + "',";
//				output += "'"+metricKey.getState() + "',";
//				output += metricKey.getTransition() + ",";
//				
//				//add metric (in order)
//				String CAA="",CAV="",CAD="",CAO="",CIA="",CIV="",CID="",CIO="",ENABLED="",ACTIVE="",MA="",MI="",CTA="",CTV="",CTD="",CTO="",CFA="",CFV="",CFD="",CFO="",MT="",MF="";
//				Metric value = metrics.getValue();
//				HashMap<MetricEnum, Integer> values = value.metrics;
//				for(Map.Entry<MetricEnum, Integer> metric : values.entrySet()){
//					switch (metric.getKey().name()){
//						case "CHANNEL_ACTIVE_A" : CAA = metric.getValue().toString(); break;
//						case "CHANNEL_ACTIVE_V" : CAV = metric.getValue().toString(); break;
//						case "CHANNEL_ACTIVE_D" : CAD = metric.getValue().toString(); break;
//						case "CHANNEL_ACTIVE_O" : CAO = metric.getValue().toString(); break;
//						case "CHANNEL_INACTIVE_A" : CIA = metric.getValue().toString(); break;
//						case "CHANNEL_INACTIVE_V" : CIV = metric.getValue().toString(); break;
//						case "CHANNEL_INACTIVE_D" : CID = metric.getValue().toString(); break;
//						case "CHANNEL_INACTIVE_O" : CIO = metric.getValue().toString(); break;
//						case "ENABLED" : ENABLED = metric.getValue().toString(); break;
//						case "ACTIVE" : ACTIVE = metric.getValue().toString(); break;
//						case "MEMORY_ACTIVE" : MA = metric.getValue().toString(); break;
//						case "MEMORY_INACTIVE" : MI = metric.getValue().toString(); break;
//						case "CHANNEL_TEMP_A" : CTA = metric.getValue().toString(); break;
//						case "CHANNEL_TEMP_V" : CTV = metric.getValue().toString(); break;
//						case "CHANNEL_TEMP_D" : CTD = metric.getValue().toString(); break;
//						case "CHANNEL_TEMP_O" : CTO = metric.getValue().toString(); break;
//						case "CHANNEL_FIRE_A" : CFA = metric.getValue().toString(); break;
//						case "CHANNEL_FIRE_V" : CFV = metric.getValue().toString(); break;
//						case "CHANNEL_FIRE_D" : CFD = metric.getValue().toString(); break;
//						case "CHANNEL_FIRE_O" : CFO = metric.getValue().toString(); break;
//						case "MEMORY_TEMP" : MT = metric.getValue().toString(); break;
//						case "MEMORY_FIRE" : MF = metric.getValue().toString(); break;
//					}
//				}
//				output += CAA+","+CAV+","+CAD+","+CAO+","+CIA+","+CIV+","+CID+","+CIO+","+ENABLED+","+ACTIVE+","+MA+","+MI+","+CTA+","+CTV+","+CTD+","+CTO+","+CFA+","+CFV+","+CFD+","+CFO+","+MT+","+MF;
//				metricsWriter.println(output);
//				
////				try {
////					stmt = c.createStatement();
////					String sql = "INSERT INTO metrics "+ //(time, actor, state, transition_id, CHANNEL_ACTIVE_A, CHANNEL_ACTIVE_V, CHANNEL_ACTIVE_D, CHANNEL_ACTIVE_O, CHANNEL_INACTIVE_A, CHANNEL_INACTIVE_V, CHANNEL_INACTIVE_D, CHANNEL_INACTIVE_O, ENABLED, ACTIVE, " +
////							//"MEMORY_ACTIVE, MEMORY_INACTIVE, CHANNEL_TEMP_A, CHANNEL_TEMP_V, CHANNEL_TEMP_D, CHANNEL_TEMP_O, CHANNEL_FIRE_A, CHANNEL_FIRE_V, CHANNEL_FIRE_D,CHANNEL_FIRE_O, MEMORY_TEMP, MEMORY_FIRE) " +
////							"VALUES (" + output + ");";
////					stmt.executeUpdate(sql);
////					stmt.close();
////				} catch (Exception e) {
////					System.out.println("SQL insert error: " + e.getMessage());
////				}
//			}
//			
//			
//			metricsWriter.close();
//			
////			c.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	private void loadTransitions()
	{
		//Get Transitions from the Events
		for(IEvent e : _team.getEvents() ) {
			
			ITransition t = e.getEnabledTransition();
			if ( _clock.getActorTransition((IActor) e) == null ) {
				if ( t != null && !e.isFinished() ) {
					_clock.addTransition((IActor) e, t, random(t.getDurationRange().min(),t.getDurationRange().max()));
					e.decrementCount();
				}
			} else {
				if ( t == null ) {
					_clock.removeTransition((IActor) e);
//					e.incrementCount();
				}
			}
		}
		
		//Get Transitions from the Actor
		HashMap<IActor, ITransition> transitions = _team.getActorTransitions();
		for(Map.Entry<IActor, ITransition> entry : transitions.entrySet() ) {
			ITransition t = entry.getValue();
			_clock.addTransition(entry.getKey(), t, duration(t.getDurationRange()));
//			metric._added_transitions++;
//			ComChannelList inputs = t.getOutputChannels();
//			metric._read_channels = inputs.size();
//			metric._read_audio_channels = inputs.countChannels(Type.AUDIO);
//			metric._read_visual_channels = inputs.countChannels(Type.VISUAL);
//			metric._read_data_channels = inputs.countChannels(Type.DATA);
		}
		
		//deactivate outputs from events after one cycle
		for(IEvent e : _team.getEvents() ) {
			ITransition t = e.getEnabledTransition();
			if(t == null){
				e.deactivate();
			}
		}
	}
	
	
	/**
	 * HELPER METHODS
	 */
	private void initializeRandom() {
		_random = new Random();
		_random.setSeed(0); //Always use the same seed
	}
	
	public int duration(Range range)
	{
		switch(_duration) {
			case MIN:
				return range.min();
			case MAX:
				return range.max();
			case MEAN:
				return range.mean();
			case MIN_MAX:
				if ( random(1) == 0 )
					return range.min();
				else
					return range.max();
			case MIN_MAX_MEAN:
				int val = random(2);
				if ( val == 0 )
					return range.min();
				else if (val == 2)
					return range.max();
				else 
					return range.mean();
			default:
				return 1;
		}
	}
	
	public int random(Range range)
	{
		return random(range.min(), range.max());
	}
	
	public int random(int val)
	{
		return random(0, val);
	}
	
	public int random(int min, int max)
	{
		return Verify.getInt(min, max);
//		return _random.nextInt(max - min + 1) + min;
	}
	
//	public void addMetric(MetricEnum metric, String name)
//	{
//		_metrics.addMetric(metric, name);
//	}
	
	public Integer getClockTime() {
		return _clock.getElapsedTime();
	}
}
