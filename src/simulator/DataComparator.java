package simulator;

public class DataComparator {
	Object data;
	public DataComparator(String _data, String data_type){
		data = _data;
		switch(data_type){
		case "String":
			data = _data;
			break;
		case "Integer":
			data = Integer.parseInt(_data);
			break;
		case "Boolean":
			data = Boolean.parseBoolean(_data);
			break;
		default:
			assert true: "Missing data type";
		}
	}
	
	public boolean isTrue(Object o){
		return true;
	}
	
}
