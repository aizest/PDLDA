package LDA.datastructure;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import datastructures.Complaint;


public class LDADocument {	
	private String docName;
	
	//the value of item i is the index of the ith COMPONENT word of the document in the vocabulary
	private int[] componentDocWordIndexes; 
	//the value of item i is the index of the ith SYMPTOM word of the document in the vocabulary
	private int[] symptomDocWordIndexes;
	//the value of item i is the index of the ith RESOLUTION word of the document in the vocabulary
	private int[] resolutionDocWordIndexes;
	//the value of item i is the index of the ith word (including all types) of the document in the vocabulary
	private int[] allDocWordIndexes;
	
	
	public LDADocument(Complaint complaint, 
			Map<String, Integer> compTermToIndexMap, List<String> compIndexToTermList, Map<String, Integer> compTermCountMap,
			Map<String, Integer> sympTermToIndexMap, List<String> sympIndexToTermList, Map<String, Integer> sympTermCountMap,
			Map<String, Integer> resoTermToIndexMap, List<String> resoIndexToTermList, Map<String, Integer> resoTermCountMap,
			Map<String, Integer> allTermToIndexMap, List<String> allIndexToTermList, Map<String, Integer> allTermCountMap){
		initDocumentWithWordGroups(complaint, 
				compTermToIndexMap, compIndexToTermList, compTermCountMap,
				sympTermToIndexMap, sympIndexToTermList, sympTermCountMap,
				resoTermToIndexMap, resoIndexToTermList, resoTermCountMap,
				allTermToIndexMap, allIndexToTermList, allTermCountMap);
	}
	

	
	/**
	 * Initiate the index data structures of the Component, Symptom, and Resolution entities, using corresponding word lists
	 * in the given complaint.
	 * This function is developed for the implementation of tree-style PD-LDA.
	 * 
	 * @param complaint
	 * @param compTermToIndexMap
	 * @param compIndexToTermList
	 * @param compTermCountMap
	 * @param sympTermToIndexMap
	 * @param sympIndexToTermList
	 * @param sympTermCountMap
	 * @param resoTermToIndexMap
	 * @param resoIndexToTermList
	 * @param resoTermCountMap
	 * @param allTermToIndexMap
	 * @param allIndexToTermList
	 * @param allTermCountMap
	 */
	public void initDocumentWithWordGroups(Complaint complaint, 
			Map<String, Integer> compTermToIndexMap, List<String> compIndexToTermList, Map<String, Integer> compTermCountMap,
			Map<String, Integer> sympTermToIndexMap, List<String> sympIndexToTermList, Map<String, Integer> sympTermCountMap, 
			Map<String, Integer> resoTermToIndexMap, List<String> resoIndexToTermList, Map<String, Integer> resoTermCountMap,
			Map<String, Integer> allTermToIndexMap, List<String> allIndexToTermList, Map<String, Integer> allTermCountMap){
		
		this.docName = complaint.getModel();
		
		//Read various types of words and initialize their word index array
		List<String> componentWords = complaint.getComponents();
		List<String> symptomWords = complaint.getSmokeWords();
		List<String> resolutionWords = complaint.getResolutionWords();
		List<String> allWords = new ArrayList<String>();
		for(String item : componentWords){
			allWords.add(item.toLowerCase());
		}
		allWords.addAll(symptomWords);
		allWords.addAll(resolutionWords);
		
		//Transfer component word to index
		this.componentDocWordIndexes = new int[componentWords.size()];
		for(int i = 0; i < componentWords.size(); i++){
			String word = componentWords.get(i);
			if(!compTermToIndexMap.containsKey(word)){
				int newIndex = compTermToIndexMap.size();
				compTermToIndexMap.put(word, newIndex);
				compIndexToTermList.add(word);
				compTermCountMap.put(word, new Integer(1));
				componentDocWordIndexes[i] = newIndex;
			} else {
				componentDocWordIndexes[i] = compTermToIndexMap.get(word);
				compTermCountMap.put(word, compTermCountMap.get(word) + 1);
			}
		}
		
		//Transfer symptom word to index
		this.symptomDocWordIndexes = new int[symptomWords.size()];
		for (int i = 0; i < symptomWords.size(); i++) {
			String word = symptomWords.get(i);
			if (!sympTermToIndexMap.containsKey(word)) {
				int newIndex = sympTermToIndexMap.size();
				sympTermToIndexMap.put(word, newIndex);
				sympIndexToTermList.add(word);
				sympTermCountMap.put(word, new Integer(1));
				symptomDocWordIndexes[i] = newIndex;
			} else {
				symptomDocWordIndexes[i] = sympTermToIndexMap.get(word);
				sympTermCountMap.put(word, sympTermCountMap.get(word) + 1);
			}
		}
		
		// Transfer resolution word to index
		this.resolutionDocWordIndexes = new int[resolutionWords.size()];
		for (int i = 0; i < resolutionWords.size(); i++) {
			String word = resolutionWords.get(i);
			if (!resoTermToIndexMap.containsKey(word)) {
				int newIndex = resoTermToIndexMap.size();
				resoTermToIndexMap.put(word, newIndex);
				resoIndexToTermList.add(word);
				resoTermCountMap.put(word, new Integer(1));
				resolutionDocWordIndexes[i] = newIndex;
			} else {
				resolutionDocWordIndexes[i] = resoTermToIndexMap.get(word);
				resoTermCountMap.put(word, resoTermCountMap.get(word) + 1);
			}
		}
		
		// Transfer all word to index
		this.allDocWordIndexes = new int[allWords.size()];
		for (int i = 0; i < allWords.size(); i++) {
			String word = allWords.get(i);
			if (!allTermToIndexMap.containsKey(word)) {
				int newIndex = allTermToIndexMap.size();
				allTermToIndexMap.put(word, newIndex);
				allIndexToTermList.add(word);
				allTermCountMap.put(word, new Integer(1));
				allDocWordIndexes[i] = newIndex;
			} else {
				allDocWordIndexes[i] = allTermToIndexMap.get(word);
				allTermCountMap.put(word, allTermCountMap.get(word) + 1);
			}
		}
	}
	
	
	public String getDocName() {
		return docName;
	}

	public void setDocName(String docName) {
		this.docName = docName;
	}

	public int[] getComponentDocWordIndexs() {
		return componentDocWordIndexes;
	}

	public void setComponentDocWordIndexs(int[] docWords) {
		this.componentDocWordIndexes = docWords;
	}
	
	public int[] getSymptomDocWordIndexes() {
		return symptomDocWordIndexes;
	}

	public void setSymptomDocWordIndexes(int[] symptomDocWordIndexes) {
		this.symptomDocWordIndexes = symptomDocWordIndexes;
	}
	
	public int[] getResolutionDocWordIndexes() {
		return resolutionDocWordIndexes;
	}

	public void setResolutionDocWordIndexes(int[] resolutionDocWordIndexes) {
		this.resolutionDocWordIndexes = resolutionDocWordIndexes;
	}
	
	public int[] getAllDocWordIndexes() {
		return allDocWordIndexes;
	}

	public void setAllDocWordIndexes(int[] allDocWordIndexes) {
		this.allDocWordIndexes = allDocWordIndexes;
	}
}
