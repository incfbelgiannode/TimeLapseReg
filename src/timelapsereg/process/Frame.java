package timelapsereg.process;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;
import timelapsereg.Transformation;

public class Frame {

	public enum Status {
		NONE, OK, INVALID_SIZE, INVALID_MEAN, INVALID_STDEV, INVALID_TRANSFORMATION
	};

	public int		number	= 0;
	private double	time	= 0;
	private String	path;
	private String	filename;

	private double	mean	= 0;
	private double	stdev	= 0;
	private int		nx		= 0;
	private int		ny		= 0;
	private Status	status	= Status.NONE;

	private Transformation transformation = new Transformation();

	public Frame(int number, double framerate, String path, String filename, ImagePlus imp) {
		this.number = number;
		this.time = (number) / framerate * 1000;
		this.path = path;
		this.filename = filename;
		int s = ImageStatistics.MEAN | ImageStatistics.STD_DEV;
		ImageProcessor ip = imp.getProcessor();
		nx = ip.getWidth();
		ny = ip.getHeight();
		ImageStatistics stats = ImageStatistics.getStatistics(ip, s, null);
		this.mean = stats.mean;
		this.stdev = stats.stdDev;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public void setTransformation(double dx, double dy, double angle) {
		transformation.setTransformation(dx, dy, angle);
	}

	public double getTime() {
		return time;
	}

	public Transformation getTransformation() {
		return transformation;
	}

	public String toCVS() {
		return filename + ", " + isValid() + ", " + transformation.dx + ", " + transformation.dy + ", " + transformation.angle + ", " + transformation.beforeRMSE + ", " + transformation.afterRMSE
				+ "\n";
	}

	public String getFilename() {
		return filename;
	}

	public String getPath() {
		return path;
	}

	public boolean validate(int nx, int ny, double minMean, double maxMean, double minStdev, double maxStdev) {
		if (nx != this.nx || ny != this.ny) {
			status = Status.INVALID_SIZE;
			return false;
		}
		if (mean > maxMean || mean < minMean) {
			status = Status.INVALID_MEAN;
			return false;
		}

		if (stdev > maxStdev || stdev < minStdev) {
			status = Status.INVALID_STDEV;
			return false;
		}
		status = Status.OK;
		return true;
	}

	@Override
	public String toString() {
		return filename + " " + status.toString();
	}

	public boolean isValid() {
		return status == Status.OK;
	}

	public String[] getInformation() {
		String s = "";
		if (status == Status.NONE)
			s = "-";
		else if (status == Status.OK)
			s = "OK";
		else if (status == Status.INVALID_MEAN)
			s = "Invalid mean";
		else if (status == Status.INVALID_SIZE)
			s = "Invalid size";
		else if (status == Status.INVALID_STDEV)
			s = "Invalid stdev";
		else if (status == Status.INVALID_TRANSFORMATION)
			s = "Invalid transformation";
		return new String[] { "" + number, Tools.format(time), filename, Tools.format(mean), Tools.format(stdev), s, transformation.toString() };
	}
}
