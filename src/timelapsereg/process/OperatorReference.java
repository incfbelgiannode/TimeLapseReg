package timelapsereg.process;

import java.io.File;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.io.FileSaver;
import ij.io.Opener;
import ij.plugin.ImageCalculator;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;
import timelapsereg.gui.components.ProcessProgressBar;
import timelapsereg.gui.ErrorDialog;

public class OperatorReference {
	
	public OperatorReference(ProcessProgressBar progress, Data data, String mode, String param1, String param2) {

		data.ref = null;
		progress.reset("Reference " + mode);
		String path = data.pathSource;
		
		if (mode.equals("position")) {
			int position = Integer.parseInt(param1);
			Opener opener = new Opener();
			File dir = new File(path);
			if (dir.isDirectory()) {
				File[] files = dir.listFiles();
				int count = 0;
				for (int i = 0; i < files.length; i++) {
					data.ref = opener.openImage(path + File.separator + files[i].getName());
					if (data.ref != null) {
						count++;
						if (count == position) {
							int s = ImageStatistics.MEAN | ImageStatistics.STD_DEV;
							ImageProcessor ip = data.ref.getProcessor();
							ImageStatistics stats = ImageStatistics.getStatistics(ip, s, null);
							data.mean = stats.mean;
							data.stdev = stats.stdDev;
							progress.progress("Ref: " + files[i].getName(), 100);
							return;
						}
					}
				}
				return;
			}
		}
		else if (mode.equals("window")) {
			
			ImagePlus imp = WindowManager.getImage(param2);
			
			if (imp != null) {
				data.ref = imp;
				int s = ImageStatistics.MEAN | ImageStatistics.STD_DEV;
				ImageProcessor ip = data.ref.getProcessor();
				ImageStatistics stats = ImageStatistics.getStatistics(ip, s, null);
				data.mean = stats.mean;
				data.stdev = stats.stdDev;
				System.out.println("Successfully read!!");
				imp.hide();
				//imp.show();
				return;
			}
			else
			{
				//new ErrorDialog();
				System.out.println("Enter valid name for the image window");
			}
		}
		else if (mode.equals("file")) {
			ImagePlus imp = new Opener().openImage(path + File.separator + param2);
			System.out.println(path + File.separator + param2 +" :::: File PATH");
			if (imp != null) {
				data.ref = imp;
				int s = ImageStatistics.MEAN | ImageStatistics.STD_DEV;
				ImageProcessor ip = data.ref.getProcessor();
				ImageStatistics stats = ImageStatistics.getStatistics(ip, s, null);
				data.mean = stats.mean;
				data.stdev = stats.stdDev;
				//imp.show();
			}
		}
		// following conditions illustrates a concept which has been replicated for computing the reference image over a specified range of frames also
		else if(mode.equals("percentage"))
		{
			ImageCalculator calc = new ImageCalculator();
			
			File dir = new File(path);
			System.out.println(path);
			String filenames[] = dir.list();
			int percentage_length = (int) (Integer.parseInt(param2) *0.01 * filenames.length) ;
			System.out.println(percentage_length+" Total files used to compute the average");
			for(int i=0 ; i<percentage_length ; i++)				
					filenames[i] = (path+File.separator+filenames[i]);
			System.out.println(filenames[0]+"::: "+filenames.length);
			
			if(param1.equalsIgnoreCase("Average"))
			{
				ImagePlus i0 = new ImagePlus(filenames[0]);
				for(int i=0; i<percentage_length ; i++)
				{
					ImagePlus i1 = new ImagePlus(filenames[i]);
					i0 = calc.run("Average create 32-bit", i0, i1);
				}
				i0.setTitle("reference image");
				//i0.show();
				data.ref = i0;
				int s = ImageStatistics.MEAN | ImageStatistics.STD_DEV;
				ImageProcessor ip = data.ref.getProcessor();
				ImageStatistics stats = ImageStatistics.getStatistics(ip, s, null);
				data.mean = stats.mean;
				data.stdev = stats.stdDev;
				//data.ref.show();
				
				System.out.println(param1+"   "+param2);
			}
			else if(param1.equalsIgnoreCase("Minimum"))
			{
				ImagePlus i0 = new ImagePlus(filenames[0]);
				for(int i=0; i<percentage_length ; i++)
				{
					ImagePlus i1 = new ImagePlus(filenames[i]);
					i0 = calc.run("Min create 32-bit", i0, i1);
				}
				i0.setTitle("reference image");
				//i0.show();
				data.ref = i0;
				int s = ImageStatistics.MEAN | ImageStatistics.STD_DEV;
				ImageProcessor ip = data.ref.getProcessor();
				ImageStatistics stats = ImageStatistics.getStatistics(ip, s, null);
				data.mean = stats.mean;
				data.stdev = stats.stdDev;
				//data.ref.show();
				
				System.out.println(param1+"   "+param2);
			}
			else if(param1.equalsIgnoreCase("Maximum"))
			{
				System.out.println("Currently Maximum is selected");
				ImagePlus i0 = new ImagePlus(filenames[0]);
				System.out.println(filenames);
				for(int i=0; i<percentage_length ; i++)
				{
					ImagePlus i1 = new ImagePlus(filenames[i]);
					i0 = calc.run("Max create 32-bit", i0, i1);
				}
				i0.setTitle("reference image");
				//i0.show();
				data.ref = i0;
				int s = ImageStatistics.MEAN | ImageStatistics.STD_DEV;
				ImageProcessor ip = data.ref.getProcessor();
				ImageStatistics stats = ImageStatistics.getStatistics(ip, s, null);
				data.mean = stats.mean;
				data.stdev = stats.stdDev;
				//data.ref.show();
				
				System.out.println(param1+"   "+param2);
			}
			
		}
			
		else {
			IJ.error("NOT YET IMPLEMENTED");
		}

	}
	public OperatorReference(ProcessProgressBar progress, Data data, String mode, String param1, String param2, String param3)
	{

		data.ref = null;
		progress.reset("Reference " + mode);
		String path = data.pathSource;
		//the following code for the else if conditions has been mostly replicated from the previous else if condition for the "percentage" case
		if(mode.equals("range"))
		{
			ImageCalculator calc = new ImageCalculator();
			
			File dir = new File(path);
			System.out.println(path);
			String filenames[] = dir.list();
			int lower_limit = Integer.parseInt(param2);
			int upper_limit = Integer.parseInt(param3);
			
			if(upper_limit > filenames.length)
			{
				IJ.log("Enter upper limit lesser than the number of files");
				return;
			}
			int num_files = upper_limit - lower_limit;
			for(int i=0 ; i<num_files ; i++)				
					filenames[i] = (path+File.separator+filenames[i]);
			System.out.println(filenames[0]+"::: "+filenames.length);
			
			if(param1.equalsIgnoreCase("Average"))
			{
				ImagePlus i0 = new ImagePlus(filenames[0]);
				for(int i=0; i<num_files ; i++)
				{
					ImagePlus i1 = new ImagePlus(filenames[i]);
					i0 = calc.run("Average create 32-bit", i0, i1);
				}
				i0.setTitle("reference image");
				//i0.show();
				data.ref = i0;
				int s = ImageStatistics.MEAN | ImageStatistics.STD_DEV;
				ImageProcessor ip = data.ref.getProcessor();
				ImageStatistics stats = ImageStatistics.getStatistics(ip, s, null);
				data.mean = stats.mean;
				data.stdev = stats.stdDev;
				//data.ref.show();
				
				System.out.println(param1+"   "+param2+"   "+param3);
			}
			else if(param1.equalsIgnoreCase("Minimum"))
			{
				ImagePlus i0 = new ImagePlus(filenames[0]);
				for(int i=0; i<num_files ; i++)
				{
					ImagePlus i1 = new ImagePlus(filenames[i]);
					i0 = calc.run("Min create 32-bit", i0, i1);
				}
				i0.setTitle("reference image");
				//i0.show();
				data.ref = i0;
				int s = ImageStatistics.MEAN | ImageStatistics.STD_DEV;
				ImageProcessor ip = data.ref.getProcessor();
				ImageStatistics stats = ImageStatistics.getStatistics(ip, s, null);
				data.mean = stats.mean;
				data.stdev = stats.stdDev;
				//data.ref.show();
				
				System.out.println(param1+"   "+param2+"  "+param3);
			}
			else if(param1.equalsIgnoreCase("Maximum"))
			{
				ImagePlus i0 = new ImagePlus(filenames[0]);
				for(int i=0; i<num_files ; i++)
				{
					ImagePlus i1 = new ImagePlus(filenames[i]);
					i0 = calc.run("Max create 32-bit", i0, i1);
				}
				i0.setTitle("reference image");
				//i0.show();
				data.ref = i0;
				int s = ImageStatistics.MEAN | ImageStatistics.STD_DEV;
				ImageProcessor ip = data.ref.getProcessor();
				ImageStatistics stats = ImageStatistics.getStatistics(ip, s, null);
				data.mean = stats.mean;
				data.stdev = stats.stdDev;
				//data.ref.show();
				
				System.out.println(param1+"   "+param2+"  "+param3);
			}
			else
			{
				IJ.log("error in selection, not implemented currently");
			}
			
		}
	
	}

}
