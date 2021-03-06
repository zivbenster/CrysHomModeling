package meshi.applications.rotamerSearch;

import meshi.molecularElements.Atom;
import meshi.molecularElements.AtomList;
import meshi.util.file.File2StringArray;
import meshi.util.file.MeshiWriter;

import java.io.IOException;

class PutOptimizedInLargerComplex {


	private static void changeChainPair(AtomList toChange, String leftChain, String rightChain, AtomList takeFrom, String leftChainTakeFrom,
	                                    String rightChainTakeFrom, int[] changedResidues) {
		changeEntireChain(toChange.chainFilter(leftChain), changedResidues, true, takeFrom.chainFilter(leftChainTakeFrom));
		changeEntireChain(toChange.chainFilter(rightChain), changedResidues, false, takeFrom.chainFilter(rightChainTakeFrom));		
	}
	
	
	private static void changeEntireChain(AtomList toChange, int[] changedResidues, boolean below1000, AtomList takeFrom) {
		for (int c=0 ; c<toChange.size() ; c++) {
			int resNum = toChange.atomAt(c).residueNumber();
			if (!below1000) {
				resNum += 1000;
			}
			for (int changedResidue : changedResidues) {
				if (resNum == changedResidue) {
					changeAtom(toChange.atomAt(c), takeFrom);
				}
			}			
		}
	}
	
		
	private static void changeAtom(Atom toChange, AtomList takeFrom) {
//		System.out.println(toChange);
		Atom atom = takeFrom.findAtomInList(toChange.name(), toChange.residueNumber());
		if (atom==null) {
			System.out.println("-------------------------------------------" + takeFrom.size() + "\n\n");
//			takeFrom.print();
		}
		//System.out.println(toChange);
		toChange.setXYZ(atom.x(), atom.y(), atom.z());
	}
	
	private static int[] readInerface(String fileName) {
		String[] models = File2StringArray.f2a(fileName);
		int[] out = new int[models.length];
		for (int c=0 ; c<out.length ; c++) {
			out[c] = Integer.parseInt(models[c]);
 		}
		return out;		
	}
	
	/**
	 * This will put many pairs into a complete model.
	 */
	public static void main(String[] args) {
		String outFileName = "C:\\Users\\Nir\\TRiC\\Crystallography\\Intra_ring_interface\\Manual_optimization_on_LJcap_15\\refine_16-half.ENCAD.refined_interface.pdb";
		String fullComplex = "C:\\Users\\Nir\\TRiC\\Crystallography\\Intra_ring_interface\\ENCAD_based\\refine_16-half.ENCAD.pdb";
		AtomList fullList = (new AtomList(fullComplex)).filter(new AtomList.NonHydrogen()).noOXTFilter();
		String pair = "C:\\Users\\Nir\\TRiC\\Crystallography\\Intra_ring_interface\\Manual_optimization_on_LJcap_15\\pair_";
		String interfaceString = "C:\\Users\\Nir\\TRiC\\Crystallography\\Intra_ring_interface\\Manual_optimization_on_LJcap_15\\interface_";
		String leftChain = "A";
		String rightChain = "G";
		AtomList pairList = new AtomList(pair+leftChain+rightChain+".pdb");
		int[] interfaceRes = readInerface(interfaceString+leftChain+rightChain+".txt");
		changeChainPair(fullList, leftChain, rightChain, pairList, leftChain, rightChain, interfaceRes);
		leftChain = "G";
		rightChain = "Z";
		pairList = new AtomList(pair+leftChain+rightChain+".pdb");
		interfaceRes = readInerface(interfaceString+leftChain+rightChain+".txt");
		changeChainPair(fullList, leftChain, rightChain, pairList, leftChain, rightChain, interfaceRes);
		leftChain = "Z";
		rightChain = "Q";
		pairList = new AtomList(pair+leftChain+rightChain+".pdb");
		interfaceRes = readInerface(interfaceString+leftChain+rightChain+".txt");
		changeChainPair(fullList, leftChain, rightChain, pairList, leftChain, rightChain, interfaceRes);
		leftChain = "Q";
		rightChain = "H";
		pairList = new AtomList(pair+leftChain+rightChain+".pdb");
		interfaceRes = readInerface(interfaceString+leftChain+rightChain+".txt");
		changeChainPair(fullList, leftChain, rightChain, pairList, leftChain, rightChain, interfaceRes);
		leftChain = "H";
		rightChain = "E";
		pairList = new AtomList(pair+leftChain+rightChain+".pdb");
		interfaceRes = readInerface(interfaceString+leftChain+rightChain+".txt");
		changeChainPair(fullList, leftChain, rightChain, pairList, leftChain, rightChain, interfaceRes);
		leftChain = "E";
		rightChain = "B";
		pairList = new AtomList(pair+leftChain+rightChain+".pdb");
		interfaceRes = readInerface(interfaceString+leftChain+rightChain+".txt");
		changeChainPair(fullList, leftChain, rightChain, pairList, leftChain, rightChain, interfaceRes);
		leftChain = "B";
		rightChain = "D";
		pairList = new AtomList(pair+leftChain+rightChain+".pdb");
		System.out.println("-------------------------------------------" + pairList.size() + "\n\n");
		interfaceRes = readInerface(interfaceString+leftChain+rightChain+".txt");
		changeChainPair(fullList, leftChain, rightChain, pairList, leftChain, rightChain, interfaceRes);
		leftChain = "D";
		rightChain = "A";
		pairList = new AtomList(pair+leftChain+rightChain+".pdb");
		interfaceRes = readInerface(interfaceString+leftChain+rightChain+".txt");
		changeChainPair(fullList, leftChain, rightChain, pairList, leftChain, rightChain, interfaceRes);
		
		try {
			fullList.print(new MeshiWriter(outFileName));
		} catch (IOException e) {
			throw new RuntimeException("Could not write file");
		}
	}

}
