package CharacterData;

public class StopWordChecker
{
	public StopWordChecker(){}
	
	public static boolean isStopWord(String word)
	{
		return word.equals("The") || word.equals("the") || word.equals("And") || word.equals("and")
						|| word.equals("Of") || word.equals("of") || word.equals("That")
						|| word.equals("that") || word.equals("To") || word.equals("to")
						|| word.equals("Thei") || word.equals("thei") || word.equals("Unto")
						|| word.equals("unto") || word.equals("I") || word.equals("i")
						|| word.equals("He") || word.equals("he") || word.equals("It")
						|| word.equals("it");
	}
}
