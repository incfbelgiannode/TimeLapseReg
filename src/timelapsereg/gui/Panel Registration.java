package timelapsereg.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import timelapsereg.gui.components.ColorName;
import timelapsereg.gui.components.GridToolbar;
import timelapsereg.gui.components.NumericalLabel;
import timelapsereg.gui.components.SpinnerDouble;
import timelapsereg.gui.components.SpinnerInteger;
import timelapsereg.gui.settings.Settings;
import timelapsereg.process.Data;
import timelapsereg.process.OperatorDiscard;
import timelapsereg.process.OperatorRead;
import timelapsereg.process.OperatorRegistration;

public class PanelRegistration extends JPanel implements ActionListener, ChangeListener {

	private Settings			settings;
	private JButton				bnNext				= new JButton("Next");
	private JButton				bnBack				= new JButton("Back");
	private JButton				bnPreview			= new JButton("Show reference image");
	private JButton				bnBrowseSource		= new JButton("Browse");
	private JButton				bnSaveAs			= new JButton("Save as...");
	private JButton				bnGetInfo			= new JButton("Get info");

	private JPanel				cards				= new JPanel(new CardLayout());
	private String				card				= "source";

	private SpinnerDouble		spnMaxDisplacement	= new SpinnerDouble(20, 0, 9999, 0.5);

	private SpinnerDouble		spnVariationMean	= new SpinnerDouble(20, 0, 9999, 0.5);
	private SpinnerDouble		spnVariationStdev	= new SpinnerDouble(20, 0, 9999, 0.5);
	private SpinnerDouble		spnFrameRate		= new SpinnerDouble(100, 0, 99999, 1);
	private SpinnerInteger		spnNbFrames			= new SpinnerInteger(999999, 1, 999999, 1);
	private JTextField			txtNameContains		= new JTextField(".tif");
	private NumericalLabel		txtReferenceMean	= new NumericalLabel();
	private NumericalLabel		txtReferenceStdev	= new NumericalLabel();
	private NumericalLabel		txtMinimumMean		= new NumericalLabel();
	private NumericalLabel		txtMinimumStdev		= new NumericalLabel();
	private NumericalLabel		txtMaximumMean		= new NumericalLabel();
	private NumericalLabel		txtMaximumStdev		= new NumericalLabel();
	private JTextField			txtSourcePath		= new JTextField("", 17);

	private JTextField			txtSaveAs			= new JTextField("", 25);
	private JComboBox			cmbChartAs			= new JComboBox(new String[] { "Translation and rotation", "Only translation", "Only rotation", "None" });
	private JComboBox			cmbDrawAs			= new JComboBox(new String[] { "Yellow", "Red", "White", "Black", "None" });

	private JCheckBox			chkDisplayCenter	= new JCheckBox("Center ", true);
	private JComboBox			cmbColorCenter		= new JComboBox(ColorName.names);
	private SpinnerInteger		spnOpacityCenter	= new SpinnerInteger(100, 0, 255, 1);
	private JCheckBox			chkDisplayHorizon	= new JCheckBox("Horizon ", true);
	private JComboBox			cmbColorHorizon		= new JComboBox(ColorName.names);
	private SpinnerInteger		spnOpacityHorizon	= new SpinnerInteger(100, 0, 255, 1);

	private JCheckBox			chkCheckoutFill		= new JCheckBox("Complete ", true);
	private JCheckBox			chkCheckoutMedian	= new JCheckBox("Remove Outliers", true);
	private JCheckBox			chkCheckoutRegular	= new JCheckBox("Regularization", true);
	private SpinnerInteger		spnCheckoutRegular	= new SpinnerInteger(1, 0, 999999, 1);

	private JLabel				lblNavigation		= new JLabel("Step 1", SwingConstants.CENTER);
	private JLabel				lblSourcePath		= new JLabel("No valid source path");
	private Dialog				dialog;
	private Data				data;
	private PanelReferenceImage	pnReference;
	private PanelAlignment		pnAlignment;

