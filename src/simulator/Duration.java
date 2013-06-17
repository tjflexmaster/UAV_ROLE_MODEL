package simulator;

public class Duration {
	
	/* Parent Search Durations */
	
	/* Mission Manager Durations */
	
	/* Video Operator Outputs */
	
	/* Video Operator Gui Outputs */
	
	/* UAV Operator Outputs */
	
	/* UAV Operator Gui Outputs*/
	
	int _minimum;
	int _maximum;

	/**
	 * creates a bounded duration
	 * @param minimum represents the minimum bound of the duration
	 * @param maximum represents the maximum bound of the duration
	 */
	public Duration(int minimum, int maximum) {
		
		_minimum = minimum;
		_maximum = maximum;
		
	}
	
	/**
	 * creates a fixed duration
	 * @param duration
	 */
	public Duration(int duration){
		
		_minimum = duration;
		_maximum = duration;
		
	}

	/**
	 * works like a normal toString methods 
	 * @return return a string representation of the duration
	 */
	public String toString() {
		String result = "";
		
		if (_minimum == _maximum) {
			result = Integer.toString(_maximum);
		} else {
			result = "(" + Integer.toString(_minimum) + "-" + Integer.toString(_maximum) + ")";
		}
		
		return result;
	}

}
