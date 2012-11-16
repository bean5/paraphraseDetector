package CharacterData;

public class characterEvaluater
{
	public static boolean isAlphaOrDashFollowedByAlpha(char currChar, char nextChar)
	{
		return isAlpha(currChar) || (is(currChar, '-') && isAlpha(nextChar));
	}

	protected static boolean is(char currChar, char c)
	{
		return currChar == c;
	}

	protected static boolean isAlpha(char currChar)
	{
		return (currChar >= 'a' && currChar <= 'z') || (currChar >= 'A' && currChar <= 'Z');
	}

	public static boolean isPunctuation(char currChar)
	{
		boolean is_punctuation = currChar == '.' || currChar == '?' || currChar == '"'
						|| currChar == '\'' || currChar == '$' || currChar == ':' || currChar == ';'
						|| currChar == ',' || currChar == '>' || currChar == '<' || currChar == '%'
						|| currChar == '#' || currChar == '(' || currChar == ')';

		if (is_punctuation) System.out.println("Punct:" + currChar);

		return is_punctuation;
	}
}