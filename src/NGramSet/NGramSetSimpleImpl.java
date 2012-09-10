package NGramSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class NGramSetSimpleImpl implements NGramSet {
	protected static int maxSize;
	protected static int count = 0;
	protected int position = 0;
	protected static List<String> document1;
	protected static List<String> document2;
	
	protected List<String> words;
	protected HashMap<String, Integer> wordCounts;
	protected List<NGramSetSimpleImpl> matches = new ArrayList<NGramSetSimpleImpl>();
	
	protected int side = 1;
	
	public NGramSetSimpleImpl(int size) {initialize(size);}
	public NGramSetSimpleImpl(NGramSet other) {
		initialize(other.size());
		side = other.getSide();
		for(String str : other.getWordList()) {
			processWord(str);
		}
	}
	
	protected void initialize(int size) {
		words = new ArrayList<String>(size);
		wordCounts = new HashMap<String, Integer>(size);
	}
	
	public List<String> getWordList() {return words;}
	public int size() {return matches.size();}
	
	public void addWord(String word) {
		words.add(word);
	}
	public void processWord(String word) {
		addWord(word);
		position = count;
		count++;
		
		if(wordCounts.containsKey(word)) {
			wordCounts.put(word, wordCounts.get(word)+1);
		}
		else {
			wordCounts.put(word, 1);
		}
	}
	
	public void popFirstWord() {
		assert(words.size() > 0);
		assert(wordCounts != null);
		assert(wordCounts.containsKey(words.get(0)));
		
		String firstWord = words.get(0);
		List<String> document = (side == 1) ? document1 : document2;
		firstWord = document.get(position);
		
		System.out.println("Position: " + position);
		if(position > 0)
			System.out.println("Word (prev):  "+document.get(position-1));
		System.out.println("Word: "+firstWord);
		System.out.println("Word: "+words.get(0));
		
		assert(wordCounts != null);
		assert(wordCounts.get(firstWord) >= 1);
		if(wordCounts.get(firstWord) > 1) {
			wordCounts.put(firstWord , wordCounts.get(firstWord)-1);
		}
		else {
			wordCounts.remove(firstWord);
		}
		words.remove(0);
	}
	
	public boolean containsAsSubSet(NGramSet o, int min) {return containsAsSubSet(o, min, STRICT);}
	
	public boolean containsAsSubSet(NGramSet o, int min, boolean strictSearch) {
		int equal = 0;
		
		for(Entry<String, Integer> e : wordCounts.entrySet()) {
			if(isStopWord(e.getKey()) == false) {
				int thisCount = getCountOfWord(e.getKey());
				int otherCount = o.getCountOfWord(e.getKey());
				
				if(thisCount > 0 && otherCount > 0) {
					//System.out.println("Found in common: " + e.getKey());
				}
				
				if(strictSearch) {
					//always be strict (no extra matching: 1 to 1 comparisons)
					if(otherCount > thisCount)
						equal += thisCount;
					else
						equal += otherCount;
				}
				else {
					equal += otherCount;
				}
				
				if(equal >= min) return true;
			}
		}
		
		return false;
	}
	
	public int getCountOfWord(String key) {
		if(wordCounts.containsKey(key)) {
			return wordCounts.get(key);
		}
		return 0;
	}

	protected boolean isStopWord(String word) {
		if(true) 
		return 
				word.equals("The")
				||
				word.equals("the")
				||
				word.equals("And")
				||
				word.equals("and")
				||
				word.equals("Of")
				||
				word.equals("of")
				||
				word.equals("That")
				||
				word.equals("that")
				||
				word.equals("To")
				||
				word.equals("to")
				||
				word.equals("They")
				||
				word.equals("they")
				||
				word.equals("Unto")
				||
				word.equals("unto")
				||
				word.equals("I")
				||
				word.equals("i")
				||
				word.equals("He")
				||
				word.equals("he")
				||
				word.equals("It")
				||
				word.equals("it")
				;
		/*return 
				word.equals("the")
				||
				word.equals("of")
				||
				word.equals("and")
				||
				word.equals("it")
				||
				word.equals("came")
				||
				word.equals("to")
				||
				word.equals("pass")
				||
				word.equals("for")
				||
				word.equals("that")
				||
				word.equals("yea")
				||
				word.equals("a");*/
		else return false;
	}

	public String toString() {
		StringBuilder st = new StringBuilder();
		st.append("Primary Match: " + leftToString() + "\n");
		
		for(NGramSetSimpleImpl s : matches) {
			st.append("Secondary Match: " + s.leftToString());
			st.append("\n");
		}
		return st.toString();
	}
	public String leftToString() {
		StringBuilder st = new StringBuilder();
		///*
		for(String word : words) {
			st.append(word);
			st.append(' ');
		}

		List<String> document = (side == 1) ? document1 : document2;
		System.out.println("Pos: " + position + " of " + document.size());
		//*/
		/*
		
		for(int i = position; i < position + maxSize && i < document.size(); i++) {
			assert(i < document.size());
			st.append(document.get(i));
			st.append(' ');
		}
		//*/
		return st.toString();
	}

	public void addRightMatch(NGramSet n) {matches.add((NGramSetSimpleImpl) n);}

	public boolean getStrictness() {return STRICT;}
	
	public List<? extends NGramSet> getRightMatches() {return matches;}
	public void setSide(int side) {this.side  = side;}
	
	public static void setDocument1(List<String> words) {document1 = words;}
	public static void setDocument2(List<String> words) {document2 = words;}
	
	public static void resetCount() {resetCountTo(0);}
	public static void resetCountTo(int i) {count = i;}
	public static void setMaxSize(int size) {maxSize = size;}
	public int getSide() {return side;}
	
	public int hashCode() {return position*3 * words.hashCode();}
}
