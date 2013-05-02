package WiSAR.Agents;

import java.util.ArrayList;
import java.util.PriorityQueue;

import CUAS.Simulator.Actor;
import CUAS.Simulator.IData;
import CUAS.Simulator.IStateEnum;
import WiSAR.Actors;
import WiSAR.Durations;

public class OperatorRole extends Actor {
	
	PriorityQueue<IData> tasks;
	
	/**
	 * private assumptions
	 */
	
	private enum Assumptions{
		LANDED,
		FLYING,
		LANDING,
		
	}
	Assumptions uav_state;
	Outputs current_output;
	/**
	 *  STATE VARS
	 */
	
	public enum Outputs implements IData
	{
		OP_POKE,
		OP_ACK,
		OP_BUSY,
		OP_TX,
		OP_END,
		/**
		 * MM Outputs
		 */
		OP_SEARCH_AOI_COMPLETE,
		OP_SEARCH_AOI_FAILED,
		TAKE_OFF,
		LOITER, 
		LAND,
		PATH, 
		
		/**
		 * OGUI Outputs
		 */
		END_SEARCH, 
		OP_SEARCH_AOI_COMPLETE_ACK,
		OP_PATH_NEW,
		OP_PATH_END, OP_LAND
	}
	
	public enum States implements IStateEnum
	{
		/**
		 * HUMAN Interaction States
		 */
		IDLE,
		POKE_MM,
		TX_MM,
		END_MM,
		ACK_MM,
		RX_MM,
		
		/**
		 * UGUI States
		 */
		OBSERVING_GUI,
		POKE_GUI,
		TX_GUI,
		END_GUI,
		WAIT_GUI,
		
		/**
		 * HANDLING UAV STATES
		 */
		OBSERVING_UAV,
		LAUNCH_UAV,
		POST_FLIGHT,
		POST_FLIGHT_COMPLETE
	}

	//Default Values
//	DataType _search_state = DataType.SEARCH_NONE;
	
	//UAV Related States
//	RoleState _uav_state = RoleState.UAV_READY;
////	DataType _uav_state = DataType.UAV_READY;
//	DataType _bat_state = DataType.UAV_BAT_OK;
//	DataType _path_state = DataType.UAV_PATH_OK;
//	DataType _hag_state = DataType.UAV_HAG_OK;
//	DataType _signal_state = DataType.UAV_SIGNAL_OK;
//	DataType _plan_state = DataType.UAV_FLIGHT_PLAN_NO;
	
	//Bad Path Internal Vars
//	int _bad_path_start_time = 0;
//	int _bad_path_allowance_threshold = Simulator.getInstance().duration(Duration.PILOT_BAD_PATH_THRESHOLD.name()); //How long do we allow for a bad path before landing the UAV?
	
	//Signal Lost Vars
//	boolean _reset_flight_plan = false;
	
	/**
	 * END STATE VARS
	 */
	

	public OperatorRole()
	{
		name( Actors.OPERATOR.name() );
		nextState(States.IDLE, 1);
		uav_state = Assumptions.LANDED;
		current_output = null;
		tasks = new PriorityQueue<IData>();
	}
	
