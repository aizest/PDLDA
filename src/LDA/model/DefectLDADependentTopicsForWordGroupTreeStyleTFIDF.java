package LDA.model;

/** Class for PDLDA model, which extract COMPONENT, SYMPTOM, and RESOLUTION topics from online posts.
 * This model takes word groups (3 entities extracted from each complaint post) as input
 * @author Xuan Zhang
 */

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import LDA.config.Modelparameters;
import LDA.config.PathConfig;
import LDA.datastructure.LDADocumentSet;
import datastructures.Complaint;
import datastructures.ComplaintEntityType;
import datastructures.TermMetric;
import preprocess.utility.DocumentProcess;
import utility.FileUtils;
import utility.TermMetricComparator;

public class DefectLDADependentTopicsForWordGroupTreeStyleTFIDF {
	public static final int REPRESENTATIVE_NUM = 10;
	public static final double DEPENDENT_PROB_THRESHOLD = 0.1;
	private int Vc, Vs, Vr; //component vocabulary size, symptom vocabulary size, resolution vocabulary size
	private int Kc, Ks, Kr, M; //component topic number, symptom topic number, resolution topic number, document number
	
	private List<Complaint> trainComplaints;
	private int[] mostRelevantTopicOfComplaints = null;
	
	private int [][] compDocWords; //word index array for component words
	private int [][] sympDocWords; //word index array for symptom words
	private int [][] resoDocWords; //word index array for resolution words
	private int [][] z; //component topic label array. [Dimension]: M * Nc
	private int [][] y; //symptom topic label array. [Dimension]: M * Ns
	private int [][] x; //resolution topic label array. [Dimension]: M * Nr
	
	private float alpha; //Dirichlet prior parameter for doc-topic  
	private float delta; //component topic-word Dirichlet prior parameter
	private float beta; //symptom topic-word Dirichlet prior parameter
	private float gamma; //resolution topic-word Dirichlet prior parameter
	private float epsilon; //Dirichlet prior parameter for topic dependence
	
	private int [][] ndk; //given document d, count times of component topic k. [Dimension]: M * Kc
	private int [][] nkl; //the number of symptom words assigned with symptom topic l and whose corresponding component topic is k. [Dimension]: Kc * Ks
	private int [][] nkm; //the number of resolution words assigned with resolution topic m and whose corresponding component topic is k. [Dimension]: Kc * Kr
	private int [][] nktc; //given component topic k, count times of term tc. [Dimension]: Kc * Vc
	private int [][] nlts; //given symptom topic l, count times of term ts. [Dimension]: Ks * Vs
	private int [][] nmtr; //given resolution topic m, count times of term tr. [Dimension]: Kr * Vr
	
	private int [] dependedCompTopics; //the depended component topic of each document. [Dimension]: M 
	
	private int [] ndkSum; //Sum for each row in ndk. [Dimension]: M
	private int [] nklSum; //Sum for each row in nkl. [Dimension]: Kc
	private int [] nkmSum; //Sum for each row in nkm. [Dimension]: Kc
	private int [] nktcSum; //Sum for each row in nktc. [Dimension]: Kc
	private int [] nltsSum; //Sum for each row in nlts. [Dimension]: Ks
	private int [] nmtrSum; //Sum for each row in nmtr. [Dimension]: Kr
	
	private double [][] phi; //Parameters for Component Topic - Word Multinomial distribution. [Dimension]: Kc * Vc
	private double [][] psi; //Parameters for Symptom Topic - Word Multinomial distribution. [Dimension]: Ks * Vs
	private double [][] tau; //Parameters for Resolution Topic - Word Multinomial distribution. [Dimension]: Kr * Vr
	private double [][] theta; //Parameters for Document - Component Topic Multinomial distribution. [Dimension]: M * Kc
	private double [][] eta; //Parameters for Component Topic - Symptom Topic Multinomial distribution. [Dimension]: Kc * Ks
	private double [][] pi; //Parameters for Component Topic - Resolution Topic Multinomial distribution. [Dimension]: Kc * Kr
	
	private int compWordNumForDisplay; //Number of component words to display
	private int sympWordNumForDisplay; //Number of symptom words to display
	private int resoWordNumForDisplay; //Number of symptom words to display
	private double compWordMinProb; //The minimum probability threshold for displaying component words
	private int iterations; //Times of iterations
	private int saveStep; //The number of iterations between two saving
	private int beginSaveIters; //Begin save model at this iteration
	
