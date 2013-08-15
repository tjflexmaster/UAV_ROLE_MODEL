package simulator;

import java.util.HashMap;
import java.util.Map.Entry;

import simulator.Metric.MetricEnum;

public class ActorVariableWrapper {

	HashMap<String, Object> _variables;
	private int _workload;
	
	ActorVariableWrapper()
	{
		_variables = new HashMap<String, Object>();
		addVariable("currentState", new State("start",0));
		addVariable("name", "default actor");
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
		Object temp = _variables.get(name);
//		if(temp == null){
//			//TODO add metric for updating a variable that wasn't active
//			Simulator.getSim().addMetric((String)_variables.get("name"), -1, MetricEnum.MEMORY_FIRE);
//		}
		//TODO add metric for changing an active variable
		Simulator.getSim().addMetric(MetricEnum.MEMORY_FIRE);
		_variables.put(name, o);
	}
	
	public Object getVariable(String name)
	{
		assert _variables.containsKey(name):"Variable '"+ name + "' doesn't exist";
		Object temp = _variables.get(name);
		if(temp != null){
			//TODO update metric for referencing active variable
			Simulator.getSim().addMetric(MetricEnum.MEMORY_ACTIVE);
		}else{
			//TODO update metric for referencing a variable
			Simulator.getSim().addMetric(MetricEnum.MEMORY_INACTIVE);
		}
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
