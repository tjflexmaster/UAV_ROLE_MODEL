/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package WiSAR.Agents;

import java.util.ArrayList;

import CUAS.Simulator.Actor;
import CUAS.Simulator.IData;
import CUAS.Simulator.IStateEnum;
import CUAS.Simulator.Simulator;
import WiSAR.Actors;
import WiSAR.Durations;
import WiSAR.Agents.OperatorRole.Outputs;
import WiSAR.Agents.OperatorRole.States;

import java.util.PriorityQueue;

/**
 *
 * @author Jared Moore
 * @author Robert Ivier
 */
public class VideoOperatorRole extends Actor
{
//	ArrayList<IData> memory = new ArrayList<IData>();
	PriorityQueue<IData> tasks;
	
	//TODO Add UAVVideoFeed object
	
	boolean _search_active;
	
	public enum Outputs implements IData
	{
		VO_POKE,
		VO_END,
		VO_ACK,
		VO_BUSY, 
		
		/**
		 * For the GUI
		 */
		VO_POSSIBLE_ANOMALY_DETECTED_T, 
		VO_POSSIBLE_ANOMALY_DETECTED_F, 
		VO_LIKELY_ANOMALY_DETECTED_T,
		VO_LIKELY_ANOMALY_DETECTED_F,
		VO_FLYBY_END_T,
		VO_FLYBY_END_F,
		VO_FLYBY_REQ_T,
		VO_FLYBY_REQ_F,
		
		/**
		 * For the OPERATOR
		 */
		VO_BAD_STREAM,
		
		/**
		 * For the MM
		 */
		VO_TARGET_SIGHTING_T,
		VO_TARGET_SIGHTING_F
		
		
	} 


	public enum States implements IStateEnum
	{
	    IDLE,
        RX_MM,
		OBSERVING_NORMAL,
		OBSERVING_FLYBY,
		POKE_GUI,
	    TX_GUI,
	    END_GUI,
	    POKE_MM,
	    TX_MM,
	    END_MM, 
	    POKE_OPERATOR, 
	    TX_OPERATOR,
	    END_OPERATOR,
	}
	
	public VideoOperatorRole()
	{
		name( Actors.VIDEO_OPERATOR.name() );
		nextState(States.IDLE, 1);
		tasks = new PriorityQueue<IData>();
		_search_active = true;
	}
	
	/**
	 * update the current state
	 * activate moore outputs
	 * set the default next state
	 */
	@Override
	public void processNextState() {//Is our next state now?

        if ( nextStateTime() != Simulator.getInstance().getTime() ) {
            return;
        }

        //Update to the next state
        state(nextState());
        
        //TODO Send out busy signals
        switch((States)nextState()) {
	        //wait the specified time to receive an acknowledgment from the receiver,
	        //if no acknowledgment is received then return to observing.
	        case IDLE:
        		nextState(null,0);
        		break;
	        case POKE_MM:
	        	sim().addOutput(Actors.MISSION_MANAGER.name(), Outputs.VO_POKE);
	        	nextState(States.IDLE,sim().duration(Durations.VO_POKE_MM_DUR.range()));
	        	break;
	        case TX_MM:
	        	//TODO transmit different durations based on output
	        	nextState(States.END_MM, sim().duration(Durations.VO_TX_MM_DUR.range()));
	        	break; 	
	        case END_MM:
	        	sim().addOutput(Actors.MISSION_MANAGER.name(), Outputs.VO_END);
        		sim().addOutput(Actors.MISSION_MANAGER.name(), tasks.poll());
	        	nextState(States.IDLE,1);
	        	break;
	        case RX_MM:
	        	nextState(States.IDLE, sim().duration(Durations.VO_RX_MM_DUR.range()));
	        	break;
	        case POKE_OPERATOR:
	        	sim().addOutput(Actors.OPERATOR.name(), Outputs.VO_POKE);
	        	nextState(States.IDLE,sim().duration(Durations.VO_POKE_OPERATOR_DUR.range()));
	        	break;
	        case TX_OPERATOR:
	        	int duration = 1;
	        	if(tasks.peek() == Outputs.VO_BAD_STREAM)
	        		duration = sim().duration(Durations.VO_TX_OPERATOR_BAD_STREAM.range());
	        	nextState(States.END_OPERATOR, duration);
	        	break;
	        case END_OPERATOR:
	        	sim().addOutput(Actors.OPERATOR.name(), Outputs.VO_END);
	        	sim().addOutput(Actors.OPERATOR.name(), tasks.poll());
	        	nextState(States.IDLE,1);
	        	break;
	        case POKE_GUI:
	        	sim().addOutput(Actors.VIDEO_OPERATOR_GUI.name(), Outputs.VO_POKE);
	        	nextState(States.IDLE,sim().duration(Durations.VO_POKE_VGUI_DUR.range()));
	        	break;
	        case TX_GUI:
	        	//TODO transmit different durations based on output
	        	nextState(States.END_GUI, sim().duration(Durations.VO_TX_VGUI_DUR.range()));
	        	break;
	        case END_GUI:
	        	sim().addOutput(Actors.VIDEO_OPERATOR_GUI.name(), Outputs.VO_END);
	        	sim().addOutput(Actors.VIDEO_OPERATOR_GUI.name(), tasks.poll());
	        	nextState(States.IDLE,1);
	        	break;
	        case OBSERVING_NORMAL:
	        case OBSERVING_FLYBY:
	        default:
	        	nextState(null,0);
	        	break;
        }
	}

