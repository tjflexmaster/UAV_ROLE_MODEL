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

import java.util.PriorityQueue;

/**
 *
 * @author Jared Moore
 * @author Robert Ivier
 */
public class VideoOperatorRole extends Actor
{
	ArrayList<IData> memory = new ArrayList<IData>();
	PriorityQueue<IData> tasks;
	
	public enum Outputs implements IData
	{
		VO_POKE,
		VO_END,
		VO_ACK,
		VO_BUSY, 
		
		/**
		 * For the GUI
		 */
		VO_GET_DATA,
		VO_CLICK_FRAME,
		VO_END_FEED,
		VO_START_FEED,
		VO_POSSIBLE_ANOMALY_DETECTED_T, 
		VO_POSSIBLE_ANOMALY_DETECTED_F, 
		VO_LIKELY_ANOMALY_DETECTED_T,
		VO_LIKELY_ANOMALY_DETECTED_F,
		VO_FLYBY_END,
		
		/**
		 * For the OPERATOR
		 */
		VO_BAD_STREAM,
		VO_STREAM_ENDED,
		
		/**
		 * For the MM
		 */
		VO_TARGET_SIGHTING_T,
		VO_TARGET_SIGHTING_F
		
		
	} /**
	     * Define Role States
	     * @author TJ-ASUS
	     *
	     */


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
        //Now determine what our next state will be
        IData output;
        //TODO handle outputing BUSY_VO
        switch((States)nextState()) {
	        //wait the specified time to receive an acknowledgment from the receiver,
	        //if no acknowledgment is received then return to observing.
	        case POKE_GUI:
	        	sim().addOutput(Actors.VIDEO_OPERATOR_GUI.name(), Outputs.VO_POKE);
	        	nextState(States.OBSERVING_NORMAL,sim().duration(Durations.VO_POKE_VGUI_DUR.range()));
	        	break;
	        case POKE_MM:
	        	sim().addOutput(Actors.MISSION_MANAGER.name(), Outputs.VO_POKE);
	        	nextState(States.OBSERVING_NORMAL,sim().duration(Durations.VO_POKE_MM_DUR.range()));
	        	break;
	        case POKE_OPERATOR:
	        	sim().addOutput(Actors.OPERATOR.name(), Outputs.VO_POKE);
	        	nextState(States.OBSERVING_NORMAL,sim().duration(Durations.VO_POKE_OPERATOR_DUR.range()));
	        	break;
	        //Transmission states : wait the time for transmission then go to the end transmission state.
	        case TX_MM:
	        	//TODO transmit different durations based on output
	        	nextState(States.END_MM, sim().duration(Durations.VO_TX_MM_DUR.range()));
	        	break; 	
	        case TX_OPERATOR:
	        	int duration = 1;
	        	if(tasks.peek() == Outputs.VO_BAD_STREAM)
	        		duration = sim().duration(Durations.VO_TX_OPERATOR_BAD_STREAM.range());
	        	else if(tasks.peek() == Outputs.VO_STREAM_ENDED)
	        		duration = sim().duration(Durations.VO_TX_OPERATOR_STREAM_ENDED.range());
	        	else{
	        		//TODO handle more outputs
	        	}
	        	nextState(States.END_OPERATOR, duration);
	        	break;
	        case TX_GUI:
	        	//TODO transmit different durations based on output
	        	nextState(States.END_GUI, sim().duration(Durations.VO_TX_VGUI_DUR.range()));
	        	break;
	        //For all the end transmission states update the inputs to the receivers then return to observation.
	        case END_GUI:
	        	output = tasks.poll();
	        	if(output == Outputs.VO_CLICK_FRAME)
	        		sim().addOutput(Actors.VIDEO_OPERATOR_GUI.name(), output);
	        	else if(output == Outputs.VO_START_FEED)
	        		sim().addOutput(Actors.VIDEO_OPERATOR_GUI.name(), output);
	        	else if(output == Outputs.VO_END_FEED)
	        		sim().addOutput(Actors.VIDEO_OPERATOR_GUI.name(), output);
	        	else{
	        		//TODO handle different outputs
	        	}
	        	sim().addOutput(Actors.VIDEO_OPERATOR_GUI.name(), Outputs.VO_END);
	        	nextState(States.OBSERVING_NORMAL,1);
	        	break;
	        case END_MM:
	        	output = tasks.poll();
	        	sim().addOutput(Actors.MISSION_MANAGER.name(), Outputs.VO_END);
	        	if(output != null)
	        		sim().addOutput(Actors.MISSION_MANAGER.name(), output);
	        	sim().addOutput(Actors.MISSION_MANAGER.name(), Outputs.VO_END);
	        	nextState(States.OBSERVING_NORMAL,1);
	        	break;
	        case END_OPERATOR:
	        	output = tasks.poll();
	        	if(output == Outputs.VO_BAD_STREAM)
	        		sim().addOutput(Actors.OPERATOR.name(), output);
	        	else if(output == Outputs.VO_STREAM_ENDED)
	        		sim().addOutput(Actors.OPERATOR.name(), output);
	        	sim().addOutput(Actors.OPERATOR.name(), Outputs.VO_END);
	        	nextState(States.OBSERVING_NORMAL,1);
	        	break;
	        case IDLE:
	        	IData current_task = tasks.peek();
	        	if(current_task == null){
					nextState(null,0);
					break;
	        	}
	        	switch((Outputs) current_task){
		    		/**
		    		 * For the GUI
		    		 */
		        	case VO_GET_DATA:
		        	case VO_CLICK_FRAME:
		        	case VO_END_FEED:
		        	case VO_START_FEED:
		        		nextState(States.POKE_GUI,1);
		        		break;
		    		/**
		    		 * For the OPERATOR
		    		 */
		        	case VO_BAD_STREAM:
		        	case VO_STREAM_ENDED:
		        		nextState(States.POKE_OPERATOR,1);
		        		break;
					default:
						nextState(null,0);
						break;
		        	}
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
		ArrayList<IData> video_feed;
		
		switch( (States) state() ) {
			case IDLE:
				if (input.contains(MissionManagerRole.Outputs.MM_POKE)) {
					sim().addOutput(Actors.MISSION_MANAGER.name(), Outputs.VO_ACK);
					nextState(States.RX_MM,1);
				}
				break;
			case RX_MM:
				if(input.contains(MissionManagerRole.Outputs.MM_END))
				{
					if(input.contains(MissionManagerRole.Outputs.MM_TARGET_DESCRIPTION))
					{
						nextState(States.OBSERVING_NORMAL,1);
					}
					else if(input.contains(MissionManagerRole.Outputs.MM_TERMINATE_SEARCH))
					{
						nextState(States.IDLE,1);
					}
					else {
						nextState(States.OBSERVING_NORMAL,1);
					}
				}
				break;
			//Check the inputs from the GUI and if the MM has initiated a handshake.
			case OBSERVING_NORMAL:
				video_feed = sim().getObservations(Actors.VIDEO_OPERATOR_GUI.name());
				if (input.contains(MissionManagerRole.Outputs.MM_POKE))
				{
					sim().addOutput(Actors.MISSION_MANAGER.name(), Outputs.VO_ACK);
					nextState(States.RX_MM,1);
				}
				else if (video_feed.contains(VideoGUIRole.Outputs.VGUI_BAD_STREAM))
				{
					tasks.add(Outputs.VO_BAD_STREAM);
					nextState(States.OBSERVING_NORMAL,1);
				} 
				else if (video_feed.contains(VideoGUIRole.Outputs.VGUI_NO_STREAM))
				{
					tasks.add(Outputs.VO_STREAM_ENDED);
					nextState(States.OBSERVING_NORMAL,1);
				}
				else if (video_feed.contains(VideoGUIRole.Outputs.VGUI_TRUE_POSITIVE))
				{
					tasks.add(detectAnomaly(VideoGUIRole.Outputs.VGUI_TRUE_POSITIVE));
					nextState(States.OBSERVING_NORMAL,1);
				}
				else if (video_feed.contains(VideoGUIRole.Outputs.VGUI_FALSE_POSITIVE))
				{
					tasks.add(detectAnomaly(VideoGUIRole.Outputs.VGUI_FALSE_POSITIVE));
					nextState(States.OBSERVING_NORMAL,1);
				}
				break;
			case OBSERVING_FLYBY:
				break;
			case POKE_GUI:
				video_feed = sim().getObservations(Actors.VIDEO_OPERATOR_GUI.name());
				if(video_feed.contains(VideoGUIRole.Outputs.VGUI_ACK))
					nextState(States.TX_GUI,1);
				break;
			case POKE_MM:
				if(input.contains(MissionManagerRole.Outputs.MM_ACK))
					nextState(States.TX_MM,1);
				break;
			case TX_GUI:
				//don't allow inputs here
				break;
			case POKE_OPERATOR:
				if(input.contains(OperatorRole.Outputs.OP_ACK))
					nextState(States.TX_OPERATOR,1);
				break;
			default:
				break;
		}//end switch
	}

