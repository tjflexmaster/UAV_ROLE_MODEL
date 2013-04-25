/**
 * 
 */
package CUAS.Simulator;

import java.util.ArrayList;





/**
 * This interface is attached to all Roles.  Each role is observable and
 * must be able to perform specific actions, such as executing Events.
 * 
 * @author TJ-ASUS
 *
 */
public interface IActor {
	/**
	 * Get next state time tells the Simulator the next time step that this actor needs
	 * processing time.
	 */
	int nextStateTime();

	/**
	 * This method updates the Role to whatever state is next.  It is a simple method,
	 * nothing needs to be checked it is just immediately updated.  If it is not time 
	 * for the event then nothing happens.  If the next event is processed then the simulator
	 * will call updateState on all of the roles.
	 */
	ArrayList<IData> processNextState();
	
	
	/**
	 * This is where the logic for each Role resides.  When this is called a role determines
	 * how it should act, setting its next event and possibly changing its current state.
	 */
	ArrayList<IData> processInputs();
	
	
	/**
	 * Add Input to the Actor
	 * 
	 * @param input
	 */
	void addInput(IData data);
	void addInput(ArrayList<IData> data);

	
}
