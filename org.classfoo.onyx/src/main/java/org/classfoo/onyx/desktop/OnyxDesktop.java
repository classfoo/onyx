package org.classfoo.onyx.desktop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Desktop
 * @author ClassFoo
 * @createdate 20180102
 */
public class OnyxDesktop {

    private static final Logger logger = LoggerFactory.getLogger(OnyxDesktop.class);

    public OnyxDesktop() {
    }

    public static void main(String[] args) throws Exception {
        OnyxDesktopServer desktopServer = new OnyxDesktopServer();
        //		Browser browser = new Browser();
        //		BrowserView view = new BrowserView(browser);
        //		 
        //		JFrame frame = new JFrame();
        //		frame.add(view, BorderLayout.CENTER);
        //		frame.setSize(1280, 768);
        //		frame.setVisible(true);
        //		browser.addDisposeListener(new DisposeListener<Browser>() {
        //			@Override
        //			public void onDisposed(DisposeEvent<Browser> arg0) {
        //				System.exit(0);
        //			}
        //		});
        //		browser.loadURL("http://localhost:"+desktopServer.getPort()+"/static/index.html");
    }
}
