package org.swiftle.ui.listener;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.swiftle.network.Entry;
import org.swiftle.ui.FileBrowser;
import org.swiftle.ui.event.NewFileEvent;

public class NewFileListener implements Listener {

	private final FileBrowser browser;

	public NewFileListener(final FileBrowser browser) {
		this.browser = browser;
	}

	public void handleEvent(Event event) {
		if (! (event instanceof NewFileEvent))
			return;
		
		final Entry entry = ((NewFileEvent) event).getEntry();
		browser.getDisplay().asyncExec(new Runnable() {
			public void run() {
				browser.add(entry);
			}
		});
	}

}
