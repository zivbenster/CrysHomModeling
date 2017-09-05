package programs;

import meshi.PDB.PdbLine;
import meshi.geometry.Distance;
import meshi.molecularElements.*;
import meshi.parameters.AtomTypes;
import meshi.parameters.Residues;
import meshi.sequences.Sequence;
import meshi.sequences.SequenceCharFilter;
import meshi.util.MeshiLog;
import meshi.util.MeshiProgram;
import meshi.util.file.MeshiWriter;
import meshi.util.filters.Filter;

import java.util.Iterator;

public class Pdb2FastaFailSafe extends MeshiProgram implements Residues, AtomTypes {
    private static final String MONOMERS_TO_IGNORE = ("HOH HEM GDP TRS LLP CA CS K CAC AMP ALF SO4 AP5 ACE AXT 3PP PO4 "+
						     "D12 CO UNK IHP FBP PGA FAD BME NAG DAC ADP FMN MN FE HEA B12 "+
						     "GTS GOL T5A MG NAP CU DCA CL ZN PER POP CIT AGM CMP GL3 MGN ORO "+
						     "F43 PGC TP7 SHT EGL CYO FS4 COM COB PYR GOA ISP 3PY CBX NH2 NAD "+
						     "TPO SM IPS SPS NDP FUC CAS C20 PLP PHO DCD TAU FE2 POH HEC NMO");
    private static final String STANDARD_RESIDUES = ("ALA CYS ASP GLU PHE GLY HIS ILE LYS LEU "+
						    "MET ASN PRO GLN ARG SER THR VAL TRP TYR");
    private static final String[][] dictionary = { join("MSE", "MET"),
						  join("ARO", "ARG"),
						  join("CSE", "CYS"), //selenium cysteine
						  join("CSZ", "CYS"), //selenium cysteine
						  join("MSE", "MSE"), //selenium methionine
						  join("SLN", "MET"), //selenium methionine
						  join("YCM", "MET"), 
						  join("OCS", "CYS"), //CYSTEINESULFONIC ACID
						  join("CSW", "CYS"), //CYSTEINE-S-DIOXIDE
						  join("SMC", "CYS"), //CYSTEINE RESIDUE METHYLATED IN S-POSITION
						  join("CYG", "CYS"), //GLUTAMYL-S-CYSTEINE 
						  join("CCS", "CYS"), 
						  join("CSX", "CYS"), 
						  join("SEP", "SER"), //PHOSPHOSERINE
						  join("ASQ", "ASP"), //PHOSPHOASPARTATE
						  join("PHD", "ASP"), //PHOSPHORYLATION
						  join("PCA", "GLU"), //PYROGLUTAMIC ACID
						  join("MHS", "HIS"), 
						  join("CME", "CYS")};//S,S-(2-HYDROXYETHYL)THIOCYSTEINE
    private static String[] join(String s0, String s1) {
	String[] out = new String[2];
	out[0] = s0;
	out[1] = s1;
	return out;
    }
					  
    public static void main(String args[]) {
	if (args.length != 4 ) 
	    throw new RuntimeException("\n"+"Usage: Pdb2Fasta filenmae [chain] output.pdb output.fasta"+"\n"+
				       "note that a \'_\' sign should be used if no chain is specified");
	busha();
	String fileName = args[0];
	String chain = args[1];
	if (chain.equals("_")) chain = " ";
	MeshiWriter pdbOut = openFile(args[2]);
	MeshiWriter fastaOut = openFile(args[3]);
	MeshiLog log = new MeshiLog();
	log.add("REMARK File generated by the command:");
	log.add("REMARK Pdb2Fasta "+args[0]+" "+args[1]+" "+args[2]+" "+args[3]);

	AtomList originalAtomsList = new AtomList(fileName,new MyFilter(chain));
	//	originalAtomsList.print();

	AtomList newAtomList = getNewAtomList(originalAtomsList, log);
	newAtomList.renumber();
	ResidueList residueList = new MyResidueList(newAtomList, log);
	log.add("REMARK Chain length "+(residueList.size() - 1));
	log.print(pdbOut);
	residueList.atoms().print(pdbOut);
	Sequence sequence = new Sequence(residueList.toString(), originalAtomsList.comment()+" chain "+chain ,
	new ResidueSequenceCharFilter());
	fastaOut.println(sequence);
 	fastaOut.flush();
   }
	
    private static AtomList getNewAtomList(AtomList originalAtomsList, MeshiLog log) {
	AtomList newAtomList = new AtomList();
	String toIgnoreFound = "";
	String toConvertFound = "";
	    
	for (Iterator atoms = originalAtomsList.iterator(); atoms.hasNext();) {
	    Atom atom = (Atom) atoms.next();
	    String residueName = atom.residueName();
	    if (STANDARD_RESIDUES.contains(residueName)) {
		newAtomList.add(atom);
	    }
	    else {
		if (MONOMERS_TO_IGNORE.contains(residueName)) {
		    if (!toIgnoreFound.contains(residueName)) {
			log.add(residueName+" monomers were ignored");
			toIgnoreFound += " "+residueName;
		    }
		}
		else {
		    boolean found = false;
		    for (int i = 0; i < dictionary.length & (! found); i++) {
			if (residueName.equals(dictionary[i][0])) {
			    if (!toConvertFound.contains(residueName)) {
				log.add("REMARK "+residueName+" was converted to "+dictionary[i][1]);
				toConvertFound += " "+residueName;
			    }
			    residueName = dictionary[i][1];
			    atom = new MyAtom(atom,residueName);
			    newAtomList.add(atom);
			    found = true;
			}
		    }
		    //if (! found) throw new RuntimeException("Weird atom "+atom);
		    if (! found) log.add("******************************************\n"+
					 "Pdb2Fasta WARNING: Weird atom "+atom+"\n"+
					 "******************************************\n");
		}
	    }
	}
	return newAtomList;
    }
    
