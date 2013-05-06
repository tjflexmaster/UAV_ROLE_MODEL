package WiSAR.Agents;

import java.util.ArrayList;

import CUAS.Simulator.Actor;
import CUAS.Simulator.IData;
import CUAS.Simulator.IStateEnum;
import CUAS.Simulator.Simulator;
import WiSAR.Actors;
import WiSAR.Agents.OperatorRole;
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
	//TODO make end flyby commands appear observable
	
    public enum Outputs implements IData
    {
    	//TODO Pass the output from the UAV directly to the operator
    	
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
	}

   @Override
    public void processNextState() {
        if ( nextStateTime() != Simulator.getInstance().getTime() ) {
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
		//TODO Handle Flyby Requests and end flyby commands
		
		
		switch ( (States) state() ) {
			case NORMAL:
				//Decide to go to alarm or stay in normal
				if ( isAlarm() )
					nextState(States.ALARM, 1);
				
				//Send any commands from the Operator to the UAV
				
				break;
			case ALARM:
				//Decide to go to normal
				if ( !isAlarm() )
					nextState(States.NORMAL, 1);
				
				//Send any commands from the Operator to the UAV
				
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
		
		//Also make the flyby requests observable
		sim().addObservations(_flyby_requests, this.name());
		//TODO make end flyby commands observable
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
			} else if ( _uav_state == UAVRole.Outputs.UAV_FLYING_FLYBY ) {
				//TODO show an alert if end flyby has been received
			}
			
		}
		
		return false;
		
	}
	
	
	private void parseUAVStateFromUAV(ArrayList<IData> observations) {
		//TODO Make sure the operator gui is sending the UAV output
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
			}
			break;
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
					//TODO add case for Flyby request
					//TODO add case for ending a FLYBY
					default:
						//Send the data to the UAV
						sim().addOutput(Actors.UAV.name(), data);
						break;
				}
				
			} else if ( data instanceof MissionManagerRole.Outputs ) {
				switch((MissionManagerRole.Outputs) data) {
					//TODO add case for Flyby request
					default:
						//Send the data to the UAV
						sim().addOutput(Actors.UAV.name(), data);
						break;
				}
			}
		}
	}
}
