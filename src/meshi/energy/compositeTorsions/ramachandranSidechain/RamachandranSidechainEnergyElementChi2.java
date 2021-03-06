package meshi.energy.compositeTorsions.ramachandranSidechain;

import meshi.energy.compositeTorsions.CompositeTorsionsDefinitions;
import meshi.energy.compositeTorsions.ResidueTorsions;

/** Ramachandran/Sidechain energy element for amino acids with no
 * sidechain torsion angles: ASP, PHE, HIS, ILE, LEU, ASN, PRO,
 * TRP, TYR.
 * 
 * @author El-ad David Amir
 *
 */
public class RamachandranSidechainEnergyElementChi2
	extends RamachandranSidechainEnergyElement
	implements CompositeTorsionsDefinitions {

	public RamachandranSidechainEnergyElementChi2(
			ResidueTorsions residueTorsions,
			RamachandranSidechainParameters rsp,
			double weight ) {
		super( residueTorsions, rsp, weight );
	}
	
	protected boolean legalResidueType() {
		return (residueTorsions.getResidueType() == ASP ||
				residueTorsions.getResidueType() == PHE ||
				residueTorsions.getResidueType() == HIS ||
				residueTorsions.getResidueType() == ILE ||
				residueTorsions.getResidueType() == LEU ||
				residueTorsions.getResidueType() == ASN ||
				residueTorsions.getResidueType() == PRO ||
				residueTorsions.getResidueType() == TRP ||
				residueTorsions.getResidueType() == TYR);
	}

	public double evaluate() {
		/* verify energy element is not frozen */
		if( frozen() ) return 0.0;
		
		/* calcualte energy and derivative */
		double energy       = rsp.evaluate( 0, residueTorsions );
		double phi_deriv	= rsp.evaluate( PHI, residueTorsions );
		double psi_deriv	= rsp.evaluate( PSI, residueTorsions );
		double chi_1_deriv  = rsp.evaluate( CHI_1, residueTorsions );
		double chi_2_deriv  = rsp.evaluate( CHI_2, residueTorsions );
		
		/* apply weight */
		energy *= weight;
		phi_deriv *= weight;
		psi_deriv *= weight;
		chi_1_deriv *= weight;
		chi_2_deriv *= weight;

		/* apply force to torsions */
		residueTorsions.applyForce( PHI, -phi_deriv );
		residueTorsions.applyForce( PSI, -psi_deriv );
		residueTorsions.applyForce( CHI_1, -chi_1_deriv );
		residueTorsions.applyForce( CHI_2, -chi_2_deriv );

		monitor();
		return energy;
	}

}
