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
public class WiSARTeam extends ArrayList<Actor> {
	
	/**
	 * i have no idea what this means, but it fixed a compilation warning...
	 */
	private static final long serialVersionUID = 682275625652767731L;

	/**
	 * initialize all of the actors that will be used during the simulation
	 * assigns the actors the inputs and outputs they will be using (can this be moved inside the actor? -rob)
	 * @param outputs
	 */
	public WiSARTeam() {
		//initialize inputs and outputs
		ComChannelList inputs = new ComChannelList();
		ComChannelList outputs = new ComChannelList();
		
		//Declare all output com channels
		//PS_Events
		ComChannel<Boolean> NewSearchEvent = new ComChannel<Boolean>("NewSearchEvent");
		ComChannel<Boolean> TerminateSearchEvent = new ComChannel<Boolean>("TerminateSearchEvent");
		
		//PS
		ComChannel<ParentSearch.PS_MM_COMM> PS_MM_COMM = new ComChannel<ParentSearch.PS_MM_COMM>("PS_MM_COMM");
		ComChannel<ParentSearch.PS_MM_DATA> PS_MM_DATA = new ComChannel<ParentSearch.PS_MM_DATA>("PS_MM_DATA");
		
		//MM
		ComChannel<MissionManager.MM_PS_COMM> MM_PS_COMM = new ComChannel<MissionManager.MM_PS_COMM>("MM_PS_COMM");
		ComChannel<MissionManager.MM_PS_DATA> MM_PS_DATA = new ComChannel<MissionManager.MM_PS_DATA>("MM_PS_DATA");
		ComChannel<MissionManager.MM_OP_COMM> MM_OP_COMM = new ComChannel<MissionManager.MM_OP_COMM>("MM_OP_COMM");
		ComChannel<MissionManager.MM_OP_DATA> MM_OP_DATA = new ComChannel<MissionManager.MM_OP_DATA>("MM_OP_DATA");
		ComChannel<MissionManager.MM_VO_COMM> MM_VO_COMM = new ComChannel<MissionManager.MM_VO_COMM>("MM_VO_COMM");
		ComChannel<MissionManager.MM_VO_DATA> MM_VO_DATA = new ComChannel<MissionManager.MM_VO_DATA>("MM_VO_DATA");
		ComChannel<MissionManager.MM_VGUI_COMM> MM_VGUI_COMM = new ComChannel<MissionManager.MM_VGUI_COMM>("MM_VGUI_COMM");
		ComChannel<MissionManager.MM_VGUI_DATA> MM_VGUI_DATA = new ComChannel<MissionManager.MM_VGUI_DATA>("MM_VGUI_DATA");
		
		
		//OP
		ComChannel<Operator.OP_MM_COMM> OP_MM_COMM = new ComChannel<Operator.OP_MM_COMM>("OP_MM_COMM");
		ComChannel<Operator.OP_MM_DATA> OP_MM_DATA = new ComChannel<Operator.OP_MM_DATA>("OP_MM_DATA");
		ComChannel<Operator.OP_OGUI_COMM> OP_OGUI_COMM = new ComChannel<Operator.OP_OGUI_COMM>("OP_OGUI_COMM");
		ComChannel<Operator.OP_OGUI_DATA> OP_OGUI_DATA = new ComChannel<Operator.OP_OGUI_DATA>("OP_OGUI_DATA");
		ComChannel<Operator.OP_UAV_COMM> OP_UAV_COMM = new ComChannel<Operator.OP_UAV_COMM>("OP_UAV_COMM");
		ComChannel<Operator.OP_UAV_DATA> OP_UAV_DATA = new ComChannel<Operator.OP_UAV_DATA>("OP_UAV_DATA");
		
		//OGUI
		ComChannel<OperatorGui.OGUI_OP_DATA> OGUI_OP_DATA = new ComChannel<OperatorGui.OGUI_OP_DATA>("OGUI_OP_DATA");
		ComChannel<OperatorGui.OGUI_UAV_DATA> OGUI_UAV_DATA = new ComChannel<OperatorGui.OGUI_UAV_DATA>("OGUI_UAV_DATA");
		ComChannel<OperatorGui.OGUI_VGUI_DATA> OGUI_VGUI_DATA = new ComChannel<OperatorGui.OGUI_VGUI_DATA>("OGUI_VGUI_DATA");
		
		//VO
		ComChannel<VideoOperator.VO_MM_COMM> VO_MM_COMM = new ComChannel<VideoOperator.VO_MM_COMM>("VO_MM_COMM");
		ComChannel<VideoOperator.VO_MM_DATA> VO_MM_DATA = new ComChannel<VideoOperator.VO_MM_DATA>("VO_MM_DATA");
		ComChannel<VideoOperator.VO_VGUI_COMM> VO_VGUI_COMM = new ComChannel<VideoOperator.VO_VGUI_COMM>("VO_OGUI_COMM");
		ComChannel<VideoOperator.VO_VGUI_DATA> VO_VGUI_DATA = new ComChannel<VideoOperator.VO_VGUI_DATA>("VO_OGUI_DATA");
		ComChannel<VideoOperator.VO_OP_COMM> VO_OP_COMM = new ComChannel<VideoOperator.VO_OP_COMM>("VO_OP_COMM");
		ComChannel<VideoOperator.VO_OP_DATA> VO_OP_DATA = new ComChannel<VideoOperator.VO_OP_DATA>("VO_OP_DATA");
		
		//VGUI
		ComChannel<VideoOperatorGui.VGUI_MM_DATA> VGUI_MM_DATA = new ComChannel<VideoOperatorGui.VGUI_MM_DATA>("VGUI_MM_DATA");
		ComChannel<VideoOperatorGui.VGUI_VO_DATA> VGUI_VO_DATA = new ComChannel<VideoOperatorGui.VGUI_VO_DATA>("VGUI_VO_DATA");
		ComChannel<VideoOperatorGui.VGUI_OGUI_DATA> VGUI_OGUI_DATA = new ComChannel<VideoOperatorGui.VGUI_OGUI_DATA>("VGUI_OGUI_DATA");

		//UAV
		ComChannel<UAV.UAV_OP_DATA> UAV_OP_DATA = new ComChannel<UAV.UAV_OP_DATA>("UAV_OP_DATA");
		ComChannel<UAV.UAV_OGUI_DATA> UAV_OGUI_DATA = new ComChannel<UAV.UAV_OGUI_DATA>("UAV_OGUI_DATA");
		ComChannel<UAV.UAV_VGUI_DATA> UAV_VGUI_DATA = new ComChannel<UAV.UAV_VGUI_DATA>("UAV_VGUI_DATA");
		
		//add Parent Search, with its inputs and outputs, to the team 
		inputs.clear();
		inputs.add(NewSearchEvent);
		inputs.add(TerminateSearchEvent);
		inputs.add(MM_PS_COMM);
		inputs.add(MM_PS_DATA);
		
//		inputs.put(UDO.PS_TIME_TIL_START_PS.name(), UDO.PS_TIME_TIL_START_PS);
//		inputs.put(UDO.PS_NEW_SEARCH_AOI_PS.name(), UDO.PS_NEW_SEARCH_AOI_PS);
//		inputs.put(UDO.PS_TARGET_DESCRIPTION_PS.name(), UDO.PS_TARGET_DESCRIPTION_PS);
//		inputs.put(UDO.MM_ACK_PS.name(), UDO.MM_ACK_PS);
		outputs.clear();
		outputs.add(PS_MM_COMM);
		outputs.add(PS_MM_DATA);
//		outputs.put(UDO.PS_POKE_MM.name(), UDO.PS_POKE_MM);
//		outputs.put(UDO.PS_NEW_SEARCH_AOI_MM.name(), UDO.PS_NEW_SEARCH_AOI_MM);
//		outputs.put(UDO.PS_NEW_SEARCH_AOI_PS.name(), UDO.PS_NEW_SEARCH_AOI_PS);
//		outputs.put(UDO.PS_TARGET_DESCRIPTION_MM.name(), UDO.PS_TARGET_DESCRIPTION_MM);
//		outputs.put(UDO.PS_TARGET_DESCRIPTION_PS.name(), UDO.PS_TARGET_DESCRIPTION_PS);
//		outputs.put(UDO.PS_END_MM.name(), UDO.PS_END_MM);
		this.add(new ParentSearch(inputs, outputs));

		//add Mission Manager, with its inputs and outputs, to the team
		inputs.clear();
		inputs.add(PS_MM_COMM);
		inputs.add(PS_MM_DATA);
		inputs.add(VO_MM_COMM);
		inputs.add(VO_MM_DATA);
		inputs.add(OP_MM_COMM);
		inputs.add(OP_MM_DATA);
		inputs.add(VGUI_MM_DATA);
//		inputs.put(UDO.PS_POKE_MM.name(), UDO.PS_POKE_MM);
//		inputs.put(UDO.PS_NEW_SEARCH_AOI_MM.name(), UDO.PS_NEW_SEARCH_AOI_MM);
//		inputs.put(UDO.MM_NEW_SEARCH_AOI_MM.name(), UDO.MM_NEW_SEARCH_AOI_MM);
//		inputs.put(UDO.PS_TARGET_DESCRIPTION_MM.name(), UDO.PS_TARGET_DESCRIPTION_MM);
//		inputs.put(UDO.MM_TARGET_DESCRIPTION_MM.name(), UDO.MM_TARGET_DESCRIPTION_MM);
//		inputs.put(UDO.PS_END_MM.name(), UDO.PS_END_MM);
//		inputs.put(UDO.OP_ACK_MM.name(), UDO.OP_ACK_MM);
//		inputs.put(UDO.VO_ACK_MM.name(), UDO.VO_ACK_MM);
		outputs.clear();
		outputs.add(MM_PS_COMM);
		outputs.add(MM_PS_DATA);
		outputs.add(MM_OP_COMM);
		outputs.add(MM_OP_DATA);
		outputs.add(MM_VO_COMM);
		outputs.add(MM_VO_DATA);
		outputs.add(MM_VGUI_COMM);
		outputs.add(MM_VGUI_DATA);
//		outputs.put(UDO.MM_ACK_PS.name(),UDO.MM_ACK_PS);
//		outputs.put(UDO.MM_POKE_VO.name(), UDO.MM_POKE_VO);
//		outputs.put(UDO.MM_POKE_OP.name(), UDO.MM_POKE_OP);
//		outputs.put(UDO.MM_NEW_SEARCH_AOI_OP.name(), UDO.MM_NEW_SEARCH_AOI_OP);
//		outputs.put(UDO.MM_NEW_SEARCH_AOI_MM.name(), UDO.MM_NEW_SEARCH_AOI_MM);
//		outputs.put(UDO.MM_TARGET_DESCRIPTION_VO.name(), UDO.MM_TARGET_DESCRIPTION_VO);
//		outputs.put(UDO.MM_TARGET_DESCRIPTION_MM.name(), UDO.MM_TARGET_DESCRIPTION_MM);
//		outputs.put(UDO.MM_END_OP.name(), UDO.MM_END_OP);
//		outputs.put(UDO.MM_END_VO.name(), UDO.MM_END_VO);
		this.add(new MissionManager(inputs, outputs));
		
		//add UAV Operator, with its inputs and outputs, to the team
		inputs.clear();
		inputs.add(MM_OP_COMM);
		inputs.add(MM_OP_DATA);
		inputs.add(VO_OP_COMM);
		inputs.add(VO_OP_DATA);
		inputs.add(OGUI_OP_DATA);
		inputs.add(UAV_OP_DATA);
//		inputs.put(UDO.MM_POKE_OP.name(), UDO.MM_POKE_OP);
//		inputs.put(UDO.MM_NEW_SEARCH_AOI_OP.name(), UDO.MM_NEW_SEARCH_AOI_OP);
//		inputs.put(UDO.OP_NEW_SEARCH_AOI_OP.name(), UDO.OP_NEW_SEARCH_AOI_OP);
//		inputs.put(UDO.MM_END_OP.name(), UDO.MM_END_OP);
		outputs.clear();
		outputs.add(OP_UAV_COMM);
		outputs.add(OP_UAV_DATA);
		outputs.add(OP_OGUI_COMM);
		outputs.add(OP_OGUI_DATA);
		outputs.add(OP_MM_COMM);
		outputs.add(OP_MM_DATA);
//		outputs.put(UDO.OP_POKE_OGUI.name(), UDO.OP_POKE_OGUI);
//		outputs.put(UDO.OP_END_OGUI.name(), UDO.OP_END_OGUI);
//		outputs.put(UDO.OP_ACK_MM.name(), UDO.OP_ACK_MM);
//		outputs.put(UDO.OP_NEW_SEARCH_AOI_OP.name(), UDO.OP_NEW_SEARCH_AOI_OP);
//		outputs.put(UDO.OP_TAKE_OFF_OGUI.name(), UDO.OP_TAKE_OFF_OGUI);
		this.add(new Operator(inputs, outputs));

		//add UAV Operator Gui, with its inputs and outputs, to the team
		inputs.clear();
		inputs.add(UAV_OGUI_DATA);
		inputs.add(OP_OGUI_COMM);
		inputs.add(OP_OGUI_DATA);
		inputs.add(VGUI_OGUI_DATA);
//		inputs.put(UDO.OP_TAKE_OFF_OGUI.name(), UDO.OP_TAKE_OFF_OGUI);
		outputs.clear();
		outputs.add(OGUI_UAV_DATA);
		outputs.add(OGUI_OP_DATA);
		outputs.add(OGUI_VGUI_DATA);

//		outputs.put(UDO.OGUI_TAKE_OFF_UAV.name(), UDO.OGUI_TAKE_OFF_UAV);
		this.add(new OperatorGui(inputs, outputs));

		//add Video Operator, with its inputs and outputs, to the team
		inputs.clear();
		inputs.add(MM_VO_COMM);
		inputs.add(MM_VO_DATA);
		inputs.add(VGUI_VO_DATA);
//		inputs.put(UDO.MM_POKE_VO.name(), UDO.MM_POKE_VO);
//		inputs.put(UDO.MM_END_VO.name(), UDO.MM_END_VO);
//		inputs.put(UDO.MM_TARGET_DESCRIPTION_VO.name(), UDO.MM_TARGET_DESCRIPTION_VO);
//		inputs.put(UDO.VO_TARGET_DESCRIPTION_VO.name(), UDO.VO_TARGET_DESCRIPTION_VO);
		outputs.clear();
		outputs.add(VO_OP_COMM);
		outputs.add(VO_OP_DATA);
		outputs.add(VO_MM_COMM);
		outputs.add(VO_MM_DATA);
		outputs.add(VO_VGUI_COMM);
		outputs.add(VO_VGUI_DATA);
//		outputs.put(UDO.VO_ACK_MM.name(), UDO.VO_ACK_MM);
//		outputs.put(UDO.VO_TARGET_DESCRIPTION_VO.name(), UDO.VO_TARGET_DESCRIPTION_VO);
		this.add(new VideoOperator(inputs, outputs));

		//add Video Operator Gui, with its inputs and outputs, to the team
		inputs.clear();
		inputs.add(VO_VGUI_COMM);
		inputs.add(VO_VGUI_DATA);
		inputs.add(MM_VGUI_COMM);
		inputs.add(MM_VGUI_DATA);
		inputs.add(OGUI_VGUI_DATA);
		inputs.add(UAV_VGUI_DATA);
		
		outputs.clear();
		outputs.add(VGUI_MM_DATA);
		outputs.add(VGUI_VO_DATA);
		outputs.add(VGUI_OGUI_DATA);
		this.add(new VideoOperatorGui(inputs, outputs));

		//add UAV, with its inputs and outputs, to the team
		inputs.clear();
		inputs.add(OP_UAV_COMM);
		inputs.add(OP_UAV_DATA);
		inputs.add(OGUI_UAV_DATA);
//		inputs.put(UDO.OGUI_TAKE_OFF_UAV.name(), UDO.OGUI_TAKE_OFF_UAV);
		outputs.clear();
		outputs.add(UAV_VGUI_DATA);
		outputs.add(UAV_OGUI_DATA);
		outputs.add(UAV_OP_DATA);
//		outputs.put(UDO.UAV_BATTERY_LOW_OGUI.name(), UDO.UAV_BATTERY_LOW_OGUI);
		this.add(new UAV(inputs, outputs));
	}

}
