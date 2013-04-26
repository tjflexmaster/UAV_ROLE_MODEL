package CUAS.Simulator;

import java.util.ArrayList;

import NewModel.Roles.RoleState;
import NewModel.Roles.RoleType;

public interface ITeam {

	/**
	 * Return the actor object by name
	 * @param actor_name
	 * @return
	 */
	public IActor getActor(String actor_name);
	
	/**
	 * Returns the minimum time that an actor will change state.
	 * @param time
	 * @return
	 */
	public int getNextStateTime(int time);
	
	/**
	 * This method has each team member process their next state.
	 * It gathers all of the output from each actor and returns this output
	 * to the simulator.
	 * @return
	 */
	public void processNextState();
	
	/**
	 * This method has each team member process their inputs.
	 * It gathers all of the output from each actor and returns this output
	 * to the simulator.
	 * @return
	 */
	public void processInputs();
	
}
