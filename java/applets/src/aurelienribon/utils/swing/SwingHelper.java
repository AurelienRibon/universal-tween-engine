package aurelienribon.utils.swing;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class SwingHelper {
	/**
	 * Adds a listener to the window parent of the given component. Can be
	 * before the component is really added to its hierachy.
	 * @param source The source component
	 * @param listener The listener to add to the window
	 */
	public static void addWindowListener(final Component source, final WindowListener listener) {
		if (source instanceof Window) {
			((Window)source).addWindowListener(listener);
		} else {
			source.addHierarchyListener(new HierarchyListener() {
				@Override public void hierarchyChanged(HierarchyEvent e) {
					if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) == HierarchyEvent.SHOWING_CHANGED) {
						SwingUtilities.getWindowAncestor(source).addWindowListener(listener);
					}
				}
			});
		}
	}

	/**
	 * Centers a component according to the window location.
	 * @param wnd The parent window
	 * @param cmp A component, usually a dialog
	 */
	public static void centerInWindow(Window wnd, Component cmp) {
		Dimension size = wnd.getSize();
		Point loc = wnd.getLocationOnScreen();
		Dimension cmpSize = cmp.getSize();
		loc.x += (size.width  - cmpSize.width)/2;
		loc.y += (size.height - cmpSize.height)/2;
		cmp.setBounds(loc.x, loc.y, cmpSize.width, cmpSize.height);
	}

	/**
	 * Opens the given website in the default browser, or show a message saying
	 * that no default browser could be accessed.
	 * @param parent The parent of the error message, if raised
	 * @param uri The website uri
	 */
	public static void browse(Component parent, String uri) {
		boolean cannotBrowse = false;
		if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Action.BROWSE)) {
			try {
				Desktop.getDesktop().browse(new URI(uri));
			} catch (URISyntaxException ex) {
			} catch (IOException ex) {
				cannotBrowse = true;
			}
		} else {
			cannotBrowse = true;
		}

		if (cannotBrowse) {
			JOptionPane.showMessageDialog(parent,
				"It seems that I can't open a website using your"
				+ "default browser, sorry.");
		}
	}
}
