package simulator;

public enum UDO {
	
	/* Parent Search Outputs */
	
	PS_POKE_MM,
	PS_END_MM,
	PS_TERMINATE_SEARCH_MM,
	PS_TARGET_DESCRIPTION_MM,
	PS_NEW_SEARCH_AOI,
	PS_BUSY_MM,
	PS_ACK_MM,
	
	/* Mission Manager Outputs */
	
	MM_POKE_PS,
	MM_POKE_VO,
	MM_POKE_OP,
	MM_POKE_VGUI,
	MM_ACK_PS,
	MM_ACK_VO,
	MM_ACK_OP,
	MM_END_PS,
	MM_END_VO,
	MM_END_OP,
	MM_END_VGUI,
	MM_TARGET_DESCRIPTION,
	MM_TERMINATE_SEARCH_VO,
	MM_NEW_SEARCH_AOI,
	MM_TERMINATE_SEARCH_OP,
	MM_SEARCH_AOI_COMPLETE,
	MM_SEARCH_FAILED,
	MM_TARGET_SIGHTING_F,
	MM_TARGET_SIGHTING_T,
	MM_FLYBY_REQ_T,
	MM_FLYBY_REQ_F,
	MM_ANOMALY_DISMISSED_T,
	MM_ANOMALY_DISMISSED_F,
	MM_BUSY_PS,
	MM_BUSY_OP,
	MM_BUSY_VO,

	/* Video Operator Outputs */

	VO_POKE_MM,
	VO_END_MM,
	VO_TARGET_SIGHTING_T_MM,
	VO_TARGET_SIGHTING_F_MM,
	VO_POKE_OP,
	VO_END_OP,
	VO_BAD_STREAM_OP,
	VO_POKE_VGUI,
	VO_END_VGUI,
	VO_FLYBY_REQ_T_VGUI,
	VO_FLYBY_REQ_F_VGUI,
	VO_FLYBY_END_SUCCESS_VGUI,
	VO_FLYBY_END_FAILED_VGUI,
	VO_POSSIBLE_ANOMALY_DETECTED_T_VGUI,
	VO_POSSIBLE_ANOMALY_DETECTED_F_VGUI,
	VO_ACK_MM,
	VO_BUSY_MM,

	/* Video Operator Gui Outputs */
	
	VGUI_FALSE_POSITIVE,
	VGUI_TRUE_POSITIVE,
	VGUI_VALIDATION_REQ_T,
	VGUI_VALIDATION_REQ_F,
	VGUI_VALIDATION_REQ_T_MM,
	VGUI_VALIDATION_REQ_F_MM,
	VO_FLYBY_REQ_T_OGUI,
	VO_FLYBY_REQ_F_OGUI,
	VO_FLYBY_END_FAILED_OGUI,
	VO_FLYBY_END_SUCCESS_OGUI,
	MM_FLYBY_REQ_F_OGUI,
	MM_FLYBY_REQ_T_OGUI,
	VGUI_NORMAL,
	VGUI_FLYBY_T,
	VGUI_FLYBY_F,
	
	/* UAV Operator Outputs */
	
	 OP_POKE_MM,
	 OP_END_MM,
	 OP_SEARCH_AOI_COMPLETE_MM,
	 OP_BUSY_MM;
	
	/* UAV Operator Gui Outputs */
	

	private boolean _active;
	private boolean _primed;
	
	private UDO(){
		this._active = false;
		this._primed = false;
		
	}

	/**
	 * this method is used to tell if a UDO is active
	 * @return return the state of the UDO
	 */
	public boolean isActive() {
		
		return _active;
		
	}

	/**
	 * primes the UDO, actors will soon be able to respond to it
	 */
	public void prime() {
		
		_primed = true;
		
	}
	
	/**
	 * updates the data status
	 * works to activate or deactivate the UDO based on the temp status
	 */
	public void processData() {
		
		_active = _primed;//activates or deactivates the UDO 
		_primed = false;
		
	}
}
