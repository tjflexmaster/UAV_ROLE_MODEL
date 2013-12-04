package model.actors;

import java.util.HashMap;

import simulator.*;

public class VideoOperator extends Actor {

	public enum AUDIO_VO_MM_COMM {
		VO_POKE_MM,
		VO_ACK_MM,
		VO_END_MM,
		VO_TARGET_SIGHTED_F_MM,
		VO_TARGET_SIGHTED_T_MM
	}

	public enum VISUAL_VO_VGUI_COMM {
		VO_POKE_VGUI,
		VO_ACK_VGUI,
		VO_END_VGUI,
		VO_FLYBY_END_FAILED_VGUI,
		VO_FLYBY_END_SUCCESS_VGUI,
		VO_FLYBY_REQ_F_VGUI,
		VO_FLYBY_REQ_T_VGUI,
		VO_POSSIBLE_ANOMALY_DETECTED_F_VGUI,
		VO_POSSIBLE_ANOMALY_DETECTED_T_VGUI
	}

	public enum AUDIO_VO_OP_COMM {
		VO_POKE_OP,
		VO_ACK_OP,
		VO_END_OP,
		VO_BAD_STREAM_OP
	}

	public VideoOperator(ComChannelList inputs, ComChannelList outputs) {
		//initialize name
		setName("VIDEO_OPERATOR");

		//initialize states
		State IDLE = new State("IDLE");
		//comm with mission manager
		State RX_MM = new State("RX_MM");
		State POKE_MM = new State("POKE_MM");
		State TX_MM = new State("TX_MM");
		State END_MM = new State("END_MM");
		//comm with operator
		State POKE_OP = new State("POKE_OP");
		State TX_OP = new State("TX_OP");
		State END_OP = new State("END_OP");
		//comm with video gui
		State OBSERVE_NORMAL = new State("OBSERVE_NORMAL");
		State OBSERVE_FLYBY = new State("OBSERVE_FLYBY");
		State POKE_GUI = new State("POKE_GUI");
		State TX_GUI = new State("TX_GUI");
		State END_GUI = new State("END_GUI");

		//initialize transitions
		initializeIDLE(inputs, outputs, IDLE, RX_MM, OBSERVE_NORMAL);
		//comm with mission manager
		initializeRX_MM(inputs, outputs, IDLE, RX_MM, OBSERVE_NORMAL);
		initializePOKE_MM(inputs, outputs, POKE_MM, TX_MM, IDLE);
		initializeTX_MM(inputs, outputs, TX_MM, END_MM);
		initializeEND_MM(inputs, outputs, IDLE, END_MM);
		//comm with operator
		initializePOKE_OP(inputs, outputs, POKE_OP, TX_OP);
		initializeTX_OP(inputs, outputs, TX_OP, END_OP);
		initializeEND_OP(inputs, outputs, IDLE, END_OP);
		//comm with video gui
		initializeOBSERVE_NORMAL(inputs, outputs, RX_MM, OBSERVE_NORMAL, POKE_GUI, POKE_OP);
		initializeOBSERVE_FLYBY(inputs, outputs, RX_MM, OBSERVE_NORMAL,OBSERVE_FLYBY, POKE_MM, POKE_OP);
		initializePOKE_GUI(inputs,outputs,POKE_GUI, TX_GUI);
		initializeTX_GUI(inputs,outputs,TX_GUI, END_GUI);
		initializeEND_GUI(inputs,outputs, IDLE, END_GUI);
		
		//initialize current state
		startState(IDLE);
	}

	/**
	 * This method assists the constructor initialize the IDLE state.<br><br>
	 * (IDLE,[MM_POKE_VO],[])x(RX_MM,[VO_ACK_MM],[])<br>
	 * (IDLE,[],[TARGET_DESCRIPTION])x(OBSERVE_NORMAL,[],[TARGET_DESCRIPTION])<br>
	 */
	private void initializeIDLE(ComChannelList inputs, ComChannelList outputs, State IDLE, State RX_MM, State OBSERVE_NORMAL) {
		//(IDLE,[MM_POKE_VO],[])x(RX_MM,[VO_ACK_MM],[])
		IDLE.add(new Transition(_internal_vars, inputs, outputs, RX_MM){
			@Override
			public boolean isEnabled(){
				if(_inputs.get("AUDIO_MM_VO_COMM").equals(MissionManager.AUDIO_MM_VO_COMM.MM_POKE_VO)){
					this.setTempOutput("AUDIO_VO_MM_COMM", VideoOperator.AUDIO_VO_MM_COMM.VO_ACK_MM);
					return true;
				}
				return false;
			}
		});
		//(IDLE,[],[TARGET_DESCRIPTION])x(OBSERVE_NORMAL,[],[TARGET_DESCRIPTION])
		IDLE.add(new Transition(_internal_vars, inputs, outputs, OBSERVE_NORMAL){
			@Override
			public boolean isEnabled(){
				if((Boolean)_internal_vars.getVariable("TARGET_DESCRIPTION")){
					return true;
				}
				return false;
			}
		});
		
		add(IDLE);
	}

