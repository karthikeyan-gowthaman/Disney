package com.ibm.rds.api;

import java.util.Map;

import org.w3c.dom.Document;

import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * Base class which provides methods for API/Service invocation. All custom APIs
 * are required to extend this class.
 */
public class ApiInvoker {

	private static YIFClientFactory apiFactory = YIFClientFactory.getInstance();

	private static YIFApi getApi() throws YIFClientCreationException {
		return apiFactory.getApi();
	}
	
	private static YIFApi getLocalApi() throws YIFClientCreationException {
		return apiFactory.getLocalApi();
	}
	
	/**
	 * Invokes a Sterling API.
	 * 
	 * @param env
	 *            Sterling Environment Context.
	 * @param templateName
	 *            Name of API Output Template that needs to be set
	 * @param apiName
	 *            Name of API to invoke.
	 * @param inDoc
	 *            Input Document to be passed to the API.
	 * @throws java.lang.Exception
	 *             Exception thrown by the API.
	 * @return Output of the API.
	 */
	public static Document invokeAPI(YFSEnvironment env, String templateName,
			String apiName, Document inDoc) throws Exception {
		env.setApiTemplate(apiName, templateName);
		Document returnDoc = ApiInvoker.getApi().invoke(env, apiName, inDoc);
		env.clearApiTemplate(apiName);
		return returnDoc;
	}
	
	/**
	 * Invokes a Sterling Local API. Also the API template will be cleared.
	 * 
	 * @param env
	 *            Sterling Environment Context.
	 * @param templateName
	 *            Name of API Output Template that needs to be set
	 * @param apiName
	 *            Name of API to invoke.
	 * @param inDoc
	 *            Input Document to be passed to the API.	 *          
	 * @throws java.lang.Exception
	 *             Exception thrown by the API.
	 * @return Output of the Local API.
	 */
	public static Document invokeLocalAPI(YFSEnvironment env, String templateName,
			String apiName, Document inDoc) throws Exception {
		env.setApiTemplate(apiName, templateName);
		Document returnDoc = ApiInvoker.getLocalApi().invoke(env, apiName, inDoc);
		env.clearApiTemplate(apiName);
		return returnDoc;
	}

	/**
	 * Invokes a Sterling API.
	 * 
	 * @param env
	 *            Sterling Environment Context.
	 * @param template
	 *            Output Template that needs to be set
	 * @param apiName
	 *            Name of API to invoke.
	 * @param inDoc
	 *            Input Document to be passed to the API.
	 * @throws java.lang.Exception
	 *             Exception thrown by the API.
	 * @return Output of the API.
	 */

	public static Document invokeAPI(YFSEnvironment env, Document template,
			String apiName, Document inDoc) throws Exception {
		env.setApiTemplate(apiName, template);
		Document returnDoc = ApiInvoker.getApi().invoke(env, apiName, inDoc);
		env.clearApiTemplate(apiName);
		return returnDoc;
	}
	
	/**
	 * Invokes a Sterling Local API. Also the API template will be cleared.
	 * 
	 * @param env
	 *            Sterling Environment Context.
	 * @param template
	 *            Output Template that needs to be set
	 * @param apiName
	 *            Name of API to invoke.
	 * @param inDoc
	 *            Input Document to be passed to the API.	 *          
	 * @throws java.lang.Exception
	 *             Exception thrown by the API.
	 * @return Output of the Local API.
	 */
	public static Document invokeLocalAPI(YFSEnvironment env, Document template,
			String apiName, Document inDoc) throws Exception {
		env.setApiTemplate(apiName, template);
		Document returnDoc = ApiInvoker.getLocalApi().invoke(env, apiName, inDoc);
		env.clearApiTemplate(apiName);
		return returnDoc;
	}

	/**
	 * Invokes a Sterling API. By Default the API template will be cleared
	 * 
	 * @param env
	 *            Sterling Environment Context.
	 * @param apiName
	 *            Name of API to invoke.
	 * @param inDoc
	 *            Input Document to be passed to the API.
	 * @throws java.lang.Exception
	 *             Exception thrown by the API.
	 * @return Output of the API.
	 */
	public static Document invokeAPI(YFSEnvironment env, String apiName,
			Document inDoc) throws Exception {
		return ApiInvoker.invokeAPI(env, apiName, inDoc, true);
	}
	