	public DefectLDADependentTopicsForWordGroupTreeStyleTFIDF(Modelparameters modelparam, List<Complaint> trainComplaints) {
		alpha = modelparam.getAlpha();
		delta = modelparam.getBeta();
		gamma = modelparam.getGamma();
		beta = modelparam.getDelta();
		epsilon = modelparam.getEpsilon();
		
		compWordNumForDisplay = modelparam.getCompWordNum();
		sympWordNumForDisplay = modelparam.getSympWordNum();
		resoWordNumForDisplay = modelparam.getResoWordNum();
		compWordMinProb = modelparam.getCompWordMinProb();
		
		iterations = modelparam.getIteration();
		Kc = modelparam.getCompTopicNum();
		Ks = modelparam.getSympTopicNum();
		Kr = modelparam.getResoTopicNum();
		saveStep = modelparam.getSaveStep();
		beginSaveIters = modelparam.getBeginSaveIters();
		
		this.trainComplaints = trainComplaints;
		
		System.out.println("\nCalculate TFIDF for each entity...");
		DocumentProcess.calculateComplaintEntityTFIDF(trainComplaints); // Calculate TF-IDF for each type of entity
		System.out.println("\nNormalize TFIDF for each entity...");
		DocumentProcess.normalizeEntityTFIDF(trainComplaints); // Normalize TF-IDF for each type of entity
		
		this.mostRelevantTopicOfComplaints = new int[this.trainComplaints.size()];
	}

	public void initializeModel(LDADocumentSet docSet) {
		M = docSet.getDocs().size();
		Vc = docSet.getCompTermToIndexMap().size();
		Vs = docSet.getSympTermToIndexMap().size();
		Vr = docSet.getResoTermToIndexMap().size();
		
		ndk = new int [M][Kc];
		nkl = new int [Kc][Ks];
		nkm = new int [Kc][Kr];
		nktc = new int[Kc][Vc];
		nlts = new int[Ks][Vs];
		nmtr = new int[Kr][Vr];
		
		dependedCompTopics = new int[M];
		
		ndkSum = new int[M];
		nklSum = new int[Kc];
		nkmSum = new int[Kc];
		nktcSum = new int[Kc];
		nltsSum = new int[Ks];
		nmtrSum = new int[Kr];
		
		phi = new double[Kc][Vc];
		psi = new double[Ks][Vs];
		tau = new double[Kr][Vr];
		theta = new double[M][Kc];
		eta = new double[Kc][Ks];
		pi = new double[Kc][Kr];
		
		//initialize documents index arrays
		//the 2nd dimension varies for different word types
		this.compDocWords = new int[M][]; 
		this.sympDocWords = new int[M][];
		this.resoDocWords = new int[M][];
		for(int m = 0; m < M; m++){
			//Notice the limit of memory
			int N = docSet.getDocs().get(m).getComponentDocWordIndexs().length;
			this.compDocWords[m] = new int[N];
			for(int n = 0; n < N; n++){
				this.compDocWords[m][n] = docSet.getDocs().get(m).getComponentDocWordIndexs()[n];
			}
			
			N = docSet.getDocs().get(m).getSymptomDocWordIndexes().length;
			this.sympDocWords[m] = new int[N];
			for(int n = 0; n < N; n++){
				this.sympDocWords[m][n] = docSet.getDocs().get(m).getSymptomDocWordIndexes()[n];
			}
			
			N = docSet.getDocs().get(m).getResolutionDocWordIndexes().length;
			this.resoDocWords[m] = new int[N];
			for(int n = 0; n < N; n++){
				this.resoDocWords[m][n] = docSet.getDocs().get(m).getResolutionDocWordIndexes()[n];
			}
		}
		
		//initialize topic label z, y, and x 
		//for each component word, symptom word, and resolution word respectively
		z = new int[M][];
		y = new int[M][];
		x = new int[M][];
		for(int m = 0; m < M; m++){
			int N = docSet.getDocs().get(m).getComponentDocWordIndexs().length;
			z[m] = new int[N];
			for(int n = 0; n < N; n++){
				int initTopicC = (int)(Math.random() * Kc);// Sample a component topic from 0 to K - 1
				z[m][n] = initTopicC;
				//number of component words in doc m assigned to component topic initTopic add 1
				ndk[m][initTopicC]++;
				//number of component terms doc[m][n] assigned to component topic initTopic add 1
				nktc[initTopicC][compDocWords[m][n]]++;
				//total number of component words assigned to topic initTopic add 1
				nktcSum[initTopicC]++;
			}
			// total number of words in document m is N
			ndkSum[m] = N;
			// sample an dependent topic from component topics assigned to above N words, for symptom words and resolution words
			int dependedCompTopic = sampleDependedTopicRandomply(z[m]);
			// Update the dependedCompTopics array
			dependedCompTopics[m] = dependedCompTopic;
			
			N = docSet.getDocs().get(m).getSymptomDocWordIndexes().length;
			y[m] = new int[N];
			for(int n = 0; n < N; n++){
				int initTopicS = (int)(Math.random() * Ks);// Sample a symptom topic from 0 to K - 1
				y[m][n] = initTopicS;
				//number of symptom terms doc[m][n] assigned to symptom topic initTopicS add 1
				nlts[initTopicS][sympDocWords[m][n]]++;
				//total number of symptom words assigned to topic initTopicS add 1
				nltsSum[initTopicS]++;
				
				//number of symptom words add 1. They are assigned with symptom topic initTopicS and their corresponding component topic is dependedCompTopic 
				nkl[dependedCompTopic][initTopicS]++;
				//total number of symptom words corresponding to dependedCompTopic add 1
				nklSum[dependedCompTopic]++;
			}
			
			N = docSet.getDocs().get(m).getResolutionDocWordIndexes().length;
			x[m] = new int[N];
			for(int n = 0; n < N; n++){
				int initTopicR = (int)(Math.random() * Kr);// Sample a resolution topic from 0 to K - 1
				x[m][n] = initTopicR;
				//number of resolution terms doc[m][n] assigned to resolution topic initTopicR add 1
				nmtr[initTopicR][resoDocWords[m][n]]++;
				// total number of resolution words assigned to topic initTopicR add 1
				nmtrSum[initTopicR]++;
				
				//number of resolution words add 1. They are assigned with resolution topic initTopicS and their corresponding component topic is dependedCompTopic 
				nkm[dependedCompTopic][initTopicR]++;
				//total number of resolution words corresponding to dependedCompTopic add 1
				nkmSum[dependedCompTopic]++;
			}
		}
	}
	

