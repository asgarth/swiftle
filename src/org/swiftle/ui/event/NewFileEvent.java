package org.swiftle.ui.event;

import org.eclipse.swt.widgets.Event;
import org.swiftle.network.Entry;

public class NewFileEvent extends Event {

	private final Entry entry;

	public NewFileEvent(final Entry entry) {
		this.entry = entry;
	}

	public Entry getEntry() {
		return entry;
	}

}
