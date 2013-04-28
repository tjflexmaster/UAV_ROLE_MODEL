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
import WiSAR.Durations;

/**
 *
 * @author Jared Moore
 * @author Robert Ivier
 */
public class VideoOperatorRole extends Actor
{
	ArrayList<IData> memory = new ArrayList<IData>();
	   
	public enum Outputs implements IData
	{
		POKE_VO,
		END_VO,
		ACK_VO,
		BUSY_VO, 
		/**
		 * For the GUI
		 */
		GET_DATA_VO,
		CLICK_FRAME_VO,
		END_FEED_VO,
		START_FEED_VO,
		
		/**
		 * For the OPERATOR
		 */
		LOOK_CLOSER_VO,
		BAD_STREAM_VO,
		STREAM_ENDED_VO,
		
		/**
		 * For the MM
		 */
		FOUND_ANOMALY_VO, 
		
		
	} /**
	     * Define Role States
	     * @author TJ-ASUS
	     *
	     */


	public enum States implements IStateEnum
	{
	    IDLE,
	    ACK_MM,
        RX_MM,
		OBSERVING,					//1 idle state, analyst is observing the feed, listening for instructions, and monitoring physical world
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
		name( Roles.VIDEO_OPERATOR.name() );
		nextState(States.IDLE, 1);
		
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

        //Each state has a designated duration

        //If a state isn't included then it doesn't deviate from the default
        //ICommunicate role; 
        //TODO handle outputing BUSY_VO
        switch((States)nextState()) {
	        case ACK_MM:
	        	//simulator().addOutput(Roles.MISSION_MANAGER.name(), MissionManagerRole.Inputs.ACK_VO);
	        	nextState(States.RX_MM,1);
	        	break;
	        case RX_MM:
	        	nextState(States.IDLE,sim().duration(Durations.VO_RX_MM_DUR.range()));
	        	break;
	        //wait the specified time to receive an acknowledgment from the receiver,
	        // if no acknowledgment is received then return to observing.
	        case POKE_GUI:
	        	sim().addOutput(Roles.VIDEO_OPERATOR_GUI.name(), Outputs.POKE_VO);
	        	nextState(States.OBSERVING,sim().duration(Durations.VO_POKE_VGUI_DUR.range()));
	        	break;
	        case POKE_MM:
	        	sim().addOutput(Roles.MISSION_MANAGER.name(), Outputs.POKE_VO);
	        	nextState(States.OBSERVING,sim().duration(Durations.VO_POKE_MM_DUR.range()));
	        	break;
	        case POKE_OPERATOR:
	        	sim().addOutput(Roles.OPERATOR.name(), Outputs.POKE_VO);
	        	nextState(States.OBSERVING,sim().duration(Durations.VO_POKE_OPERATOR_DUR.range()));
	        	break;
	        //Transmission states : wait the time for transmission then go to the end transmission state. 	
	        case TX_GUI:
	        	//TODO transmit different durations based on output
	        	nextState(States.END_GUI, sim().duration(Durations.VO_TX_VGUI_DUR.range()));
	        	break;
	        case TX_MM:
	        	//TODO transmit different durations based on output
	        	nextState(States.END_MM, sim().duration(Durations.VO_TX_MM_DUR.range()));
	        	break;
	        case TX_OPERATOR:
	        	int duration = 1;
	        	if(memory.contains(Outputs.BAD_STREAM_VO))
	        		duration = sim().duration(Durations.VO_TX_OPERATOR_BAD_STREAM.range());
	        	else if(memory.contains(Outputs.STREAM_ENDED_VO))
	        		duration = sim().duration(Durations.VO_TX_OPERATOR_STREAM_ENDED.range());
	        	else if(memory.contains(Outputs.LOOK_CLOSER_VO))
	        		duration = sim().duration(Durations.VO_TX_OPERATOR_LOOK_CLOSER.range());
	        	else{
	        		//TODO handle more outputs
	        	}
	        	nextState(States.END_OPERATOR, duration);
	        	break;
	        //For all the end transmission states update the inputs to the receivers then return to observation.
	        case END_GUI:
	        	if(memory.contains(Outputs.CLICK_FRAME_VO))
	        		sim().addOutput(Roles.VIDEO_OPERATOR_GUI.name(), Outputs.CLICK_FRAME_VO);
	        	else if(memory.contains(Outputs.START_FEED_VO))
	        		sim().addOutput(Roles.VIDEO_OPERATOR_GUI.name(), Outputs.START_FEED_VO);
	        	else if(memory.contains(Outputs.END_FEED_VO))
	        		sim().addOutput(Roles.VIDEO_OPERATOR_GUI.name(), Outputs.END_FEED_VO);
	        	else{
	        		//TODO handle different outputs
	        	}
	        	nextState(States.OBSERVING,1);
	        	break;
	        case END_MM:
	        	sim().addOutput(Roles.MISSION_MANAGER.name(), Outputs.END_VO);
	        	if(memory.contains(Outputs.FOUND_ANOMALY_VO))
	        		sim().addOutput(Roles.MISSION_MANAGER.name(), Outputs.FOUND_ANOMALY_VO);
	        	nextState(States.OBSERVING,1);
	        	break;
	        case END_OPERATOR:
	        	//TODO implement comm with OPERATOR
	        	/**
	        	if(memory.contains(Outputs.BAD_STREAM))
	        		simulator().addOutput(Roles.OPERATOR.name(), OPERATORRole.Inputs.BAD_STREAM);
	        	else if(memory.contains(Outputs.STREAM_ENDED))
	        		simulator().addOutput(Roles.OPERATOR.name(), OPERATORRole.Inputs.STREAM_ENDED);
	        	else if(memory.contains(Outputs.LOOK_CLOSER))
	        		simulator().addOutput(Roles.OPERATOR.name(), OPERATORRole.Inputs.LOOK_CLOSER);
	        		*/
	        	nextState(States.OBSERVING,1);
	        	break;
	        default:
	        	nextState(null,0);
	        	break;
        }
	}

