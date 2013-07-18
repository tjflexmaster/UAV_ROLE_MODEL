package simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
		do {
			//Get all event and team transitions
			loadTransitions();
			
			//Advance Time
			_clock.advanceTime();
			System.out.println("advanced");
			//Process Ready Transitions
			_ready_transitions.clear();
			_ready_transitions.addAll(_clock.getReadyTransitions());
			for(ITransition transition : _ready_transitions){
				transition.fire();
			}
		} while (!_ready_transitions.isEmpty());
		
		
	}
	
	private void loadTransitions()
	{
		//Get Transitions from the Events
		for(IEvent e : _team.getEvents() ) {
			
			ITransition t = e.getEnabledTransition();
			if ( _clock.getActorTransition((IActor) e) == null ) {
				if ( t != null && !e.isFinished() ) {
					_clock.addTransition((IActor) e, t, random(0,10000));
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
	
	
	
	
	
	
	
	
	
	/**
	 * this method initializes the simulator
	 * then this method makes the clock run the simulation
	 * @param args should be left blank, all input will be ignored
	 */
//	public static void main(String[] args) {
//		//initialize simulation variables
//		ITeam team = new WiSARTeam();
//		IDeltaClock clock = new DeltaClock();
//		Scanner scanner = new Scanner(System.in);
//		ArrayList<ITransition> readyTransitions = new ArrayList<ITransition>();
//		int runTime = 0;
//		
//		//run the simulator until the clock is empty
//		do {
//			//First get the Actor Transitions
//			HashMap<IActor, ITransition> transitions = team.getEventTransitions();
//			for(Map.Entry<IActor, ITransition> entry : transitions.entrySet() ) {
//				//TODO get a duration range from the transition
//				clock.addTransition(entry.getKey(), entry.getValue(), 1);
//			}
//			
//			//advance time
//			clock.advanceTime();
//			runTime = clock.elapsedTime();
//			
//			//Process all ready transitions
//			readyTransitions = clock.getReadyTransitions();
//			
//			//communicate with the user
//			runTime = communicateWithUser(scanner, clock, readyTransitions, runTime);
//			
//			//fire all ready transitions
//			for (int index = 0; index < readyTransitions.size(); index++) {
//				readyTransitions.get(index).fire();
//			}
//		} while (!readyTransitions.isEmpty());
//		
//		//close the scanner once the simulation is complete
//		scanner.close();
//	}
	
	/**
	 * this method prints swim lanes and accepts user commands
	 * @param scanner 
	 * @param clock
	 * @param readyTransitions
	 * @return return the integer time value to advance to
	 */
//	private static int communicateWithUser(Scanner scanner, IDeltaClock clock, ArrayList<ITransition> readyTransitions, int runTime) {
//		if(readyTransitions.isEmpty()){
//			if(clock.elapsedTime() == 0){
//				System.out.println("Would you like to run in debug mode (y / n)?");
//				String response = scanner.nextLine();
//				if(response.equalsIgnoreCase("y")){
//					debug = true;
//					System.out.println("Entering Debug Mode");
//				}else if(response.equalsIgnoreCase("n")){
//					debug = false;
//					System.out.println("Running Simulation");
//				}else{
//					debug = true;
//					System.out.println("Entering Debug Mode");
//				}
//			} else {
//				System.out.println("Thank you for using the simulator, goodbye.");
//				return runTime;
//			}
//		} else {
//			System.out.println("----Time: " + clock.elapsedTime() + "----");
//			for(ITransition transition : readyTransitions){
//				System.out.println(transition.toString());
//			}
//		}
//		
//		if (runTime <= clock.elapsedTime()) {
//			System.out.println("In integer format enter a time to skip to. Then press Enter.");
//			String response = scanner.nextLine();
//			try {
//				runTime = Integer.parseInt(response);
//			} catch(NumberFormatException e) {
//				System.out.println("Contining to the next transition.");
//			}
//		}
//		return runTime;
//	}
	
}
