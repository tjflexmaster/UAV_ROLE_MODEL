package simulator;

import gov.nasa.jpf.vm.Verify;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


import simulator.metrics.MetricContainer;
import simulator.metrics.MetricDisplayPanel;

public class Simulator {
  
  public static String nL = System.getProperty("line.separator");
	
	public enum DebugMode {
	  PROD,
	  DEBUG,
	  DEBUG_VERBOSE
	}
	
	public enum DurationMode {
		MIN,
		MAX,
		MEAN,
		MIN_MAX,
		MIN_MAX_MEAN
	}
	
	private ITeam _team;
	private IDeltaClock _clock;
	private HashMap<IActor, ITransition> _ready_transitions = new HashMap<IActor, ITransition>();
	private ArrayList<IActor> _active_events = new ArrayList<IActor>();
	private DebugMode _mode;
	private DurationMode _duration;
	private Random _random;
	
	private Vector<MetricDisplayPanel> _panels = new Vector<MetricDisplayPanel>();	
	JTextArea _txtArea;
	
	//Singleton variables
	private boolean _setup = false;
	private static Simulator _instance = null;
	private Date _date = null;
	
	/**
	 * Get simulator singleton
	 * @return
	 */
	public static synchronized Simulator getSim() {
        if (_instance == null) {
            _instance = new Simulator();
        }
        return _instance;
	}
	
	private Simulator() {
		_clock = new DeltaClock();
		_date = new Date();
	}
	
	public void setup(ITeam team, DebugMode mode, DurationMode duration)
	{
		_setup = false;
		_clock = new DeltaClock();
		
		_team = team;
		_mode = mode;
		_duration = duration;
		
		initializeRandom();
		_setup = true;
		
		//Show charts
    JFrame f = new JFrame();
    f.setSize(1900,1100);
    f.setExtendedState(f.MAXIMIZED_BOTH);
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    JPanel container = new JPanel();
    container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
    _panels = team.getPanels();
    for(MetricDisplayPanel p : _panels) {
      container.add(p);
    }
    
    _txtArea = new JTextArea();
    JScrollPane textScroll = new JScrollPane(_txtArea);
    container.add(textScroll);
    
    JScrollPane scrollPane = new JScrollPane(container);
    f.add(scrollPane);
    f.setVisible(true);
    
//    try {
//      Thread.sleep(5000);
//    } catch (InterruptedException e) {
//      // do nothing
//    }
	}
	
	/**
	 * Main Simulation method.
	 */
	public void run()
	{
		assert _setup : "Simulator not setup correctly";
	  String out = "";
		do {
			//Get all event and team transitions
			loadTransitions();
			
			//Get some metrics
			try {
        Thread.sleep(500);
        MetricContainer c = new MetricContainer();
        _team.setMetrics(c);
        for(MetricDisplayPanel p : _panels) {
          p.updateScrollBarSize(1);
        }
      } catch (InterruptedException e) {
        // do nothing
      }
      
			//Advance Time
			_clock.advanceTime();
			//debug mode
			if ( mode().compareTo(DebugMode.DEBUG) >= 0) {
			  logMsg("Time: "+ _clock.getElapsedTime());
			}
			
			//Process Ready Transitions
			_ready_transitions.clear();
			_ready_transitions.putAll(_clock.getReadyTransitions());
			for(Entry<IActor, ITransition> e : _ready_transitions.entrySet()){
				ITransition t = (ITransition) e.getValue();
				if (Simulator.getSim().mode().compareTo(DebugMode.DEBUG) >= 0 ) {
				  logMsg("Fired Transition:\t"+t.toString());
				}
				t.fire();
			}
			
			//Get some metrics
      try {
        Thread.sleep(1);
        MetricContainer c = new MetricContainer();
        _team.setMetrics(c);
        for(MetricDisplayPanel p : _panels) {
          p.updateScrollBarSize(1);
        }
      } catch (InterruptedException e) {
        // do nothing
      }
			if (Simulator.getSim().mode().compareTo(DebugMode.DEBUG) >= 0 ) {
			  logMsg("-----------------------------------------------");
			}
		} while (!_ready_transitions.isEmpty());
		
	}

	private void loadTransitions()
	{
		//Get Transitions from the Events
		for(IEvent e : _team.getEvents() ) {
			
			ITransition t = e.getEnabledTransition();
			if ( _clock.getActorTransition((IActor) e) == null ) {
				if ( t != null && !e.isFinished() ) {
					_clock.addTransition((IActor) e, t, random(t.getDurationRange().min(),t.getDurationRange().max()));
					e.decrementCount();
				}
			} else {
				if ( t == null ) {
					_clock.removeTransition((IActor) e);
				}
			}
		}
		
		//Get Transitions from the Actor
		HashMap<IActor, ITransition> transitions = _team.getActorTransitions();
		for(Map.Entry<IActor, ITransition> entry : transitions.entrySet() ) {
			ITransition t = entry.getValue();
			_clock.addTransition(entry.getKey(), t, duration(t.getDurationRange()));
		}
		
	}

	/** Returns the simulation mode */
	public DebugMode mode()
  {
    return _mode;
  }
	
	/**
	 * HELPER METHODS
	 */
	private void initializeRandom() {
		_random = new Random();
		_random.setSeed(0); //Always use the same seed
	}
	
	public int duration(Range range)
	{
		switch(_duration) {
			case MIN:
				return range.min();
			case MAX:
				return range.max();
			case MEAN:
				return range.mean();
			case MIN_MAX:
				if ( random(1) == 0 )
					return range.min();
				else
					return range.max();
			case MIN_MAX_MEAN:
				int val = random(2);
				if ( val == 0 )
					return range.min();
				else if (val == 2)
					return range.max();
				else 
					return range.mean();
			default:
				return 1;
		}
	}
	
	public int random(Range range)
	{
		return random(range.min(), range.max());
	}
	
	public int random(int val)
	{
		return random(0, val);
	}
	
	public int random(int min, int max)
	{
		return Verify.getInt(min, max);
	}
	
	public Integer getClockTime() {
		return _clock.getElapsedTime();
	}
	
	public ITransition getActorTransition(IActor actor) {
	  return _clock.getActorTransition(actor);
	}
	
	public void logMsg(String msg)
	{
	  if ( _txtArea != null )
	    _txtArea.append(msg+nL);
	  System.out.println(msg);
	}
}
