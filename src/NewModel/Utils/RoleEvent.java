package NewModel.Utils;

import java.util.ArrayList;
import java.util.List;

import NewModel.Roles.RoleState;
import NewModel.Simulation.Simulator;

public class RoleEvent {

	private RoleState _state;
	private ArrayList<DataType> _data = new ArrayList<DataType>();
	
	public RoleEvent(RoleState state) {
		_state = state;
	}
	
	public RoleEvent(RoleState state, DataType data) {
		_state = state;
		_data.add(data);
	}
	
	public RoleEvent(RoleState state, ArrayList<DataType> data) {
		_state = state;
		_data = data;
	}
	
	public RoleState state()
	{
		return _state;
	}
	
	public ArrayList<DataType> data()
	{
		return _data;
	}
	
	
}
