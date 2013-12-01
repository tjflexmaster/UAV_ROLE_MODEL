package simulator;

public class Metric {
	
	public enum TypeEnum {
		setDecisionWorkload,
		setChannelConflict,
		setChannelLoad,
		setActorOutputs,
		_unknown
	}

	public TypeEnum _type = TypeEnum._unknown;
	public int _value = -1;
	public String _valueString = "";
	
	public Metric (TypeEnum type, int value) {
		_type = type;
		_value = value;
	}
	
	public Metric (TypeEnum type, String valueString) {
		_type = type;
		_valueString = valueString;
	}
	
	public String toString() {
		return (_value == -1) ?
				("(" + _type.name() + ", " + _valueString + ")")
				: ("(" + _type.name() + ", " + _value + ")");
	}

	public void add(int value) {
		_value += value;
	}
}