package model.actors;


import model.team.Channels;
import model.team.Duration;
import simulator.Actor;
import simulator.ComChannelList;
import simulator.State;
import simulator.Transition;

public class MissionCompletionWatcher extends Actor {
public MissionCompletionWatcher(ComChannelList inputs, ComChannelList outputs) {
	
	State BAD_STREAM_3 = new State("BAD_STREAM_3");
	State BAD_STREAM_2 = new State("BAD_STREAM_2");
	State BAD_STREAM_1 = new State("BAD_STREAM_1");
	State BAD_STREAM = new State("BAD_STREAM");
	State PATH_COMPLETE_1 = new State("PATH_COMPLETE_1");
	State PATH_COMPLETION = new State("PATH_COMPLETION");
	State FLYBY = new State("FLYBY");
	State TARGET_SIGHTED_F = new State("TARGET_SIGHTED_F");
	State VALIDATE = new State("VALIDATE");
	State SUCCESS = new State("SUCCESS");
	State ENDING_SEQUENCE_3 = new State("ENDING_SEQUENCE_3");
	State ENDING_SEQUENCE_2 = new State("ENDING_SEQUENCE_2");
	State ENDING_SEQUENCE_1 = new State("ENDING_SEQUENCE_1");
	State ENDING_SEQUENCE = new State("ENDING_SEQUENCE");
	State REPORT_PS = new State("REPORT_PS");
	State RECOGNIZED = new State("RECOGNIZED");
	State TARGET_SIGHTED_T = new State("TARGET_SIGHTED_T");
	State TARGET_SIGHTED = new State("TARGET_SIGHTED");
	State WAITING = new State("WAITING");
	State WAITING_FOR_LAUNCH = new State("WAITING_FOR_LAUNCH");
	State INFORM_MM = new State("INFORM_MM");
	State WAITING_FOR_START = new State("WAITING_FOR_START");
	_internal_vars.setVariable("currentState", WAITING_FOR_START);
	_internal_vars.setVariable("name", "MissionCompletionWatcher");
	initializeInternalVariables();
	initializeINFORM_MM(inputs, outputs, WAITING_FOR_LAUNCH, INFORM_MM);
	initializeENDING_SEQUENCE(inputs, outputs, ENDING_SEQUENCE, ENDING_SEQUENCE_1);
	initializeWAITING_FOR_LAUNCH(inputs, outputs, WAITING_FOR_LAUNCH, WAITING);
	initializeTARGET_SIGHTED_T(inputs, outputs, FLYBY, VALIDATE, TARGET_SIGHTED_T, RECOGNIZED);
	initializeBAD_STREAM(inputs, outputs, BAD_STREAM, BAD_STREAM_1);
	initializeRECOGNIZED(inputs, outputs, RECOGNIZED, REPORT_PS);
	initializeVALIDATE(inputs, outputs, FLYBY, VALIDATE, WAITING);
	initializeBAD_STREAM_3(inputs, outputs, BAD_STREAM_3, SUCCESS);
	initializeBAD_STREAM_2(inputs, outputs, BAD_STREAM_2, BAD_STREAM_3);
	initializeBAD_STREAM_1(inputs, outputs, BAD_STREAM_1, BAD_STREAM_2);
	initializeWAITING_FOR_START(inputs, outputs, WAITING_FOR_START, INFORM_MM);
	initializeFLYBY(inputs, outputs, WAITING, FLYBY, RECOGNIZED);
	initializeWAITING(inputs, outputs, BAD_STREAM, PATH_COMPLETION, WAITING, TARGET_SIGHTED);
	initializePATH_COMPLETE_1(inputs, outputs, PATH_COMPLETE_1, SUCCESS);
	initializeREPORT_PS(inputs, outputs, REPORT_PS, ENDING_SEQUENCE);
	initializeENDING_SEQUENCE_3(inputs, outputs, ENDING_SEQUENCE_3, SUCCESS);
	initializeENDING_SEQUENCE_2(inputs, outputs, ENDING_SEQUENCE_2, ENDING_SEQUENCE_3);
	initializeENDING_SEQUENCE_1(inputs, outputs, ENDING_SEQUENCE_1, ENDING_SEQUENCE_2);
	initializePATH_COMPLETION(inputs, outputs, PATH_COMPLETION, PATH_COMPLETE_1);
	initializeTARGET_SIGHTED_F(inputs, outputs, FLYBY, TARGET_SIGHTED_F, VALIDATE);
	
}
 public void initializeINFORM_MM(ComChannelList inputs, ComChannelList outputs, State WAITING_FOR_LAUNCH, State INFORM_MM) {
	// (INFORM_MM,[A=PS_NEW_SEARCH_AOI_MM],[],0,NEXT,1.0)X(INFORM_MM,[],[AREA_OF_INTEREST=TRUE])
	INFORM_MM.add(new Transition(_internal_vars, inputs, outputs, INFORM_MM, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!ParentSearch.AUDIO_PS_MM_COMM.PS_NEW_SEARCH_AOI_MM.equals(_inputs.get(Channels.AUDIO_PS_MM_COMM.name()).value())) {
				return false;
			}
			setTempInternalVar("AREA_OF_INTEREST", true);
			return true;
		}
	});
	// (INFORM_MM,[A=PS_TARGET_DESCRIPTION_MM],[],0,NEXT,1.0)X(INFORM_MM,[],[TARGET_DESCRIPTION=TRUE])
	INFORM_MM.add(new Transition(_internal_vars, inputs, outputs, INFORM_MM, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!ParentSearch.AUDIO_PS_MM_COMM.PS_TARGET_DESCRIPTION_MM.equals(_inputs.get(Channels.AUDIO_PS_MM_COMM.name()).value())) {
				return false;
			}
			setTempInternalVar("TARGET_DESCRIPTION", true);
			return true;
		}
	});
	// (INFORM_MM,[A=MM_NEW_SEARCH_AOI_OP],[],0,NEXT,1.0)X(INFORM_MM,[],[OP_INFORMED=TRUE])
	INFORM_MM.add(new Transition(_internal_vars, inputs, outputs, INFORM_MM, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!MissionManager.AUDIO_MM_OP_COMM.MM_NEW_SEARCH_AOI_OP.equals(_inputs.get(Channels.AUDIO_MM_OP_COMM.name()).value())) {
				return false;
			}
			setTempInternalVar("OP_INFORMED", true);
			return true;
		}
	});
	// (INFORM_MM,[A=MM_TARGET_DESCRIPTION_VO],[],0,NEXT,1.0)X(INFORM_MM,[],[VO_INFORMED=TRUE])
	INFORM_MM.add(new Transition(_internal_vars, inputs, outputs, INFORM_MM, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!MissionManager.AUDIO_MM_VO_COMM.MM_TARGET_DESCRIPTION_VO.equals(_inputs.get(Channels.AUDIO_MM_VO_COMM.name()).value())) {
				return false;
			}
			setTempInternalVar("VO_INFORMED", true);
			return true;
		}
	});
	// (INFORM_MM,[],[AREA_OF_INTEREST=TRUE,TARGET_DESCRIPTION=TRUE],0,NEXT,1.0)X(WAITING_FOR_LAUNCH,[],[])
	INFORM_MM.add(new Transition(_internal_vars, inputs, outputs, WAITING_FOR_LAUNCH, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("AREA_OF_INTEREST"))) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("TARGET_DESCRIPTION"))) {
				return false;
			}
			return true;
		}
	});
	add(INFORM_MM);
}
 public void initializeENDING_SEQUENCE(ComChannelList inputs, ComChannelList outputs, State ENDING_SEQUENCE, State ENDING_SEQUENCE_1) {
	// (ENDING_SEQUENCE,[A=MM_TERMINATE_SEARCH_OP],[],0,NEXT,1.0)X(ENDING_SEQUENCE_1,[],[])
	ENDING_SEQUENCE.add(new Transition(_internal_vars, inputs, outputs, ENDING_SEQUENCE_1, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!MissionManager.AUDIO_MM_OP_COMM.MM_TERMINATE_SEARCH_OP.equals(_inputs.get(Channels.AUDIO_MM_OP_COMM.name()).value())) {
				return false;
			}
			return true;
		}
	});
	add(ENDING_SEQUENCE);
}
 public void initializeWAITING_FOR_LAUNCH(ComChannelList inputs, ComChannelList outputs, State WAITING_FOR_LAUNCH, State WAITING) {
	// (WAITING_FOR_LAUNCH,[A=MM_NEW_SEARCH_AOI_OP],[],0,NEXT,1.0)X(WAITING_FOR_LAUNCH,[],[OP_INFORMED=TRUE])
	WAITING_FOR_LAUNCH.add(new Transition(_internal_vars, inputs, outputs, WAITING_FOR_LAUNCH, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!MissionManager.AUDIO_MM_OP_COMM.MM_NEW_SEARCH_AOI_OP.equals(_inputs.get(Channels.AUDIO_MM_OP_COMM.name()).value())) {
				return false;
			}
			setTempInternalVar("OP_INFORMED", true);
			return true;
		}
	});
	// (WAITING_FOR_LAUNCH,[A=MM_TARGET_DESCRIPTION_VO],[],0,NEXT,1.0)X(WAITING_FOR_LAUNCH,[],[VO_INFORMED=TRUE])
	WAITING_FOR_LAUNCH.add(new Transition(_internal_vars, inputs, outputs, WAITING_FOR_LAUNCH, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!MissionManager.AUDIO_MM_VO_COMM.MM_TARGET_DESCRIPTION_VO.equals(_inputs.get(Channels.AUDIO_MM_VO_COMM.name()).value())) {
				return false;
			}
			setTempInternalVar("VO_INFORMED", true);
			return true;
		}
	});
	// (WAITING_FOR_LAUNCH,[D=OP_TAKE_OFF_OGUI],[],0,NEXT,1.0)X(WAITING_FOR_LAUNCH,[],[LAUNCHED=TRUE])
	WAITING_FOR_LAUNCH.add(new Transition(_internal_vars, inputs, outputs, WAITING_FOR_LAUNCH, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!Operator.VISUAL_OP_UAV_COMM.OP_TAKE_OFF_UAV.equals(_inputs.get(Channels.VISUAL_OP_UAV_COMM.name()).value())) {
				return false;
			}
			setTempInternalVar("LAUNCHED", true);
			return true;
		}
	});
	// (WAITING_FOR_LAUNCH,[D=OP_TAKE_OFF_OGUI],[],0,NEXT,1.0)X(WAITING_FOR_LAUNCH,[],[LAUNCHED=TRUE])
	WAITING_FOR_LAUNCH.add(new Transition(_internal_vars, inputs, outputs, WAITING_FOR_LAUNCH, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!Operator.VISUAL_OP_UAV_COMM.OP_TAKE_OFF_UAV.equals(_inputs.get(Channels.VISUAL_OP_UAV_COMM.name()).value())) {
				return false;
			}
			setTempInternalVar("LAUNCHED", true);
			return true;
		}
	});
	// (WAITING_FOR_LAUNCH,[],[OP_INFORMED=TRUE,VO_INFORMED=TRUE,LAUNCHED=TRUE],0,NEXT,1.0)X(WAITING,[],[])
	WAITING_FOR_LAUNCH.add(new Transition(_internal_vars, inputs, outputs, WAITING, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!new Boolean(true).equals(_internal_vars.getVariable ("OP_INFORMED"))) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("VO_INFORMED"))) {
				return false;
			}
			if(!new Boolean(true).equals(_internal_vars.getVariable ("LAUNCHED"))) {
				return false;
			}
			return true;
		}
	});
	add(WAITING_FOR_LAUNCH);
}
 public void initializeTARGET_SIGHTED_T(ComChannelList inputs, ComChannelList outputs, State FLYBY, State VALIDATE, State TARGET_SIGHTED_T, State RECOGNIZED) {
	// (TARGET_SIGHTED_T,[A=VO_TARGET_FOUND_T_MM],[],0,NEXT,1.0)X(RECOGNIZED,[],[])
	TARGET_SIGHTED_T.add(new Transition(_internal_vars, inputs, outputs, RECOGNIZED, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!VideoOperator.AUDIO_VO_MM_COMM.VO_TARGET_SIGHTED_T.equals(_inputs.get(Channels.AUDIO_VO_MM_COMM.name()).value())) {
				return false;
			}
			return true;
		}
	});
	// (TARGET_SIGHTED_T,[V=VGUI_VALIDATION_REQ_T_MM],[],0,NEXT,1.0)X(VALIDATE,[],[])
	TARGET_SIGHTED_T.add(new Transition(_internal_vars, inputs, outputs, VALIDATE, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!VideoOperatorGui.VIDEO_VGUI_MM_COMM.VGUI_VALIDATION_REQ_T_MM.equals(_inputs.get(Channels.VIDEO_VGUI_MM_COMM.name()).value())) {
				return false;
			}
			return true;
		}
	});
	// (TARGET_SIGHTED_T,[D=VO_FLYBY_REQ_T_VGUI],[],0,NEXT,1.0)X(FLYBY,[],[])
	TARGET_SIGHTED_T.add(new Transition(_internal_vars, inputs, outputs, FLYBY, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!VideoOperator.VISUAL_VO_VGUI_COMM.VO_FLYBY_REQ_T_VGUI.equals(_inputs.get(Channels.VISUAL_VO_VGUI_COMM.name()).value())) {
				return false;
			}
			return true;
		}
	});
	add(TARGET_SIGHTED_T);
}
 public void initializeBAD_STREAM(ComChannelList inputs, ComChannelList outputs, State BAD_STREAM, State BAD_STREAM_1) {
	// (BAD_STREAM,[A=VO_BAD_STREAM_OP],[],0,NEXT,1.0)X(BAD_STREAM_1,[],[])
	BAD_STREAM.add(new Transition(_internal_vars, inputs, outputs, BAD_STREAM_1, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!VideoOperator.AUDIO_VO_OP_COMM.VO_BAD_STREAM.equals(_inputs.get(Channels.AUDIO_VO_OP_COMM.name()).value())) {
				return false;
			}
			return true;
		}
	});
	add(BAD_STREAM);
}
 public void initializeRECOGNIZED(ComChannelList inputs, ComChannelList outputs, State RECOGNIZED, State REPORT_PS) {
	// (RECOGNIZED,[A=MM_TARGET_SIGHTED_T_PS],[],0,NEXT,1.0)X(REPORT_PS,[],[TARGET_SIGHTED=TRUE])
	RECOGNIZED.add(new Transition(_internal_vars, inputs, outputs, REPORT_PS, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!MissionManager.AUDIO_MM_PS_COMM.MM_TARGET_SIGHTED_T_PS.equals(_inputs.get(Channels.AUDIO_MM_PS_COMM.name()).value())) {
				return false;
			}
			setTempInternalVar("TARGET_SIGHTED", true);
			return true;
		}
	});
	add(RECOGNIZED);
}
 public void initializeVALIDATE(ComChannelList inputs, ComChannelList outputs, State FLYBY, State VALIDATE, State WAITING) {
	// (VALIDATE,[D=MM_ANOMALY_DISMISSED_T_VGUI],[],0,NEXT,1.0)X(WAITING,[],[])
	VALIDATE.add(new Transition(_internal_vars, inputs, outputs, WAITING, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!MissionManager.DATA_MM_VGUI_COMM.MM_ANOMALY_DISMISSED_T_VGUI.equals(_inputs.get(Channels.VISUAL_MM_VGUI_COMM.name()).value())) {
				return false;
			}
			return true;
		}
	});
	// (VALIDATE,[D=MM_ANOMALY_DISMISSED_F_VGUI],[],0,NEXT,1.0)X(WAITING,[],[])
	VALIDATE.add(new Transition(_internal_vars, inputs, outputs, WAITING, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!MissionManager.DATA_MM_VGUI_COMM.MM_ANOMALY_DISMISSED_F_VGUI.equals(_inputs.get(Channels.VISUAL_MM_VGUI_COMM.name()).value())) {
				return false;
			}
			return true;
		}
	});
	// (VALIDATE,[D=MM_FLYBY_REQ_F_VGUI],[],0,NEXT,1.0)X(FLYBY,[],[])
	VALIDATE.add(new Transition(_internal_vars, inputs, outputs, FLYBY, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!MissionManager.DATA_MM_VGUI_COMM.MM_FLYBY_REQ_F_VGUI.equals(_inputs.get(Channels.VISUAL_MM_VGUI_COMM.name()).value())) {
				return false;
			}
			return true;
		}
	});
	// (VALIDATE,[D=MM_FLYBY_REQ_T_VGUI],[],0,NEXT,1.0)X(FLYBY,[],[])
	VALIDATE.add(new Transition(_internal_vars, inputs, outputs, FLYBY, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!MissionManager.DATA_MM_VGUI_COMM.MM_FLYBY_REQ_T_VGUI.equals(_inputs.get(Channels.VISUAL_MM_VGUI_COMM.name()).value())) {
				return false;
			}
			return true;
		}
	});
	add(VALIDATE);
}
 public void initializeBAD_STREAM_3(ComChannelList inputs, ComChannelList outputs, State BAD_STREAM_3, State SUCCESS) {
	// (BAD_STREAM_3,[D=OP_POST_FLIGHT_COMPLETE_UAV],[],0,NEXT,1.0)X(SUCCESS,[],[])
	BAD_STREAM_3.add(new Transition(_internal_vars, inputs, outputs, SUCCESS, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!Operator.VISUAL_OP_UAV_COMM.OP_POST_FLIGHT_COMPLETE.equals(_inputs.get(Channels.VISUAL_OP_UAV_COMM.name()).value())) {
				return false;
			}
			return true;
		}
	});
	add(BAD_STREAM_3);
}
 public void initializeBAD_STREAM_2(ComChannelList inputs, ComChannelList outputs, State BAD_STREAM_2, State BAD_STREAM_3) {
	// (BAD_STREAM_2,[A=MM_SEARCH_FAILED_PS],[],0,NEXT,1.0)X(BAD_STREAM_3,[],[])
	BAD_STREAM_2.add(new Transition(_internal_vars, inputs, outputs, BAD_STREAM_3, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!MissionManager.AUDIO_MM_PS_COMM.MM_SEARCH_FAILED_PS.equals(_inputs.get(Channels.AUDIO_MM_PS_COMM.name()).value())) {
				return false;
			}
			return true;
		}
	});
	add(BAD_STREAM_2);
}
 public void initializeBAD_STREAM_1(ComChannelList inputs, ComChannelList outputs, State BAD_STREAM_1, State BAD_STREAM_2) {
	// (BAD_STREAM_1,[A=OP_SEARCH_FAILED_MM],[],0,NEXT,1.0)X(BAD_STREAM_2,[],[])
	BAD_STREAM_1.add(new Transition(_internal_vars, inputs, outputs, BAD_STREAM_2, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!Operator.AUDIO_OP_MM_COMM.OP_SEARCH_FAILED.equals(_inputs.get(Channels.AUDIO_OP_MM_COMM.name()).value())) {
				return false;
			}
			return true;
		}
	});
	add(BAD_STREAM_1);
}
 public void initializeWAITING_FOR_START(ComChannelList inputs, ComChannelList outputs, State WAITING_FOR_START, State INFORM_MM) {
	// (WAITING_FOR_START,[E=NEW_SEARCH_EVENT],[],0,NEXT,1.0)X(INFORM_MM,[],[])
	WAITING_FOR_START.add(new Transition(_internal_vars, inputs, outputs, INFORM_MM, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(_inputs.get(Channels.NEW_SEARCH_EVENT.name()).value() == null && !(Boolean)_inputs.get(Channels.NEW_SEARCH_EVENT.name()).value()) {
				return false;
			}
			return true;
		}
	});
	add(WAITING_FOR_START);
}
 public void initializeFLYBY(ComChannelList inputs, ComChannelList outputs, State WAITING, State FLYBY, State RECOGNIZED) {
	// (FLYBY,[D=VO_FLYBY_END_SUCCESS_VGUI],[],0,NEXT,1.0)X(RECOGNIZED,[],[])
	FLYBY.add(new Transition(_internal_vars, inputs, outputs, RECOGNIZED, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!VideoOperator.VISUAL_VO_VGUI_COMM.VO_FLYBY_END_SUCCESS_VGUI.equals(_inputs.get(Channels.VISUAL_VO_VGUI_COMM.name()).value())) {
				return false;
			}
			return true;
		}
	});
	// (FLYBY,[D=VO_FLYBY_END_FAILED_VGUI],[],0,NEXT,1.0)X(WAITING,[],[])
	FLYBY.add(new Transition(_internal_vars, inputs, outputs, WAITING, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!VideoOperator.VISUAL_VO_VGUI_COMM.VO_FLYBY_END_FAILED_VGUI.equals(_inputs.get(Channels.VISUAL_VO_VGUI_COMM.name()).value())) {
				return false;
			}
			return true;
		}
	});
	add(FLYBY);
}
 public void initializeWAITING(ComChannelList inputs, ComChannelList outputs, State BAD_STREAM, State PATH_COMPLETION, State WAITING, State TARGET_SIGHTED) {
	// (WAITING,[E=TARGET_SIGHTED_TRUE],[],0,NEXT,1.0)X(TARGET_SIGHTED,[],[TARGET_SIGHTED_T=TRUE])
	WAITING.add(new Transition(_internal_vars, inputs, outputs, TARGET_SIGHTED, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(_inputs.get(Channels.TARGET_SIGHTED_T_EVENT.name()).value() == null && !(Boolean)_inputs.get(Channels.TARGET_SIGHTED_T_EVENT.name()).value()) {
				return false;
			}
			setTempInternalVar("TARGET_SIGHTED_T", true);
			return true;
		}
	});
	// (WAITING,[E=TARGET_SIGHTED_FALSE],[],0,NEXT,1.0)X(TARGET_SIGHTED,[],[TARGET_SIGHTED_F=TRUE])
	WAITING.add(new Transition(_internal_vars, inputs, outputs, TARGET_SIGHTED, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(_inputs.get(Channels.TARGET_SIGHTED_F_EVENT.name()).value() == null && !(Boolean)_inputs.get(Channels.TARGET_SIGHTED_F_EVENT.name()).value()) {
				return false;
			}
			setTempInternalVar("TARGET_SIGHTED_F", true);
			return true;
		}
	});
