package WiSAR.submodule;

import CUAS.Simulator.Actor;
import CUAS.Simulator.IData;
import CUAS.Simulator.IStateEnum;

public class UAVSignal extends Actor {

	public enum Outputs implements IData {
		SIGNAL_LOST,
		SIGNAL_OK
	}
	
	public enum States implements IStateEnum{
		OK,
		LOST
	}
	
	@Override
	public void processNextState() {
		// TODO Auto-generated method stub

	}

	@Override
	public void processInputs() {
		// TODO Auto-generated method stub

	}

}
