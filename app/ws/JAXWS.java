/**
 * 
 */
package ws;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author eknowit
 * 
 */
public class JAXWS {

	public static final String WS_PATH = "/ws/";

	public static final Map<String, WSAdapter> adapters = new HashMap<String, WSAdapter>();

	public static List<WSAdapter> adapterList = new ArrayList<WSAdapter>();

	public static List<Class> classesList = new ArrayList<Class>();

	protected JAXWS(List<WSAdapter> adapters, List<Class> classes) {

		adapterList.addAll(adapters);
		for (WSAdapter adapter : adapters) {
			JAXWS.adapters.put(adapter.getName(), adapter);
		}
		classesList.addAll(classes);
	}

	public static void createContex(List<WSAdapter> adapters, List<Class> classes) {

		JAXWS.adapters.clear();
		JAXWS.adapterList.clear();
		JAXWS.classesList.clear();
		new JAXWS(adapters, classes);
	}

}
