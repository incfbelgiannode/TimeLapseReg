package timelapsereg.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import timelapsereg.gui.components.GridPanel;
import timelapsereg.gui.components.ProcessProgressBar;
import timelapsereg.gui.components.SpinnerInteger;
import timelapsereg.gui.settings.Settings;
import timelapsereg.process.Data;
import timelapsereg.process.OperatorReference;
import timelapsereg.process.ReferenceImageConstant;

public class PanelReferenceImage extends GridPanel implements ActionListener {

	private JComboBox	cmbMode				= new JComboBox(ReferenceImageConstant.modesGUI);
	//private String[] 			metric				= new String[] {"Average", "Median", "Maximum", "Minimum"};
	private String[] 			metric				= new String[] {"Average", "Maximum", "Minimum"};	
	private JComboBox	cmbMethodPercentage	= new JComboBox(new String[] { "Average", "Median", "Maximum", "Minimum" });
	private JComboBox	cmbMethodRange		= new JComboBox(new String[] { "Average", "Median", "Maximum", "Minimum" });
	private JComboBox	cmbMethodSliding	= new JComboBox(new String[] { "Average", "Median", "Maximum", "Minimum" });
	private JComboBox	cmbMethodBlock		= new JComboBox(new String[] { "Average", "Median", "Maximum", "Minimum" });
	private JTextField			txtWindow			= new JTextField("", 20);
	private JTextField			txtFile				= new JTextField("", 20);
	private SpinnerInteger		spnPosition			= new SpinnerInteger(1, 1, 999999, 1);
	private SpinnerInteger		spnRangeMin			= new SpinnerInteger(1, 1, 999999, 1);
	private SpinnerInteger		spnRangeMax			= new SpinnerInteger(1, 1, 999999, 1);
	private SpinnerInteger		spnPercentage		= new SpinnerInteger(100, 1, 100, 1);
	private SpinnerInteger		spnSliding			= new SpinnerInteger(100, 1, 999999, 1);
	private SpinnerInteger		spnBlock			= new SpinnerInteger(100, 1, 999999, 1);

	private GridPanel			panels[]	= new GridPanel[ReferenceImageConstant.modes.length];
	private JPanel				cards		= new JPanel(new CardLayout());
	private ProcessProgressBar	progress;
	private Data				data;

	public PanelReferenceImage(ProcessProgressBar progress, Settings settings, Data data) {
		super(false);
		this.progress = progress;
		this.data = data;
		setLayout(new BorderLayout());
		add(cmbMode, BorderLayout.NORTH);

		settings.record("cmbMode", cmbMode, "Specified by a position");
		settings.record("cmbMethodPercentage", cmbMethodPercentage, "Average");
		settings.record("cmbMethodRange", cmbMethodRange, "Average");
		settings.record("cmbMethodSliding", cmbMethodSliding, "Average");
		settings.record("cmbMethodBlock", cmbMethodBlock, "Average");

		settings.record("txtWindow", txtWindow, "");
		settings.record("txtFile", txtFile, "");
		settings.record("spnPosition", spnPosition, "1");
		settings.record("spnRangeMin", spnRangeMin, "1");
		settings.record("spnRangeMax", spnRangeMax, "100");
		settings.record("spnPercentage", spnPercentage, "100");
		settings.record("spnSliding", spnSliding, "100");
		settings.record("spnBlock", spnBlock, "100");
		settings.loadRecordedItems();

		for (int i = 0; i < ReferenceImageConstant.modes.length; i++)
			panels[i] = new GridPanel(true);

		panels[0].place(0, 0, "Frame Position");
		panels[0].place(0, 1, spnPosition);

		panels[1].place(0, 0, "Title of the window");
		panels[1].place(1, 0, txtFile);

		panels[2].place(0, 0, "Filename");
		panels[2].place(1, 0, txtWindow);

		panels[3].place(0, 0, "Method");
		panels[3].place(0, 1, cmbMethodPercentage);
		panels[3].place(1, 0, "Percentage of frames");
		panels[3].place(1, 1, spnPercentage);

		panels[4].place(0, 0, "Method");
		panels[4].place(0, 1, cmbMethodRange);
		panels[4].place(1, 0, "Lower limit");
		panels[4].place(1, 1, spnRangeMin);
		panels[4].place(2, 0, "Upper limit");
		panels[4].place(2, 1, spnRangeMax);

		
		/*
		 * following code removed for implementation of the 1st version
		 * will be implemented in the 2nd version
		 * 
		panels[5].place(0, 0, "Method");
		panels[5].place(0, 1, cmbMethodSliding);
		panels[5].place(1, 0, "Sliding window");
		panels[5].place(1, 1, spnSliding);
		panels[5].place(1, 2, "frames");

		panels[6].place(0, 0, "Method");
		panels[6].place(0, 1, cmbMethodBlock);
		panels[6].place(1, 0, "Size of the block");
		panels[6].place(1, 1, spnBlock);
		panels[6].place(1, 2, "frames");
		*/
		for (int i = 0; i < ReferenceImageConstant.modes.length; i++)
			cards.add(panels[i], ReferenceImageConstant.modes[i]);

		//cards.remove(panels[5]);// removed only for the first version
		//cards.remove(panels[6]);// removed only for the first version
		
		cmbMode.addActionListener(this);
		add(cards, BorderLayout.CENTER);
	}

