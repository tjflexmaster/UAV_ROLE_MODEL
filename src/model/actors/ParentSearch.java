package model.actors;

import java.util.ArrayList;
import java.util.HashMap;

import model.team.Channels;
import model.team.Duration;
import simulator.Actor;
import simulator.ComChannel;
import simulator.ComChannelList;
import simulator.IActor;
import simulator.ITransition;
import simulator.State;
import simulator.Transition;

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
		PS_NEW_SEARCH_AOI,
		PS_TERMINATE_SEARCH,
		PS_TARGET_DESCRIPTION
	}
	
	/**
	 * This constructor initializes all of the states and transitions that belong to the parent search.
	 * @param inputs
	 * @param outputs
	 */
	public ParentSearch(ComChannelList inputs, ComChannelList outputs) {
		//initialize name
		_name = "PARENT_SEARCH";
		
		//initialize states
		State IDLE = new State("IDLE",0);
		State POKE_MM = new State("POKE_MM",1);
		State TX_MM = new State("TX_MM",1);
		State END_MM = new State("END_MM",1);
		State RX_MM = new State("RX_MM",1);
		
		RX_MM.add(new Transition(_internal_vars,inputs,outputs,IDLE){
			@Override
			public boolean isEnabled(){
				//if(_inputs.get(Channels.AUDIO_MM_PS_COMM.name()).value() != null){
					if(MissionManager.AUDIO_MM_PS_COMM.MM_END_PS.equals(_inputs.get(Channels.AUDIO_MM_PS_COMM.name()).value())){
						return true;
					} else if(MissionManager.AUDIO_MM_PS_COMM.MM_SEARCH_COMPLETE.equals(_inputs.get(Channels.AUDIO_MM_PS_COMM.name()).value())){
						this.setTempInternalVar("SEARCH_COMPLETE", true);
						this.setTempInternalVar("SEARCH_ACTIVE", false);
						return true;
					} else if(MissionManager.AUDIO_MM_PS_COMM.MM_SEARCH_FAILED.equals(_inputs.get(Channels.AUDIO_MM_PS_COMM.name()).value())){
						this.setTempInternalVar("SEARCH_FAILED", true);
						this.setTempInternalVar("SEARCH_ACTIVE", false);
						return true;
					} else if(MissionManager.AUDIO_MM_PS_COMM.MM_TARGET_SIGHTED_F.equals(_inputs.get(Channels.AUDIO_MM_PS_COMM.name()).value())){
						this.setTempInternalVar("TARGET_FOUND", true);
						return true;
					} else if(MissionManager.AUDIO_MM_PS_COMM.MM_TARGET_SIGHTED_T.equals(_inputs.get(Channels.AUDIO_MM_PS_COMM.name()).value())){
						this.setTempInternalVar("TARGET_FOUND", true);
						return true;
					}
					//return true;
				//}
				return false;
			}
		});
		
		//Initialize Internal Variables
		this.initializeInternalVariables();

		//initialize transitions
//		Transition idle_poke_mm = new Transition(this.getInternalVars(), inputs, outputs, POKE_MM) {
//			@Override
//			public boolean isEnabled() 
//			{
//				if ( this._internal_vars.getVariable("test").equals("test")  ) {
//					this.setTempOutput("test", 1);
//					this.setTempInternalVar("test", 2);
//					return true;
//				}
//				return false;
//						
//			}
//		};
//		Transition idle_poke_mm = new Transition(this.getInternalVars(), new ComChannelList )
//		createIDLETransitions(inputs, outputs, State[]{POKE_MM, RX_MM});

//		initializePokeMM(inputs, outputs, IDLE, POKE_MM, TX_MM);
		
//		TX_MM.addTransition((ITransition) t);
//		TX_MM.addTransition(
//				new UDO[]{UDO.PS_NEW_SEARCH_AOI_PS, UDO.PS_TARGET_DESCRIPTION_PS},
//				null,
//				new UDO[]{outputs.get(UDO.PS_END_MM.name()), outputs.get(UDO.PS_NEW_SEARCH_AOI_MM.name()), outputs.get(UDO.PS_TARGET_DESCRIPTION_MM.name())},
//				null,
//				END_MM, Duration.PS_TX_DATA_MM, 0);
//		END_MM.addTransition(
//				null,
//				null,
//				null,
//				null, IDLE,Duration.NEXT,0);
//		Transition idle_poke_mm = new Transition(this.getInternalVars(), new ComChannelList );
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
				if((Integer)_internal_vars.getVariable("NEW_SEARCH_AOI") > 0){
					this.setTempOutput(Channels.AUDIO_PS_MM_COMM.name(), ParentSearch.AUDIO_PS_MM_COMM.PS_NEW_SEARCH_AOI);
					int num = (Integer) _internal_vars.getVariable("NEW_SEARCH_AOI")-1;
					this.setTempInternalVar("NEW_SEARCH_AOI", num);
				}else if((Integer)_internal_vars.getVariable("NEW_TARGET_DESCRIPTION") > 0){
					this.setTempOutput(Channels.AUDIO_PS_MM_COMM.name(), ParentSearch.AUDIO_PS_MM_COMM.PS_TARGET_DESCRIPTION);
					int num = (Integer) _internal_vars.getVariable("NEW_TARGET_DESCRIPTION")-1;
					this.setTempInternalVar("NEW_TARGET_DESCRIPTION", num);
				}else if((Integer)_internal_vars.getVariable("NEW_TERMINATE_SEARCH") > 0){
					this.setTempOutput(Channels.AUDIO_PS_MM_COMM.name(), ParentSearch.AUDIO_PS_MM_COMM.PS_TERMINATE_SEARCH);
					int num = (Integer) _internal_vars.getVariable("NEW_TERMINATE_SEARCH")-1;
					this.setTempInternalVar("NEW_TERMINATE_SEARCH", num);
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
				if(MissionManager.AUDIO_MM_PS_COMM.MM_ACK_PS.equals(_inputs.get(Channels.AUDIO_MM_PS_COMM.name()).value())){
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
				if(_inputs.get(Channels.NEW_SEARCH_EVENT.name()).value() != null && (Boolean)_inputs.get(Channels.NEW_SEARCH_EVENT.name()).value()){
					int num = 1;
					assert(!(Boolean)_internal_vars.getVariable("SEARCH_ACTIVE")):"There is already a search going on";
					this.setTempInternalVar("SEARCH_ACTIVE", true);
					
					num = (Integer)_internal_vars.getVariable("NEW_SEARCH_AOI")+1;
					this.setTempInternalVar("NEW_SEARCH_AOI", num);
					
					num = (Integer)_internal_vars.getVariable("NEW_TARGET_DESCRIPTION")+1;
					this.setTempInternalVar("NEW_TARGET_DESCRIPTION", num);
					
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
				if((Boolean)_inputs.get(Channels.NEW_SEARCH_AREA_EVENT.name()).value()){
					assert((Boolean)_internal_vars.getVariable("SEARCH_ACTIVE")):"There is no search active";
					int num = (Integer)_internal_vars.getVariable("NEW_SEARCH_AOI")+1;
					this.setTempInternalVar("NEW_SEARCH_AOI", num);
					this.setTempOutput("AUDIO_PS_MM_COMM", ParentSearch.AUDIO_PS_MM_COMM.PS_POKE_MM);
				}
				return false;
			}
		});
		//(IDLE,[NewTargetDescriptionEvent],[SEARCH_ACTIVE])x(POKE_MM,[PS_POKE_MM],[SEARCH_ACTIVE, NEW_TARGET_DESCRIPTION+1])
		IDLE.add(new Transition(_internal_vars,inputs,outputs,POKE_MM){
			@Override
			public boolean isEnabled(){
				if((Boolean)_inputs.get(Channels.TARGET_DESCRIPTION_EVENT.name()).value()){
					assert((Boolean)_internal_vars.getVariable("SEARCH_ACTIVE")):"There is no search active";
					int num = (Integer)_internal_vars.getVariable("NEW_TARGET_DESCRIPTION")+1;
					this.setTempInternalVar("NEW_TARGET_DESCRIPTION", num);
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
				if((Boolean)_inputs.get(Channels.TERMINATE_SEARCH_EVENT.name()).value()){
					assert((Boolean)_internal_vars.getVariable("SEARCH_ACTIVE")):"There is no search active";
					int num = 1;
					if(_internal_vars.getVariable("NEW_TERMINATE_SEARCH")!=null){
						num = (Integer)_internal_vars.getVariable("NEW_TERMINATE_SEARCH") + 1;
					}
					setTempInternalVar("NEW_TERMINATE_SEARCH", num);
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
				if((Integer)_internal_vars.getVariable("NEW_TERMINATE_SEARCH") > 0){
					this.setTempOutput(Channels.AUDIO_PS_MM_COMM.name(), ParentSearch.AUDIO_PS_MM_COMM.PS_POKE_MM);
					return true;
				}if((Integer)_internal_vars.getVariable("NEW_SEARCH_AOI") > 0){
					this.setTempOutput(Channels.AUDIO_PS_MM_COMM.name(), ParentSearch.AUDIO_PS_MM_COMM.PS_POKE_MM);
					return true;
				}if((Integer)_internal_vars.getVariable("NEW_TARGET_DESCRIPTION") > 0){
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
				if(MissionManager.AUDIO_MM_PS_COMM.MM_POKE_PS.equals(_inputs.get(Channels.AUDIO_MM_PS_COMM.name()).value())){
					this.setTempOutput(Channels.AUDIO_PS_MM_COMM.name(), ParentSearch.AUDIO_PS_MM_COMM.PS_ACK_MM);
					return true;
				}
				return false;
			}
		});
		IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_MM){
			@Override
			public boolean isEnabled(){
				if((Boolean)_internal_vars.getVariable("SEARCH_ACTIVE")){
					if((Boolean)_internal_vars.getVariable("TARGET_FOUND")){
						this.setTempInternalVar("NEW_TERMINATE_SEARCH", (Integer)_internal_vars.getVariable("NEW_TERMINATE_SEARCH")+1);
						this.setTempOutput(Channels.AUDIO_PS_MM_COMM.name(), ParentSearch.AUDIO_PS_MM_COMM.PS_POKE_MM);
						return true;
					}
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

//	@Override
//	public HashMap<IActor, ITransition> getTransitions() {
//		State state = this.getCurrentState();
//		ArrayList<ITransition> enabledTransitions = state.getEnabledTransitions();
//		if(enabledTransitions.size() == 0)
//			return null;
//		ITransition nextTransition = enabledTransitions.get(0);
//		for(ITransition t : enabledTransitions){
//			if(nextTransition.priority() < t.priority()){
//				nextTransition = t;
//			}
//		}
//		HashMap<IActor, ITransition> transitions = new HashMap<IActor, ITransition>();
//		transitions.put(this, nextTransition);
//		return transitions;
//	}

}