	/**
	 * This method assists the constructor initialize the RX_MM state.<br><br>
	 * (RX_MM,[MM_END_VO],[])x(IDLE,[],[])<br>
	 * (RX_MM,[MM_END_VO,MM_TERMINATE_SEARCH],[])x(IDLE,[],[])<br>
	 * (RX_MM,[MM_END_VO, MM_TARGET_DESCRIPTION],[])x(OBSERVE_NORMAL,[],[TARGET_DESCRIPTION])<br>
	 * (RX_MM,[],[])x(IDLE,[],[])<br>
	 * (RX_MM,[MM_END_VO,MM_TARGET_DESCRIPTION],[])x(IDLE,[],[TARGET_DESCRIPTION])<br>
	 */
	private void initializeRX_MM(ComChannelList inputs, ComChannelList outputs, State IDLE, State RX_MM, State OBSERVE_NORMAL) {
		//(RX_MM,[MM_END_VO],[])x(IDLE,[],[])
		RX_MM.add(new Transition(_internal_vars, inputs, outputs, IDLE){
			@Override
			public boolean isEnabled(){
				if(_inputs.get("AUDIO_MM_VO_COMM").equals(MissionManager.AUDIO_MM_VO_COMM.MM_END_VO)){
					return true;
				}
				return false;
			}
		});
		//(RX_MM,[MM_END_VO,MM_TERMINATE_SEARCH],[])x(IDLE,[],[])
		RX_MM.add(new Transition(_internal_vars, inputs, outputs, IDLE){
			@Override
			public boolean isEnabled(){
				if(_inputs.get("AUDIO_MM_VO_COMM").equals(MissionManager.AUDIO_MM_VO_COMM.MM_TERMINATE_SEARCH_VO)
						&& _inputs.get("AUDIO_MM_VO_COMM").equals(MissionManager.AUDIO_MM_VO_COMM.MM_END_VO) ){
					this.setTempInternalVar("TARGET_DESCRIPTION", false);
					return true;
				}
				return false;
			}
		});
		//(RX_MM,[MM_END_VO, MM_TARGET_DESCRIPTION],[])x(OBSERVE_NORMAL,[],[TARGET_DESCRIPTION])
		RX_MM.add(new Transition(_internal_vars, inputs, outputs, OBSERVE_NORMAL){
			@Override
			public boolean isEnabled(){
				if(_inputs.get("AUDIO_MM_VO_COMM").equals(MissionManager.AUDIO_MM_VO_COMM.MM_END_VO)
						&& _inputs.get("AUDIO_MM_VO_COMM").equals(MissionManager.AUDIO_MM_VO_COMM.MM_TARGET_DESCRIPTION_VO)){
					this.setTempInternalVar("TARGET_DESCRIPTION", true);
					return true;
				}
				return false;
			}
		});
		//(RX_MM,[],[])x(IDLE,[],[])
		RX_MM.add(new Transition(_internal_vars, inputs, outputs, IDLE){
			@Override
			public boolean isEnabled(){
				return true;
			}
		});
		//(RX_MM,[MM_END_VO,MM_TARGET_DESCRIPTION],[])x(IDLE,[],[TARGET_DESCRIPTION])
		RX_MM.add(new Transition(_internal_vars, inputs, outputs, IDLE){
			@Override
			public boolean isEnabled(){
				if(_inputs.get("AUDIO_MM_VO_COMM").equals(MissionManager.AUDIO_MM_VO_COMM.MM_END_VO)
						&& _inputs.get("AUDIO_MM_VO_COMM").equals(MissionManager.AUDIO_MM_VO_COMM.MM_TARGET_DESCRIPTION_VO)){
					this.setTempInternalVar("TARGET_DESCRIPTION", true);
					return true;
				}
				return false;
			}
		});

		add(RX_MM);
	}

