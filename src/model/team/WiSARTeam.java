package model.team;

import model.actors.MissionManager;
import model.actors.Operator;
import model.actors.OperatorGui;
import model.actors.ParentSearch;
import model.actors.UAV;
import model.actors.UAV_OGUI_WateredDown;
import model.actors.VO_WateredDown;
import model.actors.VideoOperator;
import model.actors.VideoOperatorGui;
import model.events.NewSearchEvent;
//import model.events.VoAckEvent;
import simulator.ComChannel;
import simulator.ComChannelList;
import simulator.Team;

/**
 * 
 * @author rob.ivie
 *
 * list all of the actors in this method
 * we may be able to use another class instead of this method
 */
public class WiSARTeam extends Team {
	
//	ComChannelList _com_channels = new ComChannelList();
	/**
	 * initialize all of the actors that will be used during the simulation
	 * assigns the actors the inputs and outputs they will be using (can this be moved inside the actor? -rob)
	 * @param outputs
	 */
	public WiSARTeam() {
		//Declare all output com channels
		//TEST
		
		_com_channels = new ComChannelList();
		//PS_Events
		_com_channels.add( new ComChannel<Boolean>(Channels.NEW_SEARCH_EVENT.name(), false, ComChannel.Type.AUDIO) );
		_com_channels.add( new ComChannel<Boolean>(Channels.TERMINATE_SEARCH_EVENT.name(), false, ComChannel.Type.AUDIO) );
		_com_channels.add(new ComChannel<Boolean>(Channels.NEW_SEARCH_AREA_EVENT.name(),false,ComChannel.Type.AUDIO));
		_com_channels.add(new ComChannel<Boolean>(Channels.TARGET_DESCRIPTION_EVENT.name(),false,ComChannel.Type.AUDIO));
		
		//PS
		_com_channels.add( new ComChannel<ParentSearch.AUDIO_PS_MM_COMM>(Channels.AUDIO_PS_MM_COMM.name(), ComChannel.Type.AUDIO) );
		
		//MM
		_com_channels.add( new ComChannel<MissionManager.AUDIO_MM_PS_COMM>(Channels.AUDIO_MM_PS_COMM.name(), ComChannel.Type.AUDIO) );
		_com_channels.add( new ComChannel<MissionManager.AUDIO_MM_VO_COMM>(Channels.AUDIO_MM_VO_COMM.name(), ComChannel.Type.AUDIO) );
		_com_channels.add( new ComChannel<MissionManager.AUDIO_MM_OP_COMM>(Channels.AUDIO_MM_OP_COMM.name(), ComChannel.Type.AUDIO) );
		_com_channels.add( new ComChannel<MissionManager.VISUAL_MM_VGUI_COMM>(Channels.VIDEO_MM_VGUI_COMM.name(), ComChannel.Type.VISUAL) );
		_com_channels.add(new ComChannel<MissionManager.DATA_MM_VGUI_COMM>(Channels.DATA_MM_VGUI_COMM.name(), ComChannel.Type.DATA));
		
		//VO
		_com_channels.add(new ComChannel<VideoOperator.AUDIO_VO_MM_COMM>(Channels.AUDIO_VO_MM_COMM.name(), ComChannel.Type.AUDIO));
		_com_channels.add(new ComChannel<VideoOperator.AUDIO_VO_OP_COMM>(Channels.AUDIO_VO_OP_COMM.name(), ComChannel.Type.AUDIO));
		_com_channels.add(new ComChannel<VideoOperator.VISUAL_VO_VGUI_COMM>(Channels.DATA_VO_VGUI.name(), ComChannel.Type.VISUAL));
		
		//OP
		_com_channels.add(new ComChannel<Operator.AUDIO_OP_MM_COMM>(Channels.AUDIO_OP_MM_COMM.name(), ComChannel.Type.AUDIO));
		_com_channels.add(new ComChannel<Operator.DATA_OP_OGUI_COMM>(Channels.DATA_OP_OGUI_COMM.name(), ComChannel.Type.DATA));
		_com_channels.add(new ComChannel<Operator.DATA_OP_UAV_COMM>(Channels.DATA_OP_UAV.name(), ComChannel.Type.DATA));
//		_com_channels.add(new ComChannel<Operator.AUDIO_OP_VO_COMM>(Channels.AUDIO_OP_VO_COMM.name(), ComChannel.Type.AUDIO));
//		_com_channels.add(new ComChannel<Operator.VISUAL_OP_OGUI_COMM>(Channels.VIDEO_OP_OGUI_COMM.name(), ComChannel.Type.VISUAL));
//		_com_channels.add(new ComChannel<Operator.VISUAL_OP_UAV_COMM>(Channels.VIDEO_OP_UAV_COMM.name(), ComChannel.Type.VISUAL));
		
		//VGUI
		_com_channels.add( new ComChannel<VideoOperatorGui.VISUAL_VGUI_MM_COMM>(Channels.VIDEO_VGUI_MM_COMM.name(), ComChannel.Type.VISUAL) );
		//_com_channels.add(new ComChannel<VideoOperatorGui.AUDIO_VGUI_MM_COMM>(Channels.AUDIO_VGUI_MM_COMM.name(), ComChannel.Type.AUDIO));
		
		//OGUI
		_com_channels.add(new ComChannel<OperatorGui.VIDEO_OGUI_OP_COMM>(Channels.VIDEO_OGUI_OP_COMM.name(), ComChannel.Type.VISUAL));
		
		//UAV
		_com_channels.add(new ComChannel<UAV.VISUAL_UAV_OP_COMM>(Channels.VIDEO_UAV_OP_COMM.name(), ComChannel.Type.VISUAL));
		_com_channels.add(new ComChannel<UAV.DATA_UAV_OGUI>(Channels.DATA_UAV_OGUI_COMM.name(), ComChannel.Type.DATA));
		_com_channels.add(new ComChannel<UAV.DATA_UAV_VGUI>(Channels.DATA_UAV_VGUI_COMM.name(), ComChannel.Type.DATA));
		
		//initialize inputs and outputs
		ComChannelList inputs = new ComChannelList();
		ComChannelList outputs = new ComChannelList();
		inputs.clear();
		inputs.add(_com_channels.get(Channels.NEW_SEARCH_EVENT.name()));
		outputs.clear();
		outputs.add(_com_channels.get(Channels.NEW_SEARCH_EVENT.name()));
		this.addEvent(new NewSearchEvent(inputs, outputs), 1);
		
//		inputs.clear();
//		inputs.add(_com_channels.get(Channels.VIDEO_VGUI_MM_COMM.name()));
//		outputs.clear();
//		outputs.add(_com_channels.get(Channels.VIDEO_VGUI_MM_COMM.name()));
//		this.addEvent(new VguiAlertMMEvent(inputs, outputs), 1);
		
//		//Setup Vgui req
//		inputs.clear();
//		inputs.add(_com_channels.get(Channels.VIDEO_VGUI_MM_COMM.name()));
//		outputs.clear();
//		outputs.add(_com_channels.get(Channels.VIDEO_VGUI_MM_COMM.name()));
//		this.addEvent(new VguiValidationReqTMMEvent(inputs, outputs), 1);

//		inputs.clear();
//		inputs.add(_com_channels.get(Channels.AUDIO_OP_MM_COMM.name()));
//		outputs.clear();
//		outputs.add(_com_channels.get(Channels.AUDIO_OP_MM_COMM.name()));
//		this.addEvent(new OpPokeMMEvent(inputs, outputs), 1);
		
//		//termination of communication without transmission event
//		inputs.clear();
//		inputs.add(_com_channels.get(Channels.AUDIO_MM_OP_COMM.name()));
//		outputs.clear();
//		outputs.add(_com_channels.get(Channels.AUDIO_OP_MM_COMM.name()));
//		this.addEvent(new OpFailedSearchMMEvent(inputs, outputs), 1);
		
//		inputs.clear();
//		inputs.add(_com_channels.get(Channels.AUDIO_MM_OP_COMM.name()));
//		outputs.clear();
//		outputs.add(_com_channels.get(Channels.AUDIO_OP_MM_COMM.name()));
//		this.addEvent(new OpAckEvent(inputs, outputs), 1);
		
		inputs.clear();
		inputs.add(_com_channels.get(Channels.AUDIO_MM_VO_COMM.name()));
		outputs.clear();
		outputs.add(_com_channels.get(Channels.AUDIO_VO_MM_COMM.name()));
		outputs.add(_com_channels.get(Channels.AUDIO_VO_OP_COMM.name()));
		this.addActor(new VO_WateredDown(inputs, outputs));
//		this.addEvent(new VoAckEvent(inputs, outputs), 1);

		
		//add Parent Search, with its inputs and outputs, to the team 
		inputs.clear();
		inputs.add(_com_channels.get(Channels.NEW_SEARCH_EVENT.name()));
		inputs.add(_com_channels.get(Channels.TERMINATE_SEARCH_EVENT.name()));
		inputs.add(_com_channels.get(Channels.AUDIO_MM_PS_COMM.name()));
		inputs.add(_com_channels.get(Channels.NEW_SEARCH_AREA_EVENT.name()));
		inputs.add(_com_channels.get(Channels.TARGET_DESCRIPTION_EVENT.name()));
		outputs.clear();
		outputs.add(_com_channels.get(Channels.AUDIO_PS_MM_COMM.name()));
		this.addActor(new ParentSearch(inputs, outputs));

		//add Mission Manager, with its inputs and outputs, to the team
		inputs.clear();
		inputs.add(_com_channels.get(Channels.AUDIO_PS_MM_COMM.name()));
		inputs.add(_com_channels.get(Channels.AUDIO_OP_MM_COMM.name()));
		inputs.add(_com_channels.get(Channels.AUDIO_VO_MM_COMM.name()));
		inputs.add(_com_channels.get(Channels.VIDEO_VGUI_MM_COMM.name()));
		outputs.clear();
		outputs.add(_com_channels.get(Channels.AUDIO_MM_PS_COMM.name()));
		outputs.add(_com_channels.get(Channels.AUDIO_MM_OP_COMM.name()));
		outputs.add(_com_channels.get(Channels.AUDIO_MM_VO_COMM.name()));
		outputs.add(_com_channels.get(Channels.DATA_MM_VGUI_COMM.name()));
		this.addActor(new MissionManager(inputs, outputs));
		
		//add UAV Operator, with its inputs and outputs, to the team
		inputs.clear();
		inputs.add(_com_channels.get(Channels.AUDIO_MM_OP_COMM.name()));
		inputs.add(_com_channels.get(Channels.AUDIO_VO_OP_COMM.name()));
		inputs.add(_com_channels.get(Channels.VIDEO_OGUI_OP_COMM.name()));
		inputs.add(_com_channels.get(Channels.VIDEO_UAV_OP_COMM.name()));
		outputs.clear();
		outputs.add(_com_channels.get(Channels.AUDIO_OP_MM_COMM.name()));
		outputs.add(_com_channels.get(Channels.DATA_OP_OGUI_COMM.name()));
		outputs.add(_com_channels.get(Channels.DATA_OP_UAV.name()));
		this.addActor(new Operator(inputs, outputs));

		inputs.clear();
		inputs.add(_com_channels.get(Channels.DATA_OP_OGUI_COMM.name()));
		inputs.add(_com_channels.get(Channels.DATA_OP_UAV.name()));
		inputs.add(_com_channels.get(Channels.DATA_UAV_OGUI_COMM.name()));
		outputs.clear();
		outputs.add(_com_channels.get(Channels.DATA_UAV_OGUI_COMM.name()));
		outputs.add(_com_channels.get(Channels.DATA_UAV_VGUI_COMM.name()));
		outputs.add(_com_channels.get(Channels.VIDEO_OGUI_OP_COMM.name()));
		outputs.add(_com_channels.get(Channels.VIDEO_UAV_OP_COMM.name()));
		outputs.add(_com_channels.get(Channels.DATA_UAV_OGUI_COMM.name()));
		outputs.add(_com_channels.get(Channels.DATA_UAV_VGUI_COMM.name()));
		this.addActor(new UAV_OGUI_WateredDown(inputs,outputs));
//		//add UAV Operator Gui, with its inputs and outputs, to the team
//		inputs.clear();
//		inputs.add(UAV_OGUI_DATA);
//		inputs.add(OP_OGUI_COMM);
//		inputs.put(UDO.OP_TAKE_OFF_OGUI.name(), UDO.OP_TAKE_OFF_OGUI);
//		outputs.clear();
//		outputs.add(OGUI_UAV_DATA);
//		outputs.add(OGUI_OP_DATA);
//		outputs.add(OGUI_VGUI_DATA);
//		outputs.put(UDO.OGUI_TAKE_OFF_UAV.name(), UDO.OGUI_TAKE_OFF_UAV);
//		this.addActor(new OperatorGui(inputs, outputs));

//		//add Video Operator, with its inputs and outputs, to the team
//		inputs.clear();
//		inputs.add(MM_VO_COMM);
//		inputs.put(UDO.MM_POKE_VO.name(), UDO.MM_POKE_VO);
//		inputs.put(UDO.MM_END_VO.name(), UDO.MM_END_VO);
//		inputs.put(UDO.MM_TARGET_DESCRIPTION_VO.name(), UDO.MM_TARGET_DESCRIPTION_VO);
//		inputs.put(UDO.VO_TARGET_DESCRIPTION_VO.name(), UDO.VO_TARGET_DESCRIPTION_VO);
//		outputs.clear();
//		outputs.add(VO_OP_COMM);
//		outputs.add(VO_MM_COMM);
//		outputs.add(VO_VGUI_COMM);
//		outputs.put(UDO.VO_ACK_MM.name(), UDO.VO_ACK_MM);
//		outputs.put(UDO.VO_TARGET_DESCRIPTION_VO.name(), UDO.VO_TARGET_DESCRIPTION_VO);
//		this.addActor(new VideoOperator(inputs, outputs));
//
//		//add Video Operator Gui, with its inputs and outputs, to the team
//		inputs.clear();
//		inputs.add(VO_VGUI_COMM);
//		inputs.add(MM_VGUI_COMM);
//		inputs.add(OGUI_VGUI_DATA);
//		inputs.add(UAV_VGUI_DATA);
//		outputs.clear();
//		this.addActor(new VideoOperatorGui(inputs, outputs));
//
//		//add UAV, with its inputs and outputs, to the team
//		inputs.clear();
//		inputs.add(OP_UAV_COMM);
//		inputs.add(OGUI_UAV_DATA);
////		inputs.put(UDO.OGUI_TAKE_OFF_UAV.name(), UDO.OGUI_TAKE_OFF_UAV);
//		outputs.clear();
//		outputs.add(UAV_VGUI_DATA);
//		outputs.add(UAV_OGUI_DATA);
//		outputs.add(UAV_OP_DATA);
////		outputs.put(UDO.UAV_BATTERY_LOW_OGUI.name(), UDO.UAV_BATTERY_LOW_OGUI);
//		this.addActor(new UAV(inputs, outputs));
	}

}
