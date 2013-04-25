package CUAS.Simulator;

import java.util.ArrayList;
import java.util.Scanner;

import CUAS.Simulator.ITeam;
import CUAS.Utils.DurationGenerator;
import CUAS.Utils.EventManager;
import CUAS.Utils.GlobalTimer;
import CUAS.Utils.Range;
import CUAS.Utils.DurationGenerator.Mode;

/**
 * This class is a singleton
 * 
 * @author TJ-ASUS
 *
 */
public class Simulator {
	
	private static volatile Simulator _instance = null;
	
	public enum Environment
	{
		DEBUG,
		PRODUCTION
	}
	
	private Simulator() {
		_timer = new GlobalTimer();
		_event_manager = new EventManager();
		_timer.time(0);
	}
	
	public static Simulator getInstance() {
		if ( _instance == null ) {
			synchronized (Simulator .class) {
				if ( _instance == null ) {
					_instance = new Simulator();
				}
			}
		}
		return _instance;
	}
	
	private ITeam _team = null;
	private GlobalTimer _timer = null;
//	private PostOffice _post_office = null;
	private EventManager _event_manager = null;
	private DurationGenerator _duration_generator = null;
	private Environment _env = Environment.DEBUG;
	
	private boolean _running = false;
	
	public void setup(Mode mode, ITeam team)
	{
		setDurations(mode);
		setTeam(team);
	}
	
	public boolean isReady()
	{
		if ( _team != null && _duration_generator != null ) 
			return true;
		else
			return false;
	}
	
	/**
	 * SIMULATION METHODS
	 */
	
	public int getTime()
	{
//		assert isReady() : "The Simulator was not setup properly";
		return _timer.time();
	}
	
	public void setTime(int time)
	{
//		assert isReady() : "The Simulator was not setup properly";
		_timer.time(time);
	}
	
	public int getNextStateTime()
	{
		assert isReady() : "The Simulator was not setup properly";
		return _team.getNextStateTime(getTime());
	}
	
//	public IActor getActor(String role_name)
//	{
//		assert isReady() : "The Simulator was not setup properly";
//		return _team.getActor(role_name);
//	}
	
	public void addInput(String actor_name, IData input)
	{
		assert isReady() : "The Simulator was not setup properly";
		//TODO Add to the PostOffice
//		_team.getActor(actor_name).addInput(input);
	}
	
	public void addInput(String actor_name, ArrayList<IData> input)
	{
		assert isReady() : "The Simulator was not setup properly";
		//TODO Add input to the post office
//		getActor(actor_name).addInput(input);
	}
	
	public ArrayList<IData> getObservations(String actor_name)
	{
		assert isReady() : "The Simulator was not setup properly";
		IActor actor = _team.getActor(actor_name);
		assert actor != null : "Actor not found";
		assert actor instanceof IObservable : "Specified Actor is not observable";
		return ((IObservable) actor).getObservations();
	}
	
	public void addEvent(IEvent event)
	{
		assert isReady() : "The Simulator was not setup properly";
		_event_manager.addEvent(event);
	}
	
	public int duration(Range range) {
		assert isReady() : "The Simulator was not setup properly";
		return _duration_generator.duration(range);
	}
	
	public void setEnvironment(Environment env)
	{
		_env = env;
	}
	
	public boolean debug()
	{
		return _env == Environment.DEBUG ? true : false;
	}
	
	/**
	 * Durations must exist for Simulation timing
	 * 
	 * @param mode
	 * @param ranges
	 */
	private void setDurations(Mode mode)
	{
		_duration_generator = new DurationGenerator(mode);
	}
	
	
	/**
	 * If Team roles require duration information be sure to set that first
	 * 
	 * @param team
	 */
	private void setTeam(ITeam team)
	{
		_team = team;
	}
	
	/**
	 * This is the main simulation method.  It will continually search the roles and
	 * update them.  If there are no future updates that need to happen then the 
	 * Simulator will notify that there are no more state changes to make.
	 */
	public void run() throws AssertionError
	{
		assert isReady() : "The Simulator was not setup properly";
		_running = true;
		if( debug() ) 
			System.out.println("Started Simulation...");
		Scanner readUserInput = new Scanner(System.in);
		
		ArrayList<IData> output = new ArrayList<IData>();
		try {
			//When is the next time something runs
			while(_running) {
				
				//Get user input
//				System.out.println("Enter Command: ");
//				String input = readUserInput.nextLine();
				//TODO Use user input to guide the system
				
//				assert Simulator.getInstance().getRoleState(RoleType.ROLE_UAV) != RoleState.UAV_CRASHED : "UAV Crashed!";
				//TODO Add asserts for anything that is incorrect that can be detected here
			
				int next_team_time = _team.getNextStateTime(getTime());
				//TODO get the next event time
				//TODO use the minimum value between these
				
				if ( next_team_time == 0 ) {
					if ( debug() )
						System.out.println("Nothing to process: " + getTime());
					_running = false;
					
				} else {
					_timer.time(next_team_time);
					if (debug())
						System.out.println("Processing Team States: " + getTime());
					//First Update each Role based on the current time
					System.out.println("Processing Next States...");
					output = _team.processNextState();
					//TODO pass output to post office
					System.out.println("Processing Finished");
					
					//TODO Send Output to the actors
					output.clear();
					
					//Now have each role determine what it's next action will be
					System.out.println("Updating States...");
					output = _team.processInputs();
					//TODO pass output to the post office
					System.out.println("Updating Finished");
					
					//TODO Send Output to the actors
					output.clear();
				}
		
			}//end while
			if (debug())
				System.out.println("Simulation Successful");
		} catch(AssertionError e) {
			if (debug())
				System.out.println("Simulation Failed: " + e.getMessage());
		}
		if (debug())
			System.out.println("Ended Simulation");
		
	}
	

	private void processExternalEvents()
	{
		//TODO Get the event system working
		System.out.println("Events...");
//		ArrayList<Event> events = _event_manager.getEvents(getTime());
//		
//		for(Event e : events) {
//			System.out.println("\tEvent: " + e.type().name());
//		}
//			
//		_team.processExternalEvents(events);
//		System.out.println("Events Finished");
	}
	
}
