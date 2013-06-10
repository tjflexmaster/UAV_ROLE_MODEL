package Utilities;

public class Data {
	
	/**
	 * this tells the simulator if the data should be visible
	 */
	boolean isVisible;
	
	/**
	 * this is the name the event chooses
	 * these names should be formated sourceActor_action_desitination
	 */
	public String name;
	
	/**
	 * this is the maximum time it will take until an output can appear
	 * in other words, this is the time it takes an actor to accomplish a task and produce an output
	 * the simulator can non-deterministically decide if it appears earlier
	 */
	public int maxTimeTilAppearance;
	public int timeTilAppearance;
	
	/**
	 * this is the maximum time the output will exist, and be visible, in the simulator
	 * the simulator can non-deterministically decide to remove the output early 
	 * this should be set to one if the source is a human
	 */
	public int maxDurationOfVisibility;
	public int durationOfVisibility;
	
	/**
	 * this constructor is used for creating new outputs
	 * @param _name represents the name of the output in the format sourceActor_action_destination
	 * @param _maxTimeTilAppearance represents the maximum amount of time it will take for the output to appear
	 */
	public Data(String _name, int _maxTimeTilAppearance, int _maxDurationOfVisibility) {
		
		isVisible = false;
		name = _name;
		maxTimeTilAppearance = _maxTimeTilAppearance;
		timeTilAppearance = _maxTimeTilAppearance;
		maxDurationOfVisibility = _maxDurationOfVisibility;
		durationOfVisibility = _maxDurationOfVisibility;
		
	}
	
	public String toString() {
		
		return name;
		
	}
	
}
