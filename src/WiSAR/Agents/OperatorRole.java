package WiSAR.Agents;

import java.util.ArrayList;

import CUAS.Simulator.Actor;
import CUAS.Simulator.IData;
import CUAS.Simulator.IStateEnum;
import WiSAR.Durations;

public class OperatorRole extends Actor {
	
	/**
	 *  STATE VARS
	 */
	
//	public enum Inputs implements IInputEnum
//	{
//		/**
//		 * PS Inputs
//		 */
//		SEARCH_AOI,
//		TERMINATE_SEARCH,
//		
//		/**
//		 * Search Inputs (Only received during RX)
//		 */
//		LOW_BAT,
//		LOW_HAG,
//		HAG_OK,
//		LOSS_OF_SIGNAL,
//		SIGNAL_OK,
//		UAV_CRASH,
//		FLIGHT_COMPLETE,
//		BAD_PATH,
//		OK_PATH,
//		
//		/**
//		 * Communication Inputs
//		 */
//		POKE_MM,
//		BUSY_MM,
//		ACK_MM,
//		END_MM,
//		POKE_VA,
//		BUSY_VA,
//		ACK_VA,
//		END_VA
//	}
	
	public enum Outputs implements IData
	{
		OP_POKE,
		OP_ACK,
		OP_BUSY,
		OP_TX,
		OP_END,
		OP_SEARCH_AOI_COMPLETE,
		OP_SEARCH_AOI_FAILED
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
		name( Roles.OPERATOR.name() );
		nextState(States.IDLE, 1);
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
//			case ACK_MM:
//				nextState(States.RX_MM, 1);
//				break;
			case RX_MM:
				nextState(States.IDLE, sim().duration(Durations.OPERATOR_RX_MM_DUR.range()) );
				break;
			case OBSERVING_UAV:
				//We should only get here if the UAV is flying
				nextState(States.OBSERVING_GUI, sim().duration(Durations.OPERATOR_OBSERVE_UAV_DUR.range()) );
				break;
			case OBSERVING_GUI:
				nextState(States.OBSERVING_UAV, sim().duration(Durations.OPERATOR_OBSERVE_UGUI_DUR.range()) );
				break;
			case LAUNCH_UAV:
				//Give command to the GUI to take off
				//Assumption: The UGUI is working and the pilot can communicate as needed
				//Launch the UAV and then wait for it to leave the take off state
				//TODO Send input to launch the UAV to the Pilot GUI
//				simulator().addInput(Roles.PILOT_GUI.name(), PilotGUIRole.Inputs);
				nextState(States.OBSERVING_GUI, sim().duration(Durations.OPERATOR_LAUNCH_UAV_DUR.range()) );
				
				break;
			case POKE_GUI:
				nextState(States.IDLE, sim().duration(Durations.OPERATOR_POKE_UGUI_DUR.range()) );
				break;
			case TX_GUI:
				//TODO base this duration on the items being transmitted
				nextState(States.END_GUI, sim().duration(Durations.OPERATOR_TX_UGUI_DUR.range()) );
				break;
			case END_GUI:
				//After we do something on the GUI we click a "save button" signaling completion
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
			case IDLE:
				nextState(null, 0);
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
		
		switch((States) state() ) {
			case POKE_MM:
				//Always respond to an ACK_MM
				if (_input.contains(MissionManagerRole.Outputs.MM_ACK)) {
					nextState(States.TX_MM, 1);
				}
				if ( _input.contains(MissionManagerRole.Outputs.MM_BUSY)) {
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
				if ( _input.contains(MissionManagerRole.Outputs.MM_END) ) {
					//TODO Handle relevant inputs from the MM such as terminate search and new search aoi
					ArrayList<IData> gui_feed = sim().getObservations(Roles.OPERATOR_GUI.name());
					if(_input.contains(MissionManagerRole.Outputs.MM_SEARCH_AOI)){
						if(gui_feed.contains(OperatorGUIRole.Outputs.IN_AIR)){
							nextState(States.POKE_GUI,1);
						}else{
							nextState(States.LAUNCH_UAV,1);
						}
					}else if(_input.contains(MissionManagerRole.Outputs.MM_SEARCH_TERMINATED)){
						if(gui_feed.contains(OperatorGUIRole.Outputs.IN_AIR)){
							nextState(States.POKE_GUI,1);
						}else{
							nextState(States.IDLE,1);
						}
					}
				}
				
				//TODO Handle other interruptions
				break;
			case LAUNCH_UAV:
				//Technically the pilot is observing the UAV here so we can update the uav state
				//Get UAV outputs
				uav_output = sim().getObservations(Roles.UAV.name());
				
				//TODO Do we care about any of the UAV outputs at take off? Yes
				
				//TODO Watch for the UAV to be flying or crashed
				
				break;
			case OBSERVING_UAV:
				
				uav_output = sim().getObservations(Roles.UAV.name());
				//TODO handle this output accordingly
				
				
				break;
			case OBSERVING_GUI:
				gui_output = sim().getObservations(Roles.OPERATOR_GUI.name());
				
				//TODO Handle GUI input
				
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
				gui_output = sim().getObservations(Roles.OPERATOR_GUI.name());
				
				//TODO Check that the operator gui is accessible
//				if ( gui_output.contains() ) 
					nextState(States.TX_GUI, 1);
//				else
//					nextState(States.IDLE, 1);
				
				break;
			case TX_GUI:
				//TODO The operator is observing the GUI while using it, if it changes then he should respond to those changes instead of continuing what he is doing.
				//TODO Also make sure the GUI is still accessible
				gui_output = sim().getObservations(Roles.OPERATOR_GUI.name());
				
				//TODO Listen to other interruptions from the other Roles
				break;
			case END_GUI:
			case WAIT_GUI:
				//No interruptions
				break;
			case IDLE:
				//TODO Watch for commands from the MM or VO
				if(_input.contains(MissionManagerRole.Outputs.MM_POKE)){
					sim().addOutput(Roles.MISSION_MANAGER.name(), Outputs.OP_ACK);
					nextState(States.RX_MM,1);
				}
				//TODO Act on internal states, such as UAV airborne, or more search areas need to be searched
				//TODO Do this using priority
				
				break;
			default:
				//Do nothing for states not mentioned
				break;
		}
		_input.clear();
	}

	
	/**
	 * Private HELPER METHODS
	 */
	

}
