/**
 * Generic class for returning more than one value
 * because Java is the wrong tool for this job.
 */
public class ResultPair {
	public int index;
	public double value;
	public ResultPair(int i, double v) {
		this.index = i;
		this.value = v;
	}
	public String toString() {
		return index +"("+ value +")";
	}
}