//	// (WAITING,[D=OGUI_SEARCH_COMPLETE_OP],[],0,NEXT,1.0)X(PATH_COMPLETION,[],[])
//	WAITING.add(new Transition(_internal_vars, inputs, outputs, PATH_COMPLETION, Duration.NEXT.getRange(), 0, 1.0) {
//		@Override
//		public boolean isEnabled() { 
//			if(!OperatorGui.VIDEO_OGUI_OP_COMM.OGUI_SEARCH_COMPLETE_OP.equals(_inputs.get(Channels.DATA_OGUI_OP_COMM.name()).value())) {
//				return false;
//			}
//			return true;
//		}
//	});
	// (WAITING,[V=VGUI_BAD_STREAM_VO],[],0,NEXT,1.0)X(BAD_STREAM,[],[])
	WAITING.add(new Transition(_internal_vars, inputs, outputs, BAD_STREAM, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!VideoOperatorGui.VGUI_VO_COMM.VGUI_BAD_STREAM_VO.equals(_inputs.get(Channels.VIDEO_VGUI_VO_COMM.name()).value())) {
				return false;
			}
			return true;
		}
	});
	add(WAITING);
}
 public void initializePATH_COMPLETE_1(ComChannelList inputs, ComChannelList outputs, State PATH_COMPLETE_1, State SUCCESS) {
	// (PATH_COMPLETE_1,[A=MM_SEARCH_COMPLETE_PS],[],0,NEXT,1.0)X(SUCCESS,[],[])
	PATH_COMPLETE_1.add(new Transition(_internal_vars, inputs, outputs, SUCCESS, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!MissionManager.AUDIO_MM_PS_COMM.MM_SEARCH_COMPLETE_PS.equals(_inputs.get(Channels.AUDIO_MM_PS_COMM.name()).value())) {
				return false;
			}
			return true;
		}
	});
	add(PATH_COMPLETE_1);
}
 public void initializeREPORT_PS(ComChannelList inputs, ComChannelList outputs, State REPORT_PS, State ENDING_SEQUENCE) {
	// (REPORT_PS,[A=PS_TERMINATE_SEARCH_MM],[],0,NEXT,1.0)X(ENDING_SEQUENCE,[],[])
	REPORT_PS.add(new Transition(_internal_vars, inputs, outputs, ENDING_SEQUENCE, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!ParentSearch.AUDIO_PS_MM_COMM.PS_TERMINATE_SEARCH_MM.equals(_inputs.get(Channels.AUDIO_PS_MM_COMM.name()).value())) {
				return false;
			}
			return true;
		}
	});
	add(REPORT_PS);
}
 public void initializeENDING_SEQUENCE_3(ComChannelList inputs, ComChannelList outputs, State ENDING_SEQUENCE_3, State SUCCESS) {
	// (ENDING_SEQUENCE_3,[D=OP_POST_FLIGHT_COMPLETE_UAV],[],0,NEXT,1.0)X(SUCCESS,[],[])
	ENDING_SEQUENCE_3.add(new Transition(_internal_vars, inputs, outputs, SUCCESS, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!Operator.VISUAL_OP_UAV_COMM.OP_POST_FLIGHT_COMPLETE.equals(_inputs.get(Channels.VISUAL_OP_UAV_COMM.name()).value())) {
				return false;
			}
			return true;
		}
	});
	add(ENDING_SEQUENCE_3);
}
 public void initializeENDING_SEQUENCE_2(ComChannelList inputs, ComChannelList outputs, State ENDING_SEQUENCE_2, State ENDING_SEQUENCE_3) {
	// (ENDING_SEQUENCE_2,[V=UAV_LANDED_OP],[],0,NEXT,1.0)X(ENDING_SEQUENCE_3,[],[])
	ENDING_SEQUENCE_2.add(new Transition(_internal_vars, inputs, outputs, ENDING_SEQUENCE_3, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.VISUAL_UAV_OP_COMM.LANDED.equals(_inputs.get(Channels.VIDEO_UAV_OP_COMM.name()).value())) {
				return false;
			}
			return true;
		}
	});
	add(ENDING_SEQUENCE_2);
}
 public void initializeENDING_SEQUENCE_1(ComChannelList inputs, ComChannelList outputs, State ENDING_SEQUENCE_1, State ENDING_SEQUENCE_2) {
	// (ENDING_SEQUENCE_1,[D=OP_LAND_OGUI],[],0,NEXT,1.0)X(ENDING_SEQUENCE_2,[],[])
	ENDING_SEQUENCE_1.add(new Transition(_internal_vars, inputs, outputs, ENDING_SEQUENCE_2, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!Operator.VISUAL_OP_OGUI_COMM.OP_LAND_UAV.equals(_inputs.get(Channels.VISUAL_OP_OGUI_COMM.name()).value())) {
				return false;
			}
			return true;
		}
	});
	add(ENDING_SEQUENCE_1);
}
 public void initializePATH_COMPLETION(ComChannelList inputs, ComChannelList outputs, State PATH_COMPLETION, State PATH_COMPLETE_1) {
	// (PATH_COMPLETION,[A=OP_SEARCH_COMPLETE_MM],[],0,NEXT,1.0)X(PATH_COMPLETE_1,[],[])
	PATH_COMPLETION.add(new Transition(_internal_vars, inputs, outputs, PATH_COMPLETE_1, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!Operator.AUDIO_OP_MM_COMM.OP_SEARCH_COMPLETE.equals(_inputs.get(Channels.AUDIO_OP_MM_COMM.name()).value())) {
				return false;
			}
			return true;
		}
	});
	add(PATH_COMPLETION);
}
 public void initializeTARGET_SIGHTED_F(ComChannelList inputs, ComChannelList outputs, State FLYBY, State TARGET_SIGHTED_F, State VALIDATE) {
	// (TARGET_SIGHTED_F,[V=VGUI_VALIDATION_REQ_F_MM],[],0,NEXT,1.0)X(VALIDATE,[],[])
	TARGET_SIGHTED_F.add(new Transition(_internal_vars, inputs, outputs, VALIDATE, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!VideoOperatorGui.VIDEO_VGUI_MM_COMM.VGUI_VALIDATION_REQ_F_MM.equals(_inputs.get(Channels.VIDEO_VGUI_MM_COMM.name()).value())) {
				return false;
			}
			return true;
		}
	});
	// (TARGET_SIGHTED_F,[D=VO_FLYBY_REQ_F_VGUI],[],0,NEXT,1.0)X(FLYBY,[],[])
	TARGET_SIGHTED_F.add(new Transition(_internal_vars, inputs, outputs, FLYBY, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!VideoOperator.VISUAL_VO_VGUI_COMM.VO_FLYBY_REQ_F_VGUI.equals(_inputs.get(Channels.VISUAL_VO_VGUI_COMM.name()).value())) {
				return false;
			}
			return true;
		}
	});
	add(TARGET_SIGHTED_F);
}
@Override
protected void initializeInternalVariables() {
	_internal_vars.addVariable("AREA_OF_INTEREST", false);
	_internal_vars.addVariable("TARGET_DESCRIPTION", false);
	_internal_vars.addVariable("OP_INFORMED", false);
	_internal_vars.addVariable("VO_INFORMED", false);
	_internal_vars.addVariable("LAUNCHED", false);
	_internal_vars.addVariable("TARGET_SIGHTED_T", false);
	_internal_vars.addVariable("TARGET_SIGHTED_F", false);
}
}