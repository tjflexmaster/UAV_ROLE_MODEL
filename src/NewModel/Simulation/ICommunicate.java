package NewModel.Simulation;

import java.util.ArrayList;

public interface ICommunicate {

	/**
	 * Add Input to the Role
	 * 
	 * @param input
	 */
	void addInput(IInputEnum input);
	void addInputs(ArrayList<IInputEnum> input);
	
	/**
	 * Get Role Output
	 * 
	 * @return
	 */
	ArrayList<IOutputEnum> getOutput();
}
