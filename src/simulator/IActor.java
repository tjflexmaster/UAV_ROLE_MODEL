package simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import org.jfree.chart.JFreeChart;

import com.objectplanet.chart.Chart;

import simulator.metrics.IMetrics;
import simulator.metrics.MetricDisplayPanel;

public interface IActor extends IMetrics {

	/**
	 * Returns the Actor transition and the transitions of any sub-actors.
	 * Note: An Actor can only take 1 transition.  If more are needed they should be placed 
	 * in sub-actors and their transitions should be passed through this method.
	 * @return
	 */
	HashMap<IActor, ITransition> getTransitions();
	
	/**
	 * Returns the unique name of the Actor.
	 * @return
	 */
	String name();

	/**
	 * 
	 * @return the current state of the actor
	 */
	IState getCurrentState();
	
	Vector<JFreeChart> getCharts();
	
	Vector<MetricDisplayPanel> getPanels();
	
}
