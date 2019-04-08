package preprocess.utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import datastructures.Complaint;
import datastructures.ComplaintEntityType;
import datastructures.Document;
import datastructures.TermMetric;
import utility.TextUtil;


public class DocumentProcess {
	public static final int MINIMUM_OVERALL_FREQUENCY = 20;
	public static final int DOCUMENT_FREQ_SHINK_LEVEL = 10;
	private static final int THREADS_LIMIT = 10;
	public static final int LOW_DF = 5;
	public static final int MAX_WORD_LENGTH = 20;
	
	public static Set<String> createFullTermSet(List<Document> docList) {
		Set<String> fullTermSet = new TreeSet<String>();
		if (docList == null)
			return fullTermSet;

		for (Document doc : docList) {
			String article = doc.getContent();

			String[] array = article.split(" ");
			for (String str : array) {
				if (str.length() > 0)
					fullTermSet.add(str);
			}
		}

		System.err.println("Size of the Full Term Set: " + fullTermSet.size());

		int num = 1;
		for (String str : fullTermSet) {
			System.out.print(str + "\t");
			if (num % 5 == 0)
				System.out.println();
			num++;
		}
		System.out.println();

		return fullTermSet;
	}
	
	/**
	 * Create a term frequency array for every document in the specified list.
	 * The dimensions of the vector are specified by the termSet
	 * 
	 * @param docList
	 * @param termSet
	 */
	public static void createTermFrequencies(List<Document> docList,
			final Set<String> termSet) {
		if (docList == null || termSet == null) {
			System.err
					.println("Null pointer appears during creating term frequency for documents!");
			return;
		}

		String[] termArray = new String[termSet.size()];
		termArray = termSet.toArray(termArray);

		for (Document doc : docList) {
			// Initialize the term frequency array with zeros
			int[] tf = new int[termSet.size()];
			for (int i = 0; i < tf.length; i++) {
				tf[i] = 0;
			}

			String article = doc.getContent();
			String[] array = article.split(" ");

			// For every appearance of certain term, increase its frequency by 1
			for (String str : array) {
				if (str == null || str.length() == 0)
					continue;

				int index = TextUtil.binarySearch(termArray, str);
				// Skip the word which is not included in the feature
				if (index < 0)
					continue;
				// Increase the corresponding array element by 1
				tf[index] += 1;
			}

			// Set the term frequencies value of this document
			doc.setTermFrequencies(tf);
		}
	}
	
	
	private static List<String> getEntityWordList(Complaint cmpl, ComplaintEntityType entityType){
		if (cmpl == null)
			return null;
		
		switch (entityType) {
		case COMPONENT:
			return cmpl.getComponents();
		case SYMPTOM:
			return cmpl.getSmokeWords();
		case RESOLUTION:
			return cmpl.getResolutionWords();
		default:
			return null;
		}
	}
	
	private static List<TermMetric> getEntityTMList(Complaint cmpl, ComplaintEntityType entityType){
		if (cmpl == null)
			return null;
		
		switch (entityType) {
		case COMPONENT:
			return cmpl.getComponentTMs();
		case SYMPTOM:
			return cmpl.getSmokeWordTMs();
		case RESOLUTION:
			return cmpl.getResolutionWordTMs();
		default:
			return null;
		}
	}
	
	private static void setEntityTFIDFList(Complaint cmpl, ComplaintEntityType entityType, List<TermMetric> tmList){
		if (cmpl == null)
			return;
		
		switch (entityType) {
		case COMPONENT:
			cmpl.setComponentTMs(tmList);
			return;
		case SYMPTOM:
			cmpl.setSmokeWordTMs(tmList);
			return;
		case RESOLUTION:
			cmpl.setResolutionWordTMs(tmList);
			return;
		default:
			return;
		}
	}
	
