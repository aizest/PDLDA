package LDA.console;

/**
 * A class to run PDLDA models on word groups extracted from given complaints.
 * @author Xuan Zhang
 */

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import LDA.config.Modelparameters;
import LDA.config.PathConfig;
import LDA.datastructure.LDADocumentSet;
import LDA.model.DefectLDADependentTopicsForWordGroupTreeStyleTFIDF;
import datastructures.Complaint;
import preprocess.forum.MacComplaintExtractorLDASeparateCSV;
import preprocess.forum.PatientComplaintExtractorLDASeparateCSV;
import utility.DateUtil;
import utility.FileUtils;


public class PDLDAOnWordGroups {
	private static final String CONFIG_PATH = "config" + File.separator + "PDLDA.config.properties";
	
	/** Get parameters from configuring file. If the 
	 * configuring file has value in it, use the value.
	 * Else the default value in program will be used
	 * @param ldaparameters
	 * @param parameterFile
	 * @return void
	 */
	private static void getParametersFromFile(Modelparameters ldaparameters, Map<String, String> configMap) {
		ldaparameters.setAlpha(Float.valueOf(configMap.get("ALPHA")));
		ldaparameters.setBeta(Float.valueOf(configMap.get("BETA")));
		ldaparameters.setGamma(Float.valueOf(configMap.get("GAMMA")));
		ldaparameters.setDelta(Float.valueOf(configMap.get("DELTA")));
		ldaparameters.setEpsilon(Float.valueOf(configMap.get("EPSILON")));
		ldaparameters.setMu(Float.valueOf(configMap.get("MU")));
		ldaparameters.setCompWordMinProb(Float.valueOf(configMap.get("COMPONENT_WORD_MIN_PROB")));
		
		ldaparameters.setTHREAD_NUM(Integer.parseInt(configMap.get("THREAD_NUM")));
		
		ldaparameters.setCompTopicNum(Integer.valueOf(configMap.get("COMPONENT_TOPIC_NUM")));
		ldaparameters.setSympTopicNum(Integer.valueOf(configMap.get("SYMPTOM_TOPIC_NUM")));
		ldaparameters.setResoTopicNum(Integer.valueOf(configMap.get("RESOLUTION_TOPIC_NUM")));
		ldaparameters.setCompWordNum(Integer.valueOf(configMap.get("COMPONENT_WORD_NUM")));
		ldaparameters.setSympWordNum(Integer.valueOf(configMap.get("SYMPTOM_WORD_NUM")));
		ldaparameters.setResoWordNum(Integer.valueOf(configMap.get("RESOLUTION_WORD_NUM")));
		ldaparameters.setIteration(Integer.valueOf(configMap.get("ITERATION")));
		ldaparameters.setSaveStep(Integer.valueOf(configMap.get("SAVE_STEP")));
		ldaparameters.setBeginSaveIters(Integer.valueOf(configMap.get("BEGIN_SAVE_ITARATION")));
	}
	
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		Date veryBegin, veryEnd;
		List<Complaint> trainComplaints = null; //The training part of extracted complaints
		veryBegin = new Date();
		
		
		Map<String, String> configMap = FileUtils.readKeyValueMap(CONFIG_PATH);
		
		System.out.println("Reading complaint data...");
		
		// *** STEP 1: Get observations (entities related with defects)
		try {
//			trainComplaints = MacComplaintExtractorLDASeparateCSV.extractComplaints(configMap);
			trainComplaints = PatientComplaintExtractorLDASeparateCSV.extractComplaints(configMap);

		} catch (ParseException e) {
			System.err.println("Exception happens during retrieving complaint from DB. Program exists...");
			e.printStackTrace();
			return;
		}
		
		if (trainComplaints.size() < 1){
			System.err.println("FAIL to retrieve any complaint samples! \nSystem exit");
			return;
		}
		
		int i=0;
		for(Complaint complaint : trainComplaints){
			System.out.println("\nComplaint [" + i + "]:");
			complaint.print();
			i++;
		}
		
		int trainComplaintNum = trainComplaints.size();
		System.out.println("\n[" + trainComplaintNum + "] training complaints collected");
		String resultPath = PathConfig.LdaResultsPath;
		
		//Read configuration
		Modelparameters ldaparameters = new Modelparameters();
		getParametersFromFile(ldaparameters, configMap);
		
		// *** STEP 2: Read complaints as LDA documents
		LDADocumentSet docSet = new LDADocumentSet();
		docSet.readDocsFromDBInWordGroup(trainComplaints);

		//Setup workspace
		FileUtils.mkdir(new File(resultPath));

		// *** STEP 3: Create and run LDA model
		DefectLDADependentTopicsForWordGroupTreeStyleTFIDF model = new DefectLDADependentTopicsForWordGroupTreeStyleTFIDF(ldaparameters, trainComplaints);
		
		System.out.println("\n1) Initialize the model ...");
		model.initializeModel(docSet);
		System.out.println("\n2) Learning and Saving the model ...");
		model.inferModel(docSet);
		System.out.println("\n3) Output the final model ...");
		model.saveIteratedModel(ldaparameters.getIteration(), docSet);
		System.out.println("Done!");
		
		veryEnd = new Date();
		System.out.println("Converge at "+ configMap.get("ITERATION") + " iterations in "
				+ DateUtil.calculatePeriod(veryBegin, veryEnd));
	}
}
