package CUAS.Utils;

import java.util.Random;

public class Range {
	private int _min = 0;
	private int _max = 0;
	
	public Range() {
		
	}
	
	public Range(int val)
	{
		_min = val;
		_max = val;
	}
	
	public Range(int min, int max)
	{
		_min = min;
		_max = max;
	}
	
	public void min(int min)
	{
		_min = min;
	}
	
	public int min()
	{
		return _min;
	}
	
	public void max(int max)
	{
		_max = max;
	}
	
	public int max()
	{
		return _max;
	}
	
	public int mean()
	{
		return (_max - _min) / 2;
	}
	
}
