package WiSAR.Agents;

import java.util.ArrayList;

import CUAS.Simulator.Actor;
import CUAS.Simulator.IData;
import CUAS.Simulator.IStateEnum;
import CUAS.Simulator.Simulator;
import WiSAR.Actors;
import WiSAR.IPriority;
import WiSAR.Agents.OperatorRole;
import WiSAR.Agents.UAVRole.Outputs;
import WiSAR.Agents.UAVRole.States;
import WiSAR.submodule.UAVBattery;
import WiSAR.submodule.UAVFlightPlan;
import WiSAR.submodule.UAVHeightAboveGround;
import WiSAR.submodule.UAVSignal;

public class OperatorGUIRole extends Actor {
 
	int flight_paths = 0;
	int _unacknowledged_paths_completed = 0;
	
	ArrayList<IData> _uav_observations;
	
	UAVRole.Outputs _uav_state;
	UAVSignal.Outputs _uav_signal;
	UAVHeightAboveGround.Outputs _uav_hag;
	UAVFlightPlan.Outputs _uav_flight_plan;
	UAVBattery.Outputs _uav_battery;
	
	ArrayList<IData> _flyby_requests;
	IData _flyby_end_cmd;
	
    public enum Outputs implements IData
    {
    	/**
    	 * Output GUI State
    	 */
    	OGUI_STATE_NORMAL,
    	OGUI_STATE_ALARM,
    	
    }
   
    public enum States implements IStateEnum
    {
    	NORMAL,
    	ALARM,
    	AUDIBLE_ALARM
    }
    
	public OperatorGUIRole()
	{
		name( Actors.OPERATOR_GUI.name() );
		nextState(States.NORMAL, 1);
		_flyby_end_cmd = null;
		_flyby_requests = new ArrayList<IData>();
		_uav_state = UAVRole.Outputs.UAV_READY;
	}

   @Override
    public void processNextState() {
        if ( nextStateTime() != Simulator.getInstance().getTime() ) {
        	setObservables();
            return;
        }
        state(nextState());
        switch ((States) nextState()) {
	        case NORMAL:
	        	//Stay in this state
	        case ALARM:
	        	//Stay in this state
	        case AUDIBLE_ALARM:
	        	/**
	        	 * Leave this out for now.
	        	 * 
	        	 * At some point if the GUI has an alarm and does not receive a 
	        	 * poke for some duration then it will give an audible alarm.
	        	 */
        	default:
	        	nextState(null,0);
	        	break;
        }
        
        //Observables get replaced after this method so make sure that the latest observables are available
        setObservables();
    }

	@Override
	public void processInputs() {
		//Pull Input and any observations that need to be made from the simulator
		_uav_observations = sim().getObservations(Actors.UAV.name());
		parseUAVStateFromUAV(_uav_observations);
		
		//Handle Inputs
		ArrayList<IData> input = sim().getInput(this.name());
		handleOPInputs(input);
		handleFlybyRequests(input);
		
		switch ( (States) state() ) {
			case NORMAL:
				//Decide to go to alarm or stay in normal
				if ( isAlarm() )
					nextState(States.ALARM, 1);
				break;
			case ALARM:
				//Decide to go to normal
				if ( !isAlarm() )
					nextState(States.NORMAL, 1);
				break;
			default:
				break;
		}
    }

	private void setObservables(){
		sim().addObservation(_uav_battery, this.name());
		sim().addObservation(_uav_flight_plan, this.name());
		sim().addObservation(_uav_hag, this.name());
		sim().addObservation(_uav_signal, this.name());
		sim().addObservation(_uav_state, this.name());
		
		IData _state = Outputs.OGUI_STATE_NORMAL;
		switch((States) state() ) {
			case NORMAL:
				_state = Outputs.OGUI_STATE_NORMAL;
				break;
			case ALARM:
				_state = Outputs.OGUI_STATE_ALARM;
				break;
			case AUDIBLE_ALARM:
				_state = Outputs.OGUI_STATE_ALARM;
				break;
		}
		sim().addObservation(_state , this.name());
		
		sim().addObservations(_flyby_requests, this.name());
		if ( _flyby_end_cmd != null )
			sim().addObservation(_flyby_end_cmd, this.name());
	}

