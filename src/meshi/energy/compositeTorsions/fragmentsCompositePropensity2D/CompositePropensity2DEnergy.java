package meshi.energy.compositeTorsions.fragmentsCompositePropensity2D;

import meshi.energy.EnergyElement;
import meshi.energy.Parameters;
import meshi.energy.SimpleEnergyTerm;
import meshi.energy.compositeTorsions.CompositeTorsionsDefinitions;
import meshi.energy.compositeTorsions.ResidueTorsions;
import meshi.energy.compositeTorsions.ResidueTorsionsList;
import meshi.geometry.DistanceMatrix;

// --Commented out by Inspection START (16:38 31/10/16):
//public class CompositePropensity2DEnergy
//	extends SimpleEnergyTerm
//	implements CompositeTorsionsDefinitions {
//
//	public CompositePropensity2DEnergy() {}
//
//	public CompositePropensity2DEnergy(
//			ResidueTorsionsList residueTorsionsList,
//			DistanceMatrix distanceMatrix,
//			CompositePropensity2DParametersList cppl,
//			double weight,
//			String comment) {
//		super( toArray(distanceMatrix, residueTorsionsList), cppl, weight );
//
//		this.comment = comment;
//		createElementsList( residueTorsionsList );
//	}
//
//	public EnergyElement createElement(Object baseElement, Parameters parameters) {
//		ResidueTorsions resTorsions =
//			(ResidueTorsions) baseElement;
//		CompositePropensity2DParameters cpp =
//			(CompositePropensity2DParameters) parameters;
//
//		return new CompositePropensity2DEnergyElement(
//					resTorsions, cpp, weight );
//	}
//
//}
// --Commented out by Inspection STOP (16:38 31/10/16)
