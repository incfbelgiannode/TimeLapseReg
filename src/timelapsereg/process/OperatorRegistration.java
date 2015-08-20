package timelapsereg.process;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import ij.IJ;
import timelapsereg.TransformationTool;
import timelapsereg.TransformationsCanvas;
import timelapsereg.gui.components.ProcessProgressBar;
import turboreg.TurboReg_;

public class OperatorRegistration implements Runnable {

	private ProcessProgressBar	progress;
	private Thread				thread	= null;
	private Data				data;

	private double	maxTranslation;
	private double	maxRotation;

	public OperatorRegistration(ProcessProgressBar progress, Data data, double maxTranslation, double maxRotation) {
		this.progress = progress;
		this.data = data;
		this.maxTranslation = maxTranslation;
		this.maxRotation = maxRotation;
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

		TransformationsCanvas canvas = new TransformationsCanvas(data.ref, data);

		TurboReg_ reg = new TurboReg_();
		int nx = data.ref.getWidth();
		int ny = data.ref.getHeight();
		String mark1 = "0 " + nx / 2 + " ";
		String mark2 = nx / 2 + " " + ny / 2 + " ";
		String mark3 = nx / 2 + " " + ny + " ";

		String dim = "" + " 0 0 " + (nx - 1) + " " + (ny - 1);
		String rigid = " -rigidBody " + mark1 + mark1 + mark2 + mark2 + mark3 + mark3;
		String ref = " -window " + experiment + dim;
		int count = 0;
		for (Frame frame : data.frames) {
			progress.progress("Register " + Tools.format(frame.getTime()), (count++)*100.0/data.frames.size());
			data.scrollTable(frame);
			if (frame.isValid()) {
				String target = " -file \"" + frame.getPath() + "\"" + dim;
				String options = "-align " + ref + target + rigid + "-hideOutput";
				reg.run(options);
				double spts[][] = reg.getSourcePoints();
				double tpts[][] = reg.getTargetPoints();

				double sangle = Math.atan2(spts[2][1] - spts[1][1], spts[2][0] - spts[1][0]);
				double tangle = Math.atan2(tpts[2][1] - tpts[1][1], tpts[2][0] - tpts[1][0]);
				double dx = (tpts[0][0] - spts[0][0]);
				double dy = (tpts[0][1] - spts[0][1]);
				double angle = (180.0 * (tangle - sangle) / Math.PI);
				double d = Math.sqrt(dx*dx+dy*dy);
				if (d > maxTranslation || angle > maxRotation) {
					frame.setStatus(Frame.Status.INVALID_TRANSFORMATION);
					frame.setTransformation(0, 0, 0);
				}
				else {
					frame.setTransformation(dx, dy, angle);
				}
				canvas.repaint();
			}
			/*
			 * ImagePlus imp2 = reg.getTransformedImage();
			 * stack.addSlice("image", imp2.getProcessor());
			 * 
			 * progress.progress("Registration",
			 * (count++)*100.0/data.frames.size());
			 * 
			 * ImagePlus imp = opener.openImage(frame.getFilename()); if (imp !=
			 * null) { if (!ready) { canvas = new TransformationsCanvas(imp,
			 * transformations); ready = true; } else { double dx =
			 * x_coordinate; double dy = y_coordinate; double angle =
			 * angle_coordinate; Transformation t = new Transformation(count,
			 * dx, dy, angle); transformations.add(t); if (canvas != null)
			 * canvas.repaint(); } }
			 */
		}
		TransformationTool.save(data);
		TransformationTool.chart(data);
		thread = null;
	}

}
