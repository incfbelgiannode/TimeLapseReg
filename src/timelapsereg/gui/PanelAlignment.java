package timelapsereg.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.io.FileInfo;
import ij.process.ImageProcessor;
import timelapsereg.gui.components.GridPanel;
import timelapsereg.gui.settings.Settings;
import timelapsereg.process.Data;
import timelapsereg.TransformationTool;

public class PanelAlignment extends JPanel implements ActionListener {

	private Settings		settings;
	private JButton			bnBrowse		= new JButton("Browse");
	private JButton			bnBrowseTransformation	= new JButton("Choose Transformation File");
	private JButton			bnStart			= new JButton("Start");
	private JButton			bnClose			= new JButton("Close");

	public JTextField	txtProjectPath		= new JTextField(IJ.getDirectory("imagej"));
	public JTextField	txtTransformFile	= new JTextField("locate transformations.csv");
	public JTextField	txtAlignedFolder	= new JTextField("transformed");
	public JTextField	txtReferenceImage	= new JTextField("reference Image");

	private Dialog dialog;
	private Data data;
		
	public PanelAlignment(Dialog dialog, Data data, Settings settings) {
		this.dialog = dialog;
		this.settings = settings;
		this.data = data;
		settings.loadRecordedItems();

	
		GridPanel pn = new GridPanel(true);
		pn.place(0, 0, "Project path");
		pn.place(0, 1, txtProjectPath);
		pn.place(1, 0, "Transformation File");
		pn.place(1, 1, txtTransformFile);
		pn.place(2, 0, "Aligned image folder");
		pn.place(2, 1, txtAlignedFolder);

		GridPanel pnButton = new GridPanel(false);
		pnButton.place(0, 0, bnBrowse);
		pnButton.place(0, 1, bnBrowseTransformation);
		pnButton.place(0, 2, bnClose);
		pnButton.place(0, 3, bnStart);

		JPanel main = new JPanel(new BorderLayout());
		main.add(pn, BorderLayout.CENTER);
		main.add(pnButton, BorderLayout.SOUTH);

		bnClose.addActionListener(this);
		bnBrowse.addActionListener(this);
		bnStart.addActionListener(this);
		bnBrowseTransformation.addActionListener(this);

		add(main);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == bnClose)
			close();
		
		else if (event.getSource() == bnBrowse) {
			JFileChooser chooser = new JFileChooser();
			chooser.setCurrentDirectory(new File("."));
			chooser.setDialogTitle("Select the project path");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setAcceptAllFileFilterUsed(false);
			chooser.setApproveButtonText("Select");

			if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				txtProjectPath.setText(chooser.getSelectedFile().getAbsolutePath());
			}
		}
		else if (event.getSource() == bnBrowseTransformation) {
			JFileChooser chooser = new JFileChooser();
			chooser.setCurrentDirectory(new File("."));
			chooser.setDialogTitle("Select the Reference image");
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chooser.setAcceptAllFileFilterUsed(false);
			chooser.setApproveButtonText("Select");

			if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				txtTransformFile.setText(chooser.getSelectedFile().getAbsolutePath());
			}
		}
		else if (event.getSource() == bnStart)
		{
			System.out.println("You have chosen to start the transformation");
			String transformation_path 	= txtTransformFile.getText();
			String dataset_path 		= txtProjectPath.getText();
			File dir 	= new File(dataset_path);
			
			String filenames[] = dir.list();
			int num_files = filenames.length;
			for(int i=0 ; i<filenames.length ; i++)
			{
				filenames[i] = dataset_path + File.separator + filenames[i];
			}
			
			try {
				FileReader fr 		= new FileReader(transformation_path);
				BufferedReader  br 	= new BufferedReader(fr);
				double values[][] = new double[num_files][3];
				String temp = br.readLine();
				for(int i=0 ; i<num_files ; i++)
				{
					if((temp = br.readLine()) ==null)
						break;
					StringTokenizer tkn = new StringTokenizer(temp,",");
					tkn.nextToken();
					tkn.nextToken();
					values[i][0] = Double.parseDouble(tkn.nextToken());
					values[i][1] = Double.parseDouble(tkn.nextToken());
					values[i][2] = Double.parseDouble(tkn.nextToken());
					tkn.nextToken();
					tkn.nextToken();
					
				}
				br.close();
				
				ImagePlus ip = new ImagePlus(filenames[0]);
				FileInfo infoF = ip.getFileInfo();
				int width = infoF.width;
				int height = infoF.height;
				ImageStack stack = new ImageStack(width,height);
				for(int i=0 ; i<num_files ; i++)
				{
					ImagePlus ip2 = new ImagePlus(filenames[i]);
					ImageProcessor ip3 = ip2.getProcessor();
					ip3.translate(-values[i][0], -values[i][1]);
					ip3.rotate(-values[i][2]);
					
					System.out.println("Hello: "+(i+1));
					stack.addSlice("image", ip2.getProcessor());
				}
				new ImagePlus("Aligned Stacks", stack).show();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for(int i=0 ; i<filenames.length ; i++)
			{
				System.out.println(filenames[i]);
			}
		}
	}

	public void close() {
		bnClose.removeActionListener(this);
		bnStart.removeActionListener(this);
		bnBrowse.removeActionListener(this);
		bnBrowseTransformation.removeActionListener(this);
		settings.storeRecordedItems();
	}
}