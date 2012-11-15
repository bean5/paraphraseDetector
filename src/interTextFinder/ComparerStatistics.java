package interTextFinder;

import java.util.TreeMap;

public class ComparerStatistics
{
	private int									left_count		= 0;
	private int									right_count		= 0;
	protected TreeMap<Double, Integer>	ordered_scores;

	public ComparerStatistics()
	{
		ordered_scores = new TreeMap<Double, Integer>();
	}

	public void incrementLeftCount()
	{
		left_count++;
	}

	public void setRightCount(int i)
	{
		right_count = i;
	}

	public int get_left_count()
	{
		return left_count;
	}

	public int get_right_count()
	{
		return right_count;
	}

	public double getLowestScore()
	{
		if(ordered_scores.size() == 0)
			return 0;
		return ordered_scores.firstKey();
	}

	public double getHighestScore()
	{
		if(ordered_scores.size() == 0)
			return 0;
		return ordered_scores.lastKey();
	}
}
