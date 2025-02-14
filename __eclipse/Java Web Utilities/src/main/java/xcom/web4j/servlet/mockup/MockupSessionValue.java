

package xcom.web4j.servlet.mockup ;


import static xcom.utils4j.data.columnar.iColumnarDataReader.EXACT_COLUMN_NAME_POLICY ;
import static xcom.web4j.servlet.mockup.MockupScriptColumns.ScriptColumn_DOM_Attribute ;
import static xcom.web4j.servlet.mockup.MockupScriptColumns.ScriptColumn_DOM_ElementID ;
import static xcom.web4j.servlet.mockup.MockupScriptColumns.ScriptColumn_DOM_Method ;
import static xcom.web4j.servlet.mockup.MockupScriptColumns.ScriptColumn_DOM_Values ;
import static xcom.web4j.servlet.mockup.MockupScriptColumns.ScriptColumn_ScriptID ;
import static xcom.web4j.servlet.mockup.MockupScriptColumns.ScriptColumn_ScriptType ;
import static xcom.web4j.servlet.mockup.MockupScriptColumns.ScriptColumn_ScriptValues ;

import java.io.IOException ;
import java.net.MalformedURLException ;
import java.util.ArrayList ;
import java.util.HashMap ;
import java.util.List ;
import java.util.Map ;

import org.fest.reflect.core.Reflection ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

import xcom.utils4j.data.columnar.ColumnarDataReader ;
import xcom.utils4j.data.columnar.ColumnarDataReader_CSV ;
import xcom.utils4j.format.Templator ;
import xcom.utils4j.format.Templator.Templator$Delimited ;
import xcom.utils4j.logging.Loggers ;
import xcom.utils4j.logging.annotations.Log ;
import xcom.web4j.servlet.mvc.model.ResponseSessionValue ;
import xcom.web4j.servlet.mvc.view.ResponseGUIs ;


public class MockupSessionValue extends ResponseSessionValue {

	private static final Logger Logger = LoggerFactory.getLogger(MockupSessionValue.class) ;

	private static final Logger Console = LoggerFactory.getLogger(Loggers.ConsoleLoggerName) ;

	class Script {

		String scriptID ;
		String screenDef ;
		String url ;
		List<Map<String, String>> markups ;
		List<Map<String, String>> responses ;

		public List<Map<String, String>> getResponses() {
			return (responses) ;
		}

		public String getURL() {
			return (url) ;
		}
	}

	Templator$Delimited templator = Templator.openTagDelimiter("<?").closeTagDelimiter("?>") ;

	Map<String, Script> scripts ;

	String currentScriptID ;

	Class<?> gui ;

	@Log
	public MockupSessionValue() {}

	@Log
	@Override
	public void initialize() throws MalformedURLException, IOException {

		super.initialize() ;

		{
			gui = Reflection.type(getProperties().getProperty("appl.mockup.gui.class")).load() ;
			Logger.debug("Loaded GUI object: |{}|", gui) ;

			ResponseGUIs.loadScreenConstants(gui) ;
			Logger.debug("Loaded GUIs constants") ;
		}

		{
			final String inFSpec = "/WEB-INF/resources/mockup-resources/" + getProperties().getProperty("appl.mockup.script", "script.csv") ;
			Logger.debug("Loading script file {} ...", inFSpec) ;

			loadScriptFile(new ColumnarDataReader_CSV(getServlet().getServletConfig().getServletContext().getResource(inFSpec), EXACT_COLUMN_NAME_POLICY)) ;
			Console.info("Loaded script file {}", inFSpec) ;
		}
	}

	@Log
	public String getInitializeScript() {

		String script = "" ;
		boolean done = false ;

		while ( !done ) {
			Logger.debug("Processing script: |{}|", currentScriptID) ;

			for ( final Map<String, String> markup : scripts.get(currentScriptID).markups ) {

				final String domElementID = markup.get(ScriptColumn_DOM_ElementID) ;
				final String domMethodName = markup.get(ScriptColumn_DOM_Method) ;
				final String domAttributeName = markup.get(ScriptColumn_DOM_Attribute) ;
				final String domValues = markup.get(ScriptColumn_DOM_Values) ;

				if ( !domAttributeName.isEmpty() )
					script += "\n document .getElementById('" + domElementID + "') ." + domAttributeName + " = '" + domValues + "' ;" ;

				if ( !domMethodName.isEmpty() )
					script += "\n document .getElementById('" + domElementID + "') ." + domMethodName + "() ;" ;
			}

			//

			done = true ;

			final List<Map<String, String>> responses = scripts.get(currentScriptID).getResponses() ;
			final Map<String, String> response = responses.get(0) ;
			final String elementID = response.get(ScriptColumn_DOM_ElementID) ;
			final String scriptValues = response.get(ScriptColumn_ScriptValues) ;

			if ( (responses.size() == 1) && elementID.isEmpty() && !scriptValues.equals(currentScriptID) ) {
				done = false ;
				currentScriptID = scriptValues ;
			}
		}

		script += "\n         if ( document .getElementById('mockupStep') != null ) " ;
		script += "\n             document .getElementById('mockupStep') .innerText = '" + currentScriptID + "' ;" ;

		Logger.debug("DOM initialization script - {}", script) ;

		return (script) ;
	}

