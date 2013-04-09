package NewModel.Roles;

import java.util.ArrayList;

import NewModel.Events.Event;
import NewModel.Simulation.Assumptions;
import NewModel.Simulation.Simulator;
import NewModel.Utils.DataType;
import NewModel.Utils.PostOffice.POBOX;

public class ParentSearchRole extends Role {
	
	//INTERNAL VARS
	int _search_aoi_count = 0;

	public ParentSearchRole()
	{
		type(RoleType.ROLE_PARENT_SEARCH);
	}
	
	@Override
	public boolean processNextState()
	{
		//Is our next state now?
		if ( nextStateTime() != Simulator.getInstance().getTime() ) {
			return false;
		}
		
		//Update to the next state
		state(nextState());
		
		//Now determine what our next state will be
		//Each state has a designated duration
//		int duration = 1;
		//If a state isn't included then it doesn't deviate from the default
		switch(nextState()) {
			case PS_POKE_MM:
//				duration = 1000;
				nextState(RoleState.IDLE, Assumptions.PS_POKE_MM_DUR);
				break;
			case PS_TX_MM:
//				duration = 50;
				nextState(RoleState.PS_END_MM, Assumptions.PS_TX_MM_DUR);
				break;
			case PS_END_MM:
				nextState(RoleState.IDLE, 1);
				break;
			case PS_ACK_MM:
				nextState(RoleState.PS_RX_MM, 1);
				break;
			case PS_RX_MM:
				//TODO Dont just receive forever
				nextState(RoleState.IDLE, Assumptions.PS_RX_MM_DUR);
				break;
			case STARTING:
				//Schedule an event in the future, this gets things running
				//Since I am going to bother the MM in 30 time units I need to give him some data
				//I put this into the PostOffice so that when we communicate it can be transferred.
//				Simulator.getInstance()addPost(POBOX.PS_MM, DataType.SEARCH_AOI);
//				Simulator.getInstance()addPost(POBOX.PS_MM, DataType.TARGET_DESCRIPTION);
//				nextState(RoleState.PS_POKE_MM, 1);
//				_search_aoi_count++; //Add a new AOI
				nextState(RoleState.IDLE, 1);
				break;
			case IDLE:
				//TODO Look at TODO LIST 
				break;
			default:
				nextState(null, 1);
				break;
		}
		
		return true;
	}

	@Override
	public void updateState() {
		switch( state() ) {
			case PS_POKE_MM:
				//If the Parent Search is in this state then it wants to communicate with the MM
				//First Look at the MM State, if it has a listen state then begin to communicate
				if ( Simulator.getInstance().getRoleState(RoleType.ROLE_MISSION_MANAGER) == RoleState.MM_ACK_PS ) {
					//IF the MM Acknowledged our request then begin transmitting on the next time step
					nextState(RoleState.PS_TX_MM, 1);
				}
				break;
			case PS_TX_MM:
				//If I am transmitting then continue to transmit no matter what
				//TODO Handle Interruptions
				break;
			case PS_END_MM:
				//Do Nothing I will soon be idle
				break;
			case PS_ACK_MM:
				//Nothing Interrupts the PS here
				break;
			case PS_RX_MM:
				if ( Simulator.getInstance().getRoleState(RoleType.ROLE_MISSION_MANAGER) == RoleState.MM_END_PS ) {
					//Check the post office for data
					ArrayList<DataType> data = Simulator.getInstance().removePosts(POBOX.MM_PS);
					if ( data.contains( DataType.SEARCH_AOI_SIGHTING ) ) {
						//If there is a sighting then have them do nothing
						nextState(RoleState.IDLE, 1);
					} else if ( data.contains( DataType.SEARCH_AOI_COMPLETE) ) {
						//If the MM reports that nothing was found then give him a new AOI if there is one
						_search_aoi_count--;
						if ( _search_aoi_count > 0 ) {
							Simulator.getInstance().addPost(POBOX.PS_MM, DataType.SEARCH_AOI);
							nextState(RoleState.PS_POKE_MM, 1);
						}
						
						
					} else {
						nextState(RoleState.IDLE, 1);
					}
					
				}
				break;
			case STARTING:
			case IDLE:
				//Look to see if MM is communicating
				if ( Simulator.getInstance().getRoleState(RoleType.ROLE_MISSION_MANAGER) == RoleState.MM_POKE_PS ) {
					nextState(RoleState.PS_ACK_MM, 1);
				}
				
				break;
			default:
				//Do Nothing for any state not mentioned here
				break;
		}

	}
	
	@Override
	public void processEvents(ArrayList<Event> events) {
		for( Event e : events ) {
			switch(e.type()) {
				case PS_TERMINATE_SEARCH:
					createTerminateSearchEvent();
					break;
				case PS_NEW_AOI:
					createNewSearchAOIEvent();
					break;
				default:
					//Do nothing with the event
					break;
			}
		}
	}
	
	
	
	/**
	 * PRIVATE HELPER METHODS
	 */
	
	
	
	private void createTerminateSearchEvent()
	{
		if ( state() == RoleState.IDLE ) {
			Simulator.getInstance().addPost(POBOX.PS_MM, DataType.TERMINATE_SEARCH);
			nextState(RoleState.PS_POKE_MM, 1);
//			System.out.println("Created new Terminate Search Event");
		} else {
			assert false : "Unable to create Terminate Search Event, Parent Search is busy";
//			System.out.println("Unable to create Terminate Search Event, Parent Search is busy");
		}
	}
	
	private void createNewSearchAOIEvent()
	{
		if ( state() == RoleState.IDLE ) {
//			_search_aoi_count++;
			Simulator.getInstance().addPost(POBOX.PS_MM, DataType.SEARCH_AOI);
			nextState(RoleState.PS_POKE_MM, 1);
//			System.out.println("Created new Search AOI Event");
		} else {
			assert false : "Unable to create Search AOI Event, Parent Search is busy";
//			System.out.println("Unable to create Search AOI Event, Parent Search is busy");
		}
	}

}
