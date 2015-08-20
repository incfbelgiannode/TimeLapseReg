package timelapsereg.process;

public class ReferenceImageConstant {
	
	public static String modes[]	= { 
			"position", 
			"window", 
			"file", 
			"percentage", 
			"range", 
			//"sliding",    removed for the first version implementation
			//"block"       removed for the first version implementation
		};
	public static String modesGUI[]	= { 
			"Specified by a position", 
			"Specified by an opened ImageJ window", 
			"Specified by file into the project directory",
			"Computed over a percentage of frames", 
			"Computed over a specified range of frames", 
			//"Computed over a sliding range of frames", 	removed for the first version implementation
			//"Computed over blocks of frames" 				removed for the first version implementation
		};
	
}
