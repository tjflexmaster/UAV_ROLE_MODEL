package model.team;

import model.actors.MissionCompletionWatcher;
import model.actors.MissionManager;
import model.actors.MissionManager.DATA_MM_VGUI_COMM;
import model.actors.Operator;
import model.actors.OperatorGui;
import model.actors.ParentSearch;
import model.actors.UAV;
import model.actors.UAV_OGUI_WateredDown;
import model.actors.VO_WateredDown;
import model.actors.VideoOperator;
import model.actors.VideoOperatorGui;
import model.actors.VideoOperatorGui.VIDEO_VGUI_MM_COMM;
import model.events.NewSearchEvent;
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
		_com_channels.add( new ComChannel<ParentSearch.AUDIO_PS_MM_COMM>(Channels.AUDIO_PS_MM_COMM.name(), ComChannel.Type.AUDIO, "PS", "MM") );
		
		//add MM channels
		_com_channels.add( new ComChannel<MissionManager.AUDIO_MM_PS_COMM>(Channels.AUDIO_MM_PS_COMM.name(), ComChannel.Type.AUDIO, "MM", "PS") );
		_com_channels.add( new ComChannel<MissionManager.AUDIO_MM_VO_COMM>(Channels.AUDIO_MM_VO_COMM.name(), ComChannel.Type.AUDIO, "MM", "VO") );
		_com_channels.add( new ComChannel<MissionManager.AUDIO_MM_OP_COMM>(Channels.AUDIO_MM_OP_COMM.name(), ComChannel.Type.AUDIO, "MM", "OP") );
		_com_channels.add( new ComChannel<MissionManager.DATA_MM_VGUI_COMM>(Channels.VIDEO_MM_VGUI_COMM.name(), ComChannel.Type.VISUAL, "MM", "VGUI") );
		_com_channels.add(new ComChannel<MissionManager.DATA_MM_VGUI_COMM>(Channels.VISUAL_MM_VGUI_COMM.name(), ComChannel.Type.DATA, "MM", "VGUI") );
		
		//add OP channels
		_com_channels.add(new ComChannel<Operator.AUDIO_OP_MM_COMM>(Channels.AUDIO_OP_MM_COMM.name(), ComChannel.Type.AUDIO, "OP", "MM"));
		_com_channels.add(new ComChannel<Operator.AUDIO_OP_MM_COMM>(Channels.DATA_OP_OGUI_COMM.name(), ComChannel.Type.VISUAL, "OP", "OGUI"));
		_com_channels.add(new ComChannel<Operator.AUDIO_OP_MM_COMM>(Channels.DATA_OP_UAV_COMM.name(), ComChannel.Type.VISUAL, "OP", "UAV"));
		
		//add VGUI channels
		_com_channels.add( new ComChannel<VIDEO_VGUI_MM_COMM>(Channels.VIDEO_VGUI_MM_COMM.name(), ComChannel.Type.VISUAL, "VGUI", "MM") );
		
		//add OGUI channels
		_com_channels.add(new ComChannel<OperatorGui.VIDEO_OGUI_OP_COMM>(Channels.VIDEO_OGUI_OP_COMM.name(), ComChannel.Type.VISUAL, "OGUI", "OP"));
		
		//add UAV channels
		_com_channels.add(new ComChannel<UAV.VIDEO_UAV_OP_COMM>(Channels.VIDEO_UAV_OP_COMM.name(), ComChannel.Type.VISUAL, "UAV", "OP"));
		_com_channels.add(new ComChannel<UAV.DATA_UAV_OGUI>(Channels.DATA_UAV_OGUI_COMM.name(), ComChannel.Type.DATA, "UAV", "OGUI"));
		_com_channels.add(new ComChannel<UAV.DATA_UAV_VGUI>(Channels.DATA_UAV_VGUI_COMM.name(), ComChannel.Type.DATA, "UAV", "VGUI"));
		
		//add VO channels
		_com_channels.add(new ComChannel<VideoOperator.AUDIO_VO_MM_COMM>(Channels.AUDIO_VO_MM_COMM.name(), ComChannel.Type.AUDIO, "VO", "MM"));
		_com_channels.add(new ComChannel<VideoOperator.AUDIO_VO_OP_COMM>(Channels.AUDIO_VO_OP_COMM.name(), ComChannel.Type.AUDIO, "VO", "OP"));
		_com_channels.add(new ComChannel<VideoOperator.VISUAL_VO_VGUI_COMM>(Channels.VISUAL_VO_VGUI.name(), ComChannel.Type.VISUAL, "VO", "VGUI"));

		_com_channels.add(new ComChannel<ParentSearch.DATA_PS_PS_COMM>(Channels.DATA_PS_PS_COMM.name(), ComChannel.Type.DATA,"PS", "PS"));
		_com_channels.add(new ComChannel<MissionManager.DATA_MM_MM_COMM>(Channels.DATA_MM_MM_COMM.name(), ComChannel.Type.DATA,"MM", "MM"));
		_com_channels.add(new ComChannel<Operator.DATA_OP_OP_COMM>(Channels.DATA_OP_OP_COMM.name(), ComChannel.Type.DATA,"OP", "OP"));
		
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
		outputs.add(_com_channels.get(Channels.DATA_PS_PS_COMM.name()));
		this.addActor(new ParentSearch(inputs, outputs));

