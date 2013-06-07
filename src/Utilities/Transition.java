package Utilities;


/**
 * @author rob.ivie
 */
public class Transition {
	
	public State _startState;
	public Data _input;
	public int _time;
	public State _nextState;
	public Data _output;

	/**
	 * a transition is used by an agent (state machine) to formally define state transitions 
	 * @param startState the state an agent must be in before it can make the transition
	 * @param input the necessary input an agent needs before it can make the transition
	 * @param nextState the new state the agent will move to
	 * @param output the output the transition produces
	 * todo add a duration that represents how long it takes to move between states
	 */
	public Transition (State startState, Data input, State nextState, Data output) {
		
		_startState = startState;
		_input = input;
		_nextState = nextState;
		_output = output;
		
	}

	/**
	 * a transition is used by an agent (state machine) to formally define state transitions 
	 * @param startState the state an agent must be in before it can make the transition
	 * @param time the necessary input an agent needs before it can make the transition
	 * @param nextState the new state the agent will move to
	 * @param output the output the transition produces
	 * todo add a duration that represents how long it takes to move between states
	 */
	public Transition(State startState, int time, State nextState, Data output) {
		
		_startState = startState;
		_time = time; 
		_nextState = nextState;
		_output = output;
		
	}
	
	public String toString() {
		
		if ( _input == null ) {
			
			return _startState.toString() + " X " + Integer.toString(_time)
					+ " -> " + _nextState.toString() + " X " + _output.toString();
			
		}
		
		else {
		
			return _startState.toString() + " X " + _input.toString()
					+ " -> " + _nextState.toString() + " X " + _output.toString();
			
		}
		
	}
	
}
