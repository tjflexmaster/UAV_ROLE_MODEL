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
	
}
