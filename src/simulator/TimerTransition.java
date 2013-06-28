package simulator;

import model.team.Duration;
import model.team.UDO;

public class TimerTransition extends Transition {

	public TimerTransition(UDO[] inputs, UDO[] outputs, State endState,
			Duration duration, int priority) {
		super(inputs, outputs, endState, duration, priority);
	}
	
	@Override 
	public boolean isEnabled() {
		if(_inputs == null)
			return true;
		for (UDO input : _inputs) {
			if(input.get() == null){
				return false;
			} else if (input.get().getClass()==Integer.class) {//handle integer counters
				input.update((Integer)input.get()-(Integer)UDO.DC_TIME_ELAPSED.get());
				if ((Integer)input.get()>0) {
					return false;
				}
				if((Integer)input.get()<= 0){
					input.set(null);
				}
			}
		}
		return true;
	}

}