	@Override
	public void processNextState() {
		//Is our next state now?
		if ( nextStateTime() != sim().getTime() ) {
			return;
		}
		
		//Update to the next state
		state(nextState());
		
		//Now determine what our next state will be
		//Each state has a designated duration
		//If a state isn't included then it doesn't deviate from the default
		switch((States) nextState()) {
			case IDLE:
				if(!tasks.isEmpty()){
					IData next_action = tasks.peek();
					if(next_action == Outputs.OP_PATH_NEW){
						nextState(States.POKE_GUI,1);
					}else if(next_action == Outputs.OP_PATH_END){
						nextState(States.POKE_GUI,1);
					}
				}else
					nextState(null, 0);
				break;
			case POKE_MM:
				nextState(States.IDLE, sim().duration(Durations.OPERATOR_POKE_MM_DUR.range()) );
				break;
			case TX_MM:
				//TODO change this duration based on the data being transmitted
				nextState(States.END_MM, sim().duration(Durations.OPERATOR_TX_MM_DUR.range()) );
				break;
			case END_MM:
				nextState(States.IDLE, 1);
				break;
			case RX_MM:
				nextState(States.IDLE, sim().duration(Durations.OPERATOR_RX_MM_DUR.range()) );
				break;
			case OBSERVING_UAV:
				//We should only get here if the UAV is flying
				if(!tasks.isEmpty()){
					IData next_action = tasks.peek();
					if(next_action == Outputs.OP_PATH_NEW){
						nextState(States.POKE_GUI,1);
					}else if(next_action == Outputs.OP_PATH_END){
						nextState(States.POKE_GUI,1);
					}else if(next_action == Outputs.OP_SEARCH_AOI_COMPLETE){
						nextState(States.POKE_MM,1);
					}
				}else
					nextState(States.OBSERVING_GUI, sim().duration(Durations.OPERATOR_OBSERVE_UAV_DUR.range()) );
				break;
			case OBSERVING_GUI:
				if(!tasks.isEmpty()){
					IData next_action = tasks.peek();
					if(next_action == Outputs.OP_PATH_NEW){
						nextState(States.POKE_GUI,1);
					}else if(next_action == Outputs.OP_PATH_END){
						nextState(States.POKE_GUI,1);
					}else if(next_action == Outputs.OP_SEARCH_AOI_COMPLETE){
						nextState(States.POKE_MM,1);
					}
				}else
					nextState(States.OBSERVING_UAV, sim().duration(Durations.OPERATOR_OBSERVE_UGUI_DUR.range()) );
				break;
			case LAUNCH_UAV:
				//Give command to the GUI to take off
				//Assumption: The UGUI is working and the pilot can communicate as needed
				//Launch the UAV and then wait for it to leave the take off state
				//TODO Send input to launch the UAV to the Pilot GUI
//				simulator().addInput(Actors.PILOT_GUI.name(), PilotGUIRole.Inputs);
				nextState(States.OBSERVING_UAV, 1 );
				
				break;
			case POKE_GUI:
				sim().addOutput(Actors.OPERATOR_GUI.name(), Outputs.OP_POKE);
				nextState(States.IDLE, sim().duration(Durations.OPERATOR_POKE_UGUI_DUR.range()) );
				break;
			case TX_GUI:
				//TODO base this duration on the items being transmitted
				nextState(States.END_GUI, sim().duration(Durations.OPERATOR_TX_UGUI_DUR.range()) );
				break;
			case END_GUI:
				sim().addOutput(Actors.OPERATOR_GUI.name(), Outputs.OP_END);
				IData output = tasks.poll();
				if(output == Outputs.OP_PATH_NEW){
					sim().addOutput(Actors.OPERATOR_GUI.name(), Outputs.OP_PATH_NEW);
				}else if(output == Outputs.OP_PATH_END){
					sim().addOutput(Actors.OPERATOR_GUI.name(), Outputs.OP_PATH_END);
				}
				if(uav_state == Assumptions.FLYING){
					//TODO check for communications that are relevant to when the UAV is aloft
				}else if(output == Outputs.OP_PATH_NEW){
					//if(current_output == Outputs.PATH){
						sim().addOutput(Actors.OPERATOR_GUI.name(), Outputs.TAKE_OFF);
						nextState(States.LAUNCH_UAV,sim().duration(Durations.OPERATOR_LAUNCH_UAV_DUR.range()));
					//}
				}
				nextState(States.WAIT_GUI, 1);
				break;
			case WAIT_GUI:
				//Helper State, in 1 time steps the UGUI passes data to the UAV
				// in the next time step it updates itself.  After this the Pilot can then
				// read the latest data on the gui.
				nextState(States.OBSERVING_GUI, 2);
				break;
			case POST_FLIGHT:
				nextState(States.POST_FLIGHT_COMPLETE, sim().duration(Durations.OPERATOR_POST_FLIGHT_LAND_DUR.range()) );
				break;
			case POST_FLIGHT_COMPLETE:
				//TODO set the next state based on if their is an active search or not
				nextState(States.IDLE, 1);
				break;
			default:
				nextState(null, 0);
				break;
		}
		
	}
	

