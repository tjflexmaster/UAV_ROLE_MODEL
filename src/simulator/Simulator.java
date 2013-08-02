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

public class Simulator {
	
	public enum Mode {
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
	private IDeltaClock _clock;
	private Scanner _scanner = new Scanner(System.in);
	private ArrayList<ITransition> _ready_transitions = new ArrayList<ITransition>();
//	private HashMap<IEvent, Integer> _events = new HashMap<IEvent, Integer>();
//	private ArrayList<IEvent> _events = new ArrayList<IEvent>();
	private ArrayList<IActor> _active_events = new ArrayList<IActor>();
	private Mode _mode;
	private DurationMode _duration;
	private Random _random;
	
	public Simulator(ITeam team, Mode mode, DurationMode duration)
	{
		_clock = new DeltaClock();
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
		HashMap<String, String> data = new HashMap<String, String>();
		String workloadOutput = "";
		do {
			//Get all event and team transitions
			loadTransitions();
			
			//Advance Time
			_clock.advanceTime();
//			System.out.printf("\nadvanced: %d", _clock.elapsedTime());String name = dt.actor.name();
			HashMap<Actor, Integer> workload = _team.getWorkload();
			for(Entry<Actor, Integer> actor_workload : workload.entrySet()){
				String name = actor_workload.getKey()._name;
				String state = actor_workload.getKey().getCurrentState().toString();
				int work = actor_workload.getValue();
				if(data.containsKey(name)){
					data.put(name, data.get(name)+ "\n" + state + "\t" + work);
				}else{
					data.put(name, "\n" + name +"\n" + state + "\t" + work);
				}
//				workloadOutput += ("\n" + name + "\t" + state + "\t" + work);
				System.out.println("\n" + name + "\t" + state + "\t" + work);
			}
			
//			int workload = dt.actor.getWorkload();
//			if(!(dt.actor instanceof Event)){
//				System.out.print("\nActor: " + name + " State: " + ((Actor)dt.actor).getCurrentState() + " Workload: " + workload);
//				PrintWriter workloadWriter;
//				try {
//					workloadWriter = new PrintWriter(new File("workload.txt"));
//					workloadWriter.append("\n" + name + "\t" + ((Actor)dt.actor).getCurrentState() + "\t" + workload);
//					workloadWriter.close();
//				} catch (FileNotFoundException e) {
//					e.printStackTrace();
//				}
//			}
			//Process Ready Transitions
			_ready_transitions.clear();
			_ready_transitions.addAll(_clock.getReadyTransitions());
			for(ITransition transition : _ready_transitions){
				//System.out.println('\n' + transition.toString());
				transition.fire();
			}
		} while (!_ready_transitions.isEmpty());

		try {
			PrintWriter workloadWriter = new PrintWriter(new File("workload.txt"));
			for(Entry<String, String> actor_workload : data.entrySet())
				workloadWriter.print(actor_workload.getValue());
			workloadWriter.close();
		} catch (FileNotFoundException e) {
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
	
}
