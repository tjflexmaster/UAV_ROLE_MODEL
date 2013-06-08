package Utilities;

import java.util.ArrayList;

public abstract class Actor {
	
	/**
	 * the simulator's delta clock uses this variable
	 */
	private int nextTime = -1;
	
	private State currentState;
	private Transition currentTransition;
	private ArrayList<State> states;
	
	public void update(int currentTime) {
		
	}
	
	public void processTransition(int currentTime){
		if(getNextTime() == 0){
			currentState = currentTransition.process();
//			currentTransition = currentState.getNextTransition();
		}
	}

	public int getNextTime() {
		return nextTime;
	}

	public void setNextTime(int nextTime) {
		this.nextTime = nextTime;
	}
	
}
