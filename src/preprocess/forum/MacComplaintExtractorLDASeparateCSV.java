/**
 * This class extracts complaints from the forum data set saved in a CSV file.
 * This class is designed as an entity extractor for PDLDA.
 * The key entities are extracted from corresponding fields of the CSV file. 
 */
package preprocess.forum;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.opencsv.CSVReader;

import utility.CSVParser;
import utility.FileUtils;
import utility.TextUtil;
import console.GlobalConstants;
import datastructures.Complaint;
import datastructures.ItemCount;
import preprocess.utility.DocumentProcess;

/**
 * @author Xuan Zhang
 *
 */
public class MacComplaintExtractorLDASeparateCSV {
	
	public static String EARLIEST_YEAR = "2010";
	private static String MODEL = null;
	
	/**
	 * Build lexicon items from CSV
	 */
	public static Set<String> readLexicon(String lexicon){
		String[] columns = {"item","count"};
		List<Object> records = CSVParser.retrieveDataFromCSV(lexicon, ItemCount.class, columns);
		Set<String> itemList=new HashSet<String>();
		for(Object obj:records){
			ItemCount ic=(ItemCount) obj;
			itemList.add(ic.getItem().toLowerCase().trim());
		}
		return itemList;
	}
	
		
	public static List<Complaint> extractComplaints(Map<String, String> configMap) throws ParseException{
		List<Complaint> result = new ArrayList<Complaint>();
		
		//Initialize data location, model configuration, and DEBUG switch from the configuration file
		if (configMap == null) // If failed to read the given configMap, then read the configuration from the default location
			configMap = FileUtils.readKeyValueMap(GlobalConstants.CONFIG_PATH);
		MODEL = (String)(configMap.get("PRODUCT_MODEL"));
		String csvPath = configMap.get("COMPLAINT_CSV");
		try {
			EARLIEST_YEAR = configMap.get("EARLIEST_YEAR");
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
			System.err.println("INVALID YEAR setting! The YEAR value is set to 1900 as default value.");
		}
		
		GlobalConstants.setDEBUG(Boolean.parseBoolean(configMap.get("DEBUG")));
		List<String[]> entries = null;
		try {
			FileReader fReader = new FileReader(csvPath);
			CSVReader csvReader = new CSVReader(fReader);
			
			entries = csvReader.readAll();
			
			int compWithReso = 0;
			int compWithSymp = 0;
			
			for(int i = 1;i < entries.size(); i++){
				String[] record = entries.get(i);
				
				List<String> currentComps;
				List<String> currentSMwords;
				List<String> currentResoWords;
				
				String currentID = record[0];
				String clusterID = record[1];
				String currentModel = MODEL;
				String currentYear = EARLIEST_YEAR;
				String title = record[2];
				String qc = record[3];
				String sc = record[4];
				String curComponents = record[5];
				String curSymptoms = record[6];
				String curResolutions = record[7];
				
				
				String content = title;
				String lastChar = content.substring(content.length()-1);
				if (!TextUtil.isPunctuation(lastChar))
					content += ". ";
				content += qc;
				lastChar = content.substring(content.length()-1);
				if (!TextUtil.isPunctuation(lastChar))
					content += ". ";
				content += sc;
				
				currentComps = Arrays.asList(curComponents.split(","));
				currentSMwords = Arrays.asList(curSymptoms.split(","));
				currentSMwords = ruleOutComponentWords(currentComps, currentSMwords);
				if(currentSMwords != null && currentSMwords.size() > 0)
					compWithSymp++;
				
				currentResoWords = Arrays.asList(curResolutions.split(","));
				currentResoWords = ruleOutComponentWords(currentComps, currentResoWords);
				if(currentResoWords != null && currentResoWords.size() > 0)
					compWithReso++;
			
				List<String> missingEntities = new ArrayList<String>();
				if (currentModel == null || currentModel.length() == 0 )
					missingEntities.add("Model");
				if(currentYear == null || currentYear.length() == 0)
					missingEntities.add("Year");
				if(currentComps == null || currentComps.size() == 0)
					missingEntities.add("Component");
				if(currentSMwords == null || currentSMwords.size() == 0)
					missingEntities.add("Symptom");
				if(currentResoWords == null || currentResoWords.size() == 0)
					missingEntities.add("Resolution");
				if(missingEntities.size() > 0){
					System.err.println("Complaint[" + currentID + "] doesn't have " + missingEntities.toString() + ". Skip this record!\n");
					continue;
				}
				
				currentModel = currentModel + ":" + currentYear;
				
				Complaint cmpl = new Complaint(currentID, currentModel, currentComps, currentSMwords, currentResoWords, currentYear, new Date(), content);
				cmpl.setClusterID(clusterID);
				result.add(cmpl);
				int size = result.size();
				if(size % 100 == 0){
					System.out.println(size + " records extracted from DB...");
				}
				
			}
			
			System.out.println("\n" + compWithSymp + " out of " + (entries.size()-1) +  " complaints have symptom sentences and words");
			System.out.println(compWithReso + " out of " + (entries.size()-1) +  " complaints have resolution sentences and words");
			
			result = DocumentProcess.filterLowDFWords(result);
			System.out.println("\n" + result.size() + " valid complaints ready for LDA\n");
			
			csvReader.close();
			fReader.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return result;
		} catch (IOException e1) {
			e1.printStackTrace();
			return result;
		}
		
		return result;
	}
	
	private static List<String> ruleOutComponentWords(List<String> compWords, List<String> entityWords){
		if (compWords == null || entityWords == null)
			return null;
		
		List<String> result = new ArrayList<String>();
		for (String word : entityWords){
			if (!compWords.contains(word))
				result.add(word);
		}
		
		return result;
	}
}