	/**
	 * Normalize the TFIDF values of each type of entity in the given complaint list
	 * 
	 * @param complaints
	 */
	public static void normalizeEntityTFIDF(List<Complaint> complaints){
		ComplaintEntityType[] entityTypeArray = {ComplaintEntityType.COMPONENT, ComplaintEntityType.SYMPTOM, ComplaintEntityType.RESOLUTION};
		for (ComplaintEntityType entityType : entityTypeArray){
			for (Complaint cmpl : complaints) {
				List<TermMetric> normalizedList = new ArrayList<TermMetric>();
				List<TermMetric> tmList = getEntityTMList (cmpl, entityType);
				if (tmList.size() == 1){
					TermMetric tm = new TermMetric(tmList.get(0).getTerm(), 1);
					normalizedList.add(tm);
				}else{
					double max = 0, min = Double.MAX_VALUE;
					
					//Find the Max and the Min
					for (TermMetric tm : tmList){
						double value = tm.getMetric();
						if (value > max)
							max = value;
						if (value < min)
							min = value;
					}
					
					double diff = max - min;
					
					for (TermMetric tm : tmList){
						String term = tm.getTerm();
						double oldValue = tm.getMetric();
						double value = (oldValue - min) / diff; // normalization formula
						normalizedList.add(new TermMetric(term, value));
					}
				}
				
				setEntityTFIDFList(cmpl, entityType, normalizedList);
			}
		}
	}
	
	
	/**
	 * Reinforce the probability of component terms in Symptom/Resolution topics.
	 * As some component topics may have high Document Frequency, which will decrease their TF/IDF value, hence move them into background topic.
	 * This method will identify the component terms in Symptom/Resolution words, and decrease their DF value, enlarge their probability to appear in Symptom/Resolution topics
	 * 
	 * @param complaints
	 * @param dfMap
	 */
	private static void reinforceComponentTerms(List<Complaint> complaints, Map<String, Integer> dfMap){
		Set<String> compTermSet = new HashSet<String>();
		for (Complaint cmpl : complaints) {
			compTermSet.addAll(cmpl.getComponents());
		}
		
		for (String term : dfMap.keySet()){
			if (compTermSet.contains(term)){
				int oldValue = dfMap.get(term);
				int newValue = oldValue / DOCUMENT_FREQ_SHINK_LEVEL;
				int shrinkedValue = newValue <= 0 ? 1 : newValue;
				dfMap.put(term, shrinkedValue);
			}
		}
	}
	
