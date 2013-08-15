package simulator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;

import model.team.WiSARTeam;
import simulator.ComChannel.Type;
import simulator.Metric.MetricEnum;



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
	
//	public static boolean debug = true;
	private ITeam _team;
	private IDeltaClock _clock;// = new DeltaClock();
	private Scanner _scanner = new Scanner(System.in);
	private HashMap<IActor, ITransition> _ready_transitions = new HashMap<IActor, ITransition>();
//	private HashMap<IEvent, Integer> _events = new HashMap<IEvent, Integer>();
//	private ArrayList<IEvent> _events = new ArrayList<IEvent>();
	private ArrayList<IActor> _active_events = new ArrayList<IActor>();
	private DebugMode _mode;// = DebugMode.DEBUG;
	private DurationMode _duration;// = DurationMode.MIN;
	private Random _random;
	public MetricManager _metrics;// = new MetricManager();
	
	//Singleton variables
	private boolean _setup = false;
	private static Simulator _instance = null;
	
	//Actor, State, Transition variable
	public String actor;
	public String state;
	public int transition;
	
	
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
		_metrics = new MetricManager();
	}
	
	public void setup(ITeam team, DebugMode mode, DurationMode duration)
	{
		_setup = false;
		_clock = new DeltaClock();
		_metrics = new MetricManager();
		
		_team = team;
		_mode = mode;
		_duration = duration;
		
		initializeRandom();
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
			
			//Save time for the current Key in the metric manager
			_metrics.currentKey._time = _clock.getElapsedTime();
			
			//Start a new metric
//			metric = new MetricDataStruct(_clock.elapsedTime());
			
//			System.out.printf("\nadvanced: %d", _clock.elapsedTime());String name = dt.actor.name();
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
			for(Entry e : _ready_transitions.entrySet()){
				//System.out.println('\n' + transition.toString());
				
				//Set metric key
				IActor a = (IActor) e.getKey();
				ITransition t = (ITransition) e.getValue();
				Simulator.getSim()._metrics.currentKey._actor_name = a.name();
				Simulator.getSim()._metrics.currentKey._state = a.getCurrentState().getName();
				Simulator.getSim()._metrics.currentKey._transition = t.getIndex();
				
				t.fire();
//				metric._fired_transitions++;
//				ComChannelList outputs = transition.getOutputChannels();
//				metric._updated_channels = outputs.size();
//				metric._updated_audio_channels = outputs.countChannels(Type.AUDIO);
//				metric._updated_visual_channels = outputs.countChannels(Type.VISUAL);
//				metric._updated_data_channels = outputs.countChannels(Type.DATA);
//				metric._states_changed++;
			}
			
			System.out.println("Looping" + _clock.getElapsedTime());
//			System.out.println(_metrics.toString());
			printMetrics();
			
			System.out.println("Test");
			
		} while (!_ready_transitions.isEmpty());
		
		printMetrics();

		System.out.println("Finished");
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
	
	private void printMetrics() {
		String output = "";
		
		//header
		output += "time,";
		output += "actor,";
		output += "state,";
		output += "transition,";
		for(MetricEnum metricName : MetricEnum.values()){
			output += metricName.name() + ",";
		}
		
		//add all keys and metrics
		HashMap<MetricKey, Metric> keys = _metrics.actor_metrics;
		for(Map.Entry<MetricKey, Metric> metrics : keys.entrySet()){
			//add key
			MetricKey metricKey = metrics.getKey();
			output += metricKey._time + ",";
			output += metricKey._actor_name + ",";
			output += metricKey._state + ",";
			output += metricKey._transition + ",";
			
			//add metric
			Metric value = metrics.getValue();
			HashMap<MetricEnum, Integer> values = value.metrics;
			for(Map.Entry<MetricEnum, Integer> metric : values.entrySet()){
				output += metric.getValue() + ",";
			}
			output += "\n";
		}
		
		//print output to a file
		try {
			PrintWriter metricsWriter = new PrintWriter(new File("metrics.txt"));
			metricsWriter.print(output);
			metricsWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

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
		return _random.nextInt(max - min + 1) + min;
	}
	
	public void addMetric(MetricEnum metric)
	{
		_metrics.addMetric(metric);
	}
	
	public Integer getClockTime() {
		return _clock.getElapsedTime();
	}
}
