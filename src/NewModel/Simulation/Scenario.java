package NewModel.Simulation;

import NewModel.Events.Event;
import NewModel.Events.EventType;

public class Scenario {

	public static void scenario1()
	{
		Simulator.getInstance().addExternalEvent(new Event(EventType.PS_NEW_AOI, 1), 3);  //Always start at this point
		Simulator.getInstance().addExternalEvent(new Event(EventType.UAV_BAD_PATH, 600), 700);
//		Simulator.getInstance().addExternalEvent(new Event(EventType.UAV_LOST_SIGNAL, 20), 120);
		Simulator.getInstance().addExternalEvent(new Event(EventType.PS_NEW_AOI, 1), 1800);
		Simulator.getInstance().addExternalEvent(new Event(EventType.UAV_LOW_HAG, 120), 2300);
//		Simulator.getInstance().addExternalEvent(new Event(EventType.UAV_LOW_BATTERY, 120), 120);
//		Simulator.getInstance().addExternalEvent(new Event(EventType.UGUI_INACCESSIBLE, 500), 120);
		Simulator.getInstance().addExternalEvent(new Event(EventType.PS_NEW_AOI, 1), 4000);
		Simulator.getInstance().addExternalEvent(new Event(EventType.PS_TERMINATE_SEARCH, 1), 5200);
		
	}
	
	public static void scenario2()
	{
		Simulator.getInstance().addExternalEvent(new Event(EventType.PS_NEW_AOI, 1), 3);  //Always start at this point
		Simulator.getInstance().addExternalEvent(new Event(EventType.UAV_BAD_PATH, 600), 700);
		Simulator.getInstance().addExternalEvent(new Event(EventType.PS_NEW_AOI, 1), 1800);
		Simulator.getInstance().addExternalEvent(new Event(EventType.UAV_LOW_HAG, 120), 2300);
		Simulator.getInstance().addExternalEvent(new Event(EventType.PS_NEW_AOI, 1), 4000);
	}
	
	public static void scenario3()
	{
		Simulator.getInstance().addExternalEvent(new Event(EventType.PS_NEW_AOI, 1), 3);  //Always start at this point
		Simulator.getInstance().addExternalEvent(new Event(EventType.UAV_LOST_SIGNAL, 180), 1000);
		Simulator.getInstance().addExternalEvent(new Event(EventType.UGUI_INACCESSIBLE, 300), 1800);
	}
}
