package org.swiftle.ui.dialog;

import static org.swiftle.util.StringUtils.isEmpty;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.snow.action.Action;
import org.snow.window.ApplicationDialog;
import org.snow.window.footer.StandardFooter;

public class ConnectionDialog extends ApplicationDialog {

	private String server;

	private int port;

	private String user;

	private String pwd;

	public ConnectionDialog(final Shell parent) {
		super(parent, "New Connection", 400, 250);

		setHeader(new TitleHeader(this, "Create a new connection to remote server", "./resources/themes/connection.png"));

		final Composite composite = getContent();

		final GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 10;
		layout.marginHeight = 10;
		composite.setLayout(layout);

		final Label serverLabel = new Label(composite, SWT.NONE);
		serverLabel.setText("Server:");
		serverLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_CENTER));
		final Text serverText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		serverText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER));

		final Label userLabel = new Label(composite, SWT.NONE);
		userLabel.setText("Username:");
		userLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_CENTER));
		final Text userText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		userText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER));

		final Label pwdLabel = new Label(composite, SWT.NONE);
		pwdLabel.setText("Password:");
		pwdLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_CENTER));
		final Text pwdText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		pwdText.setEchoChar('*');
		pwdText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER));

		final Label portLabel = new Label(composite, SWT.NONE);
		portLabel.setText("Port:");
		portLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_CENTER));
		final Text portText = new Text(composite, SWT.BORDER | SWT.SINGLE);
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
		portText.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_CENTER));

		final StandardFooter footer = new StandardFooter(this, "Connect", "Cancel");
		footer.addOkAction(new Action() {
			public boolean execute() {
				server = serverText.getText();
				port = isEmpty(portText.getText()) ? -1 : Integer.parseInt(portText.getText());
				user = userText.getText();
				pwd = pwdText.getText();

				shell.close();
				return true;
			}
		});
		setFooter(footer);

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
