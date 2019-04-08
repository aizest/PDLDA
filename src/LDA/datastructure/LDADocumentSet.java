package LDA.datastructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import datastructures.Complaint;


public class LDADocumentSet {
	//Document list
	private List<LDADocument> docs; 

	//A map which saves the index and term info of component words. 
	//Key: Term; Value: Order (index) of this word in the "compIndexToTermList" list
	private Map<String, Integer> compTermToIndexMap;
	
	//A map which saves the index and term info of symptom words. 
	//Key: Term; Value: Order (index) of this word in the "sympIndexToTermList" list
	private Map<String, Integer> sympTermToIndexMap;
	
	// A map which saves the index and term info of resolution words.
	// Key: Term; Value: Order (index) of this word in the "resoIndexToTermList" list
	private Map<String, Integer> resoTermToIndexMap;
	
	// A map which saves the index and term info of all types of words.
	// Key: Term; Value: Order (index) of this word in the "allIndexToTermList" list
	private Map<String, Integer> allTermToIndexMap;

	//The list works as a vocabulary for component words, whose values are terms. 
	//The list index maintains the order of the term in this dictionary.
	private List<String> compIndexToTermList;
	
	// The list works as a vocabulary for symptom words, whose values are terms.
	// The list index maintains the order of the term in this dictionary.
	private List<String> sympIndexToTermList;
	
	// The list works as a vocabulary for resolution words, whose values are terms.
	// The list index maintains the order of the term in this dictionary.
	private List<String> resoIndexToTermList;
	
	// The list works as a vocabulary for all types of words, whose values are terms.
	// The list index maintains the order of the term in this dictionary.
	private List<String> allIndexToTermList;


	//A map for term frequency of component words. 
	//Key: Term; Value: term count in the corpus
	private Map<String,Integer> compTermCountMap;
	
	//A map for term frequency of symptom words. 
	//Key: Term; Value: term count in the corpus
	private Map<String,Integer> sympTermCountMap;
	
	// A map for term frequency of resolution words.
	// Key: Term; Value: term count in the corpus
	private Map<String, Integer> resoTermCountMap;
	
	// A map for term frequency of all types of words.
	// Key: Term; Value: term count in the corpus
	private Map<String, Integer> allTermCountMap;
	
	public LDADocumentSet(){
		docs = new ArrayList<LDADocument>();
		
		compTermToIndexMap = new HashMap<String, Integer>();
		compIndexToTermList = new ArrayList<String>();
		compTermCountMap = new HashMap<String, Integer>();
		
		sympTermToIndexMap = new HashMap<String, Integer>();
		sympIndexToTermList = new ArrayList<String>();
		sympTermCountMap = new HashMap<String, Integer>();
		
		resoTermToIndexMap = new HashMap<String, Integer>();
		resoIndexToTermList = new ArrayList<String>();
		resoTermCountMap = new HashMap<String, Integer>();
		
		allTermToIndexMap = new HashMap<String, Integer>();
		allIndexToTermList = new ArrayList<String>();
		allTermCountMap = new HashMap<String, Integer>();
	}
	
	
	/**
	 * This function reads every complaint object in the given list, and updates the indexing data structures of 3 entities 
	 * (Component, Symptom, and Resolution)
	 * 
	 * @param complaints
	 */
	public void readDocsFromDBInWordGroup(List<Complaint> complaints){
		for(Complaint complaint : complaints){
			LDADocument doc = new LDADocument(complaint, compTermToIndexMap, compIndexToTermList, compTermCountMap,
					sympTermToIndexMap, sympIndexToTermList, sympTermCountMap,
					resoTermToIndexMap, resoIndexToTermList, resoTermCountMap,
					allTermToIndexMap, allIndexToTermList, allTermCountMap);
			docs.add(doc);
		}
	}

	
	public List<LDADocument> getDocs() {
		return docs;
	}

	public void setDocs(ArrayList<LDADocument> docs) {
		this.docs = docs;
	}

	public Map<String, Integer> getCompTermToIndexMap() {
		return compTermToIndexMap;
	}

	public void setCompTermToIndexMap(Map<String, Integer> termToIndexMap) {
		this.compTermToIndexMap = termToIndexMap;
	}

	public List<String> getCompIndexToTermList() {
		return compIndexToTermList;
	}

	public void setCompIndexToTermList(ArrayList<String> indexToTermList) {
		this.compIndexToTermList = indexToTermList;
	}

	public Map<String, Integer> getCompTermCountMap() {
		return compTermCountMap;
	}

	public void setCompTermCountMap(Map<String, Integer> termCountMap) {
		this.compTermCountMap = termCountMap;
	}
	
	public Map<String, Integer> getSympTermToIndexMap() {
		return sympTermToIndexMap;
	}


	public void setSympTermToIndexMap(Map<String, Integer> sympTermToIndexMap) {
		this.sympTermToIndexMap = sympTermToIndexMap;
	}


	public List<String> getSympIndexToTermList() {
		return sympIndexToTermList;
	}


	public void setSympIndexToTermList(List<String> sympIndexToTermList) {
		this.sympIndexToTermList = sympIndexToTermList;
	}


	public Map<String, Integer> getSympTermCountMap() {
		return sympTermCountMap;
	}


	public void setSympTermCountMap(Map<String, Integer> sympTermCountMap) {
		this.sympTermCountMap = sympTermCountMap;
	}
	
	public Map<String, Integer> getResoTermToIndexMap() {
		return resoTermToIndexMap;
	}


	public void setResoTermToIndexMap(Map<String, Integer> resoTermToIndexMap) {
		this.resoTermToIndexMap = resoTermToIndexMap;
	}


	public List<String> getResoIndexToTermList() {
		return resoIndexToTermList;
	}


	public void setResoIndexToTermList(List<String> resoIndexToTermList) {
		this.resoIndexToTermList = resoIndexToTermList;
	}


	public Map<String, Integer> getResoTermCountMap() {
		return resoTermCountMap;
	}


	public void setResoTermCountMap(Map<String, Integer> resoTermCountMap) {
		this.resoTermCountMap = resoTermCountMap;
	}
	
	public Map<String, Integer> getAllTermToIndexMap() {
		return allTermToIndexMap;
	}


	public void setAllTermToIndexMap(Map<String, Integer> allTermToIndexMap) {
		this.allTermToIndexMap = allTermToIndexMap;
	}


	public List<String> getAllIndexToTermList() {
		return allIndexToTermList;
	}


	public void setAllIndexToTermList(List<String> allIndexToTermList) {
		this.allIndexToTermList = allIndexToTermList;
	}


	public Map<String, Integer> getAllTermCountMap() {
		return allTermCountMap;
	}


	public void setAllTermCountMap(Map<String, Integer> allTermCountMap) {
		this.allTermCountMap = allTermCountMap;
	}

}
