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
				nextState(States.IDLE, sim().duration(Durations.MM_POKE_DUR.range()));
				break;
			case POKE_OP:
				//TODO handle when the OPERATOR is busy but the VO is not and 
				//		the information needs to be passed to both
				sim().addOutput(Roles.OPERATOR.name(), Outputs.POKE_MM);
				nextState(States.IDLE, sim().duration(Durations.MM_POKE_DUR.range()));
				break;
			case POKE_VO:
				sim().addOutput(Roles.VIDEO_OPERATOR.name(), Outputs.POKE_MM);
				nextState(States.IDLE, sim().duration(Durations.MM_POKE_DUR.range()));
				break;
			case TX_PS:
				if(memory == Outputs.SEARCH_AOI_COMPLETE_MM)
					nextState(States.END_PS,sim().duration(Durations.MM_TX_SEARCH_COMPLETE_PS_DUR.range()));
				else if(memory == Outputs.FOUND_ANOMALY_MM)
					nextState(States.END_PS, sim().duration(Durations.MM_TX_SIGHTING_PS_DUR.range()));
				else{
					//TODO handle other possible transmission lengths
					nextState(States.END_PS,1);
				}
				break;
			case TX_OP:
				if(memory == Outputs.SEARCH_AOI_MM)
					nextState(States.END_OP, sim().duration(Durations.MM_TX_AOI_PILOT_DUR.range()));
				else if(memory == Outputs.SEARCH_TERMINATED_MM)
					nextState(States.END_OP, sim().duration(Durations.MM_TX_TERMINATE_PILOT_DUR.range()));
				else{
					//TODO handle other possible transmission lengths
					nextState(States.END_OP,1);
				}
				break;
			case TX_VO:
				if(memory == Outputs.SEARCH_AOI_MM){
					nextState(States.END_VO, sim().duration(Durations.MM_TX_INITIATE_FEED_VO_DUR.range()));
				} else if(memory == Outputs.SEARCH_TERMINATED_MM) {
					nextState(States.END_VO, sim().duration(Durations.MM_TX_END_FEED.range()));
				}else{
					//TODO handle other possible transmission lengths
					nextState(States.END_VO,1);
				}
				
				break;
			case END_PS:
				//Send the Data and End Msg and move into an idle state
				sim().addOutput(Roles.PARENT_SEARCH.name(), Outputs.END_MM);
				if(memory == Outputs.SEARCH_AOI_COMPLETE_MM)
					sim().addOutput(Roles.PARENT_SEARCH.name(), Outputs.SEARCH_AOI_COMPLETE_MM);
				else if(memory == Outputs.FOUND_ANOMALY_MM)
					sim().addOutput(Roles.PARENT_SEARCH.name(), Outputs.FOUND_ANOMALY_MM);
				nextState(States.IDLE, 1);
				break;
			case END_OP:
				nextState(States.IDLE, 1);
				sim().addOutput(Roles.OPERATOR.name(), Outputs.END_MM);
				if(memory == Outputs.SEARCH_AOI_MM){
					sim().addOutput(Roles.OPERATOR.name(), Outputs.SEARCH_AOI_MM);
					nextState(States.POKE_VO,1);
				}else if(memory == Outputs.SEARCH_TERMINATED_MM){
					sim().addOutput(Roles.OPERATOR.name(), Outputs.SEARCH_TERMINATED_MM);
					nextState(States.POKE_VO,1);
				}else {
					nextState(States.IDLE,1);
				}
				break;
			case END_VO:
				sim().addOutput(Roles.VIDEO_OPERATOR.name(), Outputs.END_MM);
				nextState(States.IDLE, 1);
				if(memory == Outputs.SEARCH_AOI_MM)
					sim().addOutput(Roles.VIDEO_OPERATOR.name(), Outputs.SEARCH_AOI_MM);
				else if(memory == Outputs.SEARCH_TERMINATED_MM)
					sim().addOutput(Roles.VIDEO_OPERATOR.name(), Outputs.SEARCH_TERMINATED_MM);
				else if(memory == Outputs.SEARCH_AOI_COMPLETE_MM){
					sim().addOutput(Roles.VIDEO_OPERATOR.name(), Outputs.SEARCH_AOI_COMPLETE_MM);
					nextState(States.POKE_PS, sim().duration(Durations.MM_POKE_DUR.range()));
				}else{
					//TODO handle other messages
				}
				break;
			case RX_PS:
				nextState(States.IDLE, sim().duration(Durations.MM_RX_DUR.range()));
				break;
			case RX_VO:
				nextState(States.IDLE, sim().duration(Durations.MM_RX_DUR.range()));
				break;
			case RX_OP:
				nextState(States.IDLE, sim().duration(Durations.MM_RX_DUR.range()));
				break;
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
			if(_input.contains(OperatorRole.Outputs.OP_POKE)){
				sim().addOutput(Roles.OPERATOR.name(), Outputs.ACK_MM);
				nextState(States.RX_OP,1);
			}
			if(_input.contains(VideoOperatorRole.Outputs.POKE_VO)){
				sim().addOutput(Roles.VIDEO_OPERATOR.name(), Outputs.ACK_MM);
				nextState(States.RX_VO,1);
			}
			//TODO Handle more input values
			break;
		case RX_PS:
			//Look for end of TX
			if ( _input.contains(ParentSearch.Outputs.END_PS) ) {
				//Look for the inputs
				if ( _input.contains(ParentSearch.Outputs.SEARCH_TERMINATED) ) {
					memory = Outputs.SEARCH_TERMINATED_MM;
					nextState(States.POKE_OP, 1);
				} else if ( _input.contains(ParentSearch.Outputs.SEARCH_AOI) ) {
					memory = Outputs.SEARCH_AOI_MM;
					nextState(States.POKE_OP,1);
				}else{
					//TODO handle other commands from PS
				}
			}
			break;
		case RX_OP:
			if(_input.contains(OperatorRole.Outputs.OP_END)){
				if(_input.contains(OperatorRole.Outputs.OP_SEARCH_AOI_COMPLETE)){
					memory = Outputs.SEARCH_AOI_COMPLETE_MM;
					nextState(States.POKE_VO,1);
				}else{
					//TODO handle other inputs from OPERATOR
					nextState(States.IDLE,1);
				}
			}
			break;
		case RX_VO:
			if(_input.contains(VideoOperatorRole.Outputs.END_VO)){
				if(_input.contains(VideoOperatorRole.Outputs.FOUND_ANOMALY_VO)){
					memory = Outputs.FOUND_ANOMALY_MM;
					nextState(States.POKE_PS,1);
				}else{
					//TODO handle other inputs from Video Operator
				}
			}
			break;
		case POKE_PS:
			//Look for Ack
			if( _input.contains(ParentSearch.Outputs.ACK_PS) ) {
				nextState(States.TX_PS, 1);
			} else if ( _input.contains(ParentSearch.Outputs.BUSY_PS) ) {
				nextState(States.IDLE, 1);
			}
			break;
		case POKE_OP:
			//Look for Ack
			if( _input.contains(OperatorRole.Outputs.OP_ACK) ) {
				nextState(States.TX_OP, 1);
			} else if ( _input.contains(OperatorRole.Outputs.OP_BUSY) ) {
				nextState(States.IDLE, 1);
			}
			break;
		case POKE_VO:
			//Look for Ack
			if( _input.contains(VideoOperatorRole.Outputs.ACK_VO) ) {
				nextState(States.TX_VO, 1);
			} else if ( _input.contains(VideoOperatorRole.Outputs.BUSY_VO) ) {
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

		default:
			//Do nothing for states not mentioned
			break;
		}
		//Now empty the temp input list
		_input.clear();
	}

}
