package model.actors;

import model.team.*;
import simulator.*;

/**
 * The parent search is an abstraction of everyone else that is involved in the search.
 * It is most easy to conceptualize this actor as the mission manager's boss.
 */
public class ParentSearch extends Actor {

	/**
	 * This is an enumeration of the communications from the parent search to the mission manager.
	 */
	public enum AUDIO_PS_MM_COMM {
		PS_POKE_MM,
		PS_BUSY_MM,
		PS_ACK_MM,
		PS_END_MM,
		PS_NEW_SEARCH_AOI_MM,
		PS_TERMINATE_SEARCH_MM,
		PS_TARGET_DESCRIPTION_MM
	}
	
	/**
	 * This constructor initializes all of the states and transitions that belong to the parent search.
	 * @param inputs
	 * @param outputs
	 */
	public ParentSearch(ComChannelList inputs, ComChannelList outputs) {
		//initialize name
		setName("PARENT_SEARCH");
		
		//initialize states
		State IDLE = new State("IDLE");
		State POKE_MM = new State("POKE_MM");
		State TX_MM = new State("TX_MM");
		State END_MM = new State("END_MM");
		State RX_MM = new State("RX_MM");
		
		//Initialize Internal Variables
		this.initializeInternalVariables();
		
		//Initialize States		
		initializeRxMM(inputs, outputs, IDLE, RX_MM);
		initializeIDLE(inputs, outputs, IDLE, POKE_MM, RX_MM);
		initializePokeMM(inputs, outputs, IDLE, POKE_MM, TX_MM);
		initializeTxMM(inputs,outputs,IDLE,TX_MM,END_MM);
		initializeEndMM(inputs,outputs,IDLE,END_MM, POKE_MM);
		
		//add states
		add(IDLE);
		add(POKE_MM);
		add(TX_MM);
		add(END_MM);
		add(RX_MM);
		
		//initialize current state
		startState(IDLE);
	}

	private void initializeRxMM(ComChannelList inputs, ComChannelList outputs,
			State IDLE, State RX_MM) {
		RX_MM.add(new Transition(_internal_vars,inputs,outputs,IDLE){
			@Override
			public boolean isEnabled(){
				//if(_inputs.get(Channels.AUDIO_MM_PS_COMM.name()).value() != null){
					Object AUDIO_MM_PS_COMM = _inputs.get(Channels.AUDIO_MM_PS_COMM.name()).value();
					if(MissionManager.AUDIO_MM_PS_COMM.MM_END_PS.equals(AUDIO_MM_PS_COMM)){
						return true;
					} else if(MissionManager.AUDIO_MM_PS_COMM.MM_SEARCH_COMPLETE_PS.equals(AUDIO_MM_PS_COMM)){
						this.setTempInternalVar("SEARCH_COMPLETE", true);
						this.setTempInternalVar("SEARCH_ACTIVE", false);
						return true;
					} else if(MissionManager.AUDIO_MM_PS_COMM.MM_SEARCH_FAILED_PS.equals(AUDIO_MM_PS_COMM)){
						this.setTempInternalVar("SEARCH_FAILED", true);
						this.setTempInternalVar("SEARCH_ACTIVE", false);
						return true;
					} else if(MissionManager.AUDIO_MM_PS_COMM.MM_TARGET_SIGHTED_F_PS.equals(AUDIO_MM_PS_COMM)){
						this.setTempInternalVar("TARGET_FOUND", true);
						return true;
					} else if(MissionManager.AUDIO_MM_PS_COMM.MM_TARGET_SIGHTED_T_PS.equals(AUDIO_MM_PS_COMM)){
						this.setTempInternalVar("TARGET_FOUND", true);
						return true;
					}
					//return true;
				//}
				return false;
			}
		});
	}

	/**
	 * This method assists the constructor initialize the end_mm state.<br><br>
	 * (END_MM, [], []) -> (IDLE, [], [])<br>
	 */
	private void initializeEndMM(ComChannelList inputs, ComChannelList outputs, State IDLE, State END_MM, State POKE_MM) {
		//(END_MM, [], []) -> (IDLE, [], [])
		END_MM.add(new Transition(_internal_vars, inputs, outputs, IDLE));
	}

