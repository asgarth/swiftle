package org.swiftle;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.widgets.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.swiftle.network.TransferData;
import org.swiftle.ui.TransferViewer;

public class TransferManager {

	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	private static final TransferManager instance = new TransferManager();

	/** transfer viewer widget */
	private TransferViewer transferViewer;

	/** transfer queue */
	private final LinkedList<TransferData> queue;

	public static TransferManager getInstance() {
		return instance;
	}

	private TransferManager() {
		queue = new LinkedList<TransferData>();
	}

	public synchronized void add(final TransferData data) {
		logger.info("New transfer queued: " + data.getSource().getName());
		final List<Listener> list = transferViewer.add(data);
		for (Listener l : list)
			data.addListener(l);

		queue.add(data);
		wake(data);

		//if (queue.size() == 1)
		//	wake(queue.getFirst());
	}

	public synchronized void remove(final int index) {
		logger.info("Transfer removed from queue: " + index);
		//		final TransferData deleted = queue.remove( index );
		//		if( deleted.isStarted() )
		//			deleted.getHandler().cancel();
	}

	private synchronized void wake(final TransferData data) {
		if (data.isStarted())
			return;

		data.setStarted(true);
		queue.remove(data);

		final TransferHandlerThread transfer = new TransferHandlerThread(data);
		new Thread(transfer).start();
	}

	public void setViewer(final TransferViewer transferViewer) {
		this.transferViewer = transferViewer;
	}

	public TransferViewer getViewer() {
		return transferViewer;
	}

}
