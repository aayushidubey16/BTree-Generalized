package main.java;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Stack;
import java.util.function.Consumer;

/**
 * The Class BTreeSet.
 *
 * @param <E> the element type
 */
public class BTreeSet<E>
extends AbstractSet<E>
implements Cloneable, java.io.Serializable
{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
     * The comparator used to maintain order in this BTree set, or
     * null if it uses the natural ordering of its entries.
     *
     */
	private final Comparator<? super E> comparator;
	
	/** The root. */
    private Entry<E> root;
    
    /** The number of entries in the set. */
    private int size = 0;
    
    /**
     * Instantiates a new b tree set.
     */
    public BTreeSet() {
        comparator = null;
    }

    /**
     * Instantiates a new b tree set with comparator for ordering its entries.
     *
     * @param comparator the comparator
     */
    public BTreeSet(Comparator<? super E> comparator) {
        this.comparator = comparator;
    }
    
    /**
     * Returns the number of entries in this set.
     *
     * @return the number of entries in this set
     */
    public int size() {
        return size;
    }
	
	/**
	 * Adds the entry to BTree set.
	 *
	 * @param entry the entry
	 * @return true, if successful
	 */
	@Override
	public boolean add(E entry) {
		size++;
		return add(entry, root, null, 0) == null;
	}
	
	/**
	 * Adds the entry to BTree set recursively.
	 *
	 * @param entry the entry
	 * @param currentNode the current node
	 * @param parentNode the parent node
	 * @param currentNodeIndex the current node index
	 * @return the e
	 */
	private E add(E entry, Entry<E> currentNode, Entry<E> parentNode, Integer currentNodeIndex) {
        if (currentNode == null) {
            addEntryToEmptyMap(entry);
            return null;
        }
		E overFlownEntry = null;
		// If node is a leaf node then assign the student data to it.
		if(currentNode.isEntryLeaf) {
			overFlownEntry = addEntryAtLeafEntry(entry, currentNode, parentNode, currentNodeIndex, overFlownEntry);
		} else {// If node is not a leaf node then assign the student data to it.
			overFlownEntry = addEntryAtInternalEntry(entry, currentNode, parentNode, currentNodeIndex, overFlownEntry);
		}
		return overFlownEntry;
	}

	/**
	 * Adds the entry at internal entry.
	 *
	 * @param entry the entry
	 * @param currentNode the current node
	 * @param parentNode the parent node
	 * @param currentNodeIndex the current node index
	 * @param overFlownEntry the over flown entry
	 * @return the e
	 */
	private E addEntryAtInternalEntry(E entry, Entry<E> currentNode, Entry<E> parentNode, Integer currentNodeIndex,
			E overFlownEntry) {
		if(currentNode.isEntryFull) {
			int firstEntryCompare = compare(currentNode.firstInnerEntry.innerEntry, entry);
			int secondEntryCompare = compare(currentNode.secondInnerEntry.innerEntry, entry);
			int childIndex = 0;
			if(firstEntryCompare < 0 && secondEntryCompare < 0) {
				childIndex = 2;
			} else if(secondEntryCompare > 0 && firstEntryCompare < 0) {
				childIndex = 1;
			}
			Entry<E> childEntry = currentNode.getChild(childIndex);
			E newOverFlownEntry = add(entry, childEntry, currentNode, childIndex);
			if(newOverFlownEntry != null ) {
				Entry<E> leftEntry = currentNode;
				Entry<E> rightEntry = new Entry<>();
				rightEntry.isEntryLeaf = Boolean.FALSE;
				overFlownEntry = shuffelEnteriesToAddNewEntry(newOverFlownEntry, currentNode,
						leftEntry, rightEntry);
				fixAfterAdd(leftEntry, rightEntry);
				overFlownEntry = addNodeAtRootLevel(parentNode, currentNodeIndex, overFlownEntry, leftEntry, rightEntry);
			}
		} else {// Need to check lexicographical order and insert the node
			int compare = compare(currentNode.firstInnerEntry.innerEntry, entry);
			int childIndex = 0;
			if(compare < 0) {
				childIndex = 1;
			} 
			Entry<E> childEntry = currentNode.getChild(childIndex);
			E newOverFlownEntry = add(entry, childEntry, currentNode, childIndex);
			if (newOverFlownEntry != null) {
				if (childIndex == 1) {
					currentNode.secondInnerEntry = new InnerEntry<E>(newOverFlownEntry);
				} else if (childIndex == 0) {
					currentNode.secondInnerEntry = currentNode.firstInnerEntry;
					currentNode.firstInnerEntry = new InnerEntry<E>(newOverFlownEntry);
				}
				currentNode.isEntryFull = Boolean.TRUE;
			}
		}
		return overFlownEntry;
	}

	/**
	 * Fix after add.
	 *
	 * @param leftEntry the left entry
	 * @param rightEntry the right entry
	 */
	private void fixAfterAdd(Entry<E> leftEntry, Entry<E> rightEntry) {
		leftEntry.secondInnerEntry = NullInnerEntry.getInstance();
		leftEntry.isEntryFull = Boolean.FALSE;
		rightEntry.left = leftEntry.right;
		rightEntry.middle = leftEntry.overFlown;
		leftEntry.right = NullEntry.getInstance();
		leftEntry.overFlown = NullEntry.getInstance();
	}

	/**
	 * Adds the node at root level.
	 *
	 * @param parentNode the parent node
	 * @param currentNodeIndex the current node index
	 * @param overFlownEntry the over flown entry
	 * @param leftEntry the left entry
	 * @param rightEntry the right entry
	 * @return the e
	 */
	private E addNodeAtRootLevel(Entry<E> parentNode, Integer currentNodeIndex, E overFlownEntry, Entry<E> leftEntry,
			Entry<E> rightEntry) {
		if (parentNode != null) {
			parentNode.adjustChild(currentNodeIndex, leftEntry, rightEntry);
		} else { // while splitting if we reached root node and that node is also overflowing then split it into two and make new root node
			Entry<E> newRootNode = new Entry<>();
			newRootNode.isEntryLeaf = Boolean.FALSE;
			newRootNode.left = leftEntry;
			newRootNode.middle = rightEntry;
			newRootNode.firstInnerEntry = new InnerEntry<E>(overFlownEntry);
			overFlownEntry = null;
			this.root = newRootNode;
		}
		return overFlownEntry;
	}

	/**
	 * Adds the entry at leaf entry.
	 *
	 * @param entry the entry
	 * @param currentNode the current node
	 * @param parentNode the parent node
	 * @param currentNodeIndex the current node index
	 * @param overFlownEntry the over flown entry
	 * @return the e
	 */
	private E addEntryAtLeafEntry(E entry, Entry<E> currentNode, Entry<E> parentNode, Integer currentNodeIndex,
			E overFlownEntry) {
		//If node is full then need to split it into two and assign overflow student of reference.
		if(currentNode.isEntryFull) {
			Entry<E> leftEntry = currentNode;
			Entry<E> rightEntry = new Entry<>();
			overFlownEntry = shuffelEnteriesToAddNewEntry(entry, currentNode, leftEntry, rightEntry);
			leftEntry.secondInnerEntry = NullInnerEntry.getInstance();
			leftEntry.isEntryFull = Boolean.FALSE;
			overFlownEntry = addNodeAtRootLevel(parentNode, currentNodeIndex, overFlownEntry, leftEntry, rightEntry);
			
		} else {// Need to check lexicographical order and insert the node and assign null to overflow reference
			int compare = compare(currentNode.firstInnerEntry.innerEntry, entry);
			if(compare > 0) {
				currentNode.secondInnerEntry = currentNode.firstInnerEntry;
				currentNode.firstInnerEntry = new InnerEntry<E>(entry);
			} else {
				currentNode.secondInnerEntry = new InnerEntry<E>(entry);
			}
			currentNode.isEntryFull = Boolean.TRUE;
		}
		return overFlownEntry;
	}

	/**
	 * Shuffel enteries to add new entry.
	 *
	 * @param entry the entry
	 * @param currentNode the current node
	 * @param leftEntry the left entry
	 * @param rightEntry the right entry
	 * @return the e
	 */
	private E shuffelEnteriesToAddNewEntry(E entry, Entry<E> currentNode, Entry<E> leftEntry,
			Entry<E> rightEntry) {
		int firstEntryCompare = compare(currentNode.firstInnerEntry.innerEntry, entry);
		int secondEntryCompare = compare(currentNode.secondInnerEntry.innerEntry, entry);
		E overFlownEntry;
		if(firstEntryCompare < 0 && secondEntryCompare > 0) {
			rightEntry.firstInnerEntry = currentNode.secondInnerEntry;
			overFlownEntry = entry;
		} else if(firstEntryCompare > 0 && secondEntryCompare > 0) {
			leftEntry.firstInnerEntry = new InnerEntry<E>(entry);
			rightEntry.firstInnerEntry = currentNode.secondInnerEntry;
			overFlownEntry = currentNode.firstInnerEntry.innerEntry;
		} else {
			rightEntry.firstInnerEntry = new InnerEntry<E>(entry);
			overFlownEntry = currentNode.secondInnerEntry.innerEntry;
		}
		return overFlownEntry;
	}
	
	/**
	 * Checks set contains given object.
	 *
	 * @param o the o
	 * @return true, if successful
	 */
	@Override
	public boolean contains(Object o) {
		return root.contains((E) o);
	}
	
	/**
	 * For each iterates entries in reverse in-order.
	 *
	 * @param action the action
	 */
	@Override
	public void forEach(Consumer<? super E> action) {
        Objects.requireNonNull(action);
        root.forEach(action);
    }
	
	/**
	 * Returns array of entries in in-order
	 *
	 * @return the object[]
	 */
	@Override
	public Object[] toArray() {
		ArrayList<E> list = new ArrayList<>();
		root.addToArrayList(list);
		E[] entries = (E[]) new Object[list.size()];
		return list.toArray(entries);
	}

	/**
	 * Iterator.
	 *
	 * @return the iterator
	 */
	@Override
	public Iterator<E> iterator() {
		return new BTreeSetIterator();
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return "BTreeSet [comparator=" + comparator + ", root=" + root + ", size=" + size + "]";
	}

	/**
	 * Adds the entry to empty map.
	 *
	 * @param entry the entry
	 */
	private void addEntryToEmptyMap(E entry) {
        compare(entry, entry); // type (and possibly null) check
        root = new Entry<>();
        root.firstInnerEntry = new InnerEntry<>(entry);
        size = 1;
    }
	
	/**
	 * Compare.
	 *
	 * @param existingEntry the existing entry
	 * @param entryToAdd the entry to add
	 * @return the int
	 */
	@SuppressWarnings("unchecked")
	final int compare(Object existingEntry, Object entryToAdd) {
        return comparator==null ? ((Comparable<? super E>)existingEntry).compareTo((E)entryToAdd)
            : comparator.compare((E)existingEntry, (E)entryToAdd);
    }

    /**
     * The Class Entry.
     *
     * @param <E> the element type
     */
    static class Entry<E> {
    	
	    /** The first inner entry. */
	    InnerEntry<E> firstInnerEntry;
    	
	    /** The second inner entry. */
	    InnerEntry<E> secondInnerEntry;
		
		/** The is entry full. */
		Boolean isEntryFull;
		
		/** The is entry leaf. */
		Boolean isEntryLeaf;
        
        /** The left. */
        Entry<E> left;
        
        /** The middle. */
        Entry<E> middle;
        
        /** The right. */
        Entry<E> right;
        
        /** The over flown. */
        Entry<E> overFlown;
		
        /**
         * Instantiates a new entry.
         */
        public Entry() {
			super();
			this.firstInnerEntry = NullInnerEntry.getInstance();
			this.secondInnerEntry = NullInnerEntry.getInstance();
			this.isEntryFull = Boolean.FALSE;
			this.isEntryLeaf = Boolean.TRUE;
			this.left = NullEntry.getInstance();
			this.middle = NullEntry.getInstance();
			this.right = NullEntry.getInstance();
			this.overFlown = NullEntry.getInstance();
		}
		
		/**
		 * Gets the child.
		 *
		 * @param childIndex the child index
		 * @return the child
		 */
		public Entry<E> getChild(int childIndex) {
			Entry<E> child = null;
			if(childIndex == 0) {
				child = this.left;
			} else if(childIndex == 1) {
				child = this.middle;
			} else if(childIndex == 2) {
				child = this.right;
			}
			return child;
		}
		
		/**
		 * Adjust child.
		 *
		 * @param childIndex the child index
		 * @param leftEntry the left entry
		 * @param rightEntry the right entry
		 */
		public void adjustChild(int childIndex, Entry<E> leftEntry, Entry<E> rightEntry) {
			if(childIndex == 0) {
				this.overFlown = this.right;
				this.right = this.middle;
				this.middle = rightEntry;
				this.left = leftEntry;
			} else if(childIndex == 1) {
				this.overFlown = this.right;
				this.right = rightEntry;
				this.middle = leftEntry;
			} else if(childIndex == 2) {
				this.overFlown = rightEntry;
				this.right = leftEntry;
			} 
		}
		
		/**
		 * For each.
		 *
		 * @param action the action
		 */
		public void forEach(Consumer<? super E> action) {
			right.forEach(action);
			secondInnerEntry.forEach(action);
			middle.forEach(action);
			firstInnerEntry.forEach(action);
			left.forEach(action);
		}
		
		/**
		 * Adds the to array list.
		 *
		 * @param list the list
		 */
		public void addToArrayList(ArrayList<E> list) {
			left.addToArrayList(list);
			firstInnerEntry.addToArrayList(list);
			middle.addToArrayList(list);
			secondInnerEntry.addToArrayList(list);
			right.addToArrayList(list);
		}
		
		/**
		 * Contains.
		 *
		 * @param entryToCheck the entry to check
		 * @return true, if successful
		 */
		public boolean contains(E entryToCheck) {
			return left.contains(entryToCheck) || firstInnerEntry.contains(entryToCheck) || middle.contains(entryToCheck) ||
					secondInnerEntry.contains(entryToCheck) || right.contains(entryToCheck);
		}

		/**
		 * To string.
		 *
		 * @return the string
		 */
		@Override
		public String toString() {
			return "Entry [firstInnerEntry=" + firstInnerEntry + ", secondInnerEntry=" + secondInnerEntry
					+ ", isEntryFull=" + isEntryFull + ", isEntryLeaf=" + isEntryLeaf + ", left=" + left + ", middle="
					+ middle + ", right=" + right + "]";
		}
	
    }
    
    /**
     * The Class NullEntry.
     *
     * @param <E> the element type
     */
    static class NullEntry<E> extends Entry<E> {
    	
	    /** The null entry. */
	    private static NullEntry nullEntry = new NullEntry<>();

		/**
		 * Instantiates a new null entry.
		 */
		private NullEntry() {
			super();	
		}
		
		/**
		 * Gets the single instance of NullEntry.
		 *
		 * @return single instance of NullEntry
		 */
		public static NullEntry getInstance() {
			return nullEntry;
		}
		
		/**
		 * For each.
		 *
		 * @param action the action
		 */
		@Override
		public void forEach(Consumer<? super E> action) {
		}

		/**
		 * To string.
		 *
		 * @return the string
		 */
		@Override
		public String toString() {
			return "()";
		}
		
		/**
		 * Adds the to array list.
		 *
		 * @param list the list
		 */
		@Override
		public void addToArrayList(ArrayList<E> list) {
		}
		
		/**
		 * Contains.
		 *
		 * @param entryToCheck the entry to check
		 * @return true, if successful
		 */
		@Override
		public boolean contains(E entryToCheck) {
			return Boolean.FALSE;
		}
    	
    } 
    
    /**
     * The Class InnerEntry.
     *
     * @param <E> the element type
     */
    static class InnerEntry<E> {
    	
	    /** The inner entry. */
	    E innerEntry;
    	
    	/**
	     * Instantiates a new inner entry.
	     */
	    public InnerEntry() {
			super();
		}

		/**
		 * Instantiates a new inner entry.
		 *
		 * @param innerEntry the inner entry
		 */
		public InnerEntry(E innerEntry) {
			super();
			this.innerEntry = innerEntry;
		}

		/**
		 * For each.
		 *
		 * @param action the action
		 */
		public void forEach(Consumer<? super E> action) {
    		action.accept(innerEntry);
		}
    	
    	/**
	     * Adds the to array list.
	     *
	     * @param list the list
	     */
	    public void addToArrayList(ArrayList<E> list) {
			list.add(innerEntry);
		}
    	
    	/**
	     * Contains.
	     *
	     * @param entryToCheck the entry to check
	     * @return true, if successful
	     */
	    public boolean contains(E entryToCheck) {
			return innerEntry.equals(entryToCheck);
		}

		/**
		 * To string.
		 *
		 * @return the string
		 */
		@Override
		public String toString() {
			return "InnerEntry [innerEntry=" + innerEntry + "]";
		}
		
    }
    
    /**
     * The Class NullInnerEntry.
     *
     * @param <E> the element type
     */
    static class NullInnerEntry<E> extends InnerEntry<E>{
    	
	    /** The null entry. */
	    private static NullInnerEntry nullEntry = new NullInnerEntry<>();

		/**
		 * Instantiates a new null inner entry.
		 */
		private NullInnerEntry() {
			super();	
		}
		
		/**
		 * Gets the single instance of NullInnerEntry.
		 *
		 * @return single instance of NullInnerEntry
		 */
		public static NullInnerEntry getInstance() {
			return nullEntry;
		}
		
		/**
		 * For each.
		 *
		 * @param action the action
		 */
		@Override
		public void forEach(Consumer<? super E> action) {
		}
		
		/**
		 * Adds the to array list.
		 *
		 * @param list the list
		 */
		@Override
		public void addToArrayList(ArrayList<E> list) {
		}
		
		/**
		 * Contains.
		 *
		 * @param entryToCheck the entry to check
		 * @return true, if successful
		 */
		@Override
		public boolean contains(E entryToCheck) {
			return Boolean.FALSE;
		}

		/**
		 * To string.
		 *
		 * @return the string
		 */
		@Override
		public String toString() {
			return "()";
		}
    }
    
    private final class BTreeSetIterator implements Iterator<E> {
		
		private Stack<Entry<E>> entryStack;
		private Stack<Integer> indexStack;
		
		public BTreeSetIterator() {
			entryStack  = new Stack<>();
			indexStack = new Stack<>();
			if (root != null)
				pushLeftEntry(root);
		}
		
		public boolean hasNext() {
			return !entryStack.isEmpty();
		}
		
		public E next() {
			if (!hasNext())
				throw new NoSuchElementException();
			
			Entry<E> node = entryStack.peek();
			int index = indexStack.pop();
			InnerEntry<E> result = getEntryByIndex(node ,index);
			index++;
			if (index < 3)
				indexStack.push(index);
			else
				entryStack.pop();
			if (!node.isEntryLeaf) {
				Entry<E> child = getChildEntryByIndex(node,index);
				if(child != null) {
					pushLeftEntry(getChildEntryByIndex(node,index));
				}
			}
			if(result.innerEntry == null && hasNext()) {
				next();
			}
			return result.innerEntry;
		}
		
		public InnerEntry<E> getEntryByIndex(Entry<E> node, Integer index){
			if(index == 0) {
				return node.firstInnerEntry;
			} else {
				return node.secondInnerEntry;
			} 
		}
		
		public Entry<E> getChildEntryByIndex(Entry<E> node, Integer index){
			if(index == 0) {
				return node.left;
			} else if(index == 1) {
				return node.middle;
			} else {
				return node.right;
			} 
		}
		
		
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
		
		private void pushLeftEntry(Entry<E> node) {
			while (true) {
				entryStack.push(node);
				indexStack.push(0);
				if (node.isEntryLeaf)
					break;
				node = node.left;
			}
		}
		
	}
	
}
