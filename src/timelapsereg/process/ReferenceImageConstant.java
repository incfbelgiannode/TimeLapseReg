package timelapsereg.process;

public class ReferenceImageConstant {
	
	public static String modes[]	= { 
			"position", 
			"window", 
			"file", 
			"percentage", 
			"range", 
			// following 2 have been removed for the first version of the pluign
			//"sliding",  
			//"block"
		};
	public static String modesGUI[]	= { 
			"Specified by a position", 
			"Specified by an opened ImageJ window", 
			"Specified by file into the project directory",
			"Computed over a percentage of frames", 
			"Computed over a specified range of frames", 
			// following 2 have been removed for the first version of the pluign
			//"Computed over a sliding range of frames", 
			//"Computed over blocks of frames"
		};
	
}
