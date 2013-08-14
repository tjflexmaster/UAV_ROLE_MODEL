package simulator;

import java.util.ArrayList;

public interface IDeltaClock {

	/**
	 * Adds a transition to the delta clock for a specific Actor, the time designates
	 * how long before this transition should occur.  It will automatically be changed
	 * into delta clock time.
	 * 
	 * To remove a transition from the delta clock a null transition may be passed to 
	 * the delta clock.
	 * 
	 * @param actor
	 * @param transition
	 * @param time
	 */
	void addTransition(IActor actor, ITransition transition, int time);
	
	/**
	 * Removes transitions for the given Actor.
	 * @param actor
	 */
	void removeTransition(IActor actor);
	
	/**
	 * Calculates how much time is remaining until an Actors transition will occur.
	 * @param actor
	 * @return
	 */
	int getTimeTillTransition(IActor actor);
	
	/**
	 * Returns the active transition for that Actor, if no transition is found
	 * returns null.
	 * @param actor
	 * @return
	 */
	ITransition getActorTransition(IActor actor);
	
	/**
	 * Advances time on the delta clock, this means that all transitions with time 0 are removed
	 * and the next set of transitions have their clock time reduced to 0.
	 */
	void advanceTime();
	
	/**
	 * Returns a list of transitions that are ready to be fired
	 * @return
	 */
	ArrayList<ITransition> getReadyTransitions();
	
	/**
	 * Returns how much time has passed since the delta clock began.
	 * @return
	 */
	int getElapsedTime();
}