	/**
	 * This method assists the constructor initialize the POKE_MM state.<br><br>
	 * (POKE_MM,[MM_ACK_VO],[TARGET_SIGHTED_T])x(TX_MM,[],[TARGET_SIGHTED_T])<br>
	 * (POKE_MM,[MM_ACK_VO],[TARGET_SIGHTED_F])x(TX_MM,[],[TARGET_SIGHTED_F])<br>
	 * (POKE_MM,[],[])x(IDLE,[],[])<br>
	 */
	private void initializePOKE_MM(ComChannelList inputs, ComChannelList outputs, State POKE_MM, State TX_MM, State IDLE) {
		//(POKE_MM,[MM_ACK_VO],[TARGET_SIGHTED_T])x(TX_MM,[],[TARGET_SIGHTED_T])
		POKE_MM.add(new Transition(_internal_vars,inputs,outputs,TX_MM){
			@Override
			public boolean isEnabled(){
				if(_inputs.get("AUDIO_MM_VO_COMM").equals(MissionManager.AUDIO_MM_VO_COMM.MM_ACK_VO)
						&& _internal_vars.getVariable("TARGET_SIGHTED_T").equals(true)){
					this.setTempInternalVar("TARGET_SIGHTED_T", true);
					return true;
				}
				return false;
			}
		});
		//(POKE_MM,[MM_ACK_VO],[TARGET_SIGHTED_F])x(TX_MM,[],[TARGET_SIGHTED_F])
		POKE_MM.add(new Transition(_internal_vars,inputs,outputs,TX_MM){
			@Override
			public boolean isEnabled(){
				if(_inputs.get("AUDIO_MM_VO_COMM").equals(MissionManager.AUDIO_MM_VO_COMM.MM_ACK_VO)
						&& _internal_vars.getVariable("TARGET_SIGHTED_F").equals(true)){
					this.setTempInternalVar("TARGET_SIGHTED_F", true);
					return true;
				}
				return false;
			}
		});
		//(POKE_MM,[],[])x(IDLE,[],[])
		POKE_MM.add(new Transition(_internal_vars,inputs,outputs,IDLE){
			@Override
			public boolean isEnabled(){
				return true;
			}
		});

		add(POKE_MM);
	}

	/**
	 * This method assists the constructor initialize the TX_MM state.<br><br>
	 * (TX_MM,[],[TARGET_SIGHTED_T])x(END_MM,[VO_END_MM,VO_TARGET_SIGHTED_T],[])<br>
	 * (TX_MM,[],[TARGET_SIGHTED_F])x(END_MM,[VO_END_MM,VO_TARGET_SIGHTED_F],[])<br>
	 */
	private void initializeTX_MM(ComChannelList inputs, ComChannelList outputs, State TX_MM, State END_MM) {
		//(TX_MM,[],[TARGET_SIGHTED_T])x(END_MM,[VO_END_MM,VO_TARGET_SIGHTED_T],[])
		TX_MM.add(new Transition(_internal_vars,inputs,outputs,END_MM){
			@Override
			public boolean isEnabled(){
				if((Boolean)_internal_vars.getVariable("TARGET_SIGHTED_T")){
					this.setTempOutput("AUDIO_VO_MM_COMM", VideoOperator.AUDIO_VO_MM_COMM.VO_END_MM);
					this.setTempOutput("AUDIO_VO_MM_COMM", VideoOperator.AUDIO_VO_MM_COMM.VO_TARGET_SIGHTED_T_MM);
					return true;
				}
				return false;
			}
		});
		//(TX_MM,[],[TARGET_SIGHTED_F])x(END_MM,[VO_END_MM,VO_TARGET_SIGHTED_F],[])
		TX_MM.add(new Transition(_internal_vars,inputs,outputs,END_MM){
			@Override
			public boolean isEnabled(){
				if((Boolean)_internal_vars.getVariable("TARGET_SIGHTED_F")){
					this.setTempOutput("AUDIO_VO_MM_COMM", VideoOperator.AUDIO_VO_MM_COMM.VO_END_MM);
					this.setTempOutput("AUDIO_VO_MM_COMM", VideoOperator.AUDIO_VO_MM_COMM.VO_TARGET_SIGHTED_F_MM);
					return true;
				}
				return false;
			}
		});

		add(TX_MM);
	}

	/**
	 * This method assists the constructor initialize the END_MM state.<br><br>
	 * (END_MM,[],[])x(IDLE,[],[])<br>
	 */
	private void initializeEND_MM(ComChannelList inputs, ComChannelList outputs, State IDLE, State END_MM) {
		//(END_MM,[],[])x(IDLE,[],[])
		END_MM.add(new Transition(_internal_vars,inputs,outputs,IDLE){
			@Override
			public boolean isEnabled(){
				return true;
			}
		});

		add(END_MM);
	}

	/**
	 * This method assists the constructor initialize the POKE_OP state.<br><br>
	 * (POKE_OP,[OP_ACK_VO],[BAD_STREAM])x(TX_OP,[],[BAD_STREAM])<br>
	 */
	private void initializePOKE_OP(ComChannelList inputs, ComChannelList outputs, State POKE_OP, State TX_OP) {
		//(POKE_OP,[OP_ACK_VO],[BAD_STREAM])x(TX_OP,[],[BAD_STREAM])
		POKE_OP.add(new Transition(_internal_vars, inputs, outputs, TX_OP){
			@Override
			public boolean isEnabled(){
				if(_inputs.get("AUDIO_OP_VO_COMM").equals(Operator.AUDIO_OP_VO_COMM.OP_ACK_VO)){
					return true;
				}
				return false;
			}
		});

		add(POKE_OP);
	}

