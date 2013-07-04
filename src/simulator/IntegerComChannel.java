package simulator;

public enum IntegerComChannel implements IComChannel {
	
	;

	private Integer _value;
	
	IntegerComChannel(Integer value){
		_value = value;
	}
	
	@Override
	public void set(Object object) {
		assert(object.getClass() == Integer.class);
		
		_value = (Integer) object;
	}

	@Override
	public Integer get() {
		return _value;
	}

}
