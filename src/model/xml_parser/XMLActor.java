package model.xml_parser;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Vector;
import java.util.Map.Entry;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.category.SlidingCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.objectplanet.chart.Chart;
import com.objectplanet.chart.LineChart;

import simulator.Actor;
import simulator.ComChannel;
import simulator.ComChannelList;
import simulator.IActor;
import simulator.IState;
import simulator.ITransition;
import simulator.Memory;
import simulator.MemoryList;
import simulator.Simulator;
import simulator.State;
import simulator.TempComChannel;
import simulator.metrics.IMetrics;
import simulator.metrics.MetricContainer;
import simulator.metrics.MetricDisplayPanel;
import simulator.metrics.StateMetrics;

public class XMLActor extends Actor implements IActor {
  
  private ComChannelList m_inputChannels = new ComChannelList();
  private ComChannelList m_outputChannels = new ComChannelList();
  
  private MemoryList m_memory = new MemoryList();
  
//  private LineChart _chart = new LineChart();
//  private double[] _sampleChannelConflicts = new double[] {};
//  private Color[] _sampleColors = new Color[] {new Color(0xFF0000)};
//  private String[] _sampleLabels = new String[] {"Channel Conflicts"};
  
  //Create JFreeChart
//  private XYSeries _series1 = new XYSeries("Test");
  DefaultCategoryDataset _cog_dataset;
  DefaultCategoryDataset _cog_output_dataset;
  DefaultCategoryDataset _alg_dataset;
  DefaultCategoryDataset _temp_dataset;
  DefaultCategoryDataset _work_dataset;
  
  MetricDisplayPanel _cog_panel;
  MetricDisplayPanel _cog_output_panel;
  MetricDisplayPanel _alg_panel;
  MetricDisplayPanel _temp_panel;
  
  private String lastState;
  private String lastCategory;
  
  private int numOfTransitionsFired = 0;
  private double lastTransitionTime = 0;
  
	public XMLActor(String name)
	{
		setName(name);
		
		m_inputChannels = new ComChannelList();
		m_outputChannels = new ComChannelList();
		
		//Setup dataset
		_cog_dataset = new DefaultCategoryDataset();
		_cog_output_dataset = new DefaultCategoryDataset();
		_alg_dataset = new DefaultCategoryDataset();
		_temp_dataset = new DefaultCategoryDataset();
		_work_dataset = new DefaultCategoryDataset();
		
		//Setup DisplayPanels
		_cog_panel = new MetricDisplayPanel(name()+" Cognitive Input", _cog_dataset);
		_cog_panel.setMinimumRangeSize(6);
		_cog_output_panel = new MetricDisplayPanel(name()+" Cognitive Output", _cog_output_dataset);
    _alg_panel = new MetricDisplayPanel(name()+" Algorithmic", _alg_dataset);
    _temp_panel = new MetricDisplayPanel(name()+" Temporal", _temp_dataset);
    _temp_panel.setMinimumRangeSize(1);
	}
	

	public void addState(IState state)
	{
		super.add(state);
	}
	
	public void setState(IState state)
	{
	  assert _states.contains(state):"Start state not available. Actor:" + 
        this.name() + " State:" + state.getName();
    _currentState = (State) state;
    lastState = state.getName();
    lastCategory = state.getName();
    numOfTransitionsFired++;
    lastTransitionTime = Simulator.getSim().getClockTime();
	}
	
	public void addInputChannel(ComChannel channel)
	{
	  assert channel != null : "Cannot add a NULL channel to Actor("+ this.name() + ")";
	  m_inputChannels.add(channel);
	}
	
	public void addOutputChannel(ComChannel channel)
	{
	  m_outputChannels.add(channel);
	}
	
	public void addMemory(Memory memory)
	{
	  m_memory.add(memory);
	}
	
	public ComChannel getInputComChannel(String name)
	{
	  return m_inputChannels.get(name);
	}
	
	public ComChannel getOutputComChannel(String name)
  {
    return m_outputChannels.get(name);
  }
	
	public Memory getMemory(String name)
  {
    return m_memory.get(name);
  }
	
	/////////////////////////Metrics//////////
	
  @Override
  public Vector<JFreeChart> getCharts()
  {
    Vector<JFreeChart> result = new Vector<JFreeChart>();
    return result;
  }
  
  @Override
  public Vector<MetricDisplayPanel> getPanels()
  {
    Vector<MetricDisplayPanel> result = new Vector<MetricDisplayPanel>();
    result.add(_cog_panel);
    result.add(_cog_output_panel);
    result.add(_alg_panel);
    result.add(_temp_panel);
//    result.add(new MetricDisplayPanel(name()+" Workload", _work_dataset));
    return result;
  }
	
