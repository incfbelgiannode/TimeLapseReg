import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.gui.Line;
import ij.gui.Overlay;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;


public class Drift_Simulation implements PlugIn {

	@Override
	public void run(String event) {
		ImagePlus imp = WindowManager.getCurrentImage();
		if (imp == null) {
			IJ.error("No open image.");
			return;
		}
		int nt = imp.getStackSize();
		int mt = (nt == 1 ? 100 : nt);
		GenericDialog dlg = new GenericDialog("Drift Simulation");
		dlg.addNumericField("Slow Drift - Translation amplitude", 0.2, 2);
		dlg.addNumericField("Slow Drift - Rotation amplitude", 0.05, 2);
		dlg.addNumericField("Number of shake event", 10, 0);
		dlg.addNumericField("Shake Drift - Translation amplitude", 10, 2);
		dlg.addNumericField("Shake Drift - Rotation amplitude", 1, 2);
		dlg.addNumericField("Number of frame of the output sequence", mt, 0);
		dlg.showDialog();
		
		if(dlg.wasCanceled())
			return;
		
		double slow[] = new double[] {dlg.getNextNumber(), dlg.getNextNumber()};
		double shake[] = new double[] {dlg.getNextNumber(), dlg.getNextNumber()};
		int nbShake = (int)dlg.getNextNumber();
		mt = (int)dlg.getNextNumber();
	
		drift(imp, mt, nbShake, slow, shake);
	}

	private void drift(ImagePlus imp, int nt, int nbShake, double[] slow, double shake[]) {
		
		double xdrift[] = new double[nt];
		double ydrift[] = new double[nt];
		double adrift[] = new double[nt];
		double dx = slow[0];
		double dy = slow[0];
		double da = slow[1];
		for(int t=1; t<nt; t++) {
			dx = dx + (Math.random()-0.5)*slow[0];
			dy = dy + (Math.random()-0.5)*slow[0];
			da = da + (Math.random()-0.5)*slow[1];
			xdrift[t] = xdrift[t-1] + dx;
			ydrift[t] = ydrift[t-1] + dy;
			adrift[t] = adrift[t-1] + da;
		}
		
		for(int e=0; e<nbShake; e++) {
			int event = (int)Math.min(nt-1, Math.max(0, Math.random()*nt));
			IJ.log("Event at frame:" + event);
			dx = (Math.random()-0.5)*shake[0];
			dx = (Math.random()-0.5)*shake[0];
			da = (Math.random()-0.5)*shake[1];
			for(int t=event; t<nt; t++) {
				xdrift[t] += dx;
				ydrift[t] += dy;
				adrift[t] += da;
			}
		}

		Overlay overlay = new Overlay();
		int xc = imp.getWidth() / 2;
		int yc = imp.getHeight() / 2;
		for(int t=1; t<nt; t++)  {
			IJ.log("" + t + " " + xdrift[t] + " " + ydrift[t] + " " + adrift[t]);
			Line line = new Line(xc+(int)Math.round(xdrift[t-1]), 
					yc+(int)Math.round(ydrift[t-1]), xc+(int)Math.round(xdrift[t]), 
					yc+(int)Math.round(ydrift[t]));
			overlay.add(line);
			
		}
		imp.setOverlay(overlay);
		
		int ns = imp.getStackSize();
		ImageStack stack = new ImageStack(imp.getWidth(), imp.getHeight());
		for(int t=0; t<nt; t++) {
			int s = (t >= ns ? ns-1 : t);
			ImageProcessor ip = imp.getStack().getProcessor(s+1).duplicate();
			ip.rotate(adrift[t]);
			ip.translate(xdrift[t], ydrift[t]);
			stack.addSlice("" + adrift[t] , ip);
		}
		new ImagePlus("Drift of " + imp.getTitle(), stack).show();
	}
}