	/**
	 * Invokes a Sterling Local API. Also the API template will be cleared.
	 * 
	 * @param env
	 *            Sterling Environment Context.
	 * @param template
	 *            Output Template that needs to be set
	 * @param apiName
	 *            Name of API to invoke.
	 * @param inDoc
	 *            Input Document to be passed to the API.	 *          
	 * @throws java.lang.Exception
	 *             Exception thrown by the API.
	 * @return Output of the Local API.
	 */
	public static Document invokeLocalAPI(YFSEnvironment env, String apiName, 
			Document inDoc) throws Exception {		
		Document returnDoc = ApiInvoker.getLocalApi().invoke(env, apiName, inDoc);		
		return returnDoc;
	}
	
	/**
	 * Invokes a Sterling API. Use this method if the API template need not be cleared. Pass 'clearTemplate' flag as 'false'.
	 * 
	 * @param env
	 *            Sterling Environment Context.
	 * @param apiName
	 *            Name of API to invoke.
	 * @param inDoc
	 *            Input Document to be passed to the API.
	 * @param clearTemplate
	 * 			  boolean to be passed to clear the template           
	 * @throws java.lang.Exception
	 *             Exception thrown by the API.
	 * @return Output of the API.
	 */
	public static Document invokeAPI(YFSEnvironment env, String apiName,
			Document inDoc, boolean clearTemplate) throws Exception {
		Document outDoc = null;
		outDoc = ApiInvoker.getApi().invoke(env, apiName, inDoc);
		
		if(clearTemplate) {			
			env.clearApiTemplate(apiName);			
		}
		
		return outDoc;
	}

	/**
	 * Invokes a Sterling API.
	 * 
	 * @param env
	 *            Sterling Environment Context.
	 * @param apiName
	 *            Name of API to invoke.
	 * @param str
	 *            Input to be passed to the API. Should be a valid XML string.
	 * @throws java.lang.Exception
	 *             Exception thrown by the API.
	 * @return Output of the API.
	 */
	public static Document invokeAPI(YFSEnvironment env, String apiName,
			String str) throws Exception {
		return ApiInvoker.invokeAPI(env, apiName, YFCDocument.parse(str)
				.getDocument());
	}

	/**
	 * Invokes a Sterling Service.
	 * 
	 * @param env
	 *            Sterling Environment Context.
	 * @param serviceName
	 *            Name of Service to invoke.
	 * @param inDoc
	 *            Input Document to be passed to the Service.
	 * @throws java.lang.Exception
	 *             Exception thrown by the Service.
	 * @return Output of the Service.
	 */
	public static Document invokeService(YFSEnvironment env,
			String serviceName, Document inDoc) throws Exception {
		return ApiInvoker.getApi().executeFlow(env, serviceName, inDoc);
	}

	/**
	 * Invokes a Sterling Service.
	 * 
	 * @param env
	 *            Sterling Environment Context.
	 * @param serviceName
	 *            Name of Service to invoke.
	 * @param str
	 *            Input to be passed to the Service. Should be a valid XML
	 *            String.
	 * @throws java.lang.Exception
	 *             Exception thrown by the Service.
	 * @return Output of the Service.
	 */
	public static Document invokeService(YFSEnvironment env,
			String serviceName, String str) throws Exception {
		return ApiInvoker.getApi().executeFlow(env, serviceName,
				YFCDocument.parse(str).getDocument());
	}

	/**
	 * Stores the object in the environment under a certain key.
	 * 
	 * @param env
	 *            Yantra Environment Context.
	 * @param key
	 *            Key to identify object in environment.
	 * @param value
	 *            Object to be stored in the environment under the given key.
	 * @return Previous object stored in the environment with the same key (if
	 *         present).
	 */
	public static Object setContextObject(YFSEnvironment env, String key,
			Object value) {
		Object oldValue = null;
		Map map = env.getTxnObjectMap();
		if (map != null) {
			oldValue = map.get(key);
		}
		env.setTxnObject(key, value);
		return oldValue;
	}

	/**
	 * Retrieves the object stored in the environment under a certain key.
	 * 
	 * @param env
	 *            Yantra Environment Context.
	 * @param key
	 *            Key to identify object in environment.
	 * @return Object retrieved from the environment under the given key.
	 */
	public static Object getContextObject(YFSEnvironment env, String key) {
		return env.getTxnObject(key);
	}
}
