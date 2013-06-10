package Utilities;

public class State {
	
	/**
	 * @author rob.ivie
	 * this is the name of the state
	 */
	String name;

	/**
	 * @param name
	 * this constructor is used for creating new states
	 */
	public State(String _name, int _maxTimeTilAppearance) {
		
		name = _name;
		
	}
	
	public String toString() {
		
		return name;
		
	}

}
