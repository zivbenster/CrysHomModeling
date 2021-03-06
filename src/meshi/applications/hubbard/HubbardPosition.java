/**Position: An object that represent the superposition of a protein for each start point (according 
 *           to the base subunit size).
 *           It's data members: distance, new, old, right_atoms,place -> help us keep the changing 
 *           superposition According to the rms function.
 *
 * For creating Position we get:
 * 1. the protein size.
 * 2. the maximal distance between two C atoms in order to join the right atoms group
 *    (-> the group we send to the rms function according to it the protein is rotated).
 * 3. the base subunit size -> the start point of the superposition, according to it, any iteration
 *    of the rms function buid a better superposition.
 * 4. place - represent us the the first atom number of the protein the subunit should start with.
 *
 * In order to get the best superposition of the protein we use HubbardOverlap class, 
 * which gets two proteins and a size of a subunit of the protein.
 * It returns the rms after rotating the second protein according to it.
 * find_best_conformation -> returns the best superposition of the protein according to the subunit size.
 *
 * According to the base sub-unit size of the Protein (Suppose to be 3), we send to the Hubbard overlap
 * all the possible subunits of the proteins ({1,2,3}, {4,5,6}...).
 * After this step, the second protein (the prediction of the protein structure) has been 
 * rotated in order to get the minimal distance between the atoms we have sent.
 *
 * For each subunit according to the answer we get, we go through all the atoms
 * and build a new group of atoms which the distance between them is less
 * than the maximal_distance. This group is becoming the new subunit we send now to the Hubbard Overlap.
 *
 * We return on the previous step untill: there is no change at the superposition of the second protein
 * (and therefor the group of the closest atoms is the same) or if we got to the num_of_loops
 * which represent the limit of the number of iterations (defined at the main function). 
 *    
 * Now, For any base subunit, we have the best superposition of the protein.
 * According to that, we build a distance array for each Position -> distance: 
 *      - its length is the protein size. 
 *      - each place in it keeps the best distance between the atoms respectively.
 **/
package meshi.applications.hubbard;
import meshi.util.overlap.Overlap;

public class HubbardPosition
{
    /////////  DATA MEMBERS  \\\\\\\\\\

    private final double[][] C1;
    private final double[][] C2;
    private int[] subset;
    private final double cutoff;
    public final double[] numbers;
    private final int[] order;
    
    
    /////////  CONSTRUCTOR  \\\\\\\\\\\\
    /**gets the protein size and the size of subunit we first sent to the rms function**/
    
    public HubbardPosition (int sub, int place, double max_distance, double[][] C1, double[][] C2)  
    {
	this.C1 = C1;
	this.C2 = C2;
	subset = new int[sub];
	for (int i=0 ; i<sub ; i++) {
		subset[i] = place + i;
	}
	cutoff = max_distance*max_distance;
	numbers = new double[C1[0].length];
	order = new int[C1[0].length];
	for (int i=0 ; i<order.length ; i++) {
		order[i] = i;
	}
    }
    
    /////////    METHODS  \\\\\\\\\\\\
    
    //----------------------------------------------------
     public void find_best_conformation(int num_of_loops)
     {
     	int[] oldset;
     	boolean conv;
     	int loopCount=0;
     	do {
     		conv = true;
     		oldset = subset;
     		doIter();
     		if (oldset.length != subset.length)
     		    conv = false;
     		else {
     		    for (int j=0 ; (j<subset.length)&&conv ; j++) {
     		    	if (subset[j] != oldset[j])
     		    	   conv = false;
     		    }
     		}      
     		if (subset.length < 3)
     		   conv = true; 
     		if (oldset.length > subset.length) {
     			subset = oldset;
     			conv = true;
     		}
     		loopCount++;
     	} while ((loopCount<num_of_loops) && (!conv));
        updateNumbers();
    }
    
    //----------------------------------------------------
    private void doIter()
    {
    	double rms;
    	rms = Overlap.rmsPartial(C1, C2, subset);
    	if ((rms <0) || (rms == Double.NaN))
    	   throw new RuntimeException("\n\nincorrect rms: " + rms + "\n\n");
    	updateSubset();
    }

    //----------------------------------------------------
    /**Updateing the subset**/
    private void updateSubset()
    {
    	int[] tmpset = new int[C1[0].length];  
    	int i,j;
    	j = 0;
    	for (i=0 ; i<C1[0].length ; i++){
    		if (((C1[0][i] - C2[0][i])*(C1[0][i] - C2[0][i])+
    		(C1[1][i] - C2[1][i])*(C1[1][i] - C2[1][i])+
    		(C1[2][i] - C2[2][i])*(C1[2][i] - C2[2][i])) < cutoff) {
    		tmpset[j] = i;
    		j++;
    	    }
    	}
    	subset = new int[j];
    	for (i=0 ; i<j ; i++){
    		subset[i] = tmpset[i];
    	}
    }


    //----------------------------------------------------
    /**Updateing the numberst**/
    private void updateNumbers()
    {
    	int i;
    	for (i=0 ; i<C1[0].length ; i++){
    		numbers[i] = (C1[0][i] - C2[0][i])*(C1[0][i] - C2[0][i])+
    		(C1[1][i] - C2[1][i])*(C1[1][i] - C2[1][i])+
    		(C1[2][i] - C2[2][i])*(C1[2][i] - C2[2][i]);
    	}
    	sort();
    	for (i=1 ; i<C1[0].length ; i++){
    		numbers[i] = (i*(numbers[i-1]) + numbers[i])/((double)(i+1));
    	}
    }


// --------------------------------------------------
    // sorting the "numbers" array in ascending order
    // the permutation is preserved in "order"
private void sort()
    {
	int min;
	double temp;
	int itemp;
	for (int index = 0; index < numbers.length-1; index++) 
	    {
		min = index;
		for (int scan = index+1; scan < numbers.length; scan++)
		    if (numbers[scan] < numbers[min])
			min = scan;
		// swap the values
		temp = numbers[min];
		itemp = order[min];
		numbers[min] = numbers[index];
		order[min] = order[index];
		numbers[index] = temp;
		order[index] = itemp;
	    }
    }  // method sort
    //------------------------------------------------------
  
}//class
			

