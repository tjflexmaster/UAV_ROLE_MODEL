package model.team;

import simulator.Range;

public enum Duration {
	
	/* Parent Search Durations */
	
	PS_SEND_DATA_PS(1,5),
	PS_POKE_MM(5,20), 
	PS_TX_DATA_MM(5,20),
	
	/* Mission Manager Durations */
	
	MM_POKE_PS(5,20),
	MM_POKE_TO_TX_PS(5),
	MM_RX_PS(22),
	MM_POKE_VO(5,20),
	MM_POKE_OP(5, 20),
	MM_TX_OP(5,20),
	MM_OBSERVING_VGUI(15,20),
	MM_TX_VGUI(5,20),
	MM_TO_IDLE(5,20),
	MM_TX_PS(5,20),
	MM_TX_VO(5,20),
	
	/* Video Operator Outputs */

	VO_RX_MM(20),
	VO_TX_MM(5,20),
	
	/* Video Operator Gui Outputs */
	
	/* UAV Operator Outputs */
	
	OP_TX_MM(5,20),
	OP_TX_OGUI(5,20),
	OP_RX_MM(22),
	
	/* UAV Operator Gui Outputs*/
	
	/* UAV Battery Outputs*/

	UAVBAT_ACTIVE_TO_LOW(50),
	UAVBAT_LOW_TO_DEAD(5),
	UAVBAT_DURATION(3600),
	UAV_ADJUST_PATH(20,100),
	UAV_LANDING(5,20),
	
	/* General */
	
	ACK(1),
	NEXT(1),
	POKE(5,20),
	OP_OBSERVE_GUI(5,20),
	OP_POST_FLIGHT_COMPLETE(5,20),
	RANDOM(5,100),
	PS_TX_MM(5,20),
	PS_RX_MM(5,20), UAV_TAKE_OFF(5,20);
	
	private Integer _minimum;
	private Integer _maximum;
	private Range _range;

	/**
	 * creates a bounded duration
	 * @param minimum represents the minimum bound of the duration in seconds
	 * @param maximum represents the maximum bound of the duration in seconds
	 */
	Duration(int minimum, int maximum) {
		_minimum = minimum;
		_maximum = maximum;
		_range = new Range(_minimum, _maximum);
	}
	
	/**
	 * creates a fixed duration
	 * @param duration represents the fixed time duration in seconds
	 */
	Duration(Integer duration){
		_minimum = duration;
		_maximum = duration;
		_range = new Range(_minimum, _maximum);
	}
	
	/**
	 * updates the _min and _max
	 * @param duration represents the integer value of the duration
	 */
	public Duration update(Integer duration){
		_minimum = duration;
		_maximum = duration;
		_range = new Range(_minimum, _maximum);
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

	public Range getRange() {
//		if(_minimum == _maximum)
//			return _maximum;
//		else{
//			Random rand = new Random();
//			int dur = _minimum + rand.nextInt(_maximum-_minimum);
//			return dur;
//		}
		return _range;
	}

}
