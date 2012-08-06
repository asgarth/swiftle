package org.swiftle.ui.listener;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.swiftle.network.TransferData;
import org.swiftle.ui.TransferViewer;
import org.swiftle.ui.event.TransferCompleteEvent;

public class TransferCompleteListener implements Listener {

	private final TransferViewer transferViewer;

	private final TransferData data;

	public TransferCompleteListener(final TransferViewer transferViewer, final TransferData data) {
		this.transferViewer = transferViewer;
		this.data = data;
	}

	public void handleEvent(Event event) {
		if (! (event instanceof TransferCompleteEvent))
			return;
		
		transferViewer.getDisplay().asyncExec(new Runnable() {
			public void run() {
				transferViewer.remove(data);
			}
		});
	}

}
