package utility;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;


public class CSVParser {
	public static List<Object> retrieveDataFromCSV(String url, Class classType, String[] columns){
		List<Object> result = null;
		ColumnPositionMappingStrategy strat = new ColumnPositionMappingStrategy();
		strat.setType(classType);
		// the fields to bind do in your JavaBean
		strat.setColumnMapping(columns);

		CsvToBean csv = new CsvToBean();
		try {
			FileReader fReader = new FileReader(url);
			CSVReader csvReader = new CSVReader(fReader);
			
			result = (List<Object>)csv.parse(strat, csvReader);
			
			csvReader.close();
			fReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("\n" + result.size() + " records parsed from " + url + "\n");
		return result;
	}

}
