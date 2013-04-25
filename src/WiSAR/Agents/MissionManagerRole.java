package WiSAR.Agents;

import java.util.ArrayList;

import NewModel.Events.IEvent;
import NewModel.Roles.Role;
import NewModel.Simulation.ICommunicate;
import NewModel.Simulation.IInputEnum;
import NewModel.Simulation.IOutputEnum;
import NewModel.Simulation.IStateEnum;
import NewModel.Simulation.Simulator;
import WiSAR.Durations;

public class MissionManagerRole extends Role {

	//INTERNAL VARS
	ArrayList<IInputEnum> temp_inputs = new ArrayList<IInputEnum>();
	
	/**
	 * Setup Inputs and Outputs
	 */
	public enum Inputs implements IInputEnum
	{
		/**
		 * PS Inputs
		 */
		SEARCH_AOI,
		TERMINATE_SEARCH,
		
		/**
		 * Search Inputs (Only received during RX)
		 */
		SEARCH_AOI_SIGHTING,
		SEARCH_AOI_COMPLETE,
		SEARCH_AOI_FAILED,
		
		/**
		 * Communication Inputs
		 */
		POKE_PS,
		BUSY_PS,
		ACK_PS,
		END_PS,
		POKE_OPERATOR,
		BUSY_OPERATOR,
		ACK_OPERATOR,
		END_OPERATOR
	}
	
	public enum Outputs implements IOutputEnum
	{
		SEARCH_AOI,
		SEARCH_TERMINATED
	}
	
	/**
	 * Define Role States
	 * @author TJ-ASUS
	 *
	 */
	public enum States implements IStateEnum
	{
		IDLE,
		POKE_PS,
//		MM_POKE_OPERATOR,
		TX_PS,
//		MM_TX_OPERATOR,
		END_PS,
//		MM_END_OPERATOR,
//		MM_ACK_PS,
//		MM_ACK_OPERATOR,
		RX_PS,
//		MM_RX_OPERATOR
	}
	
	
	public MissionManagerRole()
	{
		name(Roles.MISSION_MANAGER.name());
		nextState(States.IDLE, 1);
	}
	
	
	/**
	 * IRole Methods	
	 */
	@Override
	public boolean processNextState() {
		//Is our next state now?
		if ( nextStateTime() != Simulator.getInstance().getTime() ) {
			return false;
		}
		
		//Update to the next state
		state(nextState());
		
		//Now determine what our next state will be
		//Each state has a designated duration
		
		//If a state isn't included then it doesn't deviate from the default
		switch((States) nextState()) {
			case IDLE:
				//TODO Look at my TODO List and see if I need to do something more
				nextState(null, 0);
				break;
			case POKE_PS:
				simulator().addInput(Roles.PARENT_SEARCH.name(), ParentSearch.Inputs.POKE_MM);
				nextState(States.IDLE, simulator().duration(Durations.MM_POKE_DUR.range()));
				break;
			case TX_PS:
				//TODO change this duration based on the data being transmitted
//				nextState(States.END_PS, simulator().duration(Durations.MM_TX_SIGHTING_PS_DUR.range()));
//				nextState(States.END_PS, simulator().duration(Durations.MM_TX_SEARCH_FAILED_PS_DUR.range()));
				nextState(States.END_PS, simulator().duration(Durations.MM_TX_SEARCH_COMPLETE_PS_DUR.range()));
				break;
			case END_PS:
				//Send the Data and End Msg and move into an idle state
				ICommunicate role = simulator().getRole(Roles.PARENT_SEARCH.name());
				role.addInput(ParentSearch.Inputs.SEARCH_AOI_COMPLETE);
				role.addInput(ParentSearch.Inputs.END_MM);
				nextState(States.IDLE, 1);
				break;
			case RX_PS:
				nextState(States.IDLE, simulator().duration(Durations.MM_RX_DUR.range()));
				break;
			default:
				//Stay as we are
				nextState(null, 0);
				break;
		}
		
		return true;
	}

	@Override
	public void updateState() {
//		temp_inputs.addAll(_input);
//		_input.clear();
		switch( (States) state() ) {
			case IDLE:
				//If the MM is idle then do the following things in sequence
				//First check for Parent Search Commands
				if ( _input.contains(Inputs.POKE_PS) ) {
					simulator().addInput(Roles.PARENT_SEARCH.name(), ParentSearch.Inputs.ACK_MM);
					nextState(States.RX_PS, 1);
				}
				
				//TODO Handle more input values
				break;
			case POKE_PS:
				//Look for Ack
				if( _input.contains(Inputs.ACK_PS) ) {
					nextState(States.TX_PS, 1);
				} else if ( _input.contains(Inputs.BUSY_PS) ) {
					nextState(States.IDLE, 1);
				}
				break;
			case TX_PS:
				//No interruptions
				//TODO Handle interruptions
				
				break;
			case END_PS:
				//Do nothing
				break;
			case RX_PS:
				//Look for end of TX
				if ( _input.contains(Inputs.END_PS) ) {
					//Look for the inputs
					if ( _input.contains(Inputs.TERMINATE_SEARCH) ) {
						//TODO Tell the OPERATOR and the VA
					} else if ( _input.contains(Inputs.SEARCH_AOI) ) {
						//TODO Send AOI to the OPERATOR
					}
					
					nextState(States.IDLE, 1);
				}
				break;
	
			default:
				//Do nothing for states not mentioned
				break;
		}
		//Now empty the temp input list
		_input.clear();
	}

	@Override
	public void processEvent(IEvent event) {
		// TODO Auto-generated method stub

	}

}
