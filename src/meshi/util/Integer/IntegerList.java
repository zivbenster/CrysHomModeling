package meshi.util.Integer;

import meshi.util.SortableMeshiList;
import meshi.util.filters.Filter;
import meshi.util.string.StringList;

import java.lang.reflect.Array;
import java.util.Comparator;
import java.util.Iterator;

// constructors
public class IntegerList extends SortableMeshiList implements Comparator{
    private IntegerList() {
	super(new IsInteger());
    }
    public IntegerList(int size) {
	this();
	for (int i = 0;i < size; i++)
	    add(0);
    }
    public IntegerList(String aStringWithIntegerNumbers){
	this();

	String integerString;
	Iterator SI = 
	    (new StringList(aStringWithIntegerNumbers,
			     StringList.standardSeparators())).iterator();
	while ((integerString = (String) SI.next()) != null)
	    add(new Integer(integerString.trim()));
    }
    public IntegerList(Object[] array) {
	this();
	for (int i = 0; i < Array.getLength(array);i++)
	    add(array[i]);
    }
    // integer shortcuts 
    private void add(int i) {
	add(new Integer(i));
    }
    public int integerAt(int index) {
	return (Integer) elementAt(index);
    }
    public double doubleAt(int index) {
	return  ((Integer) elementAt(index)).doubleValue();
    }

    // sort
    public int compare(Object o1, Object o2) {
	return ((Integer) o1).compareTo((Integer) o2);
    }
    
    static class IsInteger implements Filter {
	public boolean accept(Object obj) {
		return (obj instanceof Integer);
	}
    }
    public boolean sortable() {return true;}
    private void set(int index, int value) {
	    set(index,new Integer(value));
	}

    public void increment(int index) {
	set(index, (Integer) elementAt(index) +1);
    }

}
