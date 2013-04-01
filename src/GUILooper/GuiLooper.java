package GUILooper;

import fileio.DocumentScanner;
import interTextFinder.InterTextualFinder;
import interTextFinder.InterTextualFinderLooper;

import javax.swing.*;
//import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.io.File;
import java.io.IOException;

/*
 * 
 */
public class GuiLooper
{
	protected static InterTextualFinder	finder							= new InterTextualFinderLooper();

	// Initialize all swing objects.
	private JFrame								f									= new JFrame("GUI Looper");			// create
																																		// Frame
	private JPanel								pnlNorth							= new JPanel();							// North
	// quadrant
	private JPanel								pnlCenter						= new JPanel();							// North
	// quadrant
	private JPanel								pnlCenterb						= new JPanel();							// Center
																																		// quadrant
	private JPanel								pnlSouth							= new JPanel();							// South
																																		// quadrant

	// Buttons some there is something to put in the panels
	private JButton							btnRun							= new JButton("Run");

	static String								corporaBaseDir					= "corpora/";
	static String								resultsBaseDir					= "intertext_results/";

	// private JTextField txtFieldFilePrimary = new JTextField(corporaBaseDir
	// + "");
	// private JTextField txtFieldFileSecondary = new JTextField(corporaBaseDir
	// + "");
	private JTextField						txtFieldFilePrimary			= new JTextField(corporaBaseDir
																						 +
																						 "gen1-3.txt");
//																										+ "test3.txt");
	private JTextField						txtFieldFileSecondary		= new JTextField(corporaBaseDir
																						 +
																						 "19-324a.txt");
//																										+ "test4.txt");
	private JTextField						txtFieldFileStopWords		= new JTextField(corporaBaseDir
					 +
					 "stopwords.txt");

	private JLabel								lblFilePrimary					= new JLabel("Primary File");
	private JLabel								lblFileSecondary				= new JLabel("Secondary File");
	private JLabel								lblFileStopWords				= new JLabel("Stopwords File");

	private JTextField						txtFieldOutFile				= new JTextField(resultsBaseDir
																										+ "results.txt");
	private JLabel								lblFileOut						= new JLabel("Output File");

	// Menu
	private JMenuBar							mb									= new JMenuBar();						// Menubar
	private JMenu								mnuFile							= new JMenu("File");					// File
																																		// Entry
																																		// on
																																		// Menu
																																		// bar
	private JMenuItem							mnuItemSave						= new JMenuItem("Save");

	private JMenu								mnuHelp							= new JMenu("Help");					// Help
																																		// Menu
																																		// entry
	private JMenuItem							mnuItemAbout					= new JMenuItem("About");				// About
																																		// Entry

	private JMenuItem							mnuItemQuit						= new JMenuItem("Quit");				// Quit
																																		// sub
																																		// item

	private JTextField						min								= new JTextField("1");
	// private JTextField max = new JTextField("500");
	//
	// private JLabel lblSecondaryMatchMin = new JLabel(
	// "Filter out primary matches with less than this many matches: ");
	private JTextField						minSecondaryMatches			= new JTextField("1");

	private JCheckBox							checkMatchCase					= new JCheckBox("Match Case",
																										false);
	private JCheckBox							checkStrict						= new JCheckBox("Strict", true);
	private JCheckBox							checkPorterStemmer			= new JCheckBox(
																										"Use Porter Stemmer",
																										true);
	private JCheckBox							checkUseStopWords				= new JCheckBox("Use Stop Words",
																										true);
	private JCheckBox							checkMaximizePrimaryWindow	= new JCheckBox(
																										"Maximize Primary Window Size",
																										false);
	private JCheckBox							checkBestScoresOnly			= new JCheckBox(
																										"Print Best Scores Only",
																										// false);
																										true);

	private JTextArea							textArea_stopWords			= new JTextArea(10, 20);
	private JTextArea							textArea							= new JTextArea(20, 80);