//		inputs.clear();
//		inputs.add(_com_channels.get(Channels.NEW_SEARCH_EVENT.name()));
//		inputs.add(_com_channels.get(Channels.TERMINATE_SEARCH_EVENT.name()));
//		inputs.add(_com_channels.get(Channels.AUDIO_MM_PS_COMM.name()));
//		inputs.add(_com_channels.get(Channels.NEW_SEARCH_AREA_EVENT.name()));
//		inputs.add(_com_channels.get(Channels.TARGET_DESCRIPTION_EVENT.name()));
//		inputs.add(_com_channels.get(Channels.NEW_SEARCH_EVENT.name()));
//		inputs.add(_com_channels.get(Channels.AUDIO_PS_MM_COMM.name()));
//		inputs.add(_com_channels.get(Channels.AUDIO_OP_MM_COMM.name()));
//		inputs.add(_com_channels.get(Channels.AUDIO_VO_MM_COMM.name()));
//		inputs.add(_com_channels.get(Channels.VIDEO_VGUI_MM_COMM.name()));
//		inputs.add(_com_channels.get(Channels.AUDIO_MM_OP_COMM.name()));
//		inputs.add(_com_channels.get(Channels.AUDIO_VO_OP_COMM.name()));
//		inputs.add(_com_channels.get(Channels.VIDEO_OGUI_OP_COMM.name()));
//		inputs.add(_com_channels.get(Channels.VIDEO_UAV_OP_COMM.name()));
//		inputs.add(_com_channels.get(Channels.DATA_UAV_OGUI_COMM.name()));
//		inputs.add(_com_channels.get(Channels.VISUAL_OP_OGUI_COMM.name()));
//		inputs.add(_com_channels.get(Channels.VISUAL_OP_UAV_COMM.name()));
//		inputs.add(_com_channels.get(Channels.AUDIO_MM_VO_COMM.name()));
//		this.addActor(new MissionCompletionWatcher(inputs,outputs));
		
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
		outputs.add(_com_channels.get(Channels.DATA_MM_MM_COMM.name()));
		this.addActor(new MissionManager(inputs, outputs));
		
		//add the uav operator
		inputs.clear();
		inputs.add(_com_channels.get(Channels.AUDIO_MM_OP_COMM.name()));
		inputs.add(_com_channels.get(Channels.AUDIO_VO_OP_COMM.name()));
		inputs.add(_com_channels.get(Channels.VIDEO_OGUI_OP_COMM.name()));
		inputs.add(_com_channels.get(Channels.VIDEO_UAV_OP_COMM.name()));
		outputs.clear();
		outputs.add(_com_channels.get(Channels.AUDIO_OP_MM_COMM.name()));
		outputs.add(_com_channels.get(Channels.DATA_OP_OGUI_COMM.name()));
		outputs.add(_com_channels.get(Channels.DATA_OP_UAV_COMM.name()));
		outputs.add(_com_channels.get(Channels.DATA_OP_OP_COMM.name()));
		this.addActor(new Operator(inputs, outputs));

		//add the uav gui (watered down)
		inputs.clear();
		inputs.add(_com_channels.get(Channels.DATA_UAV_OGUI_COMM.name()));
		inputs.add(_com_channels.get(Channels.DATA_OP_OGUI_COMM.name()));
		inputs.add(_com_channels.get(Channels.DATA_OP_UAV_COMM.name()));
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
		inputs.add(_com_channels.get(Channels.DATA_UAV_VGUI_COMM.name()));
		outputs.clear();
		outputs.add(_com_channels.get(Channels.AUDIO_VO_MM_COMM.name()));
		outputs.add(_com_channels.get(Channels.AUDIO_VO_OP_COMM.name()));
		this.addActor(new VO_WateredDown(inputs, outputs));
	}

}
