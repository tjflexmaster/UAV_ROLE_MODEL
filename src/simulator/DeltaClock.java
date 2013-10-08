package simulator;

import java.util.*;
import java.util.Map.Entry;

/**
 *
 */
public class DeltaClock implements IDeltaClock {
	
	/**
	 * This class uses Actor names to compare objects to one another.
	 * @author TJ-ASUS
	 *
	 */
	private class DeltaTime {
		public int time;
		public IActor actor;
		public ITransition transition;
		
		DeltaTime(int delta, IActor a, ITransition t)
		{
			time = delta;
			actor = a;
			transition = t;
		}
		
		@Override
		public boolean equals(Object obj) 
		{
			if ( obj == this )
				return true;
			
			if (obj instanceof DeltaTime) {
				if ( ((DeltaTime) obj).actor.name() == actor.name() )
					return true;
				//suggested revision
//				if(((DeltaTime) obj).actor.getClass() == actor.getClass()){
//					return true;
//				}
			}
			
			return false;
		}
		
		@Override
		public int hashCode()
		{
			return actor.name().hashCode();
		}
	}

	private LinkedList<DeltaTime> _clock;
	private int _elapsedTime = 0;
	
	/**
	 * this method builds a delta clock of actors, that ticks their nextTime value
	 * @param actors specifies the team of actors that will be used in this clock
	 */
	public DeltaClock() {
		_clock = new LinkedList<DeltaTime>();
	}

	@Override
	public void addTransition(IActor actor, ITransition transition, int time) {
		//First remove this actors transition
		removeTransition(actor);
		
		//If transition is not null then add it
		if ( transition != null ) {
			//Then add the transition
			DeltaTime newTime = new DeltaTime(time, actor, transition);
			
			//Add active metric to this transition
//			Simulator.getSim()._metrics.currentKey.setActor(actor.name());
//			Simulator.getSim()._metrics.currentKey.setState(actor.getCurrentState().getName());
//			Simulator.getSim()._metrics.currentKey.setTransition(transition.getIndex());
//			Simulator.getSim()._metrics.addMetric(MetricEnum.ACTIVE, "ACTIVE_TRANSITION");
			
			//Loop through the linked list and insert this transition at the correct point.
			int total_time = 0;
			int time_diff = 0;
			boolean added = false;
			for( int i=0; i<_clock.size(); i++) {
				DeltaTime dt = _clock.get(i);
				if ( dt.time + total_time > newTime.time ) {
					//Difference between new time and the time that comes before it.
					time_diff = newTime.time - total_time;
					
					//We need to modify the time that comes after the new time so that it is the correct
					//distance apart.
					dt.time = dt.time - time_diff;
					
					//Insert the newTime at this point
					_clock.add(i, newTime);
					added = true;
					break;
				}
				
				total_time += dt.time;
			}
			
			//If we haven't added yet then put it on the end.
			if ( !added ) {
				newTime.time = newTime.time - total_time;
				_clock.addLast(newTime);
			}
			
			//Save channel load metric
			sendChannelLoad(actor, transition, time);
			
		}
	}

	@Override
	public void removeTransition(IActor actor) {
		DeltaTime dt = new DeltaTime(0,actor, null);
		
		if ( _clock.contains(dt) ) {
			int i = _clock.indexOf(dt);
			dt = _clock.get(i);
			_clock.remove(i);
			
			//Make sure to add the removed time back into the clock
			if(_clock.size() > i)
				_clock.get(i).time += dt.time;
		}
		
	}

	@Override
	public int getTimeTillTransition(IActor actor) {
		DeltaTime fake = new DeltaTime(0, actor, null);
		
		int total_time = 0;
		for( int i=0; i<_clock.size(); i++) {
			DeltaTime dt = _clock.get(i);
			total_time += dt.time;
			if ( dt.equals(fake) )
				break;
		}
		return total_time;
	}

	@Override
	public ITransition getActorTransition(IActor actor) {
		DeltaTime fake = new DeltaTime(0, actor, null);
		
		int i = _clock.indexOf(fake);
		if ( i < 0 )
			return null;
		else
			return _clock.get(i).transition;
	}

	@Override
	public void advanceTime() {
		//This only advances time, it removes the first elements it finds with a time of zero.
		//It then changes the value of the first remaining element to zero.
		//Do this recursively for simplicity
		
		if ( !_clock.isEmpty() ) {
			DeltaTime dt = _clock.getFirst();
			if ( dt.time == 0 ) {
				_clock.remove();
				advanceTime();
			} else {
				_elapsedTime += dt.time;
				dt.time = 0;
				
				//Send channel conflict data
				sendChannelConflictData();
				
				//Send actor output data
				sendActorOutput();
				
			}
		}
		
	}
	

	@Override
	public HashMap<IActor, ITransition> getReadyTransitions() {
		HashMap<IActor, ITransition> result = new HashMap<IActor, ITransition>();
		
		for( int i=0; i<_clock.size(); i++) {
			DeltaTime dt = _clock.get(i);
			if ( dt.time == 0 ){
				result.put(dt.actor, dt.transition);
			}else
				break;
			
		}
		return result;
	}

