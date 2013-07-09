package model.actors;

import java.util.HashMap;

import model.team.Duration;

import simulator.Actor;
import simulator.ComChannelList;
import simulator.IActor;
import simulator.ITransition;
import simulator.State;
import simulator.Transition;

public class ParentSearch extends Actor {
	
	public enum PS_MM_COMM {
		PS_POKE_MM,
		PS_TX_MM,
		PS_END_MM,
	}
	
	public enum PS_MM_DATA {
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
		createIDLETransitions(inputs, outputs, IDLE, POKE_MM, RX_MM);

		initializePokeMM(inputs, outputs, IDLE, POKE_MM, TX_MM);
		initializeTxMM(inputs,outputs,IDLE,TX_MM,END_MM);
		initializeEndMM(inputs,outputs,IDLE,END_MM, POKE_MM);
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
		END_MM.add(new Transition(_internal_vars, inputs, outputs, IDLE){
			@Override
			public boolean isEnabled(){
				if((Integer)_internal_vars.getVariable("TerminateSearchAreas") == 1){
					setTempInternalVar("TerminateSearchAreas", 0);
					return true;
				}
				return false;
			}
		});
		END_MM.add(new Transition(_internal_vars, inputs, outputs, IDLE){
			@Override
			public boolean isEnabled(){
				if((Integer)_internal_vars.getVariable("AreasToSearch") == 1){
					setTempInternalVar("AreasToSearch", 0);
					return true;
				}
				return false;
			}
		});
		END_MM.add(new Transition(_internal_vars, inputs, outputs, POKE_MM){
			@Override
			public boolean isEnabled(){
				if((Integer)_internal_vars.getVariable("TerminateSearchAreas") > 0){
					int num = (Integer)_internal_vars.getVariable("TerminateSearchAreas")-1;
					setTempInternalVar("TerminateSearchAreas", num);
					return true;
				}
				return false;
			}
		});
		END_MM.add(new Transition(_internal_vars, inputs, outputs, POKE_MM){
			@Override
			public boolean isEnabled(){
				if((Integer)_internal_vars.getVariable("AreasToSearch") > 0){
					int num = (Integer)_internal_vars.getVariable("AreasToSearch")-1;
					setTempInternalVar("AreasToSearch", num);
					return true;
				}
				return false;
			}
		});
	}

	private void initializeTxMM(ComChannelList inputs, ComChannelList outputs,
			State IDLE, State TX_MM, State END_MM) {
		TX_MM.add(new Transition(_internal_vars, inputs, outputs, END_MM, Duration.PS_TX_DATA_MM));
	}

	@Override
	protected void initializeInternalVariables() {
		this._internal_vars.addVariable("test", 0);
		
	}

	@Override
	public HashMap<IActor, ITransition> getTransitions() {
		// TODO Auto-generated method stub
		return null;
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
		POKE_MM.add(new Transition(_internal_vars, inputs, outputs, TX_MM){
			@Override
			public boolean isEnabled(){
				if(_inputs.containsKey("MM_PS_COMM") && _inputs.get("MM_PS_COMM").get().equals("MM_ACK_PS")){
					return true;
				}
				return false;
			}
		});
//		POKE_MM.addTransition(
//				new UDO[]{UDO.PS_NEW_SEARCH_AOI_PS, UDO.PS_TARGET_DESCRIPTION_PS},
//				null,
//				new UDO[]{outputs.get(UDO.PS_POKE_MM.name()), UDO.PS_NEW_SEARCH_AOI_PS, UDO.PS_TARGET_DESCRIPTION_PS},
//				null,
//				IDLE, Duration.PS_POKE_MM, 0);
//		POKE_MM.addTransition(
//				new UDO[]{inputs.get(UDO.MM_ACK_PS.name()), UDO.PS_NEW_SEARCH_AOI_PS, UDO.PS_TARGET_DESCRIPTION_PS},
//				null,
//				new UDO[]{UDO.PS_NEW_SEARCH_AOI_PS, UDO.PS_TARGET_DESCRIPTION_PS},
//				null,
//				TX_MM,Duration.NEXT,1);
	}

