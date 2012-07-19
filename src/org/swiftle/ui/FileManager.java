package org.swiftle.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.snow.util.cache.ImageCache;
import org.swiftle.TransferManager;
import org.swiftle.network.DirectoryEntry;
import org.swiftle.network.Entry;
import org.swiftle.network.FileEntry;
import org.swiftle.network.TransferData;
import org.swiftle.network.connection.LocalConnection;
import org.swiftle.ui.listener.NewFileListener;

public class FileManager extends Composite {

	private final ImageCache imageCache = ImageCache.getInstance();

	private final FileBrowser left;

	private final FileBrowser right;

	public FileManager(final Composite parent) {
		super(parent, SWT.NONE);

		final GridLayout fmLayout = new GridLayout(3, false);
		fmLayout.horizontalSpacing = 0;
		fmLayout.marginWidth = 0;
		setLayout(fmLayout);

		left = new FileBrowser(this);
		left.setLayoutData(new GridData(GridData.FILL_BOTH));
		left.connect(new LocalConnection());

		final Composite arrow = new Composite(this, SWT.NONE);
		arrow.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		final GridLayout arrowLayout = new GridLayout(1, true);
		arrowLayout.horizontalSpacing = 0;
		arrowLayout.marginWidth = 0;
		arrow.setLayout(arrowLayout);
		final Button moveLeft = new Button(arrow, SWT.PUSH);
		moveLeft.setImage(imageCache.getImage("./resources/themes/go-left.png"));
		moveLeft.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
		final Button moveRight = new Button(arrow, SWT.PUSH);
		moveRight.setImage(imageCache.getImage("./resources/themes/go-right.png"));
		moveRight.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));

		right = new FileBrowser(this);
		right.setLayoutData(new GridData(GridData.FILL_BOTH));
		right.connect(new LocalConnection());

		moveLeft.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				startTransfer(right, left);
			}
		});

		moveRight.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				startTransfer(left, right);
			}
		});
	}
	
	private void startTransfer(final FileBrowser orig, final FileBrowser dest) {
		final Entry source = orig.getSelectedEntry();
		final Entry target;
		if (source.isFile())
			target = new FileEntry(source.getName(), dest.getConnection().pwd() + dest.getConnection().getPathSeparator() + source.getName(), source.size());
		else
			target = new DirectoryEntry(source.getName(), dest.getConnection().pwd() + dest.getConnection().getPathSeparator() + source.getName());

		final TransferData data = new TransferData(source, target, orig.getConnection(), dest.getConnection());
		data.addListener(new NewFileListener(dest));
		TransferManager.getInstance().add(data);
	}

}
