

package xcom.web4j.servlet.mvc.view ;


public class ScreenDefEntry {

	String domElementID ;

	boolean submitted ;

	String domType ;

	String groupMsg ;

	String moniker ;


	public String getDomElementID() {

		return domElementID ;
	}


	public void setDomElementID(final String domElementID) {

		this.domElementID = domElementID ;
	}


	public boolean isSubmitted() {

		return submitted ;
	}


	public void setSubmitted(final boolean submitted) {

		this.submitted = submitted ;
	}


	public String getDomType() {

		return domType ;
	}


	public void setDomType(final String domType) {

		this.domType = domType ;
	}


	public String getGroupMsg() {

		return groupMsg ;
	}


	public void setGroupMsg(final String groupMsg) {

		this.groupMsg = groupMsg ;
	}


	public String getMoniker() {

		return moniker ;
	}


	public void setMoniker(final String moniker) {

		this.moniker = moniker ;
	}
}
