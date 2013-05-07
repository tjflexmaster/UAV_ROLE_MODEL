package WiSAR.Agents;

import java.util.ArrayList;

import CUAS.Simulator.Actor;
import CUAS.Simulator.IData;
import CUAS.Simulator.IStateEnum;
import CUAS.Simulator.Simulator;
import WiSAR.Actors;
import WiSAR.Events.TargetSightingFalseEvent;
import WiSAR.Events.TargetSightingTrueEvent;
import WiSAR.submodule.FlybyAnomaly;
import WiSAR.submodule.UAVBattery;
import WiSAR.submodule.UAVFlightPlan;
import WiSAR.submodule.UAVHeightAboveGround;
import WiSAR.submodule.UAVSignal;
import WiSAR.submodule.UAVVideoFeed;

public class VideoGUIRole extends Actor {

	boolean target_true;
	ArrayList<IData> _visible_anomalies;
	ArrayList<IData> _verification_requests;
	UAVRole.Outputs _uav_state;
	UAVVideoFeed.Outputs _uav_video_feed;
	Outputs _vgui_mode;
   
    public enum Outputs implements IData
    {
    	/**
    	 * Observables
    	 */
    	VGUI_FLYBY_T,
    	VGUI_FLYBY_F,
    	VGUI_NORMAL,
    	
        /**
         * For the VideoOperator or MissionManager
         */
        VGUI_FALSE_POSITIVE, 
        VGUI_TRUE_POSITIVE,
        
        /**
         * GUI to GUI ouputs
         */
        VGUI_VALIDATION_REQ_TRUE, 
        VGUI_VALIDATION_REQ_FALSE,
       
    }
   
    public enum States implements IStateEnum
    {
        STREAMING_NORMAL,
        STREAMING_FLYBY,
    }
    
	public VideoGUIRole()
	{
		name( Actors.VIDEO_OPERATOR_GUI.name() );
		nextState(States.STREAMING_NORMAL, 1);
		_visible_anomalies = new ArrayList<IData>();
		_verification_requests = new ArrayList<IData>();
		_vgui_mode = Outputs.VGUI_NORMAL;
	}

   @Override
    public void processNextState() {//Is our next state now?
        if ( nextStateTime() != Simulator.getInstance().getTime() ) {
        	setObservations();
            return;
        }
        state(nextState());
        switch ((States) nextState()) {
        	default:
	        	nextState(null,0);
	        	break;
        }
        
        //Set observables
        setObservations();
    }

