package WiSAR.Agents;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

import CUAS.Simulator.IData;
import CUAS.Simulator.IStateEnum;
import CUAS.Simulator.Actor;
import CUAS.Simulator.Simulator;
import WiSAR.Actors;
import WiSAR.Durations;
import WiSAR.MemoryObject;
import WiSAR.Agents.OperatorRole.Outputs;

public class MissionManagerRole extends Actor {

	//INTERNAL VARS
	PriorityQueue<IData> tasks;
	
	public enum Outputs implements IData
	{
		/**
		 * Basic Communication
		 */
		MM_POKE, 
		MM_ACK,
		MM_END,
		
		/**
		 * Video Operator
		 */
		
		MM_TARGET_DESCRIPTION,
		MM_TERMINATE_SEARCH,
		
		/**
		 * Operator
		 */
		MM_NEW_SEARCH_AOI,
		 
		/**
		 * ParentSearch
		 */
		MM_SEARCH_AOI_COMPLETE,
		MM_SEARCH_FAILED,
		MM_TARGET_SIGHTING_F,
		MM_TARGET_SIGHTING_T,
		
		
		/**
		 * VGUI
		 */
		MM_FLYBY_T,
		MM_FLYBY_F,
		MM_ANOMALY_VERIFIED_T,
		MM_ANOMALY_VERIFIED_F,
		
		/**
		 * Observable
		 */
		
		/**
		 * global outputs
		 */
		MM_BUSY
	}
	
	/**
	 * Define Role States
	 * @author TJ-ASUS
	 *
	 */
	public enum States implements IStateEnum
	{
		IDLE,
		
		/**
		 * Communicate with PS
		 */
		POKE_PS,
		TX_PS,
		END_PS,
		RX_PS,
		
		/**
		 * Communicate with OP
		 */
		POKE_OP,
		TX_OP,
		END_OP,
		RX_OP,
		
		/**
		 * Communicate with VO
		 */
		POKE_VO,
		TX_VO,
		END_VO,
		RX_VO,
		
