package CUAS.Utils;

public class GlobalTimer {

	private int _last_time = 0;
	private int _time = 0;
	
	public GlobalTimer() { }
	
	public int time() {
		return _time;
	}
	
	public int lastTime() {
		return _last_time;
	}
	
	public void time(int time) {
		if ( time > _time ) {
			_last_time = _time;
			_time = time;
		}
	}
	

}