	/**
	 * This method assists the constructor initialize the TX_OP state.<br><br>
	 * (TX_OP,[],[BAD_STREAM])x(END_OP,[VO_END_OP,VO_BAD_STREAM],[])<br>
	 */
	private void initializeTX_OP(ComChannelList inputs, ComChannelList outputs, State TX_OP, State END_OP) {
		//(TX_OP,[],[BAD_STREAM])x(END_OP,[VO_END_OP,VO_BAD_STREAM],[])
		TX_OP.add(new Transition(_internal_vars,inputs,outputs,END_OP){
			@Override
			public boolean isEnabled(){
				this.setTempOutput("AUDIO_VO_OP_COMM", VideoOperator.AUDIO_VO_OP_COMM.VO_END_OP);
				if((Boolean)_internal_vars.getVariable("BAD_STREAM")){
					this.setTempOutput("AUDIO_VO_OP_COMM", VideoOperator.AUDIO_VO_OP_COMM.VO_BAD_STREAM_OP);
				}
				return true;
			}
		});

		add(TX_OP);
	}

	/**
	 * This method assists the constructor initialize the END_OP state.<br><br>
	 * (END_OP,[],[])x(IDLE,[],[])<br>
	 */
	private void initializeEND_OP(ComChannelList inputs, ComChannelList outputs, State IDLE, State END_OP) {
		//(END_OP,[],[])x(IDLE,[],[])
		END_OP.add(new Transition(_internal_vars,inputs,outputs,IDLE){
			@Override
			public boolean isEnabled(){
				return true;
			}
		});

		add(END_OP);
	}

