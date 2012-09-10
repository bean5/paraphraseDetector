package interTextFinder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import NGramSet.NGramSet;
import NGramSet.NGramSetImplStemmed;
import NGramSet.NGramSetSimpleImpl;

public class FuzzyNGramDocumentComparer<T1 extends NGramSet> implements DocumentCommonalityFinder {
	protected boolean isTesting = true;
	protected boolean STRICT = false;
	protected boolean usePorterStemmer = false;
	protected boolean matchCase = true;

	public HashSet<NGramSet> findCommonNGrams(String string1, String string2, int min, int max) {
		HashSet<NGramSet> foundNGrams = (HashSet<NGramSet>) new HashSet<T1>();
		
		//ensure that min <= max
		if(min > max) {
			int temp = max;
			max = min;
			min = temp;
		}
		
		char[] chars1 = string1.toCharArray();
		char[] chars2 = string2.toCharArray();
		
		List<String> words1 = scanForWords(chars1);
		List<String> words2 = scanForWords(chars2);
		if(isTesting) {
			words1 = words1.subList(0, (200 > words1.size()) ? words1.size() : 200);
			words2 = words2.subList(0, (200 > words2.size()) ? words2.size() : 200);
		}
		
		NGramSetSimpleImpl.setDocument1(words1);
		NGramSetSimpleImpl.setDocument2(words2);
		
		for(int i = max; i <= max; i++) {
			ArrayList<NGramSet> nGrams1 = getAllNGramsOfSize(words1, i, 1);
			ArrayList<NGramSet> nGrams2 = getAllNGramsOfSize(words2, i, 2);

			findAllCommon(foundNGrams, nGrams1, nGrams2, min);
		}
		mergeRepeats(foundNGrams, min, max);
		rankResults(foundNGrams, min, max);
		//printFindings(NGramSetStemmeds);
		
		return foundNGrams;
	}

	private void rankResults(HashSet<NGramSet> foundNGrams, int min, int max) {
		// TODO Auto-generated method stub
	}

	private void mergeRepeats(HashSet<NGramSet> foundNGrams, int min, int max) {
		// TODO Auto-generated method stub
	}

	protected void findAllCommon(HashSet<NGramSet> commons, ArrayList<NGramSet> nGrams1, ArrayList<NGramSet> nGrams2, int min) {
		for(NGramSet set1 : nGrams1) {
			for(NGramSet set2 : nGrams2) {
				if(set1.containsAsSubSet(set2, min)) {
					set1.addRightMatch(set2);
					commons.add(set1);

//					if(set1.size() == 1) System.out.println("Left:\t" + set1.leftToString() + "\n");
//					System.out.println("Right:\t" + set2.leftToString());
				}
			}
//			if(set1.size() > 0) System.out.println("\n\n");
		}
	}

	private ArrayList<NGramSet> getAllNGramsOfSize(List<String> words, int size, int side) {
		if(usePorterStemmer) {		
			NGramSetImplStemmed.setMaxSize(size);
			
			ArrayList<NGramSet> sets = new ArrayList<NGramSet>(words.size());
			NGramSetImplStemmed current = new NGramSetImplStemmed(size);
			current.setSide(side);
			
			for(int i = 0; i < size; i++) {
				current.processWord(words.get(i));
			}
			sets.add(current);
			
			NGramSetImplStemmed prev = current;
			for(int i = size ; i < words.size(); i++) {
				NGramSetImplStemmed newNGramSet = new NGramSetImplStemmed(prev);// new T1(prev);
				
				newNGramSet.processWord(words.get(i));
				newNGramSet.popFirstWord();
				
				sets.add(newNGramSet);
				
				prev = newNGramSet;
			}
			NGramSetImplStemmed.resetCount();
			return sets;
		}
		else {			
			NGramSetSimpleImpl.setMaxSize(size);
			
			ArrayList<NGramSet> sets = new ArrayList<NGramSet>(words.size());
			NGramSetSimpleImpl current = new NGramSetSimpleImpl(size);
			
			current.setSide(side);
			
			for(int i = 0; i < size; i++) {
				current.processWord(words.get(i));
			}
			sets.add(current);
			
			NGramSetSimpleImpl prev = current;
			for(int i = size ; i < words.size(); i++) {
				NGramSetSimpleImpl newNGramSet = new NGramSetSimpleImpl(prev);// new T1(prev);
				
				newNGramSet.processWord(words.get(i));
				newNGramSet.popFirstWord();
				
				sets.add(newNGramSet);
				
				prev = newNGramSet;
			}
			NGramSetSimpleImpl.resetCount();
			return sets;
		}
	}
	
	private List<String> scanForWords(char[] chars) {
		List<String> words = new ArrayList<String>(chars.length/4);//assume that the average word length is 4
		StringBuilder str = new StringBuilder();
		str.setLength(30);

		int total = 0;
		int length = 0;
		int max = chars.length;
		
		for(int i = 0; i < max; i++) {
			char currChar = chars[i];
			System.out.print(currChar);
		}
		/*System.out.println("");
		words.add("The");
		words.add("The");
		words.add("The");
		words.add("The");
		words.add("The");
		words.add("The");
		words.add("The");
		words.add("The");
		words.add("The");
		words.add("The");
		words.add("The");
		words.add("The");
		words.add("The");
		words.add("The");
		words.add("The");
		words.add("The");
		words.add("The");
		words.add("The");
		if(true) return words;*/
		
		for(int i = 0; i < max; i++) {
			char currChar = chars[i];
			char nextChar = chars[i];
			
			if(characterEvaluater.isAlphaOrDashFollowedByAlpha(currChar, nextChar)) { 
				//str.append(currChar);
				str.setCharAt(length, currChar);
				length++;
			}
			else if(length > 0) {
				if(matchCase) {
					words.add(str.substring(0, length));
				} else {
					System.out.println("Using lower case");
					String newString = new String(str.substring(0, length).toLowerCase());
					words.add(newString);
				}
				total += length;
				length = 0;
			}
		}
		/*
		System.out.println("Total length: " + total);
		System.out.println("Predicted length: " + chars.length/8);
		System.out.println("Average length: " + total/words.size());
		*/
		assert(chars.length == 0 || words.size() > 0);
		
		return words;
	}

	public void setStrict(boolean STRICT) {this.STRICT = STRICT;}
	public void setPorterStemmerUsage(boolean usePorterStemmer) {this.usePorterStemmer = usePorterStemmer;}
	public void setMatchCase(boolean matchCase) {this.matchCase  = matchCase;}
	
	public String toString() {return new String("");}
}