	/**
	 * Sample a component topic from the component topic array for symptom words and resolution words
	 * 
	 * @param topics
	 * @return
	 */
	public int sampleDependedTopicRandomply(int topics[]){
		int index = (int)(Math.random() * topics.length);
		
		return topics[index];
	}
	

	public void inferModel(LDADocumentSet docSet) throws IOException {
		if(iterations < saveStep + beginSaveIters){
			System.err.println("Error: the number of iterations should be larger than " + (saveStep + beginSaveIters));
			System.exit(0);
		}
		
		for(int i = 0; i < iterations; i++){
			System.out.println("Iteration " + i);
			if((i >= beginSaveIters) && (((i - beginSaveIters) % saveStep) == 0)){
				//Saving the model
				System.out.println("Saving model at iteration " + i +" ... ");
				//Firstly update parameters
				updateEstimatedParameters();
				//Secondly print model variables
				saveIteratedModel(i, docSet);
			}
			
			//Use Gibbs Sampling to update z[][], y[][], and x[][]
			for(int d = 0; d < M; d++){
				// Get the component topic PREVIOUSLY depended on by symptom words and resolution words of document d
				int oldDependedTopic = dependedCompTopics[d];
				
				int N = docSet.getDocs().get(d).getComponentDocWordIndexs().length;
				for(int n = 0; n < N; n++){
					// Sample from p(z_i|z_-i, w)
					int newTopic = sampleComponentTopic(d, n);
					z[d][n] = newTopic;
				}
				
				// Sample an depended topic from component topics NEWLY assigned to above N component words, for symptom words and resolution words
				int newDependedTopic = sampleDependedTopicRandomply(z[d]);
				// Update the dependedCompTopics array after sampling new depended component topic
				dependedCompTopics[d] = newDependedTopic;
				
				N = docSet.getDocs().get(d).getSymptomDocWordIndexes().length;
				for(int n = 0; n < N; n++){
					// Sample from p(y_i|z_i=k, y_-i, w)
					int newTopic = sampleSymptomTopic(d, n, oldDependedTopic, newDependedTopic);
					y[d][n] = newTopic;
				}
				
				N = docSet.getDocs().get(d).getResolutionDocWordIndexes().length;
				for(int n = 0; n < N; n++){
					// Sample from p(x_i|z_i=k, x_-i, w)
					int newTopic = sampleResolutionTopic(d, n, oldDependedTopic, newDependedTopic);
					x[d][n] = newTopic;
				}
			}
		}
	}
	
	private void updateEstimatedParameters() {
		for(int k = 0; k < Kc; k++){
			for(int t = 0; t < Vc; t++){
				phi[k][t] = (nktc[k][t] + delta) / (nktcSum[k] + Vc * delta);
			}
		}
		
		for(int l = 0; l < Ks; l++){
			for(int t = 0; t < Vs; t++){
				psi[l][t] = (nlts[l][t] + beta) / (nltsSum[l] + Vs * beta);
			}
		}
		
		for(int m = 0; m < Kr; m++){
			for(int t = 0; t < Vr; t++){
				tau[m][t] = (nmtr[m][t] + gamma) / (nmtrSum[m] + Vr * gamma);
			}
		}
		
		for(int m = 0; m < M; m++){
			for(int k = 0; k < Kc; k++){
				theta[m][k] = (ndk[m][k] + alpha) / (ndkSum[m] + Kc * alpha);
			}
		}
		
		for(int k = 0; k < Kc; k++){
			for(int l = 0; l < Ks; l++){
				eta[k][l] = (nkl[k][l] + epsilon) / (nklSum[k] + Ks * epsilon);
			}
		}
		
		for(int k = 0; k < Kc; k++){
			for(int m = 0; m < Kr; m++){
				pi[k][m] = (nkm[k][m] + epsilon) / (nkmSum[k] + Kr * epsilon);
			}
		}
	}