	/**
	 * This method assists the constructor initialize the tx_mm state.<br><br>
	 * (TX_MM,[],[NEW_SEARCH_AOI,*])x(END_MM,[END_MM,PS_NEW_SEARCH_AOI],[*])<br>
	 * (TX_MM,[],[NEW_TARGET_DESCRIPTION,*])x(END_MM,[END_MM,PS_TARGET_DESCRIPTION],[*])<br>
	 * (TX_MM,[],[NEW_TERMINATE_SEARCH,*])x(END_MM,[END_MM,PS_TERMINATE_SEARCH],[*])<br>
	 */
	private void initializeTxMM(ComChannelList inputs, ComChannelList outputs, State IDLE, State TX_MM, State END_MM) {
		//(TX_MM,[],[NEW_SEARCH_AOI,*])x(END_MM,[END_MM,PS_NEW_SEARCH_AOI],[*])
		//(TX_MM,[],[NEW_TARGET_DESCRIPTION,*])x(END_MM,[END_MM,PS_TARGET_DESCRIPTION],[*])
		//(TX_MM,[],[NEW_TERMINATE_SEARCH,*])x(END_MM,[END_MM,PS_TERMINATE_SEARCH],[*])
		TX_MM.add(new Transition(_internal_vars, inputs, outputs, END_MM, Duration.PS_TX_DATA_MM.getRange()){
			@Override
			public boolean isEnabled(){
				Integer NEW_SEARCH_AOI = (Integer)_internal_vars.getVariable("NEW_SEARCH_AOI", false);
				Integer NEW_TARGET_DESCRIPTION = (Integer)_internal_vars.getVariable("NEW_TARGET_DESCRIPTION", false);
				Integer NEW_TERMINATE_SEARCH = (Integer)_internal_vars.getVariable("NEW_TERMINATE_SEARCH", false);
				if(NEW_SEARCH_AOI > 0){
					this.setTempOutput(Channels.AUDIO_PS_MM_COMM.name(), ParentSearch.AUDIO_PS_MM_COMM.PS_NEW_SEARCH_AOI_MM);
					this.setTempInternalVar("NEW_SEARCH_AOI", NEW_SEARCH_AOI-1);
				}else if(NEW_TARGET_DESCRIPTION > 0){
					this.setTempOutput(Channels.AUDIO_PS_MM_COMM.name(), ParentSearch.AUDIO_PS_MM_COMM.PS_TARGET_DESCRIPTION_MM);
					this.setTempInternalVar("NEW_TARGET_DESCRIPTION", NEW_TARGET_DESCRIPTION-1);
				}else if(NEW_TERMINATE_SEARCH > 0){
					this.setTempOutput(Channels.AUDIO_PS_MM_COMM.name(), ParentSearch.AUDIO_PS_MM_COMM.PS_TERMINATE_SEARCH_MM);
					this.setTempInternalVar("NEW_TERMINATE_SEARCH", NEW_TERMINATE_SEARCH-1);
					this.setTempInternalVar("SEARCH_ACTIVE", false);
				}
//				this.setTempOutput("AUDIO_PS_MM_COMM", ParentSearch.PS_MM_COMM.PS_END_MM);
				return true;
			}
		});
	}

	/**
	 * This method assists the constructor initialize the poke_mm state.<br><br>
	 * (POKE_MM,[MM_ACK_PS],[])x(TX_MM,[],[])<br>
	 * (POKE_MM,[],[*])x(IDLE,[],[*])<br>
	 */
	private void initializePokeMM(ComChannelList inputs, ComChannelList outputs, State IDLE, State POKE_MM, State TX_MM) {
		//(POKE_MM,[MM_ACK_PS],[])x(TX_MM,[],[])
		POKE_MM.add(new Transition(_internal_vars, inputs, outputs, TX_MM,Duration.NEXT.getRange(),1){
			@Override
			public boolean isEnabled(){
				Object AUDIO_MM_PS_COMM = _inputs.get(Channels.AUDIO_MM_PS_COMM.name()).value();
				if(MissionManager.AUDIO_MM_PS_COMM.MM_ACK_PS.equals(AUDIO_MM_PS_COMM)){
					this.setTempOutput("AUDIO_PS_MM_COMM", null);
					return true;
				}
				return false;
			}
		});
		
		//(POKE_MM,[],[*])x(IDLE,[],[*])
		POKE_MM.add(new Transition(_internal_vars, outputs, outputs, IDLE, Duration.PS_POKE_MM.getRange(), 0){
			
		});
	}

