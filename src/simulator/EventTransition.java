package simulator;

import java.util.HashMap;
import java.util.Map.Entry;

public class EventTransition implements ITransition {

	protected ComChannelList _inputs;
	private ComChannelList _outputs;
	private HashMap<String, Object> _temp_outputs;
	
	public EventTransition (ComChannelList inputs, ComChannelList outputs) {
		
		_inputs = new ComChannelList();
		for(Entry<String, ComChannel<?>> entry : inputs.entrySet()){
			ComChannel<?> channel = entry.getValue();
			assert channel.type() == ComChannel.Type.EVENT : "Incompatible ComChannel Type used for Event transition input";
			_inputs.add(entry.getValue());
		}
		_outputs = new ComChannelList();
		for(Entry<String, ComChannel<?>> entry : outputs.entrySet()){
			ComChannel<?> channel = entry.getValue();
			assert channel.type() == ComChannel.Type.EVENT : "Incompatible ComChannel Type used for Event transition output";
			_outputs.add(entry.getValue());
		}
		buildTempOutput();
	}
	
	private void buildTempOutput()
	{
		_temp_outputs = new HashMap<String, Object>();
		for( ComChannel<?> c : _outputs.values()) {
			_temp_outputs.put(c.name(), null);
		}
	}
	
	protected void setTempOutput(String varname, Object value)
	{
		assert _temp_outputs.containsKey(varname): "Cannot set temp output, variable does not exist";
		_temp_outputs.put(varname, value);
	}
	
	@Override
	public void fire() {
		if(!_temp_outputs.isEmpty()){
			for(ComChannel<?> output : _outputs.values()){
				Object temp = _temp_outputs.get(output.name());
				output.set(temp);
				_temp_outputs.put(output.name(), null);
			}
		}
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public Range getDurationRange() {
		return new Range();
	}

	@Override
	public int priority() {
		return 1;
	}

}
