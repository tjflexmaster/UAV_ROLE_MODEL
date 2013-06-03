package WiSAR.Agents;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.PriorityQueue;

import CUAS.Simulator.Actor;
import CUAS.Simulator.IData;
import CUAS.Simulator.IStateEnum;
import WiSAR.Actors;
import WiSAR.DataPriorityComparator;
import WiSAR.Durations;
import WiSAR.IPriority;
import WiSAR.Transition;
import WiSAR.submodule.UAVBattery;
import WiSAR.submodule.UAVFlightPlan;
import WiSAR.submodule.UAVHeightAboveGround;
import WiSAR.submodule.UAVSignal;

public class OperatorRole extends Actor {
	
	PriorityQueue<IData> tasks;
	
	ArrayList<IData> _uav_observations;
	ArrayList<IData> _gui_observations;
	
	OperatorGUIRole.Outputs  _gui_state;
	UAVRole.Outputs _uav_state;
	UAVSignal.Outputs _uav_signal;
	UAVHeightAboveGround.Outputs _uav_hag;
	UAVFlightPlan.Outputs _uav_flight_plan;
	UAVBattery.Outputs _uav_battery;
	
	boolean _flyby_mode = false;
	boolean _search_active = false;
	int _total_search_aoi = 0;
	int _sent_search_aoi = 0;
	int _completed_search_aoi = 0;
	boolean _gui_alert = false;
	
	/**
	 *  STATE VARS
	 */
	public enum Outputs implements IData, IPriority
	{
		/**
		 * BAsic communication
		 */
		OP_POKE,
		OP_ACK,
		OP_BUSY,
		OP_END,
		
		/**
		 * MM Outputs
		 */
		OP_SEARCH_AOI_COMPLETE(10),
//		OP_SEARCH_AOI_FAILED,
		
		/**
		 * OGUI Outputs
		 */
		OP_TAKE_OFF(1),
		OP_LOITER(2), 
		OP_RESUME(2),
		OP_LAND(5),
		OP_NEW_FLIGHT_PLAN(11),
		OP_MODIFY_FLIGHT_PLAN(2),
		OP_FLYBY_START_T(15),
		OP_FLYBY_START_F(15),
		OP_FLYBY_END(15),
		
		/**
		 * Output to UAV
		 */
		OP_POST_FLIGHT(7),
		OP_POST_FLIGHT_COMPLETE;
		
		private int _priority;
		
		Outputs()
		{
			_priority = 1000;
		}
		
		Outputs(int priority)
		{
			_priority = priority;
		}
		
		@Override
		public int priority()
		{
			return _priority;
		}
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
		RX_MM,
		RX_VO,
		
		
		/**
		 * UGUI States
		 */
		OBSERVING_GUI,
		FLYBY_GUI, //Operator is making continuous adjustment to UAV flight per VO directions
		POKE_GUI,
		TX_GUI,
		END_GUI,
		
		/**
		 * HANDLING UAV STATES
		 */
		OBSERVING_UAV,
		LAUNCH_UAV,
		POST_FLIGHT,
		POST_FLIGHT_COMPLETE
	}

	/**
	 * END STATE VARS
	 */
	
	/**
	 * Initiate Transitions
	 */
	public ArrayList<Transition> Transitions = new ArrayList<Transition>();
	
