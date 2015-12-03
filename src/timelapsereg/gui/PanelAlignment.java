package timelapsereg.gui;

import ij.IJ;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import timelapsereg.gui.components.GridPanel;
import timelapsereg.gui.components.GridToolbar;
import timelapsereg.gui.settings.Settings;
import timelapsereg.process.Data;
import timelapsereg.process.OperatorAlignment;

public class PanelAlignment extends JPanel implements ActionListener {

	private Settings		settings;
	private JButton			bnBrowseSource	= new JButton("Browse");
	private JButton			bnBrowseAligned	= new JButton("Browse");
	private JButton			bnBrowseTransfo	= new JButton("Browse");
	private JButton			bnStart			= new JButton("Start");

	private JTextField	txtAlignPath		= new JTextField(IJ.getDirectory("imagej"), 25);
	private JTextField	txtAlignedPath		= new JTextField(IJ.getDirectory("imagej"), 25);
	private JTextField	txtTransformation	= new JTextField("transformation.csv", 25);

	private Dialog dialog;
	
	public PanelAlignment(Dialog dialog, Settings settings) {
		this.dialog = dialog;
		this.settings = settings;
		
		settings.record("txtAlignPath", txtAlignPath, "");
		settings.record("txtAlignedPath", txtAlignedPath, "");
		settings.record("txtTransformation", txtTransformation, "");
		
		settings.loadRecordedItems();

		GridToolbar pn = new GridToolbar(true);
		pn.place(0, 0, "Source images path");
		pn.place(0, 2, bnBrowseSource);	
		pn.place(1, 0, 3, 1, txtAlignPath);

		pn.place(2, 0, "Aligned images path");
		pn.place(2, 2, bnBrowseAligned);	
		pn.place(3, 0, 3, 1, txtAlignedPath);

		pn.place(4, 0, "Transformation");
		pn.place(4, 2, bnBrowseTransfo);	
		pn.place(5, 0, 3, 1, txtTransformation);

		GridPanel pnButton = new GridPanel(false);
		pnButton.place(0, 3, bnStart);

		JPanel main = new JPanel(new BorderLayout());
		main.add(pn, BorderLayout.CENTER);
		main.add(pnButton, BorderLayout.SOUTH);

		bnBrowseSource.addActionListener(this);
		bnBrowseAligned.addActionListener(this);
		bnBrowseTransfo.addActionListener(this);
		bnStart.addActionListener(this);

		add(main);
	}
	
	public void setTransformation(String txt) {
		txtTransformation.setText(txt);
	}

	public void setSourceAlign(String txt) {
		txtAlignPath.setText(txt);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == bnStart) {
			new OperatorAlignment(dialog.getProgressBar(), txtAlignPath.getText(), txtAlignedPath.getText(), txtTransformation.getText());
		}
		else if (event.getSource() == bnBrowseSource) 
			browseSaveFolder(txtAlignPath);
		else if (event.getSource() == bnBrowseAligned)
			browseOpenFolder(txtAlignedPath);
		else if (event.getSource() == bnBrowseTransfo)
			browseFile(txtTransformation);
	}

	private void browseSaveFolder(JTextField txt) {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("."));
		chooser.setDialogTitle("Select the path");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setApproveButtonText("Select");
		if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
			txt.setText(chooser.getSelectedFile().getAbsolutePath());
	}
	
	private void browseOpenFolder(JTextField txt) {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("."));
		chooser.setDialogTitle("Select the path");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setApproveButtonText("Select");
		if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
			txt.setText(chooser.getSelectedFile().getAbsolutePath());
	}

	private void browseFile(JTextField txt) {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("."));
		chooser.setDialogTitle("Select the transformation file");
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setApproveButtonText("Select");
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
			txt.setText(chooser.getSelectedFile().getAbsolutePath());
	}
	
	public void close() {
		bnStart.removeActionListener(this);
		bnBrowseSource.removeActionListener(this);
		bnBrowseAligned.removeActionListener(this);
		bnBrowseTransfo.removeActionListener(this);
		settings.storeRecordedItems();
	}
}
