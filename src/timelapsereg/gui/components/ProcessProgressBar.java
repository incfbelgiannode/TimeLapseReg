package timelapsereg.gui.components;

import java.awt.Dimension;

import javax.swing.JProgressBar;

public class ProcessProgressBar extends JProgressBar {

	private double chrono;
	
	public ProcessProgressBar(String msg) {
		setStringPainted(true);
		reset(msg);
		this.setPreferredSize(new Dimension(250, 50));
	}

	public void progress(String msg, double value) {
		progress(msg, (int)value);
	}

	public void progress(String msg, int value) {
		double elapsedTime = System.currentTimeMillis() - chrono;
		String t = " [" + (elapsedTime > 3000 ?  Math.round(elapsedTime/10)/100.0 + "s." : elapsedTime + "ms") + "]";
		setValue(value);
		setString(msg + t);
	}
	
	public void reset(String msg) {
		chrono = System.currentTimeMillis();
		setValue(0);
		setString(msg );
	}
}
