
package timelapsereg.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import timelapsereg.gui.components.GridPanel;
import timelapsereg.gui.components.GridToolbar;
import timelapsereg.gui.components.NumericalLabel;
import timelapsereg.gui.components.SpinnerDouble;
import timelapsereg.gui.settings.Settings;
import timelapsereg.process.Data;
import timelapsereg.process.OperatorDiscard;
import timelapsereg.process.OperatorRead;
import timelapsereg.process.OperatorRegistration;

public class PanelRegistration extends JPanel implements ActionListener, ChangeListener {

	private Settings	settings;
	private JButton		bnNext			= new JButton("Next");
	private JButton		bnBack			= new JButton("Back");
	private JButton		bnOperate		= new JButton("Read Frames >>");
	private JButton		bnPreview		= new JButton("Show a reference image");
	private JButton		bnBrowseSource	= new JButton("Browse");
	private JButton		bnBrowseOutput	= new JButton("Browse");
	private JButton		bnRead			= new JButton("Preview");
	private JButton		bnRegister		= new JButton("Register");
	
	private JLabel 		infoFolder		= new JLabel("");

	private JPanel	cards	= new JPanel(new CardLayout());
	private String	card	= "source";

	private SpinnerDouble	spnMaxRotation		= new SpinnerDouble(10, 0, 360, 0.5);
	private SpinnerDouble	spnMaxTranslation	= new SpinnerDouble(20, 0, 9999, 0.5);

	private SpinnerDouble	spnVariationMean	= new SpinnerDouble(20, 0, 9999, 0.5);
	private SpinnerDouble	spnVariationStdev	= new SpinnerDouble(20, 0, 9999, 0.5);
	private SpinnerDouble	spnFrameRate		= new SpinnerDouble(100, 1, 99999, 1);
	private JTextField		txtNbFrames			= new JTextField("", 5);

	private NumericalLabel	txtReferenceMean	= new NumericalLabel();
	private NumericalLabel	txtReferenceStdev	= new NumericalLabel();
	private NumericalLabel	txtMinimumMean		= new NumericalLabel();
	private NumericalLabel	txtMinimumStdev		= new NumericalLabel();
	private NumericalLabel	txtMaximumMean		= new NumericalLabel();
	private NumericalLabel	txtMaximumStdev		= new NumericalLabel();
	private JTextField		txtOutputPath		= new JTextField("", 20);
	private JTextField		txtSourcePath		= new JTextField("", 30);

	private Dialog				dialog;
	private Data				data;
	private PanelReferenceImage	pnReference;
	
	private int 				numberOfImages=0;

