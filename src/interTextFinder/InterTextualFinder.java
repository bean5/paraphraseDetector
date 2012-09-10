package interTextFinder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import NGramSet.NGramSet;
import NGramSet.NGramSetSimpleImpl;

public class InterTextualFinder {
	private String paramString;
	private static FuzzyNGramDocumentComparer<NGramSetSimpleImpl> comparer;
	private static HashSet<NGramSet> commonNGrams;

	public void findIntertextQuotesGivenParams(String primarySourcePath,
			String secondarySourcePath,
			//String outFilePath,
			int minimumMatches,
			int wordSpanSize, boolean matchCase, boolean strictSearch,
			boolean usePorterStemmer) {
		// or use a glob of the corpora
		// File[] fileList = {new File(corporaBaseDir + "Abinadi.txt"), new
		// File(corporaBaseDir + "Alma 2.txt")};
		// File[] fileList = {new File(corporaBaseDir + "Abinadi.txt"), new
		// File(corporaBaseDir + "Alma 39-42.txt")};
		File[] fileList = { new File(primarySourcePath),
				new File(secondarySourcePath) };

		List<String> files = new ArrayList<String>(2);

		readInFiles(fileList, files, matchCase);

		double start = System.currentTimeMillis();

		// NGramSet.STRICT = strictSearch;

		comparer = new FuzzyNGramDocumentComparer<NGramSetSimpleImpl>();
		comparer.setMatchCase(matchCase);
		comparer.setStrict(strictSearch);
		comparer.setPorterStemmerUsage(usePorterStemmer);
		commonNGrams = comparer.findCommonNGrams(files.get(0), files.get(1), minimumMatches, wordSpanSize);

		double end = System.currentTimeMillis();
		double totalTime = end - start;
		totalTime /= (1000 * 6);// convert to minutes
		totalTime = totalTime / 10;

		paramString = convertParametersToString(primarySourcePath,
				secondarySourcePath, minimumMatches, wordSpanSize, matchCase,
				strictSearch, usePorterStemmer, totalTime);
	}
	
	public static String toString(String paramsAsString, HashSet<NGramSet> commonNGrams) {
		StringBuilder str = new StringBuilder();
		
		int matchCount = 0;
		Iterator<NGramSet> itr = commonNGrams.iterator();
		while(itr.hasNext()) {
			NGramSet n = itr.next();
			str.append(n.toString());
			str.append("\n\n");
			matchCount += ((NGramSet) n).size();
		}
		
		String stringToSaveToFile =
			paramsAsString
			+ "Left Match Count: " + commonNGrams.size()+"\n" 
			+ "Right Match Count: " + matchCount+"\n\n"
			+ str.toString()
		;
		return stringToSaveToFile;
	}

	private static String convertParametersToString(String primarySource,
			String secondarySource, int minimumMatches, int wordSpanSize,
			boolean matchCase, boolean strictSearch, boolean usePorterStemmer,
			double totalTime) {
		String params = new String();

		params += "Primary Source: " + primarySource + "\n";
		params += "Secondary Source: " + secondarySource + "\n";

		params += "Match Case: ";
		if (matchCase)
			params += "Yes" + "\n";
		else
			params += "No" + "\n";

		params += "Use Porter Stemmer: ";
		if (usePorterStemmer)
			params += "Yes" + "\n";
		else
			params += "No" + "\n";

		params += "Strict Search: ";
		if (strictSearch)
			params += "Yes" + "\n";
		else
			params += "No" + "\n";

		params += "Fuzzy Search Parameters: " + minimumMatches + "/"
				+ wordSpanSize + "\n";

		params += "Time to complete: " + totalTime + " minutes.\n";
		return params;
	}

	private static void readInFiles(File[] fileList, List<String> files,
			boolean matchCase) {
		System.out
				.println("Consider making everything compatible with unicode");
		for (File f : fileList) {
			FileInputStream fis = null;
			// InputStreamReader in = null;

			try {
				fis = new FileInputStream(f);
				if (fis != null) {
					// in = new InputStreamReader(fis, "UTF-8");
					String newLine = read(f.toString(), "UTF-8");
					//String newLine = read(f.toString(), "unicode");
					if (matchCase)
						newLine = new String(newLine.toLowerCase());
					files.add(newLine);
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static String read(String filename, String fEncoding)
			throws IOException {
		File fFilename = new File(filename);

		StringBuilder text = new StringBuilder();
		String NL = System.getProperty("line.separator");
		Scanner scanner = new Scanner(new FileInputStream(fFilename), fEncoding);
		try {
			while (scanner.hasNextLine()) {
				text.append(scanner.nextLine() + NL);
			}
		} finally {
			scanner.close();
		}
		return text.toString();
	}

	public void saveTo(String outFilePath) {
		try{
			  // Create file 
			  FileWriter fstream = new FileWriter(outFilePath);
			  BufferedWriter out = new BufferedWriter(fstream);
			  out.write(toString());
			  //Close the output stream
			  out.close();
		  }	catch (Exception e){//Catch exception if any
			  System.err.println("Error: " + e.getMessage());
		  }
	}
	
	public String toString() {
		if(paramString == null || commonNGrams == null)
			return "No data. ";
		else
			return toString(paramString, commonNGrams).replaceAll("\n", System.getProperty("line.separator"));
	}
}
