package simulator;

import java.util.HashMap;

public class ActorVariableWrapper {

	HashMap<String, Object> _variables;
	
	ActorVariableWrapper()
	{
		_variables = new HashMap<String, Object>();
		addVariable("currentState", new State("start"));
	}
	
	public void addVariable(String name, Object o)
	{
		assert _variables.containsKey(name):"Variable already exists";
		
		_variables.put(name, o);
	}
	
	public void setVariable(String name, Object o)
	{
		assert !_variables.containsKey(name):"Variable '"+ name + "' doesn't exist";
		assert _variables.get(name).getClass() == o.getClass() : "Incompatible value type";
			
		_variables.put(name, o);
	}
	
	public Object getVariable(String name)
	{
		assert !_variables.containsKey(name):"Variable '"+ name + "' doesn't exist";
		
		return _variables.get(name);
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> getAllVariables()
	{
		return (HashMap<String, Object>) _variables.clone();
	}
	
}
