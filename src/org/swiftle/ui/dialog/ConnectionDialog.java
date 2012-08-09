package org.swiftle.ui.dialog;

import static org.swiftle.util.StringUtils.isEmpty;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.snow.action.Action;
import org.snow.util.cache.ImageCache;
import org.snow.util.layout.FormDataBuilder;
import org.snow.window.ApplicationDialog;
import org.snow.window.footer.StandardFooter;
import org.swiftle.network.connection.FTPConnection;
import org.swiftle.network.connection.Protocol;
import org.swiftle.network.connection.SFTPConnection;

public class ConnectionDialog extends ApplicationDialog {

	private Protocol protocol;

	private String server;

	private int port;

	private String user;

	private String pwd;

	public ConnectionDialog(final Shell parent) {
		super(parent, "New Connection", 550, 320);

		setHeader(new TitleHeader(this, "Create a new connection to remote server", "./resources/themes/connection.png"));

		final Composite composite = getContent();
		composite.setLayout(new FormLayout());

		/** protocol group */
		final Group protocolGroup = new Group(composite, SWT.NONE);
		protocolGroup.setText("Protocol");
		final FormData protocolData = new FormDataBuilder().top(0, 5).bottom(100, -5).left(0, 5).right(30).build();
		protocolGroup.setLayoutData(protocolData);
		final FillLayout protocolLayout = new FillLayout();
		protocolLayout.marginHeight = 5;
		protocolLayout.marginWidth = 5;
		protocolGroup.setLayout(protocolLayout);

		final Table table = new Table(protocolGroup, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);
		table.setLinesVisible(false);
		table.setHeaderVisible(false);
		final TableColumn protocolColumn = new TableColumn(table, SWT.LEFT);
		for (Protocol p : Protocol.values()) {
			final TableItem item = new TableItem(table, SWT.NONE);
			item.setText(p.toString());
			item.setImage(ImageCache.getInstance().getImage(p.getImage()));
		}
		table.setSelection(0);
		protocolColumn.pack();

		/** connection details group */
		final Group connectionGroup = new Group(composite, SWT.NONE);
		connectionGroup.setText("Connection");
		final FormData connectionData = new FormDataBuilder().top(0, 5).bottom(100, -5).left(protocolGroup, 5).right(100, -5).build();
		connectionGroup.setLayoutData(connectionData);
		final GridLayout connectionLayout = new GridLayout(2, false);
		connectionLayout.marginWidth = 5;
		connectionLayout.marginHeight = 5;
		connectionGroup.setLayout(connectionLayout);

		final Label serverLabel = new Label(connectionGroup, SWT.NONE);
		serverLabel.setText("Server:");
		serverLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_CENTER));
		final Text serverText = new Text(connectionGroup, SWT.BORDER | SWT.SINGLE);
		serverText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER));

		final Label userLabel = new Label(connectionGroup, SWT.NONE);
		userLabel.setText("Username:");
		userLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_CENTER));
		final Text userText = new Text(connectionGroup, SWT.BORDER | SWT.SINGLE);
		userText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER));

		final Label pwdLabel = new Label(connectionGroup, SWT.NONE);
		pwdLabel.setText("Password:");
		pwdLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_CENTER));
		final Text pwdText = new Text(connectionGroup, SWT.BORDER | SWT.SINGLE);
		pwdText.setEchoChar('*');
		pwdText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER));

		final Label portLabel = new Label(connectionGroup, SWT.NONE);
		portLabel.setText("Port:");
		portLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_CENTER));
		final Text portText = new Text(connectionGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
		portText.setText("21");
		portText.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
		portText.addListener(SWT.Verify, new Listener() {
			public void handleEvent(Event e) {
				for (char c : e.text.toCharArray()) {
					if (!(c >= '0' && c <= '9')) {
						e.doit = false;
						return;
					}
				}
			}
		});
		final GridData portTextData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_CENTER);
		portTextData.minimumWidth = 70;
		portTextData.widthHint = 70;
		portText.setLayoutData(portTextData);

		table.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (Protocol.valueOf(table.getSelection()[0].getText()) == Protocol.SFTP)
					portText.setText(Integer.toString(SFTPConnection.DEFAULT_PORT));
				else
					portText.setText(Integer.toString(FTPConnection.DEFAULT_PORT));
			}
		});
		
		final StandardFooter footer = new StandardFooter(this, "Connect", "Cancel");
		footer.addOkAction(new Action() {
			public boolean execute() {
				protocol = Protocol.valueOf(table.getSelection()[0].getText());
				server = serverText.getText();
				port = isEmpty(portText.getText().trim()) ? -1 : Integer.parseInt(portText.getText().trim());
				user = userText.getText();
				pwd = pwdText.getText();

				shell.close();

				return true;
			}
		});
		setFooter(footer);
	}

	public Protocol getProtocol() {
		return protocol;
	}
	
	public String getServer() {
		return server;
	}

	public int getPort() {
		return port;
	}

	public String getUser() {
		return user;
	}

	public String getPwd() {
		return pwd;
	}

}
