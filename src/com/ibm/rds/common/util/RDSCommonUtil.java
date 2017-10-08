package com.ibm.rds.common.util;

//Miscellaneous Imports
import java.rmi.RemoteException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogLevel;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * Encapsulates set of utility methods
 */
public final class RDSCommonUtil {

	/**
	 * LoggerUtil Instance.
	 */
	/*private static CnALoggerUtil logger = CnALoggerUtil.getLogger(CnACommonUtil.class
			.getName());*/
	private static YFCLogCategory logger = YFCLogCategory.instance(RDSCommonUtil.class);

	// Utility Class - Mask Constructor
	private RDSCommonUtil() {
	}

	/**
	 * Instance of YIFApi used to invoke Sterling Commerce APIs or services.
	 */
	private static YIFApi api;

	static {
		try {
			RDSCommonUtil.api = YIFClientFactory.getInstance().getApi();
		} catch (Exception excp) {
			logger.log(YFCLogLevel.ERROR, excp.getMessage());
		}
	}

	
	/**
	 * Invokes a Sterling Commerce API.
	 * 
	 * @param env
	 *            Sterling Commerce Environment Context.
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
		Document returnDoc = RDSCommonUtil.api.invoke(env, apiName, inDoc);
		env.clearApiTemplate(apiName);
		return returnDoc;
	}

	/**
	 * Invokes a Sterling Commerce API.
	 * 
	 * @param env
	 *            Sterling Commerce Environment Context.
	 * @param template
	 *            Output template document for the API
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
		Document returnDoc = RDSCommonUtil.api.invoke(env, apiName, inDoc);
		env.clearApiTemplate(apiName);
		return returnDoc;
	}

	/**
	 * Invokes a Sterling Commerce API.
	 * 
	 * @param env
	 *            Sterling Commerce Environment Context.
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
		return RDSCommonUtil.api.invoke(env, apiName, inDoc);
	}

	/**
	 * Invokes a Sterling Commerce API.
	 * 
	 * @param env
	 *            Sterling Commerce Environment Context.
	 * @param apiName
	 *            Name of API to invoke.
	 * @param inDocStr
	 *            Input to be passed to the API. Should be a valid XML string.
	 * @throws java.lang.Exception
	 *             Exception thrown by the API.
	 * @return Output of the API.
	 */
	public static Document invokeAPI(YFSEnvironment env, String apiName,
			String inDocStr) throws Exception {
		return RDSCommonUtil.api.invoke(env, apiName, YFCDocument.parse(inDocStr)
				.getDocument());
	}

	/**
	 * Invokes a Sterling Commerce Service.
	 * 
	 * @param env
	 *            Sterling Commerce Environment Context.
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
		return RDSCommonUtil.api.executeFlow(env, serviceName, inDoc);
	}

	/**
	 * Invokes a Sterling Commerce Service.
	 * 
	 * @param env
	 *            Sterling Commerce Environment Context.
	 * @param serviceName
	 *            Name of Service to invoke.
	 * @param inDocStr
	 *            Input to be passed to the Service. Should be a valid XML String.
	 * @throws java.lang.Exception
	 *             Exception thrown by the Service.
	 * @return Output of the Service.
	 */
	public static Document invokeService(YFSEnvironment env,
			String serviceName, String inDocStr) throws Exception {
		return RDSCommonUtil.api.executeFlow(env, serviceName, YFCDocument.parse(
				inDocStr).getDocument());
	}
	
	/**
	 * @param userId 
	 * @param progId
	 * @return YFSEnvironment object
	 * @throws YFSException
	 * @throws RemoteException
	 */
	public static YFSEnvironment createCustomEnvironment(String userId, String progId) throws YFSException, RemoteException{
		YFCDocument document = YFCDocument.createDocument("YFSEnvironment");
		YFCElement eleEnv = document.getDocumentElement();
		eleEnv.setAttribute("userId", userId);
		eleEnv.setAttribute("progId", progId);
		return RDSCommonUtil.api.createEnvironment(document.getDocument());
	}
	
	public static Document getCommonCodeList(YFSEnvironment env, String strCommonCodeType, String strOrganizationCode) throws Exception {
      Document docInput = RDSXmlUtil.createDocument(RDSXMLLiterals.E_COMMON_CODE);
      Element eleInput = docInput.getDocumentElement();
      eleInput.setAttribute(RDSXMLLiterals.A_ORGANIZATION_CODE, strOrganizationCode);
      eleInput.setAttribute(RDSXMLLiterals.A_CODE_TYPE, strCommonCodeType);
      Document docCommonCodeList = invokeAPI(env, RDSXMLLiterals.API_GET_COMMON_CODE_LIST, docInput);
      return docCommonCodeList;
   }

}