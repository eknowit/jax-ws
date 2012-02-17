/**
 * 
 */
package ws;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.WebServiceException;

import org.apache.commons.lang.StringUtils;

import play.mvc.Http;
import play.mvc.Http.Header;
import play.mvc.Http.Request;
import play.mvc.Http.Response;

import com.sun.security.auth.UserPrincipal;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.server.PortAddressResolver;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.server.WebServiceContextDelegate;
import com.sun.xml.internal.ws.resources.WsservletMessages;
import com.sun.xml.internal.ws.transport.http.WSHTTPConnection;

/**
 * @author eknowit
 * 
 */
public class WSConnectionImpl extends WSHTTPConnection implements WebServiceContextDelegate {

	private final Request request;

	private final Response response;

	private final WSAdapter adapter;

	private final Map<String, Header> responseHeaders;

	private int status;

	private final Principal principal;

	private static final PropertyMap model;

	static {
		model = parse(WSConnectionImpl.class);
	}

	public WSConnectionImpl(WSAdapter wsAdapter, Request request, Response response) {

		this.request = request;
		this.response = response;
		this.adapter = wsAdapter;
		this.principal = new UserPrincipal(StringUtils.defaultIfEmpty(request.user, StringUtils.EMPTY));
		this.responseHeaders = new HashMap<String, Http.Header>(16);
	}

	@Override
	public InputStream getInput() throws IOException {

		return request.body;
	}

	@Override
	public OutputStream getOutput() throws IOException {

		this.response.status = this.status;
		if (!this.responseHeaders.isEmpty()) {
			String name;
			for (Map.Entry<String, Header> headerEntry : this.responseHeaders.entrySet()) {
				name = headerEntry.getKey();
				if (!name.equalsIgnoreCase("Content-Type") && !name.equalsIgnoreCase("Content-Length")) {
					this.response.headers.put(name, headerEntry.getValue());
				}
			}
		}
		return response.out;
	}

	@Override
	public String getPathInfo() {

		return request.path;
	}

	@Override
	public String getQueryString() {

		return request.querystring;
	}

	@Override
	public String getRequestHeader(String headerName) {

		headerName = headerName.toLowerCase();
		if (!request.headers.containsKey(headerName)) {
			return null;
		}
		return request.headers.get(headerName).value();
	}

	@Override
	public Map<String, List<String>> getRequestHeaders() {

		Map<String, List<String>> requestHeaders = new HashMap<String, List<String>>();
		for (Map.Entry<String, Header> entry : request.headers.entrySet()) {
			requestHeaders.put(entry.getKey(), entry.getValue().values);
		}
		return requestHeaders;
	}

	@Override
	public String getRequestMethod() {

		return request.method;
	}

	@Override
	public Map<String, List<String>> getResponseHeaders() {

		Map<String, List<String>> responseHeaders = new HashMap<String, List<String>>();
		for (Map.Entry<String, Header> entry : this.responseHeaders.entrySet()) {
			responseHeaders.put(entry.getKey(), entry.getValue().values);
		}
		return responseHeaders;
	}

	@Override
	public int getStatus() {

		return this.status;
	}

	@Override
	public WebServiceContextDelegate getWebServiceContextDelegate() {

		return this;
	}

	@Override
	public boolean isSecure() {

		return this.request.secure;
	}

	@Override
	public void setContentTypeResponseHeader(String value) {

		this.response.contentType = value;
	}

	@Override
	public void setResponseHeaders(Map<String, List<String>> headers) {

		if (headers != null) {
			String name;
			for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
				name = entry.getKey();
				this.responseHeaders.put(name, new Header(name, entry.getValue()));
			}
		}
	}

	@Override
	public void setStatus(int status) {

		this.status = status;
	}

	@Override
	protected PropertyMap getPropertyMap() {

		return WSConnectionImpl.model;
	}

	@Override
	public String getEPRAddress(Packet packet, WSEndpoint endpoint) {

		PortAddressResolver resolver = adapter.owner.createPortAddressResolver(getBaseAddress());
		String address = resolver.getAddressFor(endpoint.getServiceName(), endpoint.getPortName().getLocalPart());
		if (address == null)
			throw new WebServiceException(WsservletMessages.SERVLET_NO_ADDRESS_AVAILABLE(endpoint.getPortName()));
		return address;
	}

	@Override
	public Principal getUserPrincipal(Packet p) {

		return principal;
	}

	@Override
	public String getWSDLAddress(Packet packet, WSEndpoint endpoint) {

		String eprAddress = getEPRAddress(packet, endpoint);
		if (adapter.getEndpoint().getPort() != null)
			return eprAddress + "?wsdl";
		else
			return null;
	}

	@Override
	public boolean isUserInRole(Packet packet, String role) {

		return true;
	}

	@Override
	public String getBaseAddress() {
		return (request.secure ? "https://" : "http://") + request.host + request.path;
	}

}
