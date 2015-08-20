package timelapsereg;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import timelapsereg.process.Data;
import timelapsereg.process.Frame;


public class TransformationTool {
	public static File file;
	public static void save(Data data) {
		file = new File(data.pathOutput + File.separator + "transformations.csv");
		try {
			String headers[] = new String[] {"Filename", "DX", "DY", "Angle", "RMSE-before", "RMSE-after"};
			BufferedWriter buffer = new BufferedWriter(new FileWriter(file));
			String s = "";
			for (int i = 0; i < headers.length; i++)
				s += headers[i] + ",";
			buffer.write(s + "\n");
			for (Frame frame : data.frames) {
				buffer.write(frame.toCVS());
			}
			buffer.close();
		}
		catch (IOException ex) {
			System.out.println("" + ex);
		}	
	}
	
	public static void chart(Data data) {
		XYSeriesCollection dataset = new XYSeriesCollection();
		
		XYSeries dx = new XYSeries("DX");
		XYSeries dy = new XYSeries("DY");
		XYSeries da = new XYSeries("Angle");
		
		for(Frame frame : data.frames) {
			Transformation t = frame.getTransformation();
			dx.add(frame.getTime(), t.dx);
			dy.add(frame.getTime(), t.dy);
			da.add(frame.getTime(), t.angle);
		}
			
		dataset.addSeries(dx);
		dataset.addSeries(dy);
		dataset.addSeries(da);
		
		JFreeChart chart = ChartFactory.createXYLineChart("Chart", "Time (ms)", "Transform", dataset, PlotOrientation.VERTICAL, true, false, false);
		XYPlot plot = chart.getXYPlot();

		
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		for (int i = 0; i < 3; i++) {
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
