package timelapsereg;

import ij.IJ;

import java.awt.geom.Point2D;

import timelapsereg.process.Tools;

public class Transformation {

	public double	dx		= 0.0;
	public double	dy		= 0.0;
	public double	angle	= 0.0;

	public Point2D.Double source[];
	public Point2D.Double target[];

	public Transformation(int nx, int ny) {
		this.source = new Point2D.Double[3];
		this.target = new Point2D.Double[3];
		init(nx, ny);
		computeTranslationAndRotation();
	}
	
	public void init(int nx, int ny) {
		source[0] = new Point2D.Double(0, ny/2);
		source[1] = new Point2D.Double(nx/2, ny/2);
		source[2] = new Point2D.Double(nx-1, ny/2);
		target[0] = new Point2D.Double(0, ny/2);
		target[1] = new Point2D.Double(nx/2, ny/2);
		target[2] = new Point2D.Double(nx-1, ny/2);	
	}
	
	public void set(double spts[][], double tpts[][]) {
		for(int i=0; i<3; i++)
			source[i] = new Point2D.Double(spts[i][0], spts[i][1]);
		for(int i=0; i<3; i++)
			target[i] = new Point2D.Double(tpts[i][0], tpts[i][1]);
		computeTranslationAndRotation();
	}
	
	private void computeTranslationAndRotation() {
		double sangle = Math.atan2(source[1].y - source[0].y, source[1].x - source[0].x);
		double tangle = Math.atan2(target[1].y - target[0].y, target[1].x - target[0].x);
		dx = (target[1].x - source[1].x);
		dy = (target[1].y - source[1].y);
		angle = (180.0 * (tangle - sangle) / Math.PI);
	}

	@Override
	public String toString() {
		return Tools.format(dx) + ", " + Tools.format(dy) + ", " + Tools.format(angle);
 	}
}