	public PanelRegistration(Dialog dialog, Data data, Settings settings, PanelAlignment pnAlignment) {
		this.dialog = dialog;
		this.settings = settings;
		this.data = data;
		this.pnAlignment = pnAlignment;

		settings.record("txtSourcePath", txtSourcePath, "");
		settings.record("spnVariationMean", spnVariationMean, "20");
		settings.record("spnVariationStdev", spnVariationStdev, "20");
		settings.record("spnFrameRate", spnFrameRate, "100");
		settings.record("spnNbFrames", spnNbFrames, "999999");
		settings.record("txtNameContains", txtNameContains, ".tif");
		settings.record("spnMaxDisplacement", spnMaxDisplacement, "20");
		settings.record("txtSaveAs", txtSaveAs, "transformation.csv");
		settings.record("cmbChartAs", cmbChartAs, "Translation and rotation");
		settings.record("cmbDrawAs", cmbDrawAs, "Yellow");

		settings.record("chkCheckoutFill", chkCheckoutFill, true);
		settings.record("chkCheckoutMedian", chkCheckoutMedian, true);
		settings.record("chkCheckoutRegular", chkCheckoutRegular, true);
		settings.record("spnCheckoutRegular", spnCheckoutRegular, "1");

		settings.record("chkDisplayCenter", chkDisplayCenter, true);
		settings.record("cmbColorCenter", cmbColorCenter, ColorName.names[1]);
		settings.record("spnOpacityCenter", spnOpacityCenter, "100");
		settings.record("chkDisplayHorizon", chkDisplayHorizon, true);
		settings.record("cmbColorHorizon", cmbColorHorizon, ColorName.names[0]);
		settings.record("spnOpacityHorizon", spnOpacityHorizon, "100");

		settings.loadRecordedItems();

		// Step 1
		lblSourcePath.setBorder(BorderFactory.createEtchedBorder());
		GridToolbar pnSource = new GridToolbar("Source frames");
		pnSource.place(1, 2, 1, 1, bnBrowseSource);
		pnSource.place(2, 2, 1, 1, bnGetInfo);
		pnSource.place(1, 0, 2, 1, txtSourcePath);
		pnSource.place(2, 0, 2, 1, lblSourcePath);

		pnSource.place(5, 0, 2, 1, "Frame rate (Hz)");
		pnSource.place(5, 2, 1, 1, spnFrameRate);
		pnSource.place(6, 0, 2, 1, "Max. number of frames to process");
		pnSource.place(6, 2, 1, 1, spnNbFrames);
		pnSource.place(7, 0, 2, 1, "Filename of frames contains");
		pnSource.place(7, 2, 1, 1, txtNameContains);

		// Step 2
		GridToolbar pnRef = new GridToolbar("Reference image");
		pnReference = new PanelReferenceImage(dialog.getProgressBar(), settings, data);
		pnRef.place(0, 0, 2, 1, pnReference);
		pnRef.place(1, 1, 2, 1, bnPreview);

		// Step 3
		GridToolbar pnCheckin = new GridToolbar("Discarding frames");
		pnCheckin.place(0, 1, "Mean");
		pnCheckin.place(0, 2, "Stdev");
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

		// Step 4
		GridToolbar pnSettings = new GridToolbar("Registration Settings");
		pnSettings.place(1, 0, "Max. displacement");
		pnSettings.place(1, 1, spnMaxDisplacement);
		pnSettings.place(1, 2, "in pixels");
		pnSettings.place(7, 0, "Resulting chart");
		pnSettings.place(7, 1, 2, 1, cmbChartAs);
		
		GridToolbar pnRegularization = new GridToolbar("Time Regularization");
		pnRegularization.place(2, 0, chkCheckoutFill);
		pnRegularization.place(2, 1, chkCheckoutMedian);
		pnRegularization.place(4, 0, chkCheckoutRegular);
		pnRegularization.place(4, 1, spnCheckoutRegular);
		
		GridToolbar pnRegister = new GridToolbar(false);
		pnRegister.place(6, 0, pnSettings);
		pnRegister.place(7, 0, pnRegularization);
			
		// Step 4
		GridToolbar pnSaveAs = new GridToolbar("Output");
		pnSaveAs.place(6, 0, 2, 1, "Transformation output file");
		pnSaveAs.place(6, 2, bnSaveAs);
		pnSaveAs.place(7, 0, 3, 1, txtSaveAs);
		pnRegister.place(7, 0, pnSaveAs);

		GridToolbar pnDisplay = new GridToolbar("Display");
		pnDisplay.place(8, 0, chkDisplayCenter);
		pnDisplay.place(8, 1, cmbColorCenter);
		pnDisplay.place(8, 2, spnOpacityCenter);
		pnDisplay.place(9, 0, chkDisplayHorizon);
		pnDisplay.place(9, 1, cmbColorHorizon);
		pnDisplay.place(9, 2, spnOpacityHorizon);

		GridToolbar pnReport = new GridToolbar(false);
		pnReport.place(6, 0, pnSaveAs);
		pnReport.place(8, 0, pnDisplay);

		cards.add(pnSource, "source");
		cards.add(pnRef, "reference");
		cards.add(pnCheckin, "checkin");
		cards.add(pnRegister, "register");
		cards.add(pnReport, "report");

		lblNavigation.setBorder(BorderFactory.createEtchedBorder());
		JPanel pnButton = new JPanel(new GridLayout());
		pnButton.add(bnBack);
		pnButton.add(lblNavigation);
		pnButton.add(bnNext);

		JPanel main = new JPanel(new BorderLayout());
		main.add(cards, BorderLayout.CENTER);
		main.add(pnButton, BorderLayout.SOUTH);

		bnNext.addActionListener(this);
		bnBack.addActionListener(this);
		bnGetInfo.addActionListener(this);
		bnBrowseSource.addActionListener(this);
		bnSaveAs.addActionListener(this);
		bnPreview.addActionListener(this);
		spnVariationMean.addChangeListener(this);
		spnVariationStdev.addChangeListener(this);
		setCard("source");
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

		String path = txtSourcePath.getText();
		File file = new File(path);
		if (!file.exists()) {
			lblSourcePath.setForeground(Color.RED);
			lblSourcePath.setText("The path do not exist.");
			return;
		}
		if (!file.isDirectory()) {
			lblSourcePath.setForeground(Color.RED);
			lblSourcePath.setText("The path is not a directory.");
			return;
		}
		String[] names = file.list();
		int count = 0;
		String pattern = txtNameContains.getText().toLowerCase();
		for (String name : names) {
			File f = new File(path + File.separator + name);
			if (f.exists())
				if (f.isFile())
					if (f.getName().contains(pattern)) {
						count++;
						if (count >= spnNbFrames.get())
							break;
					}
		}
		lblSourcePath.setForeground(Color.BLACK);
		lblSourcePath.setText("The path contains " + count + " files");
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

		if (event.getSource() == bnNext) {
			if (card.equals("source"))
				reader();
			else if (card.equals("reference"))
				reference();
			else if (card.equals("report"))
				register();
			nav = 1;
		}
		else if (event.getSource() == bnPreview) {
			pnReference.computeReferenceImage(true);
			updateInterface();
		}
		else if (event.getSource() == bnSaveAs)
			browseOutput(txtSaveAs);
		else if (event.getSource() == bnBrowseSource) {
			browseSource();
			updateInterface();
		}
		else if (event.getSource() == bnGetInfo)
			updateInterface();
		else if (event.getSource() == bnNext)
			nav = 1;
		else if (event.getSource() == bnBack)
			nav = -1;

		if (nav != 0) {
			if (card.equals("source"))
				setCard(nav == -1 ? "source" : "reference");
			else if (card.equals("reference"))
				setCard(nav == -1 ? "source" : "checkin");
			else if (card.equals("checkin"))
				setCard(nav == -1 ? "reference" : "register");
			else if (card.equals("register"))
				setCard(nav == -1 ? "checkin" : "report");
			else if (card.equals("report"))
				setCard(nav == -1 ? "register" : "report");
		}
	}

