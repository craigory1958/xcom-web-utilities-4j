

package xcom.web4j.servlet.mvc.control ;


import static xcom.web4j.servlet.mvc.view.ResponseGUIs.ScreenData ;
import static xcom.web4j.servlet.mvc.view.ResponseGUIs.ScreenDataWidget ;
import static xcom.web4j.servlet.mvc.view.ResponseGUIs.ScreenDef_Length ;
import static xcom.web4j.servlet.mvc.view.ResponseGUIs.ScreenRequestTypeWidget ;
import static xcom.web4j.servlet.mvc.view.ResponseGUIs.ScreenRequestWidget ;
import static xcom.web4j.servlet.mvc.view.ResponseGUIs.ScreenValues ;

import java.util.ArrayList ;
import java.util.HashMap ;
import java.util.LinkedHashMap ;
import java.util.List ;
import java.util.Map ;
import java.util.Map.Entry ;

import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

import com.google.gson.reflect.TypeToken ;

import xcom.utils4j.JSONs ;
import xcom.utils4j.logging.Loggers ;
import xcom.utils4j.logging.annotations.Log ;
import xcom.web4j.servlet.mvc.model.ResponseSessionValue ;
import xcom.web4j.servlet.mvc.model.SessionValue ;
import xcom.web4j.servlet.mvc.view.ResponseGUIs ;


public abstract class ResponseServlet extends LogonServlet {

	private static final long serialVersionUID = 7417431327997227436L ;

	private static final Logger Logger = LoggerFactory.getLogger(ResponseServlet.class) ;

	private static final Logger Console = LoggerFactory.getLogger(Loggers.ConsoleLoggerName) ;


	@Log
	@Override
	public void doAction(final SessionValue sessionValue) {

		final ResponseSessionValue sv = ((ResponseSessionValue) sessionValue) ;


		// Extract screen request from form submission ...
		{
			String[] parm = null ;
			@SuppressWarnings("unchecked")
			final Map<String, String[]> parms = sv.getRequest().getParameterMap() ;

			parm = parms.get(ScreenRequestTypeWidget) ;
			sv.setScreenRequestType((parm != null ? parm[0] : "Null")) ;

			parm = parms.get(ScreenRequestWidget) ;
			sv.setScreenRequest((parm != null ? parm[0] : "Null")) ;
		}


		// Initialize screen data if first time ...
		{
			final Map<String, Object> svs = sv.getSessionValues() ;

			if ( sessionValue.isInitial() || !svs.containsKey(ScreenDataWidget) ) {
				svs.put(ScreenDataWidget, new LinkedHashMap<String, String>()) ;
				svs.put(ScreenData, JSONs.toJSON(new ArrayList<Object>())) ;
			}
		}


		//
		// Fetch submitted data packaged as a JSON Array string ...
		//

		{
			Map<String, String> _screenData ;
			String dataJSONString = "[]" ;
			final String[] data = sessionValue.getRequestParameters().get(ScreenDataWidget) ;
			if ( (data != null) && (data.length > 0) )
				dataJSONString = data[0] ;

			dataJSONString = dataJSONString.replace("\\\"", "") ;
			Logger.debug("Raw screen data |{}|", dataJSONString) ;

			_screenData = JSONs.fromJSON(dataJSONString, new TypeToken<HashMap<String, String>>() {}.getType()) ;
			Logger.debug("Received screen data: |{}|", _screenData) ;

			sv.setScreenData(_screenData) ;
			Logger.debug("Received screen data: |{}|", sv.getScreenData()) ;
			Console.info("Received screen data {}", sv.getScreenData()) ;

			doAction(_screenData, sessionValue) ;
		}


		//
		// Store screen information into Session Value ...
		//

		final Map<String, Object> svs = sv.getSessionValues() ;

		// Store screen defs ...
		{
			final ArrayList<List<Object>> _screenDefs = new ArrayList<List<Object>>() ;

			if ( sv.getScreenDef() != null )
				for ( int i = 0; (i < sv.getScreenDef().length); i++ ) {
					final ArrayList<Object> _screenDef = new ArrayList<Object>() ;
					_screenDefs.add(_screenDef) ;

					for ( int f = 0; (f < ScreenDef_Length); f++ )
						_screenDef.add(sv.getScreenDef()[i][f]) ;
				}

			svs.put(ResponseGUIs.ScreenDefs, _screenDefs) ;
			Logger.debug("Returned screen def: |{}|", _screenDefs) ;
			Console.debug("Returned screen def {}", _screenDefs) ;
		}

		// Store screen data ...
		{
			svs.put(ScreenDataWidget, sv.getScreenData()) ;
			Logger.debug("Returned screen data: |{}|", sv.getScreenData()) ;
			Console.debug("Returned screen data {}", sv.getScreenData()) ;

			final List<List<Object>> screenValues = new ArrayList<List<Object>>() ;

			for ( final Entry<String, String> entry : sv.getScreenData().entrySet() ) {
				final List<Object> screenValue = new ArrayList<Object>() ;
				screenValues.add(screenValue) ;

				screenValue.add(entry.getKey()) ;
				screenValue.add(entry.getValue()) ;
			}
			Logger.debug("Returned screen values: |{}|", screenValues) ;

			svs.put(ScreenValues, screenValues) ;
		}

		//
		if ( Logger.isTraceEnabled() )
			for ( final String key : sv.getSessionValues().keySet() )
				Logger.trace("Session value |{}|: |{}|", key, sv.getSessionValues().get(key)) ;
	}


	abstract public void doAction(Map<String, String> screenData, SessionValue sessionValue) ;


	@Override
	abstract public SessionValue newSessionValue() ;
}
