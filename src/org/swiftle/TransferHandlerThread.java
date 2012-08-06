package org.swiftle;

import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.swiftle.network.TransferData;
import org.swiftle.network.connection.Connection;
import org.swiftle.ui.event.NewFileEvent;
import org.swiftle.ui.event.TransferCompleteEvent;
import org.swiftle.ui.event.TransferUpdateEvent;

public class TransferHandlerThread implements Runnable {

	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	private final TransferData data;

	public TransferHandlerThread(final TransferData data) {
		this.data = data;
	}

	public void run() {
		logger.info("New transfer started for file: " + data.getSource().getName());

		final Connection orig = data.getOrig();
		final Connection dest = data.getDest();

		final InputStream in = orig.getStream(data.getSource().getAbsolutePath());
		final OutputStream out = dest.putStream(data.getTarget().getAbsolutePath());

		try {
			final byte[] buf = new byte[4096];
			final long total = data.getSource().size();
			int len;
			long current = 0;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
				current += len;

				data.notify(new TransferUpdateEvent((int)(current * 100 / total)));
			}
			orig.closeGetStream(in);
			dest.closePutStream(out);
		} catch (Exception e) {
			logger.error("Error transfering file " + data.toString(), e);
			data.notify(new TransferCompleteEvent(false));
			data.notify(new TransferUpdateEvent(-1));
			return;
		}

		logger.info("Transfer completed for file: " + data.getSource().getName());

		// send notification events
		data.notify(new TransferCompleteEvent(true));
		data.notify(new NewFileEvent(data.getTarget()));
	}

}
