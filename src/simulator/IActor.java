package simulator;

import java.util.ArrayList;
import java.util.HashMap;

public interface IActor {

	/**
	 * Returns the Actor transition and the transitions of any sub-actors.
	 * Note: An Actor can only take 1 transition.  If more are needed they should be placed 
	 * in sub-actors and their transitions should be passed through this method.
	 * @return
	 */
	HashMap<IActor, ITransition> getTransitions();
	
	/**
	 * Returns the unique name of the Actor.
	 * @return
	 */
	String name();
	
}
