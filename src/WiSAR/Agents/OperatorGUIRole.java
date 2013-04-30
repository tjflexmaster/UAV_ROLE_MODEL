package WiSAR.Agents;

import java.util.ArrayList;

import CUAS.Simulator.Actor;
import CUAS.Simulator.IData;
import CUAS.Simulator.IStateEnum;
import CUAS.Simulator.Simulator;
import WiSAR.Durations;
import WiSAR.Agents.OperatorRole;
import WiSAR.Agents.Roles;

public class OperatorGUIRole extends Actor {
 
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
		UAV_FOUND
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
		name( Roles.OPERATOR_GUI.name() );
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
	        	nextState(States.UAV_IN_AIR,sim().duration(Durations.OGUI_RX_DUR.range()));
	        	break;
        	default:
	        	nextState(null,0);
	        	break;
        }
    }

	@Override
	public void processInputs() {
    	_output.clear();
		switch ( (States) state() ) {
			case UAV_IDLE:
				if (_input.contains(OperatorRole.Outputs.OP_POKE)) {
					sim().addOutput(Roles.OPERATOR.name(), Outputs.ACK_OP_GUI);
					nextState(States.RX_OP, 1);
				}
				break;
			case RX_OP:
				if(_input.contains(OperatorRole.Outputs.OP_END));{
					if (_input.contains(OperatorRole.Outputs.TAKE_OFF)) {
						sim().addOutput(Roles.UAV.name(), Outputs.TAKE_OFF);
						_output.add(Outputs.DEPARTING);
						nextState(States.UAV_IN_AIR,1);
					} else if (_input.contains(OperatorRole.Outputs.GOOD_PATH)) {
						sim().addOutput(Roles.UAV.name(), Outputs.GOOD_PATH);
						_output.add(Outputs.GOOD_PATH);
						nextState(States.UAV_IN_AIR,1);
					} else if (_input.contains(OperatorRole.Outputs.RETURN)) {
						sim().addOutput(Roles.UAV.name(), Outputs.RETURN);
						_output.add(Outputs.RETURNING);
						nextState(States.UAV_IN_AIR,1);
					} else if (_input.contains(OperatorRole.Outputs.LOITER)) {
						sim().addOutput(Roles.UAV.name(), Outputs.LOITER);
						_output.add(Outputs.LOITERING);
						nextState(States.UAV_IN_AIR,1);
					} else if (_input.contains(OperatorRole.Outputs.LAND)) {
						sim().addOutput(Roles.UAV.name(), Outputs.LAND);
						_output.add(Outputs.LANDED);
						nextState(States.UAV_IDLE,1);
					}
				}
				break;
			case UAV_TAKING_OFF:
				if (_input.contains(EventEnum.UAV_LOW_BATTERY)) {
					sim().addOutput(Roles.OPERATOR.name(), Outputs.LOW_BATTERY);
					nextState(States.UAV_IDLE,1);
				} else if (!_input.contains(OperatorRole.Outputs.GOOD_PATH)) {
					sim().addOutput(Roles.OPERATOR.name(), Outputs.NO_PATH);
					nextState(States.UAV_IDLE,1);
				} else if (_input.contains(EventEnum.UAV_LOST_SIGNAL)) {
					sim().addOutput(Roles.OPERATOR.name(), Outputs.UAV_LOST);
					nextState(States.UAV_IDLE,1);
				} else if (_input.contains(EventEnum.UAV_GOOD_HAG)) {
					sim().addOutput(Roles.OPERATOR.name(), Outputs.IN_AIR);
					nextState(States.UAV_IN_AIR,1);
				}
				break;
			case UAV_IN_AIR:
				if (_input.contains(OperatorRole.Outputs.OP_POKE)) {
					sim().addOutput(Roles.OPERATOR.name(), Outputs.ACK_OP_GUI);
					nextState(States.RX_OP,1);
				} else if (_input.contains(EventEnum.UAV_LOST_SIGNAL)) {
					sim().addOutput(Roles.OPERATOR.name(), Outputs.UAV_LOST);
					nextState(States.UAV_LOST,1);
				} else if (_input.contains(EventEnum.UAV_BAD_HAG)) {
					sim().addOutput(Roles.OPERATOR.name(), Outputs.UAV_BAD_HAG);
					nextState(States.UAV_LOST,1);
				} else if (_input.contains(EventEnum.UAV_ARRIVED)) {
					sim().addOutput(Roles.OPERATOR.name(), Outputs.LANDING);
					nextState(States.UAV_LANDING,1);
				} else if (_input.contains(EventEnum.UAV_LOW_BATTERY)) {
					sim().addOutput(Roles.OPERATOR.name(), Outputs.LOW_BATTERY);
					nextState(States.UAV_IN_AIR,1);
				} else if (_input.contains(UAVRole.Outputs.BAD_PATH)) {
					sim().addOutput(Roles.OPERATOR.name(), Outputs.BAD_PATH);
					nextState(States.UAV_IN_AIR,1);
				}
				break;
			case UAV_LANDING:
				if (_input.contains(EventEnum.UAV_LOST_SIGNAL)) {
					sim().addOutput(Roles.OPERATOR.name(), Outputs.UAV_LOST);
					nextState(States.UAV_LOST,1);
				} else if (_input.contains(UAVRole.Outputs.NO_HAG)) {
					sim().addOutput(Roles.OPERATOR.name(), Outputs.LANDED);
					nextState(States.UAV_LOST,1);
				}
				break;
			case UAV_CRASHING:
				if (_input.contains(OperatorRole.Outputs.OP_POKE)) {
					sim().addOutput(Roles.OPERATOR.name(), Outputs.ACK_OGUI);
					nextState(States.RX_OP,1);
				} else if (_input.contains(EventEnum.UAV_LOST_SIGNAL)) {
					sim().addOutput(Roles.OPERATOR.name(), Outputs.UAV_LOST);
					nextState(States.UAV_LOST,1);
				}
				break;
			case UAV_LOST:
				if (_input.contains(EventEnum.UAV_LOST_SIGNAL)) {
					sim().addOutput(Roles.OPERATOR.name(), Outputs.UAV_FOUND);
					nextState(States.UAV_IN_AIR,1);
				}
				break;
			default:
				break;
		}
    }
