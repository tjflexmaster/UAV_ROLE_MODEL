package CUAS.Simulator;


public interface IEvent {

	public int getNextTime();
	
	public void processNextState();
	
	public void processInputs();
	
	public int getCount();
	
}
