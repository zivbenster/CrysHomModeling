package meshi.solv.parametrization;

import meshi.energy.compositeTorsions.CompositeTorsionsDefinitions;
import meshi.energy.compositeTorsions.SplinedPolynomialsLoader;
import meshi.geometry.DistanceMatrix;
import meshi.geometry.ResidueBuilder;
import meshi.geometry.rotamers.DunbrackLib;
import meshi.molecularElements.Atom;
import meshi.molecularElements.AtomList;
import meshi.molecularElements.Protein;
import meshi.molecularElements.Residue;
import meshi.molecularElements.residuesExtendedAtoms.ResidueExtendedAtoms;
import meshi.parameters.AtomTypes;
import meshi.parameters.MeshiPotential;
import meshi.parameters.Residues;
import meshi.util.Command;
import meshi.util.CommandList;
import meshi.util.KeyWords;
import meshi.util.MeshiProgram;
import meshi.util.file.File2StringArray;
import meshi.util.rotamericTools.RotamericTools;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This program will collect statistics on the environments of coarse representaions: ROT1 and CBs of the MESHI atom types (190 of them). 
 * For each atom type we would wish to characterize its:
 * 1. Number of heavy atom neighbors when the model is in Rot1.
 * 2. Number of CBs in a certain cutoff.
 * 
 * @author Nir
 */
class ExtractCoarseSolvateDataPairwise extends MeshiProgram implements Residues, AtomTypes, MeshiPotential, KeyWords, CompositeTorsionsDefinitions{

	// We get these user defined parameters from the command line
	// ----------------------------------------------------------
	// The structure database:
	private static String listOfStructures = "";
	// Output file:
	private static String outputFile = "";
	// The carbon cutoff (carbon will counted in a sphere with this radius):
	private static double cutoffNeighbor = 99.9;
	// The shift:
	private static int threadingShift = 0;
	

