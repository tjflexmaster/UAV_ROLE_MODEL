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
		MM_END_OP;
	}
	
	public enum MM_OP_DATA {
		MM_NEW_SEARCH_AOI,
		MM_TERMINATE_SEARCH,
		
	}
	
	public enum MM_VO_COMM {
		MM_POKE_VO,
		MM_ACK_VO,
		MM_END_VO;
	}
	
	public enum MM_VO_DATA {
		MM_TARGET_DESCRIPTION,
		MM_TERMINATE_SEARCH,
	}
	
	public enum MM_VGUI_COMM {
		MM_POKE_VGUI,
		MM_END_VGUI
	}
	
	public enum MM_VGUI_DATA {
		MM_ANOMALY_DISMISSED_T,
		MM_ANOMALY_DISMISSED_F,
		MM_FLYBY_REQ_T,
		MM_FLYBY_REQ_F
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
		initializePOKE_PS(inputs, outputs, POKE_PS,TX_PS);
		initializeTX_PS(TX_PS);
		initializeEND_PS(END_PS);
		initializeRX_PS(inputs, outputs, IDLE, RX_PS, POKE_OP);
		//comm with OP
		initializePOKE_OP(inputs, outputs, IDLE, POKE_OP, TX_OP);
		initializeTX_OP(inputs, outputs, TX_OP, END_OP);
		initializeEND_OP(inputs, outputs, END_OP, IDLE, POKE_VO);
		initializeRX_OP(inputs, IDLE, RX_OP);
		//comm with VO
		initializePOKE_VO(inputs, outputs, IDLE, POKE_VO, TX_VO);
		initializeTX_VO(inputs, outputs, TX_VO, END_VO);
		initializeEND_VO(inputs, outputs, END_VO, IDLE);
		initializeRX_VO(inputs, IDLE, RX_VO);
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
				if ( _inputs.containsKey("PS_MM_COMM") && _inputs.get("PS_MM_COMM").equals("PS_POKE_MM") ) {
					this.setTempOutput("MM_PS_COMM", "MM_ACK_PS");
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
				if ( (Integer)this._internal_vars.getVariable("NewTargetDescription") > 0 ) {
					this.setTempOutput("MM_VO_COMM", MissionManager.MM_VO_COMM.MM_POKE_VO);
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
				if ( (Integer) this._internal_vars.getVariable("NewSearchAOI") > 0) {
					this.setTempOutput("MM_OP_COMM", MissionManager.MM_OP_COMM.MM_POKE_OP);
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
				if ( (Integer) this._internal_vars.getVariable("NewTerminateSearch") > 0) {
					this.setTempOutput("MM_OP_COMM", MissionManager.MM_OP_COMM.MM_POKE_OP);
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

	private void initializePOKE_PS(ComChannelList inputs, ComChannelList outputs, State POKE_PS, State TX_PS) {
		/*POKE_PS.addTransition(
				new UDO[]{inputs.get(UDO.PS_BUSY_MM)},
				new UDO[]{outputs.get(UDO.MM_POKE_PS)},
				POKE_PS, null, 0);*/

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

		//(RX_PS, [PS_AREA_OF_INTEREST_MM, PS_END_MM], [])->(IDLE, [], [TARGET_DESCRIPTION])
		//(RX_PS, [PS_TARGET_DESCRIPTION_MM, PS_END_MM], [])->(IDLE, [], [PS_TARGET_DESCRIPTION_MM])
		//(RX_PS, [PS_AREA_OF_INTEREST_MM, PS_TARGET_DESCRIPTION_MM, PS_END_MM], [])->(IDLE, [], [TARGET_DESCRIPTION, PS_TARGET_DESCRIPTION_MM])
		t = new Transition(this._internal_vars, inputs, outputs, IDLE ) {
			@Override
			public boolean isEnabled() 
			{
				boolean result = false;
				
				if(_inputs.get("PS_MM_COMM").equals(ParentSearch.PS_MM_COMM.PS_END_MM)){
					if ( this._inputs.get("PS_MM_DATA").equals(ParentSearch.PS_MM_DATA.PS_NEW_SEARCH_AOI) ) {
						int num = 1;
						if(_internal_vars.getVariable("NewSearchAOI") != null)
							num = (Integer)_internal_vars.getVariable("NewSearchAOI")+1;
						this.setTempInternalVar("NewSearchAOI", num);
					}
					if ( _inputs.get("PS_MM_DATA").equals(ParentSearch.PS_MM_DATA.PS_TARGET_DESCRIPTION) ) {
						int num = 1;
						if(_internal_vars.getVariable("NewTargetDescription") != null)
							num = (Integer)_internal_vars.getVariable("NewTargetDescription")+1;
						this.setTempInternalVar("NewTargetDescription", num);
					}
					if ( this._inputs.get("PS_MM_DATA").equals(ParentSearch.PS_MM_DATA.PS_TERMINATE_SEARCH) ) {
						int num = 1;
						if(_internal_vars.getVariable("NewTerminateSearch") != null)
							num = (Integer)_internal_vars.getVariable("NewTerminateSearch")+1;
						this.setTempInternalVar("NewTerminateSearch", num);
					}
					result = true;
				}
				return result;
			}
		};
		RX_PS.add(t);

		//(RX_PS, [PS_END_MM], [])->(IDLE, [], [TARGET_DESCRIPTION])
		t = new Transition(this._internal_vars, inputs, outputs, IDLE) {
			@Override
			public boolean isEnabled() 
			{
				boolean result = false;
				if ( this._internal_vars.getVariable("PS_MM").equals("END") ) {
					result = true;
				}
				return result;
			}
		};
		RX_PS.add(t);
		
		/*RX_PS.addTransition(
				new UDO[]{inputs.get(UDO.PS_END_MM.name())},
				null,
				null,
				null,
				IDLE, Duration.MM_RX_PS, -1);
		RX_PS.addTransition(
				null,
				null,
				RX_PS, null, 0);
		RX_PS.addTransition(
				new UDO[]{inputs.get(UDO.PS_END_MM.name()), inputs.get(UDO.PS_TARGET_DESCRIPTION_MM.name()), inputs.get(UDO.PS_NEW_SEARCH_AOI_MM.name())},
				null,
				new UDO[]{outputs.get(UDO.MM_TARGET_DESCRIPTION_MM.name()), outputs.get(UDO.MM_NEW_SEARCH_AOI_MM.name()), outputs.get(UDO.MM_POKE_OP.name())},
				null,
				POKE_OP, Duration.NEXT, 0);
		RX_PS.addTransition(
				new UDO[]{inputs.get(UDO.PS_TERMINATE_SEARCH_MM)},
				new UDO[]{outputs.get(UDO.MM_TERMINATE_SEARCH_MM)},
				RX_PS, null, 0);*/

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
					if(_internal_vars.getVariable("NewTerminateSearch") != null){
						num = (Integer)_internal_vars.getVariable("NewTerminateSearch")-1;
						this.setTempInternalVar("NewTerminateSearch", num);
						this.setTempOutput("MM_OP_DATA", MissionManager.MM_OP_DATA.MM_TERMINATE_SEARCH);
					} else if(_internal_vars.getVariable("NewSearchAOI") != null){
						num = (Integer)_internal_vars.getVariable("NewSearchAOI")-1;
						this.setTempInternalVar("NewSearchAOI", num);
						this.setTempOutput("MM_OP_DATA", MissionManager.MM_OP_DATA.MM_NEW_SEARCH_AOI);
					}
					this.setTempOutput("MM_OP_COMM", MissionManager.MM_OP_COMM.MM_END_OP);
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
				if((Integer)_internal_vars.getVariable("NewTerminateSearch") > 0 ){
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
				if((Integer)_internal_vars.getVariable("NewSearchAOI") > 0){
					this.setTempInternalVar("NewSearchAOI", (Integer) _internal_vars.getVariable("NewSearchAOI")-1);
				}
				return true;
			}
		};
		END_OP.add(t);
		
		add(END_OP);
	}

	private void initializeRX_OP(ComChannelList inputs, State IDLE, State RX_OP) {
		/*RX_OP.addTransition(
				new UDO[]{inputs.get(UDO.OP_END_MM.name()), inputs.get(UDO.OP_SEARCH_AOI_COMPLETE_MM.name())},
				null,
				IDLE, null, 0);*/

		add(RX_OP);
	}

	private void initializePOKE_VO(ComChannelList inputs, ComChannelList outputs, State IDLE, State POKE_VO, State TX_VO) {
		Transition t;
		
		//(POKE_VO, [VO_ACK_MM], [])->(TX_VO, [], [])
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
		
		/*POKE_VO.addTransition(
				new UDO[]{inputs.get(UDO.MM_TARGET_DESCRIPTION_MM.name())},
				null,
				new UDO[]{outputs.get(UDO.MM_POKE_VO.name()), UDO.MM_TARGET_DESCRIPTION_MM},
				null,
				IDLE, Duration.MM_POKE_VO, 0);
		POKE_VO.addTransition(
				new UDO[]{inputs.get(UDO.VO_ACK_MM.name()), UDO.MM_TARGET_DESCRIPTION_MM},
				null,
				new UDO[]{outputs.get(UDO.MM_END_VO.name()), outputs.get(UDO.MM_TARGET_DESCRIPTION_VO.name())},
				null,
				TX_VO, Duration.NEXT, 0);*/

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
				if ( (Integer)this._internal_vars.getVariable("NewTargetDescription") > 0 ) {
					this.setTempOutput("MM_VO_DATA", MissionManager.MM_VO_DATA.MM_TARGET_DESCRIPTION);
					this.setTempInternalVar("NewTargetDescription", (Integer)_internal_vars.getVariable("NewTargetDescription")-1);
				}
				if((Integer)_internal_vars.getVariable("NewTerminateSearch") > 0){
					this.setTempOutput("MM_VO_DATA", MissionManager.MM_VO_DATA.MM_TERMINATE_SEARCH);
					this.setTempInternalVar("NewTerminateSearch", (Integer)_internal_vars.getVariable("NewTerminateSearch")-1);
				}
				this.setTempOutput("MM_VO_COMM", MissionManager.MM_VO_COMM.MM_END_VO);
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

	private void initializeRX_VO(ComChannelList inputs, State IDLE, State RX_VO) {
		/*RX_VO.addTransition(
				new UDO[]{inputs.get(UDO.VO_END_MM.name()),inputs.get(UDO.VO_TARGET_SIGHTING_F_MM.name())},
				null,
				IDLE, null, -1);
		RX_VO.addTransition(
				new UDO[]{inputs.get(UDO.VO_END_MM.name()), inputs.get(UDO.VO_TARGET_SIGHTING_T_MM.name())},
				null,
				IDLE, null, -1);*/

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
		this._internal_vars.addVariable("PS_POKE_MM", 0);
	}

}
