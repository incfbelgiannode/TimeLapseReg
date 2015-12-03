package timelapsereg.process;

import ij.IJ;
import ij.ImagePlus;
import ij.io.Opener;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import timelapsereg.TransformationTool;
import timelapsereg.TransformationsCanvas;
import timelapsereg.gui.components.ProcessProgressBar;
import turboreg.TurboReg_;

public class OperatorRegistration implements Runnable {

	private ProcessProgressBar	progress;
	private Thread				thread	= null;
	private Data				data;

	private double				maxDisplacement;
	private String				chartAs;
	private String				drawAs;

	public OperatorRegistration(ProcessProgressBar progress, Data data, double maxDisplacement, String chartAs, String drawAs) {
		this.progress = progress;
		this.data = data;
		this.maxDisplacement = maxDisplacement;
		this.chartAs = chartAs;
		this.drawAs = drawAs;
		if (thread == null) {
			thread = new Thread(this);
			thread.setPriority(Thread.MIN_PRIORITY);
			thread.start();
		}
	}

	@Override
	public void run() {

		progress.reset("Start registration");

		String experiment = "reference" + new SimpleDateFormat("HHmmss").format(Calendar.getInstance().getTime());
		data.ref.show();
		data.ref.setTitle(experiment);

		TransformationsCanvas canvas = null;
		if (!drawAs.equals("None"))
			canvas = new TransformationsCanvas(data.ref, data, drawAs);

		TurboReg_ turboReg = new TurboReg_();
		int nx = data.ref.getWidth();
		int ny = data.ref.getHeight();
		String mark1 = " " + (nx * 0.1) + " " + (ny / 2) + " ";
		String mark2 = " " + (nx * 0.5) + " " + (ny / 2) + " ";
		String mark3 = " " + (nx * 0.9) + " " + (ny / 2) + " ";
		if (ny > nx) {
			mark1 = " " + (nx / 2) + " " + (ny * 0.1) + " ";
			mark2 = " " + (nx / 2) + " " + (ny * 0.5) + " ";
			mark3 = " " + (nx / 2) + " " + (ny * 0.9) + " ";
		}
		String dim = "" + " 0 0 " + (nx - 1) + " " + (ny - 1);
		String rigid = " -rigidBody " + mark1 + mark1 + mark2 + mark2 + mark3 + mark3;
		String ref = " -window " + experiment + dim;
		int count = 0;
		
		FloatProcessor fpRef = (FloatProcessor)data.ref.getProcessor();
		Opener opener = new Opener();
		for (Frame frame : data.frames) {
			progress.progress("Register " + Tools.format(frame.getTime()), (count++) * 100.0 / data.frames.size());
			data.scrollTable(frame);
			if (frame.isValid()) {
				ImageProcessor fimp = opener.openImage(frame.getPath()).getProcessor();

				String target = " -file \"" + frame.getPath() + "\"" + dim;
				String options = "-align " + ref + target + rigid + "-hideOutput";
				turboReg.run(options);
				double spts[][] = turboReg.getSourcePoints();
				double tpts[][] = turboReg.getTargetPoints();
				
				/* back reconstruction
				IJ.log("\n " + frame.getPath() + String.format(" transformation: dx=%3.2f dy=%3.2f a=%3.2f ", dx1, dy1, angle1));
				double xpts[] = new double[3];
				double ypts[] = new double[3];
				for(int i=0; i<3; i++) {
					double x = tpts[i][0];
					double y = tpts[i][1];
					double a = angle1 * Math.PI / 180;		
					xpts[i] = Math.cos(a) * x + Math.sin(a) * y;
					ypts[i] = -Math.sin(a) * x + Math.cos(a) * y;
				}
				
				for(int i=0; i<3; i++) {
					xpts[i] = xpts[i] - dx1;
					ypts[i] = ypts[i] - dy1;
				}
				for(int i=0; i<3; i++) 
					IJ.log(String.format("(%3.2f %3.2f) > (%3.2f %3.2f) ", tpts[i][0], tpts[i][1], spts[i][0], spts[i][1]));
				*/
				
				if (checkDistance(spts, tpts, maxDisplacement)) {
					frame.setStatus(Frame.Status.OK);
					frame.getTransformation().set(spts, tpts);
					String p = "";
					for(int i=0; i<3; i++) 
						p += " " + tpts[i][0] + " " + tpts[i][1] + " " + spts[i][0] + " " + spts[i][1] + " ";
					String t = " -file \"" + frame.getPath() + "\" " + (nx) + " " + (ny) + " ";
					String options1 = "-transform " + t + " -rigidBody " + p + " -hideOutput";
					turboReg.run(options1);
					ImagePlus imp = turboReg.getTransformedImage();			
					if (imp != null) {
						if (imp.getStackSize() == 2) {
							frame.setRMSE(rmse(fpRef, fimp), rmse(fpRef, (FloatProcessor)imp.getProcessor()));
						}
					}
				}
				else {
					frame.setStatus(Frame.Status.INVALID_TRANSFORMATION);
					frame.getTransformation().init(nx, ny);
					frame.setRMSE(rmse(fpRef, fimp), -1);
				}
				
				if (canvas != null)
					canvas.repaint();
				
			}
			data.updateTable();
		}
		TransformationTool.save(data);
		progress.progress("End of " + count + " Registrations", 100);

		if (chartAs.equals("Translation and rotation"))
			TransformationTool.chart(data, 3);
		if (chartAs.equals("Only translation"))
			TransformationTool.chart(data, 1);
		if (chartAs.equals("Only rotation"))
			TransformationTool.chart(data, 2);
		thread = null;
	}

	private boolean checkDistance(double[][] source, double[][] target, double maxDisplacement) {
		for(int i=0; i<3; i++) {
			double dx = source[i][0] - target[i][0];
			double dy = source[i][1] - target[i][1];
			double d = Math.sqrt(dx * dx + dy * dy);
			if (d > maxDisplacement) {
				return false;
			}
		}
		return true;
	
	}
	private double rmse(FloatProcessor ref, ImageProcessor ip) {
		float[] r = (float[])ref.getPixels();
		int n = r.length;
		float rmse = 0f;
		if (ip instanceof ByteProcessor) {
			byte[] t = (byte[])ip.getPixels();
			for(int k=0; k<n; k++)
				rmse += (r[k]-t[k])*(r[k]-t[k]);
		
		}
		else if (ip instanceof ShortProcessor) {
			short[] t = (short[])ip.getPixels();
			for(int k=0; k<n; k++)
				rmse += (r[k]-t[k])*(r[k]-t[k]);
		}
		else {
			float[] t = (float[])ip.getPixels();
			for(int k=0; k<n; k++)
				rmse += (r[k]-t[k])*(r[k]-t[k]);
		}
		return Math.sqrt(rmse / n);
	}
}
