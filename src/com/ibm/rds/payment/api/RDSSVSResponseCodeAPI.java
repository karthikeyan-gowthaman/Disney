package com.ibm.rds.payment.api;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ibm.rds.util.XMLUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;

public class RDSSVSResponseCodeAPI {
	private static final YFCLogCategory log = YFCLogCategory.instance(RDSSVSResponseCodeAPI.class);

	public static Map<String, String> svsResponseMap = new HashMap<String, String>();
	static {
		svsResponseMap.put("01", "Approval");
		svsResponseMap.put("02", "Inactive Card");
		svsResponseMap.put("03", "Invalid Card Number");
		svsResponseMap.put("04", "Invalid Transaction Code");
		svsResponseMap.put("05", "Insufficient Funds");
		svsResponseMap.put("06", "No Previous Authorizations");
		svsResponseMap.put("07", "Invalid Message");
		svsResponseMap.put("08", "No Card Found");
		svsResponseMap.put("09", "Insufficient Funds due to Outstanding Pre-Authorization");
		svsResponseMap.put("10", "Denial  No Previous Authorization");
		svsResponseMap.put("11", "No Authorization Number  Special Edit");
		svsResponseMap.put("12", "Invalid Authorization Number  Special Edit");
		svsResponseMap.put("13", "Maximum Single Recharge Exceeded");
		svsResponseMap.put("14", "Maximum Working Balance Exceeded");
		svsResponseMap.put("15", "Shut Down");
		svsResponseMap.put("16", "Invalid Card Status");
		svsResponseMap.put("17", "Unknown Dealer/Store Code");
		svsResponseMap.put("18", "Maximum Number of Recharges Exceeded");
		svsResponseMap.put("19", "Invalid Card Verification Value");
		svsResponseMap.put("20", "Invalid PIN Number");
		svsResponseMap.put("21", "Card Already Issued");
		svsResponseMap.put("22", "Card Not Issued");
		svsResponseMap.put("23", "Card Already Used");
		svsResponseMap.put("24", "Manual Transaction Not Allowed");
		svsResponseMap.put("25", "Mag Stripe Read Not Valid");
		svsResponseMap.put("26", "Transaction Type Unknown");
		svsResponseMap.put("27", "Invalid Tender Type");
		svsResponseMap.put("28", "Invalid Customer Type");
		svsResponseMap.put("29", "PIN Locked");
		svsResponseMap.put("30", "Maximum Number of Redemptions Exceeded");
		svsResponseMap.put("31", "Invalid Currency Code");
		svsResponseMap.put("32", "Invalid Server ID");
		svsResponseMap.put("33", "Frozen");
		svsResponseMap.put("34", "Invalid Amount");
		svsResponseMap.put("35", "Not Used");
		svsResponseMap.put("36", "Invalid source");
		svsResponseMap.put("37", "Invalid/merchant/ store");
		svsResponseMap.put("38", "Max transactions exceeded");
		svsResponseMap.put("42", "Amount minimum not met");

	}
	
	private static String getText(Element ele, String name) {
		if (ele != null && name != null && !(name.trim().equals(""))){
			Element childele = SCXmlUtil.getChildElement(ele, name);
			log.debug("childele: " + SCXmlUtil.getString(childele));
			if (childele != null){
				log.debug("Element :: element name " + name + "childElement" + SCXmlUtil.getString(childele));
				return childele.getTextContent();
			}
		}
		log.debug("Element :: element name " + name + " is EMPTY in" + SCXmlUtil.getString(ele));
		return "";
	}
	
	private static String getResponseMessage(Document responseDoc){
		return getResponseMessage(responseDoc.getDocumentElement());
	}
	
	private static String getResponseMessage(Element cybResponseElement){
		Element soapele = SCXmlUtil.getChildElement(cybResponseElement,"soapenv:Body");
		Element replyMsg = SCXmlUtil.getChildElement(soapele,"preAuthCompleteReturn");
		
		if (("").equals(getText(soapele,"preAuthCompleteReturn"))){
			return "Reply from SVS missing";
		}
		String reasonCode = getText(replyMsg, "c:reasonCode");
		if (! ("").equals(reasonCode) && reasonCode.equals("101")){
			String missingField=getText(replyMsg, "c:missingField");
			if (!YFCCommon.isVoid(missingField))
				return "SVS Error Message :" + svsResponseMap.get(reasonCode) + "Missing Field(s) " + missingField;
		}
		if (! ("").equals(reasonCode) && reasonCode.equals("102")){
			String invalidField=getText(replyMsg, "c:invalidField");
			if (!YFCCommon.isVoid(invalidField))
				return "SVS Error Message :" + svsResponseMap.get(reasonCode) + "Invalid Field(s) " + invalidField;
		}
		if (! ("").equals(reasonCode)){
			String res = "";
			if (!reasonCode.equals("100"))
					res = res + "SVS Error :";
			if (svsResponseMap.get(reasonCode) == null)
					res = res + "SVS Error Code :" + reasonCode;
			else 
				res = res + svsResponseMap.get(reasonCode);
			return res;
		}
		return "Missing SVS reason code";
	}
	
