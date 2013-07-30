package simulator;

public interface IPredicate {

	/**
	 * Evaluates the given object with an internal object based on the classes predicate.
	 * @param data
	 * @return
	 */
	public boolean evaluate(Object data);
}