	/**
	 * Sample from p(z_i=k|z_-i, w) using Gibbs update rule
	 * 
	 * @param d
	 * @param n
	 * @return
	 */
	private int sampleComponentTopic(int d, int n) {
		//Remove topic label for w_{m,n}
		int oldTopicC = z[d][n];

		ndk[d][oldTopicC]--;
		nktc[oldTopicC][compDocWords[d][n]]--;
		ndkSum[d]--;
		nktcSum[oldTopicC]--;
		
		//Compute p(z_i=k|z_-i, w)
		double [] p = new double[Kc];
		//2-level loop for 2 latent variables case, and 3-level loop for 3 latent variables case
		for(int k = 0; k < Kc; k++){
			p[k] = (nktc[k][compDocWords[d][n]] + delta) / (nktcSum[k] + Vc * delta)
				* (ndk[d][k] + alpha) / (ndkSum[d] + Kc * alpha);
		}
		
		//Sample two new topic labels for w_{m, n} like roulette
		//Compute accumulated probability for p
		for(int k = 1; k < Kc; k++){
			p[k] += p[k - 1];
		}
		double u = Math.random() * p[Kc - 1]; //p[] is unnormalized
		int newTopic;
		for(newTopic = 0; newTopic < Kc; newTopic++){
			if(u < p[newTopic]){
				break;
			}
		}
		
		//Add new topic labels for w_{m, n}, update the related matrices
		ndk[d][newTopic]++;
		nktc[newTopic][compDocWords[d][n]]++;
		ndkSum[d]++;
		nktcSum[newTopic]++;
		
		return newTopic;
	}
	
	/**
	 * Sample from p(y_i=l|z_i=k, y_-i, w) using Gibbs update rule
	 * 
	 * @param d the document id
	 * @param n the word id
	 * @return
	 */
	private int sampleSymptomTopic(int d, int n, int oldDependedTopic, int newDependedTopic) {
		int newTopic;
		
		//Remove topic label for w_{m,n}
		int oldTopicC = oldDependedTopic;
		int oldTopicS = y[d][n];

		nkl[oldTopicC][oldTopicS]--;
		nlts[oldTopicS][sympDocWords[d][n]]--;
		nklSum[oldTopicC]--;
		nltsSum[oldTopicS]--;
		
		//Assume Topic[0] is a general (background) topic, assign the general topic or one of the regular topics,
		//according to the TF-IDF value of this word
		int isGeneral = -1;
		double[] gProbs = new double[2];
		//The normalized TF/IDF value, indicating the probability to be a regular/general topic word
		//A small value may indicate general topic word, a large value may mean a regular topic word
		gProbs[0] = trainComplaints.get(d).getSmokeWordTMs().get(n).getMetric();
		gProbs[1] = 1;
		double v = Math.random();
		// Sample a type (general or regular) for w_{d, n} like roulette
		for (isGeneral = 0; isGeneral <= 1; isGeneral++){
			if ( v < gProbs[isGeneral] || isGeneral == 1)
				break;
		}
		
		if (isGeneral == 1)
			newTopic = 0;
		else {

			// Compute p(y_i=l|z_i=k, y_-i, w)
			double[] p = new double[Ks];
			p[0] = 0;
			// 2-level loop for 2 latent variables case, and 3-level loop
			// for 3 latent variables case
			for (int l = 1; l < Ks; l++) {//Skip p[0]
				p[l] = (nlts[l][sympDocWords[d][n]] + beta) / (nltsSum[l] + Vs * beta) // Newly added
						* (nkl[newDependedTopic][l] + epsilon) / (nklSum[newDependedTopic] + Ks * epsilon);
			}

			// Sample new topic label for w_{d, n} like roulette
			// Compute accumulated probability for p
			for (int l = 1; l < Ks; l++) {
				p[l] += p[l - 1];
			}

			double u = Math.random() * p[Ks - 1]; // p[] is unnormalized

			for (newTopic = 1; newTopic < Ks; newTopic++) {
				if (u < p[newTopic] || newTopic == Ks - 1) {
					break;
				}
			}
		}
		
		//Add new topic labels for w_{m, n}, update the related matrices
		nkl[newDependedTopic][newTopic]++;
		nlts[newTopic][sympDocWords[d][n]]++;
		nklSum[newDependedTopic]++;
		nltsSum[newTopic]++;
		
		return newTopic;
	}
	
