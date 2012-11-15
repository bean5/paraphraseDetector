package fileio;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DocumentScanner
{

	public static List<String> tokenizeString(String string)
	{
		char[] chars = string.toCharArray();

		List<String> words = new ArrayList<String>(chars.length / 8);// assume
		// that the
		// average
		// word
		// length
		// is 7
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
		// System.out.println("Predicted length: " + chars.length / 8);
		System.out.println("Average length: " + total / words.size());

		assert (chars.length == 0 || words.size() > 0);

		return words;
	}


	public static String readInFileToString(String path) throws IOException
	{
		FileInputStream fis = null;
		// InputStreamReader in = null;

		// try
		// {
		fis = new FileInputStream(path);
		
		String returnString = "";
		if (fis != null)
		{
			// in = new InputStreamReader(fis, "UTF-8");
			returnString = read(path.toString(), "UTF-8");
			// String newLine = read(f.toString(), "unicode");
		}
		fis.close();
		// }
		// catch (UnsupportedEncodingException e)
		// {
		// e.printStackTrace();
		// }
		// catch (FileNotFoundException e1)
		// {
		// e1.printStackTrace();
		// }
		// catch (IOException e)
		// {
		// e.printStackTrace();
		// }
		
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
		List<String> words1 = tokenizeString(primarySourceText);
		return words1;
	}
}