		/**
		 * Communicate with VGUI
		 */
		OBSERVING_VGUI,
		POKE_VGUI,
		TX_VGUI,
		END_VGUI
		
	}
	
	
	public MissionManagerRole()
	{
		name(Actors.MISSION_MANAGER.name());
		nextState(States.IDLE, 1);
		tasks = new PriorityQueue<IData>();
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
		int duration = 1;
		//If a state isn't included then it doesn't deviate from the default
		switch((States) nextState()) {
			case IDLE:
				nextState(States.OBSERVING_VGUI, sim().duration(Durations.MM_IDLE_DUR.range()));
				break;
			case POKE_PS:
				sim().addOutput(Actors.PARENT_SEARCH.name(), Outputs.MM_POKE);
				nextState(States.IDLE, sim().duration(Durations.MM_POKE_DUR.range()));
				break;
			case TX_PS:
				//TODO change duration based on the data being sent
				sim().addObservation(Outputs.MM_BUSY, Actors.MISSION_MANAGER.name());
				nextState(States.END_PS, sim().duration(Durations.MM_DEFAULT_TX_DUR.range()));
				break;
			case END_PS:
				//Send the Data and End Msg and move into an idle state
				//TODO handle extra stuff that PS might want to know (see VO.PNS.ENDGUI)
				sim().addOutput(Actors.PARENT_SEARCH.name(), Outputs.MM_END);
				nextState(States.IDLE, 1);
				break;
			case POKE_OP:
				sim().addOutput(Actors.OPERATOR.name(), Outputs.MM_POKE);
				nextState(States.IDLE, sim().duration(Durations.MM_POKE_DUR.range()));
				break;
			case TX_OP:
				//TODO change duration based on the data being sent
				sim().addObservation(Outputs.MM_BUSY, Actors.MISSION_MANAGER.name());
				nextState(States.END_OP, sim().duration(Durations.MM_DEFAULT_TX_DUR.range()));
				break;
			case END_OP:
				//TODO handle extra stuff that OP might want to know (see VO.PNS.ENDGUI)
				sim().addOutput(Actors.OPERATOR.name(), Outputs.MM_END);
				nextState(States.IDLE, 1);
				break;
			case POKE_VO:
				sim().addOutput(Actors.VIDEO_OPERATOR.name(), Outputs.MM_POKE);
				nextState(States.IDLE, sim().duration(Durations.MM_POKE_DUR.range()));
				break;
			case TX_VO:
				//TODO change duration based on the data being sent
				sim().addObservation(Outputs.MM_BUSY, Actors.MISSION_MANAGER.name());
				nextState(States.END_VO, sim().duration(Durations.MM_DEFAULT_TX_DUR.range()));
				break;
			case END_VO:
				//TODO handle extra stuff that VO might want to know (see VO.PNS.ENDGUI)
				sim().addOutput(Actors.VIDEO_OPERATOR.name(), Outputs.MM_END);
				nextState(States.IDLE, 1);
				break;
			case RX_PS:
			case RX_VO:
			case RX_OP:
				sim().addObservation(Outputs.MM_BUSY, Actors.MISSION_MANAGER.name());
				nextState(States.IDLE, sim().duration(Durations.MM_RX_DUR.range()));
				break;
			case OBSERVING_VGUI:
				nextState(States.IDLE, sim().duration(Durations.MM_OBSERVE_VGUI.range()));
				break;
			case POKE_VGUI:
				sim().addObservation(Outputs.MM_BUSY, Actors.MISSION_MANAGER.name());
				nextState(States.IDLE, sim().duration(Durations.MM_POKE_DUR.range()));
				break;
			case TX_VGUI:
				//TODO Change duration based on what is being sent
				sim().addObservation(Outputs.MM_BUSY, Actors.MISSION_MANAGER.name());
				nextState(States.END_VGUI, sim().duration(Durations.MM_DEFAULT_TX_DUR.range()));
				break;
			case END_VGUI:
				sim().addObservation(Outputs.MM_BUSY, Actors.MISSION_MANAGER.name());
				sim().addOutput(Actors.VIDEO_OPERATOR_GUI.name(), Outputs.MM_END);
				if ( tasks.peek() != null ) {
					//If it is a True Positive anomaly then handle it the following way
					if ( tasks.peek() == Outputs.MM_ANOMALY_VERIFIED_T ) {
						//TODO Add non-determinism to this decision instead of always doing a flyby of true positives
						tasks.add(Outputs.MM_FLYBY_T);
					} else if ( tasks.peek() == Outputs.MM_ANOMALY_VERIFIED_F ) {
						//TODO Add non-determinism to this decision instead of never doing a flyby of false positives
						//Never do a flyby of false positives
					}
				}
				nextState(States.IDLE, 1);
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
		ArrayList<IData> vgui_observations = sim().getObservations(Actors.VIDEO_OPERATOR_GUI.name());
		ArrayList<IData> op_observations = sim().getObservations(Actors.OPERATOR.name());
		ArrayList<IData> vo_observations = sim().getObservations(Actors.VIDEO_OPERATOR.name());
		ArrayList<IData> ps_observations = sim().getObservations(Actors.PARENT_SEARCH.name());
		
		switch( (States) state() ) {
			case IDLE:
				//If the MM is idle then do the following things in sequence
				//First check for Parent Search Commands
				//Once we have accepted an input then we
				boolean result = handlePokes(input);
				
				//If there are no pokes then do next task
				if ( !result )
					doNextTask();
				break;
			case RX_PS:
				//Look for end of TX
				if ( input.contains(ParentSearch.Outputs.PS_END) ) {
					//Look for the inputs
					if ( input.contains(ParentSearch.Outputs.PS_TERMINATE_SEARCH) ) {
						tasks.add(Outputs.MM_TERMINATE_SEARCH);
						tasks.add(Outputs.MM_TERMINATE_SEARCH);
					} else {
						if ( input.contains(ParentSearch.Outputs.PS_TARGET_DESCRIPTION) ) {
							tasks.add(Outputs.MM_TARGET_DESCRIPTION);
						}
						if ( input.contains(ParentSearch.Outputs.PS_NEW_SEARCH_AOI) ) {
							tasks.add(Outputs.MM_NEW_SEARCH_AOI);
						}
					}
					nextState(States.IDLE, 1);
				}
				break;
			case RX_OP:
				if(input.contains(OperatorRole.Outputs.OP_END)){
					if(input.contains(OperatorRole.Outputs.OP_SEARCH_AOI_COMPLETE)) {
						//Pass this data to the PS
						tasks.add(Outputs.MM_SEARCH_AOI_COMPLETE);
					}
					if ( input.contains(OperatorRole.Outputs.OP_SEARCH_AOI_FAILED)) {
						//Pass this data to the PS
						tasks.add(Outputs.MM_SEARCH_FAILED);
					}
					nextState(States.IDLE, 1);
				}
				break;
			case RX_VO:
				if(input.contains(VideoOperatorRole.Outputs.VO_END)) {
					if(input.contains(VideoOperatorRole.Outputs.VO_TARGET_SIGHTING_T)){
						tasks.add(Outputs.MM_TARGET_SIGHTING_T);
					}
					if(input.contains(VideoOperatorRole.Outputs.VO_TARGET_SIGHTING_F)){
						tasks.add(Outputs.MM_TARGET_SIGHTING_F);
					}
					nextState(States.IDLE, 1);
				}
				break;
			case POKE_PS:
				//Look for Ack
				if( input.contains(ParentSearch.Outputs.PS_ACK) ) {
					nextState(States.TX_PS, 1);
				} else if ( input.contains(ParentSearch.Outputs.PS_BUSY) || 
						ps_observations.contains(ParentSearch.Outputs.PS_BUSY) ) {
					nextState(States.IDLE, 1);
				} else {
					//PS is more important so accept pokes from them
					if ( input.contains(ParentSearch.Outputs.PS_POKE) ) {
						sim().addOutput(Actors.PARENT_SEARCH.name(), Outputs.MM_ACK);
						nextState(States.RX_PS, 1);
					} 
					if(input.contains(OperatorRole.Outputs.OP_POKE)) {
						//Dont accept Operator pokes while poking PS
						sim().addOutput(Actors.OPERATOR.name(), Outputs.MM_BUSY);
					}
					if(input.contains(VideoOperatorRole.Outputs.VO_POKE)) {
						//Dont accept VO pokes while poking PS
						sim().addOutput(Actors.VIDEO_OPERATOR.name(), Outputs.MM_BUSY);
					}
				}
				break;
			case TX_PS:
				//No interruptions
				//TODO Handle interruptions
				break;
			case END_PS:
				//Do the next task immediately if there is one
				doNextTask();
				break;
				
			case POKE_OP:
				//Look for Ack
				if( input.contains(OperatorRole.Outputs.OP_ACK) ) {
					nextState(States.TX_OP, 1);
				} else if ( input.contains(OperatorRole.Outputs.OP_BUSY) || 
						op_observations.contains(OperatorRole.Outputs.OP_BUSY)) {
					nextState(States.IDLE, 1);
				} else {
					//PS is more important so accept pokes from them
					if ( input.contains(ParentSearch.Outputs.PS_POKE) ) {
						//Dont accept PS pokes while poking OP
						sim().addOutput(Actors.PARENT_SEARCH.name(), Outputs.MM_BUSY);
					} 
					
					//Ignore pokes from the OP since we are poking them
					
					if(input.contains(VideoOperatorRole.Outputs.VO_POKE)) {
						//Dont accept VO pokes while poking OP
						sim().addOutput(Actors.VIDEO_OPERATOR.name(), Outputs.MM_BUSY);
					}
				}
				break;
			case TX_OP:
				//Do nothing
				break;
			case END_OP:
				//Immediately do the next task
				doNextTask();
				break;
			case POKE_VO:
				//Look for Ack
				if( input.contains(VideoOperatorRole.Outputs.VO_ACK) ) {
					nextState(States.TX_VO, 1);
				} else if ( input.contains(VideoOperatorRole.Outputs.VO_BUSY) || 
						vo_observations.contains(VideoOperatorRole.Outputs.VO_BUSY)) {
					nextState(States.IDLE, 1);
				} else {
					if ( input.contains(ParentSearch.Outputs.PS_POKE) ) {
						//Dont accept PS pokes while poking OP
						sim().addOutput(Actors.PARENT_SEARCH.name(), Outputs.MM_BUSY);
					} 
					if(input.contains(OperatorRole.Outputs.OP_POKE)) {
						//Dont accept Operator pokes while poking PS
						sim().addOutput(Actors.OPERATOR.name(), Outputs.MM_BUSY);
					}
					
					//Ignore pokes from VO while poking VO
				}
				break;
			case TX_VO:
				//No Interuptions
				break;
			case END_VO:
				//Do next task immediately
				doNextTask();
				break;
			case POKE_VGUI:
				/**
				 * Always assume that the GUI is available for now
				 */
//				if ( vgui_observations.contains(VideoGUIRole.Outputs.VGUI_ACCESSIBLE) ) {
				nextState(States.TX_VGUI, 1);
//				}
				break;
			case TX_VGUI:
				//No interruptions
				break;
			case END_VGUI:
				//Do next task
				doNextTask();
				break;
			case OBSERVING_VGUI:
				//Handle observations from the vgui
				/**
				 * Always assume that the GUI is available for now
				 */
//				if ( vgui_observations.contains(VideoGUIRole.Outputs.VGUI_ACCESSIBLE) ) {
					for( IData observation : vgui_observations ) {
						//Only accept one verify task at a time
						if ( observation == VideoGUIRole.Outputs.VGUI_VALIDATION_REQ_TRUE ) {
							tasks.add(Outputs.MM_ANOMALY_VERIFIED_T);
							break;
						} else if ( observation == VideoGUIRole.Outputs.VGUI_VALIDATION_REQ_FALSE ) {
							tasks.add(Outputs.MM_ANOMALY_VERIFIED_F);
							break;
						}
					}
//				}
				
				boolean pokes = handlePokes(input);
				
				//If there are no pokes then do next task
				if ( !pokes )
					doNextTask();
				break;
	
			default:
				//Do nothing for states not mentioned
				break;
		}
	}
	
	/**
	 * Remove this task from memory and act on it
	 */
	private void doNextTask()
	{
		if ( !tasks.isEmpty() ) {
			Outputs task = (Outputs) tasks.poll(); //Grab the First thing we remember to do
		}
	}
	
	private boolean handlePokes(ArrayList<IData> input)
	{
		boolean accepted_poke = false;
		if ( input.contains(ParentSearch.Outputs.PS_POKE) ) {
			sim().addOutput(Actors.PARENT_SEARCH.name(), Outputs.MM_ACK);
			nextState(States.RX_PS, 1);
			accepted_poke = true;
		} 
		if(input.contains(OperatorRole.Outputs.OP_POKE)) {
			if ( !accepted_poke ) {
				sim().addOutput(Actors.OPERATOR.name(), Outputs.MM_ACK);
				nextState(States.RX_OP,1);
				accepted_poke = true;
			} else {
				sim().addOutput(Actors.OPERATOR.name(), Outputs.MM_BUSY);
			}
		}
		if(input.contains(VideoOperatorRole.Outputs.VO_POKE)){
			//If we are already going to change state then send a busy
			if ( !accepted_poke ) {
				sim().addOutput(Actors.VIDEO_OPERATOR.name(), Outputs.MM_ACK);
				nextState(States.RX_VO,1);
				accepted_poke = true;
			} else {
				sim().addOutput(Actors.VIDEO_OPERATOR.name(), Outputs.MM_BUSY);
			}
		}
		
		return accepted_poke;
	}
}
