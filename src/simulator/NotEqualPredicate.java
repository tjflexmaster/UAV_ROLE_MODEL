package simulator;

/**
 * !=
 */
public class NotEqualPredicate<T extends Comparable<? super T>> implements IPredicate {

	Object _left;
	
	NotEqualPredicate(Object left)
	{
		_left = left;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean evaluate(Object right) {
		assert _left.getClass() == right.getClass() : "Data is of different type.";
		if (_left == null || right == null)
			return false;
		if (((T) _left).compareTo((T) right) != 0)
			return true;
		return false;
	}
}
