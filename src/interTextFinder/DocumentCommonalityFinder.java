package interTextFinder;

import java.util.HashSet;
import java.util.List;

import NGramSet.NGramSet;

public interface DocumentCommonalityFinder
{
	public HashSet<NGramSet> findCommonNGrams(List<String> words1, List<String> words2,
					double min_score, int max, boolean maximizePrimaryWindowSize);

	public String toString();

	public int hashCode();
}
