package NewModel.Simulation;

import java.util.ArrayList;
import java.util.Scanner;

import NewModel.Events.Event;
import NewModel.Events.EventManager;
import NewModel.Roles.RoleState;
import NewModel.Roles.RoleType;
import NewModel.Utils.DataType;
import NewModel.Utils.GlobalTimer;
import NewModel.Utils.PostOffice;
import NewModel.Utils.PostOffice.POBOX;


public class Simulator {
	
	public static final DefaultTeam team = new DefaultTeam();
	public static final GlobalTimer timer = new GlobalTimer();
	public static final PostOffice post_office = new PostOffice();
	public static final EventManager event_manager = new EventManager();
	
	private boolean _running = false;
	
	public static int getTime()
	{
		return timer.time();
	}
	
	public static void setTime(int time)
	{
		timer.time(time);
	}
	
	public static RoleState getRoleState(RoleType type)
	{
		return team.getRoleState(type);
	}
	
	public static int getNextStateTime()
	{
		return team.getNextStateTime(getTime());
	}
	
	public static ArrayList<DataType> removePosts(POBOX pobox)
	{
		return post_office.removePosts(pobox);
	}
	
	public static void addPost(POBOX pobox, DataType data)
	{
		post_office.addPost(pobox, data);
	}
	
	public static ArrayList<DataType> getPosts(POBOX pobox)
	{
		return post_office.getPosts(pobox);
	}
	
	public static boolean isPoboxEmpty(POBOX pobox)
	{
		return post_office.isPoboxEmpty(pobox);
	}
	
	public static void clearPost(POBOX pobox)
	{
		post_office.clearPost(pobox);
	}
	
	public static void addExternalEvent(Event event, int time)
	{
		event_manager.addEvent(event, time);
	}
	
	public Simulator()
	{
		//Initialize time to 1
		timer.time(0);
		
	}
	
	/**
	 * This is the main simulation method.  It will continually search the roles and
	 * update them.  If there are no future updates that need to happen then the 
	 * Simulator will notify that there are no more state changes to make.
	 */
	public void run()
	{
		_running = true;
		System.out.println("Started Simulation...");
		Scanner readUserInput = new Scanner(System.in);
		
		//When is the next time something runs
		while(_running) {
			
			//Get user input
//			System.out.println("Enter Command: ");
//			String input = readUserInput.nextLine();
			//TODO Use user input to guide the system
			assert Simulator.getRoleState(RoleType.ROLE_UAV) != RoleState.UAV_CRASHED;
	
		
			int next_team_time = team.getNextStateTime(getTime());
			int next_event_time = event_manager.getNextEventTime(getTime());
			
			if ( next_event_time == 0 && next_team_time == 0 ) {
				System.out.println("Nothing to process: " + getTime());
				_running = false;
			} else if ( next_event_time != 0 && next_team_time == 0 ) {
				timer.time(next_event_time);
				System.out.println("Processing External Events: " + getTime());
				processExternalEvents();
			} else if ( next_event_time == 0 && next_team_time != 0 ) {
				timer.time(next_team_time);
				System.out.println("Processing Team Events: " + getTime());
				processNextStates();
			} else if ( next_event_time < next_team_time) {
				timer.time(next_event_time);
				System.out.println("Processing External Events: " + getTime());
				processExternalEvents();
			} else if ( next_team_time < next_event_time ) {
				timer.time(next_team_time);
				System.out.println("Processing Team Events: " + getTime());
				processNextStates();
			} else if ( next_event_time == next_team_time ) {
				timer.time(next_event_time);
				System.out.println("Processing External Events: " + getTime());
				processExternalEvents();
				System.out.println("Processing Team Events: " + getTime());
				processNextStates();
			}
			System.out.println("\n");
		}//end while
		
		System.out.println("Ended Simulation");
		
	}
	
	private void processNextStates()
	{
		//First Update each Role based on the current time
		System.out.println("Processing Next States...");
		team.processNextState();
		System.out.println("Processing Finished");
		
		//Now have each role determine what it's next action will be
		System.out.println("Updating States...");
		team.updateState();
		System.out.println("Updating Finished");
	}

	private void processExternalEvents()
	{
		System.out.println("Events...");
		ArrayList<Event> events = event_manager.getEvents(getTime());
		
		for(Event e : events) {
			System.out.println("\tEvent: " + e.type().name());
		}
			
		team.processExternalEvents(events);
		System.out.println("Events Finished");
	}
	
}
