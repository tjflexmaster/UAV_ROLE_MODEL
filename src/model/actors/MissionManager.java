package model.actors;

import java.util.ArrayList;
import java.util.HashMap;

import model.team.Channels;
import model.team.Duration;
import simulator.Actor;
import simulator.ComChannelList;
import simulator.IActor;
import simulator.ITransition;
import simulator.State;
import simulator.Transition;

public class MissionManager extends Actor {

	/**
	 * This is an enumeration of the communications from the mission manager to the parent search.
	 */
	public enum AUDIO_MM_PS_COMM {
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
	public enum AUDIO_MM_OP_COMM {
		MM_POKE_OP,
		MM_ACK_OP,
		MM_END_OP,
		MM_NEW_SEARCH_AOI,
		MM_TERMINATE_SEARCH;
	}

	/**
	 * This is an enumeration of the communications from the mission manager to the video operator.
	 */
	public enum AUDIO_MM_VO_COMM {
		MM_POKE_VO,
		MM_ACK_VO,
		MM_END_VO,
		MM_TARGET_DESCRIPTION,
		MM_TERMINATE_SEARCH;
	}

	/**
	 * This is an enumeration of the communications from the mission manager to the video gui.
	 */
	public enum VISUAL_MM_VGUI_COMM {
		MM_POKE_VGUI,
		MM_END_VGUI,
		MM_ANOMALY_DISMISSED_T,
		MM_ANOMALY_DISMISSED_F,
		MM_FLYBY_REQ_T,
		MM_FLYBY_REQ_F;
	}
	public MissionManager(ComChannelList inputs, ComChannelList outputs) {
		//initialize states
		State IDLE = new State("IDLE",0);
		//comm with PS
		State POKE_PS = new State("POKE_PS",1);
		State TX_PS = new State("TX_PS",1);
		State END_PS = new State("END_PS",1);
		State RX_PS = new State("RX_PS",1);
		//comm with OP
		State POKE_OP = new State("POKE_OP",1);
		State TX_OP = new State("TX_OP",1);
		State END_OP = new State("END_OP",1);
		State RX_OP = new State("RX_OP",1);
		//comm with VO
		State POKE_VO = new State("POKE_VO",1);
		State TX_VO = new State("TX_VO",1);
		State END_VO = new State("END_VO",1);
		State RX_VO = new State("RX_VO",1);
		//comm with VGUI
		State OBSERVING_VGUI = new State("OBSERVING_VGUI",6);
		State POKE_VGUI = new State("POKE_VGUI",1);
		State TX_VGUI = new State("TX_VGUI",1);
		State END_VGUI = new State("END_VGUI",1);

		this.initializeInternalVariables();
		
		//initialize transitions
		initializeIDLE(inputs, outputs, IDLE, RX_PS, POKE_VO, POKE_OP, OBSERVING_VGUI,
				POKE_VGUI, RX_OP, RX_VO, POKE_PS);
		//comm with PS
		initializePOKE_PS(inputs, outputs, POKE_PS,TX_PS,IDLE);
		initializeTX_PS(inputs, outputs, TX_PS, END_PS);
		initializeEND_PS(inputs, outputs, END_PS, IDLE);
		initializeRX_PS(inputs, outputs, IDLE, RX_PS, POKE_OP);
		//comm with OP
		initializePOKE_OP(inputs, outputs, IDLE, POKE_OP, TX_OP);
		initializeTX_OP(inputs, outputs, TX_OP, END_OP);
		initializeEND_OP(inputs, outputs, END_OP, IDLE);
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
		
		//initialize start state
		startState(IDLE);
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
	private void initializeIDLE(ComChannelList inputs, ComChannelList outputs,
			State IDLE, State RX_PS, State POKE_VO, State POKE_OP,
			State OBSERVING_VGUI, State POKE_VGUI, State RX_OP, State RX_VO, State POKE_PS) {
		//(IDLE,[OP_POKE_MM],[])x(RX_OP,[MM_ACK_OP],[])
		IDLE.add(new Transition(_internal_vars, inputs, outputs, RX_OP){
			@Override
			public boolean isEnabled(){
				if(_inputs.get("AUDIO_OP_MM_COMM").equals(Operator.AUDIO_OP_MM_COMM.OP_POKE_MM)){
					this.setTempOutput("AUDIO_MM_OP_COMM", MissionManager.AUDIO_MM_OP_COMM.MM_ACK_OP);
				}
				return false;
			}
		});
		
		//(IDLE,[VO_POKE_MM],[])x(RX_VO,[MM_ACK_VO],[])
		IDLE.add(new Transition(_internal_vars, inputs, outputs, RX_VO){
			@Override
			public boolean isEnabled(){
				if(_inputs.get("AUDIO_VO_MM_COMM").equals(VideoOperator.AUDIO_VO_MM_COMM.VO_POKE_MM)){
					this.setTempOutput("AUDIO_MM_VO_COMM", MissionManager.AUDIO_MM_VO_COMM.MM_ACK_VO);
				}
				return false;
			}
		});
		
		//(IDLE, [VGUI_ALERT],[])x(OBSERVING_VGUI,[],[])
		IDLE.add(new Transition(_internal_vars, inputs, outputs, OBSERVING_VGUI){
			@Override
			public boolean isEnabled(){
				if(VideoOperatorGui.VISUAL_VGUI_MM_COMM.VGUI_ALERT_MM.equals(_inputs.get(Channels.VIDEO_VGUI_MM_COMM.name()).value())){
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
				if(_inputs.get(Channels.AUDIO_PS_MM_COMM.name()).value() != null){
					
					if ( ParentSearch.AUDIO_PS_MM_COMM.PS_POKE_MM.equals(_inputs.get(Channels.AUDIO_PS_MM_COMM.name()).value()) ) {
						this.setTempOutput(Channels.AUDIO_MM_PS_COMM.name(), MissionManager.AUDIO_MM_PS_COMM.MM_ACK_PS);
						result = true;
					}
				}
				return result;		
			}
		});

		//(IDLE, [OP_POKE_MM], [])->(POKE_VO, [MM_ACK_OP], [])
		IDLE.add(new Transition(this._internal_vars, inputs, outputs, RX_OP) {
			@Override
			public boolean isEnabled() 
			{
				boolean result = false;
				if ( Operator.AUDIO_OP_MM_COMM.OP_POKE_MM.equals(_inputs.get(Channels.AUDIO_OP_MM_COMM.name()).value()) ) {
					this.setTempOutput(Channels.AUDIO_MM_OP_COMM.name(), MissionManager.AUDIO_MM_OP_COMM.MM_ACK_OP);
					result = true;
				}
				return result;		
			}
		});
		
		//(IDLE, [VO_POKE_MM], [])->(RX_VO, [MM_ACK_VO], [])
		IDLE.add(new Transition(this._internal_vars, inputs, outputs, RX_VO) {
			@Override
			public boolean isEnabled() 
			{
				boolean result = false;
				if ( VideoOperator.AUDIO_VO_MM_COMM.VO_POKE_MM.equals(_inputs.get(Channels.AUDIO_VO_MM_COMM.name()).value()) ) {
					this.setTempOutput(Channels.AUDIO_MM_VO_COMM.name(), MissionManager.AUDIO_MM_VO_COMM.MM_ACK_VO);
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
				if ( "NEW".equals(this._internal_vars.getVariable("TARGET_DESCRIPTION")) ) {
					this.setTempOutput(Channels.AUDIO_MM_VO_COMM.name(), MissionManager.AUDIO_MM_VO_COMM.MM_POKE_VO);
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
				if ( this._internal_vars.getVariable("AREA_OF_INTEREST") != null && this._internal_vars.getVariable("AREA_OF_INTEREST").equals("NEW") ) {
					this.setTempOutput(Channels.AUDIO_MM_OP_COMM.name(), MissionManager.AUDIO_MM_OP_COMM.MM_POKE_OP);
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
				if ( this._internal_vars.getVariable("TERMINATE_SEARCH") != null && (Integer) this._internal_vars.getVariable("TERMINATE_SEARCH") > 0) {
					this.setTempOutput("AUDIO_MM_OP_COMM", AUDIO_MM_OP_COMM.MM_POKE_OP);
					result = true;
				}
				return result;		
			}
		});

		//(IDLE,[],[ANOMALY_DISMISSED_T])x(POKE_VGUI,[],[ANOMALY_DISMISSED_T])
		IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_VGUI){
			@Override
			public boolean isEnabled(){
				if(_internal_vars.getVariable("ANOMALY_DISMISSED_T") != null && (Boolean)_internal_vars.getVariable("ANOMALY_DISMISSED_T")){
					this.setTempInternalVar("ANOMALY_DISMISSED_T", true);
					return true;
				}
				return false;
			}
		});
		
		//(IDLE,[],[ANOMALY_DISMISSED_F])x(POKE_VGUI,[],[ANOMALY_DISMISSED_F])
		IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_VGUI){
			@Override
			public boolean isEnabled(){
				if(_internal_vars.getVariable("ANOMALY_DISMISSED_F") != null && (Boolean)_internal_vars.getVariable("ANOMALY_DISMISSED_F")){
					this.setTempInternalVar("ANOMALY_DISMISSED_F", true);
					return true;
				}
				return false;
			}
		});

		//(IDLE,[],[FLYBY_REQ_T])x(POKE_VGUI,[],[FLYBY_REQ_T])
		IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_VGUI){
			@Override
			public boolean isEnabled(){
				if(_internal_vars.getVariable("FLYBY_REQ_T") != null && (Boolean)_internal_vars.getVariable("FLYBY_REQ_T")){
					this.setTempInternalVar("FLYBY_REQ_T", true);
					return true;
				}
				return false;
			}
		});

