package interTextFinder;


import java.util.HashSet;

import NGramSet.NGramSet;
import NGramSet.NGramSetImplStemmed;
import NGramSet.NGramSetImpl;

public class NotFuzzySearchDocumentComparer<T1 extends NGramSet> extends FuzzyNGramDocumentComparer<T1> implements DocumentCommonalityFinder {

	/*
	 * (non-Javadoc)
	 * @see interTextFinder.FuzzyNGramDocumentComparer#findCommonNGrams(java.lang.String, java.lang.String, int, int)
	 * 
	 */
	public HashSet<NGramSet> findCommonNGrams(String string1, String string2, int min, int max) {
		HashSet<NGramSet> NGramSetStemmeds = super.findCommonNGrams(string1, string2, min, max);
		
		NGramSetStemmeds = useFuzzyMatchesToDeriveHardMatches(NGramSetStemmeds, min, max);
		
		return NGramSetStemmeds;
	}

	private HashSet<NGramSet> useFuzzyMatchesToDeriveHardMatches(HashSet<NGramSet> nGramSets, int min, int max) {
		// TODO Auto-generated method stub
		//loop through findings and grab all strict matches
		
		HashSet<NGramSet> nGramSetsStrict = new HashSet<NGramSet>();
		for(NGramSet set : nGramSets) {
			NGramSet temp = new NGramSetImpl(max);
			
			nGramSetsStrict.add(temp);
		}
		
		return nGramSetsStrict;
		//return nGramSets;
	}
}
