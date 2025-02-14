

package xcom.web4j.servlet.mvc.model ;


import java.io.IOException ;
import java.net.MalformedURLException ;

import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

import xcom.utils4j.logging.Loggers ;
import xcom.utils4j.logging.annotations.Log ;


public abstract class LogonSessionValue extends SessionValue {

	private static final Logger Logger = LoggerFactory.getLogger(LogonSessionValue.class) ;

	private static final Logger Console = LoggerFactory.getLogger(Loggers.ConsoleLoggerName) ;


	String userid ;

	public String getUserid() {
		return (userid) ;
	}

	public LogonSessionValue setUserid(final String userid) {
		this.userid = userid ;
		return (this) ;
	}


	boolean simulatedLogon ;


	@Log
	public LogonSessionValue() {}


	@Override
	@Log
	public void initialize() throws MalformedURLException, IOException {

		super.initialize() ;

		simulatedLogon = Boolean.valueOf(properties.getProperty("appl.simulated.logon", "true")).booleanValue() ;
		Console.info("Simulated Logon {}", simulatedLogon) ;

		if ( simulatedLogon )
			userid = "js1234" ;

		Logger.debug("UserID |{}|", userid) ;
	}
}
