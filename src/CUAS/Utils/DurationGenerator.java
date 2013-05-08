package CUAS.Utils;

import java.util.Map;
import java.util.Random;

public class DurationGenerator {
	
	private Mode _mode = Mode.MIN;
	private Random _random = null;
	
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
		initializeRandom();
	}
	
	public DurationGenerator(Mode mode)
	{
		_mode = mode;
		initializeRandom();
	}
	
	private void initializeRandom() {
		_random = new Random();
		_random.setSeed(0);		
	}
	
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
		return _random.nextInt(max - min + 1) + min;
	}
	
	private void initializeDefaults()
	{
		_mode = Mode.MIN;
	}

}
