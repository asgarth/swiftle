package org.swiftle.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.snow.util.cache.ImageCache;
import org.snow.util.layout.FormDataBuilder;

public class TransferBar extends Composite {

	public static final int TRANSFER_BAR_HIGHT = 24;
	
	private final ImageCache cache;

	private final ToolBar transferBar;
	
	private final ToolItem transferItem;
	
	private final Label transferLabel;
	
	private final ProgressBar transferProgressBar;
	
	public TransferBar(final Composite parent) {
		super(parent, SWT.NONE);

		/** init UI */
		cache = ImageCache.getInstance();
		setLayout(new FormLayout());

		/** init bottom bar */
		transferBar = new ToolBar(this, SWT.FLAT | SWT.HORIZONTAL);
		transferBar.setLayoutData(new FormDataBuilder().top(100, -TRANSFER_BAR_HIGHT).bottom(100).left(0, 10).build());
		transferItem = new ToolItem(transferBar, SWT.NONE);
		transferItem.setImage(cache.getImage("./resources/images/transfers_down.png"));
		
		transferLabel = new Label(this, SWT.RIGHT);
		transferLabel.setText("0/0");
		final FormData transferLabelData = new FormDataBuilder().left(transferBar, 10).build();
		transferLabelData.top = new FormAttachment(transferBar, 0, SWT.CENTER);
		transferLabel.setLayoutData(transferLabelData);
		
		transferProgressBar = new ProgressBar(this, SWT.HORIZONTAL | SWT.SMOOTH);
		final FormData transferProgressBarData = new FormDataBuilder().left(transferLabel, 10).right(transferLabel, 280).build();
		transferProgressBarData.top = new FormAttachment(transferLabel, 0, SWT.CENTER);
		transferProgressBar.setLayoutData(transferProgressBarData);
	}
	
	public void setHidePanelAction(final SelectionAdapter adapter) {
		transferItem.addSelectionListener(adapter);
	}
	
	public void setHidePanelImage(final Image image) {
		transferItem.setImage(image);
	}

}