	@Log
	void loadScriptFile(final ColumnarDataReader csv) throws IOException {

		Logger.trace("columns: |{}|", csv.getColumnNames()) ;

		scripts = new HashMap<String, Script>() ;

		{
			Script script = null ;
			String prvScriptID = null ;

			while ( csv.next() ) {
				final String scriptID = csv.getColumn(ScriptColumn_ScriptID).trim() ;
				final String scriptType = csv.getColumn(ScriptColumn_ScriptType).trim() ;
				final String scriptValues = templator.template(csv.getColumn(ScriptColumn_ScriptValues)).inject(ResponseGUIs.constants).trim() ;
				final String domElementID = templator.template(csv.getColumn(ScriptColumn_DOM_ElementID)).inject(ResponseGUIs.constants).trim() ;
				final String domMethodName = templator.template(csv.getColumn(ScriptColumn_DOM_Method)).inject(ResponseGUIs.constants).trim() ;
				final String domAttributeName = templator.template(csv.getColumn(ScriptColumn_DOM_Attribute)).inject(ResponseGUIs.constants).trim() ;
				final String domValues = templator.template(csv.getColumn(ScriptColumn_DOM_Values)).inject(ResponseGUIs.constants).trim() ;

				final String curScriptID = scriptID ;

				if ( !curScriptID.equals(prvScriptID) ) {

					if ( script != null )
						scripts.put(prvScriptID, script) ;

					script = new Script() ;
					script.scriptID = curScriptID ;
					script.url = scriptValues ;
					script.markups = new ArrayList<Map<String, String>>() ;
					script.responses = new ArrayList<Map<String, String>>() ;
				}

				if ( scriptType.equals("Header") ) {

					final String[] values = scriptValues.split(";") ;

					for ( String value : values ) {
						value = value.trim() ;

						if ( value.indexOf("ScreenDef=") >= 0 )
							script.screenDef = value.substring(10) ;
						if ( value.indexOf("URL=") >= 0 )
							script.url = value.substring(4) ;
					}
				}

				if ( scriptType.equals("Markup") ) {

					final Map<String, String> markup = new HashMap<String, String>() ;
					markup.put(ScriptColumn_DOM_ElementID, domElementID) ;
					markup.put(ScriptColumn_DOM_Method, domMethodName) ;
					markup.put(ScriptColumn_DOM_Attribute, domAttributeName) ;
					markup.put(ScriptColumn_DOM_Values, domValues) ;

					script.markups.add(markup) ;
					Logger.trace("Markup: |{}|", markup) ;
				}

				if ( scriptType.equals("Response") ) {

					final Map<String, String> response = new HashMap<String, String>() ;
					response.put(ScriptColumn_ScriptValues, scriptValues) ;
					response.put(ScriptColumn_DOM_ElementID, domElementID) ;
					response.put(ScriptColumn_DOM_Values, domValues) ;

					script.getResponses().add(response) ;
					Logger.trace("Response: |{}|", response) ;
				}

				prvScriptID = curScriptID ;
			}

			scripts.put(prvScriptID, script) ;

			csv.close() ;
		}

		currentScriptID = getProperties().getProperty("appl.mockup.script.start") ;

		Logger.debug("Initial scriptID: |{}|", currentScriptID) ;
		Console.info("Initial scriptID {}", currentScriptID) ;

		for ( final String scriptID : scripts.keySet() ) {
			final Script script = scripts.get(scriptID) ;
			Logger.debug("Header |{}|", script.scriptID) ;
			Logger.debug("... |{}| |{}|", script.url, script.screenDef) ;
			Logger.debug("Markups |{}|", script.markups) ;
			Logger.debug("Responses |{}|", script.getResponses()) ;
		}

		// Initialize starting script ...

		final Object[][] obj = Reflection.staticField(scripts.get(currentScriptID).screenDef).ofType(Object[][].class).in(gui).get() ;
		setScreenDef(obj) ;
		Logger.debug("Initial screenDef: |{}|", ((Object[]) obj)) ;
	}
}
