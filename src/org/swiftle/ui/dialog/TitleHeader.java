package org.swiftle.ui.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.snow.util.Displays;
import org.snow.util.ImageUtils;
import org.snow.util.layout.FormDataBuilder;
import org.snow.window.ApplicationWindow;
import org.snow.window.header.Header;

public class TitleHeader extends Header {

	public static final int HEIGHT = 40;

	public static final int IMAGE_SIZE = 32;

	public TitleHeader(final ApplicationWindow parent, final String message, final String image) {
		super(parent);

		final Display display = parent.getDisplay();

		/** init header */
		setLayout(new FormLayout());
		setBackground(display.getSystemColor(SWT.COLOR_WHITE));

		/** init separator line */
		final Label separator = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL | SWT.SHADOW_OUT);

		/** init image label */
		final Label label = new Label(this, SWT.NONE);
		final int vOffset = (HEIGHT - IMAGE_SIZE) / 2 - 1;
		final FormData labelData = new FormDataBuilder().top(0, vOffset).bottom(separator, -vOffset).left(0, 5).right(0, IMAGE_SIZE + 5).build();
		label.setLayoutData(labelData);

		label.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		label.setImage(ImageUtils.loadImageFromFile(parent.getDisplay(), image));
		label.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				if (label.getImage() != null && !label.getImage().isDisposed())
					label.getImage().dispose();
			}
		});

		/** init header message */
		final Label text = new Label(this, SWT.NONE);
		text.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		text.setFont(Displays.getSystemFontBold(display));
		text.setText(message);

		final FormData textData = new FormData();
		textData.top = new FormAttachment(label, 0, SWT.CENTER);
		textData.bottom = new FormAttachment(100, 0);
		textData.left = new FormAttachment(label, 10);
		textData.right = new FormAttachment(100, -3);
		text.setLayoutData(textData);
		text.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				if (text.getFont() != null && !text.getFont().isDisposed())
					text.getFont().dispose();
			}
		});

		/** add separator line */
		final FormData sepData = new FormData();
		sepData.bottom = new FormAttachment(100, 0);
		sepData.left = new FormAttachment(0, 0);
		sepData.right = new FormAttachment(100, 0);
		separator.setLayoutData(sepData);
	}

	public boolean isHeightFixed() {
		return true;
	}

	public int getHeight() {
		return HEIGHT;
	}

}
