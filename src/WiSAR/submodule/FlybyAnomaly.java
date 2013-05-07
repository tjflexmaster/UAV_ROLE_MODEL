package WiSAR.submodule;

import java.util.ArrayList;

import CUAS.Simulator.Actor;
import CUAS.Simulator.IData;
import CUAS.Simulator.IStateEnum;
import CUAS.Simulator.Simulator;
import WiSAR.Actors;
import WiSAR.Durations;
import WiSAR.Agents.OperatorGUIRole;
import WiSAR.Agents.OperatorRole;
import WiSAR.Agents.UAVRole;
import WiSAR.Agents.VideoGUIRole;

public class FlybyAnomaly extends Actor {

	boolean target = false;
	int _dur = 0;
	private int _start_time = 0;
	private int _pause_time = 0;
	private boolean _initialized = false;
	public enum Outputs implements IData{
		FLYBY_ANOMALY_T,
		FLYBY_ANOMALY_F,
	}
	public enum States implements IStateEnum{
		IDLE,
		PAUSED,
		ANOMALY_NOT_SEEN,
		ANOMALY_SEEN,
		END_FLYBY,
	}
	
	public FlybyAnomaly(){
		name(Actors.FLYBY_ANOMALY.name());
		nextState(States.IDLE,1);
	}

	@Override
	public void processNextState() {
        if ( nextStateTime() != Simulator.getInstance().getTime() ) {
        	setObservations();
            return;
        }
        
        if(!_initialized){
        	resetFlybyTime();
        	_initialized = true;
        }
        
        state(nextState());
        switch((States)nextState()){
	        case ANOMALY_NOT_SEEN:
	        	_start_time = sim().getTime();
	        	nextState(States.ANOMALY_SEEN,getRemainingTime());
	        	break;
	        case PAUSED:
	        	_pause_time = sim().getTime();
	        	pauseSearch();
	        	nextState(null,0);
	        	break;
	        case END_FLYBY:
	        	nextState(States.IDLE,1);
	        	_initialized = false;
	        	break;
	    	default:
	    		nextState(null,0);
        }
        
		setObservations();
	}

	@Override
	public void processInputs() {
		ArrayList<IData> input = sim().getInput(this.name());
		ArrayList<IData> uav = sim().getObservations(Actors.UAV.name());
		
		switch((States)state()){
		case IDLE:
			if(input.contains(OperatorRole.Outputs.OP_FLYBY_START_F)){
				target = false;
				nextState(States.ANOMALY_NOT_SEEN,1);
<<<<<<< Upstream, based on master
			}else if(input.contains(OperatorRole.Outputs.OP_FLYBY_START_F)){
=======
			}else if(input.contains(OperatorRole.Outputs.OP_FLYBY_START_T)){
>>>>>>> 81e367f partial of the UAVFlyby subactor.
				target = true;
				nextState(States.ANOMALY_NOT_SEEN,1);
			}
			break;
		case ANOMALY_NOT_SEEN:
<<<<<<< Upstream, based on master
			if( !isFlybyMode(uav) ){
=======
			if(input.contains(OperatorRole.Outputs.OP_LAND)){
				pauseSearch();
>>>>>>> 81e367f partial of the UAVFlyby subactor.
				nextState(States.PAUSED,1);
			}
			if(input.contains(OperatorRole.Outputs.OP_FLYBY_END)){
				nextState(States.IDLE,1);
			}
			break;
		case PAUSED:
<<<<<<< Upstream, based on master
			if( isFlybyMode(uav) ){
=======
			if(input.contains(OperatorRole.Outputs.OP_RESUME)){
				if(_dur > 0){
					nextState(States.ANOMALY_NOT_SEEN,1);
				}else{
					nextState(States.ANOMALY_SEEN,1);
				}
			} else if(uav.contains(UAVRole.Outputs.UAV_FLYING_FLYBY)){
>>>>>>> 81e367f partial of the UAVFlyby subactor.
				if(_dur > 0){
					nextState(States.ANOMALY_NOT_SEEN,1);
				}else{
					nextState(States.ANOMALY_SEEN,1);
				}
			}
<<<<<<< Upstream, based on master
			if(input.contains(OperatorRole.Outputs.OP_FLYBY_END)){
				nextState(States.IDLE,1);
			}
=======
>>>>>>> 81e367f partial of the UAVFlyby subactor.
			break;
		case ANOMALY_SEEN:
			if(input.contains(OperatorRole.Outputs.OP_FLYBY_END)){
				nextState(States.IDLE,1);
			}
			break;
		default:
		}
	}
	
	private void pauseSearch()
	{
<<<<<<< Upstream, based on master
		//Change the time by the amount of time used
		_dur -= (_pause_time - _start_time);
=======
		//Change the time till anomaly sighting by the time already passed
		_dur -= (sim().getTime() - _start_time);
>>>>>>> 81e367f partial of the UAVFlyby subactor.
	}
	
	private int getRemainingTime()
	{
		return Math.max(0, ((_start_time + _dur) - sim().getTime()) );
	}
	
	private void resetFlybyTime(){
		_dur = sim().duration(Durations.FLYBY_FIND_ANOMALY.range());
	}
	
	private void setObservations(){
		if(state() == States.ANOMALY_SEEN){
			if(target){
				sim().addObservation(Outputs.FLYBY_ANOMALY_T, this.name());
			}else{
				sim().addObservation(Outputs.FLYBY_ANOMALY_F, this.name());
			}
		}
	}
	
	private boolean isFlybyMode(ArrayList<IData> uav_observations)
	{
		UAVRole.Outputs uav_state = UAVRole.Outputs.UAV_READY;
		for( IData data : uav_observations ) {
			if ( data instanceof UAVRole.Outputs ) {
				uav_state = (UAVRole.Outputs) data;
				break;
			}
		}
		
		switch( uav_state ) {
			case UAV_FLYING_FLYBY:
				return true;
			default:
				return false;
		}
	}
}
