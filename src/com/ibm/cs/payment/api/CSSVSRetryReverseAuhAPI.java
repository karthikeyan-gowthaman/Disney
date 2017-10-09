package com.ibm.cs.payment.api;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.ibm.cs.utils.CSCommonUtil;
import com.ibm.cs.utils.CSConstants;
import com.ibm.cs.utils.CSXMLConstants;
import com.ibm.cs.utils.XMLUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * 
 * @author IBM
 *
 */
public class CSSVSRetryReverseAuhAPI implements CSXMLConstants, CSConstants{
	
	private static YFCLogCategory logger = YFCLogCategory.instance(CSSVSRetryReverseAuhAPI.class);
	
	/**
	 * 
	 * @param env
	 * @param inDoc
	 */
	public void retryReverseAuth(YFSEnvironment env, Document inDoc){
		
		try {
			Document svsAuthOutDoc= CSCommonUtil.invokeService(env, "CSGiftCardReverseAuthSyncService", inDoc);
			Element svsAuthDocEle = svsAuthOutDoc.getDocumentElement();
			NodeList nodeList = svsAuthDocEle.getElementsByTagName("soapenv:Body");
			if (nodeList != null && nodeList.getLength() > 0) {
			    Element eleSoapenvBody = (Element) nodeList.item(0);
			    logger.debug("notesTextList:"+SCXmlUtil.getString(eleSoapenvBody));
			    Element elePreAuthComplGiftCardResponse = SCXmlUtil.getChildElement(eleSoapenvBody, "p751:preAuthCompleteResponse");
				Element elePreAuthComplGiftCardReturn = SCXmlUtil.getChildElement(elePreAuthComplGiftCardResponse, "preAuthCompleteReturn");
				Element eleReturnCodePrnt = SCXmlUtil.getChildElement(elePreAuthComplGiftCardReturn, "returnCode");
				Element eleReturnCode = SCXmlUtil.getChildElement(eleReturnCodePrnt, "returnCode");
				if(XMLUtil.isVoid(eleReturnCode))
					throw new YFSException("Response does not have returnCode element");
				String returnCode = eleReturnCode.getTextContent();
				logger.debug("returnCode:"+returnCode);
				if(!"01".equals(returnCode)){
					throw new YFSException("Error occured in async request processor");
				}
			}
		} catch (Exception e) {
			logger.debug(e);
			throw new YFSException("Error occured in async request processor");
		}
		
	}

}
