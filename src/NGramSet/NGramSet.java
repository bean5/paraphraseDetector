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

	public List<String> getModifiedWordList();
	
	public void setMinScore(double d);
	public double getMinRequiredScore();
	public int get_window_size();
	
	int size();
	
	String toString();
	
	boolean hasMatches();
	boolean isStopWord(String word);
	
	List<String> getDocument();

	int getPosition();

	double findBestScore();

	boolean hasMatchesOfAtLeastScore(double minscore);

	String toStringAtLeast(double minscore);

	int countMatchesOfAtLeastScore(double minscore);

	public TreeMap<Double, Integer> getScores();

	double getScore(NGramSet set);

	void filterMatchesWithScoresLowerThan(double bestScore);
	
	public int getTotalCount();

	void computeScore(int integer, NGramSet set);

	double lowestScore();
}