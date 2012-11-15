package NGramSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class NGramSetImpl implements NGramSet
{
	protected static double					min_required_score	= 0.0D;
	public int									window_size;
	protected static boolean				matchCase				= false;
	protected static boolean				useStopWords			= true;
	protected static boolean				useSrictMatching		= true;

	protected int								position					= 0;
	protected int								totalCount				= 0;

	protected List<String>					document;

	protected List<String>					words;
	protected List<String>					modifiedWords;

	protected HashMap<String, Integer>	word_counts;
	protected HashMap<NGramSet, Double>	matches;
	protected TreeMap<Double, Integer>	ordered_scores;
	private double								score						= 0.0D;
	protected HashMap<NGramSet, Double>	scores_for_primary;

	public NGramSetImpl(int size)
	{
		initialize(size);
	}

	public NGramSetImpl(NGramSet other)
	{
		initialize(other.get_window_size());

		document = other.getDocument();
		position = 0;

		for (String word : other.getModifiedWordList())
		{
			processModifiedWord(word);
		}
		position = other.getPosition();
		min_required_score = other.getMinRequiredScore();
	}

	protected void initialize(int size)
	{
		words = new ArrayList<String>(size);
		modifiedWords = new ArrayList<String>(size);
		word_counts = new HashMap<String, Integer>(size);
		matches = new HashMap<NGramSet, Double>();
		window_size = size;
		ordered_scores = new TreeMap<Double, Integer>();
		scores_for_primary = new HashMap<NGramSet, Double>();
	}

	public String processWord(String word)
	{
		String modifiedWord = modifyWord(word);

		return processModifiedWord(modifiedWord);
	}

	protected String modifyWord(String word)
	{
		String modifiedWord;
		if (!matchCase)
		{
			modifiedWord = new String(word).toLowerCase();
		}
		else
		{
			modifiedWord = word;
		}
		return modifiedWord;
	}

	protected String processModifiedWord(String word)
	{
		addProcessedWord(word);
		incrementPosition();

		return incrementWordCount(word);
	}

	protected void addProcessedWord(String stemmedWord)
	{
		modifiedWords.add(stemmedWord);
	}

	public int consume(HashMap<String, List<NGramSet>> map)
	{
		assert (window_size > 0);
		assert (score == 0.0D);

		// System.out.print("\nSearching for matches for:\n\n" + toString());

		HashMap<NGramSet, Integer> potentialMatches = new HashMap<NGramSet, Integer>();

		// accumulate all possible matches
		// non-strict matching
		if (useSrictMatching)
		{
			runStrictMatch(map, potentialMatches);
		}
		else
		{
			runNonStrictMatch(map, potentialMatches);
		}

		return accumulateMatchesOfSufficientScore(potentialMatches);
	}

	private void runStrictMatch(HashMap<String, List<NGramSet>> map,
					HashMap<NGramSet, Integer> potentialMatches)
	{
		for (Entry<String, Integer> word_and_count : word_counts.entrySet())
		{
			String s = word_and_count.getKey();

			List<NGramSet> oneWordMatches = map.get(s);

			if (oneWordMatches == null) continue;

//			System.out.println("Matching Word: " + s);

			Set<NGramSet> oneWordMatchesUnique = new HashSet<NGramSet>(oneWordMatches);

			assert (oneWordMatches != null);
			assert (oneWordMatches.size() > 0);
			assert (oneWordMatchesUnique.size() > 0);
			assert (oneWordMatchesUnique.size() <= oneWordMatches.size());

			for (NGramSet possibleMatchNGram : oneWordMatchesUnique)
			{
				int currentCount = 0;
				int increment = 1;

				if (potentialMatches.containsKey(possibleMatchNGram))
				{
					currentCount = potentialMatches.get(possibleMatchNGram);
				}
//				System.out.println("Count of " + s + ": " + possibleMatchNGram.getCountOfWord(s));
				increment = (word_and_count.getValue() > possibleMatchNGram.getCountOfWord(s)) ? possibleMatchNGram
								.getCountOfWord(s) : word_and_count.getValue();

				increment += currentCount;
//				System.out.println("Increment: " + increment);

				potentialMatches.put(possibleMatchNGram, increment);
			}
		}
	}

	private void runNonStrictMatch(HashMap<String, List<NGramSet>> map,
					HashMap<NGramSet, Integer> potentialMatches)
	{
		// non-STRICT matching
		for (Entry<String, Integer> e : word_counts.entrySet())
		{
			String s = e.getKey();

			for (int j = e.getValue(); j > 0; j--)
			{
				List<NGramSet> oneWordMatches = map.get(s);

				if (oneWordMatches == null) break;

				for (NGramSet possibleMatchNGram : oneWordMatches)
				{
					int currentCount = 0;
					int increment = 1;

					if (potentialMatches.containsKey(possibleMatchNGram))
					{
						currentCount = potentialMatches.get(possibleMatchNGram);
					}
					increment += currentCount;
					increment = (increment > window_size) ? window_size : increment;

					potentialMatches.put(possibleMatchNGram, increment);
				}
			}
		}
	}

	private int accumulateMatchesOfSufficientScore(HashMap<NGramSet, Integer> potentialMatches)
	{
		// accumulate matches that meet minimum requirement
		int c = 0;
		for (Entry<NGramSet, Integer> potential_match_and_match_count : potentialMatches.entrySet())
		{
			NGramSet set = potential_match_and_match_count.getKey();
			// set.set_match_count(potential_match_and_match_count.getValue());
			set.computeScore(potential_match_and_match_count.getValue(), this);
			
			double score_for_set = set.getScore(this);
//			System.out.println("score for set: " + score_for_set + " min req: " + min_required_score);
			if (score_for_set >= min_required_score)
			{
				int count = 1;
				if (ordered_scores.containsKey(score_for_set))
				{
					count += ordered_scores.get(score_for_set);
				}
				ordered_scores.put(score_for_set, count);

				matches.put(set, score_for_set);

				// System.out.println("Found Match: " + e.getKey().toString());
				// potentialMatches.remove(e.getValue());
				c++;
			}
			if (score_for_set > score) score = score_for_set;
		}

		// if(c > 0) System.out.println("Size: " + c);
		return c;
	}

	public void filterMatchesWithScoresLowerThan(double bestScore)
	{
		HashMap<NGramSet, Double> filteredMap = new HashMap<NGramSet, Double>();
		for (Entry<NGramSet, Double> e : matches.entrySet())
		{
			if (e.getKey().getScore(this) >= bestScore) filteredMap.put(e.getKey(), e.getValue());
		}
		matches = filteredMap;
	}

	protected void incrementPosition()
	{
		assert (position < document.size());
		position++;
	}

	protected String incrementWordCount(String word)
	{
		if (isStopWord(word)) return null;

		if (word_counts.containsKey(word))
		{
			word_counts.put(word, word_counts.get(word) + 1);
		}
		else
		{
			word_counts.put(word, 1);
		}

		totalCount++;

		return word;
	}

	public void popFirstWord()
	{
		assert (modifiedWords.size() > 0);
		assert (word_counts != null);

		String firstWord = modifiedWords.get(0);

		decrementWordCount(firstWord);

		modifiedWords.remove(0);
	}

	protected void decrementWordCount(String word)
	{
		assert (word_counts != null);
		if (isStopWord(word)) return;

		assert (word_counts.get(word) >= 1);
		assert (word_counts.containsKey(word));

		if (word_counts.get(word) > 1)
		{
			word_counts.put(word, word_counts.get(word) - 1);
		}
		else
		{
			word_counts.remove(word);
		}
	}

	public int getCountOfWord(String key)
	{
		if (word_counts.containsKey(key)) { return word_counts.get(key); }
		return 0;
	}

	public boolean isStopWord(String word)
	{
		if (useStopWords == false) return false;
		return word.equals("The") || word.equals("the") || word.equals("And") || word.equals("and")
						|| word.equals("Of") || word.equals("of") || word.equals("That")
						|| word.equals("that") || word.equals("To") || word.equals("to")
						|| word.equals("They") || word.equals("they") || word.equals("Unto")
						|| word.equals("unto") || word.equals("I") || word.equals("i")
						|| word.equals("He") || word.equals("he") || word.equals("It")
						|| word.equals("it");
	}

	public String toString()
	{
		StringBuilder st = new StringBuilder();
		st.append("Primary Match (size: " + window_size + "): " + leftToString() + "\n");

		appendRightToStringBuilder(st);

		return st.toString();
	}

	protected void appendRightToStringBuilder(StringBuilder st)
	{
		for (Entry<NGramSet, Double> e : matches.entrySet())
		{
			NGramSetImpl set = (NGramSetImpl) e.getKey();

			st.append("Secondary Match [" + set.getMatchCount(this) + "/" + set.get_window_size() + " = "
							+ set.getScore(this) + "]:" + set.leftToString());
			st.append("\n");
		}
	}

	private int getMatchCount(NGramSet set)
	{
		return (int) (scores_for_primary.get(set) * window_size);
	}

	private void appendRightToStringBuilderAtLeast(StringBuilder st, double minscore)
	{
		for (Entry<NGramSet, Double> e : matches.entrySet())
		{
			if (e.getKey().getScore(this) < minscore) continue;
			NGramSetImpl set = (NGramSetImpl) e.getKey();

			st.append("Secondary Match [" + set.getMatchCount(this) + "/" + set.get_window_size() + " = "
							+ set.getScore(this) + "]:" + set.leftToString());
			st.append("\n");
		}
	}

	public String leftToString()
	{
		StringBuilder st = new StringBuilder();
		st.append(" Position: " + (position - window_size) + "/" + document.size());
		st.append(' ');

		st.append(' ');
		st.append(' ');
		for (int i = position - window_size; i < position; i++)
		{
			assert (i < document.size());
			st.append(document.get(i));
			st.append(' ');
		}

		st.append("\tbasis [ ");
		for (String word : modifiedWords)
		{
			st.append(word);
			st.append(' ');
		}
		st.append("]");

		return st.toString();
	}

	public boolean hasMatches()
	{
		return matches.entrySet().size() > 0;
	}

	// public List<String> getWordList() {return words;}
	public List<String> getModifiedWordList()
	{
		return modifiedWords;
	}

	public boolean getStrictness()
	{
		return STRICT;
	}

	public static void setMatchCase(boolean mc)
	{
		matchCase = mc;
	}

	public int size()
	{
		return matches.entrySet().size();
	}

	public void computeScore(int value, NGramSet set)
	{
		scores_for_primary.put(set, (double) value / (double) window_size);
		// this.score = ;
	}

	public void setMinScore(double d)
	{
		min_required_score = d;
	}

	public void setMaxSize(int size)
	{
		window_size = size;
	}

	public static void setUseStopWords(boolean USESTOPWORDS)
	{
		useStopWords = USESTOPWORDS;
	}

	public void setDocument(List<String> words)
	{
		document = words;
	}

	public int getPosition()
	{
		return position;
	}

	public double getMinRequiredScore()
	{
		return min_required_score;
	}

	public int get_window_size()
	{
		return window_size;
	}

	public List<String> getDocument()
	{
		return document;
	}

	// public int hashCode() {return word_counts.hashCode();}
	public static void setStrictness(boolean STRICT)
	{
		useSrictMatching = STRICT;
	}

	public double findBestScore()
	{
		return score;
	}

	public double lowestScore()
	{
		if (ordered_scores.size() == 0) return -0.0D;
		return ordered_scores.firstKey();
	}

	public boolean hasMatchesOfAtLeastScore(double minscore)
	{
		return ordered_scores.ceilingKey(minscore) != null;
	}

	public String toStringAtLeast(double minscore)
	{
		StringBuilder st = new StringBuilder();
		st.append("Primary Match: " + leftToString() + "\n");

		appendRightToStringBuilderAtLeast(st, minscore);

		return st.toString();
	}

	public int countMatchesOfAtLeastScore(double minscore)
	{
		int count = 0;
		for (Entry<NGramSet, Double> e : matches.entrySet())
		{
			if (e.getValue() >= minscore) count++;
		}

		return count;
	}

	public TreeMap<Double, Integer> getScores()
	{
		return ordered_scores;
	}

	public void computeScore()
	{
		this.score = (double) totalCount / (double) window_size;
	}

	public double getScore(NGramSet set)
	{
		return scores_for_primary.get(set);
	}

	public int getTotalCount()
	{
		return totalCount;
	}
}
