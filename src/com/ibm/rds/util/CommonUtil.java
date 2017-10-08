package com.ibm.rds.util;

/**
 * This class contains the utility methods required for the
 * 
 * @(#) CommonUtil.java Package Declaration: File Name:
 *      CommonUtil.java Package Name: Project name: Type Declaration: Class Name:
 *      CommonUtil Type Comment: (C) Copyright 2006-2007 by owner. All Rights Reserved. This
 *      software is the confidential and proprietary information of the owner.
 *      ("Confidential Information"). Redistribution of the source code or binary form is not
 *      permitted without prior authorization from the owner.
 */
//Java Imports
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.shared.omp.OMPTransactionCache;
import com.yantra.shared.ycp.YFSContext;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfc.util.YFCDate;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * Encapsulates set of utility methods
 */
public final class CommonUtil implements YIFCustomApi,Constants,XMLLiterals {

	/**
	 * LoggerUtil Instance.
	 */
	public static LoggerUtil logger = LoggerUtil.getLogger(CommonUtil.class.getName());

	// Utility Class - Mask Constructor
	// private CommonUtil() {
	// }

	/**
	 * Instance of YIFApi used to invoke Sterling Commerce APIs or services.
	 */
	public static YIFApi api;

	static {
		try {
			CommonUtil.api = YIFClientFactory.getInstance().getApi();
		} catch (Exception e) {
			CommonUtil.logger.error(IOM_UTIL_0001, e);
		}
	}

