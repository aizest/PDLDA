package datastructures;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Date;
import java.util.List;

import org.json.simple.JSONObject;

import utility.DataStructureUtil;

public class Complaint implements Cloneable{
	private String model;
	private List<String> components;
	private List<String> smokeWords;
	private List<String> resolutionWords;
	private List<TermMetric> componentTMs;
	private List<TermMetric> smokeWordTMs;
	private List<TermMetric> resolutionWordTMs;

	private String year;
	private Date postDate;
	private String description;
	
	private String clusterID;

	private String cmplID;
	
	public Complaint clone(){
		Complaint copy = new Complaint();
		copy.setModel(this.model);
		copy.setYear(this.year);
		copy.setDescription(this.description);
		copy.setClusterID(this.clusterID);
		if(this.postDate == null)
			copy.setPostDate(null);
		else
			copy.setPostDate(new Date(this.postDate.getTime()));
		
		if(this.components == null)
			copy.setComponents(null);
		else
			copy.setComponents(DataStructureUtil.cloneStringList(this.components));
		
		if(this.smokeWords == null)
			copy.setSmokeWords(null);
		else
			copy.setSmokeWords(DataStructureUtil.cloneStringList(this.smokeWords));
		
		if(this.resolutionWords == null)
			copy.setResolutionWords(null);
		else
			copy.setResolutionWords(DataStructureUtil.cloneStringList(this.resolutionWords));
		
		return copy;
	}

	protected Complaint(){
		super();
	}
	
	public Complaint(String model, List<String> components,
			List<String> smokeWords, String year, Date postDate) {
		super();
		this.model = model;
		this.components = components;
		this.smokeWords = smokeWords;
		this.year = year;
		this.postDate = postDate;
	}
	
	
	public Complaint(String model, List<String> components,
			List<String> smokeWords, String year, Date postDate, String description) {
		super();
		this.model = model;
		this.components = components;
		this.smokeWords = smokeWords;
		this.year = year;
		this.postDate = postDate;
		this.description = description;
	}
	
	public Complaint(String cmplID, String model, List<String> components,
			List<String> smokeWords, List<String> resolutionWords, String year, Date postDate, String description) {
		super();
		this.cmplID = cmplID;
		this.model = model;
		this.components = components;
		this.smokeWords = smokeWords;
		this.resolutionWords = resolutionWords;
		this.year = year;
		this.postDate = postDate;
		this.description = description;
	}
	
	
	public Complaint(String cmplID, String model, List<String> components,
			List<String> smokeWords, List<String> resolutionWords, String year, Date postDate, String description, String clusterID) {
		super();
		this.cmplID = cmplID;
		this.model = model;
		this.components = components;
		this.smokeWords = smokeWords;
		this.resolutionWords = resolutionWords;
		this.year = year;
		this.postDate = postDate;
		this.description = description;
		this.clusterID = clusterID;
	}
	
	public Complaint(String cmplID, String model, List<String> components,
			List<String> dependentWords, String year, Date postDate, String description, ComplaintEntityType entityType) {
		super();
		this.cmplID = cmplID;
		this.model = model;
		this.components = components;
		if (entityType == ComplaintEntityType.SYMPTOM)
			this.smokeWords = dependentWords;
		else if (entityType == ComplaintEntityType.RESOLUTION)
			this.resolutionWords = dependentWords;
		this.year = year;
		this.postDate = postDate;
		this.description = description;
	}
	
	public Complaint(String model, List<String> components,
			List<String> smokeWords, String year, Date postDate, String description, String clusterID) {
		super();
		this.model = model;
		this.components = components;
		this.smokeWords = smokeWords;
		this.year = year;
		this.postDate = postDate;
		this.description = description;
		this.clusterID = clusterID;
	}
	
	public Complaint(String model, String year, List<String> components, Date postDate,
			String description, List<String> subjectModifiers,
			List<String> subjects, List<String> verbModifiers, 
			List<String> verbs, List<String> objectModifiers, 
			List<String> objects, String clusterID) {
		super();
		this.model = model;
		this.year = year;
		this.components = components;
		this.postDate = postDate;
		this.description = description;
		this.clusterID = clusterID;
	}
	
	public String getModel() {
		return model;
	}
	
	public void setModel(String model) {
		this.model = model;
	}
	
