package WiSAR;

import java.util.Comparator;

import CUAS.Simulator.IData;

public class DataPriorityComparator implements Comparator<IData> {

	@Override
	public int compare(IData arg0, IData arg1) {
		
		int val0;
		int val1;
		if ( arg0 instanceof IPriority ) {
			val0 = ((IPriority) arg0).priority();
		} else
			val0 = 1000;
		
		if ( arg1 instanceof IPriority ) {
			val1 = ((IPriority) arg1).priority();
		} else
			val1 = 1000;
		
		
		if ( val0 < val1 ) {
			return -1;
		} else if ( val0 > val1 ) {
			return 1;
		} else
			return 0;
	}

}
