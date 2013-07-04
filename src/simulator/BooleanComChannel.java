package simulator;

public enum BooleanComChannel implements IComChannel {
	
	/* Parent Search Data */
	
	//Outputs
	PS_POKE_MM,
	PS_END_MM,
	PS_NEW_SEARCH_AOI_MM,
	PS_TARGET_DESCRIPTION_MM,
	//Internals
	PS_NEW_SEARCH_AOI_PS,
	PS_TARGET_DESCRIPTION_PS,
	//Counters
	PS_TIME_TIL_START_PS,
	
	/* Mission Manager Data */
	
	//Outputs
	MM_ACK_PS,
	MM_POKE_VO,
	MM_TARGET_DESCRIPTION_VO,
	MM_END_VO,
	MM_POKE_OP,
	MM_NEW_SEARCH_AOI_OP,
	MM_END_OP,
	//Internals
	MM_TARGET_DESCRIPTION_MM,
	MM_NEW_SEARCH_AOI_MM,
	//Counters

	/* Video Operator Data */

	//Outputs
	VO_ACK_MM,
	//Internals
	VO_TARGET_DESCRIPTION_VO,
	//Counters

	/* Video Operator Gui Data */
	
	/* UAV Operator Data */
	
	//Outputs
	OP_POKE_OGUI,
	OP_END_OGUI,
	OP_ACK_MM,
	OP_TAKE_OFF_OGUI,
	//Internals
	OP_NEW_SEARCH_AOI_OP,
	//Counters
	
	/* UAV Operator Gui Data */
	
	//Outputs
	OGUI_TAKE_OFF_UAV,
	//Internals
	//Counters
	
	/* UAV Data */
	
	//Outputs
	UAV_LANDED,
	UAV_BATTERY_OFF_OGUI,
	//Internals
	//Counters
	
	/* UAV Battery Data */
	
	UAVBAT_TIME_TIL_LOW_UAVBAT,
	UAVBAT_TIME_TIL_DEAD,
	UAV_BATTERY_OK_OGUI,
	UAV_BATTERY_LOW_OGUI,
	UAV_BATTERY_DEAD_OGUI,
	
	/* UAV Video Feed */
	
	VF_SIGNAL_OK_VGUI, 
	VF_SIGNAL_NONE_VGUI,
	
	/* Event Data */
	
	EVENT_TARGET_SIGHTED_F_VGUI, 
	EVENT_TARGET_SIGHTED_END_VGUI, 
	EVENT_TARGET_SIGHTED_T_VGUI,
	EVENT_FLYBY_ANOMALY_F,
	EVENT_FLYBY_ANOMALY_T, 
	EVENT_TERMINATE_SEARCH_PS, 
	EVENT_START_SEARCH_PS,
	
	/* Delta Clock Data */
	
	DC_TIME_ELAPSED,
	OP_POST_FLIGHT_COMPLETE_UAV;

	private Boolean _value;
	
	private BooleanComChannel(){
		_value = false;
	}
	
	@Override
	public void set(Object object) {
		assert(object.getClass() == Boolean.class);
		
		_value= (Boolean) object;
	}
	
	@Override
	public Object get() {
		return _value;
	}
}
