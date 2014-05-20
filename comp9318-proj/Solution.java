import java.util.ArrayList;


public class Solution {
	public String path;
	public Double logProb;
	
	public Solution(ArrayList<ResultPair> s) {
		double logprob = 0.0;
		String key = "";
		for(ResultPair p : s) {
			logprob += p.value;
			key += p.index + ",";
		}
		key += "4";
		path = key;
		logProb = logprob;
	}
}