	public PanelRegistration(Dialog dialog, Data data, Settings settings) {
		this.dialog = dialog;
		this.settings = settings;
		this.data = data;
		

		settings.record("txtOutputPath", txtOutputPath, "");
		settings.record("txtSourcePath", txtSourcePath, "");
		settings.record("spnVariationMean", spnVariationMean, "20");
		settings.record("spnVariationStdev", spnVariationStdev, "20");
		settings.record("spnFrameRate", spnFrameRate, "100");
		settings.record("spnMaxRotation", spnMaxRotation, "20");
		settings.record("spnMaxTranslation", spnMaxTranslation, "20");
		//settings.storeRecordedItems();
		
		
		
		settings.loadRecordedItems();

		// Step 1
		GridToolbar pnSource = new GridToolbar("Step 1: Source frames");
		pnSource.setFloatable(false);
		pnSource.place(2, 0, 1, 1, "Source path");
		pnSource.place(3, 0, 3, 1, txtSourcePath);
		pnSource.place(4, 0, 1, 1, bnBrowseSource);
		pnSource.place(5, 0, 1, 1, infoFolder);
		pnSource.place(6, 0, 1, 1, "Frame rate (Hz)");
		pnSource.place(6, 1, 1, 1, spnFrameRate);
		pnSource.place(6, 2, 1, 1, bnRead);

		// Step 2
		GridToolbar pnRef = new GridToolbar("Step 2: Reference image");
		pnReference = new PanelReferenceImage(dialog.getProgressBar(), settings, data);
		pnRef.place(0, 0, 2, 1, pnReference);
		pnRef.place(1, 1, 2, 1, bnPreview);

		// Step 3
		GridToolbar pnCheckin = new GridToolbar("Step 3: Discarding frames");
		pnCheckin.place(0, 1, "Mean");
		pnCheckin.place(0, 2, "Standard Deviation");
		pnCheckin.place(1, 0, "Reference");
		pnCheckin.place(1, 1, txtReferenceMean);
		pnCheckin.place(1, 2, txtReferenceStdev);
		pnCheckin.place(2, 0, "Tolerance +/-");
		pnCheckin.place(2, 1, spnVariationMean);
		pnCheckin.place(2, 2, spnVariationStdev);
		pnCheckin.place(3, 0, "Minimum");
		pnCheckin.place(3, 1, txtMinimumMean);
		pnCheckin.place(3, 2, txtMinimumStdev);
		pnCheckin.place(4, 0, "Maximum");
		pnCheckin.place(4, 1, txtMaximumMean);
		pnCheckin.place(4, 2, txtMaximumStdev);
		pnCheckin.place(6, 0, "Status");
		pnCheckin.place(6, 1, 2, 1, txtNbFrames);

		// Step 4
		GridToolbar pnRegister = new GridToolbar("Step 4: Registration");
		pnRegister.place(0, 0, 3, 1, "Frame-to-frame Constraint");
		pnRegister.place(1, 0, "Max. translation");
		pnRegister.place(1, 1, spnMaxTranslation);
		pnRegister.place(1, 2, "in pixels");
		pnRegister.place(2, 0, "Max. rotation");
		pnRegister.place(2, 1, spnMaxRotation);
		pnRegister.place(2, 2, "in degrees");
		pnRegister.place(4, 0, "Output path");
		pnRegister.place(4, 1, 2, 1, txtOutputPath);
		pnRegister.place(5, 1, bnBrowseOutput);
		pnRegister.place(5, 2, bnRegister);

		// Step 5
		GridToolbar pnCheckout = new GridToolbar("Step 5: Check out");
		pnCheckout.place(2, 0, "TO DO Post processing");

		cards.add(pnSource, "source");
		cards.add(pnRef, "reference");
		cards.add(pnCheckin, "checkin");
		cards.add(pnRegister, "register");
		cards.add(pnCheckout, "checkout");

		GridPanel pnButton = new GridPanel(false);
		pnButton.place(0, 1, bnBack);
		bnBack.hide();
		pnButton.place(0, 2, bnNext);
		pnButton.place(0, 3, "     ");
		pnButton.place(0, 4, bnOperate);

		JPanel main = new JPanel(new BorderLayout());
		main.add(cards, BorderLayout.CENTER);
		main.add(pnButton, BorderLayout.SOUTH);

		bnNext.addActionListener(this);
		bnBack.addActionListener(this);
		bnRegister.addActionListener(this);
		bnBrowseSource.addActionListener(this);
		bnBrowseOutput.addActionListener(this);
		bnRead.addActionListener(this);
		bnOperate.addActionListener(this);
		bnPreview.addActionListener(this);
		spnVariationMean.addChangeListener(this);
		spnVariationStdev.addChangeListener(this);

		add(main);
		updateInterface();
	}

	private void updateInterface() {
		double m = data.getReferenceMean();
		double s = data.getReferenceStdev();
		double mv = spnVariationMean.get() / 100;
		double ms = spnVariationStdev.get() / 100;
		txtReferenceMean.setText(m);
		txtReferenceStdev.setText(s);
		txtMinimumMean.setText(m * (1.0 - mv));
		txtMinimumStdev.setText(s * (1.0 - ms));
		txtMaximumMean.setText(m * (1.0 + mv));
		txtMaximumStdev.setText(s * (1.0 + ms));
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == spnVariationMean) {
			updateInterface();
			discard();
		}
		if (e.getSource() == spnVariationStdev) {
			updateInterface();
			discard();
		}
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		int nav = 0;
		Dialog.projectPath = data.pathProject;
		System.out.println("in the constructor of PanelRegistration: "+Dialog.projectPath);
		if (event.getSource() == bnOperate) {
			if (card.equals("source"))
				reader();
			else if (card.equals("reference"))
				pnReference.computeReferenceImage(true);
			else if (card.equals("checkin"))
				reference();
			else if (card.equals("register"))
				register();
			else if (card.equals("checkout"))
				dialog.setSelectedIndex(1);
			nav = 1;
		}
		else if (event.getSource() == bnPreview) {
			pnReference.computeReferenceImage(true);
			updateInterface();
		}
		else if (event.getSource() == bnBrowseOutput)
			browseOutput();
		else if (event.getSource() == bnBrowseSource)
			browseSource();
		else if (event.getSource() == bnRead)
		{
			
			/*synchronized(this)
			{
				try {
					while(!reader())
						this.wait();
					
					 
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}*/
			reader();
			numberOfImages = data.frames.size();
			infoFolder.setText(numberOfImages+" images in the Folder");
			
		}
		else if (event.getSource() == bnRegister)
			register();
		else if (event.getSource() == bnNext)
			nav = 1;
		else if (event.getSource() == bnBack)
			nav = -1;

