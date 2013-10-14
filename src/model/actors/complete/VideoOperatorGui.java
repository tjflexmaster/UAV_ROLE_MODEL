package model.actors.complete;

import model.actors.MissionManager;
import model.team.*;
import simulator.*;

public class VideoOperatorGui extends Actor {
public enum VIDEO_VGUI_VO_COMM{
	VGUI_FALSE_POSITIVE_VO,
	VGUI_TRUE_POSITIVE_VO,
	VGUI_FLYBY_ANOMALY_F_VO,
	VGUI_FLYBY_ANOMALY_T_VO,
	VGUI_UAV_READY_VO,
	VGUI_UAV_TAKE_OFF_VO,
	VGUI_UAV_FLYING_NORMA_VO,
	VGUI_UAV_FLYING_FLYBY_VO,
	VGUI_UAV_LOITERING_VO,
	VGUI_UAV_LANDING_VO,
	VGUI_UAV_LANDED_VO,
	VGUI_UAV_CRASHED_VO,
	VGUI_SIGNAL_NONE_VO,
	VGUI_SIGNAL_OK_VO,
	VGUI_SIGNAL_BAD_VO,
}
public enum DATA_VGUI_OGUI_COMM{
	VGUI_FLYBY_REQ_OGUI,
}
public enum AUDIO_VGUI_MM_COMM{
	VGUI_ALERT_MM,
}
public enum VIDEO_VGUI_MM_COMM{
	VGUI_VALIDATION_REQ_F_MM,
	VGUI_VALIDATION_REQ_T_MM,
}
public VideoOperatorGui(ComChannelList inputs, ComChannelList outputs) {
	State STREAMING_FLYBY = new State("STREAMING_FLYBY");
	State STREAMING_NORMAL = new State("STREAMING_NORMAL");
	initializeSTREAMING_NORMAL(inputs, outputs, STREAMING_FLYBY, STREAMING_NORMAL);
	initializeSTREAMING_FLYBY(inputs, outputs, STREAMING_FLYBY, STREAMING_NORMAL);
}
 public void initializeSTREAMING_NORMAL(ComChannelList inputs, ComChannelList outputs, State STREAMING_FLYBY, State STREAMING_NORMAL) {
	// (STREAMING_NORMAL,[E=TARGET_SIGHTED_F_EVENT],[],1,NEXT,1.0)X(STREAMING_NORMAL,[V=VGUI_FALSE_POSITIVE_VO],[TARGET_SIGHTED_F=++])
	STREAMING_NORMAL.add(new Transition(_internal_vars, inputs, outputs, STREAMING_NORMAL, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(_inputs.get(Channels.TARGET_SIGHTED_F_EVENT.name()).value() == null && !(Boolean)_inputs.get(Channels.TARGET_SIGHTED_F_EVENT.name()).value()) {
				return false;
			}
			setTempOutput(Channels.VIDEO_VGUI_VO_COMM.name(), VideoOperatorGui.VIDEO_VGUI_VO_COMM.VGUI_FALSE_POSITIVE_VO);
			setTempInternalVar("TARGET_SIGHTED_F", (Integer)_internal_vars.getVariable("++") + 1);
			return true;
		}
	});
	// (STREAMING_NORMAL,[E=TARGET_SIGHTED_F_END_EVENT],[],1,NEXT,1.0)X(STREAMING_NORMAL,[],[TARGET_SIGHTED_F=__])
	STREAMING_NORMAL.add(new Transition(_internal_vars, inputs, outputs, STREAMING_NORMAL, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(_inputs.get(Channels.TARGET_SIGHTED_F_END_EVENT.name()).value() == null && !(Boolean)_inputs.get(Channels.TARGET_SIGHTED_F_END_EVENT.name()).value()) {
				return false;
			}
			setTempInternalVar("TARGET_SIGHTED_F", "__");
			return true;
		}
	});
	// (STREAMING_NORMAL,[E=TARGET_SIGHTED_T_EVENT],[],1,NEXT,1.0)X(STREAMING_NORMAL,[V=VGUI_TRUE_POSITIVE_VO],[TARGET_SIGHTED_T=++])
	STREAMING_NORMAL.add(new Transition(_internal_vars, inputs, outputs, STREAMING_NORMAL, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(_inputs.get(Channels.TARGET_SIGHTED_T_EVENT.name()).value() == null && !(Boolean)_inputs.get(Channels.TARGET_SIGHTED_T_EVENT.name()).value()) {
				return false;
			}
			setTempOutput(Channels.VIDEO_VGUI_VO_COMM.name(), VideoOperatorGui.VIDEO_VGUI_VO_COMM.VGUI_TRUE_POSITIVE_VO);
			setTempInternalVar("TARGET_SIGHTED_T", (Integer)_internal_vars.getVariable("++") + 1);
			return true;
		}
	});
	// (STREAMING_NORMAL,[E=TARGET_SIGHTED_T_END_EVENT],[],1,NEXT,1.0)X(STREAMING_NORMAL,[],[TARGET_SIGHTED_T=__])
	STREAMING_NORMAL.add(new Transition(_internal_vars, inputs, outputs, STREAMING_NORMAL, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(_inputs.get(Channels.TARGET_SIGHTED_T_END_EVENT.name()).value() == null && !(Boolean)_inputs.get(Channels.TARGET_SIGHTED_T_END_EVENT.name()).value()) {
				return false;
			}
			setTempInternalVar("TARGET_SIGHTED_T", "__");
			return true;
		}
	});
	// (STREAMING_NORMAL,[D=VO_POSSIBLE_ANOMALY_DETECTED_F_VGUI],[],1,NEXT,1.0)X(STREAMING_NORMAL,[V=VGUI_VALIDATION_REQ_F_MM,A=VGUI_ALERT_MM],[VALIDATION_REQ_F=++])
	STREAMING_NORMAL.add(new Transition(_internal_vars, inputs, outputs, STREAMING_NORMAL, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!VideoOperator.DATA_VO_VGUI_COMM.VO_POSSIBLE_ANOMALY_DETECTED_F_VGUI.equals(_inputs.get(Channels.DATA_VO_VGUI_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_VGUI_MM_COMM.name(), VideoOperatorGui.VIDEO_VGUI_MM_COMM.VGUI_VALIDATION_REQ_F_MM);
			setTempOutput(Channels.AUDIO_VGUI_MM_COMM.name(), VideoOperatorGui.AUDIO_VGUI_MM_COMM.VGUI_ALERT_MM);
			setTempInternalVar("VALIDATION_REQ_F", (Integer)_internal_vars.getVariable("++") + 1);
			return true;
		}
	});
	// (STREAMING_NORMAL,[D=VO_POSSIBLE_ANOMALY_DETECTED_T_VGUI],[],1,NEXT,1.0)X(STREAMING_NORMAL,[V=VGUI_VALIDATION_REQ_T_MM,A=VGUI_ALERT_MM],[VALIDATION_REQ_T=++])
	STREAMING_NORMAL.add(new Transition(_internal_vars, inputs, outputs, STREAMING_NORMAL, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!VideoOperator.DATA_VO_VGUI_COMM.VO_POSSIBLE_ANOMALY_DETECTED_T_VGUI.equals(_inputs.get(Channels.DATA_VO_VGUI_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_VGUI_MM_COMM.name(), VideoOperatorGui.VIDEO_VGUI_MM_COMM.VGUI_VALIDATION_REQ_T_MM);
			setTempOutput(Channels.AUDIO_VGUI_MM_COMM.name(), VideoOperatorGui.AUDIO_VGUI_MM_COMM.VGUI_ALERT_MM);
			setTempInternalVar("VALIDATION_REQ_T", (Integer)_internal_vars.getVariable("++") + 1);
			return true;
		}
	});
	// (STREAMING_NORMAL,[D=VO_FLYBY_REQ_T_VGUI],[],1,NEXT,1.0)X(STREAMING_NORMAL,[D=VGUI_FLYBY_REQ_OGUI],[])
	STREAMING_NORMAL.add(new Transition(_internal_vars, inputs, outputs, STREAMING_NORMAL, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!VideoOperator.DATA_VO_VGUI_COMM.VO_FLYBY_REQ_T_VGUI.equals(_inputs.get(Channels.DATA_VO_VGUI_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_VGUI_OGUI_COMM.name(), VideoOperatorGui.DATA_VGUI_OGUI_COMM.VGUI_FLYBY_REQ_OGUI);
			return true;
		}
	});
	// (STREAMING_NORMAL,[D=VO_FLYBY_REQ_F_VGUI],[],1,NEXT,1.0)X(STREAMING_NORMAL,[D=VGUI_FLYBY_REQ_OGUI],[])
	STREAMING_NORMAL.add(new Transition(_internal_vars, inputs, outputs, STREAMING_NORMAL, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!VideoOperator.DATA_VO_VGUI_COMM.VO_FLYBY_REQ_F_VGUI.equals(_inputs.get(Channels.DATA_VO_VGUI_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_VGUI_OGUI_COMM.name(), VideoOperatorGui.DATA_VGUI_OGUI_COMM.VGUI_FLYBY_REQ_OGUI);
			return true;
		}
	});
	// (STREAMING_NORMAL,[D=OGUI_FLYBY_VGUI],[STREAMING=NORMAL],1,NEXT,1.0)X(STREAMING_FLYBY,[],[STREAMING=FLYBY])
	STREAMING_NORMAL.add(new Transition(_internal_vars, inputs, outputs, STREAMING_FLYBY, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.DATA_OGUI_VGUI_COMM.OGUI_FLYBY_VGUI.equals(_inputs.get(Channels.DATA_OGUI_VGUI_COMM.name()).value())) {
				return false;
			}
			if(!"NORMAL".equals(_internal_vars.getVariable ("STREAMING"))) {
				return false;
			}
			setTempInternalVar("STREAMING", "FLYBY");
			return true;
		}
	});
	// (STREAMING_NORMAL,[D=MM_FLYBY_REQ_F_VGUI],[VALIDATION_REQ_F>0],1,NEXT,1.0)X(STREAMING_NORMAL,[D=VGUI_FLYBY_REQ_OGUI],[VALIDATION_REQ_F=__])
	STREAMING_NORMAL.add(new Transition(_internal_vars, inputs, outputs, STREAMING_NORMAL, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!MissionManager.DATA_MM_VGUI_COMM.MM_FLYBY_REQ_F_VGUI.equals(_inputs.get(Channels.DATA_MM_VGUI_COMM.name()).value())) {
				return false;
			}
			if(_internal_vars.getVariable("VALIDATION_REQ_F") instanceof Integer && new Integer(0) > (Integer) _internal_vars.getVariable ("VALIDATION_REQ_F")) {
				return false;
			}
			setTempOutput(Channels.DATA_VGUI_OGUI_COMM.name(), VideoOperatorGui.DATA_VGUI_OGUI_COMM.VGUI_FLYBY_REQ_OGUI);
			setTempInternalVar("VALIDATION_REQ_F", "__");
			return true;
		}
	});
	// (STREAMING_NORMAL,[D=MM_FLYBY_REQ_T_VGUI],[VALIDATION_REQ_T>0],1,NEXT,1.0)X(STREAMING_NORMAL,[D=VGUI_FLYBY_REQ_OGUI],[VALIDATION_REQ_T=__])
	STREAMING_NORMAL.add(new Transition(_internal_vars, inputs, outputs, STREAMING_NORMAL, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!MissionManager.DATA_MM_VGUI_COMM.MM_FLYBY_REQ_T_VGUI.equals(_inputs.get(Channels.DATA_MM_VGUI_COMM.name()).value())) {
				return false;
			}
			if(_internal_vars.getVariable("VALIDATION_REQ_T") instanceof Integer && new Integer(0) > (Integer) _internal_vars.getVariable ("VALIDATION_REQ_T")) {
				return false;
			}
			setTempOutput(Channels.DATA_VGUI_OGUI_COMM.name(), VideoOperatorGui.DATA_VGUI_OGUI_COMM.VGUI_FLYBY_REQ_OGUI);
			setTempInternalVar("VALIDATION_REQ_T", "__");
			return true;
		}
	});
	// (STREAMING_NORMAL,[D=MM_ANOMALY_DISMISSED_F_VGUI],[VALIDATION_REQ_F>0],1,NEXT,1.0)X(STREAMING_NORMAL,[],[VALIDATION_REQ_F=__])
	STREAMING_NORMAL.add(new Transition(_internal_vars, inputs, outputs, STREAMING_NORMAL, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!MissionManager.DATA_MM_VGUI_COMM.MM_ANOMALY_DISMISSED_F_VGUI.equals(_inputs.get(Channels.DATA_MM_VGUI_COMM.name()).value())) {
				return false;
			}
			if(_internal_vars.getVariable("VALIDATION_REQ_F") instanceof Integer && new Integer(0) > (Integer) _internal_vars.getVariable ("VALIDATION_REQ_F")) {
				return false;
			}
			setTempInternalVar("VALIDATION_REQ_F", "__");
			return true;
		}
	});
	// (STREAMING_NORMAL,[D=MM_ANOMALY_DISMISSED_T_VGUI],[VALIDATION_REQ_T>0],1,NEXT,1.0)X(STREAMING_NORMAL,[],[VALIDATION_REQ_T=__])
	STREAMING_NORMAL.add(new Transition(_internal_vars, inputs, outputs, STREAMING_NORMAL, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!MissionManager.DATA_MM_VGUI_COMM.MM_ANOMALY_DISMISSED_T_VGUI.equals(_inputs.get(Channels.DATA_MM_VGUI_COMM.name()).value())) {
				return false;
			}
			if(_internal_vars.getVariable("VALIDATION_REQ_T") instanceof Integer && new Integer(0) > (Integer) _internal_vars.getVariable ("VALIDATION_REQ_T")) {
				return false;
			}
			setTempInternalVar("VALIDATION_REQ_T", "__");
			return true;
		}
	});
	// (STREAMING_NORMAL,[D=UAV_READY_VGUI],[],1,NEXT,1.0)X(STREAMING_NORMAL,[V=VGUI_UAV_READY_VO],[])
	STREAMING_NORMAL.add(new Transition(_internal_vars, inputs, outputs, STREAMING_NORMAL, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.DATA_UAV_VGUI_COMM.UAV_READY_VGUI.equals(_inputs.get(Channels.DATA_UAV_VGUI_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_VGUI_VO_COMM.name(), VideoOperatorGui.VIDEO_VGUI_VO_COMM.VGUI_UAV_READY_VO);
			return true;
		}
	});
	// (STREAMING_NORMAL,[D=UAV_TAKE_OFF_VGUI],[],1,NEXT,1.0)X(STREAMING_NORMAL,[V=VGUI_UAV_TAKE_OFF_VO],[])
	STREAMING_NORMAL.add(new Transition(_internal_vars, inputs, outputs, STREAMING_NORMAL, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.DATA_UAV_VGUI_COMM.UAV_TAKE_OFF_VGUI.equals(_inputs.get(Channels.DATA_UAV_VGUI_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_VGUI_VO_COMM.name(), VideoOperatorGui.VIDEO_VGUI_VO_COMM.VGUI_UAV_TAKE_OFF_VO);
			return true;
		}
	});
	// (STREAMING_NORMAL,[D=UAV_FLYING_NORMAL_VGUI],[],1,NEXT,1.0)X(STREAMING_NORMAL,[V=VGUI_UAV_FLYING_NORMA_VO],[])
	STREAMING_NORMAL.add(new Transition(_internal_vars, inputs, outputs, STREAMING_NORMAL, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.DATA_UAV_VGUI_COMM.UAV_FLYING_NORMAL_VGUI.equals(_inputs.get(Channels.DATA_UAV_VGUI_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_VGUI_VO_COMM.name(), VideoOperatorGui.VIDEO_VGUI_VO_COMM.VGUI_UAV_FLYING_NORMA_VO);
			return true;
		}
	});
	// (STREAMING_NORMAL,[D=UAV_FLYING_FLYBY_VGUI],[],1,NEXT,1.0)X(STREAMING_FLYBY,[V=VGUI_UAV_FLYING_FLYBY_VO],[])
	STREAMING_NORMAL.add(new Transition(_internal_vars, inputs, outputs, STREAMING_FLYBY, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.DATA_UAV_VGUI_COMM.UAV_FLYING_FLYBY_VGUI.equals(_inputs.get(Channels.DATA_UAV_VGUI_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_VGUI_VO_COMM.name(), VideoOperatorGui.VIDEO_VGUI_VO_COMM.VGUI_UAV_FLYING_FLYBY_VO);
			return true;
		}
	});
	// (STREAMING_NORMAL,[D=UAV_LOITERING_VGUI],[],1,NEXT,1.0)X(STREAMING_NORMAL,[V=VGUI_UAV_LOITERING_VO],[])
	STREAMING_NORMAL.add(new Transition(_internal_vars, inputs, outputs, STREAMING_NORMAL, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.DATA_UAV_VGUI_COMM.UAV_LOITERING_VGUI.equals(_inputs.get(Channels.DATA_UAV_VGUI_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_VGUI_VO_COMM.name(), VideoOperatorGui.VIDEO_VGUI_VO_COMM.VGUI_UAV_LOITERING_VO);
			return true;
		}
	});
	// (STREAMING_NORMAL,[D=UAV_LANDING_VGUI],[],1,NEXT,1.0)X(STREAMING_NORMAL,[V=VGUI_UAV_LANDING_VO],[])
	STREAMING_NORMAL.add(new Transition(_internal_vars, inputs, outputs, STREAMING_NORMAL, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.DATA_UAV_VGUI_COMM.UAV_LANDING_VGUI.equals(_inputs.get(Channels.DATA_UAV_VGUI_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_VGUI_VO_COMM.name(), VideoOperatorGui.VIDEO_VGUI_VO_COMM.VGUI_UAV_LANDING_VO);
			return true;
		}
	});
	// (STREAMING_NORMAL,[D=UAV_LANDED_VGUI],[],1,NEXT,1.0)X(STREAMING_NORMAL,[V=VGUI_UAV_LANDED_VO],[])
	STREAMING_NORMAL.add(new Transition(_internal_vars, inputs, outputs, STREAMING_NORMAL, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.DATA_UAV_VGUI_COMM.UAV_LANDED_VGUI.equals(_inputs.get(Channels.DATA_UAV_VGUI_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_VGUI_VO_COMM.name(), VideoOperatorGui.VIDEO_VGUI_VO_COMM.VGUI_UAV_LANDED_VO);
			return true;
		}
	});
	// (STREAMING_NORMAL,[D=UAV_CRASHED_VGUI],[],1,NEXT,1.0)X(STREAMING_NORMAL,[V=VGUI_UAV_CRASHED_VO],[])
	STREAMING_NORMAL.add(new Transition(_internal_vars, inputs, outputs, STREAMING_NORMAL, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.DATA_UAV_VGUI_COMM.UAV_CRASHED_VGUI.equals(_inputs.get(Channels.DATA_UAV_VGUI_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_VGUI_VO_COMM.name(), VideoOperatorGui.VIDEO_VGUI_VO_COMM.VGUI_UAV_CRASHED_VO);
			return true;
		}
	});
