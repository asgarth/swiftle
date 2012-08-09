package org.swiftle.ui;

import static org.swiftle.util.StringUtils.isEmpty;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.snow.util.cache.ImageCache;
import org.snow.util.layout.FormDataBuilder;
import org.snow.window.ApplicationWindow;
import org.snow.window.dialog.AboutDialog;
import org.swiftle.TransferManager;
import org.swiftle.config.Configuration;
import org.swiftle.config.Server;
import org.swiftle.network.connection.Connection;
import org.swiftle.network.connection.ConnectionFactory;
import org.swiftle.ui.dialog.ConnectionDialog;
import org.swiftle.util.Constants;

public class SwiftleUI extends ApplicationWindow {

	private final Configuration config = Configuration.getInstance();

	private final ImageCache cache = ImageCache.getInstance();
	
	private final FileManager fileManager;

	public SwiftleUI(final Display display, final String title, final int width, final int height) {
		super(display, title, width, height);

		if (config.getLayout().getBoolean("maximize"))
			shell.setMaximized(true);

		shell.setImages(new Image[] { cache.getImage("./resources/images/swiftle.png"),
				cache.getImage("./resources/images/swiftle_32.png"),
				cache.getImage("./resources/images/swiftle_16.png")});

		shell.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent arg0) {
				config.getLayout().put("width", shell.getSize().x);
				config.getLayout().put("height", shell.getSize().y);
				config.getLayout().put("maximize", shell.getMaximized());
			}
		});

		/** init menu */
		final Menu menu = buildMenu();
		setMenu(menu);

		final Composite content = getContent();
		content.setLayout(new FormLayout());

		fileManager = new FileManager(content);
		final TransferViewer tv = new TransferViewer(content);
		final TransferBar tb = new TransferBar(content);

		fileManager.setLayoutData(new FormDataBuilder().top(0).left(0).right(100).bottom(tv).build());
		tv.setLayoutData(new FormDataBuilder().top(70).left(0).right(100).bottom(tb, 0).build());
		tb.setLayoutData(new FormDataBuilder().top(100, -TransferBar.TRANSFER_BAR_HIGHT).left(0).right(100).bottom(100, 0).build());

		tb.setHidePanelAction(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				tv.setVisible(! tv.getVisible());
				fileManager.setLayoutData(new FormDataBuilder().top(0).left(0).right(100).bottom(tv.getVisible() ? tv : tb).build());

				tb.setHidePanelImage(cache.getImage(tv.getVisible() ? "./resources/images/transfers_down.png" : "./resources/images/transfers_up.png"));

				content.layout();
			}
		});

		TransferManager.getInstance().setViewer(tv);
	}
	
	private Menu buildMenu() {
		// main menu
		final Menu menu = new Menu(shell, SWT.BAR);

		// file menu
		final MenuItem file = new MenuItem(menu, SWT.CASCADE);
		file.setText("File");
		final Menu fileMenu = new Menu(shell, SWT.DROP_DOWN);
		file.setMenu(fileMenu);

		final MenuItem newConnectionItem = new MenuItem(fileMenu, SWT.PUSH);
		newConnectionItem.setText("New Connection...");
		newConnectionItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				final ConnectionDialog dialog = new ConnectionDialog(getShell());
				dialog.open();

				if (isEmpty(dialog.getServer()) || dialog.getProtocol() == null)
					return;
				
				final Connection newConnection = ConnectionFactory.build(dialog.getProtocol(), dialog.getServer(), dialog.getPort(), dialog.getUser(), dialog.getPwd());
				fileManager.getRight().connect(newConnection);
				
				final Server server = new Server(dialog.getProtocol().name());
				server.set("hostname", dialog.getServer());
				if (dialog.getPort() > 0)
					server.set("port", Integer.toString(dialog.getPort()));
				server.set("username", dialog.getUser());
				server.set("password", dialog.getPwd());
				
				config.addServer(dialog.getUser() + "@" + dialog.getServer(), server);
			}
		});

		
		new MenuItem(fileMenu, SWT.SEPARATOR);
		
		final MenuItem quitItem = new MenuItem(fileMenu, SWT.PUSH);
		quitItem.setText("Quit");
		quitItem.setImage(cache.getImage("./resources/themes/quit.png"));
		quitItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				shell.close();
			}
		});

		// tools menu
		final MenuItem tools = new MenuItem(menu, SWT.CASCADE);
		tools.setText("Tools");
		final Menu toolsMenu = new Menu(shell, SWT.DROP_DOWN);
		tools.setMenu(toolsMenu);
		final MenuItem preferenceItem = new MenuItem(toolsMenu, SWT.PUSH);
		preferenceItem.setText("Preferences");
		preferenceItem.setImage(cache.getImage("./resources/themes/preference.png"));
		preferenceItem.setEnabled(false);

		// help menu
		final MenuItem help = new MenuItem(menu, SWT.CASCADE);
		help.setText("Help");
		final Menu helpMenu = new Menu(shell, SWT.DROP_DOWN);
		help.setMenu(helpMenu);
		final MenuItem aboutItem = new MenuItem(helpMenu, SWT.PUSH);
		aboutItem.setText("About " + Constants.SWIFTLE);
		aboutItem.setImage(cache.getImage("./resources/themes/about.png"));
		aboutItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				final AboutDialog dialog = new AboutDialog(shell, "About " + Constants.SWIFTLE, "./resources/images/about.png", "Version: " + Constants.getVersion());
				dialog.setTextColor(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
				dialog.setWebsite("http://code.google.com/p/swiftle");
				dialog.setText("A simple cross-platform file transfer client with support for multiple protocols and direct transfer between romote hosts.");
				dialog.open();
			}
		});

		return menu;
	}
	
}
