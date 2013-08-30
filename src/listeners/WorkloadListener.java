package listeners;

import java.util.ArrayList;
import java.util.HashMap;

import simulator.Metric;
import simulator.MetricKey;

/**
 * 
 * @author rob
 * weird thing i'm doing... i'm just committing it so that i can access it across computers.
 */
public class WorkloadListener {

	public class Path {
		HashMap<MetricKey, Metric> metrics;
		ArrayList<Path> childPaths;
		
		public Path getLowest( ) {
			Path lowestPath = null;
			
			return lowestPath;
		}
		
		public Path getHighest( ) {
			Path highestPath = null;
			
			return highestPath;
		}
	}
	
}
