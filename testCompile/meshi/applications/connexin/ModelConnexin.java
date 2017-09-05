package meshi.applications.connexin;

import meshi.applications.minimizeProtein.ExtendedAtomsProtein;
import meshi.energy.EnergyCreator;
import meshi.energy.TotalEnergy;
import meshi.energy.angle.AngleCreator;
import meshi.energy.bond.BondCreator;
import meshi.energy.outOfPlane.OutOfPlaneCreator;
import meshi.energy.plane.PlaneCreator;
import meshi.geometry.DistanceMatrix;
import meshi.geometry.rotamers.DunbrackLib;
import meshi.molecularElements.Atom;
import meshi.molecularElements.AtomList;
import meshi.molecularElements.Protein;
import meshi.molecularElements.Residue;
import meshi.molecularElements.residuesExtendedAtoms.ResidueExtendedAtoms;
import meshi.optimizers.LBFGS;
import meshi.optimizers.Minimizer;
import meshi.parameters.AtomTypes;
import meshi.parameters.Residues;
import meshi.util.CommandList;
import meshi.util.MeshiProgram;
import meshi.util.file.MeshiWriter;
import programs.SCMOD;

class ModelConnexin extends MeshiProgram implements Residues, AtomTypes {

	private static CommandList commands;

	public static void main(String[] args){
		init(args);	
		
		int[][] helixMove = { {23,47,0},
				{73,104,0},
				{127,153,0},
				{185,213,0}	};
		
		for (int moveC = -6 ; moveC <=6 ; moveC++) {
			helixMove[0][2] = moveC;
			AtomList model = makeConnexinModel(helixMove);
			for (int c=0; c<model.size() ; c++) {
				model.atomAt(c).addEnergy(137.44);
			}
			try {
				model.noOXTFilter().print(new MeshiWriter("connexin_"+helixMove[0][2]+"_"+helixMove[1][2]+"_"+helixMove[2][2]+"_"+helixMove[3][2]+".pdb"));
			}
			catch (Exception e) {
				System.out.print("\nThere was a problem writing the output:\n" + e + "\n\nContinueing...\n\n");
			}
		}
	}	
	
	private static AtomList makeConnexinModel(int[][] helixMove) {
//		AtomList origStruct = new AtomList("C:\\Users\\Nir\\Connexin\\2ZW3_35Ang.pdb");
//		AtomList origStruct = new AtomList("C:\\Users\\Nir\\Connexin\\2ZW3_Hexamer_Modified.pdb");
//		AtomList origStruct = new AtomList("C:\\Users\\Nir\\Connexin\\2ZW3_35Ang_minimized.pdb");
		AtomList origStruct = new AtomList("C:\\Users\\Nir\\Connexin\\2ZW3_minimized_by_03.pdb");
		String chains = "ABCDEF";
		String background = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
		String trueSeq =    "MDWGTLQTILGGVNKHSTSIGKIWLTVLFIFRIMILVVAAKEVWGDEQADFVCNTLQPGCKNVCYDHYFPISHIRLWALQLIFVSTPALLVAMHVAYRRHEKKRKFIKGEIKSEFKDIEEIKTQKVRIEGSLWWTYTSSIFFRVIFEAAFMYVFYVMYDGFSMQRLVKCNAWPCPNTVDCFVSRPTEKTVFTVFMIAVSGICILLNVTELCYLLIRYCSGKSKKPV";
//		String connonicalStr = "XDWGTLQTILGGVNKHSTSIGAAAAAAAAAAAAAAAVVAAAAAAAAAQADFVCNTLQPGCKNVCYDHYFPISAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAXXXXXXXXXXXXXXXAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAVMYDGFSMQRLVKCNAWPCPNTVDCFVSRAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
		
		
		AtomList outList = new AtomList();
		for (int chainC=0; chainC<chains.length() ; chainC++) {
			for (int[] aHelixMove : helixMove) {
				background = background.substring(0, aHelixMove[0] - 1 + aHelixMove[2]) + trueSeq.substring(aHelixMove[0] - 1, aHelixMove[1]) +
						background.substring(aHelixMove[1] + aHelixMove[2]);
			}
			outList.add(homologyModeling(origStruct.chainFilter(chains.substring(chainC, chainC+1)), trueSeq /*connonicalStr*/, background));
		}
		return outList;
	}
	
	
	
	
	
