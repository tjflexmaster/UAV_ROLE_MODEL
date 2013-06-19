package simulator;

import team.*;

public class DeltaClock {

	private int _currentTime = 0;
	private Team _actors = new Team();

	/**
	 * this method builds a delta clock of actors, that ticks their nextTime value
	 * @param actors specifies the team of actors that will be used in this clock
	 */
	public DeltaClock(Team actors) {
		for (int actorsIndex = 0; actorsIndex < actors.size(); actorsIndex++) {
			addActor(actors.get(actorsIndex));
		}
	}

	/**
	 * this checks to see if the clock is full and advances the time
	 * @param clock
	 * @return return true if the clock is still ticking, else return false
	 */
	public int tick() {
		//check all actors to see if they are making a transition
		for (int actorsIndex = 0; actorsIndex < _actors.size(); actorsIndex++) {
			_actors.remove(actorsIndex);
			if (_actors.get(actorsIndex).makingTransition()) {
				addActor(_actors.get(actorsIndex));
			}
		}
		
		//update next planned transition (currentTransition) of all actors
		for (int actorsIndex = 0; actorsIndex < _actors.size(); actorsIndex++) {
			Actor nextActor = _actors.remove(actorsIndex);
			if (nextActor.hasNewTransition(_currentTime)) {
				addActor(_actors.get(actorsIndex));
			}
		}
		
		//tick the clock until a transition happens
		_currentTime += _actors.get(0).getNextTime();
		_actors.get(0).setNextTime(0);
		
		return _currentTime;
	}

	/**
	 * places new actor in delta clock order
	 * @param actor specifies the actor to that will be added to this clock
	 * @return 
	 */
	private void addActor(Actor actor) {
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
