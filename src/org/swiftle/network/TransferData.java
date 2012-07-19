package org.swiftle.network;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.swiftle.network.connection.Connection;

public class TransferData {

	private final Entry source;
	
	private final Entry target;

	private final Connection orig;

	private final Connection dest;
	
	private final List<Listener> listeners;
	
	private boolean started;
	
	public TransferData(final Entry source, final Entry target, final Connection orig, final Connection dest) {
		this.source = source;
		this.target = target;
		this.orig = orig;
		this.dest = dest;

		this.listeners = new LinkedList<Listener>();
		
		this.started = false;
	}

	public Entry getSource() {
		return source;
	}
	
	public Entry getTarget() {
		return target;
	}

	public Connection getOrig() {
		return orig;
	}

	public Connection getDest() {
		return dest;
	}
	
	public void addListener(final Listener listener) {
		listeners.add(listener);
	}
	
	public boolean removeListener(final Listener listener) {
		return listeners.remove(listener);
	}

	public synchronized boolean isStarted() {
		return started;
	}

	public synchronized void setStarted(boolean started) {
		this.started = started;
	}

	public void notify(final Event event) {
		for (Listener listener : listeners)
			listener.handleEvent(event);
	}

}