	/**
	 * Sample from p(x_i=m|x_-i, z_i=k, w) using Gibbs update rule
	 * 
	 * @param d the document id
	 * @param n the word id
	 * @return
	 */
	private int sampleResolutionTopic(int d, int n, int oldDependedTopic, int newDependedTopic) {
		int newTopic;
		
		//Remove topic label for w_{m,n}
		int oldTopicC = oldDependedTopic;
		int oldTopicR = x[d][n];

		nkm[oldTopicC][oldTopicR]--;
		nmtr[oldTopicR][resoDocWords[d][n]]--;
		nkmSum[oldTopicC]--;
		nmtrSum[oldTopicR]--;
		
		//Assume Topic[0] is a general (background) topic, assign the general topic or one of the regular topics,
		//according to the TF-IDF value of this word
		int isGeneral = -1;
		double[] gProbs = new double[2];
		//The normalized TF/IDF value, indicating the probability to be a regular/general topic word
		//A small value may indicate general topic word, a large value may mean a regular topic word
		gProbs[0] = trainComplaints.get(d).getResolutionWordTMs().get(n).getMetric();
		gProbs[1] = 1;
		double v = Math.random();
		for (isGeneral = 0; isGeneral <= 1; isGeneral++){
			if ( v < gProbs[isGeneral] || isGeneral == 1)
				break;
		}
		
		if (isGeneral == 1)
			newTopic = 0;
		else {

			// Compute p(x_i=m|x_-i, z_i=k, w)
			double[] p = new double[Kr];
			p[0] = 0;
			// 2-level loop for 2 latent variables case, and 3-level loop
			// for 3 latent variables case
			for (int m = 1; m < Kr; m++) {//Skip p[0]
				p[m] = (nmtr[m][resoDocWords[d][n]] + gamma) / (nmtrSum[m] + Vr * gamma) // Newly added
						* (nkm[newDependedTopic][m] + epsilon) / (nkmSum[newDependedTopic] + Kr * epsilon);
			}

			// Sample new topic label for w_{m, n} like roulette
			// Compute accumulated probability for p
			for (int m = 1; m < Kr; m++) {
				p[m] += p[m - 1];
			}

			double u = Math.random() * p[Kr - 1]; // p[] is unnormalized

			for (newTopic = 0; newTopic < Kr; newTopic++) {
				if (u < p[newTopic] || newTopic == Kr - 1) {
					break;
				}
			}
		}
		
		//Add new topic labels for w_{m, n}, update the related matrices
		nkm[newDependedTopic][newTopic]++;
		nmtr[newTopic][resoDocWords[d][n]]++;
		nkmSum[newDependedTopic]++;
		nmtrSum[newTopic]++;
		
		return newTopic;
	}
	
	
	public void saveIteratedModel(int iters, LDADocumentSet docSet) throws IOException {
		//lda.params lda.phi lda.theta lda.tassign lda.twords
		//lda.params
		String resPath = PathConfig.LdaResultsPath;
		String modelName = "lda_" + iters;
		ArrayList<String> lines = new ArrayList<String>();
		lines.add("alpha = " + alpha);
		lines.add("delta = " + delta);
		lines.add("beta = " + beta);
		lines.add("gamma = " + gamma);
		lines.add("epsilon = " + epsilon);
		lines.add("Component Topic Num = " + Kc);
		lines.add("Symptom Topic Num = " + Ks);
		lines.add("Resolution Topic Num = " + Kr);
		lines.add("docNum = " + M);
		lines.add("Component TermNum = " + Vc);
		lines.add("Symptom TermNum = " + Vs);
		lines.add("Resolution TermNum = " + Vr);
		lines.add("iterations = " + iterations);
		lines.add("saveStep = " + saveStep);
		lines.add("beginSaveIters = " + beginSaveIters);
		FileUtils.writeLines(resPath + modelName + ".params", lines);
		DecimalFormat df2 = new DecimalFormat("0.00");
		DecimalFormat df4 = new DecimalFormat("0.0000");
		
		//lda.phi Kc * Vc
		BufferedWriter writer = new BufferedWriter(new FileWriter(resPath + modelName + ".phi"));		
		for (int i = 0; i < Kc; i++){
			for (int j = 0; j < Vc; j++){
				writer.write(df4.format(phi[i][j]) + "\t");
			}
			writer.write("\n");
		}
		writer.close();
		
		//lda.psi Ks * Vs
		writer = new BufferedWriter(
				new FileWriter(resPath + modelName + ".psi"));
		for (int i = 0; i < Ks; i++) {
			for (int j = 0; j < Vs; j++) {
				writer.write(df4.format(psi[i][j]) + "\t");
			}
			writer.write("\n");
		}
		writer.close();
		
		// lda.tau Kr * Vr
		writer = new BufferedWriter(
				new FileWriter(resPath + modelName + ".tau"));
		for (int i = 0; i < Kr; i++) {
			for (int j = 0; j < Vr; j++) {
				writer.write(df4.format(tau[i][j]) + "\t");
			}
			writer.write("\n");
		}
		writer.close();
		
		//lda.theta M * K
		writer = new BufferedWriter(new FileWriter(resPath + modelName + ".theta"));
		for(int i = 0; i < M; i++){
			for(int j = 0; j < Kc; j++){
				writer.write(df4.format(theta[i][j]) + "\t");
			}
			writer.write("\n");
		}
		writer.close();
		
		// lda.eta Kc * Ks
		writer = new BufferedWriter(new FileWriter(resPath + modelName
				+ ".eta"));
		for (int i = 0; i < Kc; i++) {
			for (int j = 0; j < Ks; j++) {
				writer.write(df4.format(eta[i][j]) + "\t");
			}
			writer.write("CT-" + String.format("%03d", i));
			writer.write("\n");
		}
		//TODO: Print the symptom topic IDs, for debug purpose.
		for (int j = 0; j < Ks; j++) {
			writer.write("ST-" + j + "\t");
		}
		writer.write("\n");
		for (int j = 0; j < Ks; j++) {
			String tType = "  ";
			if (isGeneralTopic(j, ComplaintEntityType.SYMPTOM))
				tType = "GT";
			writer.write(tType + "\t\t");
		}
		writer.close();
		
		// lda.pi Kc * Kr
		writer = new BufferedWriter(
				new FileWriter(resPath + modelName + ".pi"));
		for (int i = 0; i < Kc; i++) {
			for (int j = 0; j < Kr; j++) {
				writer.write(df4.format(pi[i][j]) + "\t");
			}
			writer.write("CT-" + String.format("%03d", i));
			writer.write("\n");
		}
		//TODO: Print the resolution topic IDs, for debug purpose.
		for (int j = 0; j < Kr; j++) {
			writer.write("RT-" + j + "\t");
		}
		writer.write("\n");
		for (int j = 0; j < Kr; j++) {
			String tType = "  ";
			if (isGeneralTopic(j, ComplaintEntityType.RESOLUTION))
				tType = "GT";
			writer.write(tType + "\t\t");
		}
		writer.close();
		
		//lda.tassignC (component topic assignment) 
		writer = new BufferedWriter(new FileWriter(resPath + modelName + ".tassignC"));
		for (int m = 0; m < M; m++) {
			for (int n = 0; n < compDocWords[m].length; n++) {
				String term = docSet.getCompIndexToTermList().get(compDocWords[m][n]);
				writer.write(term + ":" + z[m][n] + "\t");
			}
			writer.write("\n");
		}
		writer.close();

		// lda.tassignS (symptom topic assignment)
		writer = new BufferedWriter(new FileWriter(resPath + modelName + ".tassignS"));
		for (int m = 0; m < M; m++) {
			for (int n = 0; n < sympDocWords[m].length; n++) {
				String term = docSet.getSympIndexToTermList().get(sympDocWords[m][n]);
				writer.write(term + ":" + y[m][n] + "\t");
			}
			writer.write("\n");
		}
		writer.close();

		// lda.tassignR (resolution topic assignment)
		writer = new BufferedWriter(new FileWriter(resPath + modelName + ".tassignR"));
		for (int m = 0; m < M; m++) {
			for (int n = 0; n < resoDocWords[m].length; n++) {
				String term = docSet.getResoIndexToTermList().get(resoDocWords[m][n]);
				writer.write(term + ":" + x[m][n] + "\t");
			}
			writer.write("\n");
		}
		writer.close();
		
		//lda.twords phi[][] K * Vc and psi[][] K * Vs and representative complaints
		writer = new BufferedWriter(new FileWriter(resPath + modelName + ".tWords"));
		for(int i=0; i<this.trainComplaints.size(); i++){
			mostRelevantTopicOfComplaints[i] = findMostRelevantComponentTopicOfComplaint(i);
		}
		

		//Sort component topics by probability
		TermMetric[] sortedCompTopics = sortComponentTopicsByProbability();
		for(int i = Kc-1; i >= 0; i--){

			int trueCompIndex = Integer.parseInt(sortedCompTopics[i].getTerm());
			double compTopicProb = sortedCompTopics[i].getMetric();

			List<Integer> tCompWordsIndexArray = new ArrayList<Integer>(); 
			for(int j = 0; j < Vc; j++){
				tCompWordsIndexArray.add(new Integer(j));
			}
			Collections.sort(tCompWordsIndexArray, new DefectLDADependentTopicsForWordGroupTreeStyleTFIDF.TwordsComparable(phi[trueCompIndex]));
			
			//Print the component topic 
			writer.write("----------------------------------------------\n");
			writer.write("\nComponent Topic [" + trueCompIndex + "] Probability:\t" + df2.format(100 * compTopicProb) + "%\n");
			writer.write("\nComponent Topic [" + trueCompIndex + "] Words:\n");
			//Find the top topic component words in each topic
			for(int t = 0; t < compWordNumForDisplay; t++){
				double wordProb = phi[trueCompIndex][tCompWordsIndexArray.get(t)];
				if (wordProb >= compWordMinProb){
					String word = docSet.getCompIndexToTermList().get(tCompWordsIndexArray.get(t));
					String prob = df2.format(wordProb * 100);
					
					writer.write("\t" + word + "\t\t\t\t" + prob + "%\n");
				}
			}
			writer.write("\n");
			
			//Print the dependent symptom topics
			List<TermMetric> dependentSympTopics = getDependentSymptomTopics(trueCompIndex);
			writer.write("\tDependent SYMPTOM Topics:\n");
			for(TermMetric tm : dependentSympTopics){
				int sympIndex = Integer.parseInt(tm.getTerm());
				
				List<Integer> tSympWordsIndexArray = new ArrayList<Integer>(); 
				for(int j = 0; j < Vs; j++){
					tSympWordsIndexArray.add(new Integer(j));
				}
				Collections.sort(tSympWordsIndexArray, new DefectLDADependentTopicsForWordGroupTreeStyleTFIDF.TwordsComparable(psi[sympIndex]));
				writer.write("\t\tSymptom Topic [" + sympIndex + "] Probability:\t" + df2.format(100 * tm.getMetric()) + "%\n");
				writer.write("\t\tSymptom Words:\n");
				//Find the top topic symptom words in each topic
				for(int t = 0; t < sympWordNumForDisplay; t++){
					String word = docSet.getSympIndexToTermList().get(tSympWordsIndexArray.get(t));
					String prob = df2.format(psi[sympIndex][tSympWordsIndexArray.get(t)] * 100);
					
					writer.write("\t\t\t" + word + "\t\t\t\t" + prob + "%\n");
				}
				writer.write("\n");
			}
			
			//Print the dependent resolution topics
			List<TermMetric> dependentResoTopics = getDependentResolutionTopics(trueCompIndex);
			writer.write("\tDependent RESOLUTION Topics:\n");
			for(TermMetric tm : dependentResoTopics){//Print key words of each resolution topic
				int resoIndex = Integer.parseInt(tm.getTerm());
				
				List<Integer> tResoWordsIndexArray = new ArrayList<Integer>(); 
				for(int j = 0; j < Vr; j++){
					tResoWordsIndexArray.add(new Integer(j));
				}
				Collections.sort(tResoWordsIndexArray, new DefectLDADependentTopicsForWordGroupTreeStyleTFIDF.TwordsComparable(tau[resoIndex]));
				writer.write("\t\tResolution Topic [" + resoIndex + "] Probability:\t" + df2.format(100 * tm.getMetric()) + "%\n");
				writer.write("\t\tResolution Words:\n");
				//Find the top topic resolution words in each topic
				for(int t = 0; t < resoWordNumForDisplay; t++){
					String word = docSet.getResoIndexToTermList().get(tResoWordsIndexArray.get(t));
					String prob = df2.format(tau[resoIndex][tResoWordsIndexArray.get(t)] * 100);
					
					writer.write("\t\t\t" + word + "\t\t\t\t" + prob + "%\n");
				}
				writer.write("\n");
			}
		}
		
		writer.write("\nGeneral Symptom Topics:\n");
		System.out.println("\nGeneral Symptom Topics:");
		for(int i=0; i<Ks; i++){
			if(isGeneralTopic(i, ComplaintEntityType.SYMPTOM)){
				printSympTopicWords(i, docSet, writer);
			}
		}
		
		writer.write("\nGeneral Resolution Topics:\n");
		System.out.println("\nGeneral Resolution Topics:");
		for(int i=0; i<Kr; i++){
			if(isGeneralTopic(i, ComplaintEntityType.RESOLUTION)){
				printResoTopicWords(i, docSet,  writer);
			}
		}
		
		writer.close();
		
		System.out.println();
		
	}
	
