package simulator;

public interface IEvent {
	
	/**
	 * Checks to see how many of these events can be fired and reports if more still need to be fired.
	 * 
	 * @return
	 */
	boolean isFinished();
	
	ITransition getEnabledTransition();
	
	void setEventCount(int count);
	
	int getEventCount();
	
	void incrementCount();
	
	void decrementCount();
	
	void deactivate();

}
