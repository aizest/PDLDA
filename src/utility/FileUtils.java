package utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class FileUtils {
	public static Map<String, String> readKeyValueMap(String location) {
		Map<String, String> result = new ConcurrentHashMap<String, String>();
		if (location == null || location.length() <= 0) {
			System.err.println("NULL configuration file!");
			return result;
		}

		try {
			File file = new File(location);
			if(!file.exists()){
				
				System.err.println("Failed to read configuration! Please put the configuration file under the path: " + location);
				System.err.println("Wrong path: " + file.getPath());
				System.exit(0);
			}
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = br.readLine();
			while (line != null) {
				if (line != null && line.indexOf(':') > 0) {
					String[] array = line.split(":", 2);
					result.put(array[0].trim(), array[1].trim());
				}
				line = br.readLine();
			}

			br.close();
		} catch (Exception e) {
			e.printStackTrace();
			return result;
		}
		return result;
	}
	
	public static List<String> readKeyValueList(String location) {
		List<String> result = new ArrayList<String>();
		if (location == null || location.length() <= 0) {
			System.err.println("NULL configuration file!");
			return result;
		}

		try {
			File file = new File(location);
			if(!file.exists()){
				
				System.err.println("Failed to read configuration! Please put the configuration file under the path: " + location);
				System.err.println("Wrong path: " + file.getPath());
				System.exit(0);
			}
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = br.readLine();
			while (line != null) {
				if (line != null && line.indexOf(':') > 0) {
					String[] array = line.split(":", 2);
					result.add(array[1].trim());
				}
				line = br.readLine();
			}

			br.close();
		} catch (Exception e) {
			e.printStackTrace();
			return result;
		}
		return result;
	}
	
	public static List<String> readValueList(String location) {
		List<String> result = new ArrayList<String>();
		if (location == null || location.length() <= 0) {
			System.err.println("NULL configuration file!");
			return result;
		}

		try {
			File file = new File(location);
			if(!file.exists()){
				
				System.err.println("Failed to read configuration! Please put the configuration file under the path: " + location);
				System.err.println("Wrong path: " + file.getPath());
				System.exit(0);
			}
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = br.readLine();
			while (line != null) {
				line = line.trim();
				if (line.length() > 0) {
					result.add(line);
				}
				line = br.readLine();
			}

			br.close();
		} catch (Exception e) {
			e.printStackTrace();
			return result;
		}
		return result;
	}

	
	public static void readLines(String file, List<String> lines) {
		BufferedReader reader = null;

		try {

			reader = new BufferedReader(new FileReader(new File(file)));

			String line = null;
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}


	public static void writeLines(String file, ArrayList<?> counts) {
		BufferedWriter writer = null;

		try {

			writer = new BufferedWriter(new FileWriter(new File(file)));

			for (int i = 0; i < counts.size(); i++) {
				writer.write(counts.get(i) + "\n");
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}


	/**
	 * Create a directory by calling mkdir();
	 * 
	 * @param dirFile
	 */
	public static void mkdir(File dirFile) {
		try {
			// File dirFile = new File(mkdirName);
			boolean bFile = dirFile.exists();
			if (bFile == true) {
				System.err.println("The folder exists.");
			} else {
				System.err
						.println("The folder do not exist,now trying to create a one...");
				bFile = dirFile.mkdir();
				if (bFile == true) {
					System.out.println("Create successfully!");
				} else {
					System.err
							.println("Disable to make the folder,please check the disk is full or not.");
				}
			}
		} catch (Exception err) {
			System.err.println("ELS - Chart : unexpected error");
			err.printStackTrace();
		}
	}

	public static void mkdir(File file, boolean b) {
		if(b) {// true delete first
			deleteDirectory(file);
			mkdir(file);
		} else {
			mkdir(file);
		}
	}

	/**
	 * 
	 * @param path
	 * @return
	 */
	static public boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

	
	/**
	 * Read the smoke word dictionary as a string set from the given path
	 * @param url
	 * @return
	 */
	public static Set<String> getWordsFromDict(String url){
		Set<String> res = new HashSet<String>();
		File file = new File(url);
		FileInputStream inStream = null;
		try {
			inStream = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inStream));
			String line = null;
			while ((line = reader.readLine()) != null) {
				String[] array = line.split(":");
				res.add(array[0].trim());
			}
			
			reader.close();
			inStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}

}
