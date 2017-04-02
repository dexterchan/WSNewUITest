package NewUITest.WSNewUITest.Service;

import org.eclipse.jetty.websocket.server.WebSocketServerFactory;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class MyEchoServlet extends WebSocketServlet
{
	
	
    @Override
	public void configure(WebSocketServletFactory factory) {
		// TODO Auto-generated method stub
    	factory.register(MyEchoSocket.class);
	}

//	@Override
//    public void configure(WebSocketServerFactory factory)
//    {
//        // register a socket class as default
//        factory.register(MyEchoSocket.class);
//    }
}