	/**
	 * This method assists the constructor initialize the OBSERVE_NORMAL state.<br><br>
	 * (OBSERVE_NORMAL,[],[TARGET_DESCRIPTION])x(OBSERVE_NORMAL,[],[TARGET_DESCRIPTION])<br>
	 * (OBSERVE_NORMAL,[MM_POKE_VO],[])x(RX_MM,[VO_ACK_MM],[])<br>
	 * (OBSERVE_NORMAL,[VGUI_BAD_STREAM_VO],[])x(POKE_OP,[VO_POKE_OP],[BAD_STREAM])<br>
	 * (OBSERVE_NORMAL,[VGUI_FALSE_POSITIVE_VO],[])x(POKE_GUI,[VO_POKE_VGUI],[POSSIBLE_ANOMALY_DETECTED_F])<br>
	 * (OBSERVE_NORMAL,[VGUI_TRUE_POSITIVE_VO],[])x(POKE_GUI,[VO_POKE_VGUI],[POSSIBLE_ANOMALAY_DETECTED_T])<br>
	 * (OBSERVE_NORMAL,[VGUI_FALSE_POSITIVE_VO],[])x(POKE_GUI,[VO_POKE_VGUI],[FLYBY_REQ_F])<br>
	 * (OBSERVE_NORMAL,[VGUI_TRUE_POSITIVE_VO],[])x(POKE_GUI,[VO_POKE_VGUI],[FLYBY_REQ_T])<br>
	 */
	private void initializeOBSERVE_NORMAL(ComChannelList inputs, ComChannelList outputs, State RX_MM, State OBSERVE_NORMAL, State POKE_GUI, State POKE_OP) {
		//(OBSERVE_NORMAL,[],[TARGET_DESCRIPTION])x(OBSERVE_NORMAL,[],[TARGET_DESCRIPTION])
		OBSERVE_NORMAL.add(new Transition(_internal_vars,inputs,outputs,OBSERVE_NORMAL){
			@Override
			public boolean isEnabled(){
				if(this._internal_vars.equals("TARGET_DESCRIPTION")){
					return true;
				}
				return false;
			}
		});
		//(OBSERVE_NORMAL,[MM_POKE_VO],[])x(RX_MM,[VO_ACK_MM],[])
		OBSERVE_NORMAL.add(new Transition(_internal_vars,inputs,outputs,RX_MM){
			@Override
			public boolean isEnabled(){
				if(this._inputs.get("AUDIO_MM_VO_COMM").equals(MissionManager.AUDIO_MM_VO_COMM.MM_POKE_VO)){
					this.setTempOutput("AUDIO_VO_MM_COMM", VideoOperator.AUDIO_VO_MM_COMM.VO_ACK_MM);
					return true;
				}
				return false;
			}
		});
		//(OBSERVE_NORMAL,[VGUI_BAD_STREAM_VO],[])x(POKE_OP,[VO_POKE_OP],[BAD_STREAM])
		OBSERVE_NORMAL.add(new Transition(_internal_vars,inputs,outputs,POKE_OP){
			@Override
			public boolean isEnabled(){
				if(this._inputs.get("VISUAL_VGUI_VO_COMM").equals(VideoOperatorGui.VGUI_VO_COMM.VGUI_BAD_STREAM_VO)){
					this.setTempOutput("AUDIO_VO_OP_COMM", VideoOperator.AUDIO_VO_OP_COMM.VO_POKE_OP);
					this.setTempInternalVar("BAD_STREAM", true);
					return true;
				}
				return false;
			}
		});
		//(OBSERVE_NORMAL,[VGUI_FALSE_POSITIVE_VO],[])x(POKE_GUI,[VO_POKE_VGUI],[POSSIBLE_ANOMALY_DETECTED_F])
		OBSERVE_NORMAL.add(new Transition(_internal_vars,inputs,outputs,POKE_GUI){
			@Override
			public boolean isEnabled(){
				if(this._inputs.get("VISUAL_VGUI_VO_COMM").equals(VideoOperatorGui.VGUI_VO_COMM.VGUI_FALSE_POSITIVE_VO)){
					this.setTempOutput("VISUAL_VO_VGUI_COMM", VideoOperator.VISUAL_VO_VGUI_COMM.VO_POKE_VGUI);
					this.setTempInternalVar("POSSIBLE_ANOMALY_DETECTED_F", true);
					return true;
				}
				return false;
			}
		});
		//(OBSERVE_NORMAL,[VGUI_TRUE_POSITIVE_VO],[])x(POKE_GUI,[VO_POKE_VGUI],[POSSIBLE_ANOMALAY_DETECTED_T])
		OBSERVE_NORMAL.add(new Transition(_internal_vars,inputs,outputs,POKE_GUI){
			@Override
			public boolean isEnabled(){
				if(this._inputs.get("VISUAL_VGUI_VO_COMM").equals(VideoOperatorGui.VGUI_VO_COMM.VGUI_TRUE_POSITIVE_VO)){
					this.setTempOutput("VISUAL_VO_VGUI_COMM", VideoOperator.VISUAL_VO_VGUI_COMM.VO_POKE_VGUI);
					this.setTempInternalVar("POSSIBLE_ANOMALY_DETECTED_T", true);
					return true;
				}
				return false;
			}
		});
		//(OBSERVE_NORMAL,[VGUI_FALSE_POSITIVE_VO],[])x(POKE_GUI,[VO_POKE_VGUI],[FLYBY_REQ_F])
		OBSERVE_NORMAL.add(new Transition(_internal_vars,inputs,outputs,POKE_GUI){
			@Override
			public boolean isEnabled(){
				if(this._inputs.get("VISUAL_VGUI_VO_COMM").equals(VideoOperatorGui.VGUI_VO_COMM.VGUI_FALSE_POSITIVE_VO)){
					this.setTempOutput("VISUAL_VO_VGUI_COMM", VideoOperator.VISUAL_VO_VGUI_COMM.VO_POKE_VGUI);
					this.setTempInternalVar("FLYBY_REQ_F", true);
					return true;
				}
				return false;
			}
		});
		//(OBSERVE_NORMAL,[VGUI_TRUE_POSITIVE_VO],[])x(POKE_GUI,[VO_POKE_VGUI],[FLYBY_REQ_T])
		OBSERVE_NORMAL.add(new Transition(_internal_vars,inputs,outputs,POKE_GUI){
			@Override
			public boolean isEnabled(){
				if(this._inputs.get("VISUAL_VGUI_VO_COMM").equals(VideoOperatorGui.VGUI_VO_COMM.VGUI_TRUE_POSITIVE_VO)){
					this.setTempOutput("VISUAL_VO_VGUI_COMM", VideoOperator.VISUAL_VO_VGUI_COMM.VO_POKE_VGUI);
					this.setTempInternalVar("FLYBY_REQ_T", true);
					return true;
				}
				return false;
			}
		});

		add(OBSERVE_NORMAL);
	}

