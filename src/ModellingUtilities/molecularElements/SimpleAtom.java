package ModellingUtilities.molecularElements;

import static ScoreUtilities.ScoringGeneralHelpers.*;

/**
 * Created by Ziv_BA on 30/07/2015.
 */
public class SimpleAtom {

	private final String type;
	String originalString;
	private final int serialNumber;
	private String aAcidName;
	final char chain;
	final int aAcidSequence;
	private final float[] atomCoords;
	double tempFactor;

	private double atomScore;
	final boolean isBackBone;

	public SimpleAtom(String atom) {

		originalString = atom;
		type = atom.substring(ATOM_NAME_START, ATOM_NAME_END + 1);
		serialNumber = Integer.parseInt(atom.substring(ATOM_NUM_START, ATOM_NUM_END + 1).trim());
		aAcidName = atom.substring(RES_NAME_START, RES_NAME_END + 1);
		chain = atom.charAt(CHAIN_ID);
		aAcidSequence = Integer.parseInt(atom.substring(RES_SEQ_START, RES_SEQ_END + 1).trim());
		try {
			tempFactor = Double.parseDouble(atom.substring(RES_TEMP_START, RES_TEMP_END));
		} catch (NullPointerException | NumberFormatException e){
//			if (debug) System.err.println("No Temperture factor data for some atoms, using C-Alpha value instead");
			tempFactor = Double.MIN_VALUE;
		}
		atomCoords = parseCoords(atom.substring(30, 54));
		isBackBone = type.matches("\\s*(C|CA|O|N)\\s*");

	}

	private float[] parseCoords(String substring) {
		float[] coords = new float[3];
		coords[0] = Float.parseFloat(substring.substring(0, 8));
		coords[1] = Float.parseFloat(substring.substring(8, 16));
		coords[2] = Float.parseFloat(substring.substring(16, 24));
		return coords;
	}

	public double getAtomScore() {
		return atomScore;
	}

	public void setAtomScore(double atomScore) {
		this.atomScore = atomScore;
	}

	public String getType() {

		return type;
	}

	public float[] getAtomCoords() {
		return atomCoords;
	}


	public String getaAcidName() {
		return aAcidName;
	}

	public void setaAcidName(String newAcid) {
		this.aAcidName = newAcid;
		originalString = originalString.substring(0, RES_NAME_START) + newAcid +
				originalString.substring(RES_NAME_END + 1);
	}

	public String getOriginalString() {
		return originalString;
	}

	public int getPos() {
		return serialNumber;
	}

	public boolean isBackbone() {
		return isBackBone;
	}
}
