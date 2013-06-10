package Utilities;

public class Data {
	
	/**
	 * this is the name the event chooses
	 * these names should be formated sourceActor_action_desitination
	 */
	public String name;
	
	/**
	 * this constructor is used for creating new outputs
	 * @param _name represents the name of the output in the format sourceActor_action_destination
	 * @param _maxTimeTilAppearance represents the maximum amount of time it will take for the output to appear
	 */
	public Data(String _name, int _maxTimeTilAppearance, int _maxDurationOfVisibility) {
		
		name = _name;
		
	}
	
	public String toString() {
		
		return name;
		
	}
	
}
