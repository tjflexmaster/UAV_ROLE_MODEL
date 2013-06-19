package actors;

import java.util.HashMap;

import simulator.*;
import team.*;

public class EventManager extends Actor {
	
	public EventManager(HashMap<String, UDO> inputs, HashMap<String, UDO> outputs) {
		//state names
		State IDLE = new State("IDLE");
		State ACTIVE = new State("ACTIVE");
		//state transitions
		IDLE.addTransition(new Transition(0, new UDO[]{UDO.EM_START_SEARCH_PS}, ACTIVE, Duration.EM_START_SEARCH_PS, 0));
		//add states
		_states.add(IDLE);
		_states.add(ACTIVE);
	}

	@Override
	public boolean hasNewTransition(int currentTime) {
		// TODO Auto-generated method stub
		return false;
	}

}
