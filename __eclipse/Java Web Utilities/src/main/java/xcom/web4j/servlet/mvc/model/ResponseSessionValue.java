

package xcom.web4j.servlet.mvc.model ;


import java.io.IOException ;
import java.net.MalformedURLException ;
import java.util.HashMap ;
import java.util.List ;
import java.util.Map ;

import xcom.utils4j.logging.annotations.Log ;


public class ResponseSessionValue extends LogonSessionValue {

	List<String> rights ;

	public List<String> getRights() {
		return (rights) ;
	}


	Map<String, String> screenData ;

	public Map<String, String> getScreenData() {
		return (screenData) ;
	}

	public ResponseSessionValue setScreenData(final Map<String, String> screenData) {
		this.screenData = screenData ;
		return (this) ;
	}


	Object[][] screenDef ;

	public Object[][] getScreenDef() {
		return (screenDef) ;
	}

	public ResponseSessionValue setScreenDef(final Object[][] screenDef) {
		this.screenDef = screenDef ;
		return (this) ;
	}


	String screenRequest ;

	public String getScreenRequest() {
		return (screenRequest) ;
	}

	public ResponseSessionValue setScreenRequest(final String screenRequest) {
		this.screenRequest = screenRequest ;
		return (this) ;
	}


	String screenRequestType ;

	public String getScreenRequestType() {
		return (screenRequestType) ;
	}

	public ResponseSessionValue setScreenRequestType(final String screenRequestType) {
		this.screenRequestType = screenRequestType ;
		return (this) ;
	}


	Map<String, Object> sessionValues ;

	public Map<String, Object> getSessionValues() {
		return (sessionValues) ;
	}

	public ResponseSessionValue setSessionValues(final Map<String, Object> sessionValues) {
		this.sessionValues = sessionValues ;
		return (this) ;
	}


	String userRole ;

	public String getUserRole() {
		return (userRole) ;
	}


	public ResponseSessionValue setUserRole(final String userRole) {
		this.userRole = userRole ;
		return (this) ;
	}


	@Log
	public ResponseSessionValue() {}


	@Override
	@Log
	public void initialize() throws MalformedURLException, IOException {

		super.initialize() ;

		sessionValues = new HashMap<String, Object>() ;
	}


	@Log
	String getScreenDataItem(final String key) {
		final String item = screenData.get(key) ;

		return (item != null ? item : "") ;
	}
}
