/**
 * 
 */
package ws;

import java.io.IOException;

import com.sun.xml.internal.ws.transport.http.WSHTTPConnection;

import play.jobs.Job;
import play.mvc.Http.Request;
import play.mvc.Http.Response;

/**
 * @author eknowit
 * 
 */
public class AsynchronousWS extends Job {

	private WSAdapter target;

	private Request request;

	private Response response;

	public AsynchronousWS(WSAdapter target, Request request, Response response) {

		this.target = target;
		this.request = request;
		this.response = response;
	}

	@Override
	public void doJob() throws Exception {

		this.target.handle(this.request, this.response);
	}

}
