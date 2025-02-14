

package xcom.web4j.webapp ;


import java.util.HashMap ;
import java.util.Map ;

import org.fest.reflect.core.Reflection ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

import xcom.utils4j.format.Templator ;
import xcom.utils4j.format.Templator.Templator$Delimited ;
import xcom.utils4j.logging.annotations.Log ;
import xcom.utils4j.workflow.Workflow ;
import xcom.utils4j.workflow.iPath ;
import xcom.utils4j.workflow.iTransitionListener ;


public class WorkflowListnerAdapter<S, P> implements iTransitionListener<S, P> {

	private static final Logger Logger = LoggerFactory.getLogger(WorkflowListnerAdapter.class) ;


	public static final String DefaultMethodSignaturePrefix = "executeListenerForPath" ;

	public static final String DefaultMethodSignatureTemplate = "${prefix}_${cameledMethodName}" ;


	/**
	 *
	 */
	Object listener ;


	/**
	 *
	 */
	String methodSignatureTemplate ;


	Templator$Delimited templator = Templator.openTagDelimiter("<?").closeTagDelimiter("?>") ;

	Map<String, Object> templateValues ;


	/**
	 * @param listener
	 */
	@Log
	public WorkflowListnerAdapter(final Object listener) {
		initialize(listener, DefaultMethodSignatureTemplate) ;
	}


	/**
	 * @param listener
	 * @param methodSignatureTemplate
	 */
	@Log
	public WorkflowListnerAdapter(final Object listener, final String methodSignatureTemplate) {
		initialize(listener, methodSignatureTemplate) ;
	}


	/**
	 * @param listener
	 * @param methodSignatureTemplate
	 */
	@Log
	void initialize(final Object listener, final String methodSignatureTemplate) {

		this.listener = listener ;
		this.methodSignatureTemplate = methodSignatureTemplate ;

		templateValues = new HashMap<String, Object>() ;
		templateValues.put("prefix", DefaultMethodSignaturePrefix) ;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see xcom.utils4j.workflow.iTransitionListener#executeTransition(xcom.utils4j.workflow.Workflow)
	 */
	@Log
	@Override
	public void executeTransition(final Workflow<S, P> workflow) {

		String methodName = ((iPath) workflow.getPath()).getName().replaceAll("[^a-zA-Z]", "") ;
		templateValues.put("methodName", methodName) ;
		templateValues.put("cameledMethodName", methodName.substring(0, 1).toUpperCase() + methodName.substring(1)) ;
		methodName = templator.template(methodSignatureTemplate).inject(templateValues) ;

		Logger.trace("Invoking listener method {} ...", methodName) ;
		Reflection.method(methodName).in(listener).invoke() ;
	}
}
