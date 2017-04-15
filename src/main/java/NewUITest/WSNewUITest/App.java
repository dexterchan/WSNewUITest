package NewUITest.WSNewUITest;

import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import NewUITest.WSNewUITest.Service.MyEchoServlet;

public class App extends AbstractHandler {
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);
		response.getWriter().println("<h1>Hello World</h1>");
	}

	public static void main(String[] args) throws Exception {
		ClassLoader cl = App.class.getClassLoader();
		// SSL context
		SslContextFactory sslContextFactory = new SslContextFactory();
		sslContextFactory.setKeyStorePath(cl.getResource("keystore.jks").toExternalForm());
		sslContextFactory.setKeyStorePassword("123456");
		sslContextFactory.setKeyManagerPassword("123456");

		// server.setHandler(new HelloWorld());

		Server server = new Server();
		// HTTP connection
		ServerConnector connector = new ServerConnector(server);
		connector.setPort(80);

		// HTTPS connection
		HttpConfiguration https = new HttpConfiguration();
		https.addCustomizer(new SecureRequestCustomizer());

		ServerConnector sslConnector = new ServerConnector(server,
				new SslConnectionFactory(sslContextFactory, "http/1.1"), new HttpConnectionFactory(https));
		sslConnector.setPort(443);
		server.setConnectors(new Connector[] { connector, sslConnector });
		// Figure out what path to serve content from

		URL f = cl.getResource("static");
		if (f == null) {
			throw new RuntimeException("Unable to find resource directory");
		}
		System.out.println("Static file is " + f.toExternalForm());

		String webappDirLocation = "src/main/resources/static/";
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		context.setResourceBase(f.toExternalForm());
		// Add default servlet
		context.addServlet(DefaultServlet.class, "/");
		server.setHandler(context);

		Server wsserver = new Server();
		ServletContextHandler wscontext = new ServletContextHandler(ServletContextHandler.SESSIONS);
		wscontext.addServlet(MyEchoServlet.class, "/");
		wsserver.setHandler(wscontext);
		ServerConnector wsConnector = new ServerConnector(wsserver);
		wsConnector.setPort(8080);
		wsserver.addConnector(wsConnector);
		ServerConnector wssConnector = new ServerConnector(wsserver,
		        new SslConnectionFactory(sslContextFactory,
		            HttpVersion.HTTP_1_1.asString()),
		        new HttpConnectionFactory(https)); // THIS WAS MISSING
		wssConnector.setPort(8082);
		wsserver.addConnector(wssConnector);

		wsserver.start();

		server.start();

		server.join();
		wsserver.join();

	}
}
