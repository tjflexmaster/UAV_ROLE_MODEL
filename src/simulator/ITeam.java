package simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import org.jfree.chart.JFreeChart;

import com.objectplanet.chart.Chart;

import simulator.metrics.IMetrics;
import simulator.metrics.MetricDisplayPanel;

public interface ITeam extends IMetrics {
	
	/**
	 * Return a list of current actor transitions
	 * @return
	 */
	HashMap<IActor, ITransition> getActorTransitions();
	
	
	/**
	 * Return a list of current event transitions
	 * @return
	 */
	ArrayList<IEvent> getEvents();


//	HashMap<Actor, Integer> getWorkload();
	
	ComChannelList getAllChannels();

	/**
	 * 
	 * @param actor's name
	 * @return the current state of the referenced actor
	 */
	String getStateName(String actor);

	
	Vector<JFreeChart> getCharts();
	
	Vector<MetricDisplayPanel> getPanels();
}
