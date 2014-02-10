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
import simulator.TempMemory;
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
  DefaultCategoryDataset _resource_in_dataset;
  DefaultCategoryDataset _resource_out_dataset;
  DefaultCategoryDataset _decision_dataset;
  DefaultCategoryDataset _temporal_dataset;
  DefaultCategoryDataset _workload_dataset;
  
  MetricDisplayPanel _resource_in_panel;
  MetricDisplayPanel _resource_out_panel;
  MetricDisplayPanel _decision_panel;
  MetricDisplayPanel _temporal_panel;
  MetricDisplayPanel _workload_panel;
  
  private boolean _showMetrics;
  
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
		_resource_in_dataset = new DefaultCategoryDataset();
		_resource_out_dataset = new DefaultCategoryDataset();
		_decision_dataset = new DefaultCategoryDataset();
		_temporal_dataset = new DefaultCategoryDataset();
		_workload_dataset = new DefaultCategoryDataset();
		
		//Setup DisplayPanels
		_resource_in_panel = new MetricDisplayPanel(name()+" Resource Input Workload", _resource_in_dataset);
		_resource_out_panel = new MetricDisplayPanel(name()+" Resource Output Workload", _resource_out_dataset);
		_decision_panel = new MetricDisplayPanel(name()+" Decision Workload", _decision_dataset);
		_temporal_panel = new MetricDisplayPanel(name()+" Temporal Workload", _temporal_dataset);
		_temporal_panel.setMinimumRangeSize(1);
    _workload_panel = new MetricDisplayPanel(name()+" Overall Workload", _workload_dataset);
    
    showMetrics(false);
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
	
	public void showMetrics(boolean show)
	{
	  _showMetrics = show;
	}
	
	public boolean showMetrics()
	{
	  return _showMetrics;
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
    if ( showMetrics() ) {
      result.add(_resource_in_panel);
      result.add(_resource_out_panel);
      result.add(_decision_panel);
  //    result.add(_temporal_panel);
      result.add(_workload_panel);
    }
    return result;
  }
	
	@Override
	public void setMetrics(MetricContainer c)
	{
	  if ( !showMetrics() )
	    return;
	  
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
      p.setCategoryTooltip(category, category);
    }
	  
	  StateMetrics m = c.currentStateMetrics;
	  ITransition t = Simulator.getSim().getActorTransition(this);
	  
	  
	  double channel_conflicts = Math.max(m.audioChannelInputs-1, 0) + 
        Math.max(m.visualChannelInputs-1,0);
	  double input_load = m.activeChannelsRead > 0 ? 
	      (Math.pow((double)(1 - (m.activeChannelsRead / m.layersRead)),2.0) * 
	          m.activeChannelsRead) : 0;
	  
    ComChannelList activeOutputChannels = new ComChannelList();
    MemoryList activeOutputMemory = new MemoryList();
    double output_layers = 0;
    double output_memory = 0;
    if ( t != null ) {
      ArrayList<TempComChannel> tcList = t.getTempOutputChannels();
      for( TempComChannel tc : tcList) {
        if ( tc.value() != null ) {
          activeOutputChannels.add(tc.channel());
          output_layers++;
        }
      }
        
      for(TempMemory tm : t.getTempOutputMemory()) {
        if ( tm.value() != null ) {
          activeOutputMemory.add(tm.memory());
          output_memory++;
        }
      }
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
    double output_load = output_channels > 0 ?
        ((1 - (output_channels / output_layers)) * output_channels) :0;    
	      
    ////////////Resource in Datasets/////////////////////////////
    _resource_in_dataset.addValue(channel_conflicts, 
        "Channel Conflicts", category);
//    _resource_in_dataset.addValue(m.activeChannelsRead, 
//        "Input Channels", category);
//    _resource_in_dataset.addValue(m.memoryInputs, 
//        "Input Memory", category);
    _resource_in_dataset.addValue(input_load + m.channelTypes, 
        "Resource Load", category);
    _resource_in_dataset.addValue(m.load,
        "State Load", category);
    
    double resource_in_total = channel_conflicts + m.channelTypes + 
        input_load + m.load;
    _resource_in_dataset.addValue(resource_in_total, 
        "Total", category);
    
	  
	  ////////////////////////////////////////////////////////////
    
    ////////////Resource Output Datasets///////////////////////
    _resource_out_dataset.addValue(output_conflicts, 
        "Channel Conflicts", category);
    //This is the same as output complexity
//    _resource_out_dataset.addValue(output_channels, 
//        "Output Channels", category);
//    _resource_out_dataset.addValue(output_memory, 
//        "Output Memory", category);
    _resource_out_dataset.addValue(output_load + output_types, 
        "Output Load", category);
    
    double resource_out_total = output_conflicts + 
        output_load + output_types;
    _resource_out_dataset.addValue(resource_out_total, 
        "Total", category);
    
    ////////////////////////////////////////////////////////////
	  
	  ////////////Algorithmic Datasets////////////////////////////
	  double dur_complexity = 0;
    if ( t != null ) {
      //Control the size of the dur complexity
      int dur = Simulator.getSim().duration(t.getDurationRange());
      double offsetDur = Math.max(dur/60, 1);
      dur_complexity = Math.log(offsetDur);
    }
    
    double dec_complexity = this.getCurrentState().getEnabledTransitions().size();
	  
	  _decision_dataset.addValue(dec_complexity, 
	      "Decision Complexity", category);
	  _decision_dataset.addValue(m.activeChannelsRead + m.memoryInputs, 
        "Input Complexity", category);
	  _decision_dataset.addValue(output_channels + output_memory, 
        "Output Complexity", category);
	  _decision_dataset.addValue(dur_complexity, 
        "Duration Complexity", category);
	  
	  double decision_total = dec_complexity + output_channels + output_memory + 
	      m.activeChannelsRead + m.memoryInputs + dur_complexity;
	  _decision_dataset.addValue(decision_total, 
        "Total", category);
	  ////////////////////////////////////////////////////////////
	  
	  
	  //////////////////TEMPORAL Metrics/////////////////////////
//	  double transition_rate = 1 - (time - lastTransitionTime / (time+1));
//	  double active_rate = m.channelsRead > 0 ? m.activeChannelsRead / m.channelsRead : 0;
//    _temporal_dataset.addValue(transition_rate, 
//        "Transition Ratio", category);
//    //Output complexity
//    _temporal_dataset.addValue(active_rate, 
//        "Active Input Ratio", category);

    /////////////////Workload///////////////////////////
    double wickens = 0;
    
    //wickens
    //outputs
    double output_targets = 0;
    double audioOutputs = 0;
    double visualOutputs = 0;
    double tactileOutputs = 0;
    if ( t != null ) {
      ComChannelList t_outputs = t.getOutputChannels().getUniqueChannels();
      output_targets = t_outputs.countTargets();
      audioOutputs = t_outputs.countChannels(ComChannel.Type.AUDIO);
      visualOutputs = t_outputs.countChannels(ComChannel.Type.VISUAL);
      tactileOutputs = t_outputs.countChannels(ComChannel.Type.TACTILE);
    }
    
    //MRT task conflicts, we consider there to be a conflict between tasks if
    //their are multiple channels coming in or going out
    double stage_dimen = m.activeInputs.countSources() >= 2 || output_targets >= 2 ? 1:0;
    double modality_dimen = m.audioChannelInputs >= 2 || 
                            audioOutputs >= 2 ||
                            m.visualChannelInputs >= 2 ? 1:0;
    double focus_dimen = tactileOutputs >= 2 ? 1:0;
    double code_dimen = (m.audioChannelInputs + audioOutputs) > 0 && 
        (m.visualChannelInputs + visualOutputs + tactileOutputs) > 0 ? 1:0;
    
    wickens = m.load + stage_dimen + modality_dimen + focus_dimen + code_dimen;
    
    _workload_dataset.addValue(wickens, 
        "Wickens Model", category);
    _workload_dataset.addValue(resource_in_total + resource_out_total + decision_total, 
        "My Model", category);
	}
	
}
