package Utilities;

import java.util.ArrayList;


/**
 * @author rob.ivie
 */
public class Transition {
	
	private ArrayList<UDO> _inputs;
	private int _duration;
	private State _endState;
	private ArrayList<UDO> _outputs;
	private State _curState;

	public Transition (ArrayList<UDO> inputs, ArrayList<UDO> outputs, State endState, State curState, int duration) {
		
		_inputs = inputs;
		_outputs = outputs;
		_endState = endState;
		_curState = curState;
		_duration = duration;
		
	}
	
	public boolean isPossible(){
		for(UDO input : _inputs){
			if(!input.isActive()){
				return false;
			}
		}
		return true;
	}
	
	public State process(){
		
		for(UDO output : _outputs){
			output.setActive();
		}
		return _endState;
		
	}
	
	public int getDuration(){
		
		return _duration;
		
	}
	
	public String toString() {
		
		if ( _inputs == null ) {
			return _curState + " X " + Integer.toString(_duration) + " -> " + _endState.toString() + " X " + _outputs.toString();
		}
		
		else {
			return _curState + " X " + _inputs.toString() + " -> " + _endState.toString() + " X " + _outputs.toString();
		}
		
	}
	
}