	// ------------------------
	// User predefined parameters:
	// ------------------------
	// The carbon bins:
	private static final double[] binsDistances = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 128, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144, 145, 146, 147, 148, 149, 150, 151, 152, 153, 154, 155, 156, 157, 158, 159, 160, 161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175, 176, 177, 178, 179, 180, 181, 182, 183, 184, 185, 186, 187, 188, 189, 190, 191, 192, 193, 194, 195, 196, 197, 198, 199, 200, 201, 202, 203, 204, 205, 206, 207, 208, 209, 210, 211, 212, 213, 214, 215, 216, 217, 218, 219, 220, 221, 222, 223, 224, 225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 235, 236, 237, 238, 239, 240, 241, 242, 243, 244, 245, 246, 247, 248, 249, 250, 251, 252, 253, 254, 255, 256, 257, 258, 259, 260, 261, 262, 263, 264, 265, 266, 267, 268, 269, 270, 271, 272, 273, 274, 275, 276, 277, 278, 279, 280, 281, 282, 283, 284, 285, 286, 287, 288, 289, 290, 291, 292, 293, 294, 295, 296, 297, 298, 299, 300, 301, 302, 303, 304, 305, 306, 307, 308, 309, 310, 311, 312, 313, 314, 315, 316, 317, 318, 319, 320, 321, 322, 323, 324, 325, 326, 327, 328, 329, 330, 331, 332, 333, 334, 335, 336, 337, 338, 339, 340, 341, 342, 343, 344, 345, 346, 347, 348, 349, 350, 351, 352, 353, 354, 355, 356, 357, 358, 359, 360, 361, 362, 363, 364, 365, 366, 367, 368, 369, 370, 371, 372, 373, 374, 375, 376, 377, 378, 379, 380, 381, 382, 383, 384, 385, 386, 387, 388, 389, 390, 391, 392, 393, 394, 395, 396, 397, 398, 399, 400, 401, 402, 403, 404, 405, 406, 407, 408, 409, 410, 411, 412, 413, 414, 415, 416, 417, 418, 419, 420, 421, 422, 423, 424, 425, 426, 427, 428, 429, 430, 431, 432, 433, 434, 435, 436, 437, 438, 439, 440, 441, 442, 443, 444, 445, 446, 447, 448, 449, 450, 451, 452, 453, 454, 455, 456, 457, 458, 459, 460, 461, 462, 463, 464, 465, 466, 467, 468, 469, 470, 471, 472, 473, 474, 475, 476, 477, 478, 479, 480, 481, 482, 483, 484, 485, 486, 487, 488, 489, 490, 491, 492, 493, 494, 495, 496, 497, 498, 499, 500, 501, 502, 503, 504, 505, 506, 507, 508, 509, 510, 511, 512, 513, 514, 515, 516, 517, 518, 519, 520, 521, 522, 523, 524, 525, 526, 527, 528, 529, 530, 531, 532, 533, 534, 535, 536, 537, 538, 539, 540, 541, 542, 543, 544, 545, 546, 547, 548, 549, 550, 551, 552, 553, 554, 555, 556, 557, 558, 559, 560, 561, 562, 563, 564, 565, 566, 567, 568, 569, 570, 571, 572, 573, 574, 575, 576, 577, 578, 579, 580, 581, 582, 583, 584, 585, 586};
	// Minimum size protein:
	private static final int min_Prot_Size = 79;
	// How many residues to ignore in locality
	private static final int ignore_local = 1;
	// What the size of the "buffer zone", i.e. the number of residues in each side of the computational 
	// region, where excess residues from the threading shift are moved to.
	private static final int bufferZone = 4;
	// What the size of the "computational zone", where residues are actually analyzed for 
	// the number of neighbors.
	private static final int compZone = 12;
	// CommandList:
	private static final CommandList commands = new CommandList("commands");
	// ----------------------------------------


	public static void main(String[] args) throws IOException{

		// Local variables
		double dis;
		Atom atom,atom1,atom2;
		int type1,type2;
		int indNeighbor;

		init(args); 

		// Dunbrack Lib
		DunbrackLib lib = new DunbrackLib(commands, 1.0, 100);

		// Ramachandran polynomials
        Command command = commands.firstWord(PARAMETERS_DIRECTORY);
		SplinedPolynomialsLoader splinedPolynomialsLoader = 
			new SplinedPolynomialsLoader(command.secondWord()+"/"+COMPOSITE_PROPENSITY_2D_PARAMETERS);
		int[] torsions = {PHI,PSI};
		
		// The data array:
		double[][][] dataCBs;
		dataCBs = new double[20][21][binsDistances.length];
		double[][][] dataROT1;
		dataROT1 = new double[20][21][binsDistances.length];

		// Going over the models
		String[] models = File2StringArray.f2a(listOfStructures);
		for (String model1 : models) {
			System.out.println("Reading: " + model1);
			
			// Creating the model
			Protein modelHardCopy = new Protein(new AtomList(model1), new ResidueExtendedAtoms(ADD_ATOMS));
			if (modelHardCopy.atoms().CAFilter().size() > min_Prot_Size) {
				DistanceMatrix dm = new DistanceMatrix(modelHardCopy.atoms(), 5.5, 1.0, 3);
				double[][] ppHardCopy = RotamericTools.putIntoRot1(modelHardCopy, dm, lib);
				Protein model = new Protein(new AtomList(model1), new ResidueExtendedAtoms(ADD_ATOMS));
				for (int c = 0; c < model.atoms().size(); c++)
					model.atoms().atomAt(c).addAttribute(new SolvateExtractionAttribute());
				dm = new DistanceMatrix(model.atoms(), 5.5, 1.0, 3);
				double[][] pp = RotamericTools.putIntoRot1(model, dm, lib);
				boolean[] rottenRamach = new boolean[pp.length];
				for (int c = 0; c < pp.length; c++)
					rottenRamach[c] = false;
				int rottenResidues = 0;
				int validResidues = 0;
				
				// A centroid part:
				for (int res = 0; res < modelHardCopy.residues().size(); res++) {
					if ((modelHardCopy.residues().residueAt(res).type < 20) &&
							(modelHardCopy.residues().residueAt(res).type > -1))
						RotamericTools.putOinSCcenter(modelHardCopy.residues().residueAt(res));
				}
				for (int res = 0; res < model.residues().size(); res++) {
					if ((model.residues().residueAt(res).type < 20) &&
							(model.residues().residueAt(res).type > -1))
						RotamericTools.putOinSCcenter(model.residues().residueAt(res));
				}
				// End of centroid part
				
				// A worst rot part:
				//				for (int res=0 ; res<modelHardCopy.residues().size(); res++) {
				//					if ((modelHardCopy.residues().residueAt(res).type<20) &&
				//							(modelHardCopy.residues().residueAt(res).type>0) &&
				//							(modelHardCopy.residues().residueAt(res).type!=GLY))
				//						ResidueBuilder.build(modelHardCopy.residues().residueAt(res),
				//								modelHardCopy.residues().residueAt(res).type,
				//								lib.getRotamer(modelHardCopy.residues().residueAt(res).type,
				//									ppHardCopy[modelHardCopy.residues().residueAt(res).number][0],
				//									ppHardCopy[modelHardCopy.residues().residueAt(res).number][1],
				//									lib.getRotamerNum(modelHardCopy.residues().residueAt(res).type,
				//											ppHardCopy[modelHardCopy.residues().residueAt(res).number][0],
				//											ppHardCopy[modelHardCopy.residues().residueAt(res).number][1])-1));
				//				}
				//				for (int res=0 ; res<model.residues().size(); res++) {
				//					if ((model.residues().residueAt(res).type<20) &&
				//							(model.residues().residueAt(res).type>0) &&
				//							(model.residues().residueAt(res).type!=GLY))
				//						ResidueBuilder.build(model.residues().residueAt(res),
				//								model.residues().residueAt(res).type,
				//								lib.getRotamer(model.residues().residueAt(res).type,
				//									pp[model.residues().residueAt(res).number][0],
				//									pp[model.residues().residueAt(res).number][1],
				//									lib.getRotamerNum(model.residues().residueAt(res).type,
				//											pp[model.residues().residueAt(res).number][0],
				//											pp[model.residues().residueAt(res).number][1])-1));
				//				}
				// End of worst rot part
				
				// A random rot part:
				//				for (int res=0 ; res<modelHardCopy.residues().size(); res++) {
				//					if ((modelHardCopy.residues().residueAt(res).type<20) &&
				//							(modelHardCopy.residues().residueAt(res).type>0) &&
				//							(modelHardCopy.residues().residueAt(res).type!=GLY))
				//						ResidueBuilder.build(modelHardCopy.residues().residueAt(res),
				//								modelHardCopy.residues().residueAt(res).type,
				//								lib.getRotamer(modelHardCopy.residues().residueAt(res).type,
				//									ppHardCopy[modelHardCopy.residues().residueAt(res).number][0],
				//									ppHardCopy[modelHardCopy.residues().residueAt(res).number][1],
				//									(int) (randomNumberGenerator().nextDouble()*lib.getRotamerNum(modelHardCopy.residues().residueAt
				// (res).type,
				//											ppHardCopy[modelHardCopy.residues().residueAt(res).number][0],
				//											ppHardCopy[modelHardCopy.residues().residueAt(res).number][1]))));
				//				}
				//				for (int res=0 ; res<model.residues().size(); res++) {
				//					if ((model.residues().residueAt(res).type<20) &&
				//							(model.residues().residueAt(res).type>0) &&
				//							(model.residues().residueAt(res).type!=GLY))
				//						ResidueBuilder.build(model.residues().residueAt(res),
				//								model.residues().residueAt(res).type,
				//								lib.getRotamer(model.residues().residueAt(res).type,
				//									pp[model.residues().residueAt(res).number][0],
				//									pp[model.residues().residueAt(res).number][1],
				//									(int) (randomNumberGenerator().nextDouble()*lib.getRotamerNum(model.residues().residueAt(res)
				// .type,
				//											pp[model.residues().residueAt(res).number][0],
				//											pp[model.residues().residueAt(res).number][1]))));
				//				}
				// End of random rot part
				
				// The main loop on the local misthreadings.
				for (int res = 0; res < model.residues().size(); ) {
					int len = 2 * bufferZone + compZone;
					boolean goodStretch = true;
					for (int runStretch = 0; (runStretch < len) && ((res + runStretch) < model.residues().size()); runStretch++) {
						Residue residue = model.residues().residueAt(res + runStretch);
						if ((residue == null) || (residue.type < 0) || (residue.type > 19) ||
								((res > 0) && ((residue.number - 1) != model.residues().residueAt(res + runStretch - 1).number)))
							goodStretch = false;
					}
					if ((res + len) > model.residues().size())
						goodStretch = false;
					if (goodStretch) {
						int stretchStart = model.residues().residueAt(res).number;
						int compStart = stretchStart + bufferZone;
						System.out.println("Moving from residue: " + stretchStart + " until residue: " + (stretchStart + len - 1));
						shiftThreadedProt(model, modelHardCopy, pp, ppHardCopy, stretchStart, threadingShift, lib);
						// Checking for rotten ramaches
						for (int compres = compStart; compres < (compStart + compZone); compres++) {
							validResidues++;
							double ramachVal = splinedPolynomialsLoader.findPolynomial(model.residue(compres).type, torsions, ALL).value(0,
									pp[compres][0],
									pp[compres][1]) / 4.0; // The 4.0 factor come because the two values below were calculated on CA energies
							if (((model.residue(compres).type == GLY) && (ramachVal > 1.65)) ||
									((model.residue(compres).type != GLY) && (ramachVal > 1.5))) {
								rottenRamach[compres] = true;
								rottenResidues++;
							}
						}
						
						// ****************************************************
						// ************    CB and ROT1     ********************
						// ****************************************************
						// Extracting the solvate data for the CB and ROT1 representaion
						for (int runStretch = compStart; runStretch < (compStart + compZone); runStretch++) {
							Residue residue = model.residue(runStretch);
							for (int c1 = 0; c1 < residue.atoms().size(); c1++)
								for (int c2 = 0; c2 < model.atoms().size(); c2++) {
									atom1 = residue.atoms().atomAt(c1);
									atom2 = model.atoms().atomAt(c2);
									if (!((atom1.residueNumber() >= compStart) && (atom1.residueNumber() < (compStart + compZone)) &&
											(atom2.residueNumber() >= compStart) && (atom2.residueNumber() < (compStart + compZone))) ||
											(atom1.number() > atom2.number())) {
										dis = Math.sqrt((atom1.x() - atom2.x()) * (atom1.x() - atom2.x()) +
												(atom1.y() - atom2.y()) * (atom1.y() - atom2.y()) +
												(atom1.z() - atom2.z()) * (atom1.z() - atom2.z()));
										if ((dis < cutoffNeighbor) &&
												(Math.abs(atom1.residueNumber() - atom2.residueNumber()) > ignore_local)) {
											type1 = atom1.residue().type;
											type2 = atom2.residue().type;
											if (type1 > type2) {
												int tmp = type1;
												type1 = type2;
												type2 = tmp;
											}
											if (atom2.name.equals("CB") && atom1.name.equals("CB"))
												dataCBs[type1][type2][(int) Math.round(dis)]++;
											if (atom2.name.equals("CA") && atom1.name.equals("CB"))
												dataCBs[atom1.residue().type][20][(int) Math.round(dis)]++;
											if (atom2.name.equals("CB") && atom1.name.equals("CA"))
												dataCBs[atom2.residue().type][20][(int) Math.round(dis)]++;
											if (atom2.name.equals("O") && atom1.name.equals("O"))
												dataROT1[type1][type2][(int) Math.round(dis)]++;
											if (atom2.name.equals("CA") && atom1.name.equals("O"))
												dataROT1[atom1.residue().type][20][(int) Math.round(dis)]++;
											if (atom2.name.equals("O") && atom1.name.equals("CA"))
												dataROT1[atom2.residue().type][20][(int) Math.round(dis)]++;
										}
									}
								}
						}
						
						
						// Fixing the moved part back
						shiftThreadedProt(model, modelHardCopy, pp, ppHardCopy, stretchStart, 0, lib);
						res += compZone;
					} else {
						res++;
					}
				}
				
				System.out.println(
						"There were " + rottenResidues + " rotten residues out of " + validResidues + "     (" + rottenResidues * 1.0 /
								validResidues + "%)");
			}
		}


		// Outputting CBs
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile+".CB.txt"));
			for (int atomType1=0; atomType1<20 ; atomType1++) {
				for (int atomType2=atomType1; atomType2<21 ; atomType2++) {
					bw.write(atomType1 + " " + atomType2 + " ");
					for (int cnc=0; cnc<binsDistances.length ; cnc++) {
						bw.write(dataCBs[atomType1][atomType2][cnc] + " ");
					}
					bw.write("\n");
				}
			}
			bw.close();
		}
		catch(Exception e) {
			throw new RuntimeException(e.getMessage());
		}

		// Outputting ROT1s
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile+".ROT1.txt"));
			for (int atomType1=0; atomType1<20 ; atomType1++) {
				for (int atomType2=atomType1; atomType2<21 ; atomType2++) {
					bw.write(atomType1 + " " + atomType2 + " ");
					for (int cnc=0; cnc<binsDistances.length ; cnc++) {
						bw.write(dataROT1[atomType1][atomType2][cnc] + " ");
					}
					bw.write("\n");
				}
			}
			bw.close();
		}
		catch(Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	} // Of main


	private static void shiftThreadedProt(Protein prot, Protein protHardCopy, double[][] pp, double[][] ppHardCopy,
	                                      int resStart, int shift, DunbrackLib lib) {
		
		int len = 2*bufferZone + compZone;
		for (int cc=0 ; cc<len ; cc++) {
			int inRes = resStart+cc;
			int takeFromRes = resStart + (cc+shift);
			if ((cc+shift)>=len)
				takeFromRes = resStart + ((cc+shift) - len); 
			if ((cc+shift)<0)
				takeFromRes = resStart + ((cc+shift) + len); 
			// shifting the PP
			pp[inRes][0] = ppHardCopy[takeFromRes][0]; 
			pp[inRes][1] = ppHardCopy[takeFromRes][1];
			// shifting the coordinates
			Atom takeFromAtom = protHardCopy.atoms().findAtomInList("C", takeFromRes);
			Atom inAtom = prot.atoms().findAtomInList("C", inRes);
			inAtom.setXYZ(takeFromAtom.x(), takeFromAtom.y(), takeFromAtom.z());
			takeFromAtom = protHardCopy.atoms().findAtomInList("O", takeFromRes);
			inAtom = prot.atoms().findAtomInList("O", inRes);
			inAtom.setXYZ(takeFromAtom.x(), takeFromAtom.y(), takeFromAtom.z());
			takeFromAtom = protHardCopy.atoms().findAtomInList("CA", takeFromRes);
			inAtom = prot.atoms().findAtomInList("CA", inRes);
			inAtom.setXYZ(takeFromAtom.x(), takeFromAtom.y(), takeFromAtom.z());
			takeFromAtom = protHardCopy.atoms().findAtomInList("N", takeFromRes);
			inAtom = prot.atoms().findAtomInList("N", inRes);
			inAtom.setXYZ(takeFromAtom.x(), takeFromAtom.y(), takeFromAtom.z());
			if ((prot.residue(inRes).type!=GLY) && (prot.residue(inRes).type!=ALA)) {
				ResidueBuilder.build(prot.residue(inRes), (int) pp[inRes][2], 
					lib.getRotamer((int) pp[inRes][2], pp[inRes][0], pp[inRes][1], 0));
				RotamericTools.putOinSCcenter(prot.residue(inRes));
//				ResidueBuilder.buildCentroid(prot.residue(inRes)); // This is for centroid
// 				ResidueBuilder.build(prot.residue(inRes), (int) pp[inRes][2], 
// 						lib.getRotamer((int) pp[inRes][2], pp[inRes][0], pp[inRes][1],
//// 								lib.getRotamerNum((int) pp[inRes][2], pp[inRes][0], pp[inRes][1])-1)); // This is for worse rot
// 				ResidueBuilder.build(prot.residue(inRes), (int) pp[inRes][2], 
// 						lib.getRotamer((int) pp[inRes][2], pp[inRes][0], pp[inRes][1],
// 								(int) (randomNumberGenerator().nextDouble()*
// 										lib.getRotamerNum((int) pp[inRes][2], pp[inRes][0], pp[inRes][1])))); // This is for randon rot
			}
			else {
				ResidueBuilder.build(prot.residue(inRes), (int) pp[inRes][2], null);				
				RotamericTools.putOinSCcenter(prot.residue(inRes));
//				ResidueBuilder.buildCentroid(prot.residue(inRes)); // This is for centroid
			}
		}
	}
	
	

	private static void init(String[] args) {

		/**** NOTE *** the next two lines. Because of a BUG in the Java VM, the 
		 * interfaces "Residues" and "AtomTypes" are not loaded automatically when MinimizeProtein initialize. 
		 * For this purpose these two lines are crucial wherever these two interfaces are implemented. The user might 
		 * rightfully feel that these two lines are "black magic" programming, but happily to our knowledge this is 
		 * the only bizarre phenomenon we are aware of in meshi.
		 **/
		int zvl = ALA; // force the reading of "meshi.parameters.Residues"
		zvl = ACA;// force the reading of "meshi.parameters.AtomTypes"

		String errorMessage = ("\n                  ******************\n"+
				"Usage java -Xmx1000m ExtractCoarseSolvateDataLocalMoves <list of structures> <output file name> <the distance cutoff of CNC> <the shift, usually in [-2,2]>\n"+
		"                    ******************\n");

		listOfStructures = getOrderedArgument(args);
		if (listOfStructures == null) throw new RuntimeException(errorMessage);
		System.out.println("# The structures are taken from: "+listOfStructures);

		outputFile = getOrderedArgument(args);
		if (outputFile == null) throw new RuntimeException(errorMessage);
		System.out.println("# Output will be written to: "+outputFile);

		String cutoffString = getOrderedArgument(args);
		if (cutoffString== null) throw new RuntimeException(errorMessage);
		cutoffNeighbor = new Double(cutoffString);
		System.out.println("# CNC cutoff value: "+ cutoffNeighbor);

		String shiftString = getOrderedArgument(args);
		if (shiftString== null) throw new RuntimeException(errorMessage);
		threadingShift = new Integer(shiftString);
		System.out.println("# Shift: "+ threadingShift);
		
		initRandom(333);
	}
}
