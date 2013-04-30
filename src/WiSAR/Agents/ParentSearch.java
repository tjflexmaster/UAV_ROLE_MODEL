package WiSAR.Agents;

import CUAS.Simulator.IData;
import CUAS.Simulator.IStateEnum;
import CUAS.Simulator.Actor;
import WiSAR.Actors;
import WiSAR.Durations;
import WiSAR.Events.NewSearchAOIEvent;
import WiSAR.Events.TerminateSearchEvent;

public class ParentSearch extends Actor {
	
	//INTERNAL VARS
	boolean _search_active = true;
	
	/**
	 * These variables are used to keep track of how many search areas have been created, given, and searched.
	 * It is assumed that all these search areas are for the UAV team.
	 */
	int _total_search_aoi = 0;
	int _sent_search_aoi = 0;
	int _received_search_aoi = 0;
	
	/**
	 * Setup Outputs
	 */
	public enum Outputs implements IData
	{
		SEARCH_AOI,
		SEARCH_TERMINATED,
		PS_POKE,
		PS_END,
		PS_BUSY,
		PS_ACK, 
		
	}
	
	/**
	 * Define Role States
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
		name( Actors.PARENT_SEARCH.name() );
		nextState(States.IDLE, 1);
		
	}
	
	@Override
	public void processNextState()
	{
		//Is our next state now?
		if ( nextStateTime() != sim().getTime() ) {
			setObservations();
			return;
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
				sim().addOutput(Actors.MISSION_MANAGER.name(), Outputs.PS_POKE);
				nextState(States.IDLE, sim().duration(Durations.PS_POKE_MM_DUR.range()));
				break;
			case TX_MM:
				int duration;
				if ( _search_active ) {
					duration = sim().duration(Durations.PS_TX_AOI_MM_DUR.range());
				} else {
					duration = sim().duration(Durations.PS_TX_TERMINATE_MM_DUR.range());
				}
				nextState(States.END_MM, duration);
				break;
			case END_MM:
				//Now send the data that got sent from the transfer
				if ( _search_active ) {
					sim().addOutput(Actors.MISSION_MANAGER.name(), Outputs.SEARCH_AOI);
					_sent_search_aoi++;
				} else {
					sim().addOutput(Actors.MISSION_MANAGER.name(), Outputs.SEARCH_TERMINATED);
				}
				sim().addOutput(Actors.MISSION_MANAGER.name(),Outputs.PS_END);
				nextState(States.IDLE, 1);
				break;
			case RX_MM:
				//Is it possible to receive for too long?
				nextState(States.IDLE, sim().duration(Durations.PS_RX_MM_DUR.range()) );
				break;
			default:
				nextState(null, 1);
				break;
		}//end switch
		
		setObservations();
	}

	@Override
	public void processInputs() {
		
		//Always check for this input
		if ( _input.contains(NewSearchAOIEvent.Outputs.NEW_SEARCH_AOI) ) {
			_total_search_aoi++;
		}
		
		if ( _input.contains(TerminateSearchEvent.Outputs.TERMINATE_SEARCH) ) {
			_search_active = false;
		}
		
		//Depending on our state we will handle different inputs.
		switch((States) state() ) {
			case IDLE:
				//IF the parent search is idle then watch for
				if ( _input.contains(MissionManagerRole.Outputs.MM_POKE) ) {
					sim().addOutput(Actors.MISSION_MANAGER.name(), Outputs.PS_ACK);
					nextState(States.RX_MM, 1);
				} else if ( _total_search_aoi > _sent_search_aoi ) {
					nextState(States.POKE_MM, 1);
				}
				
				//TODO Handle more input values
				break;
			case POKE_MM:
				//TODO Handle simultaneous pokes from MM
				
				//Look for Busy or Ack from MM
				if ( _input.contains(MissionManagerRole.Outputs.MM_ACK) ) {
					nextState(States.TX_MM, 1);
				} else if ( _input.contains(MissionManagerRole.Outputs.MM_BUSY) ) {
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
				//Look for the MM_END input before handling other inputs
				if ( _input.contains(MissionManagerRole.Outputs.MM_END) ) {
					//TODO Handle all inputs from the MM
					
					if ( _input.contains(MissionManagerRole.Outputs.MM_SEARCH_AOI_COMPLETE) ) {
						_received_search_aoi++;
					}
					
					//TODO Add Search Failed to the Mission Manager
					if (_input.contains(MissionManagerRole.Outputs.MM_SEARCH_AOI_COMPLETE)) {
						_received_search_aoi = _sent_search_aoi;
					}
					
					//TODO Handle findings
					
					nextState(States.IDLE, 1);
				}
			
				break;
			default:
				//Do Nothing for any state not mentioned here
				break;
		}
		
		//Set the parent search observations after handling inputs
		setObservations();
		
		//Input has been handled so clear it
		_input.clear();

	}
	
	/**
	 * PRIVATE HELPER METHODS
	 */
	private void setObservations()
	{
		
		if ( !_search_active ) {
			sim().addObservation(Outputs.SEARCH_TERMINATED, this.name());
			sim().addObservation(Outputs.PS_BUSY, this.name());
		} else if (state() != States.IDLE ) {
			sim().addObservation(Outputs.PS_BUSY, this.name());
		}
	}
	
}
