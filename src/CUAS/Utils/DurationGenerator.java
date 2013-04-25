package CUAS.Utils;

//import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import CUAS.Utils.Duration;

public class DurationGenerator {
	
//	private HashMap<String, Range> _ranges;
	private Mode _mode = Mode.MIN;
	
	public enum Mode
	{
		MIN,
		MAX,
		MIN_OR_MAX,
		MIN_MEAN_OR_MAX,
		RANDOM
	}
	
	public DurationGenerator()
	{
		initializeDefaults();
	}
	
	public DurationGenerator(Mode mode)
	{
//		_ranges = new HashMap<String, Range>();
//		_ranges.putAll(ranges);
		_mode = mode;
	}
	
//	public int duration(String key)
//	{
//		if ( _ranges.containsKey(key) ) {
//			switch(_mode) {
//				case MIN:
//					return _ranges.get(key).min();
//				case MAX:
//					return _ranges.get(key).max();
//				case MIN_OR_MAX:
//					if ( random(1) == 0 )
//						return _ranges.get(key).min();
//					else
//						return _ranges.get(key).max();
//				case MIN_MEAN_OR_MAX:
//					int val = random(2);
//					if ( val == 0 )
//						return _ranges.get(key).min();
//					else if (val == 2)
//						return _ranges.get(key).max();
//					else 
//						return _ranges.get(key).mean();
//				case RANDOM:
//					return random(_ranges.get(key));
//				default:
//					return 1;
//			}
//		} else {
//			//By default return 1
//			return 1;
//		}
//	}
	
	public int duration(Range range)
	{
		switch(_mode) {
			case MIN:
				return range.min();
			case MAX:
				return range.max();
			case MIN_OR_MAX:
				if ( random(1) == 0 )
					return range.min();
				else
					return range.max();
			case MIN_MEAN_OR_MAX:
				int val = random(2);
				if ( val == 0 )
					return range.min();
				else if (val == 2)
					return range.max();
				else 
					return range.mean();
			case RANDOM:
				return random(range);
			default:
				return 1;
		}
	}
	
	public int random(Range range)
	{
		return random(range.min(), range.max());
	}
	
	public int random(int val)
	{
		return random(0, val);
	}
	
	public int random(int min, int max)
	{
//		if ( min > max ) {
//			return 0;
//		}
		Random rand = new Random();
		return rand.nextInt(max - min + 1) + min;
	}
	
	private void initializeDefaults()
	{
		_mode = Mode.MIN;
//		_ranges = new HashMap<String, Range>();
//		_ranges.put(Duration.DUR_30.name(), new Range(30, 30));
//		_ranges.put(Duration.DUR_60.name(), new Range(60, 60));
//		_ranges.put(Duration.DUR_5MIN.name(), new Range(300, 300));
//		_ranges.put(Duration.DUR_10MIN.name(), new Range(600, 600));
//		_ranges.put(Duration.DUR_30MIN.name(), new Range(1800, 1800));
	}

}