	public OperatorRole()
	{
		name( Actors.OPERATOR.name() );
		nextState(States.IDLE, 1);
		_uav_state = UAVRole.Outputs.UAV_READY;
		tasks = new PriorityQueue<IData>(5, new DataPriorityComparator());

		//define transitions
		//rx & tx AOI
		Transitions.add(new Transition(States.IDLE, MissionManagerRole.Outputs.MM_POKE, States.RX_MM, Outputs.OP_ACK));
		Transitions.add(new Transition(States.RX_MM, MissionManagerRole.Outputs.MM_NEW_SEARCH_AOI, States.LAUNCH_UAV, Outputs.OP_NEW_FLIGHT_PLAN));
		Transitions.add(new Transition(States.LAUNCH_UAV, UAVRole.Outputs.UAV_READY, States.OBSERVING_GUI, Outputs.OP_TAKE_OFF));
		//observe UAV
		Transitions.add(new Transition(States.OBSERVING_GUI, null, States.OBSERVING_UAV, null));
		Transitions.add(new Transition(States.OBSERVING_UAV, null, States.OBSERVING_GUI, null));
		//rx & tx TERM
		Transitions.add(new Transition(States.RX_MM, MissionManagerRole.Outputs.MM_TERMINATE_SEARCH_OP, States.TX_GUI, Outputs.OP_LAND));
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
		Outputs task;
		//Each state has a designated duration
		//If a state isn't included then it doesn't deviate from the default
		switch((States) nextState()) {
			case IDLE:
				nextState(null, 0);
				break;
			case POKE_MM:
				//Send the Poke protocol output
				sim().addOutput(Actors.MISSION_MANAGER.name(), Outputs.OP_POKE);
				nextState(States.IDLE, sim().duration(Durations.OPERATOR_POKE_MM_DUR.range()) );
				break;
			case TX_MM:
				//TODO change this duration based on the data being transmitted
				nextState(States.END_MM, sim().duration(Durations.OPERATOR_TX_MM_DUR.range()) );
				break;
			case END_MM:
				sim().addOutput(Actors.MISSION_MANAGER.name(), Outputs.OP_END);
				sim().addOutput(Actors.MISSION_MANAGER.name(), tasks.poll());

				if(_completed_search_aoi == _sent_search_aoi){
					tasks.add(Outputs.OP_LAND);
				}
				nextState(States.IDLE, 1);
				break;
			case RX_MM:
				nextState(States.IDLE, sim().duration(Durations.OPERATOR_RX_MM_DUR.range()) );
				break;
			case RX_VO:
				nextState(States.IDLE, sim().duration(Durations.OPERATOR_RX_MM_DUR.range()) );
				break;
			case LAUNCH_UAV:
				//Give command to the GUI to take off
				//assert _uav_observations.contains(UAVRole.Outputs.UAV_READY) : "Only launch a UAV that is ready to be launched";
				//assert _uav_observations.contains(UAVFlightPlan.Outputs.UAV_FLIGHT_PLAN_YES) : "Only launch a UAV that has a flight plan";
				
				//Assumption: The UGUI is working and the pilot can communicate as needed
				//Launch the UAV and then wait for it to leave the take off state
				task = (Outputs) tasks.poll();
				sim().addOutput(Actors.OPERATOR_GUI.name(), task);
				nextState(null, 0);
				break;
			case OBSERVING_UAV:
				//We should only get here if the UAV is flying
				nextState(States.OBSERVING_GUI, sim().duration(Durations.OPERATOR_OBSERVE_UAV_DUR.range()) );
				break;
			case OBSERVING_GUI:
				nextState(States.OBSERVING_UAV, sim().duration(Durations.OPERATOR_OBSERVE_UGUI_DUR.range()) );
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
				task = (Outputs) tasks.poll();
				if ( task == Outputs.OP_NEW_FLIGHT_PLAN ) {
					_sent_search_aoi++;
				}
				sim().addOutput(Actors.OPERATOR_GUI.name(), task);
				nextState(States.OBSERVING_GUI, 3);
				break;
			case FLYBY_GUI:
				//Stay in this state until the VO says they are done with it
				sim().addOutput(Actors.OPERATOR_GUI.name(), tasks.poll());
				nextState(null, 0);
				break;
			case POST_FLIGHT:
				assert _uav_state == UAVRole.Outputs.UAV_LANDED : "Post flight when the UAV was not landed";
				nextState(States.POST_FLIGHT_COMPLETE, sim().duration(Durations.OPERATOR_POST_FLIGHT_LAND_DUR.range()) );
				break;
			case POST_FLIGHT_COMPLETE:
				assert tasks.poll() == Outputs.OP_POST_FLIGHT : "entered post flight complete stage without task post_flight_complete being in the tasks";
				sim().addOutput(Actors.UAV.name(), Outputs.OP_POST_FLIGHT_COMPLETE);
				nextState(States.IDLE, 1);
				break;
			default:
				nextState(null, 0);
				break;
		}
		
	}
	

