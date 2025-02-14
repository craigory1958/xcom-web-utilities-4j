

package xcom.web4j.servlet.mvc.control ;


import java.io.IOException ;
import java.util.Arrays ;
import java.util.Enumeration ;
import java.util.Properties ;

import javax.servlet.ServletException ;
import javax.servlet.http.HttpServlet ;
import javax.servlet.http.HttpServletRequest ;
import javax.servlet.http.HttpServletResponse ;

import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

import xcom.utils4j.logging.Loggers ;
import xcom.utils4j.logging.annotations.Log ;
import xcom.web4j.servlet.mvc.model.SessionValue ;


public abstract class Servlet extends HttpServlet {

	private static final long serialVersionUID = 3570462782939086811L ;

	private static final Logger Logger = LoggerFactory.getLogger(Servlet.class) ;

	private static final Logger Console = LoggerFactory.getLogger(Loggers.ConsoleLoggerName) ;


	@Log
	static Properties loadProperties(final SessionValue sessionValue, final String propertiesName) {

		final Properties props = new Properties() ;

		try {
			final String propsFile = "/WEB-INF/" + propertiesName ;
			props.load(sessionValue.getServlet().getServletConfig().getServletContext().getResourceAsStream(propsFile)) ;
		}
		catch ( final Exception ex ) {
			ex.printStackTrace() ;
		}

		return (props) ;
	}


	abstract public void doAction(SessionValue sessionValue) throws ServletException, IOException ;


	abstract public SessionValue newSessionValue() ;


	@Override
	@Log
	protected final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		doAction(request, response) ;
	}


	@Override
	@Log
	protected final void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		doAction(request, response) ;
	}


	@Log
	protected final void doAction(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {

		Logger.debug("RequestURL: |{}|", request.getRequestURL()) ;
		Logger.debug("Protocol: |{}|", request.getProtocol()) ;
		Logger.debug("ServerName: |{}|", request.getServerName()) ;
		Logger.debug("ServerPort: |{}|", request.getServerPort()) ;
		Logger.debug("RequestURI: |{}|", request.getRequestURI()) ;
		Logger.debug("ContextPath: |{}|", request.getContextPath()) ;
		Logger.debug("ServletPath: |{}|", request.getServletPath()) ;
		Logger.debug("PathTranslated: |{}|", request.getPathTranslated()) ;
		Logger.debug("PathInfo: |{}|", request.getPathInfo()) ;
		Logger.debug("Method: |{}|", request.getMethod()) ;
		Logger.debug("RemoteUser: |{}|", request.getRemoteUser()) ;
		Logger.debug("RequestedSessionId: |{}|", request.getRequestedSessionId()) ;
		Logger.debug("QueryString: |{}|", request.getQueryString()) ;

		if ( Logger.isDebugEnabled() )
			for ( @SuppressWarnings("unchecked")
			final Enumeration<String> params = (request.getParameterNames()); (params.hasMoreElements()); /* no inc */ ) {
				final String param = (params.nextElement()) ;
				Logger.debug("Param: |{}|, |{}|", param, Arrays.toString(request.getParameterValues(param))) ;
			}

		//

		SessionValue sessionValue = (SessionValue) (request.getSession(true).getAttribute("sessionValue")) ;

		String curPrefix = getClass().getName() ;
		curPrefix = curPrefix.substring(0, curPrefix.lastIndexOf('.')) ;

		String prvPrefix = "" ;

		if ( sessionValue != null ) {
			prvPrefix = sessionValue.getServlet().getClass().getName() ;
			prvPrefix = prvPrefix.substring(0, prvPrefix.lastIndexOf('.')) ;
		}

		Logger.trace("Previous servlet base: |{}|", prvPrefix) ;
		Logger.trace("Current servlet base:  |{}|", curPrefix) ;

		if ( (sessionValue == null) || !curPrefix.equals(prvPrefix) ) {

			sessionValue = newSessionValue() ;
			Logger.debug("Created session value: |{}|", sessionValue) ;
			Console.info("Created session value {}", sessionValue) ;
		}
		else
			sessionValue.setInitial(false) ;

		//

		sessionValue.setServlet(this) ;
		sessionValue.setRequest(request) ;
		sessionValue.setResponse(response) ;

		if ( sessionValue.getProperties() == null )
			sessionValue.setProperties(loadProperties(sessionValue, "resources/appl-resources/appl.properties")) ;

		if ( sessionValue.isInitial() )
			sessionValue.initialize() ;

		sessionValue.parseRequestParameters() ;

		sessionValue.setDispatchURL("") ;
		Logger.debug("Reset dispatchURL") ;

		doAction(sessionValue) ;


		// Map servlet to default JSP page ...

		if ( sessionValue.getDispatchURL().isEmpty() ) {

			String url = sessionValue.getProperties().getProperty("appl.url." + request.getServletPath().substring(1), "") ;
			if ( url.isEmpty() )
				url = sessionValue.getProperties().getProperty("appl.url.default") ;

			sessionValue.setDispatchURL(url) ;
			Logger.debug("Set dispatchURL: |{}|", sessionValue.getDispatchURL()) ;
			Console.info("Set dispatchURL: {}", sessionValue.getDispatchURL()) ;
		}


		// Standard redirect to JSP page ...

		if ( !sessionValue.getDispatchURL().isEmpty() ) {

			request.getSession().setAttribute("sessionValue", sessionValue) ;
			Logger.debug("Fowarding to |{}|", sessionValue.getDispatchURL()) ;
			Console.info("Fowarding to {}", sessionValue.getDispatchURL()) ;
			getServletContext().getRequestDispatcher(sessionValue.getDispatchURL()).forward(request, response) ;
		}
	}
}
