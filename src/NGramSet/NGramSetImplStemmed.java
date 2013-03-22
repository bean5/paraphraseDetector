package NGramSet;

import CharacterData.PorterStemmer;

public class NGramSetImplStemmed extends NGramSetImpl implements NGramSet
{
	public NGramSetImplStemmed(int size)
	{
		super(size);
	}

	public NGramSetImplStemmed(NGramSet other)
	{
		super(other);
	}

	public NGramSetImplStemmed(NGramSetImplStemmed other)
	{
		this((NGramSetImpl) other);
	}

	@Override
	protected String modifyWord(String word)
	{
		String modifiedWord = null;
		if (!matchCase)
		{
			modifiedWord = new String(word).toLowerCase();
		}
		else
		{
			modifiedWord = word;
		}
		return findWordStem(modifiedWord);
	}

	protected String findWordStem(String word)
	{
		PorterStemmer stem = new PorterStemmer();
		char[] letters = word.toCharArray();
		for (int i = 0; i < letters.length; i++)
		{
			stem.add(letters[i]);
		}
		stem.stem();

		String stemmed = stem.toString();
		// System.out.println("Word stemmed:\t" + original + ":" + stemmed);
		return stemmed;
	}
}
