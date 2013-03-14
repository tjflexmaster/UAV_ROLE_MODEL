package NewModel.Simulation;

import java.util.ArrayList;

import NewModel.Roles.RoleState;
import NewModel.Roles.RoleType;
import NewModel.Utils.DataType;
import NewModel.Utils.GlobalTimer;
import NewModel.Utils.PostOffice;


public class Simulator {
	
	public static final DefaultTeam team = new DefaultTeam();
	public static final GlobalTimer timer = new GlobalTimer();
	public static final PostOffice post_office = new PostOffice();
	
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
		return team.getNextStateTime();
	}
	
	public static ArrayList<DataType> removePosts(RoleState state)
	{
		return post_office.removePosts(state);
	}
	
	public static void addPost(RoleState state, DataType data)
	{
		post_office.addPost(state, data);
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
		
		//When is the next time something runs
		while(_running) {
		
			int next_team_time = team.getNextStateTime();
			int next_event_time = 0;
			
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
		}//end while
		
		System.out.println("Ended Simulation");
		
	}
	
	private void processNextStates()
	{
		//First Update each Role based on the current time
		System.out.println("Processing Next States...");
		team.processNextState();
		System.out.println("Finished");
		
		//Now have each role determine what it's next action will be
		System.out.println("Updating States...");
		team.updateState();
		System.out.println("Finished");
	}

	private void processExternalEvents()
	{
		//Do nothing for now
	}
	
}