	/** Constructor for the GUI */
	public GuiLooper()
	{
		// checkStrict.setEnabled(false);
		// checkPorterStemmer.setEnabled(false);
		// checkUseStopWords.setEnabled(false);
		ActionListener clicked = new Clicked();

		// Set menubar
		f.setJMenuBar(mb);
		// f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Build Menus
		mnuFile.add(mnuItemSave);
		mnuFile.add(mnuItemQuit); // Create Quit line

		mnuHelp.add(mnuItemAbout); // Create About line

		mb.add(mnuFile); // Add Menu items to form
		// mb.add(mnuHelp);

		txtFieldFilePrimary.setColumns(20);
		txtFieldFileSecondary.setColumns(20);
		txtFieldFileStopWords.setColumns(20);
		txtFieldOutFile.setColumns(20);

		// Add Buttons
		pnlNorth.add(lblFilePrimary);
		pnlNorth.add(txtFieldFilePrimary);

		pnlCenter.add(lblFileSecondary);
		pnlCenter.add(txtFieldFileSecondary);

		pnlCenterb.add(lblFileStopWords);
		pnlCenterb.add(txtFieldFileStopWords);

		pnlSouth.add(lblFileOut);
		pnlSouth.add(txtFieldOutFile);

		JPanel mainBox = new JPanel();
		mainBox.setLayout(new BorderLayout());
		mainBox.add(pnlNorth, BorderLayout.NORTH);
		
		JPanel mainBox_sub = new JPanel();
		mainBox_sub.setLayout(new BorderLayout());
		mainBox_sub.add(pnlCenter, BorderLayout.NORTH);
		mainBox_sub.add(pnlCenterb, BorderLayout.CENTER);
		
		mainBox.add(mainBox_sub, BorderLayout.CENTER);
		mainBox.add(pnlSouth, BorderLayout.SOUTH);

		JPanel secondMainBox = new JPanel();
		secondMainBox.setLayout(new BorderLayout());

		JPanel options = new JPanel();
		options.add(checkMatchCase);
		options.add(checkStrict);
		options.add(checkPorterStemmer);
		options.add(checkUseStopWords);
		options.add(checkMaximizePrimaryWindow);

		// JPanel secondOptions = new JPanel();
		// minSecondaryMatches.setColumns(3);
		// secondOptions.add(lblSecondaryMatchMin);
		// secondOptions.add(minSecondaryMatches);
		//
		// min.addActionListener(clicked);
		// min.setColumns(3);
		// secondOptions.add(new JLabel("Fuzzy Search Parameters"));
		// secondOptions.add(min);
		// secondOptions.add(new JLabel("/"));
		//
		// max.addActionListener(clicked);
		// max.setColumns(3);
		// max.addActionListener(clicked);
		// secondOptions.add(max);
		//
		secondMainBox.add(mainBox, BorderLayout.NORTH);
		secondMainBox.add(options, BorderLayout.CENTER);
		
		textArea_stopWords.setMargin(new Insets(5, 5, 5, 5));
		textArea_stopWords.setEditable(true);
		JScrollPane stopWordsScrollPane = new JScrollPane(textArea_stopWords);
		secondMainBox.add(stopWordsScrollPane, BorderLayout.EAST);
		
		// secondMainBox.add(secondOptions, BorderLayout.SOUTH);

		// Setup Main Frame
		f.getContentPane().setLayout(new BorderLayout());
		f.getContentPane().add(secondMainBox, BorderLayout.NORTH);
		f.getContentPane().add(btnRun, BorderLayout.EAST);

		textArea.setMargin(new Insets(5, 5, 5, 5));
		textArea.setEditable(false);
		JScrollPane logScrollPane = new JScrollPane(textArea);
		f.getContentPane().add(logScrollPane, BorderLayout.CENTER);
		textArea.setText("Output will display here with time.");

		f.pack();
		f.setVisible(true);

		// Allows the Swing App to be closed
		f.addWindowListener(new ListenCloseWindow());

		// Add listeners
		btnRun.addActionListener(clicked);

		txtFieldFileStopWords.addActionListener(clicked);
		checkUseStopWords.addActionListener(clicked);
		checkMaximizePrimaryWindow.addActionListener(clicked);
		checkBestScoresOnly.addActionListener(clicked);
		mnuItemSave.addActionListener(clicked);
		mnuItemAbout.addActionListener(clicked);
		mnuItemQuit.addActionListener(new ListenMenuQuit());
		
		((Clicked) clicked).displayStopWords();
		//((Clicked) clicked).run_find_given_parameters();
	}