	private IData detectAnomaly(VideoGUIRole.Outputs anomaly) {
		//we need params to verify an anomaly, but a random number works for now
		int percent = (int) (Math.random() * 100);
		if (percent <= 50 && anomaly == VideoGUIRole.Outputs.VGUI_TRUE_POSITIVE) {
			sim().addOutput(Actors.VIDEO_OPERATOR_GUI.name(), Outputs.VO_POSSIBLE_ANOMALY_DETECTED_T);
		}
		else if (percent <= 50 && anomaly == VideoGUIRole.Outputs.VGUI_FALSE_POSITIVE) {
			sim().addOutput(Actors.VIDEO_OPERATOR_GUI.name(), Outputs.VO_POSSIBLE_ANOMALY_DETECTED_F);
		} else if (percent > 50 && anomaly == VideoGUIRole.Outputs.VGUI_TRUE_POSITIVE) {
			sim().addOutput(Actors.VIDEO_OPERATOR_GUI.name(), Outputs.VO_LIKELY_ANOMALY_DETECTED_T);
		} else if (percent > 50 && anomaly == VideoGUIRole.Outputs.VGUI_FALSE_POSITIVE) {
			sim().addOutput(Actors.VIDEO_OPERATOR_GUI.name(), Outputs.VO_LIKELY_ANOMALY_DETECTED_F);
		}
		return null;
	}

   
}