		//(IDLE,[],[FLYBY_REQ_F])x(POKE_VGUI,[],[FLYBY_REQ_F])
		IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_VGUI){
			@Override
			public boolean isEnabled(){
				if(_internal_vars.getVariable("FLYBY_REQ_F") != null && (Boolean)_internal_vars.getVariable("FLYBY_REQ_F")){
					this.setTempInternalVar("FLYBY_REQ_F", true);
					return true;
				}
				return false;
			}
		});
		
		//(IDLE,[],[TARGET_SIGHTED_F])x(POKE_PS,[],[POKE_PS])
		IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_PS){
			@Override
			public boolean isEnabled(){
				if(_internal_vars.getVariable("TARGET_SIGHTED_F") != null && (Boolean)_internal_vars.getVariable("TARGET_SIGHTED_F")){
					this.setTempOutput(Channels.AUDIO_MM_PS_COMM.name(), MissionManager.AUDIO_MM_PS_COMM.MM_POKE_PS);
					return true;
				}
				return false;
			}
		});
		
		//(IDLE,[],[TARGET_SIGHTED_T])x(POKE_PS,[],[POKE_PS])
		IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_PS){
			@Override
			public boolean isEnabled(){
				if(_internal_vars.getVariable("TARGET_SIGHTED_T") != null && (Boolean)_internal_vars.getVariable("TARGET_SIGHTED_F")){
					this.setTempOutput(Channels.AUDIO_MM_PS_COMM.name(), MissionManager.AUDIO_MM_PS_COMM.MM_POKE_PS);
					return true;
				}
				return false;
			}
		});

		//(IDLE,[],[SEARCH_COMPLETE])x(POKE_OP,[],[POKE_PS])
		IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_PS){
			@Override
			public boolean isEnabled(){
				if(_internal_vars.getVariable("SEARCH_COMPLETE") != null && (Boolean)_internal_vars.getVariable("SEARCH_COMPLETE")){
					this.setTempOutput(Channels.AUDIO_MM_PS_COMM.name(), MissionManager.AUDIO_MM_PS_COMM.MM_POKE_PS);
					return true;
				}
				return false;
			}
		});
		//(IDLE,[],[SEARCH_FAILED])x(POKE_OP,[],[POKE_PS])
		IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_PS){
			@Override
			public boolean isEnabled(){
				if(_internal_vars.getVariable("SEARCH_FAILED") != null && (Boolean)_internal_vars.getVariable("SEARCH_FAILED")){
					this.setTempOutput(Channels.AUDIO_MM_PS_COMM.name(), MissionManager.AUDIO_MM_PS_COMM.MM_POKE_PS);
					return true;
				}
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
				if(ParentSearch.AUDIO_PS_MM_COMM.PS_BUSY_MM.equals(_inputs.get(Channels.AUDIO_PS_MM_COMM.name()).value())){
					return true;
				}
				return false;
			}
		});
		
		//(POKE_PS,[PS_ACK_MM],[])x(TX_PS,[],[])
		POKE_PS.add(new Transition(_internal_vars, inputs, outputs, TX_PS){
			@Override
			public boolean isEnabled(){
				if(ParentSearch.AUDIO_PS_MM_COMM.PS_ACK_MM.equals(_inputs.get(Channels.AUDIO_PS_MM_COMM.name()).value())){
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
				TX_PS.add(new Transition(_internal_vars, inputs, outputs, END_PS){
					@Override
					public boolean isEnabled(){
						if((new Boolean(true)).equals(_internal_vars.getVariable("SEARCH_COMPLETE"))){
							this.setTempOutput("AUDIO_MM_PS_COMM", MissionManager.AUDIO_MM_PS_COMM.MM_SEARCH_COMPLETE);
							this.setTempInternalVar("SEARCH_COMPLETE", false);
							return true;
						}
						return false;
					}
				});
				
				//(TX_PS,[],[SearchFailed])x(END_PS,[MM_SEARCH_FAILED],[])
				TX_PS.add(new Transition(_internal_vars, inputs, outputs, END_PS){
					@Override
					public boolean isEnabled(){
						if((new Boolean(true)).equals(_internal_vars.getVariable("SEARCH_FAILED"))){
							this.setTempOutput("AUDIO_MM_PS_COMM", MissionManager.AUDIO_MM_PS_COMM.MM_SEARCH_FAILED);
							this.setTempInternalVar("SEARCH_FAILED", false);
							return true;
						}
						return false;
					}
				});
				
				//(TX_PS,[],[TargetFound_T])x(END_PS,[MM_TARGET_SIGHTED_T],[])
				TX_PS.add(new Transition(_internal_vars, inputs, outputs, END_PS){
					@Override
					public boolean isEnabled(){
						if((new Boolean(true)).equals(_internal_vars.getVariable("TARGET_SIGHTED_T"))){
							this.setTempOutput("AUDIO_MM_PS_COMM", MissionManager.AUDIO_MM_PS_COMM.MM_TARGET_SIGHTED_T);
							this.setTempInternalVar("TARGET_SIGHTED_T", false);
							return true;
						}
						return false;
					}
				});
				
				//(TX_PS,[],[TargetFound_F])x(END_PS,[MM_TARGET_SIGHTED_F],[])
				TX_PS.add(new Transition(_internal_vars, inputs, outputs, END_PS){
					@Override
					public boolean isEnabled(){
						if((new Boolean(true)).equals(_internal_vars.getVariable("TARGET_SIGHTED_F"))){
							this.setTempOutput("AUDIO_MM_PS_COMM", MissionManager.AUDIO_MM_PS_COMM.MM_TARGET_SIGHTED_F);
							this.setTempInternalVar("TARGET_SIGHTED_F", false);
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
			@Override
			public boolean isEnabled(){
				return true;
			}
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
		RX_PS.add(new Transition(this._internal_vars, inputs, outputs, IDLE){
			@Override
			public boolean isEnabled()
			{
				if ( ParentSearch.AUDIO_PS_MM_COMM.PS_END_MM.equals(_inputs.get(Channels.AUDIO_PS_MM_COMM.name()).value()) ) {
					return true;
				}
				return false;
			}
		});
		
		//(RX_PS, [PS_TARGET_DESCRIPTION_MM], [])->(IDLE, [], [PS_TARGET_DESCRIPTION_MM+1])
		RX_PS.add(new Transition(this._internal_vars, inputs, outputs, IDLE){
			@Override
			public boolean isEnabled()
			{
				if ( ParentSearch.AUDIO_PS_MM_COMM.PS_TARGET_DESCRIPTION.equals(_inputs.get(Channels.AUDIO_PS_MM_COMM.name()).value()) ) {
					this.setTempInternalVar("TARGET_DESCRIPTION", "NEW");
					return true;
				}
				return false;
			}
		});
		
		//(RX_PS, [PS_TERMINATE_SEARCH], [])->(IDLE, [], [NEW_TERMINATE_SEARCH])
		RX_PS.add(new Transition(this._internal_vars, inputs, outputs, IDLE){
			@Override
			public boolean isEnabled() 
			{
				if ( ParentSearch.AUDIO_PS_MM_COMM.PS_TERMINATE_SEARCH.equals(_inputs.get(Channels.AUDIO_PS_MM_COMM.name()).value()) ) {
					this.setTempInternalVar("TERMINATE_SEARCH", "NEW");
					return true;
				}
				return false;
			}
		});

		//(RX_PS, [PS_AREA_OF_INTEREST_MM], [])->(IDLE, [], [NEW_SEARCH_AOI+1])
		RX_PS.add(new Transition(this._internal_vars, inputs, outputs, IDLE){
			@Override
			public boolean isEnabled() 
			{
				if ( ParentSearch.AUDIO_PS_MM_COMM.PS_NEW_SEARCH_AOI.equals(_inputs.get(Channels.AUDIO_PS_MM_COMM.name()).value()) ) {
					this.setTempInternalVar("AREA_OF_INTEREST", "NEW");
					return true;
				}
				return false;
			}
		});

		add(RX_PS);
	}

	/**
	 * This method assists the constructor initialize the POKE_OP state.<br><br>
	 * (POKE_OP, [OP_ACK_MM], [])->(TX_OP, [], [])<br>
	 */
	private void initializePOKE_OP(ComChannelList inputs, ComChannelList outputs,State IDLE, State POKE_OP, State TX_OP) {
		Transition t;
		
		//(POKE_OP, [OP_ACK_MM], [])->(TX_OP, [], [])
		t = new Transition(this._internal_vars, inputs, outputs, TX_OP) {
			@Override
			public boolean isEnabled() 
			{
				boolean result = false;
				if (Operator.AUDIO_OP_MM_COMM.OP_ACK_MM.equals(_inputs.get(Channels.AUDIO_OP_MM_COMM.name()).value())) {
					result = true;
				}
				return result;		
			}
		};
		POKE_OP.add(t);
		
		//(POKE_OP,[],[])x(IDLE,[],[])
		t = new Transition(this._internal_vars, inputs, outputs, IDLE, Duration.MM_POKE_OP.getRange()){
			@Override
			public boolean isEnabled() 
			{
				return true;
			}
		};
		POKE_OP.add(t);

		add(POKE_OP);
	}

	/**
	 * This method assists the constructor initialize the POKE_OP state.<br><br>
	 * (TX_OP, [], [AREA_OF_INTEREST])->(END_OP, [MM_AREA_OF_INTEREST_OP, MM_END_OP], [])<br>
	 */
	private void initializeTX_OP(ComChannelList inputs, ComChannelList outputs, State TX_OP, State END_OP) {
		Transition t;
		
		//(TX_OP, [], [AREA_OF_INTEREST])->(END_OP, [MM_AREA_OF_INTEREST_OP, MM_END_OP], [])
		t = new Transition(this._internal_vars, inputs, outputs, END_OP) {
			@Override
			public boolean isEnabled() 
			{
				boolean result = false;
				if ( this._internal_vars.getVariable("AREA_OF_INTEREST").equals("NEW") ) {
					this.setTempOutput("MM_AREA_OF_INTEREST_OP", "NEW");
					this.setTempInternalVar("AREA_OF_INTEREST", "CURRENT");
					result = true;
				}
				return result;		
			}
		};
		TX_OP.add(t);

		add(TX_OP);
	}

	/**
	 * This method assists the constructor initialize the POKE_OP state.<br><br>
	 * (END_OP, [], [Terminate_Search])->(POKE_VO, [MM_POKE_VO], [Terminate_Search])<br>
	 * (END_OP, [], [AREA_OF_INTEREST])->(IDLE, [], [AREA_OF_INTEREST-1])<br>
	 * (END_OP, [], [])->(IDLE,[],[])<br>
	 */
	private void initializeEND_OP(ComChannelList inputs, ComChannelList outputs, State END_OP, State IDLE) {
		Transition t;

		//(END_OP, [], [Terminate_Search])->(POKE_VO, [MM_POKE_VO], [Terminate_Search])
		//(END_OP, [], [AREA_OF_INTEREST])->(IDLE, [], [AREA_OF_INTEREST-1])
		//(END_OP, [], [])->(IDLE, [], [])
		t = new Transition(this._internal_vars, inputs, outputs, IDLE) {
			@Override
			public boolean isEnabled() 
			{
				boolean result = false;
				this.setTempOutput("MM_OP", "END");
				this.setTempInternalVar("AREA_OF_INTEREST", "CURRENT");
				result = true;
				return result;		
			}
		};
		END_OP.add(t);
		
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
			public boolean isEnabled() {
				if(_inputs.get("AUDIO_OP_MM_COMM").value() != null){
					if(_inputs.get("AUDIO_OP_MM_COMM").value().equals(Operator.AUDIO_OP_MM_COMM.OP_END_MM)){
						return true;
					} else if(_inputs.get("AUDIO_OP_MM_COMM").value().equals(Operator.AUDIO_OP_MM_COMM.OP_SEARCH_COMPLETE)){
						this.setTempInternalVar("SEARCH_COMPLETE", true);
						return true;
					} else if(_inputs.get("AUDIO_OP_MM_COMM").value().equals(Operator.AUDIO_OP_MM_COMM.OP_SEARCH_FAILED)){
						this.setTempInternalVar("SEARCH_FAILED", true);
						return true;
					}
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
		Transition t;
		
		//(POKE_VO, [VO_ACK_MM], [])->(TX_VO, [], [])
		//(POKE_VO, [VO_ACK_MM], [TERMINATE_SEARCH])->(TX_VO, [], [TERMINATE_SEARCH])
		//(POKE_VO, [VO_ACK_MM], [TARGET_DESCRIPTION])->(TX_VO, [], [TARGET_DESCRIPTION])
		t = new Transition(this._internal_vars, inputs, outputs, TX_VO, Duration.NEXT.getRange(), 1) {
			@Override
			public boolean isEnabled() 
			{
				boolean result = false;
				if ( VideoOperator.AUDIO_VO_MM_COMM.VO_ACK_MM.equals(_inputs.get(Channels.AUDIO_VO_MM_COMM.name()).value()) ) {
					result = true;
				}
				return result;		
			}
		};
		POKE_VO.add(t);
		
		t = new Transition(this._internal_vars, inputs, outputs, IDLE, Duration.MM_POKE_VO.getRange(), 0);
		POKE_VO.add(t);
		
		add(POKE_VO);
	}

	/**
	 * This method assists the constructor initialize the POKE_OP state.<br><br>
	 * (TX_VO, [], [TARGET_DESCRIPTION])->(END_VO, [MM_TARGET_DESCRIPTION_VO, MM_END_VO], [TARGET_DESCRIPTION-1])<br>
	 * (TX_VO, [], [TERMINATE_SEARCH])->(END_VO, [MM_TARGET_DESCRIPTION_VO, MM_END_VO], [TERMINATE_SEARCH-1])<br>
	 */
	private void initializeTX_VO(ComChannelList inputs, ComChannelList outputs, State TX_VO, State END_VO) {
		Transition t;
		
		//(TX_VO, [], [TARGET_DESCRIPTION])->(END_VO, [MM_TARGET_DESCRIPTION_VO, MM_END_VO], [])
		//(TX_VO, [], [TERMINATE_SEARCH])->(END_VO, [MM_TARGET_DESCRIPTION_VO, MM_END_VO], [TERMINATE_SEARCH-1)
		t = new Transition(this._internal_vars, inputs, outputs, END_VO) {
			@Override
			public boolean isEnabled() 
			{
				boolean result = false;
				if ( this._internal_vars.getVariable("TARGET_DESCRIPTION").equals("NEW") ) {
					this.setTempOutput(Channels.AUDIO_MM_VO_COMM.name(), MissionManager.AUDIO_MM_VO_COMM.MM_TARGET_DESCRIPTION);
					this.setTempInternalVar("TARGET_DESCRIPTION", "CURRENT");
					result = true;
				}
				if(_internal_vars.getVariable("NEW_TERMINATE_SEARCH") != null && (Integer)_internal_vars.getVariable("NEW_TERMINATE_SEARCH") > 0){
					this.setTempOutput("AUDIO_MM_VO_COMM", MissionManager.AUDIO_MM_VO_COMM.MM_TERMINATE_SEARCH);
					this.setTempInternalVar("NEW_TERMINATE_SEARCH", (Integer)_internal_vars.getVariable("NEW_TERMINATE_SEARCH")-1);
					result = true;
				}
				return result;		
			}
		};
		TX_VO.add(t);
		
		add(TX_VO);
	}

	/**
	 * This method assists the constructor initialize the POKE_OP state.<br><br>
	 * (END_VO, [], [])->(IDLE, [MM_END_OP], [])<br>
	 */
	private void initializeEND_VO(ComChannelList inputs, ComChannelList outputs, State END_VO, State IDLE) {
		Transition t;
		
		//(END_VO, [], [])->(IDLE, [MM_END_OP], [])
		t = new Transition(this._internal_vars, inputs, outputs, IDLE) {
			@Override
			public boolean isEnabled() 
			{
				boolean result = false;
				this.setTempOutput("MM_OP", "END");
				this.setTempInternalVar("TARGET_DESCRIPTION", "CURRENT");
				result = true;
				return result;		
			}
		};
		END_VO.add(t);
		
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
				if(this._inputs.get("AUDIO_VO_MM_COMM").value() != null){
					if(VideoOperator.AUDIO_VO_MM_COMM.VO_TARGET_SIGHTED_F.equals(_inputs.get(Channels.AUDIO_VO_MM_COMM.name()).value())){
						this.setTempInternalVar("TARGET_SIGHTED_F", true);
					}
					if(VideoOperator.AUDIO_VO_MM_COMM.VO_TARGET_SIGHTED_T.equals(_inputs.get(Channels.AUDIO_VO_MM_COMM.name()).value())){
						this.setTempInternalVar("TARGET_SIGHTED_T", true);
					}
					return true;
				}
				return false;
			}
		});

		add(RX_VO);
	}

	/**
	 * This method assists the constructor initialize the OBSERVING_GUI state.<br><br>
	 * (OBSERVING_VGUI,[],[])x(IDLE,[],[])<br>
	 * (OBSERVING_VGUI,[VGUI_VALIDATION_REQ_T],[])x(IDLE,[],[FLYBY_REQ_T])<br>
	 * (OBSERVING_VGUI,[VGUI_VALIDATION_REQ_T],[])x(IDLE,[],[ANOMALY_DISMISSED_T])<br>
	 * (OBSERVING_VGUI,[VGUI_VALIDATION_REQ_T],[])x(IDLE,[],[ANOMALY_DISMISSED_T])<br>
	 * (OBSERVING_VGUI,[VGUI_VALIDATION_REQ_F],[])x(IDLE,[],[FLYBY_REQ_F])<br>
	 * (OBSERVING_VGUI,[VGUI_VALIDATION_REQ_F],[])x(IDLE,[],[ANOMALY_DISMISSED_F])<br>
	 */
	private void initializeOBSERVING_VGUI(ComChannelList inputs, ComChannelList outputs, State OBSERVING_VGUI, State IDLE) {
		//(OBSERVING_VGUI,[],[])x(IDLE,[],[])
		OBSERVING_VGUI.add(new Transition(_internal_vars, inputs, outputs, IDLE, Duration.MM_OBSERVING_VGUI.getRange(),0));
		
		//(OBSERVING_VGUI,[VGUI_VALIDATION_REQ_T],[])x(IDLE,[],[FLYBY_REQ_T])
		OBSERVING_VGUI.add(new Transition(_internal_vars,inputs,outputs,IDLE, Duration.NEXT.getRange(),1){
			@Override
			public boolean isEnabled(){
				if(VideoOperatorGui.VISUAL_VGUI_MM_COMM.VGUI_VALIDATION_REQ_T.equals(_inputs.get(Channels.VIDEO_VGUI_MM_COMM.name()).value())){
					this.setTempInternalVar("FLYBY_REQ_T", true);
					return true;
				}
				return false;
			}
		});
		
		//(OBSERVING_VGUI,[VGUI_VALIDATION_REQ_T],[])x(IDLE,[],[ANOMALY_DISMISSED_T])
		OBSERVING_VGUI.add(new Transition(_internal_vars,inputs,outputs,IDLE, Duration.NEXT.getRange(),1){
			@Override
			public boolean isEnabled(){
				if(VideoOperatorGui.VISUAL_VGUI_MM_COMM.VGUI_VALIDATION_REQ_T.equals(_inputs.get(Channels.VIDEO_VGUI_MM_COMM.name()).value())){
					this.setTempInternalVar("ANOMALY_DISMISSED_T", true);
					return true;
				}
				return false;
			}
		});
		
		//(OBSERVING_VGUI,[VGUI_VALIDATION_REQ_F],[])x(IDLE,[],[FLYBY_REQ_F])
		OBSERVING_VGUI.add(new Transition(_internal_vars,inputs,outputs,IDLE, Duration.NEXT.getRange(),1){
			@Override
			public boolean isEnabled(){
				if(VideoOperatorGui.VISUAL_VGUI_MM_COMM.VGUI_VALIDATION_REQ_F.equals(_inputs.get(Channels.VIDEO_VGUI_MM_COMM.name()).value())){
					this.setTempInternalVar("FLYBY_REQ_F", true);
					return true;
				}
				return false;
			}
		});
		
		//(OBSERVING_VGUI,[VGUI_VALIDATION_REQ_F],[])x(IDLE,[],[ANOMALY_DISMISSED_F])
		OBSERVING_VGUI.add(new Transition(_internal_vars,inputs,outputs,IDLE, Duration.NEXT.getRange(),1){
			@Override
			public boolean isEnabled(){
				if(VideoOperatorGui.VISUAL_VGUI_MM_COMM.VGUI_VALIDATION_REQ_F.equals(_inputs.get(Channels.VIDEO_VGUI_MM_COMM.name()).value())){
					this.setTempInternalVar("ANOMALY_DISMISSED_F", true);
					return true;
				}
				return false;
			}
		});
		
		add(OBSERVING_VGUI);
	}

	/**
	 * This method assists the constructor initialize the POKE_VGUI state.<br><br>
	 * (POKE_VGUI,[],[])x(TX_VGUI,[],[])<br>
	 */
	private void initializePOKE_VGUI(ComChannelList inputs, ComChannelList outputs, State POKE_VGUI, State TX_VGUI) {
		//(POKE_VGUI,[],[])x(TX_VGUI,[],[])
		POKE_VGUI.add(new Transition(_internal_vars,inputs,outputs,TX_VGUI){
			@Override
			public boolean isEnabled(){
				return true;
			}
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
		TX_VGUI.add(new Transition(_internal_vars, inputs, outputs, END_VGUI){
			@Override
			public boolean isEnabled(){
				if((Boolean)_internal_vars.getVariable("ANOMALY_DISMISSED_T")){
					this.setTempInternalVar("ANOMALY_DISMISSED_T", false);
					this.setTempOutput("VISUAL_MM_VGUI_COMM", MissionManager.VISUAL_MM_VGUI_COMM.MM_ANOMALY_DISMISSED_T);
				}else if((Boolean)_internal_vars.getVariable("ANOMALY_DISMISSED_F")){
					this.setTempInternalVar("ANOMALY_DISMISSED_F", false);
					this.setTempOutput("VISUAL_MM_VGUI_COMM", MissionManager.VISUAL_MM_VGUI_COMM.MM_ANOMALY_DISMISSED_F);
				}else if((Boolean)_internal_vars.getVariable("FLYBY_REQ_T")){
					this.setTempInternalVar("FLYBY_REQ_T", false);
					this.setTempOutput("VISUAL_MM_VGUI_COMM", MissionManager.VISUAL_MM_VGUI_COMM.MM_FLYBY_REQ_T);
				}else if((Boolean)_internal_vars.getVariable("FLYBY_REQ_F")){
					this.setTempInternalVar("FLYBY_REQ_F", false);
					this.setTempOutput("VISUAL_MM_VGUI_COMM", MissionManager.VISUAL_MM_VGUI_COMM.MM_FLYBY_REQ_F);
				}
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
			@Override
			public boolean isEnabled(){
				return true;
			}
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
			if(nextTransition.priority() < t.priority()){
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
		this._internal_vars.addVariable("TARGET_DESCRIPTION", null);
		this._internal_vars.addVariable("AREA_OF_INTEREST", null);
		this._internal_vars.addVariable("TERMINATE_SEARCH", null);
		this._internal_vars.addVariable("ANOMALY_DISMISSED_T", false);
		this._internal_vars.addVariable("ANOMALY_DISMISSED_F",false);
		this._internal_vars.addVariable("FLYBY_REQ_T",false);
		this._internal_vars.addVariable("FLYBY_REQ_F",false);
	}

}
