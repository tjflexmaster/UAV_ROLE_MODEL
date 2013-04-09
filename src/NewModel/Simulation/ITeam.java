package NewModel.Simulation;

import java.util.ArrayList;

import NewModel.Events.Event;
import NewModel.Roles.RoleState;
import NewModel.Roles.RoleType;

public interface ITeam {

	public RoleState getRoleState(RoleType type);
	
	public int getNextStateTime(int time);
	
	public boolean processNextState();
	
	public void updateState();
	
	public void processExternalEvents(ArrayList<Event> events);
}
