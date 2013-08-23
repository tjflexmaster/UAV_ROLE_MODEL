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
	
	/**
	 * initialize all of the actors that will be used during the simulation
	 * assigns the actors the inputs and outputs they will be using (can this be moved inside the actor? -rob)
	 * @param outputs
	 */
	public WiSARTeam() {
		//initialize the list of communication channels
		_com_channels = new ComChannelList();
		
		//add EVENT channels
		_com_channels.add( new ComChannel<Boolean>(Channels.NEW_SEARCH_EVENT.name(), false, ComChannel.Type.AUDIO) );
		_com_channels.add( new ComChannel<Boolean>(Channels.TERMINATE_SEARCH_EVENT.name(), false, ComChannel.Type.AUDIO) );
		_com_channels.add(new ComChannel<Boolean>(Channels.NEW_SEARCH_AREA_EVENT.name(),false,ComChannel.Type.AUDIO));
		_com_channels.add(new ComChannel<Boolean>(Channels.TARGET_DESCRIPTION_EVENT.name(),false,ComChannel.Type.AUDIO));
		
		//add PS channels
		_com_channels.add( new ComChannel<ParentSearch.AUDIO_PS_MM_COMM>(Channels.AUDIO_PS_MM_COMM.name(), ComChannel.Type.AUDIO) );
		
		//add MM channels
		_com_channels.add( new ComChannel<MissionManager.AUDIO_MM_PS_COMM>(Channels.AUDIO_MM_PS_COMM.name(), ComChannel.Type.AUDIO) );
		_com_channels.add( new ComChannel<MissionManager.AUDIO_MM_VO_COMM>(Channels.AUDIO_MM_VO_COMM.name(), ComChannel.Type.AUDIO) );
		_com_channels.add( new ComChannel<MissionManager.AUDIO_MM_OP_COMM>(Channels.AUDIO_MM_OP_COMM.name(), ComChannel.Type.AUDIO) );
		_com_channels.add( new ComChannel<MissionManager.VISUAL_MM_VGUI_COMM>(Channels.VIDEO_MM_VGUI_COMM.name(), ComChannel.Type.VISUAL) );
		_com_channels.add(new ComChannel<MissionManager.DATA_MM_VGUI_COMM>(Channels.VISUAL_MM_VGUI_COMM.name(), ComChannel.Type.DATA));
		
		//add OP channels
		_com_channels.add(new ComChannel<Operator.AUDIO_OP_MM_COMM>(Channels.AUDIO_OP_MM_COMM.name(), ComChannel.Type.AUDIO));
		_com_channels.add(new ComChannel<Operator.AUDIO_OP_MM_COMM>(Channels.VISUAL_OP_OGUI_COMM.name(), ComChannel.Type.VISUAL));
		_com_channels.add(new ComChannel<Operator.AUDIO_OP_MM_COMM>(Channels.VISUAL_OP_UAV_COMM.name(), ComChannel.Type.VISUAL));
		
		//add VGUI channels
		_com_channels.add( new ComChannel<VideoOperatorGui.VISUAL_VGUI_MM_COMM>(Channels.VIDEO_VGUI_MM_COMM.name(), ComChannel.Type.VISUAL) );
		
		//add OGUI channels
		_com_channels.add(new ComChannel<OperatorGui.VIDEO_OGUI_OP_COMM>(Channels.VIDEO_OGUI_OP_COMM.name(), ComChannel.Type.VISUAL));
		
		//add UAV channels
		_com_channels.add(new ComChannel<UAV.VISUAL_UAV_OP_COMM>(Channels.VIDEO_UAV_OP_COMM.name(), ComChannel.Type.VISUAL));
		_com_channels.add(new ComChannel<UAV.DATA_UAV_OGUI>(Channels.DATA_UAV_OGUI_COMM.name(), ComChannel.Type.DATA));
		_com_channels.add(new ComChannel<UAV.DATA_UAV_VGUI>(Channels.DATA_UAV_VGUI_COMM.name(), ComChannel.Type.DATA));
		
		//add VO channels
		_com_channels.add(new ComChannel<VideoOperator.AUDIO_VO_MM_COMM>(Channels.AUDIO_VO_MM_COMM.name(), ComChannel.Type.AUDIO));
		_com_channels.add(new ComChannel<VideoOperator.AUDIO_VO_OP_COMM>(Channels.AUDIO_VO_OP_COMM.name(), ComChannel.Type.AUDIO));
		_com_channels.add(new ComChannel<VideoOperator.VISUAL_VO_VGUI_COMM>(Channels.VISUAL_VO_VGUI.name(), ComChannel.Type.VISUAL));
		
		//initialize inputs and outputs (temporary lists)
		ComChannelList inputs = new ComChannelList();
		ComChannelList outputs = new ComChannelList();
		
		//add new search event
		inputs.clear();
		inputs.add(_com_channels.get(Channels.NEW_SEARCH_EVENT.name()));
		outputs.clear();
		outputs.add(_com_channels.get(Channels.NEW_SEARCH_EVENT.name()));
		this.addEvent(new NewSearchEvent(inputs, outputs), 1);

		//add the parent search
		inputs.clear();
		inputs.add(_com_channels.get(Channels.NEW_SEARCH_EVENT.name()));
		inputs.add(_com_channels.get(Channels.TERMINATE_SEARCH_EVENT.name()));
		inputs.add(_com_channels.get(Channels.AUDIO_MM_PS_COMM.name()));
		inputs.add(_com_channels.get(Channels.NEW_SEARCH_AREA_EVENT.name()));
		inputs.add(_com_channels.get(Channels.TARGET_DESCRIPTION_EVENT.name()));
		outputs.clear();
		outputs.add(_com_channels.get(Channels.AUDIO_PS_MM_COMM.name()));
		this.addActor(new ParentSearch(inputs, outputs));

		//add the mission manager
		inputs.clear();
		inputs.add(_com_channels.get(Channels.AUDIO_PS_MM_COMM.name()));
		inputs.add(_com_channels.get(Channels.AUDIO_OP_MM_COMM.name()));
		inputs.add(_com_channels.get(Channels.AUDIO_VO_MM_COMM.name()));
		inputs.add(_com_channels.get(Channels.VIDEO_VGUI_MM_COMM.name()));
		outputs.clear();
		outputs.add(_com_channels.get(Channels.AUDIO_MM_PS_COMM.name()));
		outputs.add(_com_channels.get(Channels.AUDIO_MM_OP_COMM.name()));
		outputs.add(_com_channels.get(Channels.AUDIO_MM_VO_COMM.name()));
		outputs.add(_com_channels.get(Channels.VISUAL_MM_VGUI_COMM.name()));
		this.addActor(new MissionManager(inputs, outputs));
		
		//add the uav operator
		inputs.clear();
		inputs.add(_com_channels.get(Channels.AUDIO_MM_OP_COMM.name()));
		inputs.add(_com_channels.get(Channels.AUDIO_VO_OP_COMM.name()));
		inputs.add(_com_channels.get(Channels.VIDEO_OGUI_OP_COMM.name()));
		inputs.add(_com_channels.get(Channels.VIDEO_UAV_OP_COMM.name()));
		outputs.clear();
		outputs.add(_com_channels.get(Channels.AUDIO_OP_MM_COMM.name()));
		outputs.add(_com_channels.get(Channels.VISUAL_OP_OGUI_COMM.name()));
		outputs.add(_com_channels.get(Channels.VISUAL_OP_UAV_COMM.name()));
		this.addActor(new Operator(inputs, outputs));

		//add the uav gui (watered down)
		inputs.clear();
		inputs.add(_com_channels.get(Channels.DATA_UAV_OGUI_COMM.name()));
		inputs.add(_com_channels.get(Channels.VISUAL_OP_OGUI_COMM.name()));
		inputs.add(_com_channels.get(Channels.VISUAL_OP_UAV_COMM.name()));
		outputs.clear();
		outputs.add(_com_channels.get(Channels.DATA_UAV_OGUI_COMM.name()));
		outputs.add(_com_channels.get(Channels.DATA_UAV_VGUI_COMM.name()));
		outputs.add(_com_channels.get(Channels.VIDEO_OGUI_OP_COMM.name()));
		outputs.add(_com_channels.get(Channels.VIDEO_UAV_OP_COMM.name()));
		outputs.add(_com_channels.get(Channels.DATA_UAV_OGUI_COMM.name()));
		outputs.add(_com_channels.get(Channels.DATA_UAV_VGUI_COMM.name()));
		this.addActor(new UAV_OGUI_WateredDown(inputs,outputs));
		
		//add the video operator (watered down)
		inputs.clear();
		inputs.add(_com_channels.get(Channels.AUDIO_MM_VO_COMM.name()));
		outputs.clear();
		outputs.add(_com_channels.get(Channels.AUDIO_VO_MM_COMM.name()));
		outputs.add(_com_channels.get(Channels.AUDIO_VO_OP_COMM.name()));
		this.addActor(new VO_WateredDown(inputs, outputs));
	}

}
