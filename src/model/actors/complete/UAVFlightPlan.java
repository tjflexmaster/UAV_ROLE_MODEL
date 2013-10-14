package model.actors.complete;

import model.team.*;
import simulator.*;

public class UAVFlightPlan extends Actor {
public enum DATA_UAVFP_UAV_COMM{
	UAVFP_COMPLETE_UAV,
	UAVFP_PAUSED_UAV,
	UAVFP_NO_PATH_UAV,
	UAVFP_YES_PATH_UAV,
}
public enum DATA_UAVFP_OGUI_COMM{
	UAVFP_COMPLETE_OGUI,
	UAVFP_PAUSED_OGUI,
	UAVFP_NO_PATH_OGUI,
	UAVFP_YES_PATH_OGUI,
}
public UAVFlightPlan(ComChannelList inputs, ComChannelList outputs) {
	State RESUME_AFTER_LAUNCH = new State("RESUME_AFTER_LAUNCH");
	State NO_PATH = new State("NO_PATH");
	State PAUSED = new State("PAUSED");
	State COMPLETE = new State("COMPLETE");
	State YES_PATH = new State("YES_PATH");
	initializeNO_PATH(inputs, outputs, PAUSED, NO_PATH, YES_PATH);
	initializeYES_PATH(inputs, outputs, PAUSED, YES_PATH, COMPLETE);
	initializePAUSED(inputs, outputs, RESUME_AFTER_LAUNCH, PAUSED, YES_PATH);
	initializeCOMPLETE(inputs, outputs, YES_PATH, COMPLETE, NO_PATH);
	initializeRESUME_AFTER_LAUNCH(inputs, outputs, RESUME_AFTER_LAUNCH, YES_PATH);
}
 public void initializeNO_PATH(ComChannelList inputs, ComChannelList outputs, State PAUSED, State NO_PATH, State YES_PATH) {
	// (NO_PATH,[D=OGUI_NEW_FP_UAV,D=UAV_LOITERING_OGUI],[START_TIME=TIME],2,NEXT,1.0)X(YES_PATH,[D=UAVFP_YES_PATH_OGUI,D=UAVFP_YES_PATH_UAV],[])
	NO_PATH.add(new Transition(_internal_vars, inputs, outputs, YES_PATH, Duration.NEXT.getRange(), 2, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.DATA_OGUI_UAV_COMM.OGUI_NEW_FP_UAV.equals(_inputs.get(Channels.DATA_OGUI_UAV_COMM.name()).value())) {
				return false;
			}
			if(!UAV.DATA_UAV_OGUI_COMM.UAV_LOITERING_OGUI.equals(_inputs.get(Channels.DATA_UAV_OGUI_COMM.name()).value())) {
				return false;
			}
			if(!"TIME".equals(_internal_vars.getVariable ("START_TIME"))) {
				return false;
			}
			setTempOutput(Channels.DATA_UAVFP_OGUI_COMM.name(), UAVFlightPlan.DATA_UAVFP_OGUI_COMM.UAVFP_YES_PATH_OGUI);
			setTempOutput(Channels.DATA_UAVFP_UAV_COMM.name(), UAVFlightPlan.DATA_UAVFP_UAV_COMM.UAVFP_YES_PATH_UAV);
			return true;
		}
	});
	// (NO_PATH,[D=OGUI_NEW_FP_UAV],[START_TIME=TIME,PAUSE_TIME=TIME],1,NEXT,1.0)X(PAUSED,[D=UAVFP_PAUSED_OGUI,D=UAVFP_PAUSED_UAV],[])
	NO_PATH.add(new Transition(_internal_vars, inputs, outputs, PAUSED, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.DATA_OGUI_UAV_COMM.OGUI_NEW_FP_UAV.equals(_inputs.get(Channels.DATA_OGUI_UAV_COMM.name()).value())) {
				return false;
			}
			if(!"TIME".equals(_internal_vars.getVariable ("START_TIME"))) {
				return false;
			}
			if(!"TIME".equals(_internal_vars.getVariable ("PAUSE_TIME"))) {
				return false;
			}
			setTempOutput(Channels.DATA_UAVFP_OGUI_COMM.name(), UAVFlightPlan.DATA_UAVFP_OGUI_COMM.UAVFP_PAUSED_OGUI);
			setTempOutput(Channels.DATA_UAVFP_UAV_COMM.name(), UAVFlightPlan.DATA_UAVFP_UAV_COMM.UAVFP_PAUSED_UAV);
			return true;
		}
	});
	add(NO_PATH);
}
 public void initializeYES_PATH(ComChannelList inputs, ComChannelList outputs, State PAUSED, State YES_PATH, State COMPLETE) {
	// (YES_PATH,[],[],0,UAV_PATH_DUR,1.0)X(COMPLETE,[D=UAVFP_COMPLETE_OGUI,D=UAVFP_COMPLETE_UAV],[])
	YES_PATH.add(new Transition(_internal_vars, inputs, outputs, COMPLETE, Duration.UAV_PATH_DUR.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			setTempOutput(Channels.DATA_UAVFP_OGUI_COMM.name(), UAVFlightPlan.DATA_UAVFP_OGUI_COMM.UAVFP_COMPLETE_OGUI);
			setTempOutput(Channels.DATA_UAVFP_UAV_COMM.name(), UAVFlightPlan.DATA_UAVFP_UAV_COMM.UAVFP_COMPLETE_UAV);
			return true;
		}
	});
	// (YES_PATH,[D=OGUI_MODIFY_FP_UAV],[],1,NEXT,1.0)X(COMPLETE,[D=UAVFP_COMPLETE_OGUI,D=UAVFP_COMPLETE_UAV],[])
	YES_PATH.add(new Transition(_internal_vars, inputs, outputs, COMPLETE, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.DATA_OGUI_UAV_COMM.OGUI_MODIFY_FP_UAV.equals(_inputs.get(Channels.DATA_OGUI_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_UAVFP_OGUI_COMM.name(), UAVFlightPlan.DATA_UAVFP_OGUI_COMM.UAVFP_COMPLETE_OGUI);
			setTempOutput(Channels.DATA_UAVFP_UAV_COMM.name(), UAVFlightPlan.DATA_UAVFP_UAV_COMM.UAVFP_COMPLETE_UAV);
			return true;
		}
	});
	// (YES_PATH,[D=UAVS_LOST_UAV],[],1,NEXT,1.0)X(PAUSED,[D=UAVFP_PAUSED_OGUI,D=UAVFP_PAUSED_UAV],[])
	YES_PATH.add(new Transition(_internal_vars, inputs, outputs, PAUSED, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAVSignal.DATA_UAVS_UAV_COMM.UAVS_LOST_UAV.equals(_inputs.get(Channels.DATA_UAVS_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_UAVFP_OGUI_COMM.name(), UAVFlightPlan.DATA_UAVFP_OGUI_COMM.UAVFP_PAUSED_OGUI);
			setTempOutput(Channels.DATA_UAVFP_UAV_COMM.name(), UAVFlightPlan.DATA_UAVFP_UAV_COMM.UAVFP_PAUSED_UAV);
			return true;
		}
	});
	// (YES_PATH,[D=OGUI_LAND_UAV],[PAUSE_TIME=TIME],1,NEXT,1.0)X(PAUSED,[D=UAVFP_PAUSED_OGUI,D=UAVFP_PAUSED_UAV],[])
	YES_PATH.add(new Transition(_internal_vars, inputs, outputs, PAUSED, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.DATA_OGUI_UAV_COMM.OGUI_LAND_UAV.equals(_inputs.get(Channels.DATA_OGUI_UAV_COMM.name()).value())) {
				return false;
			}
			if(!"TIME".equals(_internal_vars.getVariable ("PAUSE_TIME"))) {
				return false;
			}
			setTempOutput(Channels.DATA_UAVFP_OGUI_COMM.name(), UAVFlightPlan.DATA_UAVFP_OGUI_COMM.UAVFP_PAUSED_OGUI);
			setTempOutput(Channels.DATA_UAVFP_UAV_COMM.name(), UAVFlightPlan.DATA_UAVFP_UAV_COMM.UAVFP_PAUSED_UAV);
			return true;
		}
	});
	// (YES_PATH,[D=OGUI_LOITER_UAV],[PAUSE_TIME=TIME],1,NEXT,1.0)X(PAUSED,[D=UAVFP_PAUSED_OGUI,D=UAVFP_PAUSED_UAV],[])
	YES_PATH.add(new Transition(_internal_vars, inputs, outputs, PAUSED, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.DATA_OGUI_UAV_COMM.OGUI_LOITER_UAV.equals(_inputs.get(Channels.DATA_OGUI_UAV_COMM.name()).value())) {
				return false;
			}
			if(!"TIME".equals(_internal_vars.getVariable ("PAUSE_TIME"))) {
				return false;
			}
			setTempOutput(Channels.DATA_UAVFP_OGUI_COMM.name(), UAVFlightPlan.DATA_UAVFP_OGUI_COMM.UAVFP_PAUSED_OGUI);
			setTempOutput(Channels.DATA_UAVFP_UAV_COMM.name(), UAVFlightPlan.DATA_UAVFP_UAV_COMM.UAVFP_PAUSED_UAV);
			return true;
		}
	});
	// (YES_PATH,[D=OGUI_FLYBY_START_T_UAV],[PAUSE_TIME=TIME],1,NEXT,1.0)X(PAUSED,[D=UAVFP_PAUSED_OGUI,D=UAVFP_PAUSED_UAV],[])
	YES_PATH.add(new Transition(_internal_vars, inputs, outputs, PAUSED, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.DATA_OGUI_UAV_COMM.OGUI_FLYBY_START_T_UAV.equals(_inputs.get(Channels.DATA_OGUI_UAV_COMM.name()).value())) {
				return false;
			}
			if(!"TIME".equals(_internal_vars.getVariable ("PAUSE_TIME"))) {
				return false;
			}
			setTempOutput(Channels.DATA_UAVFP_OGUI_COMM.name(), UAVFlightPlan.DATA_UAVFP_OGUI_COMM.UAVFP_PAUSED_OGUI);
			setTempOutput(Channels.DATA_UAVFP_UAV_COMM.name(), UAVFlightPlan.DATA_UAVFP_UAV_COMM.UAVFP_PAUSED_UAV);
			return true;
		}
	});
	// (YES_PATH,[D=OGUI_FLYBY_START_F_UAV],[PAUSE_TIME=TIME],1,NEXT,1.0)X(PAUSED,[D=UAVFP_PAUSED_OGUI,D=UAVFP_PAUSED_UAV],[])
	YES_PATH.add(new Transition(_internal_vars, inputs, outputs, PAUSED, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.DATA_OGUI_UAV_COMM.OGUI_FLYBY_START_F_UAV.equals(_inputs.get(Channels.DATA_OGUI_UAV_COMM.name()).value())) {
				return false;
			}
			if(!"TIME".equals(_internal_vars.getVariable ("PAUSE_TIME"))) {
				return false;
			}
			setTempOutput(Channels.DATA_UAVFP_OGUI_COMM.name(), UAVFlightPlan.DATA_UAVFP_OGUI_COMM.UAVFP_PAUSED_OGUI);
			setTempOutput(Channels.DATA_UAVFP_UAV_COMM.name(), UAVFlightPlan.DATA_UAVFP_UAV_COMM.UAVFP_PAUSED_UAV);
			return true;
		}
	});
	add(YES_PATH);
}
 public void initializePAUSED(ComChannelList inputs, ComChannelList outputs, State RESUME_AFTER_LAUNCH, State PAUSED, State YES_PATH) {
	// (PAUSED,[D=OGUI_RESUME_UAV],[],1,NEXT,1.0)X(YES_PATH,[D=UAVFP_YES_PATH_OGUI,D=UAVFP_YES_PATH_UAV],[])
	PAUSED.add(new Transition(_internal_vars, inputs, outputs, YES_PATH, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.DATA_OGUI_UAV_COMM.OGUI_RESUME_UAV.equals(_inputs.get(Channels.DATA_OGUI_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_UAVFP_OGUI_COMM.name(), UAVFlightPlan.DATA_UAVFP_OGUI_COMM.UAVFP_YES_PATH_OGUI);
			setTempOutput(Channels.DATA_UAVFP_UAV_COMM.name(), UAVFlightPlan.DATA_UAVFP_UAV_COMM.UAVFP_YES_PATH_UAV);
			return true;
		}
	});
	// (PAUSED,[D=UAV_LOITERING_OGUI],[],1,NEXT,1.0)X(YES_PATH,[D=UAVFP_YES_PATH_OGUI,D=UAVFP_YES_PATH_UAV],[])
	PAUSED.add(new Transition(_internal_vars, inputs, outputs, YES_PATH, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.DATA_UAV_OGUI_COMM.UAV_LOITERING_OGUI.equals(_inputs.get(Channels.DATA_UAV_OGUI_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_UAVFP_OGUI_COMM.name(), UAVFlightPlan.DATA_UAVFP_OGUI_COMM.UAVFP_YES_PATH_OGUI);
			setTempOutput(Channels.DATA_UAVFP_UAV_COMM.name(), UAVFlightPlan.DATA_UAVFP_UAV_COMM.UAVFP_YES_PATH_UAV);
			return true;
		}
	});
	// (PAUSED,[D=UAVS_RESUMED_UAV],[],1,NEXT,1.0)X(YES_PATH,[D=UAVFP_YES_PATH_OGUI,D=UAVFP_YES_PATH_UAV],[])
	PAUSED.add(new Transition(_internal_vars, inputs, outputs, YES_PATH, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAVSignal.DATA_UAVS_UAV_COMM.UAVS_RESUMED_UAV.equals(_inputs.get(Channels.DATA_UAVS_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_UAVFP_OGUI_COMM.name(), UAVFlightPlan.DATA_UAVFP_OGUI_COMM.UAVFP_YES_PATH_OGUI);
			setTempOutput(Channels.DATA_UAVFP_UAV_COMM.name(), UAVFlightPlan.DATA_UAVFP_UAV_COMM.UAVFP_YES_PATH_UAV);
			return true;
		}
	});
	// (PAUSED,[D=OP_LAUNCH_UAV],[],1,NEXT,1.0)X(RESUME_AFTER_LAUNCH,[D=UAVFP_PAUSED_OGUI,D=UAVFP_PAUSED_UAV],[])
	PAUSED.add(new Transition(_internal_vars, inputs, outputs, RESUME_AFTER_LAUNCH, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!Operator.DATA_OP_UAV_COMM.OP_LAUNCH_UAV.equals(_inputs.get(Channels.DATA_OP_UAV_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_UAVFP_OGUI_COMM.name(), UAVFlightPlan.DATA_UAVFP_OGUI_COMM.UAVFP_PAUSED_OGUI);
			setTempOutput(Channels.DATA_UAVFP_UAV_COMM.name(), UAVFlightPlan.DATA_UAVFP_UAV_COMM.UAVFP_PAUSED_UAV);
			return true;
		}
	});
	add(PAUSED);
}
 public void initializeCOMPLETE(ComChannelList inputs, ComChannelList outputs, State YES_PATH, State COMPLETE, State NO_PATH) {
	// (COMPLETE,[],[],0,NEXT,1.0)X(NO_PATH,[D=UAVFP_NO_PATH_OGUI,D=UAVFP_NO_PATH_UAV],[])
	COMPLETE.add(new Transition(_internal_vars, inputs, outputs, NO_PATH, Duration.NEXT.getRange(), 0, 1.0) {
		@Override
		public boolean isEnabled() { 
			setTempOutput(Channels.DATA_UAVFP_OGUI_COMM.name(), UAVFlightPlan.DATA_UAVFP_OGUI_COMM.UAVFP_NO_PATH_OGUI);
			setTempOutput(Channels.DATA_UAVFP_UAV_COMM.name(), UAVFlightPlan.DATA_UAVFP_UAV_COMM.UAVFP_NO_PATH_UAV);
			return true;
		}
	});
	// (COMPLETE,[D=OGUI_NEW_FP_UAV],[START_TIME=TIME],1,NEXT,1.0)X(YES_PATH,[D=UAVFP_YES_PATH_OGUI,D=UAVFP_YES_PATH_UAV],[])
	COMPLETE.add(new Transition(_internal_vars, inputs, outputs, YES_PATH, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!OperatorGui.DATA_OGUI_UAV_COMM.OGUI_NEW_FP_UAV.equals(_inputs.get(Channels.DATA_OGUI_UAV_COMM.name()).value())) {
				return false;
			}
			if(!"TIME".equals(_internal_vars.getVariable ("START_TIME"))) {
				return false;
			}
			setTempOutput(Channels.DATA_UAVFP_OGUI_COMM.name(), UAVFlightPlan.DATA_UAVFP_OGUI_COMM.UAVFP_YES_PATH_OGUI);
			setTempOutput(Channels.DATA_UAVFP_UAV_COMM.name(), UAVFlightPlan.DATA_UAVFP_UAV_COMM.UAVFP_YES_PATH_UAV);
			return true;
		}
	});
	add(COMPLETE);
}
 public void initializeRESUME_AFTER_LAUNCH(ComChannelList inputs, ComChannelList outputs, State RESUME_AFTER_LAUNCH, State YES_PATH) {
	// (RESUME_AFTER_LAUNCH,[D=UAV_LOITERING_OGUI],[],1,NEXT,1.0)X(YES_PATH,[D=UAVFP_YES_PATH_OGUI,D=UAVFP_YES_PATH_UAV],[])
	RESUME_AFTER_LAUNCH.add(new Transition(_internal_vars, inputs, outputs, YES_PATH, Duration.NEXT.getRange(), 1, 1.0) {
		@Override
		public boolean isEnabled() { 
			if(!UAV.DATA_UAV_OGUI_COMM.UAV_LOITERING_OGUI.equals(_inputs.get(Channels.DATA_UAV_OGUI_COMM.name()).value())) {
				return false;
			}
			setTempOutput(Channels.DATA_UAVFP_OGUI_COMM.name(), UAVFlightPlan.DATA_UAVFP_OGUI_COMM.UAVFP_YES_PATH_OGUI);
			setTempOutput(Channels.DATA_UAVFP_UAV_COMM.name(), UAVFlightPlan.DATA_UAVFP_UAV_COMM.UAVFP_YES_PATH_UAV);
			return true;
		}
	});
	add(RESUME_AFTER_LAUNCH);
}
@Override
protected void initializeInternalVariables() {
	_internal_vars.addVariable("PAUSE_TIME", null);
	_internal_vars.addVariable("START_TIME", null);
}
}