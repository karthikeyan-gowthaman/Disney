package com.ibm.rds.payment.api;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


import com.ibm.rds.api.ApiInvoker;
import com.ibm.rds.util.PaymentProcessingUtil;
import com.ibm.rds.util.XMLLiterals;
import com.ibm.rds.util.XMLUtil;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.yfc.date.YTimestamp;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCDate;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.yantra.yfs.japi.YFSExtnPaymentCollectionOutputStruct;

public class RDSSVSAuthorizeAPI {
	private static YFCLogCategory log = YFCLogCategory.instance(RDSSVSAuthorizeAPI.class);
	public YFSExtnPaymentCollectionOutputStruct authorize(YFSEnvironment env, Document docAuthorizeInput, YFSExtnPaymentCollectionOutputStruct outStruct) throws YFSException {
		log.verbose("Begin : RDSSVSAuthorizeAPI:authorize()");
		Element eleAuthorizeInput = docAuthorizeInput.getDocumentElement();
		String strRequestAmount = eleAuthorizeInput.getAttribute("RequestAmount");
		double dRequestAmount = Double.parseDouble(strRequestAmount);
		String strAuthId = eleAuthorizeInput.getAttribute("AuthorizationId");
		
		outStruct.authorizationAmount = Math.abs(dRequestAmount);
		outStruct.authorizationId = strAuthId;		
		outStruct.authCode = "100";
		outStruct.tranAmount = dRequestAmount;
		outStruct.tranReturnCode = "ACCEPT";
		outStruct.tranReturnFlag = "Y";
		outStruct.requestID = "";	  
		outStruct.retryFlag = "N";     		
		outStruct.internalReturnCode = "AUTHORIZATION";
		
		return outStruct;
		
		
		
	}

