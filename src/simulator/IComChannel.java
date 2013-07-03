package simulator;

public interface IComChannel<T> {

	/**
	 * Returns the unique name of this com channel
	 * @return
	 */
	String name();
	
	/**
	 * Returns the stored value of this com channel
	 * @return
	 */
	T value();
}
