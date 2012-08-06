package org.swiftle.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.snow.util.cache.ImageCache;
import org.snow.util.layout.FormDataBuilder;
import org.snow.util.layout.TableColumnAdapter;
import org.swiftle.TransferManager;
import org.swiftle.network.Entry;
import org.swiftle.network.SizeUnit;
import org.swiftle.network.TransferData;
import org.swiftle.ui.listener.TransferCompleteListener;
import org.swiftle.ui.listener.TransferProgressListener;
import org.swiftle.ui.util.IconUtils;

public class TransferViewer extends Composite {

	private final TransferManager manager;

	private final ImageCache cache;

	private final Map<TransferData, TableItem> transferMap;
	
	private final Map<TableItem, TableEditor> editorMap;

	private final ToolBar sideBar;

	private final Table table;

	public TransferViewer(final Composite parent) {
		super(parent, SWT.NONE);

		manager = TransferManager.getInstance();

		transferMap = new HashMap<TransferData, TableItem>();
		editorMap = new HashMap<TableItem, TableEditor>();

		/** init UI */
		cache = ImageCache.getInstance();
		setLayout(new FormLayout());

		/** init sidebar */
		sideBar = buildSideBar();

		/** init table */
		table = buildTransferTable();
	}

	/** Add a new transfer to the transfer widget. */
	public List<Listener> add(final TransferData data) {
		if (data.getSource().isDirectory())
			throw new UnsupportedOperationException("Directory transfer not supported yet :(");

		// create transfer control widget
		final ProgressBar progressBar = new ProgressBar(table, SWT.HORIZONTAL);
		progressBar.setMaximum(100);
		final TableEditor editor = buildItem(data.getSource(), data.getOrig().toString(), progressBar);

		// store transfer in map
		transferMap.put(data, editor.getItem());
		
		// save progress bar editor -> required for table redraw
		editorMap.put(editor.getItem(), editor);

		// add listener in current transfer command
		final List<Listener> list = new ArrayList<Listener>(2);
		list.add(new TransferProgressListener(progressBar));
		list.add(new TransferCompleteListener(this, data));

		return list;
	}
	
	/** Remove a transfer from the transfer widget. */
	public void remove(final TransferData data) {
		final TableItem item = transferMap.remove(data);
		if( item == null || item.isDisposed() )
			return;
		
		item.dispose();

		// remove from editor list and redraw table
		editorMap.remove(item);
		for (TableEditor editor : editorMap.values())
			editor.layout();

		table.layout();
	}

	/** Init sidebar with action button for current transfers. */
	private ToolBar buildSideBar() {
		final ToolBar sideBar = new ToolBar(this, SWT.NONE | SWT.VERTICAL);
		final FormData sideBarData = new FormDataBuilder().top(0).bottom(100, -25).left(100, -25).right(100, -5).build();
		sideBar.setLayoutData(sideBarData);

		final ToolItem play = new ToolItem(sideBar, SWT.PUSH);
		play.setImage(cache.getImage("./resources/themes/play.png"));
		play.setToolTipText("Start");
		play.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if (table.getSelectionCount() != 1)
					return;

				//manager.wake(table.getSelectionIndex());
			}
		});
		play.setEnabled(false);

		final ToolItem cancel = new ToolItem(sideBar, SWT.PUSH);
		cancel.setImage(cache.getImage("./resources/themes/cancel.png"));
		cancel.setToolTipText("Delete");
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (table.getSelectionCount() != 1)
					return;

				//manager.remove(table.getSelectionIndex());
				// remove(table.getSelection()[0]);
			}
		});
		cancel.setEnabled(false);

		return sideBar;
	}

	/** Init transfer table. */
	private Table buildTransferTable() {
		final Composite tableComposite = new Composite(this, SWT.NONE);
		final FormData tableData = new FormDataBuilder().top(0).bottom(100, -5).left(0, 5).right(sideBar).build();
		tableComposite.setLayoutData(tableData);
		tableComposite.setLayout(new FillLayout());

		final Table table = new Table(tableComposite, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		final TableColumn filename = new TableColumn(table, SWT.LEFT);
		filename.setText("Filename");
		final TableColumn progress = new TableColumn(table, SWT.LEFT);
		progress.setText("Progress");
		final TableColumn connection = new TableColumn(table, SWT.LEFT);
		connection.setText("Connection");
		final TableColumn size = new TableColumn(table, SWT.LEFT);
		size.setText("Size");

		/** init table layout */
		table.addControlListener(new TableColumnAdapter(table, 40, 20, 20, 19));

		return table;
	}

	/** Create the table entry for a new transfer job. */
	private TableEditor buildItem(final Entry entry, final String connection, final ProgressBar progressBar) {
		final TableItem item = new TableItem(table, SWT.NONE);
		item.setImage(IconUtils.getIcon(entry));
		item.setText(0, entry.getName());
		item.setText(2, connection);
		item.setText(3, SizeUnit.asString(entry.size()));

		final TableEditor editor = new TableEditor(table);
		editor.grabHorizontal = true;
		editor.setEditor(progressBar, item, 1);

		item.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent event) {
				progressBar.dispose();
			}
		});

		return editor;
	}

}
