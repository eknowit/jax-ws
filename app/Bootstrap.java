import play.jobs.Job;
import play.jobs.OnApplicationStart;
import ws.WSPlugin;

/**
 * 
 */

/**
 * @author eknowit
 * 
 */
@OnApplicationStart
public class Bootstrap extends Job {

	@Override
	public void doJob() throws Exception {
		
		new WSPlugin().onApplicationStart();
	}

}
