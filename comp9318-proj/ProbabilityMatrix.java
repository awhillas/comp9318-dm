import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Manage processing the input files.
 */

/**
 * @author Alexander Whillas
 */
public abstract class ProbabilityMatrix implements MatrixFromFile {
	/**
	 * Number of states/symbols
	 */
	public int N;
	
	/**
	 * Human readable names of the states/symbols
	 */
	protected List<String> Names;
	
	/**
	 * 2D Transition matrix. Where row index is 'from' state and column index is 'to' state/symbol.
	 */
	double[][] T;
	
	double inf = Double.POSITIVE_INFINITY * -1;
	
	/**
	 * Constructor.
	 * 
	 * @param filename	File with the transition tallies.
	 * @param y			The height of the transition matrix. This is used by the emission matrix as its dependent on the number of states.
	 * 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public ProbabilityMatrix()
			throws FileNotFoundException, IOException {
		Names = new ArrayList<String>();
	}
	
	public double get(int from, int to) {
		return T[from][to];
	}
	
	/**
	 * Reads in the number of states/symbols, their names and transition counts.
	 * 
	 * @param file_name
	 * @param dimension_y
	 * 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	protected void parse(String filename) 
			throws IOException, FileNotFoundException {
		String line = "";
		BufferedReader br = new BufferedReader(new FileReader(filename));
		// First line says how many lines there are
		try {
			N = Integer.parseInt(br.readLine());
		} catch (NumberFormatException e) {
			System.out.println("Bad integer :(");
		}
		// Read state/symbol names.		
		for (int i = 0; i < N; i++ ) {
			if ((line = br.readLine()) != null) {
				Names.add(line);
			}
		}
		T = new double[getHeight()][getWidth()];
		// Read in the probabilities
		while ((line = br.readLine()) != null) {
			String[] p = line.split(" ");
			int from = Integer.parseInt(p[0]);
			int to = Integer.parseInt(p[1]);
			int count = Integer.parseInt(p[2]);
			T[from][to] = count;
		}
		br.close();
	}

	protected void print2ddouble(String comment, double[][] in) {
		System.out.println("[" + comment);
		for(double[] row : in) {
			System.out.print("[ ");
			for(double col : row) {
				System.out.format(" %.6f ", col);
			}
			System.out.println(" ]");
		}
		System.out.println("]");
	}	
}