import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

public class Proj1 {

	/**
	 * State to State transitions
	 */
	private TransitionMatrix A;
	/**
	 * State to observation probabilities 
	 */
	private EmissionMatrix  B;

	private Hashtable Best;
	
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
		Best = new Hashtable();
		A.show();
		B.show();
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
		double[][] max_val = new double[O.length + 1][A.N];
		int[][] arg_max = new int[O.length + 1][A.N];
		
//		for (int i = 0; i < A.N; i++)
//			viterbi_backward(O.length - 1, i, O, max_val, arg_max);
		viterbi_procedural(O, max_val, arg_max);
		
		// Add in the final transition to the artificial END state
		int best_last = getMaxIndex(max_val[O.length - 1]);
		int end_i = A.getEndIndex();
		arg_max[O.length][end_i] = best_last;
		max_val[O.length][end_i] = max_val[O.length - 1][best_last] + A.get(best_last, end_i);
		
		print2ddouble("max_val", max_val); // debug
		print2dint("arg_max", arg_max); // debug

		List<Integer> solution = new ArrayList<Integer>();
		solution.add(A.getBeginIndex());
		//trace(O.length, end_i, arg_max, solution);
		solution.add(A.getEndIndex());
		
		//printSolution(solution, max_val[O.length][end_i]);
		
		getTop(k, O, max_val, arg_max);
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
	private void trace(int row, int col, int[][] arg_maxs, List<Integer> solution) {
		if (row != 0) {
			trace(row - 1, arg_maxs[row][col], arg_maxs, solution);
			solution.add(arg_maxs[row][col]);
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
	
	private void viterbi_procedural(int[] O, double[][] max_val, int[][] arg_max) {
		// Base case, t = 0
		for(int  i = 0; i < A.N; i++) {
			max_val[0][i] = A.get(A.getBeginIndex(), i) + B.get(i, O[0]);
		}
		//Hashtable<String, Double> best; // = new Hashtable<String, Double>(A.N * A.N);
		for(int t = 1; t < O.length; t++) {
			for(int i = 0; i < A.N; i++) {
				double[] p = new double[A.N];
				for(int j = 0; j < A.N; j++) {
					p[j] = max_val[t-1][j] + A.get(j, i) + B.get(i, O[t]);
				}
				arg_max[t][i] = getMaxIndex(p);
				max_val[t][i] = p[arg_max[t][i]];
			}
		}
	}
	
	private double[][] getTop(int k, int[] O, double[][] max_val, int[][] arg_max) {
		double[][] best_val = new double[A.N][A.N];
		int[][] best_path = new int[A.N][A.N];
		int t = max_val.length - 2;
		int end_i = A.getEndIndex();
		for(int i = 0; i < A.N; i++) 
			for(int j = 0; j < A.N; j++) {
				best_val[i][j] = max_val[t-1][j] + A.get(j, i) + B.get(i, O[t]) + A.get(j, end_i);
			}
		
		print2ddouble("best_val", best_val); // debug
		return best_val;
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
			System.out.print("[ ");
			for(double col : row) {
				System.out.format(" %.6f ", col);
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
}