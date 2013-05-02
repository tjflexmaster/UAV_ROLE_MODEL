package WiSAR.Agents;

import java.util.ArrayList;

import CUAS.Simulator.Actor;
import CUAS.Simulator.IData;
import CUAS.Simulator.IStateEnum;
import CUAS.Simulator.Simulator;
import WiSAR.Actors;
import WiSAR.Durations;
import WiSAR.Agents.OperatorRole;
import WiSAR.submodule.UAVFlightPlan;

public class OperatorGUIRole extends Actor {
 
	int flight_paths = 0;
	int _unacknowledged_paths_completed = 0;
	
    public enum Outputs implements IData
    {
		ACK_OP_GUI,
		DEPARTING,
		GOOD_PATH,
		RETURNING,
		LOITERING,
		LANDED,
		TAKE_OFF,
		RETURN, 
		LOITER, 
		LAND, 
		LOW_BATTERY, 
		NO_PATH, 
		UAV_LOST, 
		IN_AIR, 
		UAV_BAD_HAG, 
		LANDING, 
		BAD_PATH, 
		ACK_OGUI, 
		
		/**
		 * UAV outputs
		 */
		OGUI_PATH_NEW,
		OGUI_PATH_END,
		UAV_FOUND, 
		RESUME_PATH,
		
		/**
		 * VGUI outputs
		 */
		OGUI_FLYBY_T,
		OGUI_FLYBY_F, 
		OGUI_FLYBY_END, 
		
		/**
		 * OP outputs
		 */
		OGUI_PATH_COMPLETE,
		OGUI_IDLE,
		OGUI_TAKING_OFF,
		OGUI_IN_AIR,
		OGUI_LANDING,
		OGUI_CRASHING,
		OGUI_LOST,
		OGUI_PATH_NO
    }
   
    public enum States implements IStateEnum
    {
        UAV_IDLE,
        RX_OP,
        UAV_TAKING_OFF,
        UAV_IN_AIR,
        UAV_LANDING,
        UAV_CRASHING,
        UAV_LOST
    }
    
	public OperatorGUIRole()
	{
		name( Actors.OPERATOR_GUI.name() );
		nextState(States.UAV_IDLE, 1);
		
	}

   @Override
    public void processNextState() {
        if ( nextStateTime() != Simulator.getInstance().getTime() ) {
            return;
        }
        state(nextState());
        switch ((States) nextState()) {
	        case RX_OP:
	        	//nextState(States.UAV_IN_AIR,sim().duration(Durations.OGUI_RX_DUR.range()));
	        	//break;
        	default:
	        	nextState(null,0);
	        	break;
        }
    }

