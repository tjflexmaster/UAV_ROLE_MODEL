package Actors;

import java.util.ArrayList;

import Simulator.Actor;
import Simulator.State;
import Simulator.UDO;

public class EventManager extends Actor {

	public EventManager(ArrayList<UDO> data) {
		
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