	/**
	 * This method assists the constructor initialize the OBSERVE_FLYBY state.<br><br>
	 * (OBSERVE_FLYBY,[MM_POKE_VO],[])x(RX_MM,[VO_ACK_MM],[])<br>
	 * (OBSERVE_FLYBY,[VGUI_BAD_STREAM_VO],[])x(POKE_OP,[VO_POKE_OP],[BAD_STREAM])<br>
	 * (OBSERVE_FLYBY,[VGUI_FLYBY_ANOMALY_F_VO],[])x(OBSERVE_NORMAL,[VO_FLYBY_END_FAILED_VGUI],[])<br>
	 * (OBSERVE_FLYBY,[VGUI_FLYBY_ANOMALY_T_VO],[])x(OBSERVE_NORMAL,[VO_FLBY_END_FAILED_VGUI],[])<br>
	 * (OBSERVE_FLYBY,[VGUI_FLYBY_ANOMALY_F_VO],[])x(OBSERVE_NORMAL,[VO_FLYBY_END_SUCCESS_VGUI],[])<br>
	 * (OBSERVE_FLYBY,[VGUI_FLYBY_ANOMALY_T_VO],[])x(OBSERVE_NORMAL,[VO_FLBY_END_SUCCESS_VGUI],[])<br>
	 */
	private void initializeOBSERVE_FLYBY(ComChannelList inputs, ComChannelList outputs, State RX_MM, State OBSERVE_NORMAL, State OBSERVE_FLYBY, State POKE_MM, State POKE_OP) {
		//(OBSERVE_FLYBY,[MM_POKE_VO],[])x(RX_MM,[VO_ACK_MM],[])
		OBSERVE_FLYBY.add(new Transition(_internal_vars,inputs,outputs,OBSERVE_NORMAL){
			@Override
			public boolean isEnabled(){
				if(this._inputs.get("AUDIO_MM_VO_COMM").equals(MissionManager.AUDIO_MM_VO_COMM.MM_POKE_VO)){
					this.setTempOutput("AUDIO_VO_MM_COMM", VideoOperator.AUDIO_VO_MM_COMM.VO_ACK_MM);
					return true;
				}
				return false;
			}
		});
		//(OBSERVE_FLYBY,[VGUI_BAD_STREAM_VO],[])x(POKE_OP,[VO_POKE_OP],[BAD_STREAM])
		OBSERVE_FLYBY.add(new Transition(_internal_vars,inputs,outputs,POKE_OP){
			@Override
			public boolean isEnabled(){
				if(this._inputs.get("VISUAL_VGUI_VO_COMM").equals(VideoOperatorGui.VGUI_VO_COMM.VGUI_BAD_STREAM_VO)){
					this.setTempOutput("AUDIO_VO_OP_COMM", VideoOperator.AUDIO_VO_OP_COMM.VO_POKE_OP);
					this.setTempInternalVar("BAD_STREAM", true);
					return true;
				}
				return false;
			}
		});
		//(OBSERVE_FLYBY,[VGUI_FLYBY_ANOMALY_F_VO],[])x(OBSERVE_NORMAL,[VO_FLYBY_END_FAILED_VGUI],[])
		OBSERVE_FLYBY.add(new Transition(_internal_vars,inputs,outputs,OBSERVE_NORMAL){
			@Override
			public boolean isEnabled(){
				if(this._inputs.get("VISUAL_VGUI_VO_COMM").equals(VideoOperatorGui.VGUI_VO_COMM.VGUI_FLYBY_ANOMALY_F_VO)){
					this.setTempOutput("VISUAL_VO_VGUI_COMM", VideoOperator.VISUAL_VO_VGUI_COMM.VO_FLYBY_END_FAILED_VGUI);
					return true;
				}
				return false;
			}
		});
		//(OBSERVE_FLYBY,[VGUI_FLYBY_ANOMALY_T_VO],[])x(OBSERVE_NORMAL,[VO_FLBY_END_FAILED_VGUI],[])
		OBSERVE_FLYBY.add(new Transition(_internal_vars,inputs,outputs,OBSERVE_NORMAL){
			@Override
			public boolean isEnabled(){
				if(this._inputs.get("VISUAL_VGUI_VO_COMM").equals(VideoOperatorGui.VGUI_VO_COMM.VGUI_FLYBY_ANOMALY_T_VO)){
					this.setTempOutput("VISUAL_VO_VGUI_COMM", VideoOperator.VISUAL_VO_VGUI_COMM.VO_FLYBY_END_FAILED_VGUI);
					return true;
				}
				return false;
			}
		});
		//(OBSERVE_FLYBY,[VGUI_FLYBY_ANOMALY_F_VO],[])x(OBSERVE_NORMAL,[VO_FLYBY_END_SUCCESS_VGUI],[])
		OBSERVE_FLYBY.add(new Transition(_internal_vars,inputs,outputs,OBSERVE_NORMAL){
			@Override
			public boolean isEnabled(){
				if(this._inputs.get("VISUAL_VGUI_VO_COMM").equals(VideoOperatorGui.VGUI_VO_COMM.VGUI_FLYBY_ANOMALY_F_VO)){
					this.setTempOutput("VISUAL_VO_VGUI_COMM", VideoOperator.VISUAL_VO_VGUI_COMM.VO_FLYBY_END_SUCCESS_VGUI);
					return true;
				}
				return false;
			}
		});
		//(OBSERVE_FLYBY,[VGUI_FLYBY_ANOMALY_T_VO],[])x(OBSERVE_NORMAL,[VO_FLBY_END_SUCCESS_VGUI],[])
		OBSERVE_FLYBY.add(new Transition(_internal_vars,inputs,outputs,OBSERVE_NORMAL){
			@Override
			public boolean isEnabled(){
				if(this._inputs.get("VISUAL_VGUI_VO_COMM").equals(VideoOperatorGui.VGUI_VO_COMM.VGUI_FLYBY_ANOMALY_T_VO)){
					this.setTempOutput("VISUAL_VO_VGUI_COMM", VideoOperator.VISUAL_VO_VGUI_COMM.VO_FLYBY_END_SUCCESS_VGUI);
					return true;
				}
				return false;
			}
		});

		add(OBSERVE_FLYBY);
	}

