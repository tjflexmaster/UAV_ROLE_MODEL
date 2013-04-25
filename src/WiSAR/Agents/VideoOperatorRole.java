/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package WiSAR.Roles;

import java.util.ArrayList;

import NewModel.Events.IEvent;
import NewModel.Roles.Role;
import NewModel.Simulation.ICommunicate;
import NewModel.Simulation.IInputEnum;
import NewModel.Simulation.IOutputEnum;
import NewModel.Simulation.IStateEnum;
import NewModel.Simulation.Simulator;
import WiSAR.Durations;

/**
 *
 * @author Jared Moore
 * @author Robert Ivier
 */
public class VideoOperatorRole extends Role
{
	Outputs current_output = null;
	
	public enum Inputs implements IInputEnum
	{
		/**
		 * MM Inputs
		 */
        TERMINATE_SEARCH,
        SEARCH_AOI,
        POKE_MM,
        ACK_MM,
        BUSY_MM,
        END_MM,
	        
		/**
		 * GUI Inputs
		 */
		STREAM_ENDED,
		GOOD_STREAM,
		BAD_STREAM,
		FALSE_POSITIVE,
		TRUE_POSITIVE,
		VGUI_ACK,
		
		/**
		 * OPERATOR Inputs
		 */
		OPERATOR_ACK
		
	}

	  

	   
	public enum Outputs implements IOutputEnum
	{
		/**
		 * For the GUI
		 */
		GET_DATA,
		CLICK_FRAME,
		END_FEED,
		START_FEED,
		
		/**
		 * For the OPERATOR
		 */
		LOOK_CLOSER,
		BAD_STREAM,
		STREAM_ENDED,
		
		/**
		 * For the MM
		 */
		FOUND_ANOMALY,
		
		
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
	public boolean processNextState() {//Is our next state now?

        if ( nextStateTime() != Simulator.getInstance().getTime() ) {
            return false;
        }

        //Update to the next state
        state(nextState());

        //Now determine what our next state will be

        //Each state has a designated duration

        //If a state isn't included then it doesn't deviate from the default
        ICommunicate role; 
        switch((States)nextState()) {
	        case ACK_MM:
	        	simulator().addInput(Roles.MISSION_MANAGER.name(), MissionManagerRole.Inputs.ACK_VO);
	        	nextState(States.RX_MM,1);
	        	break;
	        case RX_MM:
	        	nextState(States.IDLE,simulator().duration(Durations.VO_RX_MM_DUR.range()));
	        	break;
	        //wait the specified time to receive an acknowledgment from the receiver,
	        // if no acknowledgment is received then return to observing.
	        case POKE_GUI:
	        	simulator().addInput(Roles.VIDEO_OPERATOR_GUI.name(), VideoGUIRole.Inputs.VO_POKE);
	        	nextState(States.OBSERVING,simulator().duration(Durations.VO_POKE_VGUI_DUR.range()));
	        	break;
	        case POKE_MM:
	        	nextState(States.OBSERVING,simulator().duration(Durations.VO_POKE_MM_DUR.range()));
	        	break;
	        case POKE_OPERATOR:
	        	nextState(States.OBSERVING,simulator().duration(Durations.VO_POKE_OPERATOR_DUR.range()));
	        	break;
	        //Transmission states : wait the time for transmission then go to the end transmission state. 	
	        case TX_GUI:
	        	nextState(States.END_GUI, simulator().duration(Durations.VO_TX_VGUI_DUR.range()));
	        	break;
	        case TX_MM:
	        	nextState(States.END_MM, simulator().duration(Durations.VO_TX_MM_DUR.range()));
	        	break;
	        case TX_OPERATOR:
	        	int duration = 1;
	        	if(_output.contains(Outputs.BAD_STREAM))
	        		duration = simulator().duration(Durations.VO_TX_OPERATOR_BAD_STREAM.range());
	        	else if(_output.contains(Outputs.STREAM_ENDED))
	        		duration = simulator().duration(Durations.VO_TX_OPERATOR_STREAM_ENDED.range());
	        	else if(_output.contains(Outputs.LOOK_CLOSER))
	        		duration = simulator().duration(Durations.VO_TX_OPERATOR_LOOK_CLOSER.range());
	        	nextState(States.END_OPERATOR, duration);
	        	break;
	        //For all the end transmission states update the inputs to the receivers then return to observation.
	        case END_GUI:
	        	if(_output.contains(Outputs.CLICK_FRAME))
	        		simulator().addInput(Roles.VIDEO_OPERATOR_GUI.name(), VideoGUIRole.Inputs.VO_POKE);
	        	else if(_output.contains(Outputs.START_FEED))
	        		simulator().addInput(Roles.VIDEO_OPERATOR_GUI.name(), VideoGUIRole.Inputs.START);
	        	else if(_output.contains(Outputs.END_FEED))
	        		simulator().addInput(Roles.VIDEO_OPERATOR_GUI.name(), VideoGUIRole.Inputs.TERMINATE);
	        	nextState(States.OBSERVING,1);
	        	break;
	        case END_MM:
	        	if(_output.contains(Outputs.FOUND_ANOMALY))
	        		simulator().addInput(Roles.MISSION_MANAGER.name(), MissionManagerRole.Inputs.SEARCH_AOI_SIGHTING);
	        	nextState(States.OBSERVING,1);
	        	break;
	        case END_OPERATOR:
	        //OPERATOR hasn't been implemented yet.
	        	/**
	        	if(_output.contains(Outputs.BAD_STREAM))
	        		simulator().addInput(Roles.OPERATOR.name(), OPERATORRole.Inputs.BAD_STREAM);
	        	else if(_output.contains(Outputs.STREAM_ENDED))
	        		simulator().addInput(Roles.OPERATOR.name(), OPERATORRole.Inputs.STREAM_ENDED);
	        	else if(_output.contains(Outputs.LOOK_CLOSER))
	        		simulator().addInput(Roles.OPERATOR.name(), OPERATORRole.Inputs.LOOK_CLOSER);
	        		*/
	        	nextState(States.OBSERVING,1);
	        	break;
	        default:
	        	nextState(null,0);
	        	break;
        }
        return true;
	}

