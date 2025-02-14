

package xcom.web4j.servlet.mvc.control ;


import java.io.IOException ;

import javax.servlet.ServletException ;

import xcom.web4j.servlet.mvc.model.SessionValue ;


public abstract class LogonServlet extends Servlet {

	private static final long serialVersionUID = -499781569804878484L ;


	@Override
	abstract public void doAction(SessionValue sessionValue) throws ServletException, IOException ;


	@Override
	abstract public SessionValue newSessionValue() ;
}
