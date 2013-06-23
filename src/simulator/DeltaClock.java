package simulator;

import java.util.ArrayList;

import team.*;

public class DeltaClock {

	private Team _actors = new Team();

	/**
	 * this method builds a delta clock of actors, that ticks their nextTime value
	 * @param actors specifies the team of actors that will be used in this clock
	 */
	public DeltaClock(Team actors) {
		for (int actorsIndex = 0; actorsIndex < actors.size(); actorsIndex++) {
			insert(actors.get(actorsIndex));
		}
	}

	/**
	 * this checks to see if the clock is full and advances the time
	 * @param clock
	 * @return return actors ready to transition, else return null
	 */
	public ArrayList<Actor> tick() {
		ArrayList<Actor> readyActors = new ArrayList<Actor>();
		
		if (_actors.isEmpty()) {
			return null;//simulator sees null as a signal to terminate
		} else {
			UDO.DC_TIME_ELAPSED.update(new Integer(_actors.get(0)._nextTime));//inform actors of time elapse
			_actors.get(0)._nextTime = 0;//advance time
		}
		
		while (readyActors.get(0)._nextTime == 0) {
			readyActors.add(_actors.remove(0));//form list of actors that are ready to transition
		}
		
		return readyActors;
	}

	/**
	 * places new actor in delta clock order
	 * @param actor specifies the actor to that will be added to this clock
	 * @return 
	 */
	public void insert(Actor actor) {
		for (int actorsIndex = 0; actorsIndex < _actors.size(); actorsIndex++) {
			//if this spot in the list (delta clock) is empty place the actor here
			if (_actors.get(actorsIndex).getNextTime() == -1) {
				_actors.add(actor);
				break;
			}
			//if the actor has less time then place it here and update next actor's nextTime
			else if (_actors.get(actorsIndex).getNextTime() > actor.getNextTime()) {
				_actors.get(actorsIndex).setNextTime(_actors.get(actorsIndex).getNextTime() - actor.getNextTime());
				_actors.add(actorsIndex, actor);
				break;
			}
			//if the actor has more time, then update its time and move to the next space
			else if (_actors.get(actorsIndex).getNextTime() < actor.getNextTime()) {
				actor.setNextTime(actor.getNextTime() - _actors.get(actorsIndex).getNextTime());
			}
			//if the list (delta clock) is full, then add actor to the end 
			else if (actorsIndex == _actors.size() - 1) {
				_actors.add(actor);
			}
		}
	}

	/**
	 * this method works like a normal toString method
	 * @return return a string representation of the clock
	 */
	public String toString() {
		String result = "";
		
		return result;
	}
}
