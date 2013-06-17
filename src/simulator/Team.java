package simulator;

import java.util.ArrayList;

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
	 * I have no idea what this means, but it fixed a compilation warning...
	 */
	private static final long serialVersionUID = 682275625652767731L;

	/**
	 * initialize all of the actors that will be used during the simulation
	 * assigns the actors the inputs and outputs they will be using (can this be moved inside the actor? -rob)
	 * @param outputs
	 */
	public Team() {
		ArrayList<UDO> inputs = new ArrayList<UDO>();
		ArrayList<UDO> outputs = new ArrayList<UDO>();
		
		this.add(new ParentSearch(outputs, inputs));
		
		//TODO actor_io and fill with MM inputs.add(UDO.MM.PS_POKE_MM);
		inputs.clear();
		outputs.clear();
		inputs.add(UDO.PS_POKE_MM);
		inputs.add(UDO.VO_POKE_MM);
		inputs.add(UDO.OP_POKE_MM);
		inputs.add(UDO.PS_END_MM);
		inputs.add(UDO.VO_END_MM);
		inputs.add(UDO.OP_END_MM);
		inputs.add(UDO.PS_TERMINATE_SEARCH_MM);
		inputs.add(UDO.PS_TARGET_DESCRIPTION_MM);
		inputs.add(UDO.PS_NEW_SEARCH_AOI);
		inputs.add(UDO.OP_SEARCH_AOI_COMPLETE_MM);
		inputs.add(UDO.VO_TARGET_SIGHTING_T_MM);
		inputs.add(UDO.VO_TARGET_SIGHTING_F_MM);
		inputs.add(UDO.PS_BUSY_MM);
		inputs.add(UDO.OP_BUSY_MM);
		inputs.add(UDO.VO_BUSY_MM);
		inputs.add(UDO.VGUI_VALIDATION_REQ_T_MM);
		inputs.add(UDO.VGUI_VALIDATION_REQ_F_MM);
		outputs.add(UDO.MM_POKE_PS);
		outputs.add(UDO.MM_POKE_VO);
		outputs.add(UDO.MM_POKE_OP);
		outputs.add(UDO.MM_POKE_VGUI);
		outputs.add(UDO.MM_ACK_PS);
		outputs.add(UDO.MM_ACK_VO);
		outputs.add(UDO.MM_ACK_OP);
		outputs.add(UDO.MM_END_PS);
		outputs.add(UDO.MM_END_VO);
		outputs.add(UDO.MM_END_OP);
		outputs.add(UDO.MM_END_VGUI);
		outputs.add(UDO.MM_TARGET_DESCRIPTION);
		outputs.add(UDO.MM_TERMINATE_SEARCH_VO);
		outputs.add(UDO.MM_NEW_SEARCH_AOI);
		outputs.add(UDO.MM_TERMINATE_SEARCH_OP);
		outputs.add(UDO.MM_SEARCH_AOI_COMPLETE);
		outputs.add(UDO.MM_SEARCH_FAILED);
		outputs.add(UDO.MM_TARGET_SIGHTING_F);
		outputs.add(UDO.MM_TARGET_SIGHTING_T);
		outputs.add(UDO.MM_FLYBY_REQ_T);
		outputs.add(UDO.MM_FLYBY_REQ_F);
		outputs.add(UDO.MM_ANOMALY_DISMISSED_T);
		outputs.add(UDO.MM_ANOMALY_DISMISSED_F);
		outputs.add(UDO.MM_BUSY_PS);
		outputs.add(UDO.MM_BUSY_OP);
		outputs.add(UDO.MM_BUSY_VO);
		this.add(new MissionManager(inputs, outputs));
		
		//TODO actor_io and fill with OP 
		inputs.clear();
		outputs.clear();
		this.add(new Operator(outputs));
		
		//TODO actor_io and fill with OGUI
		inputs.clear();
		outputs.clear();
		this.add(new OperatorGui(outputs));
		
		//TODO actor_io and fill with VO
		inputs.clear();
		outputs.clear();
		this.add(new VideoOperator(outputs));
		
		//TODO actor_io and fill with VGUI
		inputs.clear();
		outputs.clear();
		this.add(new VideoOperatorGui(outputs));
		
		//TODO actor_io and fill with UAV
		inputs.clear();
		outputs.clear();
		this.add(new UAV(outputs));
		
	}

}