	/**
	 * This method assists the constructor initialize the idle state.<br><br>
	 * (IDLE, [], [NewSearchEvent]) -> (POKE_MM, [PS_POKE_MM], [SEARCH_ACTIVE])<br>
	 * (IDLE,[NewSearchAreaEvent],[])x(POKE_MM,[],[SEARCH_ACTIVE, NEW_SEARCH_AOI+1])<br>
	 * (IDLE,[NewTargetDescriptionEvent],[SEARCH_ACTIVE])x(POKE_MM,[PS_POKE_MM],[SEARCH_ACTIVE, NEW_TARGET_DESCRIPTION+1])<br>
	 * (IDLE,[TerminateSearchEvent],[SEARCH_ACTIVE])x(POKE_MM,[],[SEARCH_ACTIVE, NEW_TERMINATE_SEARCH+1])<br>
	 * (IDLE,[MM_POKE_PS],[*])x(RX_MM,[PS_ACK_MM],[*])<br>
	 */
	private void initializeIDLE(ComChannelList inputs, ComChannelList outputs, State IDLE, State POKE_MM, State RX_MM) {
		//(IDLE, [NewSearchEvent], []) -> (POKE_MM, [PS_POKE_MM], [SEARCH_ACTIVE])
		IDLE.add(new Transition(this._internal_vars, inputs, outputs, POKE_MM){
			@Override
			public boolean isEnabled(){
				Boolean NEW_SEARCH_EVENT = (Boolean) _inputs.get(Channels.NEW_SEARCH_EVENT.name()).value();
				Boolean SEARCH_ACTIVE = (Boolean) _internal_vars.getVariable("SEARCH_ACTIVE", false);
				Integer NEW_SEARCH_AOI = (Integer)_internal_vars.getVariable("NEW_SEARCH_AOI", false);
				Integer NEW_TARGET_DESCRIPTION = (Integer)_internal_vars.getVariable("NEW_TARGET_DESCRIPTION", false);
				if(NEW_SEARCH_EVENT != null && NEW_SEARCH_EVENT){
					assert(!SEARCH_ACTIVE):"There is already a search going on";
					this.setTempInternalVar("SEARCH_ACTIVE", true);
					this.setTempInternalVar("NEW_SEARCH_AOI", NEW_SEARCH_AOI+1);
					this.setTempInternalVar("NEW_TARGET_DESCRIPTION", NEW_TARGET_DESCRIPTION+1);
					this.setTempOutput(Channels.AUDIO_PS_MM_COMM.name(), ParentSearch.AUDIO_PS_MM_COMM.PS_POKE_MM);
					return true;
				}
				return false;
			}
		});
		//(IDLE,[NewSearchAreaEvent],[])x(POKE_MM,[],[SEARCH_ACTIVE, NEW_SEARCH_AOI+1])
		IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_MM){
			@Override
			public boolean isEnabled(){
				Boolean NEW_SEARCH_AREA_EVENT = (Boolean) _inputs.get(Channels.NEW_SEARCH_AREA_EVENT.name()).value();
				Boolean SEARCH_ACTIVE = (Boolean) _internal_vars.getVariable("SEARCH_ACTIVE");
				Integer NEW_SEARCH_AOI = (Integer)_internal_vars.getVariable("NEW_SEARCH_AOI", false);
				if(NEW_SEARCH_AREA_EVENT){
					assert(SEARCH_ACTIVE):"There is no search active";
					this.setTempInternalVar("NEW_SEARCH_AOI", NEW_SEARCH_AOI+1);
					this.setTempOutput("AUDIO_PS_MM_COMM", ParentSearch.AUDIO_PS_MM_COMM.PS_POKE_MM);
				}
				return false;
			}
		});
		//(IDLE,[NewTargetDescriptionEvent],[SEARCH_ACTIVE])x(POKE_MM,[PS_POKE_MM],[SEARCH_ACTIVE, NEW_TARGET_DESCRIPTION+1])
		IDLE.add(new Transition(_internal_vars,inputs,outputs,POKE_MM){
			@Override
			public boolean isEnabled(){
				Boolean TARGET_DESCRIPTION_EVENT = (Boolean) _inputs.get(Channels.TARGET_DESCRIPTION_EVENT.name()).value();
				Integer NEW_TARGET_DESCRIPTION = (Integer)_internal_vars.getVariable("NEW_TARGET_DESCRIPTION", false);
				if(TARGET_DESCRIPTION_EVENT){
					assert((Boolean)_internal_vars.getVariable("SEARCH_ACTIVE")):"There is no search active";
					this.setTempInternalVar("NEW_TARGET_DESCRIPTION", NEW_TARGET_DESCRIPTION+1);
					this.setTempOutput("AUDIO_PS_MM_COMM", ParentSearch.AUDIO_PS_MM_COMM.PS_POKE_MM);
					return true;
				}
				return false;
			}
		});
		//(IDLE,[TerminateSearchEvent],[SEARCH_ACTIVE])x(POKE_MM,[],[SEARCH_ACTIVE, NEW_TERMINATE_SEARCH+1])
		IDLE.add(new Transition(this._internal_vars, inputs, outputs, POKE_MM){
			@Override
			public boolean isEnabled(){
				Boolean TERMINATE_SEARCH_EVENT = (Boolean) _inputs.get(Channels.TERMINATE_SEARCH_EVENT.name()).value();
				Boolean SEARCH_ACTIVE = (Boolean)_internal_vars.getVariable("SEARCH_ACTIVE");
				Integer NEW_TERMINATE_SEARCH = (Integer)_internal_vars.getVariable("NEW_TERMINATE_SEARCH", false);
				if(TERMINATE_SEARCH_EVENT){
					assert(SEARCH_ACTIVE):"There is no search active";
					setTempInternalVar("NEW_TERMINATE_SEARCH", NEW_TERMINATE_SEARCH+1);
					this.setTempOutput("AUDIO_PS_MM_COMM", ParentSearch.AUDIO_PS_MM_COMM.PS_POKE_MM);
					return true;
				}
				return false;
			}
		});
		//(IDLE, [],[NEW_TERMINATE_SEARCH])x(POKE_MM,[PS_POKE_MM],[NEW_TERMINATE_SEARCH])
		//(IDLE, [],[NEW_SEARCH_AOI])x(POKE_MM,[PS_POKE_MM],[NEW_SEARCH_AOI])
		//(IDLE, [],[NEW_TARGET_DESCRIPTION])x(POKE_MM,[PS_POKE_MM],[NEW_TARGET_DESCRIPTION])
		IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_MM){
			@Override
			public boolean isEnabled(){
				Integer NEW_TERMINATE_SEARCH = (Integer)_internal_vars.getVariable("NEW_TERMINATE_SEARCH", false);
				Integer NEW_SEARCH_AOI = (Integer)_internal_vars.getVariable("NEW_SEARCH_AOI", false);
				Integer NEW_TARGET_DESCRIPTION = (Integer)_internal_vars.getVariable("NEW_TARGET_DESCRIPTION", false);
				if(NEW_TERMINATE_SEARCH > 0){
					this.setTempOutput(Channels.AUDIO_PS_MM_COMM.name(), ParentSearch.AUDIO_PS_MM_COMM.PS_POKE_MM);
					return true;
				}if(NEW_SEARCH_AOI > 0){
					this.setTempOutput(Channels.AUDIO_PS_MM_COMM.name(), ParentSearch.AUDIO_PS_MM_COMM.PS_POKE_MM);
					return true;
				}if(NEW_TARGET_DESCRIPTION > 0){
					this.setTempOutput(Channels.AUDIO_PS_MM_COMM.name(), ParentSearch.AUDIO_PS_MM_COMM.PS_POKE_MM);
					return true;
				}
				return false;
			}
		});
		//(IDLE,[MM_POKE_PS],[*])x(RX_MM,[PS_ACK_MM],[*])
		IDLE.add(new Transition(_internal_vars, inputs, outputs, RX_MM){
			@Override
			public boolean isEnabled(){
				Object AUDIO_MM_PS_COMM = _inputs.get(Channels.AUDIO_MM_PS_COMM.name()).value();
				if(MissionManager.AUDIO_MM_PS_COMM.MM_POKE_PS.equals(AUDIO_MM_PS_COMM)){
					this.setTempOutput(Channels.AUDIO_PS_MM_COMM.name(), ParentSearch.AUDIO_PS_MM_COMM.PS_ACK_MM);
					return true;
				}
				return false;
			}
		});
		IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_MM){
			@Override
			public boolean isEnabled(){
				Boolean SEARCH_ACTIVE = (Boolean)_internal_vars.getVariable("SEARCH_ACTIVE");
				Boolean TARGET_FOUND = (Boolean)_internal_vars.getVariable("TARGET_FOUND");
				Integer NEW_TERMINATE_SEARCH = (Integer)_internal_vars.getVariable("NEW_TERMINATE_SEARCH", false);
				if(SEARCH_ACTIVE && TARGET_FOUND){
					this.setTempInternalVar("NEW_TERMINATE_SEARCH", NEW_TERMINATE_SEARCH+1);
					this.setTempOutput(Channels.AUDIO_PS_MM_COMM.name(), ParentSearch.AUDIO_PS_MM_COMM.PS_POKE_MM);
					return true;
				}
				return false;
			}
		});
	}


	@Override
	protected void initializeInternalVariables() {
		this._internal_vars.addVariable("SEARCH_ACTIVE", false);
		this._internal_vars.addVariable("NEW_SEARCH_AOI", 0);
		this._internal_vars.addVariable("NEW_TARGET_DESCRIPTION", 0);
		this._internal_vars.addVariable("NEW_TERMINATE_SEARCH", 0);
		this._internal_vars.addVariable("TARGET_FOUND", false);
		this._internal_vars.addVariable("SEARCH_COMPLETE", false);
		this._internal_vars.addVariable("SEARCH_FAILED", false);
		
	}

}
