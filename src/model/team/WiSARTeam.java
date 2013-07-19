package model.team;

import model.actors.MissionManager;
import model.actors.Operator;
import model.actors.OperatorGui;
import model.actors.ParentSearch;
import model.actors.VideoOperator;
import model.actors.VideoOperatorGui;
import model.events.NewSearchEvent;
import model.events.OpAckEvent;
import model.events.VoAckEvent;
import simulator.ComChannel;
import simulator.ComChannel.Type;
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
	
	ComChannelList _channels = new ComChannelList();
	/**
	 * initialize all of the actors that will be used during the simulation
	 * assigns the actors the inputs and outputs they will be using (can this be moved inside the actor? -rob)
	 * @param outputs
	 */
	public WiSARTeam() {
		//Declare all output com channels
		//PS_Events
		_channels.add( new ComChannel<Boolean>(Channels.NEW_SEARCH_EVENT.name(), false, ComChannel.Type.AUDIO) );
		_channels.add( new ComChannel<Boolean>(Channels.TERMINATE_SEARCH_EVENT.name(), false, ComChannel.Type.AUDIO) );
		_channels.add(new ComChannel<Boolean>(Channels.NEW_SEARCH_AREA_EVENT.name(),false,ComChannel.Type.AUDIO));
		_channels.add(new ComChannel<Boolean>(Channels.TARGET_DESCRIPTION_EVENT.name(),false,ComChannel.Type.AUDIO));
		
		//PS
		_channels.add( new ComChannel<ParentSearch.AUDIO_PS_MM_COMM>(Channels.AUDIO_PS_MM_COMM.name(), ComChannel.Type.AUDIO) );
		
		//MM
		_channels.add( new ComChannel<MissionManager.AUDIO_MM_PS_COMM>(Channels.AUDIO_MM_PS_COMM.name(), ComChannel.Type.AUDIO) );
		_channels.add( new ComChannel<MissionManager.AUDIO_MM_VO_COMM>(Channels.AUDIO_MM_VO_COMM.name(), ComChannel.Type.AUDIO) );
		_channels.add( new ComChannel<MissionManager.AUDIO_MM_OP_COMM>(Channels.AUDIO_MM_OP_COMM.name(), ComChannel.Type.AUDIO) );
		_channels.add( new ComChannel<MissionManager.VISUAL_MM_VGUI_COMM>(Channels.VIDEO_MM_VGUI_COMM.name(), ComChannel.Type.VISUAL) );
		
		//VO
		_channels.add(new ComChannel<VideoOperator.AUDIO_VO_MM_COMM>(Channels.AUDIO_VO_MM_COMM.name(), ComChannel.Type.AUDIO));
		
		//OP
		_channels.add(new ComChannel<Operator.AUDIO_OP_MM_COMM>(Channels.AUDIO_OP_MM_COMM.name(), ComChannel.Type.AUDIO));
		
		
		//VGUI
		_channels.add( new ComChannel<VideoOperatorGui.VISUAL_VGUI_MM_COMM>(Channels.VIDEO_VGUI_MM_COMM.name(), ComChannel.Type.VISUAL) );
		
		
		//initialize inputs and outputs
		ComChannelList inputs = new ComChannelList();
		ComChannelList outputs = new ComChannelList();
		
		//Setup NewSearchEvent
		inputs.clear();
		inputs.add(_channels.get(Channels.NEW_SEARCH_EVENT.name()));
		outputs.clear();
		outputs.add(_channels.get(Channels.NEW_SEARCH_EVENT.name()));
		this.addEvent(new NewSearchEvent(inputs, outputs), 1);

		inputs.clear();
		inputs.add(_channels.get(Channels.AUDIO_MM_VO_COMM.name()));
		outputs.clear();
		outputs.add(_channels.get(Channels.AUDIO_VO_MM_COMM.name()));
		this.addEvent(new VoAckEvent(inputs, outputs), 1);

		inputs.clear();
		inputs.add(_channels.get(Channels.AUDIO_MM_OP_COMM.name()));
		outputs.clear();
		outputs.add(_channels.get(Channels.AUDIO_OP_MM_COMM.name()));
		this.addEvent(new OpAckEvent(inputs, outputs), 1);

		//Declare all output com channels
		//PS_Events
		ComChannel<Boolean> NewSearchEvent = new ComChannel<Boolean>("NewSearchEvent", Type.DATA);
		ComChannel<Boolean> TerminateSearchEvent = new ComChannel<Boolean>("TerminateSearchEvent", Type.DATA);
		
		//PS
		ComChannel<ParentSearch.AUDIO_PS_MM_COMM> PS_MM_COMM = new ComChannel<ParentSearch.AUDIO_PS_MM_COMM>("PS_MM_COMM", Type.AUDIO);
		
		//MM
		ComChannel<MissionManager.AUDIO_MM_PS_COMM> MM_PS_COMM = new ComChannel<MissionManager.AUDIO_MM_PS_COMM>("MM_PS_COMM", Type.AUDIO);
		ComChannel<MissionManager.AUDIO_MM_OP_COMM> MM_OP_COMM = new ComChannel<MissionManager.AUDIO_MM_OP_COMM>("MM_OP_COMM", Type.AUDIO);
		ComChannel<MissionManager.AUDIO_MM_VO_COMM> MM_VO_COMM = new ComChannel<MissionManager.AUDIO_MM_VO_COMM>("MM_VO_COMM", Type.AUDIO);
		ComChannel<MissionManager.VISUAL_MM_VGUI_COMM> MM_VGUI_COMM = new ComChannel<MissionManager.VISUAL_MM_VGUI_COMM>("MM_VGUI_COMM", Type.VISUAL);
		
		
		//OP
		ComChannel<Operator.AUDIO_OP_MM_COMM> OP_MM_COMM = new ComChannel<Operator.AUDIO_OP_MM_COMM>("OP_MM_COMM", Type.AUDIO);
		ComChannel<Operator.VISUAL_OP_OGUI_COMM> OP_OGUI_COMM = new ComChannel<Operator.VISUAL_OP_OGUI_COMM>("OP_OGUI_COMM", Type.VISUAL);
		ComChannel<Operator.VISUAL_OP_UAV_COMM> OP_UAV_COMM = new ComChannel<Operator.VISUAL_OP_UAV_COMM>("OP_UAV_COMM", Type.VISUAL);
		
		//OGUI
		ComChannel<OperatorGui.OGUI_OP_DATA> OGUI_OP_DATA = new ComChannel<OperatorGui.OGUI_OP_DATA>("OGUI_OP_DATA", Type.DATA);
		ComChannel<OperatorGui.OGUI_UAV_DATA> OGUI_UAV_DATA = new ComChannel<OperatorGui.OGUI_UAV_DATA>("OGUI_UAV_DATA", Type.DATA);
		ComChannel<OperatorGui.OGUI_VGUI_DATA> OGUI_VGUI_DATA = new ComChannel<OperatorGui.OGUI_VGUI_DATA>("OGUI_VGUI_DATA", Type.DATA);
		
		//VO
		ComChannel<VideoOperator.AUDIO_VO_MM_COMM> VO_MM_COMM = new ComChannel<VideoOperator.AUDIO_VO_MM_COMM>("VO_MM_COMM", Type.AUDIO);
		ComChannel<VideoOperator.VISUAL_VO_VGUI_COMM> VO_VGUI_COMM = new ComChannel<VideoOperator.VISUAL_VO_VGUI_COMM>("VO_OGUI_COMM", Type.VISUAL);
		ComChannel<VideoOperator.AUDIO_VO_OP_COMM> VO_OP_COMM = new ComChannel<VideoOperator.AUDIO_VO_OP_COMM>("VO_OP_COMM", Type.AUDIO);
		
		//VGUI

		//UAV
//		ComChannel<UAV.DATA_UAV_OP> UAV_OP_DATA = new ComChannel<UAV.DATA_UAV_OP>("UAV_OP_DATA", Type.DATA);
//		ComChannel<UAV.DATA_UAV_OGUI> UAV_OGUI_DATA = new ComChannel<UAV.DATA_UAV_OGUI>("UAV_OGUI_DATA", Type.DATA);
//		ComChannel<UAV.DATA_UAV_VGUI> UAV_VGUI_DATA = new ComChannel<UAV.DATA_UAV_VGUI>("UAV_VGUI_DATA", Type.DATA);
		
		//add Parent Search, with its inputs and outputs, to the team 
		inputs.clear();
		inputs.add(_channels.get(Channels.NEW_SEARCH_EVENT.name()));
		inputs.add(_channels.get(Channels.TERMINATE_SEARCH_EVENT.name()));
		inputs.add(_channels.get(Channels.AUDIO_MM_PS_COMM.name()));
		inputs.add(_channels.get(Channels.NEW_SEARCH_AREA_EVENT.name()));
		inputs.add(_channels.get(Channels.TARGET_DESCRIPTION_EVENT.name()));
		outputs.clear();
		outputs.add(_channels.get(Channels.AUDIO_PS_MM_COMM.name()));
		this.addActor(new ParentSearch(inputs, outputs));

		//add Mission Manager, with its inputs and outputs, to the team
		inputs.clear();
		inputs.add(_channels.get(Channels.AUDIO_PS_MM_COMM.name()));
		inputs.add(_channels.get(Channels.AUDIO_OP_MM_COMM.name()));
		inputs.add(_channels.get(Channels.AUDIO_VO_MM_COMM.name()));
		inputs.add(_channels.get(Channels.VIDEO_VGUI_MM_COMM.name()));
//		inputs.add(PS_MM_COMM);
//		inputs.add(VO_MM_COMM);
//		inputs.add(OP_MM_COMM);
//		inputs.put(UDO.PS_POKE_MM.name(), UDO.PS_POKE_MM);
//		inputs.put(UDO.PS_NEW_SEARCH_AOI_MM.name(), UDO.PS_NEW_SEARCH_AOI_MM);
//		inputs.put(UDO.MM_NEW_SEARCH_AOI_MM.name(), UDO.MM_NEW_SEARCH_AOI_MM);
//		inputs.put(UDO.PS_TARGET_DESCRIPTION_MM.name(), UDO.PS_TARGET_DESCRIPTION_MM);
//		inputs.put(UDO.MM_TARGET_DESCRIPTION_MM.name(), UDO.MM_TARGET_DESCRIPTION_MM);
//		inputs.put(UDO.PS_END_MM.name(), UDO.PS_END_MM);
//		inputs.put(UDO.OP_ACK_MM.name(), UDO.OP_ACK_MM);
//		inputs.put(UDO.VO_ACK_MM.name(), UDO.VO_ACK_MM);
		outputs.clear();
		outputs.add(_channels.get(Channels.AUDIO_MM_PS_COMM.name()));
		outputs.add(_channels.get(Channels.AUDIO_MM_OP_COMM.name()));
		outputs.add(_channels.get(Channels.AUDIO_MM_VO_COMM.name()));
		outputs.add(_channels.get(Channels.VIDEO_MM_VGUI_COMM.name()));
//		outputs.add(MM_PS_COMM);
//		outputs.add(MM_OP_COMM);
//		outputs.add(MM_VO_COMM);
//		outputs.add(MM_VGUI_COMM);
//		outputs.put(UDO.MM_ACK_PS.name(),UDO.MM_ACK_PS);
//		outputs.put(UDO.MM_POKE_VO.name(), UDO.MM_POKE_VO);
//		outputs.put(UDO.MM_POKE_OP.name(), UDO.MM_POKE_OP);
//		outputs.put(UDO.MM_NEW_SEARCH_AOI_OP.name(), UDO.MM_NEW_SEARCH_AOI_OP);
//		outputs.put(UDO.MM_NEW_SEARCH_AOI_MM.name(), UDO.MM_NEW_SEARCH_AOI_MM);
//		outputs.put(UDO.MM_TARGET_DESCRIPTION_VO.name(), UDO.MM_TARGET_DESCRIPTION_VO);
//		outputs.put(UDO.MM_TARGET_DESCRIPTION_MM.name(), UDO.MM_TARGET_DESCRIPTION_MM);
//		outputs.put(UDO.MM_END_OP.name(), UDO.MM_END_OP);
//		outputs.put(UDO.MM_END_VO.name(), UDO.MM_END_VO);
		this.addActor(new MissionManager(inputs, outputs));
		
//		//add UAV Operator, with its inputs and outputs, to the team
//		inputs.clear();
//		inputs.add(MM_OP_COMM);
//		inputs.add(VO_OP_COMM);
//		inputs.add(OGUI_OP_DATA);
//		inputs.add(UAV_OP_DATA);
////		inputs.put(UDO.MM_POKE_OP.name(), UDO.MM_POKE_OP);
////		inputs.put(UDO.MM_NEW_SEARCH_AOI_OP.name(), UDO.MM_NEW_SEARCH_AOI_OP);
////		inputs.put(UDO.OP_NEW_SEARCH_AOI_OP.name(), UDO.OP_NEW_SEARCH_AOI_OP);
////		inputs.put(UDO.MM_END_OP.name(), UDO.MM_END_OP);
//		outputs.clear();
//		outputs.add(OP_UAV_COMM);
//		outputs.add(OP_OGUI_COMM);
//		outputs.add(OP_MM_COMM);
////		outputs.put(UDO.OP_POKE_OGUI.name(), UDO.OP_POKE_OGUI);
////		outputs.put(UDO.OP_END_OGUI.name(), UDO.OP_END_OGUI);
////		outputs.put(UDO.OP_ACK_MM.name(), UDO.OP_ACK_MM);
////		outputs.put(UDO.OP_NEW_SEARCH_AOI_OP.name(), UDO.OP_NEW_SEARCH_AOI_OP);
////		outputs.put(UDO.OP_TAKE_OFF_OGUI.name(), UDO.OP_TAKE_OFF_OGUI);
//		this.addActor(new Operator(inputs, outputs));
//
//		//add UAV Operator Gui, with its inputs and outputs, to the team
//		inputs.clear();
//		inputs.add(UAV_OGUI_DATA);
//		inputs.add(OP_OGUI_COMM);
////		inputs.put(UDO.OP_TAKE_OFF_OGUI.name(), UDO.OP_TAKE_OFF_OGUI);
//		outputs.clear();
//		outputs.add(OGUI_UAV_DATA);
//		outputs.add(OGUI_OP_DATA);
//		outputs.add(OGUI_VGUI_DATA);
//
////		outputs.put(UDO.OGUI_TAKE_OFF_UAV.name(), UDO.OGUI_TAKE_OFF_UAV);
//		this.addActor(new OperatorGui(inputs, outputs));
//
//		//add Video Operator, with its inputs and outputs, to the team
//		inputs.clear();
//		inputs.add(MM_VO_COMM);
////		inputs.put(UDO.MM_POKE_VO.name(), UDO.MM_POKE_VO);
////		inputs.put(UDO.MM_END_VO.name(), UDO.MM_END_VO);
////		inputs.put(UDO.MM_TARGET_DESCRIPTION_VO.name(), UDO.MM_TARGET_DESCRIPTION_VO);
////		inputs.put(UDO.VO_TARGET_DESCRIPTION_VO.name(), UDO.VO_TARGET_DESCRIPTION_VO);
//		outputs.clear();
//		outputs.add(VO_OP_COMM);
//		outputs.add(VO_MM_COMM);
//		outputs.add(VO_VGUI_COMM);
////		outputs.put(UDO.VO_ACK_MM.name(), UDO.VO_ACK_MM);
////		outputs.put(UDO.VO_TARGET_DESCRIPTION_VO.name(), UDO.VO_TARGET_DESCRIPTION_VO);
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
