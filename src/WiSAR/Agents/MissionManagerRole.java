package WiSAR.Agents;

import java.util.ArrayList;

import CUAS.Simulator.IData;
import CUAS.Simulator.IStateEnum;
import CUAS.Simulator.Actor;
import CUAS.Simulator.Simulator;
import WiSAR.Actors;
import WiSAR.Durations;

public class MissionManagerRole extends Actor {

	//INTERNAL VARS
	ArrayList<IData> temp_inputs = new ArrayList<IData>();
	IData current_output = null;
	
	public enum Outputs implements IData
	{
		/**
		 * Operator and VideoOperator
		 */
		MM_SEARCH_AOI,
		MM_SEARCH_TERMINATED,
		
		/**
		 * Video Operator
		 */
		MM_POKE, 
		MM_ACK, 
		MM_END, 
		
		/**
		 * Operator
		 */
		 
		/**
		 * ParentSearch
		 */
		MM_SEARCH_AOI_COMPLETE,
		MM_FOUND_ANOMALY,
		
		/**
		 * global outputs
		 */
		MM_BUSY, 
		MM_SEARCH_FAILED
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
		RX_PS,
		RX_VO,
		RX_OP,
		ACK_PS,
		ACK_VO,
		ACK_OP,
	}
	
	
	public MissionManagerRole()
	{
		name(Actors.MISSION_MANAGER.name());
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
				sim().addOutput(Actors.PARENT_SEARCH.name(), Outputs.MM_POKE);
				nextState(States.IDLE, sim().duration(Durations.MM_POKE_DUR.range()));
				break;
			case POKE_OP:
				//TODO handle when the OPERATOR is busy but the VO is not and 
				//		the information needs to be passed to both
				sim().addOutput(Actors.OPERATOR.name(), Outputs.MM_POKE);
				nextState(States.IDLE, sim().duration(Durations.MM_POKE_DUR.range()));
				break;
			case POKE_VO:
				sim().addOutput(Actors.VIDEO_OPERATOR.name(), Outputs.MM_POKE);
				nextState(States.IDLE, sim().duration(Durations.MM_POKE_DUR.range()));
				break;
			case TX_PS:
				if(current_output == Outputs.MM_SEARCH_AOI_COMPLETE)
					nextState(States.END_PS,sim().duration(Durations.MM_TX_SEARCH_COMPLETE_PS_DUR.range()));
				else if(current_output == Outputs.MM_FOUND_ANOMALY)
					nextState(States.END_PS, sim().duration(Durations.MM_TX_SIGHTING_PS_DUR.range()));
				else{
					//TODO handle other possible transmission lengths
					nextState(States.END_PS,1);
				}
				break;
			case TX_OP:
				if(current_output == Outputs.MM_SEARCH_AOI)
					nextState(States.END_OP, sim().duration(Durations.MM_TX_AOI_OPERATOR_DUR.range()));
				else if(current_output == Outputs.MM_SEARCH_TERMINATED)
					nextState(States.END_OP, sim().duration(Durations.MM_TX_TERMINATE_OPERATOR_DUR.range()));
				else{
					//TODO handle other possible transmission lengths
					nextState(States.END_OP,1);
				}
				break;
			case TX_VO:
				if(current_output == Outputs.MM_SEARCH_AOI){
					nextState(States.END_VO, sim().duration(Durations.MM_TX_INITIATE_FEED_VO_DUR.range()));
				} else if(current_output == Outputs.MM_SEARCH_TERMINATED) {
					nextState(States.END_VO, sim().duration(Durations.MM_TX_END_FEED.range()));
				}else{
					//TODO handle other possible transmission lengths
					nextState(States.END_VO,1);
				}
				
				break;
			case END_PS:
				//Send the Data and End Msg and move into an idle state
				nextState(States.IDLE, 1);
				sim().addOutput(Actors.PARENT_SEARCH.name(), Outputs.MM_END);
				if(current_output == Outputs.MM_SEARCH_AOI_COMPLETE)
					sim().addOutput(Actors.PARENT_SEARCH.name(), Outputs.MM_SEARCH_AOI_COMPLETE);
				else if(current_output == Outputs.MM_FOUND_ANOMALY)
					sim().addOutput(Actors.PARENT_SEARCH.name(), Outputs.MM_FOUND_ANOMALY);
				break;
			case END_OP:
				nextState(States.IDLE, 1);
				sim().addOutput(Actors.OPERATOR.name(), Outputs.MM_END);
				if(current_output == Outputs.MM_SEARCH_AOI){
					sim().addOutput(Actors.OPERATOR.name(), Outputs.MM_SEARCH_AOI);
					nextState(States.POKE_VO,1);
				}else if(current_output == Outputs.MM_SEARCH_TERMINATED){
					sim().addOutput(Actors.OPERATOR.name(), Outputs.MM_SEARCH_TERMINATED);
					nextState(States.POKE_VO,1);
				}else {
					nextState(States.IDLE,1);
				}
				break;
			case END_VO:
				nextState(States.IDLE, 1);
				sim().addOutput(Actors.VIDEO_OPERATOR.name(), Outputs.MM_END);
				if(current_output == Outputs.MM_SEARCH_AOI)
					sim().addOutput(Actors.VIDEO_OPERATOR.name(), Outputs.MM_SEARCH_AOI);
				else if(current_output == Outputs.MM_SEARCH_TERMINATED)
					sim().addOutput(Actors.VIDEO_OPERATOR.name(), Outputs.MM_SEARCH_TERMINATED);
				else if(current_output == Outputs.MM_SEARCH_AOI_COMPLETE){
					sim().addOutput(Actors.VIDEO_OPERATOR.name(), Outputs.MM_SEARCH_AOI_COMPLETE);
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

		//Pull Input and any observations that need to be made from the simulator
		ArrayList<IData> input = sim().getInput(this.name());
		
		switch( (States) state() ) {
			case IDLE:
				//If the MM is idle then do the following things in sequence
				//First check for Parent Search Commands
				if ( input.contains(ParentSearch.Outputs.PS_POKE) ) {
					sim().addOutput(Actors.PARENT_SEARCH.name(), Outputs.MM_ACK);
					nextState(States.RX_PS, 1);
				} 
				if(input.contains(OperatorRole.Outputs.OP_POKE)){
					sim().addOutput(Actors.OPERATOR.name(), Outputs.MM_ACK);
					nextState(States.RX_OP,1);
				}
				if(input.contains(VideoOperatorRole.Outputs.VO_POKE)){
					sim().addOutput(Actors.VIDEO_OPERATOR.name(), Outputs.MM_ACK);
					nextState(States.RX_VO,1);
				}
				//TODO Handle more input values
				break;
			case RX_PS:
				//Look for end of TX
				if ( input.contains(ParentSearch.Outputs.PS_END) ) {
					//Look for the inputs
					if ( input.contains(ParentSearch.Outputs.SEARCH_TERMINATED) ) {
						current_output = Outputs.MM_SEARCH_TERMINATED;
						nextState(States.POKE_OP, 1);
					} else if ( input.contains(ParentSearch.Outputs.SEARCH_AOI) ) {
						current_output = Outputs.MM_SEARCH_AOI;
						nextState(States.POKE_OP,1);
					}else{
						//TODO handle other commands from PS
					}
				}
				break;
			case RX_OP:
				if(input.contains(OperatorRole.Outputs.OP_END)){
					if(input.contains(OperatorRole.Outputs.OP_SEARCH_AOI_COMPLETE)){
						current_output = Outputs.MM_SEARCH_AOI_COMPLETE;
						nextState(States.POKE_VO,1);
					}else{
						//TODO handle other inputs from OPERATOR
						nextState(States.IDLE,1);
					}
				}
				break;
			case RX_VO:
				if(input.contains(VideoOperatorRole.Outputs.VO_END)){
					if(input.contains(VideoOperatorRole.Outputs.VO_LIKELY_ANOMALY_DETECTED_T)){
						current_output = Outputs.MM_FOUND_ANOMALY;
						nextState(States.POKE_PS,1);
					}else{
						//TODO handle other inputs from Video Operator
						nextState(States.IDLE,1);
					}
				}
				break;
			case POKE_PS:
				//Look for Ack
				if( input.contains(ParentSearch.Outputs.PS_ACK) ) {
					nextState(States.TX_PS, 1);
				} else if ( input.contains(ParentSearch.Outputs.PS_BUSY) ) {
					nextState(States.IDLE, 1);
				}
				break;
			case POKE_OP:
				//Look for Ack
				if( input.contains(OperatorRole.Outputs.OP_ACK) ) {
					nextState(States.TX_OP, 1);
				} else if ( input.contains(OperatorRole.Outputs.OP_BUSY) ) {
					nextState(States.IDLE, 1);
				}
				break;
			case POKE_VO:
				//Look for Ack
				if( input.contains(VideoOperatorRole.Outputs.VO_ACK) ) {
					nextState(States.TX_VO, 1);
				} else if ( input.contains(VideoOperatorRole.Outputs.VO_BUSY) ) {
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
	}
}
