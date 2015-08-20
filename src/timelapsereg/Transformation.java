package timelapsereg;

import timelapsereg.process.Tools;

public class Transformation {

	public double	dx			= 0.0;
	public double	dy			= 0.0;
	public double	angle		= 0.0;

	public double	beforeRMSE	= 0.0;
	public double	afterRMSE	= 0.0;

	public void setTransformation(double dx, double dy, double angle) {
		this.dx = dx;
		this.dy = dy;
		this.angle = angle;
	}

	@Override
	public String toString() {
		return Tools.format(dx) + ", " + Tools.format(dy) + ", " + Tools.format(angle);
	}
}
