package actors;

import java.util.HashMap;

import simulator.*;
import team.Duration;
import team.UDO;

public class ParentSearch extends Actor {

	public ParentSearch(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs) {

		//state names
		State IDLE = new State("IDLE");
		State POKE_MM = new State("POKE_MM");
		//state transitions
		//IDLE.addTransition(new Transition());
		//add states
		_states.add(IDLE);
		_states.add(POKE_MM);
		
	}
	
	@Override
	public boolean hasNewTransition(int currentTime) {
		// TODO Auto-generated method stub
		return false;
	}

}
