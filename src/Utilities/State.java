package Utilities;

public class State {
	
	/**
	 * @author rob.ivie
	 * this is the name of the state
	 */
	String name;
	
	/**
	 * this is the maximum time it will take for an actor to change to this state
	 */
	public int maxTimeTilAppearance;
	public int timeTilAppearance;

	/**
	 * @param name
	 * this constructor is used for creating new states
	 */
	public State(String _name, int _maxTimeTilAppearance) {
		
		name = _name;
		maxTimeTilAppearance = _maxTimeTilAppearance;
		timeTilAppearance = _maxTimeTilAppearance;
		
	}
	
	public String toString() {
		
		return name;
		
	}

}
