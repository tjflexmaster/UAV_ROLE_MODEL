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
	 * I have no idea what this means, but it fixed a compilation warning...
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
		
		//add Event Manager, with its inputs and outputs, to the team
		inputs.clear();
		outputs.clear();
		this.add(new EventManager(inputs, outputs));

		//add Parent Search, with its inputs and outputs, to the team 
		inputs.clear();
		outputs.clear();
		this.add(new ParentSearch(inputs, outputs));

		//add Mission Manager, with its inputs and outputs, to the team
		inputs.clear();
		inputs.put(UDO.PS_POKE_MM.name(),UDO.PS_POKE_MM);
		inputs.put(UDO.VO_POKE_MM.name(),UDO.VO_POKE_MM);
		inputs.put(UDO.OP_POKE_MM.name(),UDO.OP_POKE_MM);
		inputs.put(UDO.PS_END_MM.name(),UDO.PS_END_MM);
		inputs.put(UDO.VO_END_MM.name(),UDO.VO_END_MM);
		inputs.put(UDO.OP_END_MM.name(),UDO.OP_END_MM);
		inputs.put(UDO.PS_TERMINATE_SEARCH_MM.name(),UDO.PS_TERMINATE_SEARCH_MM);
		inputs.put(UDO.PS_TARGET_DESCRIPTION_MM.name(),UDO.PS_TARGET_DESCRIPTION_MM);
		inputs.put(UDO.PS_NEW_SEARCH_AOI.name(),UDO.PS_NEW_SEARCH_AOI);
		inputs.put(UDO.OP_SEARCH_AOI_COMPLETE_MM.name(),UDO.OP_SEARCH_AOI_COMPLETE_MM);
		inputs.put(UDO.VO_TARGET_SIGHTING_T_MM.name(),UDO.VO_TARGET_SIGHTING_T_MM);
		inputs.put(UDO.VO_TARGET_SIGHTING_F_MM.name(),UDO.VO_TARGET_SIGHTING_F_MM);
		inputs.put(UDO.PS_BUSY_MM.name(),UDO.PS_BUSY_MM);
		inputs.put(UDO.OP_BUSY_MM.name(),UDO.OP_BUSY_MM);
		inputs.put(UDO.VO_BUSY_MM.name(),UDO.VO_BUSY_MM);
		inputs.put(UDO.VGUI_VALIDATION_REQ_T_MM.name(),UDO.VGUI_VALIDATION_REQ_T_MM);
		inputs.put(UDO.VGUI_VALIDATION_REQ_F_MM.name(),UDO.VGUI_VALIDATION_REQ_F_MM);
		outputs.clear();
		outputs.put(UDO.MM_POKE_PS.name(),UDO.MM_POKE_PS);
		outputs.put(UDO.MM_POKE_VO.name(),UDO.MM_POKE_VO);
		outputs.put(UDO.MM_POKE_OP.name(),UDO.MM_POKE_OP);
		outputs.put(UDO.MM_POKE_VGUI.name(),UDO.MM_POKE_VGUI);
		outputs.put(UDO.MM_ACK_PS.name(),UDO.MM_ACK_PS);
		outputs.put(UDO.MM_ACK_VO.name(),UDO.MM_ACK_VO);
		outputs.put(UDO.MM_ACK_OP.name(),UDO.MM_ACK_OP);
		outputs.put(UDO.MM_END_PS.name(),UDO.MM_END_PS);
		outputs.put(UDO.MM_END_VO.name(),UDO.MM_END_VO);
		outputs.put(UDO.MM_END_OP.name(),UDO.MM_END_OP);
		outputs.put(UDO.MM_END_VGUI.name(),UDO.MM_END_VGUI);
		outputs.put(UDO.MM_TARGET_DESCRIPTION_VO.name(),UDO.MM_TARGET_DESCRIPTION_VO);
		outputs.put(UDO.MM_TERMINATE_SEARCH_VO.name(),UDO.MM_TERMINATE_SEARCH_VO);
		outputs.put(UDO.MM_NEW_SEARCH_AOI_OP.name(),UDO.MM_NEW_SEARCH_AOI_OP);
		outputs.put(UDO.MM_TERMINATE_SEARCH_OP.name(),UDO.MM_TERMINATE_SEARCH_OP);
		outputs.put(UDO.MM_SEARCH_AOI_COMPLETE.name(),UDO.MM_SEARCH_AOI_COMPLETE);
		outputs.put(UDO.MM_SEARCH_FAILED.name(),UDO.MM_SEARCH_FAILED);
		outputs.put(UDO.MM_TARGET_SIGHTING_F.name(),UDO.MM_TARGET_SIGHTING_F);
		outputs.put(UDO.MM_TARGET_SIGHTING_T.name(),UDO.MM_TARGET_SIGHTING_T);
		outputs.put(UDO.MM_FLYBY_REQ_F_VGUI.name(),UDO.MM_FLYBY_REQ_F_VGUI);
		outputs.put(UDO.MM_FLYBY_REQ_T_VGUI.name(),UDO.MM_FLYBY_REQ_T_VGUI);
		outputs.put(UDO.MM_ANOMALY_DISMISSED_T.name(),UDO.MM_ANOMALY_DISMISSED_T);
		outputs.put(UDO.MM_ANOMALY_DISMISSED_F.name(),UDO.MM_ANOMALY_DISMISSED_F);
		outputs.put(UDO.MM_BUSY_PS.name(),UDO.MM_BUSY_PS);
		outputs.put(UDO.MM_BUSY_OP.name(),UDO.MM_BUSY_OP);
		outputs.put(UDO.MM_BUSY_VO.name(),UDO.MM_BUSY_VO);
		this.add(new MissionManager(inputs, outputs));
		
		//add UAV Operator, with its inputs and outputs, to the team
		inputs.clear();
		inputs.put(UDO.MM_POKE_OP.name(),UDO.MM_POKE_OP);
		inputs.put(UDO.VO_POKE_OP.name(),UDO.VO_POKE_OP);
		inputs.put(UDO.UAV_FLYING_NORMAL.name(),UDO.UAV_FLYING_NORMAL);
		inputs.put(UDO.UAV_FLYING_FLYBY.name(),UDO.UAV_FLYING_FLYBY);
		inputs.put(UDO.UAV_LOITERING.name(),UDO.UAV_LOITERING);
		inputs.put(UDO.UAV_LANDING.name(),UDO.UAV_LANDING);
		inputs.put(UDO.UAV_TAKE_OFF.name(),UDO.UAV_TAKE_OFF);
		inputs.put(UDO.UAV_LANDED.name(),UDO.UAV_LANDED);
		inputs.put(UDO.MM_ACK_OP.name(),UDO.MM_ACK_OP);
		inputs.put(UDO.MM_BUSY_OP.name(),UDO.MM_BUSY_OP);
		inputs.put(UDO.MM_END_OP.name(),UDO.MM_END_OP);
		inputs.put(UDO.MM_NEW_SEARCH_AOI_OP.name(),UDO.MM_NEW_SEARCH_AOI_OP);
		inputs.put(UDO.MM_TERMINATE_SEARCH_OP.name(),UDO.MM_TERMINATE_SEARCH_OP);
		inputs.put(UDO.VO_END_OP.name(),UDO.VO_END_OP);
		//inputs.put(UDO.HAG_LOW.name(),);
		//inputs.put(UDO.BATTERY_LOW.name(),);
		//inputs.put(UDO.SIGNAL_LOST.name(),);
		inputs.put(UDO.VO_FLYBY_END_FAILED_VGUI.name(),UDO.VO_FLYBY_END_FAILED_VGUI);
		inputs.put(UDO.VO_FLYBY_END_SUCCESS_VGUI.name(),UDO.VO_FLYBY_END_SUCCESS_VGUI);
		inputs.put(UDO.OGUI_STATE_NORMAL.name(),UDO.OGUI_STATE_NORMAL);
		inputs.put(UDO.UAV_READY.name(),UDO.UAV_READY);
		inputs.put(UDO.UAV_FLIGHT_PLAN_YES_OGUI.name(),UDO.UAV_FLIGHT_PLAN_YES_OGUI);
		inputs.put(UDO.UAV_FLIGHT_PLAN_COMPLETE.name(),UDO.UAV_FLIGHT_PLAN_COMPLETE);
		inputs.put(UDO.VO_FLYBY_REQ_T_VGUI.name(),UDO.VO_FLYBY_REQ_T_VGUI);
		inputs.put(UDO.VO_FLYBY_REQ_T_VGUI.name(),UDO.VO_FLYBY_REQ_T_VGUI);
		inputs.put(UDO.MM_FLYBY_REQ_T_VGUI.name(),UDO.MM_FLYBY_REQ_T_VGUI);
		inputs.put(UDO.MM_FLYBY_REQ_T_VGUI.name(),UDO.MM_FLYBY_REQ_T_VGUI);
		inputs.put(UDO.OGUI_STATE_ALARM.name(),UDO.OGUI_STATE_ALARM);
		outputs.clear();
		outputs.put(UDO.OP_POKE_MM.name(),UDO.OP_POKE_MM);
		outputs.put(UDO.OP_END_MM.name(),UDO.OP_END_MM);
		outputs.put(UDO.OP_POKE_OGUI.name(),UDO.OP_POKE_OGUI);
		outputs.put(UDO.OP_END_OGUI.name(),UDO.OP_END_OGUI);
		outputs.put(UDO.OP_ACK_MM.name(),UDO.OP_ACK_MM);
		outputs.put(UDO.OP_ACK_VO.name(),UDO.OP_ACK_VO);
		outputs.put(UDO.OP_BUSY_MM.name(),UDO.OP_BUSY_MM);
		outputs.put(UDO.OP_BUSY_VO.name(),UDO.OP_BUSY_VO);
		outputs.put(UDO.OP_NEW_FLIGHT_PLAN_OGUI.name(),UDO.OP_NEW_FLIGHT_PLAN_OGUI);
		outputs.put(UDO.OP_LAND_OGUI.name(),UDO.OP_LAND_OGUI);
		outputs.put(UDO.OP_LOITER_OGUI.name(),UDO.OP_LOITER_OGUI);
		outputs.put(UDO.OP_RESUME_OGUI.name(),UDO.OP_RESUME_OGUI);
		outputs.put(UDO.OP_SEARCH_AOI_COMPLETE_MM.name(),UDO.OP_SEARCH_AOI_COMPLETE_MM);
		outputs.put(UDO.OP_POST_FLIGHT_COMPLETE_UAV.name(),UDO.OP_POST_FLIGHT_COMPLETE_UAV);
		outputs.put(UDO.OP_TAKE_OFF_UAV.name(),UDO.OP_TAKE_OFF_UAV);
		this.add(new Operator(inputs, outputs));

		//add UAV Operator Gui, with its inputs and outputs, to the team
		inputs.clear();
		inputs.put(UDO.UAV_BATTERY_DEAD_OGUI.name(),UDO.UAV_BATTERY_DEAD_OGUI);
		inputs.put(UDO.UAV_BATTERY_LOW_OGUI.name(),UDO.UAV_BATTERY_LOW_OGUI);
		inputs.put(UDO.UAV_BATTERY_OFF_OGUI.name(),UDO.UAV_BATTERY_OFF_OGUI);
		inputs.put(UDO.UAV_BATTERY_OK_OGUI.name(),UDO.UAV_BATTERY_OK_OGUI);
		inputs.put(UDO.UAV_FLIGHT_PLAN_NO_OGUI.name(),UDO.UAV_FLIGHT_PLAN_NO_OGUI);
		inputs.put(UDO.UAV_FLIGHT_PLAN_YES_OGUI.name(),UDO.UAV_FLIGHT_PLAN_YES_OGUI);
		inputs.put(UDO.UAV_FLIGHT_PLAN_PAUSED_OGUI.name(),UDO.UAV_FLIGHT_PLAN_PAUSED_OGUI);
		inputs.put(UDO.UAV_FLIGHT_PLAN_COMPLETE_OGUI.name(),UDO.UAV_FLIGHT_PLAN_COMPLETE_OGUI);
		inputs.put(UDO.UAV_HAG_LOW_OGUI.name(),UDO.UAV_HAG_LOW_OGUI);
		inputs.put(UDO.UAV_HAG_NONE_OGUI.name(),UDO.UAV_HAG_NONE_OGUI);
		inputs.put(UDO.UAV_HAG_GOOD_OGUI.name(),UDO.UAV_HAG_GOOD_OGUI);
		inputs.put(UDO.UAV_HAG_CRASHED_OGUI.name(),UDO.UAV_HAG_CRASHED_OGUI);
		inputs.put(UDO.UAV_SIGNAL_NONE_OGUI.name(),UDO.UAV_SIGNAL_NONE_OGUI);
		inputs.put(UDO.UAV_SIGNAL_LOST_OGUI.name(),UDO.UAV_SIGNAL_LOST_OGUI);
		inputs.put(UDO.UAV_SIGNAL_OK_OGUI.name(),UDO.UAV_SIGNAL_OK_OGUI);
		inputs.put(UDO.UAV_SIGNAL_RESUMED_OGUI.name(),UDO.UAV_SIGNAL_RESUMED_OGUI);
		inputs.put(UDO.UAV_FLYING_NORMAL.name(),UDO.UAV_FLYING_NORMAL);
		inputs.put(UDO.UAV_FLYING_FLYBY.name(),UDO.UAV_FLYING_FLYBY);
		inputs.put(UDO.UAV_LOITERING.name(),UDO.UAV_LOITERING);
		inputs.put(UDO.UAV_LANDING.name(),UDO.UAV_LANDING);
		inputs.put(UDO.UAV_TAKE_OFF.name(),UDO.UAV_TAKE_OFF);
		inputs.put(UDO.UAV_LANDED.name(),UDO.UAV_LANDED);
		inputs.put(UDO.UAV_READY.name(),UDO.UAV_READY);
		inputs.put(UDO.OP_FLYBY_START_F_OGUI.name(),UDO.OP_FLYBY_START_F_OGUI);
		inputs.put(UDO.OP_FLYBY_START_T_OGUI.name(),UDO.OP_FLYBY_START_T_OGUI);
		inputs.put(UDO.OP_FLYBY_END_OGUI.name(),UDO.OP_FLYBY_END_OGUI);
		inputs.put(UDO.OP_LAND_OGUI.name(),UDO.OP_LAND_OGUI);
		inputs.put(UDO.OP_LOITER_OGUI.name(),UDO.OP_LOITER_OGUI);
		inputs.put(UDO.OP_MODIFY_FLIGHT_PLAN_OGUI.name(),UDO.OP_MODIFY_FLIGHT_PLAN_OGUI);
		inputs.put(UDO.OP_NEW_FLIGHT_PLAN_OGUI.name(),UDO.OP_NEW_FLIGHT_PLAN_OGUI);
		inputs.put(UDO.OP_RESUME_OGUI.name(),UDO.OP_RESUME_OGUI);
		inputs.put(UDO.OP_TAKE_OFF_OGUI.name(),UDO.OP_TAKE_OFF_OGUI);
		inputs.put(UDO.VO_FLYBY_REQ_T_OGUI.name(),UDO.VO_FLYBY_REQ_T_OGUI);
		inputs.put(UDO.VO_FLYBY_REQ_F_OGUI.name(),UDO.VO_FLYBY_REQ_F_OGUI);
		inputs.put(UDO.VO_FLYBY_END_SUCCESS_OGUI.name(),UDO.VO_FLYBY_END_SUCCESS_OGUI);
		inputs.put(UDO.VO_FLYBY_END_FAILED_OGUI.name(),UDO.VO_FLYBY_END_FAILED_OGUI);
		inputs.put(UDO.MM_FLYBY_REQ_F_OGUI.name(),UDO.MM_FLYBY_REQ_F_OGUI);
		inputs.put(UDO.MM_FLYBY_REQ_T_OGUI.name(),UDO.MM_FLYBY_REQ_T_OGUI);
		outputs.clear();
		outputs.put(UDO.OGUI_STATE_NORMAL.name(),UDO.OGUI_STATE_NORMAL);
		outputs.put(UDO.OGUI_STATE_ALARM.name(),UDO.OGUI_STATE_ALARM);
		outputs.put(UDO.OGUI_FLYBY_START_F_VO.name(),UDO.OGUI_FLYBY_START_F_VO);
		outputs.put(UDO.OGUI_FLYBY_START_T_VO.name(),UDO.OGUI_FLYBY_START_T_VO);
		outputs.put(UDO.OP_FLYBY_END_UAV.name(),UDO.OP_FLYBY_END_UAV);
		outputs.put(UDO.OP_LAND_UAV.name(),UDO.OP_LAND_UAV);
		outputs.put(UDO.OP_LOITER_UAV.name(),UDO.OP_LOITER_UAV);
		outputs.put(UDO.OGUI_MODIFY_FLIGHT_PLAN_UAV.name(),UDO.OGUI_MODIFY_FLIGHT_PLAN_UAV);
		outputs.put(UDO.OGUI_NEW_FLIGHT_PLAN_UAV.name(),UDO.OGUI_NEW_FLIGHT_PLAN_UAV);
		outputs.put(UDO.OGUI_RESUME_UAV.name(),UDO.OGUI_RESUME_UAV);
		outputs.put(UDO.OGUI_TAKE_OFF_UAV.name(),UDO.OGUI_TAKE_OFF_UAV);
		outputs.put(UDO.OGUI_UAV_BATTERY_DEAD_OP.name(),UDO.OGUI_UAV_BATTERY_DEAD_OP);
		outputs.put(UDO.OGUI_UAV_BATTERY_LOW_OP.name(),UDO.OGUI_UAV_BATTERY_LOW_OP);
		outputs.put(UDO.OGUI_UAV_BATTERY_OFF_OP.name(),UDO.OGUI_UAV_BATTERY_OFF_OP);
		outputs.put(UDO.OGUI_UAV_BATTERY_OK_OP.name(),UDO.OGUI_UAV_BATTERY_OK_OP);
		outputs.put(UDO.OGUI_UAV_FLIGHT_PLAN_NO_OP.name(),UDO.OGUI_UAV_FLIGHT_PLAN_NO_OP);
		outputs.put(UDO.OGUI_UAV_FLIGHT_PLAN_YES_OP.name(),UDO.OGUI_UAV_FLIGHT_PLAN_YES_OP);
		outputs.put(UDO.OGUI_UAV_FLIGHT_PLAN_PAUSED_OP.name(),UDO.OGUI_UAV_FLIGHT_PLAN_PAUSED_OP);
		outputs.put(UDO.OGUI_UAV_FLIGHT_PLAN_COMPLETE_OP.name(),UDO.OGUI_UAV_FLIGHT_PLAN_COMPLETE_OP);
		outputs.put(UDO.OGUI_UAV_HAG_LOW_OP.name(),UDO.OGUI_UAV_HAG_LOW_OP);
		outputs.put(UDO.OGUI_UAV_HAG_NONE_OP.name(),UDO.OGUI_UAV_HAG_NONE_OP);
		outputs.put(UDO.OGUI_UAV_HAG_GOOD_OP.name(),UDO.OGUI_UAV_HAG_GOOD_OP);
		outputs.put(UDO.OGUI_UAV_HAG_CRASHED_OP.name(),UDO.OGUI_UAV_HAG_CRASHED_OP);
		outputs.put(UDO.OGUI_UAV_SIGNAL_NONE_OP.name(),UDO.OGUI_UAV_SIGNAL_NONE_OP);
		outputs.put(UDO.OGUI_UAV_SIGNAL_LOST_OP.name(),UDO.OGUI_UAV_SIGNAL_LOST_OP);
		outputs.put(UDO.OGUI_UAV_SIGNAL_OK_OP.name(),UDO.OGUI_UAV_SIGNAL_OK_OP);
		outputs.put(UDO.OGUI_UAV_SIGNAL_RESUMED_OP.name(),UDO.OGUI_UAV_SIGNAL_RESUMED_OP);
		outputs.put(UDO.OGUI_UAV_FLYING_NORMAL_OP.name(),UDO.OGUI_UAV_FLYING_NORMAL_OP);
		outputs.put(UDO.OGUI_UAV_FLYING_FLYBY_OP.name(),UDO.OGUI_UAV_FLYING_FLYBY_OP);
		outputs.put(UDO.OGUI_UAV_LOITERING_OP.name(),UDO.OGUI_UAV_LOITERING_OP);
		outputs.put(UDO.OGUI_UAV_LANDING_OP.name(),UDO.OGUI_UAV_LANDING_OP);
		outputs.put(UDO.OGUI_UAV_TAKE_OFF_OP.name(),UDO.OGUI_UAV_TAKE_OFF_OP);
		outputs.put(UDO.OGUI_UAV_LANDED_OP.name(),UDO.OGUI_UAV_LANDED_OP);
		outputs.put(UDO.OGUI_UAV_READY_OP.name(),UDO.OGUI_UAV_READY_OP);
		outputs.put(UDO.OGUI_FLYBY_START_F_UAV.name(),UDO.OGUI_FLYBY_START_F_UAV);
		outputs.put(UDO.OGUI_FLYBY_START_T_UAV.name(),UDO.OGUI_FLYBY_START_T_UAV);
		outputs.put(UDO.OGUI_FLYBY_END_UAV.name(),UDO.OGUI_FLYBY_END_UAV);
		outputs.put(UDO.OGUI_LAND_UAV.name(),UDO.OGUI_LAND_UAV);
		outputs.put(UDO.OGUI_LOITER_UAV.name(),UDO.OGUI_LOITER_UAV);
		outputs.put(UDO.OGUI_MODIFY_FLIGHT_PLAN_UAV.name(),UDO.OGUI_MODIFY_FLIGHT_PLAN_UAV);
		outputs.put(UDO.OGUI_NEW_FLIGHT_PLAN_UAV.name(),UDO.OGUI_NEW_FLIGHT_PLAN_UAV);
		outputs.put(UDO.OGUI_RESUME_UAV.name(),UDO.OGUI_RESUME_UAV);
		outputs.put(UDO.OGUI_TAKE_OFF_UAV.name(),UDO.OGUI_TAKE_OFF_UAV);
		outputs.put(UDO.OGUI_FLYBY_REQ_T_OP.name(),UDO.OGUI_FLYBY_REQ_T_OP);
		outputs.put(UDO.OGUI_FLYBY_REQ_F_OP.name(),UDO.OGUI_FLYBY_REQ_F_OP);
		outputs.put(UDO.OGUI_FLYBY_END_SUCCESS_OP.name(),UDO.OGUI_FLYBY_END_SUCCESS_OP);
		outputs.put(UDO.OGUI_FLYBY_END_FAILED_OP.name(),UDO.OGUI_FLYBY_END_FAILED_OP);
		outputs.put(UDO.OGUI_FLYBY_REQ_F_OP.name(),UDO.OGUI_FLYBY_REQ_F_OP);
		outputs.put(UDO.OGUI_FLYBY_REQ_T_OP.name(),UDO.OGUI_FLYBY_REQ_T_OP);
		this.add(new OperatorGui(inputs, outputs));

		//add Video Operator, with its inputs and outputs, to the team
		inputs.clear();
		inputs.put(UDO.MM_POKE_VO.name(),UDO.MM_POKE_VO);
		inputs.put(UDO.MM_ACK_VO.name(),UDO.MM_ACK_VO);
		inputs.put(UDO.MM_BUSY_VO.name(),UDO.MM_BUSY_VO);
		inputs.put(UDO.MM_END_VO.name(),UDO.MM_END_VO);
		inputs.put(UDO.MM_TARGET_DESCRIPTION_VO.name(),UDO.MM_TARGET_DESCRIPTION_VO);
		inputs.put(UDO.MM_TERMINATE_SEARCH_VO.name(),UDO.MM_TERMINATE_SEARCH_VO);
		inputs.put(UDO.OP_ACK_VO.name(),UDO.OP_ACK_VO);
		inputs.put(UDO.OP_BUSY_VO.name(),UDO.OP_BUSY_VO);
		inputs.put(UDO.VO_SIGNAL_BAD_VO.name(),UDO.VO_SIGNAL_BAD_VO);
		inputs.put(UDO.VGUI_FALSE_POSITIVE_VO.name(),UDO.VGUI_FALSE_POSITIVE_VO);
		inputs.put(UDO.VGUI_TRUE_POSITIVE_VO.name(),UDO.VGUI_TRUE_POSITIVE_VO);
		inputs.put(UDO.VO_FLYBY_ANOMALY_F_VO.name(),UDO.VO_FLYBY_ANOMALY_F_VO);
		inputs.put(UDO.VO_FLYBY_ANOMALY_T_VO.name(),UDO.VO_FLYBY_ANOMALY_T_VO);
		outputs.clear();
		outputs.put(UDO.VO_POKE_MM.name(),UDO.VO_POKE_MM);
		outputs.put(UDO.VO_END_MM.name(),UDO.VO_END_MM);
		outputs.put(UDO.VO_TARGET_SIGHTING_T_MM.name(),UDO.VO_TARGET_SIGHTING_T_MM);
		outputs.put(UDO.VO_TARGET_SIGHTING_F_MM.name(),UDO.VO_TARGET_SIGHTING_F_MM);
		outputs.put(UDO.VO_POKE_OP.name(),UDO.VO_POKE_OP);
		outputs.put(UDO.VO_END_OP.name(),UDO.VO_END_OP);
		outputs.put(UDO.VO_BAD_STREAM_OP.name(),UDO.VO_BAD_STREAM_OP);
		outputs.put(UDO.VO_POKE_VGUI.name(),UDO.VO_POKE_VGUI);
		outputs.put(UDO.VO_END_VGUI.name(),UDO.VO_END_VGUI);
		outputs.put(UDO.VO_FLYBY_REQ_T_VGUI.name(),UDO.VO_FLYBY_REQ_T_VGUI);
		outputs.put(UDO.VO_FLYBY_REQ_F_VGUI.name(),UDO.VO_FLYBY_REQ_F_VGUI);
		outputs.put(UDO.VO_FLYBY_END_SUCCESS_VGUI.name(),UDO.VO_FLYBY_END_SUCCESS_VGUI);
		outputs.put(UDO.VO_FLYBY_END_FAILED_VGUI.name(),UDO.VO_FLYBY_END_FAILED_VGUI);
		outputs.put(UDO.VO_POSSIBLE_ANOMALY_DETECTED_T_VGUI.name(),UDO.VO_POSSIBLE_ANOMALY_DETECTED_T_VGUI);
		outputs.put(UDO.VO_POSSIBLE_ANOMALY_DETECTED_F_VGUI.name(),UDO.VO_POSSIBLE_ANOMALY_DETECTED_F_VGUI);
		outputs.put(UDO.VO_ACK_MM.name(),UDO.VO_ACK_MM);
		outputs.put(UDO.VO_BUSY_MM.name(),UDO.VO_BUSY_MM);
		this.add(new VideoOperator(inputs, outputs));

		//add Video Operator Gui, with its inputs and outputs, to the team
		inputs.clear();
		inputs.put(UDO.VO_END_VGUI.name(),UDO.VO_END_VGUI);
		inputs.put(UDO.VO_POSSIBLE_ANOMALY_DETECTED_T_VGUI.name(),UDO.VO_POSSIBLE_ANOMALY_DETECTED_T_VGUI);
		inputs.put(UDO.VO_POSSIBLE_ANOMALY_DETECTED_F_VGUI.name(),UDO.VO_POSSIBLE_ANOMALY_DETECTED_F_VGUI);
		inputs.put(UDO.VO_FLYBY_REQ_T_VGUI.name(),UDO.VO_FLYBY_REQ_T_VGUI);
		inputs.put(UDO.VO_FLYBY_REQ_F_VGUI.name(),UDO.VO_FLYBY_REQ_F_VGUI);
		inputs.put(UDO.VO_FLYBY_END_FAILED_VGUI.name(),UDO.VO_FLYBY_END_FAILED_VGUI);
		inputs.put(UDO.VO_FLYBY_END_SUCCESS_VGUI.name(),UDO.VO_FLYBY_END_SUCCESS_VGUI);
		inputs.put(UDO.OP_FLYBY_START_F_VGUI.name(),UDO.OP_FLYBY_START_F_VGUI);
		inputs.put(UDO.OP_FLYBY_START_T_VGUI.name(),UDO.OP_FLYBY_START_T_VGUI);
		inputs.put(UDO.MM_END_VGUI.name(),UDO.MM_END_VGUI);
		inputs.put(UDO.MM_FLYBY_REQ_F_VGUI.name(),UDO.MM_FLYBY_REQ_F_VGUI);
		inputs.put(UDO.MM_FLYBY_REQ_T_VGUI.name(),UDO.MM_FLYBY_REQ_T_VGUI);
		inputs.put(UDO.MM_ANOMALY_DISMISSED_F_VGUI.name(),UDO.MM_ANOMALY_DISMISSED_F_VGUI);
		inputs.put(UDO.MM_ANOMALY_DISMISSED_T_VGUI.name(),UDO.MM_ANOMALY_DISMISSED_T_VGUI);
		//inputs.put(UDO.EVENT_TARGET_SIGHTED_F_VGUI.name(),);
		//inputs.put(UDO.EVENT_TARGET_SIGHTED_END_VGUI.name(),);
		//inputs.put(UDO.EVENT_TARGET_SIGHTED_T_VGUI.name(),);
		//inputs.put(UDO.EVENT_FLYBY_ANOMALY_F.name(),);
		//inputs.put(UDO.EVENT_FLYBY_ANOMALY_T.name(),);
		//inputs.put(UDO.VF_SIGNAL_OK_VGUI.name(),);
		//inputs.put(UDO.VF_SIGNAL_NONE_VGUI.name(),);
		outputs.clear();
		outputs.put(UDO.VGUI_FALSE_POSITIVE_VO.name(),UDO.VGUI_FALSE_POSITIVE_VO);
		outputs.put(UDO.VGUI_TRUE_POSITIVE_VO.name(),UDO.VGUI_TRUE_POSITIVE_VO);
		outputs.put(UDO.VGUI_VALIDATION_REQ_T.name(),UDO.VGUI_VALIDATION_REQ_T);
		outputs.put(UDO.VGUI_VALIDATION_REQ_F.name(),UDO.VGUI_VALIDATION_REQ_F);
		outputs.put(UDO.VO_FLYBY_REQ_T_OGUI.name(),UDO.VO_FLYBY_REQ_T_OGUI);
		outputs.put(UDO.VO_FLYBY_REQ_F_OGUI.name(),UDO.VO_FLYBY_REQ_F_OGUI);
		outputs.put(UDO.VO_FLYBY_END_FAILED_OGUI.name(),UDO.VO_FLYBY_END_FAILED_OGUI);
		outputs.put(UDO.VO_FLYBY_END_SUCCESS_OGUI.name(),UDO.VO_FLYBY_END_SUCCESS_OGUI);
		outputs.put(UDO.MM_FLYBY_REQ_F_OGUI.name(),UDO.MM_FLYBY_REQ_F_OGUI);
		outputs.put(UDO.MM_FLYBY_REQ_T_OGUI.name(),UDO.MM_FLYBY_REQ_T_OGUI);
		outputs.put(UDO.VGUI_NORMAL.name(),UDO.VGUI_NORMAL);
		outputs.put(UDO.VGUI_FLYBY_T.name(),UDO.VGUI_FLYBY_T);
		outputs.put(UDO.VGUI_FLYBY_F.name(),UDO.VGUI_FLYBY_F);
		this.add(new VideoOperatorGui(inputs, outputs));

		//add UAV, with its inputs and outputs, to the team
		inputs.clear();
		inputs.put(UDO.OGUI_TAKE_OFF_UAV.name(),UDO.OGUI_TAKE_OFF_UAV);
		inputs.put(UDO.OGUI_FLYBY_START_F_UAV.name(),UDO.OGUI_FLYBY_START_F_UAV);
		inputs.put(UDO.OGUI_FLYBY_START_T_UAV.name(),UDO.OGUI_FLYBY_START_T_UAV);
		inputs.put(UDO.OGUI_FLYBY_END_UAV.name(),UDO.OGUI_FLYBY_END_UAV);
		inputs.put(UDO.OGUI_LAND_UAV.name(),UDO.OGUI_LAND_UAV);
		inputs.put(UDO.OGUI_NEW_FLIGHT_PLAN_UAV.name(),UDO.OGUI_NEW_FLIGHT_PLAN_UAV);
		inputs.put(UDO.OGUI_LOITER_UAV.name(),UDO.OGUI_LOITER_UAV);
		//inputs.put(UDO.BATTERY_DEAD.name(),);
		outputs.clear();
		this.add(new UAV(inputs, outputs));	
	}

}
