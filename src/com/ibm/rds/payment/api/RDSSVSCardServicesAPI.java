package com.ibm.rds.payment.api;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.ibm.rds.util.RDSConstants;
import com.ibm.pca.sbc.business.utils.SBCApiUtils;
import com.ibm.rds.api.ApiInvoker;
import com.ibm.rds.util.BaseCustomAPI;
import com.ibm.rds.util.XMLUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * This class is used to activate the gift cards through SVS interface. 
 * 
 * @author IBM
 */
public class RDSSVSCardServicesAPI extends BaseCustomAPI implements RDSConstants {
	
	final String ISSUE_BASE_DOC = "<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' " +
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
	 * This method is invoked to activate plastic gift card.
	 * 
	 * Input Document:
	 * 
	 * <GiftCard>
	 * 		<Card EOVDate="2016-03-16T13:56:52" CardCurrency="USD" 
	 * 			CardNumber="6006491399499901745" PIN="0128" CardExpiration=""
	 * 			CardTrackOne=""  CardTrackTwo="" />
	 * 
	 * 		<IssueAmount Amount="" Currency="CAD" />
	 * 
	 * 		<Transaction MerchantName="JoshTest" MerchantNumber="61398" 
	 * 			StoreNumber="9999999999" Division="00000" Date="" 
	 * 			InvoiceNumber="" RoutingID="" Stan="" TransactionID="" CheckForDuplicate="" /> 
	 * </GiftCard>
	 * 
	 * @param envr
	 * @param inDoc
	 * @return
	 */
	public Document issueGiftCard (YFSEnvironment envr, Document inDoc) {
		Document docSVSWebserviceOut = null;
		try {
			Document docSoapRequest = getIssueGiftCardRequest (envr, inDoc);;
			
			String isStubmode = SBCApiUtils.getCommonCodeValue(envr,  SVS_STUB_MODE);
			// return a input DOC with additional attribute indicating Y/N of GC activation..
			if(RDSConstants.YES.equalsIgnoreCase(isStubmode)){
				String isGCApprpoved="N";
				isGCApprpoved= SBCApiUtils.getCommonCodeValue(envr,  "IS_GC_APPROVED");
				((Element)inDoc.getElementsByTagName("Card").item(0)).setAttribute("IsGCApproved", isGCApprpoved);
			//	inDoc.getDocumentElement().setAttribute("IsGCActivated", isGCActivated);
				logger.debug("Stub ouput of issueGiftCard method is ::\n"+XMLUtil.getXMLString(inDoc));	
				 
				return inDoc;
			}
			
			// Actual logic of invoking SVS systems.
			else{
				
				docSVSWebserviceOut = ApiInvoker.invokeService(envr, "RDSWebserviceSyncService", docSoapRequest);
			String responseCode = getReturnCode (docSVSWebserviceOut);
			
			if ("01".equalsIgnoreCase(responseCode)) {
				// Success
			} else {
				// TODO - Handle Error codes.
			}
			
			}
		} catch (Exception exception) {
			logger.error("", exception);
		}
		
		return docSVSWebserviceOut;
	}
	
	/**
	 * 
	 * @param envr
	 * @param inDoc
	 * @return
	 */
	public Document issueVirtualGiftCard (YFSEnvironment envr, Document inDoc) {
		Document docSVSWebserviceOut = null;
		
		try {
			Document docSoapRequest = getIssueVirtualGiftCardRequest (envr, inDoc);;
			
		
			
			docSVSWebserviceOut = ApiInvoker.invokeService(envr, "RDSWebserviceSyncService", docSoapRequest);
			
			String responseCode = getReturnCode (docSVSWebserviceOut);
			
			if ("01".equalsIgnoreCase(responseCode)) {
				// Success
			} else {
				// TODO - Handle Error codes.
			}
		
			
			
		} catch (Exception exception) {
			logger.error("", exception);
		}
		
		return docSVSWebserviceOut;
	}
	
