package WiSAR.Agents;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

import CUAS.Simulator.IData;
import CUAS.Simulator.IObservable;
import CUAS.Simulator.IStateEnum;
import CUAS.Simulator.Actor;
import NewModel.Events.IEvent;
import WiSAR.Durations;
import WiSAR.EventEnum;

public class ParentSearch extends Actor {
	
	//INTERNAL VARS
	boolean _search_active = true;
	
	Deque<Outputs> output_queue = new ArrayDeque<Outputs>();
	Outputs current_output = null;
	
	/**
	 * Setup Inputs and Outputs
	 */

	
	public enum Outputs implements IData
	{
		SEARCH_AOI,
		SEARCH_TERMINATED,
		POKE_PS,
		END_PS,
		BUSY_PS,
		ACK_PS, 
	}
	
	/**
	 * Define Role States
	 * @author TJ-ASUS
	 *
	 */
	public enum States implements IStateEnum
	{
		IDLE,
		POKE_MM,
		TX_MM,
		END_MM,
		RX_MM
	}
	
	public ParentSearch()
	{
		name( Roles.PARENT_SEARCH.name() );
		nextState(States.IDLE, 1);
		
	}
	
	@Override
	public void processNextState()
	{
		//Is our next state now?
		if ( nextStateTime() != sim().getTime() ) {
			return null;
		}
		
		//Update to the next state
		state(nextState());
		
		//Now determine what our next state will be
		//If a state isn't included then it doesn't deviate from the default
		switch((States) nextState()) {
			case IDLE:
				nextState(null, 0);
				break;
			case POKE_MM:
				sim().addInput(Roles.MISSION_MANAGER.name(), Outputs.POKE_PS);
				nextState(States.IDLE, sim().duration(Durations.PS_POKE_MM_DUR.range()));
				break;
			case TX_MM:
				current_output = output_queue.pollLast();
				int duration = 1;
				if ( current_output == Outputs.SEARCH_AOI ) {
					duration = sim().duration(Durations.PS_TX_AOI_MM_DUR.range());
				} else if ( current_output == Outputs.SEARCH_TERMINATED ) {
					duration = sim().duration(Durations.PS_TX_TERMINATE_MM_DUR.range());
				}
				nextState(States.END_MM, duration);
				break;
			case END_MM:
				//Now send the data that got sent from the transfer
				if ( current_output == Outputs.SEARCH_AOI ) {
					sim().addInput(Roles.MISSION_MANAGER.name(), Outputs.SEARCH_AOI);
				} else if ( current_output == Outputs.SEARCH_TERMINATED ) {
					sim().addInput(Roles.MISSION_MANAGER.name(), Outputs.SEARCH_TERMINATED);
				}
				sim().addInput(Roles.MISSION_MANAGER.name(),Outputs.END_PS);
				nextState(States.IDLE, 1);
				break;
			case RX_MM:
				//Is it possible to receive for too long?
				nextState(States.IDLE, sim().duration(Durations.PS_RX_MM_DUR.range()) );
				break;
			default:
				nextState(null, 1);
				break;
		}
		
		return _output;
	}

	@Override
	public ArrayList<IData> processInputs() {
		
		//Depending on our state we will handle different inputs.
		switch((States) state() ) {
			case IDLE:
				//If the MM is idle then do the following things in sequence
				//First check for Parent Search Commands
				if ( _input.contains(MissionManagerRole.Outputs.POKE_PS) ) {
					sim().addInput(Roles.MISSION_MANAGER.name(), Outputs.ACK_PS);
					nextState(States.RX_MM, 1);
				} else if( output_queue.size() > 0 ) {
					nextState(States.POKE_MM, 1);
				}
				
				//TODO Handle more input values
				break;
			case POKE_MM:
				//TODO Handle simultaneous pokes from MM
				
				//Look for Busy or Ack from MM
				if ( _input.contains(MissionManagerRole.Outputs.ACK_PS) ) {
					nextState(States.TX_MM, 1);
				} else if ( _input.contains(MissionManagerRole.Outputs.BUSY_MM) ) {
					nextState(States.IDLE, 1);
				}
				
				break;
			case TX_MM:
				//If I am transmitting then continue to transmit no matter what
				//TODO Handle Interruptions
				break;
			case END_MM:
				//Do Nothing I will soon be idle
				break;
			case RX_MM:
				//Look for the END_MM input before handling other inputs
				if ( _input.contains(MissionManagerRole.Outputs.END_PS) ) {
					//TODO Handle all inputs from the MM
					
					if ( _input.contains(MissionManagerRole.Outputs.SEARCH_AOI_COMPLETE) ) {
						//Do we do anything with this info?
					}
					
					nextState(States.IDLE, 1);
				}
			
				break;
			default:
				//Do Nothing for any state not mentioned here
				break;
		}

		return _output;
	}

	@Override
	public void addInput(ArrayList<IData> data) {
		_input.addAll(data);
	}

	@Override
	public ArrayList<IData> getObservations() {
		// TODO Auto-generated method stub
		return null;
	}

	
//	@Override
//	public void processEvents(ArrayList<Event> events) {
//		for( Event e : events ) {
//			switch(e.type()) {
//				case PS_TERMINATE_SEARCH:
//					createTerminateSearchEvent();
//					break;
//				case PS_NEW_AOI:
//					createNewSearchAOIEvent();
//					break;
//				default:
//					//Do nothing with the event
//					break;
//			}
//		}
//	}
	
	
	
	/**
	 * PRIVATE HELPER METHODS
	 */
	
	
	
//	private void createTerminateSearchEvent()
//	{
//		if ( state() == RoleState.IDLE ) {
//			Simulator.getInstance().addPost(POBOX.PS_MM, DataType.TERMINATE_SEARCH);
//			nextState(RoleState.POKE_MM, 1);
////			System.out.println("Created new Terminate Search Event");
//		} else {
//			assert false : "Unable to create Terminate Search Event, Parent Search is busy";
////			System.out.println("Unable to create Terminate Search Event, Parent Search is busy");
//		}
//	}
	
//	private void createNewSearchAOIEvent()
//	{
//		if ( state() == RoleState.IDLE ) {
////			_search_aoi_count++;
//			Simulator.getInstance().addPost(POBOX.PS_MM, DataType.SEARCH_AOI);
//			nextState(RoleState.POKE_MM, 1);
////			System.out.println("Created new Search AOI Event");
//		} else {
//			assert false : "Unable to create Search AOI Event, Parent Search is busy";
////			System.out.println("Unable to create Search AOI Event, Parent Search is busy");
//		}
//	}

}
