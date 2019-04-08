package utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Xuan Zhang
 */

public class DateUtil {
	/**
	 * Calculate the time period between the specified start time and end time (in Minutes and seconds)
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static String calculatePeriod(Date start, Date end){
		long lStart = start.getTime();
		long lEnd = end.getTime();
		
		long lMin = (lEnd - lStart) / (1000 * 60);
		long lSec = ((lEnd - lStart) % (1000 * 60)) / 1000;
		
		return (lMin + " MIN " + lSec + " SEC"); 
	}
	
	public static List<String> sortDateString(HashMap<String, Integer> When, int threshold){
		List<String> sortedWhen = new LinkedList<String>();
		List<Date> dateList = new LinkedList<Date>();
		SimpleDateFormat formatter = new SimpleDateFormat("MMM d , yyyy");		
		
		for(String dateString : When.keySet()){
			
			if(When.get(dateString) < threshold) continue;
			
			try {				
				dateList.add(formatter.parse(dateString));				
			} catch (ParseException e) {
				//e.printStackTrace();
			}			
		}
		
		Date tempDate = null;  
        for (int i = dateList.size()- 1; i > 0; --i){
            for (int j = 0; j < i; ++j) {  
            	if(dateList.get(j+1).before(dateList.get(j))){  
                    tempDate = dateList.get(j);  
                    dateList.set(j, dateList.get(j+1));  
                    dateList.set(j+1, tempDate);  
                }
            }
        }
        
        for(Date date : dateList){
        	sortedWhen.add(formatter.format(date));
        }
        
		return sortedWhen;
	}

	public static void main(String args[]){
		try{
			// test calculatePeriod
			Date start = new Date();			
			Thread.sleep(3000);
			Date end = new Date();			
			System.err.println(calculatePeriod(start, end));
			
			// test sortDateString
			HashMap<String, Integer> newWhen = new HashMap<String, Integer>();
			newWhen.put("Mar 6 , 2014", 5);
			newWhen.put("Mar 26 , 2014", 3);
			newWhen.put("Mar 19 , 2014", 1);
			newWhen.put("Apr 9 , 2014", 4);
			newWhen.put("Mar 14 , 2014", 3);
			newWhen.put("Apr 2 , 2014", 1);
			newWhen.put("Monday", 5);
			newWhen.put("Mar 18 , 2014", 2);
			newWhen.put("Tuesday", 2);
			newWhen.put("Wednesday", 1);
			newWhen.put("Mar 7 , 2014", 7);
			List<String> date = sortDateString(newWhen, 3);
			System.out.println(date);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
