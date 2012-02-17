/**
 * 
 */
package ws;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jws.WebService;
import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.EntityResolver;

import play.Logger;
import play.Play;
import play.utils.Java;

import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.InstanceResolver;
import com.sun.xml.internal.ws.api.server.Invoker;
import com.sun.xml.internal.ws.api.server.SDDocument;
import com.sun.xml.internal.ws.api.server.SDDocumentSource;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.model.RuntimeModeler;
import com.sun.xml.internal.ws.transport.http.DeploymentDescriptorParser.AdapterFactory;

/**
 * @author eknowit
 * 
 */
public class WSClassPathParser<A> {

	private final AdapterFactory<A> adapterFactory;

	private final Set<String> names = new HashSet<String>();

	private final List<A> adapters = new ArrayList<A>();

	private List<Class> wsClasses;

	public WSClassPathParser(AdapterFactory<A> adapterFactory) {
		this.adapterFactory = adapterFactory;
	}

	public WSClassPathParser<A> parse() {

		List<Class> wsClasses = Play.classloader.getAnnotatedClasses(WebService.class);
		this.wsClasses = wsClasses;

		for (Class wsClass : wsClasses) {
			if (!wsClass.isInterface()) {
				try {
					String name = getServiceName(wsClass);
					if (!names.add(name)) {
						Logger.warn("There are other webservice with the same name %s", name);
					}
					Class<?> implementorClass = wsClass;
					Object implementation = implementorClass.newInstance();
					SDDocumentSource primaryWSDL = null;
					QName serviceName = null;
					QName portName = null;
					WSBinding binding = BindingImpl.create(BindingID.parse(wsClass));
					String urlPattern = getUrlPattern(wsClass);
					Collection<SDDocumentSource> docs = new ArrayList<SDDocumentSource>();
					Invoker invoker = InstanceResolver.createSingleton(implementation).createInvoker();
					EntityResolver entityResolver = null;
					Container container = null;
					WSEndpoint<?> endpoint = WSEndpoint.create(implementorClass, true, invoker, serviceName, portName, container, binding, primaryWSDL, docs, entityResolver, true);
					this.adapters.add(adapterFactory.createAdapter(name, urlPattern, endpoint));
				} catch (Exception e) {
					Object[] args = { wsClass.getName() };
					Logger.warn(e, "The class %s hasn't loaded", args);
				}
			}
		}

		return this;
	}

	private String getUrlPattern(Class wsClass) {

		return StringUtils.EMPTY;
	}

	private String getServiceName(Class wsClass) {

		// TODO Cambiar por un nombre correspondiente al campo serviceName de la anotacion @WebService o @WebServiceProvider
		return wsClass.getSimpleName();
	}

	public List<Class> getClasses() {

		return this.wsClasses;
	}

	public List<A> getAdapters() {

		return this.adapters;
	}

}
