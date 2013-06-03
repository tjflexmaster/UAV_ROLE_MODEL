package WiSAR;

import CUAS.Simulator.IData;
import CUAS.Simulator.IStateEnum;

/**
 * @author rob.ivie
 */
public class Transition {
	
	IStateEnum startState;
	IData input;
	IStateEnum nextState;
	IData output;

	/**
	 * a transition is used by an agent (state machine) to formally define state transitions 
	 * @param _startState the state an agent must be in before it can make the transition
	 * @param _input the necessary input an agent needs before it can make the transition
	 * @param _nextState the new state the agent will move to
	 * @param _output the output the transition produces
	 * todo add a duration that represents how long it takes to move between states
	 */
	public Transition (IStateEnum _startState, IData _input, IStateEnum _nextState, IData _output) {
		
		startState = _startState;
		input = _input;
		nextState = _nextState;
		output = _output;
		
	}
	
}
