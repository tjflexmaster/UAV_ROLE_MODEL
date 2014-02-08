package simulator.metrics;

import java.util.Vector;

public class MetricContainer
{
  //Team values
  public Vector<MetricContainer> actorMetrics = new Vector<MetricContainer>();
  
  //Actor values
  public String actorName;
  public int actorInputChannels;
  public int actorOutputChannels;
  public int actorTotalMemory;
  public int actorTotalStates;
  public int actorTotalTransitions;
  public int actorInputLayers;
  public int actorOutputLayers;
  
  public int actorActiveMemory;
  public int actorActiveInputChannels;
  public int actorActiveOutputChannels;
  public int actorActiveInputLayers;
  public int actorActiveOutputLayers;
  
  public MetricContainer actorActiveTransition;
  
  public StateMetrics currentStateMetrics;
  
  //Algorithmic Calculations
  public double alg_decision_complexity = 0.0;
  public double alg_output_complexity = 0.0;
  public double alg_duration_complexity = 0.0;
  
  //State values
  public String currentState;
  public int numOfTransitions;
  public int numOfEnabledTransitions;
  
  //Actor Conflict vars
  public int numOfAudioChannels;
  public int numOfVisualChannels;
  public int numOfAudioLayers;
  public int numOfVisualLayers;
  
  //Transition values
  public int numOfChannelOutputs;
  public int numOfLayerOutputs;
  public int numOfMemoryOutputs;
  
  //Channel values
  public int num_of_layers = 0;
  public int num_of_active_layers = 0;
  public int avg_time_between_change = 0;
  
  //Layer values
  public int layer_change_rate = 0;
  public boolean layer_active = false;
  
  //Memory values
  public int memory_change_rate = 0;
  public boolean memory_active = false;
}
