package model.actors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import model.team.Channels;
import model.team.Duration;
import simulator.Actor;
import simulator.ComChannelList;
import simulator.IActor;
import simulator.ITransition;
import simulator.State;
import simulator.Transition;

public class Operator extends Actor {

	public enum VISUAL_OP_UAV_COMM {
		OP_POKE_UAV,
		OP_ACK_UAV,
		OP_END_UAV
	}

	public enum VISUAL_OP_OGUI_COMM {
		OP_POKE_OGUI,
		OP_ACK_OGUI,
		OP_END_OGUI
	}

	public enum AUDIO_OP_MM_COMM {
		OP_POKE_MM,
		OP_ACK_MM,
		OP_END_MM,
		OP_SEARCH_FAILED,
		OP_SEARCH_COMPLETE
	}

	public enum AUDIO_OP_VO_COMM {
		OP_ACK_VO
	}
	public enum DATA_OP_UAV_COMM {
		OP_POST_FLIGHT_COMPLETE, TAKE_OFF
	}
	
	public enum DATA_OP_OGUI_COMM {
		OP_END_FLYBY,
		NEW_SEARCH_AOI,
		OP_LAND_UAV
	}

	public Operator(ComChannelList inputs, ComChannelList outputs) {
		//initialize name
		_name = "OPERATOR";
		
		//initialize states
		State IDLE = new State("IDLE",0);
		State POST_FLIGHT = new State("POST_FLIGHT",6);
		State POST_FLIGHT_COMPLETE = new State("POST_FLIGHT_COMPLETE",1);
		State LAUNCH_UAV = new State("LAUNCH_UAV",6);
		State OBSERVE_GUI = new State("OBSERVE_GUI",3);
		State OBSERVE_UAV = new State("OBSERVE_UAV",3);
		//comm with mission manager
		State POKE_MM = new State("POKE_MM",1);
		State TX_MM = new State("TX_MM",1);
		State END_MM = new State("END_MM",1);
		State RX_MM = new State("RX_MM",1);
		//comm with the video operator
		State RX_VO = new State("RX_VO",1);
		State OBSERVE_FLYBY = new State("OBSERVE_FLYBY",5);
		//comm with the operator gui
		State POKE_OGUI = new State("POKE_OGUI",1);
		State TX_OGUI = new State("TX_OGUI",1);
		State END_OGUI = new State("END_OGUI",1);
		
		this.initializeInternalVariables();

		//initialize transitions
		initializeIDLE(inputs, outputs, IDLE, RX_MM, LAUNCH_UAV, OBSERVE_GUI, POKE_OGUI);
		initializePOST_FLIGHT(inputs, outputs, POST_FLIGHT, POST_FLIGHT_COMPLETE);
		initializePOST_FLIGHT_COMPLETE(inputs, outputs, POST_FLIGHT_COMPLETE, IDLE);
		initializeLAUNCH_UAV(inputs, outputs, LAUNCH_UAV, OBSERVE_GUI);
		initializeOBSERVE_GUI(inputs, outputs, OBSERVE_GUI, POKE_OGUI, POST_FLIGHT, OBSERVE_UAV, RX_MM);
		initializeOBSERVE_UAV(inputs, outputs, OBSERVE_UAV, POST_FLIGHT, OBSERVE_GUI, RX_MM);
		//comm with mission manager
		initializePOKE_MM(inputs, outputs, POKE_MM, TX_MM);
		initializeTX_MM(inputs, outputs, TX_MM, END_MM);
		initializeEND_MM(inputs, outputs, END_MM, IDLE);
		initializeRX_MM(inputs, outputs, RX_MM, POKE_OGUI, IDLE);
		//comm with video operator
		initializeRX_VO(inputs, outputs, RX_VO, IDLE);
		initializeOBSERVE_FLYBY(inputs, outputs, OBSERVE_FLYBY, IDLE, POKE_OGUI);
		//comm with operator gui
		initializePOKE_OGUI(inputs, outputs, POKE_OGUI, TX_OGUI);
		initializeTX_OGUI(inputs, outputs, TX_OGUI, END_OGUI);
		initializeEND_GUI(inputs, outputs, END_OGUI, OBSERVE_GUI);
		
		//initialize current state
		startState(IDLE);
	}

