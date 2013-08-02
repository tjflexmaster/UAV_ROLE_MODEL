package simulator;

import java.util.HashMap;


@SuppressWarnings("serial")
public class ComChannelList extends HashMap<String, ComChannel<?> > {

//	private HashMap<String, ComChannel<?> > _list;
//	
//	public ComChannelList()
//	{
//		_list = new HashMap<String, ComChannel<?> >();
//	}

	public ComChannelList add(ComChannel<?> item) {
		this.put(item.name(), item);
		return this;
	}


	/**
	 * Get Comchannels by type
	 */
	public ComChannelList getChannels(ComChannel.Type type)
	{
		ComChannelList result = new ComChannelList();
		for( ComChannel<?> channel : this.values() ) {
			if ( channel.type() == type ) {
				result.add(channel);
			}
		}
		return result;
	}

	/**
	 * Count channels by type
	 */
	public int countChannels(ComChannel.Type type)
	{
		int count = 0;
		for( ComChannel<?> channel : this.values() ) {
			if ( channel.type() == type ) {
				count++;
			}
		}
		return count;
	}


	/**
	 * Get ComChannel by name
	 */
	public ComChannel<?> getChannel(String name)
	{
		return this.get(name);
	}

	/**
	 * Get Active (non-null value) ComChannels
	 */
	public ComChannelList getActiveChannels()
	{
		ComChannelList result = new ComChannelList();
		for( ComChannel<?> channel : this.values() ) {
			if ( channel.value() != null ) {
				result.add(channel);
			}
		}
		return result;
	}

	public ComChannelList getActiveChannels(ComChannel.Type type)
	{
		ComChannelList result = new ComChannelList();
		for( ComChannel<?> channel : this.values() ) {
			if ( channel.value() != null && channel.type() == type ) {
				result.add(channel);
			}
		}
		return result;
	}

	/**
	 * Get Active channel count
	 */
	public int countActiveChannels()
	{
		int count = 0;
		for( ComChannel<?> channel : this.values() ) {
			if ( channel.value() != null ) {
				count++;
			}
		}
		return count;
	}

	public int countActiveChannels(ComChannel.Type type)
	{
		int count = 0;
		for( ComChannel<?> channel : this.values() ) {
			if ( channel.value() != null && channel.type() == type ) {
				count++;
			}
		}
		return count;
	}
}