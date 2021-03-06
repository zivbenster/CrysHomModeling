package meshi.geometry;

import meshi.molecularElements.Atom;

import java.util.Arrays;
import java.util.Iterator;

public  class MatrixRow {
    public final Atom atom;
    private final int number;
    private int searchStart = 0;
    private final MatrixRow[] matrix;
    private final double rMax2;
    private final double rMaxPlusBuffer2;
    private Distance[] distances;
    private int size;
    private int capacity;
    


     
    public MatrixRow(Atom atom, int capacity, MatrixRow[] matrix) {
	this.capacity = capacity;
        distances = new Distance[capacity];
	size = 0;                 
	this.atom = atom;
	number = atom.number();
	this.matrix = matrix;
	rMax2 = DistanceMatrix.rMax2;
	rMaxPlusBuffer2 = DistanceMatrix.rMaxPlusBuffer2;

    }
	
    public int update() {
	boolean doneRow = false;
	int out = 0;
	for (int iDistance = 0; (iDistance < size) & (! doneRow); iDistance++) {
	    Distance distance = distances[iDistance];
	    int atom2number = distance.atom2Number();
	    if (atom2number >= number) doneRow = true;
	    else {
		    boolean keep;
		    try {
			keep = distance.update(rMax2,rMaxPlusBuffer2);
		    }
		    catch (RuntimeException ex) { System.out.println("xxxxx "+this+" "+atom+" "+number+" "+atom2number+
				    "\n"+distance);
		                         throw ex;
		    }
		if (!keep) {
		    remove(atom2number);
		    matrix[atom2number].remove(number);
		    iDistance--;
		}
		else out++;
	    }
	}
	return out;
    }

    public int updateIncludingMirrors() {
	int out = 0;
	for (int iDistance = 0; (iDistance < size); iDistance++) {
	    Distance distance = distances[iDistance];
	    int atom2number = distance.atom2Number();
	    if (atom2number >= number) distance = ((DistanceMirror) distance).source;
	    boolean keep = distance.update(rMax2,rMaxPlusBuffer2);
	    if (!keep) {
		remove(atom2number);
		matrix[atom2number].remove(number);
		iDistance--;
	    }
	    else out++;
	}
	return out;
    }

    protected Distance distanceAt(int index) {
      if (index >= size) 
	  throw new RuntimeException("index out of bounds "+index);
      return distances[index];
    }  
  
    public int size() {return size;}
    
    final Distance binarySearch(int key){
	int low = 0, middle, high = size-1;
	Distance distance;
	int atom2number;
	while  (low <= high )   {
	    middle = (low+high)>>1;
	    distance =  distances[middle];
	    atom2number = distance.atom2Number;
	    if (key == atom2number)
		return distance;
	    else 
		if (key <  atom2number) high = middle - 1;
		else low = middle + 1;
	}
	return null;
    }
    private void resetSerialSearch() {searchStart = 0;}

    private Distance serialBinarySearch(int key){
	int low = searchStart, middle, high = size-1;
	Distance distance;
	int atom2number;
	while  (low <= high )   {
	    middle = (low+high)>>1;
	    distance = distances[middle];
	    atom2number = distance.atom2Number;
	    if (key == atom2number) {
                searchStart = middle+1;
		return distance;
            }
	    else 
		if (key <  atom2number) high = middle - 1;
		else low = middle + 1;
	}
	return null;
    }
      
    protected final Distance serialSearch(int key ){
	Distance distance = null;
	int searchIndex = searchStart;
	int atom2number = -1;

	while ((searchIndex < size) &&
	       ((atom2number = (distance = distances[searchIndex]).atom2Number) < key)) 
	    searchIndex++;
	if (atom2number == key) {
	    atom2number = -1;
	    searchStart = searchIndex+1;
	    return distance;
	}
	searchIndex = searchStart-1;
	if (searchIndex >= size) searchIndex = size-1;
	while ((searchIndex >= 0) &&
	       ((atom2number = (distance = distances[searchIndex]).atom2Number) > key))
	    searchIndex--;
	if (atom2number == key) {
	    searchStart = searchIndex+1;
	    atom2number = -1;	
	    return distance;
	}
	return null;
    }
    
    
    private Distance remove(int key) {
	int low = 0, middle, high = size-1;
	Distance dis;
	int atom2number;
	while  (low <= high )   {
	    middle = (low+high)>>1;
	    dis =  distances[middle];
	    atom2number = dis.atom2Number;
	    if (key == atom2number){
		    System.arraycopy(distances, middle + 1, distances, middle, size - 1 - middle);
		size--;
		return dis;
	    }
	    else 
		if (key <  atom2number) high = middle - 1;
		else low = middle + 1;
	}
	throw new RuntimeException("Trying to remove ("+atom.number()+","+key+") from a matrix row: \n"+
				   atom+"\nthat does not include it.");
    }
    public String toString() {
	   String out =  "MatrixRow atom = "+atom+" number = "+number+"\n";
	    for (Distance distance : distances)
		    out += distance + " ; ";
	   return out;
    }
	    
