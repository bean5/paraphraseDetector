package NGramSet;


import java.util.ArrayList;
import java.util.List;

public class NGramSetImplStemmed extends NGramSetSimpleImpl implements NGramSet {
	protected List<String> stemWords;
	
	public NGramSetImplStemmed(int size) {
		super(size);
		initialize(size);
	}
	
	protected void initialize(int size) {
		super.initialize(size);
		stemWords = new ArrayList<String>(size);
	}
	
	public NGramSetImplStemmed(NGramSetImplStemmed other) {
		super(other.getWordList().size());
		initialize(other.size());
		
		for(String word : other.getWordList()) {
			addWord(word);
		}
		for(String word : other.getStemWordList()) {
			addStemWord(word);
		}
	}

	public void processWord(String word) {
		addWord(word);
		position = count;
		count++;
		
		String stemmed = findWordStem(word);
		addStemWord(stemmed);
	}
	
	private void addStemWord(String stemmedWord) {
		stemWords.add(stemmedWord);
		if(wordCounts.containsKey(stemmedWord)) {
			wordCounts.put(stemmedWord, wordCounts.get(stemmedWord)+1);
		}
		else
			wordCounts.put(stemmedWord, 1);
	}

	protected List<String> getStemWordList() {return stemWords;}
	
	protected String findWordStem(String word) {		
		PorterStemmer stem = new PorterStemmer();
		char[] letters = word.toCharArray();
		for(int i = 0 ; i < letters.length; i++) {
			stem.add(letters[i]);
		}
		stem.stem();

		String stemmed = stem.toString();
		//System.out.println("Word stemmed:\t" + word + ":" + stemmed);
		return stemmed;
	}
	
	protected void incrementValueForWord(String word) {
		if(wordCounts.containsKey(word)) {
			wordCounts.put(word, wordCounts.get(word)+1);
		}
		else {
			wordCounts.put(word, 1);
		}
	}
	
	public void popFirstWord() {
		String firstWord = stemWords.get(0);
		//super.popFirstWord();
		
		assert(words.size() > 0);
		assert(wordCounts.containsKey(firstWord));
		
		/*List<String> document = (side == 1) ? document1 : document2;
		firstWord = document.get(position);
		firstWord = findWordStem(firstWord);*/
		if(wordCounts.get(firstWord) > 1) {
			wordCounts.put(firstWord, wordCounts.get(firstWord)-1);
		}
		else {
			wordCounts.remove(firstWord);
		}
		words.remove(0);
		stemWords.remove(0);
	}
	
	public String leftToString() {
		StringBuilder st = new StringBuilder();
		st.append(super.leftToString());
		
		assert(words.size() > 0);
		assert(stemWords.size() > 0);
		assert(words.size() == stemWords.size());
		
		st.append("\t[basis: ");
		for(String word : stemWords) {
			st.append(word);
			st.append(' ');
		}
		st.append(']');
		return st.toString();
	}

	@Override
	public List<NGramSet> getRightMatches() {return null;}
	//public List<NGramSetStemmed> getRightMatches() {return matches;}

	@Override
	public void addRightMatch(NGramSet n) {matches.add((NGramSetImplStemmed) n);}
}
