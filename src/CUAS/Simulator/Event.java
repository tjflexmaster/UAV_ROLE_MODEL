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
		
		switch( (States) state()) {
			case INACTIVE:
				//If the event is possible
				if ( eventPossible() ) {
					//If no next state then create
					if ( nextState() == null ) {
						nextState(States.ACTIVE, sim().duration(new Range(1, 1800)));
						return nextStateTime();
					} else if ( nextStateTime() == sim().getTime() ) {
						state( nextState() );
						resetNextState();
						return getNextTime();
					} else {
						return nextStateTime();
					}
				}
				nextState(null, 0);
				return 0;
			case ACTIVE:
				//Once the event is active we dont need to check if it is possible because it
				//has already been sent out
				if ( nextState() == null ) {
					activateEvent();
					nextState(States.FINISHED, activeDuration());
					return nextStateTime();
				} else if( nextStateTime() == sim().getTime() ) {
					state( nextState() );
					resetNextState();
					return getNextTime();
				}
				return nextStateTime();
			case FINISHED:
				//We always move to Inactive State if we are Finished
				finishEvent();
				state( States.INACTIVE );
				resetNextState();
				return 0;
		}
		//If INACTIVE
		return 0;
	}
	
}
