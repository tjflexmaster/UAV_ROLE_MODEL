package simulator;

public class Range {
	
	
	private int _minimum;
	private int _maximum;

	/**
	 * creates a bounded duration
	 * @param minimum represents the minimum bound of the duration in seconds
	 * @param maximum represents the maximum bound of the duration in seconds
	 */
	Range(int minimum, int maximum) {
		_minimum = minimum;
		_maximum = maximum;
	}
	
	/**
	 * creates a fixed duration
	 * @param duration represents the fixed time duration in seconds
	 */
	Range(int duration){
		_minimum = duration;
		_maximum = duration;
	}

}
