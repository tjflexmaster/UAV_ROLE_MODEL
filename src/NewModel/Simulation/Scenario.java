package NewModel.Simulation;

import NewModel.Events.Event;
import NewModel.Events.EventType;

public class Scenario {

	public static void scenario1()
	{
//		Simulator.addExternalEvent(new Event(EventType.ENV_HIGH_WIND, 500), 200);
//		Simulator.addExternalEvent(new Event(EventType.UAV_BAD_PATH, 80), 120);
//		Simulator.addExternalEvent(new Event(EventType.UAV_LOST_SIGNAL, 20), 120);
//		Simulator.addExternalEvent(new Event(EventType.UAV_LOW_HAG, 120), 120);
//		Simulator.addExternalEvent(new Event(EventType.UAV_LOW_BATTERY, 120), 120);
//		Simulator.addExternalEvent(new Event(EventType.UGUI_INACCESSIBLE, 500), 120);
//		Simulator.addExternalEvent(new Event(EventType.PS_TERMINATE_SEARCH, 1), 120);
		Simulator.addExternalEvent(new Event(EventType.PS_NEW_AOI, 1), 300);
		
	}
}
