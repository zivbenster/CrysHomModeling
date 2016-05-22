package meshi.energy.propensityTorsion;
import meshi.energy.EnergyElement;
import meshi.geometry.Torsion;
import meshi.geometry.TorsionPair;
import meshi.molecularElements.Atom;
import meshi.molecularElements.AtomList;
import meshi.molecularElements.Residue;

//---------------------------------------------------------------------
public class PropensityTorsionEnergyElement extends EnergyElement {
	private Atom atom1,atom2,atom3,atom4,atom5,atmo6,atom7,atom8;
    private Torsion torsion1,torsion2;
    private int residueType;
    private double limit;
    private double resolution;
    private double[][][] coef;    
    private int[] mapping = new int[21];
    private double[][] mapCoefs;
    private double[][] mapCoefsOmni;
    private double weight;

    /*
     *The constructor needs two torsions. 
     *It checks that the given torsions are indeed of corresponding types to the parameters.
     *There are additional checks that a torsion pair could be properly constructed.
     */	
     
    public PropensityTorsionEnergyElement(TorsionPair torsionPair,
					     PropensityTorsionParameters param, double weight) {
	this.weight = weight;
	this.torsion1 = torsionPair.torsion1();
	this.torsion2 = torsionPair.torsion2();
	residueType = Residue.type(torsion2.getTorsionResName());
	limit = param.limit;
	resolution = param.resolution;
	mapping = param.mapping;

	if (! torsion1.getTorsionName().equals(param.torsion1Name))
	    throw new RuntimeException("\nError: Expected a torsion of type " + param.torsion1Name + 
				       " but got type " + torsion1.getTorsionName() + "\n");
	if (!torsion2.getTorsionName().equals(param.torsion2Name))
	    throw new RuntimeException("\nError: Expected a torsion of type " + param.torsion2Name + 
				       " but got type " + torsion2.getTorsionName() + "\n");
        // The following check is not critical and might be omitted in later versions, if we allow coupling of torsions from different residues.
	if ((torsion1.getTorsionResNum() != torsion2.getTorsionResNum()) || (torsion2.getTorsionCode() < 0))
	    throw new RuntimeException("\nError: The torsions are not from the same residue.");	        
	if (mapping[residueType] == -1)
	throw new RuntimeException("\nError:Residue type:" +" "+torsion2.getTorsionResName()+ 
				   " can not be used with this energy\n");
	setAtoms();
	mapCoefs = param.coef[mapping[residueType]];
	mapCoefsOmni = param.coef[mapping[21]];
	updateFrozen();
    }
	

