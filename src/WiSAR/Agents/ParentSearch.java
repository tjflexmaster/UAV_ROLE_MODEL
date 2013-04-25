package WiSAR.Agents;

import java.util.ArrayDeque;
import java.util.Deque;

import CUAS.Simulator.IObservable;
import CUAS.Simulator.IInputEnum;
import CUAS.Simulator.IOutputEnum;
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
	public enum Inputs implements IInputEnum
	{
		/**
		 * Search Inputs (Only received during RX)
		 */
		SEARCH_AOI_SIGHTING,
		SEARCH_AOI_COMPLETE,
		SEARCH_AOI_FAILED,
		
		/**
		 * Communication Inputs
		 */
		POKE_MM,
		BUSY_MM,
		ACK_MM,
		END_MM
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
		POKE_MM,
		TX_MM,
		END_MM,
		RX_MM
	}
	
	public ParentSearch()
	{
		name( Roles.PARENT_SEARCH.name() );
		nextState(States.IDLE, 1);
		
		//Initialize that a new search area needs to be searched
		processEvent(EventEnum.PS_NEW_AOI);
	}
	
	@Override
	public boolean processNextState()
	{
		//Is our next state now?
		if ( nextStateTime() != simulator().getTime() ) {
			return false;
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
				simulator().addInput(Roles.MISSION_MANAGER.name(), MissionManagerRole.Inputs.POKE_PS);
				nextState(States.IDLE, simulator().duration(Durations.PS_POKE_MM_DUR.range()));
				break;
			case TX_MM:
				current_output = output_queue.pollLast();
				int duration = 1;
				if ( current_output == Outputs.SEARCH_AOI ) {
					duration = simulator().duration(Durations.PS_TX_AOI_MM_DUR.range());
				} else if ( current_output == Outputs.SEARCH_TERMINATED ) {
					duration = simulator().duration(Durations.PS_TX_TERMINATE_MM_DUR.range());
				}
				nextState(States.END_MM, duration);
				break;
			case END_MM:
				//Now send the data that got sent from the transfer
				IObservable role = simulator().getRole(Roles.MISSION_MANAGER.name());
				if ( current_output == Outputs.SEARCH_AOI ) {
					role.addInput(MissionManagerRole.Inputs.SEARCH_AOI);
				} else if ( current_output == Outputs.SEARCH_TERMINATED ) {
					role.addInput(MissionManagerRole.Inputs.TERMINATE_SEARCH);
				}
				role.addInput(MissionManagerRole.Inputs.END_PS);
				nextState(States.IDLE, 1);
				break;
			case RX_MM:
				//Is it possible to receive for too long?
				nextState(States.IDLE, simulator().duration(Durations.PS_RX_MM_DUR.range()) );
				break;
			default:
				nextState(null, 1);
				break;
		}
		
		return true;
	}

	@Override
	public void updateState() {
		
		//Depending on our state we will handle different inputs.
		switch((States) state() ) {
			case IDLE:
				//If the MM is idle then do the following things in sequence
				//First check for Parent Search Commands
				if ( _input.contains(Inputs.POKE_MM) ) {
					simulator().addInput(Roles.MISSION_MANAGER.name(), MissionManagerRole.Inputs.ACK_PS);
					nextState(States.RX_MM, 1);
				} else if( output_queue.size() > 0 ) {
					nextState(States.POKE_MM, 1);
				}
				
				//TODO Handle more input values
				break;
			case POKE_MM:
				//TODO Handle simultaneous pokes from MM
				
				//Look for Busy or Ack from MM
				if ( _input.contains(Inputs.ACK_MM) ) {
					nextState(States.TX_MM, 1);
				} else if ( _input.contains(Inputs.BUSY_MM) ) {
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
				if ( _input.contains(Inputs.END_MM) ) {
					//TODO Handle all inputs from the MM
					
					if ( _input.contains(Inputs.SEARCH_AOI_COMPLETE) ) {
						//Do we do anything with this info?
					}
					
					nextState(States.IDLE, 1);
				}
			
				break;
			default:
				//Do Nothing for any state not mentioned here
				break;
		}

	}
	
	@Override
	public void processEvent(IEvent event) {
		
		switch((EventEnum) event) {
			case PS_NEW_AOI:
				output_queue.add(Outputs.SEARCH_AOI);
				break;
			case PS_TERMINATE_SEARCH:
				output_queue.clear(); //If we terminate the search then we dont need to send more data
				output_queue.add(Outputs.SEARCH_TERMINATED);
				break;
			default:
				//Do Nothing
				break;
		}
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