	public YFSExtnPaymentCollectionOutputStruct reverseAuth(
			YFSEnvironment env, Document docRevAuthInput,
			YFSExtnPaymentCollectionOutputStruct outStruct) {
		
		log.verbose("Begin : RDSSVSAuthorizeAPI:reverseAuth()");
		Document docSVSWebserviceOut =  null;
		Element eleInDoc = docRevAuthInput.getDocumentElement();
		String strRequestAmount = eleInDoc.getAttribute("RequestAmount");
		double dRequestAmount = Double.parseDouble(strRequestAmount);
		String strAuthId = eleInDoc.getAttribute("AuthorizationId");
		//String strRequestId = eleInDoc.getAttribute("AuthorizationId");
		String strChargeType = eleInDoc.getAttribute("ChargeType");
		String strEnterpriseCode = eleInDoc.getAttribute("EnterpriseCode");
		String strOrderHeaderKey = eleInDoc.getAttribute(XMLLiterals.A_ORDER_HEADER_KEY);
		String strOrderNo = eleInDoc.getAttribute(XMLLiterals.A_ORDER_NO);
		String strRequestID = null;
		//String strReconciliationID = null;
		//String strEnteredBy = null;
		String strSVSResponseStatus = null;
		try{
		//SOAPMessage setRequestMessage = generateSettleInputRequest(env, eleInDoc);
		Date javaUtilDate= new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		String strDate = formatter.format(javaUtilDate);
		eleInDoc.setAttribute("Date",strDate);
		//SVS properties
		String strUserName = YFSSystem.getProperty("RDS_SVS_USER_NAME");
		String strPassword = YFSSystem.getProperty("RDS_SVS_PASSWORD");
		String strMerchantName = YFSSystem.getProperty("RDS_MERCHANT_NAME");
		String strMerchantNumber = YFSSystem.getProperty("RDS_MERCHANT_NUMBER");
		String strStoreNumber = YFSSystem.getProperty("RDS_STORE_NUMBER");
		String strDivision = YFSSystem.getProperty("RDS_DIVISION");
		String strRoutingId = YFSSystem.getProperty("RDS_SVS_ROUTING_ID");
		
		eleInDoc.setAttribute("SvsUsername",strUserName);
		eleInDoc.setAttribute("SvsPassword",strPassword);
		eleInDoc.setAttribute("SvsMerchantName",strMerchantName);
		eleInDoc.setAttribute("SvsMerchantNo",strMerchantNumber);
		eleInDoc.setAttribute("SvsStoreNo",strStoreNumber);
		eleInDoc.setAttribute("SvsDivision",strDivision);
		eleInDoc.setAttribute("SvsRoutingID",strRoutingId);
		
		Document docSoapRequest = ApiInvoker.invokeService(env, "RDSSVSAuthSoapReqSyncService", docRevAuthInput);
			
		log.verbose("RDSSVSAuthorizeAPI:reverseAuth: SVS input XML :: : "+XmlUtils.getString(docSoapRequest.getDocumentElement()));
		
		docSVSWebserviceOut = ApiInvoker.invokeService(env, "RDSWebserviceSyncService", docSoapRequest);
		Element eleDocSVSWebserviceOut = docSVSWebserviceOut.getDocumentElement();
		
		log.verbose("RDSSVSAuthorizeAPI:reverseAuth: SVS output XML :: " + XmlUtils.getString(docSVSWebserviceOut));
		
		Element replyNode =  (Element) eleDocSVSWebserviceOut.getElementsByTagName("preAuthCompleteReturn").item(0);
		
		//Element replyNode =  (Element) docSVSWebserviceOut.getElementsByTagNameNS("*", "replyMessage").item(0);
		//strEnteredBy = getEnteredBy(strOrderHeaderKey, env);
		String responseCode = RDSSVSResponseCodeAPI.getReasonCode(replyNode);
		strSVSResponseStatus = RDSSVSResponseCodeAPI.getDecision(replyNode);
		log.debug("strSVSResponseStatus: " + strSVSResponseStatus);
		strRequestID = RDSSVSResponseCodeAPI.getRequestId(replyNode);
		log.debug("strRequestID: " + strRequestID);
		//strReconciliationID = RDSCyberSourceResponseCodeAPI.getReconciliationID(docSVSWebserviceOut, "c:ccCaptureReply");
	//	String errorResponse = CyberSourceResponseCode.getResponseMessageFromCommonCode(env, responseCode);
		String errorResponse = RDSSVSResponseCodeAPI.svsResponseMap.get(responseCode);
		String authorizationExpirationDate = PaymentProcessingUtil.getAuthExpirationDate(env, strChargeType, strEnterpriseCode);
		log.debug("errorResponse: " + errorResponse);	
		if ("Approval".equals(strSVSResponseStatus)) {	
			outStruct.authorizationAmount = dRequestAmount;
			log.debug("outStruct.authorizationAmount: " + outStruct.authorizationAmount);
			outStruct.authorizationId = strRequestID;		
			outStruct.tranAmount = dRequestAmount;
			outStruct.tranType = strChargeType;
			outStruct.authCode = "100";
			outStruct.tranReturnCode = "ACCEPT";
			outStruct.tranReturnFlag = "Y";					
			outStruct.internalReturnCode = "REVERSEAUTHORIZATION"; 
			outStruct.requestID = strRequestID;
			outStruct.tranReturnMessage = strRequestID;
			log.debug("outStruct.tranReturnMessage: " + outStruct.tranReturnMessage);
			outStruct.authorizationExpirationDate = authorizationExpirationDate;
			outStruct.authTime = YTimestamp.newMutableTimestamp().getString(YFCDate.XML_DATE_FORMAT);
			return outStruct;
		}
	
		if ("Host Unavailable".equals(strSVSResponseStatus)) {
			outStruct.authorizationAmount = 0.0D;
			outStruct.tranAmount =  0.0D;	
			outStruct.tranType = strChargeType;	
			outStruct.tranReturnCode = strSVSResponseStatus;
			outStruct.tranReturnFlag = "N";
			outStruct.retryFlag = "Y";
			outStruct.internalReturnCode = "REVERSEAUTHORIZATION"; 
			return outStruct;
		}
		outStruct.authorizationAmount = 0.0D;	
		outStruct.authorizationId = strAuthId;
		outStruct.tranAmount = dRequestAmount;
		outStruct.tranType = strChargeType;
		outStruct.tranReturnCode = strSVSResponseStatus;
		outStruct.internalReturnMessage = "Payment Settlement Failed : " + strSVSResponseStatus;
		outStruct.holdOrderAndRaiseEvent = true;
		outStruct.holdReason = "PAYMENT_FAILURE_HOLD";		 
		outStruct.retryFlag = "N";
		outStruct.suspendPayment = "Y";
		if (docSVSWebserviceOut != null) {
			log.error("RDSSVSAuthorizeAPI:reverseAuth::Payment settlement Failed :: Status :: " + strSVSResponseStatus + " Error Message :: " + errorResponse);
		}
			ApiInvoker.invokeAPI(env, "createException", "<Inbox AutoResolvedFlag='N' InboxType='Payment Failures' " +
				"QueueId='YCD_PAYMENT_DECLINED' ErrorType=''  ExceptionType='PAYMENTEXCEPTION' " + "ExceptionTypeDescription='PAYMENTEXCEPTION' " +
				"OrderHeaderKey='"+strOrderHeaderKey+"' OrderNo='"+strOrderNo+
				"' ErrorReason='" + "EXTN-CYB-" + responseCode + 
				"' Description='Credit Card Authorization failed' >" +
				"<InboxReferencesList><InboxReferences Name='ApiName' " +
				"ReferenceType='TEXT' Value='PaymentExecution'/>" +
				"<InboxReferences Name='Response Status' ReferenceType='TEXT' Value='"+strSVSResponseStatus+"'/>" +
				"<InboxReferences Name='ERRORDESCRIPTION' ReferenceType='TEXT' Value='"+
				errorResponse +"'/></InboxReferencesList></Inbox>");
			return outStruct;
		
		} catch (Exception exception) {
			exception.printStackTrace();
			log.error("An Exception occured while processing RDSSVSAuthorizeAPI:reverseAuth() method", exception);
			try{
				ApiInvoker.invokeAPI(env, "createException", "<Inbox AutoResolvedFlag='N' InboxType='Payment Failures' " +
					"QueueId='YCD_PAYMENT_DECLINED' ErrorType=''  ExceptionType='PAYMENTEXCEPTION' ExceptionTypeDescription='PAYMENTEXCEPTION' " +
					"OrderHeaderKey='"+strOrderHeaderKey+"' OrderNo='"+strOrderNo+"' Description='An Exception occured while processing CollectionCreditCardUEImpl.settlement() method' > " +
					"<InboxReferencesList><InboxReferences Name='ApiName' ReferenceType='TEXT' Value='PaymentExecution'/>" +
					"<InboxReferences Name='ERRORDESCRIPTION' ReferenceType='TEXT' Value='An Exception occured while processing CollectionCreditCardUEImpl.settlement() " +
					"method'/></InboxReferencesList></Inbox>");
			}catch(Exception e)
			{
				log.error("Exception occured while raising the alert.");
			}
			throw new YFSException("An Exception occured while processing RDSSVSAuthorizeAPI:reverseAuth() method" + exception.getMessage());
		}
		
		
		/*String authorizationExpirationDate = PaymentProcessingUtil.getAuthExpirationDate(env, strChargeType, strEnterpriseCode);	 
		//System.out.println("TIME: "+authorizedDateTime +  "Amount:  "+ amount+"Expiration Date" + authorizationExpirationDate);
		outStruct.authorizationAmount = dRequestAmount;
		outStruct.authorizationId = strAuthId;		
		outStruct.authCode = "100";
		outStruct.tranAmount = dRequestAmount;
		outStruct.tranReturnCode = "ACCEPT";
		outStruct.tranReturnFlag = "Y";
		outStruct.requestID = strAuthId;	  
		outStruct.retryFlag = "N";     		
		outStruct.internalReturnCode = "REVERSEAUTHORIZATION";
		outStruct.authorizationExpirationDate = authorizationExpirationDate;
		outStruct.authTime = YTimestamp.newMutableTimestamp().getString(YFCDate.XML_DATE_FORMAT);
		
		return outStruct;*/
	}

	
}
