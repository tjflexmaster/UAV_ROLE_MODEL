package team;

import java.util.ArrayList;
import java.util.HashMap;

import simulator.*;
import actors.*;

/**
 * 
 * @author rob.ivie
 *
 * list all of the actors in this method
 * we may be able to use another class instead of this method
 */
public class Team extends ArrayList<Actor> {
	
	/**
	 * i have no idea what this means, but it fixed a compilation warning...
	 */
	private static final long serialVersionUID = 682275625652767731L;

	/**
	 * initialize all of the actors that will be used during the simulation
	 * assigns the actors the inputs and outputs they will be using (can this be moved inside the actor? -rob)
	 * @param outputs
	 */
	public Team() {
		//initialize inputs and outputs
		HashMap<String,UDO> inputs = new HashMap<String,UDO>();
		HashMap<String,UDO> outputs = new HashMap<String,UDO>();

		//add Parent Search, with its inputs and outputs, to the team 
		inputs.clear();
		inputs.put(UDO.PS_TIME_TIL_START_PS.name(), UDO.PS_TIME_TIL_START_PS);
		inputs.put(UDO.PS_NEW_SEARCH_AOI_PS.name(), UDO.PS_NEW_SEARCH_AOI_PS);
		inputs.put(UDO.PS_TARGET_DESCRIPTION_PS.name(), UDO.PS_TARGET_DESCRIPTION_PS);
		inputs.put(UDO.MM_ACK_PS.name(), UDO.MM_ACK_PS);
		outputs.clear();
		outputs.put(UDO.PS_POKE_MM.name(), UDO.PS_POKE_MM);
		outputs.put(UDO.PS_NEW_SEARCH_AOI_MM.name(), UDO.PS_NEW_SEARCH_AOI_MM);
		outputs.put(UDO.PS_NEW_SEARCH_AOI_PS.name(), UDO.PS_NEW_SEARCH_AOI_PS);
		outputs.put(UDO.PS_TARGET_DESCRIPTION_MM.name(), UDO.PS_TARGET_DESCRIPTION_MM);
		outputs.put(UDO.PS_TARGET_DESCRIPTION_PS.name(), UDO.PS_TARGET_DESCRIPTION_PS);
		outputs.put(UDO.PS_END_MM.name(), UDO.PS_END_MM);
		this.add(new ParentSearch(inputs, outputs));

		//add Mission Manager, with its inputs and outputs, to the team
		inputs.clear();
		inputs.put(UDO.PS_POKE_MM.name(), UDO.PS_POKE_MM);
		inputs.put(UDO.PS_NEW_SEARCH_AOI_MM.name(), UDO.PS_NEW_SEARCH_AOI_MM);
		inputs.put(UDO.MM_NEW_SEARCH_AOI_MM.name(), UDO.MM_NEW_SEARCH_AOI_MM);
		inputs.put(UDO.PS_TARGET_DESCRIPTION_MM.name(), UDO.PS_TARGET_DESCRIPTION_MM);
		inputs.put(UDO.MM_TARGET_DESCRIPTION_MM.name(), UDO.MM_TARGET_DESCRIPTION_MM);
		inputs.put(UDO.PS_END_MM.name(), UDO.PS_END_MM);
		inputs.put(UDO.OP_ACK_MM.name(), UDO.OP_ACK_MM);
		inputs.put(UDO.VO_ACK_MM.name(), UDO.VO_ACK_MM);
		outputs.clear();
		outputs.put(UDO.MM_ACK_PS.name(),UDO.MM_ACK_PS);
		outputs.put(UDO.MM_POKE_VO.name(), UDO.MM_POKE_VO);
		outputs.put(UDO.MM_POKE_OP.name(), UDO.MM_POKE_OP);
		outputs.put(UDO.MM_NEW_SEARCH_AOI_OP.name(), UDO.MM_NEW_SEARCH_AOI_OP);
		outputs.put(UDO.MM_NEW_SEARCH_AOI_MM.name(), UDO.MM_NEW_SEARCH_AOI_MM);
		outputs.put(UDO.MM_TARGET_DESCRIPTION_VO.name(), UDO.MM_TARGET_DESCRIPTION_VO);
		outputs.put(UDO.MM_TARGET_DESCRIPTION_MM.name(), UDO.MM_TARGET_DESCRIPTION_MM);
		outputs.put(UDO.MM_END_OP.name(), UDO.MM_END_OP);
		outputs.put(UDO.MM_END_VO.name(), UDO.MM_END_VO);
		this.add(new MissionManager(inputs, outputs));
		
		//add UAV Operator, with its inputs and outputs, to the team
		inputs.clear();
		inputs.put(UDO.MM_POKE_OP.name(), UDO.MM_POKE_OP);
		inputs.put(UDO.MM_NEW_SEARCH_AOI_OP.name(), UDO.MM_NEW_SEARCH_AOI_OP);
		inputs.put(UDO.OP_NEW_SEARCH_AOI_OP.name(), UDO.OP_NEW_SEARCH_AOI_OP);
		inputs.put(UDO.MM_END_OP.name(), UDO.MM_END_OP);
		outputs.clear();
		outputs.put(UDO.OP_ACK_MM.name(), UDO.OP_ACK_MM);
		outputs.put(UDO.OP_NEW_SEARCH_AOI_OP.name(), UDO.OP_NEW_SEARCH_AOI_OP);
		outputs.put(UDO.OP_TAKE_OFF_OGUI.name(), UDO.OP_TAKE_OFF_OGUI);
		this.add(new Operator(inputs, outputs));

		//add UAV Operator Gui, with its inputs and outputs, to the team
		inputs.clear();
		inputs.put(UDO.OP_TAKE_OFF_OGUI.name(), UDO.OP_TAKE_OFF_OGUI);
		outputs.clear();
		outputs.put(UDO.OGUI_TAKE_OFF_UAV.name(), UDO.OGUI_TAKE_OFF_UAV);
		this.add(new OperatorGui(inputs, outputs));

		//add Video Operator, with its inputs and outputs, to the team
		inputs.clear();
		inputs.put(UDO.MM_POKE_VO.name(), UDO.MM_POKE_VO);
		inputs.put(UDO.MM_END_VO.name(), UDO.MM_END_VO);
		inputs.put(UDO.MM_TARGET_DESCRIPTION_VO.name(), UDO.MM_TARGET_DESCRIPTION_VO);
		inputs.put(UDO.VO_TARGET_DESCRIPTION_VO.name(), UDO.VO_TARGET_DESCRIPTION_VO);
		outputs.clear();
		outputs.put(UDO.VO_ACK_MM.name(), UDO.VO_ACK_MM);
		outputs.put(UDO.VO_TARGET_DESCRIPTION_VO.name(), UDO.VO_TARGET_DESCRIPTION_VO);
		this.add(new VideoOperator(inputs, outputs));

		//add Video Operator Gui, with its inputs and outputs, to the team
		inputs.clear();
		outputs.clear();
		this.add(new VideoOperatorGui(inputs, outputs));

		//add UAV, with its inputs and outputs, to the team
		inputs.clear();
		inputs.put(UDO.OGUI_TAKE_OFF_UAV.name(), UDO.OGUI_TAKE_OFF_UAV);
		outputs.clear();
		this.add(new UAV(inputs, outputs));
	}

}