	/**
	 * (IDLE,[MM_POKE_OP],[])x(RX_MM,[OP_ACK_MM],[])
	 * (IDLE,[],[TAKE_OFF])x(LAUNCH_UAV,[OP_TAKE_OFF_OGUI],[])
	 * (IDLE,[UAV_FLYING_NORMAL],[])x(OBSERVING_GUI,[],[])
	 * (IDLE,[UAV_FLYING_FLYBY],[])x(OBSERVING_GUI,[],[])
	 */
	private void initializeIDLE(ComChannelList inputs, ComChannelList outputs, State IDLE, State RX_MM, State LAUNCH_UAV, State OBSERVE_GUI, State POKE_GUI) {
		//(IDLE,[MM_POKE_OP],[])x(RX_MM,[OP_ACK_MM],[])
		IDLE.add(new Transition(_internal_vars, inputs, outputs, RX_MM){
			@Override
			public boolean isEnabled(){
				if(MissionManager.AUDIO_MM_OP_COMM.MM_POKE_OP.equals(_inputs.get(Channels.AUDIO_MM_OP_COMM.name()).value())){
					this.setTempOutput("AUDIO_OP_MM_COMM", Operator.AUDIO_OP_MM_COMM.OP_ACK_MM);
					return true;
				}
				return false;
			};
		});
		
		//(IDLE,[],[TAKE_OFF])x(LAUNCH_UAV,[OP_TAKE_OFF_OGUI],[])
		IDLE.add(new Transition(_internal_vars, inputs, outputs, LAUNCH_UAV){
			@Override
			public boolean isEnabled(){
				if((Boolean) _internal_vars.getVariable("TAKE_OFF")){
					this.setTempOutput("AUDIO_OP_MM_COMM", Operator.AUDIO_OP_MM_COMM.OP_ACK_MM);
					return true;
				}
				return false;
			}
		});
		
		//(IDLE,[UAV_FLYING_NORMAL],[])x(OBSERVE_GUI,[],[])
		//(IDLE,[UAV_FLYING_FLYBY],[])x(OBSERVE_GUI,[],[])
		IDLE.add(new Transition(_internal_vars, inputs, outputs, OBSERVE_GUI){
			@Override
			public boolean isEnabled(){
				if(OperatorGui.VIDEO_OGUI_OP_COMM.UAV_FLYING_NORMAL.equals(_inputs.get(Channels.VIDEO_OGUI_OP_COMM))){
					return true;
				} else if (OperatorGui.VIDEO_OGUI_OP_COMM.UAV_FLYING_FLYBY.equals(_inputs.get(Channels.VIDEO_OGUI_OP_COMM))){
					
				}
				return false;
			}
		});
		IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_GUI){
			@Override
			public boolean isEnabled(){
				if((Boolean)_internal_vars.getVariable("LAND_UAV")){
					return true;
				}
				return false;
			}
		});
		IDLE.add(new Transition(_internal_vars, inputs, outputs, LAUNCH_UAV){
			@Override
			public boolean isEnabled(){
				if((Integer)_internal_vars.getVariable("NEW_SEARCH_AOI") > 0 && UAV.VISUAL_UAV_OP_COMM.LANDED.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())){
					this.setTempOutput(Channels.DATA_OP_UAV.name(), Operator.DATA_OP_UAV_COMM.TAKE_OFF);
					return true;
				}
				return false;
			}
		});
		IDLE.add(new Transition(_internal_vars, inputs, outputs, POKE_GUI){
			@Override
			public boolean isEnabled(){
				if((Integer)_internal_vars.getVariable("NEW_SEARCH_AOI") > 0 && !UAV.VISUAL_UAV_OP_COMM.LANDED.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())){
					return true;
				}
				return false;
			}
		});
		add(IDLE);
	}
	
	/**
	 * (POST_FLIGHT,[],[])x(POST_FLIGHT_COMPLETE,[OP_POST_FLIGHT_COMPLETE_UAV,[])
	 */
	private void initializePOST_FLIGHT(ComChannelList inputs, ComChannelList outputs, State POST_FLIGHT, State POST_FLIGHT_COMPLETE){
		//(POST_FLIGHT,[],[])x(POST_FLIGHT_COMPLETE,[OP_POST_FLIGHT_COMPLETE_UAV],[])
		POST_FLIGHT.add(new Transition(_internal_vars, inputs, outputs, POST_FLIGHT_COMPLETE, Duration.OP_POST_FLIGHT_COMPLETE.getRange()){
			@Override
			public boolean isEnabled(){
				this.setTempOutput("DATA_OP_UAV_COMM", Operator.DATA_OP_UAV_COMM.OP_POST_FLIGHT_COMPLETE);
				return true;
			}
		});
		add(POST_FLIGHT);
	}
	
	/**
	 * (POST_FLIGHT_COMPLETE,[],[])x(IDLE,[],[])
	 */
	private void initializePOST_FLIGHT_COMPLETE(ComChannelList inputs, ComChannelList outputs, State POST_FLIGHT_COMPLETE, State IDLE){
		//(POST_FLIGHT_COMPLETE,[],[])x(IDLE,[],[])
		POST_FLIGHT_COMPLETE.add(new Transition(_internal_vars, inputs, outputs, IDLE){
			@Override
			public boolean isEnabled(){
				return true;
			}
		});
		
		add(POST_FLIGHT_COMPLETE);
	}

	/**
	 * (LAUNCH_UAV, [UAV_FLYING_NORMAL], [])x(OBSERVE_GUI, [], [])
	 */
	private void initializeLAUNCH_UAV(ComChannelList inputs, ComChannelList outputs, State LAUNCH_UAV, State OBSERVE_GUI) {
		//(LAUNCH_UAV, [UAV_FLYING_NORMAL], [])x(OBSERVE_GUI, [], [])
		LAUNCH_UAV.add(new Transition(_internal_vars, inputs, outputs, OBSERVE_GUI){
			@Override
			public boolean isEnabled(){
				if(OperatorGui.VIDEO_OGUI_OP_COMM.UAV_FLYING_NORMAL.equals(_inputs.get(Channels.VIDEO_OGUI_OP_COMM.name()).value())){
					return true;
				}
				return false;
			}
		});
		
		add(LAUNCH_UAV);
	}

	/**
	 * (OBSERVE_GUI,[OGUI_FLYBY_REQ_F_OP],[])x(POKE_OGUI,[OP_POKE_OGUI],[])
	 * (OBSERVE_GUI,[OGUI_FLYBY_REQ_T_OP],[])x(POKE_OGUI,[OP_POKE_OGUI],[])
	 * (OBSERVE_GUI,[OGUI_LANDED_OP],[]),x(POST_FLIGHT,[],[])
	 * (OBSERVE_GUI,[],[])x(OBSERVE_UAV,[],[])
	 */
	private void initializeOBSERVE_GUI(ComChannelList inputs, ComChannelList outputs, State OBSERVE_GUI,
			State POKE_OGUI, State POST_FLIGHT, State OBSERVE_UAV, State RX_MM) {
		//(OBSERVE_GUI,[OGUI_FLYBY_REQ_F_OP],[])x(POKE_OGUI,[OP_POKE_OGUI],[])
		//(OBSERVE_GUI,[OGUI_FLYBY_REQ_T_OP],[])x(POKE_OGUI,[OP_POKE_OGUI],[])
		OBSERVE_GUI.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI, Duration.NEXT.getRange(),1){
			@Override
			public boolean isEnabled(){
				if(_inputs.get("VIDEO_OGUI_OP_COMM").equals(OperatorGui.VIDEO_OGUI_OP_COMM.OGUI_FLYBY_REQ_F)){
//					this.setTempOutput("VISUAL_OP_OGUI_COMM", Operator.VISUAL_OP_OGUI_COMM.OP_POKE_OGUI);
					return true;
				}else if(_inputs.get("VIDEO_OGUI_OP_COMM").equals(OperatorGui.VIDEO_OGUI_OP_COMM.OGUI_FLYBY_REQ_T)){
//					this.setTempOutput("VISUAL_OP_OGUI_COMM", Operator.VISUAL_OP_OGUI_COMM.OP_POKE_OGUI);
					return true;
				} else if((Integer)_internal_vars.getVariable("NEW_SEARCH_AOI") > 0){
					return true;
				}
				return false;
			}
		});
		
		//(OBSERVE_GUI,[OGUI_LANDED_OP],[]),x(POST_FLIGHT,[],[])
		OBSERVE_GUI.add(new Transition(_internal_vars, inputs, outputs, OBSERVE_UAV,Duration.NEXT.getRange(),1){
			@Override
			public boolean isEnabled(){
				if(OperatorGui.VIDEO_OGUI_OP_COMM.OGUI_LANDED.equals(_inputs.get(Channels.VIDEO_OGUI_OP_COMM.name()).value())){
					return true;
				}
				return false;
			}
		});

		OBSERVE_GUI.add(new Transition(_internal_vars, inputs, outputs, RX_MM, Duration.NEXT.getRange(), 1){
			@Override
			public boolean isEnabled(){
				if(MissionManager.AUDIO_MM_OP_COMM.MM_POKE_OP.equals(_inputs.get(Channels.AUDIO_MM_OP_COMM.name()).value())){
					this.setTempOutput(Channels.AUDIO_OP_MM_COMM.name(), Operator.AUDIO_OP_MM_COMM.OP_ACK_MM);
					return true;
				}
				return false;
			}
		});
		//(OBSERVE_GUI,[],[])x(OBSERVE_UAV,[],[])
		OBSERVE_GUI.add(new Transition(_internal_vars, inputs, outputs, OBSERVE_UAV, Duration.OP_OBSERVE_GUI.getRange(),0){
			@Override
			public boolean isEnabled(){
				return true;
			}
		});
		add(OBSERVE_GUI);
	}

	/**
	 * (OBSERVE_UAV,[UAV_LANDED],[])x(POST_FLIGHT,[],[])
	 * (OBSERVE_UAV,[],[])x(OBSERVE_GUI,[],[])
	 */
	private void initializeOBSERVE_UAV(ComChannelList inputs, ComChannelList outputs, State OBSERVE_UAV, State POST_FLIGHT, State OBSERVE_GUI, State RX_MM) {
		//(OBSERVE_UAV,[UAV_LANDED],[])x(POST_FLIGHT,[],[])
		OBSERVE_UAV.add(new Transition(_internal_vars, inputs, outputs, POST_FLIGHT, Duration.NEXT.getRange(), 1){
			@Override
			public boolean isEnabled(){
				if(OperatorGui.VIDEO_OGUI_OP_COMM.OGUI_LANDED.equals(_inputs.get(Channels.VIDEO_OGUI_OP_COMM.name()).value())){
					this.setTempInternalVar("LAND_UAV", false);
					return true;
				}
				return false;
			}
		});
		
		OBSERVE_UAV.add(new Transition(_internal_vars, inputs, outputs, RX_MM, Duration.NEXT.getRange(), 1){
			@Override
			public boolean isEnabled(){
				if(MissionManager.AUDIO_MM_OP_COMM.MM_POKE_OP.equals(_inputs.get(Channels.AUDIO_MM_OP_COMM.name()).value())){
					this.setTempOutput(Channels.AUDIO_OP_MM_COMM.name(), Operator.AUDIO_OP_MM_COMM.OP_ACK_MM);
					return true;
				}
				return false;
			}
		});
		
		//(OBSERVE_UAV,[],[])x(OBSERVE_GUI,[],[])
		OBSERVE_UAV.add(new Transition(_internal_vars, inputs, outputs, OBSERVE_GUI, Duration.OP_OBSERVE_GUI.getRange(), 0));
		
		add(OBSERVE_UAV);
	}

	/**
	 * (POKE_MM,[MM_ACK_OP],[])x([TX_MM,[],[])
	 */
	private void initializePOKE_MM(ComChannelList inputs, ComChannelList outputs, State POKE_MM, State TX_MM) {
		//(POKE_MM,[MM_ACK_OP],[])x([TX_MM,[],[])
		POKE_MM.add(new Transition(_internal_vars, inputs, outputs, TX_MM, Duration.NEXT.getRange(), 1){
			@Override
			public boolean isEnabled(){
				if(_inputs.get("AUDIO_MM_OP_COMM").equals(MissionManager.AUDIO_MM_OP_COMM.MM_ACK_OP)){
					return true;
				}
				return false;
			}
		});
		
		add(POKE_MM);
	}

	/**
	 * (TX_MM,[],[])x(END_MM,[OP_SEARCH_FAILED],[])
	 * (TX_MM,[],[])x(END_MM,[OP_SEARCH_COMPLETE],[])
	 */
	private void initializeTX_MM(ComChannelList inputs, ComChannelList outputs, State TX_MM, State END_MM) {
		//(TX_MM,[],[SEARCH_COMPLETE])x(END_MM,[OP_SEARCH_COMPLETE],[])
		//(TX_MM,[],[SEARCH_FAILED])x(END_MM,[OP_SEARCH_FAILED],[])
		TX_MM.add(new Transition(_internal_vars, inputs, outputs, END_MM, Duration.NEXT.getRange(), 1){
			@Override
			public boolean isEnabled(){
				if((Boolean) _internal_vars.getVariable("OP_SEARCH_FAILED")){
					return true;
				}else if((Boolean) _internal_vars.getVariable("SEARCH_COMPLETE")){
					return true;
				}
				return false;
			}
		});
		
		add(TX_MM);
	}

	/**
	 * (END_MM,[],[])x([IDLE,[],[])
	 */
	private void initializeEND_MM(ComChannelList inputs, ComChannelList outputs, State END_MM, State IDLE) {
		//(END_MM,[],[])x([IDLE,[],[])
		END_MM.add(new Transition(_internal_vars, inputs, outputs, IDLE, Duration.NEXT.getRange(), 1){
			@Override
			public boolean isEnabled(){
				return true;
			}
		});
				
		add(END_MM);
	}
	
	/**
	 * (RX_MM,[MM_END_OP],[])x(IDLE,[],[])
	 * (RX_MM,[MM_END_OP,MM_NEW_SEARCH_AOI],[])x(IDLE,[],[NEW_SEARCH_AOI])
	 * (RX_MM,[MM_END_OP,MM_TERMINATE_SEARCH_AOI],[])x(IDLE,[],[TERMINATE_SEARCH_AOI])
	 * (RX_MM,[],[])x(IDLE,[],[])
	 * (RX_MM,[MM_END_OP],[])x(IDLE,[],[])
	 * (RX_MM,[MM_END_OP, MM_NEW_SEARCH_AOI],[])x(POKE_OGUI,[OP_POKE_OGUI],[NEW_SEARCH_AOI])
	 */
	private void initializeRX_MM(ComChannelList inputs, ComChannelList outputs, State RX_MM, State POKE_OGUI, State IDLE){
		//(RX_MM,[MM_END_OP],[])x(IDLE,[],[])
		//(RX_MM,[MM_NEW_SEARCH_AOI],[])x(IDLE,[],[NEW_SEARCH_AOI])
		//(RX_MM,[MM_TERMINATE_SEARCH_AOI],[])x(IDLE,[],[TERMINATE_SEARCH_AOI])
		RX_MM.add(new Transition(_internal_vars, inputs, outputs, IDLE){
			@Override
			public boolean isEnabled(){
				if(MissionManager.AUDIO_MM_OP_COMM.MM_NEW_SEARCH_AOI.equals(_inputs.get(Channels.AUDIO_MM_OP_COMM.name()).value())){
					this.setTempInternalVar("NEW_SEARCH_AOI", (Integer)_internal_vars.getVariable("NEW_SEARCH_AOI")+1);
					return true;
				}
				if(MissionManager.AUDIO_MM_OP_COMM.MM_TERMINATE_SEARCH.equals(_inputs.get(Channels.AUDIO_MM_OP_COMM.name()).value())){
					this.setTempInternalVar("TERMINATE_SEARCH", "NEW");
					this.setTempInternalVar("LAND_UAV", true);
					return true;
				}
				if(MissionManager.AUDIO_MM_OP_COMM.MM_END_OP.equals(_inputs.get(Channels.AUDIO_MM_OP_COMM.name()).value()))
					return true;
				return false;
			}
		});
		//(RX_MM,[],[])x(IDLE,[],[])
		
		add(RX_MM);
	}

	/**
	 * (RX_VO,[VO_BAD_STREAM],[])x(IDLE,[],[BAD_STREAM])
	 */
	private void initializeRX_VO(ComChannelList inputs, ComChannelList outputs, State RX_VO, State IDLE) {
		//(RX_VO,[VO_BAD_STREAM],[])x(IDLE,[],[BAD_STREAM])
		RX_VO.add(new Transition(_internal_vars, inputs, outputs, IDLE){
			@Override
			public boolean isEnabled(){
				if(_inputs.get(Channels.AUDIO_VO_OP_COMM.name()) != null){
					if(VideoOperator.AUDIO_VO_OP_COMM.VO_BAD_STREAM.equals(_inputs.get(Channels.AUDIO_VO_OP_COMM.name()))){
						this.setTempInternalVar("BAD_STREAM", true);
					}
					return true;
				}
				return false;
			}
		});
		add(RX_VO);
	}

	/**
	 * (OBSERVE_FLYBY,[OGUI_FLYBY_END_FAILED, OGUI_FLYBY_END_SUCCESS],[])x(POKE_OGUI,[],[])
	 */
	private void initializeOBSERVE_FLYBY(ComChannelList inputs, ComChannelList outputs, State OBSERVE_FLYBY, State IDLE,
			State POKE_OGUI) {
		OBSERVE_FLYBY.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI){
			@Override
			public boolean isEnabled(){
				if(OperatorGui.VIDEO_OGUI_OP_COMM.OGUI_FLYBY_END_FAILED.equals(_inputs.get(Channels.VIDEO_OGUI_OP_COMM.name()))){
					this._internal_vars.setVariable("END_FLYBY", true);
				} else if(OperatorGui.VIDEO_OGUI_OP_COMM.OGUI_FLYBY_END_SUCCESS.equals(_inputs.get(Channels.VIDEO_OGUI_OP_COMM.name()))){
					this._internal_vars.setVariable("END_FLYBY", true);
				}
				return false;
			}
		});
		OBSERVE_FLYBY.add(new Transition(_internal_vars, inputs, outputs, POKE_OGUI){
			@Override
			public boolean isEnabled(){
				if(OperatorGui.VIDEO_OGUI_OP_COMM.OGUI_BATTERY_LOW.equals(_inputs.get(Channels.VIDEO_OGUI_OP_COMM.name()))){
					this.setTempInternalVar("LAND_UAV", true);
				}
				return false;
			}
		});
		add(OBSERVE_FLYBY);
	}

	/**
	 * @(POKE_OGUI, [], [])x(TX_OGUI,[],[])
	 */
	private void initializePOKE_OGUI(ComChannelList inputs, ComChannelList outputs, State POKE_OGUI, State TX_OGUI) {
		// TODO Auto-generated method stub
		POKE_OGUI.add(new Transition(_internal_vars, inputs, outputs, TX_OGUI));
		add(POKE_OGUI);
	}

	/**
	 * (RX_MM,[MM_END_OP],[NEW_SEARCH_AOI])x(END_OGUI,[NEW_SEARCH_AOI],[])
	 * (TX_OGUI,[],[END_FLYBY])x(END_OGUI,[OP_END_FLYBY],[])
	 */
	private void initializeTX_OGUI(ComChannelList inputs, ComChannelList outputs, State TX_OGUI, State END_OGUI) {
		TX_OGUI.add(new Transition(_internal_vars, inputs, outputs, END_OGUI, Duration.OP_TX_OGUI.getRange()){
			@Override
			public boolean isEnabled(){
				if((Boolean)_internal_vars.getVariable("LAND_UAV")){
					this.setTempInternalVar("TERMINATE_SEARCH", "CURRENT");
					this.setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_LAND_UAV);
					return true;
				}
				return false;
			}
		});
		//(TX_OGUI,[],[NEW_SEARCH_AOI])x(END_OGUI,[OP_TAKE_OFF_OGUI],[])
		TX_OGUI.add(new Transition(_internal_vars, inputs, outputs, END_OGUI, Duration.OP_TX_OGUI.getRange()){
			@Override
			public boolean isEnabled(){
				if(((Integer)_internal_vars.getVariable("NEW_SEARCH_AOI")) > 0){
					this.setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.NEW_SEARCH_AOI);
					this.setTempInternalVar("NEW_SEARCH_AOI", ((Integer)_internal_vars.getVariable("NEW_SEARCH_AOI"))-1);
					return true;
				}
				return false;
			}
		});
		//(TX_OGUI,[],[END_FLYBY])x(END_OGUI,[OP_END_FLYBY],[])
		TX_OGUI.add(new Transition(_internal_vars, inputs, outputs, END_OGUI, Duration.OP_TX_OGUI.getRange()){
			@Override
			public boolean isEnabled(){
				if(((Boolean)_internal_vars.getVariable("END_FLYBY"))){
					this.setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_END_FLYBY);
					this.setTempInternalVar("END_FLYBY", false);
					return true;
				}
				return false;
			}
		});
		//(TX_OGUI,[],[LAND_UAV])x(END_OGUI,[OP_LAND],[])
		TX_OGUI.add(new Transition(_internal_vars, inputs, outputs, END_OGUI, Duration.OP_TX_OGUI.getRange()){
			@Override
			public boolean isEnabled(){
				if(((Boolean)_internal_vars.getVariable("END_FLYBY"))){
					this.setTempOutput(Channels.DATA_OP_OGUI_COMM.name(), Operator.DATA_OP_OGUI_COMM.OP_LAND_UAV);
					this.setTempInternalVar("END_FLYBY", false);
					return true;
				}
				return false;
			}
		});
		add(TX_OGUI);
	}

	/**
	 * (END_OGUI,[],[])x(IDLE,[],[])
	 */
	private void initializeEND_GUI(ComChannelList inputs, ComChannelList outputs, State END_OGUI, State OBSERVE_GUI) {
		// TODO Auto-generated method stub
		END_OGUI.add(new Transition(_internal_vars, inputs, outputs, OBSERVE_GUI));
		add(END_OGUI);
	}

	@Override
	protected void initializeInternalVariables() {
		this._internal_vars.addVariable("SEARCH_COMPLETE", false);
		this._internal_vars.addVariable("OP_SEARCH_COMPLETE", false);
		this._internal_vars.addVariable("BAD_STREAM", false);
		this._internal_vars.addVariable("LAND_UAV", false);
		this._internal_vars.addVariable("TAKE_OFF", false);
		this._internal_vars.addVariable("END_FLYBY", false);
		this._internal_vars.addVariable("NEW_SEARCH_AOI", 0);
		this._internal_vars.addVariable("TERMINATE_SEARCH", null);
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
