package CharacterData;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class StopWordChecker
{
	static boolean ignoreCase = true;
	static Set<String> stopWords = null;
	
	public StopWordChecker(){}
	
	public static void setCase(boolean b) {ignoreCase = b;}
	public static void setStopWords(List<String> stops, boolean ignoreCase)
	{
//		if(stops != null && stops.size() > 0)
//		{
//			System.out.println("Stops found:");
//			for(String s : stops)
//			{
//				System.out.println("\t" + s);
//			}
////			System.exit(0);
//		}
//		else
//		{
//			System.out.println("Stops is null or empty");
////			System.exit(0);
//		}
		stopWords = new HashSet<String>();
		StopWordChecker.ignoreCase = ignoreCase;
		
		if(ignoreCase)
		{
			for(String s : stops)
				stopWords.add(s.toLowerCase());
		}
		else if(stops != null)
			stopWords.addAll(stops);
	}
	
	public static boolean isStopWord(String word)
	{
		if(stopWords != null)
		{
			for(String s : stopWords)
			{
				if(word.equals(s) || (ignoreCase && s.equals(word.toLowerCase())))
					return true;
			}
			return false;
		}
		return false;
	}
}
