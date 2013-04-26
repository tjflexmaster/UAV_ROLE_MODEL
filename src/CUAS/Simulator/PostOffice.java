package CUAS.Simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class PostOffice  {
	
	private HashMap<String,ArrayList<IData>> POBox;
	
	
	public PostOffice() {
		POBox = new HashMap<String,ArrayList<IData>>();
	}
	
	

	public void addOutput(IData data, String name){
		if(POBox.containsKey(name)){
			POBox.get(name).add(data);
		}else{
			ArrayList<IData> inputs = new ArrayList<IData>();
			inputs.add(data);
			POBox.put(name, inputs);
		}
	}
	
	public void addOutputs(ArrayList<IData> data, String name){
		if(POBox.containsKey(name)){
			POBox.get(name).addAll(data);
		}else{
			ArrayList<IData> inputs = new ArrayList<IData>();
			inputs.addAll(data);
			POBox.put(name, inputs);
		}
	}
	
	public void addInputs(){
		for(Map.Entry<String, ArrayList<IData>> pair : POBox.entrySet()){
			sim().addInput(pair.getKey(), pair.getValue());
		}
	}



	/**
	 * Convenience method so that getInstance does not have to be called over and over
	 * @return
	 */
	protected Simulator sim()
	{
		return Simulator.getInstance();
	}

}
