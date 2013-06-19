package team;

public enum Duration {
	
	/* Parent Search Durations */
	
	/* Mission Manager Durations */
	
	MM_POKE_PS(5,20),
	MM_POKE_TO_TX_PS(5);
	
	/* Video Operator Outputs */
	
	/* Video Operator Gui Outputs */
	
	/* UAV Operator Outputs */
	
	/* UAV Operator Gui Outputs*/
	
	int _minimum;
	int _maximum;

	/**
	 * creates a bounded duration
	 * @param minimum represents the minimum bound of the duration in seconds
	 * @param maximum represents the maximum bound of the duration in seconds
	 */
	Duration(int minimum, int maximum) {
		
		_minimum = minimum;
		_maximum = maximum;
		
	}
	
	/**
	 * creates a fixed duration
	 * @param duration represents the fixed time duration in seconds
	 */
	Duration(int duration){
		
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
