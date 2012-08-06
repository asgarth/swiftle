package org.swiftle.ui;

import org.eclipse.swt.SWT;
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
import org.swiftle.util.Constants;

public class SwiftleUI extends ApplicationWindow {

	private final ImageCache imageCache = ImageCache.getInstance();

	public SwiftleUI(final Display display, final String title, final int width, final int height) {
		super(display, title, width, height);

		shell.setImage(imageCache.getImage("./resources/images/swiftle.png"));

		/** init menu */
		final Menu menu = buildMenu();
		setMenu(menu);

		final Composite content = getContent();
		content.setLayout(new FormLayout());

		final FileManager fm = new FileManager(content);
		final TransferViewer tv = new TransferViewer(content);

		fm.setLayoutData(new FormDataBuilder().top(0).left(0).right(100).bottom(tv).build());
		tv.setLayoutData(new FormDataBuilder().top(71).left(0).right(100).bottom(100, 0).build());

		/*
		final ToolBar transferBar = new ToolBar(content, SWT.FLAT | SWT.HORIZONTAL);
		transferBar.setLayoutData(new FormDataBuilder().top(tv).bottom(100).left(0, 10).build());
		final ToolItem transferItem = new ToolItem(transferBar, SWT.NONE);
		transferItem.setImage(ImageCache.getInstance().getImage("./resources/images/downloads.png"));
		
		final ProgressBar transferProgressBar = new ProgressBar(content, SWT.HORIZONTAL | SWT.SMOOTH);
		transferProgressBar.setLayoutData(new FormDataBuilder().top(tv, 5).bottom(100, -5).left(transferBar, 10).right(transferBar, 210).build());
		*/
		
		/*
		tv.setLayoutData(new FormDataBuilder().top(fm).left(0).right(100).bottom(100, -24).build());
		
		final ToolBar bottomBar = new ToolBar(content, SWT.FLAT);
		bottomBar.setLayoutData(new FormDataBuilder().top(tv).left(0).right(100).bottom(100).build());

		final ToolItem downloadCountItem = new ToolItem(bottomBar, SWT.NONE);
		downloadCountItem.setImage(ImageCache.getInstance().getImage("/home/sergio/Desktop/download.png"));
		*/
		
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

		final MenuItem quitItem = new MenuItem(fileMenu, SWT.PUSH);
		quitItem.setText("Quit");
		quitItem.setImage(imageCache.getImage("./resources/themes/quit.png"));
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
		preferenceItem.setImage(imageCache.getImage("./resources/themes/preference.png"));
		preferenceItem.setEnabled(false);

		// help menu
		final MenuItem help = new MenuItem(menu, SWT.CASCADE);
		help.setText("Help");
		final Menu helpMenu = new Menu(shell, SWT.DROP_DOWN);
		help.setMenu(helpMenu);
		final MenuItem aboutItem = new MenuItem(helpMenu, SWT.PUSH);
		aboutItem.setText("About...");
		aboutItem.setImage(imageCache.getImage("./resources/themes/about.png"));
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
