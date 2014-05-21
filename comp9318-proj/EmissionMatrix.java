import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * State to Emission probabilities
 */

/**
 * @author Alexander Whillas
 */
public class EmissionMatrix extends ProbabilityMatrix {

	private TransitionMatrix A;
	
	/**
	 * @param filename
	 * @param numberOfSates
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public EmissionMatrix(String filename, TransitionMatrix Trans)
			throws FileNotFoundException, IOException {
		super();
		A = Trans;
		parse(filename);
		rowStochastic();
	}

	/**
	 * Convert a query string to an array of indices.
	 * @param q
	 */
	public int[] queryToIndex(String q) {
		String[] O = q.split(" ");
		int[] out = new int[O.length];
		for(int i = 0; i < O.length; i++) {
			out[i] = Names.indexOf(O[i]);
			if(out[i] == -1) {
				out[i] = N;
			}
		}
		return out;
	}

	/**
	 * Makes the matrix row stochastic while smoothing at the same time.
	 */
	@Override
	public void rowStochastic() {
		for(int j = 0; j < getHeight(); j++) {
			int sum = 0;
			for (int i = 0; i < T[j].length; i++)
				sum += T[j][i];
			for (int i = 0; i < T[j].length; i++)
				T[j][i] = Math.log((T[j][i] + 1.0) / (sum + N + 1));
		}
		// Handle BEGIN and END states
		for (int i = 0; i < getWidth(); i++) {
			T[A.getBeginIndex()][i] = inf;
			T[A.getEndIndex()][i] = inf;
		}
	}
	
	@Override
	public void show() {
		print2ddouble("B", T);
	}

	@Override
	public int getWidth() {
		return N + 1;	// Number of symbols
	}

	@Override
	public int getHeight() {
		return A.N;	// number of states
	}
}
