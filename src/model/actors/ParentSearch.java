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

public class ParentSearch extends Actor {
	
//	public enum PS_MM_COMM {
//		PS_POKE_MM,
//		PS_TX_MM,
//		PS_END_MM, PS_ACK_MM, PS_BUSY_MM,
//	}
	
	public enum PS_MM_COMM {
		PS_POKE_MM,
		PS_BUSY_MM,
		PS_ACK_MM,
		PS_END_MM,
		PS_NEW_SEARCH_AOI,
		PS_TERMINATE_SEARCH,
		PS_TARGET_DESCRIPTION
	}

	public ParentSearch(ComChannelList inputs, ComChannelList outputs) {
		//initialize name
		_name = "PARENT_SEARCH";
		
		//initialize states
		State IDLE = new State("IDLE");
		State POKE_MM = new State("POKE_MM");
		State TX_MM = new State("TX_MM");
		State END_MM = new State("END_MM");
		State RX_MM = new State("RX_MM");
		
		//Set start state
		this.startState(IDLE);
		
		//Initialize Internal Variables
		this.initializeInternalVariables();

		//initialize transitions
		//IDLE Transitions
//		Transition idle_poke_mm = new Transition(this.getInternalVars(), new ComChannelList );
		initializeIDLE(inputs, outputs, IDLE, POKE_MM, RX_MM);

		initializePokeMM(inputs, outputs, IDLE, POKE_MM, TX_MM);
		initializeTxMM(inputs,outputs,IDLE,TX_MM,END_MM);
		initializeEndMM(inputs,outputs,IDLE,END_MM, POKE_MM);
		//add states
		add(POKE_MM);
		add(IDLE);
		add(POKE_MM);
		add(TX_MM);
		add(END_MM);
		add(RX_MM);
		
		//initialize current state
		startState(IDLE);
	}
	
	private void initializeEndMM(ComChannelList inputs, ComChannelList outputs,
			State IDLE, State END_MM, State POKE_MM) {
		END_MM.add(new Transition(_internal_vars, inputs, outputs, IDLE));//{
//			@Override
//			public boolean isEnabled(){
//				if((Integer)_internal_vars.getVariable("NEW_TERMINATE_SEARCH") == 1){
//					setTempInternalVar("NEW_TERMINATE_SEARCH", 0);
//					return true;
//				}
//				return false;
//			}
//		});
//		END_MM.add(new Transition(_internal_vars, inputs, outputs, IDLE){
//			@Override
//			public boolean isEnabled(){
//				if((Integer)_internal_vars.getVariable("NEW_AREAS_TO_SEARCH") == 1){
//					setTempInternalVar("NEW_AREAS_TO_SEARCH", 0);
//					return true;
//				}
//				return false;
//			}
//		});
//		END_MM.add(new Transition(_internal_vars, inputs, outputs, POKE_MM){
//			@Override
//			public boolean isEnabled(){
//				if((Integer)_internal_vars.getVariable("NEW_TERMINATE_SEARCH") > 0){
//					int num = (Integer)_internal_vars.getVariable("NEW_TERMINATE_SEARCH")-1;
//					setTempInternalVar("NEW_TERMINATE_SEARCH", num);
//					return true;
//				}
//				return false;
//			}
//		});
//		END_MM.add(new Transition(_internal_vars, inputs, outputs, POKE_MM){
//			@Override
//			public boolean isEnabled(){
//				if((Integer)_internal_vars.getVariable("NEW_AREAS_TO_SEARCH") > 0){
//					int num = (Integer)_internal_vars.getVariable("NEW_AREAS_TO_SEARCH")-1;
//					setTempInternalVar("NEW_AREAS_TO_SEARCH", num);
//					return true;
//				}
//				return false;
//			}
//		});
	}

	private void initializeTxMM(ComChannelList inputs, ComChannelList outputs,
			State IDLE, State TX_MM, State END_MM) {
		//(TX_MM,[],[NEW_SEARCH_AOI,*])x(END_MM,[END_MM,PS_NEW_SEARCH_AOI],[*])
		//(TX_MM,[],[NEW_TARGET_DESCRIPTION,*])x(END_MM,[END_MM,PS_TARGET_DESCRIPTION],[*])
		//(TX_MM,[],[NEW_TERMINATE_SEARCH,*])x(END_MM,[END_MM,PS_TERMINATE_SEARCH],[*])
		TX_MM.add(new Transition(_internal_vars, inputs, outputs, END_MM, Duration.PS_TX_DATA_MM){
			@Override
			public boolean isEnabled(){
				if((Integer)_internal_vars.getVariable("NEW_SEARCH_AOI") > 0){
					this.setTempOutput("PS_MM_COMM", ParentSearch.PS_MM_COMM.PS_NEW_SEARCH_AOI);
					int num = (Integer) _internal_vars.getVariable("NEW_SEARCH_AOI")-1;
					this.setTempInternalVar("NEW_SEARCH_AOI", num);
				}else
				if((Integer)_internal_vars.getVariable("NEW_TARGET_DESCRIPTION") > 0){
					this.setTempOutput("PS_MM_COMM", ParentSearch.PS_MM_COMM.PS_TARGET_DESCRIPTION);
					int num = (Integer) _internal_vars.getVariable("NEW_TARGET_DESCRIPTION")-1;
					this.setTempInternalVar("NEW_TARGET_DESCRIPTION", num);
				}else
				if((Integer)_internal_vars.getVariable("NEW_TERMINATE_SEARCH") > 0){
					this.setTempOutput("PS_MM_COMM", ParentSearch.PS_MM_COMM.PS_TERMINATE_SEARCH);
					int num = (Integer) _internal_vars.getVariable("NEW_TERMINATE_SEARCH")-1;
					this.setTempInternalVar("NEW_TERMINATE_SEARCH", num);
				}
//				this.setTempOutput("PS_MM_COMM", ParentSearch.PS_MM_COMM.PS_END_MM);
				return true;
			}
		});
	}

