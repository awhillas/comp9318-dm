import java.util.ArrayList;

/**
 * Object for storing a solution i.e. a state path and its corresponding Log Probability
 * Handles the comparison of solution paths as per 3.3.1 of the spec.
 */
public class Solution {
	protected ArrayList<ResultPair> path;
	
	public Solution(ArrayList<ResultPair> p) {
		this.path = p;
	}

	public double getLogProb() {
		double out = 0.0;
		for (ResultPair rp : path)
			out += rp.value;
		return out;
	}
	
	public String getPath() {
		String key = "";
		for(ResultPair p : path)
			key += p.index + ",";
		key += "4";	// add the END state
		return key;
	}
	
	public int compareTo(Solution s) {
		for(int i = s.path.size() - 1; i > 0; i--) {
			//System.out.format("s1:%d s2:%d%n", this.path.get(i).index, s.path.get(i).index);
			if(s.path.get(i).index > this.path.get(i).index) return -1;
			if(s.path.get(i).index < this.path.get(i).index) return 1;
		}
		return 0;
	}
	
	public String toString() {
		return getPath() + " " + getLogProb();
	}
	
	public static void main(String[] args) {
		// Testing compareTo as per 3.3.1 in the spec.
		ArrayList<ResultPair> a1 = new ArrayList<ResultPair>();
		a1.add(new ResultPair(1, 0.0));
		a1.add(new ResultPair(2, 0.0));
		a1.add(new ResultPair(3, 0.0));
		a1.add(new ResultPair(4, 0.0));
		Solution s1 = new Solution(a1);

		ArrayList<ResultPair> a2 = new ArrayList<ResultPair>();
		a2.add(new ResultPair(2, 0.0));
		a2.add(new ResultPair(1, 0.0));
		a2.add(new ResultPair(3, 0.0));
		a2.add(new ResultPair(4, 0.0));
		Solution s2 = new Solution(a2);
		
		if(s1.compareTo(s2) == 1) {
			System.out.println("s1 is greater!");
		}
		else if (s1.compareTo(s2) == -1) {
			System.out.println("s1 is smaller!");
		}
		else {
			System.out.println("They are equal?");
		}
	}
}