	/*
	 * Closes
	 */
	public class ListenMenuQuit implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			System.exit(0);
		}
	}

	/*
	 * Closes
	 */
	public class ListenCloseWindow extends WindowAdapter
	{
		public void windowClosing(WindowEvent e)
		{
			System.exit(0);
		}
	}

	/*
	 * ActionListener Handling
	 */
	public class Clicked implements ActionListener
	{
		// private JFileChooser fc;
		
		public void actionPerformed(ActionEvent e)
		{
			Object source = e.getSource();
			// System.out.println(e.getActionCommand());
			if (source == btnRun)
			{
				run_find_given_parameters();
			}
			else if (source == txtFieldFileStopWords)
			{
				displayStopWords();
			}
			else if (source == checkUseStopWords)
			{
				displayStopWords();
			}
			else if (source == mnuItemSave)
			{
				finder.saveTo(txtFieldOutFile.getText().trim());
			}
			else if (source == checkMaximizePrimaryWindow)
			{
				if (checkMaximizePrimaryWindow.isSelected())
				{
					checkStrict.setSelected(true);
					checkStrict.setEnabled(false);
				}
				else
				{
					checkStrict.setEnabled(true);
				}
			}
			else if (source == checkBestScoresOnly)
			{
				if (checkBestScoresOnly.isSelected())
				{
					minSecondaryMatches.setText("1");
					minSecondaryMatches.setEnabled(false);
				}
				else
				{
					minSecondaryMatches.setEnabled(true);
				}
			}
			else if (source == mnuItemAbout)
			{
				// System.exit(0);
				// launch about frame
			}
			else if (source == min)
			{

			}
		}

		public void displayStopWords()
		{
			String stopWordsAsString;
			if(checkUseStopWords.isSelected() == false)
			{
				stopWordsAsString = "";
			}
			else
			{
				try
				{
					stopWordsAsString = DocumentScanner.readInFileToString(txtFieldFileStopWords.getText());
					stopWordsAsString = stopWordsAsString.replace(", ", ",");
					stopWordsAsString = stopWordsAsString.replace(",", ",\n");
				}
				catch(IOException ioe)
				{
					stopWordsAsString = "";
				}
			}
			textArea_stopWords.setText(stopWordsAsString);
		}

		public void run_find_given_parameters()
		{
			textArea.setText("Running...");
			min.selectAll();
			min.setCaretPosition(min.getDocument().getLength());
			// if(true) return;

			finder.setPrimaryPath(txtFieldFilePrimary.getText().trim());
			finder.setSecondaryPath(txtFieldFileSecondary.getText().trim());
			
			if(checkUseStopWords.isSelected())
				finder.setStopWords(textArea_stopWords.getText(), checkMatchCase.isSelected());

			finder.setMinimumScore(Integer.parseInt(min.getText().trim()));
			// finder.setWindowSize(Integer.parseInt(max.getText().trim()));
			// finder.setMinimumSecondaryMatches(Integer
			// .parseInt(minSecondaryMatches.getText().trim()));

			finder.setMatchCase(checkMatchCase.isSelected());
			finder.setStrictness(checkStrict.isSelected());
			finder.setUsePorterStemmer(checkPorterStemmer.isSelected());
			finder.setMaximizePrimaryWindow(checkMaximizePrimaryWindow.isSelected());
			finder.setUseStopWords(checkUseStopWords.isSelected());
			finder.setPrintBestScoresOnly(checkBestScoresOnly.isSelected());

			boolean noError = true;
			try
			{
				finder.findIntertextQuotesFromFiles();
			}
			catch (IOException e1)
			{
				noError = false;
				textArea.setText(e1.toString());
			}

			if (noError) textArea.setText(finder.toString());
		}

		private java.util.List<String> getStopWords()
		{
			String stop_words_path = txtFieldFileStopWords.getText().trim();
			File f = new File(stop_words_path);
			if(f.exists() && f.isFile() && f.canRead())
			{
				char comma = ',';
				
				java.util.List<String> words = null;
				try
				{
					words = DocumentScanner.readInFileToString_Delimited(stop_words_path, comma);
				}
				catch(Exception e)
				{
					return words;
				}
				return words;
			}
			else
				return null;
		}

		public void mouseClicked(MouseEvent e) {}

		public void mousePressed(MouseEvent e) {}

		public void mouseReleased(MouseEvent e) {}

		public void mouseEntered(MouseEvent e) {}

		public void mouseExited(MouseEvent e) {}
	}

	// Display Frame
	public void launchFrame()
	{
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.pack(); // Adjusts panel to components for display
		f.setVisible(true);
	}

	public static void main(String[] args)
	{
		GuiLooper gui = new GuiLooper();
		gui.launchFrame();
	}
}
