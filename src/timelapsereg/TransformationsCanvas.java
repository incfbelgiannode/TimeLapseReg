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

	public TransformationsCanvas(ImagePlus imp, Data data) {
		super(imp);
		this.data = data;
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

		int hx = imp.getWidth() / 2;
		int hy = imp.getWidth() / 2;
		for (int i = 1; i < data.frames.size(); i++) {
			Transformation p = data.frames.get(i - 1).getTransformation();
			Transformation t = data.frames.get(i).getTransformation();
			bufferGraphics.setColor(new Color(200, 200, 10, 100));
			bufferGraphics.setStroke(new BasicStroke(4));
			bufferGraphics.drawLine(screenXD(p.dx + hx), screenYD(p.dy + hy), screenXD(t.dx + hx), screenYD(t.dy + hy));
		}
		g.drawImage(offscreen, 0, 0, this);
	}
}