	@Override
	public void processInputs() {
		//Pull Input and any observations that need to be made from the simulator
		ArrayList<IData> input = sim().getInput(this.name());
		ArrayList<IData> uav_data = sim().getObservations(Actors.UAV.name());
    	
		switch ( (States) state() ) {
			case UAV_IDLE:
				if (input.contains(OperatorRole.Outputs.OP_POKE)) {
					sim().addOutput(Actors.OPERATOR.name(), Outputs.ACK_OP_GUI);
					nextState(States.RX_OP, 1);
				}
				break;
			case RX_OP:
				if(input.contains(OperatorRole.Outputs.OP_END)){
					if (input.contains(OperatorRole.Outputs.OP_PATH_NEW)) {
						flight_paths++;
						sim().addOutput(Actors.UAV.name(), Outputs.GOOD_PATH);
					}
					if (input.contains(OperatorRole.Outputs.TAKE_OFF)) {
						sim().addOutput(Actors.UAV.name(), Outputs.TAKE_OFF);
						nextState(States.UAV_TAKING_OFF,1);
					
//					} else if (_input.contains(OperatorRole.Outputs.RETURN)) {
//						sim().addOutput(Actors.UAV.name(), Outputs.RETURN);
//						_output.add(Outputs.RETURNING);
//						nextState(States.UAV_IN_AIR,1);
					} else if (input.contains(OperatorRole.Outputs.LOITER)) {
						sim().addOutput(Actors.UAV.name(), Outputs.LOITER);
						nextState(States.UAV_IN_AIR,1);
					} else if (input.contains(OperatorRole.Outputs.LAND)) {
						sim().addOutput(Actors.UAV.name(), Outputs.LAND);
						nextState(States.UAV_IDLE,1);
					}else if(uav_data.contains(UAVRole.Outputs.UAV_LANDED)){
						nextState(States.UAV_IDLE,1);
					}else if(input.contains(OperatorRole.Outputs.OP_SEARCH_AOI_COMPLETE_ACK)){
						_unacknowledged_paths_completed--;
					}else{
						nextState(States.UAV_IN_AIR,1);
					}
				}
				break;
			case UAV_TAKING_OFF:
				if(uav_data.contains(UAVRole.Outputs.UAV_FLYING) || uav_data.contains(UAVRole.Outputs.UAV_LOITERING)){
					nextState(States.UAV_IN_AIR,1);
				}
//				if (_input.contains(EventEnum.UAV_LOW_BATTERY)) {
//					sim().addOutput(Actors.OPERATOR.name(), Outputs.LOW_BATTERY);
//					nextState(States.UAV_IDLE,1);
//				} else if (!_input.contains(OperatorRole.Outputs.GOOD_PATH)) {
//					sim().addOutput(Actors.OPERATOR.name(), Outputs.NO_PATH);
//					nextState(States.UAV_IDLE,1);
//				} else if (_input.contains(EventEnum.UAV_LOST_SIGNAL)) {
//					sim().addOutput(Actors.OPERATOR.name(), Outputs.UAV_LOST);
//					nextState(States.UAV_IDLE,1);
//				} else if (_input.contains(EventEnum.UAV_GOOD_HAG)) {
//					sim().addOutput(Actors.OPERATOR.name(), Outputs.IN_AIR);
//					nextState(States.UAV_IN_AIR,1);
//				}
				break;
			case UAV_IN_AIR:
				if(flight_paths > 0 && uav_data.contains(UAVFlightPlan.Outputs.UAV_FLIGHT_PLAN_NO)){
					flight_paths--;
					sim().addOutput(Actors.UAV_FLIGHT_PLAN.name(), Outputs.OGUI_PATH_NEW);
				}else if(uav_data.contains(UAVFlightPlan.Outputs.UAV_FLIGHT_PLAN_COMPLETE)){
					_unacknowledged_paths_completed++;
					if(flight_paths > 0){
						flight_paths--;
						sim().addOutput(Actors.UAV_FLIGHT_PLAN.name(), Outputs.OGUI_PATH_NEW);
					}
					nextState(States.UAV_IN_AIR,1);
				}
//				if (_input.contains(OperatorRole.Outputs.OP_POKE)) {
//					sim().addOutput(Actors.OPERATOR.name(), Outputs.ACK_OP_GUI);
//					nextState(States.RX_OP,1);
//				} else if (_input.contains(EventEnum.UAV_LOST_SIGNAL)) {
//					sim().addOutput(Actors.OPERATOR.name(), Outputs.UAV_LOST);
//					nextState(States.UAV_LOST,1);
//				} else if (_input.contains(EventEnum.UAV_BAD_HAG)) {
//					sim().addOutput(Actors.OPERATOR.name(), Outputs.UAV_BAD_HAG);
//					nextState(States.UAV_LOST,1);
//				} else if (_input.contains(EventEnum.UAV_ARRIVED)) {
//					sim().addOutput(Actors.OPERATOR.name(), Outputs.LANDING);
//					nextState(States.UAV_LANDING,1);
//				} else if (_input.contains(EventEnum.UAV_LOW_BATTERY)) {
//					sim().addOutput(Actors.OPERATOR.name(), Outputs.LOW_BATTERY);
//					nextState(States.UAV_IN_AIR,1);
//				} else if (_input.contains(UAVRole.Outputs.BAD_PATH)) {
//					sim().addOutput(Actors.OPERATOR.name(), Outputs.BAD_PATH);
//					nextState(States.UAV_IN_AIR,1);
//				}
				break;
			case UAV_LANDING:
//				if (_input.contains(EventEnum.UAV_LOST_SIGNAL)) {
//					sim().addOutput(Actors.OPERATOR.name(), Outputs.UAV_LOST);
//					nextState(States.UAV_LOST,1);
//				} else if (_input.contains(UAVRole.Outputs.NO_HAG)) {
//					sim().addOutput(Actors.OPERATOR.name(), Outputs.LANDED);
//					nextState(States.UAV_LOST,1);
//				}
				break;
			case UAV_CRASHING:
//				if (_input.contains(OperatorRole.Outputs.OP_POKE)) {
//					sim().addOutput(Actors.OPERATOR.name(), Outputs.ACK_OGUI);
//					nextState(States.RX_OP,1);
//				} else if (_input.contains(EventEnum.UAV_LOST_SIGNAL)) {
//					sim().addOutput(Actors.OPERATOR.name(), Outputs.UAV_LOST);
//					nextState(States.UAV_LOST,1);
//				}
				break;
			case UAV_LOST:
//				if (_input.contains(EventEnum.UAV_LOST_SIGNAL)) {
//					sim().addOutput(Actors.OPERATOR.name(), Outputs.UAV_FOUND);
//					nextState(States.UAV_IN_AIR,1);
//				}
				break;
			default:
				break;
		}
		setObservables();
    }

	private void setObservables(){
		if(_unacknowledged_paths_completed > 0){
			sim().addObservation(Outputs.OGUI_PATH_COMPLETE, this.name());
		}
		IData state = Outputs.OGUI_IDLE;
		switch((States)state()){
		case UAV_LOST:
			state = Outputs.OGUI_LOST;
			break;
		case UAV_CRASHING:
			state = Outputs.OGUI_CRASHING;
			break;
		case UAV_IN_AIR:
			state = Outputs.OGUI_IN_AIR;
			break;
		case UAV_LANDING:
			state = Outputs.OGUI_LANDING;
			break;
		case UAV_TAKING_OFF:
			state = Outputs.OGUI_TAKING_OFF;
			break;
		default:
		}
		sim().addObservation(state, this.name());
	}

    /**
     * /////////////////////////////PRIVATE HELPER METHODS///////////////////////////////////////////
     */
}
