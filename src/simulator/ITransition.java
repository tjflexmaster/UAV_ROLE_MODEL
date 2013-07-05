package simulator;

public interface ITransition {

	/**
	 * Fire the transition causing all of its output and state changes to occur immediately.
	 */
	void fire();
	
	/**
	 * If the transition is possible then this method will return true.  This method should be overwritten
	 * to control the logic which decides if the transition is enabled.
	 * @return
	 */
	boolean isEnabled();
	
	/**
	 * return an int from the range of durations
	 * @return
	 */
	int getDuration();
}
