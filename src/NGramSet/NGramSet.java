package NGramSet;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public interface NGramSet {
	public static boolean STRICT = false;
	
	String processWord(String word);
	
	void popFirstWord();

	int consume(HashMap<String, List<NGramSet>> map);
	
	void setDocument(List<String> words);
	
	int getCountOfWord(String key);
//	public List<String> getWordList();
	public List<String> getModifiedWordList();
//	public static double setMinScore();
	
	public void setMinScore(double d);
	public double getMinScore();
	public int getMaxSize();
	
	int size();
	
	String toString();
	
	boolean hasMatches();
	boolean isStopWord(String word);
	
	List<String> getDocument();

	int getPosition();
//	int hashCode();

	double findBestScore();

	boolean hasMatchesOfAtLeastScore(double minscore);

	String toStringAtLeast(double minscore);

	int countMatchesOfAtLeastScore(double minscore);

	public TreeMap<Double, Integer> getScores();

	double getScore();

	void filterMatchesWithScoresLowerThan(double bestScore);
	
	public int getTotalCount();
}