package fileio;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DocumentScanner
{
	public static List<String> tokenize_string_with_punctuation(String string)
	{
		final String regex = " ";
		
		String[] words_array = string.split(regex);

		/*
		 * assume that the average original length is 7
		 */
		List<String> words = new ArrayList<String>();
		for(String w : words_array)
			words.add(w);
		
//		System.out.println(words);
		
		return words;
	}

	private static List<String> tokenize_string_without_punctuation(String string)
	{
		final String regex = "([\\.,:;\\s]?[ \\n\\r])|\\-";
		
		/*
		 * assume that the average original length is 7
		 */
		List<String> words = new ArrayList<String>();

		String[] words_array = string.split(regex);
		
		for(String s : words_array)
			words.add(s);
		System.out.println(words);
//		assert(false);
		return words;
	}
	
	public static List<String> tokenize_string_as_delimited(String string, char delimiter)
	{
//		System.out.println("stops: " + string);
		string = string.trim();
		
		/*
		 * assume that the average original length is 7
		 */
		List<String> words = new ArrayList<String>();
		
		if(string.length() <= 0)
			return words;
		
		String[] words_array = string.split("" + delimiter);
		
//		System.out.println(Arrays.toString(words_array));
		
		for(String s : words_array)
			words.add(s.trim());

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
			//returnString = read(f.toString(), "unicode");
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
		List<String> words = tokenize_string_without_punctuation(primarySourceText);
		return words;
	}
}