    public double evaluate() {
   	double[] ramach;
	double energy, dEdTorsion1, dEdTorsion2;
    	
   	if (frozen())
	    return 0.0;
	ramach = computePropensityTorsionEnergy(torsion1.torsion(),torsion2.torsion());
	energy = ramach[0] * weight;
	dEdTorsion1 = -1*ramach[1] * weight;// negating the derivatives for the force calculation
	dEdTorsion2 = -1*ramach[2] * weight;// negating the derivatives for the force calculation
	if (torsion1.atom1.frozen() == false) {
	    torsion1.atom1.addToFx(dEdTorsion1*torsion1.dTorsionDx1());
	    torsion1.atom1.addToFy(dEdTorsion1*torsion1.dTorsionDy1());
	    torsion1.atom1.addToFz(dEdTorsion1*torsion1.dTorsionDz1());
	}
	if (torsion1.atom2.frozen() == false) {
	    torsion1.atom2.addToFx(dEdTorsion1*torsion1.dTorsionDx2());
	    torsion1.atom2.addToFy(dEdTorsion1*torsion1.dTorsionDy2());
	    torsion1.atom2.addToFz(dEdTorsion1*torsion1.dTorsionDz2());
	}
	if (torsion1.atom3.frozen() == false) {
	    torsion1.atom3.addToFx(dEdTorsion1*torsion1.dTorsionDx3());
	    torsion1.atom3.addToFy(dEdTorsion1*torsion1.dTorsionDy3());
	    torsion1.atom3.addToFz(dEdTorsion1*torsion1.dTorsionDz3());
	}
	if (torsion1.atom4.frozen() == false) {
	    torsion1.atom4.addToFx(dEdTorsion1*torsion1.dTorsionDx4());
	    torsion1.atom4.addToFy(dEdTorsion1*torsion1.dTorsionDy4());
	    torsion1.atom4.addToFz(dEdTorsion1*torsion1.dTorsionDz4());
	}	    
	if (torsion2.atom1.frozen() == false) {
	    torsion2.atom1.addToFx(dEdTorsion2*torsion2.dTorsionDx1());
	    torsion2.atom1.addToFy(dEdTorsion2*torsion2.dTorsionDy1());
	    torsion2.atom1.addToFz(dEdTorsion2*torsion2.dTorsionDz1());
	}
	if (torsion2.atom2.frozen() == false) {
	    torsion2.atom2.addToFx(dEdTorsion2*torsion2.dTorsionDx2());
	    torsion2.atom2.addToFy(dEdTorsion2*torsion2.dTorsionDy2());
	    torsion2.atom2.addToFz(dEdTorsion2*torsion2.dTorsionDz2());
	}
	if (torsion2.atom3.frozen() == false) {
	    torsion2.atom3.addToFx(dEdTorsion2*torsion2.dTorsionDx3());
	    torsion2.atom3.addToFy(dEdTorsion2*torsion2.dTorsionDy3());
	    torsion2.atom3.addToFz(dEdTorsion2*torsion2.dTorsionDz3());
	}
	if (torsion2.atom4.frozen() == false) {
	    torsion2.atom4.addToFx(dEdTorsion2*torsion2.dTorsionDx4());
	    torsion2.atom4.addToFy(dEdTorsion2*torsion2.dTorsionDy4());
	    torsion2.atom4.addToFz(dEdTorsion2*torsion2.dTorsionDz4());
	}	    	    
    	return energy;

    }


