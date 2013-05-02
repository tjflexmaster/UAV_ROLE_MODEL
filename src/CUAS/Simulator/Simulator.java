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
		_post_office = new PostOffice();
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
	private EventManager _event_manager = null;
	private DurationGenerator _duration_generator = null;
	private Environment _env = Environment.DEBUG;
	private PostOffice _post_office = null;
	
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
		assert isReady() : "getNextStateTime(): The Simulator was not setup properly";
		return _team.getNextStateTime(getTime());
	}
	
	public void addOutput(String actor_name, IData input){
		_post_office.addOutput(input, actor_name);
	}
	
	public void addOutputs(String actor_name, ArrayList<IData> inputs){
		_post_office.addOutputs(inputs, actor_name);
	}
	
	public ArrayList<IData> getInput(String actor_name)
	{
		return _post_office.getInput(actor_name);
	}
	
	public void linkInput(String parent, String child)
	{
		_post_office.linkInput(parent, child);
	}
	
	public void linkObservations(String parent, String child)
	{
		_post_office.linkObservations(parent, child);
	}
	
	public ArrayList<IData> getObservations(String actor_name)
	{
		assert isReady() : "getObservations: The Simulator was not setup properly";
		return _post_office.getObservations(actor_name);
	}
	
	public void addObservation(IData data, String actor_name)
	{
		//Give the observation to the post office
		_post_office.addObservation(data, actor_name);
	}
	
	public void addObservations(ArrayList<IData> data, String actor_name)
	{
		_post_office.addObservations(data, actor_name);
	}
	
	public void addEvent(IEvent event)
	{
		assert isReady() : "The Simulator was not setup properly";
		_event_manager.addEvent(event);
	}
	
	public int duration(Range range) {
		assert isReady() : "duration(): The Simulator was not setup properly";
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
		
		try {
			int time_to_run = 0;
			//When is the next time something runs
			while(_running) {
				/**
				 * By calling getNextEventTime() on the event manager events are automatically processed.
				 */
				int next_time = 0;
				int evt_time = _event_manager.getNextEventTime();
				int team_time = _team.getNextStateTime(getTime());
				if (  evt_time > 0 && team_time > 0 )
					next_time = Math.min(evt_time, team_time);
				else
					next_time = Math.max(evt_time, team_time);
				
				//Get user input
				if ( debug() ) {
					if(time_to_run <= next_time){
						boolean needCommand = true;
						while (needCommand) {
							System.out.println("Enter Command: ");
							String input = readUserInput.nextLine();
							if (input.isEmpty()) {
								needCommand = false;
							} else {
								try {
									int inputInt = Integer.parseInt(input);
									time_to_run = inputInt;
									needCommand = false;
								} catch(Exception e) {
									System.out.println("USAGE: Command may only be empty or a number.");
								}
							}
						}
					}
				}
				
				if ( next_time == 0 ) {
					if ( debug() )
						System.out.println("Nothing to process: " + getTime());
					int test = _event_manager.totalEventsRemaining();
					assert test > 0 : "Terminated when there were still events left to be processed.";
					_running = false;
					
				} else {
					_timer.time(next_time);
					System.out.println("At time: " + next_time);
					//First Process Events
					_event_manager.processNextState();
					_event_manager.processInputs();
					
					//First Update each Role based on the current time
					if (debug())
						System.out.println("Processing Next States...");
					_team.processNextState();
					if (debug())
						System.out.println("Processing Finished");
					
					//Process both the Outputs and the Observations so they will be visible for
					//the processInputs step
					_post_office.processInboundData();
					
					//Now have each role determine what it's next action will be
					if (debug())
						System.out.println("Updating States...");
					_team.processInputs();
					if (debug())
						System.out.println("Updating Finished\n");
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
	
}
