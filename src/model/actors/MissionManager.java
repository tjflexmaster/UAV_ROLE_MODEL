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

public class MissionManager extends Actor {
	
	public enum MM_PS_COMM {
		MM_POKE_PS,
		MM_TX_PS,
		MM_END_PS,
		MM_ACK_PS
	}
	
	public enum MM_PS_DATA {
		PS_NEW_SEARCH_AOI,
		PS_TERMINATE_SEARCH,
		PS_TARGET_DESCRIPTION,
		
		MM_TARGET_SIGHTED_T,
		MM_TARGET_SIGHTED_F,
		MM_SEARCH_COMPLETE,
		MM_SEARCH_FAILED
	}
	
	public enum MM_OP_COMM {
		MM_POKE_OP,
		MM_ACK_OP,
		MM_END_OP,
		MM_NEW_SEARCH_AOI,
		MM_TERMINATE_SEARCH;
	}
	
	public enum MM_VO_COMM {
		MM_POKE_VO,
		MM_ACK_VO,
		MM_END_VO,
		MM_TARGET_DESCRIPTION,
		MM_TERMINATE_SEARCH;
	}
	
	public enum MM_VGUI_COMM {
		MM_POKE_VGUI,
		MM_END_VGUI,
		MM_ANOMALY_DISMISSED_T,
		MM_ANOMALY_DISMISSED_F,
		MM_FLYBY_REQ_T,
		MM_FLYBY_REQ_F;
	}

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

		this.initializeInternalVariables();
		