	/**
	 * This method assists the constructor initialize the POKE_GUI state.<br><br>
	 * (POKE_GUI,[],[FLYBY_REQ_F])x(TX_GUI,[],[FLYBY_REQ_F])<br>
	 * (POKE_GUI,[],[FLYBY_REQ_T])x)TX_GUI,[],[FLYBY_REQ_T])<br>
	 * (POKE_GUI,[],[POSSIBLE_ANOMALY_DETECTED_F])x(TX_GUI,[],[POSSIBLE_ANOMALY_DETECTED_F])<br>
	 * (POKE_GUI,[],[POSSIBLE_ANOMALY_DETECTED_T])x(TX_GUI,[],[POSSIBLE_ANOMALY_DETECTED_T])<br>
	 */
	private void initializePOKE_GUI(ComChannelList inputs, ComChannelList outputs, State POKE_GUI, State TX_GUI) {
		//(POKE_GUI,[],[FLYBY_REQ_F])x(TX_GUI,[],[FLYBY_REQ_F])
		POKE_GUI.add(new Transition(_internal_vars,inputs,outputs,TX_GUI){
			@Override
			public boolean isEnabled(){
				if(this._internal_vars.getVariable("FLYBY_REQ_F").equals(true)){
					this.setTempInternalVar("FLYBY_REQ_F", true);
					return true;
				}
				return false;
			}
		});
		//(POKE_GUI,[],[FLYBY_REQ_T])x)TX_GUI,[],[FLYBY_REQ_T])
		POKE_GUI.add(new Transition(_internal_vars,inputs,outputs,TX_GUI){
			@Override
			public boolean isEnabled(){
				if(this._internal_vars.getVariable("FLYBY_REQ_T").equals(true)){
					this.setTempInternalVar("FLYBY_REQ_T", true);
					return true;
				}
				return false;
			}
		});
		//(POKE_GUI,[],[POSSIBLE_ANOMALY_DETECTED_F])x(TX_GUI,[],[POSSIBLE_ANOMALY_DETECTED_F])
		POKE_GUI.add(new Transition(_internal_vars,inputs,outputs,TX_GUI){
			@Override
			public boolean isEnabled(){
				if(this._internal_vars.getVariable("POSSIBLE_ANOMALY_DETECTED_F").equals(true)){
					this.setTempInternalVar("POSSIBLE_ANOMALY_DETECTED_F", true);
					return true;
				}
				return false;
			}
		});
		//(POKE_GUI,[],[POSSIBLE_ANOMALY_DETECTED_T])x(TX_GUI,[],[POSSIBLE_ANOMALY_DETECTED_T])
		POKE_GUI.add(new Transition(_internal_vars,inputs,outputs,TX_GUI){
			@Override
			public boolean isEnabled(){
				if(this._internal_vars.getVariable("POSSIBLE_ANOMALY_DETECTED_T").equals(true)){
					this.setTempInternalVar("POSSIBLE_ANOMALY_DETECTED_T", true);
					return true;
				}
				return false;
			}
		});

		add(POKE_GUI);
	}

