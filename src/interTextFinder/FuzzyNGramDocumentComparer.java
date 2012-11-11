package interTextFinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
//import java.util.LinkedList;
import java.util.List;
//import java.util.Set;
//import java.util.TreeSet;

import NGramSet.NGramSet;
import NGramSet.NGramSetImplStemmed;
import NGramSet.NGramSetImpl;

public class FuzzyNGramDocumentComparer<T1 extends NGramSet> implements DocumentCommonalityFinder
{
	protected boolean	isTesting			= false;
	protected boolean	STRICT				= false;
	protected boolean	usePorterStemmer	= false;
	protected boolean	matchCase			= true;
	protected int		totalRightMatches	= 0;
	private boolean	USESTOPWORDS		= true;
	private List<Error> errors;

	public HashSet<NGramSet> findCommonNGrams(String string1, String string2, int min, int max,
					boolean maximizePrimaryWindowSize)
	{
		errors = new ArrayList<Error>();
		HashSet<NGramSet> NGramsWithMatches = new HashSet<NGramSet>();

		// ensure that min <= max
		if (min > max)
		{
			int temp = max;
			max = min;
			min = temp;

			logError("Min greater than max; assuming the opposite parameterization");
		}

		char[] chars1 = string1.toCharArray();
		char[] chars2 = string2.toCharArray();

		List<String> words1 = scanForWords(chars1);
		List<String> words2 = scanForWords(chars2);

		// when testing, restrict the length of documents to be small
		if (isTesting)
		{
			int maxSub = 1000;
			words1 = words1.subList(0, maxSizeOutOfRangeForSource(maxSub, words1) ? words1.size() : maxSub);
			words2 = words2.subList(0, maxSizeOutOfRangeForSource(maxSub, words2) ? words2.size() : maxSub);
		}

		NGramSetImpl.setMatchCase(matchCase);
		NGramSetImpl.setUseStopWords(USESTOPWORDS);
		NGramSetImpl.setStrictness(STRICT);
		NGramSetImpl.setMinSize(min);
		
		int leftMax = (words1.size() <= max || maximizePrimaryWindowSize) ? words1.size() : max;
		int rightMax = (words2.size() <= max) ? words2.size() - 1 : max;
//		NGramSetImpl.setMaxSize(rightMax);

		HashMap<String, List<NGramSet>> map = new HashMap<String, List<NGramSet>>();
		
		if(rightMax < max)
		{
			logError("Window size greater than number of length of secondary text; decreasing secondary window size to: " + rightMax);
		}
		if(leftMax < max && maximizePrimaryWindowSize)
		{
			logError("Maximizing primary window");
		}
		else if(leftMax < max)
		{
			logError("Max out of range for primary source.  Scaling down to: " + leftMax);
		}
		
		ArrayList<NGramSet> nGrams1 = null;

		nGrams1 = getAllNGramsOfSize(words1, leftMax, null);
		
		// ArrayList<NGramSet> nGrams2 =
						getAllNGramsOfSize(words2, rightMax, map);

		// System.out.println("Words Left: " + nGrams1.size());
		// System.out.println("Words Right: " + nGrams2.size());

		findAllCommon(NGramsWithMatches, nGrams1, map);

		organizeMatches(NGramsWithMatches);
		mergeRepeats(NGramsWithMatches, min, max);
		rankResults(NGramsWithMatches, min, max);
		
		return NGramsWithMatches;
	}

	private boolean maxSizeOutOfRangeForSource(int max, List<String> words2)
	{
		return max > words2.size();
	}

	private boolean maxSizeOutOfRangeForPrimarySource(int max, boolean maximizePrimaryWindowSize,
					List<String> words1)
	{
		return maxSizeOutOfRangeForSource(max, words1) && !maximizePrimaryWindowSize;
	}

	private void logError(String message)
	{
		if(errors == null)
			System.out.println("Error list does not exist; aborting log of error: " + message);

		else {
			errors.add(new Error(message));
			System.out.println(message);
		}
	}

	private void organizeMatches(HashSet<NGramSet> nGramsWithMatches)
	{
//		Set<NGramSet> bst = new TreeSet<NGramSet>(nGramsWithMatches);
		// nGramsWithMatches = new LinkedList<NGramSet>(bst.toArray());
	}

	private void rankResults(HashSet<NGramSet> foundNGrams, int min, int max)
	{
	}

	private void mergeRepeats(HashSet<NGramSet> foundNGrams, int min, int max)
	{
	}

	protected void findAllCommon(HashSet<NGramSet> NGramsWithMatches, ArrayList<NGramSet> nGrams,
					HashMap<String, List<NGramSet>> map)
	{
		totalRightMatches = 0;
		for (NGramSet ngram : nGrams)
		{
			totalRightMatches += ngram.consume(map);
			if (ngram.hasMatches())
			{
				NGramsWithMatches.add(ngram);
			}
		}
	}