    public double[] computePropensityTorsionEnergy(double phi, double psi) {
	double[] outputVal = {0.0 , 0.0 , 0.0};
	Long lo;
	double rh,rs,hh,ss;
	int ln;
	double hh2,hh3,ss2,ss3;
		
	// Finding the correct spline square that holds the {phi,psi} 
	rh = Math.floor(((phi+limit) - 2*limit*Math.floor((phi+limit)/(2*limit)))/resolution);
	rs = Math.floor(((psi+limit) - 2*limit*Math.floor((psi+limit)/(2*limit)))/resolution);
	// The line in 'coefs' that holds polynom coefficients of this rectangle
	lo = new Long(Math.round((2*limit)/resolution*rh) + Math.round(rs));
	ln = lo.intValue();
	// Offset in that rectangle
	hh = (phi+limit) - 2*limit*Math.floor((phi+limit)/(2*limit)) - rh*resolution;
	ss = (psi+limit) - 2*limit*Math.floor((psi+limit)/(2*limit)) - rs*resolution;	
	// Adding the cubic polynom terms
	hh2 = hh*hh;
	hh3 = hh2*hh;
	ss2 = ss*ss;
	ss3 = ss2*ss;
	outputVal[0] = mapCoefs[ln][0] + 
	hh*mapCoefs[ln][1] +
	hh2*mapCoefs[ln][2] +
	hh3*mapCoefs[ln][3] +
	ss*mapCoefs[ln][4] +
	hh*ss*mapCoefs[ln][5] +
	hh2*ss*mapCoefs[ln][6] +
	hh3*ss*mapCoefs[ln][7] +
	ss2*mapCoefs[ln][8] +
	hh*ss2*mapCoefs[ln][9] +
	hh2*ss2*mapCoefs[ln][10] +
	hh3*ss2*mapCoefs[ln][11] +
	ss3*mapCoefs[ln][12] +
	hh*ss3*mapCoefs[ln][13] +
	hh2*ss3*mapCoefs[ln][14] +
	hh3*ss3*mapCoefs[ln][15];

	outputVal[1] = 
	mapCoefs[ln][1] +
	2*hh*mapCoefs[ln][2] +
	3*hh2*mapCoefs[ln][3] +
	ss*mapCoefs[ln][5] +
	2*hh*ss*mapCoefs[ln][6] +
	3*hh2*ss*mapCoefs[ln][7] +
	ss2*mapCoefs[ln][9] +
	2*hh*ss2*mapCoefs[ln][10] +
	3*hh2*ss2*mapCoefs[ln][11] +
	ss3*mapCoefs[ln][13] +
	2*hh*ss3*mapCoefs[ln][14] +
	3*hh2*ss3*mapCoefs[ln][15];				

	outputVal[2] =  
	mapCoefs[ln][4] +
	hh*mapCoefs[ln][5] +
	hh2*mapCoefs[ln][6] +
	hh3*mapCoefs[ln][7] +
	2*ss*mapCoefs[ln][8] +
	2*hh*ss*mapCoefs[ln][9] +
	2*hh2*ss*mapCoefs[ln][10] +
	2*hh3*ss*mapCoefs[ln][11] +
	3*ss2*mapCoefs[ln][12] +
	3*hh*ss2*mapCoefs[ln][13] +
	3*hh2*ss2*mapCoefs[ln][14] +
	3*hh3*ss2*mapCoefs[ln][15];


	outputVal[0] -= mapCoefsOmni[ln][0] + 
	hh*mapCoefsOmni[ln][1] +
	hh2*mapCoefsOmni[ln][2] +
	hh3*mapCoefsOmni[ln][3] +
	ss*mapCoefsOmni[ln][4] +
	hh*ss*mapCoefsOmni[ln][5] +
	hh2*ss*mapCoefsOmni[ln][6] +
	hh3*ss*mapCoefsOmni[ln][7] +
	ss2*mapCoefsOmni[ln][8] +
	hh*ss2*mapCoefsOmni[ln][9] +
	hh2*ss2*mapCoefsOmni[ln][10] +
	hh3*ss2*mapCoefsOmni[ln][11] +
	ss3*mapCoefsOmni[ln][12] +
	hh*ss3*mapCoefsOmni[ln][13] +
	hh2*ss3*mapCoefsOmni[ln][14] +
	hh3*ss3*mapCoefsOmni[ln][15];

	outputVal[1] -= 
	mapCoefsOmni[ln][1] +
	2*hh*mapCoefsOmni[ln][2] +
	3*hh2*mapCoefsOmni[ln][3] +
	ss*mapCoefsOmni[ln][5] +
	2*hh*ss*mapCoefsOmni[ln][6] +
	3*hh2*ss*mapCoefsOmni[ln][7] +
	ss2*mapCoefsOmni[ln][9] +
	2*hh*ss2*mapCoefsOmni[ln][10] +
	3*hh2*ss2*mapCoefsOmni[ln][11] +
	ss3*mapCoefsOmni[ln][13] +
	2*hh*ss3*mapCoefsOmni[ln][14] +
	3*hh2*ss3*mapCoefsOmni[ln][15];				

	outputVal[2] -=  
	mapCoefsOmni[ln][4] +
	hh*mapCoefsOmni[ln][5] +
	hh2*mapCoefsOmni[ln][6] +
	hh3*mapCoefsOmni[ln][7] +
	2*ss*mapCoefsOmni[ln][8] +
	2*hh*ss*mapCoefsOmni[ln][9] +
	2*hh2*ss*mapCoefsOmni[ln][10] +
	2*hh3*ss*mapCoefsOmni[ln][11] +
	3*ss2*mapCoefsOmni[ln][12] +
	3*hh*ss2*mapCoefsOmni[ln][13] +
	3*hh2*ss2*mapCoefsOmni[ln][14] +
	3*hh3*ss2*mapCoefsOmni[ln][15];

	return outputVal;
    }	



    public String toString() {
    	String str = "\nTwo torsions element. Made of:\n--------------------\nTorsion1:";
    	str = str.concat(torsion1.toString());
    	str = str.concat("\nTorsion2:");
    	str = str.concat(torsion2.toString());
    	str = str.concat("\n");
    	return str;
    }
    
    protected void setAtoms() {
    	atoms = new AtomList();
    	atoms.add(torsion1.atom1);
    	atoms.add(torsion1.atom2);
    	atoms.add(torsion1.atom3);
    	atoms.add(torsion1.atom4);
    	atoms.add(torsion2.atom1);
    	atoms.add(torsion2.atom2);
    	atoms.add(torsion2.atom3);
    	atoms.add(torsion2.atom4);
    }	


}