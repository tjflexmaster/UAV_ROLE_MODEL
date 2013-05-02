package WiSAR;

import java.util.ArrayList;

import CUAS.Simulator.IData;
import CUAS.Simulator.IStateEnum;

public class MemoryObject {

	private IStateEnum _state;
	private String _receiver;
	private ArrayList<IData> _data;
	
	public MemoryObject(IStateEnum state)
	{
		_state = state;
		_receiver = null;
		_data = new ArrayList<IData>();
	}
	
	public MemoryObject(IStateEnum state, String receiver, IData data)
	{
		_state = state;
		_receiver = receiver;
		_data = new ArrayList<IData>();
		_data.add(data);
	}
	
	public MemoryObject(IStateEnum state, String receiver, ArrayList<IData> data)
	{
		_state = state;
		_receiver = receiver;
		_data = new ArrayList<IData>();
	}
	
	public IStateEnum state() {
		return _state;
	}
	public void state(IStateEnum _state) {
		this._state = _state;
	}
	public String receiver() {
		return _receiver;
	}
	public void receiver(String _receiver) {
		this._receiver = _receiver;
	}
	public ArrayList<IData> data() {
		return _data;
	}
	public void data(ArrayList<IData> _data) {
		this._data = _data;
	}
	
	
}
