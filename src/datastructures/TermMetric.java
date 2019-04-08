package datastructures;

public class TermMetric {
	private String term;
	private double metric;
	
	public TermMetric(String term, double metric) {
		super();
		this.term = term;
		this.metric = metric;
	}
	

	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public double getMetric() {
		return metric;
	}
	public void setMetric(double metric) {
		this.metric = metric;
	}
	public String toString(){
		return "<Term = " + this.term + ", Metric = " + this.metric + ">"; 
	}
}
