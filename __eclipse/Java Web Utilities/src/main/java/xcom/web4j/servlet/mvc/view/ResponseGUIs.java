

package xcom.web4j.servlet.mvc.view ;


import java.lang.reflect.Field ;
import java.lang.reflect.Modifier ;
import java.util.HashMap ;
import java.util.Map ;

import org.apache.commons.lang3.StringUtils ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

import xcom.utils4j.format.Templator ;
import xcom.utils4j.format.Templator.Templator$Delimited ;
import xcom.utils4j.logging.annotations.Log ;


public abstract class ResponseGUIs {

	private static final Logger Logger = LoggerFactory.getLogger(ResponseGUIs.class) ;


	public static Map<String, Object> constants ;

	static Templator$Delimited templator = Templator.openTagDelimiter("<?").closeTagDelimiter("?>") ;


	//
	// Standard screen widgets ...
	//

	public static final String ScreenDataWidget = "screenDataWidget" ;

	public static final String ScreenFormWidget = "screenFormWidget" ;

	public static final String ScreenRequestWidget = "screenRequestWidget" ;

	public static final String ScreenRequestTypeWidget = "screenRequestTypeWidget" ;

	//

	public static final String ScreenDefs = "ScreenDefs" ;

	public static final String ScreenData = "ScreenData" ;

	public static final String ScreenValues = "ScreenValues" ;

	//

	public static final int ScreenDef_Length = 5 ; //

	public static final int ScreenDefEntry_DOMElementID = 0 ; // Element ID

	public static final int ScreenDefEntry_Submitted = 1 ; // Include field in form submission

	public static final int ScreenDefEntry_DOMType = 2 ; // HTML type

	public static final int ScreenDefEntry_GroupMsg = 3 ; // Field ID to report error messages

	public static final int ScreenDefEntry_Moniker = 4 ; // Moniker


	/**
	 * @param clazz
	 */
	@Log
	public static void loadScreenConstants(final Class<?> clazz) {

		if ( constants == null )
			constants = new HashMap<String, Object>() ;

		for ( final Field member : clazz.getFields() ) {
			final int mods = member.getModifiers() ;

			if ( Modifier.isPublic(mods) && Modifier.isStatic(mods) && Modifier.isFinal(mods)
					&& member.getType().getSimpleName().equals(String.class.getSimpleName()) )
				try {
					Logger.trace("Member: |{}|", member.toGenericString()) ;
					Logger.trace("|{}|", member.getName()) ;
					Logger.trace("|{}|", member.get(null)) ;

					constants.put(member.getName(), ((String) member.get(null))) ;

				}
				catch ( final IllegalArgumentException ex ) {
					ex.printStackTrace() ;

				}
				catch ( final IllegalAccessException ex ) {
					ex.printStackTrace() ;
				}
		}

		Logger.debug("constants: |{}|", constants) ;
	}


	/**
	 * @param fieldID
	 * @param screenDefs
	 * @return
	 */
	@Log
	public static Object[] fetchFieldSpecs(final String fieldID, final Object[][] screenDefs) {

		boolean found = false ;
		int i = 0 ;
		for ( /* no init */; ((i < screenDefs.length) && !found); i++ )
			if ( ((String) screenDefs[i][ScreenDefEntry_DOMElementID]).equals(fieldID) )
				found = true ;

		return (screenDefs[i - 1]) ;
	}


	/**
	 * @param enable
	 * @param href
	 * @param title
	 * @param content
	 * @return
	 */
	@Log
	public static String formatMenuItem(final boolean enable, final String href, final String title, final String content) {

		String results = "" ;

		results += (enable ? "<li>" : "<li  class='disable' >") ;
		results += (enable ? "<a  href='" + href + "'" : "<a") ;
		results += "  title='" + title + "' >" + content + "</a></li>" ;

		return (results) ;
	}


	/**
	 * @param object
	 * @return
	 */
	@Log
	public static boolean normalizeToBoolean(final Object object) {

		final String str = StringUtils.trimToNull(((String) object)) ;

		return (Boolean.valueOf(str == null ? "false" : str)) ;
	}


	/**
	 * @param object
	 * @return
	 */
	@Log
	public static String normalizeToNumeric(final Object object) {
		return (StringUtils.trimToEmpty((String) object).replaceAll("[^0-9]", "")) ;
	}


	/**
	 * @param object
	 * @return
	 */
	@Log
	public static String normalizeToString(final Object object) {
		return (StringUtils.trimToEmpty((String) object)) ;
	}


	/**
	 * @param fieldID
	 * @param msg
	 * @param eol
	 * @param screen
	 * @param screenDef
	 * @return
	 */
	@Log
	public static boolean postScreenError(final String fieldID, final String msg, final String eol, final Map<String, String> screen,
			final Object[][] screenDef) {
		return (postScreenError(fetchFieldSpecs(fieldID, screenDef), msg, eol, screen)) ;
	}


	/**
	 * @param specs
	 * @param msg
	 * @param eol
	 * @param screen
	 * @return
	 */
	@Log
	public static boolean postScreenError(final Object[] specs, final String msg, final String eol, final Map<String, String> screen) {

		final String groupID = ((String) specs[ScreenDefEntry_GroupMsg]) ;
		String msgs = normalizeToString(screen.get(groupID)) ;

		if ( !msgs.isEmpty() )
			msgs += eol ;

		msgs += templator.template(msg).inject(Templator.InjectFromArray, specs[ScreenDefEntry_Moniker]) ;

		screen.put(groupID, msgs) ;

		return (true) ;
	}
}