	@Override
	public void setMetrics(MetricContainer c)
	{
	  c.actorName = name();
	  c.actorInputChannels = m_inputChannels.size();
	  c.actorOutputChannels = m_outputChannels.size();
	  c.actorTotalMemory = m_memory.size();
	  c.actorTotalStates = this.getStates().size();
	  
//	  c.actorActiveMemory = 0;
//	  //Set memory metrics
//	  for(Entry<String, Memory> e : m_memory.entrySet())
//    {
//	    if(e.getValue().layer().value() != null )
//	      c.actorActiveMemory++;
//    }
	  
//	  //Set input channel metrics
//	  for(Entry<String, ComChannel> e : m_inputChannels.entrySet()) {
//	    if(e.getValue().isActive())
//	      c.actorActiveInputChannels++;
//	    
//	    c.actorActiveInputLayers += e.getValue().activeLayers();
//	  }
	  
//	  //Set output channel metrics
//	  for(Entry<String, ComChannel> e : m_outputChannels.entrySet()) {
//      if(e.getValue().isActive())
//        c.actorActiveOutputChannels++;
//      
//      c.actorActiveOutputLayers += e.getValue().activeLayers();
//    }
	  
	  //Set State metrics
	  this.getCurrentState().setMetrics(c);
	  
//	  //Set active transition
//	  ITransition activeTrans = Simulator.getSim().getActorTransition(this);
//	  if ( activeTrans != null ) {
//	    MetricContainer t = new MetricContainer();
//  	  activeTrans.setMetrics(t);
//  	  c.actorActiveTransition = t;
//	  }
	  
	  //Update team sample sets
	  int time = Simulator.getSim().getClockTime();
	  
	  //Create category from the state
	  String category = lastCategory;
	  if ( getCurrentState().getName().equals(lastState) ) {
	    lastState = getCurrentState().getName();
	    category = lastState+"_"+String.valueOf(time);
	    category = category.equals(lastCategory) ? category+"_2":category;
	    lastCategory = category;
	  }
	  //Update the tooltips
    for(MetricDisplayPanel p : getPanels()) {
      _cog_panel.setCategoryTooltip(category, category);
    }
	  
	  StateMetrics m = c.currentStateMetrics;
	  ITransition t = Simulator.getSim().getActorTransition(this);
	  
	  ////////////Cognitive Datasets/////////////////////////////
	  double channel_conflicts = Math.max(m.audioChannelInputs-1, 0) + 
        Math.max(m.visualChannelInputs-1,0);
	  double channel_count = m.activeChannelsRead;
	  double input_load = m.layersRead * m.channelTypes;
	  
    _cog_dataset.addValue(channel_conflicts, 
        "Channel Conflicts", category);
    _cog_dataset.addValue(channel_count, 
        "Channel Reads", category);
    _cog_dataset.addValue(input_load, 
        "Input Load", category);
    _cog_dataset.addValue(channel_conflicts + channel_count + input_load, 
        "Total", category);
    
	  
	  ////////////////////////////////////////////////////////////
    
    ////////////Cognitive Output Datasets///////////////////////
    
    ComChannelList activeOutputChannels = new ComChannelList();
    double output_layers = 0;
    if ( t != null ) {
      ArrayList<TempComChannel> tcList = t.getTempOutputChannels();
//      if ( tcList != null ) {
        for( TempComChannel tc : tcList) {
          if ( tc.value() != null ) {
            activeOutputChannels.add(tc.channel());
            output_layers++;
          }
        }
//      }
    }
    ComChannelList uniqueOutputChannels = activeOutputChannels.getUniqueChannels();
    double output_channels = uniqueOutputChannels.size();
    double output_types = 0;
    double output_conflicts = 0;
    double output_audio = uniqueOutputChannels.countChannels(ComChannel.Type.AUDIO);
    double output_visual = uniqueOutputChannels.countChannels(ComChannel.Type.VISUAL);
    double output_tactile = uniqueOutputChannels.countChannels(ComChannel.Type.TACTILE);
    if ( output_audio > 0) {
      output_types++;
      output_conflicts += output_audio - 1;
    }
    if ( output_visual > 0 ) {
      output_types++;
      output_conflicts += output_visual - 1;
    }
    if ( output_tactile > 0 ) {
      output_types++;
      output_conflicts += output_tactile - 1;
    }
    double output_load = output_layers * output_types;
    
    _cog_output_dataset.addValue(output_conflicts, 
        "Channel Conflicts", category);
    _cog_output_dataset.addValue(output_channels, 
        "Channel Outputs", category);
    _cog_output_dataset.addValue(output_load, 
        "Output Load", category);
    _cog_output_dataset.addValue(output_conflicts + output_channels + output_load, 
        "Total", category);
    
    ////////////////////////////////////////////////////////////
	  
	  ////////////Algorithmic Datasets////////////////////////////
	  double dec_complexity = 0;
	  double output_complexity = 0;
	  double dur_complexity = 0;
	  
    if ( t != null ) {
      output_complexity = t.getTempOutputChannels().size();
      
      //Control the size of the dur complexity
      int dur = Simulator.getSim().duration(t.getDurationRange());
      double offsetDur = Math.max(dur/60, 1);
      dur_complexity = Math.log(offsetDur);
    }
    
    dec_complexity = this.getCurrentState().getEnabledTransitions().size();
	  
	  _alg_dataset.addValue(dec_complexity, 
	      "Decision Complexity", category);
	  _alg_dataset.addValue(output_complexity, 
        "Output Complexity", category);
	  _alg_dataset.addValue(dur_complexity, 
        "Duration Complexity", category);
	  
	  _alg_dataset.addValue(dec_complexity + output_complexity + dur_complexity, 
        "Total", category);
	  ////////////////////////////////////////////////////////////
	  
	  
	  //////////////////TEMPORAL Metrics/////////////////////////
	  double transition_rate = 1 - (time - lastTransitionTime / (time+1));
	  double active_rate = m.channelsRead > 0 ? m.activeChannelsRead / m.channelsRead : 0;
    _temp_dataset.addValue(transition_rate, 
        "Transition Ratio", category);
    //Output complexity
    _temp_dataset.addValue(active_rate, 
        "Active Input Ratio", category);

    /////////////////Workload///////////////////////////
    double cognitive = 0;
    double algorithmic = 0;
    double temporal = 0;
    
	}
	
}