	@Override
	public void processInputs() {
//		ArrayList<IData> gui_observations = sim().getObservations(Actors.OPERATOR_GUI.name());
		ArrayList<IData> mm_observations;
		
		//Pull Input and any observations that need to be made from the simulator
		ArrayList<IData> input = sim().getInput(this.name());
		
		switch((States) state() ) {
		
			case IDLE:
				
				boolean accepted_poke = false;
				//First accept MM poke
				if ( input.contains(MissionManagerRole.Outputs.MM_POKE) ) {
					sim().addOutput(Actors.MISSION_MANAGER.name(), Outputs.OP_ACK);
					nextState(States.RX_MM,1);
					accepted_poke = true;
				}
				//Second accept VO poke
				if ( input.contains(VideoOperatorRole.Outputs.VO_POKE) ) {
					if ( !accepted_poke ) {
						sim().addOutput(Actors.VIDEO_OPERATOR.name(), Outputs.OP_ACK);
						nextState(States.RX_VO,1);
						accepted_poke = true;
					} else {
						sim().addOutput(Actors.VIDEO_OPERATOR.name(), Outputs.OP_BUSY);
					}
				}
				
				_uav_observations = sim().getObservations(Actors.UAV.name());
				parseUAVStateFromUAV(_uav_observations);
				
				//Third act based on what we know about the UAV state
				if ( !accepted_poke ) {
					switch(_uav_state) {
						case UAV_FLYING_NORMAL:
						case UAV_FLYING_FLYBY:
						case UAV_LOITERING:
						case UAV_LANDING:
						case UAV_TAKE_OFF:
							nextState(States.OBSERVING_GUI, 1);
							break;
						case UAV_LANDED:
							tasks.add(Outputs.OP_POST_FLIGHT);
							nextState(States.POST_FLIGHT, 1);
							break;
						default:
							break;
					}
					
					//If there are any tasks then do those
					doNextTask();
				}
				break;
				
			case POKE_MM:
				
				mm_observations = sim().getObservations(Actors.MISSION_MANAGER.name());
				
				//Always respond to an ACK_MM
				if ( input.contains(MissionManagerRole.Outputs.MM_ACK )) {
					nextState(States.TX_MM, 1);
				} else if ( input.contains(MissionManagerRole.Outputs.MM_BUSY) || 
						mm_observations.contains(MissionManagerRole.Outputs.MM_BUSY)) {
					nextState(States.IDLE, 1);
				} else {
					//MM is more important so accept pokes from them
					if ( input.contains(MissionManagerRole.Outputs.MM_POKE) ) {
						sim().addOutput(Actors.MISSION_MANAGER.name(), Outputs.OP_ACK);
						nextState(States.RX_MM, 1);
					} 
					if(input.contains(VideoOperatorRole.Outputs.VO_POKE)) {
						//Dont accept Operator pokes while poking PS
						sim().addOutput(Actors.VIDEO_OPERATOR.name(), Outputs.OP_BUSY);
					}
				}
				break;
				
			case TX_MM:
				
				//No interruptions
				break;
				
			case END_MM:
				
				//Immediately perform next task instead of going idle first
				doNextTask();
				break;
				
			case RX_MM:
				
				//Look for end of TX
				//Whatever the Pilot does next it should be on the next time step so that it does not
				//appear that he is receiving after the MM stopped transmitting.
				if ( input.contains(MissionManagerRole.Outputs.MM_END) ) {
					if(input.contains(MissionManagerRole.Outputs.MM_NEW_SEARCH_AOI) ){
						tasks.add(Outputs.OP_NEW_FLIGHT_PLAN);
						tasks.add(Outputs.OP_TAKE_OFF);
						_total_search_aoi++;
					}
					else if ( input.contains(MissionManagerRole.Outputs.MM_TERMINATE_SEARCH_OP) ) {
						_search_active = false;
						switch(_uav_state) {
							case UAV_FLYING_NORMAL:
							case UAV_FLYING_FLYBY:
							case UAV_LOITERING:
							case UAV_TAKE_OFF:
								tasks.add(Outputs.OP_LAND);
								break;
							default:
								break;
						}
						
					}
					//Go Idle first in case more information needs to be sent
					nextState(States.IDLE, 1);
				}
				break;
				
			case RX_VO:
				
				if ( input.contains(VideoOperatorRole.Outputs.VO_END) ) {
					//TODO Handle the Bad Stream output
					
					//Go Idle first in case more information needs to be sent
					nextState(States.IDLE, 1);
				}
				break;
				
			case LAUNCH_UAV:
				
				//Technically the pilot is observing the UAV here so we can update the uav state
				_uav_observations = sim().getObservations(Actors.UAV.name());
				parseUAVStateFromUAV(_uav_observations);
				
				switch(_uav_state) {
					case UAV_FLYING_NORMAL:
					case UAV_LOITERING:
					case UAV_LANDING:
						nextState(States.OBSERVING_GUI, 1);
						break;
					case UAV_LANDED:
						nextState(States.POST_FLIGHT, 1);
					case UAV_FLYING_FLYBY:
						nextState(States.FLYBY_GUI, 1);
						break;
					default:
						break;
				}
				break;
				
			case OBSERVING_UAV:
				
				//Update the operator observations
				_uav_observations = sim().getObservations(Actors.UAV.name());
				parseUAVStateFromUAV(_uav_observations);
				
				//Check the state of the UAV
				if ( _uav_state == UAVRole.Outputs.UAV_LANDED ) {
					tasks.add(Outputs.OP_POST_FLIGHT);
				}
				else if ( _uav_state == UAVRole.Outputs.UAV_READY ) {
					if ( _search_active ) {
						if ( _uav_flight_plan == UAVFlightPlan.Outputs.UAV_FLIGHT_PLAN_YES ) {
							tasks.add(Outputs.OP_TAKE_OFF);
						}
					}
				}
				
				//Check the HAG of the UAV
				if ( _uav_hag == UAVHeightAboveGround.Outputs.HAG_LOW ) {
					tasks.add(Outputs.OP_MODIFY_FLIGHT_PLAN);
				}
				
				//Now deal with pokes
				boolean accepted_poke_ob_uav = false;
				//First accept MM poke
				if (input.contains(MissionManagerRole.Outputs.MM_POKE)) {
					sim().addOutput(Actors.MISSION_MANAGER.name(), Outputs.OP_ACK);
					nextState(States.RX_MM,1);
					accepted_poke_ob_uav = true;
				}
				//Second accept VO poke
				if ( input.contains(VideoOperatorRole.Outputs.VO_POKE) ) {
					if ( !accepted_poke_ob_uav ) {
						sim().addOutput(Actors.VIDEO_OPERATOR.name(), Outputs.OP_ACK);
						nextState(States.RX_VO,1);
						accepted_poke_ob_uav = true;
					} else {
						sim().addOutput(Actors.VIDEO_OPERATOR.name(), Outputs.OP_BUSY);
					}
				}
				
				//Third act based on what we know about the UAV state
				if ( !accepted_poke_ob_uav ) {
					doNextTask();
				}
				break;
				
			case OBSERVING_GUI:
				
				_gui_observations = sim().getObservations(Actors.OPERATOR_GUI.name());
				parseUAVStateFromGUI(_gui_observations);
				//If we are in alarm mode then only look at the following alarm items
				if ( _gui_alert ) {
					
					if ( _uav_hag == UAVHeightAboveGround.Outputs.HAG_LOW ) {
						tasks.add(Outputs.OP_MODIFY_FLIGHT_PLAN);
					}
					
					if ( _uav_battery == UAVBattery.Outputs.BATTERY_LOW ) {
						tasks.add(Outputs.OP_LAND);
					}
					
					if ( _uav_signal == UAVSignal.Outputs.SIGNAL_LOST ) {
						tasks.add(Outputs.OP_MODIFY_FLIGHT_PLAN);
					}
					
					//Handle the results from the flyby
					if ( _gui_observations.contains(VideoOperatorRole.Outputs.VO_FLYBY_END_FAILED) ) {
						tasks.add(Outputs.OP_FLYBY_END);
					} else if ( _gui_observations.contains(VideoOperatorRole.Outputs.VO_FLYBY_END_SUCCESS) ) {
						tasks.add(Outputs.OP_FLYBY_END);
					}
					
				}
				else if ( _gui_observations.contains(OperatorGUIRole.Outputs.OGUI_STATE_NORMAL ) ) {
					
					if ( _uav_state == UAVRole.Outputs.UAV_LANDED ) {
						tasks.add(Outputs.OP_POST_FLIGHT);
					}
					
					else if ( _uav_state == UAVRole.Outputs.UAV_READY ) {
						if ( _uav_flight_plan == UAVFlightPlan.Outputs.UAV_FLIGHT_PLAN_YES ) {
							tasks.add(Outputs.OP_TAKE_OFF);
						}
					}
					
					if ( _completed_search_aoi < _sent_search_aoi ) {
						Iterator<IData> iter = _gui_observations.iterator();
						int c = 0;
						while(iter.hasNext()){
							if(iter.next() == UAVFlightPlan.Outputs.UAV_FLIGHT_PLAN_COMPLETE){
								c++;
							}
						}
						while(c > _completed_search_aoi){
							_completed_search_aoi++;
							tasks.add(Outputs.OP_SEARCH_AOI_COMPLETE); 
							//tasks.add(Outputs.OP_LAND);
						}
					}
					
					//If we don't have a flyby request in our task queue then see if we need to add one
					if ( !tasks.contains(Outputs.OP_FLYBY_START_F) && !tasks.contains(Outputs.OP_FLYBY_START_T) ) {
						for(int i=0; i < _gui_observations.size(); i++) {
							if ( _gui_observations.get(i) == VideoOperatorRole.Outputs.VO_FLYBY_REQ_F ||
									 _gui_observations.get(i) == MissionManagerRole.Outputs.MM_FLYBY_REQ_F ) {
								tasks.add(Outputs.OP_FLYBY_START_F);
								break;
							}
							if ( _gui_observations.get(i) == VideoOperatorRole.Outputs.VO_FLYBY_REQ_T ||
									 _gui_observations.get(i) == MissionManagerRole.Outputs.MM_FLYBY_REQ_T ) {
								tasks.add(Outputs.OP_FLYBY_START_T);
								break;
							}
						}
					}
						
					//TODO Look at any other important input
					
				}
				
				//Now deal with pokes
				boolean accepted_poke_ob_gui = false;
				//First accept MM poke
				if (input.contains(MissionManagerRole.Outputs.MM_POKE)) {
					sim().addOutput(Actors.MISSION_MANAGER.name(), Outputs.OP_ACK);
					nextState(States.RX_MM,1);
					accepted_poke_ob_gui = true;
				}
				
				//Second accept VO poke
				if ( input.contains(VideoOperatorRole.Outputs.VO_POKE) ) {
					if ( !accepted_poke_ob_gui ) {
						sim().addOutput(Actors.VIDEO_OPERATOR.name(), Outputs.OP_ACK);
						nextState(States.RX_VO,1);
						accepted_poke_ob_gui = true;
					} else {
						sim().addOutput(Actors.VIDEO_OPERATOR.name(), Outputs.OP_BUSY);
					}
				}
				
				//Third act on our task list
				if ( !accepted_poke_ob_gui ) {
					doNextTask();
				}
				break;
				
			case POKE_GUI:
				
				//Check the GUI output to make sure it is accessible
				_gui_observations = sim().getObservations(Actors.OPERATOR_GUI.name());
				/**
				 * Added Assumption that the GUI is always working.  If it is not then things will fail fast.
				 * We can add this later if wanted.
				 */
				nextState(States.TX_GUI, 1);
				break;
				
			case TX_GUI:
				
				_gui_observations = sim().getObservations(Actors.OPERATOR_GUI.name());
				parseUAVStateFromGUI(_gui_observations);
				
				/**
				 * Assumption is that the Operator is busy while performing tasks on the GUI.
				 */
				//If the GUI has an alarm while working on a task then abandon that task and immediately create a task for the alarm.
				if ( _gui_alert ) {
					
					if ( _uav_hag == UAVHeightAboveGround.Outputs.HAG_LOW ) {
						tasks.add(Outputs.OP_MODIFY_FLIGHT_PLAN);
					}
					if ( _uav_battery == UAVBattery.Outputs.BATTERY_LOW ) {
						tasks.add(Outputs.OP_LAND);
					}
					if ( _uav_signal == UAVSignal.Outputs.SIGNAL_LOST ) {
						tasks.add(Outputs.OP_MODIFY_FLIGHT_PLAN);
					}
					doNextTask();
				}
				break;
				
			case END_GUI:
				
				//Do nothing here, we want to observe the GUI in a few time steps to make sure our changes fixed things
				break;
				
			case FLYBY_GUI:
				
				//During FLYBY mode the operator is doing a lot of control of the UAV trying to get it to locate the anomaly.
				//Flyby mode will continue until the Video Operator reports that the anomaly has been verified.
				//The operator is using the GUI so he can update what is going on.  There should not be HAG issues in this mode
				//but battery is still an issue.
				_gui_observations = sim().getObservations(Actors.OPERATOR_GUI.name());
				parseUAVStateFromGUI(_gui_observations);
				
				//First listen for input from the VO
				if ( input.contains(VideoOperatorRole.Outputs.VO_POKE) ) {
					sim().addOutput(Actors.VIDEO_OPERATOR.name(), Outputs.OP_ACK);
					nextState(States.RX_VO,1);
				}
				
				else if ( _gui_observations.contains(OperatorGUIRole.Outputs.OGUI_STATE_ALARM) ) {
					if ( _uav_battery == UAVBattery.Outputs.BATTERY_LOW ) {
						tasks.add(Outputs.OP_LAND);
						doNextTask();
					}
					
					/**
					 * Assumptions: UAV will not lose signal nor experience HAG during FLYBY
					 */
					//Handle the results from the flyby
					if ( _gui_observations.contains(VideoOperatorRole.Outputs.VO_FLYBY_END_FAILED) ) {
						tasks.add(Outputs.OP_FLYBY_END);
					} else if ( _gui_observations.contains(VideoOperatorRole.Outputs.VO_FLYBY_END_SUCCESS) ) {
						tasks.add(Outputs.OP_FLYBY_END);
					}
				}
				break;
				
			case POST_FLIGHT:
				
				//Operator is moving the UAV into a Ready state and is busy
				break;
				
			case POST_FLIGHT_COMPLETE:
				
				//Immediately perform next task instead of going idle first
				doNextTask();
				break;

			default:
				//Do nothing for states not mentioned
				break;
		}
		_gui_alert = false;
	}

	
	/**
	 * Private HELPER METHODS
	 */
	
