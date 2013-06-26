package simulator;

import java.util.ArrayList;

import team.*;

public class DeltaClock {

	private ArrayList<Actor> _actors;
	private int absoluteTime;
	
	/**
	 * this method builds a delta clock of actors, that ticks their nextTime value
	 * @param actors specifies the team of actors that will be used in this clock
	 */
	public DeltaClock() {
		UDO.DC_TIME_ELAPSED.update(new Integer(0));
		_actors = new ArrayList<Actor>();
		absoluteTime = 0;
	}

	/**
	 * this checks to see if the clock is full and advances the time
	 * @param clock
	 * @return return actors ready to transition, else return null
	 */
	public ArrayList<Actor> tick() {
		ArrayList<Actor> readyActors = new ArrayList<Actor>();
		
		if (_actors.isEmpty()) {
			return _actors;//simulator sees null as a signal to terminate
		} else {
			absoluteTime += _actors.get(0)._nextTime;
			UDO.DC_TIME_ELAPSED.update(new Integer(_actors.get(0)._nextTime));//inform actors of time elapse
			_actors.get(0)._nextTime = 0;//advance time
		}
		
		while (_actors.size() > 0 && _actors.get(0).getNextTime() == 0) {
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
		if (actor.getNextTime() == -1 || _actors.contains(actor)) {
			return;
		}
		
		for (int actorsIndex = 0; actorsIndex < _actors.size(); actorsIndex++) {
			//if actor at actorsIndex == -1 then insert newActor and move actor to the next location
			//if actor at actorsIndex != -1 and newActor is less than actor then insert newActor and decrement actor's time
			//if actor at actorsIndex != -1 and newActor is greater than actor then decrement new Actor and check next location
			if(_actors.get(actorsIndex).getNextTime() == -1){
				_actors.add(actorsIndex, actor);
				break;
			}else if ( _actors.get(actorsIndex).getNextTime() >= actor.getNextTime() ) {
				_actors.get(actorsIndex).setNextTime(_actors.get(actorsIndex).getNextTime() - actor.getNextTime());
				_actors.add(actorsIndex, actor);
				break;
			} else {
				actor.setNextTime(actor.getNextTime() - _actors.get(actorsIndex).getNextTime());
			}
		}
		
		if (!_actors.contains(actor)) {
			_actors.add(actor);
		}
	}

	/**
	 * this method works like a normal toString method
	 * @return return a string representation of the clock
	 */
	public String toString() {
		return _actors.toString();
	}
	
	public int getAbsoluteTime(){
		return absoluteTime;
	}
}