	/**
	 * Stores the object in the environment under a certain key.
	 * 
	 * @param env
	 *            Sterling Commerce Environment Context.
	 * @param key
	 *            Key to identify object in environment.
	 * @param value
	 *            Object to be stored in the environment under the given key.
	 * @return Previous object stored in the environment with the same key (if
	 *         present).
	 */
	public static Object setContextObject(YFSEnvironment env, String key, Object value) {
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
	 *            Sterling Commerce Environment Context.
	 * @param key
	 *            Key to identify object in environment.
	 * @return Object retrieved from the environment under the given key.
	 */
	public static Object getContextObject(YFSEnvironment env, String key) {
		return env.getTxnObject(key);
	}

	/**
	 * Retrieves the property stored in the environment under a certain key.
	 * 
	 * @param env
	 *            Sterling Commerce Environment Context.
	 * @param key
	 *            Key to identify object in environment.
	 * @return Poperty retrieved from the environment under the given key.
	 */
	public static String getContextProperty(YFSEnvironment env, String key) {
		String value = null;
		Object obj = env.getTxnObject(key);
		if (obj != null) {
			value = obj.toString();
		}
		return value;
	}

	/**
	 * Removes an object from the environment.
	 * 
	 * @param env
	 *            Sterling Commerce Environment Context.
	 * @param key
	 *            Key to identify object in environment.
	 * @return The object stored in the environment under the specified key (if
	 *         any).
	 */
	public static Object removeContextObject(YFSEnvironment env, String key) {
		Object oldValue = null;
		Map map = env.getTxnObjectMap();
		if (map != null) {
			oldValue = map.remove(key);
		}
		return oldValue;
	}

	/**
	 * Clears the environment of any user objects stored.
	 * 
	 * @param env
	 *            Sterling Commerce Environment Context.
	 */
	public static void clearContextObjects(YFSEnvironment env) {
		Map map = env.getTxnObjectMap();
		if (map != null) {
			map.clear();
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
	public static Document invokeAPI(YFSEnvironment env, String templateName, String apiName, Document inDoc) throws Exception {
		env.setApiTemplate(apiName, templateName);
		Document returnDoc = CommonUtil.api.invoke(env, apiName, inDoc);
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
	public static Document invokeAPI(YFSEnvironment env, Document template, String apiName, Document inDoc) throws Exception {
		env.setApiTemplate(apiName, template);
		Document returnDoc = CommonUtil.api.invoke(env, apiName, inDoc);
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
	public static Document invokeAPI(YFSEnvironment env, String apiName, Document inDoc) throws Exception {
		return CommonUtil.api.invoke(env, apiName, inDoc);
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
	public static Document invokeAPI(YFSEnvironment env, String apiName, String inDocStr) throws Exception {
		return CommonUtil.api.invoke(env, apiName, YFCDocument.parse(inDocStr).getDocument());
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
	public static Document invokeService(YFSEnvironment env, String serviceName, Document inDoc) throws Exception {
		return CommonUtil.api.executeFlow(env, serviceName, inDoc);
	}

	/**
	 * Invokes a Sterling Commerce Service.
	 * 
	 * @param env
	 *            Sterling Commerce Environment Context.
	 * @param serviceName
	 *            Name of Service to invoke.
	 * @param inDocStr
	 *            Input to be passed to the Service. Should be a valid XML
	 *            String.
	 * @throws java.lang.Exception
	 *             Exception thrown by the Service.
	 * @return Output of the Service.
	 */
	public static Document invokeService(YFSEnvironment env, String serviceName, String inDocStr) throws Exception {
		return CommonUtil.api.executeFlow(env, serviceName, YFCDocument.parse(inDocStr).getDocument());
	}

	/**
	 * Returns the clone of an XML Document.
	 * 
	 * @param doc
	 *            Input document to be cloned.
	 * @throws java.lang.Exception
	 *             If unable to clone document.
	 * @return Clone of the document.
	 */
	public static Document cloneDocument(Document doc) throws Exception {
		return YFCDocument.parse(XMLUtil.getXMLString(doc)).getDocument();
	}

	/**
	 * Returns the clone of an XML Document.
	 * 
	 * @param doc
	 *            Input document to be cloned.
	 * @throws java.lang.Exception
	 *             If unable to clone document.
	 * @return Clone of the document.
	 */
	public static YFCDocument cloneDocument(YFCDocument doc) throws Exception {
		return YFCDocument.parse(doc.getString());
	}

	/**
	 * Method to get resource as InputStream
	 * 
	 * @param resource
	 *            Resource path relative to classpath
	 * @return Resource as InputStream
	 */
	public static InputStream getResourceStream(String resource) {
		return CommonUtil.class.getResourceAsStream(resource);
	}

	/**
	 * Method to create YFS environment with a userid and progrid.
	 * 
	 * @param userID
	 *            UserId used in the context of this environment
	 * @param progID
	 *            ProgId used in the context of this environment
	 * @return YFSEnvironment object containing state information about the
	 *         underlying implementation of YIFApi.
	 * @throws java.lang.Exception
	 *             Exception thrown if unable to create Environment object
	 */
	public static YFSEnvironment createEnvironment(String userID, String progID) throws Exception {
		Document doc = XMLUtil.createDocument("YFSEnvironment");
		Element elem = doc.getDocumentElement();
		elem.setAttribute(A_USER_ID, userID);
		elem.setAttribute(PROG_ID, progID);

		return CommonUtil.api.createEnvironment(doc);
	}

	/**
	 * Method to log the properties
	 * 
	 * @param properties
	 *            Properties to be logged
	 * @param log
	 *            LoggerUtil to be used for logging
	 */
	public static void logProperties(Map properties, LoggerUtil log) {
		Iterator keyIt = properties.keySet().iterator();
		log.verbose("******[Properties List Start]************");
		while (keyIt.hasNext()) {
			String key = (String) keyIt.next();
			String val = (String) properties.get(key);
			log.verbose("<" + key + ":" + val + ">");

		}
		log.verbose("******[Properties List End]*************");
	}

	/**
	 * Removes the passed Node name from the input document. If no name is
	 * passed, it removes all the nodes.
	 * 
	 * @param node
	 *            Node from which we have to remove the child nodes
	 * @param nodeType
	 *            nodeType e.g. Element Node, Comment Node or Text Node
	 * @param name
	 *            Name of the Child node to be removed
	 */
	public static void removeAll(Node node, short nodeType, String name) {
		if (node.getNodeType() == nodeType && (name == null || node.getNodeName().equals(name))) {
			node.getParentNode().removeChild(node);
		} else {
			// Visit the children
			NodeList list = node.getChildNodes();
			for (int i = 0; i < list.getLength(); i++) {
				CommonUtil.removeAll(list.item(i), nodeType, name);
			}
		}

	}

	/**
	 * @return True if 'isnamespaceaware' is activated
	 */
	public static boolean isDocumentNamespaceAware() {
		return "Y".equals(ResourceUtil.get("yantra.document.isnamespaceaware"));
	}

	/**
	 * @param dt
	 *            Input Date object
	 * @return YFCDate in yyyyMMdd'T'HH:mm:ss format.
	 */
	public static String convertToYantraDate(Date dt) {
		YFCDate yDate = new YFCDate(dt);
		return yDate.getString();
	}

	/**
	 * @param nList
	 *            org.w3c.dom.NodeList
	 * @return ArrayList of org.w3c.dom.Node objects
	 */
	public static List getListFromNodeList(NodeList nList) {
		if (null == nList) {
			return null;
		}
		int nodeLength = nList.getLength();
		ArrayList<Node> list = new ArrayList<Node>(nodeLength);
		for (int i = 0; i < nodeLength; i++) {
			list.add(i, nList.item(i));
		}
		return list;
	}

	/**
	 * Description of getYFCDate returns the YFCDate for a date string of the
	 * format yyyy-MM-dd
	 * 
	 * @param date
	 *            String date in format yyyy-MM-dd
	 * @return YFCDate
	 */
	public static YFCDate getYFCDate(String date) {
		String strReqStartDate = date;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss");
		Date dtReqStartDate = null;
		try {
			dtReqStartDate = sdf.parse(strReqStartDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		String strYFCReqStartDate = sdf1.format(dtReqStartDate);
		Date yFCFormatReqStartDate = null;
		try {
			yFCFormatReqStartDate = sdf1.parse(strYFCReqStartDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		YFCDate yfcReqStartDate = new YFCDate(yFCFormatReqStartDate);
		return yfcReqStartDate;
	}

	public static Document getCommonCodeList(YFSEnvironment env, String strCommonCodeType, String strOrganizationCode) throws Exception {
		Document docInput = XMLUtil.createDocument(E_COMMON_CODE);
		Element eleInput = docInput.getDocumentElement();
		eleInput.setAttribute(A_ORGANIZATION_CODE, strOrganizationCode);
		eleInput.setAttribute(A_CODE_TYPE, strCommonCodeType);
		Document docCommonCodeList = invokeAPI(env, API_GET_COMMON_CODE_LIST, docInput);
		return docCommonCodeList;
	}

	public static Document getCommonCodeList(YFSEnvironment env, String strCommonCodeType, String strCommonCodeValue, String strOrganizationCode) throws Exception {
		Document docInput = XMLUtil.createDocument(E_COMMON_CODE);
		Element eleInput = docInput.getDocumentElement();
		eleInput.setAttribute(A_ORGANIZATION_CODE, strOrganizationCode);
		eleInput.setAttribute(A_CODE_TYPE, strCommonCodeType);
		eleInput.setAttribute(A_CODE_VALUE, strCommonCodeValue);
		Document docCommonCodeList = invokeAPI(env, API_GET_COMMON_CODE_LIST, docInput);
		return docCommonCodeList;
	}

	/**
	 * Read the OrderHeaderKey from the TransactionCache
	 * 
	 * @param env
	 * @return
	 */
	public static String readOHKFromCache(YFSEnvironment env) {

		String orderHeaderKey = null;

		YFSContext oEnv = (YFSContext) env;

		OMPTransactionCache oCache = (OMPTransactionCache) oEnv.getTransactionCache(OMP);

		if (oCache != null) {
			Map headerMap = (Map) oCache.getEntityMap(YFS_ORDER_HEADER);

			if (headerMap != null) {

				Set keySet = headerMap.keySet();

				for (Iterator itr = keySet.iterator(); itr.hasNext();) {
					orderHeaderKey = (String) itr.next();
				}
			}
		}
		return orderHeaderKey;
	}

	/**
	 * This method returns the opposite of the string value that is passed
	 * 
	 * @param string
	 * @return
	 */
	public static String opp(String string) {

		String returnStr = null;

		if (YFCCommon.equals(string, YES)) {
			returnStr = NO;
		} else if (YFCCommon.equals(string, NO)) {
			returnStr = YES;
		}

		return returnStr;
	}

	/**
	 * HashMap of CommoncodeList
	 * 
	 * @param docCommonCodeList
	 * @return
	 */

	public static HashMap<String, String> getHashMap(Document docCommonCodeList) {

		HashMap<String, String> hashMapCommonCode = new HashMap<String, String>();
		Element eleCommonCodeList = docCommonCodeList.getDocumentElement();
		NodeList nodeListCommonCode = eleCommonCodeList.getElementsByTagName(XMLLiterals.E_COMMON_CODE);
		for (int i = 0; i < nodeListCommonCode.getLength(); i++) {
			Element eleCommonCode = ((Element) nodeListCommonCode.item(i));
			hashMapCommonCode.put(eleCommonCode.getAttribute(XMLLiterals.A_CODE_VALUE), eleCommonCode.getAttribute(A_CODE_SHORT_DESCRIPTION));

		}

		return hashMapCommonCode;
	}

	/**
	 * 
	 * @param parentEle
	 * @param childName
	 * @param createIfNotExists
	 * @return
	 */
	public static Element getChildElement(Element parentEle, String childName) {

		Element child = null;
		if (parentEle != null && !YFCCommon.isVoid(childName)) {
			for (Node n = parentEle.getFirstChild(); n != null; n = n.getNextSibling()) {
				if (n.getNodeType() == Node.ELEMENT_NODE && n.getNodeName().equals(childName)) {
					return (Element) n;
				}
			}

		}
		return child;
	}

	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub

	}

}
