package simulator;

public enum Range {
	
	/* Parent Search Durations */
	
	PS_SEND_DATA_PS(1,5),
	PS_POKE_MM(5,20), 
	PS_TX_DATA_MM(5,20),
	
	/* Mission Manager Durations */
	
	MM_POKE_PS(5,20),
	MM_POKE_TO_TX_PS(5),
	MM_RX_PS(22),
	MM_POKE_VO(5,20),
	
	/* Video Operator Outputs */

	VO_RX_MM(20),
	
	/* Video Operator Gui Outputs */
	
	/* UAV Operator Outputs */
	
	OP_TX_OGUI(5,20),
	OP_RX_MM(22),
	
	/* UAV Operator Gui Outputs*/
	
	/* UAV Battery Outputs*/
	
	UAVBAT_ACTIVE_TO_LOW(3600),
	UAVBAT_DURATION(3600),
	
	/* General */
	
	ACK(1),
	NEXT(1), MM_TX_OP(5,20);
	
	private Integer _minimum;
	private Integer _maximum;

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
	Range(Integer duration){
		_minimum = duration;
		_maximum = duration;
	}
	
	/**
	 * updates the _min and _max
	 * @param duration represents the integer value of the duration
	 */
	public Range update(Integer duration){
		_minimum = duration;
		_maximum = duration;
		return this;
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

	public int getdur() {
//		if(_minimum == _maximum)
//			return _maximum;
//		else{
//			Random rand = new Random();
//			int dur = _minimum + rand.nextInt(_maximum-_minimum);
//			return dur;
//		}
		return _maximum;
	}

}
