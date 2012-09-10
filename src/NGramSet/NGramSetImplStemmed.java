package NGramSet;

public class NGramSetImplStemmed extends NGramSetImpl implements NGramSet {
	public NGramSetImplStemmed(int size) {super(size);}
	
	public NGramSetImplStemmed(NGramSet other) {super(other);}
	
	public NGramSetImplStemmed(NGramSetImplStemmed other) {this((NGramSetImpl) other);}
	
	@Override
	protected String modifyWord(String word) {
		String modifiedWord = null;
		if(!matchCase) {
			modifiedWord = new String(word).toLowerCase();
		}
		else {
			modifiedWord = word;
		}
		return findWordStem(modifiedWord);
	}
	
	protected String findWordStem(String word) {
		final PorterStemmer stem = new PorterStemmer();
		char[] letters = word.toCharArray();
		for(int i = 0 ; i < letters.length; i++) {
			stem.add(letters[i]);
		}
		stem.stem();

		String stemmed = stem.toString();
		//System.out.println("Word stemmed:\t" + word + ":" + stemmed);
		return stemmed;
	}
	
	@Override
	public boolean isStopWord(String word) {
		if(useStopWords == false)
			return false;
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
			word.equals("Thei")
			||
			word.equals("thei")
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
	}
}
