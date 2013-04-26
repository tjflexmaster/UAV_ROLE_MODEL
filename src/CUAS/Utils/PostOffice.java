package CUAS.Utils;

import java.util.ArrayList;

import CUAS.Simulator.IData;
import CUAS.Simulator.Simulator;
import WiSAR.Agents.Roles;

public class PostOffice  {
	
	private ArrayList<IData> PS;
	private ArrayList<IData> MM;
	private ArrayList<IData> VO;
	private ArrayList<IData> OP;
	private ArrayList<IData> VO_GUI;
	private ArrayList<IData> OP_GUI;
	
	
	
	public PostOffice() {
		PS = new ArrayList<IData>();
		MM = new ArrayList<IData>();
		VO = new ArrayList<IData>();
		OP = new ArrayList<IData>();
	}
	
	

	public void addOutput(IData d, String name){
		switch(name){
		case Roles.PARENT_SEARCH.name():
			PS.add(d);
			break;
		case Roles.MISSION_MANAGER.name():
			MM.add(d);
			break;
		case Roles.VIDEO_OPERATOR.name():
			VO.add(d);
			break;
		case Roles.OPERATOR.name():
			OP.add(d);
			break;
		case Roles.VIDEO_OPERATOR_GUI.name():
			VO_GUI.add(d);
			break;
		case Roles.OPERATOR_GUI.name():
			OP_GUI.add(d);
			break;
		default:
			break;
		}
	}
	
	public void addOutputs(ArrayList<IData> data, String name){
		switch(name){
		case Roles.PARENT_SEARCH.name():
			PS.addAll(data);
			break;
		case Roles.MISSION_MANAGER.name():
			MM.addAll(data);
			break;
		case Roles.VIDEO_OPERATOR.name():
			VO.addAll(data);
			break;
		case Roles.OPERATOR.name():
			OP.addAll(data);
			break;
		case Roles.VIDEO_OPERATOR_GUI.name():
			VO_GUI.addAll(data);
			break;
		case Roles.OPERATOR_GUI.name():
			OP_GUI.addAll(data);
			break;
		default:
			break;
		}
	}
	
	public void addInputs(){
		sim().addInput(Roles.PARENT_SEARCH.name(), PS);
		sim().addInput(Roles.MISSION_MANAGER.name(), MM);
		sim().addInput(Roles.OPERATOR.name(), OP);
		sim().addInput(Roles.VIDEO_OPERATOR.name(), VO);
		sim().addInput(Roles.OPERATOR_GUI.name(), OP_GUI);
		sim().addInput(Roles.VIDEO_OPERATOR_GUI.name(), VO_GUI);
	}



	/**
	 * Convenience method so that getInstance does not have to be called over and over
	 * @return
	 */
	protected Simulator sim()
	{
		return Simulator.getInstance();
	}

}