	/**
	 * This method returns the request document for plastic gift card activation. 
	 * 
	 * @param envr - Environment Object
	 * @param inDoc - Input Document
	 * @return request document
	 */
	private Document getIssueGiftCardRequest (YFSEnvironment envr, Document inDoc) {
		Document issueGiftCardReqDoc = null;
		
		Element eleRoot = inDoc.getDocumentElement();
		
		try {
			issueGiftCardReqDoc = XMLUtil.getDocument(ISSUE_BASE_DOC);
			
			Element eleSoapRequest = issueGiftCardReqDoc.getDocumentElement();
			NodeList nodeList = eleSoapRequest.getElementsByTagName("soapenv:Body");
			if (nodeList != null && nodeList.getLength() > 0) {
				Element eleSoapBody = (Element) nodeList.item(0);
				Element eleIssueGiftCard = XMLUtil.createChild(eleSoapBody, "ser:issueGiftCard");
				Element eleRequest = XMLUtil.createChild(eleIssueGiftCard, "request");
				
				String cardNumber = eleRoot.getAttribute("CardNumber");
				String cardCurrency = eleRoot.getAttribute("CardCurrency");
				String pinNumber = eleRoot.getAttribute("PIN");
				String cardExpiration = eleRoot.getAttribute("CardExpiration");
				String eovDate = eleRoot.getAttribute("eovDate");
				
				/* <card>...</card> */
				Element eleCard = XMLUtil.createChild(eleRequest, "card");
				Element eleEovDate = XMLUtil.createChild(eleCard, "eovDate");
				eleEovDate.setTextContent(eovDate);
				Element eleCardCurrency = XMLUtil.createChild(eleCard, "cardCurrency");
				eleCardCurrency.setTextContent(cardCurrency);
				Element eleCardNumber = XMLUtil.createChild(eleCard, "cardNumber");
				eleCardNumber.setTextContent(cardNumber);
				Element elePinNumber = XMLUtil.createChild(eleCard, "pinNumber");
				elePinNumber.setTextContent(pinNumber);
				Element eleCardExpiration = XMLUtil.createChild(eleCard, "cardExpiration");
				eleCardExpiration.setTextContent(cardExpiration);
				Element eleCardTrackOne = XMLUtil.createChild(eleCard, "cardTrackOne");
				eleCardTrackOne.setTextContent(" ");
				Element eleCardTrackTwo = XMLUtil.createChild(eleCard, "cardTrackTwo");
				eleCardTrackTwo.setTextContent(" ");
				
				setBasicRequest (inDoc, eleRequest);
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
	 * 
	 * @param envr - Environment Object
	 * @param inDoc - Input Document
	 * @return request document
	 */
	private Document getIssueVirtualGiftCardRequest (YFSEnvironment envr, Document inDoc) {
		Document issueVirtualGiftCardReqDoc = null;
		
		try {
			issueVirtualGiftCardReqDoc = XMLUtil.getDocument(ISSUE_BASE_DOC);
			
			Element eleSoapRequest = issueVirtualGiftCardReqDoc.getDocumentElement();
			NodeList nodeList = eleSoapRequest.getElementsByTagName("soapenv:Body");
			if (nodeList != null && nodeList.getLength() > 0) {
				Element eleSoapBody = (Element) nodeList.item(0);
				Element eleIssueVirtualGiftCard = XMLUtil.createChild(eleSoapBody, "ser:issueVirtualGiftCard");
				Element eleRequest = XMLUtil.createChild(eleIssueVirtualGiftCard, "request");
				
				setBasicRequest (inDoc, eleRequest);
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
	 * @param inDoc - Input Document
	 * @param eleRequest - SOAP Request
	 */
	private void setBasicRequest (Document inDoc, Element eleRequest) {
		/*
		 * <issueAmount>
		 * 		<amount>0</amount>
		 * 		<currency>CAD</currency>
		 * </issueAmount>
		 * 
		 * <merchant>
		 * 	<merchantName>Test</merchantName>
		 * 	<merchantNumber>61398</merchantNumber>
		 * 	<storeNumber>9999999999</storeNumber>
		 * 	<division>00000</division>
		 * </merchant>
		 * 
		 * <date>2016-03-16T13:56:52</date>
		 * <invoiceNumber>9876</invoiceNumber>		 * 
		 * <routingID>6006491398000000000</routingID>
		 * <stan>124578</stan>
		 * <transactionID></transactionID>
		 * <checkForDuplicate>False</checkForDuplicate>
		 */
		
		setSecurity(eleRequest);
		
		Element eleInputRoot = inDoc.getDocumentElement();
		Element eleInIssueAmount = XMLUtil.getChildElement(eleInputRoot, "IssueAmount");
		Element eleTransaction = XMLUtil.getChildElement(eleInputRoot, "Transaction");
		
		Element eleDate = XMLUtil.createChild(eleRequest, "date");
		eleDate.setTextContent(getGMTDate());
		
		Element eleInvoiceNumber = XMLUtil.createChild(eleRequest, "invoiceNumber");
		eleInvoiceNumber.setTextContent(eleTransaction.getAttribute("InvoiceNumber"));
		
		/* IssueAmount */
		Element eleIssueAmount = XMLUtil.createChild(eleRequest, "issueAmount");
		Element eleAmount = XMLUtil.createChild(eleIssueAmount, "amount");
		eleAmount.setTextContent(eleInIssueAmount.getAttribute("Amount"));
		Element eleCurrency = XMLUtil.createChild(eleIssueAmount, "currency");
		eleCurrency.setTextContent(eleInIssueAmount.getAttribute("Currency"));
		
		/* merchant */
		Element eleMerchant = XMLUtil.createChild(eleRequest, "merchant");
		Element eleMerchantName = XMLUtil.createChild(eleMerchant, "merchantName");
		eleMerchantName.setTextContent(eleTransaction.getAttribute("MerchantName"));
		Element eleMerchantNum = XMLUtil.createChild(eleMerchant, "merchantNumber");
		eleMerchantNum.setTextContent(eleTransaction.getAttribute("MerchantNumber"));
		Element eleStoreNumber = XMLUtil.createChild(eleMerchant, "storeNumber");
		eleStoreNumber.setTextContent(eleTransaction.getAttribute("StoreNumber"));
		Element eleDivision = XMLUtil.createChild(eleMerchant, "division");
		eleDivision.setTextContent(eleTransaction.getAttribute("Division"));
		
		Element eleRoutingID = XMLUtil.createChild(eleRequest, "routingID");
		eleRoutingID.setTextContent(eleTransaction.getAttribute("RoutingID"));
		
		Element eleStan = XMLUtil.createChild(eleRequest, "stan");
		eleStan.setTextContent(eleTransaction.getAttribute("Stan"));
		
		Element eleTransactionID = XMLUtil.createChild(eleRequest, "transactionID");
		eleTransactionID.setTextContent(eleTransaction.getAttribute("TransactionID"));
		
		Element eleCheckForDuplicate = XMLUtil.createChild(eleRequest, "checkForDuplicate");
		eleCheckForDuplicate.setTextContent(eleTransaction.getAttribute("CheckForDuplicate"));
	}
	
	/**
	 * This method returns the return code from the SVS response.
	 * 
	 * @param responseDoc - SVS Response document.
	 * @return responseCode
	 */
	private String getReturnCode (Document responseDoc) {
		String returnCode = "";
		Element eleResponse = responseDoc.getDocumentElement();
		NodeList nodeList = eleResponse.getElementsByTagName("returnCode");
		if (nodeList != null && nodeList.getLength() > 0) {
			Element eleReturnCode = (Element) nodeList.item(0);
			Element eleChildReturnCode = XMLUtil.getChildElement(eleReturnCode, "returnCode");
			returnCode = eleChildReturnCode.getTextContent();
		}
		
		return returnCode;
	}
	
	/**
	 * This method returns current date in GMT format.
	 * @return
	 */
	private String getGMTDate () {
		Date currentDate = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
		String strDate = formatter.format(currentDate);
		
		return strDate;
	}
	
	/**
	 * This method is used to set the security parameters for the request.
	 * 
	 * @param eleRequest
	 */
	private void setSecurity (Element eleSoapRequest) {
		
		/* Get the API arguments from Service Definition. */
		String userNameKey = getProperties().getProperty("RDS_SVS_USER_NAME");
		String passwordKey = getProperties().getProperty("RDS_SVS_PASSWORD");
		
		/* Get the value from customer_overrides. */
		String userName = YFSSystem.getProperty(userNameKey);
		String password = YFSSystem.getProperty(passwordKey);
		
		NodeList nodeList = eleSoapRequest.getElementsByTagName("wsse:UsernameToken");
		if (nodeList != null && nodeList.getLength() > 0) {
			Element eleUsernameToken = (Element) nodeList.item(0);
			Element eleUsername = XMLUtil.getChildElement(eleUsernameToken, "wsse:Username");
			eleUsername.setTextContent(userName);
			Element elePassword = XMLUtil.getChildElement(eleUsernameToken, "wsse:Password");
			elePassword.setTextContent(password);
		}
	}
}
