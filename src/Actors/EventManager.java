package Actors;

import Simulator.*;

public class EventManager extends Actor {

	public EventManager(UDOList outputs) {
		
		State idle = new State("idle");
		State active = new State("active");
		
		//idle.addTransition(new Transition(, , , new Duration(1, 10), 0));
		//idle.addTransition(new Transition());;
		
		_states.add(idle);
		_states.add(active);
		
	}

	@Override
	public boolean updateTransition(int currentTime) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
