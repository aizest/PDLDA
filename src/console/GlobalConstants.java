package console;

import java.io.File;

public class GlobalConstants {
	public static boolean DEBUG = true;
	
	public static final String WORKING_DIRECTORY = "workspace";
	
	public static final String CONFIG_PATH = "config" + File.separator + "config.properties";
	
	public static final int EARLEST_YEAR = 2002;
	public static final int THIS_YEAR = 2015;

	public static boolean isDEBUG() {
		return DEBUG;
	}

	public static void setDEBUG(boolean dEBUG) {
		DEBUG = dEBUG;
	}

}
