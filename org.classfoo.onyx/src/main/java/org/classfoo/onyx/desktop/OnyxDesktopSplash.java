package org.classfoo.onyx.desktop;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;

/**
 * Onyx Splash
 */
public class OnyxDesktopSplash extends JWindow {

	private static final long serialVersionUID = 7444949909735790400L;

	public OnyxDesktopSplash() {
		super();
		URL path = OnyxDesktop.class.getResource("splash.gif");
		JLabel label = new JLabel(new ImageIcon(path));
		getContentPane().add(label, BorderLayout.CENTER);
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension labelSize = label.getPreferredSize();
		setLocation(screenSize.width / 2 - (labelSize.width / 2), screenSize.height / 2 - (labelSize.height / 2));

		Runnable runSplash = new Runnable() {
			public void run() {
				setVisible(true);
			}
		};

		Thread runSplashThread = new Thread(runSplash, "runSplashThread");
		runSplashThread.start();
	}

	public void close() {
		setVisible(false);
		dispose();
	}

}
