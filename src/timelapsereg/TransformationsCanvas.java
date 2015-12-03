//====================================================================================================
// Project: SpotCaliper
// 
// Authors: Zsuzsanna Puspoki and Daniel Sage
// Organization: Biomedical Imaging Group (BIG), Ecole Polytechnique Federale de Lausanne
// Address: EPFL-STI-IMT-LIB, 1015 Lausanne, Switzerland
//
// Information: http://bigwww.epfl.ch/algorithms/spotcaliper/
//
// References:
// Zs. Puspoki et al.
// SpotCaliper 
// Bioinformatics Oxford, submitted in June 2015..
// Available at: http://bigwww.epfl.ch/publications/
//
// Conditions of use:
// You'll be free to use this software for research purposes, but you should not redistribute 
// it without our consent. In addition, we expect you to include a citation whenever you 
// present or publish results that are based on it.
//====================================================================================================
package timelapsereg;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import ij.ImagePlus;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.gui.StackWindow;
import timelapsereg.process.Data;

public class TransformationsCanvas extends ImageCanvas {

	private ImageCanvas	canvasOriginal;
	private Data		data;

	// For the double buffering
	private Dimension	dim;
	private Image		offscreen;
	private Graphics2D	bufferGraphics;
	private Color 		color = new Color(200, 200, 0);
	
	public TransformationsCanvas(ImagePlus imp, Data data, String color) {
		super(imp);
		this.data = data;
		if (color.equals("Red"))
			this.color = new Color(255, 0, 0);
		if (color.equals("Black"))
			this.color = new Color(0, 0, 0);
		if (color.equals("White"))
			this.color = new Color(255, 255, 255);
		canvasOriginal = imp.getCanvas();
		if (imp.getStackSize() > 1)
			imp.setWindow(new StackWindow(imp, this));
		else
			imp.setWindow(new ImageWindow(imp, this));
		resetBuffer();
	}

	public void reset() {
		if (canvasOriginal != null)
			if (imp != null)
				if (!imp.isVisible())
					imp.setWindow(new ImageWindow(imp, canvasOriginal));
	}

	private void resetBuffer() {
		if (bufferGraphics != null) {
			bufferGraphics.dispose();
			bufferGraphics = null;
		}
		if (offscreen != null) {
			offscreen.flush();
			offscreen = null;
		}
		dim = getSize();
		offscreen = createImage(dim.width, dim.height);
		bufferGraphics = (Graphics2D) offscreen.getGraphics();
	}

	@Override
	public void paint(Graphics g) {

		if (dim.width != getSize().width || dim.height != getSize().height || bufferGraphics == null || offscreen == null)
			resetBuffer();

		super.paint(bufferGraphics);

		if (data == null)
			return;

		if (data.frames.size() < 2)
			return;

		for (int i = 0; i < data.frames.size(); i++) {
			Transformation t = data.frames.get(i).getTransformation();
			bufferGraphics.setColor(color);
			bufferGraphics.setStroke(new BasicStroke(2));
			if (i > 0) {
				Transformation p = data.frames.get(i - 1).getTransformation();
				bufferGraphics.drawLine(screenXD(t.source[1].x), screenYD(t.source[1].y), screenXD(p.source[1].x), screenYD(p.source[1].y));
			}
			bufferGraphics.setColor(new Color(200, 200, 200, 100));
			bufferGraphics.setStroke(new BasicStroke(1));
			bufferGraphics.drawLine(screenXD(t.source[0].x), screenYD(t.source[0].y), screenXD(t.source[2].x), screenYD(t.source[2].y));
		}
		g.drawImage(offscreen, 0, 0, this);
	}
}