	private void doNextTask()
	{
		//Third Act on tasks
		if ( !tasks.isEmpty() ) {
			Outputs task = (Outputs) tasks.peek();
			switch(task) {
				case OP_FLYBY_START_F:
				case OP_FLYBY_START_T:
					//TODO Make sure this launches the UAV if it is on the ground
					if ( _uav_state == UAVRole.Outputs.UAV_FLYING_NORMAL
							|| _uav_state == UAVRole.Outputs.UAV_FLYING_FLYBY
							|| _uav_state == UAVRole.Outputs.UAV_LOITERING ) {
						nextState(States.FLYBY_GUI, 1);
					} else {
						assert false : "Attempting to do a FLYBY while UAV is not flying";
					}
					break;
				case OP_NEW_FLIGHT_PLAN:
				case OP_LAND:
				case OP_LOITER:
				case OP_RESUME:
					if ( _uav_state == UAVRole.Outputs.UAV_FLYING_NORMAL
							|| _uav_state == UAVRole.Outputs.UAV_FLYING_FLYBY
							|| _uav_state == UAVRole.Outputs.UAV_LOITERING ) {
						nextState(States.POKE_GUI, 1);
					} else {
						assert false : "Attempting to resume while UAV is not flying";
					}
					break;
				case OP_TAKE_OFF:
					assert _uav_state == UAVRole.Outputs.UAV_READY : "UAV was not in the READY state when it took off";
					nextState(States.LAUNCH_UAV, 1);
					break;
				case OP_SEARCH_AOI_COMPLETE:
					if ( _uav_state == UAVRole.Outputs.UAV_FLYING_NORMAL
							|| _uav_state == UAVRole.Outputs.UAV_FLYING_FLYBY
							|| _uav_state == UAVRole.Outputs.UAV_LOITERING ) {
						nextState(States.POKE_MM, 1);
					} else {
						assert false : "Attempting to complete search, but UAV wasn't flying";
					}
					break;
//				case OP_SEARCH_AOI_FAILED:
				case OP_POST_FLIGHT:
					//TODO Make sure this never happens if the search is complete
					assert _uav_state == UAVRole.Outputs.UAV_LANDED : "UAV wasn't landed when post flight was performed";
					nextState(States.POST_FLIGHT, 1);
					break;
				default:
					//Do whatever the states decides
					break;
			}
			
		}//end if
	}
	