	private static AtomList homologyModeling(AtomList templateAtoms, String trueSeq, String newSeq) {

		Protein template = new ExtendedAtomsProtein(templateAtoms, DO_NOT_ADD_ATOMS);
		Protein query = null;
		int firstResInTemplate = 1;
		int firstResInQuery = 1;
		int resNumCounterQ=0;
		int resNumCounterT=0;
		boolean[] completedResidue; 



		// Reading the alignment file
		completedResidue = new boolean[newSeq.length()];
		for (int c = 0; c< newSeq.length() ; c++)
			completedResidue[c] = true;


		// Building the protein instance of the trivial homology
		AtomList CAslist = new AtomList();
		resNumCounterQ = firstResInQuery-1;
		resNumCounterT = firstResInTemplate-1;
		for (int position = 0; position< newSeq.length() ; position++) {
			if (newSeq.charAt(position)!='-')
				resNumCounterQ++;
			if (trueSeq.charAt(position)!='-')
				resNumCounterT++;		
			if (newSeq.charAt(position)!='-') {
				if ((trueSeq.charAt(position)!='-') && (template.residue(resNumCounterT)!= null) && (template.residue(resNumCounterT).ca()!= null)) {
					if (Residue.nameOneLetter(template.residue(resNumCounterT).type).charAt(0)!= trueSeq.charAt(position)) {
						System.out.println(Residue.nameOneLetter(template.residue(resNumCounterT).type).charAt(0) + "  BBB   " + trueSeq.charAt(position));
						throw new RuntimeException("Mismatch between template structure and sequence");
					}
					else {
						CAslist.add(new Atom(0,0,0,"CA",
								Residue.one2three(newSeq.charAt(position)),
								resNumCounterQ,-1));
					}
				}
			}
		}
		query = new Protein(CAslist, new ResidueExtendedAtoms(ADD_ATOMS));

		// Assigning the coordinates to the trivial homology
		resNumCounterQ = firstResInQuery-1;
		resNumCounterT = firstResInTemplate-1;
		for (int position = 0; position< newSeq.length() ; position++)  {
			if (newSeq.charAt(position)!='-')
				resNumCounterQ++;
			if (trueSeq.charAt(position)!='-')
				resNumCounterT++;		
			if ((newSeq.charAt(position)!='-') && (trueSeq.charAt(position)!='-') &&
					(template.residue(resNumCounterT)!= null) && (template.residue(resNumCounterT).ca()!= null)) {
				for (int atomCounter=0 ; atomCounter<query.residue(resNumCounterQ).atoms().size() ; atomCounter++) {
					if (query.residue(resNumCounterQ).atoms().atomAt(atomCounter).name().equals("CA")) {
						System.out.println(resNumCounterQ + " " + newSeq.charAt(position) + " " + resNumCounterT + " " + trueSeq.charAt(position));
					}
					Atom atomInQuery = query.residue(resNumCounterQ).atoms().atomAt(atomCounter);
					Atom atomInTemplate = template.residue(resNumCounterT).atoms().getAtom(atomInQuery.name());
					if (atomInTemplate!=null) {
						atomInQuery.setXYZ(atomInTemplate.x(), atomInTemplate.y(), atomInTemplate.z());
						atomInQuery.freeze();
					}
					else {
						if (!atomInQuery.isHydrogen) // We are missing a heavy atom in the template
							completedResidue[position] = false;
						atomInQuery.defrost();
					}
				}
			}
		}

		// Brining new atoms close to their bonded atoms
		for (int atomCounter=0 ; atomCounter<query.atoms().size() ; atomCounter++) {
			Atom atom = query.atoms().atomAt(atomCounter);
			if (!atom.frozen())
				atom.setXYZ(atom.bonded().atomAt(0).x()+Math.random(), atom.bonded().atomAt(0).y()+Math.random(), atom.bonded().atomAt(0).z()+Math.random());
		}


		// First Minimization - This will mainly set the hydrogens right.
		EnergyCreator[] energyCreators = {
				new BondCreator(),
				new AngleCreator(),
				new PlaneCreator(),
				new OutOfPlaneCreator()
		};
		DistanceMatrix distanceMatrix = new DistanceMatrix(query.atoms(), 5.5,  2.0,  4);  
		TotalEnergy energy = new TotalEnergy(query, distanceMatrix, energyCreators, commands);
		Minimizer minimizer = new LBFGS(energy, 0.05 , 100000 , 100 );
		try {
			System.out.println(minimizer.minimize());
		}
		catch (Exception e) {
			System.out.println(e + "\n\n\nAn error in the minimization. Continueing...");
		}

		// Freezing only residues whose sidechains are determined from the template
		resNumCounterQ = 0;
		for (int position = 0; position< newSeq.length() ; position++)
			if (newSeq.charAt(position)!='-') {
				resNumCounterQ++;
				if (query.residue(resNumCounterQ)!=null) {
					if ((trueSeq.charAt(position)== newSeq.charAt(position)) &&
							completedResidue[position]) { 
						//System.out.println("Freezing: " + resNumCounterQ + " " + templateAlignment.charAt(position));
						query.residue(resNumCounterQ).atoms().freeze();
					}
					else {
						//System.out.println("Defrost: " + resNumCounterQ + " " + templateAlignment.charAt(position));
						query.residue(resNumCounterQ).atoms().defrost();
					}
				}
			}

		// Running SCMOD
		SCMOD.scmod(commands, new DunbrackLib(commands,1.0,50), query, 0);
		query.atoms().setChain(template.atoms().atomAt(0).chain());
		return query.atoms();
	}

	
	
	
	
	
	
	
	/** ================================= init =========================================
	 *
	 *A static function for parsing of the command line arguments and assigning the 
	 *variables commandsFileName, modelFileName and randomNumberSeed with the right inputs. Note that this
	 *static method is using parsing functions such as getOrderedArguments that are defined in MeshiProgram
	 *that MinimizeProtein inherits.
	 **/

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
				"Usage java -Xmx600m SimpleHomologyModeling <commands file name> <alignment file name> \n"+
		"                    ******************\n");

		if (getFlag("-debug",args)) tableSet("debug", Boolean.TRUE);
		String commandsFileName = getOrderedArgument(args);
		if (commandsFileName == null) throw new RuntimeException(errorMessage);
		System.out.println("# commandsFileName = "+commandsFileName);

		commands = new CommandList(commandsFileName);

		initRandom(333);
	}		
	
	
}