package ModellingUtilities.molecularElements;

import java.util.Arrays;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Ziv_BA on 30/07/2015.
 */
public class AminoAcid implements Iterable<SimpleAtom>{
	private SimpleAtom[] atoms;
	private String name;
	private char singleLetterName;
	private char chainID;
	private int seqNum;
	private double acidScore;

	/**
	 * constructor for AminoAcid object, gets a string list (such as from PDB file).
	 *
	 * @param listOfAtoms
	 */
	public AminoAcid(List<String> listOfAtoms) throws InvalidPropertiesFormatException {
		atoms = new SimpleAtom[listOfAtoms.size()];

		for (int i = 0; i < listOfAtoms.size(); i++) {
			atoms[i] = new SimpleAtom(listOfAtoms.get(i));
		}
		name = atoms[0].getaAcidName();
		singleLetterName = ProteinActions.resToSingleLetter(name);
		chainID = atoms[0].chain;
		seqNum = atoms[0].aAcidSequence;

		Double CATemp = Double.MIN_VALUE;
		for (int i=0; i<atoms.length; i++){
			if (atoms[i].getName().equals("CA")){
				CATemp = atoms[i].tempFactor;
			}
			if (atoms[i].tempFactor==Double.MIN_VALUE){
				atoms[i].tempFactor=CATemp;
			}
		}
	}

	public void setAcidScore(double acidScore) {
		this.acidScore = acidScore;
	}

	public int getSeqNum() {
		return seqNum;
	}

	public char getChainID() {
		return atoms[0].chain;
	}


	public String getName() {

		return name;
	}

	@Override
	public Iterator<SimpleAtom> iterator() {
		return new Iterator<SimpleAtom>() {
			int counter;
			@Override
			public boolean hasNext() {
				return counter < atoms.length;
			}

			@Override
			public SimpleAtom next() {
				return atoms[counter++];
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	public void substituteWith(String newAcid) {
		String curAtom = "";
		try {
			name = newAcid;
			for (SimpleAtom atom : atoms) {
				curAtom = atom.getOriginalString();
				atom.setaAcidName(newAcid);
			}
		}catch (NullPointerException e){
			System.out.println("AA substitution exception at:\n");
			System.out.println("tried to put " + newAcid+" at:\n"+curAtom+"\n");
		}
	}

	public Integer getAcidGlobalIndex() throws InvalidPropertiesFormatException {
		return ProteinActions.acidToIndex(name);
	}

	public void strip() {
		atoms = Arrays.copyOf(atoms,4);
	}

	public char getSingleLetter() {
		return singleLetterName;
	}
}