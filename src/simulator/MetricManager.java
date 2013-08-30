package simulator;

/**
 * Metrics taken care of
 * accessing internal variables
 * accessing active internal variables
 * accessing com channels
 * accessing active com channels
 * initializing com_channels
 * deactivating com_channels
 * changing com_channel data
 * @author jaredmoore
 *
 */
public class MetricManager {
	
	private static MetricManager _instance = null;
	
	private MetricManager() {}
	
	public static synchronized MetricManager instance() {
		if ( _instance == null ) {
			_instance = new MetricManager();
		}
		return _instance;
	}

	public void setDecisionWorkload(int time, String actor, String state, int workload)
	{
		//Do nothing JPF will handle the data
	}
	
	/**
	 * This method is used to report channel conflicts.  A channel conflict is when an Actor is 
	 * receiving input on an audio, visual, or tactile channel from more than a single source.
	 * An example of this would be having two different people trying to talk to a single person.  
	 * 
	 * We collect this by looking at the current outputs within the delta clock.  Each output 
	 * represents what is being output by an actor at that time.
	 * @param time
	 * @param actor
	 * @param channel_type
	 */
	public void setChannelConflict(int time, String actor_target, String channel_type, int load)
	{
		//Do nothing JPF will handle the data
	}
	
	public void setChannelLoad(int time, String actor, String channel_type, int load)
	{
		//Do nothing JPF will handle the data
	}
	
	public void endSimulation()
	{
		//Do nothing JPF recognizes this and prints
	}
}
