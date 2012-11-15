package fileio;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class DocumentSaver
{

	public static void saveToFile(String outFilePath, String string)
	{
		try
		{
			// Create file
			FileWriter fstream = new FileWriter(outFilePath);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(string);
			// Close the output stream
			out.close();
		}
		catch (Exception e)
		{// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

}
