package CUAS.Simulator;

public interface ITeam {

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
