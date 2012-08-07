package org.swiftle.ui;

import static org.swiftle.util.StringUtils.isEmpty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.snow.util.cache.ImageCache;
import org.snow.util.layout.GridDataBuilder;
import org.snow.util.layout.TableColumnAdapter;
import org.swiftle.ListSortAction;
import org.swiftle.network.Entry;
import org.swiftle.network.SizeUnit;
import org.swiftle.network.connection.Connection;
import org.swiftle.network.connection.FTPConnection;
import org.swiftle.network.connection.LocalConnection;
import org.swiftle.network.connection.SFTPConnection;
import org.swiftle.ui.dialog.ConnectionDialog;
import org.swiftle.ui.util.IconUtils;

public class FileBrowser extends Composite {

	/** widgets */
	private final ImageCache cache;

	private Text toolBarPath;

	private final Table table;

	/** current connection reference */
	private Connection connection;

	/** file entries list */
	private Map<String, Entry> currentEntryMap = new HashMap<String, Entry>();

	public FileBrowser(final Composite parent) {
		super(parent, SWT.NONE);
		cache = ImageCache.getInstance();

		/** init UI */
		setLayout(new GridLayout(1, false));

		/** init toolbar */
		buildToolBar();

		/** init filesystem table */
		table = buildFilesystemTable();

		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent arg0) {
				if (connection != null && connection.isConnected())
					connection.disconnect();
			}
		});
	}

	public void connect(final Connection newConnection) {
		BusyIndicator.showWhile(getDisplay(), new Runnable() {
			public void run() {
				if (connection != null && connection.isConnected())
					connection.disconnect();

				connection = newConnection;

				refresh(connection.pwd(), connection.list());
			}
		});
	}

	public Connection getConnection() {
		return connection;
	}

	public Entry getSelectedEntry() {
		if (table.getSelectionCount() != 1)
			return null;

		final String selection = table.getSelection()[0].getText(0);
		return currentEntryMap.get(selection);
	}

	public List<Entry> getSelectedEntries() {
		final List<Entry> selected = new ArrayList<Entry>(table.getSelectionCount());
		for (TableItem selection : table.getSelection())
			selected.add(currentEntryMap.get(selection.getText(0)));

		return selected;
	}

	public void add(final Entry entry) {
		BusyIndicator.showWhile(getDisplay(), new Runnable() {
			public void run() {
				final String filePath = entry.getAbsolutePath().replace(getConnection().getPathSeparator() + entry.getName(), "");
				final String browserPath = getConnection().pwd();
				if (! filePath.equals(browserPath))
					return;

				if (currentEntryMap.containsKey(entry.getName()))
					return;

				currentEntryMap.put(entry.getName(), entry);

				refresh(browserPath, new ArrayList<Entry>(currentEntryMap.values()));
			}
		});
	}

	private void refresh(final String currentPath, final List<Entry> entries) {
		/** clear old entries */
		table.removeAll();

		/** refresh toolbar path */
		toolBarPath.setText(currentPath);

		/** sort entries list */
		new ListSortAction(entries, true).execute();

		/** add files list */
		currentEntryMap = new HashMap<String, Entry>();
		for (Entry entry : entries) {
			currentEntryMap.put(entry.getName(), entry);

			final TableItem item = new TableItem(table, SWT.NONE);
			item.setText(new String[] { entry.getName(), entry.isFile() ? SizeUnit.asString(entry.size()) : "" });
			item.setImage(IconUtils.getIcon(entry));
		}
	}

	private void cd(final String path) {
		BusyIndicator.showWhile(getDisplay(), new Runnable() {
			public void run() {
				connection.cd(path);
				refresh(connection.pwd(), connection.list());
			}
		});
	}

	private Table buildFilesystemTable() {
		final Composite tableComposite = new Composite(this, SWT.NONE);
		final GridData tableData = new GridDataBuilder(GridData.FILL_BOTH).build();
		tableComposite.setLayoutData(tableData);

		final Table table = new Table(tableComposite, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		final TableColumn filename = new TableColumn(table, SWT.LEFT);
		filename.setText("Filename");
		final TableColumn size = new TableColumn(table, SWT.LEFT);
		size.setText("Size");
		final TableColumn permission = new TableColumn(table, SWT.LEFT);
		permission.setText("Permission");
		final TableColumn changed = new TableColumn(table, SWT.LEFT);
		changed.setText("Changed");

		table.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(final MouseEvent e) {
				if (table.getSelectionCount() != 1)
					return;

				final String selection = table.getSelection()[0].getText(0);

				if (table.getSelection()[0].getImage().equals(IconUtils.getDirectoryIcon())) {
					cd(selection);
				} else {
					if (connection instanceof LocalConnection)
						Program.launch(connection.pwd() + "/" + selection);
				}
			}
		});

		/** init table layout */
		table.addControlListener(new TableColumnAdapter(table, 50, 15, 20, 5));
		tableComposite.setLayout(new FillLayout());

		return table;
	}

	private ToolBar buildToolBar() {
		final ToolBar toolBar = new ToolBar(this, SWT.FLAT);
		toolBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final ToolItem connectionItem = new ToolItem(toolBar, SWT.NONE);
		connectionItem.setImage(cache.getImage("./resources/themes/connection_list.png"));
		connectionItem.setToolTipText("Connect to a remote host");
		final Menu connectionMenu = buildConnectionMenu();
		connectionItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				final Rectangle rect = connectionItem.getBounds();
				Point pt = new Point(rect.x, rect.y + rect.height);
				pt = toolBar.toDisplay(pt);
				connectionMenu.setLocation(pt.x, pt.y);
				connectionMenu.setVisible(true);
			}
		});

		final ToolItem homeItem = new ToolItem(toolBar, SWT.NONE);
		homeItem.setImage(cache.getImage("./resources/themes/home.png"));
		homeItem.setToolTipText("Go to home directpry");
		homeItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				BusyIndicator.showWhile(getDisplay(), new Runnable() {
					public void run() {
						connection.home();
						refresh(connection.pwd(), connection.list());
					}
				});
			}
		});

		final ToolItem pathItem = new ToolItem(toolBar, SWT.SEPARATOR);
		toolBarPath = new Text(toolBar, SWT.BORDER | SWT.SINGLE);
		toolBarPath.addSelectionListener(new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		pathItem.setControl(toolBarPath);

		final ToolItem refresh = new ToolItem(toolBar, SWT.PUSH);
		refresh.setImage(cache.getImage("./resources/themes/refresh.png"));
		refresh.setToolTipText("Refresh file list");
		refresh.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				BusyIndicator.showWhile(getDisplay(), new Runnable() {
					public void run() {
						refresh(connection.pwd(), connection.list());
					}
				});
			}
		});

		new ToolItem(toolBar, SWT.SEPARATOR);

		final ToolItem up = new ToolItem(toolBar, SWT.PUSH);
		up.setImage(cache.getImage("./resources/themes/go-up.png"));
		up.setToolTipText("Go to parent directory");
		up.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				cd("..");
			}
		});

		final ToolItem mkdir = new ToolItem(toolBar, SWT.PUSH);
		mkdir.setImage(cache.getImage("./resources/themes/new-folder.png"));
		mkdir.setToolTipText("Create a new directory");
		mkdir.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {

			}
		});
		mkdir.setEnabled(false);

		final ToolItem remove = new ToolItem(toolBar, SWT.PUSH);
		remove.setImage(cache.getImage("./resources/themes/cancel.png"));
		remove.setToolTipText("Delete selected files");
		remove.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				BusyIndicator.showWhile(getDisplay(), new Runnable() {
					public void run() {
						for (int index : table.getSelectionIndices()) {
							final TableItem item = table.getItem(index);

							boolean res = connection.delete(item.getText(0));
							if (res) {
								currentEntryMap.remove(item.getText(0));
								table.remove(index);
							}
						}
					}
				});
			}
		});

		/** add resive listener to redraw path toolbar item */
		addListener(SWT.Resize, new Listener() {
			public void handleEvent(final Event e) {
				final Rectangle rect = getClientArea();
				final Point size = toolBar.computeSize(rect.width, SWT.DEFAULT);

				int usedSpace = 0;
				for (ToolItem item : toolBar.getItems())
					usedSpace += item.getControl() == null ? item.getWidth() : 0;

					pathItem.setWidth(size.x - 25 - usedSpace);
					toolBar.setSize(size);
			}
		});

		return toolBar;
	}

	private Menu buildConnectionMenu() {
		final Menu menu = new Menu(getShell(), SWT.POP_UP);
		final MenuItem newConnectionItem = new MenuItem(menu, SWT.PUSH);
		newConnectionItem.setText("New connection...");

		new MenuItem(menu, SWT.SEPARATOR);

		final MenuItem disconnectItem = new MenuItem(menu, SWT.PUSH);
		disconnectItem.setText("Disconnect");
		disconnectItem.setEnabled(false);

		newConnectionItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				final ConnectionDialog dialog = new ConnectionDialog(getShell());
				dialog.open();

				if (isEmpty(dialog.getServer()) || dialog.getProtocol() == null)
					return;

				final Connection newConnection;
				if (dialog.getProtocol() == ConnectionDialog.Protocol.SFTP)
					newConnection = new SFTPConnection();
				else
					newConnection = new FTPConnection();

				BusyIndicator.showWhile(getDisplay(), new Runnable() {
					public void run() {
						if( dialog.getPort() > 0 )
							newConnection.connect(dialog.getServer(), dialog.getPort(), dialog.getUser(), dialog.getPwd());
						else
							newConnection.connect(dialog.getServer(), dialog.getUser(), dialog.getPwd());
					}
				});

				connect(newConnection);
				disconnectItem.setEnabled(true);

			}
		});

		disconnectItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				connect(new LocalConnection());
				disconnectItem.setEnabled(false);
			}
		});

		return menu;
	}

}