//	// (STREAMING_NORMAL,[D=UAVVF_NONE_VGUI],[],1,NEXT,1.0)X(STREAMING_NORMAL,[V=VGUI_SIGNAL_NONE_VO],[])
//	STREAMING_NORMAL.add(new Transition(_internal_vars, inputs, outputs, STREAMING_NORMAL, Duration.NEXT.getRange(), 1, 1.0) {
//		@Override
//		public boolean isEnabled() { 
//			if(!UAVVideoFeed.DATA_UAVVF_VGUI_COMM.UAVVF_NONE_VGUI.equals(_inputs.get(Channels.DATA_UAVVF_VGUI_COMM.name()).value())) {
//				return false;
//			}
//			setTempOutput(Channels.VIDEO_VGUI_VO_COMM.name(), VideoOperatorGui.VIDEO_VGUI_VO_COMM.VGUI_SIGNAL_NONE_VO);
//			return true;
//		}
//	});
	// (STREAMING_NORMAL,[D=UAVVF_OK_VGUI],[],1,NEXT,1.0)X(STREAMING_NORMAL,[V=VGUI_SIGNAL_OK_VO],[])
	STREAMING_NORMAL.add(new Transition(_internal_vars, inputs, outputs, STREAMING_NORMAL, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAVVideoFeed.DATA_UAVVF_VGUI_COMM.UAVVF_OK_VGUI.equals(_inputs.get(Channels.DATA_UAVVF_VGUI_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_VGUI_VO_COMM.name(), VideoOperatorGui.VIDEO_VGUI_VO_COMM.VGUI_SIGNAL_OK_VO);
			return true;
		}
	});
	// (STREAMING_NORMAL,[D=UAVVF_BAD_VGUI],[],1,NEXT,1.0)X(STREAMING_NORMAL,[V=VGUI_SIGNAL_BAD_VO],[])
	STREAMING_NORMAL.add(new Transition(_internal_vars, inputs, outputs, STREAMING_NORMAL, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAVVideoFeed.DATA_UAVVF_VGUI_COMM.UAVVF_BAD_VGUI.equals(_inputs.get(Channels.DATA_UAVVF_VGUI_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_VGUI_VO_COMM.name(), VideoOperatorGui.VIDEO_VGUI_VO_COMM.VGUI_SIGNAL_BAD_VO);
			return true;
		}
	});
	add(STREAMING_NORMAL);
}
 public void initializeSTREAMING_FLYBY(ComChannelList inputs, ComChannelList outputs, State STREAMING_FLYBY, State STREAMING_NORMAL) {
	// (STREAMING_FLYBY,[D=VO_FLYBY_END_FAILED_VGUI],[STREAMING=FLYBY],1,NEXT,1.0)X(STREAMING_NORMAL,[],[STREAMING=NORMAL])
	STREAMING_FLYBY.add(new Transition(_internal_vars, inputs, outputs, STREAMING_NORMAL, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!VideoOperator.DATA_VO_VGUI_COMM.VO_FLYBY_END_FAILED_VGUI.equals(_inputs.get(Channels.DATA_VO_VGUI_COMM.name()).value())) {
				return false;
			}
			if(!"FLYBY".equals(_internal_vars.getVariable ("STREAMING"))) {
				return false;
			}
			setTempInternalVar("STREAMING", "NORMAL");
			return true;
		}
	});
	// (STREAMING_FLYBY,[D=VO_FLYBY_END_SUCCESS_VGUI],[STREAMING=FLYBY],1,NEXT,1.0)X(STREAMING_NORMAL,[],[STREAMING=NORMAL])
	STREAMING_FLYBY.add(new Transition(_internal_vars, inputs, outputs, STREAMING_NORMAL, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!VideoOperator.DATA_VO_VGUI_COMM.VO_FLYBY_END_SUCCESS_VGUI.equals(_inputs.get(Channels.DATA_VO_VGUI_COMM.name()).value())) {
				return false;
			}
			if(!"FLYBY".equals(_internal_vars.getVariable ("STREAMING"))) {
				return false;
			}
			setTempInternalVar("STREAMING", "NORMAL");
			return true;
		}
	});