		if (nav != 0) {
			if (card.equals("source"))
			{
				setCard(nav == -1 ? "source" : "reference");
			}
			else if (card.equals("reference"))
				setCard(nav == -1 ? "source" : "checkin");
			else if (card.equals("checkin"))
				setCard(nav == -1 ? "reference" : "register");
			else if (card.equals("register"))
				setCard(nav == -1 ? "checkin" : "checkout");
			else if (card.equals("checkout"))
				setCard(nav == -1 ? "register" : "checkout");
		}
	}

	private void setCard(String name) {
		CardLayout cl = (CardLayout) (cards.getLayout());
		cl.show(cards, name);
		card = name;

		if (card.equals("source")) {
			bnOperate.setText("Read Frames >>");
			settings.storeValue("txtSourcePath", txtSourcePath.getText());
			bnBack.hide();
		}
		else if (card.equals("reference")) {
			bnOperate.setText("Accept Reference >>");
			bnBack.show();
		}
		else if (card.equals("checkin")) {
			bnOperate.setText("Accept Frames >>");
			reference();
		}
		else if (card.equals("register")) {
			bnOperate.setText("Run Registration >>");
		}
		else if (card.equals("checkout")) {
			bnOperate.setText("Accept >>");
		}
	}

	private void browseSource() {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("."));
		chooser.setDialogTitle("Select the project path");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setApproveButtonText("Select");
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			txtSourcePath.setText(chooser.getSelectedFile().getAbsolutePath());
			txtOutputPath.setText(chooser.getSelectedFile().getParent());
		}
	}

	private void browseOutput() {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("."));
		chooser.setDialogTitle("Select the project path");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setApproveButtonText("Select");
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			txtOutputPath.setText(chooser.getSelectedFile().getAbsolutePath());
		}
	}

	private void reader() {
		data.framerate = spnFrameRate.get();
		data.pathSource = txtSourcePath.getText();
		data.pathProject = new File(txtSourcePath.getText()).getParent();
		new OperatorRead(dialog.getProgressBar(), data);
		
	}

	private void reference() {
		pnReference.computeReferenceImage(false);
		discard();
		updateInterface();
	}

	private void discard() {
		double m = spnVariationMean.get();
		double s = spnVariationStdev.get();
		OperatorDiscard op = new OperatorDiscard(dialog.getProgressBar(), data, m, s);
		txtNbFrames.setText(op.getStatus());
		data.updateTable();
	}

	private void register() {
		double t = spnMaxTranslation.get();
		double r = spnMaxRotation.get();
		data.pathOutput = txtOutputPath.getText();
		new OperatorRegistration(dialog.getProgressBar(), data, t, r);
		data.updateTable();
	}

	public void close() {
		bnNext.removeActionListener(this);
		bnBack.removeActionListener(this);
		bnRegister.removeActionListener(this);
		bnBrowseSource.removeActionListener(this);
		bnBrowseOutput.removeActionListener(this);
		bnRead.removeActionListener(this);
		bnOperate.removeActionListener(this);
		bnPreview.removeActionListener(this);
		spnVariationMean.removeChangeListener(this);
		spnVariationStdev.removeChangeListener(this);
		settings.storeRecordedItems();
	}

}