		//initialize transitions
		initializeIdle(inputs, outputs, IDLE, RX_PS, POKE_VO, POKE_OP);
		//comm with PS
		initializePOKE_PS(inputs, outputs, POKE_PS,TX_PS,IDLE);
		initializeTX_PS(TX_PS);
		initializeEND_PS(END_PS);
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
		initializeOBSERVING_VGUI(OBSERVING_VGUI);
		initializePOKE_VGUI(POKE_VGUI);
		initializeTX_VGUI(TX_VGUI);
	}

	private void initializeIdle(ComChannelList inputs, ComChannelList outputs, State IDLE, State RX_PS, State POKE_VO, State POKE_OP) {
		Transition t;
		
		//(IDLE, [PS_POKE_MM], [])->(RX_PS, [MM_ACK_PS], [])
		t = new Transition(this._internal_vars, inputs, outputs, RX_PS ) {
			@Override
			public boolean isEnabled() 
			{
				boolean result = false;
				if ( _inputs.get("PS_MM_COMM").equals(ParentSearch.PS_MM_COMM.PS_POKE_MM) ) {
					this.setTempOutput("MM_PS_COMM", MM_PS_COMM.MM_ACK_PS);
					result = true;
				}
				return result;
			}
		};
		IDLE.add(t);
		
		//(IDLE, [], [TARGET_DESCRIPTION])->(POKE_VO, [MM_POKE_VO], [])
		t = new Transition(this._internal_vars, inputs, outputs, POKE_VO) {
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
		};
		IDLE.add(t);

		//(IDLE, [], [AREA_OF_INTEREST])->(POKE_OP, [POKE_OP], [])
		t = new Transition(this._internal_vars, inputs, outputs, POKE_OP) {
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
		};
		IDLE.add(t);
		
		//(IDLE, [], [Terminate_Search])->(POKE_OP, [POKE_OP], [])
		t = new Transition(this._internal_vars, inputs, outputs, POKE_OP) {
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
		};
		IDLE.add(t);
				
		/*IDLE.addTransition(
				new UDO[]{inputs.get(UDO.PS_POKE_MM.name())},
				null,
				new UDO[]{outputs.get(UDO.MM_ACK_PS.name())},
				null,
				RX_PS, Duration.ACK, 0);
		IDLE.addTransition(
				new UDO[]{inputs.get(UDO.VGUI_POSSIBLE_ANOMALY_DETECTED_F_MM)},
				new UDO[]{outputs.get(UDO.MM_FLYBY_REQ_F_VGUI)},
				IDLE, null, 0);
		IDLE.addTransition(
				new UDO[]{inputs.get(UDO.VGUI_POSSIBLE_ANOMALY_DETECTED_T_MM)},
				new UDO[]{outputs.get(UDO.MM_FLYBY_REQ_T_VGUI)},
				IDLE, null, 0);*/
		
		add(IDLE);
	}

	private void initializePOKE_PS(ComChannelList inputs, ComChannelList outputs, State POKE_PS, State TX_PS, State IDLE) {
		//(POKE_PS,[PS_BUSY_MM],[])x(IDLE,[],[])
		POKE_PS.add(new Transition(_internal_vars, inputs, outputs, TX_PS){
			@Override
			public boolean isEnabled(){
				if(_inputs.get("PS_MM_COMM").equals(ParentSearch.PS_MM_COMM.PS_ACK_MM)){
					return true;
				}
				return false;
			}
		});
		
		//(POKE_PS,[PS_ACK_MM],[])x(TX_PS,[],[])
		POKE_PS.add(new Transition(_internal_vars, inputs, outputs, IDLE){
			@Override
			public boolean isEnabled(){
				if(_inputs.get("PS_MM_COMM").equals(ParentSearch.PS_MM_COMM.PS_BUSY_MM)){
					return true;
				}
				return false;
			}
		});

		add(POKE_PS);
	}

	private void initializeTX_PS(State TX_PS) {
		add(TX_PS);
	}
	
	private void initializeEND_PS(State END_PS) {
		add(END_PS);
	}

	private void initializeRX_PS(ComChannelList inputs, ComChannelList outputs, State IDLE, State RX_PS, State POKE_OP) {
		Transition t;

		//(RX_PS, [PS_END_MM], [])->(IDLE, [], [])
		//(RX_PS, [PS_AREA_OF_INTEREST_MM], [])->(IDLE, [], [TARGET_DESCRIPTION])
		//(RX_PS, [PS_TARGET_DESCRIPTION_MM], [])->(IDLE, [], [PS_TARGET_DESCRIPTION_MM])
		//(RX_PS, [PS_TERMINATE_SEARCH], [])->(IDLE, [], [NEW_TERMINATE_SEARCH])
		t = new Transition(this._internal_vars, inputs, outputs, IDLE ) {
			@Override
			public boolean isEnabled() 
			{
				boolean result = false;
				
				if(_inputs.get("PS_MM_COMM").equals(ParentSearch.PS_MM_COMM.PS_END_MM)){
					result = true;
				}
				if ( this._inputs.get("PS_MM_COMM").equals(ParentSearch.PS_MM_COMM.PS_NEW_SEARCH_AOI) ) {
					int num = 1;
					if(_internal_vars.getVariable("NEW_SEARCH_AOI") != null)
						num = (Integer)_internal_vars.getVariable("NEW_SEARCH_AOI")+1;
					this.setTempInternalVar("NEW_SEARCH_AOI", num);
					result = true;
				}
				if ( _inputs.get("PS_MM_COMM").equals(ParentSearch.PS_MM_COMM.PS_TARGET_DESCRIPTION) ) {
					int num = 1;
					if(_internal_vars.getVariable("NEW_TARGET_DESCRIPTION") != null)
						num = (Integer)_internal_vars.getVariable("NEW_TARGET_DESCRIPTION")+1;
					this.setTempInternalVar("NEW_TARGET_DESCRIPTION", num);
					result = true;
				}
				if ( this._inputs.get("PS_MM_COMM").equals(ParentSearch.PS_MM_COMM.PS_TERMINATE_SEARCH) ) {
					int num = 1;
					if(_internal_vars.getVariable("NEW_TERMINATE_SEARCH") != null)
						num = (Integer)_internal_vars.getVariable("NEW_TERMINATE_SEARCH")+1;
					this.setTempInternalVar("NEW_TERMINATE_SEARCH", num);
					result = true;
				}
				return result;
			}
		};
		RX_PS.add(t);
		

		add(RX_PS);
	}

	private void initializePOKE_OP(ComChannelList inputs, ComChannelList outputs,State IDLE, State POKE_OP, State TX_OP) {
		Transition t;
		
		//(POKE_OP, [OP_ACK_MM], [])->(TX_OP, [], [])
		t = new Transition(this._internal_vars, inputs, outputs, TX_OP) {
			@Override
			public boolean isEnabled() 
			{
				boolean result = false;
				if ( this._internal_vars.getVariable("OP_MM_COMM").equals(Operator.OP_MM_COMM.OP_ACK_MM) ) {
					result = true;
				}
				return result;		
			}
		};
		POKE_OP.add(t);
		
		/*POKE_OP.addTransition(
				new UDO[]{UDO.MM_TARGET_DESCRIPTION_MM, UDO.MM_NEW_SEARCH_AOI_MM},
				null,
				new UDO[]{outputs.get(UDO.MM_POKE_OP.name()), UDO.MM_TARGET_DESCRIPTION_MM, UDO.MM_NEW_SEARCH_AOI_MM},
				null,
				IDLE, Duration.MM_POKE_PS, 0);
		POKE_OP.addTransition(
				new UDO[]{inputs.get(UDO.OP_ACK_MM.name()), UDO.MM_TARGET_DESCRIPTION_MM, UDO.MM_NEW_SEARCH_AOI_MM},
				null,
				new UDO[]{UDO.MM_TARGET_DESCRIPTION_MM, UDO.MM_NEW_SEARCH_AOI_MM},
				null,
				TX_OP, Duration.NEXT, 1);*/

		add(POKE_OP);
	}

	private void initializeTX_OP(ComChannelList inputs, ComChannelList outputs, State TX_OP, State END_OP) {
		Transition t;
		
		//(TX_OP, [], [AREA_OF_INTEREST])->(END_OP, [MM_AREA_OF_INTEREST_OP, MM_END_OP], [])
		t = new Transition(_internal_vars, inputs, outputs, END_OP, Duration.MM_TX_OP){
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
//					this.setTempOutput("MM_OP_COMM", MissionManager.MM_OP_COMM.MM_END_OP);
				return true;
			}
		};
		
		TX_OP.add(t);
		
		/*TX_OP.addTransition(
				new UDO[]{ UDO.MM_TARGET_DESCRIPTION_MM,UDO.MM_NEW_SEARCH_AOI_MM},
				null,
				new UDO[]{ outputs.get(UDO.MM_END_OP.name()),  outputs.get(UDO.MM_NEW_SEARCH_AOI_OP.name()), UDO.MM_TARGET_DESCRIPTION_MM},
				null,
				END_OP, Duration.MM_TX_OP, 0);*/

		add(TX_OP);
	}

	private void initializeEND_OP(ComChannelList inputs, ComChannelList outputs, State END_OP, State IDLE, State POKE_VO) {
		Transition t;

		//(END_OP, [], [Terminate_Search])->(POKE_VO, [MM_POKE_VO], [Terminate_Search])
		t = new Transition(this._internal_vars, inputs, outputs, POKE_VO) {
			@Override
			public boolean isEnabled() 
			{
				if((Integer)_internal_vars.getVariable("NEW_TERMINATE_SEARCH") > 0 ){
					return true;
				}
				return false;
			}
		};
		END_OP.add(t);
		
		//(END_OP, [], [AREA_OF_INTEREST])->(IDLE, [], [AREA_OF_INTEREST-1])
		//(END_OP, [], [])->(IDLE,[],[])
		t = new Transition(this._internal_vars, inputs, outputs, IDLE) {
			@Override
			public boolean isEnabled() 
			{
				if((Integer)_internal_vars.getVariable("NEW_SEARCH_AOI") > 0){
					this.setTempInternalVar("NEW_SEARCH_AOI", (Integer) _internal_vars.getVariable("NEW_SEARCH_AOI")-1);
				}
				return true;
			}
		};
		END_OP.add(t);
		
		add(END_OP);
	}

	private void initializeRX_OP(ComChannelList inputs, ComChannelList outputs, State IDLE, State RX_OP) {
		//(RX_OP,[*],[*])x(IDLE,[],[])
		RX_OP.add(new Transition(_internal_vars,inputs,outputs,IDLE){
			@Override
			public boolean isEnabled() 
			{
				return true;
			}
		});
		
		add(RX_OP);
	}

	private void initializePOKE_VO(ComChannelList inputs, ComChannelList outputs, State IDLE, State POKE_VO, State TX_VO) {
		Transition t;
		
		//(POKE_VO, [VO_ACK_MM], [*])->(TX_VO, [], [*])
		//(POKE_VO, [VO_ACK_MM], [TERMINATE_SEARCH])->(TX_VO, [], [TERMINATE_SEARCH])
		//(POKE_VO, [VO_ACK_MM], [TARGET_DESCRIPTION])->(TX_VO, [], [TARGET_DESCRIPTION])
		t = new Transition(this._internal_vars, inputs, outputs, TX_VO) {
			@Override
			public boolean isEnabled() 
			{
				boolean result = false;
				if (_inputs.get("VO_MM_COMM").equals(VideoOperator.VO_MM_COMM.VO_ACK_MM) ) {
					result = true;
				}
				return result;		
			}
		};
		POKE_VO.add(t);

		add(POKE_VO);
	}

	private void initializeTX_VO(ComChannelList inputs, ComChannelList outputs, State TX_VO, State END_VO) {
		Transition t;

		//(TX_VO, [], [TARGET_DESCRIPTION])->(END_VO, [MM_TARGET_DESCRIPTION_VO, MM_END_VO], [TARGET_DESCRIPTION-1])
		//(TX_VO, [], [TERMINATE_SEARCH])->(END_VO, [MM_TARGET_DESCRIPTION_VO, MM_END_VO], [TERMINATE_SEARCH-1)
		t = new Transition(this._internal_vars, inputs, outputs, END_VO) {
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
//				this.setTempOutput("MM_VO_COMM", MissionManager.MM_VO_COMM.MM_END_VO);
				return true;		
			}
		};
		TX_VO.add(t);
		
		add(TX_VO);
	}
	
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

	private void initializeRX_VO(ComChannelList inputs, ComChannelList outputs, State IDLE, State RX_VO) {
		//(RX_VO,[VO_END_MM, VO_TARGET_SIGHTING_F],[])x(IDLE,[],[TARGET_SIGHTING_F])
		//(RX_VO,[VO_END_MM, VO_TARGET_SIGHTING_T],[])x(IDLE,[],[TARGET_SIGHTING_T])
		RX_VO.add(new Transition(_internal_vars,inputs,outputs,IDLE){
			@Override
			public boolean isEnabled(){
				if(_inputs.get("VO_MM_COMM").get() != null){
					if(_inputs.get("VO_MM_COMM").equals(VideoOperator.VO_MM_DATA.VO_TARGET_SIGHTED_F)){
						this.setTempInternalVar("TARGET_SIGHTED_T", true);
					}
					if(_inputs.get("VO_MM_COMM").equals(VideoOperator.VO_MM_DATA.VO_TARGET_SIGHTED_T)){
						this.setTempInternalVar("TARGET_SIGHTED_F", true);
					}
					return true;
				}
				return false;
			}
		});
		add(RX_VO);
	}

	private void initializeOBSERVING_VGUI(State OBSERVING_VGUI) {
		add(OBSERVING_VGUI);
	}
	
	private void initializePOKE_VGUI(State POKE_VGUI) {
		add(POKE_VGUI);
	}
	
	private void initializeTX_VGUI(State TX_VGUI) {
		add(TX_VGUI);
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
		//TODO
		this._internal_vars.addVariable("NEW_SEARCH_AOI", 0);
		this._internal_vars.addVariable("NEW_TARGET_DESCRIPTION", 0);
		this._internal_vars.addVariable("NEW_TERMINATE_SEARCH", 0);
		this._internal_vars.addVariable("TARGET_SIGHTED_T", false);
		this._internal_vars.addVariable("TARGET_SIGHTED_F", false);
	}

}
