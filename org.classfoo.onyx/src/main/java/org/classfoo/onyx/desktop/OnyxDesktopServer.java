package org.classfoo.onyx.desktop;

/**
 * Desktop RunJetty Server
 */
public class OnyxDesktopServer {
	//
	//	private static final Logger logger = LoggerFactory.getLogger(OnyxDesktopServer.class);
	//
	//	private String DEFAULT_WEBAPP_PATH = "src/main/webapp";
	//
	//	private String WEBXML_PATH = "src/main/webapp/WEB-INF/web.xml";
	//
	//	private Server server = new Server();
	//
	//	private int port = 8080;
	//
	//	public OnyxDesktopServer() throws Exception {
	//		checkAndGetPort();
	//		runJetty();
	//	}
	//
	//	/**
	//	 * 启动jetty服务器
	//	 * @throws Exception
	//	 */
	//	private void runJetty() throws Exception {
	//		server.setStopAtShutdown(true);
	//		ServerConnector connector = new ServerConnector(server);
	//		connector.setPort(port);
	//		connector.setReuseAddress(false);
	//		server.setConnectors(new Connector[] { connector });
	//		WebAppContext webContext = new WebAppContext(DEFAULT_WEBAPP_PATH, "/");
	//		//		if (OnyxDesktop.DI_HOME != null && OnyxDesktop.DI_HOME.length() > 0) {
	//		//			webContext.setWar(OnyxDesktop.DI_HOME + "/lib/bundle.jar");
	//		//		}
	//		//		else {
	//		webContext.setDescriptor(WEBXML_PATH);
	//		webContext.setResourceBase(DEFAULT_WEBAPP_PATH);
	//		webContext.setClassLoader(Thread.currentThread().getContextClassLoader());
	//		//		}
	//		server.setHandler(webContext);
	//		server.start();
	//	}
	//
	//	/**
	//	 * 检测并获取端口号
	//	 * @return
	//	 */
	//	private void checkAndGetPort() {
	//		while (!isPortAvailable(port)) {
	//			port += 1;
	//		}
	//	}
	//
	//	/**
	//	 * 判断端口号是否被占用
	//	 * @param port
	//	 * @return
	//	 */
	//	private boolean isPortAvailable(int port) {
	//		try {
	//			bindPort("0.0.0.0", port);
	//			bindPort(InetAddress.getLocalHost().getHostAddress(), port);
	//			if (logger.isErrorEnabled()) {
	//				logger.info("端口号{}未被占用", port);
	//			}
	//			return true;
	//		}
	//		catch (Exception e) {
	//			if (logger.isErrorEnabled()) {
	//				logger.info("端口号{}已被占用", port);
	//			}
	//			return false;
	//		}
	//	}
	//
	//	private void bindPort(String host, int port) throws Exception {
	//		Socket s = new Socket();
	//		try {
	//			s.bind(new InetSocketAddress(host, port));
	//		}
	//		finally {
	//			s.close();
	//		}
	//	}
	//
	//	public Server getServer() {
	//		return this.server;
	//	}
	//
	//	public int getPort() {
	//		return this.port;
	//	}
	//
	//	public boolean isStarted() {
	//		return this.server.isStarted();
	//	}
	//
	//	public void stop() throws Exception {
	//		this.server.stop();
	//	}
}
