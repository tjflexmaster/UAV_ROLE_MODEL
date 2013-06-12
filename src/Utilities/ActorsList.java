package Utilities;

import java.util.ArrayList;

import Actors.*;

/**
 * 
 * @author rob.ivie
 *
 * list all of the actors in this method
 * we may be able to use another class instead of this method
 */
public class ActorsList extends ArrayList<Actor> {
	
	/**
	 * I have no idea what this means, but it fixed a compilation warning...
	 */
	private static final long serialVersionUID = 682275625652767731L;

	public ActorsList(UDOList outputs) {
		this.add(new EventManager(outputs));
		this.add(new MissionManager(outputs));
		this.add(new Operator(outputs));
		this.add(new OperatorGui(outputs));
		this.add(new VideoOperator(outputs));
		this.add(new VideoOperatorGui(outputs));
		this.add(new UAV(outputs));
	}

}
