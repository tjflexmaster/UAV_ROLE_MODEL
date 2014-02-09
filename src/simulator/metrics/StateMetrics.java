package simulator.metrics;

import simulator.ComChannelList;

public class StateMetrics
{
  public int audioChannelInputs = 0;
  public int visualChannelInputs = 0;
  public int memoryInputs = 0;
  public int channelsRead = 0;
  public int activeChannelsRead = 0;
  public int channelTypes = 0;
  public int layersRead = 0;
  public int load = 0;
  public ComChannelList activeInputs;
}