	/**
	 * Process the current state and all inputs to determine the next state, also activates mealy outputs.
	 */
	public void processInputs() {
		ArrayList<IData> video_feed;
		switch( (States) state() ) {
			case IDLE:
				if(_input.contains(Outputs.POKE_VO))
					nextState(States.ACK_MM,1);//we need to code state ack_mm
				break;
			case RX_MM:
				if(_input.contains(MissionManagerRole.Outputs.END_MM))
				{
					if(_input.contains(MissionManagerRole.Outputs.SEARCH_AOI_MM))
					{
						memory.add(Outputs.START_FEED_VO);
						nextState(States.POKE_GUI,1);
					}
					else if(_input.contains(MissionManagerRole.Outputs.SEARCH_TERMINATED_MM))
					{
						memory.add(Outputs.END_FEED_VO);
						nextState(States.POKE_GUI,1);
					}
					else
						nextState(States.IDLE,1);
				}
				break;
		//Check the inputs from the GUI and if the MM has initiated a handshake.
			case OBSERVING:
				video_feed = sim().getObservations(Roles.VIDEO_OPERATOR_GUI.name());
				if(video_feed.contains(VideoGUIRole.Outputs.BAD_STREAM))
				{
					memory.add(Outputs.BAD_STREAM_VO);
					nextState(States.POKE_OPERATOR, 1);
				} 
				else if (video_feed.contains(VideoGUIRole.Outputs.BAD_STREAM))
				{
					memory.add(Outputs.STREAM_ENDED_VO);
					nextState(States.POKE_OPERATOR, 1);
				}
				else if (_input.contains(MissionManagerRole.Outputs.ACK_MM))
				{
					nextState(States.ACK_MM,1);
				}
				else if(video_feed.contains(VideoGUIRole.Outputs.TRUE_POSITIVE))
				{
					memory.add(Outputs.FOUND_ANOMALY_VO);
					nextState(States.POKE_GUI,1);
				}
				else if(video_feed.contains(VideoGUIRole.Outputs.FALSE_POSITIVE))
				{
					memory.add(Outputs.FOUND_ANOMALY_VO);
					nextState(States.POKE_GUI,1);
				}
				break;
			case POKE_GUI:
				video_feed = sim().getObservations(Roles.VIDEO_OPERATOR_GUI.name());
				if(video_feed.contains(VideoGUIRole.Outputs.ACK_VGUI))
					nextState(States.TX_GUI,1);
				break;
			case POKE_MM:
				if(_input.contains(MissionManagerRole.Outputs.ACK_MM))
					nextState(States.TX_MM,1);
				break;
			case POKE_OPERATOR:
				if(_input.contains(OperatorRole.Outputs.OP_ACK))
					nextState(States.TX_OPERATOR,1);
				break;
			default:
				break;
		}
		_input.clear();
	}

   
}