/**
 * 
 */
package ws;

import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.transport.http.DeploymentDescriptorParser.AdapterFactory;
import com.sun.xml.internal.ws.transport.http.HttpAdapterList;

/**
 * @author eknowit
 * 
 */
public class WSAdapterList extends HttpAdapterList<WSAdapter> implements AdapterFactory<WSAdapter> {

	@Override
	protected WSAdapter createHttpAdapter(String name, String urlPattern, WSEndpoint<?> endpoint) {
		return new WSAdapter(name, endpoint, this, urlPattern);
	}

}
