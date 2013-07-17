package model.team;

import java.util.ArrayList;
import java.util.HashMap;

import model.actors.*;
import model.events.NewSearchEvent;

import simulator.*;

/**
 * 
 * @author rob.ivie
 *
 * list all of the actors in this method
 * we may be able to use another class instead of this method
 */
public class WiSARTeam extends Team {
	
	ComChannelList _channels = new ComChannelList();
	/**
	 * initialize all of the actors that will be used during the simulation
	 * assigns the actors the inputs and outputs they will be using (can this be moved inside the actor? -rob)
	 * @param outputs
	 */
	public WiSARTeam() {
		
		//Declare all output com channels
		//PS_Events
		_channels.add( new ComChannel<Boolean>(Channels.NEW_SEARCH_EVENT.name(), ComChannel.Type.AUDIO) );
		_channels.add( new ComChannel<Boolean>(Channels.TERMINATE_SEARCH_EVENT.name(), ComChannel.Type.AUDIO) );
		
		//PS
		_channels.add( new ComChannel<ParentSearch.PS_MM_COMM>(Channels.PS_MM_COMM.name(), ComChannel.Type.AUDIO) );
		
		//MM
		_channels.add( new ComChannel<MissionManager.MM_PS_COMM>(Channels.MM_PS_COMM.name(), ComChannel.Type.AUDIO) );
		
		
		//initialize inputs and outputs
		ComChannelList inputs = new ComChannelList();
		ComChannelList outputs = new ComChannelList();
		
		//Setup NewSearchEvent
		inputs.clear();
		inputs.add(_channels.get(Channels.NEW_SEARCH_EVENT.name()));
		outputs.clear();
		outputs.add(_channels.get(Channels.NEW_SEARCH_EVENT.name()));
		this.addEvent(new NewSearchEvent(inputs, outputs), 3);
		
		//add Parent Search, with its inputs and outputs, to the team 
		inputs.clear();
		inputs.add(_channels.get(Channels.NEW_SEARCH_EVENT.name()));
		inputs.add(_channels.get(Channels.TERMINATE_SEARCH_EVENT.name()));
		inputs.add(_channels.get(Channels.MM_PS_COMM.name()));
		outputs.clear();
		outputs.add(_channels.get(Channels.PS_MM_COMM.name()));
		this.addActor(new ParentSearch(inputs, outputs));

		//add Mission Manager, with its inputs and outputs, to the team
		inputs.clear();
		inputs.add(_channels.get(Channels.PS_MM_COMM.name()));
		outputs.clear();
		outputs.add(_channels.get(Channels.MM_PS_COMM.name()));
		this.addActor(new MissionManager(inputs, outputs));
		
		//add UAV Operator, with its inputs and outputs, to the team
//		inputs.clear();
//		inputs.put(UDO.MM_POKE_OP.name(), UDO.MM_POKE_OP);
//		inputs.put(UDO.MM_NEW_SEARCH_AOI_OP.name(), UDO.MM_NEW_SEARCH_AOI_OP);
//		inputs.put(UDO.OP_NEW_SEARCH_AOI_OP.name(), UDO.OP_NEW_SEARCH_AOI_OP);
//		inputs.put(UDO.MM_END_OP.name(), UDO.MM_END_OP);
//		outputs.clear();
//		outputs.put(UDO.OP_POKE_OGUI.name(), UDO.OP_POKE_OGUI);
//		outputs.put(UDO.OP_END_OGUI.name(), UDO.OP_END_OGUI);
//		outputs.put(UDO.OP_ACK_MM.name(), UDO.OP_ACK_MM);
//		outputs.put(UDO.OP_NEW_SEARCH_AOI_OP.name(), UDO.OP_NEW_SEARCH_AOI_OP);
//		outputs.put(UDO.OP_TAKE_OFF_OGUI.name(), UDO.OP_TAKE_OFF_OGUI);
//		this.add(new Operator(inputs, outputs));

		//add UAV Operator Gui, with its inputs and outputs, to the team
//		inputs.clear();
//		inputs.put(UDO.OP_TAKE_OFF_OGUI.name(), UDO.OP_TAKE_OFF_OGUI);
//		outputs.clear();
//		outputs.put(UDO.OGUI_TAKE_OFF_UAV.name(), UDO.OGUI_TAKE_OFF_UAV);
//		this.add(new OperatorGui(inputs, outputs));

		//add Video Operator, with its inputs and outputs, to the team
//		inputs.clear();
//		inputs.put(UDO.MM_POKE_VO.name(), UDO.MM_POKE_VO);
//		inputs.put(UDO.MM_END_VO.name(), UDO.MM_END_VO);
//		inputs.put(UDO.MM_TARGET_DESCRIPTION_VO.name(), UDO.MM_TARGET_DESCRIPTION_VO);
//		inputs.put(UDO.VO_TARGET_DESCRIPTION_VO.name(), UDO.VO_TARGET_DESCRIPTION_VO);
//		outputs.clear();
//		outputs.put(UDO.VO_ACK_MM.name(), UDO.VO_ACK_MM);
//		outputs.put(UDO.VO_TARGET_DESCRIPTION_VO.name(), UDO.VO_TARGET_DESCRIPTION_VO);
//		this.add(new VideoOperator(inputs, outputs));

		//add Video Operator Gui, with its inputs and outputs, to the team
//		inputs.clear();
//		outputs.clear();
//		this.add(new VideoOperatorGui(inputs, outputs));

		//add UAV, with its inputs and outputs, to the team
//		inputs.clear();
//		inputs.put(UDO.OGUI_TAKE_OFF_UAV.name(), UDO.OGUI_TAKE_OFF_UAV);
//		outputs.clear();
//		outputs.put(UDO.UAV_BATTERY_LOW_OGUI.name(), UDO.UAV_BATTERY_LOW_OGUI);
//		this.add(new UAV(inputs, outputs));
	}

}
