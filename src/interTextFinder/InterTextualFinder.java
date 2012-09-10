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
import NGramSet.NGramSetImpl;

public class InterTextualFinder {
	private String paramString;
	private static FuzzyNGramDocumentComparer<NGramSetImpl> comparer;
	private static HashSet<NGramSet> commonNGrams;

	public void findIntertextQuotesGivenParams(
			String primarySourcePath, String secondarySourcePath, //String outFilePath,
			int minimumMatches, int wordSpanSize, 
			boolean matchCase, boolean strictSearch, boolean usePorterStemmer, boolean useStopWords, 
			int minimumSecondaryMatches
	) {
		File[] fileList = { new File(primarySourcePath), new File(secondarySourcePath) };

		List<String> files = new ArrayList<String>(2);

		readInFiles(fileList, files);

		double start = System.currentTimeMillis();
		
		comparer = new FuzzyNGramDocumentComparer<NGramSetImpl>();
		comparer.setMatchCase(matchCase);
		comparer.setStrict(strictSearch);
		comparer.setPorterStemmerUsage(usePorterStemmer);
		comparer.setUseStopWords(useStopWords);
		commonNGrams = comparer.findCommonNGrams(files.get(0), files.get(1), minimumMatches, wordSpanSize);

		double end = System.currentTimeMillis();
		double totalTime = end - start;
		totalTime /= (1000 * 6);// convert to minutes
		totalTime = totalTime / 10;
		
		filterNGrams(minimumSecondaryMatches);

		paramString = convertParametersToString(
			primarySourcePath, secondarySourcePath, minimumMatches, wordSpanSize, minimumSecondaryMatches, 
			matchCase, strictSearch, usePorterStemmer, useStopWords, totalTime
		);
	}
	
	private void filterNGrams(int minimumSecondaryMatches) {
		HashSet<NGramSet> filteredCommonNGrams = new HashSet<NGramSet>(commonNGrams.size());
		
		for(NGramSet ngram : commonNGrams) {
			if(ngram.size() >= minimumSecondaryMatches) {
				filteredCommonNGrams.add(ngram);
			}
		}
		commonNGrams = filteredCommonNGrams;
	}

	public static String toString(String paramsAsString, HashSet<NGramSet> commonNGrams) {
		StringBuilder str = new StringBuilder();
		
		int matchCount = 0;
		Iterator<NGramSet> itr = commonNGrams.iterator();
		
		//for(NGramSet n : commonNGrams) {
		while(itr.hasNext()) {
			NGramSet nGram = itr.next();
			str.append(nGram.toString());
			str.append("\n\n");
			matchCount += ((NGramSet) nGram).size();
		}
		
		String stringToSaveToFile =
			paramsAsString
			+ "Left Match Count: " + commonNGrams.size()+"\n" 
			+ "Right Match Count: " + matchCount+"\n\n"
			+ str.toString()
		;
		return stringToSaveToFile;
	}

	private static String convertParametersToString(
		String primarySource, String secondarySource, int minimumMatches, int wordSpanSize, int minimumSecondaryMatches, 
		boolean matchCase, boolean strictSearch, boolean usePorterStemmer, boolean useStopWords, double totalTime
	) {
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

		params += "Use Stop Words: ";
		if (useStopWords)
			params += "Yes" + "\n";
		else
			params += "No" + "\n";

		params += "Fuzzy Search Parameters: " + minimumMatches + "/"
				+ wordSpanSize + "\n";
		
		params += "Require at least " + minimumSecondaryMatches + " secondary matches\n";

		params += "Time to complete: " + totalTime + " minutes.\n";
		return params;
	}

	private static void readInFiles(File[] fileList, List<String> files) {
		// TODO
		System.out.println("Consider making everything compatible with unicode.\n");
		for (File f : fileList) {
			FileInputStream fis = null;
			// InputStreamReader in = null;

			try {
				fis = new FileInputStream(f);
				if (fis != null) {
					// in = new InputStreamReader(fis, "UTF-8");
					String newLine = read(f.toString(), "UTF-8");
					//String newLine = read(f.toString(), "unicode");
					
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

	private static String read(String filename, String fEncoding) throws IOException {
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
