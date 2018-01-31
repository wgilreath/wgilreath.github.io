package com.adhoc;

import java.util.ArrayList;
import java.util.Comparator;

@SuppressWarnings("serial")
public class SortedArrayList<E> extends ArrayList<E> {

	protected final Comparator<E> comparator;

	public SortedArrayList() {
		comparator = null;
	}

	@Override
	public boolean add(final E o) {
		int idx = 0;
		if (!isEmpty()) {
			idx = findInsertionPoint(o);
		}
		super.add(idx, o);
		return true;
	}

	public int findInsertionPoint(final E o) {
		return findInsertionPoint(o, 0, size() - 1);
	}

	@SuppressWarnings( {"unchecked"})
	protected int compare(final E k1, final E k2) {
		if (comparator == null) {
			return ((Comparable<E>) k1).compareTo(k2);
		}
		return comparator.compare(k1, k2);
	}

	protected int findInsertionPoint(final E o, int low, int high) {

		while (low <= high) {
			int mid = (low + high) >>> 1;
			int delta = compare(get(mid), o);

			if (delta > 0) {
				high = mid - 1;
			} else {
				low = mid + 1;
			}
		}

		return low;
	}

}//end class SortedArrayList