	/**
	 * Process the current state and all inputs to determine the next state, also activates mealy outputs.
	 */
	public void processInputs() {
		//Pull Input and any observations that need to be made from the simulator
		ArrayList<IData> input = sim().getInput(this.name());
		ArrayList<IData> video_feed, uav_observations;
		ArrayList<IData> mm_observations;
		
		switch( (States) state() ) {
			case IDLE:
				//Stay Idle if the search is no longer active
				if ( !_search_active ) {
					break;
				}
				boolean accept_poke = false;
				if (input.contains(MissionManagerRole.Outputs.MM_POKE)) {
					sim().addOutput(Actors.MISSION_MANAGER.name(), Outputs.VO_ACK);
					nextState(States.RX_MM,1);
					accept_poke = true;
				}
				
				if ( !accept_poke ) {
					//TODO Check to see if we need to observe the gui
					nextState(States.OBSERVING_NORMAL, 1);
					
					//If there are any tasks then do those
					doNextTask();
				}
				break;
			case POKE_MM:
				mm_observations = sim().getObservations(Actors.MISSION_MANAGER.name());
				
				//Always respond to an ACK_MM
				if (input.contains(MissionManagerRole.Outputs.MM_ACK)) {
					nextState(States.TX_MM, 1);
				} else if ( input.contains(MissionManagerRole.Outputs.MM_BUSY) || 
						mm_observations.contains(MissionManagerRole.Outputs.MM_BUSY)) {
					nextState(States.IDLE, 1);
				} else {
					//MM is more important so accept pokes from them
					if ( input.contains(MissionManagerRole.Outputs.MM_POKE) ) {
						sim().addOutput(Actors.MISSION_MANAGER.name(), Outputs.VO_ACK);
						nextState(States.RX_MM, 1);
					} 
				}
				break;
			case TX_MM:
				//No interruptions
				break;
			case END_MM:
				//Immediately perform next task instead of going idle first
				//TODO change the default nextState to be based on what the VO should be doing
				doNextTask();
				break;
			case RX_MM:
				if(input.contains(MissionManagerRole.Outputs.MM_END))
				{
					if(input.contains(MissionManagerRole.Outputs.MM_TARGET_DESCRIPTION))
					{
						nextState(States.IDLE,1);
					}
					else if(input.contains(MissionManagerRole.Outputs.MM_TERMINATE_SEARCH))
					{
						_search_active = false;
						nextState(States.IDLE,1);
					}
					else {
						nextState(States.IDLE,1);
					}
				}
				break;
			case POKE_OPERATOR:
				if(input.contains(OperatorRole.Outputs.OP_ACK))
					nextState(States.TX_OPERATOR,1);
				break;
			case TX_OPERATOR:
				//No interrupts
				break;
			case END_OPERATOR:
				//Immediately perform next task instead of going idle first
				//TODO change the default nextState to be based on what the VO should be doing
				doNextTask();
				break;
			case POKE_GUI:
				video_feed = sim().getObservations(Actors.VIDEO_OPERATOR_GUI.name());
				
				/**
				 * Assumption that the gui is always working.  If it isn't then everything dies which isn't that interesting.
				 */
				nextState(States.TX_GUI,1);
				break;
			case TX_GUI:
				//don't allow inputs here
				break;
			case END_GUI:
				//Immediately perform next task instead of going idle first
				//TODO change the default nextState to be based on what the VO should be doing
				doNextTask();
				break;
			case OBSERVING_NORMAL:
				video_feed = sim().getObservations(Actors.VIDEO_OPERATOR_GUI.name());
				
				if (input.contains(MissionManagerRole.Outputs.MM_POKE)) {
					sim().addOutput(Actors.MISSION_MANAGER.name(), Outputs.VO_ACK);
					nextState(States.RX_MM,1);
					break;
				} 
				
				//TODO Look for the UAVVideoFeed output
				if ( video_feed.contains() ) {
					//Bad stream means we cannot see the anomalies
					tasks.add(Outputs.VO_BAD_STREAM);
				} else {
					//TODO randomly pick 2 out of all visible anomalies
					if ( video_feed.contains(VideoGUIRole.Outputs.VGUI_FALSE_POSITIVE) ) {
						switch( analyzeAnomaly(false) ) {
							case 0:
								tasks.add(Outputs.VO_LIKELY_ANOMALY_DETECTED_F);
							case 1:
								tasks.add(Outputs.VO_POSSIBLE_ANOMALY_DETECTED_F);
							default:
								//Do nothing
								break;
						}
					}
					//TODO randomly pick 2 out of all visible anomalies
					if ( video_feed.contains(VideoGUIRole.Outputs.VGUI_TRUE_POSITIVE) ) {
						switch( analyzeAnomaly(true) ) {
							case 0:
								tasks.add(Outputs.VO_LIKELY_ANOMALY_DETECTED_T);
							case 1:
								tasks.add(Outputs.VO_POSSIBLE_ANOMALY_DETECTED_T);
							default:
								//Do nothing
								break;
						}
					}
				}
				
				//TODO Check to see if we need to observe the gui as the default
				nextState(States.OBSERVING_NORMAL, 1);
				
				//If there are any tasks then do those
				doNextTask();
				break;
			case OBSERVING_FLYBY:
				video_feed = sim().getObservations(Actors.VIDEO_OPERATOR_GUI.name());
				uav_observations = sim().getInput(Actors.UAV.name());
				
				if (input.contains(MissionManagerRole.Outputs.MM_POKE)) {
					sim().addOutput(Actors.MISSION_MANAGER.name(), Outputs.VO_ACK);
					nextState(States.RX_MM,1);
					break;
				} 
				
				//TODO Look for the UAVVideoFeed output
				if ( video_feed.contains() ) {
					tasks.add(Outputs.VO_BAD_STREAM);
				} else {
					//Look for the flyby anomaly, once we see it then we make a decision on if it is real or not
					//TODO Look at the video feed for the flyby anomaly
					if ( uav_observations.contains(o))
						if ( isTarget(true_positive) ) {
							//TODO send that the target is spotted
						}
					
				}
				
				//TODO Check to see if we need to observe the gui as the default
				nextState(States.OBSERVING_NORMAL, 1);
				
				//If there are any tasks then do those
				doNextTask();
				break;
			default:
				break;
		}//end switch
	}
	
