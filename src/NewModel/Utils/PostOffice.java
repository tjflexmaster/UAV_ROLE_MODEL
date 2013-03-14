package NewModel.Utils;

import java.util.ArrayList;
import java.util.HashMap;

import NewModel.Roles.RoleState;

public class PostOffice {
	
	private HashMap<RoleState, ArrayList<DataType> > _po_boxes = new HashMap<RoleState, ArrayList<DataType> >();
	
	public PostOffice() {
		
	}
	
	public void addPost(RoleState state, DataType data)
	{
		ArrayList<DataType> mail;
		if ( _po_boxes.containsKey(state) ) {
			mail = _po_boxes.get(state);
			mail.add(data);
		} else {
			mail = new ArrayList<DataType>();
			mail.add(data);
		}
		_po_boxes.put(state, mail);
	}
	
	public ArrayList<DataType> getPosts(RoleState state)
	{
		return _po_boxes.get(state);
	}
	
	public ArrayList<DataType> removePosts(RoleState state)
	{
		return _po_boxes.remove(state);
	}
	
	public void clearPost(RoleState state)
	{
		_po_boxes.remove(state);
	}

}
