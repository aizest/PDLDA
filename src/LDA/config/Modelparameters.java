package LDA.config;

public class Modelparameters {
	private float alpha = 0.5f; //usual value is 50 / K
	private float beta = 0.1f;//usual value is 0.1
	private float gamma = 0.1f;//usual value is 0.1
	private float delta = 0.1f;//usual value is 0.1
	private float epsilon = 0.01f;
	private float mu = 0.4f;
	
	private int compTopicNum = 10;
	private int sympTopicNum = 10;
	private int resoTopicNum = 10;
	private float compWordMinProb = 0.2f;
	
	private int compWordNum = 10;
	private int sympWordNum = 10;
	private int resoWordNum = 10;

	private int iteration = 100;
	private int saveStep = 10;
	private int beginSaveIters = 50;
	
	private int THREAD_NUM = 10;
	

	public float getAlpha() {
		return alpha;
	}
	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}
	public float getBeta() {
		return beta;
	}
	public void setBeta(float beta) {
		this.beta = beta;
	}
	public float getGamma() {
		return gamma;
	}
	public void setGamma(float gamma) {
		this.gamma = gamma;
	}
	public float getDelta() {
		return delta;
	}
	public void setDelta(float delta) {
		this.delta = delta;
	}
	public float getEpsilon() {
		return epsilon;
	}
	public void setEpsilon(float epsilon) {
		this.epsilon = epsilon;
	}
	public float getMu() {
		return mu;
	}
	public void setMu(float mu) {
		this.mu = mu;
	}
	public int getCompTopicNum() {
		return compTopicNum;
	}
	public int getSympTopicNum() {
		return sympTopicNum;
	}
	public void setSympTopicNum(int sympTopicNum) {
		this.sympTopicNum = sympTopicNum;
	}
	public int getResoTopicNum() {
		return resoTopicNum;
	}
	public void setResoTopicNum(int resoTopicNum) {
		this.resoTopicNum = resoTopicNum;
	}
	public float getCompWordMinProb() {
		return compWordMinProb;
	}
	public void setCompWordMinProb(float compWordMinProb) {
		this.compWordMinProb = compWordMinProb;
	}
	public int getCompWordNum() {
		return compWordNum;
	}
	public void setCompWordNum(int compWordNum) {
		this.compWordNum = compWordNum;
	}
	public int getSympWordNum() {
		return sympWordNum;
	}
	public void setSympWordNum(int sympWordNum) {
		this.sympWordNum = sympWordNum;
	}
	public int getResoWordNum() {
		return resoWordNum;
	}
	public void setResoWordNum(int resoWordNum) {
		this.resoWordNum = resoWordNum;
	}
	public void setCompTopicNum(int topicNum) {
		this.compTopicNum = topicNum;
	}
	
	public int getIteration() {
		return iteration;
	}
	public void setIteration(int iteration) {
		this.iteration = iteration;
	}
	public int getSaveStep() {
		return saveStep;
	}
	public void setSaveStep(int saveStep) {
		this.saveStep = saveStep;
	}
	public int getBeginSaveIters() {
		return beginSaveIters;
	}
	public void setBeginSaveIters(int beginSaveIters) {
		this.beginSaveIters = beginSaveIters;
	}
	public int getTHREAD_NUM() {
		return THREAD_NUM;
	}
	public void setTHREAD_NUM(int tHREAD_NUM) {
		THREAD_NUM = tHREAD_NUM;
	}
}