	@Override
	public int getElapsedTime() {
		return _elapsedTime;
	}

	
	public void sendChannelConflictData()
	{
		for(DeltaTime t : _clock) {
			//First get a list of all outputs that the transition might change
			HashMap<String, Object> result = t.transition.getTempOutputChannels();
			ComChannelList outputs = t.transition.getOutputChannels();
			
			HashMap<String, HashMap<String, Integer> > conflicts = new HashMap<String, HashMap<String, Integer> >();
			
			//Second go through the list and mark which ones are not null
			for(Entry<String, Object> e : result.entrySet() ) {
				if ( e.getValue() != null ) {
					//Find out who the information is going too
					ComChannel<?> o = outputs.get(e.getKey());
					if ( o != null ) {
						String target = o.target();
						if ( target != "None" ) {
							//Save this information to be passed to JPF
							if ( conflicts.containsKey(target) ) {
								HashMap<String, Integer> channels = conflicts.get(target);
								if ( channels.containsKey(o.type().name()) ) {
									channels.put(o.type().name(), channels.get(o.type().name()) + 1);
								} else {
									channels.put(o.type().name(), 1);
								}
								
							} else {
								HashMap<String, Integer> channels = new HashMap<String, Integer>();
								channels.put(o.type().name(), 1);
								conflicts.put(target, channels);
							}
						}
					}
				}
			}
//			MetricManager.instance().setChannelConflict(_elapsedTime, t.actor.name(), target, o.type().name());
//			
			
			//Send all the data to JPF
			for(Entry<String, HashMap<String, Integer> > e : conflicts.entrySet()) {
				for(Entry<String, Integer> c : e.getValue().entrySet()) {
					MetricManager.instance().setChannelConflict(_elapsedTime, e.getKey(), c.getKey(), c.getValue());
				}
			}
			
		}
	}
	
	public void sendChannelLoad(IActor a, ITransition t, int time)
	{
		//First get a list of all outputs that the transition might change
		HashMap<String, Object> result = t.getTempOutputChannels();
		ComChannelList outputs = t.getOutputChannels();
		
		
		//Second go through the list and mark which ones are not null
		for(Entry<String, Object> e : result.entrySet() ) {
			if ( e.getValue() != null ) {
				//Find out who the information is going too
				ComChannel<?> o = outputs.get(e.getKey());
				if ( o != null ) {
					MetricManager.instance().setChannelLoad(_elapsedTime, a.name(), o.target(), o.type().name(), time);
				}
			}
		}
	}
	
	public void sendActorOutput()
	{
		//IActor a, ITransition t, int time
		for(DeltaTime t : _clock) {
			//First get a list of all outputs that the transition might change
			HashMap<String, Object> tempOutputs = t.transition.getTempOutputChannels();
		
			//Second loop through those outputs
			int outputCount = 0;
			for(Entry<String, Object> e : tempOutputs.entrySet() ) {
				if ( e.getValue() != null ) {
					outputCount++;
				}
			}
		
		
			//First get a list of all outputs that the transition might change
			HashMap<String, Object> tempMemory = t.transition.getTempInternalVars();
			
			//Second loop through those outputs
			int memoryCount = 0;
			for(Entry<String, Object> e : tempMemory.entrySet() ) {
				if ( e.getValue() != null ) {
					memoryCount++;
				}
			}
		
			MetricManager.instance().setActorOutput(_elapsedTime, t.actor.name(), memoryCount, outputCount);
		}
	}
	
//	/**
//	 * this checks to see if the clock is full and advances the time
//	 * @param clock
//	 * @return return actors ready to transition, else return null
//	 */
//	public ArrayList<Actor> tick() {
//		ArrayList<Actor> readyActors = new ArrayList<Actor>();
//		
//		if (_actors.isEmpty()) {
//			return _actors;//simulator sees null as a signal to terminate
//		} else {
//			absoluteTime += _actors.get(0).get_nextTime();
//			UDO.DC_TIME_ELAPSED.update(new Integer(_actors.get(0).get_nextTime()));//inform actors of time elapse
//			_actors.get(0).set_nextTime(0);//advance time
//		}
//		
//		while (_actors.size() > 0 && _actors.get(0).get_nextTime() == 0) {
//			readyActors.add(_actors.remove(0));//form list of actors that are ready to transition
//		}
//		
//		return readyActors;
//	}
//
//	/**
//	 * places new actor in delta clock order
//	 * @param actor specifies the actor to that will be added to this clock
//	 * @return 
//	 */
//	public void insert(Actor actor) {
//		if (actor.get_nextTime() == -1 || _actors.contains(actor)) {
//			return;
//		}
//		
//		for (int actorsIndex = 0; actorsIndex < _actors.size(); actorsIndex++) {
//			//if actor at actorsIndex == -1 then insert newActor and move actor to the next location
//			//if actor at actorsIndex != -1 and newActor is less than actor then insert newActor and decrement actor's time
//			//if actor at actorsIndex != -1 and newActor is greater than actor then decrement new Actor and check next location
//			if(_actors.get(actorsIndex).get_nextTime() == -1){
//				_actors.add(actorsIndex, actor);
//				break;
//			}else if ( _actors.get(actorsIndex).get_nextTime() >= actor.get_nextTime() ) {
//				_actors.get(actorsIndex).set_nextTime(_actors.get(actorsIndex).get_nextTime() - actor.get_nextTime());
//				_actors.add(actorsIndex, actor);
//				break;
//			} else {
//				actor.set_nextTime(actor.get_nextTime() - _actors.get(actorsIndex).get_nextTime());
//			}
//		}
//		
//		if (!_actors.contains(actor)) {
//			_actors.add(actor);
//		}
//	}
//
//	/**
//	 * this method works like a normal toString method
//	 * @return return a string representation of the clock
//	 */
//	public String toString() {
//		return _actors.toString();
//	}
//	
//	public int getAbsoluteTime(){
//		return absoluteTime;
//	}


}
