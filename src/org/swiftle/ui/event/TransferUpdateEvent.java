package org.swiftle.ui.event;

import org.eclipse.swt.widgets.Event;

public class TransferUpdateEvent extends Event {

	public final int progress;

	public TransferUpdateEvent(final int progress) {
		this.progress = progress;
	}

	public int getProgress() {
		return progress;
	}

}
