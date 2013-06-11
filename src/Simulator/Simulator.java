package Simulator;

import java.util.ArrayList;

import Utilities.*;

public class Simulator {
	
	/**
	 * this method runs the simulator
	 * @param args should be left blank, all input will be ignored
	 */
	public static void main(String[] args) {
		//initialize simulation variables
		int currentTime = 0;
		ArrayList<UDO> outputs = initializeOutputs();
		ArrayList<Actor> actors = initializeActors(outputs);
		
		//run the simulator until the clock is empty
		do {
			
			//process actors
			for (int actorsIndex = 0; actorsIndex < actors.size(); actorsIndex++) {
				
				int nextTime = actors.get(actorsIndex).getNextTime();
				actors.get(actorsIndex).update(currentTime);
				if (nextTime != actors.get(actorsIndex).getNextTime()) {
					actors = resetClock(actors, actors.get(actorsIndex));
				}
				
			}
			
			//tick the clock until a transition happens
			currentTime += actors.get(0).getNextTime();
			actors.get(0).setNextTime(0);
			
		} while (isRunning(actors));
	}

	/**
	 * list all of the outputs in this method
	 * we may be able to use another class instead of this method
	 * @return
	 */
	private static ArrayList<UDO> initializeOutputs() {
		ArrayList<UDO> outputs = new ArrayList<UDO>();
		//TODO add outputs
		return outputs;
	}

	/**
	 * list all of the actors in this method
	 * we may be able to use another class instead of this method
	 * @param outputs 
	 * @return
	 */
	private static ArrayList<Actor> initializeActors(ArrayList<UDO> outputs) {
		ArrayList<Actor> actors = new ArrayList<Actor>();
		//TODO add actors
		return actors;
	}

	/**
	 * reorder actors as you would a delta clock
	 * @param actors 
	 * @param actor
	 * @return 
	 */
	private static ArrayList<Actor> resetClock(ArrayList<Actor> actors, Actor actor) {
		actors.remove(actor);
		
		for (int actorsIndex = 0; actorsIndex < actors.size(); actorsIndex++) {
			if (actors.get(actorsIndex).getNextTime() == -1) {
				actors.add(actor);
				break;
			} else if (actors.get(actorsIndex).getNextTime() > actor.getNextTime()) {
				actors.get(actorsIndex)
						.setNextTime(actors.get(actorsIndex).getNextTime() - actor.getNextTime());
				actors.add(actorsIndex, actor);
				break;
			} else if (actors.get(actorsIndex).getNextTime() < actor.getNextTime()) {
				actor.setNextTime(actor.getNextTime() - actors.get(actorsIndex).getNextTime());
			}
		}
		
		return actors;
	}

	/**
	 * this checks the actors to see if the clock is active
	 * @param actors
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
