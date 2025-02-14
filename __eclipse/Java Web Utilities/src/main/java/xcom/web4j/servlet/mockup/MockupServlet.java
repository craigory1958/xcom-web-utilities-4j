

package xcom.web4j.servlet.mockup ;


import static xcom.web4j.servlet.mockup.MockupScriptColumns.ScriptColumn_DOM_ElementID ;
import static xcom.web4j.servlet.mockup.MockupScriptColumns.ScriptColumn_DOM_Values ;
import static xcom.web4j.servlet.mockup.MockupScriptColumns.ScriptColumn_ScriptValues ;

import java.util.List ;
import java.util.Map ;

import org.fest.reflect.core.Reflection ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

import xcom.utils4j.logging.Loggers ;
import xcom.utils4j.logging.annotations.Log ;
import xcom.web4j.servlet.mvc.control.ResponseServlet ;
import xcom.web4j.servlet.mvc.model.SessionValue ;


public class MockupServlet extends ResponseServlet {

	private static final long serialVersionUID = 4321809181647127553L ;

	private static final Logger Logger = LoggerFactory.getLogger(MockupServlet.class) ;

	private static final Logger Console = LoggerFactory.getLogger(Loggers.ConsoleLoggerName) ;


	@Log
	@Override
	public void doAction(final Map<String, String> screenData, final SessionValue sessionValue) {

		final MockupSessionValue sv = ((MockupSessionValue) sessionValue) ;
		Logger.debug("sv.currentScriptID: |{}|", sv.currentScriptID) ;
		Console.info("Processing responses for scriptID {}", sv.currentScriptID) ;

		@SuppressWarnings("unchecked")
		final Map<String, String[]> parms = sv.getRequest().getParameterMap() ;
		Logger.debug("parms: |{}|", parms) ;

		final List<Map<String, String>> responses = sv.scripts.get(sv.currentScriptID).getResponses() ;
		Logger.debug("responses: |{}|", responses) ;

		boolean found = false ;
		Map<String, String> response = null ;

		for ( int r = 0; (!found && (r < responses.size())); r++ ) {
			response = responses.get(r) ;

			final String domElementID = response.get(ScriptColumn_DOM_ElementID) ;
			final String domValues = response.get(ScriptColumn_DOM_Values) ;
			Logger.debug("Matching response |{}| to |{}|", domElementID, domValues) ;

			final String[] parm = parms.get(domElementID) ;

			if ( domElementID.isEmpty() || ((parm != null) && parm[0].equals(domValues)) )
				found = true ;
		}

		if ( found ) {
			sv.currentScriptID = response.get(ScriptColumn_ScriptValues) ;
			Logger.debug("Next scriptID: |{}|", sv.currentScriptID) ;
			Console.info("Next scriptID {}", sv.currentScriptID) ;
		}

		final Object[][] obj = Reflection.staticField(sv.scripts.get(sv.currentScriptID).screenDef).ofType(Object[][].class).in(sv.gui).get() ;
		sv.setScreenDef(obj) ;
		Logger.debug("Set screenDef: |{}|", ((Object[]) obj)) ;

		sv.setDispatchURL(sv.scripts.get(sv.currentScriptID).getURL()) ;
		Logger.debug("Set dispatchURL: |{}|", sv.getDispatchURL()) ;
	}


	@Log
	@Override
	public SessionValue newSessionValue() {
		return (new MockupSessionValue()) ;
	}
}
