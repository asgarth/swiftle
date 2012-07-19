package org.swiftle.ui.listener;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableItem;
import org.swiftle.ui.TransferViewer;
import org.swiftle.ui.event.TransferCompleteEvent;

public class TransferCompleteListener implements Listener {

	private final TransferViewer transferViewer;

	private final TableItem item;

	public TransferCompleteListener(final TransferViewer transferViewer, final TableItem item) {
		this.transferViewer = transferViewer;
		this.item = item;
	}

	public void handleEvent(Event event) {
		if (! (event instanceof TransferCompleteEvent))
			return;
		
		transferViewer.getDisplay().asyncExec(new Runnable() {
			public void run() {
				transferViewer.remove(item);
			}
		});
	}

}
