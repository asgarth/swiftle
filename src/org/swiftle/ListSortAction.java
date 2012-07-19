package org.swiftle;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.snow.action.Action;
import org.swiftle.network.Entry;

public class ListSortAction implements Action {

	private final List<Entry> list;

	private final boolean sortAsc;

	public ListSortAction(final List<Entry> list, final boolean sortAsc) {
		this.list = list;
		this.sortAsc = sortAsc;
	}

	public boolean execute() {
		// init comparator
		final Comparator<Entry> comparator = new Comparator<Entry>() {

			public int compare(final Entry arg0, final Entry arg1) {
				if (arg0.isDirectory() && !arg1.isDirectory())
					return sortAsc ? -1 : 1;

				if (arg1.isDirectory() && !arg0.isDirectory())
					return sortAsc ? 1 : -1;

				return sortAsc ? arg0.getName().compareTo(arg1.getName()) : arg1.getName().compareTo(arg0.getName());
			}
		};

		// sort the data based on column and direction
		Collections.sort(list, comparator);

		return true;
	}

}