    private static MeshiWriter openFile(String fileName) {
	try {
	    return new MeshiWriter(fileName);
	}
	catch (Exception ex) { throw new RuntimeException("Cannot open "+fileName+" for writing   \n"+ex);}
    }

				
    private static class MyResidueList extends ResidueList {
	private Residue previousResidue = null;
	public MyResidueList(AtomList atomList, MeshiLog log) {
	    super();
	    Iterator atoms = atomList.iterator();
	    Residue PreviousResidue = null;
	    if (! atoms.hasNext()) throw new RuntimeException(" No Atoms in AtomList "+atomList.comment());
	    Atom first = (Atom) atoms.next();
	    if (! atoms.hasNext()) throw new RuntimeException(" Not enough Atoms in AtomList "+atomList.comment());
	    Atom atom = first;
	    while (atoms.hasNext()) {
		Object[] temp = getResidue(atoms, first);
		Residue residue = (Residue) temp[0];
		first = (Atom) temp[1];
		if (residue.ca() == null) {
		    residue.atoms().print();
		    log.add("******************************************\n"+
			    "Pdb2Fasta WARNING: No CA in "+residue+"\n"+
			    "******************************************\n");
		}
		else {
		    if (residue.number <= 0) log.add("REMARK residue "+residue+" ignored - non-positive residue number");
		    else {
			if (previousResidue != null) {
			    if (previousResidue.number >= residue.number){
				log.add("Weird residues "+previousResidue+"\n"+residue);
				continue;
			    }
			    else {
				if (previousResidue.number == residue.number-1) {
				    double distance = (new Distance(previousResidue.ca(),residue.ca())).distance();
				    if (distance > 4.2) {
					log.add("*****************************************\n"+
						"Pdb2Fasta WARNING:Consecutive CA atoms are too far away.\ndistance = "+
						distance+"   between these residues CAs:"+previousResidue+
						"  "+residue+
						"\n******************************************\n");
					continue;
				    }
				}
				if (previousResidue.number < residue.number-1)
				    log.add("REMARK Gap between "+previousResidue+" and "+residue);
			    }
			}
			add(residue, previousResidue, log);
			previousResidue = residue;
		    }
		}
	    }
	}
	Object[] getResidue(Iterator atoms, Atom first) {
	    Object[] out = new Object[2];
	    AtomList atomList = new AtomList();
	    Atom atom = first;
	    atomList.add(first);
	    while (atoms.hasNext() & (atom.residueNumber() == first.residueNumber())) {
		atom = (Atom) atoms.next();
		if (atom.residueNumber() == first.residueNumber()) {
		    atomList.add(atom);
		}
	    }
	    out[1] = atom; // the next first;
	    out[0] = new MyResidue(atomList);
	    return out;
	}		
	boolean add(Residue residue, Residue previousResidue, MeshiLog log) {
	    if (residue.number <= 0) throw new RuntimeException("Residue "+residue+" with number <= 0 cannot be added");
	    int iPrevious;
	    if (previousResidue == null) 
		iPrevious = -1;
	    else iPrevious = previousResidue.number;

	    for (int i = iPrevious+1 ; i < residue.number; i++) {
		DummyResidue newResidue = new DummyResidue(i);
		log.add("REMARK filling the gap with "+newResidue);
	    super.add(newResidue);
	    }
	    previousResidue = residue;
	    return super.add(residue);
	}
    }
    

    // public Residue(String name, int type, int number, 
    // 		   AtomList atomList, int mode) {

    private static class MyResidue extends Residue {
	public MyResidue(AtomList atomList) {
	    super(getName(atomList),getType(atomList),getNumber(atomList),NORMAL);
	    atoms = atomList;
	}
	private static String getName(AtomList atomList) {
	    return ((Atom) atomList.elementAt(0)).residueName();
	}
	private static int getNumber(AtomList atomList) {
	    return ((Atom) atomList.elementAt(0)).residueNumber();
	}
	private static int getType(AtomList atomList) {
	    String name = getName(atomList);
	    int out;
	    try {
		out =  Residue.type(name);
	    }
	    catch (Exception ex) {out = -1;}
	    return out;
	}
    }
	
    private static class ResidueSequenceCharFilter extends SequenceCharFilter {
	public boolean accept(Object obj) {
	    Character c = (Character) obj;
		return "XACDEFGHIKLMNOPQRSTVWY-".indexOf(c) >= 0;
	}
    }

 
    private static class MyAtom extends Atom {
	public MyAtom(Atom atom, String residueName){
	    super(atom.name, residueName , atom.residueNumber() , "A",
		  atom.type);
	    this.alternateLocation = atom.alternateLocation();
	    this.temperatureFactor = atom.temperatureFactor();
	    this.setXYZ(atom.x(),atom.y(),atom.z());
	}
    }

    private static class MyFilter implements Filter {
	private final String chain;
	public MyFilter(String chain) {
	    this.chain = chain;
	}
	public boolean accept(Object obj) {
	    PdbLine line = (PdbLine) obj;
	    return (line.isAnAtomOrHeteroAtom() && line.chain().equals(chain));
	}
    }

    private static void busha() {
	int zvl = ALA; // force the reading of "meshi.parameters.Residues"
	zvl = ACA;// force the reading of "meshi.parameters.AtomTypes"
    }

}
 
