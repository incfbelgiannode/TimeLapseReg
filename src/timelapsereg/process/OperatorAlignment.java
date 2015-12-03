package timelapsereg.process;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.io.FileInfo;
import ij.io.FileSaver;
import ij.io.Opener;

import java.io.File;
import java.util.ArrayList;

import timelapsereg.Transformation;
import timelapsereg.TransformationTool;
import timelapsereg.gui.components.ProcessProgressBar;
import turboreg.TurboReg_;

public class OperatorAlignment implements Runnable {

	private ProcessProgressBar	progress;
	private Thread				thread	= null;
	private String				transformationFile;
	private String				pathSource;
	private String				pathAligned;

	public OperatorAlignment(ProcessProgressBar progress, String pathSource, String pathAligned, String transformationFile) {
		this.progress = progress;
		this.pathSource = pathSource;
		this.pathAligned = pathAligned;
		this.transformationFile = transformationFile;
		File dir = new File(pathSource);
		if (dir.exists()) {
			if (thread == null) {
				thread = new Thread(this);
				thread.setPriority(Thread.MIN_PRIORITY);
				thread.start();
			}
		}
		else {
			progress.progress("Error: this source does not exist.", 100);
		}
	}

	@Override
	public void run() {
		progress.reset("Start alignment");

		String[] names = new File(pathSource).list();
		TurboReg_ turboReg = new TurboReg_();
		int count = 0;
		ArrayList<Transformation> transformations = TransformationTool.load(transformationFile);
		IJ.log("Debug " + transformations.size());
		IJ.log("Debug " + names.length);
		
		// sahdev code
		System.out.println(pathSource+ File.separator+ names[0]);
		ImageStack stack = null;
		String name_0 = pathSource+ File.separator+ names[0];
		if(name_0!=null)
		{
			ImagePlus ip = new ImagePlus(name_0);
			FileInfo infoF = ip.getFileInfo();
			int width = infoF.width;
			int height = infoF.height;
			stack = new ImageStack(width,height);
		
		}
		//
		
		for (int i = 0; i < Math.min(transformations.size(), names.length); i++) {
			Transformation t = transformations.get(i);
			progress.progress("Align " + names[i], (count++) * 100.0 / names.length);
			String path = pathSource + File.separator + names[i];
			IJ.log("Debug " + path);
			ImagePlus imp = new Opener().openImage(path);
			if (imp != null) {
				String p = "";
				for (int j = 0; j < 3; j++)
					p += " " + t.target[j].x + " " + t.target[j].y + " " + t.source[j].x + " " + t.source[j].y + " ";
				String target = " -file \"" + path + "\" " + (imp.getWidth()) + " " + (imp.getHeight()) + " ";
				String options1 = "-transform " + target + " -rigidBody " + p + " -hideOutput";
				turboReg.run(options1);
				ImagePlus impa = turboReg.getTransformedImage();		
				if (impa != null)
				{
					stack.addSlice("image",impa.getProcessor());
					if (impa.getStackSize() == 2) {
						FileSaver saver = new FileSaver(new ImagePlus(imp.getTitle(), impa.getStack().getProcessor(1)));
						saver.saveAsTiff(pathAligned + File.separator + names[i]);
					}
				}
			}
		}
		// now we display the aligned stack
		new ImagePlus("Aligned Stacks", stack).show();
		
		progress.progress("End of " + count + " Registrations", 100);
		thread = null;
	}

}
