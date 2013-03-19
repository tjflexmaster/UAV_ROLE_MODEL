package NewModel.Utils;

import java.util.ArrayList;
import java.util.HashMap;

import NewModel.Roles.RoleState;

public class PostOffice {
	
	private HashMap<POBOX, ArrayList<DataType> > _po_boxes = new HashMap<POBOX, ArrayList<DataType> >();
	
	public enum POBOX {
		PS_MM,
		MM_PS,
		MM_PILOT,
		MM_VA,
		VA_MM,
		PILOT_MM,
		PILOT_UGUI,
		UGUI_PILOT,
		UGUI_UAV,
		UAV_UGUI,
		UAV_PILOT
	}
	
	public PostOffice() {
		
	}
	
	public void addPost(POBOX pobox, DataType data)
	{
		ArrayList<DataType> mail;
		if ( _po_boxes.containsKey(pobox) ) {
			mail = _po_boxes.get(pobox);
			mail.add(data);
		} else {
			mail = new ArrayList<DataType>();
			mail.add(data);
		}
		_po_boxes.put(pobox, mail);
	}
	
	public ArrayList<DataType> getPosts(POBOX pobox)
	{
		return _po_boxes.get(pobox);
	}
	
	public ArrayList<DataType> removePosts(POBOX pobox)
	{
		return _po_boxes.remove(pobox);
	}
	
	public void clearPost(POBOX pobox)
	{
		_po_boxes.remove(pobox);
	}

}