	/**
	 * Process the current state and all inputs to determine the next state, also activates mealy outputs.
	 */
	@Override
	public void updateState() {
		ArrayList<IOutputEnum> video_feed;
		switch( (States) state() ) {
			case IDLE:
				if(_input.contains(Inputs.POKE_MM))
					nextState(States.ACK_MM,1);//we need to code state ack_mm
				break;
			case RX_MM:
				if(_input.contains(Inputs.END_MM))
				{
					if(_input.contains(Inputs.SEARCH_AOI))
					{
						_output.add(Outputs.START_FEED);
						nextState(States.POKE_GUI,1);
					}
					else if(_input.contains(Inputs.TERMINATE_SEARCH))
					{
						_output.add(Outputs.END_FEED);
						nextState(States.POKE_GUI,1);
					}
					else
						nextState(States.IDLE,1);
				}
				break;
		//Check the inputs from the GUI and if the MM has initiated a handshake.
			case OBSERVING:
				video_feed = simulator().getRole(Roles.VIDEO_OPERATOR_GUI.name()).getOutput();
				if(video_feed.contains(VideoGUIRole.Outputs.BAD_STREAM))
				{
					_output.add(Outputs.BAD_STREAM);
					nextState(States.POKE_OPERATOR, 1);
				} 
				else if (video_feed.contains(VideoGUIRole.Outputs.BAD_STREAM))
				{
					_output.add(Outputs.STREAM_ENDED);
					nextState(States.POKE_OPERATOR, 1);
				}
				else if (_input.contains(Inputs.ACK_MM))
				{
					nextState(States.ACK_MM,1);
				}
				else if(video_feed.contains(VideoGUIRole.Outputs.TRUE_POSITIVE))
				{
					_output.add(Outputs.FOUND_ANOMALY);
					nextState(States.POKE_GUI,1);
				}
				else if(video_feed.contains(VideoGUIRole.Outputs.FALSE_POSITIVE))
				{
					_output.add(Outputs.FOUND_ANOMALY);
					nextState(States.POKE_GUI,1);
				}
				break;
			case POKE_GUI:
				video_feed = simulator().getRole(Roles.VIDEO_OPERATOR_GUI.name()).getOutput();
				if(video_feed.contains(VideoGUIRole.Outputs.ACK_VO))
					nextState(States.TX_GUI,1);//we need to code state tx_gui
				break;
			case POKE_MM:
				if(_input.contains(Inputs.ACK_MM))
					nextState(States.TX_MM,1);//we need to code state tx_mm
				break;
			case POKE_OPERATOR:
				if(_input.contains(Inputs.OPERATOR_ACK))
					nextState(States.TX_OPERATOR,1);//we need to code state tx_OPERATOR
				break;
			default:
				break;
		}
		_input.clear();
	}

	/**
	 * Will implement events associated with hunger, fatigue, boredom, and other things representing the human error.
	 */
	@Override
	public void processEvent(IEvent event) {
		// TODO Auto-generated method stub
		
	}
   
}