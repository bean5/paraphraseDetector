package intertextGUI;

import interTextFinder.InterTextualFinder;

import javax.swing.*;
//import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

/*
 * 
 */
public class IntertextualFinderGui
{
	protected static InterTextualFinder	finder							= new InterTextualFinder();

	// Initialize all swing objects.
	private JFrame								f									= new JFrame("Rephrase Locator Beta");														// create
																																																// Frame
	private JPanel								pnlNorth							= new JPanel();																					// North
																																																// quadrant
	private JPanel								pnlCenter						= new JPanel();																					// Center
																																																// quadrant
	private JPanel								pnlSouth							= new JPanel();																					// South
																																																// quadrant

	// Buttons some there is something to put in the panels
	private JButton							btnRun							= new JButton("Run");

	static String								corporaBaseDir					= "corpora/";
	static String								resultsBaseDir					= "intertext_results/";

	private JTextField						txtFieldFilePrimary			= new JTextField(corporaBaseDir
																										+ "");
	private JTextField						txtFieldFileSecondary		= new JTextField(corporaBaseDir
																										+ "");
	// private JTextField txtFieldFilePrimary = new JTextField(corporaBaseDir +
	// "Abinadi.txt");
	// private JTextField txtFieldFileSecondary = new JTextField(corporaBaseDir +
	// "Alma 2.txt");

	private JLabel								lblFilePrimary					= new JLabel("Primary File");
	private JLabel								lblFileSecondary				= new JLabel("Secondary File");

	private JTextField						txtFieldOutFile				= new JTextField(resultsBaseDir
																										+ "results.txt");
	private JLabel								lblFileOut						= new JLabel("Output File");

	// Menu
	private JMenuBar							mb									= new JMenuBar();																				// Menubar
	private JMenu								mnuFile							= new JMenu("File");																			// File
																																																// Entry
																																																// on
																																																// Menu
																																																// bar
	private JMenuItem							mnuItemSave						= new JMenuItem("Save");

	private JMenu								mnuHelp							= new JMenu("Help");																			// Help
																																																// Menu
																																																// entry
	private JMenuItem							mnuItemAbout					= new JMenuItem("About");																		// About
																																																// Entry

	private JMenuItem							mnuItemQuit						= new JMenuItem("Quit");																		// Quit
																																																// sub
																																																// item

	private JTextField						min								= new JTextField("5");
	private JTextField						max								= new JTextField("6");

	private JLabel								lblSecondaryMatchMin			= new JLabel(
																										"Filter out primary matches with less than this many matches: ");
	private JTextField						minSecondaryMatches			= new JTextField("2");

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

	private JTextArea							textArea							= new JTextArea(20, 80);

	/** Constructor for the GUI */
	public IntertextualFinderGui()
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
		txtFieldOutFile.setColumns(20);

		// Add Buttons
		pnlNorth.add(lblFilePrimary);
		pnlNorth.add(txtFieldFilePrimary);

		pnlCenter.add(lblFileSecondary);
		pnlCenter.add(txtFieldFileSecondary);

		pnlSouth.add(lblFileOut);
		pnlSouth.add(txtFieldOutFile);

		JPanel mainBox = new JPanel();
		mainBox.setLayout(new BorderLayout());
		mainBox.add(pnlNorth, BorderLayout.NORTH);
		mainBox.add(pnlCenter, BorderLayout.CENTER);
		mainBox.add(pnlSouth, BorderLayout.SOUTH);

		JPanel secondMainBox = new JPanel();
		secondMainBox.setLayout(new BorderLayout());

		JPanel options = new JPanel();
		options.add(checkMatchCase);
		options.add(checkStrict);
		options.add(checkPorterStemmer);
		options.add(checkUseStopWords);
		options.add(checkMaximizePrimaryWindow);
		// options.add(checkOptimize);

		min.addActionListener(clicked);
		options.add(new JLabel("Fuzzy Search Parameters"));
		options.add(min);
		min.setColumns(3);
		options.add(new JLabel("/"));
		max.addActionListener(clicked);
		options.add(max);
		max.setColumns(3);

		JPanel secondOptions = new JPanel();
		minSecondaryMatches.setColumns(3);
		secondOptions.add(lblSecondaryMatchMin);
		secondOptions.add(minSecondaryMatches);

		secondMainBox.add(mainBox, BorderLayout.NORTH);
		secondMainBox.add(options, BorderLayout.CENTER);
		secondMainBox.add(secondOptions, BorderLayout.SOUTH);

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
		mnuItemSave.addActionListener(clicked);
		mnuItemAbout.addActionListener(clicked);
		mnuItemQuit.addActionListener(new ListenMenuQuit());
	}

	public class ListenMenuQuit implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			System.exit(0);
		}
	}

	public class ListenCloseWindow extends WindowAdapter
	{
		public void windowClosing(WindowEvent e)
		{
			System.exit(0);
		}
	}

	public class Clicked implements ActionListener
	{
//		private JFileChooser	fc;

		public void actionPerformed(ActionEvent e)
		{
			Object source = e.getSource();
			System.out.println(e.getActionCommand());
			if (source == btnRun)
			{
				min.selectAll();
				min.setCaretPosition(min.getDocument().getLength());
				// if(true) return;

				finder.setPrimaryPath(txtFieldFilePrimary.getText().trim());
				finder.setSecondaryPath(txtFieldFileSecondary.getText().trim());

				finder.setMinimumMatches(Integer.parseInt(min.getText().trim()));
				finder.setWindowSize(Integer.parseInt(max.getText().trim()));
				finder.setMinimumSecondaryMatches(Integer
								.parseInt(minSecondaryMatches.getText().trim()));

				finder.setMatchCase(checkMatchCase.isSelected());
				finder.setStrictness(checkStrict.isSelected());
				finder.setUsePorterStemmer(checkPorterStemmer.isSelected());
				finder.setMaximizePrimaryWindow(checkMaximizePrimaryWindow.isSelected());
				finder.setUseStopWords(checkUseStopWords.isSelected());

				finder.findIntertextQuotesFromFiles();

				textArea.setText(finder.toString());
			}
			else if (source == mnuItemSave)
			{
				finder.saveTo(txtFieldOutFile.getText().trim());
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

		public void mouseClicked(MouseEvent e)
		{
		}

		public void mousePressed(MouseEvent e)
		{
		}

		public void mouseReleased(MouseEvent e)
		{
		}

		public void mouseEntered(MouseEvent e)
		{
		}

		public void mouseExited(MouseEvent e)
		{
		}
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
		IntertextualFinderGui gui = new IntertextualFinderGui();
		gui.launchFrame();
	}
}
