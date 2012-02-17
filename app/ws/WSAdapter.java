/**
 * 
 */
package ws;

import java.io.IOException;

import javax.xml.namespace.QName;

import play.mvc.Http.Request;
import play.mvc.Http.Response;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.server.Adapter;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.server.Adapter.Toolkit;
import com.sun.xml.internal.ws.transport.http.HttpAdapter;
import com.sun.xml.internal.ws.transport.http.HttpAdapterList;
import com.sun.xml.internal.ws.transport.http.WSHTTPConnection;

/**
 * @author eknowit
 * 
 */
public class WSAdapter extends HttpAdapter {

	final String name;

	protected WSAdapter(String name, WSEndpoint endpoint, HttpAdapterList<? extends HttpAdapter> adapterList, String urlPattern) {
		super(endpoint, adapterList, urlPattern);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public QName getPortName() {
		WSDLPort port = getEndpoint().getPort();
		if (port == null) {
			return null;
		} else {
			return port.getName();
		}
	}

	public void handle(Request request, Response response) throws IOException {
		WSHTTPConnection connection = new WSConnectionImpl(this, request, response);
		super.handle(connection);
	}

	public void dispose() {
		endpoint.dispose();
	}

}