	private ArrayList<NGramSet> getAllNGramsOfSize(List<String> words, int size,
					HashMap<String, List<NGramSet>> map)
	{
		String processedWord = null;
		ArrayList<NGramSet> sets = new ArrayList<NGramSet>(words.size());
		final int documentSize = words.size();

		if (usePorterStemmer)
		{
			NGramSetImplStemmed current = new NGramSetImplStemmed(size);
			current.setMaxSize(size);
			current.setDocument(words);

			for (int i = 0; i < size && i < documentSize; i++)
			{
				processedWord = current.processWord(words.get(i));

				// if map is null, then tracking doesn't matter
				// if processWord was null, it was a stop-word
				if (map != null && processedWord != null)
				{
					// System.out.println("Mapping: " + current.toString() + " for "
					// + processedWord);
					List<NGramSet> nGrams = map.get(processedWord);
					if (nGrams != null)
					{
						final int prevSize = nGrams.size();

						nGrams.add(current);

						assert (prevSize != map.get(processedWord).size());
					}
					else
					{
						List<NGramSet> l = new ArrayList<NGramSet>();
						l.add(current);
						map.put(processedWord, l);
					}
				}
			}
			sets.add(current);

			NGramSetImplStemmed prev = current;
			for (int i = size; i < documentSize; i++)
			{
				current = new NGramSetImplStemmed((NGramSet) prev);

				processedWord = current.processWord(words.get(i));
				current.popFirstWord();

				sets.add(current);
				prev = current;

				if (map == null || processedWord == null) continue;

				List<String> relevantWords = current.getModifiedWordList();
				for (String relevantWord : relevantWords)
				{
					List<NGramSet> nGrams = map.get(relevantWord);
					if (nGrams != null)
					{
						nGrams.add(current);
					}
					else
					{
						List<NGramSet> l = new ArrayList<NGramSet>();
						l.add(current);
						map.put(relevantWord, l);
					}
				}
			}
		}
		else
		{
			NGramSetImpl current = new NGramSetImpl(size);

			current.setDocument(words);

			for (int i = 0; i < size && i < documentSize; i++)
			{
				processedWord = current.processWord(words.get(i));
				if (map != null && processedWord != null)
				{
					List<NGramSet> nGrams = map.get(processedWord);
					if (nGrams != null)
					{
						nGrams.add(current);
					}
					else
					{
						List<NGramSet> l = new ArrayList<NGramSet>();
						l.add(current);
						map.put(processedWord, l);
					}
				}
			}
			sets.add(current);

			NGramSetImpl prev = current;
			for (int i = size; i < documentSize; i++)
			{
				current = new NGramSetImpl((NGramSet) prev);

				processedWord = current.processWord(words.get(i));
				current.popFirstWord();

				sets.add(current);
				prev = current;

				if (map == null || processedWord == null) continue;

				List<String> relevantWords = current.getModifiedWordList();
				for (String relevantWord : relevantWords)
				{
					List<NGramSet> nGrams = map.get(relevantWord);
					if (nGrams != null)
					{
						nGrams.add(current);
					}
					else
					{
						List<NGramSet> l = new ArrayList<NGramSet>();
						l.add(current);
						map.put(relevantWord, l);
					}
				}
			}
		}
		// if(map != null) System.out.println("Map size: " +
		// map.entrySet().size());
		return sets;
	}

	private List<String> scanForWords(char[] chars)
	{
		List<String> words = new ArrayList<String>(chars.length / 4);// assume
																							// that the
																							// average
																							// word
																							// length
																							// is 4
		StringBuilder str = new StringBuilder();
		str.setLength(30);

		int total = 0;
		int length = 0;
		int max = chars.length;

		// for(int i = 0; i < max; i++) {
		// char currChar = chars[i];
		// //System.out.print(currChar);
		// }

		for (int i = 0; i < max; i++)
		{
			char currChar = chars[i];
			// TODO fix this because it is wrong.
			char nextChar = chars[i];

			if (characterEvaluater.isAlphaOrDashFollowedByAlpha(currChar, nextChar))
			{
				str.setCharAt(length, currChar);
				// System.out.print(currChar);
				length++;
			}
			else if (length > 0)
			{
				// if(matchCase || true) {
				words.add(str.substring(0, length));
				// } else {
				// //System.out.println("Using lower case");
				// String newString = new String(str.substring(0,
				// length).toLowerCase());
				// words.add(newString);
				// //System.out.print(newString + ' ');
				// }
				total += length;
				length = 0;
			}
		}

		System.out.println("Total length: " + total);
		System.out.println("Predicted length: " + chars.length / 8);
		System.out.println("Average length: " + total / words.size());

		assert (chars.length == 0 || words.size() > 0);

		return words;
	}

	public void setStrict(boolean strictness)
	{
		STRICT = strictness;
	}

	public void setPorterStemmerUsage(boolean usePorterStemmer)
	{
		this.usePorterStemmer = usePorterStemmer;
	}

	public void setMatchCase(boolean matchCase)
	{
		this.matchCase = matchCase;
	}

	public String toString()
	{
		return new String("");
	}

	public void setUseStopWords(boolean useStopWords)
	{
		USESTOPWORDS = useStopWords;
	}

	public String toStringStopWordsToStringOfSize(int i)
	{
		// TODO Auto-generated method stub
		return "";
	}

	public String errorsToString()
	{
		if(errors == null || errors.size() == 0)
			return "";

		String errorString = "\nNotes:\n";	
		for(Error e : errors)
		{
			errorString += e.getMessage() + "\n";
		}
		
		return errorString + "\n\n";
	}
}
