import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Proj1 {

	/**
	 * State to State transitions
	 */
	private TransitionMatrix A;
	/**
	 * State to observation probabilities 
	 */
	private EmissionMatrix  B;
	
	/**
	 * 
	 * @param states_file
	 * @param symbol_file
	 * @param query_file
	 * @param k
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public Proj1(String states_file, String symbol_file, String query_file, int k) throws FileNotFoundException, IOException {
		A = new TransitionMatrix(states_file);
		B = new EmissionMatrix(symbol_file, A);
//		A.show();
//		B.show();
		processQueries(query_file, k);
	}
		
	private void processQueries(String query_filename, int k) throws FileNotFoundException, IOException {
		String line;
		BufferedReader br = new BufferedReader(new FileReader(query_filename));
		while ((line = br.readLine()) != null) {
			System.out.println(line);
			this.query(B.queryToIndex(line), k);
		}
		br.close();
	}
	
	private void query(int[] O, int k) {
		double[][][] values = new double[O.length + 1][A.N][A.N];
		int[][][] paths = new int[O.length + 1][A.N][A.N];
		
		viterbi_procedural(O, values, paths);
		
		print2ddoubleDouble("max_val", values);
		print2dInt2("arg_max", paths);

		// get solutions
		List<ArrayList<ResultPair>> solutions = new ArrayList<ArrayList<ResultPair>>(A.N * A.N * O.length);
		tracePaths(O.length, A.N-1, paths, values, new ArrayList<ResultPair>(), solutions);
		
		getTop(k, solutions);
	}

	
	private Map<String, Double> getTop(int k, List<ArrayList<ResultPair>> solutions) {
		LinkedHashMap top = new LinkedHashMap<String, Double>();
		// Create hash map
		

		// Sort the map
		Collections.sort(top, new Comparator<Map.Entry<String, Double>>() {
			public int compare(Map.Entry<String, Double> a, Map.Entry<String, Double> b) {
				return a.getValue().compareTo(b.getValue());
			}
		});
		Map<String, Double> sortedTop = new LinkedHashMap<String, Double>();
		for (Map.Entry<String, Double> entry : top) {
			sortedTop.put(entry.getKey(), entry.getValue());
		}
		// Take the top k
		
		return sortedTop;
	}
	
	private void tracePaths(int row, int col, int[][][] paths, double[][][] values, 
			ArrayList<ResultPair> currentPath, List<ArrayList<ResultPair>> solutons) {
		if (row == 0) {
			// base case.
			if(values[row][col][0] != A.inf) {
				currentPath.add(new ResultPair(3, values[row][col][0]));
				Collections.reverse(currentPath);
				solutons.add(currentPath);
			}
		}
		else {
			for(int i = 0; i < paths[row][col].length - 1; i++) {
				if(values[row][col][i] == A.inf)
					continue;	// skip if we get a -infinity
				ArrayList<ResultPair> subpath = (ArrayList<ResultPair>) currentPath.clone();
				subpath.add(new ResultPair(paths[row][col][i], values[row][col][i]));
				tracePaths(row - 1, paths[row][col][i], paths, values, subpath, solutons);
			}
		}
	}
	
	private void printSolution(List<Integer> solution, double logLikeleyhood) {
		for(int i = 0; i < solution.size(); i++) {
			System.out.format("%d,", solution.get(i));
		}
		System.out.format(" %f%n", logLikeleyhood);
	}

	/**
	 * Recursively trace back the though the arg_max using the max_vals to get the solution.
	 */
	private void trace(int row, int col, int[][] paths, List<Integer> solution) {
		if (row != 0) {
			trace(row - 1, paths[row][col], paths, solution);
			solution.add(paths[row][col]);
		}
	}
	
	/**
	 * Note: notation taken from [Stamp, 2012]
	 * @param t		observation index.
	 * @param i		the end state we are finding the max value for.
	 * @param O		observation sequence.
	 * @param max_val	book keeping for maximum states sequence values.
	 * @return
	 */
	@SuppressWarnings("unused")
	private double viterbi_recursive(int t, int i, int[] O, double[][] max_val, int[][] arg_max) {
		if (max_val[t][i] != 0)
			return max_val[t][i];
	
		double[] p = new double[A.N];
		for(int j = 0; j < A.N; j++) {
			if(t == 0) {	// Base case
				p[j] = A.get(A.getBeginIndex(), i) + B.get(i, O[0]);
			}
			else {
				p[j] = viterbi_recursive(t - 1, j, O, max_val, arg_max) + A.get(j, i) + B.get(i, O[t]);
			}
		}
		// Book keeping
		arg_max[t][i] = getMaxIndex(p);
		max_val[t][i] = p[arg_max[t][i]];
			
		return max_val[t][i];	// return best value
	}
	
	private void viterbi_procedural(int[] O, double[][][] values, int[][][] paths) {
		// Base case, t = 0
		for(int  i = 0; i < A.N; i++)
			for(int j = 0; j < A.N; j++)
				values[0][i][j] = A.get(A.getBeginIndex(), i) + B.get(i, O[0]);
		
		for(int t = 1; t < O.length; t++) {
			for(int i = 0; i < A.N; i++) {
				double[] p = new double[A.N];
				for(int j = 0; j < A.N; j++) {
					//paths[t][i][j] = getMaxIndex(values[t-1][j]);
					//values[t][i][j] = values[t-1][j][paths[t][i][j]] + A.get(j, i) + B.get(i, O[t]);
					values[t][i][j] = A.get(j, i) + B.get(i, O[t]);
					paths[t][i][j] = j;
				}
			}
		}
		// Add in the final transition to the artificial END state
		int end_i = A.getEndIndex();	// index to the END state
		for (int i = 0; i < A.N; i++) {
			int best_last_i = getMaxIndex(values[O.length - 1][i]);	// (index of) the best of each set in the last row
			paths[O.length][end_i][i] = best_last_i;
			//values[O.length][end_i][i] = values[O.length - 1][i][best_last_i] + A.get(best_last_i, end_i);
			values[O.length][end_i][i] = A.get(best_last_i, end_i);
		}		
	}
	
	private void getTop(int k) {

	}
	
	/**
	 * @param	in		input
	 * @return	index 	to the max value in input
	 */
	private int getMaxIndex(double[] in) {
		int max_i = 0;
		for(int i = 0; i < in.length; i++) {
			if (in[i] > in[max_i]) {
				max_i = i;
			}
		}
		return max_i;
	}
	
	private double getMax(double[] in) {
		return in[getMaxIndex(in)];
	}
	
	/**
	 *	@param args
	 * 		0: STATE FILE: it is the file that stores part of the HMM model. 
	 *		1: SYMBOL FILE: it is the file that stores the other part of the HMM model. 
	 *		2: QUERYFILE: the file that contains addresses to be parsed.
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException {
		int x = 1;
		if(args.length < 2) {
			System.out.println("Too few arguments :(");
			System.exit(1);
		}
		if (args.length > 3)
			try {
				x = Integer.parseInt(args[3]);
			} catch (NumberFormatException e) {
				System.out.println("Bad integer :(");
			}
		
		new Proj1(args[0], args[1], args[2], x);
		
		System.exit(0);
	}
	
	/* * * * * * * DEBUG functions * * * * * * * */
	private void print2ddouble(String comment, double[][] in) {
		System.out.println("[" + comment);
		for(double[] row : in) {
			System.out.print("[");
			for(double col : row) {
				System.out.format(" %.6f ", col);
			}
			System.out.println("]");
		}
		System.out.println("]");
	}
	private void print2ddoubleDouble(String comment, double[][][] in) {
		System.out.println("[" + comment);
		for(double[][] row : in) {
			System.out.print("[ ");
			for(double[] col : row) {
				System.out.print("[");
				for(double i : col) 
					System.out.format(" %.6f ", i);
				System.out.print("]");
			}
			System.out.println(" ]");
		}
		System.out.println("]");
	}
	private void print2dint(String comment, int[][] in) {
		System.out.println("[" + comment);
		for(int[] row : in) {
			System.out.println(Arrays.toString(row));
		}
		System.out.println("]");
	}
	private void print2dInt2(String comment, int[][][] in) {
		System.out.println("[" + comment);
		for(int[][] row : in) {
			System.out.print("[ ");
			for(int[] col : row) {
				System.out.print("[");
				for(int i : col) 
					System.out.format(" %d ", i);
				System.out.print("]");
			}
			System.out.println(" ]");
		}
		System.out.println("]");
	}	
}