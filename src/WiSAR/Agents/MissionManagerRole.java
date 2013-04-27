package WiSAR.Agents;

import java.util.ArrayList;

import CUAS.Simulator.IData;
import CUAS.Simulator.IObservable;
import CUAS.Simulator.IStateEnum;
import CUAS.Simulator.Actor;
import CUAS.Simulator.Simulator;
import WiSAR.Durations;

public class MissionManagerRole extends Actor {

	//INTERNAL VARS
	ArrayList<IData> temp_inputs = new ArrayList<IData>();
	
	
	public enum Outputs implements IData
	{
		/**
		 * Operator and VideoOperator
		 */
		SEARCH_AOI_MM,
		SEARCH_TERMINATED_MM,
		
		/**
		 * Video Operator
		 */
		POKE_MM, 
		ACK_MM, 
		END_MM, 
		
		/**
		 * Operator
		 */
		 
		/**
		 * ParentSearch
		 */
		SEARCH_AOI_COMPLETE_MM,
		FOUND_ANOMALY_MM,
		
		/**
		 * global outputs
		 */
		BUSY_MM
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
		POKE_OP,
		POKE_VO,
		TX_PS,
		TX_OP,
		TX_VO,
		END_PS,
		END_OP,
		END_VO,
//		MM_ACK_PS,
//		MM_ACK_OPERATOR,
		RX_PS,
		RX_VO,
		RX_OP,
		ACK_PS,
		ACK_VO,
		ACK_OP,
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
	public void processNextState() {
		//Is our next state now?
		if ( nextStateTime() != Simulator.getInstance().getTime() ) {
			return;
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
				sim().addOutput(Roles.PARENT_SEARCH.name(), Outputs.POKE_MM);
				//TODO store the message to be sent in END_PS
				nextState(States.IDLE, sim().duration(Durations.MM_POKE_DUR.range()));
				break;
			case POKE_OP:
				sim().addOutput(Roles.OPERATOR.name(), Outputs.POKE_MM);
				nextState(States.IDLE, sim().duration(Durations.MM_POKE_DUR.range()));
				break;
			case POKE_VO:
				sim().addOutput(Roles.VIDEO_OPERATOR.name(), Outputs.POKE_MM);
				nextState(States.IDLE, sim().duration(Durations.MM_POKE_DUR.range()));
				break;
			case TX_PS:
				//TODO change this duration based on the data being transmitted
//				nextState(States.END_PS, simulator().duration(Durations.MM_TX_SIGHTING_PS_DUR.range()));
//				nextState(States.END_PS, simulator().duration(Durations.MM_TX_SEARCH_FAILED_PS_DUR.range()));
				nextState(States.END_PS, sim().duration(Durations.MM_TX_SEARCH_COMPLETE_PS_DUR.range()));
				break;
			case TX_OP:
				//TODO change this duration based on data transmitted
				nextState(States.END_OP, 1);
				break;
			case TX_VO:
				//TODO change this duration based on data transmitted
				nextState(States.END_VO, 1);
				break;
			case END_PS:
				//Send the Data and End Msg and move into an idle state
				sim().addOutput(Roles.PARENT_SEARCH.name(), Outputs.SEARCH_AOI_COMPLETE_MM);
				sim().addOutput(Roles.PARENT_SEARCH.name(), Outputs.END_MM);
				nextState(States.IDLE, 1);
				break;
			case END_OP:
				//TODO output based off of what the PS instructions are
				//TODO if message to be forwarded to the VO then nextState POKE_VO
				nextState(States.IDLE, 1);
				break;
			case END_VO:
				//TODO output based off of what the PS instructions are
				nextState(States.IDLE, 1);
				break;
			case RX_PS:
				nextState(States.IDLE, sim().duration(Durations.MM_RX_DUR.range()));
				break;
			case RX_VO:
				
			default:
				//Stay as we are
				nextState(null, 0);
				break;
		}
		
		return;
	}

	@Override
	public void processInputs() {
//		temp_inputs.addAll(_input);
//		_input.clear();
		switch( (States) state() ) {
		
			case IDLE:
				//If the MM is idle then do the following things in sequence
				//First check for Parent Search Commands
				if ( _input.contains(ParentSearch.Outputs.POKE_PS) ) {
					sim().addOutput(Roles.PARENT_SEARCH.name(), Outputs.ACK_MM);
					nextState(States.RX_PS, 1);
				} 
				//TODO Handle pokes from the OPERATOR and the VIDEO_OPERATOR.
				//TODO Handle more input values
				break;
			case POKE_PS:
				//Look for Ack
				if( _input.contains(ParentSearch.Outputs.ACK_PS) ) {
					nextState(States.TX_PS, 1);
				} else if ( _input.contains(ParentSearch.Outputs.BUSY_PS) ) {
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
				if ( _input.contains(ParentSearch.Outputs.END_PS) ) {
					//Look for the inputs
					if ( _input.contains(ParentSearch.Outputs.SEARCH_TERMINATED) ) {
						//TODO Tell the OPERATOR and the VO
						//TODO Store the message to be sent to the OPERATOR and VideoOperator
						nextState(States.POKE_OP, 1);
					} else if ( _input.contains(ParentSearch.Outputs.SEARCH_AOI) ) {
						//TODO Send AOI to the OPERATOR
						//TODO store desired message to send OPERATOR
						nextState(States.POKE_OP,1);
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

}
