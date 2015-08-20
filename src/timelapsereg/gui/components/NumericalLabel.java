package timelapsereg.gui.components;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

public class NumericalLabel extends JLabel {

	public NumericalLabel() {
		this.setBorder(BorderFactory.createEtchedBorder());
		this.setAlignmentX(JLabel.RIGHT_ALIGNMENT);
	}
	
	public void setText(double value) {
		this.setText(String.format("%2.3f", value));
	}
	
}
