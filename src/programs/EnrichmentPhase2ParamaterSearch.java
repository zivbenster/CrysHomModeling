package programs;

import meshi.util.file.File2StringArray;

import java.util.Arrays;
import java.util.StringTokenizer;

/**
 * Running:
 * 
 * java EnrichmentPhase2ParamaterSearch <list of data files> <column for solvate1> <column for solvate2>
 * 
 * @author Nir
 *
 */

class EnrichmentPhase2ParamaterSearch {
	
	public static void main(String[] args) {
		System.out.println("Reading files from: " + args[0].trim() + 
				"      and solvate is from columns:"  + args[1].trim() + "  and  " + args[2].trim());

		// Enrichment percentage
		double enrichPercent = 0.1;
		
		// Columns - Parameters matches
		int column1 = 14; // RAMACH
		int column2 = 15; // PROP
		int column3 = 12; //HB 
		int column4 = new Integer(args[1].trim());  // SOLV 1
		int column5 = new Integer(args[2].trim());  // SOLV 2
		int column6 = 1;  // Currently dummy
		int columnRMS = 4;
		
		// parameter ranges
		double[] range1 = {0, 0.02, 0.05, 0.1, 0.2, 0.4, 0.6, 0.8, 1.0, 1.5, 2.0, 3.0, 4.0};
		double[] range2 = {0, 0.02, 0.05, 0.1, 0.2, 0.4, 0.6, 0.8, 1.0, 1.5, 2.0, 3.0, 4.0};
		double[] range3 = {0, 0.5};
		double[] range4 = {0.02, 0.05, 0.1, 0.2, 0.4, 0.6, 0.8, 1.0, 1.5, 2.0, 3.0, 4.0};
		double[] range5 = {0};//{-0.2, 0, 0.02, 0.05, 0.1, 0.2, 0.4, 0.6, 0.8, 1.0, 1.5, 2.0, 3.0, 4.0};
		double[] range6 = {0}; //{-0.2, 0, 0.02, 0.05, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6};//{0};
		
		
		// Reading data
		String[] listOfDataFiles = File2StringArray.f2a(args[0].trim());
		double[][][] data = new double[listOfDataFiles.length][][];
		double[] pertileRMS = new double[data.length];
		double[] pertile1 = new double[data.length];
		double[] pertile2 = new double[data.length];
		double[] pertile3 = new double[data.length];
		double[] pertile4 = new double[data.length];
		double[] pertile5 = new double[data.length];
		double[] pertile6 = new double[data.length];
		String token;
		for (int loop=0; loop<data.length ; loop++) {
			String[] tmpData = File2StringArray.f2a(listOfDataFiles[loop]);
			data[loop] = new double[9][tmpData.length];
			for (int decoy=0 ; decoy<tmpData.length ; decoy++) {
				StringTokenizer st = new StringTokenizer(tmpData[decoy]);
				for (int column=1 ; st.hasMoreTokens() ; column++) {
					token = st.nextToken();
					if (column==columnRMS)
						data[loop][0][decoy] = new Double(token);
					if (column==column1)
						data[loop][1][decoy] = new Double(token);
					if (column==column2)
						data[loop][2][decoy] = new Double(token);
					if (column==column3)
						data[loop][3][decoy] = new Double(token);
					if (column==column4)
						data[loop][4][decoy] = new Double(token);
					if (column==column5)
						data[loop][5][decoy] = new Double(token);
					if (column==column6)
						data[loop][6][decoy] = new Double(token);
				}
			}
			pertileRMS[loop] = getPercentile(data[loop][0],enrichPercent);
			pertile1[loop] = getPercentile(data[loop][1],enrichPercent);
			pertile2[loop] = getPercentile(data[loop][2],enrichPercent);
			pertile3[loop] = getPercentile(data[loop][3],enrichPercent);
			pertile4[loop] = getPercentile(data[loop][4],enrichPercent);
			pertile5[loop] = getPercentile(data[loop][5],enrichPercent);
			pertile6[loop] = getPercentile(data[loop][6],enrichPercent);
		}
		
		// Do the parameter search
		double bestEnrich = -9999.0;
		double totalEnrich = 0;
		double loopEnrich = 0;
		double energyPercentile = 0;
		double best1 = -9999;
		double best2 = -9999;
		double best3 = -9999;
		double best4 = -9999;
		double best5 = -9999;
		double best6 = -9999;
		for (double aRange6 : range6)
			for (double aRange3 : range3)
				for (double aRange1 : range1)
					for (double aRange2 : range2)
						for (double aRange4 : range4)
							for (double aRange5 : range5) {
								// Calculating the enrichment
								totalEnrich = 0.0;
								for (int loop = 0; loop < data.length; loop++) {
									for (int decoy = 0; decoy < data[loop][0].length; decoy++) {
										data[loop][7][decoy] = aRange1 * data[loop][1][decoy] +
												aRange2 * data[loop][2][decoy] +
												aRange3 * data[loop][3][decoy] +
												aRange4 * data[loop][4][decoy] +
												aRange5 * data[loop][5][decoy] +
												aRange6 * data[loop][6][decoy];
										data[loop][8][decoy] = data[loop][7][decoy];
									}
									Arrays.sort(data[loop][8]);
									energyPercentile = data[loop][8][(int) (enrichPercent * data[loop][8].length)];
									loopEnrich = 0;
									for (int decoy = 0; decoy < data[loop][0].length; decoy++) {
										if ((data[loop][7][decoy] < energyPercentile) && (data[loop][0][decoy] < pertileRMS[loop]))
											loopEnrich++;
									}
									loopEnrich = loopEnrich / (enrichPercent * enrichPercent * data[loop][0].length);
									totalEnrich += loopEnrich;
								}
								totalEnrich = totalEnrich / data.length;
								// Is this better?
								if (totalEnrich > bestEnrich) {
									best1 = aRange1;
									best2 = aRange2;
									best3 = aRange3;
									best4 = aRange4;
									best5 = aRange5;
									best6 = aRange6;
									bestEnrich = totalEnrich;
									System.out.println(
											"Best: " + bestEnrich + " " + best1 + " " + best2 + " " + best3 + " " + best4 + " " + best5 + "" +
													" " +
													best6);
								}
							}
	}
	
	private static double getPercentile(double[] ar, double pertile) {
		double[] copy_ar = new double[ar.length];
		System.arraycopy(ar, 0, copy_ar, 0, ar.length);
		Arrays.sort(copy_ar);
		return copy_ar[(int) (pertile*copy_ar.length)];
	}

}
