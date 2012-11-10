package interTextFinder;

import java.util.HashSet;

import NGramSet.NGramSet;

public interface DocumentCommonalityFinder {
	public HashSet<NGramSet> findCommonNGrams(String string1, String string2, int min, int max, boolean maximizePrimaryWindowSize);
	
	public String toString();
	public int hashCode();
}
