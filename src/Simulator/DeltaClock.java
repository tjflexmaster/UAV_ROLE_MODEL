package Simulator;

import java.util.ArrayList;
import java.util.Scanner;


public class DeltaClock {

	/**
	 * this method does most of the processing of the simulator
	 * @param clock is a list of all the actors
	 * 		this list will be reordered by actor.nextTime as a delta clock is ordered 
	 * @param 
	 */
	public void run(ActorsList actors) {
		
		//the scanner is used to communicate with the user running the simulation
		Scanner scanner = new Scanner(System.in);
		
		int currentTime = 0;
		
		do {
			
			//update actors next planned transition (currentTransition)
			for (int actorsIndex = 0; actorsIndex < actors.size(); actorsIndex++) {
				
				if (actors.get(actorsIndex).updateTransition(currentTime)) {
					actors = resetClock(actors, actors.get(actorsIndex));
				}
				
			}
			
			//tick the clock until a transition happens
			currentTime += actors.get(0).getNextTime();
			actors.get(0).setNextTime(0);
			
			//communicate with the user
			communicate(currentTime);
			
			//process all actors that have a next time equal to zero
			for (int actorsIndex = 0; actorsIndex < actors.size(); actorsIndex++) {
				
				if (actors.get(actorsIndex).processTransition()) {
					actors = resetClock(actors, actors.get(actorsIndex));
				}
				
			}
			
		} while (isRunning(actors));
		
		//close the scanner once the simulation is complete
		scanner.close();
		
	}

	/**
	 * reorder actors as you would a delta clock
	 * @param clock 
	 * @param actor
	 * @return 
	 */
	private static ActorsList resetClock(ActorsList actors, Actor actor) {
		actors.remove(actor);
		
		for (int actorsIndex = 0; actorsIndex < actors.size(); actorsIndex++) {
			//if this spot in the list (delta clock) is empty place the actor here
			if (actors.get(actorsIndex).getNextTime() == -1) {
				actors.add(actor);
				break;
			}
			//if the actor has less time then place it here and update next actor's nextTime
			else if (actors.get(actorsIndex).getNextTime() > actor.getNextTime()) {
				actors.get(actorsIndex).setNextTime(actors.get(actorsIndex).getNextTime() - actor.getNextTime());
				actors.add(actorsIndex, actor);
				break;
			}
			//if the actor has more time, then update its time and move to the next space
			else if (actors.get(actorsIndex).getNextTime() < actor.getNextTime()) {
				actor.setNextTime(actor.getNextTime() - actors.get(actorsIndex).getNextTime());
			}
			//if the list (delta clock) is full, then add actor to the end 
			else if (actorsIndex == actors.size() - 1) {
				actors.add(actor);
			}
		}
		
		return actors;
	}
	
	/**
	 * this method prints swim lanes and accepts user commands
	 * @param currentTime 
	 */
	private void communicate(int currentTime) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * this checks to see if the clock is full
	 * @param clock
	 * @return
	 */
	private static boolean isRunning(ArrayList<Actor> actors) {
		
		boolean result = false;
		for (int actorsIndex = 0; actorsIndex < actors.size(); actorsIndex++) {
			if (actors.get(actorsIndex).getNextTime() != -1) {
				result = true;
			}
		}
		
		return result;
	}

}
