package timelapsereg;

import ij.IJ;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import timelapsereg.gui.TableFrames;
import timelapsereg.process.Data;
import timelapsereg.process.Frame;

public class TransformationTool {
	
	public static void save(Data data) {
		File file = new File(data.pathOutput);
		try {
			String headers[] = TableFrames.headers;
			BufferedWriter buffer = new BufferedWriter(new FileWriter(file));
			String s = "";
			for (int i = 0; i < headers.length; i++)
				s += headers[i] + ",";
			buffer.write(s + "\r\n");
			for (Frame frame : data.frames)
				buffer.write(frame.toCVS()+ "\r\n");
			
			buffer.close();
		}
		catch (IOException ex) {
			System.out.println("" + ex);
		}	
	}
	
	public static ArrayList<Transformation> load(String filename) {
		ArrayList<Transformation> transformations = new ArrayList<Transformation>();
		String line = "";
		
		try {
			BufferedReader buffer = new BufferedReader(new FileReader(filename));
			line = buffer.readLine();
			line = buffer.readLine();
			while (line != null) {
				StringTokenizer tokens = new StringTokenizer(line, ",");
				int id = Integer.parseInt(tokens.nextToken().trim());
				double time = Double.parseDouble(tokens.nextToken().trim());
				String name = tokens.nextToken().trim();
				double mean = Double.parseDouble(tokens.nextToken().trim());
				double stdev = Double.parseDouble(tokens.nextToken().trim());
				String content = tokens.nextToken().trim();
				double dx = Double.parseDouble(tokens.nextToken().trim());
				double dy = Double.parseDouble(tokens.nextToken().trim());
				double angle = Double.parseDouble(tokens.nextToken().trim());
				double rmseBefore = Double.parseDouble(tokens.nextToken().trim());
				double rmseAfter = Double.parseDouble(tokens.nextToken().trim());

				double s1x = Double.parseDouble(tokens.nextToken().trim());
				double s1y = Double.parseDouble(tokens.nextToken().trim());
				double s2x = Double.parseDouble(tokens.nextToken().trim());
				double s2y = Double.parseDouble(tokens.nextToken().trim());
				double s3x = Double.parseDouble(tokens.nextToken().trim());
				double s3y = Double.parseDouble(tokens.nextToken().trim());

				double t1x = Double.parseDouble(tokens.nextToken().trim());
				double t1y = Double.parseDouble(tokens.nextToken().trim());
				double t2x = Double.parseDouble(tokens.nextToken().trim());
				double t2y = Double.parseDouble(tokens.nextToken().trim());
				double t3x = Double.parseDouble(tokens.nextToken().trim());
				double t3y = Double.parseDouble(tokens.nextToken().trim());
					
				double[][] spts = new double[][] {{s1x, s1y},{s2x, s2y},{s3x, s3y}};
				double[][] tpts = new double[][] {{t1x, t1y},{t2x, t2y},{t3x, t3y}};
				
				Transformation transformation = new Transformation(1000, 1000);
				transformation.set(spts, tpts);
				transformations.add(transformation);
				line = buffer.readLine();
				IJ.log(" debug " + line);
							
			}
			buffer.close();
		}
		catch (Exception ex) {
			System.out.println("Unable to read the file " + line);
		}
		return transformations;
		
	}
	
	
	public static void chart(Data data, int kind) {
		XYSeriesCollection dataset = new XYSeriesCollection();
		
		XYSeries dx = new XYSeries("DX");
		XYSeries dy = new XYSeries("DY");
		XYSeries da = new XYSeries("Angle");
		
		for(Frame frame : data.frames) {
			Transformation t = frame.getTransformation();
			if (kind != 2) dx.add(frame.getTime(), t.dx);
			if (kind != 2) dy.add(frame.getTime(), t.dy);
			if (kind != 1) da.add(frame.getTime(), t.angle);
		}
			
		if (kind != 2) dataset.addSeries(dx);
		if (kind != 2) dataset.addSeries(dy);
		if (kind != 1) dataset.addSeries(da);
		
		JFreeChart chart = ChartFactory.createXYLineChart("Chart", "Time (ms)", "Transform", dataset, PlotOrientation.VERTICAL, true, false, false);
		XYPlot plot = chart.getXYPlot();

		
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		for (int i = 0; i < dataset.getSeriesCount(); i++) {
			renderer.setSeriesStroke(i, new BasicStroke(3f));
			renderer.setSeriesLinesVisible(i, true);
			renderer.setSeriesShapesVisible(i, true);
		}
		plot.setRenderer(renderer);

		plot.setOutlinePaint(Color.black);
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(800, 500));
		JFrame frame = new JFrame("Test chart");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.getContentPane().add(chartPanel, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);

	}

}
