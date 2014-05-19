import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * State to state transition matrix.
 */

/**
 * @author Alexander Whillas
 */
public class TransitionMatrix extends ProbabilityMatrix {

	/**
	 * @param filename
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public TransitionMatrix(String filename)
			throws FileNotFoundException, IOException {
		super();
		parse(filename);
		rowStochastic();
	}

	/**
	 * Makes the matrix row stochastic while smoothing at the same time.
	 */
	@Override
	public void rowStochastic() {
		for(int j = 0; j < N; j++) {
			int sum = 0;
			for (int i = 0; i < N; i++)
				sum += T[j][i];
			for (int i = 0; i < N; i++)
				T[j][i] = Math.log((T[j][i] + 1.0) / (sum + N - 1));
		}
		for(int i = 0; i < N; i++) {
			// Nothing transitions to BEGIN
			T[i][getBeginIndex()] = inf;
			// Nothing is transitions from END
			T[getEndIndex()][i] = inf;
		}
	}
	
	public int getBeginIndex() {
		return Names.indexOf("BEGIN");
	}
	
	public int getEndIndex() {
		return Names.indexOf("END");
	}

	@Override
	public void show() {
		print2ddouble("A", T);
	}

	@Override
	public int getWidth() {
		return N;
	}

	@Override
	public int getHeight() {
		return N;
	}
}
