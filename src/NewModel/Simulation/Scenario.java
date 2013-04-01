package NewModel.Simulation;

import NewModel.Events.Event;
import NewModel.Events.EventType;

public class Scenario {

	public static void scenario1()
	{
//		Simulator.addExternalEvent(new Event(EventType.ENV_HIGH_WIND, 500), 200);
		Simulator.addExternalEvent(new Event(EventType.UAV_LOST_SIGNAL, 20), 120);
//		Simulator.addExternalEvent(new Event(EventType.UAV_LOW_HAG, 120), 280);
//		Simulator.addExternalEvent(new Event(EventType.PS_TERMINATE_SEARCH, 1), 400);
		
	}
}
