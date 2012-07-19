package org.swiftle.ui.event;

import org.eclipse.swt.widgets.Event;

public class TransferCompleteEvent extends Event {

	private final boolean success;

	public TransferCompleteEvent(final boolean success) {
		this.success = success;
	}

	public boolean isSuccess() {
		return success;
	}

}