	@Override
	public void processInputs() {
		
		//Pull Input and any observations that need to be made from the simulator
		ArrayList<IData> input = sim().getInput(this.name());
		ArrayList<IData> uav_observations = sim().getObservations(Actors.UAV.name());
		parseUAVStateFromUAV(uav_observations);
		
		switch ( (States) state() ) {
			case STREAMING_NORMAL:
				
				//Handle False Anomalies
				if(input.contains(TargetSightingFalseEvent.Outputs.TARGET_SIGHTED_FALSE)){
					_visible_anomalies.add(Outputs.VGUI_FALSE_POSITIVE);
				} else if ( input.contains(TargetSightingFalseEvent.Outputs.TARGET_SIGHTED_END) ) {
					_visible_anomalies.remove(Outputs.VGUI_FALSE_POSITIVE);
				}
				
				//Handle True Anomalies
				if(input.contains(TargetSightingTrueEvent.Outputs.TARGET_SIGHTED_TRUE)){
					_visible_anomalies.add(Outputs.VGUI_TRUE_POSITIVE);
				} else if ( input.contains(TargetSightingTrueEvent.Outputs.TARGET_SIGHTED_END) ) {
					_visible_anomalies.remove(Outputs.VGUI_TRUE_POSITIVE);
				}
					
				//Handle all VO input
				if(input.contains(VideoOperatorRole.Outputs.VO_END)){
					if ( input.contains(VideoOperatorRole.Outputs.VO_POSSIBLE_ANOMALY_DETECTED_T) ) {
						_verification_requests.add(Outputs.VGUI_VALIDATION_REQ_TRUE);
					} else if ( input.contains(VideoOperatorRole.Outputs.VO_POSSIBLE_ANOMALY_DETECTED_F) ) {
						_verification_requests.add(Outputs.VGUI_VALIDATION_REQ_FALSE);
					} else if ( input.contains(VideoOperatorRole.Outputs.VO_FLYBY_REQ_T) ) {
						sim().addOutput(Actors.OPERATOR_GUI.name(), VideoOperatorRole.Outputs.VO_FLYBY_REQ_T);
					} else if ( input.contains(VideoOperatorRole.Outputs.VO_FLYBY_REQ_F) ) {
						sim().addOutput(Actors.OPERATOR_GUI.name(), VideoOperatorRole.Outputs.VO_FLYBY_REQ_F);
					}
				}
				
				//Handle the MM inputs
				if ( input.contains(MissionManagerRole.Outputs.MM_END) ) {
					if ( input.contains(MissionManagerRole.Outputs.MM_FLYBY_REQ_F) ) {
						sim().addOutput(Actors.OPERATOR_GUI.name(), MissionManagerRole.Outputs.MM_FLYBY_REQ_F);
					} else if ( input.contains(MissionManagerRole.Outputs.MM_FLYBY_REQ_T) ) {
						sim().addOutput(Actors.OPERATOR_GUI.name(), MissionManagerRole.Outputs.MM_FLYBY_REQ_T);
					} else if ( input.contains(MissionManagerRole.Outputs.MM_ANOMALY_DISMISSED_F) ) {
						_verification_requests.remove(Outputs.VGUI_VALIDATION_REQ_FALSE);
					} else if ( input.contains(MissionManagerRole.Outputs.MM_ANOMALY_DISMISSED_T) ) {
						_verification_requests.remove(Outputs.VGUI_VALIDATION_REQ_TRUE);
					}
				}
				
				//Handle OGUI inputs
				if ( input.contains(OperatorRole.Outputs.OP_FLYBY_START_F) ) {
					_vgui_mode = Outputs.VGUI_FLYBY_F;
					_visible_anomalies.clear();
					nextState(States.STREAMING_FLYBY, 1);
				} else if ( input.contains(OperatorRole.Outputs.OP_FLYBY_START_T) ) {
					_vgui_mode = Outputs.VGUI_FLYBY_T;
					_visible_anomalies.clear();
					nextState(States.STREAMING_FLYBY, 1);
				}
				break;
			case STREAMING_FLYBY:
				//Stay in this state until we receive a message to leave the state
				
				//Handle the MM inputs
				if ( input.contains(MissionManagerRole.Outputs.MM_END) ) {
					if ( input.contains(MissionManagerRole.Outputs.MM_FLYBY_REQ_F) ) {
						sim().addOutput(Actors.OPERATOR_GUI.name(), MissionManagerRole.Outputs.MM_FLYBY_REQ_F);
					} else if ( input.contains(MissionManagerRole.Outputs.MM_FLYBY_REQ_T) ) {
						sim().addOutput(Actors.OPERATOR_GUI.name(), MissionManagerRole.Outputs.MM_FLYBY_REQ_T);
					} else if ( input.contains(MissionManagerRole.Outputs.MM_ANOMALY_DISMISSED_F) ) {
						_verification_requests.remove(Outputs.VGUI_VALIDATION_REQ_FALSE);
					} else if ( input.contains(MissionManagerRole.Outputs.MM_ANOMALY_DISMISSED_T) ) {
						_verification_requests.remove(Outputs.VGUI_VALIDATION_REQ_TRUE);
					}
				}
				
				//Handle all VO input
				if(input.contains(VideoOperatorRole.Outputs.VO_END)){
					if ( input.contains(VideoOperatorRole.Outputs.VO_FLYBY_END_FAILED) ) {
						sim().addOutput(Actors.OPERATOR_GUI.name(), VideoOperatorRole.Outputs.VO_FLYBY_END_FAILED);
						nextState(States.STREAMING_NORMAL,1);
						_vgui_mode = Outputs.VGUI_NORMAL;
					} else if ( input.contains(VideoOperatorRole.Outputs.VO_FLYBY_END_SUCCESS) ) {
						sim().addOutput(Actors.OPERATOR_GUI.name(), VideoOperatorRole.Outputs.VO_FLYBY_END_SUCCESS);
						nextState(States.STREAMING_NORMAL,1);
						_vgui_mode = Outputs.VGUI_NORMAL;
					}
				}
				
				//Handle FlybyAnomaly
				if ( _visible_anomalies.isEmpty() && _uav_video_feed == UAVVideoFeed.Outputs.VF_SIGNAL_OK ) {
					if ( uav_observations.contains(FlybyAnomaly.Outputs.FLYBY_ANOMALY_F) ) {
						_visible_anomalies.add(FlybyAnomaly.Outputs.FLYBY_ANOMALY_F);
					} else if ( uav_observations.contains(FlybyAnomaly.Outputs.FLYBY_ANOMALY_T) ) {
						_visible_anomalies.add(FlybyAnomaly.Outputs.FLYBY_ANOMALY_T);
					}
				} else if ( _uav_video_feed == UAVVideoFeed.Outputs.VF_SIGNAL_NONE ) {
					_visible_anomalies.clear();
				}
				break;
			default:
				break;
		}
    }

    /**
     * /////////////////////////////PRIVATE HELPER METHODS///////////////////////////////////////////
     */
	private void setObservations() {
		sim().addObservation(_vgui_mode, this.name());
		sim().addObservations(_verification_requests, this.name());
		if ( _uav_video_feed == UAVVideoFeed.Outputs.VF_SIGNAL_OK )
			sim().addObservations(_visible_anomalies, this.name());
		sim().addObservation(_uav_video_feed, this.name());
		
	}
	
	private void parseUAVStateFromUAV(ArrayList<IData> observations) {
		//TODO Make sure the operator gui is sending the UAV output
		for( IData data : observations ) {
			if ( data instanceof UAVRole.Outputs ) {
				_uav_state = (UAVRole.Outputs) data;
			} else if ( data instanceof UAVVideoFeed.Outputs ) {
				_uav_video_feed = (UAVVideoFeed.Outputs) data;
			}
			break;
		}
	}
}