	@Override
	public void processInputs() {
		ArrayList<IData> uav_output;
		ArrayList<IData> gui_output;
		
		//Pull Input and any observations that need to be made from the simulator
		ArrayList<IData> input = sim().getInput(this.name());
		
		switch((States) state() ) {
			case IDLE:
				//TODO Watch for commands from the MM or VO
				if (input.contains(MissionManagerRole.Outputs.MM_POKE)) {
					sim().addOutput(Actors.MISSION_MANAGER.name(), Outputs.OP_ACK);
					nextState(States.RX_MM,1);
				}
				//TODO Act on internal states, such as UAV airborne, or more search areas need to be searched
				//TODO Do this using priority
				
				break;
			case POKE_MM:
				//Always respond to an ACK_MM
				if (input.contains(MissionManagerRole.Outputs.MM_ACK)) {
					nextState(States.TX_MM, 1);
				}
				if ( input.contains(MissionManagerRole.Outputs.MM_BUSY)) {
					nextState(States.IDLE, 1);
				}
				//TODO listen for more inputs such as PILOT GUI Alarms
				break;
			case TX_MM:
				//TODO Handle interruptions
				
				break;
			case END_MM:
				//Do nothing, output was sent when entering the END_MM state
			case ACK_MM:
				//Do nothing
				break;
			case RX_MM:
				//Look for end of TX
				//Whatever the Pilot does next it should be on the next time step so that it does not
				//appear that he is receiving after the MM stopped transmitting.
				if ( input.contains(MissionManagerRole.Outputs.MM_END) ) {
					//TODO Handle relevant inputs from the MM such as terminate search and new search aoi
					ArrayList<IData> gui_feed = sim().getObservations(Actors.OPERATOR_GUI.name());
					if(input.contains(MissionManagerRole.Outputs.MM_SEARCH_AOI)){
						tasks.add(Outputs.OP_PATH_NEW);
//						if(gui_feed.contains(OperatorGUIRole.Outputs.IN_AIR)){
//							nextState(States.POKE_GUI,1);
//						}else{
//							nextState(States.POKE_GUI,1);
//						}
					}else if(input.contains(MissionManagerRole.Outputs.MM_SEARCH_TERMINATED)){
						assert gui_feed.contains(OperatorGUIRole.Outputs.IN_AIR) : "The UAV has not yet left the ground";
						tasks.add(Outputs.END_SEARCH);
						//nextState(States.POKE_GUI,1);
					}
					if(gui_feed.contains(OperatorGUIRole.Outputs.IN_AIR)){
						nextState(States.OBSERVING_GUI,1);
					} else {
						nextState(States.IDLE,1);
					}
				}
				
				//TODO Handle other interruptions
				break;
			case LAUNCH_UAV:
				//Technically the pilot is observing the UAV here so we can update the uav state
				//Get UAV outputs
				uav_output = sim().getObservations(Actors.UAV.name());
				//TODO Do we care about any of the UAV outputs at take off? Yes
				assert !uav_output.contains(UAVRole.Outputs.UAV_FLYING) : "UAV should not be flying at this point";
				//TODO Watch for the UAV to be flying or crashed
				
				break;
			case OBSERVING_UAV:
				
				uav_output = sim().getObservations(Actors.UAV.name());
				//TODO handle this output accordingly
				if(uav_output.contains(UAVRole.Outputs.UAV_FLIGHT_PLAN_NO)){
					//current_output = Outputs.LAND;
					tasks.add(Outputs.OP_LAND);
					nextState(States.OBSERVING_UAV,1);
				}
				break;
			case OBSERVING_GUI:
				gui_output = sim().getObservations(Actors.OPERATOR_GUI.name());
				
				//TODO Handle GUI input
				if(gui_output.contains(OperatorGUIRole.Outputs.OGUI_PATH_COMPLETE)){
					tasks.add(Outputs.OP_SEARCH_AOI_COMPLETE);
					if(gui_output.contains(OperatorGUIRole.Outputs.OGUI_PATH_NO)){
						tasks.add(Outputs.OP_LAND);
					}
					nextState(States.OBSERVING_GUI,1);
				}
				
				break;
			case POST_FLIGHT:
				//Pilot is manually doing something with the plane, handle any interruptions here
				//TODO Handle interruptions
				break;
			case POST_FLIGHT_COMPLETE:
				//Helper Method do nothing
				break;
			case POKE_GUI:
				//Check the GUI output to make sure it is accessible
				gui_output = sim().getObservations(Actors.OPERATOR_GUI.name());
				
				//TODO Check that the operator gui is accessible
//				if ( gui_output.contains() ) 
					nextState(States.TX_GUI, 1);
//				else
//					nextState(States.IDLE, 1);
				
				break;
			case TX_GUI:
				//TODO The operator is observing the GUI while using it, if it changes then he should respond to those changes instead of continuing what he is doing.
				//TODO Also make sure the GUI is still accessible
				gui_output = sim().getObservations(Actors.OPERATOR_GUI.name());
				
				//TODO Listen to other interruptions from the other Roles
				break;
			case END_GUI:
				break;
			case WAIT_GUI:
				//No interruptions
				break;
			default:
				//Do nothing for states not mentioned
				break;
		}
		
	}

	
	/**
	 * Private HELPER METHODS
	 */
	

}
