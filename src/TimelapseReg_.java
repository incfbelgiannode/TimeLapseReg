import ij.plugin.PlugIn;
import timelapsereg.gui.Dialog;
import timelapsereg.process.Data;

public class TimelapseReg_ implements PlugIn {

	private Data data = new Data();

	@Override
	public void run(String arg) {
		new Dialog(data);
	}
}
