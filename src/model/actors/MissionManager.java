package model.actors;

import java.util.ArrayList;
import java.util.HashMap;

import model.team.Duration;
import simulator.Actor;
import simulator.ComChannelList;
import simulator.IActor;
import simulator.ITransition;
import simulator.State;
import simulator.Transition;

/**
 * The mission manager acts as a liaison between the operators and the parent search.
 * He also watches verifies target sightings to approve sightings.
 */
public class MissionManager extends Actor {
	
	/**
	 * This is an enumeration of the communications from the mission manager to the parent search.
	 */
	public enum MM_PS_COMM {
		MM_POKE_PS,
		MM_TX_PS,
		MM_END_PS,
		MM_ACK_PS,
		MM_TARGET_SIGHTED_T,
		MM_TARGET_SIGHTED_F,
		MM_SEARCH_COMPLETE,
		MM_SEARCH_FAILED
	}

	/**
	 * This is an enumeration of the communications from the mission manager to the uav operator.
	 */
	public enum MM_OP_COMM {
		MM_POKE_OP,
		MM_ACK_OP,
		MM_END_OP,
		MM_NEW_SEARCH_AOI,
		MM_TERMINATE_SEARCH;
	}

	/**
	 * This is an enumeration of the communications from the mission manager to the video operator.
	 */
	public enum MM_VO_COMM {
		MM_POKE_VO,
		MM_ACK_VO,
		MM_END_VO,
		MM_TARGET_DESCRIPTION,
		MM_TERMINATE_SEARCH;
	}

	/**
	 * This is an enumeration of the communications from the mission manager to the video gui.
	 */
	public enum MM_VGUI_COMM {
		MM_POKE_VGUI,
		MM_END_VGUI,
		MM_ANOMALY_DISMISSED_T,
		MM_ANOMALY_DISMISSED_F,
		MM_FLYBY_REQ_T,
		MM_FLYBY_REQ_F;
	}

	/**
	 * This constructor initializes all of the states and transitions that belong to the mission manager.
	 * @param inputs the list of assigned inputs.
	 * @param outputs the list of assigned outputs.
	 */
	public MissionManager(ComChannelList inputs, ComChannelList outputs) {
		//initialize states
		State IDLE = new State("IDLE");
		//comm with PS
		State POKE_PS = new State("POKE_PS");
		State TX_PS = new State("TX_PS");
		State END_PS = new State("END_PS");
		State RX_PS = new State("RX_PS");
		//comm with OP
		State POKE_OP = new State("POKE_OP");
		State TX_OP = new State("TX_OP");
		State END_OP = new State("END_OP");
		State RX_OP = new State("RX_OP");
		//comm with VO
		State POKE_VO = new State("POKE_VO");
		State TX_VO = new State("TX_VO");
		State END_VO = new State("END_VO");
		State RX_VO = new State("RX_VO");
		//comm with VGUI
		State OBSERVING_VGUI = new State("OBSERVING_VGUI");
		State POKE_VGUI = new State("POKE_VGUI");
		State TX_VGUI = new State("TX_VGUI");
		State END_VGUI = new State("END_VGUI");

		this.initializeInternalVariables();
		//initialize transitions
		initializeIDLE(inputs, outputs, IDLE, RX_PS, POKE_VO, POKE_OP, OBSERVING_VGUI, POKE_VGUI, RX_OP, RX_VO);
		//comm with PS
		initializePOKE_PS(inputs, outputs, POKE_PS,TX_PS,IDLE);
		initializeTX_PS(inputs, outputs, TX_PS, END_PS);
		initializeEND_PS(inputs, outputs, END_PS, IDLE);
		initializeRX_PS(inputs, outputs, IDLE, RX_PS, POKE_OP);
		//comm with OP
		initializePOKE_OP(inputs, outputs, IDLE, POKE_OP, TX_OP);
		initializeTX_OP(inputs, outputs, TX_OP, END_OP);
		initializeEND_OP(inputs, outputs, END_OP, IDLE, POKE_VO);
		initializeRX_OP(inputs, outputs, IDLE, RX_OP);
		//comm with VO
		initializePOKE_VO(inputs, outputs, IDLE, POKE_VO, TX_VO);
		initializeTX_VO(inputs, outputs, TX_VO, END_VO);
		initializeEND_VO(inputs, outputs, END_VO, IDLE);
		initializeRX_VO(inputs, outputs, IDLE, RX_VO);
		//comm with VGUI
		initializeOBSERVING_VGUI(inputs, outputs, OBSERVING_VGUI, IDLE);
		initializePOKE_VGUI(inputs, outputs, POKE_VGUI, TX_VGUI);
		initializeTX_VGUI(inputs, outputs, TX_VGUI, END_VGUI);
		initializeEND_VGUI(inputs, outputs, IDLE, END_VGUI);
	}

