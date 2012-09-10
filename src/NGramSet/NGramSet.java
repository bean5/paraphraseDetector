package NGramSet;

import java.util.List;

public interface NGramSet {
	public static boolean STRICT = false;
	
	//abstract void setStrictness(boolean strictness);
	//abstract boolean getStrictness();
	
	void addWord(String word);
	void popFirstWord();

	boolean containsAsSubSet(NGramSet o, int min);
	boolean containsAsSubSet(NGramSet o, int min, boolean strictSearch);
	
	int getCountOfWord(String key);
	public List<String> getWordList();
	int size();
	
	String toString();
	String leftToString();

	void addRightMatch(NGramSet n);
	List<? extends NGramSet> getRightMatches();
	
	int getSide();
	
	public void setSide(int side);
	
	int hashCode();
	
	/*void setDocument1(List<String> words);
	void setDocument2(List<String> words);
	
	void resetCount();
	void setMaxSize(int size);*/
}