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
	
	
	//constructor classes
	
	public void addState(String name){
		states.add(new State(name));
	}
	
	public void addTransition(String state, ArrayList<UDO> inputs, ArrayList<UDO> outputs, State endState, int duration, int priority){
		//find the state and add the transition to it
		for(int index = 0; index < states.size(); index++){
			State temp = states.get(index);
			if(temp.equals(state)){
				temp.addTransition(new Transition(inputs,outputs,endState,duration,priority));
				return;
			}
		}
		//if the state doesn't exist yet, create a new state and add the transition
		State new_state = new State(state);
		new_state.addTransition(new Transition(inputs,outputs,endState,duration,priority));
		states.add(new_state);
	}
}
