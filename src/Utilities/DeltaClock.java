package Utilities;

import java.util.ArrayList;
import java.util.Scanner;

public class DeltaClock {
	
	static int currentTime = 0;

	public void run(ActorsList clock, Scanner scanner) {
		
		do {
			
			//update actors next planned transition (currentTransition)
			for (int clockIndex = 0; clockIndex < clock.size(); clockIndex++) {
				
				if (clock.get(clockIndex).updateTransition()) {
					clock = resetClock(clock, clock.get(clockIndex));
				}
				
			}
			
			//tick the clock until a transition happens
			currentTime += clock.get(0).getNextTime();
			clock.get(0).setNextTime(0);
			
			//communicate with the user
			
			//process all actors that have a next time equal to zero
			for (int clockIndex = 0; clockIndex < clock.size(); clockIndex++) {
				
				if (clock.get(clockIndex).processTransition()) {
					clock = resetClock(clock, clock.get(clockIndex));
				}
				
			}
			
		} while (isRunning(clock));
		
	}

	/**
	 * reorder actors as you would a delta clock
	 * @param clock 
	 * @param actor
	 * @return 
	 */
	private static ActorsList resetClock(ActorsList clock, Actor actor) {
		clock.remove(actor);
		
		for (int clockIndex = 0; clockIndex < clock.size(); clockIndex++) {
			//if this spot in the list (delta clock) is empty place the actor here
			if (clock.get(clockIndex).getNextTime() == -1) {
				clock.add(actor);
				break;
			}
			//if the actor has less time then place it here and update next actor's nextTime
			else if (clock.get(clockIndex).getNextTime() > actor.getNextTime()) {
				clock.get(clockIndex).setNextTime(clock.get(clockIndex).getNextTime() - actor.getNextTime());
				clock.add(clockIndex, actor);
				break;
			}
			//if the actor has more time, then update its time and move to the next space
			else if (clock.get(clockIndex).getNextTime() < actor.getNextTime()) {
				actor.setNextTime(actor.getNextTime() - clock.get(clockIndex).getNextTime());
			}
			//if the list (delta clock) is full, then add actor to the end 
			else if (clockIndex == clock.size() - 1) {
				clock.add(actor);
			}
		}
		
		return clock;
	}

	/**
	 * this checks to see if the clock is full
	 * @param clock
	 * @return
	 */
	private static boolean isRunning(ArrayList<Actor> clock) {
		
		boolean result = false;
		for (int clockIndex = 0; clockIndex < clock.size(); clockIndex++) {
			if (clock.get(clockIndex).getNextTime() != -1) {
				result = true;
			}
		}
		
		return result;
	}

}
