package WiSAR;

/**
 * These are objects in the simulation that maintain state but do not make decisions.  
 * The agents will base their decisions off of the output generated from objects.
 * 
 * These can be anything from real world objects, to virtual objects only meant to trigger
 * state changes for agents or other objects.
 * 
 * @author TJ-ASUS
 *
 */
public enum ObjectEnum {
	PARENT_SEARCH,
	OPERATOR_BAD_PATH_TIMER,
	UAV_BATTERY_TIMER,
	UAV_FLIGHT_PATH_TIMER,
	UAV_HAG_TIMER,
	UAV_SIGNAL_TIMER,
}
