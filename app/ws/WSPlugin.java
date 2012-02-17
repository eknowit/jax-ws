/**
 * 
 */
package ws;

import java.util.List;

import javax.jws.WebService;

import org.apache.commons.collections.CollectionUtils;

import com.sun.xml.internal.ws.api.server.Container;

import play.Play;
import play.PlayPlugin;

/**
 * @author eknowit
 * 
 */
public class WSPlugin extends PlayPlugin {

	@Override
	public void onApplicationStart() {

		WSClassPathParser<WSAdapter> parser = new WSClassPathParser<WSAdapter>(new WSAdapterList());
		parser.parse();
		JAXWS.createContex(parser.getAdapters(),parser.getClasses());
	}

}