	/**
	 * PRIVATE HELPER METHODS
	 */
	private int analyzeAnomaly(boolean true_positive)
	{
		
		int likely, possible;
		if ( true_positive ) {
			likely = sim().duration(Durations.VO_DETECT_LIKELY_TP.range());
			possible = sim().duration(Durations.VO_DETECT_POSSIBLE_TP.range());
		} else {
			likely = sim().duration(Durations.VO_DETECT_LIKELY_FP.range());
			possible = sim().duration(Durations.VO_DETECT_POSSIBLE_FP.range());
		}
		
		int percent = (int) (Math.random() * 100);
		if ( percent <= likely ) {
				return 0;
		}
		percent = (int) (Math.random() * 100);
		if ( percent <= possible ) {
			return 1;
		}
		
		return 2;
	}
	
	private boolean isTarget(boolean true_positive)
	{
		int believe_anomaly;
		if ( true_positive ) {
			believe_anomaly = sim().duration(Durations.VO_BELIEVE_FLYBY_TP.range());
		} else {
			believe_anomaly = sim().duration(Durations.VO_BELIEVE_FLYBY_FP.range());
		}
		
		int percent = (int) (Math.random() * 100);
		if ( percent <= believe_anomaly ) {
			return true;
		} else {
			return false;
		}
		
	}
	
	/**
	 * Contains logic for determining the next task to complete
	 */
	private void doNextTask()
	{
		//Third Act on tasks
		if ( !tasks.isEmpty() ) {
			Outputs task = (Outputs) tasks.peek();
			switch(task) {
				case VO_FLYBY_REQ_F:
				case VO_FLYBY_REQ_T:
				case VO_FLYBY_END_T:
				case VO_FLYBY_END_F:
				case VO_POSSIBLE_ANOMALY_DETECTED_T:
				case VO_POSSIBLE_ANOMALY_DETECTED_F:
				case VO_LIKELY_ANOMALY_DETECTED_T:
				case VO_LIKELY_ANOMALY_DETECTED_F:
					nextState(States.POKE_GUI, 1);
					break;
				case VO_BAD_STREAM:
					nextState(States.POKE_OPERATOR, 1);
					break;
				case VO_TARGET_SIGHTING_T:
				case VO_TARGET_SIGHTING_F:
					nextState(States.POKE_MM, 1);
					break;
				default:
					//Use the existing next state
					break;
			}
			
		}//end if
	}
}