package WiSAR.Agents;

import java.util.ArrayList;

import CUAS.Simulator.Actor;
import CUAS.Simulator.IData;
import CUAS.Simulator.IStateEnum;
import CUAS.Simulator.Simulator;
import WiSAR.Durations;
import WiSAR.Agents.OperatorRole;
import WiSAR.Agents.Roles;

public class OperatorGUIRole extends Actor {
 
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
		UAV_FOUND
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
		name( Roles.OPERATOR_GUI.name() );
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
	        	nextState(States.UAV_IN_AIR,sim().duration(Durations.OGUI_RX_DUR.range()));
	        	break;
        	default:
	        	nextState(null,0);
	        	break;
        }
    }

    /**
     * /////////////////////////////PRIVATE HELPER METHODS///////////////////////////////////////////
     */
}
	@Override
	public void processInputs() {
    	_output.clear();
		switch ( (States) state() ) {
			case UAV_IDLE:
				if (_input.contains(OperatorRole.Outputs.POKE_OP)) {
					sim().addOutput(Roles.OPERATOR.name(), Outputs.ACK_OP_GUI);
					nextState(States.RX_OP, 1);
				}
				break;
			case RX_OP:
				if(_input.contains(OperatorRole.Outputs.END_OP));{
					if (_input.contains(OperatorRole.Outputs.TAKE_OFF)) {
						sim().addOutput(Roles.UAV.name(), Outputs.TAKE_OFF);
						_output.add(Outputs.DEPARTING);
						nextState(States.UAV_IN_AIR,1);
					} else if (_input.contains(OperatorRole.Outputs.GOOD_PATH)) {
						sim().addOutput(Roles.UAV.name(), Outputs.GOOD_PATH);
						_output.add(Outputs.GOOD_PATH);
						nextState(States.UAV_IN_AIR,1);
					} else if (_input.contains(OperatorRole.Outputs.RETURN)) {
						sim().addOutput(Roles.UAV.name(), Outputs.RETURN);
						_output.add(Outputs.RETURNING);
						nextState(States.UAV_IN_AIR,1);
					} else if (_input.contains(OperatorRole.Outputs.LOITER)) {
						sim().addOutput(Roles.UAV.name(), Outputs.LOITER);
						_output.add(Outputs.LOITERING);
						nextState(States.UAV_IN_AIR,1);
					} else if (_input.contains(OperatorRole.Outputs.LAND)) {
						sim().addOutput(Roles.UAV.name(), Outputs.LAND);
						_output.add(Outputs.LANDED);
						nextState(States.UAV_IDLE,1);
					}
				}
				break;
			case UAV_TAKING_OFF:
				if (_input.contains(EventEnum.UAV_LOW_BATTERY)) {
					sim().addOutput(Roles.OPERATOR.name(), Outputs.LOW_BATTERY);
					nextState(States.UAV_IDLE,1);
				} else if (!_input.contains(OperatorRole.Outputs.GOOD_PATH)) {
					sim().addOutput(Roles.OPERATOR.name(), Outputs.NO_PATH);
					nextState(States.UAV_IDLE,1);
				} else if (_input.contains(EventEnum.UAV_LOST_SIGNAL)) {
					sim().addOutput(Roles.OPERATOR.name(), Outputs.UAV_LOST);
					nextState(States.UAV_IDLE,1);
				} else if (_input.contains(EventEnum.UAV_GOOD_HAG)) {
					sim().addOutput(Roles.OPERATOR.name(), Outputs.IN_AIR);
					nextState(States.UAV_IN_AIR,1);
				}
				break;
			case UAV_IN_AIR:
				if (_input.contains(OperatorRole.Outputs.POKE_OP)) {
					sim().addOutput(Roles.OPERATOR.name(), Outputs.ACK_OP_GUI);
					nextState(States.RX_OP,1);
				} else if (_input.contains(EventEnum.UAV_LOST_SIGNAL)) {
					sim().addOutput(Roles.OPERATOR.name(), Outputs.UAV_LOST);
					nextState(States.UAV_LOST,1);
				} else if (_input.contains(EventEnum.UAV_BAD_HAG)) {
					sim().addOutput(Roles.OPERATOR.name(), Outputs.UAV_BAD_HAG);
					nextState(States.UAV_LOST,1);
				} else if (_input.contains(EventEnum.UAV_ARRIVED)) {
					sim().addOutput(Roles.OPERATOR.name(), Outputs.LANDING);
					nextState(States.UAV_LANDING,1);
				} else if (_input.contains(EventEnum.UAV_LOW_BATTERY)) {
					sim().addOutput(Roles.OPERATOR.name(), Outputs.LOW_BATTERY);
					nextState(States.UAV_IN_AIR,1);
				} else if (_input.contains(UAVRole.Outputs.BAD_PATH)) {
					sim().addOutput(Roles.OPERATOR.name(), Outputs.BAD_PATH);
					nextState(States.UAV_IN_AIR,1);
				}
				break;
			case UAV_LANDING:
				if (_input.contains(EventEnum.UAV_LOST_SIGNAL)) {
					sim().addOutput(Roles.OPERATOR.name(), Outputs.UAV_LOST);
					nextState(States.UAV_LOST,1);
				} else if (_input.contains(UAVRole.Outputs.NO_HAG)) {
					sim().addOutput(Roles.OPERATOR.name(), Outputs.LANDED);
					nextState(States.UAV_LOST,1);
				}
				break;
			case UAV_CRASHING:
				if (_input.contains(OperatorRole.Outputs.POKE_OP)) {
					sim().addOutput(Roles.OPERATOR.name(), Outputs.ACK_OGUI);
					nextState(States.RX_OP,1);
				} else if (_input.contains(EventEnum.UAV_LOST_SIGNAL)) {
					sim().addOutput(Roles.OPERATOR.name(), Outputs.UAV_LOST);
					nextState(States.UAV_LOST,1);
				}
				break;
			case UAV_LOST:
				if (_input.contains(EventEnum.UAV_LOST_SIGNAL)) {
					sim().addOutput(Roles.OPERATOR.name(), Outputs.UAV_FOUND);
					nextState(States.UAV_IN_AIR,1);
				}
				break;
			default:
				break;
		}
    }


	@Override
	public void addInput(ArrayList<IData> data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ArrayList<IData> getObservations() {
		// TODO Auto-generated method stub
		return null;
	}

    /**
     * /////////////////////////////PRIVATE HELPER METHODS///////////////////////////////////////////
     */
}