	private void setCard(String name) {
		CardLayout cl = (CardLayout) (cards.getLayout());
		cl.show(cards, name);
	}

	public void computeReferenceImage(boolean show) {
		int mode = cmbMode.getSelectedIndex();
		if(mode == 0)
		{
			new OperatorReference(progress, data, ReferenceImageConstant.modes[mode], "" + spnPosition.get(), "" + -1);
		}
		else if(mode == 1)
		{
			new OperatorReference(progress, data, ReferenceImageConstant.modes[mode], "" + spnPosition.get(), "" + txtWindow.getText());
		}
		else if(mode == 2)
		{
			new OperatorReference(progress, data, ReferenceImageConstant.modes[mode], "" + spnPosition.get(), "" + txtFile.getText());			
		}
		else if(mode == 3)
		{
			if(cmbMethodPercentage.getSelectedItem() == metric[0]) //metric[0] = "Average"
			{
					System.out.println("Average is selected");
					new OperatorReference(progress, data, ReferenceImageConstant.modes[mode],"" +metric[0],""+spnPercentage.get());
					
			}
			else if(cmbMethodPercentage.getSelectedItem() == metric[1]) // "Median"
			{
				System.out.println("Median is selected");
				new OperatorReference(progress, data, ReferenceImageConstant.modes[mode],"" +metric[1],""+spnPercentage.get());				
			}	
			else if(cmbMethodPercentage.getSelectedItem() == metric[2]) // "Maximum"
			{
				System.out.println("Maximum is selected");
				new OperatorReference(progress, data, ReferenceImageConstant.modes[mode],"" +metric[2],""+spnPercentage.get());				
			}	
			else if(cmbMethodPercentage.getSelectedItem() == metric[3]) // "Minimum"
			{
				System.out.println("Minimum is selected");
				new OperatorReference(progress, data, ReferenceImageConstant.modes[mode],"" +metric[3],""+spnPercentage.get());				
			}
		}
		else if(mode == 4)
		{
			int lower_limit = spnRangeMin.get();
			int upper_limit = spnRangeMax.get();
			
			if(cmbMethodPercentage.getSelectedItem() == metric[0]) //metric[0] = "Average"
			{
					System.out.println("Average is selected");
					new OperatorReference(progress, data, ReferenceImageConstant.modes[mode],"" +metric[0],""+lower_limit,""+upper_limit);
					
			}
			else if(cmbMethodPercentage.getSelectedItem() == metric[1]) // "Median"
			{
				System.out.println("Median is selected");
				new OperatorReference(progress, data, ReferenceImageConstant.modes[mode],"" +metric[1],""+lower_limit,""+upper_limit);				
			}	
			else if(cmbMethodPercentage.getSelectedItem() == metric[2]) // "Maximum"
			{
				System.out.println("Maximum is selected");
				new OperatorReference(progress, data, ReferenceImageConstant.modes[mode],"" +metric[2],""+lower_limit,""+upper_limit);				
			}	
			else if(cmbMethodPercentage.getSelectedItem() == metric[3]) // "Minimum"
			{
				System.out.println("Minimum is selected");
				new OperatorReference(progress, data, ReferenceImageConstant.modes[mode],"" +metric[3],""+lower_limit,""+upper_limit);				
			}	
		}

		if (show)
			data.showReference();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == cmbMode) {
			int mode = cmbMode.getSelectedIndex();
			setCard(ReferenceImageConstant.modes[mode]);
		}
	}
}