	private void setCard(String name) {
		CardLayout cl = (CardLayout) (cards.getLayout());
		cl.show(cards, name);
		card = name;
		bnBack.setEnabled(!card.equals("source"));

		if (card.equals("source")) {
			lblNavigation.setText("<html><b>Step 1/5</b></html>");
			bnNext.setText("Read");
		}
		else if (card.equals("reference")) {
			lblNavigation.setText("<html><b>Step 2/5</b></html>");
			bnNext.setText("Create");
		}
		else if (card.equals("checkin")) {
			lblNavigation.setText("<html><b>Step 3/5</b></html>");
			bnNext.setText("Accept");
		}
		else if (card.equals("register")) {
			lblNavigation.setText("<html><b>Step 4/5</b></html>");
			bnNext.setText("Next");
		}
		else if (card.equals("report")) {
			lblNavigation.setText("<html><b></b></html>");
			bnNext.setText("Register");
		}
	}

	private void browseSource() {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("."));
		chooser.setDialogTitle("Select the source path");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setApproveButtonText("Select");
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			txtSourcePath.setText(chooser.getSelectedFile().getAbsolutePath());
			pnAlignment.setSourceAlign(chooser.getSelectedFile().getAbsolutePath());
		}
	}

	private void browseOutput(JTextField txt) {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File(txtSourcePath.getText()).getAbsoluteFile());
		chooser.setSelectedFile(new File("transformation.csv"));
		chooser.setDialogTitle("Select the project path");
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setApproveButtonText("Select");
		if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			txt.setText(chooser.getSelectedFile().getAbsolutePath());
			pnAlignment.setTransformation(chooser.getSelectedFile().getAbsolutePath());
		}
	}

	private void reader() {
		data.framerate = spnFrameRate.get();
		data.pathSource = txtSourcePath.getText();
		data.pathProject = new File(txtSourcePath.getText()).getParent();
		new OperatorRead(dialog.getProgressBar(), data, spnNbFrames.get(), txtNameContains.getText());
	}

	private void reference() {
		pnReference.computeReferenceImage(false);
		discard();
		updateInterface();
	}

	private void discard() {
		double m = spnVariationMean.get();
		double s = spnVariationStdev.get();
		new OperatorDiscard(dialog.getProgressBar(), data, m, s);
		data.updateTable();
	}

	private void register() {
		double maxDisplacement = spnMaxDisplacement.get();
		data.pathOutput = txtSaveAs.getText();
		String chartAs = (String)cmbChartAs.getSelectedItem();
		String drawAs = (String)cmbDrawAs.getSelectedItem();
		new OperatorRegistration(dialog.getProgressBar(), data, maxDisplacement, chartAs, drawAs);
		data.updateTable();
	}

	public void close() {
		bnNext.removeActionListener(this);
		bnBack.removeActionListener(this);
		bnBrowseSource.removeActionListener(this);
		bnSaveAs.removeActionListener(this);
		bnPreview.removeActionListener(this);
		spnVariationMean.removeChangeListener(this);
		spnVariationStdev.removeChangeListener(this);
		settings.storeRecordedItems();
	}
}
