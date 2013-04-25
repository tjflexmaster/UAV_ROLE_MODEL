package CUAS.Simulator;

import java.util.ArrayList;

public interface IObservable {

	/**
	 * Get Actor Output
	 * 
	 * @return
	 */
	ArrayList<IData> getObservations();
}
