package simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import simulator.metrics.IMetrics;

public interface ITransition extends IMetrics {

	/**
	 * Fire the transition causing all of its output and state changes to occur immediately.
	 */
	void fire();

	/**
	 * If the transition is possible then this method will return true.  This method should be overwritten
	 * to control the logic which decides if the transition is enabled.
	 * @return
	 */
	boolean isEnabled();
//	/**
//	 * If the transition is possible then this method will return true. It also clears out the temporary data from previous checks.
//	 * @return
//	 */
//	boolean updateTransition();
	
	/**
	 * return an int from the range of durations
	 * @return
	 */
	Range getDurationRange();
	
	/**
	 * return an int representing the priority
	 */
	int priority();
	
	ComChannelList getInputChannels();
	HashMap<String, IComLayer> getInputLayers();
	ComChannelList getOutputChannels();
	
	ArrayList<TempComChannel > getTempOutputChannels();
	ArrayList<TempMemory > getTempOutputMemory();

	void setIndex(int indexOf);
	int getIndex();
}
