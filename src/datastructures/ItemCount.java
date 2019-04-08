package datastructures;

public class ItemCount {
	private String item;
	private double count;
	
	public ItemCount(String item, double count) {
		super();
		this.item = item;
		this.count = count;
	}
	
	public ItemCount(){
		this.item = null;
		this.count = 0;
	}
	
	
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public double getCount() {
		return count;
	}
	public void setCount(double count) {
		this.count = count;
	}
	
	
	

}