	private void createIDLETransitions(ComChannelList inputs, ComChannelList outputs, State IDLE, State POKE_MM, State TX_MM) {
		Transition t = new Transition(this._internal_vars, inputs, outputs, POKE_MM ) {
			@Override
			public boolean isEnabled() 
			{
				if ( this._internal_vars.getVariable("test").equals("test")  ) {
					this.setTempOutput("test", 1);
					this.setTempInternalVar("test", 2);
					return true;
				}
				return false;
						
			}
		};
		TX_MM.add(t);
		IDLE.add(new Transition(this._internal_vars, inputs, outputs, POKE_MM){
			@Override
			public boolean isEnabled(){
				if(_inputs.containsKey("NewSearchEvent") && (Boolean)_inputs.get("NewSearchEvent").get()){
					int num = 1;
					
					if(_internal_vars.getVariable("AreasToSearch")!=null){
						num = (Integer)_internal_vars.getVariable("AreasToSearch") + 1;
					}
					setTempInternalVar("AreasToSearch", num);
					return true;
				}
				return false;
			}
		});

		IDLE.add(new Transition(this._internal_vars, inputs, outputs, POKE_MM){
			@Override
			public boolean isEnabled(){
				if(_inputs.containsKey("TerminateSearchEvent") && (Boolean)_inputs.get("TerminateSearchEvent").get()){
					int num = 1;
					if(_internal_vars.getVariable("TerminateSearchAreas")!=null){
						num = (Integer)_internal_vars.getVariable("TerminateSearchAreas") + 1;
					}
					setTempInternalVar("TerminateSearchAreas", num);
					return true;
				}
				return false;
			}
		});
		/*IDLE.addTransition(new TimerTransition(
				new UDO[]{inputs.get(UDO.PS_TIME_TIL_START_PS.name()).update(new Integer(0))}, 
				null,
				new UDO[]{outputs.get(UDO.PS_POKE_MM.name()), outputs.get(UDO.PS_NEW_SEARCH_AOI_PS.name()), outputs.get(UDO.PS_TARGET_DESCRIPTION_PS.name())},
				null,
				POKE_MM, Duration.PS_SEND_DATA_PS, 0));
		IDLE.addTransition(
				new UDO[]{inputs.get(UDO.MM_POKE_PS.name())},
				new UDO[]{outputs.get(UDO.PS_ACK_MM.name())},
				RX_MM, null, 0);
		IDLE.addTransition(
				new UDO[]{inputs.get(UDO.EVENT_TERMINATE_SEARCH_PS.name())},
				new UDO[]{UDO.PS_TERMINATE_SEARCH_PS},
				IDLE, null, 2);
		IDLE.addTransition(
				new UDO[]{inputs.get(UDO.EVENT_START_SEARCH_PS.name())},
				new UDO[]{UDO.PS_NEW_SEARCH_AOI_PS, UDO.PS_TARGET_DESCRIPTION_PS},
				IDLE,null,0);
		IDLE.addTransition(
				new UDO[]{UDO.PS_NEW_SEARCH_AOI_PS},
				new UDO[]{outputs.get(UDO.PS_POKE_MM.name()), outputs.get(UDO.PS_NEW_SEARCH_AOI_MM.name())},
				POKE_MM, null, 0);
		IDLE.addTransition(
				new UDO[]{UDO.PS_TARGET_DESCRIPTION_PS},
				new UDO[]{outputs.get(UDO.PS_POKE_MM.name()), outputs.get(UDO.PS_TARGET_DESCRIPTION_MM.name())},
				POKE_MM, null, 0);
		IDLE.addTransition(
				new UDO[]{UDO.PS_TERMINATE_SEARCH_PS},
				new UDO[]{outputs.get(UDO.PS_POKE_MM.name()), outputs.get(UDO.PS_TERMINATE_SEARCH_MM.name())},
				POKE_MM, null, 1);*/
	}


}
