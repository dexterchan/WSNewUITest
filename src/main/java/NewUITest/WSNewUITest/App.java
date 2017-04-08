package NewUITest.WSNewUITest;

import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

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

		// server.setHandler(new HelloWorld());

		Server server = new Server(8080);

		// Figure out what path to serve content from
		ClassLoader cl = App.class.getClassLoader();
		URL f = cl.getResource("static");
		if (f == null) {
			throw new RuntimeException("Unable to find resource directory");
		}
		System.out.println("Static file is " + f.toExternalForm());
		

		String webappDirLocation = "src/main/resources/static/";
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		context.setResourceBase(f.toExternalForm());
		server.setHandler(context);

		
		// Add default servlet
		context.addServlet(DefaultServlet.class, "/");

		Server wsserver = new Server(9001);
		ServletContextHandler wscontext = new ServletContextHandler(ServletContextHandler.SESSIONS);
		wscontext.addServlet(MyEchoServlet.class, "/");
		wsserver.setHandler(wscontext);

		wsserver.start();

		server.start();

		server.join();
		wsserver.join();

	}
}
