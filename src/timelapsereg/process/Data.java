package timelapsereg.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;

import ij.IJ;
import ij.gui.GUI;
import timelapsereg.gui.components.ProcessProgressBar;
import timelapsereg.gui.settings.Settings;
import timelapsereg.process.Data;

public class Dialog extends JDialog implements ActionListener {

	private Settings			settings	= new Settings("TimelapseReg", IJ.getDirectory("plugins") + "TimelapseReg.txt");
	private JButton				bnHelp		= new JButton("Help");
	private JButton				bnClose		= new JButton("Close");
	private PanelRegistration	pnr;
	private PanelAlignment		pna;
	private ProcessProgressBar	progress	= new ProcessProgressBar("TimelapseReg 1.0");
	private JTabbedPane			tab			= new JTabbedPane();
	public  static String projectPath ="";
	public Dialog(Data data) {
		super(new JFrame(), "TimelapseReg");

		pnr = new PanelRegistration(this, data, settings);
		pna = new PanelAlignment(this, data, settings);
	
		tab.add("Registration", pnr);
		tab.add("Alignement", pna);

		JToolBar tool = new JToolBar();
		tool.setFloatable(false);
		tool.add(bnHelp);
		tool.add(progress);
		tool.add(bnClose);

		JPanel main = new JPanel(new BorderLayout());
		main.add(tab, BorderLayout.CENTER);
		main.add(tool, BorderLayout.SOUTH);

		bnClose.addActionListener(this);
		bnHelp.addActionListener(this);

		add(main);
		pack();
		GUI.center(this);
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == bnClose) {
			pna.close();
			pnr.close();
			bnClose.removeActionListener(this);
			settings.storeRecordedItems();
			dispose();
		}
	}

	public ProcessProgressBar getProgressBar() {
		return progress;
	}

	public void progress(String msg, double unit) {
		progress.progress(msg, unit * 100.0);
	}

	public void progress(String msg, int position, int capacity) {
		progress.progress(msg, position * 100.0 / capacity);
	}

	public void setSelectedIndex(int index) {
		tab.setSelectedIndex(index);
	}
}
