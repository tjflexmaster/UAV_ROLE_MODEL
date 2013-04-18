package NewModel.Simulation;

import java.util.ArrayList;

import NewModel.Roles.RoleState;
import NewModel.Roles.RoleType;

public interface ITeam {

//	public RoleState getRoleState(RoleType type);
	
	public ICommunicate getRole(String role_name);
	
	public int getNextStateTime(int time);
	
	public boolean processNextState();
	
	public void updateState();
	
//	public boolean canProcessEvent(String type);
	
	public void processEvent(String type);
}
