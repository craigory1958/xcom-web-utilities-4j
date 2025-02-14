

package xcom.web4j.servlet.mvc.model ;


import java.io.IOException ;
import java.net.MalformedURLException ;
import java.util.Arrays ;
import java.util.Enumeration ;
import java.util.HashMap ;
import java.util.Iterator ;
import java.util.List ;
import java.util.Map ;
import java.util.Properties ;

import javax.servlet.Servlet ;
import javax.servlet.http.HttpServletRequest ;
import javax.servlet.http.HttpServletResponse ;

import org.apache.commons.fileupload.FileItem ;
import org.apache.commons.fileupload.FileUploadException ;
import org.apache.commons.fileupload.disk.DiskFileItemFactory ;
import org.apache.commons.fileupload.servlet.ServletFileUpload ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

import xcom.utils4j.logging.annotations.Log ;


public abstract class SessionValue {

	private static final Logger Logger = LoggerFactory.getLogger(SessionValue.class) ;


	public static final int DebugLevel_None = 0 ;

	public static final int DebugLevel_Error = 1 ;

	public static final int DebugLevel_Info = 2 ;

	public static final int DebugLevel_Debug = 3 ;

	public static final int DebugLevel_Trace = 4 ;


	/**
	 *
	 */
	String dispatchURL ;

	public String getDispatchURL() {
		return (dispatchURL) ;
	}

	public SessionValue setDispatchURL(final String dispatchURL) {
		this.dispatchURL = dispatchURL ;
		return (this) ;
	}


	/**
	 *
	 */
	boolean initial ;

	public boolean isInitial() {
		return (initial) ;
	}

	public SessionValue setInitial(final boolean initial) {
		this.initial = initial ;
		return (this) ;
	}


	/**
	 *
	 */
	Properties properties ;

	public Properties getProperties() {
		return (properties) ;
	}

	public SessionValue setProperties(final Properties properties) {
		this.properties = properties ;
		return (this) ;
	}


	/**
	 *
	 */
	HttpServletRequest request ;

	public HttpServletRequest getRequest() {
		return (request) ;
	}

	public SessionValue setRequest(final HttpServletRequest request) {
		this.request = request ;
		return (this) ;
	}


	/**
	 *
	 */
	Map<String, String[]> requestParameters ;

	public Map<String, String[]> getRequestParameters() {
		return (requestParameters) ;
	}

	public SessionValue setRequestParameters(final Map<String, String[]> requestParameters) {
		this.requestParameters = requestParameters ;
		return (this) ;
	}


	/**
	 *
	 */
	Map<String, FileItem> requestFileItems ;

	public Map<String, FileItem> getRequestFileItems() {
		return (requestFileItems) ;
	}

	public SessionValue setRequestFileItems(final Map<String, FileItem> requestFileItems) {
		this.requestFileItems = requestFileItems ;
		return (this) ;
	}


	/**
	 *
	 */
	HttpServletResponse response ;

	public HttpServletResponse getResponse() {
		return (response) ;
	}

	public SessionValue setResponse(final HttpServletResponse response) {
		this.response = response ;
		return (this) ;
	}


	/**
	 *
	 */
	Servlet servlet ;

	public Servlet getServlet() {
		return (servlet) ;
	}

	public SessionValue setServlet(final Servlet servlet) {
		this.servlet = servlet ;
		return (this) ;
	}


	/**
	 *
	 */
	String applDescription ;

	public String getApplDescription() {
		return (applDescription) ;
	}

	public SessionValue setApplDescription(final String applDescription) {
		this.applDescription = applDescription ;
		return (this) ;
	}


	/**
	 *
	 */
	String applName ;

	public String getApplName() {
		return (applName) ;
	}

	public SessionValue setApplName(final String applName) {
		this.applName = applName ;
		return (this) ;
	}


	/**
	 *
	 */
	String applTitle ;

	public String getApplTitle() {
		return (applTitle) ;
	}

	public SessionValue setApplTitle(final String applTitle) {
		this.applTitle = applTitle ;
		return (this) ;
	}


	@Log
	public SessionValue() {
		initial = true ; // Mark as first time in session.
	}


	@Log
	public String getContextPath() {
		return (request.getContextPath()) ;
	}


	@Log
	public void initialize() throws MalformedURLException, IOException {
		applTitle = properties.getProperty("appl.title") ;
		applName = properties.getProperty("appl.name") ;
		applDescription = properties.getProperty("appl.description") ;
	}


	@Log
	public boolean isDebug(final int level) {

		boolean debug = false ;
		final int debugLevel = Integer.valueOf(properties.getProperty("appl.debug.level", "0")) ;

		if ( (debugLevel > 0) && (debugLevel <= level) )
			debug = true ;

		return (debug) ;
	}


	@Log
	public void parseRequestParameters() {

		requestParameters = new HashMap<String, String[]>() ;
		requestFileItems = new HashMap<String, FileItem>() ;

		if ( ServletFileUpload.isMultipartContent(request) ) {
			Logger.trace("Processing multipart content ...") ;

			final ServletFileUpload servletFileUpload = new ServletFileUpload(new DiskFileItemFactory()) ;
			List<?> fileItemsList ;

			try {
				fileItemsList = servletFileUpload.parseRequest(request) ;
				Logger.trace("Processing |{}| item multipart content ...", fileItemsList.size()) ;

				final Iterator<?> it = fileItemsList.iterator() ;
				while ( it.hasNext() ) {
					final FileItem fi = ((FileItem) it.next()) ;
					Logger.trace("processing |{}|", fi.getFieldName()) ;

					final String fieldName = fi.getFieldName() ;

					if ( fi.isFormField() ) {
						String[] values = requestParameters.get(fieldName) ;

						if ( values == null )
							values = new String[0] ;

						values = Arrays.copyOf(values, values.length + 1) ;
						values[values.length - 1] = fi.getString() ;

						requestParameters.put(fieldName, values) ;

					}
					else {
						final String[] fileNames = new String[1] ;
						fileNames[0] = fi.getName() ;
						requestParameters.put(fieldName, fileNames) ;

						requestFileItems.put(fieldName, fi) ;
					}
				}

			}
			catch ( final FileUploadException ex ) {}

		}
		else {
			Logger.trace("Processing singlepart content ...") ;

			for ( @SuppressWarnings("unchecked")
			final Enumeration<String> params = (request.getParameterNames()); (params.hasMoreElements()); /* no inc */ ) {
				final String param = (params.nextElement()) ;
				requestParameters.put(param, request.getParameterValues(param)) ;
			}
		}

		Logger.trace("Parsed parameters: |{}|", requestParameters) ;
		Logger.trace("Parsed file items: |{}|", requestFileItems) ;
	}
}
