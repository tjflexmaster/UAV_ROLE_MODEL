package simulator;

import java.util.HashMap;
import java.util.Map.Entry;

public class ActorVariableWrapper {

	HashMap<String, Object> _variables;
	private int _workload;
	
	ActorVariableWrapper()
	{
		_variables = new HashMap<String, Object>();
		addVariable("currentState", new State("start",0));
	}
	
	public void addVariable(String name, Object o)
	{
		assert !_variables.containsKey(name):"Variable already exists";

		_variables.put(name, o);
	}
	
	public void setVariable(String name, Object o)
	{
		assert _variables.containsKey(name):"Variable '"+ name + "' doesn't exist";
		assert _variables.get(name).getClass() == o.getClass() : "Incompatible value type";
			
		_variables.put(name, o);
	}
	
	public Object getVariable(String name)
	{
		assert _variables.containsKey(name):"Variable '"+ name + "' doesn't exist";
		
		return _variables.get(name);
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> getAllVariables()
	{
		return (HashMap<String, Object>) _variables.clone();
	}
	
	public int getWorkload(){
		_workload = 0;
//		int max = _variables.size();
		for(Entry<String, Object> i : _variables.entrySet()){
			if(i.getValue() != null){
				if(i.getValue() instanceof Boolean && !(Boolean) i.getValue()){
					continue;
				} else if(i.getValue() instanceof Integer && ((Integer)i.getValue()) == 0){
					continue;
				}
				_workload++;
			}
		}
		return _workload;
	}
}
