package Utilities;

public class UDO {

	private boolean _active;
	private boolean _temp;
	private String _name;
	
	public UDO(String name){
		
		_name = name;
		_active = false;
		_temp = false;
		
	}

	public boolean isActive() {
		
		return _active;
		
	}

	public void setActive() {
		
		_temp = true;
		
	}
	
	/**
	 * updates the data status
	 */
	public void processData(){
		
		_active = _temp;
		_temp = false;
		
	}
	
}
