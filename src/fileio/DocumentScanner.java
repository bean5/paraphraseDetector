package fileio;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import CharacterData.characterEvaluater;

public class DocumentScanner
{
	public static List<String> tokenize_string_with_punctuation(String string)
	{
		// TODO turn off this switch and use punctuations
		if(1==(2-1))
			return tokenize_string_without_punctuation(string);
		
		char[] chars = string.toCharArray();

		/*
		 * assume that the average word length is 7
		 */
		List<String> words = new ArrayList<String>(chars.length / 8);
		
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
			// TODO fix this because it is wrong (or at least sub-optimal).
			char nextChar = chars[i];

			if(characterEvaluater.isPunctuation(currChar))
			{
				if(length > 0)
				{
					words.add(str.substring(0, length));
					length = 0;
				}
				
				System.out.println("here3");
				words.add(new String("" +currChar));
				continue;
			}
			if (characterEvaluater.isAlphaOrDashFollowedByAlpha(currChar, nextChar))
			{
				str.setCharAt(length, currChar);
				// System.out.print(currChar);
				length++;
			}
			else if (length > 0)
			{
				System.out.println("here2");
				words.add(str.substring(0, length));
			
				total += length;
				length = 0;
			}
		}

		System.out.println("Total length: " + total);
		// System.out.println("Predicted length: " + chars.length / 8);
		System.out.println("Average length: " + total / words.size());

		assert (chars.length == 0 || words.size() > 0);

		return words;
	}

	private static List<String> tokenize_string_without_punctuation(String string)
	{
		char[] chars = string.toCharArray();

		/*
		 * assume that the average word length is 7
		 */
		List<String> words = new ArrayList<String>(chars.length / 8);
		
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
			// TODO fix this because it is wrong (or at least sub-optimal).
			char nextChar = chars[i];

			if (characterEvaluater.isAlphaOrDashFollowedByAlpha(currChar, nextChar))
			{
				str.setCharAt(length, currChar);
				// System.out.print(currChar);
				length++;
			}
			else if (length > 0)
			{
				words.add(str.substring(0, length));
			
				total += length;
				length = 0;
			}
		}

		System.out.println("Total length: " + total);
		// System.out.println("Predicted length: " + chars.length / 8);
		System.out.println("Average length: " + total / words.size());

		assert (chars.length == 0 || words.size() > 0);

		return words;
	}
	
	public static List<String> tokenize_string_as_delimited(String string, char delimiter)
	{
//		System.out.println("stops: " + string);
//		string = string.trim();
		char[] chars = string.toCharArray();
		/*
		 * assume that the average word length is 7
		 */
		List<String> words = new ArrayList<String>(chars.length / 8);
		
		if(string.length() <= 0)
			return words;
		
		String[] words_array = string.split("" + delimiter);
		
//		System.out.println(Arrays.toString(words_array));
		
		for(String s : words_array)
			words.add(s);

		return words;
	}

	public static List<String> readInFileToString_Delimited(String path, char delimiter) throws IOException
	{
		return tokenize_string_as_delimited(readInFileToString(path), delimiter);
	}

	public static String readInFileToString(String path) throws IOException
	{
		FileInputStream fis = null;
		// InputStreamReader in = null;
		
		fis = new FileInputStream(path);

		String returnString = "";
		if (fis != null)
		{
			// in = new InputStreamReader(fis, "UTF-8");
			returnString = read(path.toString(), "UTF-8");
			// String newLine = read(f.toString(), "unicode");
		}
		fis.close();

		return returnString;
	}

	/*
	 * 
	 */
	private static String read(String filename, String fEncoding) throws IOException
	{
		File fFilename = new File(filename);

		StringBuilder text = new StringBuilder();
		String NL = System.getProperty("line.separator");
		Scanner scanner = new Scanner(new FileInputStream(fFilename), fEncoding);

		while (scanner.hasNextLine())
		{
			text.append(scanner.nextLine() + NL);
		}

		scanner.close();

		return text.toString();
	}

	public static List<String> tokenizeFromFile(String path) throws IOException
	{
		String primarySourceText = readInFileToString(path);
		List<String> words1 = tokenize_string_with_punctuation(primarySourceText);
		return words1;
	}
}
