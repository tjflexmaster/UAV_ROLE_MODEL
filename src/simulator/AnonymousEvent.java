package simulator;

public class AnonymousEvent extends Event {

	AnonymousEvent(String name, int count, EventTransition transition)
	{
		_transition = transition;
	}
	
	@Override
	public ITransition getEnabledTransition() {
		
		if ( _transition.isEnabled() )
			return _transition;
		else
			return null;
	}


}
