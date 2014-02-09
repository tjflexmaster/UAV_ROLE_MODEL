package simulator;

import java.util.ArrayList;

public interface IState {

	/**
	 * Returns a list of enabled transitions.
	 * @return
	 */
	ArrayList<ITransition> getEnabledTransitions();
	
	String getName();
	
	int getLoad();
}
