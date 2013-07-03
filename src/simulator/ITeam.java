package simulator;

import java.util.HashMap;

public interface ITeam {
	
	/**
	 * Return a list of current actor transitions
	 * @return
	 */
	HashMap<IActor, ITransition> getTransitions();

}
