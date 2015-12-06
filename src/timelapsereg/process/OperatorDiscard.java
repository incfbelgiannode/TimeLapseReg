package timelapsereg.process;

import timelapsereg.gui.components.ProcessProgressBar;

public class OperatorDiscard {

	private String status = "not yet run";
	public OperatorDiscard(ProcessProgressBar progress, Data data, double varMean, double varStdev) {
		double m = data.getReferenceMean();
		double s = data.getReferenceStdev();
		double mv = varMean / 100;
		double ms = varStdev / 100;
		double minMean = m * (1.0 - mv);
		double maxMean = m * (1.0 + mv);
		double minStdev = s * (1.0 - ms);
		double maxStdev = s * (1.0 + ms);
		
		int nx = data.ref.getWidth();
		int ny = data.ref.getHeight();
		progress.reset("Validate");
		
		int count = 0;
		if (data != null) {
			if (data.frames != null) {
				for(Frame frame : data.frames) {
					if (frame.validate(nx, ny, minMean, maxMean, minStdev, maxStdev))
						count++;
				}
			}
		}
		status = "" + count + " validated frames / " + data.frames.size() + " files";
		progress.progress(status, 100);
	}
	
	public String getStatus() {
		return status; 
	}
}
