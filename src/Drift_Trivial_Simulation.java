import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.gui.Line;
import ij.gui.Overlay;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;


public class Drift_Trivial_Simulation implements PlugIn {

	@Override
	public void run(String event) {
		ImagePlus imp = WindowManager.getCurrentImage();
		if (imp == null) {
			IJ.error("No open image.");
			return;
		}

		ImageStack stack = new ImageStack(imp.getWidth(), imp.getHeight());
		stack.addSlice("", imp.getProcessor());

		ImageProcessor ip1 = imp.getProcessor().duplicate();
		ip1.setBackgroundValue(0);
		ip1.setColor(0);
		ip1.rotate(10);
		stack.addSlice("rotate 10", ip1);

		ImageProcessor ip2 = imp.getProcessor().duplicate();
		ip2.setBackgroundValue(0);
		ip2.setColor(0);
		ip2.rotate(-10);
		stack.addSlice("rotate -10", ip2);

		ImageProcessor ip3 = imp.getProcessor().duplicate();
		ip3.setBackgroundValue(0);
		ip3.setColor(0);
		ip3.translate(-5, 0);
		stack.addSlice("rotate -5", ip3);

		ImageProcessor ip4 = imp.getProcessor().duplicate();
		ip4.setBackgroundValue(0);
		ip4.setColor(0);
		ip4.translate(0, -5);
		stack.addSlice("rotate -5", ip4);

		ImageProcessor ip5 = imp.getProcessor().duplicate();
		ip5.setBackgroundValue(0);
		ip5.setColor(0);
		ip5.translate(-5, -5);
		ip5.rotate(5);
		stack.addSlice("translate + rotate", ip5);

		ImageProcessor ip6 = imp.getProcessor().duplicate();
		ip6.setBackgroundValue(0);
		ip6.setColor(0);
		ip6.translate(-15, -0);
		ip6.rotate(5);
		stack.addSlice("rotate + translate", ip6);

		ImageProcessor ip7 = imp.getProcessor().duplicate();
		ip7.setBackgroundValue(0);
		ip7.setColor(0);
		ip7.translate(-7, -7);
		ip7.rotate(15);
		stack.addSlice("rotate + translate", ip7);

		new ImagePlus("Trivial Simulation", stack).show();

	}
}
