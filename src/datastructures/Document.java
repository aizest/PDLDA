package datastructures;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Document implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;

	private String title;

	private String content;

	private double metrics;

	private Date time;

	private int categoryID;

	private String categoryStr;

	private int[] termFrequencies;

	private long docID;

	public Document(String title, String content, long docID) {
		this.title = title;
		this.content = content;
		this.docID = docID;
	}
	

	public Document(String title, String content, double metrics) {
		this.title = title;
		this.content = content;
		this.metrics = metrics;
	}

	public Document(String title, String content, double metrics, Date time,
			long docID) {
		this.title = title;
		this.content = content;
		this.metrics = metrics;
		this.time = time;
		this.docID = docID;
	}
	
	public Document(String title, String content,
			Date time, String categoryStr, long docID) {
		this.title = title;
		this.content = content;
		this.time = time;
		this.categoryStr = categoryStr;
		this.docID = docID;
	}

	public Document(String title, String content, double metrics, Date date,
			int categoryID, String categoryStr, int[] termFrequencies, long docID) {
		super();
		this.title = title;
		this.content = content;
		this.metrics = metrics;
		this.time = date;
		this.categoryID = categoryID;
		this.categoryStr = categoryStr;
		this.termFrequencies = termFrequencies;
		this.docID = docID;
	}

	public String getCategoryStr() {
		return categoryStr;
	}

	public void setCategoryStr(String categoryStr) {
		this.categoryStr = categoryStr;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public double getMetrics() {
		return metrics;
	}

	public void setMetrics(double metrics) {
		this.metrics = metrics;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date date) {
		this.time = date;
	}

	public int getCategoryID() {
		return categoryID;
	}

	public void setCategoryID(int categoryID) {
		this.categoryID = categoryID;
	}

	public int[] getTermFrequencies() {
		return termFrequencies;
	}

	public void setTermFrequencies(int[] termFrequencies) {
		this.termFrequencies = termFrequencies;
	}

	public long getDocID() {
		return docID;
	}

	public void setDocID(long docID) {
		this.docID = docID;
	}

	public Document clone() {
		// Copy the term frequency by value
		int[] tfCopy = null;
		if (this.termFrequencies != null) {
			tfCopy = new int[this.termFrequencies.length];
			for (int i = 0; i < this.termFrequencies.length; i++) {
				tfCopy[i] = this.termFrequencies[i];
			}
		}
		// System.arraycopy(this.termFrequencies, 0, tfCopy, 0,
		// this.termFrequencies.length);

		return new Document(this.title, this.content, this.metrics, this.time,
				this.categoryID, this.categoryStr, tfCopy, this.docID);
	}

	public static List<Document> cloneDocumentList(final List<Document> list) {
		synchronized (list) {
			List<Document> result = new ArrayList<Document>();
			if (list == null) {
				System.err.println("NULL document list!");
				return null;
			}

			for (Document item : list) {
				result.add(item.clone());
			}

			return result;
		}
	}
	
	/**
	 * Clone the document list, but set the term frequency array of each document to NULL
	 * @param list
	 * @return
	 */
	public static List<Document> cloneDocumentListWithNullTF(final List<Document> list) {
		List<Document> result = new ArrayList<Document>();
		synchronized (list) {
			
			if (list == null) {
				System.err.println("NULL document list!");
				return null;
			}

			for (Document item : list) {
				Document copy = item.clone();
				copy.setTermFrequencies(null);
				result.add(copy);
			}

		}
		
		System.gc();
		return result;
	}
	
	/**
	 * Print the ID, Title, and Content of this document
	 */
	public void printBasicInfo(){
		System.out.println(this.getDocID() + "\t" + this.getTitle() + "\t" + this.getContent());
	}

}