	/**
	 * Extract state data by observing the UAV.  This model only allows the operator to get state data about the UAV
	 * and its HAG.
	 * @param observations
	 */
	private void parseUAVStateFromUAV(ArrayList<IData> observations)
	{
		for( IData data : observations ) {
			if ( data instanceof UAVRole.Outputs ) {
				_uav_state = (WiSAR.Agents.UAVRole.Outputs) data;
			} else if ( data instanceof UAVHeightAboveGround.Outputs ) {
				_uav_hag = (WiSAR.submodule.UAVHeightAboveGround.Outputs) data;
			}
		}
	}
	
	private void parseUAVStateFromGUI(ArrayList<IData> observations) {
		//TODO Make sure the operator gui is sending the UAV output
		for( IData data : observations ) {
			if ( data instanceof UAVRole.Outputs ) {
				_uav_state = (WiSAR.Agents.UAVRole.Outputs) data;
			} else if ( data instanceof UAVSignal.Outputs ) {
				_uav_signal = (WiSAR.submodule.UAVSignal.Outputs) data;
			} else if ( data instanceof UAVBattery.Outputs ) {
				_uav_battery = (WiSAR.submodule.UAVBattery.Outputs) data;
			} else if (data instanceof UAVFlightPlan.Outputs ) {
				if( data == UAVFlightPlan.Outputs.UAV_FLIGHT_PLAN_NO || 
						data == UAVFlightPlan.Outputs.UAV_FLIGHT_PLAN_YES ) {
					_uav_flight_plan = (WiSAR.submodule.UAVFlightPlan.Outputs) data;
				}
			} else if ( data instanceof UAVHeightAboveGround.Outputs ) {
				_uav_hag = (WiSAR.submodule.UAVHeightAboveGround.Outputs) data;
			} else if( data instanceof OperatorGUIRole.Outputs ) {
				if(_gui_state != OperatorGUIRole.Outputs.OGUI_STATE_ALARM && data == OperatorGUIRole.Outputs.OGUI_STATE_ALARM){
					_gui_alert = true;
				}
				_gui_state = (WiSAR.Agents.OperatorGUIRole.Outputs) data;
			}
		}
	}
}
