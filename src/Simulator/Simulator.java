package Simulator;

import java.util.ArrayList;
import java.util.Scanner;

import Utilities.*;
import Actors.*;

public class Simulator {
	
	/**
	 * this method runs the simulator
	 * @param args should be left blank, all input will be ignored
	 */
	public static void main(String[] args) {
		//initialize simulation variables
		int currentTime = 0;
		ArrayList<UDO> outputs = initializeOutputs();
		ArrayList<Actor> clock = initializeClock(outputs);
		Scanner scanner = new Scanner(System.in);
		
		//run the simulator until the clock is empty
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
			communicate(clock, scanner);
			
			//process all actors that have a next time equal to zero
			for (int clockIndex = 0; clockIndex < clock.size(); clockIndex++) {
				
				if (clock.get(clockIndex).processTransition()) {
					clock = resetClock(clock, clock.get(clockIndex));
				}
				
			}
			
		} while (isRunning(clock));
		
		//close variables
		scanner.close();
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
	private static ArrayList<Actor> initializeClock(ArrayList<UDO> outputs) {
		ArrayList<Actor> actors = new ArrayList<Actor>();
		
		actors.add(new EventManager(outputs));
		actors.add(new MissionManager(outputs));
		actors.add(new Operator(outputs));
		actors.add(new OperatorGui(outputs));
		actors.add(new UAV(outputs));
		actors.add(new VideoOperator(outputs));
		actors.add(new VideoOperatorGui(outputs));
		
		return actors;
	}
	
	/**
	 * print current state of the simulator and take input from the user
	 * @param clock the clock is passed so that it can be printed to the user
	 * @param scanner the scanner is passed so that commands can be received
	 */
	private static void communicate(ArrayList<Actor> clock, Scanner scanner) {
		
		for (int clockIndex = 0; clockIndex < clock.size(); clockIndex++) {
			System.out.println(clock.get(clockIndex).toString());
		}
		scanner.nextLine();
		
	}

	/**
	 * reorder actors as you would a delta clock
	 * @param clock 
	 * @param actor
	 * @return 
	 */
	private static ArrayList<Actor> resetClock(ArrayList<Actor> clock, Actor actor) {
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
