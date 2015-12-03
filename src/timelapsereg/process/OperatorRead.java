package timelapsereg.process;

import java.io.File;

import ij.IJ;
import ij.ImagePlus;
import ij.io.Opener;
import timelapsereg.gui.components.ProcessProgressBar;

public class OperatorRead implements Runnable {

	private ProcessProgressBar	progress;
	private Thread				thread	= null;
	private Data				data;
	private int					maxNbFrame;
	private String				pattern;

	public OperatorRead(ProcessProgressBar progress, Data data, int maxNbFrame, String pattern) {
		this.progress = progress;
		this.data = data;
		this.maxNbFrame = maxNbFrame;
		this.pattern = pattern.toLowerCase();
		if (thread == null) {
			thread = new Thread(this);
			thread.setPriority(Thread.MIN_PRIORITY);
			thread.start();
		}
	}

	@Override
	public void run() {
		data.frames.clear();
		File dir = new File(data.pathSource);
		if (!dir.isDirectory()) {
			IJ.error(data.pathSource + "is not a directory");
			progress.progress("Invalid source directory", 1);
			return;
		}
		File[] files = dir.listFiles();
		progress.reset("Read all files");
		Opener opener = new Opener();
		int n = files.length;
		int count = 0;

		for (File file : files) {
			if (file.getName().toLowerCase().contains(pattern)) {
				progress.progress(file.getName(), count * 100.0 / n);
				ImagePlus imp = opener.openImage(file.getAbsolutePath());
				if (imp != null) {
					Frame frame = new Frame(data.frames.size() + 1, data.framerate, file.getAbsolutePath(), file.getName(), imp);
					data.frames.add(frame);
					count++;
					if (count >= maxNbFrame)
						break;
				}
			}
		}
		progress.progress("" + data.frames.size() + " frames", 100);

		if (data.frames.size() > 0)
			data.showFramesAsTable();
		thread = null;
	}
}
