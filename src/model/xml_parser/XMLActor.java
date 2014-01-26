package model.xml_parser;

import simulator.Actor;
import simulator.ComChannel;
import simulator.ComChannelList;
import simulator.IActor;
import simulator.IState;
import simulator.Memory;
import simulator.MemoryList;

public class XMLActor extends Actor implements IActor {
  
  private ComChannelList m_inputChannels = new ComChannelList();
  private ComChannelList m_outputChannels = new ComChannelList();
  
  private MemoryList m_memory = new MemoryList();
  
	public XMLActor(String name)
	{
		setName(name);
		
		m_inputChannels = new ComChannelList();
		m_outputChannels = new ComChannelList();
	}
	

	public void addState(IState state)
	{
		super.add(state);
	}
	
	public void setStartState(IState state)
	{
	  super.startState(state);
	}
	
	public void addInputChannel(ComChannel channel)
	{
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
}
