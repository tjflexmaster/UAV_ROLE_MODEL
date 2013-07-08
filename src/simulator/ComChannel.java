package simulator;

public class ComChannel<T> {
	private Class<T> classType;
	T _value;
	String _name;
	
	public ComChannel(String name)
	{
		_name = name;
	}
	
	public ComChannel(String name, T value)
	{
		_name = name;
		_value = value;
	}
	
	
	@SuppressWarnings("unchecked")
	public void set(Object value) 
	{
//		assert (value instanceof classType):"Invalid ComChannel datatype.";
		_value = (T) value;
	}
	
	public T get()
	{
		return _value;
	}
	
	public String name()
	{
		return _name;
	}
	
	public boolean isEqual(T value)
	{
		return _value.equals(value);
	}

	@Override
	public boolean equals(Object obj)
	{
		if ( obj == this )
			return true;
		
		if (obj instanceof ComChannel) {
			if ( ((ComChannel<?>) obj).name() == this._name )
				return true;
		}
		
		return false;
	}
	
	@Override
	public int hashCode()
	{
		return _name.hashCode();
	}
}
