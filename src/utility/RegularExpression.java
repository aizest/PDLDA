package utility;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegularExpression {
	public static boolean hasTripleRepeatedLetters (String str){
		Pattern pattern = Pattern.compile("([a-z])\\1{2}");
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
	}
	
	public static String[] search2Nums (String str){
		Pattern pattern = Pattern.compile("(?=\\D|^|\\b)((8|9|0){1}\\d{1}|1{1}[012345]{1})(?=\\D|$|\\b)");
        Matcher matcher = pattern.matcher(str);
        
        List<String> list = new ArrayList<String>();
        while(matcher.find()){
        	list.add(matcher.group(0).trim());
        }
        
        String[] result = new String[list.size()];
        result = list.toArray(result);
        
        return result;
	}
	
	public static String[] search4Nums (String str){
		Pattern pattern = Pattern.compile("(?=\\D|^|\\b)((198|199|200){1}\\d{1}|(201){1}[012345]{1})(?=\\D|$|\\b)");
        Matcher matcher = pattern.matcher(str);
        
        List<String> list = new ArrayList<String>();
        while(matcher.find()){
        	list.add(matcher.group(0).trim());
        }
        
        String[] result = new String[list.size()];
        result = list.toArray(result);
        
        return result;
	}
	
	public static void printYears(String[] array){
		if(array != null){
			System.out.print("Years extracted: ");
			for(String str: array){
				System.out.print(str + "\t");
			}
		}
		System.out.println();
	}
	
	/**
	 * Remove the American State Abbreviations, such as "*NM", "*SCC", etc.
	 * 
	 * @param text
	 * @return
	 */
	public static String removeStateAbbr(String text){
		if(text != null)
			return text.replaceAll("\\s\\*[A-Z]{2,3}", "");
		else
			return null;
	}
	
	public static void main(String[] args)
    {
//        Pattern pattern = Pattern.compile("([a-z])\\1{2}");
//        Matcher matcher = pattern.matcher("ffuunnn");
//        System.out.println(matcher.find());
		System.out.println(hasTripleRepeatedLetters("ffuuunnnn"));
		
		String str = "1980 Camry Windshield Wiper Malfunction";
		System.out.println(str);
		String[] array = search4Nums(str);
		printYears(array);
		
		str = "A few questions on 2013 camry";
		System.out.println(str);
		array = search4Nums(str);
		printYears(array);
		
		str = "A few questions on 2017 camry";
		System.out.println(str);
		array = search4Nums(str);
		printYears(array);
		
		str = "A few questions on 2014+ camry";
		System.out.println(str);
		array = search4Nums(str);
		printYears(array);
		
		str = "95' Camry - Weak Electrical System?";
		System.out.println(str);
		array = search2Nums(str);
		printYears(array);
		
		str = "85 95 Camry 96 - Weak Electrical System?";
		System.out.println(str);
		array = search2Nums(str);
		printYears(array);
		
		str = "OBDII scanner location in '97?";
		System.out.println(str);
		array = search2Nums(str);
		printYears(array);
		
		str = "OBDII scanner is 18 inch";
		System.out.println(str);
		array = search2Nums(str);
		printYears(array);
		
		str = "OBDII scanner location in 197";
		System.out.println(str);
		array = search2Nums(str);
		printYears(array);
		
		str = "OBDII scanner location in 7";
		System.out.println(str);
		array = search2Nums(str);
		printYears(array);
    }

}
