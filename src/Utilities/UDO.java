package Utilities;

public class UDO {

	private boolean active;
	private boolean temp;
	private String name;
	
	public UDO(String _name){
		name = _name;
		active = false;
		temp = false;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive() {
		temp = true;
	}
	
	/**
	 * updates the data status
	 */
	public void processData(){
		active = temp;
		temp = false;
	}
	
}
