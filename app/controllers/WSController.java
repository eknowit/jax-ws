/**
 * 
 */
package controllers;

import java.io.IOException;

import play.libs.F.Promise;
import play.mvc.Controller;
import play.mvc.Http.Request;
import play.mvc.Http.Response;
import ws.AsynchronousWS;
import ws.JAXWS;
import ws.WSAdapter;
import ws.WSConnectionImpl;

import com.sun.xml.internal.ws.transport.http.WSHTTPConnection;

/**
 * @author eknowit
 * 
 */
public class WSController extends Controller {

	public static void invoke(String service) {

		WSAdapter target = getTarget(service);
		if (target != null) {
			AsynchronousWS ws = new AsynchronousWS(target, request, response);
			Promise promise = ws.now();
			await(promise);
		} else {
			notFound(String.format("Service %s not found", service));
		}

	}

	private static WSAdapter getTarget(String service) {

		return JAXWS.adapters.get(service);
	}

}