    /**
     * /////////////////////////////PRIVATE HELPER METHODS///////////////////////////////////////////
     */
	private boolean isAlarm()
	{
		//Look at the UAV to determine if we should be alarmed
		if ( _uav_signal == UAVSignal.Outputs.SIGNAL_LOST ) {
			return true;
		} else if ( isAirborne() ) {
			if (_uav_battery == UAVBattery.Outputs.BATTERY_LOW ||
					_uav_hag == UAVHeightAboveGround.Outputs.HAG_LOW ) {
				return true;
			} else if ( _flyby_end_cmd != null ) {
				//We are in alarm state if there is  flyby end cmd
				return true;
			}
			
		}
		
		return false;
		
	}
	
	
	private void parseUAVStateFromUAV(ArrayList<IData> observations) {
		for( IData data : observations ) {
			if ( data instanceof UAVRole.Outputs ) {
				_uav_state = (WiSAR.Agents.UAVRole.Outputs) data;
			} else if ( data instanceof UAVSignal.Outputs ) {
				_uav_signal = (WiSAR.submodule.UAVSignal.Outputs) data;
			} else if ( data instanceof UAVBattery.Outputs ) {
				_uav_battery = (WiSAR.submodule.UAVBattery.Outputs) data;
			} else if (data instanceof UAVFlightPlan.Outputs ) {
				if( data == UAVFlightPlan.Outputs.UAV_FLIGHT_PLAN_NO || 
						data == UAVFlightPlan.Outputs.UAV_FLIGHT_PLAN_YES ) {
					_uav_flight_plan = (WiSAR.submodule.UAVFlightPlan.Outputs) data;
				}
			} else if ( data instanceof UAVHeightAboveGround.Outputs ) {
				_uav_hag = (WiSAR.submodule.UAVHeightAboveGround.Outputs) data;
			} else {
				//ignore video feed
				//break;
			}
		}
	}
	
	private boolean isAirborne()
	{
		switch( _uav_state ) {
			case UAV_FLYING_FLYBY:
			case UAV_FLYING_NORMAL:
			case UAV_LOITERING:
			case UAV_LANDING:
			case UAV_TAKE_OFF:
				return true;
			default:
				return false;
		}
	}
	
	private void handleOPInputs(ArrayList<IData> input)
	{
		for(IData data : input) {
			if ( data instanceof OperatorRole.Outputs ) {
				switch((OperatorRole.Outputs) data) {
					case OP_FLYBY_START_F:
					case OP_FLYBY_START_T:
						sim().addOutput(Actors.VIDEO_OPERATOR_GUI.name(), data);
						sim().addOutput(Actors.UAV.name(), data);
						break;
					case OP_FLYBY_END:
						sim().addOutput(Actors.UAV.name(), data);
						_flyby_end_cmd = null;
						break;
					case OP_LAND:
					case OP_LOITER:
					case OP_MODIFY_FLIGHT_PLAN:
					case OP_NEW_FLIGHT_PLAN:
					case OP_RESUME:
					case OP_TAKE_OFF:
						//Send the data to the UAV
						sim().addOutput(Actors.UAV.name(), data);
						break;
				}
			}
		}
	}
	
	private void handleFlybyRequests(ArrayList<IData> input)
	{
		for(IData data : input) {
			if ( data instanceof VideoOperatorRole.Outputs ) {
				switch((VideoOperatorRole.Outputs) data) {
					case VO_FLYBY_REQ_T:
					case VO_FLYBY_REQ_F:
						_flyby_requests.add(data);
						break;
					case VO_FLYBY_END_SUCCESS:
					case VO_FLYBY_END_FAILED:
						
						break;
					default:
						break;
				}
				
			} else if ( data instanceof MissionManagerRole.Outputs ) {
				switch((MissionManagerRole.Outputs) data) {
					case MM_FLYBY_REQ_F:
					case MM_FLYBY_REQ_T:
						_flyby_requests.add(data);
						break;
					default:
						break;
				}
			}
		}
	}
}
