

package xcom.web4j.webapp ;


public class UIWidgetAttributes {

	/**
	 *
	 */
	String domID ;

	public String getDomID() {
		return domID ;
	}

	public UIWidgetAttributes setDomID(final String domID) {
		this.domID = domID ;
		return this ;
	}


	/**
	 *
	 */
	String domClass ;

	public String getDomClass() {
		return (domDisabled ? domClass + " Disabled" : domClass) ;
	}

	public UIWidgetAttributes setDomClass(final String domClass) {
		this.domClass = domClass ;
		return this ;
	}


	/**
	 *
	 */
	String domValue ;

	public String getDomValue() {
		return domValue ;
	}

	public UIWidgetAttributes setDomValue(final String domValue) {
		this.domValue = domValue ;
		return this ;
	}


	/**
	 *
	 */
	boolean domDisabled ;

	public boolean isDomDisabled() {
		return domDisabled ;
	}

	public UIWidgetAttributes setDomDisabled(final boolean domDisabled) {
		this.domDisabled = domDisabled ;
		return this ;
	}


	/**
	 *
	 */
	String springPath ;

	public String getSpringPath() {
		return springPath ;
	}

	public UIWidgetAttributes setSpringPath(final String springPath) {
		this.springPath = springPath ;
		return this ;
	}


	/**
	 *
	 */
	boolean disableOnFormChange ;

	public boolean isDisableOnFormChange() {
		return disableOnFormChange ;
	}

	public UIWidgetAttributes setDisableOnFormChange(final boolean disableOnFormChange) {
		this.disableOnFormChange = disableOnFormChange ;
		return this ;
	}


	/**
	 *
	 */
	boolean enableOnFormChange ;

	public boolean isEnableOnFormChange() {
		return enableOnFormChange ;
	}

	public UIWidgetAttributes setEnableOnFormChange(final boolean enableOnFormChange) {
		this.enableOnFormChange = enableOnFormChange ;
		return this ;
	}
}
