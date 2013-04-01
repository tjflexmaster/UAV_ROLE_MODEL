/**
 * 
 */
package NewModel.Roles;

import java.util.ArrayList;

import NewModel.Events.Event;




/**
 * This interface is attached to all Roles.  Each role is observable and
 * must be able to perform specific actions, such as executing Events.
 * 
 * @author TJ-ASUS
 *
 */
public interface IRole {

	/**
	 * This method updates the Role to whatever state is next.  It is a simple method,
	 * nothing needs to be checked it is just immediately updated.  If it is not time 
	 * for the event then nothing happens.  If the next event is processed then the simulator
	 * will call updateState on all of the roles.
	 */
	boolean processNextState();
	
	
	/**
	 * This is where the logic for each Role resides.  When this is called a role determines
	 * how it should act, setting its next event and possibly changing its current state.
	 */
	void updateState();
	
	/**
	 * This is where the logic for handling events resides.  When events occur the role will look at
	 * each event and decide how it should change its state.
	 * @param events
	 */
	public abstract void processEvents(ArrayList<Event> events);
	
	/**
	 * Helper methods for obtaining role specific information
	 */
	int nextStateTime();
	RoleState state();
	RoleState nextState();
	RoleType type();
	
}
