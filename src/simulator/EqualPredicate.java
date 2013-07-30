package simulator;

public class EqualPredicate implements IPredicate {

	Object _data;
	
	EqualPredicate(Object data)
	{
		_data = data;
	}
	
	@Override
	public boolean evaluate(Object data) {
		
		if ( data.equals(data) )
			return true;
		else
			return false;
	}

}