	/**
	 * @param inputs
	 * @param outputs
	 * @param IDLE
	 * @param POKE_MM
	 * @param TX_MM
	 */
	private void initializePokeMM(ComChannelList inputs,
			ComChannelList outputs, State IDLE, State POKE_MM, State TX_MM) {
		//(POKE_MM,[MM_ACK_PS],[])x(TX_MM,[],[])
		POKE_MM.add(new Transition(_internal_vars, inputs, outputs, TX_MM,Duration.NEXT,1){
			@Override
			public boolean isEnabled(){
				if(_inputs.get("MM_PS_COMM").get().equals("MM_ACK_PS")){
					this.setTempOutput("PS_MM_COMM", null);
					return true;
				}
				return false;
			}
		});
		//(POKE_MM,[],[*])x(IDLE,[],[*])
		POKE_MM.add(new Transition(_internal_vars, outputs, outputs, TX_MM, Duration.PS_POKE_MM, 0));
	}

	private void initializeIDLE(ComChannelList inputs, ComChannelList outputs, State IDLE, State POKE_MM, State RX_MM) {
		IDLE.add(new Transition(this._internal_vars, inputs, outputs, POKE_MM){
			@Override
			public boolean isEnabled(){
				if(_inputs.containsKey("NewSearchEvent") && (Boolean)_inputs.get("NewSearchEvent").get()){
					int num = 1;
					assert(!(Boolean)_internal_vars.getVariable("SEARCH_ACTIVE")):"There is already a search going on";
					this.setTempInternalVar("SEARCH_ACTIVE", true);
					this.setTempOutput("PS_MM_COMM", ParentSearch.PS_MM_COMM.PS_POKE_MM);
					return true;
				}
				return false;
			}
		});
		//(IDLE,[NewSearchAreaEvent],[])x(POKE_MM,[],[SEARCH_ACTIVE, NEW_AREAS_TO_SEARCH+1])
		IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_MM){
			@Override
			public boolean isEnabled(){
				if((Boolean)_inputs.get("NewSearchAreaEvent").get()){
					assert((Boolean)_internal_vars.getVariable("SEARCH_ACTIVE")):"There is no search active";
					int num = (Integer)_internal_vars.getVariable("NEW_AREAS_TO_SEARCH")+1;
					this.setTempInternalVar("NEW_AREAS_TO_SEARCH", num);
					this.setTempOutput("PS_MM_COMM", ParentSearch.PS_MM_COMM.PS_POKE_MM);
				}
				return false;
			}
		});
		//(IDLE,[NewTargetDescriptionEvent],[SEARCH_ACTIVE])x(POKE_MM,[PS_POKE_MM],[SEARCH_ACTIVE, NEW_TARGET_DESCRIPTION+1])
		IDLE.add(new Transition(_internal_vars,inputs,outputs,POKE_MM){
			@Override
			public boolean isEnabled(){
				if((Boolean)_inputs.get("NewTargetDescriptionEvent").get()){
					assert((Boolean)_internal_vars.getVariable("SEARCH_ACTIVE")):"There is no search active";
					int num = (Integer)_internal_vars.getVariable("NEW_TARGET_DESCRIPTION")+1;
					this.setTempInternalVar("NEW_TARGET_DESCRIPTION", num);
					this.setTempOutput("PS_MM_COMM", ParentSearch.PS_MM_COMM.PS_POKE_MM);
					return true;
				}
				return false;
			}
		});
		//(IDLE,[TerminateSearchEvent],[SEARCH_ACTIVE])x(POKE_MM,[],[SEARCH_ACTIVE, NEW_TERMINATE_SEARCH+1])
		IDLE.add(new Transition(this._internal_vars, inputs, outputs, POKE_MM){
			@Override
			public boolean isEnabled(){
				if((Boolean)_inputs.get("TerminateSearchEvent").get()){
					assert((Boolean)_internal_vars.getVariable("SEARCH_ACTIVE")):"There is no search active";
					int num = 1;
					if(_internal_vars.getVariable("NEW_TERMINATE_SEARCH")!=null){
						num = (Integer)_internal_vars.getVariable("NEW_TERMINATE_SEARCH") + 1;
					}
					setTempInternalVar("NEW_TERMINATE_SEARCH", num);
					this.setTempOutput("PS_MM_COMM", ParentSearch.PS_MM_COMM.PS_POKE_MM);
					return true;
				}
				return false;
			}
		});
		//(IDLE,[MM_POKE_PS],[*])x(RX_MM,[PS_ACK_MM],[*])
		IDLE.add(new Transition(_internal_vars, inputs, outputs, RX_MM){
			@Override
			public boolean isEnabled(){
				if((Boolean)_inputs.get("MM_PS_COMM").get().equals(MissionManager.MM_PS_COMM.MM_POKE_PS)){
					this.setTempOutput("PS_MM_COMM", ParentSearch.PS_MM_COMM.PS_ACK_MM);
					return true;
				}
				return false;
			}
		});
	}


	@Override
	protected void initializeInternalVariables() {
		this._internal_vars.addVariable("test", 0);
		this._internal_vars.addVariable("SEARCH_ACTIVE", false);
		this._internal_vars.addVariable("NEW_AREAS_TO_SEARCH", 0);
		this._internal_vars.addVariable("NEW_TARGET_DESCRIPTION", 0);
		this._internal_vars.addVariable("NEW_TERMINATE_SEARCH", 0);
		
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

}