//	// (STREAMING_FLYBY,[D=UAV_FLYBY_ANOMALY_F_VGUI],[],1,NEXT,1.0)X(STREAMING_FLYBY,[V=VGUI_FLYBY_ANOMALY_F_VO],[])
//	STREAMING_FLYBY.add(new Transition(_internal_vars, inputs, outputs, STREAMING_FLYBY, Duration.NEXT.getRange(), 1, 1.0) {
//		@Override
//		public boolean isEnabled() { 
//			if(!UAV.DATA_UAV_VGUI_COMM.UAV_FLYBY_ANOMALY_F_VGUI.equals(_inputs.get(Channels.DATA_UAV_VGUI_COMM.name()).value())) {
//				return false;
//			}
//			setTempOutput(Channels.VIDEO_VGUI_VO_COMM.name(), VideoOperatorGui.VIDEO_VGUI_VO_COMM.VGUI_FLYBY_ANOMALY_F_VO);
//			return true;
//		}
//	});
//	// (STREAMING_FLYBY,[D=UAV_FLYBY_ANOMALY_T_VGUI],[],1,NEXT,1.0)X(STREAMING_FLYBY,[V=VGUI_FLYBY_ANOMALY_T_VO],[])
//	STREAMING_FLYBY.add(new Transition(_internal_vars, inputs, outputs, STREAMING_FLYBY, Duration.NEXT.getRange(), 1, 1.0) {
//		@Override
//		public boolean isEnabled() { 
//			if(!UAV.DATA_UAV_VGUI_COMM.UAV_FLYBY_ANOMALY_T_VGUI.equals(_inputs.get(Channels.DATA_UAV_VGUI_COMM.name()).value())) {
//				return false;
//			}
//			setTempOutput(Channels.VIDEO_VGUI_VO_COMM.name(), VideoOperatorGui.VIDEO_VGUI_VO_COMM.VGUI_FLYBY_ANOMALY_T_VO);
//			return true;
//		}
//	});
	// (STREAMING_FLYBY,[D=UAV_READY_VGUI],[],1,NEXT,1.0)X(STREAMING_NORMAL,[V=VGUI_UAV_READY_VO],[])
	STREAMING_FLYBY.add(new Transition(_internal_vars, inputs, outputs, STREAMING_NORMAL, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.DATA_UAV_VGUI_COMM.UAV_READY_VGUI.equals(_inputs.get(Channels.DATA_UAV_VGUI_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_VGUI_VO_COMM.name(), VideoOperatorGui.VIDEO_VGUI_VO_COMM.VGUI_UAV_READY_VO);
			return true;
		}
	});
	// (STREAMING_FLYBY,[D=UAV_TAKE_OFF_VGUI],[],1,NEXT,1.0)X(STREAMING_NORMAL,[V=VGUI_UAV_TAKE_OFF_VO],[])
	STREAMING_FLYBY.add(new Transition(_internal_vars, inputs, outputs, STREAMING_NORMAL, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.DATA_UAV_VGUI_COMM.UAV_TAKE_OFF_VGUI.equals(_inputs.get(Channels.DATA_UAV_VGUI_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_VGUI_VO_COMM.name(), VideoOperatorGui.VIDEO_VGUI_VO_COMM.VGUI_UAV_TAKE_OFF_VO);
			return true;
		}
	});
	// (STREAMING_FLYBY,[D=UAV_FLYING_NORMAL_VGUI],[],1,NEXT,1.0)X(STREAMING_NORMAL,[V=VGUI_UAV_FLYING_NORMA_VO],[])
	STREAMING_FLYBY.add(new Transition(_internal_vars, inputs, outputs, STREAMING_NORMAL, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.DATA_UAV_VGUI_COMM.UAV_FLYING_NORMAL_VGUI.equals(_inputs.get(Channels.DATA_UAV_VGUI_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_VGUI_VO_COMM.name(), VideoOperatorGui.VIDEO_VGUI_VO_COMM.VGUI_UAV_FLYING_NORMA_VO);
			return true;
		}
	});
	// (STREAMING_FLYBY,[D=UAV_FLYING_FLYBY_VGUI],[],1,NEXT,1.0)X(STREAMING_FLYBY,[V=VGUI_UAV_FLYING_FLYBY_VO],[])
	STREAMING_FLYBY.add(new Transition(_internal_vars, inputs, outputs, STREAMING_FLYBY, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.DATA_UAV_VGUI_COMM.UAV_FLYING_FLYBY_VGUI.equals(_inputs.get(Channels.DATA_UAV_VGUI_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_VGUI_VO_COMM.name(), VideoOperatorGui.VIDEO_VGUI_VO_COMM.VGUI_UAV_FLYING_FLYBY_VO);
			return true;
		}
	});
	// (STREAMING_FLYBY,[D=UAV_LOITERING_VGUI],[],1,NEXT,1.0)X(STREAMING_NORMAL,[V=VGUI_UAV_LOITERING_VO],[])
	STREAMING_FLYBY.add(new Transition(_internal_vars, inputs, outputs, STREAMING_NORMAL, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.DATA_UAV_VGUI_COMM.UAV_LOITERING_VGUI.equals(_inputs.get(Channels.DATA_UAV_VGUI_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_VGUI_VO_COMM.name(), VideoOperatorGui.VIDEO_VGUI_VO_COMM.VGUI_UAV_LOITERING_VO);
			return true;
		}
	});
	// (STREAMING_FLYBY,[D=UAV_LANDING_VGUI],[],1,NEXT,1.0)X(STREAMING_NORMAL,[V=VGUI_UAV_LANDING_VO],[])
	STREAMING_FLYBY.add(new Transition(_internal_vars, inputs, outputs, STREAMING_NORMAL, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.DATA_UAV_VGUI_COMM.UAV_LANDING_VGUI.equals(_inputs.get(Channels.DATA_UAV_VGUI_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_VGUI_VO_COMM.name(), VideoOperatorGui.VIDEO_VGUI_VO_COMM.VGUI_UAV_LANDING_VO);
			return true;
		}
	});
	// (STREAMING_FLYBY,[D=UAV_LANDED_VGUI],[],1,NEXT,1.0)X(STREAMING_NORMAL,[V=VGUI_UAV_LANDED_VO],[])
	STREAMING_FLYBY.add(new Transition(_internal_vars, inputs, outputs, STREAMING_NORMAL, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.DATA_UAV_VGUI_COMM.UAV_LANDED_VGUI.equals(_inputs.get(Channels.DATA_UAV_VGUI_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_VGUI_VO_COMM.name(), VideoOperatorGui.VIDEO_VGUI_VO_COMM.VGUI_UAV_LANDED_VO);
			return true;
		}
	});
	// (STREAMING_FLYBY,[D=UAV_CRASHED_VGUI],[],1,NEXT,1.0)X(STREAMING_NORMAL,[V=VGUI_UAV_CRASHED_VO],[])
	STREAMING_FLYBY.add(new Transition(_internal_vars, inputs, outputs, STREAMING_NORMAL, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.DATA_UAV_VGUI_COMM.UAV_CRASHED_VGUI.equals(_inputs.get(Channels.DATA_UAV_VGUI_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.VIDEO_VGUI_VO_COMM.name(), VideoOperatorGui.VIDEO_VGUI_VO_COMM.VGUI_UAV_CRASHED_VO);
			return true;
		}
	});
	add(STREAMING_FLYBY);
}
@Override
protected void initializeInternalVariables() {
	_internal_vars.addVariable("TARGET_SIGHTED_F", false);
	_internal_vars.addVariable("TARGET_SIGHTED_T", false); 
	_internal_vars.addVariable("VALIDATION_REQ_F",  false);
	_internal_vars.addVariable("VALIDATION_REQ_T",  false);
	_internal_vars.addVariable("STREAMING", null);
}
}