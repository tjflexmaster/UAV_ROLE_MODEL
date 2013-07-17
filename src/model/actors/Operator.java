package model.actors;

import java.util.ArrayList;
import java.util.HashMap;

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
		OP_END_MM, OP_SEARCH_FAILED, OP_SEARCH_COMPLETE
	}
	
	public enum AUDIO_OP_VO_COMM {
		OP_ACK_VO
	}

	public Operator(ComChannelList inputs, ComChannelList outputs) {
		//initialize name
		_name = "OPERATOR";
		
		//initialize states
		State IDLE = new State("IDLE");
		State POST_FLIGHT = new State("POST_FLIGHT");
		State POST_FLIGHT_COMPLETE = new State("POST_FLIGHT_COMPLETE");
		State LAUNCH_UAV = new State("LAUNCH_UAV");
		State OBSERVE_GUI = new State("OBSERVE_GUI");
		State OBSERVE_UAV = new State("OBSERVE_UAV");
		//comm with mission manager
		State POKE_MM = new State("POKE_MM");
		State TX_MM = new State("TX_MM");
		State END_MM = new State("END_MM");
		State RX_MM = new State("RX_MM");
		//comm with the video operator
		State RX_VO = new State("RX_VO");
		State OBSERVE_FLYBY = new State("OBSERVE_FLYBY");
		//comm with the operator gui
		State POKE_OGUI = new State("POKE_OGUI");
		State TX_OGUI = new State("TX_OGUI");
		State END_OGUI = new State("END_OGUI");

		//initialize transitions
		initializeIDLE(inputs, outputs, IDLE, RX_MM, LAUNCH_UAV, OBSERVE_GUI);
		initializePOST_FLIGHT(inputs, outputs, POST_FLIGHT, POST_FLIGHT_COMPLETE);
		initializePOST_FLIGHT_COMPLETE(inputs, outputs, POST_FLIGHT_COMPLETE, IDLE);
		initializeLAUNCH_UAV(inputs, outputs, LAUNCH_UAV);
		initializeOBSERVE_GUI(inputs, outputs, OBSERVE_GUI, POKE_OGUI, POST_FLIGHT, OBSERVE_UAV);
		initializeOBSERVE_UAV(inputs, outputs, OBSERVE_UAV, POST_FLIGHT, OBSERVE_GUI);
		//comm with mission manager
		initializePOKE_MM(inputs, outputs, POKE_MM);
		initializeTX_MM(inputs, outputs, TX_MM);
		initializeEND_MM(inputs, outputs, END_MM);
		initializeRX_MM(inputs, outputs, RX_MM, POKE_OGUI, IDLE);
		//comm with video operator
		initializeRX_VO(inputs, outputs, RX_VO);
		initializeOBSERVE_FLYBY(inputs, outputs, OBSERVE_FLYBY);
		//comm with operator gui
		initializePOKE_OGUI(inputs, outputs, POKE_OGUI);
		initializeTX_OGUI(inputs, outputs, TX_OGUI, END_OGUI);
		initializeEND_GUI(inputs, outputs, END_OGUI);
		
		//initialize current state
		startState(IDLE);
	}

	/**
	 * (IDLE,[MM_POKE_OP],[])x(RX_MM,[OP_ACK_MM],[])
	 * (IDLE,[],[TAKE_OFF])x(LAUNCH_UAV,[OP_TAKE_OFF_OGUI],[])
	 * (IDLE,[UAV_FLYING_NORMAL],[])x(OBSERVING_GUI,[],[])
	 * (IDLE,[UAV_FLYING_FLYBY],[])x(OBSERVING_GUI,[],[])
	 */
	private void initializeIDLE(ComChannelList inputs, ComChannelList outputs, State IDLE, State RX_MM, State LAUNCH_UAV, State OBSERVE_GUI) {
		//(IDLE,[MM_POKE_OP],[])x(RX_MM,[OP_ACK_MM],[])
		IDLE.add(new Transition(_internal_vars, inputs, outputs, RX_MM){
			@Override
			public boolean isEnabled(){
				if(_inputs.get("AUDIO_MM_OP_COMM").equals(MissionManager.AUDIO_MM_OP_COMM.MM_POKE_OP)){
					this.setTempOutput("AUDIO_OP_MM_COMM", Operator.AUDIO_OP_MM_COMM.OP_ACK_MM);
					return true;
				}
				return false;
			};
		});
		//(IDLE,[],[TAKE_OFF])x(LAUNCH_UAV,[OP_TAKE_OFF_OGUI],[])
		//(IDLE,[UAV_FLYING_NORMAL],[])x(OBSERVING_GUI,[],[])
		//(IDLE,[UAV_FLYING_FLYBY],[])x(OBSERVING_GUI,[],[])
		
		add(IDLE);
	}
	
	/**
	 * (POST_FLIGHT,[],[])x(POST_FLIGHT_COMPLETE,[OP_POST_FLIGHT_COMPLETE_UAV,[])
	 */
	private void initializePOST_FLIGHT(ComChannelList inputs, ComChannelList outputs, State POST_FLIGHT, State POST_FLIGHT_COMPLETE){
		//(POST_FLIGHT,[],[])x(POST_FLIGHT_COMPLETE,[OP_POST_FLIGHT_COMPLETE_UAV,[])
		
		add(POST_FLIGHT);
	}
	
	/**
	 * (POST_FLIGHT_COMPLETE,[],[])x(IDLE,[],[])
	 */
	private void initializePOST_FLIGHT_COMPLETE(ComChannelList inputs, ComChannelList outputs, State POST_FLIGHT_COMPLETE, State IDLE){
		//(POST_FLIGHT_COMPLETE,[],[])x(IDLE,[],[])
		
		add(POST_FLIGHT_COMPLETE);
	}

	/**
	 * 
	 */
	private void initializeLAUNCH_UAV(ComChannelList inputs, ComChannelList outputs, State LAUNCH_UAV) {
		// TODO Auto-generated method stub
		
		add(LAUNCH_UAV);
	}

	/**
	 * (OBSERVE_GUI,[OGUI_FLYBY_REQ_F_OP],[])x(POKE_OGUI,[OP_POKE_OGUI],[])
	 * (OBSERVE_GUI,[OGUI_FLYBY_REQ_T_OP],[])x(POKE_OGUI,[OP_POKE_OGUI],[])
	 * (OBSERVE_GUI,[OGUI_LANDED_OP],[]),x(POST_FLIGHT,[],[])
	 * (OBSERVE_GUI,[],[])x(OBSERVE_UAV,[],[])
	 */
	private void initializeOBSERVE_GUI(ComChannelList inputs, ComChannelList outputs, State OBSERVE_GUI, State POKE_OGUI, State POST_FLIGHT, State OBSERVE_UAV) {
		//(OBSERVE_GUI,[OGUI_FLYBY_REQ_F_OP],[])x(POKE_OGUI,[OP_POKE_OGUI],[])
		//(OBSERVE_GUI,[OGUI_FLYBY_REQ_T_OP],[])x(POKE_OGUI,[OP_POKE_OGUI],[])
		//(OBSERVE_GUI,[OGUI_LANDED_OP],[]),x(POST_FLIGHT,[],[])
		//(OBSERVE_GUI,[],[])x(OBSERVE_UAV,[],[])
		
		add(OBSERVE_GUI);
	}

	/**
	 * (OBSERVE_UAV,[UAV_LANDED],[])x(POST_FLIGHT,[],[])
	 * (OBSERVE_UAV,[],[])x(OBSERVE_GUI,[],[])
	 */
	private void initializeOBSERVE_UAV(ComChannelList inputs, ComChannelList outputs, State OBSERVE_UAV, State POST_FLIGHT, State OBSERVE_GUI) {
		//(OBSERVE_UAV,[UAV_LANDED],[])x(POST_FLIGHT,[],[])
		//(OBSERVE_UAV,[],[])x(OBSERVE_GUI,[],[])
		
		add(OBSERVE_UAV);
	}

	/**
	 * 
	 */
	private void initializePOKE_MM(ComChannelList inputs, ComChannelList outputs, State POKE_MM) {
		// TODO Auto-generated method stub
		
		add(POKE_MM);
	}

	/**
	 * 
	 */
	private void initializeTX_MM(ComChannelList inputs, ComChannelList outputs, State TX_MM) {
		// TODO Auto-generated method stub
		
		add(TX_MM);
	}

	/**
	 * 
	 */
	private void initializeEND_MM(ComChannelList inputs, ComChannelList outputs, State END_MM) {
		// TODO Auto-generated method stub
		
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
		//(RX_MM,[MM_END_OP,MM_NEW_SEARCH_AOI],[])x(IDLE,[],[NEW_SEARCH_AOI])
		//(RX_MM,[MM_END_OP,MM_TERMINATE_SEARCH_AOI],[])x(IDLE,[],[TERMINATE_SEARCH_AOI])
		RX_MM.add(new Transition(_internal_vars, inputs, outputs, IDLE){
			@Override
			public boolean isEnabled(){
				if(_inputs.get("AUDIO_MM_OP_COMM").equals(MissionManager.AUDIO_MM_OP_COMM.MM_END_OP)){
					if(_inputs.get("AUDIO_MM_OP_COMM").equals(MissionManager.AUDIO_MM_OP_COMM.MM_NEW_SEARCH_AOI)){
						this.setTempInternalVar("SEARCH_AOI", (Integer)_internal_vars.getVariable("SEARCH_AOI")+1);
					}
					if(_inputs.get("AUDIO_MM_OP_COMM").equals(MissionManager.AUDIO_MM_OP_COMM.MM_TERMINATE_SEARCH)){
						this.setTempInternalVar("TERMINATE_SEARCH_AOI", (Integer)_internal_vars.getVariable("TERMINATE_SEARCH_AOI")+1);
					}
					return true;
				}
				return false;
			}
		});
		//(RX_MM,[],[])x(IDLE,[],[])
		//(RX_MM,[MM_END_OP],[])x(IDLE,[],[])
		//(RX_MM,[MM_END_OP, MM_NEW_SEARCH_AOI],[])x(POKE_OGUI,[OP_POKE_OGUI],[NEW_SEARCH_AOI])
		
		add(RX_MM);
	}

	/**
	 * 
	 */
	private void initializeRX_VO(ComChannelList inputs, ComChannelList outputs, State RX_VO) {
		// TODO Auto-generated method stub
		
		add(RX_VO);
	}

	/**
	 * 
	 */
	private void initializeOBSERVE_FLYBY(ComChannelList inputs, ComChannelList outputs, State OBSERVE_FLYBY) {
		// TODO Auto-generated method stub
		
		add(OBSERVE_FLYBY);
	}

	/**
	 * 
	 */
	private void initializePOKE_OGUI(ComChannelList inputs, ComChannelList outputs, State POKE_OGUI) {
		// TODO Auto-generated method stub
		
		add(POKE_OGUI);
	}

	/**
	 * (RX_MM,[MM_END_OP, MM_NEW_SEARCH_AOI],[])x(POKE_OGUI,[OP_POKE_OGUI],[NEW_SEARCH_AOI])
	 */
	private void initializeTX_OGUI(ComChannelList inputs, ComChannelList outputs, State TX_OGUI, State END_OGUI) {
		//(TX_OGUI,[],[NEW_SEARCH_AOI])x(END_OGUI,[OP_END_OGUI,OP_TAKE_OFF_OGUI],[])
		
		add(TX_OGUI);
	}

	/**
	 * 
	 */
	private void initializeEND_GUI(ComChannelList inputs, ComChannelList outputs, State END_OGUI) {
		// TODO Auto-generated method stub
		
		add(END_OGUI);
	}

	@Override
	protected void initializeInternalVariables() {
		// TODO Auto-generated method stub
		
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
