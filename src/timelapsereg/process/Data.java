package timelapsereg.process;

import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.JFrame;

import ij.ImagePlus;
import timelapsereg.gui.TableFrames;
import timelapsereg.process.Frame.Status;

public class Data {

	public String pathProject = "";
	public String pathSource = "";
	public String pathOutput = "";
	public ArrayList<Frame> frames = new ArrayList<Frame>();
	public double framerate = 100;
	public ImagePlus ref;
	public double mean = 0;
	public double stdev = 0;
	
	public JFrame frame = null;
	private TableFrames table;
	
	public double getReferenceMean() {
		return mean;
	}
	
	public double getReferenceStdev() {
		return stdev;
	}
	
	
	public Frame.Status getStatusFrame(int num) {
		for(Frame frame : frames)
			if (frame.number == num) 
				return frame.getStatus();
		return Status.NONE;
	}
	public void showReference() {
		if (ref == null)
			return;
		ref.show();
	}
			
	public void updateTable() {
		if (table != null)
			table.update();
	}
	
	public void scrollTable(Frame frame) {
		if (table != null) {
			for(int i=0; i<frames.size(); i++) {
				if (frames.get(i).number == frame.number) {
					table.getSelectionModel().setSelectionInterval(i, i);
					table.scrollRectToVisible(new Rectangle(table.getCellRect(i, 0, true)));
				}
			}
		}
	}
	
	public void showFramesAsTable() {
		if (frame != null) {
			frame.dispose();
			frame = null;
			table.removeAll();
			table = null;
			System.gc();
		}
		
		frame = new JFrame(pathSource);
		table = new TableFrames(this);
		frame.getContentPane().add(table.getPane(700, 500));
		frame.pack();
		frame.setVisible(true);
	}
	
	
}