	/**
	 * Calculate the TFIDF value of each type of entity in the given complaint list
	 * 
	 * @param complaints
	 */
	public static void calculateComplaintEntityTFIDF(List<Complaint> complaints){
		ComplaintEntityType[] entityTypeArray = {ComplaintEntityType.COMPONENT, ComplaintEntityType.SYMPTOM, ComplaintEntityType.RESOLUTION};
		
		//For each type of entity, calculate TF-IDF of entity words
		for (ComplaintEntityType entityType : entityTypeArray){
			//Create a Document Frequency map, where the key is term and the value is DF
			Map<String, Integer> dfMap = new TreeMap<String, Integer>();
			if (complaints == null){
				System.err.println("NULL complaint list! Failed to calculate complaint entity TF-IDF");
				return;
			}
			
			int[] docVolumeArray = new int[complaints.size()];
			int count = 0;
			for (Complaint cmpl : complaints) {
				List<String> words = getEntityWordList(cmpl, entityType);
				docVolumeArray[count] = words.size();
				Set<String> wordSet = new HashSet<String>();
				wordSet.addAll(words);
				
				for (String str : wordSet){
					if (str.length() > 0){
						Integer value = dfMap.get(str);
						if(value == null)
							dfMap.put(str, new Integer(1));
						else
							dfMap.put(str, new Integer(value.intValue() + 1));
					}
				}
				count++;
			}
			
			//For symptom/resolution words, reinforce the component terms in them by shrinking their DF value
			if (entityType != ComplaintEntityType.COMPONENT)
				reinforceComponentTerms(complaints, dfMap);
			
			//Create a thread pool
			ExecutorService es = Executors
					.newFixedThreadPool(THREADS_LIMIT);
			
			for (Complaint cmpl : complaints) {
				ComplaintTFIDFThreadFor1Entity thread = new ComplaintTFIDFThreadFor1Entity(cmpl, entityType, dfMap, complaints.size());
				es.execute(thread);
			}
			
			es.shutdown();
			
			try {
				es.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	
	/**
	 * Calculate the TF-IDF of every term in corpus level from the specified document list
	 * 
	 * @param docList
	 * @return
	 */
	public static TermMetric[] calculateGlobalTermTFIDF(List<Document> docList, boolean dropLowDFTerms){
		//Create a term frequency map
		Map<String, Integer> map = new TreeMap<String, Integer>();
		if (docList == null)
			return null;
		
		int[] docVolumeArray = new int[docList.size()];
		int count = 0;
		for (Document doc : docList) {
			String article = doc.getContent();
			String[] array = article.split(" ");
			docVolumeArray[count] = array.length;
			for (String str : array) {
				
				if (str.length() > 0 && str.length() <= MAX_WORD_LENGTH){
					Integer value = map.get(str);
					if(value == null)
						map.put(str, new Integer(1));
					else
						map.put(str, new Integer(value.intValue() + 1));
				}
			}
			count++;
		}
		
		//Total number of words in the corpus
		long sum = 0;
		for(int i: docVolumeArray){
			sum += i;
		}
		
		//Create a thread pool
		ExecutorService es = Executors
				.newFixedThreadPool(THREADS_LIMIT);
		
		//Create a concurrent Term/TF-IDF map 
		Map<String, Double> tfIdfMap = new ConcurrentHashMap<String, Double>();

		
		//Compute TF-IDF of each term with a thread concurrently
		for(String str : map.keySet()){
			//Remove terms whose 1st character is NOT a letter
			char c = str.charAt(0);
			if(!TextUtil.isEnglishLetter(c))
				continue;
			
			Integer value = map.get(str);
			DocumentTFIDFThread thread = new DocumentTFIDFThread(str, value, sum, docList, tfIdfMap, dropLowDFTerms);
			es.execute(thread);
		}
		
		es.shutdown();
		
		try {
			es.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		TermMetric[] tmArray = new TermMetric[tfIdfMap.size()];
		count = 0;
		for(String term : tfIdfMap.keySet()){
			tmArray[count] = new TermMetric(term, tfIdfMap.get(term));
			count++;
		}
		
		return tmArray;
	}
	
	static class ComplaintTFIDFThreadFor1Entity implements Runnable{
		private Complaint cmpl;
		private ComplaintEntityType entityType;
		private Map<String, Integer> dfMap;
		private int docNum;
		
		public ComplaintTFIDFThreadFor1Entity (Complaint cmpl, ComplaintEntityType entityType, Map<String, Integer> dfMap, int docNum){
			this.cmpl = cmpl;
			this.entityType = entityType;
			this.dfMap = dfMap;
			this.docNum = docNum;
		}

		@Override
		public void run() {
			Map<String, Integer> tfMap = new HashMap<String, Integer>();
			List<String> wordList = getEntityWordList(cmpl, entityType);
			
			//Calculate raw TF
			for (String word : wordList){
				if (word.length() > 0){
					Integer value = tfMap.get(word);
					if(value == null)
						tfMap.put(word, new Integer(1));
					else
						tfMap.put(word, new Integer(value.intValue() + 1));
				}
			}
			
			int largestTF = 0;
			for (String term : tfMap.keySet()){
				int value = tfMap.get(term);
				if (value > largestTF)
					largestTF = value;
			}
			
			List<TermMetric> tmList = new ArrayList<TermMetric>();
			for (String word : wordList){
				if (word.length() <= 0){
					System.err.println("Null word in the <" + entityType + "> word list!");
					continue;
				}
				double tf = 0.5 + 0.5 * (double)tfMap.get(word) / largestTF; //Augmented term frequency, see Wikipedia
				double idf = Math.log(((double) docNum) / dfMap.get(word));
				double tfidf = tf * idf;
				tmList.add(new TermMetric(word, tfidf));
			}
			
			setEntityTFIDFList(cmpl, entityType, tmList);
		}
		
	}
	
	
	static class DocumentTFIDFThread implements Runnable{
		private String term;
		private int freq;
		private long sum;
		private List<Document> docList;
		private Map<String, Double> tfIdfMap;
		private boolean dropLowDFTerms;
		
		public DocumentTFIDFThread (String term, int freq, long sum, 
				List<Document> docList, Map<String, Double> tfIdfMap,
				boolean dropLowDFTerms){
			this.term = term;
			this.freq = freq;
			this.sum = sum;
			this.docList = docList;
			this.tfIdfMap = tfIdfMap;
			this.dropLowDFTerms = dropLowDFTerms;
		}
		
		@Override
		public void run() {
			double tf = (double)freq / sum;
			//Take the square root value of term frequency to penalize it
//			double tf = Math.sqrt(freq) / sum;
			
			int df = 1;//avoid divided by 0 when calculating IDF
			for(Document doc : docList){
				if(termExistsInDocument(term, doc))
					df++;
			}
			
			// Remove some rare terms, which occur in only very few documents. These might be typos
			if (dropLowDFTerms)
				if (df < LOW_DF + 1) 
					return;
			
			double idf = Math.log(((double) docList.size()) / df);
			double tf_idf = tf * idf;
			//Increase the weight of IDF
//			double tf_idf = tf * idf * idf;
			tfIdfMap.put(term, tf_idf);
		}
		
	}
	
	
	/**
	 * Remove the words with low document frequency (<5) from the complaint entities
	 * 
	 * @param complaints
	 * @return
	 */
	public static List<Complaint> filterLowDFWords(List<Complaint> complaints){
		List<Complaint> result = new ArrayList<Complaint>();
		
		System.out.println("\nRemoving words with LOW Document Frequency from complaint entities...");
		
		// Create a Document Frequency map, where the key is term and the value
		// is DF
		Map<String, Integer> dfMap = new TreeMap<String, Integer>();
		if (complaints == null) {
			System.err.println("NULL complaint list! Failed to calculate complaint entity TF-IDF");
			return null;
		}
		
		for (Complaint cmpl : complaints) {
			Set<String> wordSet = new HashSet<String>();
			List<String> compList = cmpl.getComponents();
			List<String> sympList = cmpl.getSmokeWords();
			List<String> resoList = cmpl.getResolutionWords();
			for (String word: compList){
				wordSet.add(word);
			}
			for (String word: sympList){
				wordSet.add(word);
			}
			for (String word: resoList){
				wordSet.add(word);
			}
			
			for (String str : wordSet){
				if (str.length() > 0){
					Integer value = dfMap.get(str);
					if(value == null)
						dfMap.put(str, new Integer(1));
					else
						dfMap.put(str, new Integer(value.intValue() + 1));
				}
			}
		}
		
		Set<String> lowDFSet = new HashSet<String>();
		for(String word : dfMap.keySet()){
			if (dfMap.get(word) < LOW_DF)
				lowDFSet.add(word);
		}
		
		for (Complaint cmpl : complaints) {
			List<String> compList = cmpl.getComponents();
			List<String> sympList = cmpl.getSmokeWords();
			List<String> resoList = cmpl.getResolutionWords();
			
			List<String> cleanCompList = new ArrayList<String>();
			List<String> cleanSympList = new ArrayList<String>();
			List<String> cleanResoList = new ArrayList<String>();
			
			for (String word: compList){
				if (!lowDFSet.contains(word))
					cleanCompList.add(word);
			}
			for (String word: sympList){
				if (!lowDFSet.contains(word))
					cleanSympList.add(word);
			}
			for (String word: resoList){
				if (!lowDFSet.contains(word))
					cleanResoList.add(word);
			}
			
			if (cleanCompList.size() > 0){
				cmpl.setComponents(cleanCompList);
				cmpl.setSmokeWords(cleanSympList);
				cmpl.setResolutionWords(cleanResoList);
				result.add(cmpl);
			}
		}
		
		System.out.println(lowDFSet.size() + " words with LOW DF have been identified and removed from complaint entities!\n");
		
		return result;
	}
	
	
	/**
	 * Test whether a term appears in a document
	 * 
	 * @param term
	 * @param doc
	 * @return
	 */
	private static boolean termExistsInDocument(String term, Document doc){
		if(term == null || doc == null){
			System.err.println("NULL pointer in termExistsInDocuments function");
			return false;
		}
		
		String article = doc.getContent();
		if(article.indexOf(" " + term + " ") > 0)
			return true;
		else if(article.indexOf(term + " ") == 0)
			return true;
		else if(article.endsWith(" " + term))
			return true;
		
		else return false;
	}
	
}
