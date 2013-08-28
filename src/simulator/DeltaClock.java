package simulator;

import java.util.*;

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
			
			//Loop through the linked list and insert this transition at the correct point.
			int total_time = 0;
			int time_diff = 0;
			boolean added = false;
			for( int i=0; i<_clock.size(); i++) {
				DeltaTime dt = _clock.get(i);
				if ( dt.time + total_time > newTime.time ) {
					//Difference between new time and the time that comes before it.
					time_diff = newTime.time - total_time;
					
					//We need to modify the time that comes after the new time so that it is the correct distance apart.
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
}
