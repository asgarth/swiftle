package org.swiftle.ui.listener;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;
import org.swiftle.ui.event.TransferUpdateEvent;

public class TransferProgressListener implements Listener {

	private final ProgressBar progressBar;
	
	public TransferProgressListener(final ProgressBar progressBar) {
		this.progressBar = progressBar;
	}

	public void handleEvent(Event event) {
		if (! (event instanceof TransferUpdateEvent))
			return;
		
		final TransferUpdateEvent progressEvent = (TransferUpdateEvent) event;
		progressBar.getDisplay().asyncExec(new Runnable() {
			public void run() {
				progressBar.setSelection(progressEvent.getProgress());
			}
		});
	}

}
