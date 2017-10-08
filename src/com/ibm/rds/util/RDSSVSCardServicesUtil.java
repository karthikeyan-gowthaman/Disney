package com.ibm.rds.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.ibm.rds.payment.api.RDSSVSCardServicesAPI;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.core.YFSSystem;

public class RDSSVSCardServicesUtil implements Constants, XMLLiterals {
	
	private final static String ISSUE_BASE_DOC = "<soapenv:Envelope " + 
			"xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' " +
			"xmlns:ser='http://service.svsxml.svs.com' " +
			"xmlns:wsse='http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd'>" +
			"<soapenv:Header>" +
			"<wsse:Security soapenv:mustUnderstand='1'>" +
			"<wsse:UsernameToken>" +
			"<wsse:Username></wsse:Username>" +
			"<wsse:Password></wsse:Password>" +
			"</wsse:UsernameToken>" +
			"</wsse:Security>" +
			"</soapenv:Header>" +
			"<soapenv:Body>" +
			"</soapenv:Body>" +
			"</soapenv:Envelope>";
	
	private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
	private static YFCLogCategory logger = YFCLogCategory.instance(RDSSVSCardServicesAPI.class);
	
	/**
	 * This method returns the request document for plastic gift card activation. 
	 * 
	 * @param envr - Environment Object
	 * @param inDoc - Input Document
	 * @return request document
	 */
	public static Document getIssueGiftCardRequest (Element eleOrderLine, Element eleShipmentTagSerial) {
		Document issueGiftCardReqDoc = null;
		
		try {
			issueGiftCardReqDoc = XMLUtil.getDocument(ISSUE_BASE_DOC);
			
			Element eleSoapRequest = issueGiftCardReqDoc.getDocumentElement();
			
			/* Set Security details */
			setSecurity(eleSoapRequest);
			
			NodeList nodeList = eleSoapRequest.getElementsByTagName("soapenv:Body");
			if (nodeList != null && nodeList.getLength() > 0) {
				Element eleSoapBody = (Element) nodeList.item(0);
				Element eleIssueGiftCard = XMLUtil.createChild(eleSoapBody, "ser:issueGiftCard");
				Element eleRequest = XMLUtil.createChild(eleIssueGiftCard, "request");
				
				String strCardNo = eleShipmentTagSerial.getAttribute("SerialNo");
				String strPIN = eleShipmentTagSerial.getAttribute("LotAttribute1");
				
				/* <card>...</card> */
				Element eleCard = XMLUtil.createChild(eleRequest, "card");
				Element eleCardCurrency = XMLUtil.createChild(eleCard, "cardCurrency");
				eleCardCurrency.setTextContent(V_CURRENCY_USD);
				Element eleCardNumber = XMLUtil.createChild(eleCard, "cardNumber");
				eleCardNumber.setTextContent(strCardNo);
				Element elePinNumber = XMLUtil.createChild(eleCard, "pinNumber");
				elePinNumber.setTextContent(strPIN);
				Element eleCardTrackOne = XMLUtil.createChild(eleCard, "cardTrackOne");
				eleCardTrackOne.setTextContent(" ");
				Element eleCardTrackTwo = XMLUtil.createChild(eleCard, "cardTrackTwo");
				eleCardTrackTwo.setTextContent(" ");
				
				setBasicRequest (eleOrderLine, eleRequest, NO);
			}
			
			if (logger.isDebugEnabled()) {
				logger.debug("issueGiftCard Request ::\n"+XMLUtil.getXMLString(issueGiftCardReqDoc));	
			}
		} catch (Exception exception) {
			logger.error("", exception);
		}
		
		return issueGiftCardReqDoc;
	}
	
	/**
	 * This method returns the request document for E-Gift Card activation. 
	 * @param eleOrderLine - E-Gift Card Line.
	 * 
	 * @return request document
	 */
	public static Document getIssueVirtualGiftCardRequest (Element eleOrderLine, String skipTranID) {
		Document issueVirtualGiftCardReqDoc = null;
		
		try {
			issueVirtualGiftCardReqDoc = XMLUtil.getDocument(ISSUE_BASE_DOC);
			
			Element eleSoapRequest = issueVirtualGiftCardReqDoc.getDocumentElement();
			
			/* Set Security details */
			setSecurity(eleSoapRequest);
			
			NodeList nodeList = eleSoapRequest.getElementsByTagName("soapenv:Body");
			if (nodeList != null && nodeList.getLength() > 0) {
				Element eleSoapBody = (Element) nodeList.item(0);
				Element eleIssueVirtualGiftCard = XMLUtil.createChild(eleSoapBody, "ser:issueVirtualGiftCard");
				Element eleRequest = XMLUtil.createChild(eleIssueVirtualGiftCard, "request");
				
				setBasicRequest (eleOrderLine, eleRequest, skipTranID);
			}
			
			if (logger.isDebugEnabled()) {
				logger.debug("issueVirtualGiftCard Request ::\n"+XMLUtil.getXMLString(issueVirtualGiftCardReqDoc));	
			}
		} catch (Exception exception) {
			logger.error("", exception);
		}
		
		return issueVirtualGiftCardReqDoc;
	}
	