	/**
	 * This method assists the constructor initialize the idle state.<br><br>
	 * (IDLE, [OP_POKE_MM], []) -> (RX_OP, [MM_ACK_OP], [])<br>
	 * (IDLE, [VO_POKE_MM], []) -> (RX_VO, [MM_ACK_VO], [])<br>
	 * (IDLE, [VGUI_ALERT], []) -> (OBSERVING_VGUI, [], [])<br>
	 * (IDLE, [PS_POKE_MM], []) -> (RX_PS, [MM_ACK_PS], [])<br>
	 * (IDLE, [], [TARGET_DESCRIPTION]) -> (POKE_VO, [MM_POKE_VO], [])<br>
	 * (IDLE, [], [AREA_OF_INTEREST]) -> (POKE_OP, [POKE_OP], [])<br>
	 * (IDLE, [], [Terminate_Search]) -> (POKE_OP, [POKE_OP], [])<br>
	 * (IDLE, [], [ANOMALY_DISMISSED_T]) -> (POKE_VGUI, [], [ANOMALY_DISMISSED_T])<br>
	 * (IDLE, [], [ANOMALY_DISMISSED_F]) -> (POKE_VGUI, [], [ANOMALY_DISMISSED_F])<br>
	 * (IDLE, [], [FLYBY_REQ_T]) -> (POKE_VGUI, [], [FLYBY_REQ_T])<br>
	 * (IDLE, [], [FLYBY_REQ_F]) -> (POKE_VGUI, [], [FLYBY_REQ_F])<br>
	 */
	private void initializeIDLE(ComChannelList inputs, ComChannelList outputs, State IDLE, State RX_PS, State POKE_VO, State POKE_OP, State OBSERVING_VGUI, State POKE_VGUI, State RX_OP, State RX_VO) {
		//(IDLE,[OP_POKE_MM],[])x(RX_OP,[MM_ACK_OP],[])
		IDLE.add(new Transition(_internal_vars, inputs, outputs, RX_OP){
			@Override
			public boolean isEnabled(){
				if(_inputs.get("OP_MM_COMM").equals(Operator.OP_MM_COMM.OP_POKE_MM)){
					this.setTempOutput("MM_OP_COMM", MissionManager.MM_OP_COMM.MM_ACK_OP);
				}
				return false;
			}
		});
		
		//(IDLE,[VO_POKE_MM],[])x(RX_VO,[MM_ACK_VO],[])
		IDLE.add(new Transition(_internal_vars, inputs, outputs, RX_VO){
			@Override
			public boolean isEnabled(){
				if(_inputs.get("VO_MM_COMM").equals(VideoOperator.VO_MM_COMM.VO_POKE_MM)){
					this.setTempOutput("MM_VO_COMM", MissionManager.MM_VO_COMM.MM_ACK_VO);
				}
				return false;
			}
		});
		
		//(IDLE, [VGUI_ALERT],[])x(OBSERVING_VGUI,[],[])
		IDLE.add(new Transition(_internal_vars, inputs, outputs, OBSERVING_VGUI){
			@Override
			public boolean isEnabled(){
				if(_inputs.get("VGUI_MM_COMM").equals(VideoOperatorGui.VGUI_MM_COMM.VGUI_ALERT_MM)){
					return true;
				}
				return false;
			}
		});
		
		//(IDLE, [PS_POKE_MM], [])->(RX_PS, [MM_ACK_PS], [])
		IDLE.add(new Transition(this._internal_vars, inputs, outputs, RX_PS ) {
			@Override
			public boolean isEnabled() 
			{
				boolean result = false;
				if ( _inputs.get("PS_MM_COMM").equals(ParentSearch.AUDIO_PS_MM_COMM.PS_POKE_MM) ) {
					this.setTempOutput("MM_PS_COMM", MM_PS_COMM.MM_ACK_PS);
					result = true;
				}
				return result;
			}
		});
		
		//(IDLE, [], [TARGET_DESCRIPTION])->(POKE_VO, [MM_POKE_VO], [])
		IDLE.add(new Transition(this._internal_vars, inputs, outputs, POKE_VO) {
			@Override
			public boolean isEnabled() 
			{
				boolean result = false;
				if ( (Integer)this._internal_vars.getVariable("NEW_TARGET_DESCRIPTION") > 0 ) {
					this.setTempOutput("MM_VO_COMM", MM_VO_COMM.MM_POKE_VO);
					result = true;
				}
				return result;		
			}
		});

		//(IDLE, [], [AREA_OF_INTEREST])->(POKE_OP, [POKE_OP], [])
		IDLE.add(new Transition(this._internal_vars, inputs, outputs, POKE_OP) {
			@Override
			public boolean isEnabled() 
			{
				boolean result = false;
				if ( (Integer) this._internal_vars.getVariable("NEW_SEARCH_AOI") > 0) {
					this.setTempOutput("MM_OP_COMM", MM_OP_COMM.MM_POKE_OP);
					result = true;
				}
				return result;		
			}
		});
		
		//(IDLE, [], [Terminate_Search])->(POKE_OP, [POKE_OP], [])
		IDLE.add(new Transition(this._internal_vars, inputs, outputs, POKE_OP) {
			@Override
			public boolean isEnabled() 
			{
				boolean result = false;
				if ( (Integer) this._internal_vars.getVariable("NEW_TERMINATE_SEARCH") > 0) {
					this.setTempOutput("MM_OP_COMM", MM_OP_COMM.MM_POKE_OP);
					result = true;
				}
				return result;		
			}
		});
		
		//(IDLE,[],[ANOMALY_DISMISSED_T])x(POKE_VGUI,[],[ANOMALY_DISMISSED_T])
		//(IDLE,[],[ANOMALY_DISMISSED_F])x(POKE_VGUI,[],[ANOMALY_DISMISSED_F])
		//(IDLE,[],[FLYBY_REQ_T])x(POKE_VGUI,[],[FLYBY_REQ_T])
		//(IDLE,[],[FLYBY_REQ_F])x(POKE_VGUI,[],[FLYBY_REQ_F])
		IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_VGUI){
			@Override
			public boolean isEnabled(){
				if((Boolean)_internal_vars.getVariable("ANOMALY_DISMISSED_T"))
					return true;
				if((Boolean)_internal_vars.getVariable("ANOMALY_DISMISSED_F"))
					return true;
				if((Boolean)_internal_vars.getVariable("FLYBY_REQ_T"))
					return true;
				if((Boolean)_internal_vars.getVariable("FLYBY_REQ_F"))
					return true;
				return false;
			}
		});
		
		add(IDLE);
	}

	/**
	 * This method assists the constructor initialize the POKE_PS state.<br><br>
	 * (POKE_PS,[PS_BUSY_MM],[])x(IDLE,[],[])<br>
	 * (POKE_PS,[PS_ACK_MM],[])x(TX_PS,[],[])<br>
	 */
	private void initializePOKE_PS(ComChannelList inputs, ComChannelList outputs, State POKE_PS, State TX_PS, State IDLE) {
		//(POKE_PS,[PS_BUSY_MM],[])x(IDLE,[],[])
		POKE_PS.add(new Transition(_internal_vars, inputs, outputs, IDLE){
			@Override
			public boolean isEnabled(){
				if(_inputs.get("PS_MM_COMM").equals(ParentSearch.AUDIO_PS_MM_COMM.PS_BUSY_MM)){
					return true;
				}
				return false;
			}
		});
		
		//(POKE_PS,[PS_ACK_MM],[])x(TX_PS,[],[])
		POKE_PS.add(new Transition(_internal_vars, inputs, outputs, IDLE){
			@Override
			public boolean isEnabled(){
				if(_inputs.get("PS_MM_COMM").equals(ParentSearch.AUDIO_PS_MM_COMM.PS_ACK_MM)){
					return true;
				}
				return false;
			}
		});

		add(POKE_PS);
	}

	/**
	 * This method assists the constructor initialize the TX_PS state.<br><br>
	 * (TX_PS,[],[SearchComplete])x(END_PS,[MM_SEARCH_COMPLETE],[])<br>
	 * (TX_PS,[],[SearchFailed])x(END_PS,[MM_SEARCH_FAILED],[])<br>
	 * (TX_PS,[],[TargetFound_T])x(END_PS,[MM_TARGET_SIGHTED_T],[])<br>
	 * (TX_PS,[],[TargetFound_F])x(END_PS,[MM_TARGET_SIGHTED_F],[])<br>
	 */
	private void initializeTX_PS(ComChannelList inputs, ComChannelList outputs, State TX_PS, State END_PS) {
		//(TX_PS,[],[SearchComplete])x(END_PS,[MM_SEARCH_COMPLETE],[])
		//(TX_PS,[],[SearchFailed])x(END_PS,[MM_SEARCH_FAILED],[])
		//(TX_PS,[],[TargetFound_T])x(END_PS,[MM_TARGET_SIGHTED_T],[])
		//(TX_PS,[],[TargetFound_F])x(END_PS,[MM_TARGET_SIGHTED_F],[])
		TX_PS.add(new Transition(_internal_vars, inputs, outputs, END_PS){
			@Override
			public boolean isEnabled(){
				if((Boolean)_internal_vars.getVariable("SearchComplete")){
					this.setTempOutput("MM_PS_COMM", MissionManager.MM_PS_COMM.MM_SEARCH_COMPLETE);
					this.setTempInternalVar("SearchComplete", false);
				}
				else if((Boolean)_internal_vars.getVariable("SearchFailed")){
					this.setTempOutput("MM_PS_COMM", MissionManager.MM_PS_COMM.MM_SEARCH_FAILED);
					this.setTempInternalVar("SearchFailed", false);
				}
				else if((Boolean)_internal_vars.getVariable("TargetFound_T")){
					this.setTempOutput("MM_PS_COMM", MissionManager.MM_PS_COMM.MM_TARGET_SIGHTED_T);
					this.setTempInternalVar("TargetFound_T", false);
				}
				else if((Boolean)_internal_vars.getVariable("TargetFound_F")){
					this.setTempOutput("MM_PS_COMM", MissionManager.MM_PS_COMM.MM_TARGET_SIGHTED_F);
					this.setTempInternalVar("TargetFound_F", false);
				}
				return true;
			}
		});
		
		add(TX_PS);
	}
	
	/**
	 * This method assists the constructor initialize the END_PS state.<br><br>
	 * (END_PS,[],[])x(IDLE,[],[])<br>
	 */
	private void initializeEND_PS(ComChannelList inputs, ComChannelList outputs, State END_PS, State IDLE) {
		//(END_PS,[],[])x(IDLE,[],[])
		END_PS.add(new Transition(_internal_vars,inputs,outputs,IDLE){
			
		});
		
		add(END_PS);
	}

	/**
	 * This method assists the constructor initialize the RX_PS state.<br><br>
	 * (RX_PS, [PS_END_MM], [])->(IDLE, [], [])<br>
	 * (RX_PS, [PS_AREA_OF_INTEREST_MM], [])->(IDLE, [], [TARGET_DESCRIPTION])<br>
	 * (RX_PS, [PS_TARGET_DESCRIPTION_MM], [])->(IDLE, [], [PS_TARGET_DESCRIPTION_MM])<br>
	 * (RX_PS, [PS_TERMINATE_SEARCH], [])->(IDLE, [], [NEW_TERMINATE_SEARCH])<br>
	 */
	private void initializeRX_PS(ComChannelList inputs, ComChannelList outputs, State IDLE, State RX_PS, State POKE_OP) {
		//(RX_PS, [PS_END_MM], [])->(IDLE, [], [])
		//(RX_PS, [PS_AREA_OF_INTEREST_MM], [])->(IDLE, [], [TARGET_DESCRIPTION])
		//(RX_PS, [PS_TARGET_DESCRIPTION_MM], [])->(IDLE, [], [PS_TARGET_DESCRIPTION_MM])
		//(RX_PS, [PS_TERMINATE_SEARCH], [])->(IDLE, [], [NEW_TERMINATE_SEARCH])
		RX_PS.add(new Transition(this._internal_vars, inputs, outputs, IDLE ) {
			@Override
			public boolean isEnabled() 
			{
				boolean result = false;
				
				if(_inputs.get("PS_MM_COMM").equals(ParentSearch.AUDIO_PS_MM_COMM.PS_END_MM)){
					result = true;
				}
				if ( this._inputs.get("PS_MM_COMM").equals(ParentSearch.AUDIO_PS_MM_COMM.PS_NEW_SEARCH_AOI) ) {
					int num = 1;
					if(_internal_vars.getVariable("NEW_SEARCH_AOI") != null)
						num = (Integer)_internal_vars.getVariable("NEW_SEARCH_AOI")+1;
					this.setTempInternalVar("NEW_SEARCH_AOI", num);
					result = true;
				}
				if ( _inputs.get("PS_MM_COMM").equals(ParentSearch.AUDIO_PS_MM_COMM.PS_TARGET_DESCRIPTION) ) {
					int num = 1;
					if(_internal_vars.getVariable("NEW_TARGET_DESCRIPTION") != null)
						num = (Integer)_internal_vars.getVariable("NEW_TARGET_DESCRIPTION")+1;
					this.setTempInternalVar("NEW_TARGET_DESCRIPTION", num);
					result = true;
				}
				if ( this._inputs.get("PS_MM_COMM").equals(ParentSearch.AUDIO_PS_MM_COMM.PS_TERMINATE_SEARCH) ) {
					int num = 1;
					if(_internal_vars.getVariable("NEW_TERMINATE_SEARCH") != null)
						num = (Integer)_internal_vars.getVariable("NEW_TERMINATE_SEARCH")+1;
					this.setTempInternalVar("NEW_TERMINATE_SEARCH", num);
					result = true;
				}
				return result;
			}
		});
		

		add(RX_PS);
	}

	/**
	 * This method assists the constructor initialize the POKE_OP state.<br><br>
	 * (POKE_OP, [OP_ACK_MM], [])->(TX_OP, [], [])<br>
	 * (POKE_OP,[],[])x(IDLE,[],[])<br>
	 */
	private void initializePOKE_OP(ComChannelList inputs, ComChannelList outputs,State IDLE, State POKE_OP, State TX_OP) {
		//(POKE_OP, [OP_ACK_MM], [])->(TX_OP, [], [])
		POKE_OP.add(new Transition(this._internal_vars, inputs, outputs, TX_OP) {
			@Override
			public boolean isEnabled() 
			{
				boolean result = false;
				if ( this._internal_vars.getVariable("OP_MM_COMM").equals(Operator.OP_MM_COMM.OP_ACK_MM) ) {
					result = true;
				}
				return result;		
			}
		});
		
		//(POKE_OP,[],[])x(IDLE,[],[])
		POKE_OP.add(new Transition(_internal_vars, inputs, outputs,IDLE, Duration.POKE){
			
		});

		add(POKE_OP);
	}

	/**
	 * This method assists the constructor initialize the POKE_OP state.<br><br>
	 * (TX_OP, [], [AREA_OF_INTEREST])->(END_OP, [MM_AREA_OF_INTEREST_OP, MM_END_OP], [])<br>
	 */
	private void initializeTX_OP(ComChannelList inputs, ComChannelList outputs, State TX_OP, State END_OP) {
		//(TX_OP, [], [AREA_OF_INTEREST])->(END_OP, [MM_AREA_OF_INTEREST_OP, MM_END_OP], [])
		TX_OP.add(new Transition(_internal_vars, inputs, outputs, END_OP, Duration.MM_TX_OP){
			@Override
			public boolean isEnabled(){
					int num = 1;
					if(_internal_vars.getVariable("NEW_TERMINATE_SEARCH") != null){
						num = (Integer)_internal_vars.getVariable("NEW_TERMINATE_SEARCH")-1;
						this.setTempInternalVar("NEW_TERMINATE_SEARCH", num);
						this.setTempOutput("MM_OP_COMM", MissionManager.MM_OP_COMM.MM_TERMINATE_SEARCH);
					} else if(_internal_vars.getVariable("NEW_SEARCH_AOI") != null){
						num = (Integer)_internal_vars.getVariable("NEW_SEARCH_AOI")-1;
						this.setTempInternalVar("NEW_SEARCH_AOI", num);
						this.setTempOutput("MM_OP_COMM", MissionManager.MM_OP_COMM.MM_NEW_SEARCH_AOI);
					}
				return true;
			}
		});
		
		add(TX_OP);
	}

	/**
	 * This method assists the constructor initialize the POKE_OP state.<br><br>
	 * (END_OP, [], [Terminate_Search])->(POKE_VO, [MM_POKE_VO], [Terminate_Search])<br>
	 * (END_OP, [], [AREA_OF_INTEREST])->(IDLE, [], [AREA_OF_INTEREST-1])<br>
	 * (END_OP, [], [])->(IDLE,[],[])<br>
	 */
	private void initializeEND_OP(ComChannelList inputs, ComChannelList outputs, State END_OP, State IDLE, State POKE_VO) {
		//(END_OP, [], [Terminate_Search])->(POKE_VO, [MM_POKE_VO], [Terminate_Search])
		END_OP.add(new Transition(this._internal_vars, inputs, outputs, POKE_VO) {
			@Override
			public boolean isEnabled() 
			{
				if((Integer)_internal_vars.getVariable("NEW_TERMINATE_SEARCH") > 0 ){
					return true;
				}
				return false;
			}
		});
		
		//(END_OP, [], [AREA_OF_INTEREST])->(IDLE, [], [AREA_OF_INTEREST-1])
		//(END_OP, [], [])->(IDLE,[],[])
		END_OP.add(new Transition(this._internal_vars, inputs, outputs, IDLE) {
			@Override
			public boolean isEnabled() 
			{
				if((Integer)_internal_vars.getVariable("NEW_SEARCH_AOI") > 0){
					this.setTempInternalVar("NEW_SEARCH_AOI", (Integer) _internal_vars.getVariable("NEW_SEARCH_AOI")-1);
				}
				return true;
			}
		});
		
		add(END_OP);
	}

	/**
	 * This method assists the constructor initialize the POKE_OP state.<br><br>
	 * (RX_OP,[OP_END_MM],[*])x(IDLE,[],[*])<br>
	 * (RX_OP,[OP_SEARCH_COMPLETE],[*])x(IDLE,[],[SEARCH_COMPLETE,*])<br>
	 * (RX_OP,[OP_SEARCH_FAILED],[*])x(IDLE,[],[SEARCH_FAILED,*])<br>
	 */
	private void initializeRX_OP(ComChannelList inputs, ComChannelList outputs, State IDLE, State RX_OP) {
		//(RX_OP,[OP_END_MM],[*])x(IDLE,[],[*])
		//(RX_OP,[OP_SEARCH_COMPLETE],[*])x(IDLE,[],[SEARCH_COMPLETE,*])
		//(RX_OP,[OP_SEARCH_FAILED],[*])x(IDLE,[],[SEARCH_FAILED,*])
		RX_OP.add(new Transition(_internal_vars,inputs,outputs,IDLE){
			@Override
			public boolean isEnabled() 
			{
				if(_inputs.get("OP_MM_COMM").get() != null){
					if(_inputs.get("OP_MM_COMM").get().equals(Operator.OP_MM_COMM.OP_END_MM)){
						
					} else if(_inputs.get("OP_MM_COMM").get().equals(Operator.OP_MM_COMM.OP_SEARCH_COMPLETE)){
						this.setTempInternalVar("SEARCH_COMPLETE", true);
					} else if(_inputs.get("OP_MM_COMM").get().equals(Operator.OP_MM_COMM.OP_SEARCH_FAILED)){
						this.setTempInternalVar("SEARCH_FAILED", true);
					}
					return true;
				}
				return false;
			}
		});
		
		add(RX_OP);
	}

	/**
	 * This method assists the constructor initialize the POKE_OP state.<br><br>
	 * (POKE_VO, [VO_ACK_MM], [*])->(TX_VO, [], [*])<br>
	 * (POKE_VO, [VO_ACK_MM], [TERMINATE_SEARCH])->(TX_VO, [], [TERMINATE_SEARCH])<br>
	 * (POKE_VO, [VO_ACK_MM], [TARGET_DESCRIPTION])->(TX_VO, [], [TARGET_DESCRIPTION])<br>
	 */
	private void initializePOKE_VO(ComChannelList inputs, ComChannelList outputs, State IDLE, State POKE_VO, State TX_VO) {
		//(POKE_VO, [VO_ACK_MM], [*])->(TX_VO, [], [*])
		//(POKE_VO, [VO_ACK_MM], [TERMINATE_SEARCH])->(TX_VO, [], [TERMINATE_SEARCH])
		//(POKE_VO, [VO_ACK_MM], [TARGET_DESCRIPTION])->(TX_VO, [], [TARGET_DESCRIPTION])
		POKE_VO.add(new Transition(this._internal_vars, inputs, outputs, TX_VO) {
			@Override
			public boolean isEnabled() 
			{
				boolean result = false;
				if (_inputs.get("VO_MM_COMM").equals(VideoOperator.VO_MM_COMM.VO_ACK_MM) ) {
					result = true;
				}
				return result;		
			}
		});

		add(POKE_VO);
	}

	/**
	 * This method assists the constructor initialize the POKE_OP state.<br><br>
	 * (TX_VO, [], [TARGET_DESCRIPTION])->(END_VO, [MM_TARGET_DESCRIPTION_VO, MM_END_VO], [TARGET_DESCRIPTION-1])<br>
	 * (TX_VO, [], [TERMINATE_SEARCH])->(END_VO, [MM_TARGET_DESCRIPTION_VO, MM_END_VO], [TERMINATE_SEARCH-1)<br>
	 */
	private void initializeTX_VO(ComChannelList inputs, ComChannelList outputs, State TX_VO, State END_VO) {
		//(TX_VO, [], [TARGET_DESCRIPTION])->(END_VO, [MM_TARGET_DESCRIPTION_VO, MM_END_VO], [TARGET_DESCRIPTION-1])
		//(TX_VO, [], [TERMINATE_SEARCH])->(END_VO, [MM_TARGET_DESCRIPTION_VO, MM_END_VO], [TERMINATE_SEARCH-1)
		TX_VO.add(new Transition(this._internal_vars, inputs, outputs, END_VO) {
			@Override
			public boolean isEnabled() 
			{
				if ( (Integer)this._internal_vars.getVariable("NEW_TARGET_DESCRIPTION") > 0 ) {
					this.setTempOutput("MM_VO_COMM", MissionManager.MM_VO_COMM.MM_TARGET_DESCRIPTION);
					this.setTempInternalVar("NEW_TARGET_DESCRIPTION", (Integer)_internal_vars.getVariable("NEW_TARGET_DESCRIPTION")-1);
				}
				if((Integer)_internal_vars.getVariable("NEW_TERMINATE_SEARCH") > 0){
					this.setTempOutput("MM_VO_COMM", MissionManager.MM_VO_COMM.MM_TERMINATE_SEARCH);
					this.setTempInternalVar("NEW_TERMINATE_SEARCH", (Integer)_internal_vars.getVariable("NEW_TERMINATE_SEARCH")-1);
				}
				return true;		
			}
		});
		
		add(TX_VO);
	}

	/**
	 * This method assists the constructor initialize the POKE_OP state.<br><br>
	 * (END_VO, [], [])->(IDLE, [MM_END_OP], [])<br>
	 */
	private void initializeEND_VO(ComChannelList inputs, ComChannelList outputs, State END_VO, State IDLE) {
		//(END_VO, [], [])->(IDLE, [MM_END_OP], [])
		END_VO.add(new Transition(this._internal_vars, inputs, outputs, IDLE) {
			@Override
			public boolean isEnabled() 
			{
				boolean result = false;
				this.setTempOutput("MM_OP", "END");
				this.setTempInternalVar("TARGET_DESCRIPTION", "CURRENT");
				result = true;
				return result;		
			}
		});
		
		add(END_VO);
	}

	/**
	 * This method assists the constructor initialize the POKE_OP state.<br><br>
	 * (RX_VO,[VO_END_MM, VO_TARGET_SIGHTING_F],[])x(IDLE,[],[TARGET_SIGHTING_F])<br>
	 * (RX_VO,[VO_END_MM, VO_TARGET_SIGHTING_T],[])x(IDLE,[],[TARGET_SIGHTING_T])<br>
	 */
	private void initializeRX_VO(ComChannelList inputs, ComChannelList outputs, State IDLE, State RX_VO) {
		//(RX_VO,[VO_END_MM, VO_TARGET_SIGHTING_F],[])x(IDLE,[],[TARGET_SIGHTING_F])
		//(RX_VO,[VO_END_MM, VO_TARGET_SIGHTING_T],[])x(IDLE,[],[TARGET_SIGHTING_T])
		RX_VO.add(new Transition(_internal_vars,inputs,outputs,IDLE){
			@Override
			public boolean isEnabled(){
				if(_inputs.get("VO_MM_COMM").get() != null){
					if(_inputs.get("VO_MM_COMM").equals(VideoOperator.VO_MM_COMM.VO_TARGET_SIGHTED_F)){
						this.setTempInternalVar("TARGET_SIGHTED_T", true);
					}
					if(_inputs.get("VO_MM_COMM").equals(VideoOperator.VO_MM_COMM.VO_TARGET_SIGHTED_T)){
						this.setTempInternalVar("TARGET_SIGHTED_F", true);
					}
					return true;
				}
				return false;
			}
		});
		
		add(RX_VO);
	}

	/**
	 * This method assists the constructor initialize the POKE_OP state.<br><br>
	 * (OBSERVING_VGUI,[],[])x(IDLE,[],[])<br>
	 * (OBSERVING_VGUI,[VGUI_VALIDATION_REQ_T],[])x(IDLE,[],[FLYBY_REQ_T])<br>
	 * (OBSERVING_VGUI,[VGUI_VALIDATION_REQ_T],[])x(IDLE,[],[ANOMALY_DISMISSED_T])<br>
	 * (OBSERVING_VGUI,[VGUI_VALIDATION_REQ_T],[])x(IDLE,[],[ANOMALY_DISMISSED_T])<br>
	 * (OBSERVING_VGUI,[VGUI_VALIDATION_REQ_F],[])x(IDLE,[],[FLYBY_REQ_F])<br>
	 * (OBSERVING_VGUI,[VGUI_VALIDATION_REQ_F],[])x(IDLE,[],[ANOMALY_DISMISSED_F])<br>
	 */
	private void initializeOBSERVING_VGUI(ComChannelList inputs, ComChannelList outputs, State OBSERVING_VGUI, State IDLE) {
		//(OBSERVING_VGUI,[],[])x(IDLE,[],[])
		OBSERVING_VGUI.add(new Transition(_internal_vars, inputs, outputs, IDLE,Duration.MM_OBSERVING_VGUI){
			
		});
		
		//(OBSERVING_VGUI,[VGUI_VALIDATION_REQ_T],[])x(IDLE,[],[FLYBY_REQ_T])
		OBSERVING_VGUI.add(new Transition(_internal_vars,inputs,outputs,IDLE){
			@Override
			public boolean isEnabled(){
				if(_inputs.get("VGUI_MM_COMM").get().equals(VideoOperatorGui.VGUI_MM_COMM.VGUI_VALIDATION_REQ_T)){
					this.setTempInternalVar("FLYBY_REQ_T", true);
					return true;
				}
				return false;
			}
		});
		
		//(OBSERVING_VGUI,[VGUI_VALIDATION_REQ_T],[])x(IDLE,[],[ANOMALY_DISMISSED_T])
		OBSERVING_VGUI.add(new Transition(_internal_vars,inputs,outputs,IDLE){
			@Override
			public boolean isEnabled(){
				if(_inputs.get("VGUI_MM_COMM").get().equals(VideoOperatorGui.VGUI_MM_COMM.VGUI_VALIDATION_REQ_T)){
					this.setTempInternalVar("ANOMALY_DISMISSED_T", true);
					return true;
				}
				return false;
			}
		});
		
		//(OBSERVING_VGUI,[VGUI_VALIDATION_REQ_F],[])x(IDLE,[],[FLYBY_REQ_F])
		OBSERVING_VGUI.add(new Transition(_internal_vars,inputs,outputs,IDLE){
			@Override
			public boolean isEnabled(){
				if(_inputs.get("VGUI_MM_COMM").get().equals(VideoOperatorGui.VGUI_MM_COMM.VGUI_VALIDATION_REQ_T)){
					this.setTempInternalVar("FLYBY_REQ_F", true);
					return true;
				}
				return false;
			}
		});
		
		//(OBSERVING_VGUI,[VGUI_VALIDATION_REQ_F],[])x(IDLE,[],[ANOMALY_DISMISSED_F])
		OBSERVING_VGUI.add(new Transition(_internal_vars,inputs,outputs,IDLE){
			@Override
			public boolean isEnabled(){
				if(_inputs.get("VGUI_MM_COMM").get().equals(VideoOperatorGui.VGUI_MM_COMM.VGUI_VALIDATION_REQ_T)){
					this.setTempInternalVar("ANOMALY_DISMISSED_F", true);
					return true;
				}
				return false;
			}
		});
		
		add(OBSERVING_VGUI);
	}
	
	/**
	 * This method assists the constructor initialize the POKE_OP state.<br><br>
	 * (POKE_VGUI,[],[])x(TX_VGUI,[],[])<br>
	 */
	private void initializePOKE_VGUI(ComChannelList inputs, ComChannelList outputs, State POKE_VGUI, State TX_VGUI) {
		//(POKE_VGUI,[],[])x(TX_VGUI,[],[])
		POKE_VGUI.add(new Transition(_internal_vars,inputs,outputs,TX_VGUI){
			
		});
		
		add(POKE_VGUI);
	}
	
	/**
	 * This method assists the constructor initialize the POKE_OP state.<br><br>
	 * (TX_VGUI,[],[MM_FLYBY_REQ_T])x(TX_VGUI,[MM_FLYBY_REQ_T],[])<br>
	 * (TX_VGUI,[],[MM_FLYBY_REQ_F])x(TX_VGUI,[MM_FLYBY_REQ_F],[])<br>
	 * (TX_VGUI,[],[MM_ANOMALY_DISMISSED_T])x(TX_VGUI,[MM_ANOMALY_DISMISSED_T],[])<br>
	 * (TX_VGUI,[],[MM_ANOMALY_DISMISSED_F])x(TX_VGUI,[MM_ANOMALY_DISMISSED_F],[])<br>
	 */
	private void initializeTX_VGUI(ComChannelList inputs, ComChannelList outputs, State TX_VGUI, State END_VGUI) {
		//(TX_VGUI,[],[MM_FLYBY_REQ_T])x(TX_VGUI,[MM_FLYBY_REQ_T],[])
		//(TX_VGUI,[],[MM_FLYBY_REQ_F])x(TX_VGUI,[MM_FLYBY_REQ_F],[])
		//(TX_VGUI,[],[MM_ANOMALY_DISMISSED_T])x(TX_VGUI,[MM_ANOMALY_DISMISSED_T],[])
		//(TX_VGUI,[],[MM_ANOMALY_DISMISSED_F])x(TX_VGUI,[MM_ANOMALY_DISMISSED_F],[])
		TX_VGUI.add(new Transition(_internal_vars, inputs, outputs, END_VGUI, Duration.MM_TX_VGUI){
			@Override
			public boolean isEnabled(){
				if((Boolean)_internal_vars.getVariable("ANOMALY_DISMISSED_T"))
					this.setTempOutput("MM_VGUI_COMM", MissionManager.MM_VGUI_COMM.MM_ANOMALY_DISMISSED_T);
				else if((Boolean)_internal_vars.getVariable("ANOMALY_DISMISSED_F"))
					this.setTempOutput("MM_VGUI_COMM", MissionManager.MM_VGUI_COMM.MM_ANOMALY_DISMISSED_F);
				else if((Boolean)_internal_vars.getVariable("FLYBY_REQ_T"))
					this.setTempOutput("MM_VGUI_COMM", MissionManager.MM_VGUI_COMM.MM_FLYBY_REQ_T);
				else if((Boolean)_internal_vars.getVariable("FLYBY_REQ_F"))
					this.setTempOutput("MM_VGUI_COMM", MissionManager.MM_VGUI_COMM.MM_FLYBY_REQ_F);
				return true;
			}
		});
		
		add(TX_VGUI);
	}

	/**
	 * This method assists the constructor initialize the POKE_OP state.<br><br>
	 * (END_VGUI,[],[])x(IDLE,[],[])<br>
	 */
	private void initializeEND_VGUI(ComChannelList inputs, ComChannelList outputs, State IDLE, State END_VGUI) {
		//(END_VGUI,[],[])x(IDLE,[],[])
		END_VGUI.add(new Transition(_internal_vars, inputs, outputs, IDLE){
			
		});
	}
	
	@Override
	public HashMap<IActor, ITransition> getTransitions() {
		State state = this.getCurrentState();
		ArrayList<ITransition> enabledTransitions = state.getEnabledTransitions();
		if(enabledTransitions.size() == 0)
			return null;
		ITransition nextTransition = enabledTransitions.get(0);
		for(ITransition t : enabledTransitions){
			if(nextTransition.priority() > t.priority()){
				nextTransition = t;
			}
		}
		HashMap<IActor, ITransition> transitions = new HashMap<IActor, ITransition>();
		transitions.put(this, nextTransition);
		return transitions;
	}

	@Override
	protected void initializeInternalVariables() {
		//initialize all memory variables
		this._internal_vars.addVariable("NEW_SEARCH_AOI", 0);
		this._internal_vars.addVariable("NEW_TARGET_DESCRIPTION", 0);
		this._internal_vars.addVariable("NEW_TERMINATE_SEARCH", 0);
		this._internal_vars.addVariable("TARGET_SIGHTED_T", false);
		this._internal_vars.addVariable("TARGET_SIGHTED_F", false);
		this._internal_vars.addVariable("FLYBY_REQ_T", false);
		this._internal_vars.addVariable("FLYBY_REQ_F", false);
		this._internal_vars.addVariable("ANOMALY_DISMISSED_T", false);
		this._internal_vars.addVariable("ANOMALY_DISMISSED_F", false);
		this._internal_vars.addVariable("SEARCH_COMPLETE", false);
		this._internal_vars.addVariable("SEARCH_FAILED", false);
	}

}