	/**
	 * This method assists the constructor initialize the TX_GUI state.<br><br>
	 * (TX_GUI,[],[FLYBY_REQ_F])x(END_GUI,[VO_END_VGUI,VO_FLYBY_REQ_F_VGUI],[])<br>
	 * (TX_GUI,[],[FLYBY_REQ_T])x(END_GUI,[VO_END_VGUI,VO_FLYBY_REQ_T_VGUI],[])<br>
	 * (TX_GUI,[],[POSSIBLE_ANOMALY_DETECTED_F])x(END_GUI,[VO_END_VGUI,VO_POSSIBLE_ANOMALY_DETECTED_F_VGUI],[])<br>
	 * (TX_GUI,[],[POSSIBLE_ANOMALY_DETECTED_T])x(END_GUI,[VO_END_VGUI,VO_POSSIBLE_ANOMALY_DETECTED_T_VGUI],[])<br>
	 */
	private void initializeTX_GUI(ComChannelList inputs, ComChannelList outputs, State TX_GUI, State END_GUI) {
		//(TX_GUI,[],[FLYBY_REQ_F])x(END_GUI,[VO_END_VGUI,VO_FLYBY_REQ_F_VGUI],[])
		TX_GUI.add(new Transition(_internal_vars,inputs,outputs,END_GUI){
			@Override
			public boolean isEnabled(){
				if(this._internal_vars.getVariable("FLYBY_REQ_F").equals(true)){
					this.setTempOutput("VISUAL_VO_VGUI_COMM", VideoOperator.VISUAL_VO_VGUI_COMM.VO_END_VGUI);
					this.setTempOutput("VISUAL_VO_VGUI_COMM", VideoOperator.VISUAL_VO_VGUI_COMM.VO_FLYBY_REQ_F_VGUI);
				}
				return false;
			}
		});
		//(TX_GUI,[],[FLYBY_REQ_T])x(END_GUI,[VO_END_VGUI,VO_FLYBY_REQ_T_VGUI],[])
		TX_GUI.add(new Transition(_internal_vars,inputs,outputs,END_GUI){
			@Override
			public boolean isEnabled(){
				if(this._internal_vars.getVariable("FLYBY_REQ_T").equals(true)){
					this.setTempOutput("VISUAL_VO_VGUI_COMM", VideoOperator.VISUAL_VO_VGUI_COMM.VO_END_VGUI);
					this.setTempOutput("VISUAL_VO_VGUI_COMM", VideoOperator.VISUAL_VO_VGUI_COMM.VO_FLYBY_REQ_F_VGUI);
				}
				return false;
			}
		});
		//(TX_GUI,[],[POSSIBLE_ANOMALY_DETECTED_F])x(END_GUI,[VO_END_VGUI,VO_POSSIBLE_ANOMALY_DETECTED_F_VGUI],[])
		TX_GUI.add(new Transition(_internal_vars,inputs,outputs,END_GUI){
			@Override
			public boolean isEnabled(){
				if(this._internal_vars.getVariable("POSSIBLE_ANOMALY_DETECTED_F").equals(true)){
					this.setTempOutput("VISUAL_VO_VGUI_COMM", VideoOperator.VISUAL_VO_VGUI_COMM.VO_END_VGUI);
					this.setTempOutput("VISUAL_VO_VGUI_COMM", VideoOperator.VISUAL_VO_VGUI_COMM.VO_POSSIBLE_ANOMALY_DETECTED_F_VGUI);
				}
				return false;
			}
		});
		//(TX_GUI,[],[POSSIBLE_ANOMALY_DETECTED_T])x(END_GUI,[VO_END_VGUI,VO_POSSIBLE_ANOMALY_DETECTED_T_VGUI],[])
		TX_GUI.add(new Transition(_internal_vars,inputs,outputs,END_GUI){
			@Override
			public boolean isEnabled(){
				if(this._internal_vars.getVariable("POSSIBLE_ANOMALY_DETECTED_T").equals(true)){
					this.setTempOutput("VISUAL_VO_VGUI_COMM", VideoOperator.VISUAL_VO_VGUI_COMM.VO_END_VGUI);
					this.setTempOutput("VISUAL_VO_VGUI_COMM", VideoOperator.VISUAL_VO_VGUI_COMM.VO_POSSIBLE_ANOMALY_DETECTED_T_VGUI);
				}
				return false;
			}
		});

		add(TX_GUI);
	}

	/**
	 * This method assists the constructor initialize the END_GUI state.<br><br>
	 * (END_GUI,[],[])x(IDLE,[],[])<br>
	 */
	private void initializeEND_GUI(ComChannelList inputs, ComChannelList outputs, State IDLE, State END_GUI) {
		//(END_GUI,[],[])x(IDLE,[],[])
		END_GUI.add(new Transition(_internal_vars, inputs, outputs, IDLE){
			@Override
			public boolean isEnabled(){
				return true;
			}
		});

		add(END_GUI);
	}

	@Override
	protected void initializeInternalVariables() {
		// TODO Auto-generated method stub
		this._internal_vars.addVariable("POSSIBLE_ANOMALY_DETECTED_T", false);
		this._internal_vars.addVariable("POSSIBLE_ANOMALY_DETECTED_F", false);
		this._internal_vars.addVariable("FLYBY_REQ_T", false);
		this._internal_vars.addVariable("FLYBY_REQ_F", false);
		this._internal_vars.addVariable("BAD_STREAM", false);
		this._internal_vars.addVariable("TARGET_DESCRIPTION", false);
		this._internal_vars.addVariable("TARGET_SIGHTED_T", false);
		this._internal_vars.addVariable("TARGET_SIGHTED_F", false);
	}

	@Override
	public HashMap<IActor, ITransition> getTransitions() {
		// TODO Auto-generated method stub
		return null;
	}
	
}