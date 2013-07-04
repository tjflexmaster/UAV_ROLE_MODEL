package simulator;

public interface IComChannel {
	
	/**
	 * Returns the unique name of this com channel
	 * @return
	 */
	public String name();
	/**
	 * works as a basic java setter
	 * @param object redefines the object
	 */
	public void set(Object object);
	/**
	 * works as a basic java getter
	 * @return the object
	 */
	public Object get();
	
}
