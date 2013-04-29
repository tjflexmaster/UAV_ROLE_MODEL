package CUAS.Simulator;


import CUAS.Utils.Range;

public abstract class Event extends State implements IEvent {

	protected enum States implements IStateEnum
	{
		INACTIVE,
		PRIMED,
		ACTIVE,
		FINISHED
	}
	
	protected abstract boolean eventPossible();
	protected abstract void activateEvent();
	protected abstract void finishEvent();
	protected abstract int activeDuration();
	
	protected int _count = 0;
	
	@Override
	public int getCount() 
	{
		return _count;
	}
	
	@Override
	public int getNextTime() 
	{
		if ( nextState() != null ) 
			return nextStateTime();
		else
			return 0;
	}
	
	@Override
	public void processNextState()
	{
		if ( nextStateTime() == sim().getTime() ) {
			state( nextState() );
			
			//Send required output
			switch( (States) state()) {
				case INACTIVE:
					nextState(null, 0);
					break;
				case ACTIVE:
					//Once the event is active we dont need to check if it is possible because it
					//has already been sent out
					activateEvent();
					nextState(States.FINISHED, activeDuration());
					break;
				case FINISHED:
					//We always move to Inactive State if we are Finished
					finishEvent();
					nextState(States.INACTIVE, 1);
					_count--; //Lower the count
					break;
				
			}//end switch
			
		}//end if
	}
		
	
	@Override
	public void processInputs()
	{
		switch( (States) state()) {
			case INACTIVE:
				//If the event is possible
				if ( eventPossible() ) {
					//If no next state then create
					if ( nextState() == null ) {
						nextState(States.ACTIVE, sim().duration(new Range(1, 1800)));
					} 
				} else {
					nextState(null, 0);
				}
				break;
			case ACTIVE:
				//Once the event is active we dont need to check if it is possible because it
				//has already been sent out
				if ( !eventPossible() ) {
					nextState(States.FINISHED, 1);
				}
				break;
			case FINISHED:
				//Do nothing the event will be inactive soon
				break;
			default:
				nextState(States.INACTIVE, 1);
				break;
		}//end switch
	}
	
}
