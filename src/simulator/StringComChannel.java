package simulator;

public enum StringComChannel implements IComChannel {
	
	;

	private String _value;
	
	@Override
	public void set(Object object) {
		assert(object.getClass() == String.class);
		
		_value = (String) object;
	}

	@Override
	public String get() {
		return _value;
	}

}
