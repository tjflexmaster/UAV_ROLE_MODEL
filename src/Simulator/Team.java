package Simulator;

import java.util.ArrayList;


import Actors.*;

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
	 * @param outputs
	 */
	public Team() {
		ArrayList<UDO> actor_io = new ArrayList<UDO>();
		this.add(new EventManager(actor_io));
		//TODO actor_io and fill with MM 
		actor_io.clear();
		this.add(new MissionManager(actor_io));
		//TODO actor_io and fill with OP 
		actor_io.clear();
		this.add(new Operator(actor_io));
		//TODO actor_io and fill with OGUI
		actor_io.clear();
		this.add(new OperatorGui(actor_io));
		//TODO actor_io and fill with VO
		actor_io.clear();
		this.add(new VideoOperator(actor_io));
		//TODO actor_io and fill with VGUI
		actor_io.clear();
		this.add(new VideoOperatorGui(actor_io));
		//TODO actor_io and fill with UAV
		actor_io.clear();
		this.add(new UAV(actor_io));
		
	}

}