	/**
	 * 
	 * @param eleOrderLine - Gift Card Line
	 * @param eleRequest - SOAP Request
	 */
	private static void setBasicRequest (Element eleOrderLine, Element eleRequest, String skipTranID) throws Exception {
		/*
		 * <issueAmount>
		 * 		<amount>0</amount>
		 * 		<currency>CAD</currency>
		 * </issueAmount>
		 * 
		 * <merchant>
		 * 	<merchantName>CharlotteRusse</merchantName>
		 * 	<merchantNumber>61398</merchantNumber>
		 * 	<storeNumber>9999999999</storeNumber>
		 * 	<division>00000</division>
		 * </merchant>
		 * 
		 * <date>2016-03-16T13:56:52</date>
		 * <invoiceNumber>9876</invoiceNumber>		 * 
		 * <routingID>6006491699750000000</routingID>
		 * <stan></stan>
		 * <transactionID>1234567890123456</transactionID>
		 * <checkForDuplicate>true</checkForDuplicate>
		 */
		
		String strOrderLineKey = eleOrderLine.getAttribute(A_ORDER_LINE_KEY);
		String strOrderNo = eleOrderLine.getAttribute(A_ORDER_NO);
		String strUnitPrice = XPathUtil.getString(eleOrderLine, "LinePriceInfo/@UnitPrice");
		
		/*
		SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
		Date dateNow = new Date();
		String strStan = sdf.format(dateNow);
		*/
		
		String strMerchantName = YFSSystem.getProperty("RDS_MERCHANT_NAME");
		String strMerchantNumber = YFSSystem.getProperty("RDS_MERCHANT_NUMBER");
		String strStoreNumber = YFSSystem.getProperty("RDS_STORE_NUMBER");
		String strDivision = YFSSystem.getProperty("RDS_DIVISION");
		String strRoutingID = YFSSystem.getProperty("RDS_SVS_ROUTING_ID");

		String strInvoiceNo = "";
		if (strOrderNo.length() < 9) {
			strInvoiceNo = strOrderNo;
		} else {
			strInvoiceNo = strOrderNo.substring(strOrderNo.length() - 8);
		}
		
		/* 16 digit Unique Alpha-Numeric */
		String strTranID = "CRR" + new Date().getTime();
		/*
		if (!YFSObject.isVoid(strOrderLineKey)) {
			strTranID = strOrderLineKey.substring(strOrderLineKey.length() - 16);
		}
		*/

		Element eleDate = XMLUtil.createChild(eleRequest, "date");
		eleDate.setTextContent(getGMTDate());
		
		Element eleInvoiceNumber = XMLUtil.createChild(eleRequest, "invoiceNumber");
		eleInvoiceNumber.setTextContent(strInvoiceNo);
		
		/* IssueAmount */
		Element eleIssueAmount = XMLUtil.createChild(eleRequest, "issueAmount");
		Element eleAmount = XMLUtil.createChild(eleIssueAmount, "amount");
		eleAmount.setTextContent(strUnitPrice);
		Element eleCurrency = XMLUtil.createChild(eleIssueAmount, "currency");
		eleCurrency.setTextContent(V_CURRENCY_USD);
		
		/* merchant */
		Element eleMerchant = XMLUtil.createChild(eleRequest, "merchant");
		Element eleMerchantName = XMLUtil.createChild(eleMerchant, "merchantName");
		eleMerchantName.setTextContent(strMerchantName);
		Element eleMerchantNum = XMLUtil.createChild(eleMerchant, "merchantNumber");
		eleMerchantNum.setTextContent(strMerchantNumber);
		Element eleStoreNumber = XMLUtil.createChild(eleMerchant, "storeNumber");
		eleStoreNumber.setTextContent(strStoreNumber);
		Element eleDivision = XMLUtil.createChild(eleMerchant, "division");
		eleDivision.setTextContent(strDivision);
		
		Element eleRoutingID = XMLUtil.createChild(eleRequest, "routingID");
		eleRoutingID.setTextContent(strRoutingID);
		
		Element eleStan = XMLUtil.createChild(eleRequest, "stan");
		//eleStan.setTextContent(strStan);
		
		Element eleTransactionID = XMLUtil.createChild(eleRequest, "transactionID");
		if (! "Y".equalsIgnoreCase(skipTranID)) {
			eleTransactionID.setTextContent(strTranID);	
		}
		
		Element eleCheckForDuplicate = XMLUtil.createChild(eleRequest, "checkForDuplicate");
		eleCheckForDuplicate.setTextContent("true");
	}
	
	/**
	 * This method is used to set the security parameters for the request.
	 * 
	 * @param eleRequest
	 */
	private static void setSecurity (Element eleSoapRequest) {
		
		/* Get the value from customer_overrides. */
		String userName = YFSSystem.getProperty("RDS_SVS_USER_NAME");
		String password = YFSSystem.getProperty("RDS_SVS_PASSWORD");
		
		NodeList nodeList = eleSoapRequest.getElementsByTagName("wsse:UsernameToken");
		if (nodeList != null && nodeList.getLength() > 0) {
			Element eleUsernameToken = (Element) nodeList.item(0);
			Element eleUsername = XMLUtil.getChildElement(eleUsernameToken, "wsse:Username");
			eleUsername.setTextContent(userName);
			Element elePassword = XMLUtil.getChildElement(eleUsernameToken, "wsse:Password");
			elePassword.setTextContent(password);
		}
	}
	
	/**
	 * This method returns current date in GMT format.
	 * @return
	 */
	private static String getGMTDate () {
		Date currentDate = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
		String strDate = formatter.format(currentDate);
		
		return strDate;
	}
}
