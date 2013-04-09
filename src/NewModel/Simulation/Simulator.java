package NewModel.Simulation;

import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

import NewModel.Simulation.ITeam;
import NewModel.Events.Event;
import NewModel.Events.EventManager;
import NewModel.Roles.RoleState;
import NewModel.Roles.RoleType;
import NewModel.Utils.DataType;
import NewModel.Utils.DurationGenerator;
import NewModel.Utils.DurationGenerator.Mode;
import NewModel.Utils.GlobalTimer;
import NewModel.Utils.PostOffice;
import NewModel.Utils.PostOffice.POBOX;
import NewModel.Utils.Range;

/**
 * This class is a singleton
 * 
 * @author TJ-ASUS
 *
 */
public class Simulator {
	
	private static volatile Simulator _instance = null;
	
	private Simulator() {
		
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
	private PostOffice _post_office = null;
	private EventManager _event_manager = null;
	private DurationGenerator _duration_generator = null;
	
	private boolean _running = false;
	
	public void setup(Mode mode, Map<String, Range> ranges, ITeam team)
	{
		_timer = new GlobalTimer();
		_post_office = new PostOffice();
		_event_manager = new EventManager();
		_timer.time(0);
		setDurations(mode, ranges);
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
		assert isReady() : "The Simulator was not setup properly";
		return _timer.time();
	}
	
	public void setTime(int time)
	{
		assert isReady() : "The Simulator was not setup properly";
		_timer.time(time);
	}
	
	public RoleState getRoleState(RoleType type)
	{
		assert isReady() : "The Simulator was not setup properly";
		return _team.getRoleState(type);
	}
	
	public int getNextStateTime()
	{
		assert isReady() : "The Simulator was not setup properly";
		return _team.getNextStateTime(getTime());
	}
	
	public ArrayList<DataType> removePosts(POBOX pobox)
	{
		assert isReady() : "The Simulator was not setup properly";
		return _post_office.removePosts(pobox);
	}
	
	public void addPost(POBOX pobox, DataType data)
	{
		assert isReady() : "The Simulator was not setup properly";
		_post_office.addPost(pobox, data);
	}
	
	public ArrayList<DataType> getPosts(POBOX pobox)
	{
		assert isReady() : "The Simulator was not setup properly";
		return _post_office.getPosts(pobox);
	}
	
	public boolean isPoboxEmpty(POBOX pobox)
	{
		assert isReady() : "The Simulator was not setup properly";
		return _post_office.isPoboxEmpty(pobox);
	}
	
	public void clearPost(POBOX pobox)
	{
		assert isReady() : "The Simulator was not setup properly";
		_post_office.clearPost(pobox);
	}
	
	public void addExternalEvent(Event event, int time)
	{
		assert isReady() : "The Simulator was not setup properly";
		_event_manager.addEvent(event, time);
	}
	
	public int duration(String key)
	{
		assert isReady() : "The Simulator was not setup properly";
		return _duration_generator.duration(key);
	}
	
	/**
	 * Durations must exist for Simulation timing
	 * 
	 * @param mode
	 * @param ranges
	 */
	private void setDurations(Mode mode, Map<String, Range> ranges)
	{
		_duration_generator = new DurationGenerator(mode, ranges);
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
		System.out.println("Started Simulation...");
		Scanner readUserInput = new Scanner(System.in);
		
		try {
			//When is the next time something runs
			while(_running) {
				
				//Get user input
				System.out.println("Enter Command: ");
				String input = readUserInput.nextLine();
				//TODO Use user input to guide the system
				
				assert Simulator.getInstance().getRoleState(RoleType.ROLE_UAV) != RoleState.UAV_CRASHED : "UAV Crashed!";
		
			
				int next_team_time = _team.getNextStateTime(getTime());
				int next_event_time = _event_manager.getNextEventTime(getTime());
				
				if ( next_event_time == 0 && next_team_time == 0 ) {
					System.out.println("Nothing to process: " + getTime());
					_running = false;
				} else if ( next_event_time != 0 && next_team_time == 0 ) {
					_timer.time(next_event_time);
					System.out.println("Processing External Events: " + getTime());
					processExternalEvents();
				} else if ( next_event_time == 0 && next_team_time != 0 ) {
					_timer.time(next_team_time);
					System.out.println("Processing Team Events: " + getTime());
					processNextStates();
				} else if ( next_event_time < next_team_time) {
					_timer.time(next_event_time);
					System.out.println("Processing External Events: " + getTime());
					processExternalEvents();
				} else if ( next_team_time < next_event_time ) {
					_timer.time(next_team_time);
					System.out.println("Processing Team Events: " + getTime());
					processNextStates();
				} else if ( next_event_time == next_team_time ) {
					_timer.time(next_event_time);
					System.out.println("Processing External Events: " + getTime());
					processExternalEvents();
					System.out.println("Processing Team Events: " + getTime());
					processNextStates();
				}
				System.out.println("\n");
			}//end while
			System.out.println("Simulation Successful");
		} catch(AssertionError e) {
			System.out.println("Simulation Failed: " + e.getMessage());
		}
		System.out.println("Ended Simulation");
		
	}
	
	private void processNextStates()
	{
		//First Update each Role based on the current time
		System.out.println("Processing Next States...");
		_team.processNextState();
		System.out.println("Processing Finished");
		
		//Now have each role determine what it's next action will be
		System.out.println("Updating States...");
		_team.updateState();
		System.out.println("Updating Finished");
	}

	private void processExternalEvents()
	{
		System.out.println("Events...");
		ArrayList<Event> events = _event_manager.getEvents(getTime());
		
		for(Event e : events) {
			System.out.println("\tEvent: " + e.type().name());
		}
			
		_team.processExternalEvents(events);
		System.out.println("Events Finished");
	}
	
}