	/*public static String getResponseMessageFromCommonCode(YFSEnvironment env, String reasonCode)
	throws YIFClientCreationException, RemoteException
	{
		Document getCommonCodeListDoc = getFromCommonCode(env, reasonCode);
		ArrayList codes= SCXmlUtil.getChildren(getCommonCodeListDoc.getDocumentElement(), "CommonCode");
		if (codes == null || codes.size() == 0)
			return "SVS Error Code :" + reasonCode;
		String codeDesc = ((Element)codes.get(0)).getAttribute("CodeLongDescription");
		if (YFCCommon.isVoid(codeDesc))
			return "SVS Error Code :" + reasonCode;
		return codeDesc;
	}
	private static Document getFromCommonCode(final YFSEnvironment env, final String reasonCode) 
	throws YIFClientCreationException, RemoteException {
			Document getCommonCodeListDoc = CommonApiUtil.getCommonCodeList(env, "CYB_ERROR_CODE", "EXTN-CYB-" + reasonCode.trim());
			return getCommonCodeListDoc;
		}*/
	
	public static String getReasonCode(Document responseDoc){
		return getReasonCode(responseDoc.getDocumentElement());
	}
	
	public static String getReasonCode(Element cybResponseElement){
		Element eleReturnCode = XMLUtil.getChildElement(cybResponseElement, "returnCode");
		log.debug("eleReturnCode: " + SCXmlUtil.getString(eleReturnCode));
		//Element eleReturnCodeValue = XMLUtil.getChildElement(eleReturnCode, "returnCode");
		String strReturnCode = getText(eleReturnCode,"returnCode");
		return strReturnCode;
	}
	
	public static String getDecision(Document responseDoc){
		return getDecision(responseDoc.getDocumentElement());
	}
	
	public static String getDecision(Element cybResponseElement){
		Element eleReturnCode = XMLUtil.getChildElement(cybResponseElement, "returnCode");
		log.debug("eleReturnCode: " + SCXmlUtil.getString(eleReturnCode));
		//Element eleReturnCodeValue = XMLUtil.getChildElement(eleReturnCode, "returnCode");
		String strReturnDesc = getText(eleReturnCode,"returnDescription");
		return strReturnDesc;
	}
	
	public static String getRequestId(Document responseDoc){
		return getRequestId(responseDoc.getDocumentElement());
	}
	
	public static String getRequestId(Element cybResponseElement){
		/*return getResponseValue(cybResponseElement, "transactionID");*/
		String strTransactionID = getText(cybResponseElement,"transactionID");
		return strTransactionID;
	}
	
	public static String getResponseValue(Document responseDoc, String name){
		return getResponseValue(responseDoc.getDocumentElement(), name);
	}
	
	public static String getReconciliationID(Document responseDoc, String parentEleName){
		return getReconciliationID(responseDoc.getDocumentElement(), parentEleName);
	}
	
	public static String getResponseValue(Element cybResponseElement, String name){
		Element soapele = SCXmlUtil.getChildElement(cybResponseElement,"soapenv:Body");
		Element replyMsg = SCXmlUtil.getChildElement(soapele,"preAuthCompleteReturn");
		
		if (("").equals(replyMsg)){
			return "";
		}
		return getText(replyMsg, name);
	
	}
	
	public static String getAuthReversalResponseValue(Document responseDoc, String name){
		return getAuthReversalResponseValue(responseDoc.getDocumentElement(), name);
	}
	
	public static String getAuthReversalResponseValue(Element cybResponseElement, String name){
		Element soapele = SCXmlUtil.getChildElement(cybResponseElement,"soapenv:Body");
		Element replyMsg = SCXmlUtil.getChildElement(soapele,"preAuthCompleteReturn");
		Element authMsg = SCXmlUtil.getChildElement(replyMsg,"c:ccAuthReversalReply");
		
		if (("").equals(replyMsg) || ("").equals(authMsg)){
			return "";
		}
		return getText(authMsg, name);
	
	}
	
	public static String getAuthResponseValue(Document responseDoc, String name){
		return getAuthResponseValue(responseDoc.getDocumentElement(), name);
	}
	
	public static String getAuthResponseValue(Element cybResponseElement, String name){
		Element soapele = SCXmlUtil.getChildElement(cybResponseElement,"soapenv:Body");
		Element replyMsg = SCXmlUtil.getChildElement(soapele,"preAuthCompleteReturn");
		Element authMsg = SCXmlUtil.getChildElement(replyMsg,"c:ccAuthReply");
		
		if (("").equals(replyMsg) || ("").equals(authMsg)){
			return "";
		}
		return getText(authMsg, name);
	
	}
	
	public static String getReconciliationID(Element cybResponseElement, String parentEleName){
		Element soapele = SCXmlUtil.getChildElement(cybResponseElement,"soapenv:Body");
		if (YFCCommon.isVoid(soapele)){
			return "";
		}
		Element replyMsg = SCXmlUtil.getChildElement(soapele,"preAuthCompleteReturn");
		if (YFCCommon.isVoid(replyMsg)){
			return "";
		}
		Element parentEle = SCXmlUtil.getChildElement(replyMsg,parentEleName);
		if (YFCCommon.isVoid(parentEle)){
			return "";
		}
		return getText(parentEle, "c:reconciliationID");
	}

}