	private void printSympTopicWords(int index, LDADocumentSet docSet, BufferedWriter writer){
		DecimalFormat df2 = new DecimalFormat("0.00");
		
		List<Integer> tSympWordsIndexArray = new ArrayList<Integer>(); 
		for(int j = 0; j < Vs; j++){
			tSympWordsIndexArray.add(new Integer(j));
		}
		Collections.sort(tSympWordsIndexArray, new DefectLDADependentTopicsForWordGroupTreeStyleTFIDF.TwordsComparable(psi[index]));
		System.out.println("\tSymptom Topic [" + index + "] Words:");
		//Find the top topic symptom words in each topic
		try {
			for(int t = 0; t < sympWordNumForDisplay; t++){
				String word = docSet.getSympIndexToTermList().get(tSympWordsIndexArray.get(t));
				String prob = df2.format(psi[index][tSympWordsIndexArray.get(t)] * 100);
				writer.write("\t\t" + word + "\t\t" + prob + "%\n");
				System.out.println("\t\t" + word + "\t\t" + prob + "%");
				
			}
			System.out.println();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void printResoTopicWords(int index, LDADocumentSet docSet, BufferedWriter writer){
		DecimalFormat df2 = new DecimalFormat("0.00");
		
		List<Integer> tResoWordsIndexArray = new ArrayList<Integer>(); 
		for(int j = 0; j < Vr; j++){
			tResoWordsIndexArray.add(new Integer(j));
		}
		Collections.sort(tResoWordsIndexArray, new DefectLDADependentTopicsForWordGroupTreeStyleTFIDF.TwordsComparable(tau[index]));
		System.out.println("\tResolution Topic [" + index + "] Words:" );
		
		try {
			//Find the top topic resolution words in each topic
			for(int t = 0; t < resoWordNumForDisplay; t++){
				String word = docSet.getResoIndexToTermList().get(tResoWordsIndexArray.get(t));
				String prob = df2.format(tau[index][tResoWordsIndexArray.get(t)] * 100);
				writer.write("\t\t" + word + "\t\t" + prob + "%\n");
				System.out.println("\t\t" + word + "\t\t" + prob + "%");
			}
			writer.write("\n");
			System.out.println();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Check whether the given topic (e.g. a symptom/resolution topic) is a general topic which is quite mixed
	 * 
	 * @param topicIndex
	 * @param entityType
	 * @return
	 */
	private boolean isGeneralTopic(int topicIndex, ComplaintEntityType entityType) {
		
		if(entityType == ComplaintEntityType.SYMPTOM){
			double upLimit = 1.0 / Ks;
			int overLimit = 0;
			for(int i=0; i<Kc; i++){
				if(eta[i][topicIndex] > upLimit)
					overLimit++;
			}
			if(overLimit > (Kc/2+1))
				return true;
			else
				return false;
			
		}else if (entityType == ComplaintEntityType.RESOLUTION){
			double upLimit = 1.0 / Kr;
			int overLimit = 0;
			for(int i=0; i<Kc; i++){
				if(pi[i][topicIndex] > upLimit)
					overLimit++;
			}
			if(overLimit > (Kc/2+1))
				return true;
			else
				return false;
		}
		return false;
	}
	

	/**
	 * Get the dependent symptom topics as an integer list given a component topic
	 * 
	 * @param compTopicID
	 * @return
	 */
	public List<TermMetric> getDependentSymptomTopics(int compTopicID){
		List<TermMetric> result = new ArrayList<TermMetric>();
		TermMetric[] array = new TermMetric[Ks];
		for(int i=0; i<Ks; i++){
			double probability = eta[compTopicID][i];
			//Skip general symptom topics by setting their probability to 0
			if (isGeneralTopic(i, ComplaintEntityType.SYMPTOM))
				probability = 0;
				
			array[i] = new TermMetric(new Integer(i).toString(), probability);
		}
		Arrays.sort(array, new TermMetricComparator<TermMetric>());
		for(int i=Ks-1; i>=0; i--){
			TermMetric tm = array[i];
			if(tm.getMetric() >= DEPENDENT_PROB_THRESHOLD || i == Ks-1){
				result.add(tm);
			}else
				break;
		}
		
		return result;
	}
	
	
	/**
	 * Get the dependent resolution topics as an integer list given a component topic
	 * 
	 * @param compTopicID
	 * @return
	 */
	public List<TermMetric> getDependentResolutionTopics(int compTopicID){
		List<TermMetric> result = new ArrayList<TermMetric>();
		TermMetric[] array = new TermMetric[Kr];
		for(int i=0; i<Kr; i++){
			double probability = pi[compTopicID][i];
			//Skip general resolution topics by setting their probability to 0
			if(isGeneralTopic(i, ComplaintEntityType.RESOLUTION))
				probability = 0;
			
			array[i] = new TermMetric(new Integer(i).toString(), probability);
		}
		Arrays.sort(array, new TermMetricComparator<TermMetric>());
		for(int i=Kr-1; i>=0; i--){
			TermMetric tm = array[i];
			if(tm.getMetric() >= DEPENDENT_PROB_THRESHOLD || i == Kr-1){
				result.add(tm);
			}else
				break;
		}
		
		return result;
	}
	
	
	/**
	 * Sort all the component topics by their probabilities
	 * 
	 * @return
	 */
	public TermMetric[] sortComponentTopicsByProbability(){
		TermMetric[] topicArray = new TermMetric[Kc];
		for(int i=0; i<Kc;i++){
			double probSum = 0.0;
			for(int j=0; j<M; j++){
				probSum += theta[j][i] * 1 / M; //P(T) = Sigma( P(T|d) * P(d) )
			}
			topicArray[i] = new TermMetric(new Integer(i).toString(), probSum);
		}
		Arrays.sort(topicArray, new TermMetricComparator<TermMetric>());
		return topicArray;
	}
	
	/**
	 * Find the most relevant defect of the given complaint, Max(P(Topic | Complaint))
	 * 
	 * @param cmplID
	 * @return
	 */
	public int findMostRelevantComponentTopicOfComplaint(int cmplID){
		double maxValue = 0;
		int maxIndex = -1;
		
		for(int i=0; i<this.theta[cmplID].length; i++){
			double cp = this.theta[cmplID][i];
			if(cp > maxValue){
				maxValue = cp;
				maxIndex = i;
			}
		}
		
		return maxIndex;
	}
	

	public class TwordsComparable implements Comparator<Integer> {
		
		public double [] sortProb; // Store probability of each word in topic k
		
		public TwordsComparable (double[] sortProb){
			this.sortProb = sortProb;
		}

		@Override
		public int compare(Integer o1, Integer o2) {
			//Sort topic word index according to the probability of each word in topic k
			if(sortProb[o1] > sortProb[o2]) return -1;
			else if(sortProb[o1] < sortProb[o2]) return 1;
			else return 0;
		}
	}
	
}