    public void addCell(GridCell cell){
	double x = atom.x();
	double y = atom.y();
	double z = atom.z();
	double dx, dy, dz, d2; 
	Iterator cellAtoms = cell.iterator();
	Atom cellAtom;
	int largeNumber = -1, smallNumber = -1;
	Distance distance = null;
	double rMaxPlusBuffer2 = DistanceMatrix.rMaxPlusBuffer2;
	double rmax2 = rMax2;
	try {
	resetSerialSearch();
	while ((cellAtom = (Atom) cellAtoms.next())!= null) {
	    if (serialBinarySearch(cellAtom.number())== null){
              if (cellAtom != atom) {
		dx = x-cellAtom.x();
		dy = y-cellAtom.y();
		dz = z-cellAtom.z();
		d2 = dx*dx+dy*dy+dz*dz;             
		if (d2 < rMaxPlusBuffer2) {		   
			int cellAtomNumber = cellAtom.number(); 
			if (number > cellAtomNumber) {
			    largeNumber = number;
			    smallNumber = cellAtomNumber;
			    if (atom.frozen() & cellAtom.frozen()) 
				distance = new FrozenDistance(atom,cellAtom,
							      rMax2,rMaxPlusBuffer2);
			    else distance = new Distance(atom,cellAtom,d2,dx,dy,dz,rmax2);
			}
			else {
			    largeNumber = cellAtomNumber;
			    smallNumber = number;
			    if (atom.frozen() & cellAtom.frozen()) 			    
				distance = new FrozenDistance(cellAtom,atom,
							      rMax2,rMaxPlusBuffer2);
			    else distance = new Distance(cellAtom,atom,d2,-dx,-dy,-dz,rmax2);
			}
			matrix[largeNumber].insert(distance);
			matrix[smallNumber].insert(new DistanceMirror(distance));
		}               
	      }
	    }
	}	
	}
	catch (RuntimeException ex) {System.out.println("xxxxxx \n"+this); throw ex;}
    }
    
    public void print() {
	for (int i = 0; i < size; i++)
	    System.out.println(distances[i]);
    }
	
    public boolean add(Distance d) {
	boolean out = addNoSort(d);
	sort();
	return out;
    }

    
    private boolean addNoSort(Distance d){
        if (size < capacity) {
              distances[size] = d;
              size++;              
              return true;
        }
        else {
              capacity *= 1.5;
              Distance[] newArray = new Distance[capacity];
	        System.arraycopy(distances, 0, newArray, 0, size);
              distances = newArray;
              return add(d) ;
              }        
    }        
    
    private void sort(){
        Arrays.sort(distances,0,size);     
    }        
   
    private int insert(Distance d) {
        if (size+1>= capacity) {
              capacity *= 1.5;
              Distance[] newArray = new Distance[capacity];
	        System.arraycopy(distances, 0, newArray, 0, size);
              distances = newArray;
              return insert(d) ;
        }
        if (size == 0) {
              distances[0] = d;
              size++;
              return 0;
        }
        if (distances[0].atom2Number > d.atom2Number) {
	        System.arraycopy(distances, 0, distances, 1, size);
              distances[0] = d;
              size++;
              return 0;        
        }
        for (int i = 0; i < size - 1; i++)                 
        if ((distances[i].atom2Number < d.atom2Number) & (distances[i+1].atom2Number >= d.atom2Number)) {
	        System.arraycopy(distances, i + 1, distances, i + 1 + 1, size - (i + 1));
               distances[i+1] = d;
               size++;
               return i+1;	                            
        }	   	
        distances [size] = d;	
        size++;
        return size;	
    }
          
    public Iterator nonBondedIterator() {
	return new RowIterator();
    }
    private class RowIterator implements Iterator {
	int current;
	public RowIterator() {
	    current = 0;
	}
	public Object next() {
	    if (current >=size) return null;
	    Distance distance = distances[current];
	    if (distance.mirror) return null;
	    current++;
	    if (distance.distance() < Distance.INFINITE_DISTANCE) return distance;
	    else return next();
	}
            
	public boolean hasNext() {
	    return( current < size);
	}
	public void remove() {
	    throw new RuntimeException("not implemented");
	}

    }
    Distance[] distances(){	return distances;}

}