	public List<String> getComponents() {
		return components;
	}
	
	public void setComponents(List<String> components) {
		this.components = components;
	}
	
	public String getCmplID() {
		return cmplID;
	}

	public void setCmplID(String cmplID) {
		this.cmplID = cmplID;
	}
	
	public List<String> getSmokeWords() {
		return smokeWords;
	}
	
	public void setSmokeWords(List<String> smokeWords) {
		this.smokeWords = smokeWords;
	}
	
	public List<String> getResolutionWords() {
		return resolutionWords;
	}

	public void setResolutionWords(List<String> resolutionWords) {
		this.resolutionWords = resolutionWords;
	}
	
	public List<TermMetric> getComponentTMs() {
		return componentTMs;
	}

	public void setComponentTMs(List<TermMetric> componentTMs) {
		this.componentTMs = componentTMs;
	}

	public List<TermMetric> getSmokeWordTMs() {
		return smokeWordTMs;
	}

	public void setSmokeWordTMs(List<TermMetric> smokeWordTMs) {
		this.smokeWordTMs = smokeWordTMs;
	}

	public List<TermMetric> getResolutionWordTMs() {
		return resolutionWordTMs;
	}

	public void setResolutionWordTMs(List<TermMetric> resolutionWordTMs) {
		this.resolutionWordTMs = resolutionWordTMs;
	}
	
	public String getYear() {
		return year;
	}
	
	public void setYear(String year) {
		this.year = year;
	}
	
	public Date getPostDate() {
		return postDate;
	}
	
	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getClusterID() {
		return clusterID;
	}

	public void setClusterID(String clusterID) {
		this.clusterID = clusterID;
	}

	public void print(){
		if (cmplID != null)
			System.out.println("[Complaint ID]: " + cmplID);
		System.out.println("[Model]: " + model);
		System.out.println("[Year]: " + year);
		if(components != null)
			System.out.println("[Component]: " + components.toString());
		if(smokeWords != null)
			System.out.println("[Symptom Words]: " + smokeWords.toString());
		if(resolutionWords != null)
			System.out.println("[Resolution words]: " + resolutionWords.toString());
		if(postDate != null)
			System.out.println("[Date]: " + postDate.toString());
		System.out.println("[Description]: " + description);
		if (clusterID != null)
			System.out.println("[Cluster ID]: " + clusterID);
	}
	
	public void print(Writer writer){
		try {
			if (cmplID != null)
				writer.write("\n\t[Complaint ID]: " + cmplID);
			writer.write("\n\t[Model]: " + model);
			writer.write("\n\t[Year]: " + year);
			if(components != null)
				writer.write("\n\t[Component]: " + components.toString());
			if(smokeWords != null)
				writer.write("\n\t[Symptom Words]: " + smokeWords.toString());
			if(resolutionWords != null)
				writer.write("\n\t[Resolution words]: " + resolutionWords.toString());
			if(postDate != null)
				writer.write("\n\t[Date]: " + postDate.toString());
			writer.write("\n\t[Description]: " + description + "\n");
//			writer.write("\n\t[Age]: " + age);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	//added by lijie, to JSONOjbect
	@SuppressWarnings("unchecked")
	public JSONObject toJson(){
		JSONObject res=new JSONObject();
		res.put("Model", model);
		res.put("Year", year);
		res.put("Component", components.toString());
		res.put("KeyWords", (smokeWords != null) ? smokeWords.toString() : "");
		res.put("date",postDate.toString());
		res.put("Description", description);
		return res;
	}
	
	public static void exportComplaintsToCSV(List<Complaint> complaintList, String url) {
		File file = new File(url);

		try {
			PrintWriter pw = new PrintWriter(file);

			int docNum = 0;
			for (Complaint complaint : complaintList) {

				// Print every row (each complaint), including model-year, component words and smoke words
				String modelYear = complaint.getModel();
				List<String> components = complaint.getComponents();
				List<String> smokeWords = complaint.getSmokeWords();
				
				pw.print(modelYear + " ");
				for(String item : components){
					pw.print(item + " ");
				}
				for(String item : smokeWords){
					pw.print(item + " ");
				}
				pw.println();
				docNum++;
			}
			
			System.out.println(docNum + " complaints exported to " + url);
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
