Note that floating numbers are not represented exactly by computers, so
although we print out 8 digits after the decimal point, your results may
not all of them. But we do expect the error is within 10E-4. Otherwise,
it is likely that there is some bug in your program.  

== Query1

Debugging info: 

#states = 5, #symbols = 3
	States[0] = S1
	States[1] = S2
	States[2] = S3
	States[3] = BEGIN
	States[4] = END
BEGIN state = 3, END state = 4

	Symbols[0] = Red
	Symbols[1] = Green
	Symbols[2] = Blue

The transition matrix (with smoothing)
* the numbers are natural log of probabilities
* the value at cell [i, j] is the log-probability of transitting from
  State i to State j. (both i and j start from 0) 
* -Inf is due to log(0.0)

-0.76214005	-1.60943791	-1.60943791	-Inf	-2.01490302	
-2.01490302	-1.32175584	-0.76214005	-Inf	-2.01490302	
-1.32175584	-0.76214005	-2.01490302	-Inf	-2.01490302	
-1.25276297	-1.25276297	-1.25276297	-Inf	-1.94591015	
-Inf	-Inf	-Inf	-Inf	-Inf	


The emission matrix (with smoothing):
* the numbers are natural log of probabilities
* the numbers in brackets are for the UNK symbol
* the value at cell [i, j] is the log-probability of emitting Symbol j
  from State i. 

-0.91629073	-1.20397280	-1.60943791	(-2.30258509)	
-1.60943791	-0.91629073	-1.20397280	(-2.30258509)	
-1.60943791	-1.60943791	-0.69314718	(-2.30258509)	
-Inf	-Inf	-Inf	(-Inf)	
-Inf	-Inf	-Inf	(-Inf)


=== Matrix for the Viterbi algorithm

Note
* the numbers are natural log of probabilities
* the i-th row is for the i-th symbol observed.
* the last row is used to compute the final probability considering the
  mandatory transition to the END state.
* the j-th column is for the State j. 

-2.16905370	-2.86220088	-2.86220088	-Inf	-Inf	
-3.84748448	-5.23377885	-5.23377885	-9999.00000000	-9999.00000000	
-5.81359734	-6.37321313	-7.06636031	-9999.00000000	-9999.00000000	
-8.18517531	-8.62700806	-7.82850036	-9999.00000000	-9999.00000000	
0.00000000	0.00000000	0.00000000	0.00000000	-9.84340338

-2.45673577	-2.16905370	-2.86220088	-Inf	-Inf	
-4.13516656	-5.10024745	-4.54063166	-9999.00000000	-9999.00000000	
-6.10127941	-6.21906245	-7.35404238	-9999.00000000	-9999.00000000	
-8.47285738	-8.74479109	-7.67434968	-9999.00000000	-9999.00000000	
0.00000000	0.00000000	0.00000000	0.00000000	-9.68925270

-2.86220088	-2.45673577	-1.94591015	-Inf	-Inf	
-4.47163879	-3.62434093	-4.82831374	-9999.00000000	-9999.00000000	
-6.15006958	-6.55553469	-5.99591890	-9999.00000000	-9999.00000000	
-7.82850036	-8.36749686	-8.92711265	-9999.00000000	-9999.00000000	
0.00000000	0.00000000	0.00000000	0.00000000	-9.84340338

== Query2

Debugging info omitted (the same as Query1)

=== Matrix for the Viterbi algorithm

-2.16905370	-2.86220088	-2.86220088	-Inf	-Inf	
-5.23377885	-5.92692603	-5.92692603	-9999.00000000	-9999.00000000	
-7.60535681	-7.89303888	-7.38221326	-9999.00000000	-9999.00000000	
0.00000000	0.00000000	0.00000000	0.00000000	-9.39711628

-2.16905370	-2.86220088	-2.86220088	-Inf	-Inf	
-3.84748448	-5.23377885	-5.23377885	-9999.00000000	-9999.00000000	
-5.52591527	-7.06636031	-7.06636031	-9999.00000000	-9999.00000000	
-7.20434605	-8.74479109	-8.74479109	-9999.00000000	-9999.00000000	
-8.88277684	-10.42322188	-10.42322188	-9999.00000000	-9999.00000000	
-10.56120762	-12.10165266	-12.10165266	-9999.00000000	-9999.00000000	
0.00000000	0.00000000	0.00000000	0.00000000	-12.57611064

